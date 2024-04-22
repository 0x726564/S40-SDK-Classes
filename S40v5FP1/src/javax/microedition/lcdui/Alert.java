package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceControl;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.AnimationListener;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class Alert extends Screen {
   public static final int FOREVER = -2;
   public static final Command DISMISS_COMMAND = new Command(10, 17);
   static final Command MORE_COMMAND = new Command(9, 7);
   private static int ALERT_MODALITY_NOT_DEFINED = -1;
   private static int ALERT_MODALITY_TIMED = 1;
   private static int ALERT_MODALITY_MODAL = 2;
   private static final int ALERT_DEFAULT_TIMEOUT = 5000;
   private static final Command[] NO_EXTRA_COMMANDS = new Command[0];
   private static final Command[] DISMISS_ONLY;
   private static final Command[] MORE_ONLY;
   private static final Command[] MORE_AND_DISMISS;
   Displayable returnScreen;
   private int timeOut;
   private String alertText;
   Pixmap alertPixmap;
   private Image midletAlertImage;
   private Gauge indicator;
   private boolean usingSystemImage;
   private boolean usingSystemText;
   private AlertType alertType;
   private int modality;
   private boolean imageOnly;
   Vector alertTextLines;
   private int nbrOfScreens;
   private int displayedScreen;
   private Timer timerService;
   Alert.TimerClient timerClient;
   private Zone smallTextZone;
   private Zone largeTextZone;
   private Zone imageZone;
   private Zone borderZone;
   private Zone indicatorZone;
   private boolean isAlertDismissActionCalled;
   private String lastPageText;

   public Alert(String title) {
      this(title, (String)null, (Image)null, (AlertType)null);
   }

   public Alert(String title, String alertText, Image alertImage, AlertType alertType) {
      super(title);
      this.returnScreen = null;
      this.timeOut = 5000;
      this.alertText = null;
      this.alertPixmap = null;
      this.midletAlertImage = null;
      this.indicator = null;
      this.usingSystemImage = false;
      this.usingSystemText = false;
      this.alertType = null;
      this.modality = ALERT_MODALITY_NOT_DEFINED;
      this.imageOnly = false;
      this.alertTextLines = new Vector();
      this.nbrOfScreens = 1;
      this.displayedScreen = 0;
      synchronized(Display.LCDUILock) {
         this.setZoneReferences();
         this.alertType = alertType;
         this.setImageImpl(alertImage);
         this.setStringImpl(alertText);
         this.setPopup(true);
      }

      this.setCommandListener((CommandListener)null);
   }

   public int getDefaultTimeout() {
      return 5000;
   }

   public Image getImage() {
      synchronized(Display.LCDUILock) {
         return this.usingSystemImage ? null : this.midletAlertImage;
      }
   }

   public Gauge getIndicator() {
      return this.indicator;
   }

   public String getString() {
      synchronized(Display.LCDUILock) {
         return this.usingSystemText ? null : this.alertText;
      }
   }

   public int getTimeout() {
      synchronized(Display.LCDUILock) {
         return this.getTimeoutImpl();
      }
   }

   public AlertType getType() {
      return this.alertType;
   }

   public void setCommandListener(CommandListener l) {
      if (l == null) {
         l = new Alert.AlertCommandListener();
      }

      super.setCommandListener((CommandListener)l);
   }

   public void setImage(Image img) {
      synchronized(Display.LCDUILock) {
         this.setImageImpl(img);
      }
   }

   public void setIndicator(Gauge indicator) {
      synchronized(Display.LCDUILock) {
         this.setIndicatorImpl(indicator);
      }
   }

   public void setString(String str) {
      synchronized(Display.LCDUILock) {
         this.setStringImpl(str);
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

   boolean alertDismissAction(Command cmd) {
      this.ensureTimerStopped();
      this.isAlertDismissActionCalled = true;
      if (cmd == null) {
         cmd = DISMISS_COMMAND;
         if (this.displayableCommands.isNotEmpty()) {
            cmd = this.displayableCommands.getCommand(0);
         }
      }

      if (this.commandListener instanceof Alert.AlertCommandListener) {
         this.myDisplay.setCurrentInternal((Displayable)null, this.returnScreen);
         return true;
      } else {
         this.myDisplay.requestCommandAction(cmd, this, this.commandListener);
         return false;
      }
   }

   void setReturnScreen(Displayable returnScreen) {
      this.returnScreen = returnScreen;
   }

   void restartTimerIfNecessary() {
      if (this.timerService != null) {
         this.ensureTimerStopped();
         this.ensureTimerStarted();
      }

   }

   void startTone(Display display) {
      if (this.alertType != null) {
         this.alertType.stopSound(display);
         this.alertType.playSound(display);
      }

   }

   boolean addCommandImpl(Command cmd) {
      boolean wasAdded = super.addCommandImpl(cmd);
      if (wasAdded) {
         this.updateModality(false);
      }

      return wasAdded;
   }

   boolean removeCommandImpl(Command cmd) {
      boolean wasRemoved = super.removeCommandImpl(cmd);
      if (wasRemoved) {
         this.updateModality(false);
      }

      return wasRemoved;
   }

   void setImageImpl(Image img) {
      Pixmap oldAlertPixmap = this.alertPixmap;
      this.midletAlertImage = img;
      if (img == null) {
         this.usingSystemImage = true;
         if (this.displayed && this.alertType != null) {
            this.alertPixmap = this.alertType.getPixmap();
         } else {
            this.alertPixmap = null;
         }
      } else {
         this.usingSystemImage = false;
         if (img.isMutable()) {
            this.alertPixmap = new Pixmap(img.getPixmap());
            this.alertPixmap.setMutable(false);
         } else {
            this.alertPixmap = img.getPixmap();
         }
      }

      this.updateImageOnly();
      if (oldAlertPixmap != null) {
         oldAlertPixmap.stopAnimationTimer();
      }

      if (this.alertPixmap != null && this.alertPixmap.isAnimatedPixmap()) {
         this.alertPixmap.resetAnimation();
         this.alertPixmap.setAnimationListener(new Alert.AlertAnimationListener());
         if (this.displayed) {
            this.alertPixmap.startAnimationTimer();
         }
      }

      this.invalidate();
   }

   void setIndicatorImpl(Gauge indicator) {
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
      this.invalidate();
   }

   boolean isConformantIndicator(Gauge ind) {
      return !ind.isInteractive() && ind.owner == null && ind.itemCommands.length() == 0 && ind.getItemCommandListener() == null && ind.getLabel() == null && ind.getLayout() == 0 && ind.lockedWidth == -1 && ind.lockedHeight == -1;
   }

   void setStringImpl(String str) {
      if (str == null) {
         this.usingSystemText = true;
         this.alertText = this.getSystemText();
      } else {
         this.alertText = str;
         this.usingSystemText = false;
      }

      this.updateImageOnly();
      this.invalidate();
   }

   void setTypeImpl(AlertType type) {
      if (this.alertType != type) {
         this.alertType = type;
         if (this.usingSystemImage) {
            this.setImageImpl((Image)null);
         }

         if (this.usingSystemText) {
            this.setStringImpl((String)null);
         }

         this.updateImageOnly();
      }
   }

   void setTimeoutImpl(int time) {
      this.timeOut = time;
      this.isAlertDismissActionCalled = false;
      this.ensureTimerStopped();
      this.updateModality(true);
   }

   int getTimeoutImpl() {
      if (!this.layoutValid) {
         this.layout();
      }

      return this.isModal() ? -2 : this.timeOut;
   }

   void setLastPageText(String lastPageText) {
      this.lastPageText = lastPageText;
   }

   void layout() {
      boolean alertHasPixmap = this.alertPixmap != null || this.usingSystemImage && this.alertType != null;
      super.layout();
      this.setZoneReferences();
      this.alertTextLines.removeAllElements();
      Vector temporaryTextLines = new Vector();
      TextBreaker.breakTextInArea(alertHasPixmap ? this.smallTextZone.width : this.largeTextZone.width, this.indicator != null ? this.smallTextZone.height : this.largeTextZone.height, TextBreaker.NBR_OF_AREAS_AS_NEEDED, this.smallTextZone.getFont(), this.alertText, TextBreaker.DEFAULT_TEXT_LEADING, false, false, temporaryTextLines, false, true);
      this.displayedScreen = 0;
      this.nbrOfScreens = temporaryTextLines.size() > 0 && temporaryTextLines.elementAt(0) instanceof Vector ? temporaryTextLines.size() : 1;
      int nbrOfScreens2;
      if (this.nbrOfScreens > 1) {
         for(nbrOfScreens2 = 0; nbrOfScreens2 < this.nbrOfScreens; ++nbrOfScreens2) {
            this.alertTextLines.addElement(temporaryTextLines.elementAt(nbrOfScreens2));
         }
      } else {
         this.alertTextLines.addElement(temporaryTextLines);
      }

      if (this.lastPageText != null) {
         temporaryTextLines = new Vector();
         TextBreaker.breakTextInArea(alertHasPixmap ? this.smallTextZone.width : this.largeTextZone.width, this.indicator != null ? this.smallTextZone.height : this.largeTextZone.height, TextBreaker.NBR_OF_AREAS_AS_NEEDED, this.smallTextZone.getFont(), this.lastPageText, TextBreaker.DEFAULT_TEXT_LEADING, false, false, temporaryTextLines, false, true);
         nbrOfScreens2 = temporaryTextLines.size() > 0 && temporaryTextLines.elementAt(0) instanceof Vector ? temporaryTextLines.size() : 1;
         if (nbrOfScreens2 > 1) {
            for(int i = 0; i < nbrOfScreens2; ++i) {
               this.alertTextLines.addElement(temporaryTextLines.elementAt(i));
            }
         } else {
            this.alertTextLines.addElement(temporaryTextLines);
         }

         this.nbrOfScreens += nbrOfScreens2;
      }

      this.updateModality(true);
   }

   Command[] getExtraCommands() {
      boolean displayMoreCommand = this.nbrOfScreens > 1 && !this.hasPositiveCommand();
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

   boolean launchExtraCommand(Command cmd) {
      if (cmd == MORE_COMMAND) {
         this.displayedScreen = (this.displayedScreen + 1) % this.nbrOfScreens;
         this.repaintFull();
      } else if (cmd == DISMISS_COMMAND) {
         return this.alertDismissAction(cmd);
      }

      return false;
   }

   void callKeyPressed(int keyCode, int keyDataIdx) {
      boolean doRepaint = false;
      boolean callSuper = true;
      synchronized(Display.LCDUILock) {
         if (!this.isModal()) {
            callSuper = false;
            this.alertDismissAction((Command)null);
         } else if (this.nbrOfScreens > 1 && keyCode == -2) {
            this.displayedScreen = (this.displayedScreen + 1) % this.nbrOfScreens;
            doRepaint = true;
         } else if (keyCode == -1) {
            this.displayedScreen = (this.displayedScreen + this.nbrOfScreens - 1) % this.nbrOfScreens;
            doRepaint = true;
         }
      }

      if (callSuper) {
         super.callKeyPressed(keyCode, keyDataIdx);
      }

      if (doRepaint) {
         this.repaintFull();
      }

   }

   void callShowNotify(Display d) {
      super.callShowNotify(d);
      synchronized(Display.LCDUILock) {
         this.isAlertDismissActionCalled = false;
         if (!this.layoutValid) {
            this.layout();
         }

         this.displayedScreen = 0;
         this.startTone(this.myDisplay);
         if (this.indicator != null) {
            this.indicator.callShowNotify();
         }

         if (this.usingSystemImage && this.alertType != null) {
            this.alertPixmap = this.alertType.getPixmap();
         }

         if (this.alertPixmap != null && this.alertPixmap.isAnimatedPixmap()) {
            this.alertPixmap.resetAnimation();
            this.alertPixmap.setAnimationListener(new Alert.AlertAnimationListener());
            this.alertPixmap.startAnimationTimer();
         }

         DeviceControl.switchOnBacklightForDefaultPeriod();
      }
   }

   void callHideNotify(Display d) {
      super.callHideNotify(d);
      synchronized(Display.LCDUILock) {
         if (this.alertType != null) {
            this.alertType.stopSound(this.myDisplay);
         }

         if (!this.isModal()) {
            this.ensureTimerStopped();
         }

         if (this.indicator != null) {
            this.indicator.callHideNotify();
         }

         if (this.alertPixmap != null) {
            if (this.alertPixmap.isAnimatedPixmap()) {
               this.alertPixmap.stopAnimationTimer();
               this.alertPixmap.setAnimationListener((AnimationListener)null);
            }

            if (this.usingSystemImage) {
               this.alertPixmap = null;
            }
         }

      }
   }

   void removedFromDisplayNotify(Display d) {
      this.returnScreen = null;
   }

   void callPaint(Graphics g) {
      super.callPaint(g);
      synchronized(Display.LCDUILock) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
         if (this.getTicker() == null || g.getClipY() + g.getClipHeight() >= this.borderZone.y) {
            uistyle.drawBorder(ng, this.borderZone);
            if (this.imageOnly) {
               uistyle.drawPixmapInArea(ng, this.borderZone.x + this.largeTextZone.x, this.borderZone.y + this.largeTextZone.y, this.largeTextZone.width, this.indicator == null ? this.largeTextZone.height : this.smallTextZone.height, this.alertPixmap);
            } else if (this.alertPixmap != null) {
               uistyle.drawPixmapInArea(ng, this.imageZone.x + this.borderZone.x, this.imageZone.y + this.borderZone.y, this.imageZone.width, this.imageZone.height, this.alertPixmap);
            }

            if (this.alertTextLines.size() > 0 && !this.imageOnly) {
               boolean orig_text_transparency = ng.getTextTransparency();
               ng.setTextTransparency(true);
               Vector textLines = this.nbrOfScreens == 1 ? (Vector)this.alertTextLines.elementAt(0) : (Vector)this.alertTextLines.elementAt(this.displayedScreen);
               int textZoneX;
               int textZoneWidth;
               if (this.alertPixmap != null || this.usingSystemImage && this.alertType != null) {
                  textZoneX = this.smallTextZone.x + this.borderZone.x;
                  textZoneWidth = this.smallTextZone.width;
               } else {
                  textZoneX = this.largeTextZone.x + this.borderZone.x;
                  textZoneWidth = this.largeTextZone.width;
               }

               int textZoneY;
               int textZoneHeight;
               if (this.indicator == null) {
                  textZoneY = this.largeTextZone.y + this.borderZone.y;
                  textZoneHeight = this.largeTextZone.height;
               } else {
                  textZoneY = this.smallTextZone.y + this.borderZone.y;
                  textZoneHeight = this.smallTextZone.height;
               }

               ColorCtrl colorControl = ng.getColorCtrl();
               int originalFgColor = colorControl.getFgColor();
               colorControl.setFgColor(UIStyle.COLOUR_NOTE_TEXT);
               ng.drawTextInArea(textZoneX, textZoneY, textZoneWidth, textZoneHeight, textLines, UIStyle.isAlignedLeftToRight ? 1 : 3);
               colorControl.setFgColor(originalFgColor);
               ng.setTextTransparency(orig_text_transparency);
            }

            if (this.indicator != null) {
               g.translate(this.borderZone.x, this.indicatorZone.y + this.borderZone.y);
               this.indicator.callPaint(g, this.indicatorZone.width, this.indicatorZone.height, false);
            }

            if (ALERT_MODALITY_MODAL == this.modality) {
               this.ensureTimerStopped();
            } else {
               this.ensureTimerStarted();
            }

         }
      }
   }

   void callInvalidate() {
      super.callInvalidate();
      synchronized(Display.LCDUILock) {
         if (!this.layoutValid) {
            this.layout();
         }
      }

      this.repaintFull();
   }

   Zone getMainZone() {
      return this.ticker != null ? uistyle.getZone(31) : uistyle.getZone(30);
   }

   private String getSystemText() {
      int textId = 21;
      if (this.alertType != null) {
         switch(this.alertType.type) {
         case 0:
            textId = 22;
            break;
         case 1:
            textId = 23;
            break;
         case 2:
            textId = 24;
            break;
         case 3:
            textId = 25;
            break;
         case 4:
            textId = 26;
         }
      }

      return TextDatabase.getText(textId);
   }

   private void setZoneReferences() {
      if (this.getTicker() != null) {
         this.borderZone = uistyle.getZone(31);
      } else {
         this.borderZone = uistyle.getZone(30);
      }

      this.imageZone = uistyle.getZone(32);
      this.indicatorZone = uistyle.getZone(33);
      this.smallTextZone = uistyle.getZone(34);
      this.largeTextZone = uistyle.getZone(35);
   }

   boolean isModal() {
      return ALERT_MODALITY_MODAL == this.modality;
   }

   private void ensureTimerStarted() {
      if (this.timerService == null && !this.isAlertDismissActionCalled) {
         int timeout = this.getTimeoutImpl();
         if (timeout > 0) {
            this.timerService = new Timer();
            this.myDisplay.registerTimer(this.timerService);
            this.timerClient = new Alert.TimerClient();
            this.timerService.schedule(this.timerClient, (long)timeout);
         }
      }

   }

   private void ensureTimerStopped() {
      if (this.timerService != null) {
         this.timerService.cancel();
         this.myDisplay.deregisterTimer(this.timerService);
         this.timerService = null;
         this.timerClient = null;
      }

   }

   private void updateModality(boolean updateSoftkeys) {
      if (!this.layoutValid) {
         this.layout();
      } else {
         this.modality = ALERT_MODALITY_TIMED;
         if (this.nbrOfScreens > 1 || this.timeOut == -2 || this.displayableCommands.length() > 1) {
            this.modality = ALERT_MODALITY_MODAL;
         }

         if (this.displayed && this.modality == ALERT_MODALITY_TIMED) {
            this.ensureTimerStarted();
         } else if (this.displayed && this.modality == ALERT_MODALITY_MODAL) {
            this.ensureTimerStopped();
         }

         if (updateSoftkeys) {
            this.updateSoftkeys(true);
         }

      }
   }

   private void updateImageOnly() {
      this.imageOnly = false;
      if (!this.usingSystemImage && this.usingSystemText && this.alertType == null && this.alertPixmap != null && (this.alertPixmap.getWidth() > this.imageZone.width || this.alertPixmap.getHeight() > this.imageZone.height)) {
         this.imageOnly = true;
      }

   }

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
            Alert.this.alertDismissAction(c);
         }
      }

      // $FF: synthetic method
      AlertCommandListener(Object x1) {
         this();
      }
   }

   private class AlertAnimationListener implements AnimationListener {
      private AlertAnimationListener() {
      }

      public void frameAdvanced(Pixmap p) {
         synchronized(Display.LCDUILock) {
            if (Alert.this.alertPixmap != null && p == Alert.this.alertPixmap) {
               Alert.this.repaintFull();
            }

         }
      }

      // $FF: synthetic method
      AlertAnimationListener(Object x1) {
         this();
      }
   }

   private class TimerClient extends TimerTask {
      private TimerClient() {
      }

      public final void run() {
         synchronized(Display.LCDUILock) {
            if (this == Alert.this.timerClient && Alert.this.displayed) {
               Alert.this.alertDismissAction((Command)null);
            }

         }
      }

      // $FF: synthetic method
      TimerClient(Object x1) {
         this();
      }
   }
}
