package com.nokia.mid.impl.isa.amms.audio;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.amms.control.audioeffect.ReverbControl;
import javax.microedition.media.MediaException;

public class ReverbControlImpl implements ReverbControl {
   private Hashtable lR = new Hashtable();
   private boolean lS;
   private boolean lT = true;
   private int lU;
   private int lV = Integer.MAX_VALUE;
   private int lW = Integer.MAX_VALUE;
   private String lX;

   public ReverbControlImpl() {
      String[] var1 = nGetPresets();

      for(int var2 = 0; var2 < var1.length; ++var2) {
         String var3;
         int var4 = (var3 = var1[var2]).indexOf(59);
         String var5 = var3.substring(0, var4);
         Integer var6 = null;
         if ((var3 = var3.substring(var4 + 1)).startsWith("0x")) {
            var3 = var3.substring(2);
         }

         var6 = Integer.valueOf(var3, 16);
         this.lR.put(var5, var6);
      }

      this.lX = "smallroom";
      this.lU = (Integer)this.lR.get(this.lX);
   }

   public int getReverbLevel() {
      return this.lW == Integer.MAX_VALUE ? 0 : this.lW;
   }

   public int getReverbTime() throws MediaException {
      throw new MediaException("Not supported.");
   }

   public synchronized int setReverbLevel(int var1) {
      if (var1 > 0) {
         throw new IllegalArgumentException("Reverb level must be negative");
      } else {
         this.lW = var1;
         this.ag();
         return this.lW;
      }
   }

   public synchronized void setReverbTime(int var1) throws MediaException {
      throw new MediaException("Not supported.");
   }

   public String getPreset() {
      return this.lX;
   }

   public String[] getPresetNames() {
      String[] var1 = new String[this.lR.size()];
      Enumeration var3 = this.lR.keys();

      for(int var2 = 0; var3.hasMoreElements(); var1[var2++] = (String)var3.nextElement()) {
      }

      return var1;
   }

   public int getScope() {
      return 1;
   }

   public boolean isEnabled() {
      return this.lS;
   }

   public boolean isEnforced() {
      return this.lT;
   }

   public synchronized void setEnabled(boolean var1) {
      this.lS = var1;
      if (var1) {
         nActivate();
         this.ag();
      } else {
         nDeactivate();
      }
   }

   public void setEnforced(boolean var1) {
      this.lT = var1;
   }

   public synchronized void setPreset(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Cannot be null");
      } else {
         Integer var2;
         if ((var2 = (Integer)this.lR.get(var1)) != null) {
            this.lU = var2;
            this.lX = var1;
            this.ag();
         } else {
            throw new IllegalArgumentException("Preset not available");
         }
      }
   }

   public void setScope(int var1) throws MediaException {
      if (var1 != 1) {
         throw new MediaException("Scope not supported.");
      }
   }

   private void ag() {
      if (this.lS) {
         nApplyChanges(this.lW, this.lV, this.lU);
      }

   }

   private static final native String[] nGetPresets();

   private static final native void nApplyChanges(int var0, int var1, int var2);

   private static final native void nActivate();

   private static final native void nDeactivate();
}
