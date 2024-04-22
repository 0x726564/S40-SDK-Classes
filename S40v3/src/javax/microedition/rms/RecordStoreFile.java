package javax.microedition.rms;

import java.io.IOException;

class RecordStoreFile {
   private static int currentMidletJarFimId;
   private static String currentMidletSuiteName;
   private static String currentMidletSuiteVendor;
   private static int RS_MAX_NBR_MIDLET_RS = 0;
   private static int totalNbrMidletRs = 0;
   private static final char SUBSTITUTION_CHAR = '%';
   private static final char UCS2_PU_START = '\ue000';
   private static final char UCS2_PU_END = '\uf8ff';
   private static char[] illegalChars;
   private int recordStoreFileId = 0;
   private int recordStoreFilePos = 0;
   private String myStoragePath;

   static String getCurrentMidletSuiteVendor() {
      return currentMidletSuiteVendor;
   }

   static String getCurrentMidletSuiteName() {
      return currentMidletSuiteName;
   }

   RecordStoreFile(String var1, String var2, String var3) throws IOException {
      int var4 = -1;
      if (var2.equals(currentMidletSuiteVendor) && var3.equals(currentMidletSuiteName)) {
         this.myStoragePath = getUniqueIdPath(var1);
         var4 = this.sysOpenFile(translateRsNameToNative(var1));
      } else if (parseVendorOrSuiteName(var2) && parseVendorOrSuiteName(var3)) {
         this.myStoragePath = getUniqueIdPath(var2, var3, var1);
         var4 = this.sysOpenFile(var2, var3, translateRsNameToNative(var1));
      }

      if (var4 == -1) {
         throw new IOException();
      }
   }

   static String getUniqueIdPath(String var0) {
      String var1;
      if (var0 != null) {
         StringBuffer var2 = new StringBuffer();
         var2.append(currentMidletSuiteVendor);
         var2.append('_');
         var2.append(currentMidletSuiteName);
         var2.append('_');
         var2.append(var0);
         var1 = var2.toString();
      } else {
         var1 = new String();
      }

      return var1;
   }

   static String getUniqueIdPath(String var0, String var1, String var2) {
      StringBuffer var4 = new StringBuffer();
      boolean var5 = false;
      if (var2 != null) {
         if (var0 != null && var1 != null) {
            var5 = true;
            var4.append(var0);
            var4.append('_');
            var4.append(var1);
         } else if (var0 == null && var1 == null) {
            var5 = true;
            var4.append(currentMidletSuiteVendor);
            var4.append('_');
            var4.append(currentMidletSuiteName);
         }

         var4.append('_');
         var4.append(var2);
      }

      String var3;
      if (var5) {
         var3 = var4.toString();
      } else {
         var3 = new String();
      }

      return var3;
   }

   String getUniqueIdPath() {
      return this.myStoragePath;
   }

   static boolean isMaxNbrMidletRsReached() {
      return RS_MAX_NBR_MIDLET_RS != 0 && RS_MAX_NBR_MIDLET_RS <= totalNbrMidletRs;
   }

   static boolean exists(String var0, String var1, String var2) {
      int var3 = -1;
      if (var1.equals(currentMidletSuiteVendor) && var2.equals(currentMidletSuiteName)) {
         var3 = sysExists(translateRsNameToNative(var0));
      } else if (parseVendorOrSuiteName(var1) && parseVendorOrSuiteName(var2)) {
         var3 = sysExists(var1, var2, translateRsNameToNative(var0));
      }

      return var3 != -1;
   }

   static boolean deleteFile(String var0) {
      int var1 = sysDeleteFile(translateRsNameToNative(var0));
      return var1 == 0;
   }

   void seek(int var1) throws IOException {
      if (var1 < 0) {
         throw new IOException();
      } else {
         this.recordStoreFilePos = var1;
      }
   }

   int length() throws IOException {
      int var1 = this.sysLength();
      if (var1 < 0) {
         throw new IOException();
      } else {
         return var1;
      }
   }

   void write(byte[] var1) throws IOException {
      int var2 = this.sysWrite(var1, 0, var1.length);
      if (var2 < 0) {
         throw new IOException();
      }
   }

   void write(byte[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var3 + var2 <= var1.length) {
         int var4 = this.sysWrite(var1, var2, var3);
         if (var4 < 0) {
            throw new IOException();
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   int read(byte[] var1) throws IOException {
      int var2 = this.sysRead(var1, 0, var1.length);
      if (var2 < 0) {
         throw new IOException();
      } else {
         return var2;
      }
   }

   int read(byte[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var3 + var2 <= var1.length) {
         int var4 = this.sysRead(var1, var2, var3);
         if (var4 < 0) {
            throw new IOException();
         } else {
            return var4;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   void close() throws IOException {
      if (this.recordStoreFileId != 0) {
         int var1 = this.sysClose();
         this.recordStoreFileId = 0;
         if (var1 < 0) {
            throw new IOException();
         }
      }

   }

   static String[] listRecordStores() {
      String[] var0 = null;
      String[] var1 = sysListRecordStores();
      if (var1 != null) {
         var0 = new String[var1.length];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            var0[var2] = translateRsNameFromNative(var1[var2]);
         }
      }

      return var0;
   }

   private static boolean parseVendorOrSuiteName(String var0) {
      boolean var1 = true;
      if (var0.indexOf(0) != -1 || var0.indexOf(10) != -1 || var0.indexOf(13) != -1) {
         var1 = false;
      }

      return var1;
   }

   private static String translateRsNameToNative(String var0) {
      StringBuffer var4 = new StringBuffer();
      int var2 = var0.length();

      for(int var1 = 0; var1 < var2; ++var1) {
         char var3 = var0.charAt(var1);
         if ((var3 < 'a' || var3 > 'z') && (var3 < '0' || var3 > '9') && var3 != '.') {
            if (var3 >= 'A' && var3 <= 'Z') {
               var3 |= '\ue000';
            } else if (var3 >= '\ue000' && var3 <= '\uf8ff') {
               var4.append('%');
            } else {
               for(int var5 = 0; var5 < illegalChars.length; ++var5) {
                  if (illegalChars[var5] == var3 && var3 <= 6399) {
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

   private static String translateRsNameFromNative(String var0) {
      StringBuffer var1 = new StringBuffer();
      boolean var2 = false;

      for(int var3 = 0; var3 < var0.length(); ++var3) {
         char var4 = var0.charAt(var3);
         if ((var4 < 'a' || var4 > 'z') && (var4 < '0' || var4 > '9') && var4 != '.') {
            if (var4 == '%') {
               var2 = !var2;
            } else if (var4 >= '\ue000' && var4 <= '\uf8ff') {
               if (!var2) {
                  var4 = (char)(var4 & -57345);
               }

               var2 = false;
            } else {
               var2 = false;
            }
         } else {
            var2 = false;
         }

         if (!var2) {
            var1.append(var4);
         }
      }

      return var1.toString();
   }

   void truncate(int var1) throws IOException {
      if (this.sysTruncate(var1) == -1) {
         throw new IOException();
      }
   }

   static int spaceAvailableForCreation(String var0) {
      return sysSpaceAvailableForCreation(translateRsNameToNative(var0));
   }

   native int spaceAvailable();

   private native int sysOpenFile(String var1);

   private native int sysOpenFile(String var1, String var2, String var3);

   private static native int sysExists(String var0);

   private static native int sysExists(String var0, String var1, String var2);

   private native int sysLength();

   private native int sysTruncate(int var1);

   private native int sysClose();

   private native int sysWrite(byte[] var1, int var2, int var3);

   private native int sysRead(byte[] var1, int var2, int var3);

   private static native int sysDeleteFile(String var0);

   private static native String[] sysListRecordStores();

   private static native int sysSpaceAvailableForCreation(String var0);

   private static native void nativeStaticInitialiser();

   static {
      nativeStaticInitialiser();
   }
}
