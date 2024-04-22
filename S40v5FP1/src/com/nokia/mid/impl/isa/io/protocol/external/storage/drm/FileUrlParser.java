package com.nokia.mid.impl.isa.io.protocol.external.storage.drm;

import com.nokia.mid.impl.isa.util.UrlParser;

public class FileUrlParser extends UrlParser {
   public static final int MODE = 4;
   public static final int PURPOSE = 5;
   public static final int PREVIEW = 6;
   public static final int TYPE = 7;
   private static final int _END_MARKER = 8;
   public static final String DRM_PREFIX = "?drm=";
   public static final String DRM_ENCRYPTED = "enc";
   public static final String DRM_DECRYPTED = "dec";
   public static final String DRM_PREVIEW = "preview";
   public static final String DRM_TYPE = "type=";

   public static String[] getUriComponents(String theUri) {
      String[] drmUri = new String[8];
      String[] baseUri = UrlParser.getUriComponents(theUri);
      drmUri[0] = baseUri[0];
      drmUri[1] = baseUri[1];
      drmUri[2] = baseUri[2];
      String query = baseUri[3].toLowerCase();
      if (query != null) {
         String QueryString = query;

         do {
            int idxAmpersand = QueryString.indexOf(38);
            if (idxAmpersand != -1) {
               query = QueryString.substring(0, idxAmpersand);
               QueryString = QueryString.substring(idxAmpersand + 1);
            } else {
               query = QueryString;
               QueryString = null;
            }

            if (query.startsWith("?drm=")) {
               query = query.substring("?drm=".length());
               int ix = query.indexOf(43);
               if (ix != -1) {
                  String mode = query.substring(0, ix);
                  if (mode.equals("dec") || mode.equals("enc")) {
                     drmUri[4] = mode;
                     query = query.substring(ix + 1);
                     ix = query.indexOf(43);
                     if (ix != -1) {
                        drmUri[5] = query.substring(0, ix);
                        drmUri[6] = "preview".equals(query.substring(ix + 1)) ? "preview" : null;
                     } else {
                        drmUri[5] = query;
                     }
                  }
               } else if (!query.equals("dec") && !query.equals("enc")) {
                  if (query.equals("preview")) {
                     drmUri[6] = "preview";
                  }
               } else {
                  drmUri[4] = query;
               }
            }

            if (query.startsWith("type=")) {
               drmUri[7] = query.substring("type=".length());
            }
         } while(QueryString != null);
      }

      return drmUri;
   }
}
