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
   private static final byte RESOURCE = 10;
   public static final byte NORMAL = 0;
   public static final byte ALERT = 1;
   public static final byte MUSIC = 2;
   private static final String CATEGORY_NORMAL = "normal";
   private static final String CATEGORY_ALERT = "alert";
   private static final String CATEGORY_MUSIC = "music";
   public String contentType = null;
   public boolean previewMode = false;
   private String schemePart = null;
   private Vector params = new Vector();
   private String protocol = null;
   private byte locatorType;
   private final byte createdBy;
   private DataSource source = null;
   private InputStream inputStream = null;
   private byte category = 0;
   private boolean streamingRequested = false;

   public ParsedLocator(DataSource source) throws MediaException {
      this.contentType = source.getContentType();
      if (!MediaPrefs.nIsDataContentSupported(this.contentType)) {
         throw new MediaException("Invalid content type: " + this.contentType);
      } else {
         this.createdBy = 1;
         this.source = source;
         this.streamingRequested = nIsJADStreamingFlagSet();
      }
   }

   public ParsedLocator(InputStream stream, String type) throws MediaException {
      this.contentType = type;
      if (!MediaPrefs.nIsDataContentSupported(this.contentType)) {
         throw new MediaException("Invalid content type: " + this.contentType);
      } else {
         this.createdBy = 2;
         this.inputStream = stream;
         this.streamingRequested = nIsJADStreamingFlagSet();
      }
   }

   public ParsedLocator(String locator) throws MediaException {
      int iParam = 0;
      boolean valid = true;
      this.createdBy = 3;
      int iProtocol;
      if ((iProtocol = locator.indexOf(58)) != -1) {
         this.protocol = locator.substring(0, iProtocol).toLowerCase();
      } else {
         valid = false;
      }

      if (valid) {
         if (locator.startsWith("://", iProtocol)) {
            iProtocol += "://".length();
         } else {
            valid = false;
         }
      }

      if (valid) {
         String[] cType = new String[1];
         if ((iParam = locator.indexOf(63, iProtocol)) != -1) {
            this.schemePart = locator.substring(iProtocol, iParam);
         } else {
            this.schemePart = locator.substring(iProtocol);
         }

         this.locatorType = MediaPrefs.nGetLocatorTypeAndContentType(this.protocol, this.schemePart.toLowerCase(), cType);
         this.contentType = cType[0];
         if (this.locatorType == 0 || !MediaPrefs.nIsContentSupported(this.protocol, this.contentType)) {
            valid = false;
         }

         if (this.locatorType == 2) {
            MediaPrefs.checkPermission(5, 0);
         }

         if (this.locatorType == 10 && (this.inputStream = this.getClass().getResourceAsStream(this.schemePart)) == null) {
            throw new MediaException("Resource not in JAR");
         }
      }

      while(valid && iParam != -1) {
         ++iParam;
         int iTmp;
         if ((iTmp = locator.indexOf(61, iParam)) != -1) {
            String param1Str = locator.substring(iParam, iTmp);
            ++iTmp;
            String param2Str;
            if ((iParam = locator.indexOf(38, iTmp)) != -1) {
               param2Str = locator.substring(iTmp, iParam);
            } else {
               param2Str = locator.substring(iTmp);
            }

            if (!param1Str.equals("") && !param2Str.equals("") && param1Str.indexOf("&") < 0) {
               this.params.addElement(param1Str);
               this.params.addElement(param2Str);
            } else {
               valid = false;
            }
         } else {
            valid = false;
         }
      }

      if (valid) {
         this.parseCommonParameters();
      } else {
         throw new MediaException("Invalid locator: " + locator);
      }
   }

   public JavaProducerSource connect() throws IOException, SecurityException {
      JavaProducerSource jps = null;
      boolean useActiveSource = false;
      String drmKey = null;
      drmKey = this.getParamValueAsString("drm");
      if (drmKey != null) {
         if (!drmKey.equals("preview")) {
            throw new SecurityException("DRM key must be 'preview'.");
         }

         this.previewMode = true;
         if (this.createdBy == 3 && this.locatorType != 2) {
            throw new SecurityException("Preview operation not valid.");
         }
      }

      if (nMimeTypeSupportsActiveSource(this.contentType) && this.streamingRequested) {
         useActiveSource = true;
      }

      switch(this.createdBy) {
      case 1:
         try {
            jps = new JavaProducerSource(new ParsedLocator.SourceStreamAsInputStream(this.source));
            jps.useActiveSource = useActiveSource;
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
               jps = new JavaProducerSource(this.protocol + "://" + this.schemePart, useActiveSource);
               return jps;
            case 2:
               jps = new JavaProducerSource(this.protocol + "://" + this.schemePart, false);
               if (drmKey == null || drmKey.equals("preview") && nIsPreviewAllowed()) {
                  return jps;
               }

               throw new SecurityException("Preview operation not valid.");
            case 3:
            case 9:
               jps = new JavaProducerSource();
               return jps;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            default:
               return jps;
            case 10:
               jps = new JavaProducerSource(this.inputStream);
               jps.useActiveSource = useActiveSource;
               return jps;
            }
         } catch (SecurityException var7) {
            throw new SecurityException("Not enough privileges");
         } catch (Exception var8) {
            throw new IOException("Error connecting to data source");
         }
      default:
         jps = new JavaProducerSource(this.inputStream);
         jps.useActiveSource = useActiveSource;
      }

      return jps;
   }

   public String getParamValueAsString(String param) {
      String res = null;

      for(int index = this.params.indexOf(param); index != -1; index = this.params.indexOf(param, index + 1)) {
         if (index % 2 == 0) {
            res = this.params.elementAt(index + 1).toString();
            break;
         }
      }

      return res;
   }

   public int getParamValueAsInt(String param, int min, int max, int def) throws MediaException {
      int res = def;
      String valueStr;
      if ((valueStr = this.getParamValueAsString(param)) != null) {
         boolean error = false;

         try {
            res = Integer.parseInt(valueStr);
            if (res < min || res > max) {
               error = true;
            }
         } catch (Exception var9) {
            error = true;
         }

         if (error) {
            throw new MediaException("Invalid parameter bounds: " + param + " = " + valueStr);
         }
      }

      return res;
   }

   public boolean getParamValueAsBoolean(String param) throws MediaException {
      boolean value = false;
      String valueStr;
      if ((valueStr = this.getParamValueAsString(param)) != null) {
         if (valueStr.equals("true")) {
            value = true;
         } else {
            if (!valueStr.equals("false")) {
               throw new MediaException("Boolean parameter expected: " + valueStr);
            }

            value = false;
         }
      }

      return value;
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

   public byte getCategory() {
      return this.category;
   }

   private void parseCommonParameters() throws MediaException {
      String value;
      if ((value = this.getParamValueAsString("category")) != null) {
         if (value.equals("music")) {
            this.category = 2;
         } else if (value.equals("alert")) {
            this.category = 1;
         } else {
            if (!value.equals("normal")) {
               throw new MediaException("Invalid category: " + value);
            }

            this.category = 0;
         }
      }

      if (this.getParamValueAsString("streamable") != null) {
         this.streamingRequested = this.getParamValueAsBoolean("streamable");
      } else {
         this.streamingRequested = nIsJADStreamingFlagSet();
      }

   }

   private static native boolean nMimeTypeSupportsActiveSource(String var0);

   private static native boolean nIsJADStreamingFlagSet();

   private static native boolean nIsPreviewAllowed();

   private static class SourceStreamAsInputStream extends InputStream {
      private DataSource source;
      private SourceStream stream;

      SourceStreamAsInputStream(DataSource source) throws IOException {
         this.source = source;
         this.stream = source.getStreams()[0];
         source.connect();
         source.start();
      }

      public int read() throws IOException {
         throw new IOException("read not supported");
      }

      public int read(byte[] b, int off, int len) throws IOException {
         return this.stream.read(b, off, len);
      }

      public void close() {
         this.source.disconnect();
      }
   }
}
