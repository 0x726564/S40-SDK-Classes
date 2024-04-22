package com.nokia.mid.impl.isa.location;

public class SecurityPermission {
   private static final Object LMS_LOCK = new Object();
   private static final Object LOC_LOCK = new Object();

   public static void checkLocationPermission() throws SecurityException {
      synchronized(LOC_LOCK) {
         nativeCheckLocationPermission();
      }
   }

   public static void checkReadPermission() throws SecurityException {
      synchronized(LMS_LOCK) {
         nativeCheckReadPermission();
      }
   }

   public static void checkWritePermission() throws SecurityException {
      synchronized(LMS_LOCK) {
         nativeCheckWritePermission();
      }
   }

   public static void checkCategoryPermission() throws SecurityException {
      synchronized(LMS_LOCK) {
         nativeCheckCategoryPermission();
      }
   }

   public static void checkManagementPermission() throws SecurityException {
      synchronized(LMS_LOCK) {
         nativeCheckManagementPermission();
      }
   }

   private static native void nativeCheckLocationPermission() throws SecurityException;

   private static native void nativeCheckReadPermission() throws SecurityException;

   private static native void nativeCheckWritePermission() throws SecurityException;

   private static native void nativeCheckCategoryPermission() throws SecurityException;

   private static native void nativeCheckManagementPermission() throws SecurityException;
}
