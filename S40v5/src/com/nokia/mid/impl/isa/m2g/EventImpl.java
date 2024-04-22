package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

public class EventImpl implements Event {
   private String mk;
   private EventTarget target;
   private boolean ml;

   public EventImpl(String var1, EventTarget var2, boolean var3) {
      this.mk = var1;
      this.target = var2;
      this.ml = var3;
   }

   public EventTarget getCurrentTarget() {
      return (EventTarget)(this.ml ? new EventTargetImpl((SVGElementImpl)this.target) : this.target);
   }

   public String getType() {
      return this.mk;
   }

   public EventTarget getListenerTarget() {
      return this.target;
   }
}
