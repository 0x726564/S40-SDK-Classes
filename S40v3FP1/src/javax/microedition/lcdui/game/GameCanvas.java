package javax.microedition.lcdui.game;

import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.DisplayAccess;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.MIDletAccess;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public abstract class GameCanvas extends Canvas {
   public static final int UP_PRESSED = 2;
   public static final int DOWN_PRESSED = 64;
   public static final int LEFT_PRESSED = 4;
   public static final int RIGHT_PRESSED = 32;
   public static final int FIRE_PRESSED = 256;
   public static final int GAME_A_PRESSED = 512;
   public static final int GAME_B_PRESSED = 1024;
   public static final int GAME_C_PRESSED = 2048;
   public static final int GAME_D_PRESSED = 4096;
   private Image offScreenBuffer = Image.createImage(DeviceInfo.getDisplayWidth(3), DeviceInfo.getDisplayHeight(3));
   private DisplayAccess da;

   protected GameCanvas(boolean var1) {
      this.setSuppressKeyEvents(var1);
   }

   public void flushGraphics() {
      this.flushGraphics(0, 0, this.getWidth(), this.getHeight());
   }

   public void flushGraphics(int var1, int var2, int var3, int var4) {
      if (var3 > 0 && var4 > 0) {
         if (this.da != null) {
            this.da.flushImageToScreen(this, this.offScreenBuffer, var1, var2, var3, var4);
         } else {
            MIDletAccess var5 = InitJALM.s_getMIDletAccessor();
            if (var5 != null && (this.da = var5.getDisplayAccessor()) != null) {
               this.da.flushImageToScreen(this, this.offScreenBuffer, var1, var2, var3, var4);
            }
         }
      }

   }

   protected Graphics getGraphics() {
      return this.offScreenBuffer.getGraphics();
   }

   public int getKeyStates() {
      return this.isShown() ? getKeyStatesImpl() : 0;
   }

   public void paint(Graphics var1) {
      var1.drawImage(this.offScreenBuffer, 0, 0, 20);
   }

   private static native int getKeyStatesImpl();

   private native void setSuppressKeyEvents(boolean var1);
}
