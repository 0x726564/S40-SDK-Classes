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

   RecordStoreFile(String filename, String vendor, String suite) throws IOException {
      int result = -1;
      if (vendor.equals(currentMidletSuiteVendor) && suite.equals(currentMidletSuiteName)) {
         this.myStoragePath = getUniqueIdPath(filename);
         result = this.sysOpenFile(translateRsNameToNative(filename));
      } else if (parseVendorOrSuiteName(vendor) && parseVendorOrSuiteName(suite)) {
         this.myStoragePath = getUniqueIdPath(vendor, suite, filename);
         result = this.sysOpenFile(vendor, suite, translateRsNameToNative(filename));
      }

      if (result == -1) {
         throw new IOException();
      }
   }

   static String getUniqueIdPath(String recordStoreName) {
      String str;
      if (recordStoreName != null) {
         StringBuffer path = new StringBuffer();
         path.append(currentMidletSuiteVendor);
         path.append('_');
         path.append(currentMidletSuiteName);
         path.append('_');
         path.append(recordStoreName);
         str = path.toString();
      } else {
         str = new String();
      }

      return str;
   }

   static String getUniqueIdPath(String vendorName, String suiteName, String recordStoreName) {
      StringBuffer path = new StringBuffer();
      boolean paramsOk = false;
      if (recordStoreName != null) {
         if (vendorName != null && suiteName != null) {
            paramsOk = true;
            path.append(vendorName);
            path.append('_');
            path.append(suiteName);
         } else if (vendorName == null && suiteName == null) {
            paramsOk = true;
            path.append(currentMidletSuiteVendor);
            path.append('_');
            path.append(currentMidletSuiteName);
         }

         path.append('_');
         path.append(recordStoreName);
      }

      String str;
      if (paramsOk) {
         str = path.toString();
      } else {
         str = new String();
      }

      return str;
   }

   String getUniqueIdPath() {
      return this.myStoragePath;
   }

   static boolean isMaxNbrMidletRsReached() {
      return RS_MAX_NBR_MIDLET_RS != 0 && RS_MAX_NBR_MIDLET_RS <= totalNbrMidletRs;
   }

   static boolean exists(String filename, String vendor, String suite) {
      int rv = -1;
      if (vendor.equals(currentMidletSuiteVendor) && suite.equals(currentMidletSuiteName)) {
         rv = sysExists(translateRsNameToNative(filename));
      } else if (parseVendorOrSuiteName(vendor) && parseVendorOrSuiteName(suite)) {
         rv = sysExists(vendor, suite, translateRsNameToNative(filename));
      }

      return rv != -1;
   }

   static boolean deleteFile(String filename) {
      int rv = sysDeleteFile(translateRsNameToNative(filename));
      return rv == 0;
   }

   void seek(int pos) throws IOException {
      if (pos < 0) {
         throw new IOException();
      } else {
         this.recordStoreFilePos = pos;
      }
   }

   int length() throws IOException {
      int rv = this.sysLength();
      if (rv < 0) {
         throw new IOException();
      } else {
         return rv;
      }
   }

   void write(byte[] buf) throws IOException {
      int rv = this.sysWrite(buf, 0, buf.length);
      if (rv < 0) {
         throw new IOException();
      }
   }

   void write(byte[] buf, int offset, int numBytes) throws IOException {
      if (offset >= 0 && numBytes >= 0 && numBytes + offset <= buf.length) {
         int rv = this.sysWrite(buf, offset, numBytes);
         if (rv < 0) {
            throw new IOException();
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   int read(byte[] buf) throws IOException {
      int rv = this.sysRead(buf, 0, buf.length);
      if (rv < 0) {
         throw new IOException();
      } else {
         return rv;
      }
   }

   int read(byte[] buf, int offset, int numBytes) throws IOException {
      if (offset >= 0 && numBytes >= 0 && numBytes + offset <= buf.length) {
         int rv = this.sysRead(buf, offset, numBytes);
         if (rv < 0) {
            throw new IOException();
         } else {
            return rv;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   void close() throws IOException {
      if (this.recordStoreFileId != 0) {
         int rv = this.sysClose();
         this.recordStoreFileId = 0;
         if (rv < 0) {
            throw new IOException();
         }
      }

   }

   static String[] listRecordStores() {
      String[] translatedRSNames = null;
      String[] untranslatedRSNames = sysListRecordStores();
      if (untranslatedRSNames != null) {
         translatedRSNames = new String[untranslatedRSNames.length];

         for(int i = 0; i < untranslatedRSNames.length; ++i) {
            translatedRSNames[i] = translateRsNameFromNative(untranslatedRSNames[i]);
         }
      }

      return translatedRSNames;
   }

   private static boolean parseVendorOrSuiteName(String name) {
      boolean parsedOk = true;
      if (name.indexOf(0) != -1 || name.indexOf(10) != -1 || name.indexOf(13) != -1) {
         parsedOk = false;
      }

      return parsedOk;
   }

   private static String translateRsNameToNative(String name) {
      StringBuffer legalName = new StringBuffer();
      StringBuffer capitalEncoding = new StringBuffer();
      int len = name.length();

      for(int i = 0; i < len; ++i) {
         char ch = name.charAt(i);
         if ((ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '.') {
            if (ch >= 'A' && ch <= 'Z') {
               capitalEncoding.append((int)1);
            } else if (ch >= '\ue000' && ch <= '\uf8ff') {
               capitalEncoding.append((int)0);
               legalName.append('%');
            } else {
               for(int j = 0; j < illegalChars.length; ++j) {
                  if (illegalChars[j] == ch && ch <= 6399) {
                     ch |= '\ue000';
                     break;
                  }
               }

               capitalEncoding.append((int)0);
            }
         } else {
            capitalEncoding.append((int)0);
         }

         legalName.append(ch);
      }

      return legalName.toString() + capitalEncoding.toString();
   }

   private static String translateRsNameFromNative(String name) {
      StringBuffer originalName = new StringBuffer();
      boolean escaped = false;

      for(int i = 0; i < name.length() / 2; ++i) {
         char ch = name.charAt(i);
         if ((ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '.') {
            if (ch == '%') {
               escaped = !escaped;
            } else if (ch >= '\ue000' && ch <= '\uf8ff') {
               if (!escaped) {
                  ch = (char)(ch & -57345);
               }

               escaped = false;
            } else {
               escaped = false;
            }
         } else {
            escaped = false;
         }

         if (!escaped) {
            originalName.append(ch);
         }
      }

      return originalName.toString();
   }

   void truncate(int size) throws IOException {
      if (this.sysTruncate(size) == -1) {
         throw new IOException();
      }
   }

   static int spaceAvailableForCreation(String recordStoreName) {
      return sysSpaceAvailableForCreation(translateRsNameToNative(recordStoreName));
   }

   native int spaceAvailable();

   int getCurrentMidletId() {
      return this.sysGetCurrentMidletId();
   }

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

   private native int sysGetCurrentMidletId();

   static {
      nativeStaticInitialiser();
   }
}
