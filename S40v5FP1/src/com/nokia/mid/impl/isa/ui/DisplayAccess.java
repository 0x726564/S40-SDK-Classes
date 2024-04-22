package com.nokia.mid.impl.isa.ui;

import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.ui.lcdui.DisplayStateListener;
import com.nokia.mid.ui.lcdui.ForegroundUnavailableException;
import com.nokia.mid.ui.lcdui.VisibilityListener;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.midlet.MIDlet;

public interface DisplayAccess {
   void setForeground(MIDlet var1);

   DisplayAccess replaceDisplay(MIDlet var1);

   void resetDisplay(MIDlet var1);

   void flushImageToScreen(Displayable var1, Image var2, int var3, int var4, int var5, int var6);

   Image createImage(Pixmap var1);

   Pixmap getImagePixmap(Image var1);

   Item createMMItem();

   void showCanvasVideo(Canvas var1, int var2, boolean var3, int var4, int var5, int var6, int var7);

   boolean isDisplayActive(Display var1);

   void setCurrent(Display var1, Displayable var2, boolean var3, String var4) throws ForegroundUnavailableException;

   void setCurrent(Display var1, Alert var2, Displayable var3, boolean var4, String var5) throws ForegroundUnavailableException;

   void setCurrentItem(Display var1, Item var2, boolean var3, String var4) throws ForegroundUnavailableException;

   void setDisplayStateListener(Display var1, DisplayStateListener var2);

   void setVisibilityListener(Displayable var1, VisibilityListener var2);
}
