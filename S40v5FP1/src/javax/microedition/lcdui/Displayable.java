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

   Displayable(String title) {
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
         this.setTitleImpl(title);
      }
   }

   public void addCommand(Command cmd) {
      if (cmd == null) {
         throw new NullPointerException();
      } else {
         synchronized(Display.LCDUILock) {
            this.addCommandImpl(cmd);
         }
      }
   }

   public void removeCommand(Command cmd) {
      if (cmd != null) {
         synchronized(Display.LCDUILock) {
            this.removeCommandImpl(cmd);
         }
      }
   }

   public void setCommandListener(CommandListener l) {
      synchronized(Display.LCDUILock) {
         this.commandListener = l;
      }
   }

   public Ticker getTicker() {
      return this.ticker;
   }

   public void setTicker(Ticker ticker) {
      synchronized(Display.LCDUILock) {
         this.setTickerImpl(ticker);
      }
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String s) {
      synchronized(Display.LCDUILock) {
         this.setTitleImpl(s);
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

   protected void sizeChanged(int w, int h) {
   }

   boolean isShownImpl() {
      return this.displayed;
   }

   void setVisibilityListener(VisibilityListener visibilityListener) {
      this.visibilityListener = visibilityListener;
   }

   boolean addCommandImpl(Command cmd) {
      boolean wasAdded = this.displayableCommands.addCommand(cmd);
      if (wasAdded) {
         this.updateSoftkeys(true);
      }

      return wasAdded;
   }

   boolean removeCommandImpl(Command cmd) {
      boolean wasRemoved = this.displayableCommands.removeCommand(cmd);
      if (wasRemoved) {
         this.updateSoftkeys(true);
      }

      return wasRemoved;
   }

   void setTickerImpl(Ticker newTicker) {
      Ticker oldTicker = this.ticker;
      if (oldTicker != newTicker) {
         this.ticker = newTicker;
         if (!this.fullScreenMode) {
            if (this.displayed) {
               if (newTicker != null) {
                  newTicker.showOn(this);
               }

               if (oldTicker != null) {
                  oldTicker.hideFrom(this);
               }
            }

            if (oldTicker == null || newTicker == null) {
               this.sizeChangeOccurred = true;
               if (this.myDisplay != null && newTicker != null) {
                  this.myDisplay.requestClearScreen(this);
               }

               this.setupViewport();
               this.invalidate();
            }
         }
      }

   }

   void setTitleImpl(String s) {
      this.title = s;
      if (this.displayed) {
         this.myDisplay.updateStatusZone();
      }

      OptionsMenu optionsMenu;
      if (this.myDisplay != null && ((Displayable)(optionsMenu = this.myDisplay.getOptionsMenu())).parentDisplayable == this) {
         ((Displayable)optionsMenu).setTitleImpl(this.title);
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

   boolean launchExtraCommand(Command c) {
      return false;
   }

   Item getCurrentItem() {
      return null;
   }

   void callKeyPressed(int keyCode, int keyDataIdx) {
      if (!this.usesSoftLabel()) {
         Command cmd = null;
         boolean isUserCommand = false;
         boolean isItemCommand = false;
         ItemCommandListener itemCommandListener = null;
         Item currentItem = null;
         synchronized(Display.LCDUILock) {
            if (DeviceInfo.isSoftkey(keyCode)) {
               if (this.fullScreenMode) {
                  cmd = OptionsMenu.optionsCommand;
               } else {
                  switch(keyCode) {
                  case -7:
                     cmd = this.softkeyCommands.getCommand(2);
                     break;
                  case -6:
                     cmd = this.softkeyCommands.getCommand(0);
                     break;
                  case -5:
                     cmd = this.softkeyCommands.getCommand(1);
                  }
               }
            }

            if (cmd == null) {
               return;
            }

            if (cmd == OptionsMenu.optionsCommand) {
               OptionsMenu optionsMenu = this.myDisplay.getOptionsMenu();
               this.myDisplay.setCurrentInternal(this, (Displayable)optionsMenu);
            } else if (this.displayableCommands.containsCommand(cmd)) {
               isUserCommand = true;
            } else if ((currentItem = this.getCurrentItem()) != null && currentItem.itemCommands.containsCommand(cmd)) {
               itemCommandListener = currentItem.getItemCommandListener();
               isItemCommand = true;
            } else {
               this.launchExtraCommand(cmd);
            }
         }

         if (isUserCommand) {
            CommandListener localCommandListener = this.commandListener;
            if (localCommandListener != null) {
               synchronized(Display.calloutLock) {
                  localCommandListener.commandAction(cmd, this);
               }
            }
         } else if (isItemCommand && itemCommandListener != null) {
            synchronized(Display.calloutLock) {
               itemCommandListener.commandAction(cmd, currentItem);
            }
         }

      }
   }

   void callKeyRepeated(int keyCode, int keyDataIdx) {
   }

   void callKeyReleased(int keyCode, int keyDataIdx) {
   }

   void callShowNotify(Display d) {
      VisibilityListener localVisibilityListener = this.visibilityListener;
      synchronized(Display.LCDUILock) {
         if (!this.displayed) {
            this.myDisplay = d;
            this.displayed = true;
            if (this.ticker != null && !this.fullScreenMode) {
               this.ticker.showOn(this);
            }
         }

         this.afterShowNotify = true;
      }

      this.callInvalidate();
      if (localVisibilityListener != null) {
         localVisibilityListener.showNotify(d, this);
      }

   }

   void callHideNotifyInProgress(Display d) {
   }

   void callHideNotify(Display d) {
      VisibilityListener localVisibilityListener = this.visibilityListener;
      synchronized(Display.LCDUILock) {
         if (this.displayed) {
            if (this.ticker != null && !this.fullScreenMode) {
               this.ticker.hideFrom(this);
            }

            this.displayed = false;
         }
      }

      if (!this.callsVisListOnHideNotify() && localVisibilityListener != null) {
         synchronized(Display.calloutLock) {
            localVisibilityListener.hideNotify(d, this);
         }
      }

   }

   void removedFromDisplayNotify(Display d) {
   }

   void callPaint(Graphics g) {
      if (!this.fullScreenMode) {
         synchronized(Display.LCDUILock) {
            if (this.ticker != null) {
               this.ticker.paint(g);
            }

            if (this.afterShowNotify) {
               this.sl.updateAll();
               this.afterShowNotify = false;
            }

         }
      }
   }

   void callInvalidate() {
      boolean mustNotify = false;
      synchronized(Display.LCDUILock) {
         this.setupViewport();
         if (this.sizeChangeOccurred) {
            this.sizeChangeOccurred = false;
            mustNotify = true;
         }
      }

      if (mustNotify) {
         synchronized(Display.calloutLock) {
            this.sizeChanged(this.viewport[2], this.viewport[3]);
         }
      }

   }

   void callItemStateChanged(Item i) {
   }

   void callDelegateEvent(int eventType, int eventValue) {
   }

   void repaintArea(int x, int y, int width, int height) {
      if (this.displayed) {
         this.myDisplay.requestRepaint(this, x, y, width, height);
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

   void changedItemState(Item i) {
      if (!i.stateChanging) {
         i.stateChanging = true;
         Display d = this.myDisplay == null ? Display.getActiveDisplay() : this.myDisplay;
         if (d != null) {
            d.requestItemStateChanged(this, i);
         }
      }

   }

   final CommandListener getCommandListener() {
      return this.commandListener;
   }

   final boolean hasPositiveCommand() {
      return this.displayableCommands.hasPositiveCommand();
   }

   final boolean commandListAction(Command selectedCmd) {
      Object source = null;
      Object listener = null;
      Item currentItem = null;
      boolean displayableChangeInstigated = false;
      if (this.displayableCommands.containsCommand(selectedCmd)) {
         source = this;
         listener = this.commandListener;
      } else if ((currentItem = this.getCurrentItem()) != null && currentItem.itemCommands != null && currentItem.itemCommands.containsCommand(selectedCmd)) {
         source = currentItem;
         listener = currentItem.commandListener;
      } else {
         displayableChangeInstigated = this.launchExtraCommand(selectedCmd);
      }

      if (listener != null) {
         this.myDisplay.requestCommandAction(selectedCmd, source, listener);
      }

      return displayableChangeInstigated;
   }

   void closeOptionsMenu() {
      OptionsMenu optionsMenu = null;
      if (this.myDisplay != null && ((Displayable)this.myDisplay.getOptionsMenu()).parentDisplayable == this) {
         optionsMenu = this.myDisplay.getOptionsMenu();
      }

      if (optionsMenu != null) {
         optionsMenu.setKeepRootOptionsMenu(false);
         optionsMenu.nativeDismissOptionsList();
      }

      this.myDisplay.setCurrentInternal((Displayable)null, this);
   }

   final void updateSoftkeys(boolean createCL) {
      synchronized(Display.LCDUILock) {
         if (!this.usesSoftLabel()) {
            Command defaultCommand = null;
            CommandVector itemCommands = null;
            Item currentItem = this.getCurrentItem();
            if (currentItem != null) {
               itemCommands = currentItem.itemCommands;
               defaultCommand = currentItem.defaultCommand;
            }

            OptionsMenu optionsMenu = null;
            Command currentCmdListCmd = null;
            int optionsMenuIndex = -1;
            if (createCL && this.myDisplay != null && ((Displayable)this.myDisplay.getOptionsMenu()).parentDisplayable == this) {
               optionsMenu = this.myDisplay.getOptionsMenu();
               optionsMenuIndex = optionsMenu.getHighlightedOptionIndex();
               if (optionsMenuIndex >= 0) {
                  currentCmdListCmd = this.optionMenuCommands.getCommand(optionsMenuIndex);
               }
            }

            this.optionMenuCommands.reconstruct(this.displayableCommands, this.getExtraCommands(), itemCommands, defaultCommand, this.midletCommandsSupported());
            if (!this.fullScreenMode) {
               softkeyManager.selectSoftkeys(this, this.optionMenuCommands, this.softkeyCommands);
            }

            this.sl.assignAll(this.softkeyCommands);
            if (this.displayed) {
               this.sl.updateAll();
            }

            int tmpNewCmdIdx;
            if (currentCmdListCmd != null && (tmpNewCmdIdx = this.optionMenuCommands.indexOfCommand(currentCmdListCmd)) != -1) {
               optionsMenuIndex = tmpNewCmdIdx;
            }

            if (optionsMenu != null) {
               optionsMenu.update(optionsMenuIndex);
            }

         }
      }
   }

   final void cleanupDisplayableStack(Display d) {
      this.removedFromDisplayNotify(d);
      if (this.parentDisplayable != null) {
         this.parentDisplayable.cleanupDisplayableStack(d);
         this.parentDisplayable = null;
      }

   }

   final Displayable getParentDisplayable() {
      return this.parentDisplayable;
   }

   final boolean isDisplayableInStack(Displayable searchDisplayable) {
      if (searchDisplayable == this) {
         return true;
      } else {
         return this.parentDisplayable == null ? false : this.parentDisplayable.isDisplayableInStack(searchDisplayable);
      }
   }

   final Displayable getBottomOfStackDisplayable() {
      return this.parentDisplayable == null ? this : this.parentDisplayable.getBottomOfStackDisplayable();
   }

   final boolean isDisplayableStackCleanupRequired(Displayable oldDisplayable) {
      return oldDisplayable != this && this.parentDisplayable != oldDisplayable && oldDisplayable != null;
   }

   final boolean setDisplayableToTopOfStack(Displayable newTosdParentDisplayable, Displayable newTopOfStackDisplayable) {
      if (newTopOfStackDisplayable == this) {
         if (newTosdParentDisplayable != null && newTosdParentDisplayable != this.parentDisplayable) {
         }

         return false;
      } else if (newTosdParentDisplayable == null) {
         return this.setDisplayableToTopOfStackNoParentInternal(newTopOfStackDisplayable);
      } else if (newTopOfStackDisplayable.isSystemScreen) {
         if (newTopOfStackDisplayable.parentDisplayable == null) {
            if (newTosdParentDisplayable == this) {
               newTopOfStackDisplayable.parentDisplayable = this;
               return true;
            } else {
               return this.setDisplayableToTopOfStackWithParentInternal(newTosdParentDisplayable, newTopOfStackDisplayable);
            }
         } else {
            return newTopOfStackDisplayable.parentDisplayable == newTosdParentDisplayable ? this.setDisplayableToTopOfStackNoParentInternal(newTopOfStackDisplayable) : false;
         }
      } else {
         return false;
      }
   }

   private final boolean setDisplayableToTopOfStackNoParentInternal(Displayable newTopOfStackDisplayable) {
      if (this.parentDisplayable == null) {
         if (newTopOfStackDisplayable.isSystemScreen) {
            if (newTopOfStackDisplayable.parentDisplayable == null) {
            }

            return false;
         } else {
            return true;
         }
      } else if (this.parentDisplayable == newTopOfStackDisplayable) {
         this.parentDisplayable = null;
         return true;
      } else {
         return this.parentDisplayable.setDisplayableToTopOfStackNoParentInternal(newTopOfStackDisplayable);
      }
   }

   private final boolean setDisplayableToTopOfStackWithParentInternal(Displayable newTosdParentDisplayable, Displayable newTopOfStackDisplayable) {
      if (this.parentDisplayable == null) {
         return false;
      } else if (this.parentDisplayable == newTosdParentDisplayable) {
         newTopOfStackDisplayable.parentDisplayable = newTosdParentDisplayable;
         this.parentDisplayable = null;
         return true;
      } else {
         return this.parentDisplayable.setDisplayableToTopOfStackWithParentInternal(newTosdParentDisplayable, newTopOfStackDisplayable);
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
      Zone zone = null;
      return (zone = this.getTickerZone()) != null ? zone.height : 0;
   }

   void setupViewport() {
      Zone mz = this.getMainZone();
      this.viewport[0] = mz.x;
      this.viewport[1] = mz.y;
      this.viewport[2] = mz.width;
      this.viewport[3] = mz.height;
   }

   final boolean isSystemScreen() {
      return this.isSystemScreen;
   }

   final void setSystemScreen(boolean value) {
      this.isSystemScreen = value;
   }

   final boolean isNativeDelegate() {
      return this.nativeDelegate;
   }

   final void setNativeDelegate(boolean value) {
      this.nativeDelegate = value;
   }

   final boolean isPopup() {
      boolean returnValue = false;
      if (!this.isNativeDelegate()) {
      }

      returnValue = this.popup;
      return returnValue;
   }

   final void setPopup(boolean value) {
      this.popup = value;
   }

   boolean midletCommandsSupported() {
      return true;
   }

   void clearScreen(Graphics g) {
      if (!this.isNativeDelegate()) {
         if (this.isPopup()) {
            g.clearClipArea(false, true, false, (Zone)null);
         } else {
            if (!(this instanceof GameCanvas)) {
               label45: {
                  if (this instanceof Canvas) {
                     if (this.fullScreenMode) {
                        break label45;
                     }

                     UIStyle var10000 = uistyle;
                     if (!UIStyle.isCanvasHasBgImage()) {
                        break label45;
                     }
                  }

                  g.clearClipArea(true, false, false, (Zone)null);
                  return;
               }
            }

            if (!this.fullScreenMode && this.ticker != null) {
               g.clearClipArea(false, false, true, Canvas.canvasTickMainZone);
            } else {
               g.clearClipArea(false, false, false, (Zone)null);
            }
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
