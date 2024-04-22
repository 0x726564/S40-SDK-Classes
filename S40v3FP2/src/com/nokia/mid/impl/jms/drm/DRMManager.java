package com.nokia.mid.impl.jms.drm;

import com.nokia.mid.impl.jms.core.MIDletSuite;
import com.nokia.mid.impl.jms.file.File;

public class DRMManager {
   public static final byte DRM_VALIDITY_NOT_VALID = 0;
   public static final byte DRM_VALIDITY_EXPIRED = 1;
   public static final byte DRM_VALIDITY_VALID = 2;
   public static final byte DRM_TYPE_UNKNOWN = 0;
   public static final byte DRM_TYPE_FORWARD_LOCK = 1;
   public static final byte DRM_TYPE_COMBINED_DELIVERY = 2;
   public static final byte DRM_TYPE_SEPARATE_DELIVERY = 3;
   public static final byte RIGHTS_RENEW_SUCCEEDED = 1;
   public static final byte RIGHTS_RENEW_FAILED = 2;
   public static final byte RIGHTS_RENEW_ABORTED = 3;
   public static final int FILE_NOT_DRM = -2;
   public static final int FILE_NO_RIGHTS = -1;
   public static final int FILE_HAS_RIGHTS = 1;

   private DRMManager() {
   }

   public static DRMInfo getDRMInfo(String var0) throws DRMException, NullPointerException, IllegalArgumentException {
      if (!File.getFile(var0).exists()) {
         throw new IllegalArgumentException("Specified path does not exist");
      } else {
         return getDRMInfo0(var0);
      }
   }

   public static DRMInfo getDRMInfo(MIDletSuite var0) throws DRMException, NullPointerException, IllegalArgumentException {
      if (var0 != null && var0.exists()) {
         return getDRMInfo0(var0.getJARFilePath());
      } else {
         throw new IllegalArgumentException("A JAR file could not be found for the specified MIDlet");
      }
   }

   public static native int hasRights(String var0, int var1);

   private static native DRMInfo getDRMInfo0(String var0);

   private static native void initialize();

   static {
      initialize();
   }
}
