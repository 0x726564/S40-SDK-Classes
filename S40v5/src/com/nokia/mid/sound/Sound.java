package com.nokia.mid.sound;

import com.nokia.mid.impl.isa.sound.SoundDatabase;
import java.io.ByteArrayInputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

public class Sound {
   public static final int SOUND_PLAYING = 0;
   public static final int SOUND_STOPPED = 1;
   public static final int SOUND_UNINITIALIZED = 3;
   public static final int FORMAT_TONE = 1;
   public static final int FORMAT_WAV = 5;
   private static boolean c = false;
   private static Sound d;
   private SoundListener e;
   private int f;
   private int g;
   private int type;
   private int status = 3;
   private int h;
   private int i;
   private byte[] data;
   private long j;
   private boolean k = false;
   private Sound.SoundPlayListener l;
   private byte[] m;
   private Player n;
   private VolumeControl o;
   private byte p;

   public Sound(int var1, long var2) {
      this.a(var1, var2);
   }

   public Sound(byte[] var1, int var2) {
      this.a(var1, var2);
   }

   private Sound(int var1) {
      this.g = var1;
      this.l = new Sound.SoundPlayListener(this);
      this.type = 10;
      this.h = 200;
      this.status = 1;
      if (!c) {
         c = true;
         this.init0();
      }

   }

   public static int getConcurrentSoundCount(int var0) {
      if (var0 != 1) {
         throw new IllegalArgumentException();
      } else {
         return 1;
      }
   }

   public int getGain() {
      return this.h;
   }

   public int getState() {
      return this.status;
   }

   public static int[] getSupportedFormats() {
      return new int[]{1};
   }

   public void init(int var1, long var2) {
      this.a(var1, var2);
      this.k = false;
   }

   public void init(byte[] var1, int var2) {
      this.a(var1, var2);
      this.k = false;
   }

   public void play(int var1) {
      this.i = var1;
      if (this.i < 0) {
         throw new IllegalArgumentException();
      } else {
         if (this.i < 255) {
            this.a(var1);
         }

      }
   }

   public void release() {
      this.a();
   }

   public void resume() {
      if (this.k) {
         this.a(this.i);
      }

   }

   public void setGain(int var1) {
      if (var1 < 0) {
         this.h = 0;
      } else if (var1 > 255) {
         this.h = 255;
      } else {
         this.h = var1;
      }
   }

   public void setSoundListener(SoundListener var1) {
      this.e = var1;
   }

   public void stop() {
      if (this.status == 0) {
         this.b();
      }

   }

   private void a(byte[] var1, int var2) throws IllegalArgumentException, NullPointerException {
      if (var1 == null) {
         this.status = 3;
         throw new NullPointerException("Data is null.");
      } else if (var2 != 1) {
         this.status = 3;
         throw new IllegalArgumentException("Type unsupported or unknown.");
      } else {
         if (this.status != 3) {
            this.a();
         }

         this.g = var2;
         this.type = 9;
         this.h = 200;
         this.data = var1;
         this.m = nConvertOTAToMIDI(this.data);
         if (this.m == null) {
            throw new IllegalArgumentException("Error converting to MIDI format.");
         } else {
            this.a(this.m);
            this.status = 1;
         }
      }
   }

   private void a(int var1, long var2) throws IllegalArgumentException {
      if (var2 > 0L && var1 <= 7692 && var1 >= 0) {
         if (this.status != 3) {
            this.a();
         }

         this.g = 1;
         this.type = 8;
         this.f = var1;
         this.j = var2 * 1000L;
         this.h = 200;
         this.p = (byte)nConvertFreqToJavaNote(this.f);
         byte var4 = (byte)((int)(this.j & 255L));
         byte var6 = (byte)((int)((this.j & 65280L) >>> 8));
         byte var3 = (byte)((int)((this.j & 16711680L) >>> 16));
         byte[] var5 = new byte[]{77, 84, 104, 100, 0, 0, 0, 6, 0, 1, 0, 2, 0, 96, 77, 84, 114, 107, 0, 0, 0, 19, 0, -1, 88, 4, 4, 2, 24, 8, 0, -1, 81, 3, var3, var6, var4, 0, -1, 47, 0, 77, 84, 114, 107, 0, 0, 0, 15, 0, -63, 77, 0, -111, this.p, 127, 96, -111, this.p, 0, 0, -1, 47, 0};
         this.a(var5);
         this.status = 1;
      } else {
         this.status = 3;
         throw new IllegalArgumentException("Parameter values are illegal.");
      }
   }

   private void a(byte[] var1) {
      try {
         this.n = Manager.createPlayer(new ByteArrayInputStream(var1), "audio/midi");
         this.l = new Sound.SoundPlayListener(this);
         this.n.addPlayerListener(this.l);
      } catch (Exception var2) {
      }
   }

   private void a(int var1) {
      if (this.type == 9 && this.data == null) {
         throw new NullPointerException("Data is null.");
      } else {
         if (this.status != 3) {
            if (d != null && d != this) {
               d.stop();
            }

            d = this;
            switch(this.type) {
            case 8:
            case 9:
               if (this.status == 0) {
                  this.b();
               }

               try {
                  this.n.prefetch();
                  this.o = (VolumeControl)this.n.getControl("VolumeControl");
                  this.o.setLevel((int)((double)this.h / 2.55D));
                  if (this.i == 0) {
                     this.n.setLoopCount(-1);
                  } else {
                     this.n.setLoopCount(this.i);
                  }

                  this.n.setMediaTime(0L);
                  this.n.start();
               } catch (Exception var2) {
               }
               break;
            case 10:
               this.play0(this.type, this.g, this.j, this.f, this.data, 255, var1);
            }

            this.status = 0;
            this.k = true;
         }

      }
   }

   private void a() {
      if (this.status != 3) {
         if (this.status == 0) {
            this.b();
         }

         if (this.n != null) {
            this.n.close();
            this.data = null;
         }

         if (this.type == 8) {
            this.f = 0;
            this.j = 0L;
            this.p = 0;
         }

         this.type = 0;
         this.g = 0;
         this.i = 0;
         this.h = 200;
         this.k = false;
         this.status = 3;
         this.l = null;
      }

   }

   private void b() {
      try {
         this.n.deallocate();
      } catch (Exception var1) {
      }

      this.status = 1;
   }

   private native int play0(int var1, int var2, long var3, int var5, byte[] var6, int var7, int var8);

   private native void init0();

   public static native byte[] nConvertOTAToMIDI(byte[] var0);

   public static native int nConvertFreqToJavaNote(int var0);

   static SoundListener a(Sound var0) {
      return var0.e;
   }

   static int a(Sound var0, int var1) {
      return var0.status = var1;
   }

   static {
      SoundDatabase.addSound(new Sound(0), 0);
      SoundDatabase.addSound(new Sound(1), 1);
      SoundDatabase.addSound(new Sound(2), 2);
      SoundDatabase.addSound(new Sound(3), 3);
      SoundDatabase.addSound(new Sound(4), 4);
      SoundDatabase.addSound(new Sound(5), 5);
   }

   private class SoundPlayListener implements PlayerListener {
      private final Sound b;

      public void playerUpdate(Player var1, String var2, Object var3) {
         if (var2 != "stopped" && var2 != "endOfMedia") {
            if (var2 == "closed") {
               if (Sound.a(this.b) != null) {
                  Sound.a((Sound)this.b, 3);
                  Sound.a(this.b).soundStateChanged(this.b, 3);
                  return;
               }
            } else if (var2 == "started" && Sound.a(this.b) != null) {
               Sound.a((Sound)this.b, 0);
               Sound.a(this.b).soundStateChanged(this.b, 0);
            }
         } else if (Sound.a(this.b) != null) {
            Sound.a((Sound)this.b, 1);
            Sound.a(this.b).soundStateChanged(this.b, 1);
            return;
         }

      }

      SoundPlayListener(Sound var1, Object var2) {
         this.b = var1;
      }
   }
}
