package javax.bluetooth;

public class UUID {
   public static final String baseUUID = "00001000800000805F9B34FB";
   private final String valueUUID;

   public UUID(long var1) {
      if (var1 >= 0L && var1 <= 4294967295L) {
         this.valueUUID = this.create128BitUuid(Long.toString(var1, 16), true);
      } else {
         throw new IllegalArgumentException("uuidValue not within range");
      }
   }

   public UUID(String var1, boolean var2) {
      if (var1 == null) {
         throw new NullPointerException("uuidValue invalid");
      } else {
         int var3 = var1.length();
         if (var3 != 0 && (!var2 || var3 <= 8) && (var2 || var3 <= 32)) {
            for(int var5 = 0; var5 < var3; ++var5) {
               char var4 = var1.charAt(var5);
               if ((var4 < '0' || var4 > '9') && (var4 < 'a' || var4 > 'f') && (var4 < 'A' || var4 > 'F')) {
                  throw new NumberFormatException("uuidValue not defined in hex");
               }
            }

            this.valueUUID = this.create128BitUuid(var1, var2);
         } else {
            throw new IllegalArgumentException("invalid length of uuid");
         }
      }
   }

   public String toString() {
      return new String(this.valueUUID);
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof UUID) {
         return this.valueUUID.compareTo(((UUID)var1).toString()) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.valueUUID.hashCode();
   }

   private String create128BitUuid(String var1, boolean var2) {
      String var3 = new String(var1);
      String var4 = null;
      if (var2) {
         var3 = var3.concat("00001000800000805F9B34FB");
      }

      var4 = new String("0");

      for(int var5 = 0; var5 < var3.length(); ++var5) {
         if (var3.charAt(var5) != '0') {
            var4 = var3.substring(var5);
            break;
         }
      }

      return var4.toUpperCase();
   }
}
