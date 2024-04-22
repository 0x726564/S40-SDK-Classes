package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.AnimationListener;
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
   private Pixmap alertPixmap;
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

   public Alert(String var1) {
      this(var1, (String)null, (Image)null, (AlertType)null);
   }

   public Alert(String var1, String var2, Image var3, AlertType var4) {
      super(var1);
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
         this.alertType = var4;
         this.setImageImpl(var3);
         this.setStringImpl(var2);
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

   public void setCommandListener(CommandListener var1) {
      if (var1 == null) {
         var1 = new Alert.AlertCommandListener();
      }

      super.setCommandListener((CommandListener)var1);
   }

   public void setImage(Image var1) {
      synchronized(Display.LCDUILock) {
         this.setImageImpl(var1);
      }
   }

   public void setIndicator(Gauge var1) {
      synchronized(Display.LCDUILock) {
         this.setIndicatorImpl(var1);
      }
   }

   public void setString(String var1) {
      synchronized(Display.LCDUILock) {
         this.setStringImpl(var1);
      }
   }

   public void setTimeout(int var1) {
      if (var1 <= 0 && var1 != -2) {
         throw new IllegalArgumentException();
      } else {
         synchronized(Display.LCDUILock) {
            this.setTimeoutImpl(var1);
         }
      }
   }

   public void setType(AlertType var1) {
      synchronized(Display.LCDUILock) {
         this.setTypeImpl(var1);
      }
   }

   boolean alertDismissAction(Command var1) {
      this.ensureTimerStopped();
      this.isAlertDismissActionCalled = true;
      if (var1 == null) {
         var1 = DISMISS_COMMAND;
         if (this.displayableCommands.isNotEmpty()) {
            var1 = this.displayableCommands.getCommand(0);
         }
      }

      if (this.commandListener instanceof Alert.AlertCommandListener) {
         this.myDisplay.setCurrentInternal((Displayable)null, this.returnScreen);
         return true;
      } else {
         this.myDisplay.requestCommandAction(var1, this, this.commandListener);
         return false;
      }
   }

   void setReturnScreen(Displayable var1) {
      this.returnScreen = var1;
   }

   void restartTimerIfNecessary() {
      if (this.timerService != null) {
         this.ensureTimerStopped();
         this.ensureTimerStarted();
      }

   }

   boolean addCommandImpl(Command var1) {
      boolean var2 = super.addCommandImpl(var1);
      if (var2) {
         this.updateModality(false);
      }

      return var2;
   }

   boolean removeCommandImpl(Command var1) {
      boolean var2 = super.removeCommandImpl(var1);
      if (var2) {
         this.updateModality(false);
      }

      return var2;
   }

   void setImageImpl(Image var1) {
      Pixmap var2 = this.alertPixmap;
      this.midletAlertImage = var1;
      if (var1 == null) {
         this.usingSystemImage = true;
         this.alertPixmap = null;
      } else {
         this.usingSystemImage = false;
         if (var1.isMutable()) {
            this.alertPixmap = new Pixmap(var1.getPixmap());
            this.alertPixmap.setMutable(false);
         } else {
            this.alertPixmap = var1.getPixmap();
         }
      }

      this.imageOnly = this.isImageOnly();
      if (var2 != null) {
         var2.stopAnimationTimer();
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

   void setIndicatorImpl(Gauge var1) {
      if (var1 != null) {
         if (!this.isConformantIndicator(var1)) {
            throw new IllegalArgumentException();
         }

         var1.setOwner(this);
      }

      if (this.indicator != null) {
         this.indicator.setOwner((Screen)null);
      }

      this.indicator = var1;
      this.invalidate();
   }

   boolean isConformantIndicator(Gauge var1) {
      return !var1.isInteractive() && var1.owner == null && var1.itemCommands.length() == 0 && var1.getItemCommandListener() == null && var1.getLabel() == null && var1.getLayout() == 0 && var1.lockedWidth == -1 && var1.lockedHeight == -1;
   }

   void setStringImpl(String var1) {
      if (var1 == null) {
         this.usingSystemText = true;
         this.alertText = this.getSystemText();
      } else {
         this.alertText = var1;
         this.usingSystemText = false;
      }

      this.imageOnly = this.isImageOnly();
      this.invalidate();
   }

   void setTypeImpl(AlertType var1) {
      if (this.alertType != var1) {
         this.alertType = var1;
         if (this.usingSystemImage) {
            this.setImageImpl((Image)null);
         }

         if (this.usingSystemText) {
            this.setStringImpl((String)null);
         }

         this.imageOnly = this.isImageOnly();
      }
   }

   void setTimeoutImpl(int var1) {
      this.timeOut = var1;
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

   void setLastPageText(String var1) {
      this.lastPageText = var1;
   }

   void layout() {
      boolean var1 = this.alertPixmap != null || this.usingSystemImage && this.alertType != null;
      super.layout();
      this.setZoneReferences();
      this.alertTextLines.removeAllElements();
      Vector var2 = new Vector();
      TextBreaker.breakTextInArea(var1 ? this.smallTextZone.width : this.largeTextZone.width, this.indicator != null ? this.smallTextZone.height : this.largeTextZone.height, TextBreaker.NBR_OF_AREAS_AS_NEEDED, this.smallTextZone.getFont(), this.alertText, TextBreaker.DEFAULT_TEXT_LEADING, false, false, var2, false, true);
      this.displayedScreen = 0;
      this.nbrOfScreens = var2.size() > 0 && var2.elementAt(0) instanceof Vector ? var2.size() : 1;
      int var3;
      if (this.nbrOfScreens > 1) {
         for(var3 = 0; var3 < this.nbrOfScreens; ++var3) {
            this.alertTextLines.addElement((Vector)var2.elementAt(var3));
         }
      } else {
         this.alertTextLines.addElement(var2);
      }

      if (this.lastPageText != null) {
         var2 = new Vector();
         TextBreaker.breakTextInArea(var1 ? this.smallTextZone.width : this.largeTextZone.width, this.indicator != null ? this.smallTextZone.height : this.largeTextZone.height, TextBreaker.NBR_OF_AREAS_AS_NEEDED, this.smallTextZone.getFont(), this.lastPageText, TextBreaker.DEFAULT_TEXT_LEADING, false, false, var2, false, true);
         var3 = var2.size() > 0 && var2.elementAt(0) instanceof Vector ? var2.size() : 1;
         if (var3 > 1) {
            for(int var4 = 0; var4 < var3; ++var4) {
               this.alertTextLines.addElement((Vector)var2.elementAt(var4));
            }
         } else {
            this.alertTextLines.addElement(var2);
         }

         this.nbrOfScreens += var3;
      }

      this.updateModality(true);
   }

   Command[] getExtraCommands() {
      boolean var1 = this.nbrOfScreens > 1 && !this.hasPositiveCommand();
      if (this.modality == ALERT_MODALITY_MODAL) {
         if (this.displayableCommands.length() == 0 && var1) {
            return MORE_AND_DISMISS;
         } else if (this.displayableCommands.length() == 0) {
            return DISMISS_ONLY;
         } else {
            return var1 ? MORE_ONLY : NO_EXTRA_COMMANDS;
         }
      } else {
         return NO_EXTRA_COMMANDS;
      }
   }

   boolean launchExtraCommand(Command var1) {
      if (var1 == MORE_COMMAND) {
         this.displayedScreen = (this.displayedScreen + 1) % this.nbrOfScreens;
         this.repaintFull();
      } else if (var1 == DISMISS_COMMAND) {
         return this.alertDismissAction(var1);
      }

      return false;
   }

   void callKeyPressed(int var1, int var2) {
      boolean var3 = false;
      boolean var4 = true;
      synchronized(Display.LCDUILock) {
         if (!this.isModal() && (!UIStyle.isRotator() || var1 != -2 && var1 != -1)) {
            var4 = false;
            this.alertDismissAction((Command)null);
         } else if (this.nbrOfScreens > 1 && var1 == -2) {
            this.displayedScreen = (this.displayedScreen + 1) % this.nbrOfScreens;
            var3 = true;
         } else if (var1 == -1) {
            this.displayedScreen = (this.displayedScreen + this.nbrOfScreens - 1) % this.nbrOfScreens;
            var3 = true;
         }
      }

      if (var4) {
         super.callKeyPressed(var1, var2);
      }

      if (var3) {
         this.repaintFull();
      }

   }

   void callShowNotify(Display var1) {
      super.callShowNotify(var1);
      synchronized(Display.LCDUILock) {
         this.isAlertDismissActionCalled = false;
         if (!this.layoutValid) {
            this.layout();
         }

         this.displayedScreen = 0;
         if (this.alertType != null) {
            this.alertType.playSound(this.myDisplay);
         }

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

      }
   }

   void callHideNotify(Display var1) {
      super.callHideNotify(var1);
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

   void removedFromDisplayNotify(Display var1) {
      this.returnScreen = null;
   }

   void callPaint(Graphics var1) {
      super.callPaint(var1);
      synchronized(Display.LCDUILock) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics var3 = var1.getImpl();
         if (this.getTicker() == null || var1.getClipY() + var1.getClipHeight() >= this.borderZone.y) {
            uistyle.drawBorder(var3, this.borderZone);
            if (this.imageOnly) {
               uistyle.drawPixmapInArea(var3, this.borderZone.x + this.largeTextZone.x, this.borderZone.y + this.largeTextZone.y, this.largeTextZone.width, this.indicator == null ? this.largeTextZone.height : this.smallTextZone.height, this.alertPixmap);
            } else if (this.alertPixmap != null) {
               uistyle.drawPixmapInArea(var3, this.imageZone.x + this.borderZone.x, this.imageZone.y + this.borderZone.y, this.imageZone.width, this.imageZone.height, this.alertPixmap);
            }

            if (this.alertTextLines.size() > 0 && !this.imageOnly) {
               boolean var8 = var3.getTextTransparency();
               var3.setTextTransparency(true);
               Vector var9 = this.nbrOfScreens == 1 ? (Vector)this.alertTextLines.elementAt(0) : (Vector)this.alertTextLines.elementAt(this.displayedScreen);
               int var4;
               int var6;
               if (this.alertPixmap != null || this.usingSystemImage && this.alertType != null) {
                  var4 = this.smallTextZone.x + this.borderZone.x;
                  var6 = this.smallTextZone.width;
               } else {
                  var4 = this.largeTextZone.x + this.borderZone.x;
                  var6 = this.largeTextZone.width;
               }

               int var5;
               int var7;
               if (this.indicator == null) {
                  var5 = this.largeTextZone.y + this.borderZone.y;
                  var7 = this.largeTextZone.height;
               } else {
                  var5 = this.smallTextZone.y + this.borderZone.y;
                  var7 = this.smallTextZone.height;
               }

               var3.drawTextInArea(var4, var5, var6, var7, var9, UIStyle.isAlignedLeftToRight ? 1 : 3);
               var3.setTextTransparency(var8);
            }

            if (this.indicator != null) {
               var1.translate(this.borderZone.x, this.indicatorZone.y + this.borderZone.y);
               this.indicator.callPaint(var1, this.indicatorZone.width, this.indicatorZone.height, false);
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
      byte var1 = 21;
      if (this.alertType != null) {
         switch(this.alertType.type) {
         case 0:
            var1 = 22;
            break;
         case 1:
            var1 = 23;
            break;
         case 2:
            var1 = 24;
            break;
         case 3:
            var1 = 25;
            break;
         case 4:
            var1 = 26;
         }
      }

      return TextDatabase.getText(var1);
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
         int var1 = this.getTimeoutImpl();
         if (var1 > 0) {
            this.timerService = new Timer();
            this.myDisplay.registerTimer(this.timerService);
            this.timerClient = new Alert.TimerClient();
            this.timerService.schedule(this.timerClient, (long)var1);
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

   private void updateModality(boolean var1) {
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

         if (var1) {
            this.updateSoftkeys(true);
         }

      }
   }

   private boolean isImageOnly() {
      boolean var1 = false;
      if (!this.usingSystemImage && this.usingSystemText && this.alertType == null && this.alertPixmap != null && (this.alertPixmap.getWidth() > this.imageZone.width || this.alertPixmap.getHeight() > this.imageZone.height)) {
         var1 = true;
      }

      return var1;
   }

   static {
      DISMISS_ONLY = new Command[]{DISMISS_COMMAND};
      MORE_ONLY = new Command[]{MORE_COMMAND};
      MORE_AND_DISMISS = new Command[]{MORE_COMMAND, DISMISS_COMMAND};
   }

   private class AlertCommandListener implements CommandListener {
      private AlertCommandListener() {
      }

      public void commandAction(Command var1, Displayable var2) {
         synchronized(Display.LCDUILock) {
            Alert.this.alertDismissAction(var1);
         }
      }

      // $FF: synthetic method
      AlertCommandListener(Object var2) {
         this();
      }
   }

   private class AlertAnimationListener implements AnimationListener {
      private AlertAnimationListener() {
      }

      public void frameAdvanced(Pixmap var1) {
         synchronized(Display.LCDUILock) {
            if (Alert.this.alertPixmap != null && var1 == Alert.this.alertPixmap) {
               Alert.this.repaintFull();
            }

         }
      }

      // $FF: synthetic method
      AlertAnimationListener(Object var2) {
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
      TimerClient(Object var2) {
         this();
      }
   }
}
