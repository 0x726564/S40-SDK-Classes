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

   Graphics(Pixmap pixmap) {
      this.pixmap = pixmap;
      this.colorCtrl = new ColorCtrl();
      this.pixmapWidth = pixmap.getWidth();
      this.pixmapHeight = pixmap.getHeight();
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

   public void drawChars(char[] data, int offset, int length, int x, int y, int anchor) {
      if (length > 0) {
         this.nativeDrawChars(data, offset, length, x, y, anchor, this.font);
      }

   }

   public void drawTextInZone(Zone textZone, int offsetX, int offsetY, Vector textLines, int alignment) {
      this.drawTextInArea(textZone.x + offsetX, textZone.y + offsetY, textZone.width, textZone.height, textLines, alignment);
   }

   public void drawTextInArea(int x, int y, int width, int height, Vector textLines, int alignment) {
      int offsetY = y;
      TextLine tLine = null;
      synchronized(textLines) {
         if (textLines.size() > 0) {
            for(Enumeration tlEnumerator = textLines.elements(); tlEnumerator.hasMoreElements(); offsetY += tLine.getTextLineHeight()) {
               tLine = (TextLine)tlEnumerator.nextElement();
               this.drawTextLine(x, offsetY, width, tLine, alignment, false);
            }
         }

      }
   }

   public void drawTextLine(Zone textZone, int offsetX, int offsetY, TextLine textLine, int alignment) {
      this.drawTextLine(textZone.x + offsetX, textZone.y + offsetY, textZone.width, textLine, alignment, false);
   }

   public void drawTextLine(int x, int y, int width, TextLine textLine, int alignment, boolean useNativeDigits) {
      int offsetX = x;
      int linewidth = false;
      if (null != textLine) {
         int linewidth = textLine.getTextLineWidth();
         if (0 < linewidth) {
            if (linewidth > width) {
               linewidth = width;
            }

            switch(alignment) {
            case 1:
               textLine.setAlignment(1);
               break;
            case 2:
               offsetX = x + width / 2;
               textLine.setAlignment(2);
               break;
            case 3:
               offsetX = x + width;
               textLine.setAlignment(3);
            }

            this.drawText(textLine, offsetX, y, linewidth, useNativeDigits);
         }
      }

   }

   public void drawText(TextLine tLine, int left, int top) {
      this.drawText(tLine, left, top, 32767);
   }

   public void drawText(TextLine tline, int left, int top, int areaWidth) {
      this.drawText(tline, left, top, areaWidth, false);
   }

   public void drawText(TextLine tline, int left, int top, int areaWidth, boolean useNativeDigits) {
      int x = false;
      int tWidth = tline.getTextLineWidth();
      int zoneWidth = this.pixmapWidth;
      int segmentWidth = false;
      int segmentAreaWidth = false;
      int renderedWidth = false;
      if (0 != (this.colorCtrl.getFgColor() >>> 24 & 255)) {
         if (tline != null) {
            int renderedWidth = areaWidth < tWidth ? areaWidth : tWidth;
            int x;
            switch(tline.getAlignment()) {
            case 0:
            case 1:
            default:
               x = left;
               break;
            case 2:
               x = left - renderedWidth / 2;
               break;
            case 3:
               x = left - renderedWidth;
            }

            int segmentAreaWidth = tWidth;
            if (tWidth > areaWidth) {
               segmentAreaWidth = areaWidth;
            }

            if (tWidth + x > 0 && x < zoneWidth) {
               this.renderText(tline, x, top + tline.getTextLineLeading(), segmentAreaWidth, useNativeDigits);
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

   public void setFont(Font font) {
      if (font == null) {
         throw new NullPointerException();
      } else {
         this.font = font;
      }
   }

   public void setClip(int x, int y, int width, int height) {
      int areaWidth = this.pixmapWidth;
      int areaHeight = this.pixmapHeight;
      if (x < areaWidth && y < areaHeight && x + width > 0 && y + height > 0 && width > 0 && height > 0) {
         if (x < 0) {
            width += x;
            x = 0;
         }

         if (x + width > areaWidth) {
            width = areaWidth - x;
         }

         if (y < 0) {
            height += y;
            y = 0;
         }

         if (y + height > areaHeight) {
            height = areaHeight - y;
         }

         this.clipX = x;
         this.clipY = y;
         this.clipWidth = width;
         this.clipHeight = height;
      } else {
         this.clipX = 0;
         this.clipY = 0;
         this.clipWidth = 0;
         this.clipHeight = 0;
      }
   }

   public void setLineWidth(int lineWidth) {
      this.lineWidth = lineWidth;
   }

   public void setRotation(int rotate) {
      if ((rotate & -32) <= 0 && ((rotate & 7) == 0 || (rotate & 7) == 1 || (rotate & 7) == 2 || (rotate & 7) == 4) && ((rotate & 24) == 0 || (rotate & 24) == 8 || (rotate & 24) == 16 || (rotate & 24) == 24)) {
         this.rotation = rotate;
      } else {
         throw new IllegalArgumentException("Rotation not recognized " + rotate);
      }
   }

   public void setRotOrigX(int rotOrigX) {
      this.rotOrigX = rotOrigX;
   }

   public void setRotOrigY(int rotOrigY) {
      this.rotOrigY = rotOrigY;
   }

   public void setTextTransparency(boolean txtTransparency) {
      this.txtTransparency = txtTransparency;
   }

   public native void refresh(int var1, int var2, int var3, int var4, int var5, int var6);

   private native void renderText(TextLine var1, int var2, int var3, int var4, boolean var5);

   public native void getPixels(byte[] var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);

   public native void getPixels(short[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   public native void getPixels(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   public native void drawPixels(byte[] var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   public native void drawPixels(short[] var1, boolean var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   public native void drawPixels(int[] var1, boolean var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   public native int getNativePixelFormat();

   public native void nativeDrawHighlight(int var1, int var2, int var3, int var4, int var5);

   public native void fillWithBackgroundImage();

   public native void fillWithFullTransparency();

   private native void nativeDrawChars(char[] var1, int var2, int var3, int var4, int var5, int var6, Font var7);

   public int resolveManipulation(int manipulation) {
      int gdiRotation = 0;
      int local_rotation = false;
      if ((manipulation & 8192) > 0) {
         gdiRotation |= 16;
      }

      if ((manipulation & 16384) > 0) {
         gdiRotation |= 8;
      }

      int local_rotation = (manipulation & 8191) % 360;
      switch(local_rotation) {
      case 0:
         return gdiRotation;
      case 90:
         return gdiRotation | 4;
      case 180:
         return gdiRotation | 2;
      case 270:
         return gdiRotation | 1;
      default:
         throw new IllegalArgumentException("Rotations in multiples of 90 only allowed" + local_rotation);
      }
   }

   static {
      nativeStaticInitialiser();
   }
}
