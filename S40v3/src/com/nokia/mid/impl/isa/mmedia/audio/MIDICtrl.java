package com.nokia.mid.impl.isa.mmedia.audio;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.MIDIControl;

class MIDICtrl extends Switchable implements MIDIControl {
   private AudioOutImpl audioOut;

   MIDICtrl(BasicPlayer var1, AudioOutImpl var2) {
      this.player = var1;
      this.audioOut = var2;
   }

   private void checkChannel(int var1) {
      if ((var1 & -16) != 0) {
         throw new IllegalArgumentException("out of bounds");
      }
   }

   private void check7bit(int var1) {
      if ((var1 & -128) != 0) {
         throw new IllegalArgumentException("out of bounds");
      }
   }

   private void checkState() {
      if (!this.player.isActive()) {
         throw new IllegalStateException("player not prefetched");
      }
   }

   public boolean isBankQuerySupported() {
      return false;
   }

   public int[] getProgram(int var1) throws MediaException {
      throw new MediaException("not implemented");
   }

   public int getChannelVolume(int var1) {
      synchronized(this.player) {
         this.checkState();
         this.checkChannel(var1);

         int var10000;
         try {
            var10000 = this.audioOut.getChannelVolume(var1);
         } catch (MediaException var5) {
            return -1;
         }

         return var10000;
      }
   }

   public void setProgram(int var1, int var2, int var3) {
      this.checkChannel(var1);
      this.check7bit(var3);
      if (var2 == -1) {
         var2 = 0;
      }

      if ((var2 & -16384) != 0) {
         throw new IllegalArgumentException("out of bounds");
      } else {
         synchronized(this.player) {
            try {
               this.shortMidiEvent(176 | var1, 0, var2 >> 7);
               this.shortMidiEvent(176 | var1, 32, var2 & 127);
               this.shortMidiEvent(192 | var1, var3, 0);
            } catch (IllegalArgumentException var7) {
            }

         }
      }
   }

   public void setChannelVolume(int var1, int var2) {
      this.checkChannel(var1);
      this.check7bit(var2);

      try {
         this.shortMidiEvent(176 | var1, 7, var2);
      } catch (IllegalArgumentException var4) {
      }

   }

   public int[] getBankList(boolean var1) throws MediaException {
      throw new MediaException("not implemented");
   }

   public int[] getProgramList(int var1) throws MediaException {
      throw new MediaException("not implemented");
   }

   public String getProgramName(int var1, int var2) throws MediaException {
      throw new MediaException("not implemented");
   }

   public String getKeyName(int var1, int var2, int var3) throws MediaException {
      throw new MediaException("not implemented");
   }

   public void shortMidiEvent(int var1, int var2, int var3) {
      synchronized(this.player) {
         this.checkState();
         if ((var1 & -256) == 0 && var1 >= 128 && var1 != 240 && var1 != 247) {
            this.check7bit(var2);
            this.check7bit(var3);
            this.audioOut.shortMsg(var1, var2, var3);
         } else {
            throw new IllegalArgumentException("out of bounds");
         }
      }
   }

   public int longMidiEvent(byte[] var1, int var2, int var3) {
      synchronized(this.player) {
         this.checkState();
         if (var1 != null && var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
            return this.audioOut.longMsg(var1, var2, var3);
         } else {
            throw new IllegalArgumentException();
         }
      }
   }
}
