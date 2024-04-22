package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.source_handling.JavaProducerSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.media.MediaException;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

public class ParsedLocator {
   public static final byte CAPTURE_CAMERA_VIDEO = 6;
   public static final byte CAPTURE_CAMERA_IMAGE = 7;
   public static final byte NORMAL = 0;
   public static final byte ALERT = 1;
   public static final byte MUSIC = 2;
   public String contentType = null;
   public boolean previewMode = false;
   private String gA = null;
   private Vector gB = new Vector();
   private String protocol = null;
   private byte gC;
   private final byte gD;
   private DataSource gE = null;
   private InputStream eM = null;
   private byte bR = 0;
   private boolean gF = false;

   public ParsedLocator(DataSource var1) throws MediaException {
      this.contentType = var1.getContentType();
      if (!MediaPrefs.nIsDataContentSupported(this.contentType)) {
         throw new MediaException("Invalid content type: " + this.contentType);
      } else {
         this.gD = 1;
         this.gE = var1;
      }
   }

   public ParsedLocator(InputStream var1, String var2) throws MediaException {
      this.contentType = var2;
      if (!MediaPrefs.nIsDataContentSupported(this.contentType)) {
         throw new MediaException("Invalid content type: " + this.contentType);
      } else {
         this.gD = 2;
         this.eM = var1;
      }
   }

   public ParsedLocator(String var1) throws MediaException {
      int var3 = 0;
      boolean var6 = true;
      this.gD = 3;
      int var2;
      if ((var2 = var1.indexOf(58)) != -1) {
         this.protocol = var1.substring(0, var2).toLowerCase();
      } else {
         var6 = false;
      }

      if (var6) {
         if (var1.startsWith("://", var2)) {
            var2 += "://".length();
         } else {
            var6 = false;
         }
      }

      if (var6) {
         String[] var4 = new String[1];
         if ((var3 = var1.indexOf(63, var2)) != -1) {
            this.gA = var1.substring(var2, var3);
         } else {
            this.gA = var1.substring(var2);
         }

         this.gC = MediaPrefs.nGetLocatorTypeAndContentType(this.protocol, this.gA.toLowerCase(), var4);
         this.contentType = var4[0];
         if (this.gC == 0 || !MediaPrefs.nIsContentSupported(this.protocol, this.contentType)) {
            var6 = false;
         }

         if (this.gC == 10) {
            this.eM = this.getClass().getResourceAsStream(this.gA);
         }
      }

      while(var6 && var3 != -1) {
         ++var3;
         if ((var2 = var1.indexOf(61, var3)) != -1) {
            String var8 = var1.substring(var3, var2);
            ++var2;
            String var5;
            if ((var3 = var1.indexOf(38, var2)) != -1) {
               var5 = var1.substring(var2, var3);
            } else {
               var5 = var1.substring(var2);
            }

            if (!var8.equals("") && !var5.equals("") && var8.indexOf("&") < 0) {
               this.gB.addElement(var8);
               this.gB.addElement(var5);
            } else {
               var6 = false;
            }
         } else {
            var6 = false;
         }
      }

      if (var6) {
         String var7;
         if ((var7 = this.getParamValueAsString("category")) != null) {
            if (var7.equals("music")) {
               this.bR = 2;
            } else if (var7.equals("alert")) {
               this.bR = 1;
            } else {
               if (!var7.equals("normal")) {
                  throw new MediaException("Invalid category: " + var7);
               }

               this.bR = 0;
            }
         }

         if (this.getParamValueAsString("streamable") != null) {
            this.gF = this.getParamValueAsBoolean("streamable");
         } else {
            this.gF = nIsJADStreamingFlagSet();
         }
      } else {
         throw new MediaException("Invalid locator: " + var1);
      }
   }

   public JavaProducerSource connect() throws IOException, SecurityException {
      JavaProducerSource var1 = null;
      boolean var2 = false;
      String var3 = null;
      if ((var3 = this.getParamValueAsString("drm")) != null) {
         if (!var3.equals("preview")) {
            throw new SecurityException("DRM key must be 'preview'.");
         }

         this.previewMode = true;
         if (this.gD == 3 && this.gC != 2) {
            throw new SecurityException("Preview operation not valid.");
         }
      }

      if (nMimeTypeSupportsActiveSource(this.contentType) && this.gF) {
         var2 = true;
      }

      switch(this.gD) {
      case 1:
         try {
            (var1 = new JavaProducerSource(new ParsedLocator.SourceStreamAsInputStream(this.gE))).useActiveSource = var2;
            break;
         } catch (SecurityException var4) {
            throw new SecurityException("Not enough privileges");
         } catch (Exception var5) {
            throw new IOException("Error connecting to data source");
         }
      case 3:
         try {
            switch(this.gC) {
            case 1:
               var1 = new JavaProducerSource(this.protocol + "://" + this.gA, var2);
               return var1;
            case 2:
               var1 = new JavaProducerSource(this.protocol + "://" + this.gA, false);
               if (var3 == null || var3.equals("preview") && nIsPreviewAllowed()) {
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
            case 10:
               (var1 = new JavaProducerSource(this.eM)).useActiveSource = var2;
               return var1;
            }
         } catch (SecurityException var6) {
            throw new SecurityException("Not enough privileges");
         } catch (Exception var7) {
            throw new IOException("Error connecting to data source");
         }
      default:
         (var1 = new JavaProducerSource(this.eM)).useActiveSource = var2;
      }

      return var1;
   }

   public String getParamValueAsString(String var1) {
      String var2 = null;

      for(int var3 = this.gB.indexOf(var1); var3 != -1; var3 = this.gB.indexOf(var1, var3 + 1)) {
         if (var3 % 2 == 0) {
            var2 = this.gB.elementAt(var3 + 1).toString();
            break;
         }
      }

      return var2;
   }

   public int getParamValueAsInt(String var1, int var2, int var3, int var4) throws MediaException {
      var4 = var4;
      String var7;
      if ((var7 = this.getParamValueAsString(var1)) != null) {
         boolean var5 = false;

         try {
            if ((var4 = Integer.parseInt(var7)) < var2 || var4 > var3) {
               var5 = true;
            }
         } catch (Exception var6) {
            var5 = true;
         }

         if (var5) {
            throw new MediaException("Invalid parameter bounds: " + var1 + " = " + var7);
         }
      }

      return var4;
   }

   public boolean getParamValueAsBoolean(String var1) throws MediaException {
      boolean var2 = false;
      String var3;
      if ((var3 = this.getParamValueAsString(var1)) != null) {
         if (var3.equals("true")) {
            var2 = true;
         } else {
            if (!var3.equals("false")) {
               throw new MediaException("Boolean parameter expected: " + var3);
            }

            var2 = false;
         }
      }

      return var2;
   }

   public boolean isMidiDeviceLocator() {
      return this.gC == 4;
   }

   public boolean isToneDeviceLocator() {
      return this.gC == 3;
   }

   public byte getLocatorType() {
      return this.gC;
   }

   public String getBasicLocator() {
      return this.gD == 3 ? this.protocol + "://" + this.gA : null;
   }

   public byte getCategory() {
      return this.bR;
   }

   private static native boolean nMimeTypeSupportsActiveSource(String var0);

   private static native boolean nIsJADStreamingFlagSet();

   private static native boolean nIsPreviewAllowed();

   private static class SourceStreamAsInputStream extends InputStream {
      private DataSource gE;
      private SourceStream gT;

      SourceStreamAsInputStream(DataSource var1) throws IOException {
         this.gE = var1;
         this.gT = var1.getStreams()[0];
         var1.connect();
         var1.start();
      }

      public int read() throws IOException {
         throw new IOException("read not supported");
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         return this.gT.read(var1, var2, var3);
      }

      public void close() {
         this.gE.disconnect();
      }
   }
}
