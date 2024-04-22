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
      return this.da.getColorCtrl().getFgColor() >>> 24;
   }

   public void setARGBColor(int var1) {
      this.da.getColorCtrl().setFgColor(var1);
   }

   public void drawImage(Image var1, int var2, int var3, int var4, int var5) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (var4 == 0) {
            var4 = 20;
         }

         if (!p(var4)) {
            throw new IllegalArgumentException("Graphics: Invalid anchor. Anchor is: " + var4);
         } else {
            int var6 = var1.getWidth();
            int var7 = var1.getHeight();
            Pixmap var10 = var1.getPixmap();
            com.nokia.mid.impl.isa.ui.gdi.Graphics var11;
            int var13 = (var11 = this.getImpl()).getRotOrigX();
            int var8 = var11.getRotOrigY();
            int var9 = var11.getRotation();
            var11.setRotOrigX(0);
            var11.setRotOrigY(0);
            var11.setRotation(var11.resolveManipulation(var5));
            var5 = var6;
            int var12 = var7;
            if ((var11.getRotation() & 5) != 0) {
               var5 = var7;
               var12 = var6;
            }

            var11.drawPixmap(var10, a(var2 + this.getTranslateX(), var4, var5), b(var3 + this.getTranslateY(), var4, var12));
            var11.setRotOrigX(var13);
            var11.setRotOrigY(var8);
            var11.setRotation(var9);
         }
      }
   }

   public void drawTriangle(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      ColorCtrl var8;
      int var9 = (var8 = this.da.getColorCtrl()).getFgColor();
      var8.setFgColor(var7);
      if (this.cX == 0) {
         this.getImpl().drawTriangle((short)(var1 + this.cY), (short)(var2 + this.cZ), (short)(var3 + this.cY), (short)(var4 + this.cZ), (short)(var5 + this.cY), (short)(var6 + this.cZ));
      } else {
         short[] var10 = new short[]{(short)(var1 + this.cY), (short)(var3 + this.cY), (short)(var5 + this.cY)};
         short[] var11 = new short[]{(short)(var2 + this.cZ), (short)(var4 + this.cZ), (short)(var6 + this.cZ)};
         this.getImpl().drawDottedPoly(var10, var11, 3, true);
      }

      var8.setFgColor(var9);
   }

   public void fillTriangle(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      ColorCtrl var8;
      int var9 = (var8 = this.da.getColorCtrl()).getFgColor();
      var8.setFgColor(var7);
      this.getImpl().fillTriangle((short)(var1 + this.cY), (short)(var2 + this.cZ), (short)(var3 + this.cY), (short)(var4 + this.cZ), (short)(var5 + this.cY), (short)(var6 + this.cZ));
      var8.setFgColor(var9);
   }

   public void drawPolygon(int[] var1, int var2, int[] var3, int var4, int var5, int var6) {
      a(var1, var2, var3, var4, var5);
      ColorCtrl var7;
      int var8 = (var7 = this.da.getColorCtrl()).getFgColor();
      var7.setFgColor(var6);
      short[] var11 = new short[var5];
      short[] var9 = new short[var5];

      for(int var10 = 0; var10 < var5; ++var10) {
         var11[var10] = (short)var1[var2 + var10];
         var9[var10] = (short)var3[var4 + var10];
      }

      if (this.cY != 0 || this.cZ != 0) {
         this.a(var11, var9, var5);
      }

      switch(var5) {
      case 0:
         break;
      case 1:
         this.getImpl().drawPixel(var11[0], var9[0]);
         break;
      case 2:
         if (this.cX == 0) {
            this.getImpl().s_drawLine(var11[0], var9[0], var11[1], var9[1]);
         } else {
            this.getImpl().drawDottedLine(var11[0], var9[0], var11[1], var9[1]);
         }
         break;
      default:
         if (this.cX == 0) {
            this.getImpl().drawPoly(var11, var9, var5, true);
         } else {
            this.getImpl().drawDottedPoly(var11, var9, var5, true);
         }
      }

      var7.setFgColor(var8);
   }

   public void fillPolygon(int[] var1, int var2, int[] var3, int var4, int var5, int var6) {
      a(var1, var2, var3, var4, var5);
      ColorCtrl var7;
      int var8 = (var7 = this.da.getColorCtrl()).getFgColor();
      var7.setFgColor(var6);
      short[] var11 = new short[var5];
      short[] var9 = new short[var5];

      for(int var10 = 0; var10 < var5; ++var10) {
         var11[var10] = (short)var1[var2 + var10];
         var9[var10] = (short)var3[var4 + var10];
      }

      if (this.cY != 0 || this.cZ != 0) {
         this.a(var11, var9, var5);
      }

      switch(var5) {
      case 0:
         break;
      case 1:
         this.getImpl().drawPixel(var11[0], var9[0]);
         break;
      case 2:
         this.getImpl().s_drawLine(var11[0], var9[0], var11[1], var9[1]);
         break;
      default:
         this.getImpl().fillPoly(var11, var9, var5);
      }

      var7.setFgColor(var8);
   }

   public void getPixels(byte[] var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      this.da.getPixels(var1, var2, var3, var4, var5, var6, var7, var8, var9);
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
                  this.da.getPixels(var1, var2, var3, var4 + this.cY, var5 + this.cZ, var6, var7, var8);
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
                  this.da.getPixels(var1, var2, var3, var4, var5, var6, var7, var8);
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
      this.da.drawPixels(var1, var2, var3, var4, var5 + this.cY, var6 + this.cZ, var7, var8, var9, var10);
   }

   public void drawPixels(short[] var1, boolean var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      boolean var11 = false;
      boolean var12 = false;
      int var14 = this.getImpl().resolveManipulation(var9);
      if (var1 == null) {
         throw new NullPointerException("Short array is null");
      } else if (var7 >= 0 && var8 >= 0) {
         if (var3 >= 0 && var4 >= 0 && var3 % var4 + var7 <= var4) {
            int var13 = var3 + (var8 - 1) * var4 + var7;
            if (var1.length < var13) {
               throw new ArrayIndexOutOfBoundsException("Not enough pixels in pixel array");
            } else {
               if (!B(var10)) {
                  var2 = false;
               }

               this.da.drawPixels(var1, var2, var3, var4, var5 + this.cY, var6 + this.cZ, var7, var8, var14, var10);
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
      int var14 = this.getImpl().resolveManipulation(var9);
      if (var1 == null) {
         throw new NullPointerException("Int array is null");
      } else if (var7 >= 0 && var8 >= 0) {
         if (var3 >= 0 && var4 >= 0 && var3 % var4 + var7 <= var4) {
            int var13 = var3 + (var8 - 1) * var4 + var7;
            if (var1.length < var13) {
               throw new ArrayIndexOutOfBoundsException("Not enough pixels in pixel array");
            } else {
               if (!B(var10)) {
                  var2 = false;
               }

               this.da.drawPixels(var1, var2, var3, var4, var5 + this.cY, var6 + this.cZ, var7, var8, var14, var10);
            }
         } else {
            throw new ArrayIndexOutOfBoundsException("Illegal offset | scanlength");
         }
      } else {
         throw new IllegalArgumentException("Negative width/height || width outside scan area");
      }
   }

   public int getNativePixelFormat() {
      return this.da.getNativePixelFormat();
   }

   private static void a(int[] var0, int var1, int[] var2, int var3, int var4) {
      if (var1 >= 0 && var3 >= 0) {
         if (var1 + var4 <= var0.length && var3 + var4 <= var2.length) {
            if (var4 < 0) {
               throw new ArrayIndexOutOfBoundsException("AIOBE: Polygon number of points negative");
            } else if (var0 == null || var2 == null) {
               throw new NullPointerException("NPE: Co-ordinate array(s) may be null");
            }
         } else {
            throw new ArrayIndexOutOfBoundsException("AIOBE: Not enough co-ordinate information to draw Polygon");
         }
      } else {
         throw new ArrayIndexOutOfBoundsException("AIOBE: Negative offset");
      }
   }

   private void a(short[] var1, short[] var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4] += this.cY;
         var2[var4] += this.cZ;
      }

   }

   private static boolean B(int var0) {
      boolean var1 = false;
      switch(var0) {
      case 1555:
      case 4444:
      case 8888:
         var1 = true;
      default:
         return var1;
      }
   }
}
