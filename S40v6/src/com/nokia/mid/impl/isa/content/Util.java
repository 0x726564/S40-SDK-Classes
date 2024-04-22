package com.nokia.mid.impl.isa.content;

import java.util.Vector;

public class Util {
   private static InvocationAccessor invAccessor;
   private static boolean chapiSecurityEnabled = true;
   // $FF: synthetic field
   static Class class$javax$microedition$midlet$MIDlet;

   public static void setInvocationAccessor(InvocationAccessor accessor) {
      invAccessor = accessor;
   }

   public static InvocationAccessor getInvocationAccessor() {
      return invAccessor;
   }

   public static boolean hasValidArrayElements(String[] array, boolean testrepeats) {
      Vector v = null;
      if (array == null) {
         return true;
      } else {
         if (testrepeats) {
            v = new Vector();
         }

         boolean repeated = true;

         for(int i = 0; i < array.length; ++i) {
            if (array[i].length() == 0) {
               repeated = false;
               break;
            }

            if (testrepeats) {
               if (v.contains(array[i])) {
                  repeated = false;
                  break;
               }

               v.addElement(array[i]);
            }
         }

         return repeated;
      }
   }

   public static boolean isValidMIDPLocale(String locale) {
      int len = locale.length();
      if (len == 0) {
         return true;
      } else if (len < 2) {
         return false;
      } else {
         char ch = locale.charAt(0);
         if (ch >= 'a' && ch <= 'z') {
            ch = locale.charAt(1);
            if (ch >= 'a' && ch <= 'z') {
               if (len > 2) {
                  if (len < 5 || len == 6) {
                     return false;
                  }

                  ch = locale.charAt(2);
                  if (ch != '-' && ch != '_') {
                     return false;
                  }

                  ch = locale.charAt(3);
                  if (ch < 'A' || ch > 'Z') {
                     return false;
                  }

                  ch = locale.charAt(4);
                  if (ch < 'A' || ch > 'Z') {
                     return false;
                  }

                  if (len > 6) {
                     ch = locale.charAt(5);
                     if (ch != '-' && ch != '_') {
                        return false;
                     }
                  }
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static int findPositionCaseSensitive(String[] array, String element) {
      return findPositionOnStringArray(array, element, true);
   }

   private static int findPositionOnStringArray(String[] array, String element, boolean sensitive) {
      int pos = -1;
      int i = 0;

      while(array != null && i < array.length) {
         label23: {
            if (sensitive) {
               if (array[i].equals(element)) {
                  break label23;
               }
            } else if (array[i].equalsIgnoreCase(element)) {
               break label23;
            }

            ++i;
            continue;
         }

         pos = i;
         break;
      }

      return pos;
   }

   public static int findPosition(String[] array, String element) {
      return findPositionOnStringArray(array, element, false);
   }

   public static boolean isMIDPLifecycleCompliant(String classname) throws ClassNotFoundException {
      if (classname.equals("javax.microedition.midlet.MIDlet")) {
         return false;
      } else {
         boolean isCompliant = true;
         if (!(class$javax$microedition$midlet$MIDlet == null ? (class$javax$microedition$midlet$MIDlet = class$("javax.microedition.midlet.MIDlet")) : class$javax$microedition$midlet$MIDlet).isAssignableFrom(Class.forName(classname))) {
            isCompliant = false;
         }

         return isCompliant;
      }
   }

   public static boolean hasNullElements(String[] array) {
      if (array == null) {
         return false;
      } else {
         boolean result = false;

         for(int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
               result = true;
               break;
            }
         }

         return result;
      }
   }

   public static boolean isInvalidID(String id) {
      if (id == null) {
         return false;
      } else if (id.length() == 0) {
         return true;
      } else {
         char[] idCharArray = id.toCharArray();

         for(int i = 0; i < idCharArray.length; ++i) {
            if (idCharArray[i] >= 0 && idCharArray[i] <= ' ') {
               return true;
            }
         }

         return false;
      }
   }

   public static long convertSourceID(byte[] ba_source_id) {
      long result = 0L;

      for(int i = ba_source_id.length - 1; i >= 0; --i) {
         result *= 256L;
         int n;
         if (ba_source_id[i] >= 0) {
            n = ba_source_id[i];
         } else {
            n = 255 - ~ba_source_id[i];
         }

         result += (long)n;
      }

      return result;
   }

   public static boolean checkChapiSecurity() {
      boolean result = true;
      if (chapiSecurityEnabled) {
         result = nCheckChapiPermission();
      }

      return result;
   }

   public static native boolean nCheckChapiPermission();

   public static native String getAppName(String var0);

   public static native String getAuthority();

   // $FF: synthetic method
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }
}
