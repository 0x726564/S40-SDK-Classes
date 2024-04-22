package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.source_handling.JavaProducerSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.media.MediaException;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

public class ParsedLocator {
   private static final byte SOURCE = 1;
   private static final byte STREAM = 2;
   private static final byte LOCATOR = 3;
   private static final String DELIMITER = "://";
   private static final byte INVALID = 0;
   private static final byte HTTP = 1;
   private static final byte FILE = 2;
   private static final byte DEVICE_TONE = 3;
   private static final byte DEVICE_MIDI = 4;
   private static final byte CAPTURE_RADIO = 5;
   public static final byte CAPTURE_CAMERA_VIDEO = 6;
   public static final byte CAPTURE_CAMERA_IMAGE = 7;
   private static final byte CAPTURE_AUDIO = 8;
   private static final byte RTSP = 9;
   public String contentType = null;
   public boolean previewMode = false;
   private String schemePart = null;
   private Vector params = new Vector();
   private String protocol = null;
   private byte locatorType;
   private final byte createdBy;
   private DataSource source = null;
   private InputStream inputStream = null;

   public ParsedLocator(DataSource var1) throws MediaException {
      this.contentType = var1.getContentType();
      if (!MediaPrefs.nIsDataContentSupported(this.contentType)) {
         throw new MediaException("Invalid content type: " + this.contentType);
      } else {
         this.createdBy = 1;
         this.source = var1;
      }
   }

   public ParsedLocator(InputStream var1, String var2) throws MediaException {
      this.contentType = var2;
      if (!MediaPrefs.nIsDataContentSupported(this.contentType)) {
         throw new MediaException("Invalid content type: " + this.contentType);
      } else {
         this.createdBy = 2;
         this.inputStream = var1;
      }
   }

   public ParsedLocator(String var1) throws MediaException {
      int var3 = 0;
      boolean var7 = true;
      this.createdBy = 3;
      int var4;
      if ((var4 = var1.indexOf(58)) != -1) {
         this.protocol = var1.substring(0, var4).toLowerCase();
      } else {
         var7 = false;
      }

      if (var7) {
         if (var1.startsWith("://", var4)) {
            var4 += "://".length();
         } else {
            var7 = false;
         }
      }

      if (var7) {
         String[] var8 = new String[1];
         if ((var3 = var1.indexOf(63, var4)) != -1) {
            this.schemePart = var1.substring(var4, var3);
         } else {
            this.schemePart = var1.substring(var4);
         }

         this.locatorType = MediaPrefs.nGetLocatorTypeAndContentType(this.protocol, this.schemePart.toLowerCase(), var8);
         this.contentType = var8[0];
         if (this.locatorType == 0 || !MediaPrefs.nIsContentSupported(this.protocol, this.contentType)) {
            var7 = false;
         }
      }

      while(var7 && var3 != -1) {
         ++var3;
         int var2;
         if ((var2 = var1.indexOf(61, var3)) != -1) {
            String var5 = var1.substring(var3, var2);
            ++var2;
            String var6;
            if ((var3 = var1.indexOf(38, var2)) != -1) {
               var6 = var1.substring(var2, var3);
            } else {
               var6 = var1.substring(var2);
            }

            if (!var5.equals("") && !var6.equals("") && var5.indexOf("&") < 0) {
               this.params.addElement(var5);
               this.params.addElement(var6);
            } else {
               var7 = false;
            }
         } else {
            var7 = false;
         }
      }

      if (!var7) {
         throw new MediaException("Invalid locator: " + var1);
      }
   }

   public JavaProducerSource connect() throws IOException, SecurityException {
      JavaProducerSource var1 = null;
      boolean var2 = false;
      String var3 = null;

      try {
         var3 = this.getParamValueAsString("drm");
      } catch (MediaException var7) {
      }

      if (var3 != null) {
         if (!var3.equals("preview")) {
            throw new SecurityException("DRM key must be 'preview'.");
         }

         this.previewMode = true;
         if (this.createdBy == 3 && this.locatorType != 2) {
            throw new SecurityException("Preview operation not valid.");
         }
      }

      if (nMimeTypeSupportsActiveSource(this.contentType)) {
         var2 = true;
      }

      switch(this.createdBy) {
      case 1:
         try {
            var1 = new JavaProducerSource(new ParsedLocator.SourceStreamAsInputStream(this.source));
            var1.useActiveSource = var2;
            break;
         } catch (SecurityException var5) {
            throw new SecurityException("Not enough privileges");
         } catch (Exception var6) {
            throw new IOException("Error connecting to data source");
         }
      case 3:
         try {
            switch(this.locatorType) {
            case 1:
               var1 = new JavaProducerSource(this.protocol + "://" + this.schemePart, var2);
               return var1;
            case 2:
               var1 = new JavaProducerSource(this.protocol + "://" + this.schemePart, false);
               if (var3 == null || var3.equals("preview") && this.nIsPreviewAllowed()) {
                  return var1;
               }

               throw new SecurityException("Preview operation not valid.");
            case 3:
            case 9:
               var1 = new JavaProducerSource();
               return var1;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            default:
               return var1;
            }
         } catch (SecurityException var8) {
            throw new SecurityException("Not enough privileges");
         } catch (Exception var9) {
            throw new IOException("Error connecting to data source");
         }
      default:
         var1 = new JavaProducerSource(this.inputStream);
         var1.useActiveSource = var2;
      }

      return var1;
   }

   public String getParamValueAsString(String var1) throws MediaException {
      String var2 = null;
      int var3 = this.params.indexOf(var1);
      if (var3 >= 0) {
         if (var3 % 2 != 0 || var3 + 2 > this.params.size()) {
            throw new MediaException("Invalid parameter location: " + var1);
         }

         var2 = this.params.elementAt(var3 + 1).toString();
      }

      return var2;
   }

   public int getParamValueAsInt(String var1, int var2, int var3, int var4) throws MediaException {
      int var5 = var4;
      String var6 = this.getParamValueAsString(var1);
      if (var6 != null) {
         boolean var7 = false;

         try {
            var5 = Integer.parseInt(var6);
            if (var5 < var2 || var5 > var3) {
               var7 = true;
            }
         } catch (Exception var9) {
            var7 = true;
         }

         if (var7) {
            throw new MediaException("Invalid parameter bounds: " + var1 + " = " + var6);
         }
      }

      return var5;
   }

   public boolean isMidiDeviceLocator() {
      return this.locatorType == 4;
   }

   public boolean isToneDeviceLocator() {
      return this.locatorType == 3;
   }

   public byte getLocatorType() {
      return this.locatorType;
   }

   public String getBasicLocator() {
      return this.createdBy == 3 ? this.protocol + "://" + this.schemePart : null;
   }

   static native boolean nMimeTypeSupportsActiveSource(String var0);

   private native boolean nIsPreviewAllowed();

   private class SourceStreamAsInputStream extends InputStream {
      private DataSource source;
      private SourceStream stream;

      SourceStreamAsInputStream(DataSource var2) throws IOException {
         this.source = var2;
         this.stream = var2.getStreams()[0];
         var2.connect();
         var2.start();
      }

      public int read() throws IOException {
         throw new IOException("read not supported");
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         return this.stream.read(var1, var2, var3);
      }

      public void close() {
         this.source.disconnect();
      }
   }
}
