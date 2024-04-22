package com.nokia.mid.impl.isa.ui.gdi;

import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class Pixmap {
   public static final int TYPE_DISPLAY = 0;
   public static final int TYPE_OFFSCREEN_PIXMAP = 1;
   public static final int TYPE_GIF_ANIM = 2;
   public static final int TYPE_PPM_ANIM = 3;
   public static final int TYPE_GAME_CANVAS_BUFFER = 4;
   public static final int icon_list_java = 0;
   public static final int icon_note_alarm = 1;
   public static final int icon_note_info = 2;
   public static final int icon_note_confirmation = 3;
   public static final int icon_note_error = 4;
   public static final int icon_note_warning = 5;
   public static final int icon_multiselection_mark = 6;
   public static final int icon_multiselection_unmark = 7;
   public static final int icon_singleselection_mark = 8;
   public static final int icon_singleselection_unmark = 9;
   public static final int icon_anim_waiting_bar = 10;
   public static final int icon_popup_arrow_left = 11;
   public static final int icon_popup_arrow_right = 12;
   public static final int icon_broken_custom_item = 13;
   public static final int icon_gauge_background = 14;
   public static final int icon_gauge_notch = 15;
   public static final int icon_gauge_knob = 16;
   public static final int icon_gauge_waiting_bar_idle = 17;
   public static final int icon_jsr135_video = 18;
   public static final int icon_jsr135_broken_video = 19;
   public static final int icon_kjava_last = 20;
   private static Timer bE;
   private static Pixmap bF;
   private int width;
   private int height;
   private int bG;
   private int bH;
   private byte[] bI;
   private byte[] bJ;
   private int bK;
   private AnimationListener bL;
   private Pixmap.AnimationTimerTask bM;
   private boolean bN;

   private static native void nativeStaticInitialiser();

   private Pixmap() {
   }

   public Pixmap(Pixmap var1) {
      this.height = var1.height;
      this.width = var1.width;
      this.bG = var1.bG;
      this.bH = var1.bH;
      this.bN = var1.bN;
      this.bJ = var1.bJ;
      this.bI = new byte[var1.bI.length];
      System.arraycopy(var1.bI, 0, this.bI, 0, var1.bI.length);
   }

   public Pixmap(Pixmap var1, int var2, int var3, int var4, int var5, int var6) {
      this.nativeCreatePixmapFromTransformedRegion(var1, var2, var3, var4, var5, var6);
   }

   public static Pixmap getUpdatedDisplayPixmap() {
      if (bF == null) {
         (bF = new Pixmap()).bG = 0;
      }

      bF.nativeUpdateDisplayPixmap();
      return bF;
   }

   private static native int getNativeIconID(int var0);

   public static Pixmap createPixmap(int var0) {
      if (var0 >= 0 && var0 < 20) {
         return createPixmapWithNativeID(getNativeIconID(var0));
      } else {
         throw new IllegalArgumentException("Pixmap: unsupported icons");
      }
   }

   public static Pixmap createPixmapWithNativeID(int var0) {
      Pixmap var1;
      (var1 = new Pixmap()).nativeGetIconData(var0);
      var1.bK = var0;
      return var1;
   }

   public static Pixmap createPixmap(int var0, int var1, boolean var2, int var3) {
      if (var0 > 0 && var1 > 0) {
         Pixmap var4 = new Pixmap();
         if (var0 == DeviceInfo.getDisplayWidth(3) && var1 == DeviceInfo.getDisplayHeight(3)) {
            var4.bG = 4;
         } else {
            var4.bG = 1;
         }

         var4.nativeInitEmptyPixmap(var0, var1, var2, -16777216 != (var3 & -16777216));
         var4.nativeSetImageColor(var3);
         return var4;
      } else {
         throw new IllegalArgumentException("Pixmap: Negative size");
      }
   }

   public static Pixmap createPixmap(byte[] var0, int var1, int var2, boolean var3) {
      if (var0 == null) {
         throw new NullPointerException("ImageData passed to Image.CreateImage() is null.");
      } else if (var2 >= 0 && var1 >= 0 && var1 + var2 <= var0.length && var1 + var2 >= 0) {
         Pixmap var4;
         (var4 = new Pixmap()).nativeDecodeImage(var0, var1, var2, var3);
         return var4;
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public static Pixmap createPixmap(InputStream var0, boolean var1) throws IOException {
      Pixmap var2 = null;
      byte[] var3 = new byte[1024];

      int var4;
      byte[] var6;
      for(var4 = var0.read(var3); var4 == 1024; var4 = var0.read(var6, var6.length - 1024, 1024)) {
         var6 = new byte[var3.length + 1024];
         System.arraycopy(var3, 0, var6, 0, var3.length);
         var3 = var6;
      }

      try {
         if (-1 == var4) {
            var2 = createPixmap((byte[])var3, 0, var3.length - 1024, var1);
         } else {
            var2 = createPixmap((byte[])var3, 0, var3.length - 1024 + var4, var1);
         }

         return var2;
      } catch (IllegalArgumentException var5) {
         throw new IOException();
      }
   }

   public static Pixmap createPixmap(int[] var0, int var1, int var2, boolean var3) {
      Pixmap var4;
      (var4 = createPixmap(var1, var2, false, var3 ? UIStyle.COLOUR_BLACK & 16777215 : UIStyle.COLOUR_BLACK)).nativeCreateRGBPixmap(var0, var1, var2, var3);
      return var4;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public byte getDisplayType() {
      return (byte)this.bH;
   }

   public boolean isMutable() {
      return this.bN;
   }

   public void setMutable(boolean var1) {
      this.bN = var1;
   }

   public native void nativeSetImageColor(int var1);

   public Graphics getGraphics() {
      return new Graphics(this);
   }

   public boolean isAnimatedPixmap() {
      return this.bG == 3 || this.bG == 2;
   }

   public boolean isSystemDisplayPixmap() {
      return this.bG == 0;
   }

   public boolean isGameCanvasPixmap() {
      return this.bG == 4;
   }

   public synchronized void setAnimationListener(AnimationListener var1) {
      this.bL = var1;
   }

   public synchronized void startAnimationTimer() {
      if (this.bM == null) {
         int var1;
         if ((var1 = this.nativeAdvanceFrame()) != 0) {
            if (bE == null) {
               bE = new Timer();
            }

            this.bM = new Pixmap.AnimationTimerTask(this, var1);
            bE.schedule(this.bM, (long)var1, (long)var1);
         }

      }
   }

   public synchronized void stopAnimationTimer() {
      if (this.bM != null) {
         this.bM.cancel();
         this.bM = null;
      }

      this.nativeDispose();
   }

   public void advanceFrame() {
      AnimationListener var2;
      synchronized(this) {
         int var1 = this.nativeAdvanceFrame();
         var2 = this.bL;
         if (this.bM != null && var1 != this.bM.lY) {
            if (var1 == 0) {
               this.stopAnimationTimer();
            } else {
               this.bM.cancel();
               this.bM = new Pixmap.AnimationTimerTask(this, var1);
               bE.schedule(this.bM, (long)var1, (long)var1);
            }
         }
      }

      if (var2 != null) {
         var2.frameAdvanced(this);
      }

   }

   public synchronized void resetAnimation() {
      boolean var1 = false;
      if (this.bM != null) {
         var1 = true;
         this.stopAnimationTimer();
      }

      if (this.bG == 2) {
         this.nativeGetIconData(this.bK);
      }

      this.nativeResetAnimation();
      if (var1) {
         this.startAnimationTimer();
      }

   }

   private native int nativeAdvanceFrame();

   private native void nativeResetAnimation();

   private native void nativeCreatePixmapFromTransformedRegion(Pixmap var1, int var2, int var3, int var4, int var5, int var6);

   private native void nativeInitEmptyPixmap(int var1, int var2, boolean var3, boolean var4);

   private native void nativeUpdateDisplayPixmap();

   private native void nativeGetIconData(int var1);

   private native void nativeDecodeImage(byte[] var1, int var2, int var3, boolean var4);

   private native void nativeCreateRGBPixmap(int[] var1, int var2, int var3, boolean var4);

   public native void nativeGetRGBFromPixmap(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7);

   private native void nativeDispose();

   static {
      nativeStaticInitialiser();
   }

   private class AnimationTimerTask extends TimerTask {
      int lY;
      private final Pixmap lZ;

      AnimationTimerTask(Pixmap var1, int var2) {
         this.lZ = var1;
         this.lY = var2;
      }

      public void run() {
         this.lZ.advanceFrame();
      }
   }
}
