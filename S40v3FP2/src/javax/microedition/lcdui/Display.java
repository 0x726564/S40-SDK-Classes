package javax.microedition.lcdui;

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
import com.nokia.mid.ui.DeviceControl;
import com.nokia.mid.ui.lcdui.DisplayStateListener;
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
   private static final Form defaultDisplayable = new Form("");
   private static final Command defaultQuitCmd = new Command(10, 13);
   private static final Form defaultNoDispSetDisplayable = new Form("");
   private static int layout;
   private static Vector displayEventParamQueue = new Vector();
   private static String statusZoneString;
   private static final EventProducer eventDispatcher = InitJALM.s_getEventProducer();
   private static Display theActiveDisplay;
   private static MIDletAccess midletAccessor;
   private static boolean barbiePlatform;
   private static int midletId;
   static boolean displayActive;
   DisplayStateListener displayStateListener;
   private static boolean displayIsVisible;
   private int midletRequestedDSCS;
   private boolean firstShowAfterFg;
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

   private Display(MIDlet var1) {
      this.firstShowAfterFg = false;
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
         this.optionsMenu = NativeOptionsMenu.getNativeOptionsMenu();
         this.truncatedItemScreen = TruncatedItemScreen.getTruncatedItemScreen();
      }

      this.resetDisplay(var1);
   }

   public static Display getDisplay(MIDlet var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         synchronized(LCDUILock) {
            if (theActiveDisplay == null) {
               theActiveDisplay = new Display(var0);
               InitJALM.s_setDisplayAccessor(theActiveDisplay.myDa);
            }

            if (theActiveDisplay.myMIDlet != var0) {
               throw new RuntimeException("Error: Attempt to getDisplay by application after destroyApp called.");
            }
         }

         return theActiveDisplay;
      }
   }

   public void callSerially(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         synchronized(LCDUILock) {
            if (this == theActiveDisplay) {
               displayEventParamQueue.addElement(var1);
               JavaEventGenerator.s_generateEvent(0, 2, 3, 0);
            }

         }
      }
   }

   public boolean flashBacklight(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Display.flashBacklight: negative duration not allowed.");
      } else {
         synchronized(LCDUILock) {
            if (!displayIsVisible || displayIsVisible && this.currentDisplayable != null && this.currentDisplayable.isNativeDelegate()) {
               return false;
            }
         }

         DeviceControl.flashLights((long)var1);
         return true;
      }
   }

   public int getBestImageHeight(int var1) {
      Zone var2 = this.getImageZone(var1);
      return var2.height - var2.getMarginTop() - var2.getMarginBottom();
   }

   public int getBestImageWidth(int var1) {
      Zone var2 = this.getImageZone(var1);
      return var2.width - var2.getMarginLeft() - var2.getMarginRight();
   }

   public int getBorderStyle(boolean var1) {
      return 0;
   }

   public int getColor(int var1) {
      switch(var1) {
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

   public void setCurrent(Displayable var1) {
      if (var1 != null) {
         this.setCurrent(var1, (String)null);
      }

   }

   void setCurrent(Displayable var1, String var2) {
      synchronized(LCDUILock) {
         if (this == theActiveDisplay) {
            if (var1 != null) {
               if (!(var1 instanceof Alert)) {
                  if (this.foldClosed && this.nativeIsNoteDisplayed()) {
                     this.nativeHideNote();
                  }
               } else {
                  Displayable var4 = this.currentDisplayable;
                  if (this.stateOfScreenChange != 2 && this.stateOfScreenChange != 3) {
                     if (this.currentDisplayable.isSystemScreen()) {
                        var4 = this.midletCurrentDisplayable;
                     }
                  } else if (this.inProgressDisplayable.isSystemScreen()) {
                     if (this.currentDisplayable.isSystemScreen()) {
                        var4 = this.midletCurrentDisplayable;
                     }
                  } else {
                     var4 = this.inProgressDisplayable;
                  }

                  if (var4 == var1) {
                     if (var4 instanceof Alert && var4 == this.currentDisplayable && this.stateOfScreenChange != 2 && this.stateOfScreenChange != 3) {
                        ((Alert)var4).restartTimerIfNecessary();
                     }

                     return;
                  }

                  if (var4 instanceof Alert) {
                     throw new IllegalArgumentException("Current Displayable is an Alert");
                  }

                  ((Alert)var1).setReturnScreen(var4);
                  if (this.foldClosed && displayActive && !this.nativeIsNoteDisplayed()) {
                     this.nativeDisplayNote();
                  }
               }
            }

            this.setCurrentImpl((Displayable)null, var1, (Item)null, var2, true, false);
         }

      }
   }

   public void setCurrent(Alert var1, Displayable var2) {
      this.setCurrent(var1, var2, (String)null);
   }

   void setCurrent(Alert var1, Displayable var2, String var3) {
      if (var1 != null && var2 != null) {
         if (var2 instanceof Alert) {
            throw new IllegalArgumentException();
         } else {
            synchronized(LCDUILock) {
               if (this == theActiveDisplay) {
                  var1.setReturnScreen(var2);
                  if (var1 == this.currentDisplayable && this.stateOfScreenChange != 2 && this.stateOfScreenChange != 3) {
                     var1.restartTimerIfNecessary();
                  }

                  if (this.foldClosed && !this.nativeIsNoteDisplayed() && displayActive) {
                     this.nativeDisplayNote();
                  }

                  this.setCurrentImpl((Displayable)null, var1, (Item)null, var3, true, false);
               }

            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void setCurrentItem(Item var1) {
      this.setCurrentItem(var1, (String)null);
   }

   void setCurrentItem(Item var1, String var2) {
      synchronized(LCDUILock) {
         Screen var4 = var1.owner;
         if (var4 != null && !(var4 instanceof Alert)) {
            if (this == theActiveDisplay) {
               this.setCurrentImpl((Displayable)null, var4, var4 instanceof Form ? var1 : null, var2, true, false);
            }

         } else {
            throw new IllegalStateException();
         }
      }
   }

   public boolean vibrate(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Display.vibrate: negative duration not allowed.");
      } else {
         synchronized(LCDUILock) {
            if (!displayIsVisible && (this.currentDisplayable == null || !this.currentDisplayable.isNativeDelegate())) {
               return false;
            }
         }

         if (var1 == 0) {
            DeviceControl.stopVibra();
            return true;
         } else {
            try {
               DeviceControl.startVibra(100, (long)var1);
            } catch (IllegalStateException var4) {
            }

            return true;
         }
      }
   }

   void changeDisplayLayout(Displayable var1) {
      if (displayActive && this == theActiveDisplay) {
         Displayable var2 = this.currentDisplayable;
         if (this.stateOfScreenChange == 2 || this.stateOfScreenChange == 3) {
            var2 = this.inProgressDisplayable;
         }

         if (var1 == var2) {
            changeDisplayLayoutImpl(var1);
            this.displayGraphics = new DirectGraphicsImpl(Pixmap.getUpdatedDisplayPixmap().getGraphics());
         }
      }

   }

   static Display getActiveDisplay() {
      return theActiveDisplay;
   }

   Displayable getCurrentImpl() {
      Displayable var1 = null;
      if (this.currentDisplayable != defaultDisplayable && this.currentDisplayable != defaultNoDispSetDisplayable && this.currentDisplayable != null) {
         if (this.currentDisplayable.isSystemScreen()) {
            var1 = this.midletCurrentDisplayable;
         } else {
            var1 = this.currentDisplayable;
         }
      }

      return var1;
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

   void requestRepaint(Displayable var1, int var2, int var3, int var4, int var5) {
      synchronized(LCDUILock) {
         if (this == theActiveDisplay && var1.isShown() && this.stateOfRepaint != 2) {
            this.paintRectX1 = Math.min(var2, this.paintRectX1);
            this.paintRectY1 = Math.min(var3, this.paintRectY1);
            this.paintRectX2 = Math.max(var2 + var4, this.paintRectX2);
            this.paintRectY2 = Math.max(var3 + var5, this.paintRectY2);
            if (this.stateOfRepaint == 0) {
               JavaEventGenerator.s_generateEvent(0, 2, 2, layout);
               this.stateOfRepaint = 1;
            }
         }

      }
   }

   void requestFullRepaint(Displayable var1) {
      synchronized(LCDUILock) {
         if (this == theActiveDisplay && var1.isShown()) {
            if (this.stateOfRepaint == 0) {
               JavaEventGenerator.s_generateEvent(0, 2, 2, layout);
            }

            this.stateOfRepaint = 2;
         }

      }
   }

   void requestInvalidate(Displayable var1) {
      synchronized(LCDUILock) {
         if (this == theActiveDisplay) {
            displayEventParamQueue.addElement(var1);
            JavaEventGenerator.s_generateEvent(0, 2, 6, 0);
         }

      }
   }

   void requestItemStateChanged(Displayable var1, Item var2) {
      synchronized(LCDUILock) {
         if (this == theActiveDisplay) {
            displayEventParamQueue.addElement(var1);
            displayEventParamQueue.addElement(var2);
            JavaEventGenerator.s_generateEvent(0, 2, 7, 0);
         }

      }
   }

   void requestCommandAction(Command var1, Object var2, Object var3) {
      if (var1 != null && var2 != null && var3 != null) {
         synchronized(LCDUILock) {
            if (this == theActiveDisplay) {
               displayEventParamQueue.addElement(var1);
               displayEventParamQueue.addElement(var2);
               displayEventParamQueue.addElement(var3);
               JavaEventGenerator.s_generateEvent(0, 2, 8, 0);
            }

         }
      }
   }

   void setCurrentInternal(Displayable var1, Displayable var2) {
      if (var2 != null && (var2.isSystemScreen() || var1 == null)) {
         if (this == theActiveDisplay) {
            if (var2 instanceof Alert) {
               if (this.foldClosed && !this.nativeIsNoteDisplayed() && displayActive) {
                  this.nativeDisplayNote();
               }
            } else if (this.foldClosed && this.nativeIsNoteDisplayed()) {
               this.nativeHideNote();
            }

            this.setCurrentImpl(var1, var2, (Item)null, (String)null, false, false);
         }

      }
   }

   final void registerTimer(Timer var1) {
      midletAccessor.registerTimer(this.myMIDlet, var1);
   }

   final void deregisterTimer(Timer var1) {
      midletAccessor.deregisterTimer(this.myMIDlet, var1);
   }

   void requestServiceRepaints() {
      synchronized(calloutLock) {
         boolean var2 = false;
         int var3 = 0;
         int var4 = 0;
         int var5 = 0;
         int var6 = 0;
         Displayable var7 = null;
         synchronized(LCDUILock) {
            if (this == theActiveDisplay) {
               if (this.stateOfRepaint == 0) {
                  return;
               }

               if (displayIsVisible && this.currentDisplayable.isShown() && this.displayGraphics != null && !this.currentDisplayable.isNativeDelegate()) {
                  var2 = true;
                  var7 = this.currentDisplayable;
                  this.displayGraphics.reset();
                  if (this.stateOfRepaint == 2) {
                     var3 = 0;
                     var4 = 0;
                     if (var7 instanceof Canvas) {
                        if (var7.fullScreenMode) {
                           var5 = fullCanvasPaintRectX2;
                           var6 = fullCanvasPaintRectY2;
                        } else {
                           var5 = canvasPaintRectX2;
                           var6 = canvasPaintRectY2;
                        }
                     } else {
                        var5 = screenPaintRectX2;
                        var6 = screenPaintRectY2;
                     }
                  } else {
                     var3 = this.paintRectX1;
                     var5 = this.paintRectX2 - this.paintRectX1;
                     var4 = this.paintRectY1;
                     var6 = this.paintRectY2 - this.paintRectY1;
                  }

                  if (var7 instanceof Canvas) {
                     this.displayGraphics.setTextTransparency(true);
                  } else {
                     this.displayGraphics.setTextTransparency(false);
                  }

                  this.displayGraphics.setClip(var3, var4, var5, var6);
                  if (this.clearScreenOnServiceRepaints(var7)) {
                     var7.clearScreen(this.displayGraphics);
                  }
               }
            }

            this.stateOfRepaint = 0;
            this.paintRectX1 = 9999;
            this.paintRectY1 = 9999;
            this.paintRectX2 = 0;
            this.paintRectY2 = 0;
         }

         if (var2) {
            var7.callPaint(this.displayGraphics);
            this.displayGraphics.refresh(var3, var4, var5, var6, 0, 0);
         }

      }
   }

   void updateStatusZone() {
      if (this == theActiveDisplay && displayActive) {
         String var1 = this.currentDisplayable.getTitle();
         if (var1 == null || var1.length() == 0) {
            var1 = this.getMIDletName();
         }

         if (var1.length() > 512) {
            var1 = var1.substring(0, 511);
         }

         this.setStatusZoneStr(var1);
      }

   }

   final void cleanupDisplayableStack() {
      if (this.currentDisplayable != defaultDisplayable && this.currentDisplayable != null) {
         this.currentDisplayable.cleanupDisplayableStack(this);
      }

   }

   final void consumeUIEvent(int var1, int var2) {
      boolean var3 = false;
      Displayable var4 = this.currentDisplayable;
      if (var4 != null && var4.isShown()) {
         var3 = true;
      }

      if (var3) {
         short var5 = (short)(var2 & '\uffff');
         short var6 = (short)(var2 >> 16 & '\uffff');
         switch(var1) {
         case 1:
            var4.callKeyPressed(var5, var6);
            break;
         case 2:
            var4.callKeyRepeated(var5, var6);
            break;
         case 3:
            var4.callKeyReleased(var5, var6);
         }
      }

   }

   final void consumeDisplayEvent(int var1, int var2) {
      boolean var4;
      boolean var5;
      boolean var6;
      MIDlet var40;
      Object var41;
      DisplayStateListener var42;
      Displayable var46;
      switch(var1) {
      case 1:
         if (this.stateOfScreenChange == 1) {
            this.consumeScreenChange();
         }
         break;
      case 2:
         if (var2 == layout) {
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
         Runnable var3 = null;
         synchronized(LCDUILock) {
            if (displayEventParamQueue.size() <= 0) {
               throw new RuntimeException("Display ERROR: No corresponding Runnable for callSerially.");
            }

            var3 = (Runnable)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
         }

         if (var3 != null) {
            synchronized(calloutLock) {
               var3.run();
            }
         }
         break;
      case 4:
         var46 = null;
         var5 = false;
         synchronized(LCDUILock) {
            if (displayIsVisible) {
               displayIsVisible = false;
               if (!this.currentDisplayable.isNativeDelegate()) {
                  var46 = this.currentDisplayable;
                  var5 = true;
               }
            }
         }

         if (var5) {
            var46.callHideNotifyInProgress(this);
            var46.callHideNotify(this);
         }
         break;
      case 5:
         var46 = null;
         var5 = false;
         var6 = false;
         synchronized(LCDUILock) {
            if (this.nativeIsNoteDisplayed()) {
               this.nativeHideNote();
            } else if (!this.foldClosed && !displayIsVisible) {
               displayIsVisible = true;
               var46 = this.currentDisplayable;
               if (!this.currentDisplayable.isShown()) {
                  var6 = true;
               }

               this.stateOfRepaint = 2;
               if (this.firstShowAfterFg) {
                  this.firstShowAfterFg = false;
                  changeDisplayLayoutImpl(this.currentDisplayable);
                  this.displayGraphics = new DirectGraphicsImpl(Pixmap.getUpdatedDisplayPixmap().getGraphics());
                  SoftLabel.makeAllInvisible();
                  this.updateStatusZone();
               }

               if (!this.clearScreenOnServiceRepaints(var46)) {
                  var46.clearScreen(this.displayGraphics);
               }

               var5 = true;
            }
         }

         if (var5) {
            if (var6) {
               var46.callShowNotify(this);
            }

            this.requestServiceRepaints();
         }
         break;
      case 6:
         var40 = null;
         synchronized(LCDUILock) {
            var46 = (Displayable)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
         }

         if (var46 != null) {
            var46.callInvalidate();
         }
         break;
      case 7:
         var40 = null;
         var41 = null;
         Item var44;
         synchronized(LCDUILock) {
            var46 = (Displayable)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            var44 = (Item)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            var44.stateChanging = false;
         }

         if (var46 != null) {
            var46.callItemStateChanged(var44);
         }
         break;
      case 8:
         var40 = null;
         var41 = null;
         var42 = null;
         Command var43;
         Object var45;
         synchronized(LCDUILock) {
            var43 = (Command)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            var41 = displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            var45 = displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
         }

         if (var41 instanceof Displayable) {
            synchronized(calloutLock) {
               ((CommandListener)var45).commandAction(var43, (Displayable)var41);
            }
         } else if (var41 instanceof Item) {
            synchronized(calloutLock) {
               ((ItemCommandListener)var45).commandAction(var43, (Item)var41);
            }
         }
         break;
      case 9:
         var40 = null;
         synchronized(LCDUILock) {
            var40 = (MIDlet)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            if (var40 != this.myMIDlet) {
               var40 = null;
            }
         }

         if (var40 != null) {
            midletAccessor.destroyMIDlet(var40);
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
         var4 = false;
         var5 = false;
         var42 = this.displayStateListener;
         synchronized(LCDUILock) {
            if (!displayActive) {
               var4 = true;
               displayActive = true;
               this.firstShowAfterFg = true;
               UIStyle.reinitialiseAllForForeground();
               if (this.midletRequestedDSCS == 2) {
                  this.midletRequestedDSCS = 0;
               }

               if (this.stateOfScreenChange != 0) {
                  var5 = true;
               }
            }
         }

         if (var4) {
            if (var42 != null) {
               synchronized(calloutLock) {
                  var42.displayActive(this);
               }
            }

            if (var5) {
               this.consumeScreenChange();
            }
         }
         break;
      case 14:
         var4 = false;
         var5 = false;
         var6 = false;
         DisplayStateListener var7 = this.displayStateListener;
         synchronized(LCDUILock) {
            if (displayActive) {
               if (this.foldClosed && this.nativeIsNoteDisplayed()) {
                  this.nativeHideNote();
               }

               if (this.currentDisplayable != null && this.currentDisplayable.isNativeDelegate()) {
                  var5 = true;
               }
            }
         }

         if (var5) {
            this.currentDisplayable.callHideNotifyInProgress(this);
            this.currentDisplayable.callHideNotify(this);
         }

         synchronized(LCDUILock) {
            if (displayActive) {
               var4 = true;
               displayActive = false;
               this.firstShowAfterFg = false;
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
                     var6 = true;
                  }
               }

               if (this.currentDisplayable.isSystemScreen()) {
                  this.pendingDisplayable = this.currentDisplayable.getBottomOfStackDisplayable();
                  this.stateOfScreenChange = 1;
                  var6 = true;
               }
            }
         }

         if (var4) {
            if (var7 != null) {
               synchronized(calloutLock) {
                  var7.displayInactive(this);
               }
            }

            if (var6) {
               this.consumeScreenChange();
            }
         }
         break;
      default:
         throw new RuntimeException("Display ERROR: Unknown event type " + var1);
      }

   }

   final void discardEvent(int var1, int var2) {
      if (var1 == 2) {
         switch(var2) {
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

   void flushImageToScreen(Displayable var1, Image var2, int var3, int var4, int var5, int var6) {
      if (this == theActiveDisplay && displayIsVisible && var1.isShown() && this.displayGraphics != null && !this.screenChangeInProgress && var2.getPixmap().isGameCanvasPixmap()) {
         int var7 = 0;
         Zone var8 = var1.getTickerZone();
         if (var8 != null) {
            var7 = var8.y + var8.height + var8.getMarginTop() + var8.getMarginBottom();
         }

         this.displayGraphics.reset();
         this.displayGraphics.translate(0, var7);
         this.displayGraphics.setClip(0, 0, var1.getWidth(), var1.getHeight());
         this.displayGraphics.clipRect(var3, var4, var5, var6);
         Graphics var9 = var2.getGraphics();
         var9.refresh(this.displayGraphics.getClipX(), this.displayGraphics.getClipY(), this.displayGraphics.getClipWidth(), this.displayGraphics.getClipHeight(), 0, var7);
      }
   }

   void hideOldDisplayable() {
      Displayable var1 = null;
      synchronized(LCDUILock) {
         if (this.currentDisplayable != null && this.currentDisplayable.isShown() && this.currentDisplayable != defaultDisplayable) {
            var1 = this.currentDisplayable;
         }
      }

      if (var1 != null) {
         var1.callHideNotifyInProgress(this);
         var1.callHideNotify(this);
      }

   }

   void resetDisplay(MIDlet var1) {
      this.hideOldDisplayable();
      synchronized(LCDUILock) {
         this.stateOfScreenChange = 0;
         this.pendingMidletScreenChange = false;
         this.cleanupDisplayableStack();
         this.myMIDlet = var1;
         this.myMIDletName = midletAccessor.getMIDletName(this.myMIDlet);
         this.currentDisplayable = defaultDisplayable;
         defaultDisplayable.deleteAll();
         defaultDisplayable.append((Item)(new StringItem((String)null, TextDatabase.getText(0, this.myMIDletName))));
         if (defaultDisplayable.isShown()) {
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

   private static void changeDisplayLayoutImpl(Displayable var0) {
      if (var0 instanceof Canvas) {
         if (var0.fullScreenMode) {
            SoftLabel.makeAllInvisible();
            layout = 3;
         } else {
            layout = 2;
         }
      } else if (var0 instanceof Alert) {
         layout = 1;
      } else {
         layout = 0;
      }

      notifyLayoutChange(layout, var0.isNativeDelegate(), var0.isPopup());

      while(!checkLayoutChangeComplete(midletId)) {
         try {
            Thread.sleep(5L);
         } catch (InterruptedException var2) {
            var2.printStackTrace();
         }
      }

   }

   private final void consumeScreenChange() {
      boolean var1 = false;
      Displayable var2 = null;
      synchronized(LCDUILock) {
         label139: {
            this.pendingMidletScreenChange = false;
            Item var4 = this.pendingItem;
            this.pendingItem = null;
            if (this.stateOfScreenChange == 1) {
               if (this.pendingDisplayable == null) {
                  this.stateOfScreenChange = 0;
                  this.pendingParentDisplayable = null;
                  return;
               }

               if (this.currentDisplayable.setDisplayableToTopOfStack(this.pendingParentDisplayable, this.pendingDisplayable) || this.currentDisplayable == defaultDisplayable && this.pendingDisplayable == defaultDisplayable && !defaultDisplayable.isShown()) {
                  this.stateOfScreenChange = 2;
                  this.screenChangeInProgress = true;
                  this.inProgressDisplayable = this.pendingDisplayable;
                  this.pendingDisplayable = null;
                  this.pendingParentDisplayable = null;
                  if (var4 != null && var4.owner == this.inProgressDisplayable && this.inProgressDisplayable instanceof Form) {
                     ((Form)this.inProgressDisplayable).setCurrentItem(var4);
                  }
                  break label139;
               }

               if (var4 != null && var4.owner == this.currentDisplayable && this.currentDisplayable instanceof Form) {
                  ((Form)this.currentDisplayable).setCurrentItem(var4);
               }

               this.stateOfScreenChange = 0;
               if (!this.currentDisplayable.isDisplayableInStack(this.pendingDisplayable)) {
                  this.pendingDisplayable.removedFromDisplayNotify(this);
               }

               this.pendingDisplayable = null;
               this.pendingParentDisplayable = null;
               return;
            }

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

         var1 = this.currentDisplayable.isShown();
         var2 = this.currentDisplayable;
      }

      if (var1) {
         var2.callHideNotifyInProgress(this);
      }

      if (displayIsVisible) {
         this.inProgressDisplayable.callShowNotify(this);
      }

      synchronized(LCDUILock) {
         this.currentDisplayable = this.inProgressDisplayable;
         this.inProgressDisplayable = null;
         if (this.currentDisplayable.isSystemScreen()) {
            if (!var2.isSystemScreen()) {
               this.midletCurrentDisplayable = var2;
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
            changeDisplayLayoutImpl(this.currentDisplayable);
            this.displayGraphics = new DirectGraphicsImpl(Pixmap.getUpdatedDisplayPixmap().getGraphics());
            this.updateStatusZone();
         }
      }

      if (displayIsVisible && !this.currentDisplayable.isNativeDelegate()) {
         this.requestServiceRepaints();
      }

      if (var1) {
         var2.callHideNotify(this);
      }

      synchronized(LCDUILock) {
         if (this.currentDisplayable.isDisplayableStackCleanupRequired(var2)) {
            var2.cleanupDisplayableStack(this);
         }

         this.screenChangeInProgress = false;
      }
   }

   private static native boolean checkLayoutChangeComplete(int var0);

   boolean clearScreenOnServiceRepaints(Displayable var1) {
      boolean var2 = false;
      if (!(var1 instanceof GameCanvas) && !(var1 instanceof Canvas)) {
         var2 = true;
      } else if (this.screenChangeInProgress && this.stateOfRepaint == 2 || !(var1 instanceof GameCanvas) && UIStyle.isCanvasHasBgImage() && !var1.fullScreenMode) {
         var2 = true;
      }

      return var2;
   }

   private static native void notifyLayoutChange(int var0, boolean var1, boolean var2);

   private native void notifyStatusZoneUpdate(String var1);

   private static native void nativeStaticInitializer();

   private native boolean requestDisplayStateChange(int var1, boolean var2, String var3);

   private void setCurrentImpl(Displayable var1, Displayable var2, Item var3, String var4, boolean var5, boolean var6) {
      boolean var7 = false;
      boolean var9 = false;
      Displayable var8;
      if (this.stateOfScreenChange != 2 && this.stateOfScreenChange != 3) {
         var8 = this.currentDisplayable;
      } else {
         var8 = this.inProgressDisplayable;
      }

      if (!barbiePlatform && var2 != null) {
         var7 = true;
      } else if (var6) {
         var7 = true;
      } else if (var2 == null) {
         if (var5) {
            var7 = this.requestDisplayStateChange(midletId, false, (String)null);
            if (displayActive || var8 == defaultNoDispSetDisplayable) {
               var9 = true;
            }

            var2 = defaultNoDispSetDisplayable;
         }
      } else if (displayActive && this.midletRequestedDSCS != 1) {
         var7 = true;
      } else if (var5) {
         var7 = this.requestDisplayStateChange(midletId, true, var4);
         var9 = true;
      }

      if (var7) {
         switch(this.stateOfScreenChange) {
         case 0:
            if (var2 == this.currentDisplayable && var3 == null && (this.currentDisplayable != defaultDisplayable || defaultDisplayable.isShown())) {
               return;
            }

            this.stateOfScreenChange = 1;
            this.setCurrentCommonNoPendingHandling(var1, (Displayable)var2, var3, var5, var9);
            break;
         case 1:
         case 3:
            this.setCurrentCommonPendingHandling(var8, var1, (Displayable)var2, var3, var5);
            break;
         case 2:
            if (var2 == var8 && var3 == null) {
               return;
            }

            this.stateOfScreenChange = 3;
            this.setCurrentCommonNoPendingHandling(var1, (Displayable)var2, var3, var5, var9);
         }
      }

   }

   private void setCurrentCommonNoPendingHandling(Displayable var1, Displayable var2, Item var3, boolean var4, boolean var5) {
      this.pendingDisplayable = var2;
      this.pendingParentDisplayable = var1;
      this.pendingItem = var3;
      this.pendingMidletScreenChange = var4;
      if (!var5) {
         JavaEventGenerator.s_generateEvent(0, 2, 1, 0);
      }

   }

   private void setCurrentCommonPendingHandling(Displayable var1, Displayable var2, Displayable var3, Item var4, boolean var5) {
      if (var5 || !this.pendingMidletScreenChange) {
         this.pendingMidletScreenChange = var5;
         if (this.pendingDisplayable != var3) {
            if (this.pendingDisplayable != null && !var1.isDisplayableInStack(this.pendingDisplayable)) {
               this.pendingDisplayable.removedFromDisplayNotify(this);
            }

            this.pendingDisplayable = var3;
         }

         this.pendingParentDisplayable = var2;
         this.pendingItem = var4;
      }

   }

   private void setStatusZoneStr(String var1) {
      if (!var1.equals(statusZoneString)) {
         statusZoneString = var1;
         this.notifyStatusZoneUpdate(var1);
      }

   }

   private Zone getImageZone(int var1) {
      Zone var2;
      switch(var1) {
      case 1:
         var2 = Displayable.uistyle.getZone(16);
         break;
      case 2:
         var2 = Displayable.uistyle.getZone(11);
         break;
      case 3:
         var2 = Displayable.uistyle.getZone(32);
         break;
      default:
         throw new IllegalArgumentException();
      }

      return var2;
   }

   private native void nativeDisplayNote();

   private native void nativeHideNote();

   private native boolean nativeIsNoteDisplayed();

   // $FF: synthetic method
   Display(MIDlet var1, Object var2) {
      this(var1);
   }

   static {
      nativeStaticInitializer();
      defaultDisplayable.addCommand(defaultQuitCmd);
      defaultNoDispSetDisplayable.append((Item)(new StringItem((String)null, "")));
   }

   private class DisplayAccessor implements DisplayAccess, EventConsumer, CommandListener {
      private DisplayAccessor() {
      }

      public void commandAction(Command var1, Displayable var2) {
         synchronized(Display.LCDUILock) {
            if (Display.this == Display.theActiveDisplay) {
               Display.displayEventParamQueue.addElement(Display.this.myMIDlet);
               JavaEventGenerator.s_generateEvent(0, 2, 9, 0);
            }

         }
      }

      public void consumeEvent(int var1, int var2, int var3) {
         synchronized(Display.displayTransitionLock) {
            boolean var5 = true;
            if (Display.this != Display.theActiveDisplay) {
               var5 = false;
               synchronized(Display.LCDUILock) {
                  Display.this.discardEvent(var1, var2);
               }
            }

            if (var5) {
               switch(var1) {
               case 1:
                  Display.this.consumeUIEvent(var2, var3);
                  break;
               case 2:
                  Display.this.consumeDisplayEvent(var2, var3);
               case 3:
               default:
                  break;
               case 4:
                  Displayable var6 = Display.this.currentDisplayable;
                  if (var6 != null) {
                     var6.callDelegateEvent(var2, var3);
                  }
               }
            }

         }
      }

      public void flushImageToScreen(Displayable var1, Image var2, int var3, int var4, int var5, int var6) {
         synchronized(Display.LCDUILock) {
            Display.this.flushImageToScreen(var1, var2, var3, var4, var5, var6);
         }
      }

      public DisplayAccess replaceDisplay(MIDlet var1) {
         synchronized(Display.displayTransitionLock) {
            Display var3 = Display.theActiveDisplay;
            if (Display.this != var3 && var3 != null) {
               return this;
            } else {
               if (var3 != null) {
                  var3.hideOldDisplayable();
               }

               Display.DisplayAccessor var10000;
               synchronized(Display.LCDUILock) {
                  if (var3 != null) {
                     var3.cleanupDisplayableStack();
                  }

                  Display.theActiveDisplay = new Display(var1);
                  var10000 = Display.theActiveDisplay.myDa;
               }

               return var10000;
            }
         }
      }

      public void resetDisplay(MIDlet var1) {
         synchronized(Display.displayTransitionLock) {
            if (Display.this == Display.theActiveDisplay) {
               Display.this.resetDisplay(var1);
            }

         }
      }

      public void setForeground(MIDlet var1) {
         synchronized(Display.displayTransitionLock) {
            if (Display.this == Display.theActiveDisplay) {
               if (Display.this.myMIDlet != var1) {
                  Display.this.resetDisplay(var1);
               }

               synchronized(Display.LCDUILock) {
                  if (Display.this.stateOfScreenChange == 0 && Display.this.currentDisplayable == Display.defaultDisplayable) {
                     Display.this.setCurrentImpl((Displayable)null, Display.this.currentDisplayable, (Item)null, (String)null, false, true);
                  }
               }
            }

         }
      }

      public Image createImage(Pixmap var1) {
         return new Image(var1);
      }

      public Pixmap getImagePixmap(Image var1) {
         return var1.getPixmap();
      }

      public Item createMMItem() {
         Item var1 = null;

         try {
            var1 = (Item)Class.forName("javax.microedition.lcdui.MMItemImpl").newInstance();
         } catch (Exception var3) {
         }

         return var1;
      }

      public void showCanvasVideo(Canvas var1, int var2, boolean var3, int var4, int var5, int var6, int var7) {
         var1.showVideo(var3, var2, var4, var5, var6, var7);
      }

      public boolean isDisplayActive(Display var1) {
         if (var1 != Display.theActiveDisplay) {
            throw new IllegalStateException();
         } else {
            Display var10000 = Display.this;
            return Display.displayActive;
         }
      }

      public void setCurrent(Display var1, Displayable var2, String var3) {
         if (var1 != Display.theActiveDisplay) {
            throw new IllegalStateException();
         } else {
            Display.this.setCurrent(var2, var3);
         }
      }

      public void setCurrent(Display var1, Alert var2, Displayable var3, String var4) {
         if (var1 != Display.theActiveDisplay) {
            throw new IllegalStateException();
         } else {
            Display.this.setCurrent(var2, var3, var4);
         }
      }

      public void setCurrentItem(Display var1, Item var2, String var3) {
         if (var1 != Display.theActiveDisplay) {
            throw new IllegalStateException();
         } else {
            Display.this.setCurrentItem(var2, var3);
         }
      }

      public void setDisplayStateListener(Display var1, DisplayStateListener var2) {
         synchronized(Display.LCDUILock) {
            if (var1 != Display.theActiveDisplay) {
               throw new IllegalStateException();
            } else {
               Display.this.displayStateListener = var2;
            }
         }
      }

      public void setVisibilityListener(Displayable var1, VisibilityListener var2) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            synchronized(Display.LCDUILock) {
               var1.setVisibilityListener(var2);
            }
         }
      }

      // $FF: synthetic method
      DisplayAccessor(Object var2) {
         this();
      }
   }
}
