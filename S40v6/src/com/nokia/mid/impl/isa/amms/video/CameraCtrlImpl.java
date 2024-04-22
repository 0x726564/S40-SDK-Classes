package com.nokia.mid.impl.isa.amms.video;

import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.amms.control.camera.CameraControl;
import javax.microedition.media.MediaException;

public class CameraCtrlImpl extends Switchable implements CameraControl {
   private static final String EXPOSURE_AUTO = "auto";
   private int[] stillResolutions = nGetStillResolutions();
   private int resolutionIdx;

   public CameraCtrlImpl() {
      int idxLastW = this.stillResolutions.length - 2;
      int idxLastH = this.stillResolutions.length - 1;
      if (this.stillResolutions[0] * this.stillResolutions[1] > this.stillResolutions[idxLastW] * this.stillResolutions[idxLastH]) {
         int[] tmp = new int[this.stillResolutions.length];
         int i = 0;

         for(int j = this.stillResolutions.length - 2; i < this.stillResolutions.length; i += 2) {
            tmp[i] = this.stillResolutions[j];
            tmp[i + 1] = this.stillResolutions[j + 1];
            j -= 2;
         }

         this.stillResolutions = tmp;
         this.resolutionIdx = 2;
      }

   }

   public int getCameraRotation() {
      return -1004;
   }

   public void enableShutterFeedback(boolean enable) throws MediaException {
      throw new MediaException("Not supported");
   }

   public boolean isShutterFeedbackEnabled() {
      return true;
   }

   public String[] getSupportedExposureModes() {
      return new String[]{"auto"};
   }

   public void setExposureMode(String arg0) {
      if (arg0 != null && !arg0.equals("auto")) {
         throw new IllegalArgumentException("Not supported");
      }
   }

   public String getExposureMode() {
      return "auto";
   }

   public int[] getSupportedVideoResolutions() {
      return new int[0];
   }

   public int[] getSupportedStillResolutions() {
      int[] ret = new int[this.stillResolutions.length];
      System.arraycopy(this.stillResolutions, 0, ret, 0, ret.length);
      return ret;
   }

   public void setVideoResolution(int arg0) {
      throw new IllegalArgumentException("Not supported");
   }

   public void setStillResolution(int idx) {
      int actualIdx = idx * 2;
      if (this.stillResolutions.length > actualIdx && idx >= 0 && this.stillResolutions.length > idx) {
         this.resolutionIdx = actualIdx;
      } else {
         throw new IllegalArgumentException("Not supported");
      }
   }

   public int getVideoResolution() {
      return -1;
   }

   public int getStillResolution() {
      return this.resolutionIdx / 2;
   }

   private static native int[] nGetStillResolutions();
}
