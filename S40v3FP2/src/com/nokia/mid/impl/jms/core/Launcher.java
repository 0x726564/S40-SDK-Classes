package com.nokia.mid.impl.jms.core;

import com.nokia.mid.impl.jms.device.Device;
import java.util.Enumeration;
import java.util.Hashtable;

public class Launcher {
   private static Launcher m_launcher;

   protected Launcher() {
   }

   public static Launcher getLauncher() {
      if (m_launcher == null) {
         m_launcher = new Launcher();
      }

      return m_launcher;
   }

   public void launchMIDlet(int var1, Hashtable var2, String var3) throws LauncherException, IllegalArgumentException {
      this.launchMIDlet(var1, 0, var2, var3);
   }

   public synchronized void launchMIDlet(int var1, int var2, Hashtable var3, String var4) throws LauncherException, IllegalArgumentException {
      if (var1 <= 0) {
         throw new IllegalArgumentException("The MIDlet Id is invalid");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("The MIDlet Number is invalid");
      } else {
         String var5 = null;
         StringBuffer var6 = new StringBuffer();
         if (var3 != null) {
            boolean var7 = true;

            Object var9;
            Object var10;
            for(Enumeration var8 = var3.keys(); var8.hasMoreElements(); var6.append(this.URLEncode((String)var9) + "=" + this.URLEncode((String)var10))) {
               var9 = var8.nextElement();
               var10 = var3.get(var9);
               if (var9 == null || var10 == null || !(var9 instanceof String) || !(var10 instanceof String)) {
                  throw new IllegalArgumentException("Non String properties");
               }

               if (var7) {
                  var7 = false;
               } else {
                  var6.append(";");
               }
            }
         }

         var5 = var6.toString();
         if (!this.launch0(var1, var2, var5)) {
            throw new LauncherException("Cannot launch the MIDlet");
         }
      }
   }

   private String URLEncode(String var1) {
      boolean var2 = false;
      StringBuffer var3 = new StringBuffer(var1.length());

      for(int var4 = 0; var4 < var1.length(); ++var4) {
         char var5 = var1.charAt(var4);
         if (!this.encodeThisCharacter(var5)) {
            var3.append((char)var5);
         } else {
            var2 = true;
            var3.append('%');
            var3.append(Integer.toHexString(var5 >> 4 & 15).toUpperCase());
            var3.append(Integer.toHexString(var5 & 15).toUpperCase());
         }
      }

      return var2 ? var3.toString() : var1;
   }

   private boolean encodeThisCharacter(int var1) {
      boolean var2 = false;
      if (var1 == 32 || var1 == 96 || var1 == 47 || var1 == 44 || var1 == 43 || var1 >= 91 && var1 <= 94 || var1 >= 123 && var1 <= 125 || var1 >= 58 && var1 <= 64 || var1 >= 34 && var1 <= 38 || var1 >= 128 && var1 <= 255 || var1 >= 0 && var1 <= 31 || var1 == 127) {
         var2 = true;
      }

      return var2;
   }

   private native boolean launch0(int var1, int var2, String var3);

   /** @deprecated */
   public static void launchBrowser(String var0) throws IllegalArgumentException {
      if (var0 == null) {
         throw new IllegalArgumentException("Invalid HTTP URL");
      } else if (var0.equals("") && !launchBrowser0(var0)) {
         throw new IllegalArgumentException("Invalid URL");
      } else {
         String var1 = var0.substring(0, 5).toUpperCase();
         if (!var1.startsWith("HTTP") && !var1.startsWith("HTTPS")) {
            throw new IllegalArgumentException("URL is not a HTTP or HTTPS url");
         } else if (Device.isInFlightMode() && checkPermission0("javax.microedition.io.Connector.http") == 0) {
            throw new SecurityException();
         } else if (!launchBrowser0(var0)) {
            throw new IllegalArgumentException("Invalid URL");
         }
      }
   }

   private static native int checkPermission0(String var0);

   private static native boolean launchBrowser0(String var0);

   public static synchronized void handleContent(String var0) throws IllegalArgumentException {
      handleContent0(var0);
   }

   private static native void handleContent0(String var0);
}
