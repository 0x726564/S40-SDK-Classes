package com.nokia.mid.impl.isa.amms;

import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import javax.microedition.media.control.VolumeControl;

public class GlobalVolumeCtrlImpl implements VolumeControl {
   public void setMute(boolean var1) {
      nSetMute(var1);
   }

   public boolean isMuted() {
      return nIsMuted();
   }

   public int setLevel(int var1) {
      return nSetLevel(MediaPrefs.boundInt(var1, 0, 100));
   }

   public int getLevel() {
      return nGetLevel();
   }

   private static native void nSetMute(boolean var0);

   private static native boolean nIsMuted();

   private static native int nSetLevel(int var0);

   private static native int nGetLevel();
}
