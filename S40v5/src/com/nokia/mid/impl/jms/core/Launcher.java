package com.nokia.mid.impl.jms.core;

import com.nokia.mid.impl.jms.device.Device;
import java.util.Enumeration;
import java.util.Hashtable;

public class Launcher {
   private static Launcher bu;

   protected Launcher() {
   }

   public static Launcher getLauncher() {
      if (bu == null) {
         bu = new Launcher();
      }

      return bu;
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
         var4 = null;
         StringBuffer var9 = new StringBuffer();
         if (var3 != null) {
            boolean var5 = true;

            Object var7;
            Object var8;
            for(Enumeration var6 = var3.keys(); var6.hasMoreElements(); var9.append(this.f((String)var7) + "=" + this.f((String)var8))) {
               var7 = var6.nextElement();
               var8 = var3.get(var7);
               if (var7 == null || var8 == null || !(var7 instanceof String) || !(var8 instanceof String)) {
                  throw new IllegalArgumentException("Non String properties");
               }

               if (var5) {
                  var5 = false;
               } else {
                  var9.append(";");
               }
            }
         }

         var4 = var9.toString();
         if (!this.launch0(var1, var2, var4)) {
            throw new LauncherException("Cannot launch the MIDlet");
         }
      }
   }

   private String f(String var1) {
      boolean var7 = false;
      StringBuffer var2 = new StringBuffer(var1.length());

      for(int var3 = 0; var3 < var1.length(); ++var3) {
         char var4;
         char var5 = var4 = var1.charAt(var3);
         boolean var6 = false;
         if (var5 == ' ' || var5 == '`' || var5 == '/' || var5 == ',' || var5 == '+' || var5 >= '[' && var5 <= '^' || var5 >= '{' && var5 <= '}' || var5 >= ':' && var5 <= '@' || var5 >= '"' && var5 <= '&' || var5 >= 128 && var5 <= 255 || var5 >= 0 && var5 <= 31 || var5 == 127) {
            var6 = true;
         }

         if (!var6) {
            var2.append((char)var4);
         } else {
            var7 = true;
            var2.append('%');
            var2.append(Integer.toHexString(var4 >> 4 & 15).toUpperCase());
            var2.append(Integer.toHexString(var4 & 15).toUpperCase());
         }
      }

      if (var7) {
         return var2.toString();
      } else {
         return var1;
      }
   }

   private native boolean launch0(int var1, int var2, String var3);

   /** @deprecated */
   public static void launchBrowser(String var0) throws IllegalArgumentException {
      if (var0 == null) {
         throw new IllegalArgumentException("Invalid HTTP URL");
      } else if (var0.equals("") && !launchBrowser0(var0)) {
         throw new IllegalArgumentException("Invalid URL");
      } else {
         String var1;
         if (!(var1 = var0.substring(0, 5).toUpperCase()).startsWith("HTTP") && !var1.startsWith("HTTPS")) {
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
