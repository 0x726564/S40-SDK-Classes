package com.nokia.mid.impl.isa.io.protocol.external.sms;

public class TextEncoder {
   private static final byte[] kA = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127};
   private static final char[] kB = new char[]{'@', '£', '$', '¥', 'è', 'é', 'ù', 'ì', 'ò', 'Ç', '\n', 'Ø', 'ø', '\r', 'Å', 'å', 'Δ', '_', 'Φ', 'Γ', 'Λ', 'Ω', 'Π', 'Ψ', 'Σ', 'Θ', 'Ξ', 'Æ', 'æ', 'ß', 'É', ' ', '!', '"', '#', '¤', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '¡', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Ä', 'Ö', 'Ñ', 'Ü', '§', '¿', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'ä', 'ö', 'ñ', 'ü', 'à'};
   private static final byte[] kC = new byte[]{20, 40, 41, 47, 60, 61, 62, 64, 101};
   private static final char[] kD = new char[]{'^', '{', '}', '\\', '[', '~', ']', '|', '€'};

   TextEncoder() {
   }

   public static byte[] encode(byte[] var0) {
      byte[] var1 = null;
      if (var0 != null) {
         int var7;
         byte[] var2 = new byte[var7 = var0.length];
         int var6 = 0;

         for(int var4 = 0; var4 < var7; var4 += 2) {
            char var3 = (char)(var0[var4] << 8 | var0[var4 + 1] & 255);

            int var5;
            for(var5 = 0; var5 < kB.length; ++var5) {
               if (var3 == kB[var5]) {
                  var2[var6++] = kA[var5];
                  break;
               }
            }

            if (var5 >= kB.length) {
               for(var5 = 0; var5 < kD.length; ++var5) {
                  if (var3 == kD[var5]) {
                     var2[var6++] = 27;
                     var2[var6++] = kC[var5];
                     break;
                  }
               }

               if (var5 >= kD.length) {
                  return null;
               }
            }
         }

         var1 = new byte[var6];
         System.arraycopy(var2, 0, var1, 0, var6);
      }

      return var1;
   }
}
