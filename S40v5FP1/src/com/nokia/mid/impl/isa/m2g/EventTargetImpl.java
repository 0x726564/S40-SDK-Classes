package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.DOMException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public class EventTargetImpl implements EventTarget {
   private SVGElementImpl targetElement;

   public EventTargetImpl(SVGElementImpl element) {
      this.targetElement = element;
   }

   public void addEventListener(String type, EventListener listener, boolean useCapture) {
      if (type != null && !type.equals("") && listener != null) {
         if (useCapture) {
            throw new DOMException((short)9, "capture phase not supported in SVG Tiny");
         } else {
            ((DocumentImpl)this.targetElement.myDocument).register(this.targetElement, type, listener, true);
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void removeEventListener(String type, EventListener listener, boolean useCapture) {
      if (type != null && !type.equals("") && listener != null) {
         if (useCapture) {
            throw new DOMException((short)9, "capture phase not supported in SVG Tiny");
         } else {
            ((DocumentImpl)this.targetElement.myDocument).register(this.targetElement, type, listener, false);
         }
      } else {
         throw new NullPointerException();
      }
   }
}
