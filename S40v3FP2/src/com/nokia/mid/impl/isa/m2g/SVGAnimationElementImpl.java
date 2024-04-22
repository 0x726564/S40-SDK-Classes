package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGAnimationElement;

public class SVGAnimationElementImpl extends SVGElementImpl implements SVGAnimationElement {
   private boolean active = false;

   public SVGAnimationElementImpl(Document var1, int var2) {
      super(var1, var2);
   }

   public void beginElementAt(float var1) {
      short var2 = _getTrait(this.myElement, (short)114);
      float var3 = SVGSVGElementImpl._getCurrentTime(this.getDocumentHandle());
      if ((this._isActive(this.myElement) != 1 || var2 != 472) && (var3 == 0.0F || var2 != 471)) {
         this._beginElementAt(this.getDocumentHandle(), this.myElement, var1);
      }

   }

   public void beginElement() {
      this.beginElementAt(0.0F);
   }

   public void endElementAt(float var1) {
      short var2 = _getTrait(this.myElement, (short)73);
      if (var2 != 445 && this._isActive(this.myElement) != 0) {
         this._endElementAt(this.getDocumentHandle(), this.myElement, var1);
      }

   }

   public void endElement() {
      this.endElementAt(0.0F);
   }

   private native void _beginElementAt(int var1, int var2, float var3);

   private native void _endElementAt(int var1, int var2, float var3);

   private native int _isActive(int var1);
}
