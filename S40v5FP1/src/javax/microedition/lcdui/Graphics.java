package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;

public class Graphics {
   public static final int DOTTED = 1;
   public static final int HCENTER = 1;
   public static final int LEFT = 4;
   public static final int RIGHT = 8;
   public static final int TOP = 16;
   public static final int BOTTOM = 32;
   public static final int BASELINE = 64;
   private static final int HORIZONTAL = 13;
   private static final int SVERTICAL = 112;
   public static final int SOLID = 0;
   public static final int VCENTER = 2;
   private static final int IVERTICAL = 50;
   private int clipx = 0;
   private int clipy = 0;
   private int clipw = 0;
   private int cliph = 0;
   int strokeStyle = 0;
   int translateX = 0;
   int translateY = 0;
   com.nokia.mid.impl.isa.ui.gdi.Graphics impl;
   private Font font = Font.getDefaultFont();

   Graphics(Image image) {
      if (image == null) {
         throw new IllegalStateException("Can not create Graphics without an Image");
      } else {
         this.impl = image.getPixmap().getGraphics();
         this.reset();
      }
   }

   Graphics(com.nokia.mid.impl.isa.ui.gdi.Graphics graphics) {
      this.impl = graphics;
      this.reset();
   }

   public void clipRect(int x, int y, int width, int height) {
      x += this.translateX;
      y += this.translateY;
      if (width > 0 && x > Integer.MAX_VALUE - width) {
         width = Integer.MAX_VALUE - x;
      }

      if (height > 0 && y > Integer.MAX_VALUE - height) {
         height = Integer.MAX_VALUE - y;
      }

      if (x < this.clipx + this.clipw && y < this.clipy + this.cliph && this.clipx < x + width && this.clipy < y + height && width > 0 && height > 0) {
         if (x > this.clipx) {
            this.clipw = (this.clipx + this.clipw > x + width ? x + width : this.clipx + this.clipw) - x;
            this.clipx = x;
         } else {
            this.clipw = (this.clipx + this.clipw > x + width ? x + width : this.clipx + this.clipw) - this.clipx;
         }

         if (y > this.clipy) {
            this.cliph = (this.clipy + this.cliph > y + height ? y + height : this.clipy + this.cliph) - y;
            this.clipy = y;
         } else {
            this.cliph = (this.clipy + this.cliph > y + height ? y + height : this.clipy + this.cliph) - this.clipy;
         }
      } else {
         this.clipx = 0;
         this.clipy = 0;
         this.clipw = 0;
         this.cliph = 0;
      }

      this.impl.setClip((short)this.clipx, (short)this.clipy, (short)this.clipw, (short)this.cliph);
   }

   public void copyArea(int srcX, int srcY, int width, int height, int destX, int destY, int anchor) {
      if (this.impl.getPixmap().isSystemDisplayPixmap()) {
         throw new IllegalStateException();
      } else {
         srcX += this.translateX;
         srcY += this.translateY;
         if (height >= 0 && width >= 0 && srcX >= 0 && srcY >= 0 && srcX + width <= this.impl.getWidth() && srcY + height <= this.impl.getHeight()) {
            if (anchor == 0) {
               anchor = 20;
            }

            if (!this.isValidImageAnchor(anchor)) {
               throw new IllegalArgumentException();
            } else {
               if (width > 0 && height > 0) {
                  this.impl.copyArea(srcX, srcY, width, height, this.resolveAnchorX(destX + this.translateX, anchor, width), this.resolveAnchorY(destY + this.translateY, anchor, height));
               }

            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      if (width >= 0 && height >= 0) {
         if (this.strokeStyle == 0) {
            this.impl.drawArc((short)(x + this.translateX), (short)(y + this.translateY), (short)(width + 1), (short)(height + 1), (short)startAngle, (short)arcAngle);
         } else {
            this.impl.drawDottedArc((short)(x + this.translateX), (short)(y + this.translateY), (short)(width + 1), (short)(height + 1), (short)startAngle, (short)arcAngle);
         }
      }

   }

   public void drawLine(int x1, int y1, int x2, int y2) {
      x1 += this.translateX;
      y1 += this.translateY;
      x2 += this.translateX;
      y2 += this.translateY;
      if (this.strokeStyle == 0) {
         this.impl.drawLine((short)x1, (short)y1, (short)x2, (short)y2);
      } else {
         this.impl.drawDottedLine((short)x1, (short)y1, (short)x2, (short)y2);
      }

   }

   public void drawRect(int x, int y, int width, int height) {
      if (width >= 0 && height >= 0) {
         if (this.strokeStyle == 0) {
            this.impl.drawRect((short)(x + this.translateX), (short)(y + this.translateY), (short)(width + 1), (short)(height + 1));
         } else {
            this.impl.drawDottedRect((short)(x + this.translateX), (short)(y + this.translateY), (short)(width + 1), (short)(height + 1));
         }
      }

   }

   public void drawRegion(Image src, int srcX, int srcY, int width, int height, int transform, int destX, int destY, int anchor) {
      if (src == null) {
         throw new NullPointerException();
      } else if (src.getPixmap() != this.impl.getPixmap() && height >= 0 && width >= 0 && srcX >= 0 && srcY >= 0 && srcX + width <= src.getWidth() && srcY + height <= src.getHeight() && transform >= 0 && transform <= 7) {
         if (anchor == 0) {
            anchor = 20;
         }

         if (!this.isValidImageAnchor(anchor)) {
            throw new IllegalArgumentException();
         } else {
            if (width > 0 && height > 0) {
               int transformW;
               int transformH;
               if (transform > 3) {
                  transformW = height;
                  transformH = width;
               } else {
                  transformW = width;
                  transformH = height;
               }

               this.impl.drawRegion(src.getPixmap(), srcX, srcY, width, height, transform, this.resolveAnchorX(destX + this.translateX, anchor, transformW), this.resolveAnchorY(destY + this.translateY, anchor, transformH));
            }

         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      if (width >= 0 && height >= 0) {
         if (this.strokeStyle == 0) {
            this.impl.drawRoundRect((short)(x + this.translateX), (short)(y + this.translateY), (short)(width + 1), (short)(height + 1), (short)arcWidth, (short)arcHeight);
         } else {
            this.impl.drawDottedRoundRect((short)(x + this.translateX), (short)(y + this.translateY), (short)(width + 1), (short)(height + 1), (short)arcWidth, (short)arcHeight);
         }
      }

   }

   public void drawImage(Image img, int x, int y, int anchor) {
      if (img == null) {
         throw new NullPointerException();
      } else {
         if (anchor == 0) {
            anchor = 20;
         }

         if (!this.isValidImageAnchor(anchor)) {
            throw new IllegalArgumentException("Graphics: Invalid anchor. Anchor is: " + anchor);
         } else {
            int width = img.getWidth();
            int height = img.getHeight();
            Pixmap pixmap = img.getPixmap();
            this.impl.drawPixmap(pixmap, (short)this.resolveAnchorX(x + this.translateX, anchor, width), (short)this.resolveAnchorY(y + this.translateY, anchor, height));
         }
      }
   }

   public void drawChar(char character, int x, int y, int anchor) {
      char[] cha = new char[]{character};
      this.impl.drawChars(cha, 0, 1, x + this.translateX, y + this.translateY, this.validateStringAnchor(anchor));
   }

   public void drawChars(char[] data, int offset, int length, int x, int y, int anchor) {
      if (data == null) {
         throw new NullPointerException();
      } else if (offset >= 0 && length >= 0 && offset + length <= data.length) {
         this.impl.drawChars(data, offset, length, x + this.translateX, y + this.translateY, this.validateStringAnchor(anchor));
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public void drawSubstring(String str, int offset, int len, int x, int y, int anchor) {
      if (str == null) {
         throw new NullPointerException();
      } else {
         int length = str.length();
         if (offset >= 0 && len >= 0 && offset + len <= length) {
            this.impl.drawChars(str.toCharArray(), offset, len, x + this.translateX, y + this.translateY, this.validateStringAnchor(anchor));
         } else {
            throw new StringIndexOutOfBoundsException();
         }
      }
   }

   public void drawString(String str, int x, int y, int anchor) {
      if (str == null) {
         throw new NullPointerException();
      } else {
         this.impl.drawChars(str.toCharArray(), 0, str.length(), x + this.translateX, y + this.translateY, this.validateStringAnchor(anchor));
      }
   }

   public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
      if (rgbData == null) {
         throw new NullPointerException();
      } else {
         int maxY = height - 1;
         int maxX = width - 1;
         if (scanlength < 0) {
            scanlength *= -1;
            offset -= maxY * scanlength;
         }

         if (height != 0 && width != 0) {
            if (height >= 0 && width >= 0 && (maxY <= 0 || Integer.MAX_VALUE / maxY >= scanlength) && offset >= 0 && offset + maxX + maxY * scanlength < rgbData.length) {
               x += this.translateX;
               y += this.translateY;
               this.impl.drawRGB(rgbData, offset, scanlength, x, y, width, height, processAlpha);
            } else {
               throw new ArrayIndexOutOfBoundsException();
            }
         }
      }
   }

   public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      if (width > 0 && height > 0) {
         x += this.translateX;
         y += this.translateY;
         this.impl.fillArc((short)x, (short)y, (short)width, (short)height, (short)startAngle, (short)arcAngle);
      }

   }

   public void fillRect(int x, int y, int width, int height) {
      if (width > 0 && height > 0) {
         x += this.translateX;
         y += this.translateY;
         this.impl.fillRect((short)x, (short)y, (short)width, (short)height);
      }

   }

   public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      if (width > 0 && height > 0) {
         x += this.translateX;
         y += this.translateY;
         this.impl.fillRoundRect((short)x, (short)y, (short)width, (short)height, (short)arcWidth, (short)arcHeight);
      }

   }

   public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
      this.impl.fillTriangle(x1 + this.translateX, y1 + this.translateY, x2 + this.translateX, y2 + this.translateY, x3 + this.translateX, y3 + this.translateY);
   }

   int getARGBColor() {
      return this.impl.getColorCtrl().getFgColor();
   }

   public int getClipHeight() {
      return this.cliph;
   }

   public int getClipWidth() {
      return this.clipw;
   }

   public int getClipX() {
      return this.clipx - this.translateX;
   }

   public int getClipY() {
      return this.clipy - this.translateY;
   }

   public int getColor() {
      return this.impl.getColorCtrl().getFgColor() & 16777215;
   }

   public int getDisplayColor(int color) {
      return this.impl.getDisplayColor(color);
   }

   public Font getFont() {
      return this.font;
   }

   public int getGrayScale() {
      int red = this.getRedComponent();
      int green = this.getGreenComponent();
      int blue = this.getBlueComponent();
      int nRetVal = blue;
      if (blue != green || blue != red) {
         nRetVal = red / 3 + green / 2 + blue * 17 / 100;
      }

      return nRetVal;
   }

   public int getGreenComponent() {
      return (this.impl.getColorCtrl().getFgColor() & '\uff00') >> 8;
   }

   public int getRedComponent() {
      return (this.impl.getColorCtrl().getFgColor() & 16711680) >> 16;
   }

   public int getBlueComponent() {
      return this.impl.getColorCtrl().getFgColor() & 255;
   }

   public int getStrokeStyle() {
      return this.strokeStyle;
   }

   public int getTranslateX() {
      return this.translateX;
   }

   public int getTranslateY() {
      return this.translateY;
   }

   public void setClip(int x, int y, int width, int height) {
      x += this.translateX;
      y += this.translateY;
      this.clipx = x;
      this.clipy = y;
      this.clipw = width;
      this.cliph = height;
      this.impl.setClip((short)this.clipx, (short)this.clipy, (short)this.clipw, (short)this.cliph);
   }

   public void setColor(int rgb) {
      int argb = -16777216 | rgb;
      this.impl.getColorCtrl().setFgColor(argb);
   }

   void setARGBColor(int argb) {
      this.impl.getColorCtrl().setFgColor(argb);
   }

   public void setColor(int red, int green, int blue) {
      if ((red & -256) == 0 && (green & -256) == 0 && (blue & -256) == 0) {
         this.setColor(red << 16 | green << 8 | blue);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setFont(Font font) {
      if (font == null) {
         this.font = Font.getDefaultFont();
      } else {
         this.font = font;
      }

      this.impl.setFont(this.font.getImpl());
   }

   public void setGrayScale(int value) {
      if ((value & -256) > 0) {
         throw new IllegalArgumentException();
      } else {
         this.setColor(value, value, value);
      }
   }

   public void setStrokeStyle(int style) {
      if (style != 1 && style != 0) {
         throw new IllegalArgumentException();
      } else {
         this.strokeStyle = style;
      }
   }

   public void translate(int x, int y) {
      this.translateX += x;
      this.translateY += y;
   }

   void reset() {
      this.reset(0, 0, this.impl.getWidth(), this.impl.getHeight());
   }

   void reset(int clipX, int clipY, int clipW, int clipH) {
      this.translateX = 0;
      this.translateY = 0;
      this.setClip(clipX, clipY, clipW, clipH);
      this.setColor(0);
      this.strokeStyle = 0;
      this.setFont((Font)null);
   }

   void clearClipArea(boolean useBackgroundImage, boolean transparentFill, boolean canvasWithTicker, Zone canvasMainZone) {
      if (useBackgroundImage) {
         if (this.impl.fillWithBackgroundImage()) {
            return;
         }
      } else {
         if (transparentFill) {
            this.impl.fillWithFullTransparency();
            return;
         }

         if (canvasWithTicker && this.impl.fillWithBackgroundImage()) {
            this.fillWithBackgroundColor(canvasMainZone.x, canvasMainZone.y, canvasMainZone.width, canvasMainZone.height);
            return;
         }
      }

      this.fillWithBackgroundColor(0, 0, this.impl.getWidth(), this.impl.getHeight());
   }

   int getWidth() {
      return this.impl.getWidth();
   }

   int getHeight() {
      return this.impl.getHeight();
   }

   void refresh(int x, int y, int width, int height, int translateX, int translateY) {
      this.impl.refresh(x, y, width, height, translateX, translateY);
   }

   com.nokia.mid.impl.isa.ui.gdi.Graphics getImpl() {
      return this.impl;
   }

   int validateStringAnchor(int anchor) {
      int retval = anchor;
      if (anchor == 0) {
         retval = 20;
      } else if ((anchor & -126) > 0 || (anchor & 13) != 4 && (anchor & 13) != 8 && (anchor & 13) != 1 || (anchor & 112) != 16 && (anchor & 112) != 32 && (anchor & 112) != 64) {
         throw new IllegalArgumentException("Graphics: Invalid anchor. Anchor is: " + anchor);
      }

      return retval;
   }

   boolean isValidImageAnchor(int anchor) {
      boolean retval = true;
      if ((anchor & -64) > 0 || (anchor & 13) != 4 && (anchor & 13) != 8 && (anchor & 13) != 1 || (anchor & 50) != 16 && (anchor & 50) != 32 && (anchor & 50) != 2) {
         retval = false;
      }

      return retval;
   }

   int resolveAnchorX(int x, int anchor, int width) {
      int nRetVal = false;
      int nRetVal;
      switch(anchor & 13) {
      case 1:
         nRetVal = x - width / 2;
         break;
      case 4:
         nRetVal = x;
         break;
      default:
         nRetVal = x - (width - 1);
      }

      return nRetVal;
   }

   int resolveAnchorY(int y, int anchor, int height) {
      int nRetVal = false;
      int nRetVal;
      switch(anchor & 114) {
      case 16:
         nRetVal = y;
         break;
      case 32:
      case 64:
         nRetVal = y - (height - 1);
         break;
      default:
         nRetVal = y - height / 2;
      }

      return nRetVal;
   }

   void setTextTransparency(boolean txtTransparency) {
      this.impl.setTextTransparency(txtTransparency);
   }

   private void fillWithBackgroundColor(int x, int y, int width, int height) {
      ColorCtrl colorCtrl = this.impl.getColorCtrl();
      int original_color = colorCtrl.getFgColor();
      colorCtrl.setFgColor(UIStyle.COLOUR_WHITE);
      this.impl.fillRect((short)x, (short)y, (short)width, (short)height);
      colorCtrl.setFgColor(original_color);
   }
}
