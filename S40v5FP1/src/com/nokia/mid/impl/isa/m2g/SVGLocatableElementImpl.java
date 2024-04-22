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

   public SVGLocatableElementImpl(Document doc, int handle) {
      super(doc, handle);
   }

   public SVGRect getBBox() {
      SVGRectImpl myBBox = new SVGRectImpl(0.0F, 0.0F, 0.0F, 0.0F);
      float[] retVal = myBBox.getArray();
      SVGElementImpl._getRectTrait(this.myElement, (short)59, retVal);
      if ((this.elementType == 14 || this.elementType == 22 || this.elementType == 29 || this.elementType == 27) && myBBox.getArray()[0] == 0.0F && myBBox.getArray()[1] == 0.0F && myBBox.getArray()[2] == 0.0F && myBBox.getArray()[3] == 0.0F) {
         myBBox = null;
      }

      if (this.elementType == 29) {
         String textContent = this.getTrait("#text");
         if (textContent.length() <= 0) {
            myBBox = null;
         }
      }

      return myBBox;
   }

   public SVGRect getScreenBBox() {
      float[] returnValue = new float[4];
      if (_elementInDOM(super.getDocumentHandle(), this.myElement)) {
         SVGElementImpl._getScreenBBox(this.myElement, returnValue);
         return (double)returnValue[0] == 0.0D && (double)returnValue[1] == 0.0D && (double)returnValue[2] == 0.0D && (double)returnValue[3] == 0.0D ? null : new SVGRectImpl(returnValue[0], returnValue[1], returnValue[2], returnValue[3]);
      } else {
         return null;
      }
   }

   public SVGMatrix getScreenCTM() {
      SVGMatrixImpl myMtx = new SVGMatrixImpl(1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
      if (_elementInDOM(super.getDocumentHandle(), this.myElement)) {
         _getMatrixTrait(this.myElement, (short)27, myMtx.getArray());
      } else {
         myMtx = null;
      }

      return myMtx;
   }
}
