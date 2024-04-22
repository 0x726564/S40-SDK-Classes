package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.KeyMap;
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
   private static final int KR_SUPPRESS = 0;
   private static final int KR_PROPAGATE = 1;
   private static final int KR_COMMAND = 2;
   private static final boolean UNICOM_KEY_CUSTOMIZATIONS = false;
   private static final String unicom_up = "UP";
   private static final String unicom_down = "DOWN";
   private static final String unicom_select = "SELECT";
   private static final String unicom_left = "LEFT";
   private static final String unicom_right = "RIGHT";
   private static final String unicom_star = "*";
   private static final String unicom_pound = "#";
   private static final String unicom_num0 = "0";
   private static final String unicom_num1 = "1";
   private static final String unicom_num2 = "2";
   private static final String unicom_num3 = "3";
   private static final String unicom_num4 = "4";
   private static final String unicom_num5 = "5";
   private static final String unicom_num6 = "6";
   private static final String unicom_num7 = "7";
   private static final String unicom_num8 = "8";
   private static final String unicom_num9 = "9";
   static final Zone canvasFullMainZone;
   static final Zone canvasNormMainZone;
   static final Zone canvasTickMainZone;
   static final Zone canvasTickTickerZone;
   private boolean isFullCanvas;
   private boolean suppressKeyEvents;
   private boolean visible = false;
   private Vector visiblePlayers;

   protected Canvas() {
      synchronized(Display.LCDUILock) {
         if (this instanceof FullCanvas) {
            this.isFullCanvas = true;
            this.fullScreenMode = true;
            this.setupViewport();
         } else {
            this.isFullCanvas = false;
         }

      }
   }

   public final void repaint(int var1, int var2, int var3, int var4) {
      Zone var5;
      synchronized(Display.LCDUILock) {
         var5 = this.getMainZone();
      }

      if (var1 < var5.width && var2 < var5.height && var3 > 0 && var4 > 0) {
         if (var1 < 0) {
            var3 += var1;
            if (var3 <= 0) {
               return;
            }

            var1 = 0;
         }

         if (var1 > var5.width - var3) {
            var3 = var5.width - var1;
         }

         if (var2 < 0) {
            var4 += var2;
            if (var4 <= 0) {
               return;
            }

            var2 = 0;
         }

         if (var2 > var5.height - var4) {
            var4 = var5.height - var2;
         }

         var1 += var5.x;
         var2 += var5.y;
         this.repaintArea(var1, var2, var3, var4);
      }
   }

   public final void repaint() {
      Zone var1;
      synchronized(Display.LCDUILock) {
         var1 = this.getMainZone();
      }

      this.repaintArea(var1.x, var1.y, var1.width, var1.height);
   }

   public final void serviceRepaints() {
      if (this.visible) {
         this.myDisplay.requestServiceRepaints();
      }

   }

   public void setFullScreenMode(boolean var1) {
      if (this.isFullCanvas) {
         if (!var1) {
            throw new IllegalArgumentException();
         }
      } else {
         synchronized(Display.LCDUILock) {
            this.setFullScreenModeImpl(var1);
         }
      }

   }

   public int getGameAction(int var1) {
      int var2 = Displayable.keyMap.getGameAction(var1);
      if (-127 == var2) {
         throw new IllegalArgumentException("getGameAction: Invalid keyCode");
      } else {
         return var2;
      }
   }

   public int getKeyCode(int var1) {
      int var2 = Displayable.keyMap.getKeyCode(var1);
      if (Integer.MIN_VALUE == var2) {
         throw new IllegalArgumentException("getKeyCode: Invalid gameAction");
      } else {
         return var2;
      }
   }

   public String getKeyName(int var1) {
      String var2 = null;
      if (-127 == Displayable.keyMap.getGameAction(var1)) {
         throw new IllegalArgumentException("getKeyName: Invalid keyCode");
      } else {
         switch(var1) {
         case -11:
            var2 = new String("End");
            break;
         case -10:
            var2 = TextDatabase.getText(50);
            break;
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

         if (UNICOM_KEY_CUSTOMIZATIONS) {
            switch(var1) {
            case -7:
            case -4:
               var2 = "RIGHT";
               break;
            case -6:
            case -3:
               var2 = "LEFT";
               break;
            case -5:
               var2 = "SELECT";
               break;
            case -2:
               var2 = "DOWN";
               break;
            case -1:
               var2 = "UP";
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
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
            case 32:
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
               break;
            case 35:
               var2 = "#";
               break;
            case 42:
               var2 = "*";
               break;
            case 48:
               var2 = "0";
               break;
            case 49:
               var2 = "1";
               break;
            case 50:
               var2 = "2";
               break;
            case 51:
               var2 = "3";
               break;
            case 52:
               var2 = "4";
               break;
            case 53:
               var2 = "5";
               break;
            case 54:
               var2 = "6";
               break;
            case 55:
               var2 = "7";
               break;
            case 56:
               var2 = "8";
               break;
            case 57:
               var2 = "9";
            }
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

   boolean callsVisListOnHideNotify() {
      return true;
   }

   boolean isShownImpl() {
      return this.visible;
   }

   void callKeyPressed(int var1, int var2) {
      switch(this.determineKeyRouting(var1)) {
      case 1:
         synchronized(Display.calloutLock) {
            this.keyPressed(var1);
            break;
         }
      case 2:
         super.callKeyPressed(var1, var2);
      }

   }

   void callKeyReleased(int var1, int var2) {
      if (1 == this.determineKeyRouting(var1)) {
         synchronized(Display.calloutLock) {
            this.keyReleased(var1);
         }
      }

   }

   void callKeyRepeated(int var1, int var2) {
      if (1 == this.determineKeyRouting(var1)) {
         synchronized(Display.calloutLock) {
            this.keyRepeated(var1);
         }
      }

   }

   void callShowNotify(Display var1) {
      super.callShowNotify(var1);
      resetKeyStates();
      synchronized(Display.calloutLock) {
         this.showNotify();
      }

      synchronized(Display.LCDUILock) {
         this.visible = true;
      }
   }

   void callHideNotify(Display var1) {
      VisibilityListener var2 = this.visibilityListener;
      super.callHideNotify(var1);
      synchronized(Display.LCDUILock) {
         this.visible = false;
      }

      synchronized(Display.calloutLock) {
         this.hideNotify();
         if (var2 != null) {
            var2.hideNotify(var1, this);
         }

      }
   }

   void callPaint(Graphics var1) {
      boolean var2 = false;
      super.callPaint(var1);
      if (this.ticker != null) {
         synchronized(Display.LCDUILock) {
            if (!this.fullScreenMode && this.ticker != null) {
               if (var1.getClipY() + var1.getClipHeight() < canvasTickMainZone.y) {
                  var2 = true;
               } else {
                  var1.translate(canvasTickMainZone.x, canvasTickMainZone.y);
                  var1.clipRect(0, 0, canvasTickMainZone.width, canvasTickMainZone.height);
               }
            }
         }
      }

      if (this.visiblePlayers != null && this.visiblePlayers.size() != 0) {
         synchronized(Display.LCDUILock) {
            Zone var4 = this.getMainZone();
            int[] var5 = new int[]{var4.x, var4.y, var4.width, var4.height};

            for(int var6 = 0; var6 < this.visiblePlayers.size(); ++var6) {
               Integer var7 = (Integer)this.visiblePlayers.elementAt(var6);
               nNotifyPainted(var7, var5);
            }
         }
      }

      if (!var2) {
         this.paint(var1);
      }

   }

   void callInvalidate() {
      super.callInvalidate();
      synchronized(Display.LCDUILock) {
         this.layoutValid = true;
      }

      this.repaintFull();
      this.serviceRepaints();
   }

   Zone getMainZone() {
      if (this.fullScreenMode) {
         return canvasFullMainZone;
      } else {
         return this.ticker != null ? canvasTickMainZone : canvasNormMainZone;
      }
   }

   Zone getTickerZone() {
      return !this.fullScreenMode && this.ticker != null ? canvasTickTickerZone : null;
   }

   void showVideo(boolean var1, int var2, int var3, int var4, int var5, int var6) {
      if (var1) {
         this.addPlayerIfNotPresent(var2);
         this.repaint(var3, var4, var5, var6);
      } else {
         this.removePlayerIfPresent(var2);
         nNotifyVideoDisabled(var2);
      }

   }

   private void addPlayerIfNotPresent(int var1) {
      if (this.visiblePlayers == null) {
         this.visiblePlayers = new Vector();
      } else {
         Enumeration var2 = this.visiblePlayers.elements();

         while(var2.hasMoreElements()) {
            Integer var3 = (Integer)var2.nextElement();
            if (var3 == var1) {
               return;
            }
         }
      }

      this.visiblePlayers.addElement(new Integer(var1));
   }

   private void removePlayerIfPresent(int var1) {
      if (this.visiblePlayers != null) {
         Enumeration var2 = this.visiblePlayers.elements();

         while(var2.hasMoreElements()) {
            Integer var3 = (Integer)var2.nextElement();
            if (var3 == var1) {
               this.visiblePlayers.removeElement(var3);
            }
         }
      }

   }

   private int determineKeyRouting(int var1) {
      byte var2 = 1;
      if (DeviceInfo.isSoftkey(var1) && !this.isFullCanvas && (!this.fullScreenMode || this.commandListener != null && this.displayableCommands.isNotEmpty())) {
         var2 = 2;
         if (UNICOM_KEY_CUSTOMIZATIONS && (this.commandListener == null || !this.displayableCommands.isNotEmpty())) {
            var2 = 1;
         }
      } else if (this.suppressKeyEvents) {
         int var3 = Displayable.keyMap.getGameAction(var1);
         KeyMap var10001 = Displayable.keyMap;
         if (var3 != 0) {
            var10001 = Displayable.keyMap;
            if (var3 != -127) {
               var2 = 0;
            }
         }
      }

      return var2;
   }

   private void setFullScreenModeImpl(boolean var1) {
      if (this.fullScreenMode != var1) {
         this.fullScreenMode = var1;
         this.setupViewport();
         this.updateSoftkeys(true);
         if (this.displayed) {
            this.myDisplay.changeDisplayLayout(this);
            if (this.ticker != null) {
               if (this.fullScreenMode) {
                  this.ticker.hideFrom(this);
               } else {
                  this.ticker.showOn(this);
               }
            }
         }

         this.sizeChangeOccurred = true;
         this.invalidate();
      }

   }

   private static native void resetKeyStates();

   private static native void nNotifyPainted(int var0, int[] var1);

   private static native void nNotifyVideoDisabled(int var0);

   static {
      canvasFullMainZone = uistyle.getZone(0);
      canvasNormMainZone = uistyle.getZone(1);
      canvasTickMainZone = uistyle.getZone(44);
      canvasTickTickerZone = uistyle.getZone(45);
   }
}
