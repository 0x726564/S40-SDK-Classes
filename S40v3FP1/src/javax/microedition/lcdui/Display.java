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
   private static Vector displayEventParamQueue = new Vector();
   private static boolean midletHasForeground;
   private static boolean midletVisibleAfterFold;
   private static String statusZoneString;
   private static final EventProducer eventDispatcher = InitJALM.s_getEventProducer();
   private static Display theActiveDisplay;
   private static MIDletAccess midletAccessor;
   private final OptionsMenu optionsMenu;
   private final TextBox textFieldEditScreen;
   private final TruncatedItemScreen truncatedItemScreen;
   private final Display.DisplayAccessor myDa;
   private int stateOfScreenChange;
   private int stateOfRepaint;
   private Displayable currentDisplayable;
   private Displayable midletCurrentDisplayable;
   private Displayable pendingDisplayable;
   private Displayable pendingParentDisplayable;
   private Displayable inProgressDisplayable;
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
         OptionsMenu var3 = null;

         try {
            var3 = (OptionsMenu)Class.forName("javax.microedition.lcdui.JavaOptionsMenu").newInstance();
         } catch (Exception var6) {
         }

         this.optionsMenu = var3;
         TextBox var4 = new TextBox((String)null, (String)null, 1, 0);
         var4.setSystemScreen(true);
         this.textFieldEditScreen = var4;
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
            if (!midletHasForeground || midletHasForeground && this.currentDisplayable != null && this.currentDisplayable.isNativeDelegate()) {
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
         synchronized(LCDUILock) {
            if (this == theActiveDisplay) {
               if (var1 instanceof Alert) {
                  Displayable var3 = this.currentDisplayable;
                  if (this.stateOfScreenChange != 2 && this.stateOfScreenChange != 3) {
                     if (this.currentDisplayable.isSystemScreen()) {
                        var3 = this.midletCurrentDisplayable;
                     }
                  } else if (this.inProgressDisplayable.isSystemScreen()) {
                     if (this.currentDisplayable.isSystemScreen()) {
                        var3 = this.midletCurrentDisplayable;
                     }
                  } else {
                     var3 = this.inProgressDisplayable;
                  }

                  if (var3 == var1) {
                     if (var3 instanceof Alert && var3 == this.currentDisplayable && this.stateOfScreenChange != 2 && this.stateOfScreenChange != 3) {
                        ((Alert)var3).restartTimerIfNecessary();
                     }

                     return;
                  }

                  if (var3 instanceof Alert) {
                     throw new IllegalArgumentException("Current Displayable is an Alert");
                  }

                  ((Alert)var1).setReturnScreen(var3);
                  if (this.foldClosed && !this.nativeIsNoteDisplayed()) {
                     this.nativeDisplayNote();
                  }
               } else if (this.foldClosed && this.nativeIsNoteDisplayed()) {
                  this.nativeHideNote();
               }

               this.setCurrentImpl((Displayable)null, var1, true);
            }
         }
      }

   }

   public void setCurrent(Alert var1, Displayable var2) {
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

                  if (this.foldClosed && !this.nativeIsNoteDisplayed()) {
                     this.nativeDisplayNote();
                  }

                  this.setCurrentImpl((Displayable)null, var1, true);
               }

            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void setCurrentItem(Item var1) {
      synchronized(LCDUILock) {
         Screen var3 = var1.owner;
         if (var3 != null && !(var3 instanceof Alert)) {
            if (this == theActiveDisplay) {
               if (var3 instanceof Form) {
                  ((Form)var3).setCurrentItem(var1);
               }

               this.setCurrentImpl((Displayable)null, var3, true);
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
            if (!midletHasForeground && (this.currentDisplayable == null || !this.currentDisplayable.isNativeDelegate())) {
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
      if (this == theActiveDisplay) {
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
      if (this.currentDisplayable != defaultDisplayable && this.currentDisplayable != null) {
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

   TextBox getEditorScreen() {
      return this.textFieldEditScreen;
   }

   String getMIDletName() {
      return this.myMIDletName;
   }

   TruncatedItemScreen getTruncatedItemScreen() {
      return this.truncatedItemScreen;
   }

   static boolean isMidletHasForeground() {
      return midletHasForeground;
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
               JavaEventGenerator.s_generateEvent(0, 2, 2, 0);
               this.stateOfRepaint = 1;
            }
         }

      }
   }

   void requestFullRepaint(Displayable var1) {
      synchronized(LCDUILock) {
         if (this == theActiveDisplay && var1.isShown()) {
            if (this.stateOfRepaint == 0) {
               JavaEventGenerator.s_generateEvent(0, 2, 2, 0);
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
               if (this.foldClosed && !this.nativeIsNoteDisplayed()) {
                  this.nativeDisplayNote();
               }
            } else if (this.foldClosed && this.nativeIsNoteDisplayed()) {
               this.nativeHideNote();
            }

            this.setCurrentImpl(var1, var2, false);
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

               if (midletHasForeground && this.currentDisplayable.isShown() && this.displayGraphics != null && !this.currentDisplayable.isNativeDelegate()) {
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
                  if (!(var7 instanceof GameCanvas) && !(var7 instanceof Canvas)) {
                     var7.clearScreen(this.displayGraphics);
                  } else if (this.screenChangeInProgress && this.stateOfRepaint == 2 || !(var7 instanceof GameCanvas) && UIStyle.isCanvasHasBgImage() && !var7.fullScreenMode) {
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
      if (this == theActiveDisplay) {
         if (this.currentDisplayable instanceof TextBox) {
            this.setStatusZoneStr("");
         } else {
            String var1 = this.currentDisplayable.getTitle();
            if (var1 == null || var1.length() == 0) {
               var1 = this.getMIDletName();
            }

            this.setStatusZoneStr(var1);
         }
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

   final void consumeDisplayEvent(int var1) {
      boolean var2 = false;
      boolean var32;
      Displayable var33;
      MIDlet var4;
      Object var5;
      switch(var1) {
      case 1:
         this.consumeScreenChange();
         break;
      case 2:
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
      case 6:
         var4 = null;
         synchronized(LCDUILock) {
            var33 = (Displayable)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
         }

         if (var33 != null) {
            var33.callInvalidate();
         }
         break;
      case 7:
         var4 = null;
         var5 = null;
         Item var31;
         synchronized(LCDUILock) {
            var33 = (Displayable)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            var31 = (Item)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            var31.stateChanging = false;
         }

         if (var33 != null) {
            var33.callItemStateChanged(var31);
         }
         break;
      case 8:
         var4 = null;
         var5 = null;
         Object var6 = null;
         Command var30;
         synchronized(LCDUILock) {
            var30 = (Command)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            var5 = displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            var6 = displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
         }

         if (var5 instanceof Displayable) {
            synchronized(calloutLock) {
               ((CommandListener)var6).commandAction(var30, (Displayable)var5);
            }
         } else if (var5 instanceof Item) {
            synchronized(calloutLock) {
               ((ItemCommandListener)var6).commandAction(var30, (Item)var5);
            }
         }
         break;
      case 9:
         var4 = null;
         synchronized(LCDUILock) {
            var4 = (MIDlet)displayEventParamQueue.firstElement();
            displayEventParamQueue.removeElementAt(0);
            if (var4 != this.myMIDlet) {
               var4 = null;
            }
         }

         if (var4 != null) {
            midletAccessor.destroyMIDlet(var4);
         }
         break;
      case 10:
         var2 = true;
         synchronized(LCDUILock) {
            this.foldClosed = true;
         }
      case 4:
         var33 = null;
         var32 = false;
         synchronized(LCDUILock) {
            if (!var2) {
               midletVisibleAfterFold = false;
            }

            if (midletHasForeground) {
               midletHasForeground = false;
               if (!this.currentDisplayable.isNativeDelegate()) {
                  var33 = this.currentDisplayable;
                  var32 = true;
               }
            }
         }

         if (var32) {
            var33.callHideNotifyInProgress(this);
            var33.callHideNotify(this);
         }
         break;
      case 11:
         var2 = true;
         synchronized(LCDUILock) {
            this.foldClosed = false;
            if (this.nativeIsNoteDisplayed()) {
               this.nativeHideNote();
               break;
            }
         }
      case 5:
         var33 = null;
         var32 = false;
         boolean var34 = false;
         synchronized(LCDUILock) {
            if (!var2) {
               midletVisibleAfterFold = true;
            }

            if ((!var2 && !this.foldClosed || var2 && midletVisibleAfterFold) && !midletHasForeground) {
               midletHasForeground = true;
               var33 = this.currentDisplayable;
               if (!this.currentDisplayable.isShown()) {
                  var34 = true;
               }

               this.stateOfRepaint = 2;
               var32 = true;
            }
         }

         if (var32) {
            if (var34) {
               var33.callShowNotify(this);
            }

            this.requestServiceRepaints();
         }
         break;
      case 12:
         synchronized(LCDUILock) {
            this.stateOfRepaint = 2;
         }

         this.requestServiceRepaints();
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
      if (this == theActiveDisplay && midletHasForeground && var1.isShown() && this.displayGraphics != null && !this.screenChangeInProgress && var2.getPixmap().isGameCanvasPixmap()) {
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
         this.currentDisplayable = defaultDisplayable;
         if (defaultDisplayable.isShown()) {
            defaultDisplayable.myDisplay = this;
            this.displayGraphics = new DirectGraphicsImpl(Pixmap.getUpdatedDisplayPixmap().getGraphics());
         }

         this.midletCurrentDisplayable = null;
         this.pendingDisplayable = null;
         this.inProgressDisplayable = null;
         this.stateOfRepaint = 0;
         this.paintRectX1 = 9999;
         this.paintRectY1 = 9999;
         this.paintRectX2 = 0;
         this.paintRectY2 = 0;
         this.myMIDlet = var1;
         this.myMIDletName = midletAccessor.getMIDletName(this.myMIDlet);
      }
   }

   private static void changeDisplayLayoutImpl(Displayable var0) {
      byte var1;
      if (var0 instanceof Canvas) {
         if (var0.fullScreenMode) {
            SoftLabel.makeAllInvisible();
            var1 = 3;
         } else {
            var1 = 2;
         }
      } else if (var0 instanceof Alert) {
         var1 = 1;
      } else {
         var1 = 0;
      }

      notifyLayoutChange(var1, var0.isNativeDelegate(), var0.isPopup());

      while(!checkLayoutChangeComplete()) {
         try {
            Thread.sleep(5L);
         } catch (InterruptedException var3) {
            var3.printStackTrace();
         }
      }

   }

   private final void consumeScreenChange() {
      boolean var1 = false;
      boolean var2 = true;
      Displayable var3 = null;
      synchronized(LCDUILock) {
         this.pendingMidletScreenChange = false;
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

         if (!this.currentDisplayable.setDisplayableToTopOfStack(this.pendingParentDisplayable, this.pendingDisplayable) && (this.currentDisplayable != defaultDisplayable || this.pendingDisplayable != defaultDisplayable || defaultDisplayable.isShown())) {
            if (this.currentDisplayable instanceof TextBox) {
               ((TextBox)this.currentDisplayable).callProcessKeys();
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
         var1 = this.currentDisplayable.isShown();
         var3 = this.currentDisplayable;
      }

      if (var1) {
         var3.callHideNotifyInProgress(this);
      }

      if (midletHasForeground) {
         this.inProgressDisplayable.callShowNotify(this);
      }

      synchronized(LCDUILock) {
         this.currentDisplayable = this.inProgressDisplayable;
         this.inProgressDisplayable = null;
         if (this.currentDisplayable.isSystemScreen()) {
            if (!var3.isSystemScreen()) {
               this.midletCurrentDisplayable = var3;
            }
         } else {
            this.midletCurrentDisplayable = null;
         }

         if (this.stateOfScreenChange == 3) {
            this.stateOfScreenChange = 1;
         } else {
            this.stateOfScreenChange = 0;
         }

         if (midletHasForeground) {
            this.stateOfRepaint = 2;
         }

         changeDisplayLayoutImpl(this.currentDisplayable);
         this.displayGraphics = new DirectGraphicsImpl(Pixmap.getUpdatedDisplayPixmap().getGraphics());
         this.updateStatusZone();
      }

      if (midletHasForeground && !this.currentDisplayable.isNativeDelegate()) {
         this.requestServiceRepaints();
      }

      if (var1) {
         var3.callHideNotify(this);
      }

      synchronized(LCDUILock) {
         if (this.currentDisplayable.isDisplayableStackCleanupRequired(var3)) {
            var3.cleanupDisplayableStack(this);
         }

         this.screenChangeInProgress = false;
      }
   }

   private static native boolean checkLayoutChangeComplete();

   private static native void notifyLayoutChange(int var0, boolean var1, boolean var2);

   private native void notifyStatusZoneUpdate(String var1);

   private static native void nativeStaticInitializer();

   private void setCurrentImpl(Displayable var1, Displayable var2, boolean var3) {
      switch(this.stateOfScreenChange) {
      case 0:
         if (var2 == this.currentDisplayable && (this.currentDisplayable != defaultDisplayable || defaultDisplayable.isShown())) {
            return;
         }

         this.stateOfScreenChange = 1;
         this.setCurrentCommonNoPendingHandling(var1, var2, var3);
         break;
      case 1:
         this.setCurrentCommonPendingHandling(this.currentDisplayable, var1, var2, var3);
         break;
      case 2:
         if (var2 == this.inProgressDisplayable) {
            return;
         }

         this.stateOfScreenChange = 3;
         this.setCurrentCommonNoPendingHandling(var1, var2, var3);
         break;
      case 3:
         if (var2 == this.inProgressDisplayable) {
            return;
         }

         this.setCurrentCommonPendingHandling(this.inProgressDisplayable, var1, var2, var3);
      }

   }

   private void setCurrentCommonNoPendingHandling(Displayable var1, Displayable var2, boolean var3) {
      this.pendingDisplayable = var2;
      this.pendingParentDisplayable = var1;
      this.pendingMidletScreenChange = var3;
      JavaEventGenerator.s_generateEvent(0, 2, 1, 0);
   }

   private void setCurrentCommonPendingHandling(Displayable var1, Displayable var2, Displayable var3, boolean var4) {
      if (var4 || !this.pendingMidletScreenChange) {
         this.pendingMidletScreenChange = var4;
         if (this.pendingDisplayable != var3) {
            if (this.pendingDisplayable != null && !var1.isDisplayableInStack(this.pendingDisplayable)) {
               this.pendingDisplayable.removedFromDisplayNotify(this);
            }

            this.pendingDisplayable = var3;
         }

         this.pendingParentDisplayable = var2;
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
      midletVisibleAfterFold = midletHasForeground;
      defaultDisplayable.append((Item)(new StringItem((String)null, TextDatabase.getText(0))));
      defaultDisplayable.addCommand(defaultQuitCmd);
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
                  Display.this.consumeDisplayEvent(var2);
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
                     Display.this.setCurrentImpl((Displayable)null, Display.this.currentDisplayable, false);
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

      // $FF: synthetic method
      DisplayAccessor(Object var2) {
         this();
      }
   }
}
