package javax.microedition.location;

import java.io.IOException;

class PersistedLandmark {
   public static final byte FIELD_DESCRIPTION = 1;
   public static final byte FIELD_ADDRESS = 2;
   LandmarkStore parentStore;
   String name;
   QualifiedCoordinates coordinates;
   AddressInfo address;
   String description;
   private int landmarkID;
   byte dataValidity;

   PersistedLandmark(Landmark lm, LandmarkStore store) {
      this.name = lm.getName();
      this.address = lm.getAddressInfo() == null ? null : lm.getAddressInfo().clone();
      this.coordinates = lm.getQualifiedCoordinates() == null ? null : lm.getQualifiedCoordinates().clone();
      this.description = lm.getDescription();
      this.parentStore = store;
      lm.setPersistedLandmark(this);
      this.dataValidity = lm.getDataValidity();
   }

   PersistedLandmark(LandmarkStore store) {
      this.parentStore = store;
      this.coordinates = new QualifiedCoordinates();
   }

   public String toString() {
      StringBuffer sb = (new StringBuffer("...name:")).append(this.name);
      sb.append("\n...store:");
      sb.append((Object)this.parentStore);
      sb.append(", landmarkID " + this.landmarkID);
      if (this.coordinates == null) {
         sb.append("\n...(NA ,NA)");
      } else {
         sb.append("\n...(").append(this.coordinates.latitude);
         sb.append(", ").append(this.coordinates.longitude);
         sb.append(")");
      }

      sb.append("\n...addr:");
      if ((this.dataValidity & 2) == 0) {
         sb.append("INVALID");
      } else {
         sb.append(this.address == null ? "NA" : this.address.toString());
      }

      sb.append("\n...descr:");
      if ((this.dataValidity & 1) == 0) {
         sb.append("INVALID");
      } else {
         sb.append(this.description == null ? "NA" : this.description);
      }

      sb.append("\n\n");
      return sb.toString();
   }

   Landmark getLandmark() throws IOException {
      return new Landmark(this);
   }

   void update(Landmark lm) throws IOException {
      String backupName = this.name;
      String backupDescription = this.description;
      QualifiedCoordinates backupGeo = this.coordinates;
      AddressInfo backupAddress = this.address;

      try {
         this.name = lm.getName();
         this.address = lm.getAddressInfo() == null ? null : lm.getAddressInfo().clone();
         this.coordinates = lm.getQualifiedCoordinates() == null ? null : lm.getQualifiedCoordinates().clone();
         this.description = lm.getDescription();
         byte[] catIDs = this.parentStore.getCatIDs(this);
         synchronized(LandmarkStore.globalLock) {
            this.nativeStoreLandmark(this.parentStore.getStoreID(), this.landmarkID, catIDs[0], catIDs[1], catIDs[2], catIDs[3]);
         }

         this.dataValidity = lm.getDataValidity();
      } catch (IOException var10) {
         var10.printStackTrace();
         this.description = backupDescription;
         this.name = backupName;
         this.address = backupAddress;
         this.coordinates = backupGeo;
         throw var10;
      }
   }

   void invalidate() {
      this.address = null;
      this.description = null;
      this.name = null;
      this.coordinates = null;
      this.parentStore = null;
      this.dataValidity = 0;
   }

   String retrieveDescription() throws IOException {
      synchronized(LandmarkStore.globalLock) {
         if ((byte)(this.dataValidity & 1) == 0) {
            String[] descarr = new String[1];
            nativeRetrieveLandmark(this.parentStore.getStoreID(), this.landmarkID, (byte)1);
            nativeGetStringArrayData(descarr);
            this.description = descarr[0];
            this.dataValidity = (byte)(this.dataValidity | 1);
            if (this.description != null && this.description.length() == 0) {
               this.description = null;
            }
         }
      }

      return this.description;
   }

   AddressInfo retrieveAdrressInfo() throws IOException {
      synchronized(LandmarkStore.globalLock) {
         if ((byte)(this.dataValidity & 2) == 0) {
            this.address = new AddressInfo();
            nativeRetrieveLandmark(this.parentStore.getStoreID(), this.landmarkID, (byte)2);
            nativeGetStringArrayData(this.address.addressFields);
            this.dataValidity = (byte)(this.dataValidity | 2);
         }

         for(int i = 0; i < this.address.addressFields.length; ++i) {
            if (this.address.addressFields[i] != null && this.address.addressFields[i].length() == 0) {
               this.address.addressFields[i] = null;
            }
         }

         return this.address;
      }
   }

   int getLandmarkID() {
      return this.landmarkID;
   }

   void setLandmarkID(int lmid) {
      this.landmarkID = lmid;
   }

   native void nativeStoreLandmark(int var1, int var2, byte var3, byte var4, byte var5, byte var6) throws IOException;

   static native void nativeRetrieveLandmark(int var0, int var1, byte var2) throws IOException;

   static native void nativeGetStringArrayData(String[] var0);
}
