package com.nokia.mid.impl.isa.ui.gdi;

import com.nokia.mid.impl.isa.ui.style.Zone;
import java.util.Enumeration;
import java.util.Vector;

public class Graphics {
   public static final int ROTATION_0 = 0;
   public static final int ROTATION_90 = 1;
   public static final int ROTATION_180 = 2;
   public static final int ROTATION_270 = 4;
   public static final int FLIP_NONE = 0;
   public static final int FLIP_UP_DOWN = 8;
   public static final int FLIP_LEFT_RIGHT = 16;
   public static final int FLIP_VER_HOR = 24;
   private static final int ROTATION = 7;
   private static final int FLIP = 24;
   private Pixmap pixmap;
   private ColorCtrl colorCtrl;
   private int pixmapWidth;
   private int pixmapHeight;
   private int clipX;
   private int clipY;
   private int clipHeight;
   private int clipWidth;
   private int lineWidth = 1;
   private int rotation = 0;
   private int rotOrigX;
   private int rotOrigY;
   private Font font;
   private boolean txtTransparency = false;

   private static native void nativeStaticInitialiser();

   Graphics(Pixmap var1) {
      this.pixmap = var1;
      this.colorCtrl = new ColorCtrl();
      this.pixmapWidth = var1.getWidth();
      this.pixmapHeight = var1.getHeight();
      this.nativeInit();
      this.setClip(0, 0, this.pixmapWidth, this.pixmapHeight);
   }

   private native void nativeInit();

   public native void copyArea(int var1, int var2, int var3, int var4, int var5, int var6);

   public native int getDisplayColor(int var1);

   public native void drawPixel(int var1, int var2);

   public native void drawLine(int var1, int var2, int var3, int var4);

   public native void s_drawLine(int var1, int var2, int var3, int var4);

   public native void drawDottedLine(int var1, int var2, int var3, int var4);

   public native void drawRect(int var1, int var2, int var3, int var4);

   public native void drawRegion(Pixmap var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   public native void drawDottedRect(int var1, int var2, int var3, int var4);

   public native void drawRoundRect(int var1, int var2, int var3, int var4, int var5, int var6);

   public native void drawDottedRoundRect(int var1, int var2, int var3, int var4, int var5, int var6);

   public native void drawOval(int var1, int var2, int var3, int var4);

   public native void drawDottedOval(int var1, int var2, int var3, int var4);

   public native void drawArc(int var1, int var2, int var3, int var4, int var5, int var6);

   public native void drawDottedArc(int var1, int var2, int var3, int var4, int var5, int var6);

   public native void drawTriangle(int var1, int var2, int var3, int var4, int var5, int var6);

   public native void drawPoly(short[] var1, short[] var2, int var3, boolean var4);

   public native void drawDottedPoly(short[] var1, short[] var2, int var3, boolean var4);

   public native void drawPixmap(Pixmap var1, int var2, int var3);

   public native int drawUnicode(char var1, int var2, int var3);

   public void drawTextInZone(Zone var1, int var2, int var3, Vector var4, int var5) {
      this.drawTextInArea(var1.x + var2, var1.y + var3, var1.width, var1.height, var4, var5);
   }

   public void drawTextInArea(int var1, int var2, int var3, int var4, Vector var5, int var6) {
      int var7 = var2;
      TextLine var8 = null;
      synchronized(var5) {
         if (var5.size() > 0) {
            for(Enumeration var10 = var5.elements(); var10.hasMoreElements(); var7 += var8.getTextLineHeight()) {
               var8 = (TextLine)var10.nextElement();
               this.drawTextLine(var1, var7, var3, var8, var6);
            }
         }

      }
   }

   public void drawTextLine(Zone var1, int var2, int var3, TextLine var4, int var5) {
      this.drawTextLine(var1.x + var2, var1.y + var3, var1.width, var4, var5);
   }

   public void drawTextLine(int var1, int var2, int var3, TextLine var4, int var5) {
      int var6 = var1;
      boolean var7 = false;
      if (null != var4) {
         int var8 = var4.getTextLineWidth();
         if (0 < var8) {
            if (var8 > var3) {
               var8 = var3;
            }

            switch(var5) {
            case 1:
               var4.setAlignment(1);
               break;
            case 2:
               var6 = var1 + var3 / 2;
               var4.setAlignment(2);
               break;
            case 3:
               var6 = var1 + var3;
               var4.setAlignment(3);
            }

            this.drawText(var4, var6, var2, var8);
         }
      }

   }

   public void drawText(TextLine var1, int var2, int var3) {
      this.drawText(var1, var2, var3, 32767);
   }

   public void drawText(TextLine var1, int var2, int var3, int var4) {
      boolean var5 = false;
      int var6 = var1.getTextLineWidth();
      int var7 = this.pixmapWidth;
      boolean var8 = false;
      boolean var9 = false;
      boolean var10 = false;
      if (0 != (this.colorCtrl.getFgColor() >>> 24 & 255)) {
         if (var1 != null) {
            int var11 = var4 < var6 ? var4 : var6;
            int var12;
            switch(var1.getAlignment()) {
            case 0:
            case 1:
            default:
               var12 = var2;
               break;
            case 2:
               var12 = var2 - var11 / 2;
               break;
            case 3:
               var12 = var2 - var11;
            }

            int var13 = var6;
            if (var6 > var4) {
               var13 = var4;
            }

            if (var6 + var12 > 0 && var12 < var7) {
               this.renderText(var1, var12, var3 + var1.getTextLineLeading(), var13);
            }
         }

      }
   }

   public native void drawRGB(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8);

   public native void fillRect(int var1, int var2, int var3, int var4);

   public native void fillRoundRect(int var1, int var2, int var3, int var4, int var5, int var6);

   public native void fillOval(int var1, int var2, int var3, int var4);

   public native void fillArc(int var1, int var2, int var3, int var4, int var5, int var6);

   public native void fillTriangle(int var1, int var2, int var3, int var4, int var5, int var6);

   public native void fillPoly(short[] var1, short[] var2, int var3);

   public ColorCtrl getColorCtrl() {
      return this.colorCtrl;
   }

   public Font getFont() {
      return this.font;
   }

   public int getLineWidth() {
      return this.lineWidth;
   }

   public int getRotation() {
      return this.rotation;
   }

   public int getRotOrigX() {
      return this.rotOrigX;
   }

   public int getRotOrigY() {
      return this.rotOrigY;
   }

   public int getClipX() {
      return this.clipX;
   }

   public int getClipY() {
      return this.clipY;
   }

   public int getClipWidth() {
      return this.clipWidth;
   }

   public int getClipHeight() {
      return this.clipHeight;
   }

   public int getWidth() {
      return this.pixmapWidth;
   }

   public int getHeight() {
      return this.pixmapHeight;
   }

   public Pixmap getPixmap() {
      return this.pixmap;
   }

   public boolean getTextTransparency() {
      return this.txtTransparency;
   }

   public void setFont(Font var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.font = var1;
      }
   }

   public void setClip(int var1, int var2, int var3, int var4) {
      int var5 = this.pixmapWidth;
      int var6 = this.pixmapHeight;
      if (var1 < var5 && var2 < var6 && var1 + var3 > 0 && var2 + var4 > 0 && var3 > 0 && var4 > 0) {
         if (var1 < 0) {
            var3 += var1;
            var1 = 0;
         }

         if (var1 + var3 > var5) {
            var3 = var5 - var1;
         }

         if (var2 < 0) {
            var4 += var2;
            var2 = 0;
         }

         if (var2 + var4 > var6) {
            var4 = var6 - var2;
         }

         this.clipX = var1;
         this.clipY = var2;
         this.clipWidth = var3;
         this.clipHeight = var4;
      } else {
         this.clipX = 0;
         this.clipY = 0;
         this.clipWidth = 0;
         this.clipHeight = 0;
      }
   }

   public void setLineWidth(int var1) {
      this.lineWidth = var1;
   }

   public void setRotation(int var1) {
      if ((var1 & -32) <= 0 && ((var1 & 7) == 0 || (var1 & 7) == 1 || (var1 & 7) == 2 || (var1 & 7) == 4) && ((var1 & 24) == 0 || (var1 & 24) == 8 || (var1 & 24) == 16 || (var1 & 24) == 24)) {
         this.rotation = var1;
      } else {
         throw new IllegalArgumentException("Rotation not recognized " + var1);
      }
   }

   public void setRotOrigX(int var1) {
      this.rotOrigX = var1;
   }

   public void setRotOrigY(int var1) {
      this.rotOrigY = var1;
   }

   public void setTextTransparency(boolean var1) {
      this.txtTransparency = var1;
   }

   public native void refresh(int var1, int var2, int var3, int var4, int var5, int var6);

   private native void renderText(TextLine var1, int var2, int var3, int var4);

   public native void getPixels(byte[] var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);

   public native void getPixels(short[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   public native void getPixels(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   public native void drawPixels(byte[] var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   public native void drawPixels(short[] var1, boolean var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   public native void drawPixels(int[] var1, boolean var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   public native int getNativePixelFormat();

   public native boolean fillWithBackgroundImage();

   public native boolean fillWithFullTransparency();

   public int resolveManipulation(int var1) {
      int var2 = 0;
      boolean var3 = false;
      if ((var1 & 8192) > 0) {
         var2 |= 16;
      }

      if ((var1 & 16384) > 0) {
         var2 |= 8;
      }

      int var4 = (var1 & 8191) % 360;
      switch(var4) {
      case 0:
         return var2;
      case 90:
         return var2 | 4;
      case 180:
         return var2 | 2;
      case 270:
         return var2 | 1;
      default:
         throw new IllegalArgumentException("Rotations in multiples of 90 only allowed" + var4);
      }
   }

   static {
      nativeStaticInitialiser();
   }
}
