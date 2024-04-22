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

   public void launchMIDlet(int midletId, Hashtable properties, String appId) throws LauncherException, IllegalArgumentException {
      this.launchMIDlet(midletId, 0, properties, appId);
   }

   public synchronized void launchMIDlet(int midletId, int midletNum, Hashtable properties, String appId) throws LauncherException, IllegalArgumentException {
      if (midletId <= 0) {
         throw new IllegalArgumentException("The MIDlet Id is invalid");
      } else if (midletNum < 0) {
         throw new IllegalArgumentException("The MIDlet Number is invalid");
      } else {
         String props = null;
         StringBuffer buffer = new StringBuffer();
         if (properties != null) {
            boolean first = true;

            Object key;
            Object value;
            for(Enumeration keys = properties.keys(); keys.hasMoreElements(); buffer.append(this.URLEncode((String)key) + "=" + this.URLEncode((String)value))) {
               key = keys.nextElement();
               value = properties.get(key);
               if (key == null || value == null || !(key instanceof String) || !(value instanceof String)) {
                  throw new IllegalArgumentException("Non String properties");
               }

               if (first) {
                  first = false;
               } else {
                  buffer.append(";");
               }
            }
         }

         props = buffer.toString();
         if (!this.launch0(midletId, midletNum, props)) {
            throw new LauncherException("Cannot launch the MIDlet");
         }
      }
   }

   private String URLEncode(String s) {
      boolean needToChange = false;
      StringBuffer out = new StringBuffer(s.length());

      for(int i = 0; i < s.length(); ++i) {
         int c = s.charAt(i);
         if (!this.encodeThisCharacter(c)) {
            out.append((char)c);
         } else {
            needToChange = true;
            out.append('%');
            out.append(Integer.toHexString(c >> 4 & 15).toUpperCase());
            out.append(Integer.toHexString(c & 15).toUpperCase());
         }
      }

      return needToChange ? out.toString() : s;
   }

   private boolean encodeThisCharacter(int c) {
      boolean result = false;
      if (c == 32 || c == 96 || c == 47 || c == 44 || c == 43 || c >= 91 && c <= 94 || c >= 123 && c <= 125 || c >= 58 && c <= 64 || c >= 34 && c <= 38 || c >= 128 && c <= 255 || c >= 0 && c <= 31 || c == 127) {
         result = true;
      }

      return result;
   }

   private native boolean launch0(int var1, int var2, String var3);

   /** @deprecated */
   public static void launchBrowser(String url) throws IllegalArgumentException {
      if (url == null) {
         throw new IllegalArgumentException("Invalid HTTP URL");
      } else if (url.equals("") && !launchBrowser0(url)) {
         throw new IllegalArgumentException("Invalid URL");
      } else {
         String scheme = url.substring(0, 5).toUpperCase();
         if (!scheme.startsWith("HTTP") && !scheme.startsWith("HTTPS")) {
            throw new IllegalArgumentException("URL is not a HTTP or HTTPS url");
         } else if (Device.isInFlightMode() && checkPermission0("javax.microedition.io.Connector.http") == 0) {
            throw new SecurityException();
         } else if (!launchBrowser0(url)) {
            throw new IllegalArgumentException("Invalid URL");
         }
      }
   }

   private static native int checkPermission0(String var0);

   private static native boolean launchBrowser0(String var0);

   public static synchronized void handleContent(String filePath) throws IllegalArgumentException {
      handleContent0(filePath);
   }

   private static native void handleContent0(String var0);
}
