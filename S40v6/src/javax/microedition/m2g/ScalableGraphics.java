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
         int translateX = this.currentTarget.getTranslateX();
         int translateY = this.currentTarget.getTranslateY();
         int t_x = translateX + x;
         int t_y = translateY + y;
         int abs_cx = this.currentTarget.getTranslateX() + this.currentTarget.getClipX();
         int abs_cy = this.currentTarget.getTranslateY() + this.currentTarget.getClipY();
         int cwidth = this.currentTarget.getClipWidth();
         cwidth = abs_cx + cwidth > this._getDeviceDisplayWidth(this.svgEngineContext) ? cwidth - abs_cx : cwidth;
         int cheight = this.currentTarget.getClipHeight();
         cheight = abs_cy + cheight > this._getDeviceDisplayHeight(this.svgEngineContext) ? cheight - abs_cy : cheight;
         if (cwidth != 0 && cheight != 0 && x != Integer.MAX_VALUE && y != Integer.MAX_VALUE) {
            SVGSVGElementImpl mydoc = (SVGSVGElementImpl)image.getDocument().getDocumentElement();
            int surface_x;
            int surface_y;
            int surface_x2;
            int surface_y2;
            synchronized(SVGImage.svgLock) {
               int viewport_width = image.getViewportWidth();
               int viewport_height = image.getViewportHeight();
               if (viewport_width == 0 || viewport_height == 0) {
                  return;
               }

               if (mydoc.getUnits(0) == 2) {
                  viewport_width = 100 * viewport_width / 100;
               }

               if (mydoc.getUnits(1) == 2) {
                  viewport_height = 100 * viewport_height / 100;
               }

               surface_x = t_x > abs_cx ? t_x : abs_cx;
               surface_x2 = t_x + viewport_width < abs_cx + cwidth ? t_x + viewport_width : abs_cx + cwidth;
               surface_y = t_y > abs_cy ? t_y : abs_cy;
               surface_y2 = t_y + viewport_height < abs_cy + cheight ? t_y + viewport_height : abs_cy + cheight;
               if (surface_y2 - surface_y <= 0 || surface_x2 - surface_x <= 0) {
                  return;
               }

               if (image.viewportHasChanged()) {
                  image.applyViewportToSurface(viewport_width, viewport_height);
               }
            }

            int renderBfr = mydoc.setRenderSurface(surface_x2 - surface_x, surface_y2 - surface_y);
            this._render(engineHndl, mydoc.getDocumentHandle(), renderBfr, surface_x - t_x, surface_y - t_y);
            this._copyFrom(renderBfr, this.svgEngineContext, this.currentAlpha, (float)surface_x, (float)surface_y, (float)surface_x, (float)surface_y, (float)(surface_x2 - surface_x), (float)(surface_y2 - surface_y));
         }
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
