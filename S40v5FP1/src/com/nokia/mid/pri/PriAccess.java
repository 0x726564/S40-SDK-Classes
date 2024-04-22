package com.nokia.mid.pri;

public class PriAccess {
   public static final int PRI_JAVA_HTTP_STACK_OPTS = 0;
   public static final int PRI_JAVA_HTTPS_STACK_OPTS = 1;
   public static final int PRI_JAVA_SSL_STACK_OPTS = 2;
   public static final int PRI_JAVA_TCK_TEST_FEATURE = 3;
   public static final int PRI_JAVA_SUSPEND_ON_INTERRUPT = 4;
   public static final int PRI_JAVA_CONFIGURATION = 5;
   public static final int PRI_JAVA_GOTO_URL = 6;
   public static final int PRI_GAMES_DOWNLOAD_URL = 7;
   public static final int PRI_OPERATOR_MENU_ENABLED = 8;
   public static final int PRI_OPERATOR_MENU_TITLE = 9;
   public static final int PRI_COLLECTION_MENU = 10;
   public static final int PRI_JAVA_JSR120_HDR_STRING_OPTS = 11;
   private static final int MAX_VALUE = 12;
   public static final int CONFIGURATION_GENERIC = 0;
   public static final int CONFIGURATION_SPRINT = 1;
   public static final int CONFIGURATION_RELIANCE = 2;
   public static final int CONFIGURATION_UNICOM = 3;
   public static final int CONFIGURATION_HUTCH = 4;
   public static final int JSR120_GHRC_DISABLED = 0;
   public static final int JSR120_GHRC_TXT_BASED = 1;
   public static final int JSR120_NONSTANDARD_TXT_BASED = 2;

   public static boolean getFlag(int id) {
      return getInt(id) != 0;
   }

   public static int getInt(int id) {
      if (id >= 0 && id < 12) {
         return getInt0(id);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static String getString(int id) {
      if (id >= 0 && id < 12) {
         return getString0(id);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static byte[] getValue(int id) {
      if (id >= 0 && id < 12) {
         return getValue0(id);
      } else {
         throw new IllegalArgumentException();
      }
   }

   private PriAccess() {
   }

   private static native int getInt0(int var0);

   private static native String getString0(int var0);

   private static native byte[] getValue0(int var0);
}
