package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPath;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRGBColor;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;

public class SVGSVGElementImpl extends SVGLocatableElementImpl implements SVGSVGElement {
   private int renderBfr = 0;
   protected SVGMatrixImpl userCTM = (SVGMatrixImpl)this.createSVGMatrixComponents(1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
   private SVGPointImpl currentTranslate = new SVGPointImpl(this);
   private float currentRotate = 0.0F;
   private float currentScale = 1.0F;
   private float seconds = 0.0F;
   public static final int WIDTH = 0;
   public static final int HEIGHT = 1;
   public static final int PIXELS = 1;
   public static final int PERCENT = 2;

   public SVGSVGElementImpl(Document doc) {
      super(doc, _getRootElement(((DocumentImpl)doc).getDocumentHandle()));
      SVGElementImpl._setMatrixTrait(this.getDocumentHandle(), this.myElement, (short)135, this.userCTM.getComponent(0), this.userCTM.getComponent(1), this.userCTM.getComponent(2), this.userCTM.getComponent(3), this.userCTM.getComponent(4), this.userCTM.getComponent(5));
      this._registerToFinalize();
   }

   public SVGMatrix createSVGMatrixComponents(float a, float b, float c, float d, float e, float f) {
      return new SVGMatrixImpl(a, b, c, d, e, f);
   }

   public SVGPath createSVGPath() {
      return new SVGPathImpl(SVGPathImpl._createSVGPath());
   }

   public SVGRect createSVGRect() {
      return new SVGRectImpl();
   }

   public SVGRGBColor createSVGRGBColor(int red, int green, int blue) {
      if (red <= 255 && red >= 0 && green <= 255 && green >= 0 && blue <= 255 && blue >= 0) {
         return new SVGRGBColorImpl(red, green, blue);
      } else {
         throw new SVGException((short)1, "Color values out of range");
      }
   }

   public float getCurrentRotate() {
      return this.currentRotate;
   }

   public float getCurrentScale() {
      return this.currentScale;
   }

   public float getCurrentTime() {
      return this.seconds;
   }

   public SVGPoint getCurrentTranslate() {
      return this.currentTranslate;
   }

   public void setCurrentRotate(float value) {
      String zoomAndPan = this.getTrait("zoomAndPan");
      if (zoomAndPan == null || !zoomAndPan.equals("disable")) {
         this.currentRotate = value;
         this.updateTransformMatrix();
      }
   }

   public void setCurrentScale(float value) {
      String zoomAndPan = this.getTrait("zoomAndPan");
      if (zoomAndPan == null || !zoomAndPan.equals("disable")) {
         if (value == 0.0F) {
            throw new DOMException((short)15, "Cannot scale by 0");
         } else {
            this.currentScale = value;
            this.updateTransformMatrix();
         }
      }
   }

   public void setCurrentTime(float newSeconds) {
      if (!(newSeconds < 0.0F)) {
         this.seconds = newSeconds;
         _setCurrentTime(this.getDocumentHandle(), this.seconds);
      }
   }

   public void incrementTime(float dSeconds) {
      if (dSeconds < 0.0F) {
         throw new IllegalArgumentException();
      } else if (Float.isNaN(this.seconds + dSeconds)) {
         throw new IllegalArgumentException();
      } else {
         this.seconds += dSeconds;
         _setCurrentTime(this.getDocumentHandle(), this.seconds);
      }
   }

   public Node getParentNode() {
      return null;
   }

   public int setRenderSurface(int surface_width, int surface_height) {
      if (this.renderBfr == 0) {
         this.renderBfr = this._createRndrBfr(DocumentImpl.getSvgEngineHandle(), surface_width, surface_height);
      }

      return this.renderBfr;
   }

   public void refreshRenderSurface(int width, int height) {
      if (this.renderBfr != 0) {
         _destroyRndrBfr(this.renderBfr);
      }

      this.renderBfr = this._createRndrBfr(DocumentImpl.getSvgEngineHandle(), width, height);
      _clearRndrBfr(this.renderBfr);
   }

   public int getUnits(int xORy) {
      if (xORy == 0) {
         return getWidthUnits(this.getDocumentHandle());
      } else {
         return xORy == 1 ? getHeightUnits(this.getDocumentHandle()) : -1;
      }
   }

   private static native int getWidthUnits(int var0);

   private static native int getHeightUnits(int var0);

   private native int _createRndrBfr(int var1, int var2, int var3);

   private static native void _clearRndrBfr(int var0);

   private static native void _destroyRndrBfr(int var0);

   private static native void _setCurrentTime(int var0, float var1);

   protected static native float _getCurrentTime(int var0);

   protected static native int _getRootElement(int var0);

   private native void _registerToFinalize();

   public void updateTransformMatrix() {
      SVGMatrixImpl a = new SVGMatrixImpl(this.currentScale, 0.0F, 0.0F, this.currentScale, this.currentTranslate.getX(), this.currentTranslate.getY());
      SVGMatrixImpl b = new SVGMatrixImpl((float)Math.cos((double)(this.currentRotate * 3.1415927F / 180.0F)), (float)Math.sin((double)(this.currentRotate * 3.1415927F / 180.0F)), -((float)Math.sin((double)(this.currentRotate * 3.1415927F / 180.0F))), (float)Math.cos((double)(this.currentRotate * 3.1415927F / 180.0F)), 0.0F, 0.0F);
      this.userCTM = (SVGMatrixImpl)a.mMultiply(b);
      SVGElementImpl._setMatrixTrait(this.getDocumentHandle(), this.myElement, (short)135, this.userCTM.getComponent(0), this.userCTM.getComponent(1), this.userCTM.getComponent(2), this.userCTM.getComponent(3), this.userCTM.getComponent(4), this.userCTM.getComponent(5));
   }
}
