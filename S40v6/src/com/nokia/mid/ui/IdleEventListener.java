package com.nokia.mid.ui;

public interface IdleEventListener {
   int IDLEITEM_ACTIVATED = 0;
   int IDLEITEM_DEACTIVATED = 1;
   int IDLEITEM_PROPERTY_CHANGED = 2;
   int IDLEITEM_THEME_CHANGED = 3;
   int IDLEITEM_LANGUAGE_CHANGED = 4;

   void eventAction(int var1);
}
