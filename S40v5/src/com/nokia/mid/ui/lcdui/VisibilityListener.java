package com.nokia.mid.ui.lcdui;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

public interface VisibilityListener {
   void showNotify(Display var1, Displayable var2);

   void hideNotify(Display var1, Displayable var2);
}
