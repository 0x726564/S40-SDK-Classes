package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGAnimationElement;

public class SVGAnimationElementImpl extends SVGElementImpl implements SVGAnimationElement {
   private boolean active = false;

   public SVGAnimationElementImpl(Document doc, int handle) {
      super(doc, handle);
   }

   public void beginElementAt(float offset) {
      short restartAttr = _getTrait(this.myElement, (short)114);
      float currTime = SVGSVGElementImpl._getCurrentTime(this.getDocumentHandle());
      if ((this._isActive(this.myElement) != 1 || restartAttr != 472) && (currTime == 0.0F || restartAttr != 471)) {
         this._beginElementAt(this.getDocumentHandle(), this.myElement, offset);
      }

   }

   public void beginElement() {
      this.beginElementAt(0.0F);
   }

   public void endElementAt(float offset) {
      short endAttr = _getTrait(this.myElement, (short)73);
      if (endAttr != 445 && this._isActive(this.myElement) != 0) {
         this._endElementAt(this.getDocumentHandle(), this.myElement, offset);
      }

   }

   public void endElement() {
      this.endElementAt(0.0F);
   }

   private native void _beginElementAt(int var1, int var2, float var3);

   private native void _endElementAt(int var1, int var2, float var3);

   private native int _isActive(int var1);
}
