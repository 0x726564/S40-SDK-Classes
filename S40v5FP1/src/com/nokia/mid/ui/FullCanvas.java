package com.nokia.mid.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

public abstract class FullCanvas extends Canvas {
   public static final int KEY_SOFTKEY1 = -6;
   public static final int KEY_SOFTKEY2 = -7;
   public static final int KEY_SEND = -10;
   public static final int KEY_END = -11;
   public static final int KEY_SOFTKEY3 = -5;
   public static final int KEY_UP_ARROW = -1;
   public static final int KEY_DOWN_ARROW = -2;
   public static final int KEY_LEFT_ARROW = -3;
   public static final int KEY_RIGHT_ARROW = -4;

   protected FullCanvas() {
   }

   public void addCommand(Command cmd) {
      throw new IllegalStateException();
   }

   public void setCommandListener(CommandListener l) {
      throw new IllegalStateException();
   }
}
