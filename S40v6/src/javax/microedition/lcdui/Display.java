package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceControl;
import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.DisplayAccess;
import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.EventProducer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.JavaEventGenerator;
import com.nokia.mid.impl.isa.ui.MIDletAccess;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import com.nokia.mid.ui.lcdui.DisplayStateListener;
import com.nokia.mid.ui.lcdui.ForegroundUnavailableException;
import com.nokia.mid.ui.lcdui.VisibilityListener;
import java.util.Timer;
import java.util.Vector;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;

public class Display {
   public static final int LIST_ELEMENT = 1;
   public static final int CHOICE_GROUP_ELEMENT = 2;
   public static final int ALERT = 3;
   public static final int COLOR_BACKGROUND = 0;
   public static final int COLOR_FOREGROUND = 1;
   public static final int COLOR_HIGHLIGHTED_BACKGROUND = 2;
   public static final int COLOR_HIGHLIGHTED_FOREGROUND = 3;
   public static final int COLOR_BORDER = 4;
   public static final int COLOR_HIGHLIGHTED_BORDER = 5;
   static final int POINTER_REGISTRATION_NONE = 0;
   static final int POINTER_REGISTRATION_PEN_BASIC = 1;
   static final int POINTER_REGISTRATION_PEN_STANDARD = 2;
   static final int JOYSTICK_EVENT_REGISTRATION_NONE = 3;
   static final int JOYSTICK_EVENT_REGISTRATION_STANDARD = 4;
   private static final int DCS_IDLE = 0;
   private static final int DCS_CHANGE_PENDING = 1;
   private static final int DCS_CHANGE_IN_PROGRESS = 2;
   private static final int DCS_CHANGE_IN_PROGRESS_AND_PENDING = 3;
   private static final int DRS_IDLE = 0;
   private static final int DRS_REPAINT_PENDING = 1;
   private static final int DRS_FULL_REPAINT_PENDING = 2;
   private static final int MRDSCS_IDLE = 0;
   private static final int MRDSCS_GOTO_BG = 1;
   private static final int MRDSCS_GOTO_FG = 2;
   private static final int TITLE_MAX_LENGTH = 512;
   static final Object LCDUILock = new Object();
   static final Object calloutLock = new Object();
   static final Object displayTransitionLock = new Object();
   private static final int fullCanvasPaintRectX2 = DeviceInfo.getDisplayWidth(3);
   private static final int fullCanvasPaintRectY2 = DeviceInfo.getDisplayHeight(3);
   private static final int canvasPaintRectX2 = DeviceInfo.getDisplayWidth(2);
   private static final int canvasPaintRectY2 = DeviceInfo.getDisplayHeight(2);
   private static final int screenPaintRectX2 = DeviceInfo.getDisplayWidth(0);
   private static final int screenPaintRectY2 = DeviceInfo.getDisplayHeight(0);
   private static final int withTickerPaintRectX2 = DeviceInfo.getDisplayWidth(6);
   private static final int withTickerPaintRectY2 = DeviceInfo.getDisplayHeight(6);
   private static final Form defaultDisplayable = new Form("");
   private static final Command defaultQuitCmd = new Command(10, 13);
   private static final Form defaultNoDispSetDisplayable = new Form("");
   private static int layout;
   private static int layout_under_popup = 0;
   private static Vector displayEventParamQueue = new Vector();
   private static String statusZoneString;
   private static final EventProducer eventDispatcher = InitJALM.s_getEventProducer();
   static Display theActiveDisplay;
   private static MIDletAccess midletAccessor;
   private static boolean barbiePlatform;
   private static int midletId;
   static boolean displayActive;
   DisplayStateListener displayStateListener;
   private static boolean displayIsVisible;
   private static boolean widgetNotificationRequired = true;
   private int midletRequestedDSCS;
   private boolean titleChangeWhileHidden;
   int pointer_registration;
   private final OptionsMenu optionsMenu;
   private final TruncatedItemScreen truncatedItemScreen;
   private final Display.DisplayAccessor myDa;
   private int stateOfScreenChange;
   private int stateOfRepaint;
   private Displayable currentDisplayable;
   private Displayable midletCurrentDisplayable;
   private Displayable pendingDisplayable;
   private Displayable pendingParentDisplayable;
   private Displayable inProgressDisplayable;
   private Item pendingItem;
   private int paintRectX1;
   private int paintRectY1;
   private int paintRectX2;
   private int paintRectY2;
   private DirectGraphicsImpl displayGraphics;
   private MIDlet myMIDlet;
   private String myMIDletName;
   private boolean pendingMidletScreenChange;
   private boolean screenChangeInProgress;
   private boolean foldClosed;

   private Display(MIDlet m) {
      this.titleChangeWhileHidden = false;
      this.pointer_registration = 0;
      this.displayGraphics = null;
      this.pendingMidletScreenChange = false;
      this.screenChangeInProgress = false;
      this.foldClosed = false;
      synchronized(LCDUILock) {
         this.myDa = new Display.DisplayAccessor();
         if (midletAccessor == null) {
            midletAccessor = InitJALM.s_getMIDletAccessor();
         }

         eventDispatcher.attachEventConsumer(2, this.myDa);
         eventDispatcher.attachEventConsumer(1, this.myDa);
         eventDispatcher.attachEventConsumer(4, this.myDa);
         defaultDisplayable.setCommandListener(this.myDa);
         this.optionsMenu = NativeOptionsMenu.getNativeOptionsMenu(this);
         this.truncatedItemScreen = TruncatedItemScreen.getTruncatedItemScreen();
      }

      this.resetDisplay(m);
   }

   public static Display getDisplay(MIDlet m) {
      if (m == null) {
         throw new NullPointerException();
      } else {
         synchronized(LCDUILock) {
            if (theActiveDisplay == null) {
               theActiveDisplay = new Display(m);
               InitJALM.s_setDisplayAccessor(theActiveDisplay.myDa);
            }

            if (theActiveDisplay.myMIDlet != m) {
               throw new RuntimeException("Error: Attempt to getDisplay by application after destroyApp called.");
            }
         }

         return theActiveDisplay;
      }
   }

   public void callSerially(Runnable r) {
      if (r == null) {
         throw new NullPointerException();
      } else {
         synchronized(LCDUILock) {
            if (this == theActiveDisplay) {
               displayEventParamQueue.addElement(r);
               JavaEventGenerator.s_generateEvent(0, 2, 3, 0);
            }

         }
      }
   }

   public boolean flashBacklight(int duration) {
      if (duration < 0) {
         throw new IllegalArgumentException("Display.flashBacklight: negative duration not allowed.");
      } else {
         synchronized(LCDUILock) {
            if (!displayActive || this.currentDisplayable == null || !this.currentDisplayable.isShownImpl() && !this.currentDisplayable.isPowerSavingActive()) {
               return false;
            }
         }

         DeviceControl.flashLights((long)duration);
         return true;
      }
   }

   public int getBestImageHeight(int imageType) {
      Zone zone = this.getImageZone(imageType);
      return zone.height - zone.getMarginTop() - zone.getMarginBottom();
   }

   public int getBestImageWidth(int imageType) {
      Zone zone = this.getImageZone(imageType);
      return zone.width - zone.getMarginLeft() - zone.getMarginRight();
   }

   public int getBorderStyle(boolean highlighted) {
      return 0;
   }

   public int getColor(int colorSpecifier) {
      switch(colorSpecifier) {
      case 0:
         return UIStyle.COLOUR_BACKGROUND;
      case 1:
         return UIStyle.COLOUR_TEXT;
      case 2:
         return UIStyle.COLOUR_HIGHLIGHT;
      case 3:
         return UIStyle.COLOUR_HIGHLIGHT_TEXT;
      case 4:
         return UIStyle.COLOUR_BACKGROUND;
      case 5:
         return UIStyle.COLOUR_HIGHLIGHT;
      default:
         throw new IllegalArgumentException();
      }
   }

   public Displayable getCurrent() {
      synchronized(LCDUILock) {
         return this.getCurrentImpl();
      }
   }

   public boolean isColor() {
      synchronized(LCDUILock) {
         return DeviceInfo.isColor();
      }
   }

   public int numAlphaLevels() {
      synchronized(LCDUILock) {
         return DeviceInfo.numAlphaLevels();
      }
   }

   public int numColors() {
      synchronized(LCDUILock) {
         return DeviceInfo.numColors();
      }
   }

   public void setCurrent(Displayable nextDisplayable) {
      if (nextDisplayable != null) {
         try {
            this.setCurrent(nextDisplayable, false, (String)null, (Image)null);
         } catch (ForegroundUnavailableException var3) {
         }
      }

   }

   void setCurrent(Displayable nextDisplayable, boolean immediateFg, String softnoteText, Image icon) throws ForegroundUnavailableException {
      synchronized(LCDUILock) {
         if (this == theActiveDisplay) {
            if (nextDisplayable != null && nextDisplayable instanceof Alert) {
               Displayable realPrecedingDisplayable = this.currentDisplayable;
               if (this.stateOfScreenChange != 2 && this.stateOfScreenChange != 3) {
                  if (this.currentDisplayable.isSystemScreen()) {
                     realPrecedingDisplayable = this.midletCurrentDisplayable;
                  }
               } else if (this.inProgressDisplayable.isSystemScreen()) {
                  if (this.currentDisplayable.isSystemScreen()) {
                     realPrecedingDisplayable = this.midletCurrentDisplayable;
                  }
               } else {
                  realPrecedingDisplayable = this.inProgressDisplayable;
               }

               if (realPrecedingDisplayable == nextDisplayable) {
                  if (realPrecedingDisplayable instanceof Alert && realPrecedingDisplayable == this.currentDisplayable && this.stateOfScreenChange != 2 && this.stateOfScreenChange != 3) {
                     ((Alert)realPrecedingDisplayable).restartTimerIfNecessary();
                  }

                  return;
               }

               if (realPrecedingDisplayable instanceof Alert) {
                  throw new IllegalArgumentException("Current Displayable is an Alert");
               }

               ((Alert)nextDisplayable).setReturnScreen(realPrecedingDisplayable);
            }

            this.setCurrentImpl((Displayable)null, nextDisplayable, (Item)null, immediateFg, softnoteText, icon, true, false);
         }

      }
   }

   public void setCurrent(Alert alert, Displayable nextDisplayable) {
      try {
         this.setCurrent(alert, nextDisplayable, false, (String)null, (Image)null);
      } catch (ForegroundUnavailableException var4) {
      }

   }

   void setCurrent(Alert alert, Displayable nextDisplayable, boolean immediateFg, String softnoteText, Image icon) throws ForegroundUnavailableException {
      if (alert != null && nextDisplayable != null) {
         if (nextDisplayable instanceof Alert) {
            throw new IllegalArgumentException();
         } else {
            synchronized(LCDUILock) {
               if (this == theActiveDisplay) {
                  alert.setReturnScreen(nextDisplayable);
                  if (alert == this.currentDisplayable && this.stateOfScreenChange != 2 && this.stateOfScreenChange != 3) {
                     alert.restartTimerIfNecessary();
                  }

                  this.setCurrentImpl((Displayable)null, alert, (Item)null, immediateFg, softnoteText, icon, true, false);
               }

            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void setCurrentItem(Item item) {
      try {
         this.setCurrentItem(item, false, (String)null);
      } catch (ForegroundUnavailableException var3) {
      }

   }

   void setCurrentItem(Item item, boolean immediateFg, String softnoteText) throws ForegroundUnavailableException {
      synchronized(LCDUILock) {
         Screen nextDisplayable = item.owner;
         if (nextDisplayable != null && !(nextDisplayable instanceof Alert)) {
            if (this == theActiveDisplay) {
               this.setCurrentImpl((Displayable)null, nextDisplayable, nextDisplayable instanceof Form ? item : null, immediateFg, softnoteText, (Image)null, true, false);
            }

         } else {
            throw new IllegalStateException();
         }
      }
   }

   public boolean vibrate(int duration) {
      if (duration < 0) {
         throw new IllegalArgumentException("Display.vibrate: negative duration not allowed.");
      } else {
         synchronized(LCDUILock) {
            if (!displayIsVisible && (this.currentDisplayable == null || !this.currentDisplayable.isNativeDelegate())) {
               return false;
            }
         }

         if (duration == 0) {
            DeviceControl.stopVibra();
            return true;
         } else {
            try {
               DeviceControl.startVibra(100, (long)duration);
            } catch (IllegalStateException var4) {
            }

            return true;
         }
      }
   }

   void changeDisplayLayout(Displayable requestingDisplayable) {
      if (displayActive && this == theActiveDisplay) {
         Displayable realCurrentDisplayable = this.currentDisplayable;
         if (this.stateOfScreenChange == 2 || this.stateOfScreenChange == 3) {
            realCurrentDisplayable = this.inProgressDisplayable;
         }

         if (requestingDisplayable == realCurrentDisplayable) {
            changeDisplayLayoutImpl(requestingDisplayable);
            this.displayGraphics = new DirectGraphicsImpl(Pixmap.getUpdatedDisplayPixmap().getGraphics());
         }
      }

   }

   static Display getActiveDisplay() {
      return theActiveDisplay;
   }

   Displayable getCurrentImpl() {
      Displayable dispRetVal = null;
      if (this.currentDisplayable != defaultDisplayable && this.currentDisplayable != defaultNoDispSetDisplayable && this.currentDisplayable != null) {
         if (this.currentDisplayable.isSystemScreen()) {
            dispRetVal = this.midletCurrentDisplayable;
         } else {
            dispRetVal = this.currentDisplayable;
         }
      }

      return dispRetVal;
   }

   OptionsMenu getOptionsMenu() {
      return this.optionsMenu;
   }

   String getMIDletName() {
      return this.myMIDletName;
   }

   TruncatedItemScreen getTruncatedItemScreen() {
      return this.truncatedItemScreen;
   }

   Displayable getCurrentTopOfStackDisplayable() {
      return this.currentDisplayable;
   }

   boolean isScreenChangeInProgress() {
      return this.screenChangeInProgress;
   }

   boolean isScreenIdle() {
      return this.stateOfScreenChange == 0;
   }

   void requestRepaint(Displayable d, int x, int y, int w, int h) {
      synchronized(LCDUILock) {
         if (this == theActiveDisplay && d.isShownImpl() && this.stateOfRepaint != 2) {
            this.paintRectX1 = Math.min(x, this.paintRectX1);
            this.paintRectY1 = Math.min(y, this.paintRectY1);
            this.paintRectX2 = Math.max(x + w, this.paintRectX2);
            this.paintRectY2 = Math.max(y + h, this.paintRectY2);
            if (this.stateOfRepaint == 0) {
               JavaEventGenerator.s_generateEvent(0, 2, 2, layout);
               this.stateOfRepaint = 1;
            }
         }

      }
   }

   void requestFullRepaint(Displayable d) {
      synchronized(LCDUILock) {
         if (this == theActiveDisplay && d.isShownImpl()) {
            if (this.stateOfRepaint == 0) {
               JavaEventGenerator.s_generateEvent(0, 2, 2, layout);
            }

            this.stateOfRepaint = 2;
         }

      }
   }

   void requestInvalidate(Displayable d) {
      synchronized(LCDUILock) {
         if (this == theActiveDisplay) {
            displayEventParamQueue.addElement(d);
            JavaEventGenerator.s_generateEvent(0, 2, 6, 0);
         }

      }
   }

   void requestItemStateChanged(Displayable d, Item i) {
      synchronized(LCDUILock) {
         if (this == theActiveDisplay) {
            displayEventParamQueue.addElement(d);
            displayEventParamQueue.addElement(i);
            JavaEventGenerator.s_generateEvent(0, 2, 7, 0);
         }

      }
   }

   void requestCommandAction(Command c, Object source, Object listener) {
      if (c != null && source != null && listener != null) {
         synchronized(LCDUILock) {
            if (this == theActiveDisplay) {
               displayEventParamQueue.addElement(c);
               displayEventParamQueue.addElement(source);
               displayEventParamQueue.addElement(listener);
               JavaEventGenerator.s_generateEvent(0, 2, 8, 0);
            }

         }
      }
   }

   void requestClearScreen(Displayable d) {
      if (this.stateOfScreenChange != 1 && this == theActiveDisplay && displayActive && d == this.currentDisplayable && this.displayGraphics != null) {
         this.displayGraphics.reset();
         d.clearScreen(this.displayGraphics);
      }

   }

   void setCurrentInternal(Displayable parentDisplayable, Displayable nextDisplayable) {
      if (nextDisplayable != null && (nextDisplayable.isSystemScreen() || parentDisplayable == null)) {
         if (this == theActiveDisplay) {
            try {
               this.setCurrentImpl(parentDisplayable, nextDisplayable, (Item)null, false, (String)null, (Image)null, false, false);
            } catch (ForegroundUnavailableException var4) {
            }
         }

      }
   }

   final void registerTimer(Timer t) {
      midletAccessor.registerTimer(this.myMIDlet, t);
   }

   final void deregisterTimer(Timer t) {
      midletAccessor.deregisterTimer(this.myMIDlet, t);
   }

   void requestServiceRepaints() {
      synchronized(calloutLock) {
         boolean performRepaint = false;
         int localPaintRectX1 = 0;
         int localPaintRectY1 = 0;
         int localPaintWidth = 0;
         int localPaintHeight = 0;
         Displayable localCurrentDisplayable = null;
         synchronized(LCDUILock) {
            if (this == theActiveDisplay) {
               if (this.stateOfRepaint == 0) {
                  return;
               }

               if (displayIsVisible && this.currentDisplayable.isShownImpl() && this.displayGraphics != null && !this.currentDisplayable.isNativeDelegate()) {
                  performRepaint = true;
                  localCurrentDisplayable = this.currentDisplayable;
                  this.displayGraphics.reset();
                  if (this.stateOfRepaint == 2) {
                     localPaintRectX1 = 0;
                     localPaintRectY1 = 0;
                     boolean hasTicker = localCurrentDisplayable.getTicker() != null;
                     if (localCurrentDisplayable instanceof Canvas) {
                        if (localCurrentDisplayable.fullScreenMode) {
                           localPaintWidth = fullCanvasPaintRectX2;
                           localPaintHeight = fullCanvasPaintRectY2;
                        } else if (hasTicker) {
                           localPaintWidth = withTickerPaintRectX2;
                           localPaintHeight = withTickerPaintRectY2;
                        } else {
                           localPaintWidth = canvasPaintRectX2;
                           localPaintHeight = canvasPaintRectY2;
                        }
                     } else if (hasTicker) {
                        localPaintWidth = screenPaintRectX2;
                        localPaintHeight = withTickerPaintRectY2;
                     } else {
                        localPaintWidth = screenPaintRectX2;
                        localPaintHeight = screenPaintRectY2;
                     }
                  } else {
                     localPaintRectX1 = this.paintRectX1;
                     localPaintWidth = this.paintRectX2 - this.paintRectX1;
                     localPaintRectY1 = this.paintRectY1;
                     localPaintHeight = this.paintRectY2 - this.paintRectY1;
                  }

                  if (localCurrentDisplayable instanceof Canvas) {
                     this.displayGraphics.setTextTransparency(true);
                  } else {
                     this.displayGraphics.setTextTransparency(false);
                  }

                  this.displayGraphics.setClip(localPaintRectX1, localPaintRectY1, localPaintWidth, localPaintHeight);
                  if (this.clearScreenOnServiceRepaints(localCurrentDisplayable)) {
                     localCurrentDisplayable.clearScreen(this.displayGraphics);
                  }
               }
            }

            this.stateOfRepaint = 0;
            this.paintRectX1 = 9999;
            this.paintRectY1 = 9999;
            this.paintRectX2 = 0;
            this.paintRectY2 = 0;
         }

         if (performRepaint) {
            localCurrentDisplayable.callPaint(this.displayGraphics);
            synchronized(LCDUILock) {
               if (localCurrentDisplayable == this.currentDisplayable) {
                  this.displayGraphics.refresh(localPaintRectX1, localPaintRectY1, localPaintWidth, localPaintHeight, 0, 0);
               }
            }
         }

      }
   }

   void updateStatusZone() {
      this.updateStatusZone(false);
   }

   private void updateStatusZone(boolean forceUpdateWhileHidden) {
      if (this == theActiveDisplay && displayActive) {
         if (!displayIsVisible && !forceUpdateWhileHidden) {
            this.titleChangeWhileHidden = true;
         } else {
            String _title = this.currentDisplayable.getTitle();
            if (_title == null || _title.length() == 0) {
               _title = this.getMIDletName();
            }

            if (_title.length() > 512) {
               _title = _title.substring(0, 511);
            }

            this.setStatusZoneStr(_title);
            this.titleChangeWhileHidden = false;
         }
      }

   }

   final void cleanupDisplayableStack() {
      if (this.currentDisplayable != defaultDisplayable && this.currentDisplayable != null) {
         this.currentDisplayable.cleanupDisplayableStack(this);
      }

   }

   final void consumeUIEvent(int type, int param) {
      boolean makeCallOut = false;
      Displayable localCurrentDisplayable = this.currentDisplayable;
      if (localCurrentDisplayable != null && localCurrentDisplayable.isShownImpl()) {
         makeCallOut = true;
      }

      if (makeCallOut) {
         int param1 = (short)(param & '\uffff');
         int param2 = (short)(param >> 16 & '\uffff');
         switch(type) {
         case 1:
            localCurrentDisplayable.callKeyPressed(param1, param2);
            break;
         case 2:
            localCurrentDisplayable.callKeyRepeated(param1, param2);
            break;
         case 3:
            localCurrentDisplayable.callKeyReleased(param1, param2);
            break;
         case 4:
            localCurrentDisplayable.callPointerPress(param1, param2);
            break;
         case 5:
            localCurrentDisplayable.callPointerMove(param1, param2);
            break;
         case 6:
            localCurrentDisplayable.callPointerRelease(param1, param2);
            break;
         case 7:
            localCurrentDisplayable.callPointerDiscontinue(param1, param2);
            break;
         case 8:
            localCurrentDisplayable.callJoystickEvent(param1, param2);
         }
      }

   }

   final void consumeDisplayEvent(int type, int param) {
      boolean displayStateChanged;
      boolean makeScreenChange;
      boolean makeScreenChange;
      MIDlet midletToExit;
      Object source;
      DisplayStateListener localDisplayStateListener;
      Displayable localCurrentDisplayable;
      switch(type) {
      case 1:
         if (this.stateOfScreenChange == 1) {
            this.consumeScreenChange();
         }
         break;
      case 2:
         if (param == layout) {
            this.requestServiceRepaints();
            break;
         }
      case 12:
         synchronized(LCDUILock) {
            this.stateOfRepaint = 2;
         }

         this.requestServiceRepaints();
         break;
      case 3:
         Runnable r = null;
         synchronized(LCDUILock) {
            if (displayEventParamQueue.size() <= 0) {
               throw new RuntimeException("Display ERROR: No corresponding Runnable for callSerially.");
            }

            r = (Runnable)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
         }

         if (r != null) {
            synchronized(calloutLock) {
               r.run();
            }
         }
         break;
      case 4:
         localCurrentDisplayable = null;
         makeScreenChange = false;
         synchronized(LCDUILock) {
            if (displayIsVisible) {
               displayIsVisible = false;
               if (!this.currentDisplayable.isNativeDelegate()) {
                  localCurrentDisplayable = this.currentDisplayable;
                  makeScreenChange = true;
               }
            }
         }

         if (makeScreenChange) {
            if (this.pointer_registration != 0) {
               this.pointer_registration = 0;
            }

            localCurrentDisplayable.callHideNotifyInProgress(this);
            localCurrentDisplayable.callHideNotify(this);
         }
         break;
      case 5:
         localCurrentDisplayable = null;
         makeScreenChange = false;
         makeScreenChange = false;
         synchronized(LCDUILock) {
            if (!displayActive) {
               return;
            }

            if (!displayIsVisible) {
               displayIsVisible = true;
               localCurrentDisplayable = this.currentDisplayable;
               if (!this.currentDisplayable.isShownImpl()) {
                  makeScreenChange = true;
               }

               this.stateOfRepaint = 2;
               if (this.titleChangeWhileHidden) {
                  this.updateStatusZone();
               }

               if (!this.clearScreenOnServiceRepaints(localCurrentDisplayable)) {
                  localCurrentDisplayable.clearScreen(this.displayGraphics);
               }

               makeScreenChange = true;
            }
         }

         if (makeScreenChange) {
            if (makeScreenChange) {
               localCurrentDisplayable.callShowNotify(this);
            }

            this.requestServiceRepaints();
         }
         break;
      case 6:
         midletToExit = null;
         synchronized(LCDUILock) {
            localCurrentDisplayable = (Displayable)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
         }

         if (localCurrentDisplayable != null) {
            localCurrentDisplayable.callInvalidate();
         }
         break;
      case 7:
         midletToExit = null;
         source = null;
         Item changedItem;
         synchronized(LCDUILock) {
            localCurrentDisplayable = (Displayable)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            changedItem = (Item)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            changedItem.stateChanging = false;
         }

         if (localCurrentDisplayable != null) {
            localCurrentDisplayable.callItemStateChanged(changedItem);
         }
         break;
      case 8:
         midletToExit = null;
         source = null;
         localDisplayStateListener = null;
         Command command;
         Object listener;
         synchronized(LCDUILock) {
            command = (Command)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            source = displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            listener = displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
         }

         if (source instanceof Displayable) {
            synchronized(calloutLock) {
               ((CommandListener)listener).commandAction(command, (Displayable)source);
            }
         } else if (source instanceof Item) {
            synchronized(calloutLock) {
               ((ItemCommandListener)listener).commandAction(command, (Item)source);
            }
         }
         break;
      case 9:
         midletToExit = null;
         synchronized(LCDUILock) {
            midletToExit = (MIDlet)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            if (midletToExit != this.myMIDlet) {
               midletToExit = null;
            }
         }

         if (midletToExit != null) {
            midletToExit.notifyDestroyed();
         }
         break;
      case 10:
         synchronized(LCDUILock) {
            this.foldClosed = true;
            break;
         }
      case 11:
         synchronized(LCDUILock) {
            this.foldClosed = false;
            break;
         }
      case 13:
         displayStateChanged = false;
         makeScreenChange = true;
         localDisplayStateListener = this.displayStateListener;
         synchronized(LCDUILock) {
            if (!displayActive) {
               displayStateChanged = true;
               displayActive = true;
               UIStyle.reinitialiseAllForForeground();
               if (this.midletRequestedDSCS == 2) {
                  this.midletRequestedDSCS = 0;
               }
            }
         }

         if (displayStateChanged) {
            if (localDisplayStateListener != null) {
               synchronized(calloutLock) {
                  localDisplayStateListener.displayActive(this);
               }
            }

            synchronized(LCDUILock) {
               if (this.stateOfScreenChange == 0) {
                  statusZoneString = "";
                  changeDisplayLayoutImpl(this.currentDisplayable);
                  this.displayGraphics = new DirectGraphicsImpl(Pixmap.getUpdatedDisplayPixmap().getGraphics());
                  SoftLabel.makeAllInvisible(true);
                  this.updateStatusZone(true);
                  makeScreenChange = false;
               } else if (this.pendingDisplayable == defaultNoDispSetDisplayable) {
                  makeScreenChange = false;
               }
            }

            if (makeScreenChange) {
               statusZoneString = "";
               widgetNotificationRequired = true;
               this.consumeScreenChange();
            }
         }
         break;
      case 14:
         displayStateChanged = false;
         makeScreenChange = false;
         makeScreenChange = false;
         DisplayStateListener localDisplayStateListener = this.displayStateListener;
         synchronized(LCDUILock) {
            if (displayActive && this.currentDisplayable != null && this.currentDisplayable.isNativeDelegate()) {
               makeScreenChange = true;
            }
         }

         if (makeScreenChange) {
            this.currentDisplayable.callHideNotifyInProgress(this);
            this.currentDisplayable.callHideNotify(this);
         }

         synchronized(LCDUILock) {
            if (displayActive) {
               displayStateChanged = true;
               displayActive = false;
               if (this.midletRequestedDSCS == 1) {
                  this.midletRequestedDSCS = 0;
               }

               if (this.stateOfScreenChange == 1) {
                  if (this.pendingDisplayable.isSystemScreen()) {
                     if (!this.currentDisplayable.isDisplayableInStack(this.pendingDisplayable)) {
                        this.pendingDisplayable.removedFromDisplayNotify(this);
                     }

                     this.pendingDisplayable = null;
                     this.stateOfScreenChange = 0;
                  } else {
                     makeScreenChange = true;
                  }
               }

               if (this.currentDisplayable.isSystemScreen()) {
                  this.pendingDisplayable = this.currentDisplayable.getBottomOfStackDisplayable();
                  this.stateOfScreenChange = 1;
                  makeScreenChange = true;
               }
            }
         }

         if (displayStateChanged) {
            if (localDisplayStateListener != null) {
               synchronized(calloutLock) {
                  localDisplayStateListener.displayInactive(this);
               }
            }

            this.nativeMIDletAcknowledgedDisplayInactive();
            if (makeScreenChange) {
               this.consumeScreenChange();
            }
         }
         break;
      default:
         throw new RuntimeException("Display ERROR: Unknown event type " + type);
      }

   }

   final void discardEvent(int category, int type) {
      if (category == 2) {
         switch(type) {
         case 1:
            if (this.pendingDisplayable != null && this.pendingDisplayable != defaultDisplayable) {
               this.pendingDisplayable.removedFromDisplayNotify(this);
            }
         case 2:
         case 4:
         case 5:
         default:
            break;
         case 3:
         case 6:
         case 9:
            displayEventParamQueue.removeElementAt(0);
            break;
         case 7:
            displayEventParamQueue.removeElementAt(0);
            displayEventParamQueue.removeElementAt(0);
            break;
         case 8:
            displayEventParamQueue.removeElementAt(0);
            displayEventParamQueue.removeElementAt(0);
            displayEventParamQueue.removeElementAt(0);
         }
      }

   }

   void flushImageToScreen(Displayable d, Image image, int x, int y, int w, int h) {
      if (this == theActiveDisplay && displayIsVisible && d.isShownImpl() && this.displayGraphics != null && !this.screenChangeInProgress && image.getPixmap().isGameCanvasPixmap()) {
         int clipY = 0;
         this.displayGraphics.reset();
         this.displayGraphics.translate(0, clipY);
         this.displayGraphics.setClip(0, 0, d.getWidth(), d.getHeight());
         this.displayGraphics.clipRect(x, y, w, h);
         Graphics gameCanvasGraphics = image.getGraphics();
         gameCanvasGraphics.refresh(this.displayGraphics.getClipX(), this.displayGraphics.getClipY(), this.displayGraphics.getClipWidth(), this.displayGraphics.getClipHeight(), 0, clipY);
      }
   }

   void hideOldDisplayable() {
      Displayable localCurrentDisplayable = null;
      synchronized(LCDUILock) {
         if (this.currentDisplayable != null && this.currentDisplayable.isShownImpl() && this.currentDisplayable != defaultDisplayable) {
            localCurrentDisplayable = this.currentDisplayable;
         }
      }

      if (localCurrentDisplayable != null) {
         localCurrentDisplayable.callHideNotifyInProgress(this);
         localCurrentDisplayable.callHideNotify(this);
      }

   }

   void resetDisplay(MIDlet m) {
      this.hideOldDisplayable();
      synchronized(LCDUILock) {
         this.stateOfScreenChange = 0;
         this.pendingMidletScreenChange = false;
         this.cleanupDisplayableStack();
         this.myMIDlet = m;
         this.myMIDletName = midletAccessor.getMIDletName(this.myMIDlet);
         this.currentDisplayable = defaultDisplayable;
         defaultDisplayable.deleteAll();
         defaultDisplayable.append((Item)(new StringItem((String)null, TextDatabase.getText(0, this.myMIDletName))));
         if (defaultDisplayable.isShownImpl()) {
            defaultDisplayable.myDisplay = this;
            this.displayGraphics = new DirectGraphicsImpl(Pixmap.getUpdatedDisplayPixmap().getGraphics());
         }

         this.midletCurrentDisplayable = null;
         this.pendingDisplayable = null;
         this.pendingParentDisplayable = null;
         this.pendingItem = null;
         this.inProgressDisplayable = null;
         this.stateOfRepaint = 0;
         this.paintRectX1 = 9999;
         this.paintRectY1 = 9999;
         this.paintRectX2 = 0;
         this.paintRectY2 = 0;
      }
   }

   void registerForPointerEvents(int registration_type) {
      if (this.pointer_registration != registration_type) {
         this.pointer_registration = registration_type;
         this.nativeRegisterToPointerEvents(this.pointer_registration);
      }

   }

   native boolean nativeIsPowerSavingEnabled();

   static void changeDisplayLayoutImpl(Displayable displayable) {
      if (!(displayable instanceof NativeOptionsMenu)) {
         if (displayable instanceof Canvas) {
            if (displayable.fullScreenMode) {
               SoftLabel.makeAllInvisible(true);
               layout = 3;
            } else {
               layout = displayable.ticker == null ? 2 : 6;
            }
         } else if (displayable instanceof Alert) {
            if (displayable.ticker != null && layout != 6 && layout != 5 && layout != 4) {
               layout_under_popup = layout;
               layout = 5;
            } else {
               if (layout != 5 || displayable.ticker != null) {
                  return;
               }

               layout = layout_under_popup;
               layout_under_popup = 0;
            }
         } else {
            layout = displayable.ticker == null ? 0 : 4;
         }

         notifyLayoutChange(layout, displayable.isNativeDelegate(), displayable.isPopup());

         while(!checkLayoutChangeComplete(midletId)) {
            try {
               Thread.sleep(5L);
            } catch (InterruptedException var2) {
               var2.printStackTrace();
            }
         }

      }
   }

   private final void consumeScreenChange() {
      boolean oldDisplayableShown = false;
      Displayable oldDisplayable = null;
      synchronized(LCDUILock) {
         this.pendingMidletScreenChange = false;
         Item inProgressItem = this.pendingItem;
         this.pendingItem = null;
         if (this.stateOfScreenChange != 1) {
            this.stateOfScreenChange = 0;
            if (this.pendingDisplayable != null) {
               if (!this.currentDisplayable.isDisplayableInStack(this.pendingDisplayable)) {
                  this.pendingDisplayable.removedFromDisplayNotify(this);
               }

               this.pendingDisplayable = null;
            }

            this.pendingParentDisplayable = null;
            return;
         }

         if (this.pendingDisplayable == null) {
            this.stateOfScreenChange = 0;
            this.pendingParentDisplayable = null;
            return;
         }

         if (!this.currentDisplayable.setDisplayableToTopOfStack(this.pendingParentDisplayable, this.pendingDisplayable) && (this.currentDisplayable != defaultDisplayable || this.pendingDisplayable != defaultDisplayable || defaultDisplayable.isShownImpl())) {
            if (inProgressItem != null && inProgressItem.owner == this.currentDisplayable && this.currentDisplayable instanceof Form) {
               ((Form)this.currentDisplayable).setCurrentItem(inProgressItem);
            }

            this.stateOfScreenChange = 0;
            if (!this.currentDisplayable.isDisplayableInStack(this.pendingDisplayable)) {
               this.pendingDisplayable.removedFromDisplayNotify(this);
            }

            this.pendingDisplayable = null;
            this.pendingParentDisplayable = null;
            return;
         }

         this.stateOfScreenChange = 2;
         this.screenChangeInProgress = true;
         this.inProgressDisplayable = this.pendingDisplayable;
         this.pendingDisplayable = null;
         this.pendingParentDisplayable = null;
         if (inProgressItem != null && inProgressItem.owner == this.inProgressDisplayable && this.inProgressDisplayable instanceof Form) {
            ((Form)this.inProgressDisplayable).setCurrentItem(inProgressItem);
         }

         oldDisplayableShown = this.currentDisplayable.isShownImpl();
         oldDisplayable = this.currentDisplayable;
      }

      if (oldDisplayableShown) {
         oldDisplayable.callHideNotifyInProgress(this);
      }

      synchronized(LCDUILock) {
         this.currentDisplayable = this.inProgressDisplayable;
         this.inProgressDisplayable = null;
         if (this.currentDisplayable.isSystemScreen()) {
            if (!oldDisplayable.isSystemScreen()) {
               this.midletCurrentDisplayable = oldDisplayable;
            }
         } else {
            this.midletCurrentDisplayable = null;
         }

         if (this.stateOfScreenChange == 3) {
            this.stateOfScreenChange = 1;
         } else {
            this.stateOfScreenChange = 0;
         }

         if (displayIsVisible) {
            this.stateOfRepaint = 2;
         }

         if (displayActive) {
            if (this.currentDisplayable instanceof Alert) {
               this.currentDisplayable.setBackgroundOfPopup(oldDisplayable);
            }

            changeDisplayLayoutImpl(this.currentDisplayable);
            this.displayGraphics = new DirectGraphicsImpl(Pixmap.getUpdatedDisplayPixmap().getGraphics());
            boolean oldDispHasVisibleTicker = oldDisplayable.ticker != null && !oldDisplayable.fullScreenMode;
            boolean newDispHasVisibleTicker = this.currentDisplayable.ticker != null && !this.currentDisplayable.fullScreenMode;
            if (!oldDispHasVisibleTicker && newDispHasVisibleTicker) {
               this.currentDisplayable.tickerShown();
            } else if (oldDispHasVisibleTicker && !newDispHasVisibleTicker) {
               oldDisplayable.tickerHidden(false);
            } else if (oldDispHasVisibleTicker && newDispHasVisibleTicker && oldDisplayable.ticker != this.currentDisplayable.ticker) {
               oldDisplayable.tickerHidden(true);
               this.currentDisplayable.tickerShown();
            }

            if (!widgetNotificationRequired && this.currentDisplayable.isNativeDelegate()) {
               if (this.currentDisplayable.nativeWrapper || oldDisplayable.nativeWrapper) {
                  SoftLabel.makeAllInvisible(true);
               }
            } else {
               SoftLabel.makeAllInvisible(true);
               this.updateStatusZone(widgetNotificationRequired);
               widgetNotificationRequired = false;
            }
         }
      }

      if (displayIsVisible || this.currentDisplayable instanceof Alert && (this.foldClosed || oldDisplayableShown)) {
         this.currentDisplayable.callShowNotify(this);
      }

      if (displayIsVisible && !this.currentDisplayable.isNativeDelegate()) {
         this.requestServiceRepaints();
      }

      if (oldDisplayableShown) {
         oldDisplayable.callHideNotify(this);
      }

      synchronized(LCDUILock) {
         if (this.currentDisplayable.isDisplayableStackCleanupRequired(oldDisplayable)) {
            oldDisplayable.cleanupDisplayableStack(this);
         }

         this.screenChangeInProgress = false;
      }
   }

   boolean clearScreenOnServiceRepaints(Displayable localCurrentDisplayable) {
      boolean clearScreen = false;
      if (!(localCurrentDisplayable instanceof GameCanvas) && !(localCurrentDisplayable instanceof Canvas)) {
         clearScreen = true;
      } else if (this.screenChangeInProgress && this.stateOfRepaint == 2 || !(localCurrentDisplayable instanceof GameCanvas) && UIStyle.isCanvasHasBgImage() && !localCurrentDisplayable.fullScreenMode) {
         clearScreen = true;
      }

      return clearScreen;
   }

   private void setCurrentImpl(Displayable parentDisplayable, Displayable nextDisplayable, Item nextItem, boolean immediateFg, String softNoteText, Image icon, boolean isMidletScreenChange, boolean suppressBgFg) throws ForegroundUnavailableException {
      boolean proceedWithScreenChange = false;
      boolean eventPrePosted = false;
      Displayable realCurrentDisplayable;
      if (this.stateOfScreenChange != 2 && this.stateOfScreenChange != 3) {
         realCurrentDisplayable = this.currentDisplayable;
      } else {
         realCurrentDisplayable = this.inProgressDisplayable;
      }

      if (!barbiePlatform && nextDisplayable != null) {
         proceedWithScreenChange = true;
      } else if (suppressBgFg) {
         proceedWithScreenChange = true;
      } else if (nextDisplayable == null) {
         if (isMidletScreenChange) {
            proceedWithScreenChange = this.requestDisplayStateChange(midletId, false, immediateFg, (String)null, (Pixmap)null);
            if (displayActive || realCurrentDisplayable == defaultNoDispSetDisplayable) {
               eventPrePosted = true;
            }

            nextDisplayable = defaultNoDispSetDisplayable;
         }
      } else if (displayActive && this.midletRequestedDSCS != 1) {
         proceedWithScreenChange = true;
      } else if (isMidletScreenChange) {
         proceedWithScreenChange = this.requestDisplayStateChange(midletId, true, immediateFg, softNoteText, icon != null ? icon.pixmap : null);
         eventPrePosted = true;
      }

      if (proceedWithScreenChange) {
         switch(this.stateOfScreenChange) {
         case 0:
            if (nextDisplayable == this.currentDisplayable && nextItem == null && (this.currentDisplayable != defaultDisplayable || defaultDisplayable.isShownImpl())) {
               return;
            }

            this.stateOfScreenChange = 1;
            this.setCurrentCommonNoPendingHandling(parentDisplayable, (Displayable)nextDisplayable, nextItem, isMidletScreenChange, eventPrePosted);
            break;
         case 1:
         case 3:
            this.setCurrentCommonPendingHandling(realCurrentDisplayable, parentDisplayable, (Displayable)nextDisplayable, nextItem, isMidletScreenChange);
            break;
         case 2:
            if (nextDisplayable == realCurrentDisplayable && nextItem == null) {
               return;
            }

            this.stateOfScreenChange = 3;
            this.setCurrentCommonNoPendingHandling(parentDisplayable, (Displayable)nextDisplayable, nextItem, isMidletScreenChange, eventPrePosted);
         }
      } else if (immediateFg && nextDisplayable != null) {
         if (this.stateOfScreenChange == 3) {
            this.stateOfScreenChange = 2;
         } else {
            this.stateOfScreenChange = 0;
         }

         this.pendingDisplayable = null;
         this.pendingParentDisplayable = null;
         this.pendingItem = null;
         this.pendingMidletScreenChange = false;
         throw new ForegroundUnavailableException();
      }

   }

   private void setCurrentCommonNoPendingHandling(Displayable parentDisplayable, Displayable nextDisplayable, Item nextItem, boolean isMidletScreenChange, boolean eventPrePosted) {
      this.pendingDisplayable = nextDisplayable;
      this.pendingParentDisplayable = parentDisplayable;
      this.pendingItem = nextItem;
      this.pendingMidletScreenChange = isMidletScreenChange;
      if (!eventPrePosted) {
         JavaEventGenerator.s_generateEvent(0, 2, 1, 0);
      }

   }

   private void setCurrentCommonPendingHandling(Displayable previousDisplayable, Displayable parentDisplayable, Displayable nextDisplayable, Item nextItem, boolean isMidletScreenChange) {
      if (isMidletScreenChange || !this.pendingMidletScreenChange) {
         this.pendingMidletScreenChange = isMidletScreenChange;
         if (this.pendingDisplayable != nextDisplayable) {
            if (this.pendingDisplayable != null && !previousDisplayable.isDisplayableInStack(this.pendingDisplayable)) {
               this.pendingDisplayable.removedFromDisplayNotify(this);
            }

            this.pendingDisplayable = nextDisplayable;
         }

         this.pendingParentDisplayable = parentDisplayable;
         this.pendingItem = nextItem;
      }

   }

   private void setStatusZoneStr(String str) {
      if (!str.equals(statusZoneString)) {
         statusZoneString = str;
         this.notifyStatusZoneUpdate(str);
      }

   }

   private Zone getImageZone(int imageType) {
      Zone var2;
      switch(imageType) {
      case 1:
         var2 = Displayable.uistyle.getZone(15);
         break;
      case 2:
         var2 = Displayable.uistyle.getZone(10);
         break;
      case 3:
         var2 = Displayable.uistyle.getZone(28);
         break;
      default:
         throw new IllegalArgumentException();
      }

      return var2;
   }

   private static native boolean checkLayoutChangeComplete(int var0);

   private static native void notifyLayoutChange(int var0, boolean var1, boolean var2);

   private native void notifyStatusZoneUpdate(String var1);

   private static native void nativeStaticInitializer();

   private native boolean requestDisplayStateChange(int var1, boolean var2, boolean var3, String var4, Pixmap var5);

   private native void nativeMIDletAcknowledgedDisplayInactive();

   private native void nativeRegisterToPointerEvents(int var1);

   // $FF: synthetic method
   Display(MIDlet x0, Object x1) {
      this(x0);
   }

   static {
      nativeStaticInitializer();
      defaultDisplayable.addCommand(defaultQuitCmd);
      defaultNoDispSetDisplayable.append((Item)(new StringItem((String)null, "")));
   }

   private class DisplayAccessor implements DisplayAccess, EventConsumer, CommandListener {
      private DisplayAccessor() {
      }

      public void commandAction(Command c, Displayable d) {
         synchronized(Display.LCDUILock) {
            if (Display.this == Display.theActiveDisplay) {
               Display.displayEventParamQueue.addElement(Display.this.myMIDlet);
               JavaEventGenerator.s_generateEvent(0, 2, 9, 0);
            }

         }
      }

      public void consumeEvent(int category, int type, int param) {
         synchronized(Display.displayTransitionLock) {
            boolean consumeEvent = true;
            if (Display.this != Display.theActiveDisplay) {
               consumeEvent = false;
               synchronized(Display.LCDUILock) {
                  Display.this.discardEvent(category, type);
               }
            }

            if (consumeEvent) {
               switch(category) {
               case 1:
                  Display.this.consumeUIEvent(type, param);
                  break;
               case 2:
                  Display.this.consumeDisplayEvent(type, param);
               case 3:
               default:
                  break;
               case 4:
                  Displayable cd = Display.this.currentDisplayable;
                  if (cd != null) {
                     cd.callDelegateEvent(type, param);
                  }
               }
            }

         }
      }

      public void flushImageToScreen(Displayable d, Image image, int x, int y, int w, int h) {
         synchronized(Display.LCDUILock) {
            Display.this.flushImageToScreen(d, image, x, y, w, h);
         }
      }

      public DisplayAccess replaceDisplay(MIDlet newMIDlet) {
         synchronized(Display.displayTransitionLock) {
            Display localActiveDisplay = Display.theActiveDisplay;
            if (Display.this != localActiveDisplay && localActiveDisplay != null) {
               return this;
            } else {
               if (localActiveDisplay != null) {
                  localActiveDisplay.hideOldDisplayable();
               }

               Display.DisplayAccessor var10000;
               synchronized(Display.LCDUILock) {
                  if (localActiveDisplay != null) {
                     localActiveDisplay.cleanupDisplayableStack();
                  }

                  Display.theActiveDisplay = new Display(newMIDlet);
                  var10000 = Display.theActiveDisplay.myDa;
               }

               return var10000;
            }
         }
      }

      public void resetDisplay(MIDlet newMIDlet) {
         synchronized(Display.displayTransitionLock) {
            if (Display.this == Display.theActiveDisplay) {
               Display.this.resetDisplay(newMIDlet);
            }

         }
      }

      public void setForeground(MIDlet m) {
         synchronized(Display.displayTransitionLock) {
            if (Display.this == Display.theActiveDisplay) {
               if (Display.this.myMIDlet != m) {
                  Display.this.resetDisplay(m);
               }

               synchronized(Display.LCDUILock) {
                  if (Display.this.stateOfScreenChange == 0 && Display.this.currentDisplayable == Display.defaultDisplayable) {
                     try {
                        Display.this.setCurrentImpl((Displayable)null, Display.this.currentDisplayable, (Item)null, false, (String)null, (Image)null, false, true);
                     } catch (ForegroundUnavailableException var7) {
                     }
                  }
               }
            }

         }
      }

      public Image createImage(Pixmap pixmap) {
         return new Image(pixmap);
      }

      public Pixmap getImagePixmap(Image image) {
         return image.getPixmap();
      }

      public Item createMMItem() {
         Item item = null;

         try {
            item = (Item)Class.forName("javax.microedition.lcdui.MMItemImpl").newInstance();
         } catch (Exception var3) {
         }

         return item;
      }

      public void showCanvasVideo(Canvas canvas, int playerId, boolean show, int x, int y, int w, int h) {
         canvas.showVideo(show, playerId, x, y, w, h);
      }

      public boolean isDisplayActive(Display display) {
         if (display != Display.theActiveDisplay) {
            throw new IllegalStateException();
         } else {
            return Display.displayActive;
         }
      }

      public void setCurrent(Display display, Displayable nextDisplayable, boolean immediateFg, String promptText) throws ForegroundUnavailableException {
         if (display != Display.theActiveDisplay) {
            throw new IllegalStateException();
         } else {
            Display.this.setCurrent(nextDisplayable, immediateFg, promptText, (Image)null);
         }
      }

      public void setCurrent(Display display, Alert alert, Displayable nextDisplayable, boolean immediateFg, String promptText) throws ForegroundUnavailableException {
         if (display != Display.theActiveDisplay) {
            throw new IllegalStateException();
         } else {
            Display.this.setCurrent(alert, nextDisplayable, immediateFg, promptText, (Image)null);
         }
      }

      public void setCurrentItem(Display display, Item item, boolean immediateFg, String promptText) throws ForegroundUnavailableException {
         if (display != Display.theActiveDisplay) {
            throw new IllegalStateException();
         } else {
            Display.this.setCurrentItem(item, immediateFg, promptText);
         }
      }

      public void setDisplayStateListener(Display display, DisplayStateListener listener) {
         synchronized(Display.LCDUILock) {
            if (display != Display.theActiveDisplay) {
               throw new IllegalStateException();
            } else {
               Display.this.displayStateListener = listener;
            }
         }
      }

      public void setVisibilityListener(Displayable displayable, VisibilityListener listener) {
         if (displayable == null) {
            throw new NullPointerException();
         } else {
            synchronized(Display.LCDUILock) {
               displayable.setVisibilityListener(listener);
            }
         }
      }

      public void setCurrent(Display display, Displayable nextDisplayable, boolean immediateFg, String promptText, Image icon) throws ForegroundUnavailableException {
         if (display != Display.theActiveDisplay) {
            throw new IllegalStateException();
         } else {
            Display.this.setCurrent(nextDisplayable, immediateFg, promptText, icon);
         }
      }

      public void setCurrent(Display display, Alert alert, Displayable nextDisplayable, boolean immediateFg, String promptText, Image icon) throws ForegroundUnavailableException {
         if (display != Display.theActiveDisplay) {
            throw new IllegalStateException();
         } else {
            Display.this.setCurrent(alert, nextDisplayable, immediateFg, promptText, icon);
         }
      }

      public Font newFont(com.nokia.mid.impl.isa.ui.gdi.Font font) {
         return new Font(font);
      }

      // $FF: synthetic method
      DisplayAccessor(Object x1) {
         this();
      }
   }
}
