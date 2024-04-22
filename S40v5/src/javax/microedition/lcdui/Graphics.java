package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;

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
   private int cT = 0;
   private int cU = 0;
   private int cV = 0;
   private int cW = 0;
   int cX = 0;
   int cY = 0;
   int cZ = 0;
   com.nokia.mid.impl.isa.ui.gdi.Graphics da;
   private Font db = Font.getDefaultFont();

   Graphics(Image var1) {
      if (var1 == null) {
         throw new IllegalStateException("Can not create Graphics without an Image");
      } else {
         this.da = var1.getPixmap().getGraphics();
         this.reset();
      }
   }

   Graphics(com.nokia.mid.impl.isa.ui.gdi.Graphics var1) {
      this.da = var1;
      this.reset();
   }

   public void clipRect(int var1, int var2, int var3, int var4) {
      var1 += this.cY;
      var2 += this.cZ;
      if (var3 > 0 && var1 > Integer.MAX_VALUE - var3) {
         var3 = Integer.MAX_VALUE - var1;
      }

      if (var4 > 0 && var2 > Integer.MAX_VALUE - var4) {
         var4 = Integer.MAX_VALUE - var2;
      }

      if (var1 < this.cT + this.cV && var2 < this.cU + this.cW && this.cT < var1 + var3 && this.cU < var2 + var4 && var3 > 0 && var4 > 0) {
         if (var1 > this.cT) {
            this.cV = (this.cT + this.cV > var1 + var3 ? var1 + var3 : this.cT + this.cV) - var1;
            this.cT = var1;
         } else {
            this.cV = (this.cT + this.cV > var1 + var3 ? var1 + var3 : this.cT + this.cV) - this.cT;
         }

         if (var2 > this.cU) {
            this.cW = (this.cU + this.cW > var2 + var4 ? var2 + var4 : this.cU + this.cW) - var2;
            this.cU = var2;
         } else {
            this.cW = (this.cU + this.cW > var2 + var4 ? var2 + var4 : this.cU + this.cW) - this.cU;
         }
      } else {
         this.cT = 0;
         this.cU = 0;
         this.cV = 0;
         this.cW = 0;
      }

      this.da.setClip((short)this.cT, (short)this.cU, (short)this.cV, (short)this.cW);
   }

   public void copyArea(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (this.da.getPixmap().isSystemDisplayPixmap()) {
         throw new IllegalStateException();
      } else {
         var1 += this.cY;
         var2 += this.cZ;
         if (var4 >= 0 && var3 >= 0 && var1 >= 0 && var2 >= 0 && var1 + var3 <= this.da.getWidth() && var2 + var4 <= this.da.getHeight()) {
            if (var7 == 0) {
               var7 = 20;
            }

            if (!p(var7)) {
               throw new IllegalArgumentException();
            } else {
               if (var3 > 0 && var4 > 0) {
                  this.da.copyArea(var1, var2, var3, var4, a(var5 + this.cY, var7, var3), b(var6 + this.cZ, var7, var4));
               }

            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public void drawArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 >= 0 && var4 >= 0) {
         if (this.cX == 0) {
            this.da.drawArc((short)(var1 + this.cY), (short)(var2 + this.cZ), (short)(var3 + 1), (short)(var4 + 1), (short)var5, (short)var6);
            return;
         }

         this.da.drawDottedArc((short)(var1 + this.cY), (short)(var2 + this.cZ), (short)(var3 + 1), (short)(var4 + 1), (short)var5, (short)var6);
      }

   }

   public void drawLine(int var1, int var2, int var3, int var4) {
      var1 += this.cY;
      var2 += this.cZ;
      var3 += this.cY;
      var4 += this.cZ;
      if (this.cX == 0) {
         this.da.drawLine((short)var1, (short)var2, (short)var3, (short)var4);
      } else {
         this.da.drawDottedLine((short)var1, (short)var2, (short)var3, (short)var4);
      }
   }

   public void drawRect(int var1, int var2, int var3, int var4) {
      if (var3 >= 0 && var4 >= 0) {
         if (this.cX == 0) {
            this.da.drawRect((short)(var1 + this.cY), (short)(var2 + this.cZ), (short)(var3 + 1), (short)(var4 + 1));
            return;
         }

         this.da.drawDottedRect((short)(var1 + this.cY), (short)(var2 + this.cZ), (short)(var3 + 1), (short)(var4 + 1));
      }

   }

   public void drawRegion(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1.getPixmap() != this.da.getPixmap() && var5 >= 0 && var4 >= 0 && var2 >= 0 && var3 >= 0 && var2 + var4 <= var1.getWidth() && var3 + var5 <= var1.getHeight() && var6 >= 0 && var6 <= 7) {
         if (var9 == 0) {
            var9 = 20;
         }

         if (!p(var9)) {
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

               this.da.drawRegion(var1.getPixmap(), var2, var3, var4, var5, var6, a(var7 + this.cY, var9, var10), b(var8 + this.cZ, var9, var11));
            }

         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void drawRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 >= 0 && var4 >= 0) {
         if (this.cX == 0) {
            this.da.drawRoundRect((short)(var1 + this.cY), (short)(var2 + this.cZ), (short)(var3 + 1), (short)(var4 + 1), (short)var5, (short)var6);
            return;
         }

         this.da.drawDottedRoundRect((short)(var1 + this.cY), (short)(var2 + this.cZ), (short)(var3 + 1), (short)(var4 + 1), (short)var5, (short)var6);
      }

   }

   public void drawImage(Image var1, int var2, int var3, int var4) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (var4 == 0) {
            var4 = 20;
         }

         if (!p(var4)) {
            throw new IllegalArgumentException("Graphics: Invalid anchor. Anchor is: " + var4);
         } else {
            int var5 = var1.getWidth();
            int var6 = var1.getHeight();
            Pixmap var7 = var1.getPixmap();
            this.da.drawPixmap(var7, (short)a(var2 + this.cY, var4, var5), (short)b(var3 + this.cZ, var4, var6));
         }
      }
   }

   public void drawChar(char var1, int var2, int var3, int var4) {
      char[] var5 = new char[]{var1};
      this.drawChars(var5, 0, 1, var2, var3, var4);
   }

   public void drawChars(char[] var1, int var2, int var3, int var4, int var5, int var6) {
      String var7 = null;
      var7 = null;
      boolean var8 = false;
      boolean var9 = false;
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (var6 == 0) {
            var6 = 20;
         }

         var8 = true;
         if ((var6 & -126) > 0 || (var6 & 13) != 4 && (var6 & 13) != 8 && (var6 & 13) != 1 || (var6 & 112) != 16 && (var6 & 112) != 32 && (var6 & 112) != 64) {
            var8 = false;
         }

         if (!var8) {
            throw new IllegalArgumentException("Graphics: Invalid anchor. Anchor is: " + var6);
         } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
            if (var1.length != 0) {
               var7 = String.valueOf(var1, var2, var3);
               var7 = this.db.getImpl().getStringWithCompatibleFont(var7);
               TextLine var10;
               if ((var10 = TextBreaker.breakOneLineTextInArea(32767, -1, this.db.getImpl(), var7, 0, true, true)) != null) {
                  int var11 = var10.getTextLineWidth();
                  int var12;
                  if ((var6 & 112) == 32) {
                     var12 = var10.getTextLineHeight();
                  } else {
                     var12 = var10.getTextLineBase();
                  }

                  this.da.drawText(var10, (short)a(var4 + this.cY, var6, var11), (short)b(var5 + this.cZ, var6, var12));
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
            var3 = -var3;
            var2 -= var9 * var3;
         }

         if (var7 != 0 && var6 != 0) {
            if (var7 >= 0 && var6 >= 0 && (var9 <= 0 || Integer.MAX_VALUE / var9 >= var3) && var2 >= 0 && var2 + var10 + var9 * var3 < var1.length) {
               var4 += this.cY;
               var5 += this.cZ;
               this.da.drawRGB(var1, var2, var3, var4, var5, var6, var7, var8);
            } else {
               throw new ArrayIndexOutOfBoundsException();
            }
         }
      }
   }

   public void fillArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 > 0 && var4 > 0) {
         var1 += this.cY;
         var2 += this.cZ;
         this.da.fillArc((short)var1, (short)var2, (short)var3, (short)var4, (short)var5, (short)var6);
      }

   }

   public void fillRect(int var1, int var2, int var3, int var4) {
      if (var3 > 0 && var4 > 0) {
         var1 += this.cY;
         var2 += this.cZ;
         this.da.fillRect((short)var1, (short)var2, (short)var3, (short)var4);
      }

   }

   public void fillRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 > 0 && var4 > 0) {
         var1 += this.cY;
         var2 += this.cZ;
         this.da.fillRoundRect((short)var1, (short)var2, (short)var3, (short)var4, (short)var5, (short)var6);
      }

   }

   public void fillTriangle(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.da.fillTriangle(var1 + this.cY, var2 + this.cZ, var3 + this.cY, var4 + this.cZ, var5 + this.cY, var6 + this.cZ);
   }

   int getARGBColor() {
      return this.da.getColorCtrl().getFgColor();
   }

   public int getClipHeight() {
      return this.cW;
   }

   public int getClipWidth() {
      return this.cV;
   }

   public int getClipX() {
      return this.cT - this.cY;
   }

   public int getClipY() {
      return this.cU - this.cZ;
   }

   public int getColor() {
      return this.da.getColorCtrl().getFgColor() & 16777215;
   }

   public int getDisplayColor(int var1) {
      return this.da.getDisplayColor(var1);
   }

   public Font getFont() {
      return this.db;
   }

   public int getGrayScale() {
      int var1 = this.getRedComponent();
      int var2 = this.getGreenComponent();
      int var4;
      int var3 = var4 = this.getBlueComponent();
      if (var4 != var2 || var4 != var1) {
         var3 = var1 / 3 + var2 / 2 + var4 * 17 / 100;
      }

      return var3;
   }

   public int getGreenComponent() {
      return (this.da.getColorCtrl().getFgColor() & '\uff00') >> 8;
   }

   public int getRedComponent() {
      return (this.da.getColorCtrl().getFgColor() & 16711680) >> 16;
   }

   public int getBlueComponent() {
      return this.da.getColorCtrl().getFgColor() & 255;
   }

   public int getStrokeStyle() {
      return this.cX;
   }

   public int getTranslateX() {
      return this.cY;
   }

   public int getTranslateY() {
      return this.cZ;
   }

   public void setClip(int var1, int var2, int var3, int var4) {
      var1 += this.cY;
      var2 += this.cZ;
      this.cT = var1;
      this.cU = var2;
      this.cV = var3;
      this.cW = var4;
      this.da.setClip((short)this.cT, (short)this.cU, (short)this.cV, (short)this.cW);
   }

   public void setColor(int var1) {
      var1 |= -16777216;
      this.da.getColorCtrl().setFgColor(var1);
   }

   void setARGBColor(int var1) {
      this.da.getColorCtrl().setFgColor(var1);
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
         this.db = Font.getDefaultFont();
      } else {
         this.db = var1;
      }

      this.da.setFont(this.db.getImpl());
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
         this.cX = var1;
      }
   }

   public void translate(int var1, int var2) {
      this.cY += var1;
      this.cZ += var2;
   }

   final void reset() {
      this.a(0, 0, this.da.getWidth(), this.da.getHeight());
   }

   final void a(int var1, int var2, int var3, int var4) {
      this.cY = 0;
      this.cZ = 0;
      this.setClip(var1, var2, var3, var4);
      this.setColor(0);
      this.cX = 0;
      this.setFont((Font)null);
   }

   final void a(boolean var1, boolean var2, boolean var3, Zone var4) {
      if (var1) {
         if (this.da.fillWithBackgroundImage()) {
            return;
         }
      } else {
         if (var2) {
            this.da.fillWithFullTransparency();
            return;
         }

         if (var3 && this.da.fillWithBackgroundImage()) {
            this.b(var4.x, var4.y, var4.width, var4.height);
            return;
         }
      }

      this.b(0, 0, this.da.getWidth(), this.da.getHeight());
   }

   int getWidth() {
      return this.da.getWidth();
   }

   int getHeight() {
      return this.da.getHeight();
   }

   final void refresh(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.da.refresh(var1, var2, var3, var4, 0, var6);
   }

   com.nokia.mid.impl.isa.ui.gdi.Graphics getImpl() {
      return this.da;
   }

   static boolean p(int var0) {
      boolean var1 = true;
      if ((var0 & -64) > 0 || (var0 & 13) != 4 && (var0 & 13) != 8 && (var0 & 13) != 1 || (var0 & 50) != 16 && (var0 & 50) != 32 && (var0 & 50) != 2) {
         var1 = false;
      }

      return var1;
   }

   static int a(int var0, int var1, int var2) {
      boolean var3 = false;
      int var4;
      switch(var1 & 13) {
      case 1:
         var4 = var0 - var2 / 2;
         break;
      case 4:
         var4 = var0;
         break;
      default:
         var4 = var0 - (var2 - 1);
      }

      return var4;
   }

   static int b(int var0, int var1, int var2) {
      boolean var3 = false;
      int var4;
      switch(var1 & 114) {
      case 16:
         var4 = var0;
         break;
      case 32:
      case 64:
         var4 = var0 - (var2 - 1);
         break;
      default:
         var4 = var0 - var2 / 2;
      }

      return var4;
   }

   void setTextTransparency(boolean var1) {
      this.da.setTextTransparency(var1);
   }

   private void b(int var1, int var2, int var3, int var4) {
      ColorCtrl var5;
      int var6 = (var5 = this.da.getColorCtrl()).getFgColor();
      var5.setFgColor(UIStyle.COLOUR_WHITE);
      this.da.fillRect((short)var1, (short)var2, (short)var3, (short)var4);
      var5.setFgColor(var6);
   }
}
