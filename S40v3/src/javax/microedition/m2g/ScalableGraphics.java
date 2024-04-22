package javax.microedition.m2g;

import com.nokia.mid.impl.isa.m2g.DocumentImpl;
import com.nokia.mid.impl.isa.m2g.SVGSVGElementImpl;
import javax.microedition.lcdui.Graphics;

public class ScalableGraphics {
   public static final int RENDERING_QUALITY_LOW = 1;
   public static final int RENDERING_QUALITY_HIGH = 2;
   private int svgEngineContext = 0;
   private static int engineHndl = 0;
   private Graphics currentTarget = null;
   private float currentAlpha = 1.0F;

   private ScalableGraphics() {
      engineHndl = this._createEngine();
      DocumentImpl.setSvgEngineHandle(engineHndl);
      this._registerToFinalize();
   }

   public void bindTarget(Object var1) {
      if (this.currentTarget != null) {
         throw new IllegalStateException("Must release current target before you can bind a new target");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof Graphics)) {
         throw new IllegalArgumentException();
      } else {
         synchronized(SVGImage.svgLock) {
            this.currentTarget = (Graphics)var1;
            if (this.svgEngineContext == 0) {
               this.svgEngineContext = this._createOffscreenContext(this.currentTarget);
            } else {
               this._bindTarget(this.svgEngineContext, this.currentTarget);
            }

         }
      }
   }

   public void releaseTarget() {
      if (this.currentTarget == null) {
         throw new IllegalStateException("There is no target to release");
      } else {
         this.currentTarget = null;
      }
   }

   public void render(int var1, int var2, ScalableImage var3) {
      SVGImage var4 = (SVGImage)var3;
      if (var4 == null) {
         throw new NullPointerException();
      } else if (this.currentTarget == null) {
         throw new IllegalStateException("from render");
      } else {
         SVGSVGElementImpl var5 = (SVGSVGElementImpl)var4.getDocument().getDocumentElement();
         int var8 = this.currentTarget.getClipX();
         int var9 = this.currentTarget.getClipY();
         int var10 = this.currentTarget.getClipWidth();
         int var11 = this.currentTarget.getClipHeight();
         int var6;
         int var7;
         synchronized(SVGImage.svgLock) {
            label61: {
               var6 = var4.getViewportWidth();
               var7 = var4.getViewportHeight();
               if (var6 != 0 && var7 != 0) {
                  if (var5.getUnits(0) == 2) {
                     var6 = (this._getDeviceDisplayWidth(this.svgEngineContext) - var1) * var6 / 100;
                  } else if (var6 > var10) {
                     var6 = var10;
                     var4.setViewportWidth(var10);
                  }

                  if (var5.getUnits(1) == 2) {
                     var7 = (this._getDeviceDisplayHeight(this.svgEngineContext) - var2) * var7 / 100;
                  } else if (var7 > var11) {
                     var7 = var11;
                     var4.setViewportHeight(var11);
                  }

                  if (var1 < var8 + var10 && var2 < var9 + var11) {
                     if (var1 + var6 >= var8 && var2 + var7 >= var9) {
                        if (var4.viewportHasChanged()) {
                           var4.applyViewportToSurface(var6, var7);
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

         int var12 = var5.setRenderSurface(var6, var7);
         this._render(engineHndl, var5.getDocumentHandle(), var12, var8, var9);
         var8 += this.currentTarget.getTranslateX();
         var9 += this.currentTarget.getTranslateY();
         this._copyFrom(var12, this.svgEngineContext, this.currentAlpha, (float)(var1 + var8), (float)(var2 + var9), (float)var8, (float)var9, (float)var10, (float)var11);
      }
   }

   public void setRenderingQuality(int var1) {
      if (var1 != 1 && var1 != 2) {
         throw new IllegalArgumentException();
      } else {
         this._setRenderingQuality(engineHndl, var1);
      }
   }

   public void setTransparency(float var1) {
      if (var1 >= 0.0F && var1 <= 1.0F) {
         this.currentAlpha = var1;
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
