package com.nokia.mid.impl.isa.mmedia.audio;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.MIDIControl;

class MIDICtrl extends Switchable implements MIDIControl {
   private AudioOutImpl audioOut;

   MIDICtrl(BasicPlayer player, AudioOutImpl audioOut) {
      this.player = player;
      this.audioOut = audioOut;
   }

   private void checkChannel(int c) {
      if ((c & -16) != 0) {
         throw new IllegalArgumentException("out of bounds");
      }
   }

   private void check7bit(int d) {
      if ((d & -128) != 0) {
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

   public int[] getProgram(int channel) throws MediaException {
      throw new MediaException("not implemented");
   }

   public int getChannelVolume(int channel) {
      synchronized(this.player) {
         this.checkState();
         this.checkChannel(channel);

         int var10000;
         try {
            var10000 = this.audioOut.getChannelVolume(channel);
         } catch (MediaException var5) {
            return -1;
         }

         return var10000;
      }
   }

   public void setProgram(int channel, int bank, int program) {
      this.checkChannel(channel);
      this.check7bit(program);
      if (bank == -1) {
         bank = 0;
      }

      if ((bank & -16384) != 0) {
         throw new IllegalArgumentException("out of bounds");
      } else {
         synchronized(this.player) {
            try {
               this.shortMidiEvent(176 | channel, 0, bank >> 7);
               this.shortMidiEvent(176 | channel, 32, bank & 127);
               this.shortMidiEvent(192 | channel, program, 0);
            } catch (IllegalArgumentException var7) {
            }

         }
      }
   }

   public void setChannelVolume(int channel, int volume) {
      this.checkChannel(channel);
      this.check7bit(volume);

      try {
         this.shortMidiEvent(176 | channel, 7, volume);
      } catch (IllegalArgumentException var4) {
      }

   }

   public int[] getBankList(boolean custom) throws MediaException {
      throw new MediaException("not implemented");
   }

   public int[] getProgramList(int bank) throws MediaException {
      throw new MediaException("not implemented");
   }

   public String getProgramName(int bank, int prog) throws MediaException {
      throw new MediaException("not implemented");
   }

   public String getKeyName(int bank, int prog, int key) throws MediaException {
      throw new MediaException("not implemented");
   }

   public void shortMidiEvent(int type, int data1, int data2) {
      synchronized(this.player) {
         this.checkState();
         if ((type & -256) == 0 && type >= 128 && type != 240 && type != 247) {
            this.check7bit(data1);
            this.check7bit(data2);
            this.audioOut.shortMsg(type, data1, data2);
         } else {
            throw new IllegalArgumentException("out of bounds");
         }
      }
   }

   public int longMidiEvent(byte[] data, int offset, int length) {
      synchronized(this.player) {
         this.checkState();
         if (data != null && offset >= 0 && length >= 0 && offset + length <= data.length) {
            return this.audioOut.longMsg(data, offset, length);
         } else {
            throw new IllegalArgumentException();
         }
      }
   }
}
