package com.nokia.mid.s40.bg;

import com.nokia.mid.impl.isa.util.SharedObjects;
import java.util.Vector;

public class BGUtils {
   private static final int MAX_PROPERTIES_FOR_MIDLET = 10;
   private static Vector vProperties = new Vector();
   private static final Object xSPBGLaunchMutex = SharedObjects.getLock("com.nokia.mid.bg.BGUtils.launchIEMIDlet");

   private BGUtils() {
   }

   public static String getArgs() {
      return nGetLaunchArgs();
   }

   public static String setMIDletProperty(String key, String value) {
      String previousValue = null;
      if (key != null && value != null) {
         if (key.trim().equals("")) {
            throw new IllegalArgumentException("Key can't be empty");
         } else {
            synchronized(SharedObjects.getLock("java.lang.System.getProperty()")) {
               previousValue = System.getProperty(key);
               if (previousValue == null) {
                  if (vProperties.size() > 10) {
                     throw new RuntimeException("Maximum no of properties exceeded: " + vProperties.size());
                  }

                  vProperties.addElement(key);
               }

               if (vProperties.contains(key)) {
                  nSetMidletProperty(key, value);
               }

               return previousValue;
            }
         }
      } else {
         throw new NullPointerException("Key can't be null");
      }
   }

   public static boolean isIEServerResident() {
      return nIsAutoStartSet();
   }

   public static void setIEServerResident(boolean alwaysOn) {
      if (!nSetAutoStartMode(alwaysOn)) {
         throw new RuntimeException("Failed to set the IE BG Server to resident application");
      }
   }

   public static boolean loadIEMIDlet(int midletNumber, String args) {
      if (midletNumber < 1) {
         throw new IllegalArgumentException();
      } else {
         return nLoadFGMIDlet(midletNumber, args);
      }
   }

   public static boolean loadIEMIDlet(int midletNumber) {
      if (midletNumber < 1) {
         throw new IllegalArgumentException();
      } else {
         return nLoadFGMIDlet(midletNumber, (String)null);
      }
   }

   public static boolean launchIEMIDlet(String midletSuiteVendor, String midletName, int midletNumber, String startupNoteText, String args) {
      boolean launchResult = false;
      if (midletSuiteVendor != null && midletName != null) {
         if (midletNumber < 1) {
            throw new IllegalArgumentException("MIDlet number always must be bigger than 0 ");
         } else if (!midletName.trim().equals("") && !midletSuiteVendor.trim().equals("")) {
            args = args == null ? "" : args.trim();
            StringBuffer jamURI = (new StringBuffer()).append("localapp://jam/launch?midlet-vendor=").append(midletSuiteVendor).append(";midlet-name=").append(midletName).append(";midlet-n=").append(midletNumber).append(";midlet-args=").append(args);
            if (startupNoteText != null) {
               jamURI.append(";Text=").append(startupNoteText);
            }

            synchronized(xSPBGLaunchMutex) {
               launchResult = nLaunchFGMIDlet(jamURI.toString());
               return launchResult;
            }
         } else {
            throw new IllegalArgumentException("MIDlet name and suite vendor never can't be empty strings");
         }
      } else {
         throw new NullPointerException("MIDlet-Name and MIDletSuiteVendor can't be null");
      }
   }

   static final native boolean nLoadFGMIDlet(int var0, String var1);

   static final native boolean nLaunchFGMIDlet(String var0);

   static final native boolean nIsAutoStartSet();

   static final native boolean nSetAutoStartMode(boolean var0);

   static final native String nGetLaunchArgs();

   static final native void nSetMidletProperty(String var0, String var1);
}
