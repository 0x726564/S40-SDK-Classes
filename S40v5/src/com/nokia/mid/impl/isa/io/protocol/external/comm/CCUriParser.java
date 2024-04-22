package com.nokia.mid.impl.isa.io.protocol.external.comm;

import javax.microedition.io.ConnectionNotFoundException;

class CCUriParser {
   private static String[] mp = new String[]{"IR", "COM", "USB"};
   private static String[] mq = new String[]{"baudrate", "bitsperchar", "stopbits", "parity", "blocking", "autocts", "autorts"};
   private static final String[] mr = new String[]{"7", "8"};
   private static final String[] ms = new String[]{"1", "2"};
   private static final String[] mt = new String[]{"even", "odd", "none"};
   private static final String[] mu = new String[]{"on", "off"};

   private CCUriParser() {
   }

   protected static String R(String var0) throws IllegalArgumentException {
      String var1 = var0;
      String[] var2 = new String[mq.length];
      if (var0.length() != 0 && var0 != null) {
         boolean var3 = false;

         while(var1 != null && var1.length() != 0) {
            if (var1.charAt(0) != ';' || var1.length() == 1) {
               throw new IllegalArgumentException("';' expected as separator before start of next parameter, or missing parameter after separator.");
            }

            var1 = var1.substring(1);

            int var4;
            for(var4 = 0; var4 < mq.length && (mq[var4].length() > var1.length() || !var1.startsWith(mq[var4])); ++var4) {
            }

            if (var4 == mq.length) {
               throw new IllegalArgumentException("Parameter is not recognised: '" + var1 + "'.");
            }

            var2[var4] = mq[var4] + "=";
            var1 = var1.substring(var2[var4].length());
            switch(var4) {
            case 0:
               var1 = a(var2, var4, var1);
               break;
            case 1:
               var1 = a(var2, var4, var1, mr);
               break;
            case 2:
               var1 = a(var2, var4, var1, ms);
               break;
            case 3:
               var1 = a(var2, var4, var1, mt);
               break;
            case 4:
            case 5:
            case 6:
               var1 = a(var2, var4, var1, mu);
               break;
            default:
               throw new RuntimeException("Unexpected but found parameter while extracting parameters.");
            }
         }
      }

      return var0;
   }

   protected static int S(String var0) throws ConnectionNotFoundException {
      for(int var1 = 0; var1 < mp.length; ++var1) {
         if (var0.length() >= mp[var1].length() && var0.substring(0, mp[var1].length()).toUpperCase().compareTo(mp[var1]) == 0) {
            return var1;
         }
      }

      throw new ConnectionNotFoundException("CommConnection port not recognised: '" + var0 + "'.");
   }

   private static String a(String[] var0, int var1, String var2) {
      String var3 = null;

      try {
         var3 = T(var2);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("Cannot extract number for parameter '" + mq[var1] + "'.");
      }

      var0[var1] = var0[var1] + var3;
      return var2.substring(var3.length());
   }

   protected static String T(String var0) throws IllegalArgumentException {
      if (var0 != null && var0.length() != 0) {
         boolean var1 = false;
         int var2 = 0;

         for(int var3 = 0; var3 < var0.length() && Character.isDigit(var0.charAt(var3)); ++var3) {
            ++var2;
         }

         if (var2 == 0) {
            throw new IllegalArgumentException();
         } else {
            return var0.substring(0, var2);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static String a(String[] var0, int var1, String var2, String[] var3) {
      if (var2 != null && var2.length() != 0) {
         boolean var4 = false;

         int var5;
         for(var5 = 0; var5 < var3.length && (var3[var5].length() > var2.length() || !var2.startsWith(var3[var5])); ++var5) {
         }

         if (var5 == var3.length) {
            throw new IllegalArgumentException("Parameter value string is not recognised: '" + var2 + "'.");
         } else {
            var0[var1] = var0[var1] + var2.substring(0, var3[var5].length());
            return var2.substring(var3[var5].length());
         }
      } else {
         throw new IllegalArgumentException("Parameter value missing for parameter : '" + mq[var1] + "'.");
      }
   }
}
