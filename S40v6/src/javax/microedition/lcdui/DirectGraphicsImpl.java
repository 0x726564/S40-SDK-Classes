package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.ui.DirectGraphics;

class DirectGraphicsImpl extends Graphics implements DirectGraphics {
   DirectGraphicsImpl(Image image) {
      super(image);
   }

   DirectGraphicsImpl(com.nokia.mid.impl.isa.ui.gdi.Graphics graphics) {
      super(graphics);
   }

   public int getAlphaComponent() {
      return (this.impl.getColorCtrl().getFgColor() & -16777216) >>> 24;
   }

   public void setARGBColor(int argbColor) {
      this.impl.getColorCtrl().setFgColor(argbColor);
   }

   public void drawImage(Image img, int x, int y, int anchor, int manipulation) {
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
            com.nokia.mid.impl.isa.ui.gdi.Graphics gdiG = this.getImpl();
            int orig_rot_orig_x = gdiG.getRotOrigX();
            int orig_rot_orig_y = gdiG.getRotOrigY();
            int orig_rotation = gdiG.getRotation();
            gdiG.setRotOrigX(0);
            gdiG.setRotOrigY(0);
            gdiG.setRotation(gdiG.resolveManipulation(manipulation));
            int rotatedWidth = width;
            int rotatedHeight = height;
            if ((gdiG.getRotation() & 5) != 0) {
               rotatedWidth = height;
               rotatedHeight = width;
            }

            gdiG.drawPixmap(pixmap, this.resolveAnchorX(x + this.getTranslateX(), anchor, rotatedWidth), this.resolveAnchorY(y + this.getTranslateY(), anchor, rotatedHeight));
            gdiG.setRotOrigX(orig_rot_orig_x);
            gdiG.setRotOrigY(orig_rot_orig_y);
            gdiG.setRotation(orig_rotation);
         }
      }
   }

   public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor) {
      ColorCtrl colorCtrl = this.impl.getColorCtrl();
      int currentColor = colorCtrl.getFgColor();
      colorCtrl.setFgColor(argbColor);
      if (this.strokeStyle == 0) {
         this.getImpl().drawTriangle((short)(x1 + this.translateX), (short)(y1 + this.translateY), (short)(x2 + this.translateX), (short)(y2 + this.translateY), (short)(x3 + this.translateX), (short)(y3 + this.translateY));
      } else {
         short[] xs = new short[]{(short)(x1 + this.translateX), (short)(x2 + this.translateX), (short)(x3 + this.translateX)};
         short[] ys = new short[]{(short)(y1 + this.translateY), (short)(y2 + this.translateY), (short)(y3 + this.translateY)};
         this.getImpl().drawDottedPoly(xs, ys, 3, true);
      }

      colorCtrl.setFgColor(currentColor);
   }

   public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor) {
      ColorCtrl colorCtrl = this.impl.getColorCtrl();
      int currentColor = colorCtrl.getFgColor();
      colorCtrl.setFgColor(argbColor);
      this.getImpl().fillTriangle((short)(x1 + this.translateX), (short)(y1 + this.translateY), (short)(x2 + this.translateX), (short)(y2 + this.translateY), (short)(x3 + this.translateX), (short)(y3 + this.translateY));
      colorCtrl.setFgColor(currentColor);
   }

   public void drawPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor) {
      this.checkPolyParameters(xPoints, xOffset, yPoints, yOffset, nPoints);
      ColorCtrl colorCtrl = this.impl.getColorCtrl();
      int currentColor = colorCtrl.getFgColor();
      colorCtrl.setFgColor(argbColor);
      short[] xs = new short[nPoints];
      short[] ys = new short[nPoints];

      for(int i = 0; i < nPoints; ++i) {
         xs[i] = (short)xPoints[xOffset + i];
         ys[i] = (short)yPoints[yOffset + i];
      }

      if (this.translateX != 0 || this.translateY != 0) {
         this.translateCoords(xs, ys, nPoints);
      }

      switch(nPoints) {
      case 0:
         break;
      case 1:
         this.getImpl().drawPixel(xs[0], ys[0]);
         break;
      case 2:
         if (this.strokeStyle == 0) {
            this.getImpl().s_drawLine(xs[0], ys[0], xs[1], ys[1]);
         } else {
            this.getImpl().drawDottedLine(xs[0], ys[0], xs[1], ys[1]);
         }
         break;
      default:
         if (this.strokeStyle == 0) {
            this.getImpl().drawPoly(xs, ys, nPoints, true);
         } else {
            this.getImpl().drawDottedPoly(xs, ys, nPoints, true);
         }
      }

      colorCtrl.setFgColor(currentColor);
   }

   public void fillPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor) {
      this.checkPolyParameters(xPoints, xOffset, yPoints, yOffset, nPoints);
      ColorCtrl colorCtrl = this.impl.getColorCtrl();
      int currentColor = colorCtrl.getFgColor();
      colorCtrl.setFgColor(argbColor);
      short[] xs = new short[nPoints];
      short[] ys = new short[nPoints];

      for(int i = 0; i < nPoints; ++i) {
         xs[i] = (short)xPoints[xOffset + i];
         ys[i] = (short)yPoints[yOffset + i];
      }

      if (this.translateX != 0 || this.translateY != 0) {
         this.translateCoords(xs, ys, nPoints);
      }

      switch(nPoints) {
      case 0:
         break;
      case 1:
         this.getImpl().drawPixel(xs[0], ys[0]);
         break;
      case 2:
         this.getImpl().s_drawLine(xs[0], ys[0], xs[1], ys[1]);
         break;
      default:
         this.getImpl().fillPoly(xs, ys, nPoints);
      }

      colorCtrl.setFgColor(currentColor);
   }

   public void getPixels(byte[] pixels, byte[] transparencyMask, int offset, int scanlength, int x, int y, int w, int h, int format) {
      this.impl.getPixels(pixels, transparencyMask, offset, scanlength, x, y, w, h, format);
   }

   public void getPixels(short[] pixels, int offset, int scanlength, int x, int y, int w, int h, int format) {
      int pixels_needed = false;
      if (pixels == null) {
         throw new NullPointerException("Short array is null");
      } else if (x >= 0 && y >= 0) {
         if (w >= 0 && h >= 0) {
            if (offset >= 0 && scanlength >= 0 && offset % scanlength + w <= scanlength) {
               int pixels_needed = offset + (h - 1) * scanlength + w;
               if (pixels.length < pixels_needed) {
                  throw new ArrayIndexOutOfBoundsException("Not enough pixels in pixel array");
               } else {
                  this.impl.getPixels(pixels, offset, scanlength, x + this.translateX, y + this.translateY, w, h, format);
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

   public void getPixels(int[] pixels, int offset, int scanlength, int x, int y, int w, int h, int format) {
      int pixels_needed = false;
      if (pixels == null) {
         throw new NullPointerException("Int array is null");
      } else if (x >= 0 && y >= 0) {
         if (w >= 0 && h >= 0) {
            if (offset >= 0 && scanlength >= 0 && offset % scanlength + w <= scanlength) {
               int pixels_needed = offset + (h - 1) * scanlength + w;
               if (pixels.length < pixels_needed) {
                  throw new ArrayIndexOutOfBoundsException("Not enough pixels in pixel array");
               } else {
                  this.impl.getPixels(pixels, offset, scanlength, x, y, w, h, format);
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

   public void drawPixels(byte[] pixels, byte[] transparencyMask, int offset, int scanlength, int x, int y, int w, int h, int manipulation, int format) {
      this.impl.drawPixels(pixels, transparencyMask, offset, scanlength, x + this.translateX, y + this.translateY, w, h, manipulation, format);
   }

   public void drawPixels(short[] pixels, boolean transparency, int offset, int scanlength, int x, int y, int w, int h, int manipulation, int format) {
      int pixels_needed = false;
      int gdiRotation = false;
      com.nokia.mid.impl.isa.ui.gdi.Graphics localGdi = this.getImpl();
      int gdiRotation = localGdi.resolveManipulation(manipulation);
      if (pixels == null) {
         throw new NullPointerException("Short array is null");
      } else if (w >= 0 && h >= 0) {
         if (offset >= 0 && scanlength >= 0 && offset % scanlength + w <= scanlength) {
            int pixels_needed = offset + (h - 1) * scanlength + w;
            if (pixels.length < pixels_needed) {
               throw new ArrayIndexOutOfBoundsException("Not enough pixels in pixel array");
            } else {
               if (!this.isAlphaGraphicsFormat(format)) {
                  transparency = false;
               }

               this.impl.drawPixels(pixels, transparency, offset, scanlength, x + this.translateX, y + this.translateY, w, h, gdiRotation, format);
            }
         } else {
            throw new ArrayIndexOutOfBoundsException("Illegal offset | scanlength");
         }
      } else {
         throw new IllegalArgumentException("Negative width/height || width outside scan area");
      }
   }

   public void drawPixels(int[] pixels, boolean transparency, int offset, int scanlength, int x, int y, int w, int h, int manipulation, int format) {
      int pixels_needed = false;
      int gdiRotation = false;
      com.nokia.mid.impl.isa.ui.gdi.Graphics localGdi = this.getImpl();
      int gdiRotation = localGdi.resolveManipulation(manipulation);
      if (pixels == null) {
         throw new NullPointerException("Int array is null");
      } else if (w >= 0 && h >= 0) {
         if (offset >= 0 && scanlength >= 0 && offset % scanlength + w <= scanlength) {
            int pixels_needed = offset + (h - 1) * scanlength + w;
            if (pixels.length < pixels_needed) {
               throw new ArrayIndexOutOfBoundsException("Not enough pixels in pixel array");
            } else {
               if (!this.isAlphaGraphicsFormat(format)) {
                  transparency = false;
               }

               this.impl.drawPixels(pixels, transparency, offset, scanlength, x + this.translateX, y + this.translateY, w, h, gdiRotation, format);
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

   public void drawHighlight(int x, int y, int w, int h, int d) {
      switch(d) {
      case 1:
         d = 1;
         break;
      case 2:
         d = 3;
      case 3:
      case 4:
      default:
         break;
      case 5:
         d = 4;
         break;
      case 6:
         d = 2;
      }

      this.impl.nativeDrawHighlight(x, y, w, h, d);
   }

   void checkPolyParameters(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints) {
      if (xOffset >= 0 && yOffset >= 0) {
         if (xOffset + nPoints <= xPoints.length && yOffset + nPoints <= yPoints.length) {
            if (nPoints < 0) {
               throw new ArrayIndexOutOfBoundsException("AIOBE: Polygon number of points negative");
            } else if (xPoints == null || yPoints == null) {
               throw new NullPointerException("NPE: Co-ordinate array(s) may be null");
            }
         } else {
            throw new ArrayIndexOutOfBoundsException("AIOBE: Not enough co-ordinate information to draw Polygon");
         }
      } else {
         throw new ArrayIndexOutOfBoundsException("AIOBE: Negative offset");
      }
   }

   void translateCoords(short[] xs, short[] ys, int nPoints) {
      for(int i = 0; i < nPoints; ++i) {
         xs[i] = (short)(xs[i] + this.translateX);
         ys[i] = (short)(ys[i] + this.translateY);
      }

   }

   boolean isAlphaGraphicsFormat(int format) {
      boolean isAlpha = false;
      switch(format) {
      case 1555:
      case 4444:
      case 8888:
         isAlpha = true;
      default:
         return isAlpha;
      }
   }
}
