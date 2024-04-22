package com.sun.midp.io;

import com.nokia.mid.impl.isa.util.UrlParser;
import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;

public class InternalConnector {
   private static String dF = "com.nokia.mid.impl.isa.io.protocol.external";
   private static String dG = "com.nokia.mid.impl.isa.io.protocol.internal";
   private static String dH = "com.sun.midp.io.j2me";
   private static String dI = "com.sun.cldc.io.j2me";
   public static final String DRM_PREFIX = "?drm=";
   private static boolean dJ = System.getProperty("ENABLE_CLDC_PROTOCOLS") != null;

   private InternalConnector() {
   }

   public static Connection openInternal(String var0, int var1, boolean var2) throws IOException {
      try {
         return a(var0, var1, var2, dG);
      } catch (ClassNotFoundException var6) {
         try {
            return a(var0, var1, var2, dF);
         } catch (ClassNotFoundException var5) {
            try {
               return a(var0, var1, var2, dH);
            } catch (ClassNotFoundException var4) {
               try {
                  return a(var0, var1, var2, dI);
               } catch (ClassNotFoundException var3) {
                  throw new ConnectionNotFoundException("The requested protocol does not exist " + var0);
               }
            }
         }
      }
   }

   public static Connection open(String var0, int var1, boolean var2) throws IOException {
      try {
         return a(var0, var1, var2, dH);
      } catch (ClassNotFoundException var6) {
         try {
            return a(var0, var1, var2, dF);
         } catch (ClassNotFoundException var5) {
            if (dJ) {
               try {
                  return a(var0, var1, var2, dG);
               } catch (ClassNotFoundException var4) {
                  try {
                     return a(var0, var1, var2, dI);
                  } catch (ClassNotFoundException var3) {
                  }
               }
            }

            throw new ConnectionNotFoundException("The requested protocol does not exist " + var0);
         }
      }
   }

   private static Connection a(String var0, int var1, boolean var2, String var3) throws IOException, ClassNotFoundException {
      if (var0 == null) {
         throw new IllegalArgumentException("Null URL");
      } else {
         int var4;
         if ((var4 = var0.indexOf(58)) < 1) {
            throw new IllegalArgumentException("no ':' in URL");
         } else {
            try {
               String var5 = var0.substring(0, var4).toLowerCase();
               var0 = var0.substring(var4 + 1);
               if (var5.equals("file")) {
                  String var9;
                  if ((var9 = UrlParser.getUriComponents(var0)[3]) != null && var9.startsWith("?drm=")) {
                     var5 = "storage.drm";
                  } else {
                     var5 = "storage";
                  }
               }

               return ((ConnectionBaseInterface)Class.forName(var3 + "." + var5 + ".Protocol").newInstance()).openPrim(var0, var1, var2);
            } catch (InstantiationException var6) {
               throw new IOException(var6.toString());
            } catch (IllegalAccessException var7) {
               throw new IOException(var7.toString());
            } catch (ClassCastException var8) {
               throw new IOException(var8.toString());
            }
         }
      }
   }
}
