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
   private static final int MAX_BUF_SIZE = 1024;
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
   private static Timer animationTimer;
   private static Pixmap displayPixmap;
   private int width;
   private int height;
   private int pixmapType;
   private int pixelFormat;
   private byte[] nativePixelData;
   private byte[] nativePaletteData;
   private int nativeIMEData;
   private int iconID;
   AnimationListener listener;
   private Pixmap.AnimationTimerTask timerTask;
   private boolean mutable;

   private static native void nativeStaticInitialiser();

   private Pixmap() {
   }

   public Pixmap(Pixmap orig) {
      this.height = orig.height;
      this.width = orig.width;
      this.pixmapType = orig.pixmapType;
      this.pixelFormat = orig.pixelFormat;
      this.mutable = orig.mutable;
      this.nativePaletteData = orig.nativePaletteData;
      this.nativePixelData = new byte[orig.nativePixelData.length];
      System.arraycopy(orig.nativePixelData, 0, this.nativePixelData, 0, orig.nativePixelData.length);
   }

   public Pixmap(Pixmap srcPixmap, int x, int y, int width, int height, int transform) {
      this.nativeCreatePixmapFromTransformedRegion(srcPixmap, x, y, width, height, transform);
   }

   public static Pixmap getUpdatedDisplayPixmap() {
      if (displayPixmap == null) {
         displayPixmap = new Pixmap();
         displayPixmap.pixmapType = 0;
      }

      displayPixmap.nativeUpdateDisplayPixmap();
      return displayPixmap;
   }

   private static native int getNativeIconID(int var0);

   public static Pixmap createPixmap(int iconID) {
      if (iconID >= 0 && iconID < 20) {
         return createPixmapWithNativeID(getNativeIconID(iconID));
      } else {
         throw new IllegalArgumentException("Pixmap: unsupported icons");
      }
   }

   public static Pixmap createPixmapWithNativeID(int nativeIconID) {
      Pixmap pixmap = new Pixmap();
      pixmap.nativeGetIconData(nativeIconID);
      pixmap.iconID = nativeIconID;
      return pixmap;
   }

   public static Pixmap createPixmap(int width, int height, boolean mutable, int initialColor) {
      if (width > 0 && height > 0) {
         Pixmap pixmap = new Pixmap();
         if (width == DeviceInfo.getDisplayWidth(3) && height == DeviceInfo.getDisplayHeight(3)) {
            pixmap.pixmapType = 4;
         } else {
            pixmap.pixmapType = 1;
         }

         pixmap.nativeInitEmptyPixmap(width, height, mutable, -16777216 != (initialColor & -16777216));
         pixmap.nativeSetImageColor(initialColor);
         return pixmap;
      } else {
         throw new IllegalArgumentException("Pixmap: Negative size");
      }
   }

   public static Pixmap createPixmap(byte[] imageData, int imageOffset, int imageLength, boolean mutable) {
      if (imageData == null) {
         throw new NullPointerException("ImageData passed to Image.CreateImage() is null.");
      } else if (imageLength >= 0 && imageOffset >= 0 && imageOffset + imageLength <= imageData.length && imageOffset + imageLength >= 0) {
         Pixmap pixmap = new Pixmap();
         pixmap.nativeDecodeImage(imageData, imageOffset, imageLength, mutable);
         return pixmap;
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public static Pixmap createPixmap(InputStream iStream, boolean mutable) throws IOException {
      Pixmap pixmap = null;
      byte[] buffer = new byte[1024];

      int readbytes;
      byte[] newbuffer;
      for(readbytes = iStream.read(buffer); readbytes == 1024; readbytes = iStream.read(newbuffer, newbuffer.length - 1024, 1024)) {
         newbuffer = new byte[buffer.length + 1024];
         System.arraycopy(buffer, 0, newbuffer, 0, buffer.length);
         buffer = newbuffer;
      }

      try {
         if (-1 == readbytes) {
            pixmap = createPixmap((byte[])buffer, 0, buffer.length - 1024, mutable);
         } else {
            pixmap = createPixmap((byte[])buffer, 0, buffer.length - 1024 + readbytes, mutable);
         }

         return pixmap;
      } catch (IllegalArgumentException var6) {
         throw new IOException();
      }
   }

   public static Pixmap createPixmap(int[] rgb, int width, int height, boolean processAlpha) {
      Pixmap pixmap = createPixmap(width, height, false, processAlpha ? UIStyle.COLOUR_BLACK & 16777215 : UIStyle.COLOUR_BLACK);
      pixmap.nativeCreateRGBPixmap(rgb, width, height, processAlpha);
      return pixmap;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public byte getDisplayType() {
      return (byte)this.pixelFormat;
   }

   public boolean isMutable() {
      return this.mutable;
   }

   public void setMutable(boolean newMutable) {
      this.mutable = newMutable;
   }

   public native void nativeSetImageColor(int var1);

   public Graphics getGraphics() {
      return new Graphics(this);
   }

   public boolean isAnimatedPixmap() {
      return this.pixmapType == 3 || this.pixmapType == 2;
   }

   public boolean isSystemDisplayPixmap() {
      return this.pixmapType == 0;
   }

   public boolean isGameCanvasPixmap() {
      return this.pixmapType == 4;
   }

   public synchronized void setAnimationListener(AnimationListener listener) {
      this.listener = listener;
   }

   public synchronized void startAnimationTimer() {
      if (this.timerTask == null) {
         int period = this.nativeAdvanceFrame();
         if (period != 0) {
            if (animationTimer == null) {
               animationTimer = new Timer();
            }

            this.timerTask = new Pixmap.AnimationTimerTask(period);
            animationTimer.schedule(this.timerTask, (long)period, (long)period);
         }

      }
   }

   public synchronized void stopAnimationTimer() {
      if (this.timerTask != null) {
         this.timerTask.cancel();
         this.timerTask = null;
      }

      this.nativeDispose();
   }

   public void advanceFrame() {
      AnimationListener list;
      synchronized(this) {
         int nextFrameDelay = this.nativeAdvanceFrame();
         list = this.listener;
         if (this.timerTask != null && nextFrameDelay != this.timerTask.frameDelay) {
            if (nextFrameDelay == 0) {
               this.stopAnimationTimer();
            } else {
               this.timerTask.cancel();
               this.timerTask = new Pixmap.AnimationTimerTask(nextFrameDelay);
               animationTimer.schedule(this.timerTask, (long)nextFrameDelay, (long)nextFrameDelay);
            }
         }
      }

      if (list != null) {
         list.frameAdvanced(this);
      }

   }

   public synchronized void resetAnimation() {
      boolean restartTimer = false;
      if (this.timerTask != null) {
         restartTimer = true;
         this.stopAnimationTimer();
      }

      if (this.nativeIMEData == 0 && this.pixmapType == 2) {
         this.nativeGetIconData(this.iconID);
      }

      this.nativeResetAnimation();
      if (restartTimer) {
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
      int frameDelay;

      AnimationTimerTask(int delay) {
         this.frameDelay = delay;
      }

      public void run() {
         Pixmap.this.advanceFrame();
      }
   }
}
