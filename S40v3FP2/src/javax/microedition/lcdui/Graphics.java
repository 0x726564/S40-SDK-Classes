package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;

public class Graphics {
   public static final int BASELINE = 64;
   public static final int BOTTOM = 32;
   public static final int DOTTED = 1;
   public static final int HCENTER = 1;
   public static final int LEFT = 4;
   public static final int RIGHT = 8;
   public static final int SOLID = 0;
   public static final int TOP = 16;
   public static final int VCENTER = 2;
   private static final int HORIZONTAL = 13;
   private static final int SVERTICAL = 112;
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

   Graphics(Image var1) {
      if (var1 == null) {
         throw new IllegalStateException("Can not create Graphics without an Image");
      } else {
         this.impl = var1.getPixmap().getGraphics();
         this.reset();
      }
   }

   Graphics(com.nokia.mid.impl.isa.ui.gdi.Graphics var1) {
      this.impl = var1;
      this.reset();
   }

   public void clipRect(int var1, int var2, int var3, int var4) {
      var1 += this.translateX;
      var2 += this.translateY;
      if (var3 > 0 && var1 > Integer.MAX_VALUE - var3) {
         var3 = Integer.MAX_VALUE - var1;
      }

      if (var4 > 0 && var2 > Integer.MAX_VALUE - var4) {
         var4 = Integer.MAX_VALUE - var2;
      }

      if (var1 < this.clipx + this.clipw && var2 < this.clipy + this.cliph && this.clipx < var1 + var3 && this.clipy < var2 + var4 && var3 > 0 && var4 > 0) {
         if (var1 > this.clipx) {
            this.clipw = (this.clipx + this.clipw > var1 + var3 ? var1 + var3 : this.clipx + this.clipw) - var1;
            this.clipx = var1;
         } else {
            this.clipw = (this.clipx + this.clipw > var1 + var3 ? var1 + var3 : this.clipx + this.clipw) - this.clipx;
         }

         if (var2 > this.clipy) {
            this.cliph = (this.clipy + this.cliph > var2 + var4 ? var2 + var4 : this.clipy + this.cliph) - var2;
            this.clipy = var2;
         } else {
            this.cliph = (this.clipy + this.cliph > var2 + var4 ? var2 + var4 : this.clipy + this.cliph) - this.clipy;
         }
      } else {
         this.clipx = 0;
         this.clipy = 0;
         this.clipw = 0;
         this.cliph = 0;
      }

      this.impl.setClip((short)this.clipx, (short)this.clipy, (short)this.clipw, (short)this.cliph);
   }

   public void copyArea(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (this.impl.getPixmap().isSystemDisplayPixmap()) {
         throw new IllegalStateException();
      } else {
         var1 += this.translateX;
         var2 += this.translateY;
         if (var4 >= 0 && var3 >= 0 && var1 >= 0 && var2 >= 0 && var1 + var3 <= this.impl.getWidth() && var2 + var4 <= this.impl.getHeight()) {
            if (var7 == 0) {
               var7 = 20;
            }

            if (!this.isValidImageAnchor(var7)) {
               throw new IllegalArgumentException();
            } else {
               if (var3 > 0 && var4 > 0) {
                  this.impl.copyArea(var1, var2, var3, var4, this.resolveAnchorX(var5 + this.translateX, var7, var3), this.resolveAnchorY(var6 + this.translateY, var7, var4));
               }

            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public void drawArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 >= 0 && var4 >= 0) {
         if (this.strokeStyle == 0) {
            this.impl.drawArc((short)(var1 + this.translateX), (short)(var2 + this.translateY), (short)(var3 + 1), (short)(var4 + 1), (short)var5, (short)var6);
         } else {
            this.impl.drawDottedArc((short)(var1 + this.translateX), (short)(var2 + this.translateY), (short)(var3 + 1), (short)(var4 + 1), (short)var5, (short)var6);
         }
      }

   }

   public void drawLine(int var1, int var2, int var3, int var4) {
      var1 += this.translateX;
      var2 += this.translateY;
      var3 += this.translateX;
      var4 += this.translateY;
      if (this.strokeStyle == 0) {
         this.impl.drawLine((short)var1, (short)var2, (short)var3, (short)var4);
      } else {
         this.impl.drawDottedLine((short)var1, (short)var2, (short)var3, (short)var4);
      }

   }

   public void drawRect(int var1, int var2, int var3, int var4) {
      if (var3 >= 0 && var4 >= 0) {
         if (this.strokeStyle == 0) {
            this.impl.drawRect((short)(var1 + this.translateX), (short)(var2 + this.translateY), (short)(var3 + 1), (short)(var4 + 1));
         } else {
            this.impl.drawDottedRect((short)(var1 + this.translateX), (short)(var2 + this.translateY), (short)(var3 + 1), (short)(var4 + 1));
         }
      }

   }

   public void drawRegion(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1.getPixmap() != this.impl.getPixmap() && var5 >= 0 && var4 >= 0 && var2 >= 0 && var3 >= 0 && var2 + var4 <= var1.getWidth() && var3 + var5 <= var1.getHeight() && var6 >= 0 && var6 <= 7) {
         if (var9 == 0) {
            var9 = 20;
         }

         if (!this.isValidImageAnchor(var9)) {
            throw new IllegalArgumentException();
         } else {
            if (var4 > 0 && var5 > 0) {
               int var10;
               int var11;
               if (var6 > 3) {
                  var10 = var5;
                  var11 = var4;
               } else {
                  var10 = var4;
                  var11 = var5;
               }

               this.impl.drawRegion(var1.getPixmap(), var2, var3, var4, var5, var6, this.resolveAnchorX(var7 + this.translateX, var9, var10), this.resolveAnchorY(var8 + this.translateY, var9, var11));
            }

         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void drawRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 >= 0 && var4 >= 0) {
         if (this.strokeStyle == 0) {
            this.impl.drawRoundRect((short)(var1 + this.translateX), (short)(var2 + this.translateY), (short)(var3 + 1), (short)(var4 + 1), (short)var5, (short)var6);
         } else {
            this.impl.drawDottedRoundRect((short)(var1 + this.translateX), (short)(var2 + this.translateY), (short)(var3 + 1), (short)(var4 + 1), (short)var5, (short)var6);
         }
      }

   }

   public void drawImage(Image var1, int var2, int var3, int var4) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (var4 == 0) {
            var4 = 20;
         }

         if (!this.isValidImageAnchor(var4)) {
            throw new IllegalArgumentException("Graphics: Invalid anchor. Anchor is: " + var4);
         } else {
            int var5 = var1.getWidth();
            int var6 = var1.getHeight();
            Pixmap var7 = var1.getPixmap();
            this.impl.drawPixmap(var7, (short)this.resolveAnchorX(var2 + this.translateX, var4, var5), (short)this.resolveAnchorY(var3 + this.translateY, var4, var6));
         }
      }
   }

   public void drawChar(char var1, int var2, int var3, int var4) {
      char[] var5 = new char[]{var1};
      this.drawChars(var5, 0, 1, var2, var3, var4);
   }

   public void drawChars(char[] var1, int var2, int var3, int var4, int var5, int var6) {
      TextLine var7 = null;
      String var8 = null;
      boolean var9 = false;
      boolean var10 = false;
      boolean var11 = false;
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (var6 == 0) {
            var6 = 20;
         }

         if (!this.isValidStringAnchor(var6)) {
            throw new IllegalArgumentException("Graphics: Invalid anchor. Anchor is: " + var6);
         } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
            if (var1.length != 0) {
               var8 = String.valueOf(var1, var2, var3);
               var8 = this.font.getImpl().getStringWithCompatibleFont(var8);
               var7 = TextBreaker.breakOneLineTextInArea(32767, -1, this.font.getImpl(), var8, 0, true, true);
               if (var7 != null) {
                  int var12 = var7.getTextLineWidth();
                  int var13;
                  if ((var6 & 112) == 32) {
                     var13 = var7.getTextLineHeight();
                  } else {
                     var13 = var7.getTextLineBase();
                  }

                  this.impl.drawText(var7, (short)this.resolveAnchorX(var4 + this.translateX, var6, var12), (short)this.resolveAnchorY(var5 + this.translateY, var6, var13));
               }
            }

         } else {
            throw new ArrayIndexOutOfBoundsException();
         }
      }
   }

   public void drawSubstring(String var1, int var2, int var3, int var4, int var5, int var6) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int var7 = var1.length();
         if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var7) {
            this.drawChars(var1.toCharArray(), var2, var3, var4, var5, var6);
         } else {
            throw new StringIndexOutOfBoundsException();
         }
      }
   }

   public void drawString(String var1, int var2, int var3, int var4) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.drawChars(var1.toCharArray(), 0, var1.length(), var2, var3, var4);
      }
   }

   public void drawRGB(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int var9 = var7 - 1;
         int var10 = var6 - 1;
         if (var3 < 0) {
            var3 *= -1;
            var2 -= var9 * var3;
         }

         if (var7 != 0 && var6 != 0) {
            if (var7 >= 0 && var6 >= 0 && (var9 <= 0 || Integer.MAX_VALUE / var9 >= var3) && var2 >= 0 && var2 + var10 + var9 * var3 < var1.length) {
               var4 += this.translateX;
               var5 += this.translateY;
               this.impl.drawRGB(var1, var2, var3, var4, var5, var6, var7, var8);
            } else {
               throw new ArrayIndexOutOfBoundsException();
            }
         }
      }
   }

   public void fillArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 > 0 && var4 > 0) {
         var1 += this.translateX;
         var2 += this.translateY;
         this.impl.fillArc((short)var1, (short)var2, (short)var3, (short)var4, (short)var5, (short)var6);
      }

   }

   public void fillRect(int var1, int var2, int var3, int var4) {
      if (var3 > 0 && var4 > 0) {
         var1 += this.translateX;
         var2 += this.translateY;
         this.impl.fillRect((short)var1, (short)var2, (short)var3, (short)var4);
      }

   }

   public void fillRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 > 0 && var4 > 0) {
         var1 += this.translateX;
         var2 += this.translateY;
         this.impl.fillRoundRect((short)var1, (short)var2, (short)var3, (short)var4, (short)var5, (short)var6);
      }

   }

   public void fillTriangle(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.impl.fillTriangle(var1 + this.translateX, var2 + this.translateY, var3 + this.translateX, var4 + this.translateY, var5 + this.translateX, var6 + this.translateY);
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

   public int getDisplayColor(int var1) {
      return this.impl.getDisplayColor(var1);
   }

   public Font getFont() {
      return this.font;
   }

   public int getGrayScale() {
      int var1 = this.getRedComponent();
      int var2 = this.getGreenComponent();
      int var3 = this.getBlueComponent();
      int var4 = var3;
      if (var3 != var2 || var3 != var1) {
         var4 = var1 / 3 + var2 / 2 + var3 * 17 / 100;
      }

      return var4;
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

   public void setClip(int var1, int var2, int var3, int var4) {
      var1 += this.translateX;
      var2 += this.translateY;
      this.clipx = var1;
      this.clipy = var2;
      this.clipw = var3;
      this.cliph = var4;
      this.impl.setClip((short)this.clipx, (short)this.clipy, (short)this.clipw, (short)this.cliph);
   }

   public void setColor(int var1) {
      int var2 = -16777216 | var1;
      this.impl.getColorCtrl().setFgColor(var2);
   }

   void setARGBColor(int var1) {
      this.impl.getColorCtrl().setFgColor(var1);
   }

   public void setColor(int var1, int var2, int var3) {
      if ((var1 & -256) == 0 && (var2 & -256) == 0 && (var3 & -256) == 0) {
         this.setColor(var1 << 16 | var2 << 8 | var3);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setFont(Font var1) {
      if (var1 == null) {
         this.font = Font.getDefaultFont();
      } else {
         this.font = var1;
      }

      this.impl.setFont(this.font.getImpl());
   }

   public void setGrayScale(int var1) {
      if ((var1 & -256) > 0) {
         throw new IllegalArgumentException();
      } else {
         this.setColor(var1, var1, var1);
      }
   }

   public void setStrokeStyle(int var1) {
      if (var1 != 1 && var1 != 0) {
         throw new IllegalArgumentException();
      } else {
         this.strokeStyle = var1;
      }
   }

   public void translate(int var1, int var2) {
      this.translateX += var1;
      this.translateY += var2;
   }

   void reset() {
      this.reset(0, 0, this.impl.getWidth(), this.impl.getHeight());
   }

   void reset(int var1, int var2, int var3, int var4) {
      this.translateX = 0;
      this.translateY = 0;
      this.setClip(var1, var2, var3, var4);
      this.setColor(0);
      this.strokeStyle = 0;
      this.setFont((Font)null);
   }

   void clearScreen(boolean var1, boolean var2) {
      if (!var1 || !this.impl.fillWithBackgroundImage()) {
         if (var2) {
            this.impl.fillWithFullTransparency();
         } else {
            ColorCtrl var3 = this.impl.getColorCtrl();
            int var4 = var3.getFgColor();
            var3.setFgColor(UIStyle.COLOUR_WHITE);
            this.impl.fillRect(0, 0, (short)this.impl.getWidth(), (short)this.impl.getHeight());
            var3.setFgColor(var4);
         }

      }
   }

   int getWidth() {
      return this.impl.getWidth();
   }

   int getHeight() {
      return this.impl.getHeight();
   }

   void refresh(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.impl.refresh(var1, var2, var3, var4, var5, var6);
   }

   com.nokia.mid.impl.isa.ui.gdi.Graphics getImpl() {
      return this.impl;
   }

   boolean isValidStringAnchor(int var1) {
      boolean var2 = true;
      if ((var1 & -126) > 0 || (var1 & 13) != 4 && (var1 & 13) != 8 && (var1 & 13) != 1 || (var1 & 112) != 16 && (var1 & 112) != 32 && (var1 & 112) != 64) {
         var2 = false;
      }

      return var2;
   }

   boolean isValidImageAnchor(int var1) {
      boolean var2 = true;
      if ((var1 & -64) > 0 || (var1 & 13) != 4 && (var1 & 13) != 8 && (var1 & 13) != 1 || (var1 & 50) != 16 && (var1 & 50) != 32 && (var1 & 50) != 2) {
         var2 = false;
      }

      return var2;
   }

   int resolveAnchorX(int var1, int var2, int var3) {
      boolean var4 = false;
      int var5;
      switch(var2 & 13) {
      case 1:
         var5 = var1 - var3 / 2;
         break;
      case 4:
         var5 = var1;
         break;
      default:
         var5 = var1 - (var3 - 1);
      }

      return var5;
   }

   int resolveAnchorY(int var1, int var2, int var3) {
      boolean var4 = false;
      int var5;
      switch(var2 & 114) {
      case 16:
         var5 = var1;
         break;
      case 32:
      case 64:
         var5 = var1 - (var3 - 1);
         break;
      default:
         var5 = var1 - var3 / 2;
      }

      return var5;
   }

   void setTextTransparency(boolean var1) {
      this.impl.setTextTransparency(var1);
   }
}
