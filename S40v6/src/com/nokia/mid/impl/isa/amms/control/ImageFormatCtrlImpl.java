package com.nokia.mid.impl.isa.amms.control;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.amms.control.ImageFormatControl;
import javax.microedition.amms.control.camera.CameraControl;
import javax.microedition.media.MediaException;

public class ImageFormatCtrlImpl extends Switchable implements ImageFormatControl {
   public static final byte PROCESSOR = 0;
   public static final byte CAMERA = 1;
   public static final byte NUM_TYPES = 2;
   public static final byte BIT_RATE = 0;
   public static final byte BIT_RATE_TYPE = 1;
   public static final byte FRAME_RATE = 2;
   public static final byte QUALITY = 3;
   public static final byte SAMPLE_RATE = 4;
   public static final byte VERSION_TYPE = 5;
   public static final byte NUM_PARAMS = 6;
   private static final String[] paramIdToString = new String[]{"bitrate", "bitrate type", "frame rate", "quality", "sample rate", "version type"};
   private static WeakReference[] stringCache = new WeakReference[6];
   private static final String UNSUPPORTED = "Unsupported";
   private static final String NULL_PARAM = "Parameter null";
   private static final String INVALID_PARAM = "Parameter invalid";
   private boolean metaDataOverride = true;
   private String outputFormat;
   private int type;
   private int[] paramValues = new int[6];
   private BasicPlayer player;

   public ImageFormatCtrlImpl(BasicPlayer p, int type, String format) {
      this.player = p;
      this.type = type;
      this.setFormat(format);
   }

   public int getEstimatedImageSize() {
      if (this.type == 1) {
         CameraControl cc = (CameraControl)this.player.getControl("javax.microedition.amms.control.camera.CameraControl");
         if (cc != null) {
            int[] res = cc.getSupportedStillResolutions();
            int width = res[cc.getStillResolution() * 2];
            int height = res[cc.getStillResolution() * 2 + 1];
            return width * height * 12 / 100;
         }
      }

      return 0;
   }

   public String[] getSupportedFormats() {
      return nGetOutputFormats(this.type);
   }

   public String[] getSupportedStrParameters() {
      int[] strParams = nGetSupportedStrParameters(this.type, this.outputFormat);
      if (strParams == null) {
         return new String[0];
      } else {
         String[] params = new String[strParams.length];

         for(int i = 0; i < strParams.length; ++i) {
            params[i] = paramIdToString[strParams[i]];
         }

         return params;
      }
   }

   public String[] getSupportedIntParameters() {
      int[] intParams;
      if ((intParams = nGetSupportedIntParameters(this.type, this.outputFormat)) == null) {
         return new String[0];
      } else {
         String[] strParams = new String[intParams.length];

         for(int i = 0; i < intParams.length; ++i) {
            strParams[i] = paramIdToString[intParams[i]];
         }

         return strParams;
      }
   }

   public int[] getSupportedIntParameterRange(String s) {
      int id = this.getIntParameterId(s);
      return nGetParameterRange(this.type, id, this.outputFormat);
   }

   public String[] getSupportedStrParameterValues(String s) {
      int id = this.getStrParameterId(s);
      int[] paramValues = nGetStrParameterValues(this.type, id, this.outputFormat);
      String[] paramValuesStrings = new String[paramValues.length];
      Hashtable ht = stringCache[id] == null ? null : (Hashtable)stringCache[id].get();
      if (ht == null) {
         synchronized(stringCache) {
            ht = new Hashtable();

            for(int i = 0; i < paramValues.length; ++i) {
               paramValuesStrings[i] = nGetStrParameterValueAsString(this.type, id, this.outputFormat, paramValues[i]);
               ht.put(paramValuesStrings[i], new Integer(paramValues[i]));
            }

            stringCache[id] = new WeakReference(ht);
         }
      } else {
         int i = 0;

         for(Enumeration allKeys = ht.keys(); allKeys.hasMoreElements(); ++i) {
            paramValuesStrings[i] = (String)allKeys.nextElement();
         }
      }

      return paramValuesStrings;
   }

   public void setFormat(String s) {
      if (s == null) {
         throw new IllegalArgumentException("Parameter null");
      } else if (!nIsOutputFormatSupported(this.type, s)) {
         throw new IllegalArgumentException("Unsupported");
      } else {
         this.outputFormat = s;
         this.paramValues[3] = 70;
         this.setParameter("version type", "JPEG");
      }
   }

   public String getFormat() {
      return this.outputFormat;
   }

   public int setParameter(String s, int i) {
      int id = this.getIntParameterId(s);
      int[] minMax = nGetParameterRange(this.type, id, this.outputFormat);
      if (i >= minMax[0] && i <= minMax[1]) {
         this.paramValues[id] = i;
         return i;
      } else {
         throw new IllegalArgumentException("Parameter invalid");
      }
   }

   public void setParameter(String s, String s1) {
      this.getSupportedStrParameterValues(s);
      int id = this.getStrParameterId(s);
      Hashtable ht = stringCache[id] == null ? null : (Hashtable)stringCache[id].get();
      if (ht == null) {
         this.getSupportedStrParameterValues(s);
         ht = (Hashtable)stringCache[id].get();
      }

      this.paramValues[id] = (Integer)ht.get(s1);
   }

   public String getStrParameterValue(String s) {
      int id = this.getStrParameterId(s);
      return nGetStrParameterValueAsString(this.type, id, this.outputFormat, this.paramValues[this.getStrParameterId(s)]);
   }

   public int getIntParameterValue(String s) {
      return this.paramValues[this.getIntParameterId(s)];
   }

   public int getEstimatedBitRate() throws MediaException {
      throw new MediaException("Unsupported");
   }

   public void setMetadata(String s, String s1) throws MediaException {
      throw new MediaException("Unsupported");
   }

   public String[] getSupportedMetadataKeys() {
      return new String[0];
   }

   public int getMetadataSupportMode() {
      return 0;
   }

   public void setMetadataOverride(boolean setting) {
      this.metaDataOverride = setting;
   }

   public boolean getMetadataOverride() {
      return this.metaDataOverride;
   }

   private int getIntParameterId(String s) {
      int id = this.getCheckParameterId(s);
      if (!nIsIntParamSupported(this.type, id, this.outputFormat)) {
         throw new IllegalArgumentException("Unsupported");
      } else {
         return id;
      }
   }

   private int getStrParameterId(String s) {
      int id = this.getCheckParameterId(s);
      if (!nIsStrParamSupported(this.type, id, this.outputFormat)) {
         throw new IllegalArgumentException("Unsupported");
      } else {
         return id;
      }
   }

   private int getCheckParameterId(String s) {
      if (s == null) {
         throw new IllegalArgumentException("Parameter null");
      } else {
         int id;
         for(id = 0; id < 6 && !paramIdToString[id].equals(s); ++id) {
         }

         if (id == 6) {
            throw new IllegalArgumentException("Unsupported");
         } else {
            return id;
         }
      }
   }

   private static native int nGetEstimatedImageSize(String var0, int var1, int var2, int var3);

   private static native String[] nGetOutputFormats(int var0);

   private static native boolean nIsOutputFormatSupported(int var0, String var1);

   private static native int[] nGetSupportedIntParameters(int var0, String var1);

   private static native boolean nIsIntParamSupported(int var0, int var1, String var2);

   private static native boolean nIsStrParamSupported(int var0, int var1, String var2);

   private static native int[] nGetParameterRange(int var0, int var1, String var2);

   private static native int[] nGetSupportedStrParameters(int var0, String var1);

   private static native int[] nGetStrParameterValues(int var0, int var1, String var2);

   private static native String nGetStrParameterValueAsString(int var0, int var1, String var2, int var3);
}
