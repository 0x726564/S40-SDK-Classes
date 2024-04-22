package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.ui.DirectGraphics;

class DirectGraphicsImpl extends Graphics implements DirectGraphics {
   DirectGraphicsImpl(Image var1) {
      super(var1);
   }

   DirectGraphicsImpl(com.nokia.mid.impl.isa.ui.gdi.Graphics var1) {
      super(var1);
   }

   public int getAlphaComponent() {
      return (this.impl.getColorCtrl().getFgColor() & -16777216) >>> 24;
   }

   public void setARGBColor(int var1) {
      this.impl.getColorCtrl().setFgColor(var1);
   }

   public void drawImage(Image var1, int var2, int var3, int var4, int var5) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (var4 == 0) {
            var4 = 20;
         }

         if (!this.isValidImageAnchor(var4)) {
            throw new IllegalArgumentException("Graphics: Invalid anchor. Anchor is: " + var4);
         } else {
            int var6 = var1.getWidth();
            int var7 = var1.getHeight();
            Pixmap var11 = var1.getPixmap();
            com.nokia.mid.impl.isa.ui.gdi.Graphics var12 = this.getImpl();
            int var8 = var12.getRotOrigX();
            int var9 = var12.getRotOrigY();
            int var10 = var12.getRotation();
            var12.setRotOrigX(0);
            var12.setRotOrigY(0);
            var12.setRotation(var12.resolveManipulation(var5));
            int var13 = var6;
            int var14 = var7;
            if ((var12.getRotation() & 5) != 0) {
               var13 = var7;
               var14 = var6;
            }

            var12.drawPixmap(var11, this.resolveAnchorX(var2 + this.getTranslateX(), var4, var13), this.resolveAnchorY(var3 + this.getTranslateY(), var4, var14));
            var12.setRotOrigX(var8);
            var12.setRotOrigY(var9);
            var12.setRotation(var10);
         }
      }
   }

   public void drawTriangle(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      ColorCtrl var8 = this.impl.getColorCtrl();
      int var9 = var8.getFgColor();
      var8.setFgColor(var7);
      if (this.strokeStyle == 0) {
         this.getImpl().drawTriangle((short)(var1 + this.translateX), (short)(var2 + this.translateY), (short)(var3 + this.translateX), (short)(var4 + this.translateY), (short)(var5 + this.translateX), (short)(var6 + this.translateY));
      } else {
         short[] var10 = new short[]{(short)(var1 + this.translateX), (short)(var3 + this.translateX), (short)(var5 + this.translateX)};
         short[] var11 = new short[]{(short)(var2 + this.translateY), (short)(var4 + this.translateY), (short)(var6 + this.translateY)};
         this.getImpl().drawDottedPoly(var10, var11, 3, true);
      }

      var8.setFgColor(var9);
   }

   public void fillTriangle(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      ColorCtrl var8 = this.impl.getColorCtrl();
      int var9 = var8.getFgColor();
      var8.setFgColor(var7);
      this.getImpl().fillTriangle((short)(var1 + this.translateX), (short)(var2 + this.translateY), (short)(var3 + this.translateX), (short)(var4 + this.translateY), (short)(var5 + this.translateX), (short)(var6 + this.translateY));
      var8.setFgColor(var9);
   }

   public void drawPolygon(int[] var1, int var2, int[] var3, int var4, int var5, int var6) {
      this.checkPolyParameters(var1, var2, var3, var4, var5, var6);
      ColorCtrl var7 = this.impl.getColorCtrl();
      int var8 = var7.getFgColor();
      var7.setFgColor(var6);
      short[] var9 = new short[var5];
      short[] var10 = new short[var5];

      for(int var11 = 0; var11 < var5; ++var11) {
         var9[var11] = (short)var1[var2 + var11];
         var10[var11] = (short)var3[var4 + var11];
      }

      if (this.translateX != 0 || this.translateY != 0) {
         this.translateCoords(var9, var10, var5);
      }

      switch(var5) {
      case 0:
         break;
      case 1:
         this.getImpl().drawPixel(var9[0], var10[0]);
         break;
      case 2:
         if (this.strokeStyle == 0) {
            this.getImpl().s_drawLine(var9[0], var10[0], var9[1], var10[1]);
         } else {
            this.getImpl().drawDottedLine(var9[0], var10[0], var9[1], var10[1]);
         }
         break;
      default:
         if (this.strokeStyle == 0) {
            this.getImpl().drawPoly(var9, var10, var5, true);
         } else {
            this.getImpl().drawDottedPoly(var9, var10, var5, true);
         }
      }

      var7.setFgColor(var8);
   }

   public void fillPolygon(int[] var1, int var2, int[] var3, int var4, int var5, int var6) {
      this.checkPolyParameters(var1, var2, var3, var4, var5, var6);
      ColorCtrl var7 = this.impl.getColorCtrl();
      int var8 = var7.getFgColor();
      var7.setFgColor(var6);
      short[] var9 = new short[var5];
      short[] var10 = new short[var5];

      for(int var11 = 0; var11 < var5; ++var11) {
         var9[var11] = (short)var1[var2 + var11];
         var10[var11] = (short)var3[var4 + var11];
      }

      if (this.translateX != 0 || this.translateY != 0) {
         this.translateCoords(var9, var10, var5);
      }

      switch(var5) {
      case 0:
         break;
      case 1:
         this.getImpl().drawPixel(var9[0], var10[0]);
         break;
      case 2:
         this.getImpl().s_drawLine(var9[0], var10[0], var9[1], var10[1]);
         break;
      default:
         this.getImpl().fillPoly(var9, var10, var5);
      }

      var7.setFgColor(var8);
   }

   public void getPixels(byte[] var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      this.impl.getPixels(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void getPixels(short[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      boolean var9 = false;
      if (var1 == null) {
         throw new NullPointerException("Short array is null");
      } else if (var4 >= 0 && var5 >= 0) {
         if (var6 >= 0 && var7 >= 0) {
            if (var2 >= 0 && var3 >= 0 && var2 % var3 + var6 <= var3) {
               int var10 = var2 + (var7 - 1) * var3 + var6;
               if (var1.length < var10) {
                  throw new ArrayIndexOutOfBoundsException("Not enough pixels in pixel array");
               } else {
                  this.impl.getPixels(var1, var2, var3, var4 + this.translateX, var5 + this.translateY, var6, var7, var8);
               }
            } else {
               throw new ArrayIndexOutOfBoundsException("Illegal offset | scanlength");
            }
         } else {
            throw new IllegalArgumentException("Negative width/height || width outside scan area");
         }
      } else {
         throw new IllegalArgumentException("Negative x/y");
      }
   }

   public void getPixels(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      boolean var9 = false;
      if (var1 == null) {
         throw new NullPointerException("Int array is null");
      } else if (var4 >= 0 && var5 >= 0) {
         if (var6 >= 0 && var7 >= 0) {
            if (var2 >= 0 && var3 >= 0 && var2 % var3 + var6 <= var3) {
               int var10 = var2 + (var7 - 1) * var3 + var6;
               if (var1.length < var10) {
                  throw new ArrayIndexOutOfBoundsException("Not enough pixels in pixel array");
               } else {
                  this.impl.getPixels(var1, var2, var3, var4, var5, var6, var7, var8);
               }
            } else {
               throw new ArrayIndexOutOfBoundsException("Illegal offset | scanlength");
            }
         } else {
            throw new IllegalArgumentException("Negative width/height || width outside scan area");
         }
      } else {
         throw new IllegalArgumentException("Negative x/y");
      }
   }

   public void drawPixels(byte[] var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      this.impl.drawPixels(var1, var2, var3, var4, var5 + this.translateX, var6 + this.translateY, var7, var8, var9, var10);
   }

   public void drawPixels(short[] var1, boolean var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      boolean var11 = false;
      boolean var12 = false;
      com.nokia.mid.impl.isa.ui.gdi.Graphics var13 = this.getImpl();
      int var15 = var13.resolveManipulation(var9);
      if (var1 == null) {
         throw new NullPointerException("Short array is null");
      } else if (var7 >= 0 && var8 >= 0) {
         if (var3 >= 0 && var4 >= 0 && var3 % var4 + var7 <= var4) {
            int var14 = var3 + (var8 - 1) * var4 + var7;
            if (var1.length < var14) {
               throw new ArrayIndexOutOfBoundsException("Not enough pixels in pixel array");
            } else {
               if (!this.isAlphaGraphicsFormat(var10)) {
                  var2 = false;
               }

               this.impl.drawPixels(var1, var2, var3, var4, var5 + this.translateX, var6 + this.translateY, var7, var8, var15, var10);
            }
         } else {
            throw new ArrayIndexOutOfBoundsException("Illegal offset | scanlength");
         }
      } else {
         throw new IllegalArgumentException("Negative width/height || width outside scan area");
      }
   }

   public void drawPixels(int[] var1, boolean var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      boolean var11 = false;
      boolean var12 = false;
      com.nokia.mid.impl.isa.ui.gdi.Graphics var13 = this.getImpl();
      int var15 = var13.resolveManipulation(var9);
      if (var1 == null) {
         throw new NullPointerException("Int array is null");
      } else if (var7 >= 0 && var8 >= 0) {
         if (var3 >= 0 && var4 >= 0 && var3 % var4 + var7 <= var4) {
            int var14 = var3 + (var8 - 1) * var4 + var7;
            if (var1.length < var14) {
               throw new ArrayIndexOutOfBoundsException("Not enough pixels in pixel array");
            } else {
               if (!this.isAlphaGraphicsFormat(var10)) {
                  var2 = false;
               }

               this.impl.drawPixels(var1, var2, var3, var4, var5 + this.translateX, var6 + this.translateY, var7, var8, var15, var10);
            }
         } else {
            throw new ArrayIndexOutOfBoundsException("Illegal offset | scanlength");
         }
      } else {
         throw new IllegalArgumentException("Negative width/height || width outside scan area");
      }
   }

   public int getNativePixelFormat() {
      return this.impl.getNativePixelFormat();
   }

   void checkPolyParameters(int[] var1, int var2, int[] var3, int var4, int var5, int var6) {
      if (var2 >= 0 && var4 >= 0) {
         if (var2 + var5 <= var1.length && var4 + var5 <= var3.length) {
            if (var5 < 0) {
               throw new ArrayIndexOutOfBoundsException("AIOBE: Polygon number of points negative");
            } else if (var1 == null || var3 == null) {
               throw new NullPointerException("NPE: Co-ordinate array(s) may be null");
            }
         } else {
            throw new ArrayIndexOutOfBoundsException("AIOBE: Not enough co-ordinate information to draw Polygon");
         }
      } else {
         throw new ArrayIndexOutOfBoundsException("AIOBE: Negative offset");
      }
   }

   void translateCoords(short[] var1, short[] var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4] = (short)(var1[var4] + this.translateX);
         var2[var4] = (short)(var2[var4] + this.translateY);
      }

   }

   boolean isAlphaGraphicsFormat(int var1) {
      boolean var2 = false;
      switch(var1) {
      case 1555:
      case 4444:
      case 8888:
         var2 = true;
      default:
         return var2;
      }
   }
}
