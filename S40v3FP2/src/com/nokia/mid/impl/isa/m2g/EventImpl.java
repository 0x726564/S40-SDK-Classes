package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

public class EventImpl implements Event {
   private String eventType;
   private EventTarget target;
   private boolean isElementInstance;

   public EventImpl(String var1, EventTarget var2, boolean var3) {
      this.eventType = var1;
      this.target = var2;
      this.isElementInstance = var3;
   }

   public EventTarget getCurrentTarget() {
      return (EventTarget)(this.isElementInstance ? new EventTargetImpl((SVGElementImpl)this.target) : this.target);
   }

   public String getType() {
      return this.eventType;
   }

   public EventTarget getListenerTarget() {
      return this.target;
   }
}
