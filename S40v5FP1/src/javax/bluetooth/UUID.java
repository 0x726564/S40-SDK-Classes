package javax.bluetooth;

public class UUID {
   private final String r;

   public UUID(long var1) {
      if (var1 >= 0L && var1 <= 4294967295L) {
         this.r = a(Long.toString(var1, 16), true);
      } else {
         throw new IllegalArgumentException("uuidValue not within range");
      }
   }

   public UUID(String var1, boolean var2) {
      if (var1 == null) {
         throw new NullPointerException("uuidValue invalid");
      } else {
         int var3;
         if ((var3 = var1.length()) != 0 && (!var2 || var3 <= 8) && (var2 || var3 <= 32)) {
            for(int var5 = 0; var5 < var3; ++var5) {
               char var4;
               if (((var4 = var1.charAt(var5)) < '0' || var4 > '9') && (var4 < 'a' || var4 > 'f') && (var4 < 'A' || var4 > 'F')) {
                  throw new NumberFormatException("uuidValue not defined in hex");
               }
            }

            this.r = a(var1, var2);
         } else {
            throw new IllegalArgumentException("invalid length of uuid");
         }
      }
   }

   public final String toString() {
      return new String(this.r);
   }

   public final boolean equals(Object var1) {
      if (var1 != null && var1 instanceof UUID) {
         return this.r.compareTo(((UUID)var1).toString()) == 0;
      } else {
         return false;
      }
   }

   public final int hashCode() {
      return this.r.hashCode();
   }

   private static String a(String var0, boolean var1) {
      String var2 = new String(var0);
      var0 = null;
      if (var1) {
         var2 = var2.concat("00001000800000805F9B34FB");
      }

      var0 = new String("0");

      for(int var3 = 0; var3 < var2.length(); ++var3) {
         if (var2.charAt(var3) != '0') {
            var0 = var2.substring(var3);
            break;
         }
      }

      return var0.toUpperCase();
   }
}
