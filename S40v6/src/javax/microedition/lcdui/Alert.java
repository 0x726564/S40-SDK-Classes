package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.style.Zone;

public class Alert extends Screen {
   public static final int FOREVER = -2;
   public static final Command DISMISS_COMMAND = new Command(10, 17);
   static final Command MORE_COMMAND = new Command(9, 7);
   private static int ALERT_MODALITY_NOT_DEFINED = -1;
   private static int ALERT_MODALITY_TIMED = 1;
   private static int ALERT_MODALITY_MODAL = 2;
   private static final Command[] NO_EXTRA_COMMANDS = new Command[0];
   private static final Command[] DISMISS_ONLY;
   private static final Command[] MORE_ONLY;
   private static final Command[] MORE_AND_DISMISS;
   private static final int ALERT_DEFAULT_TIMEOUT = 5000;
   private static final int ALERT_DISMISS_CMD_ID = -1;
   private static final int ALERT_OPTIONS_CMD_FLAG = 1;
   private static final int ALERT_MORE_CMD_FLAG = 2;
   Displayable returnScreen;
   Displayable backgroundScreen;
   private int timeOut;
   private String alertText;
   private Image midletAlertImage;
   private Gauge indicator;
   private AlertType alertType;
   private int modality;
   private int pending_modality;
   private boolean isMultiPaged;
   private boolean isVisible;

   public Alert(String title) {
      this(title, (String)null, (Image)null, (AlertType)null);
   }

   public Alert(String title, String text, Image image, AlertType alertType) {
      super(title);
      this.returnScreen = null;
      this.backgroundScreen = null;
      this.timeOut = 5000;
      this.alertText = null;
      this.midletAlertImage = null;
      this.indicator = null;
      this.alertType = null;
      this.modality = ALERT_MODALITY_NOT_DEFINED;
      this.pending_modality = ALERT_MODALITY_NOT_DEFINED;
      this.isMultiPaged = false;
      this.isVisible = true;
      synchronized(Display.LCDUILock) {
         this.alertType = alertType;
         this.alertText = text;
         this.midletAlertImage = image;
         this.setPopup(true);
         this.setNativeDelegate(true);
      }

      this.setCommandListener((CommandListener)null);
      this.nativeWrapper = true;
   }

   public int getDefaultTimeout() {
      return 5000;
   }

   public Image getImage() {
      synchronized(Display.LCDUILock) {
         return this.midletAlertImage;
      }
   }

   public Gauge getIndicator() {
      synchronized(Display.LCDUILock) {
         return this.indicator;
      }
   }

   public String getString() {
      synchronized(Display.LCDUILock) {
         return this.alertText;
      }
   }

   public int getTimeout() {
      synchronized(Display.LCDUILock) {
         return this.getTimeoutImpl();
      }
   }

   public AlertType getType() {
      synchronized(Display.LCDUILock) {
         return this.alertType;
      }
   }

   public void setCommandListener(CommandListener l) {
      if (l == null) {
         l = new Alert.AlertCommandListener();
      }

      super.setCommandListener((CommandListener)l);
   }

   public void setImage(Image img) {
      synchronized(Display.LCDUILock) {
         this.midletAlertImage = img;
         if (this.displayed) {
            this.updatePendingModality(this.nativeSetImage(this.getMidletAlertPixmap()));
         }

      }
   }

   public void setIndicator(Gauge indicator) {
      synchronized(Display.LCDUILock) {
         this.updatePendingModality(this.setIndicatorImpl(indicator));
      }
   }

   public void setString(String str) {
      synchronized(Display.LCDUILock) {
         this.alertText = str;
         this.isMultiPaged = false;
         if (this.displayed) {
            this.updatePendingModality(this.nativeSetString(this.alertText));
         }

      }
   }

   public void setTimeout(int time) {
      if (time <= 0 && time != -2) {
         throw new IllegalArgumentException();
      } else {
         synchronized(Display.LCDUILock) {
            this.setTimeoutImpl(time);
         }
      }
   }

   public void setType(AlertType type) {
      synchronized(Display.LCDUILock) {
         this.setTypeImpl(type);
      }
   }

   void setReturnScreen(Displayable returnScreen) {
      this.returnScreen = returnScreen;
   }

   public boolean isShown() {
      synchronized(Display.LCDUILock) {
         return this.isVisible && this.isShownImpl();
      }
   }

   boolean addCommandImpl(Command cmd) {
      boolean wasAdded = this.displayableCommands.addCommand(cmd);
      if (wasAdded) {
         this.updateModality(true);
         if (this.displayed && this.nativeIsOptionsMenuShown()) {
            this.handleOptionsSelected();
         }
      }

      return wasAdded;
   }

   boolean removeCommandImpl(Command cmd) {
      boolean wasRemoved = this.displayableCommands.removeCommand(cmd);
      if (wasRemoved) {
         this.updateModality(true);
         if (this.displayed && this.nativeIsOptionsMenuShown()) {
            this.handleOptionsSelected();
         }
      }

      return wasRemoved;
   }

   boolean setIndicatorImpl(Gauge indicator) {
      boolean ret = false;
      if (indicator != null) {
         if (!this.isConformantIndicator(indicator)) {
            throw new IllegalArgumentException();
         }

         indicator.setOwner(this);
      }

      if (this.indicator != null) {
         this.indicator.setOwner((Screen)null);
      }

      this.indicator = indicator;
      if (this.displayed) {
         int val = -2;
         int maxVal = -2;
         if (indicator != null) {
            val = indicator.getValue();
            maxVal = indicator.getMaxValue();
         }

         ret = this.nativeUpdateIndicator(val, maxVal);
      }

      return ret;
   }

   boolean isConformantIndicator(Gauge ind) {
      return !ind.isInteractive() && ind.owner == null && ind.itemCommands.length() == 0 && ind.getItemCommandListener() == null && ind.getLabel() == null && ind.getLayout() == 0 && ind.lockedWidth == -1 && ind.lockedHeight == -1;
   }

   void setTypeImpl(AlertType type) {
      if (this.alertType != type) {
         this.alertType = type;
         if (this.displayed) {
            this.nativeSetType(this.alertType == null ? -1 : this.alertType.type);
         }

      }
   }

   void setTimeoutImpl(int time) {
      this.timeOut = time;
      this.updateModality(true);
   }

   int getTimeoutImpl() {
      return this.pending_modality == ALERT_MODALITY_MODAL ? -2 : this.timeOut;
   }

   void setBackgroundOfPopup(Displayable backgroundDisplayable) {
      if (backgroundDisplayable instanceof OptionsMenu) {
         this.backgroundScreen = backgroundDisplayable.getParentDisplayable();
      } else {
         this.backgroundScreen = backgroundDisplayable;
      }

   }

   Displayable getBackgroundOfPopup() {
      return this.backgroundScreen;
   }

   void updateSoftKeyLabel(String labelText, int index) {
      int cmdFlag = 0;
      String label = labelText;
      Command cmd = this.softkeyCommands.getCommand(index);
      if (cmd == null) {
         label = null;
      } else if (cmd == OptionsMenu.optionsCommand) {
         cmdFlag = 1;
      } else if (cmd == MORE_COMMAND) {
         cmdFlag = 2;
      }

      this.nativeUpdateSoftKey(label, index, cmdFlag);
   }

   Command[] getExtraCommands() {
      boolean displayMoreCommand = this.isMultiPaged && !this.hasPositiveCommand();
      if (this.modality == ALERT_MODALITY_MODAL) {
         if (this.displayableCommands.length() == 0 && displayMoreCommand) {
            return MORE_AND_DISMISS;
         } else if (this.displayableCommands.length() == 0) {
            return DISMISS_ONLY;
         } else {
            return displayMoreCommand ? MORE_ONLY : NO_EXTRA_COMMANDS;
         }
      } else {
         return NO_EXTRA_COMMANDS;
      }
   }

   void callKeyPressed(int keyCode, int keyDataIdx) {
   }

   void callShowNotify(Display d) {
      synchronized(Display.LCDUILock) {
         this.displayed = true;
         this.myDisplay = d;
         if (this.backgroundScreen != null) {
            this.backgroundScreen.stayAlive(true);
            this.backgroundScreen.undimTickerZone(this.getTicker() != null);
         }

         this.nativeCreateAlert(this.getInternalTitle(), this.alertType == null ? -1 : this.alertType.type, this.alertText, this.getMidletAlertPixmap(), this.getTicker() != null);
         if (this.indicator != null) {
            this.nativeUpdateIndicator(this.indicator.getValue(), this.indicator.getMaxValue());
         }

         this.nativeSetActive(false);
         this.updatePendingModality(this.nativeLaunchAlert());
         this.updateModality(true);
         this.nativeSetActive(true);
      }

      super.callShowNotify(d);
   }

   void invalidate() {
      if (this.displayed) {
         boolean hasTicker = this.getTicker() != null;
         this.nativeUndimTicker(hasTicker);
         if (this.backgroundScreen != null) {
            this.backgroundScreen.undimTickerZone(hasTicker);
         }
      }

   }

   void callHideNotify(Display d) {
      synchronized(Display.LCDUILock) {
         if (this.backgroundScreen != null) {
            this.backgroundScreen.stayAlive(false);
            this.backgroundScreen = null;
         }

         if (this.displayed) {
            this.nativeDismissAlert();
         }
      }

      super.callHideNotify(d);
   }

   void callHideNotifyInProgress(Display d) {
      if (d.getCurrent() instanceof Alert) {
         synchronized(Display.LCDUILock) {
            this.nativeDismissAlert();
            if (this.displayed) {
               this.displayed = false;
            }
         }
      }

   }

   void removedFromDisplayNotify(Display d) {
      this.returnScreen = null;
   }

   void callPaint(Graphics g) {
   }

   void callInvalidate() {
   }

   Zone getMainZone() {
      return uistyle.getZone(26);
   }

   void setTitleImpl(String s) {
      this.title = s;
      if (this.displayed) {
         this.nativeSetTitle(this.getInternalTitle());
      }

   }

   void restartTimerIfNecessary() {
      if (this.displayed && this.modality == ALERT_MODALITY_TIMED) {
         this.nativeSetTimeOut(this.timeOut);
      }

   }

   void callDelegateEvent(int eventType, int eventValue) {
      switch(eventType) {
      case 1:
         this.handleCommand(eventValue);
      case 2:
      case 3:
      case 5:
      case 6:
      case 9:
      default:
         break;
      case 4:
         synchronized(Display.LCDUILock) {
            this.handleOptionsSelected();
            break;
         }
      case 7:
         synchronized(Display.LCDUILock) {
            this.handleMultiPage(eventValue == 1);
            break;
         }
      case 8:
         this.isVisible = eventValue == 1;
         break;
      case 10:
         synchronized(Display.LCDUILock) {
            this.myDisplay.setCurrentInternal((Displayable)null, this.returnScreen);
         }
      }

   }

   boolean isPowerSavingActive() {
      return false;
   }

   private void handleMultiPage(boolean newMultipagedStatus) {
      this.isMultiPaged = newMultipagedStatus;
      this.updateModality(true);
   }

   private void handleOptionsSelected() {
      String[] optionsLabels = new String[this.optionMenuCommands.length()];

      for(int i = 0; i < optionsLabels.length; ++i) {
         optionsLabels[i] = this.optionMenuCommands.getCommand(i).getMenuLabel();
      }

      this.nativeSetMenuCmds(optionsLabels);
   }

   private void handleCommand(int cmdIx) {
      Command cmd = null;
      CommandListener listener;
      synchronized(Display.LCDUILock) {
         if (cmdIx == -1) {
            cmd = DISMISS_COMMAND;
         } else if (cmdIx < 3) {
            cmd = this.softkeyCommands.getCommand(cmdIx);
         } else {
            cmd = this.optionMenuCommands.getCommand(cmdIx - 3);
         }

         listener = this.commandListener;
         if (listener instanceof Alert.AlertCommandListener) {
            listener.commandAction(cmd, this);
            return;
         }

         if (cmd != DISMISS_COMMAND && this.isModal()) {
            String label = this.nativeGetLabelAndActivate();
            if (cmd == null || label == null || !label.equals(cmdIx < 3 ? cmd.getMenuLabel() : cmd.getLabelImpl().trim())) {
               return;
            }
         } else if (this.displayableCommands.isNotEmpty()) {
            cmd = this.displayableCommands.getCommand(0);
         }
      }

      synchronized(Display.calloutLock) {
         listener.commandAction(cmd, this);
      }
   }

   private Pixmap getMidletAlertPixmap() {
      Pixmap pixmap = null;
      if (this.midletAlertImage != null) {
         if (this.midletAlertImage.isMutable()) {
            pixmap = new Pixmap(this.midletAlertImage.getPixmap());
            pixmap.setMutable(false);
         } else {
            pixmap = this.midletAlertImage.getPixmap();
         }
      }

      return pixmap;
   }

   boolean isModal() {
      return ALERT_MODALITY_MODAL == this.modality;
   }

   private void updatePendingModality(boolean isMultipaged) {
      this.pending_modality = !isMultipaged && this.displayableCommands.length() <= 1 ? ALERT_MODALITY_TIMED : ALERT_MODALITY_MODAL;
   }

   private void updateModality(boolean updateSoftkeys) {
      this.modality = ALERT_MODALITY_TIMED;
      if (this.isMultiPaged || this.timeOut == -2 || this.displayableCommands.length() > 1) {
         this.modality = ALERT_MODALITY_MODAL;
      }

      if (updateSoftkeys) {
         this.updateSoftkeys(false, false);
      }

      this.pending_modality = this.modality;
      if (this.displayed) {
         this.nativeSetTimeOut(this.timeOut);
      }

   }

   private native void nativeCreateAlert(String var1, int var2, String var3, Pixmap var4, boolean var5);

   private native boolean nativeLaunchAlert();

   private native void nativeDismissAlert();

   private native void nativeUndimTicker(boolean var1);

   private native void nativeSetMenuCmds(String[] var1);

   private native void nativeUpdateSoftKey(String var1, int var2, int var3);

   private native void nativeSetTitle(String var1);

   private native boolean nativeSetString(String var1);

   private native boolean nativeSetImage(Pixmap var1);

   private native void nativeSetTimeOut(int var1);

   private native void nativeSetType(int var1);

   native boolean nativeUpdateIndicator(int var1, int var2);

   native boolean nativeIsOptionsMenuShown();

   native String nativeGetLabelAndActivate();

   native void nativeSetActive(boolean var1);

   static {
      DISMISS_ONLY = new Command[]{DISMISS_COMMAND};
      MORE_ONLY = new Command[]{MORE_COMMAND};
      MORE_AND_DISMISS = new Command[]{MORE_COMMAND, DISMISS_COMMAND};
   }

   private class AlertCommandListener implements CommandListener {
      private AlertCommandListener() {
      }

      public void commandAction(Command c, Displayable s) {
         synchronized(Display.LCDUILock) {
            Alert.this.myDisplay.setCurrentInternal((Displayable)null, Alert.this.returnScreen);
         }
      }

      // $FF: synthetic method
      AlertCommandListener(Object x1) {
         this();
      }
   }
}
