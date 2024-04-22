package org.w3c.dom.events;

public interface EventTarget {
   void addEventListener(String var1, EventListener var2, boolean var3);

   void removeEventListener(String var1, EventListener var2, boolean var3);
}
