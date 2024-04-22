package com.nokia.mid.impl.isa.io.protocol.external.storage.drm;

import com.nokia.mid.impl.isa.util.UrlParser;

public class FileUrlParser extends UrlParser {
   public static final int MODE = 4;
   public static final int PURPOSE = 5;
   public static final int PREVIEW = 6;
   public static final String DRM_PREFIX = "?drm=";
   public static final String DRM_ENCRYPTED = "enc";
   public static final String DRM_DECRYPTED = "dec";

   public static String[] getUriComponents(String var0) {
      String[] var1 = new String[7];
      String[] var2 = UrlParser.getUriComponents(var0);
      var1[0] = var2[0];
      var1[1] = var2[1];
      var1[2] = var2[2];
      String var3 = var2[3].toLowerCase();
      if (var3 != null && var3.startsWith("?drm=")) {
         var3 = var3.substring("?drm=".length());
         int var4 = var3.indexOf(43);
         if (var4 != -1) {
            String var5 = var3.substring(0, var4);
            if (var5.equals("dec") || var5.equals("enc")) {
               var1[4] = var5;
               var3 = var3.substring(var4 + 1);
               var4 = var3.indexOf(43);
               if (var4 != -1) {
                  var1[5] = var3.substring(0, var4);
                  var1[6] = var3.substring(var4 + 1);
               } else {
                  var1[5] = var3;
               }
            }
         } else if (var3.equals("dec") || var3.equals("enc")) {
            var1[4] = var3;
         }
      }

      return var1;
   }
}
