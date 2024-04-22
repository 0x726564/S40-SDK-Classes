package com.nokia.mid.impl.isa.amms.audio;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.amms.control.audioeffect.ReverbControl;
import javax.microedition.media.MediaException;

public class ReverbControlImpl implements ReverbControl {
   private Hashtable presetsTable = new Hashtable();
   private boolean isEnabled;
   private boolean isEnforced = true;
   private int presetValue;
   private int reverbDelay = Integer.MAX_VALUE;
   private int reverbLevel = Integer.MAX_VALUE;
   private String presetName;

   public ReverbControlImpl() {
      String[] presets = nGetPresets();

      for(int i = 0; i < presets.length; ++i) {
         String encodedPreset = presets[i];
         int sepIdx = encodedPreset.indexOf(59);
         String name = encodedPreset.substring(0, sepIdx);
         Integer val = null;
         String valString = encodedPreset.substring(sepIdx + 1);
         if (valString.startsWith("0x")) {
            valString = valString.substring(2);
         }

         val = Integer.valueOf(valString, 16);
         this.presetsTable.put(name, val);
      }

      this.presetName = "smallroom";
      this.presetValue = (Integer)this.presetsTable.get(this.presetName);
   }

   public int getReverbLevel() {
      return this.reverbLevel == Integer.MAX_VALUE ? 0 : this.reverbLevel;
   }

   public int getReverbTime() throws MediaException {
      throw new MediaException("Not supported.");
   }

   public synchronized int setReverbLevel(int i) {
      if (i > 0) {
         throw new IllegalArgumentException("Reverb level must be negative");
      } else {
         this.reverbLevel = i;
         this.updateParameters();
         return this.reverbLevel;
      }
   }

   public synchronized void setReverbTime(int i) throws MediaException {
      throw new MediaException("Not supported.");
   }

   public String getPreset() {
      return this.presetName;
   }

   public String[] getPresetNames() {
      String[] p = new String[this.presetsTable.size()];
      Enumeration e = this.presetsTable.keys();

      for(int var3 = 0; e.hasMoreElements(); p[var3++] = (String)e.nextElement()) {
      }

      return p;
   }

   public int getScope() {
      return 1;
   }

   public boolean isEnabled() {
      return this.isEnabled;
   }

   public boolean isEnforced() {
      return this.isEnforced;
   }

   public synchronized void setEnabled(boolean flag) {
      this.isEnabled = flag;
      if (flag) {
         nActivate();
         this.updateParameters();
      } else {
         nDeactivate();
      }

   }

   public void setEnforced(boolean flag) {
      this.isEnforced = flag;
   }

   public synchronized void setPreset(String s) {
      if (s == null) {
         throw new IllegalArgumentException("Cannot be null");
      } else {
         Integer val = (Integer)this.presetsTable.get(s);
         if (val != null) {
            this.presetValue = val;
            this.presetName = s;
            this.updateParameters();
         } else {
            throw new IllegalArgumentException("Preset not available");
         }
      }
   }

   public void setScope(int i) throws MediaException {
      if (i != 1) {
         throw new MediaException("Scope not supported.");
      }
   }

   private void updateParameters() {
      if (this.isEnabled) {
         nApplyChanges(this.reverbLevel, this.reverbDelay, this.presetValue);
      }

   }

   private static final native String[] nGetPresets();

   private static final native void nApplyChanges(int var0, int var1, int var2);

   private static final native void nActivate();

   private static final native void nDeactivate();
}
