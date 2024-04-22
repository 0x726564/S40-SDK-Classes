package com.nokia.mid.impl.isa.amms.audio;

import javax.microedition.amms.control.audioeffect.EqualizerControl;
import javax.microedition.media.MediaException;

public class EqualizerControlImpl implements EqualizerControl {
   private final int MILLIBELS_PER_DECIBEL = 100;
   private boolean enabled = false;
   private int EQ_ID = -1;
   private String[] preset_names = null;
   private static int[] bandLevels = null;
   private static final int INVALID_BAND_LEVEL = Integer.MAX_VALUE;
   private int currentPreset = -1;
   private Object mutex = new Object();
   boolean enforced = false;

   private synchronized void CreateEqualizer() {
      if (this.EQ_ID == -1) {
         this.nSwitchStateReq(false);
         this.nDeleteEqualizer();
         this.EQ_ID = this.nCreateEqualizer();
         if (this.enabled) {
            this.nSwitchStateReq(true);
         }
      }

   }

   public void setEnabled(boolean flag) {
      synchronized(this.mutex) {
         if (this.enabled != flag) {
            this.CreateEqualizer();
            if (this.nSwitchStateReq(flag)) {
               this.enabled = flag;
            }

         }
      }
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setPreset(String s) {
      if (s == null) {
         throw new IllegalArgumentException("The given preset is null.");
      } else {
         synchronized(this.mutex) {
            if (this.preset_names == null) {
               this.getPresetNames();
            }

            boolean found = false;

            for(int i = 0; i < this.preset_names.length; ++i) {
               if (this.preset_names[i].equals(s)) {
                  this.nSetPreset(i);
                  this.currentPreset = i;
                  found = true;

                  for(int b = 0; b < 8; ++b) {
                     bandLevels[b] = Integer.MAX_VALUE;
                  }
               }
            }

            if (!found) {
               throw new IllegalArgumentException("The given preset is not available.");
            }
         }
      }
   }

   public String getPreset() {
      if (this.currentPreset == -1) {
         return null;
      } else {
         synchronized(this.mutex) {
            return this.getPresetNames()[this.currentPreset];
         }
      }
   }

   public String[] getPresetNames() {
      if (this.preset_names != null) {
         return this.preset_names;
      } else {
         synchronized(this.mutex) {
            this.preset_names = this.nGetPresetNames();
            return this.preset_names;
         }
      }
   }

   public int getMinBandLevel() {
      this.CreateEqualizer();
      int decibels = this.nGetMinGain();
      return decibels * 100;
   }

   public int getMaxBandLevel() {
      this.CreateEqualizer();
      int decibels = this.nGetMaxGain();
      return decibels * 100;
   }

   public void setBandLevel(int level, int band) throws IllegalArgumentException {
      this.CreateEqualizer();
      if (band >= 0 && band < this.getNumberOfBands()) {
         if (level >= this.getMinBandLevel() && level <= this.getMaxBandLevel()) {
            synchronized(this.mutex) {
               bandLevels[band] = level;
               int decibel_level = level / 100;
               this.nSetBandGain(decibel_level, band);
               this.currentPreset = -1;
            }
         } else {
            throw new IllegalArgumentException("invalid level");
         }
      } else {
         throw new IllegalArgumentException("invalid band");
      }
   }

   public int getBandLevel(int band) throws IllegalArgumentException {
      this.CreateEqualizer();
      if (band >= 0 && band < this.getNumberOfBands()) {
         synchronized(this.mutex) {
            if (bandLevels[band] != Integer.MAX_VALUE) {
               return bandLevels[band];
            } else {
               int decibel = this.nGetBandGain(band);
               return decibel * 100;
            }
         }
      } else {
         throw new IllegalArgumentException("invalid band");
      }
   }

   public int getNumberOfBands() {
      this.CreateEqualizer();
      return this.nGetNumberOfBands();
   }

   public int getCenterFreq(int band) throws IllegalArgumentException {
      this.CreateEqualizer();
      synchronized(this.mutex) {
         int[] center_frequencies = this.nGetCenterFreqs();
         if (center_frequencies == null) {
            throw new OutOfMemoryError();
         } else if (band >= 0 && band < this.getNumberOfBands()) {
            return center_frequencies[band];
         } else {
            throw new IllegalArgumentException("band out of range.");
         }
      }
   }

   public int getBand(int frequency) {
      this.CreateEqualizer();
      synchronized(this.mutex) {
         int[] center_frequencies = this.nGetCenterFreqs();
         if (center_frequencies == null) {
            throw new OutOfMemoryError();
         } else if (frequency < 0) {
            return -1;
         } else {
            int band = 0;
            int lowest_diff = Math.abs(center_frequencies[0] - frequency);

            for(int i = 1; i < center_frequencies.length; ++i) {
               int diff = Math.abs(center_frequencies[i] - frequency);
               if (diff < lowest_diff) {
                  band = i;
                  lowest_diff = diff;
               }
            }

            return band;
         }
      }
   }

   private int calculateGain(int min, int max, int scale_value) {
      if (scale_value <= 100 && scale_value >= 0) {
         int gain = 0;
         if (scale_value > 50) {
            gain = max * (scale_value - 50) / 50;
         } else if (scale_value < 50) {
            gain = Math.abs(min) * scale_value / 50;
            gain += min;
         }

         return gain;
      } else {
         throw new IllegalArgumentException("level out of range.");
      }
   }

   private int setBand(int band, int level) throws IllegalArgumentException {
      int maxGain = this.getMaxBandLevel();
      int minGain = this.getMinBandLevel();
      int actualGain = this.calculateGain(minGain, maxGain, level);
      this.setBandLevel(actualGain, band);
      return level;
   }

   public int setBass(int level) throws IllegalArgumentException {
      return this.setBand(0, level);
   }

   public int setTreble(int level) throws IllegalArgumentException {
      int trebleBand = this.getNumberOfBands() - 1;
      return this.setBand(trebleBand, level);
   }

   public int getBass() {
      int level = this.getBandLevel(0);
      int maxGain = this.getMaxBandLevel();
      int minGain = this.getMinBandLevel();
      int gainRange = maxGain - minGain;
      int result = (level - minGain) * 100 / gainRange;
      return result;
   }

   public int getTreble() {
      int level = this.getBandLevel(this.getNumberOfBands() - 1);
      int maxGain = this.getMaxBandLevel();
      int minGain = this.getMinBandLevel();
      int gainRange = maxGain - minGain;
      int result = (level - minGain) * 100 / gainRange;
      return result;
   }

   public boolean isEnforced() {
      return this.enforced;
   }

   public void setEnforced(boolean flag) {
      this.enforced = flag;
   }

   public int getScope() {
      return 1;
   }

   public void setScope(int scope) throws MediaException {
      if (scope != 1) {
         throw new MediaException("the given scope is not supported.");
      }
   }

   static native void nInit();

   native int nCreateEqualizer();

   native void nDeleteEqualizer();

   native boolean nSwitchStateReq(boolean var1);

   native void nSetBandGain(int var1, int var2);

   native int nGetBandGain(int var1);

   native int nGetMinGain();

   native int nGetMaxGain();

   native int nGetNumberOfBands();

   native int[] nGetCenterFreqs();

   native String[] nGetPresetNames();

   native void nSetPreset(int var1);

   static {
      nInit();
      bandLevels = new int[8];

      for(int i = 0; i < 8; ++i) {
         bandLevels[i] = Integer.MAX_VALUE;
      }

   }
}
