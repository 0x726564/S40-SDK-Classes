package javax.microedition.rms;

import java.io.IOException;

class RecordStoreFile {
   private static int bF = 0;
   private static int bG = 0;
   private int bH = 0;
   private String bI;

   static String getCurrentMidletSuiteVendor() {
      return null;
   }

   static String getCurrentMidletSuiteName() {
      return null;
   }

   RecordStoreFile(String var1, String var2, String var3) throws IOException {
      int var4 = -1;
      if (var2.equals((Object)null) && var3.equals((Object)null)) {
         this.bI = d(var1);
         var4 = this.sysOpenFile(g(var1));
      } else if (f(var2) && f(var3)) {
         this.bI = a(var2, var3, var1);
         var4 = this.sysOpenFile(var2, var3, g(var1));
      }

      if (var4 == -1) {
         throw new IOException();
      }
   }

   static String d(String var0) {
      String var2;
      if (var0 != null) {
         StringBuffer var1;
         (var1 = new StringBuffer()).append((String)null);
         var1.append('_');
         var1.append((String)null);
         var1.append('_');
         var1.append(var0);
         var2 = var1.toString();
      } else {
         var2 = new String();
      }

      return var2;
   }

   static String a(String var0, String var1, String var2) {
      StringBuffer var3 = new StringBuffer();
      boolean var4 = false;
      if (var2 != null) {
         if (var0 != null && var1 != null) {
            var4 = true;
            var3.append(var0);
            var3.append('_');
            var3.append(var1);
         } else if (var0 == null && var1 == null) {
            var4 = true;
            var3.append((String)null);
            var3.append('_');
            var3.append((String)null);
         }

         var3.append('_');
         var3.append(var2);
      }

      String var5;
      if (var4) {
         var5 = var3.toString();
      } else {
         var5 = new String();
      }

      return var5;
   }

   String getUniqueIdPath() {
      return this.bI;
   }

   static boolean b(String var0, String var1, String var2) {
      int var3 = -1;
      if (var1.equals((Object)null) && var2.equals((Object)null)) {
         var3 = sysExists(g(var0));
      } else if (f(var1) && f(var2)) {
         var3 = sysExists(var1, var2, g(var0));
      }

      return var3 != -1;
   }

   static boolean e(String var0) {
      return sysDeleteFile(g(var0)) == 0;
   }

   final void seek(int var1) throws IOException {
      if (var1 < 0) {
         throw new IOException();
      }
   }

   final void write(byte[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var3 + var2 <= var1.length) {
         if (this.sysWrite(var1, var2, var3) < 0) {
            throw new IOException();
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   final int read(byte[] var1) throws IOException {
      int var2;
      if ((var2 = this.sysRead(var1, 0, var1.length)) < 0) {
         throw new IOException();
      } else {
         return var2;
      }
   }

   final int read(byte[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var3 + var2 <= var1.length) {
         int var4;
         if ((var4 = this.sysRead(var1, var2, var3)) < 0) {
            throw new IOException();
         } else {
            return var4;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   static String[] listRecordStores() {
      String[] var0 = null;
      String[] var1;
      if ((var1 = sysListRecordStores()) != null) {
         var0 = new String[var1.length];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            String var3 = var1[var2];
            StringBuffer var4 = new StringBuffer();
            boolean var5 = false;

            for(int var6 = 0; var6 < var3.length(); ++var6) {
               char var7;
               if (((var7 = var3.charAt(var6)) < 'a' || var7 > 'z') && (var7 < '0' || var7 > '9') && var7 != '.') {
                  if (var7 == '%') {
                     var5 = !var5;
                  } else if (var7 >= '\ue000' && var7 <= '\uf8ff') {
                     if (!var5) {
                        var7 = (char)(var7 & -57345);
                     }

                     var5 = false;
                  } else {
                     var5 = false;
                  }
               } else {
                  var5 = false;
               }

               if (!var5) {
                  var4.append(var7);
               }
            }

            var0[var2] = var4.toString();
         }
      }

      return var0;
   }

   private static boolean f(String var0) {
      boolean var1 = true;
      if (var0.indexOf(0) != -1 || var0.indexOf(10) != -1 || var0.indexOf(13) != -1) {
         var1 = false;
      }

      return var1;
   }

   private static String g(String var0) {
      StringBuffer var4 = new StringBuffer();
      int var2 = var0.length();

      for(int var1 = 0; var1 < var2; ++var1) {
         char var3;
         if (((var3 = var0.charAt(var1)) < 'a' || var3 > 'z') && (var3 < '0' || var3 > '9') && var3 != '.') {
            if (var3 >= 'A' && var3 <= 'Z') {
               var3 |= '\ue000';
            } else if (var3 >= '\ue000' && var3 <= '\uf8ff') {
               var4.append('%');
            } else {
               for(int var5 = 0; var5 < ((Object[])null).length; ++var5) {
                  if (((Object[])null)[var5] == var3 && var3 <= 6399) {
                     var3 |= '\ue000';
                     break;
                  }
               }
            }
         }

         var4.append(var3);
      }

      return var4.toString();
   }

   final void truncate(int var1) throws IOException {
      if (this.sysTruncate(var1) == -1) {
         throw new IOException();
      }
   }

   static int h(String var0) {
      return sysSpaceAvailableForCreation(g(var0));
   }

   native int spaceAvailable();

   int getCurrentMidletId() {
      return this.sysGetCurrentMidletId();
   }

   private native int sysOpenFile(String var1);

   private native int sysOpenFile(String var1, String var2, String var3);

   private static native int sysExists(String var0);

   private static native int sysExists(String var0, String var1, String var2);

   private native int sysTruncate(int var1);

   private native int sysWrite(byte[] var1, int var2, int var3);

   private native int sysRead(byte[] var1, int var2, int var3);

   private static native int sysDeleteFile(String var0);

   private static native String[] sysListRecordStores();

   private static native int sysSpaceAvailableForCreation(String var0);

   private static native void nativeStaticInitialiser();

   private native int sysGetCurrentMidletId();

   static {
      nativeStaticInitialiser();
   }
}
