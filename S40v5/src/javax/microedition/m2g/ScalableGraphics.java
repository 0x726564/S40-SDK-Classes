package javax.microedition.m2g;

import com.nokia.mid.impl.isa.m2g.DocumentImpl;
import com.nokia.mid.impl.isa.m2g.SVGSVGElementImpl;
import javax.microedition.lcdui.Graphics;

public class ScalableGraphics {
   public static final int RENDERING_QUALITY_LOW = 1;
   public static final int RENDERING_QUALITY_HIGH = 2;
   private int e = 0;
   private static int f = 0;
   private Graphics g = null;
   private float h = 1.0F;

   private ScalableGraphics() {
      DocumentImpl.setSvgEngineHandle(f = this._createEngine());
      this._registerToFinalize();
   }

   public void bindTarget(Object var1) {
      if (this.g != null) {
         throw new IllegalStateException("Must release current target before you can bind a new target");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof Graphics)) {
         throw new IllegalArgumentException();
      } else {
         synchronized(SVGImage.b) {
            this.g = (Graphics)var1;
            if (this.e == 0) {
               this.e = this._createOffscreenContext(this.g);
            } else {
               this._bindTarget(this.e, this.g);
            }

         }
      }
   }

   public void releaseTarget() {
      if (this.g == null) {
         throw new IllegalStateException("There is no target to release");
      } else {
         this.g = null;
      }
   }

   public void render(int var1, int var2, ScalableImage var3) {
      SVGImage var13;
      if ((var13 = (SVGImage)var3) == null) {
         throw new NullPointerException();
      } else if (this.g == null) {
         throw new IllegalStateException("from render");
      } else {
         SVGSVGElementImpl var4 = (SVGSVGElementImpl)var13.getDocument().getDocumentElement();
         int var7 = this.g.getClipX();
         int var8 = this.g.getClipY();
         int var9 = this.g.getClipWidth();
         int var10 = this.g.getClipHeight();
         int var5;
         int var6;
         synchronized(SVGImage.b) {
            label61: {
               var5 = var13.getViewportWidth();
               var6 = var13.getViewportHeight();
               if (var5 != 0 && var6 != 0) {
                  if (var4.getUnits(0) == 2) {
                     var5 = (this._getDeviceDisplayWidth(this.e) - var1) * var5 / 100;
                  } else if (var5 > var9) {
                     var5 = var9;
                     var13.setViewportWidth(var9);
                  }

                  if (var4.getUnits(1) == 2) {
                     var6 = (this._getDeviceDisplayHeight(this.e) - var2) * var6 / 100;
                  } else if (var6 > var10) {
                     var6 = var10;
                     var13.setViewportHeight(var10);
                  }

                  if (var1 < var7 + var9 && var2 < var8 + var10) {
                     if (var1 + var5 >= var7 && var2 + var6 >= var8) {
                        if (var13.a()) {
                           var13.a(var5, var6);
                        }
                        break label61;
                     }

                     return;
                  }

                  return;
               }

               return;
            }
         }

         int var11 = var4.setRenderSurface(var5, var6);
         this._render(f, var4.getDocumentHandle(), var11, var7, var8);
         var7 += this.g.getTranslateX();
         var8 += this.g.getTranslateY();
         this._copyFrom(var11, this.e, this.h, (float)(var1 + var7), (float)(var2 + var8), (float)var7, (float)var8, (float)var9, (float)var10);
      }
   }

   public void setRenderingQuality(int var1) {
      if (var1 != 1 && var1 != 2) {
         throw new IllegalArgumentException();
      } else {
         this._setRenderingQuality(f, var1);
      }
   }

   public void setTransparency(float var1) {
      if (var1 >= 0.0F && var1 <= 1.0F) {
         this.h = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static ScalableGraphics createInstance() {
      return new ScalableGraphics();
   }

   private native int _createEngine();

   private native int _createOffscreenContext(Graphics var1);

   private native void _bindTarget(int var1, Graphics var2);

   private native void _setRenderingQuality(int var1, int var2);

   private native int _getDeviceDisplayWidth(int var1);

   private native int _getDeviceDisplayHeight(int var1);

   private native void _render(int var1, int var2, int var3, int var4, int var5);

   private native void _copyFrom(int var1, int var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9);

   private native void _registerToFinalize();
}
