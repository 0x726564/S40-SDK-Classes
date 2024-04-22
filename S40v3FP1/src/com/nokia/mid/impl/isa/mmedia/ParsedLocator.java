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
   public String contentType = null;
   private String schemePart = null;
   private Vector params = new Vector();
   private byte locatorType;
   private final byte createdBy;
   private DataSource source = null;
   private SourceStream sourceStream = null;
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
      String var7 = null;
      boolean var8 = true;
      this.createdBy = 3;
      int var4;
      if ((var4 = var1.indexOf(58)) != -1) {
         var7 = var1.substring(0, var4).toLowerCase();
      } else {
         var8 = false;
      }

      if (var8) {
         if (var1.startsWith("://", var4)) {
            var4 += "://".length();
         } else {
            var8 = false;
         }
      }

      if (var8) {
         String[] var9 = new String[1];
         if ((var3 = var1.indexOf(63, var4)) != -1) {
            this.schemePart = var1.substring(var4, var3);
         } else {
            this.schemePart = var1.substring(var4);
         }

         this.locatorType = MediaPrefs.nGetLocatorTypeAndContentType(var7, this.schemePart.toLowerCase(), var9);
         this.contentType = var9[0];
         if (this.locatorType == 0 || !MediaPrefs.nIsContentSupported(var7, this.contentType)) {
            var8 = false;
         }
      }

      while(var8 && var3 != -1) {
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
               var8 = false;
            }
         } else {
            var8 = false;
         }
      }

      if (!var8) {
         throw new MediaException("Invalid locator: " + var1);
      }
   }

   public JavaProducerSource connect() throws IOException {
      JavaProducerSource var1 = null;
      switch(this.createdBy) {
      case 1:
         try {
            var1 = new JavaProducerSource(new ParsedLocator.SourceStreamAsInputStream(this.source));
            break;
         } catch (Exception var4) {
            throw new IOException("Error connecting to data source");
         }
      case 3:
         try {
            if (this.locatorType == 1) {
               var1 = new JavaProducerSource("http://" + this.schemePart);
            } else if (this.locatorType == 2) {
               var1 = new JavaProducerSource("file://" + this.schemePart);
            } else if (this.locatorType == 3) {
               var1 = new JavaProducerSource();
            }
            break;
         } catch (Exception var3) {
            throw new IOException("Error connecting to data source");
         }
      default:
         var1 = new JavaProducerSource(this.inputStream);
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

   public int getLocatorType() {
      return this.locatorType;
   }

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
