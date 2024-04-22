package com.sun.midp.io;

import com.nokia.mid.impl.isa.util.UrlParser;
import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;

public class InternalConnector {
   private static String nokiaExternalRoot = "com.nokia.mid.impl.isa.io.protocol.external";
   private static String nokiaInternalRoot = "com.nokia.mid.impl.isa.io.protocol.internal";
   private static String midpRoot = "com.sun.midp.io.j2me";
   private static String cldcRoot = "com.sun.cldc.io.j2me";
   public static final String DRM_PREFIX = "?drm=";
   private static boolean enableAllProtocols = System.getProperty("ENABLE_CLDC_PROTOCOLS") != null;

   private InternalConnector() {
   }

   public static Connection openInternal(String name, int mode, boolean timeouts) throws IOException {
      try {
         return openPrim(name, mode, timeouts, nokiaInternalRoot);
      } catch (ClassNotFoundException var7) {
         try {
            return openPrim(name, mode, timeouts, nokiaExternalRoot);
         } catch (ClassNotFoundException var6) {
            try {
               return openPrim(name, mode, timeouts, midpRoot);
            } catch (ClassNotFoundException var5) {
               try {
                  return openPrim(name, mode, timeouts, cldcRoot);
               } catch (ClassNotFoundException var4) {
                  throw new ConnectionNotFoundException("The requested protocol does not exist " + name);
               }
            }
         }
      }
   }

   public static Connection open(String name, int mode, boolean timeouts) throws IOException {
      try {
         return openPrim(name, mode, timeouts, midpRoot);
      } catch (ClassNotFoundException var7) {
         try {
            return openPrim(name, mode, timeouts, nokiaExternalRoot);
         } catch (ClassNotFoundException var6) {
            if (enableAllProtocols) {
               try {
                  return openPrim(name, mode, timeouts, nokiaInternalRoot);
               } catch (ClassNotFoundException var5) {
                  try {
                     return openPrim(name, mode, timeouts, cldcRoot);
                  } catch (ClassNotFoundException var4) {
                  }
               }
            }

            throw new ConnectionNotFoundException("The requested protocol does not exist " + name);
         }
      }
   }

   private static Connection openPrim(String name, int mode, boolean timeouts, String root) throws IOException, ClassNotFoundException {
      if (name == null) {
         throw new IllegalArgumentException("Null URL");
      } else {
         int colon = name.indexOf(58);
         if (colon < 1) {
            throw new IllegalArgumentException("no ':' in URL");
         } else {
            try {
               String protocol = name.substring(0, colon).toLowerCase();
               name = name.substring(colon + 1);
               if (protocol.equals("file")) {
                  String query = UrlParser.getUriComponents(name)[3];
                  if (query != null && query.startsWith("?drm=")) {
                     protocol = "storage.drm";
                  } else {
                     protocol = "storage";
                  }
               }

               Class clazz = Class.forName(root + "." + protocol + ".Protocol");
               ConnectionBaseInterface uc = (ConnectionBaseInterface)clazz.newInstance();
               return uc.openPrim(name, mode, timeouts);
            } catch (InstantiationException var8) {
               throw new IOException(var8.toString());
            } catch (IllegalAccessException var9) {
               throw new IOException(var9.toString());
            } catch (ClassCastException var10) {
               throw new IOException(var10.toString());
            }
         }
      }
   }
}
