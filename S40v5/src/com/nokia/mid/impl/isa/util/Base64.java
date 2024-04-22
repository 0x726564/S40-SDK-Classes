package com.nokia.mid.impl.isa.util;

public class Base64 {
   private static final char[] bx = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

   public static String encode(byte[] var0, int var1, int var2) {
      char[] var3 = new char[(var2 + 2) / 3 << 2];
      int var4 = 0;

      for(int var5 = 0; var5 < var3.length; var5 += 4) {
         boolean var6 = false;
         boolean var7 = false;
         boolean var8 = false;
         byte var9 = var0[var1 + var4];
         var3[var5] = bx[var9 >>> 2 & 63];
         byte var10;
         if (var2 - var4 > 2) {
            var10 = var0[var1 + var4 + 1];
            byte var11 = var0[var1 + var4 + 2];
            var3[var5 + 1] = bx[(var9 << 4 & 48) + (var10 >>> 4 & 15)];
            var3[var5 + 2] = bx[(var10 << 2 & 60) + (var11 >>> 6 & 3)];
            var3[var5 + 3] = bx[var11 & 63];
         } else if (var2 - var4 > 1) {
            var10 = var0[var1 + var4 + 1];
            var3[var5 + 1] = bx[(var9 << 4 & 48) + (var10 >>> 4 & 15)];
            var3[var5 + 2] = bx[var10 << 2 & 60];
            var3[var5 + 3] = '=';
         } else {
            var3[var5 + 1] = bx[var9 << 4 & 48];
            var3[var5 + 2] = '=';
            var3[var5 + 3] = '=';
         }

         var4 += 3;
      }

      return new String(var3);
   }
}
