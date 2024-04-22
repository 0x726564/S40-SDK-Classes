package com.nokia.mid.impl.isa.io.protocol.external.storage.drm;

import com.nokia.mid.impl.isa.util.UrlParser;

public class FileUrlParser extends UrlParser {
   public static final int MODE = 4;
   public static final int PURPOSE = 5;
   public static final int PREVIEW = 6;
   public static final int TYPE = 7;
   public static final String DRM_PREFIX = "?drm=";
   public static final String DRM_ENCRYPTED = "enc";
   public static final String DRM_DECRYPTED = "dec";
   public static final String DRM_TYPE = "type=";

   public static String[] getUriComponents(String var0) {
      String[] var1 = new String[8];
      String[] var5 = UrlParser.getUriComponents(var0);
      var1[0] = var5[0];
      var1[1] = var5[1];
      var1[2] = var5[2];
      if ((var0 = var5[3].toLowerCase()) != null) {
         String var2 = var0;

         do {
            int var3;
            if ((var3 = var2.indexOf(38)) != -1) {
               var0 = var2.substring(0, var3);
               var2 = var2.substring(var3 + 1);
            } else {
               var0 = var2;
               var2 = null;
            }

            if (var0.startsWith("?drm=")) {
               if ((var3 = (var0 = var0.substring("?drm=".length())).indexOf(43)) != -1) {
                  String var4;
                  if ((var4 = var0.substring(0, var3)).equals("dec") || var4.equals("enc")) {
                     var1[4] = var4;
                     if ((var3 = (var0 = var0.substring(var3 + 1)).indexOf(43)) != -1) {
                        var1[5] = var0.substring(0, var3);
                        var1[6] = var0.substring(var3 + 1);
                     } else {
                        var1[5] = var0;
                     }
                  }
               } else if (var0.equals("dec") || var0.equals("enc")) {
                  var1[4] = var0;
               }
            }

            if (var0.startsWith("type=")) {
               var1[7] = var0.substring("type=".length());
            }
         } while(var2 != null);
      }

      return var1;
   }
}
