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
   private int jx = 0;
   protected SVGMatrixImpl userCTM = (SVGMatrixImpl)this.createSVGMatrixComponents(1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
   private SVGPointImpl jy = new SVGPointImpl(this);
   private float jz = 0.0F;
   private float jA = 1.0F;
   private float jB = 0.0F;
   public static final int WIDTH = 0;
   public static final int HEIGHT = 1;
   public static final int PIXELS = 1;
   public static final int PERCENT = 2;

   public SVGSVGElementImpl(Document var1) {
      super(var1, _getRootElement(((DocumentImpl)var1).getDocumentHandle()));
      SVGElementImpl._setMatrixTrait(this.getDocumentHandle(), this.myElement, (short)135, this.userCTM.getComponent(0), this.userCTM.getComponent(1), this.userCTM.getComponent(2), this.userCTM.getComponent(3), this.userCTM.getComponent(4), this.userCTM.getComponent(5));
      this._registerToFinalize();
   }

   public SVGMatrix createSVGMatrixComponents(float var1, float var2, float var3, float var4, float var5, float var6) {
      return new SVGMatrixImpl(var1, var2, var3, var4, var5, var6);
   }

   public SVGPath createSVGPath() {
      return new SVGPathImpl(SVGPathImpl._createSVGPath());
   }

   public SVGRect createSVGRect() {
      return new SVGRectImpl();
   }

   public SVGRGBColor createSVGRGBColor(int var1, int var2, int var3) {
      if (var1 <= 255 && var1 >= 0 && var2 <= 255 && var2 >= 0 && var3 <= 255 && var3 >= 0) {
         return new SVGRGBColorImpl(var1, var2, var3);
      } else {
         throw new SVGException((short)1, "Color values out of range");
      }
   }

   public float getCurrentRotate() {
      return this.jz;
   }

   public float getCurrentScale() {
      return this.jA;
   }

   public float getCurrentTime() {
      return this.jB;
   }

   public SVGPoint getCurrentTranslate() {
      return this.jy;
   }

   public void setCurrentRotate(float var1) {
      String var2;
      if ((var2 = this.getTrait("zoomAndPan")) == null || !var2.equals("disable")) {
         this.jz = var1;
         this.updateTransformMatrix();
      }
   }

   public void setCurrentScale(float var1) {
      String var2;
      if ((var2 = this.getTrait("zoomAndPan")) == null || !var2.equals("disable")) {
         if (var1 == 0.0F) {
            throw new DOMException((short)15, "Cannot scale by 0");
         } else {
            this.jA = var1;
            this.updateTransformMatrix();
         }
      }
   }

   public void setCurrentTime(float var1) {
      if (!(var1 < this.jB)) {
         this.jB = var1;
         _setCurrentTime(this.getDocumentHandle(), this.jB);
      }
   }

   public void incrementTime(float var1) {
      if (var1 < 0.0F) {
         throw new IllegalArgumentException();
      } else if (Float.isNaN(this.jB + var1)) {
         throw new IllegalArgumentException();
      } else {
         this.jB += var1;
         _setCurrentTime(this.getDocumentHandle(), this.jB);
      }
   }

   public Node getParentNode() {
      return null;
   }

   public int setRenderSurface(int var1, int var2) {
      if (this.jx == 0) {
         this.jx = this._createRndrBfr(DocumentImpl.getSvgEngineHandle(), var1, var2);
      }

      return this.jx;
   }

   public void refreshRenderSurface(int var1, int var2) {
      if (this.jx != 0) {
         _destroyRndrBfr(this.jx);
      }

      this.jx = this._createRndrBfr(DocumentImpl.getSvgEngineHandle(), var1, var2);
      _clearRndrBfr(this.jx);
   }

   public int getUnits(int var1) {
      if (var1 == 0) {
         return getWidthUnits(this.getDocumentHandle());
      } else {
         return var1 == 1 ? getHeightUnits(this.getDocumentHandle()) : -1;
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
      SVGMatrixImpl var1 = new SVGMatrixImpl(this.jA, 0.0F, 0.0F, this.jA, this.jy.getX(), this.jy.getY());
      SVGMatrixImpl var2 = new SVGMatrixImpl((float)Math.cos((double)(this.jz * 3.1415927F / 180.0F)), (float)Math.sin((double)(this.jz * 3.1415927F / 180.0F)), -((float)Math.sin((double)(this.jz * 3.1415927F / 180.0F))), (float)Math.cos((double)(this.jz * 3.1415927F / 180.0F)), 0.0F, 0.0F);
      this.userCTM = (SVGMatrixImpl)var1.mMultiply(var2);
      SVGElementImpl._setMatrixTrait(this.getDocumentHandle(), this.myElement, (short)135, this.userCTM.getComponent(0), this.userCTM.getComponent(1), this.userCTM.getComponent(2), this.userCTM.getComponent(3), this.userCTM.getComponent(4), this.userCTM.getComponent(5));
   }
}
