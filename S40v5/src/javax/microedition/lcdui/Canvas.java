package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.style.Zone;
import com.nokia.mid.ui.FullCanvas;
import com.nokia.mid.ui.lcdui.VisibilityListener;
import java.util.Enumeration;
import java.util.Vector;

public abstract class Canvas extends Displayable {
   public static final int UP = 1;
   public static final int LEFT = 2;
   public static final int RIGHT = 5;
   public static final int DOWN = 6;
   public static final int FIRE = 8;
   public static final int GAME_A = 9;
   public static final int GAME_B = 10;
   public static final int GAME_C = 11;
   public static final int GAME_D = 12;
   public static final int KEY_POUND = 35;
   public static final int KEY_STAR = 42;
   public static final int KEY_NUM0 = 48;
   public static final int KEY_NUM1 = 49;
   public static final int KEY_NUM2 = 50;
   public static final int KEY_NUM3 = 51;
   public static final int KEY_NUM4 = 52;
   public static final int KEY_NUM5 = 53;
   public static final int KEY_NUM6 = 54;
   public static final int KEY_NUM7 = 55;
   public static final int KEY_NUM8 = 56;
   public static final int KEY_NUM9 = 57;
   private static Zone aL;
   private static Zone aM;
   static final Zone aN;
   private static Zone aO;
   private boolean aP;
   private boolean visible = false;
   private Vector aQ;

   protected Canvas() {
      synchronized(Display.hG) {
         if (this instanceof FullCanvas) {
            this.aP = true;
            this.eS = true;
            this.ai();
         } else {
            this.aP = false;
         }

      }
   }

   public final void repaint(int var1, int var2, int var3, int var4) {
      Zone var5;
      synchronized(Display.hG) {
         var5 = this.getMainZone();
      }

      if (var1 < var5.width && var2 < var5.height && var3 > 0 && var4 > 0) {
         if (var1 < 0) {
            if ((var3 += var1) <= 0) {
               return;
            }

            var1 = 0;
         }

         if (var1 > var5.width - var3) {
            var3 = var5.width - var1;
         }

         if (var2 < 0) {
            if ((var4 += var2) <= 0) {
               return;
            }

            var2 = 0;
         }

         if (var2 > var5.height - var4) {
            var4 = var5.height - var2;
         }

         var1 += var5.x;
         var2 += var5.y;
         this.d(var1, var2, var3, var4);
      }
   }

   public final void repaint() {
      Zone var1;
      synchronized(Display.hG) {
         var1 = this.getMainZone();
      }

      this.d(var1.x, var1.y, var1.width, var1.height);
   }

   public final void serviceRepaints() {
      if (this.visible) {
         this.eV.aF();
      }

   }

   public void setFullScreenMode(boolean var1) {
      if (this.aP) {
         if (!var1) {
            throw new IllegalArgumentException();
         }
      } else {
         synchronized(Display.hG) {
            this.setFullScreenModeImpl(var1);
         }
      }
   }

   public int getGameAction(int var1) {
      int var2 = Displayable.eK.getGameAction(var1);
      if (-127 == var2) {
         throw new IllegalArgumentException("getGameAction: Invalid keyCode");
      } else {
         return var2;
      }
   }

   public int getKeyCode(int var1) {
      int var2 = Displayable.eK.getKeyCode(var1);
      if (Integer.MIN_VALUE == var2) {
         throw new IllegalArgumentException("getKeyCode: Invalid gameAction");
      } else {
         return var2;
      }
   }

   public String getKeyName(int var1) {
      String var2 = null;
      if (-127 == Displayable.eK.getGameAction(var1)) {
         throw new IllegalArgumentException("getKeyName: Invalid keyCode");
      } else {
         switch(var1) {
         case -23:
            var2 = TextDatabase.getText(77);
            break;
         case -22:
            var2 = TextDatabase.getText(76);
            break;
         case -21:
            var2 = TextDatabase.getText(75);
            break;
         case -20:
            var2 = TextDatabase.getText(74);
            break;
         case -19:
         case -18:
         case -17:
         case -16:
         case -15:
         case -14:
         case -13:
         case -12:
         case -9:
         case -8:
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 9:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 33:
         case 34:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         default:
            if (var1 >= 0 && var1 <= 65535) {
               var2 = (new Character((char)var1)).toString();
            } else {
               var2 = "UnnamedKey";
            }
            break;
         case -11:
            var2 = new String("End");
            break;
         case -10:
            var2 = TextDatabase.getText(50);
            break;
         case -7:
            var2 = TextDatabase.getText(51);
            break;
         case -6:
            var2 = TextDatabase.getText(52);
            break;
         case -5:
            var2 = TextDatabase.getText(53);
            break;
         case -4:
            var2 = TextDatabase.getText(54);
            break;
         case -3:
            var2 = TextDatabase.getText(55);
            break;
         case -2:
            var2 = TextDatabase.getText(56);
            break;
         case -1:
            var2 = TextDatabase.getText(57);
            break;
         case 8:
            var2 = TextDatabase.getText(70);
            break;
         case 10:
            var2 = TextDatabase.getText(72);
            break;
         case 32:
            var2 = TextDatabase.getText(71);
            break;
         case 35:
            var2 = TextDatabase.getText(58);
            break;
         case 42:
            var2 = TextDatabase.getText(59);
            break;
         case 48:
            var2 = TextDatabase.getText(60);
            break;
         case 49:
            var2 = TextDatabase.getText(61);
            break;
         case 50:
            var2 = TextDatabase.getText(62);
            break;
         case 51:
            var2 = TextDatabase.getText(63);
            break;
         case 52:
            var2 = TextDatabase.getText(64);
            break;
         case 53:
            var2 = TextDatabase.getText(65);
            break;
         case 54:
            var2 = TextDatabase.getText(66);
            break;
         case 55:
            var2 = TextDatabase.getText(67);
            break;
         case 56:
            var2 = TextDatabase.getText(68);
            break;
         case 57:
            var2 = TextDatabase.getText(69);
         }

         return var2;
      }
   }

   public boolean hasPointerEvents() {
      return DeviceInfo.hasPointerEvents();
   }

   public boolean hasPointerMotionEvents() {
      return DeviceInfo.hasPointerMotionEvents();
   }

   public boolean hasRepeatEvents() {
      return DeviceInfo.hasRepeatEvents();
   }

   public int getWidth() {
      return super.getWidth();
   }

   public int getHeight() {
      return super.getHeight();
   }

   public boolean isDoubleBuffered() {
      return true;
   }

   protected abstract void paint(Graphics var1);

   protected void keyPressed(int var1) {
   }

   protected void keyReleased(int var1) {
   }

   protected void keyRepeated(int var1) {
   }

   protected void pointerDragged(int var1, int var2) {
   }

   protected void pointerPressed(int var1, int var2) {
   }

   protected void pointerReleased(int var1, int var2) {
   }

   protected void showNotify() {
   }

   protected void hideNotify() {
   }

   final boolean B() {
      return true;
   }

   final boolean C() {
      return this.visible;
   }

   final void c(int var1, int var2) {
      switch(this.j(var1)) {
      case 1:
         synchronized(Display.hH) {
            this.keyPressed(var1);
            return;
         }
      case 2:
         super.c(var1, var2);
      default:
      }
   }

   final void h(int var1, int var2) {
      if (1 == this.j(var1)) {
         synchronized(Display.hH) {
            this.keyReleased(var1);
         }
      }
   }

   final void i(int var1, int var2) {
      if (1 == this.j(var1)) {
         synchronized(Display.hH) {
            this.keyRepeated(var1);
         }
      }
   }

   final void a(Display var1) {
      super.a(var1);
      resetKeyStates();
      synchronized(Display.hH) {
         this.showNotify();
      }

      synchronized(Display.hG) {
         this.visible = true;
      }
   }

   final void b(Display var1) {
      VisibilityListener var2 = this.fb;
      super.b(var1);
      synchronized(Display.hG) {
         this.visible = false;
      }

      synchronized(Display.hH) {
         this.hideNotify();
         if (var2 != null) {
            var2.hideNotify(var1, this);
         }

      }
   }

   final void b(Graphics var1) {
      boolean var2 = false;
      super.b(var1);
      if (this.eQ != null) {
         synchronized(Display.hG) {
            if (!this.eS && this.eQ != null) {
               if (var1.getClipY() + var1.getClipHeight() < aN.y) {
                  var2 = true;
               } else {
                  var1.translate(aN.x, aN.y);
                  var1.clipRect(0, 0, aN.width, aN.height);
               }
            }
         }
      }

      if (this.aQ != null && this.aQ.size() != 0) {
         synchronized(Display.hG) {
            Zone var4 = this.getMainZone();
            int[] var9 = new int[]{var4.x, var4.y, var4.width, var4.height};

            for(int var5 = 0; var5 < this.aQ.size(); ++var5) {
               nNotifyPainted((Integer)this.aQ.elementAt(var5), var9);
            }
         }
      }

      if (!var2) {
         this.paint(var1);
      }

   }

   final void D() {
      super.D();
      synchronized(Display.hG) {
         this.fh = true;
      }

      this.ag();
      this.serviceRepaints();
   }

   Zone getMainZone() {
      if (this.eS) {
         return aL;
      } else {
         return this.eQ != null ? aN : aM;
      }
   }

   Zone getTickerZone() {
      return !this.eS && this.eQ != null ? aO : null;
   }

   final void a(boolean var1, int var2, int var3, int var4, int var5, int var6) {
      if (!var1) {
         this.i(var2);
         nNotifyVideoDisabled(var2);
      } else {
         label23: {
            int var7 = var2;
            if (this.aQ == null) {
               this.aQ = new Vector();
            } else {
               Enumeration var8 = this.aQ.elements();

               while(var8.hasMoreElements()) {
                  if ((Integer)var8.nextElement() == var7) {
                     break label23;
                  }
               }
            }

            this.aQ.addElement(new Integer(var7));
         }

         this.repaint(var3, var4, var5, var6);
      }
   }

   private void i(int var1) {
      if (this.aQ != null) {
         Enumeration var2 = this.aQ.elements();

         while(var2.hasMoreElements()) {
            Integer var3;
            if (var3 = (Integer)var2.nextElement() == var1) {
               this.aQ.removeElement(var3);
            }
         }
      }

   }

   private int j(int var1) {
      byte var2 = 1;
      if (!this.aP && (!this.eS || this.fa != null && this.eW.ar()) && DeviceInfo.isSoftkey(var1)) {
         var2 = 2;
      }

      return var2;
   }

   private void setFullScreenModeImpl(boolean var1) {
      if (this.eS != var1) {
         this.eS = var1;
         this.ai();
         this.c(true);
         if (this.eU) {
            this.eV.f(this);
            if (this.eQ != null) {
               if (this.eS) {
                  this.eQ.b(this);
               } else {
                  this.eQ.a(this);
               }
            }
         }

         this.eT = true;
         this.invalidate();
      }

   }

   private static native void resetKeyStates();

   private static native void nNotifyPainted(int var0, int[] var1);

   private static native void nNotifyVideoDisabled(int var0);

   static {
      aL = eI.getZone(0);
      aM = eI.getZone(1);
      aN = eI.getZone(45);
      aO = eI.getZone(46);
   }
}
