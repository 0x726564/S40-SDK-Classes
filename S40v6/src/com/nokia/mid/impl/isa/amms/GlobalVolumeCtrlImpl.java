package com.nokia.mid.impl.isa.amms;

import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import javax.microedition.media.control.VolumeControl;

public class GlobalVolumeCtrlImpl implements VolumeControl {
   public void setMute(boolean mute) {
      nSetMute(mute);
   }

   public boolean isMuted() {
      return nIsMuted();
   }

   public int setLevel(int newVol) {
      newVol = MediaPrefs.boundInt(newVol, 0, 100);
      return nSetLevel(newVol);
   }

   public int getLevel() {
      return nGetLevel();
   }

   private static native void nSetMute(boolean var0);

   private static native boolean nIsMuted();

   private static native int nSetLevel(int var0);

   private static native int nGetLevel();
}
