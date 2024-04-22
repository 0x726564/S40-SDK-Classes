package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.style.Zone;
import com.nokia.mid.ui.FullCanvas;
import com.nokia.mid.ui.JoystickEventListener;
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
   static final Zone canvasFullMainZone;
   static final Zone canvasNormMainZone;
   static final Zone canvasTickMainZone;
   private boolean isFullCanvas;
   private boolean suppressKeyEvents;
   private boolean visible = false;
   private Vector visiblePlayers;
   private JoystickEventListener joystickEventListener = null;

   protected Canvas() {
      synchronized(Display.LCDUILock) {
         if (this instanceof FullCanvas) {
            this.isFullCanvas = true;
            this.fullScreenMode = true;
            this.setupViewport();
         } else {
            this.isFullCanvas = false;
         }

         if (this instanceof JoystickEventListener) {
            this.joystickEventListener = (JoystickEventListener)this;
         }

      }
   }

   public final void repaint(int x, int y, int width, int height) {
      Zone currentMainZone;
      synchronized(Display.LCDUILock) {
         currentMainZone = this.getMainZone();
      }

      if (x < currentMainZone.width && y < currentMainZone.height && width > 0 && height > 0) {
         if (x < 0) {
            width += x;
            if (width <= 0) {
               return;
            }

            x = 0;
         }

         if (x > currentMainZone.width - width) {
            width = currentMainZone.width - x;
         }

         if (y < 0) {
            height += y;
            if (height <= 0) {
               return;
            }

            y = 0;
         }

         if (y > currentMainZone.height - height) {
            height = currentMainZone.height - y;
         }

         x += currentMainZone.x;
         y += currentMainZone.y;
         this.repaintArea(x, y, width, height);
      }
   }

   public final void repaint() {
      Zone currentMainZone;
      synchronized(Display.LCDUILock) {
         currentMainZone = this.getMainZone();
      }

      this.repaintArea(currentMainZone.x, currentMainZone.y, currentMainZone.width, currentMainZone.height);
   }

   public final void serviceRepaints() {
      if (this.visible) {
         this.myDisplay.requestServiceRepaints();
      }

   }

   public void setFullScreenMode(boolean mode) {
      if (this.isFullCanvas) {
         if (!mode) {
            throw new IllegalArgumentException();
         }
      } else {
         synchronized(Display.LCDUILock) {
            this.setFullScreenModeImpl(mode);
         }
      }

   }

   public int getGameAction(int keyCode) {
      int gameAction = Displayable.keyMap.getGameAction(keyCode);
      if (-127 == gameAction) {
         throw new IllegalArgumentException("getGameAction: Invalid keyCode");
      } else {
         return gameAction;
      }
   }

   public int getKeyCode(int gameAction) {
      int keyCode = Displayable.keyMap.getKeyCode(gameAction);
      if (Integer.MIN_VALUE == keyCode) {
         throw new IllegalArgumentException("getKeyCode: Invalid gameAction");
      } else {
         return keyCode;
      }
   }

   public String getKeyName(int keyCode) {
      String keyName = null;
      if (-127 == Displayable.keyMap.getGameAction(keyCode)) {
         throw new IllegalArgumentException("getKeyName: Invalid keyCode");
      } else {
         switch(keyCode) {
         case -23:
            keyName = TextDatabase.getText(77);
            break;
         case -22:
            keyName = TextDatabase.getText(76);
            break;
         case -21:
            keyName = TextDatabase.getText(75);
            break;
         case -20:
            keyName = TextDatabase.getText(74);
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
            if (keyCode >= 0 && keyCode <= 65535) {
               keyName = (new Character((char)keyCode)).toString();
            } else {
               keyName = "UnnamedKey";
            }
            break;
         case -11:
            keyName = new String("End");
            break;
         case -10:
            keyName = TextDatabase.getText(50);
            break;
         case -7:
            keyName = TextDatabase.getText(51);
            break;
         case -6:
            keyName = TextDatabase.getText(52);
            break;
         case -5:
            keyName = TextDatabase.getText(53);
            break;
         case -4:
            keyName = TextDatabase.getText(54);
            break;
         case -3:
            keyName = TextDatabase.getText(55);
            break;
         case -2:
            keyName = TextDatabase.getText(56);
            break;
         case -1:
            keyName = TextDatabase.getText(57);
            break;
         case 8:
            keyName = TextDatabase.getText(70);
            break;
         case 10:
            keyName = TextDatabase.getText(72);
            break;
         case 32:
            keyName = TextDatabase.getText(71);
            break;
         case 35:
            keyName = TextDatabase.getText(58);
            break;
         case 42:
            keyName = TextDatabase.getText(59);
            break;
         case 48:
            keyName = TextDatabase.getText(60);
            break;
         case 49:
            keyName = TextDatabase.getText(61);
            break;
         case 50:
            keyName = TextDatabase.getText(62);
            break;
         case 51:
            keyName = TextDatabase.getText(63);
            break;
         case 52:
            keyName = TextDatabase.getText(64);
            break;
         case 53:
            keyName = TextDatabase.getText(65);
            break;
         case 54:
            keyName = TextDatabase.getText(66);
            break;
         case 55:
            keyName = TextDatabase.getText(67);
            break;
         case 56:
            keyName = TextDatabase.getText(68);
            break;
         case 57:
            keyName = TextDatabase.getText(69);
         }

         return keyName;
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

   protected void keyPressed(int keyCode) {
   }

   protected void keyReleased(int keyCode) {
   }

   protected void keyRepeated(int keyCode) {
   }

   protected void pointerDragged(int x, int y) {
      if (this.myDisplay != null && this.myDisplay.pointer_registration == 2) {
         this.myDisplay.registerForPointerEvents(1);
      }

   }

   protected void pointerPressed(int x, int y) {
   }

   protected void pointerReleased(int x, int y) {
   }

   protected void showNotify() {
   }

   protected void hideNotify() {
   }

   void callPointerPress(int x, int y) {
      if (this.pointerListeningState == 1) {
         this.pointerListeningState = 2;
         this.pointerPressed(x, y);
      }

   }

   void callPointerRelease(int x, int y) {
      if (this.pointerListeningState == 2) {
         this.pointerListeningState = 1;
         this.pointerReleased(x, y);
      }

   }

   void callPointerMove(int x, int y) {
      if (this.pointerListeningState != 0) {
         this.pointerDragged(x, y);
      }

   }

   void callPenDiscontinue(int x, int y) {
      if (this.pointerListeningState == 2) {
         this.pointerListeningState = 1;
      }

   }

   void callJoystickEvent(int x, int y) {
      if (this.joystickEventListener != null) {
         this.joystickEventListener.joystickEvent(x, y);
      }

   }

   boolean callsVisListOnHideNotify() {
      return true;
   }

   boolean isShownImpl() {
      return this.visible;
   }

   void callKeyPressed(int keyCode, int keyDataIdx) {
      switch(this.determineKeyRouting(keyCode)) {
      case 1:
         synchronized(Display.calloutLock) {
            this.keyPressed(keyCode);
            break;
         }
      case 2:
         super.callKeyPressed(keyCode, keyDataIdx);
      }

   }

   void callKeyReleased(int keyCode, int keyDataIdx) {
      if (1 == this.determineKeyRouting(keyCode)) {
         synchronized(Display.calloutLock) {
            this.keyReleased(keyCode);
         }
      }

   }

   void callKeyRepeated(int keyCode, int keyDataIdx) {
      if (1 == this.determineKeyRouting(keyCode)) {
         synchronized(Display.calloutLock) {
            this.keyRepeated(keyCode);
         }
      }

   }

   void callShowNotify(Display d) {
      this.pointerListeningState = 1;
      super.callShowNotify(d);
      resetKeyStates();
      synchronized(Display.calloutLock) {
         this.showNotify();
      }

      synchronized(Display.LCDUILock) {
         this.visible = true;
      }

      uistyle.hideIndex(this);
      this.myDisplay.registerForPointerEvents(2);
      if (this.joystickEventListener != null) {
         this.myDisplay.registerForPointerEvents(4);
      }

   }

   void callHideNotify(Display d) {
      VisibilityListener localVisibilityListener = this.visibilityListener;
      super.callHideNotify(d);
      synchronized(Display.LCDUILock) {
         this.visible = false;
      }

      if (this.joystickEventListener != null) {
         this.myDisplay.registerForPointerEvents(3);
      }

      synchronized(Display.calloutLock) {
         this.hideNotify();
         if (localVisibilityListener != null) {
            localVisibilityListener.hideNotify(d, this);
         }

      }
   }

   void callPaint(Graphics g) {
      Zone mainZone = this.getMainZone();
      super.callPaint(g);
      synchronized(Display.LCDUILock) {
         g.translate(mainZone.x, mainZone.y);
         g.clipRect(0, 0, mainZone.width, mainZone.height);
      }

      if (this.visiblePlayers != null && this.visiblePlayers.size() != 0) {
         synchronized(Display.LCDUILock) {
            int[] screenClip = new int[]{mainZone.x, mainZone.y, mainZone.width, mainZone.height};

            for(int i = 0; i < this.visiblePlayers.size(); ++i) {
               Integer pi = (Integer)this.visiblePlayers.elementAt(i);
               nNotifyPainted(pi, screenClip);
            }
         }
      }

      this.paint(g);
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

   void showVideo(boolean show, int playerId, int x, int y, int w, int h) {
      if (show) {
         this.addPlayerIfNotPresent(playerId);
         this.repaint(x, y, w, h);
      } else {
         this.removePlayerIfPresent(playerId);
         nNotifyVideoDisabled(playerId);
      }

   }

   private void addPlayerIfNotPresent(int playerId) {
      if (this.visiblePlayers == null) {
         this.visiblePlayers = new Vector();
      } else {
         Enumeration e = this.visiblePlayers.elements();

         while(e.hasMoreElements()) {
            Integer pi = (Integer)e.nextElement();
            if (pi == playerId) {
               return;
            }
         }
      }

      this.visiblePlayers.addElement(new Integer(playerId));
   }

   private void removePlayerIfPresent(int playerId) {
      if (this.visiblePlayers != null) {
         Enumeration e = this.visiblePlayers.elements();

         while(e.hasMoreElements()) {
            Integer pi = (Integer)e.nextElement();
            if (pi == playerId) {
               this.visiblePlayers.removeElement(pi);
            }
         }
      }

   }

   private int determineKeyRouting(int keyCode) {
      int keyRouting = 1;
      if (!this.isFullCanvas && (!this.fullScreenMode || this.commandListener != null && this.displayableCommands.isNotEmpty()) && DeviceInfo.isSoftkey(keyCode)) {
         keyRouting = 2;
      } else if (this.suppressKeyEvents) {
         int action = Displayable.keyMap.getGameAction(keyCode);
         if (action != 0 && action != -127) {
            keyRouting = 0;
         }
      }

      return keyRouting;
   }

   private void setFullScreenModeImpl(boolean mode) {
      if (this.fullScreenMode != mode) {
         this.fullScreenMode = mode;
         this.setupViewport();
         this.updateSoftkeys(true);
         if (this.displayed) {
            this.myDisplay.changeDisplayLayout(this);
            if (this.ticker != null) {
               if (this.fullScreenMode) {
                  this.ticker.hideNotify();
               } else {
                  this.ticker.showNotify();
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
      canvasTickMainZone = uistyle.getZone(41);
   }
}
