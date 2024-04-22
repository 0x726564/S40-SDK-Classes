package javax.microedition.location;

import com.nokia.mid.impl.isa.location.SecurityPermission;
import com.nokia.mid.impl.isa.util.SharedObjects;
import com.nokia.mid.impl.policy.PolicyAccess;
import com.nokia.mid.s40.location.LocationUtil;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Enumeration;

public class LandmarkStore {
   private static final String DEFAULT_LANDMARKSTORE = "~*default*~";
   private static final int STORE_CAPACITY_INCREMENT = 10;
   private static int MAX_LANDMARKS_PER_STORE = 255;
   private static int MAX_CATEGORIES_PER_STORE = 255;
   private static int MAX_CATS_PER_LANDMARK = 4;
   private static final String READ_ONLY_STORE_ERR_MSG = "Read Only LandmarkStore";
   static final Object globalLock = SharedObjects.getLock("javax.microedition.location.LandmarkStore.globalLock");
   private static WeakReference[] openStores;
   private boolean readOnly = true;
   private String storeName;
   private PersistedLandmark[] pLandmarks;
   private Category[] categories;
   private int pLandmarksSize;
   private int storeID;

   private LandmarkStore(String storename) {
      this.storeName = storename;
      this.readOnly = false;
   }

   public static LandmarkStore getInstance(String storeName) {
      SecurityPermission.checkReadPermission();
      LandmarkStore tmpStore = null;
      if (storeName == null) {
         storeName = "~*default*~";
      }

      synchronized(globalLock) {
         tmpStore = isStoreOpen(storeName);
         if (tmpStore != null) {
            return tmpStore;
         } else {
            int sid = nativeStoreExists(storeName);
            if (sid == -1) {
               return null;
            } else {
               tmpStore = new LandmarkStore(storeName);
               tmpStore.storeID = sid;

               label59: {
                  Object var10000;
                  try {
                     int catNum = nativeGetCategories(tmpStore.storeID);
                     if (catNum <= MAX_CATEGORIES_PER_STORE || tmpStore.readOnly) {
                        if (catNum > 0) {
                           String[] strs = new String[catNum];
                           byte[] ids = new byte[catNum];
                           nativeLoadCategories(strs, ids);
                           tmpStore.categories = new Category[catNum];

                           for(int i = 0; i < catNum; ++i) {
                              tmpStore.categories[i] = new Category(strs[i], ids[i]);
                           }
                        }

                        try {
                           tmpStore.loadLandmarks();
                        } catch (IOException var9) {
                        }
                        break label59;
                     }

                     var10000 = null;
                  } catch (IOException var10) {
                     break label59;
                  }

                  return (LandmarkStore)var10000;
               }

               addOpenStore(tmpStore);
               return tmpStore;
            }
         }
      }
   }

   public static void createLandmarkStore(String storeName) throws IOException, LandmarkException {
      if (storeName == null) {
         throw new NullPointerException("The store name is  null");
      } else {
         SecurityPermission.checkManagementPermission();
         synchronized(globalLock) {
            if (nativeStoreExists(storeName) != -1) {
               throw new IllegalArgumentException();
            } else {
               nativeCreateStore(storeName);
            }
         }
      }
   }

   public static void deleteLandmarkStore(String storeName) throws IOException, LandmarkException {
      if (storeName == null) {
         throw new NullPointerException("The default store cannot be deleted");
      } else {
         SecurityPermission.checkManagementPermission();
         synchronized(globalLock) {
            LandmarkStore store2delete = isStoreOpen(storeName);
            int delStoreID = true;
            int delStoreID;
            if (store2delete == null) {
               delStoreID = nativeStoreExists(storeName);
            } else {
               delStoreID = store2delete.getStoreID();
               store2delete.reset();
               closeOpenStore(store2delete);
            }

            if (delStoreID != -1) {
               nativeDeleteStore(delStoreID);
            }

         }
      }
   }

   public static String[] listLandmarkStores() throws IOException {
      String[] storenames = null;
      String[] allstorenames = null;
      int n = 0;
      SecurityPermission.checkReadPermission();
      synchronized(globalLock) {
         allstorenames = nativeGetStorenames();
      }

      if (allstorenames.length == 1) {
         return null;
      } else {
         storenames = new String[allstorenames.length - 1];

         for(int s = 0; s < allstorenames.length; ++s) {
            if (!allstorenames[s].equals("~*default*~")) {
               storenames[n] = allstorenames[s];
               ++n;
            }
         }

         return storenames;
      }
   }

   public void addLandmark(Landmark landmark, String category) throws IOException {
      if (landmark == null) {
         throw new NullPointerException();
      } else {
         SecurityPermission.checkWritePermission();
         this.checkWritableStore();
         synchronized(globalLock) {
            int idx = this.getCategoryIndex(category);
            Category categoryRef = idx < 0 ? null : this.categories[idx];
            if (categoryRef == null && category != null) {
               throw new IllegalArgumentException("Category not found:" + category);
            } else {
               PersistedLandmark plm = landmark.getPersistedLandmark();
               if (plm != null && plm.parentStore == this) {
                  if (categoryRef != null) {
                     idx = getLandmarkIndex(categoryRef.pLandmarks, plm);
                     if (idx < 0) {
                        nativeAddCategoryToLandmark(this.storeID, plm.getLandmarkID(), categoryRef.getID());
                        categoryRef.addLandmark(plm);
                     }
                  }

               } else if (this.pLandmarksSize >= MAX_LANDMARKS_PER_STORE) {
                  throw new IOException();
               } else {
                  plm = new PersistedLandmark(landmark, this);

                  try {
                     int lmid = false;
                     int lmid;
                     if (categoryRef != null) {
                        lmid = nativeAddLandmark(this.storeID, plm, categoryRef.getID());
                     } else {
                        lmid = nativeAddLandmark(this.storeID, plm, (byte)0);
                     }

                     plm.setLandmarkID(lmid);
                  } catch (IOException var9) {
                     landmark.setPersistedLandmark((PersistedLandmark)null);
                     throw var9;
                  }

                  landmark.parentStore = this;
                  this.addJLandmark(plm);
                  if (categoryRef != null) {
                     categoryRef.addLandmark(plm);
                  }

               }
            }
         }
      }
   }

   public Enumeration getLandmarks(String category, String name) throws IOException {
      PersistedLandmark[] dataSet = null;
      Enumeration enumeration = null;
      synchronized(globalLock) {
         dataSet = this.retrieveByCategoryAndCoords(category, false, 0.0D, 0.0D, 0.0D, 0.0D);
         dataSet = this.retrieveByName(name, dataSet);
         enumeration = this.enumerateLandmarks(dataSet);
         return enumeration;
      }
   }

   public Enumeration getLandmarks() throws IOException {
      return this.enumerateLandmarks(this.pLandmarks);
   }

   public Enumeration getLandmarks(String category, double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) throws IOException {
      PersistedLandmark[] dataSet = null;
      Enumeration enumeration = null;
      synchronized(globalLock) {
         if (minLongitude > maxLongitude) {
            PersistedLandmark[] dataSet1 = null;
            PersistedLandmark[] dataSet2 = null;
            dataSet1 = this.retrieveByCategoryAndCoords(category, true, minLatitude, maxLatitude, -180.0D, maxLongitude);
            dataSet2 = this.retrieveByCategoryAndCoords(category, true, minLatitude, maxLatitude, minLongitude, 179.9999D);
            if (dataSet1 != null) {
               if (dataSet2 != null) {
                  dataSet = new PersistedLandmark[dataSet1.length + dataSet2.length];
                  System.arraycopy(dataSet1, 0, dataSet, 0, dataSet1.length);
                  System.arraycopy(dataSet2, 0, dataSet, dataSet1.length, dataSet2.length);
               } else {
                  dataSet = dataSet1;
               }
            } else {
               dataSet = dataSet2;
            }
         } else {
            dataSet = this.retrieveByCategoryAndCoords(category, true, minLatitude, maxLatitude, minLongitude, maxLongitude);
         }

         enumeration = this.enumerateLandmarks(dataSet);
         return enumeration;
      }
   }

   public void removeLandmarkFromCategory(Landmark lm, String category) throws IOException {
      if (lm != null && category != null) {
         SecurityPermission.checkWritePermission();
         this.checkWritableStore();
         synchronized(globalLock) {
            int idxC = this.getCategoryIndex(category);
            if (idxC >= 0) {
               Category categoryObj = this.categories[idxC];
               int idxL = getLandmarkIndex(categoryObj.pLandmarks, lm.getPersistedLandmark());
               if (idxL >= 0) {
                  nativeDeleteCategoryFromLandmark(this.storeID, lm.getPersistedLandmark().getLandmarkID(), categoryObj.getID());
                  categoryObj.removeLandmark(idxL);
               }
            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void updateLandmark(Landmark lm) throws LandmarkException, IOException {
      if (lm == null) {
         throw new NullPointerException();
      } else {
         SecurityPermission.checkWritePermission();
         this.checkWritableStore();
         PersistedLandmark plm = lm.getPersistedLandmark();
         if (plm != null && plm.parentStore == this) {
            synchronized(globalLock) {
               plm.update(lm);
            }
         } else {
            throw new LandmarkException();
         }
      }
   }

   public void deleteLandmark(Landmark lm) throws LandmarkException, IOException {
      PersistedLandmark plm = lm.getPersistedLandmark();
      SecurityPermission.checkWritePermission();
      this.checkWritableStore();
      if (plm != null && plm.parentStore != this) {
         throw new LandmarkException("Landmark is not in this Landmark Store");
      } else if (plm == null && lm.parentStore != this) {
         throw new LandmarkException("Landmark is not in this Landmark Store");
      } else {
         synchronized(globalLock) {
            int idx = getLandmarkIndex(this.pLandmarks, plm);
            if (idx >= 0) {
               nativeDeleteLandmark(this.storeID, plm.getLandmarkID());
               plm.invalidate();
               lm.setPersistedLandmark((PersistedLandmark)null);
               this.removeJLandmark(idx);
            }
         }
      }
   }

   public Enumeration getCategories() {
      String[] catStrings = null;
      synchronized(globalLock) {
         if (this.categories != null) {
            catStrings = new String[this.categories.length];

            for(int i = 0; i < catStrings.length; ++i) {
               catStrings[i] = this.categories[i].name;
            }
         }
      }

      return new HelperEnumeration(catStrings);
   }

   public void addCategory(String categoryName) throws IOException, LandmarkException {
      if (categoryName == null) {
         throw new NullPointerException();
      } else {
         SecurityPermission.checkCategoryPermission();
         if (this.readOnly) {
            throw new LandmarkException("Read Only LandmarkStore");
         } else {
            synchronized(globalLock) {
               int idx = this.getCategoryIndex(categoryName);
               if (idx >= 0) {
                  throw new IllegalArgumentException("The category named " + categoryName + " already exists");
               } else if (this.categories != null && this.categories.length >= MAX_CATEGORIES_PER_STORE) {
                  throw new IOException();
               } else {
                  Category category = new Category(categoryName);
                  byte catid = false;
                  byte catid = this.nativeAddCategory(this.storeID, categoryName);
                  category.setID(catid);
                  this.addJCategory(category);
               }
            }
         }
      }
   }

   public void deleteCategory(String categoryName) throws IOException, LandmarkException {
      if (categoryName == null) {
         throw new NullPointerException();
      } else {
         SecurityPermission.checkCategoryPermission();
         if (this.readOnly) {
            throw new LandmarkException("Read Only LandmarkStore");
         } else {
            synchronized(globalLock) {
               int idx = this.getCategoryIndex(categoryName);
               Category categoryRef = idx >= 0 ? this.categories[idx] : null;
               if (categoryRef != null) {
                  nativeDeleteCategory(this.storeID, categoryRef.getID());
                  categoryRef.reset();
                  this.removeJCategory(idx);
               }
            }
         }
      }
   }

   int getStoreID() {
      return this.storeID;
   }

   byte[] getCatIDs(PersistedLandmark plm) {
      byte[] result = new byte[4];
      int lastItem = 0;

      for(int i = 0; i < this.categories.length; ++i) {
         if (this.categories[i].containsLandmark(plm)) {
            result[lastItem++] = this.categories[i].getID();
         }
      }

      return result;
   }

   private static void addOpenStore(LandmarkStore tmpStore) {
      if (openStores == null) {
         openStores = new WeakReference[1];
      } else {
         WeakReference[] tmp = openStores;
         openStores = new WeakReference[tmp.length + 1];
         System.arraycopy(tmp, 0, openStores, 0, tmp.length);
      }

      openStores[openStores.length - 1] = new WeakReference(tmpStore);
   }

   private static void closeOpenStore(LandmarkStore ls) {
      if (openStores != null) {
         LandmarkStore tmpLS = null;

         for(int i = 0; i < openStores.length; ++i) {
            tmpLS = (LandmarkStore)openStores[i].get();
            if (tmpLS != null && tmpLS.equals(ls)) {
               WeakReference[] tmp = new WeakReference[openStores.length - 1];
               System.arraycopy(openStores, 0, tmp, 0, i);
               System.arraycopy(openStores, i + 1, tmp, i, tmp.length - i);
               openStores = tmp.length == 0 ? null : tmp;
               return;
            }
         }

      }
   }

   private static LandmarkStore isStoreOpen(String storeName) {
      if (storeName != null && openStores != null) {
         LandmarkStore tmpLS = null;

         for(int i = 0; i < openStores.length; ++i) {
            tmpLS = (LandmarkStore)openStores[i].get();
            if (tmpLS != null && tmpLS.storeName.equals(storeName)) {
               return tmpLS;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private void loadLandmarks() throws IOException {
      PersistedLandmark plm = null;
      boolean found = false;
      int catlen = false;
      String[] names = null;
      byte[] IDs = null;
      int totalLandmarks = false;
      int totalLandmarks = nativeCountLandmarks(this.storeID);
      names = new String[MAX_CATS_PER_LANDMARK];
      byte[] IDs = new byte[MAX_CATS_PER_LANDMARK];

      for(int l = 0; l < totalLandmarks; ++l) {
         try {
            plm = new PersistedLandmark(this);
            nativeLoadNextLandmark(this.storeID, plm);
            if (!nativeMoveLandmarkToJava(plm, IDs) || !this.isGeoLocationValid(plm.coordinates)) {
               plm.coordinates = null;
            }

            for(int i = 0; i < MAX_CATS_PER_LANDMARK; ++i) {
               int catlen = this.categories == null ? 0 : this.categories.length;

               for(int j = 0; j < catlen && !found; ++j) {
                  found = this.categories[j].getID() == IDs[i];
                  if (found) {
                     this.categories[j].addLandmark(plm);
                  }
               }

               found = false;
            }

            this.addJLandmark(plm);
         } catch (IOException var10) {
            var10.printStackTrace();
         }
      }

      nativeClearLandmarks();
   }

   private int getCategoryIndex(String categoryStr) {
      if (categoryStr != null && this.categories != null) {
         for(int i = 0; i < this.categories.length; ++i) {
            if (this.categories[i].equivalent(categoryStr, (byte)0)) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private void addJCategory(Category category) {
      if (this.categories == null) {
         this.categories = new Category[]{category};
      } else {
         Category[] tmp = new Category[this.categories.length + 1];
         System.arraycopy(this.categories, 0, tmp, 0, this.categories.length);
         tmp[this.categories.length] = category;
         this.categories = tmp;
      }

   }

   private void removeJCategory(int element) {
      Category[] tmp = new Category[this.categories.length - 1];
      System.arraycopy(this.categories, 0, tmp, 0, element);
      System.arraycopy(this.categories, element + 1, tmp, element, tmp.length - element);
      this.categories = tmp.length == 0 ? null : tmp;
   }

   static int getLandmarkIndex(PersistedLandmark[] plms, PersistedLandmark plm) {
      if (plm != null && plms != null) {
         for(int i = 0; i < plms.length; ++i) {
            if (plms[i].equals(plm)) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private void addJLandmark(PersistedLandmark plm) {
      if (this.pLandmarks == null) {
         this.pLandmarks = new PersistedLandmark[10];
         this.pLandmarks[0] = plm;
         this.pLandmarksSize = 1;
      } else {
         if (this.pLandmarksSize == this.pLandmarks.length) {
            PersistedLandmark[] tmp = new PersistedLandmark[this.pLandmarksSize + 10];
            System.arraycopy(this.pLandmarks, 0, tmp, 0, this.pLandmarksSize);
            this.pLandmarks = tmp;
         }

         this.pLandmarks[this.pLandmarksSize] = plm;
         ++this.pLandmarksSize;
      }
   }

   private void removeJLandmark(int element) {
      System.arraycopy(this.pLandmarks, 0, this.pLandmarks, 0, element);
      System.arraycopy(this.pLandmarks, element + 1, this.pLandmarks, element, this.pLandmarks.length - 1 - element);
      this.pLandmarks[--this.pLandmarksSize] = null;
      if (this.pLandmarksSize == 0) {
         this.pLandmarks = null;
      }

   }

   private void checkWritableStore() throws IOException {
      if (this.readOnly) {
         throw new IOException("Read Only LandmarkStore");
      }
   }

   private boolean isGeoLocationValid(QualifiedCoordinates qf) {
      boolean valid = false;
      valid = qf.latitude >= -90.0D && qf.latitude <= 90.0D && qf.longitude >= -180.0D && qf.longitude < 180.0D && (Float.isNaN(qf.horizontalAccuracy) || qf.horizontalAccuracy >= 0.0F) && (Float.isNaN(qf.verticalAccuracy) || qf.verticalAccuracy >= 0.0F);
      return valid;
   }

   private void reset() {
      int len = this.categories == null ? 0 : this.categories.length;

      int i;
      for(i = 0; i < len; ++i) {
         this.categories[i].reset();
      }

      for(i = 0; i < this.pLandmarksSize; ++i) {
         this.pLandmarks[i].invalidate();
      }

      this.pLandmarks = null;
      this.categories = null;
   }

   private PersistedLandmark[] retrieveByName(String name, PersistedLandmark[] dataSet) {
      if (name == null) {
         return dataSet;
      } else if (dataSet == null) {
         return null;
      } else {
         PersistedLandmark[] tmpDataSet = new PersistedLandmark[dataSet.length];
         int dataIndex = 0;

         for(int i = 0; i < dataSet.length && dataSet[i] != null; ++i) {
            if (dataSet[i].name.equals(name)) {
               tmpDataSet[dataIndex] = dataSet[i];
               ++dataIndex;
            }
         }

         if (dataIndex < tmpDataSet.length) {
            PersistedLandmark[] oldData = tmpDataSet;
            tmpDataSet = new PersistedLandmark[dataIndex];
            System.arraycopy(oldData, 0, tmpDataSet, 0, dataIndex);
         }

         return tmpDataSet;
      }
   }

   private PersistedLandmark[] retrieveByCategoryAndCoords(String cat, boolean hascoords, double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) throws IOException {
      int[] landmarkids = null;
      if (!(minLongitude < -180.0D) && !(minLongitude >= 180.0D) && !(maxLongitude < -180.0D) && !(maxLongitude >= 180.0D) && !(minLatitude < -90.0D) && !(minLatitude > 90.0D) && !(maxLatitude < -90.0D) && !(maxLatitude > 90.0D) && !(minLatitude > maxLatitude)) {
         if (this.pLandmarks == null) {
            return null;
         } else {
            int[] landmarkids;
            if (cat == null) {
               landmarkids = nativeRetrieveByCategoryAndCoords(false, (byte)0, hascoords, minLatitude, maxLatitude, minLongitude, maxLongitude, this.storeID);
            } else {
               int idx = this.getCategoryIndex(cat);
               Category tmpcat = idx < 0 ? null : this.categories[idx];
               if (tmpcat == null) {
                  return null;
               }

               landmarkids = nativeRetrieveByCategoryAndCoords(true, tmpcat.getID(), hascoords, minLatitude, maxLatitude, minLongitude, maxLongitude, this.storeID);
            }

            PersistedLandmark[] tmpDataSet = new PersistedLandmark[landmarkids.length];
            int dataIndex = 0;

            for(int i = 0; i < landmarkids.length; ++i) {
               for(int j = 0; j < this.pLandmarks.length; ++j) {
                  if (this.pLandmarks[j].getLandmarkID() == landmarkids[i]) {
                     tmpDataSet[dataIndex] = this.pLandmarks[j];
                     ++dataIndex;
                     break;
                  }
               }
            }

            if (dataIndex < tmpDataSet.length) {
               PersistedLandmark[] oldData = tmpDataSet;
               tmpDataSet = new PersistedLandmark[dataIndex];
               System.arraycopy(oldData, 0, tmpDataSet, 0, dataIndex);
            }

            return tmpDataSet;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private Enumeration enumerateLandmarks(PersistedLandmark[] plms) throws IOException {
      if (plms != null && this.pLandmarksSize != 0) {
         int i = false;
         Landmark[] dataSet = new Landmark[plms.length];

         int i;
         for(i = 0; i < plms.length && plms[i] != null; ++i) {
            dataSet[i] = plms[i].getLandmark();
         }

         if (i < dataSet.length) {
            Landmark[] oldData = dataSet;
            dataSet = new Landmark[i];
            System.arraycopy(oldData, 0, dataSet, 0, i);
         }

         return dataSet != null && i != 0 ? new HelperEnumeration(dataSet) : null;
      } else {
         return null;
      }
   }

   private static native int nativeStoreExists(String var0);

   private static native void nativeDeleteStore(int var0) throws IOException;

   private static native void nativeCreateStore(String var0) throws IOException, IllegalArgumentException;

   private static native String[] nativeGetStorenames() throws IOException;

   private static native int nativeCountLandmarks(int var0) throws IOException;

   private static native void nativeLoadNextLandmark(int var0, PersistedLandmark var1) throws IOException;

   private static native boolean nativeMoveLandmarkToJava(PersistedLandmark var0, byte[] var1);

   private static native void nativeClearLandmarks();

   private static native int nativeGetCategories(int var0) throws IOException;

   private static native void nativeLoadCategories(String[] var0, byte[] var1) throws IOException;

   private static native int nativeAddLandmark(int var0, PersistedLandmark var1, byte var2) throws IOException;

   private static native void nativeDeleteLandmark(int var0, int var1) throws IOException;

   private native byte nativeAddCategory(int var1, String var2) throws IOException;

   private static native void nativeDeleteCategory(int var0, byte var1) throws IOException;

   private static native void nativeAddCategoryToLandmark(int var0, int var1, byte var2) throws IOException;

   private static native void nativeDeleteCategoryFromLandmark(int var0, int var1, byte var2) throws IOException;

   private static native int[] nativeRetrieveByCategoryAndCoords(boolean var0, byte var1, boolean var2, double var3, double var5, double var7, double var9, int var11) throws IOException;

   static {
      if (PolicyAccess.isManufacturerSigned0()) {
         LocationUtil.setLandmarkStoreAccessorIF(new LandmarkStore.LandmarkStoreAccessor());
      }

   }

   private static class LandmarkStoreAccessor implements LocationUtil.LandmarkStoreAccessorIF {
      private LandmarkStoreAccessor() {
      }

      public int getLandmarkStoreID(LandmarkStore lms) {
         return lms.getStoreID();
      }

      // $FF: synthetic method
      LandmarkStoreAccessor(Object x0) {
         this();
      }
   }
}
