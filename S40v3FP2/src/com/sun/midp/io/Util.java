package com.sun.midp.io;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

public abstract class Util {
   public static byte[] toCString(String var0) {
      int var1 = var0.length();
      byte[] var2 = new byte[var1 + 1];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = (byte)var0.charAt(var3);
      }

      return var2;
   }

   public static String toJavaString(byte[] var0) {
      int var1;
      for(var1 = 0; var0[var1] != 0; ++var1) {
      }

      try {
         return new String(var0, 0, var1, "ISO8859_1");
      } catch (UnsupportedEncodingException var4) {
         return null;
      }
   }

   public static Vector getCommaSeparatedValues(String var0) {
      Vector var1 = new Vector(5, 5);
      int var2 = var0.length();
      if (var2 == 0) {
         return var1;
      } else {
         int var3 = 0;

         while(true) {
            int var4 = var0.indexOf(44, var3);
            if (var4 == -1) {
               var1.addElement(var0.substring(var3, var2).trim());
               return var1;
            }

            var1.addElement(var0.substring(var3, var4).trim());
            var3 = var4 + 1;
         }
      }
   }
}
