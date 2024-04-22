package com.nokia.mid.impl.isa.ui;

import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import javax.microedition.lcdui.Canvas;
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
}
