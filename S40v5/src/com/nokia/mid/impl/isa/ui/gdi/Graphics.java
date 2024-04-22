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
   private Pixmap bS;
   private ColorCtrl bT;
   private int bU;
   private int bV;
   private int bW;
   private int bX;
   private int bY;
   private int bZ;
   private int ca = 1;
   private int cb = 0;
   private int cc;
   private int cd;
   private Font ce;
   private boolean cf = false;

   private static native void nativeStaticInitialiser();

   Graphics(Pixmap var1) {
      this.bS = var1;
      this.bT = new ColorCtrl();
      this.bU = var1.getWidth();
      this.bV = var1.getHeight();
      this.nativeInit();
      this.setClip(0, 0, this.bU, this.bV);
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
      var2 = var2;
      TextLine var9 = null;
      synchronized(var5) {
         if (var5.size() > 0) {
            for(Enumeration var10 = var5.elements(); var10.hasMoreElements(); var2 += var9.getTextLineHeight()) {
               var9 = (TextLine)var10.nextElement();
               this.drawTextLine(var1, var2, var3, var9, var6);
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
      int var7 = this.bU;
      boolean var8 = false;
      boolean var9 = false;
      var8 = false;
      if (0 != (this.bT.getFgColor() >>> 24 & 255)) {
         if (var1 != null) {
            int var11 = var4 < var6 ? var4 : var6;
            int var10;
            switch(var1.getAlignment()) {
            case 0:
            case 1:
            default:
               var10 = var2;
               break;
            case 2:
               var10 = var2 - var11 / 2;
               break;
            case 3:
               var10 = var2 - var11;
            }

            int var12 = var6;
            if (var6 > var4) {
               var12 = var4;
            }

            if (var6 + var10 > 0 && var10 < var7) {
               this.renderText(var1, var10, var3 + var1.getTextLineLeading(), var12);
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
      return this.bT;
   }

   public Font getFont() {
      return this.ce;
   }

   public int getLineWidth() {
      return this.ca;
   }

   public int getRotation() {
      return this.cb;
   }

   public int getRotOrigX() {
      return this.cc;
   }

   public int getRotOrigY() {
      return this.cd;
   }

   public int getClipX() {
      return this.bW;
   }

   public int getClipY() {
      return this.bX;
   }

   public int getClipWidth() {
      return this.bZ;
   }

   public int getClipHeight() {
      return this.bY;
   }

   public int getWidth() {
      return this.bU;
   }

   public int getHeight() {
      return this.bV;
   }

   public Pixmap getPixmap() {
      return this.bS;
   }

   public boolean getTextTransparency() {
      return this.cf;
   }

   public void setFont(Font var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.ce = var1;
      }
   }

   public void setClip(int var1, int var2, int var3, int var4) {
      int var5 = this.bU;
      int var6 = this.bV;
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

         this.bW = var1;
         this.bX = var2;
         this.bZ = var3;
         this.bY = var4;
      } else {
         this.bW = 0;
         this.bX = 0;
         this.bZ = 0;
         this.bY = 0;
      }
   }

   public void setLineWidth(int var1) {
      this.ca = var1;
   }

   public void setRotation(int var1) {
      if ((var1 & -32) <= 0 && ((var1 & 7) == 0 || (var1 & 7) == 1 || (var1 & 7) == 2 || (var1 & 7) == 4) && ((var1 & 24) == 0 || (var1 & 24) == 8 || (var1 & 24) == 16 || (var1 & 24) == 24)) {
         this.cb = var1;
      } else {
         throw new IllegalArgumentException("Rotation not recognized " + var1);
      }
   }

   public void setRotOrigX(int var1) {
      this.cc = var1;
   }

   public void setRotOrigY(int var1) {
      this.cd = var1;
   }

   public void setTextTransparency(boolean var1) {
      this.cf = var1;
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
      int var3 = 0;
      boolean var2 = false;
      if ((var1 & 8192) > 0) {
         var3 = 16;
      }

      if ((var1 & 16384) > 0) {
         var3 |= 8;
      }

      int var4;
      switch(var4 = (var1 & 8191) % 360) {
      case 0:
         return var3;
      case 90:
         return var3 | 4;
      case 180:
         return var3 | 2;
      case 270:
         return var3 | 1;
      default:
         throw new IllegalArgumentException("Rotations in multiples of 90 only allowed" + var4);
      }
   }

   static {
      nativeStaticInitialiser();
   }
}
