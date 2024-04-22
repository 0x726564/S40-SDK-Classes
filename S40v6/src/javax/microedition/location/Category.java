package javax.microedition.location;

class Category {
   static final byte INVALID_CATEGORY_ID = 0;
   private static final int[] CATEGORY_IDS = new int[]{3000, 6000, 9000, 12000, 15000, 18000, 21000, 24000, 27000, 30000, 33000, 36000, 39000, 42000, 45000};
   private static final String[] CATEGORY_NAMES;
   String name;
   private byte id;
   PersistedLandmark[] pLandmarks;

   Category(String name) {
      this.name = name;
      this.id = 0;

      for(int i = 0; i < CATEGORY_NAMES.length; ++i) {
         if (name.equals(CATEGORY_NAMES[i])) {
            this.id = (byte)CATEGORY_IDS[i];
            break;
         }
      }

   }

   Category(String name, byte id) {
      this.name = name;
      this.id = id;
   }

   boolean equivalent(String name, byte id) {
      return id != 0 && id == this.id || this.name != null && this.name.equals(name);
   }

   void addLandmark(PersistedLandmark plm) {
      int index = LandmarkStore.getLandmarkIndex(this.pLandmarks, plm);
      if (index == -1) {
         if (this.pLandmarks == null) {
            this.pLandmarks = new PersistedLandmark[]{plm};
         } else {
            PersistedLandmark[] tmp = new PersistedLandmark[this.pLandmarks.length + 1];
            System.arraycopy(this.pLandmarks, 0, tmp, 0, this.pLandmarks.length);
            tmp[this.pLandmarks.length] = plm;
            this.pLandmarks = tmp;
         }
      }

   }

   boolean containsLandmark(PersistedLandmark plm) {
      int index = LandmarkStore.getLandmarkIndex(this.pLandmarks, plm);
      return index != -1;
   }

   void removeLandmark(int element) {
      if (element >= 0 && element < this.pLandmarks.length) {
         PersistedLandmark[] tmp = new PersistedLandmark[this.pLandmarks.length - 1];
         System.arraycopy(this.pLandmarks, 0, tmp, 0, element);
         System.arraycopy(this.pLandmarks, element + 1, tmp, element, tmp.length - element);
         this.pLandmarks = tmp.length == 0 ? null : tmp;
      }
   }

   void reset() {
      this.id = 0;
      this.name = null;
      this.pLandmarks = null;
   }

   byte getID() {
      return this.id;
   }

   void setID(byte cid) {
      this.id = cid;
   }

   private static native String nativeGetText(byte var0);

   static {
      CATEGORY_NAMES = new String[CATEGORY_IDS.length];

      for(byte i = 0; i < CATEGORY_NAMES.length; ++i) {
         CATEGORY_NAMES[i] = nativeGetText(i);
      }

   }
}
