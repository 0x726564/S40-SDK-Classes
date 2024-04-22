package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGLocatableElement;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;

public class SVGLocatableElementImpl extends SVGElementImpl implements SVGLocatableElement {
   private int renderBfr = 0;
   private SVGPointImpl myPoint = null;
   public static final int WIDTH = 0;
   public static final int HEIGHT = 1;
   public static final int PIXELS = 1;
   public static final int PERCENT = 2;

   public SVGLocatableElementImpl(Document var1, int var2) {
      super(var1, var2);
   }

   public SVGRect getBBox() {
      SVGRectImpl var1 = new SVGRectImpl(0.0F, 0.0F, 0.0F, 0.0F);
      float[] var2 = var1.getArray();
      SVGElementImpl._getRectTrait(this.myElement, (short)59, var2);
      if ((this.elementType == 14 || this.elementType == 22) && var1.getArray()[0] == 0.0F && var1.getArray()[1] == 0.0F && var1.getArray()[2] == 0.0F && var1.getArray()[3] == 0.0F) {
         var1 = null;
      }

      return var1;
   }

   public SVGRect getScreenBBox() {
      float[] var1 = new float[4];
      if (_elementInDOM(super.getDocumentHandle(), this.myElement)) {
         SVGElementImpl._getScreenBBox(this.myElement, var1);
         return new SVGRectImpl(var1[0], var1[1], var1[2], var1[3]);
      } else {
         return null;
      }
   }

   public SVGMatrix getScreenCTM() {
      SVGMatrixImpl var1 = new SVGMatrixImpl(1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
      if (_elementInDOM(super.getDocumentHandle(), this.myElement)) {
         _getMatrixTrait(this.myElement, (short)27, var1.getArray());
      } else {
         var1 = null;
      }

      return var1;
   }
}
