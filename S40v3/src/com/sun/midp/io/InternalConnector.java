package com.sun.midp.io;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;

public class InternalConnector {
   private static String nokiaExternalRoot = "com.nokia.mid.impl.isa.io.protocol.external";
   private static String nokiaInternalRoot = "com.nokia.mid.impl.isa.io.protocol.internal";
   private static String midpRoot = "com.sun.midp.io.j2me";
   private static String cldcRoot = "com.sun.cldc.io.j2me";
   private static boolean enableAllProtocols = System.getProperty("ENABLE_CLDC_PROTOCOLS") != null;

   private InternalConnector() {
   }

   public static Connection openInternal(String var0, int var1, boolean var2) throws IOException {
      try {
         return openPrim(var0, var1, var2, nokiaInternalRoot);
      } catch (ClassNotFoundException var7) {
         try {
            return openPrim(var0, var1, var2, nokiaExternalRoot);
         } catch (ClassNotFoundException var6) {
            try {
               return openPrim(var0, var1, var2, midpRoot);
            } catch (ClassNotFoundException var5) {
               try {
                  return openPrim(var0, var1, var2, cldcRoot);
               } catch (ClassNotFoundException var4) {
                  throw new ConnectionNotFoundException("The requested protocol does not exist " + var0);
               }
            }
         }
      }
   }

   public static Connection open(String var0, int var1, boolean var2) throws IOException {
      try {
         return openPrim(var0, var1, var2, midpRoot);
      } catch (ClassNotFoundException var7) {
         try {
            return openPrim(var0, var1, var2, nokiaExternalRoot);
         } catch (ClassNotFoundException var6) {
            if (enableAllProtocols) {
               try {
                  return openPrim(var0, var1, var2, nokiaInternalRoot);
               } catch (ClassNotFoundException var5) {
                  try {
                     return openPrim(var0, var1, var2, cldcRoot);
                  } catch (ClassNotFoundException var4) {
                  }
               }
            }

            throw new ConnectionNotFoundException("The requested protocol does not exist " + var0);
         }
      }
   }

   private static Connection openPrim(String var0, int var1, boolean var2, String var3) throws IOException, ClassNotFoundException {
      if (var0 == null) {
         throw new IllegalArgumentException("Null URL");
      } else {
         int var4 = var0.indexOf(58);
         if (var4 < 1) {
            throw new IllegalArgumentException("no ':' in URL");
         } else {
            try {
               String var5 = var0.substring(0, var4).toLowerCase();
               var0 = var0.substring(var4 + 1);
               if (var5.equals("file")) {
                  var5 = "storage";
               } else if (enableAllProtocols && var5.equals("socket") && System.getProperty("ENABLE_HTTP_WIRE") != null) {
                  var5 = "tck";
               }

               Class var6 = Class.forName(var3 + "." + var5 + ".Protocol");
               ConnectionBaseInterface var7 = (ConnectionBaseInterface)var6.newInstance();
               return var7.openPrim(var0, var1, var2);
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
