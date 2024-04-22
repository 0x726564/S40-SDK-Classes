package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.DOMException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public class EventTargetImpl implements EventTarget {
   private SVGElementImpl mo;

   public EventTargetImpl(SVGElementImpl var1) {
      this.mo = var1;
   }

   public void addEventListener(String var1, EventListener var2, boolean var3) {
      if (var1 != null && !var1.equals("") && var2 != null) {
         if (var3) {
            throw new DOMException((short)9, "capture phase not supported in SVG Tiny");
         } else {
            ((DocumentImpl)this.mo.myDocument).register(this.mo, var1, var2, true);
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void removeEventListener(String var1, EventListener var2, boolean var3) {
      if (var1 != null && !var1.equals("") && var2 != null) {
         if (var3) {
            throw new DOMException((short)9, "capture phase not supported in SVG Tiny");
         } else {
            ((DocumentImpl)this.mo.myDocument).register(this.mo, var1, var2, false);
         }
      } else {
         throw new NullPointerException();
      }
   }
}
