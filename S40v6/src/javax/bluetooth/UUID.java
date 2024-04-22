package javax.bluetooth;

public class UUID {
   private static final String baseUUID = "00001000800000805F9B34FB";
   private final String valueUUID;

   public UUID(long uuidValue) {
      if (uuidValue >= 0L && uuidValue <= 4294967295L) {
         this.valueUUID = this.create128BitUuid(Long.toString(uuidValue, 16), true);
      } else {
         throw new IllegalArgumentException("uuidValue not within range");
      }
   }

   public UUID(String uuidValue, boolean shortUUID) {
      if (uuidValue == null) {
         throw new NullPointerException("uuidValue invalid");
      } else {
         int length = uuidValue.length();
         if (length != 0 && (!shortUUID || length <= 8) && (shortUUID || length <= 32)) {
            for(int i = 0; i < length; ++i) {
               char c = uuidValue.charAt(i);
               if ((c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F')) {
                  throw new NumberFormatException("uuidValue not defined in hex");
               }
            }

            this.valueUUID = this.create128BitUuid(uuidValue, shortUUID);
         } else {
            throw new IllegalArgumentException("invalid length of uuid");
         }
      }
   }

   public String toString() {
      return new String(this.valueUUID);
   }

   public boolean equals(Object value) {
      if (value != null && value instanceof UUID) {
         return this.valueUUID.compareTo(((UUID)value).toString()) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.valueUUID.hashCode();
   }

   private String create128BitUuid(String uuidValue, boolean shortUUID) {
      String temp = new String(uuidValue);
      String res = null;
      if (shortUUID) {
         temp = temp.concat("00001000800000805F9B34FB");
      }

      res = new String("0");

      for(int i = 0; i < temp.length(); ++i) {
         if (temp.charAt(i) != '0') {
            res = temp.substring(i);
            break;
         }
      }

      return res.toUpperCase();
   }
}
