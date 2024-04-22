package com.nokia.mid.impl.isa.io.protocol.external.storage.drm;

import java.io.IOException;
import javax.microedition.io.Connection;

public class Protocol extends com.nokia.mid.impl.isa.io.protocol.external.storage.Protocol {
   private final int DRM_DEFAULT = 0;
   private final int DRM_PLAY = 1;
   private final int DRM_DISPLAY = 2;
   private final int DRM_EXECUTE = 3;
   private final int DRM_PRINT = 4;
   private final int DRM_ENC = 5;
   private int drmOperation = -1;
   private int source;

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException, IllegalArgumentException {
      String[] var4 = FileUrlParser.getUriComponents(var1);
      String var5 = var4[4];
      String var6 = var4[5];
      if (var5 != null) {
         var5 = var5.toLowerCase();
         if (var5.equals("enc")) {
            if (var6 == null) {
               this.drmOperation = 5;
            }
         } else if (var5.equals("dec")) {
            this.drmOperation = this.getOperation(var6);
         }
      }

      if (this.drmOperation == -1) {
         throw new IllegalArgumentException("Invalid URL");
      } else {
         return super.openPrim("//" + var4[2], var2, var3);
      }
   }

   protected boolean filesys_access_allowed(String var1, int var2) {
      if (var2 != 1) {
         throw new SecurityException("DRM access for reading only");
      } else {
         this.source = this.createSource(var1, this.drmOperation);
         return this.source != 0;
      }
   }

   protected native int filesys_fileSize();

   protected boolean filesys_open() {
      this.fileHandle = this.source;
      return this.source != 0;
   }

   protected native boolean filesys_close();

   protected native int filesys_read(byte[] var1, int var2, int var3, int var4);

   private int getOperation(String var1) {
      byte var2;
      if (var1 == null) {
         var2 = 0;
      } else {
         var1 = var1.toLowerCase();
         if (var1.equals("play")) {
            var2 = 1;
         } else if (var1.equals("display")) {
            var2 = 2;
         } else if (var1.equals("execute")) {
            var2 = 3;
         } else {
            if (!var1.equals("print")) {
               throw new IllegalArgumentException("Invalid URL");
            }

            var2 = 4;
         }
      }

      return var2;
   }

   private native int createSource(String var1, int var2);
}
