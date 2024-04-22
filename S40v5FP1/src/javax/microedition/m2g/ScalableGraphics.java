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

   public void bindTarget(Object target) {
      if (this.currentTarget != null) {
         throw new IllegalStateException("Must release current target before you can bind a new target");
      } else if (target == null) {
         throw new NullPointerException();
      } else if (!(target instanceof Graphics)) {
         throw new IllegalArgumentException();
      } else {
         synchronized(SVGImage.svgLock) {
            this.currentTarget = (Graphics)target;
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

   public void render(int x, int y, ScalableImage img) {
      SVGImage image = (SVGImage)img;
      if (image == null) {
         throw new NullPointerException();
      } else if (this.currentTarget == null) {
         throw new IllegalStateException("from render");
      } else {
         SVGSVGElementImpl mydoc = (SVGSVGElementImpl)image.getDocument().getDocumentElement();
         int cx = this.currentTarget.getClipX();
         int cy = this.currentTarget.getClipY();
         int cwidth = this.currentTarget.getClipWidth();
         int cheight = this.currentTarget.getClipHeight();
         int surface_width;
         int surface_height;
         synchronized(SVGImage.svgLock) {
            label61: {
               surface_width = image.getViewportWidth();
               surface_height = image.getViewportHeight();
               if (surface_width != 0 && surface_height != 0) {
                  if (mydoc.getUnits(0) == 2) {
                     surface_width = (this._getDeviceDisplayWidth(this.svgEngineContext) - x) * surface_width / 100;
                  } else if (surface_width > cwidth) {
                     surface_width = cwidth;
                     image.setViewportWidth(cwidth);
                  }

                  if (mydoc.getUnits(1) == 2) {
                     surface_height = (this._getDeviceDisplayHeight(this.svgEngineContext) - y) * surface_height / 100;
                  } else if (surface_height > cheight) {
                     surface_height = cheight;
                     image.setViewportHeight(cheight);
                  }

                  if (x < cx + cwidth && y < cy + cheight) {
                     if (x + surface_width >= cx && y + surface_height >= cy) {
                        if (image.viewportHasChanged()) {
                           image.applyViewportToSurface(surface_width, surface_height);
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

         int renderBfr = mydoc.setRenderSurface(surface_width, surface_height);
         this._render(engineHndl, mydoc.getDocumentHandle(), renderBfr, cx, cy);
         cx += this.currentTarget.getTranslateX();
         cy += this.currentTarget.getTranslateY();
         this._copyFrom(renderBfr, this.svgEngineContext, this.currentAlpha, (float)(x + cx), (float)(y + cy), (float)cx, (float)cy, (float)cwidth, (float)cheight);
      }
   }

   public void setRenderingQuality(int mode) {
      if (mode != 1 && mode != 2) {
         throw new IllegalArgumentException();
      } else {
         this._setRenderingQuality(engineHndl, mode);
      }
   }

   public void setTransparency(float alpha) {
      if (alpha >= 0.0F && alpha <= 1.0F) {
         this.currentAlpha = alpha;
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
