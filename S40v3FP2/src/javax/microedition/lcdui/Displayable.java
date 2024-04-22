package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.KeyMap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import com.nokia.mid.ui.lcdui.VisibilityListener;
import javax.microedition.lcdui.game.GameCanvas;

public abstract class Displayable {
   static final int X = 0;
   static final int Y = 1;
   static final int WIDTH = 2;
   static final int HEIGHT = 3;
   static final UIStyle uistyle = UIStyle.getUIStyle();
   static final SoftkeyManager softkeyManager = SoftkeyManager.getSoftkeyManager();
   static final KeyMap keyMap = KeyMap.getKeyMap();
   static final Zone screenNormMainZone;
   static final Zone screenNormScrollbarZone;
   static final Zone screenTickMainZone;
   static final Zone screenTickScrollbarZone;
   static final Zone screenTickTickerZone;
   Ticker ticker;
   String title;
   int[] viewport;
   boolean fullScreenMode;
   boolean sizeChangeOccurred;
   boolean displayed;
   Display myDisplay;
   CommandVector displayableCommands;
   CommandVector softkeyCommands;
   CommandVector optionMenuCommands;
   SoftLabel sl;
   CommandListener commandListener;
   VisibilityListener visibilityListener;
   private boolean isSystemScreen;
   private boolean nativeDelegate;
   private boolean popup;
   private boolean afterShowNotify;
   private Displayable parentDisplayable;
   boolean layoutValid;

   Displayable() {
      this((String)null);
   }

   Displayable(String var1) {
      this.ticker = null;
      this.title = null;
      this.viewport = new int[4];
      this.fullScreenMode = false;
      this.sizeChangeOccurred = true;
      this.displayed = false;
      this.displayableCommands = new CommandVector();
      this.softkeyCommands = new CommandVector(3);
      this.optionMenuCommands = new CommandVector();
      this.sl = new SoftLabel(this);
      this.commandListener = null;
      this.isSystemScreen = false;
      this.nativeDelegate = false;
      this.popup = false;
      this.afterShowNotify = false;
      this.parentDisplayable = null;
      this.layoutValid = false;
      synchronized(Display.LCDUILock) {
         this.setupViewport();
         this.setTitleImpl(var1);
      }
   }

   public void addCommand(Command var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         synchronized(Display.LCDUILock) {
            this.addCommandImpl(var1);
         }
      }
   }

   public void removeCommand(Command var1) {
      if (var1 != null) {
         synchronized(Display.LCDUILock) {
            this.removeCommandImpl(var1);
         }
      }
   }

   public void setCommandListener(CommandListener var1) {
      synchronized(Display.LCDUILock) {
         this.commandListener = var1;
      }
   }

   public Ticker getTicker() {
      return this.ticker;
   }

   public void setTicker(Ticker var1) {
      synchronized(Display.LCDUILock) {
         this.setTickerImpl(var1);
      }
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String var1) {
      synchronized(Display.LCDUILock) {
         this.setTitleImpl(var1);
      }
   }

   public int getWidth() {
      return this.viewport[2];
   }

   public int getHeight() {
      return this.viewport[3];
   }

   public boolean isShown() {
      return this.isShownImpl();
   }

   protected void sizeChanged(int var1, int var2) {
   }

   boolean isShownImpl() {
      return this.displayed;
   }

   void setVisibilityListener(VisibilityListener var1) {
      this.visibilityListener = var1;
   }

   boolean addCommandImpl(Command var1) {
      boolean var2 = this.displayableCommands.addCommand(var1);
      if (var2) {
         this.updateSoftkeys(true);
      }

      return var2;
   }

   boolean removeCommandImpl(Command var1) {
      boolean var2 = this.displayableCommands.removeCommand(var1);
      if (var2) {
         this.updateSoftkeys(true);
      }

      return var2;
   }

   void setTickerImpl(Ticker var1) {
      Ticker var2 = this.ticker;
      if (var2 != var1) {
         this.ticker = var1;
         if (!this.fullScreenMode) {
            if (this.displayed) {
               if (var1 != null) {
                  var1.showOn(this);
               }

               if (var2 != null) {
                  var2.hideFrom(this);
               }
            }

            if (var2 == null || var1 == null) {
               this.sizeChangeOccurred = true;
               this.setupViewport();
               this.invalidate();
            }
         }
      }

   }

   void setTitleImpl(String var1) {
      this.title = var1;
      if (this.displayed) {
         this.myDisplay.updateStatusZone();
      }

      OptionsMenu var2;
      if (this.myDisplay != null && ((Displayable)(var2 = this.myDisplay.getOptionsMenu())).parentDisplayable == this) {
         ((Displayable)var2).setTitleImpl(this.title);
      }

   }

   void layout() {
      this.setupViewport();
      this.layoutValid = true;
   }

   boolean usesSoftLabel() {
      return false;
   }

   boolean callsVisListOnHideNotify() {
      return false;
   }

   Command[] getExtraCommands() {
      return null;
   }

   boolean launchExtraCommand(Command var1) {
      return false;
   }

   Item getCurrentItem() {
      return null;
   }

   void callKeyPressed(int var1, int var2) {
      if (!this.usesSoftLabel()) {
         Command var3 = null;
         boolean var4 = false;
         boolean var5 = false;
         ItemCommandListener var6 = null;
         Item var7 = null;
         synchronized(Display.LCDUILock) {
            if (DeviceInfo.isSoftkey(var1)) {
               if (this.fullScreenMode) {
                  var3 = OptionsMenu.optionsCommand;
               } else {
                  switch(var1) {
                  case -7:
                     var3 = this.softkeyCommands.getCommand(2);
                     break;
                  case -6:
                     var3 = this.softkeyCommands.getCommand(0);
                     break;
                  case -5:
                     var3 = this.softkeyCommands.getCommand(1);
                  }
               }
            }

            if (var3 == null) {
               return;
            }

            if (var3 == OptionsMenu.optionsCommand) {
               OptionsMenu var9 = this.myDisplay.getOptionsMenu();
               this.myDisplay.setCurrentInternal(this, (Displayable)var9);
            } else if (this.displayableCommands.containsCommand(var3)) {
               var4 = true;
            } else if ((var7 = this.getCurrentItem()) != null && var7.itemCommands.containsCommand(var3)) {
               var6 = var7.getItemCommandListener();
               var5 = true;
            } else {
               this.launchExtraCommand(var3);
            }
         }

         if (var4) {
            CommandListener var8 = this.commandListener;
            if (var8 != null) {
               synchronized(Display.calloutLock) {
                  var8.commandAction(var3, this);
               }
            }
         } else if (var5 && var6 != null) {
            synchronized(Display.calloutLock) {
               var6.commandAction(var3, var7);
            }
         }

      }
   }

   void callKeyRepeated(int var1, int var2) {
   }

   void callKeyReleased(int var1, int var2) {
   }

   void callShowNotify(Display var1) {
      VisibilityListener var2 = this.visibilityListener;
      synchronized(Display.LCDUILock) {
         if (!this.displayed) {
            this.myDisplay = var1;
            this.displayed = true;
            if (this.ticker != null && !this.fullScreenMode) {
               this.ticker.showOn(this);
            }
         }

         this.afterShowNotify = true;
      }

      this.callInvalidate();
      if (var2 != null) {
         var2.showNotify(var1, this);
      }

   }

   void callHideNotifyInProgress(Display var1) {
   }

   void callHideNotify(Display var1) {
      VisibilityListener var2 = this.visibilityListener;
      synchronized(Display.LCDUILock) {
         if (this.displayed) {
            if (this.ticker != null && !this.fullScreenMode) {
               this.ticker.hideFrom(this);
            }

            this.displayed = false;
         }
      }

      if (!this.callsVisListOnHideNotify() && var2 != null) {
         synchronized(Display.calloutLock) {
            var2.hideNotify(var1, this);
         }
      }

   }

   void removedFromDisplayNotify(Display var1) {
   }

   void callPaint(Graphics var1) {
      if (!this.fullScreenMode) {
         synchronized(Display.LCDUILock) {
            if (this.ticker != null) {
               this.ticker.paint(var1);
            }

            if (this.afterShowNotify) {
               uistyle.hideIndex();
               this.sl.updateAll();
               this.afterShowNotify = false;
            }

         }
      }
   }

   void callInvalidate() {
      boolean var1 = false;
      synchronized(Display.LCDUILock) {
         this.setupViewport();
         if (this.sizeChangeOccurred) {
            this.sizeChangeOccurred = false;
            var1 = true;
         }
      }

      if (var1) {
         synchronized(Display.calloutLock) {
            this.sizeChanged(this.viewport[2], this.viewport[3]);
         }
      }

   }

   void callItemStateChanged(Item var1) {
   }

   void callDelegateEvent(int var1, int var2) {
   }

   void repaintArea(int var1, int var2, int var3, int var4) {
      if (this.displayed) {
         this.myDisplay.requestRepaint(this, var1, var2, var3, var4);
      }

   }

   void repaintFull() {
      if (this.displayed) {
         this.myDisplay.requestFullRepaint(this);
      }

   }

   void invalidate() {
      if (this.layoutValid) {
         this.layoutValid = false;
         if (this.myDisplay != null) {
            this.myDisplay.requestInvalidate(this);
         }
      }

   }

   void changedItemState(Item var1) {
      if (!var1.stateChanging) {
         var1.stateChanging = true;
         Display var2 = this.myDisplay == null ? Display.getActiveDisplay() : this.myDisplay;
         if (var2 != null) {
            var2.requestItemStateChanged(this, var1);
         }
      }

   }

   final CommandListener getCommandListener() {
      return this.commandListener;
   }

   final boolean hasPositiveCommand() {
      return this.displayableCommands.hasPositiveCommand();
   }

   final boolean commandListAction(Command var1) {
      Object var2 = null;
      Object var3 = null;
      Item var4 = null;
      boolean var5 = false;
      if (this.displayableCommands.containsCommand(var1)) {
         var2 = this;
         var3 = this.commandListener;
      } else if ((var4 = this.getCurrentItem()) != null && var4.itemCommands != null && var4.itemCommands.containsCommand(var1)) {
         var2 = var4;
         var3 = var4.commandListener;
      } else {
         var5 = this.launchExtraCommand(var1);
      }

      if (var3 != null) {
         this.myDisplay.requestCommandAction(var1, var2, var3);
      }

      return var5;
   }

   final void updateSoftkeys(boolean var1) {
      synchronized(Display.LCDUILock) {
         if (!this.usesSoftLabel()) {
            Command var3 = null;
            CommandVector var4 = null;
            Item var5 = this.getCurrentItem();
            if (var5 != null) {
               var4 = var5.itemCommands;
               var3 = var5.defaultCommand;
            }

            OptionsMenu var6 = null;
            Command var7 = null;
            int var8 = -1;
            if (var1 && this.myDisplay != null && ((Displayable)this.myDisplay.getOptionsMenu()).parentDisplayable == this) {
               var6 = this.myDisplay.getOptionsMenu();
               var8 = var6.getHighlightedOptionIndex();
               if (var8 >= 0) {
                  var7 = this.optionMenuCommands.getCommand(var8);
               }
            }

            this.optionMenuCommands.reconstruct(this.displayableCommands, this.getExtraCommands(), var4, var3, this.midletCommandsSupported());
            if (!this.fullScreenMode) {
               softkeyManager.selectSoftkeys(this, this.optionMenuCommands, this.softkeyCommands);
            }

            this.sl.assignAll(this.softkeyCommands);
            if (this.displayed) {
               this.sl.updateAll();
            }

            int var9;
            if (var7 != null && (var9 = this.optionMenuCommands.indexOfCommand(var7)) != -1) {
               var8 = var9;
            }

            if (var6 != null) {
               var6.update(var8);
            }

         }
      }
   }

   final void cleanupDisplayableStack(Display var1) {
      this.removedFromDisplayNotify(var1);
      if (this.parentDisplayable != null) {
         this.parentDisplayable.cleanupDisplayableStack(var1);
         this.parentDisplayable = null;
      }

   }

   final Displayable getParentDisplayable() {
      return this.parentDisplayable;
   }

   final boolean isDisplayableInStack(Displayable var1) {
      if (var1 == this) {
         return true;
      } else {
         return this.parentDisplayable == null ? false : this.parentDisplayable.isDisplayableInStack(var1);
      }
   }

   final Displayable getBottomOfStackDisplayable() {
      return this.parentDisplayable == null ? this : this.parentDisplayable.getBottomOfStackDisplayable();
   }

   final boolean isDisplayableStackCleanupRequired(Displayable var1) {
      return var1 != this && this.parentDisplayable != var1 && var1 != null;
   }

   final boolean setDisplayableToTopOfStack(Displayable var1, Displayable var2) {
      if (var2 == this) {
         if (var1 != null && var1 != this.parentDisplayable) {
         }

         return false;
      } else if (var1 == null) {
         return this.setDisplayableToTopOfStackNoParentInternal(var2);
      } else if (var2.isSystemScreen) {
         if (var2.parentDisplayable == null) {
            if (var1 == this) {
               var2.parentDisplayable = this;
               return true;
            } else {
               return this.setDisplayableToTopOfStackWithParentInternal(var1, var2);
            }
         } else {
            return var2.parentDisplayable == var1 ? this.setDisplayableToTopOfStackNoParentInternal(var2) : false;
         }
      } else {
         return false;
      }
   }

   private final boolean setDisplayableToTopOfStackNoParentInternal(Displayable var1) {
      if (this.parentDisplayable == null) {
         if (var1.isSystemScreen) {
            if (var1.parentDisplayable == null) {
            }

            return false;
         } else {
            return true;
         }
      } else if (this.parentDisplayable == var1) {
         this.parentDisplayable = null;
         return true;
      } else {
         return this.parentDisplayable.setDisplayableToTopOfStackNoParentInternal(var1);
      }
   }

   private final boolean setDisplayableToTopOfStackWithParentInternal(Displayable var1, Displayable var2) {
      if (this.parentDisplayable == null) {
         return false;
      } else if (this.parentDisplayable == var1) {
         var2.parentDisplayable = var1;
         this.parentDisplayable = null;
         return true;
      } else {
         return this.parentDisplayable.setDisplayableToTopOfStackWithParentInternal(var1, var2);
      }
   }

   Zone getScrollbarZone() {
      return null;
   }

   Zone getMainZone() {
      return this.ticker != null ? screenTickMainZone : screenNormMainZone;
   }

   Zone getTickerZone() {
      return this.ticker != null ? screenTickTickerZone : null;
   }

   int getTickerHeight() {
      Zone var1 = null;
      return (var1 = this.getTickerZone()) != null ? var1.height : 0;
   }

   void setupViewport() {
      Zone var1 = this.getMainZone();
      this.viewport[0] = var1.x;
      this.viewport[1] = var1.y;
      this.viewport[2] = var1.width;
      this.viewport[3] = var1.height;
   }

   final boolean isSystemScreen() {
      return this.isSystemScreen;
   }

   final void setSystemScreen(boolean var1) {
      this.isSystemScreen = var1;
   }

   final boolean isNativeDelegate() {
      return this.nativeDelegate;
   }

   final void setNativeDelegate(boolean var1) {
      this.nativeDelegate = var1;
   }

   final boolean isPopup() {
      boolean var1 = false;
      if (!this.isNativeDelegate()) {
      }

      var1 = this.popup;
      return var1;
   }

   final void setPopup(boolean var1) {
      this.popup = var1;
   }

   boolean midletCommandsSupported() {
      return true;
   }

   void clearScreen(Graphics var1) {
      if (!this.isNativeDelegate()) {
         if (this.isPopup()) {
            var1.clearScreen(false, true);
         } else {
            if (!(this instanceof GameCanvas)) {
               label37: {
                  if (this instanceof Canvas) {
                     if (this.fullScreenMode) {
                        break label37;
                     }

                     UIStyle var10000 = uistyle;
                     if (!UIStyle.isCanvasHasBgImage()) {
                        break label37;
                     }
                  }

                  var1.clearScreen(true, false);
                  return;
               }
            }

            var1.clearScreen(false, false);
         }

      }
   }

   static {
      screenNormMainZone = uistyle.getZone(2);
      screenNormScrollbarZone = uistyle.getZone(3);
      screenTickMainZone = uistyle.getZone(4);
      screenTickScrollbarZone = uistyle.getZone(5);
      screenTickTickerZone = uistyle.getZone(6);
   }
}
