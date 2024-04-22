package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.gdi.AnimationListener;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import java.util.Timer;
import java.util.TimerTask;

public class Gauge extends Item {
   static final Command EDIT = new Command(9, 29);
   static final Command[] extraCommands;
   private static final Zone valueGraphicZone;
   private static final Zone valueSmallTextZone;
   private static final Zone valueLargeTextZone;
   private static final Zone minValueTextZone;
   private static final Zone maxValueTextZone;
   private static final boolean FOUR_WAY_SCROLL;
   private static final int INIT_STATE = -2;
   public static final int INDEFINITE = -1;
   public static final int CONTINUOUS_IDLE = 0;
   public static final int INCREMENTAL_IDLE = 1;
   public static final int CONTINUOUS_RUNNING = 2;
   public static final int INCREMENTAL_UPDATING = 3;
   boolean focused = false;
   int maxValue = 0;
   int value = -2;
   boolean isInteractive = true;
   private TextLine valueText = null;
   private Pixmap gaugePixmap;
   private int highlightedWidth;
   private int highlightedX;
   private Gauge.KeyRepeatTimerTask keyRepeatTimerTask;
   private long delay = 1000L;
   private Zone inlineEditZone = null;
   private Timer keyRepeatTimer;
   private long lastKeyPressedTime;
   private static final int MAXIMUM_POWER = 100000000;
   private static final int[] roundingValues;
   private static final int MAXGAUGETIME_51_100 = 8000;
   private static final int MAXGAUGETIME_101_1000 = 12000;
   private static final int MAXGAUGETIME_1001_X = 20000;
   private int keyStatus = 0;
   private static final int KEY_RELEASED = 0;
   private static final int KEY_INITIAL_PRESS = 1;
   private static final int KEY_INITIAL_REPEAT = 2;
   private static final int KEY_REPEATED = 3;
   private int incrementalValue = 1;
   private static final long KEYREPEATINTERVAL = 350L;
   private static final boolean isInline;
   private static Pixmap interactiveBackgroundPixmap;
   private static Pixmap interactiveNotchPixmap;
   private static Pixmap interactiveKnobPixmap;
   private static final int SMALL_LAYOUT_LIMIT = 1000;

   public Gauge(String label, boolean interactive, int maxValue, int initialValue) {
      super(label);
      if (interactive) {
         if (maxValue <= 0) {
            throw new IllegalArgumentException();
         }
      } else {
         if (maxValue <= 0 && maxValue != -1) {
            throw new IllegalArgumentException();
         }

         if (maxValue == -1 && (initialValue < 0 || initialValue > 3)) {
            throw new IllegalArgumentException();
         }
      }

      synchronized(Display.LCDUILock) {
         this.isInteractive = interactive;
         this.maxValue = maxValue;
         this.inlineEditZone = valueLargeTextZone;
         this.setValueImpl(initialValue, false);
         this.setMaxValueImpl(maxValue);
      }
   }

   public int getMaxValue() {
      return this.maxValue;
   }

   public int getValue() {
      return this.value;
   }

   public boolean isInteractive() {
      return this.isInteractive;
   }

   public void setMaxValue(int maxValue) {
      synchronized(Display.LCDUILock) {
         if (this.isInteractive) {
            if (maxValue <= 0) {
               throw new IllegalArgumentException();
            }
         } else if (maxValue <= 0 && maxValue != -1) {
            throw new IllegalArgumentException();
         }

         this.setMaxValueImpl(maxValue);
      }
   }

   public void setValue(int value) {
      synchronized(Display.LCDUILock) {
         if (this.isInteractive || this.maxValue != -1 || value >= 0 && value <= 3) {
            this.setValueImpl(value, false);
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public void setLabel(String label) {
      if (this.owner instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setLabel(label);
      }
   }

   public void setLayout(int layout) {
      if (this.owner instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setLayout(layout);
      }
   }

   public void addCommand(Command cmd) {
      if (this.owner instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.addCommand(cmd);
      }
   }

   public void setItemCommandListener(ItemCommandListener l) {
      if (this.owner instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setItemCommandListener(l);
      }
   }

   public void setPreferredSize(int width, int height) {
      if (this.owner instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setPreferredSize(width, height);
      }
   }

   public void setDefaultCommand(Command cmd) {
      if (this.owner instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setDefaultCommand(cmd);
      }
   }

   boolean equateNLA() {
      if (super.equateNLA()) {
         return true;
      } else {
         return (this.layout & 16384) != 16384;
      }
   }

   boolean equateNLB() {
      if (super.equateNLB()) {
         return true;
      } else {
         return (this.layout & 16384) != 16384;
      }
   }

   Gauge clone() {
      return new Gauge((String)null, this.isInteractive, this.maxValue, this.value);
   }

   void setMaxValueImpl(int maxValue) {
      if (maxValue > 0) {
         if (this.maxValue > 0) {
            if (this.value > maxValue) {
               this.value = maxValue;
            }
         } else {
            this.value = 0;
         }
      }

      if (!this.isInteractive && maxValue == -1 && this.maxValue > 0) {
         this.value = 0;
      }

      this.maxValue = maxValue;
      this.computeIncrementalValue();
      this.setPixmap();
      if (this.isInteractive) {
         if (this.inlineEditZone != valueLargeTextZone && this.maxValue >= 1000) {
            this.inlineEditZone = valueLargeTextZone;
            this.invalidate();
         } else if (this.inlineEditZone != valueSmallTextZone && this.maxValue < 1000) {
            this.inlineEditZone = valueSmallTextZone;
            this.invalidate();
         }

         this.valueText = this.getText(this.value);
         this.highlightedWidth = Displayable.uistyle.getEditableTextWidth(this.inlineEditZone, this.valueText, 2);
         this.highlightedX = Displayable.uistyle.getEditableTextX(this.inlineEditZone, this.valueText, 2);
      }

      if (this.owner != null) {
         this.owner.repaintRequest();
      }

   }

   void setValueImpl(int value, boolean hasItemStateChanged) {
      if (this.maxValue > 0) {
         if (value < 0) {
            value = 0;
         } else if (value > this.maxValue) {
            value = this.maxValue;
         }
      }

      if (this.owner instanceof Form && this.value != value && hasItemStateChanged) {
         this.owner.changedItemState(this);
      }

      if (this.value != value) {
         this.value = value;
         this.setPixmap();
      } else if (this.maxValue == -1 && this.value == 3 && this.gaugePixmap != null) {
         this.gaugePixmap.advanceFrame();
      }

      if (this.isInteractive) {
         this.valueText = this.getText(this.value);
         this.highlightedWidth = Displayable.uistyle.getEditableTextWidth(this.inlineEditZone, this.valueText, 2);
         this.highlightedX = Displayable.uistyle.getEditableTextX(this.inlineEditZone, this.valueText, 2);
      }

      if (this.owner != null) {
         this.owner.repaintRequest();
      }

   }

   void callPaint(Graphics g, int w, int h, boolean isFocused) {
      super.callPaint(g, w, h, isFocused);
      synchronized(Display.LCDUILock) {
         this.focused = isFocused;
         com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
         int posX = g.getTranslateX();
         int posY = g.getTranslateY();
         if (this.owner instanceof Form) {
            posY += TextBreaker.DEFAULT_TEXT_LEADING;
         }

         if (this.isInteractive) {
            this.renderInteractiveGaugeImage(g, w, h, isFocused, valueGraphicZone);
            Displayable.uistyle.drawEditableTextWithBorder(ng, this.inlineEditZone, posX, posY, this.valueText, valueSmallTextZone.getBorderType(), 2, this.focused);
         } else {
            if (!(this.owner instanceof Alert) && this.isFocusable()) {
               UIStyle.getUIStyle().drawBorder(ng, posX, posY, valueGraphicZone.width, valueGraphicZone.height, valueSmallTextZone.getBorderType(), this.focused);
            }

            this.renderNonInteractiveGaugeImage(g, w, h, isFocused);
         }

      }
   }

   void renderInteractiveGaugeImage(Graphics g, int w, int h, boolean isFocused, Zone zone) {
      com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
      int posX = g.getTranslateX() + zone.x;
      int posY = g.getTranslateY() + zone.y;
      int offsetY = 0;
      int offsetX = (zone.width - interactiveBackgroundPixmap.getWidth()) / 2;
      posX += offsetX;
      if (this.owner instanceof Form) {
         offsetY += TextBreaker.DEFAULT_TEXT_LEADING + 2;
      }

      ng.drawPixmap(interactiveBackgroundPixmap, posX, posY);
      int numberOfNotches = this.maxValue <= 10 ? this.maxValue + 1 : 5;
      int width = interactiveBackgroundPixmap.getWidth();
      int gapBetweenNotches = (width - interactiveNotchPixmap.getWidth() << 5) / (numberOfNotches - 1);

      for(int i = 1; i < numberOfNotches - 1; ++i) {
         int xx = posX + (gapBetweenNotches * i >> 5);
         ng.drawPixmap(interactiveNotchPixmap, xx, posY);
      }

      long p = (long)this.value * (long)(width - interactiveNotchPixmap.getWidth());
      p /= (long)this.maxValue;
      ng.drawPixmap(interactiveKnobPixmap, posX + (int)p, posY);
      ColorCtrl color_ctrl = ng.getColorCtrl();
      int orig_fg_colour = color_ctrl.getFgColor();
      int orig_bg_colour = color_ctrl.getBgColor();
      com.nokia.mid.impl.isa.ui.gdi.Font orig_font = ng.getFont();
      ng.setFont(minValueTextZone.getFont());
      color_ctrl.setFgColor(UIStyle.COLOUR_TEXT);
      color_ctrl.setBgColor(UIStyle.COLOUR_BACKGROUND);
      ng.drawTextLine(minValueTextZone, posX, posY + offsetY, TextBreaker.breakOneLineTextInZone(minValueTextZone, "0"), 1);
      ng.drawTextLine(maxValueTextZone, 0 - posX, posY + offsetY, TextBreaker.breakOneLineTextInZone(maxValueTextZone, Integer.toString(this.maxValue)), 3);
      color_ctrl.setFgColor(orig_fg_colour);
      color_ctrl.setBgColor(orig_bg_colour);
      ng.setFont(orig_font);
   }

   void renderNonInteractiveGaugeImage(Graphics g, int w, int h, boolean isFocused) {
      com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
      int posX = g.getTranslateX();
      int posY = g.getTranslateY();
      boolean aForm = false;
      if (this.owner instanceof Form) {
         posY += TextBreaker.DEFAULT_TEXT_LEADING;
         aForm = true;
      }

      if (this.maxValue == -1) {
         if (this.gaugePixmap != null) {
            ng.drawPixmap(this.gaugePixmap, (short)((valueGraphicZone.width - this.gaugePixmap.getWidth()) / 2 + posX), (short)(valueGraphicZone.y + valueGraphicZone.getMarginTop() + posY));
         } else {
            Displayable.uistyle.drawGauge(ng, valueGraphicZone, posX, posY, 0, this.maxValue, isFocused, aForm);
         }
      } else {
         Displayable.uistyle.drawGauge(ng, valueGraphicZone, posX, posY, this.value, this.maxValue, isFocused, aForm);
      }

   }

   void callShowNotify() {
      super.callShowNotify();
      synchronized(Display.LCDUILock) {
         this.keyStatus = 0;
         if (this.gaugePixmap != null && this.gaugePixmap.isAnimatedPixmap()) {
            this.gaugePixmap.resetAnimation();
            if (this.value == 2) {
               this.gaugePixmap.setAnimationListener(new Gauge.GaugeAnimationListener());
               this.gaugePixmap.startAnimationTimer();
            }
         }

      }
   }

   void callHideNotify() {
      super.callHideNotify();
      synchronized(Display.LCDUILock) {
         this.stopAnimation();
         this.stopKeyRepeatTimer();
         this.keyStatus = 0;
      }
   }

   void setOwner(Screen owner) {
      super.setOwner(owner);
      synchronized(Display.LCDUILock) {
         if (owner == null) {
            this.stopAnimation();
         }

      }
   }

   private void stopAnimation() {
      if (this.gaugePixmap != null && this.gaugePixmap.isAnimatedPixmap()) {
         this.gaugePixmap.stopAnimationTimer();
         this.gaugePixmap.setAnimationListener((AnimationListener)null);
      }

   }

   int getNextScrollPixels(boolean direction, int pos) {
      synchronized(Display.LCDUILock) {
         Zone mainZone = this.owner.getMainZone();
         int itemH = this.getHeight();
         int rval = pos;
         if (!direction) {
            rval = pos + itemH - mainZone.height + EDITABLE_TEXT_BOTTOM_MARGIN;
         }

         return rval;
      }
   }

   int getHeight() {
      synchronized(Display.LCDUILock) {
         int height;
         if (null != this.valueText && this.isInteractive()) {
            height = this.inlineEditZone.y + this.inlineEditZone.height;
         } else {
            height = valueGraphicZone.height;
         }

         if (this.owner instanceof Form) {
            height += TextBreaker.DEFAULT_TEXT_LEADING;
         }

         return height;
      }
   }

   int callPreferredWidth(int h) {
      return valueGraphicZone.width;
   }

   int callPreferredHeight(int w) {
      return this.getHeight();
   }

   int callMinimumWidth() {
      return valueGraphicZone.width;
   }

   int callMinimumHeight() {
      return this.getHeight();
   }

   int callHighlightedWidth() {
      return this.isInteractive ? this.highlightedWidth : this.bounds[2];
   }

   int callHighlightedX() {
      return this.isInteractive ? this.highlightedX + this.bounds[0] : this.bounds[0];
   }

   boolean isFocusable() {
      return this.isInteractive || this.itemCommands.isNotEmpty();
   }

   void setFocus(boolean dir) {
      if (this.isInteractive || this.itemCommands.isNotEmpty()) {
         this.focused = true;
      }

   }

   void removeFocus() {
      this.focused = false;
   }

   boolean supportHorizontalScrolling() {
      return true;
   }

   void callKeyPressed(int keyCode, int keyDataIdx) {
      synchronized(Display.LCDUILock) {
         if (this.isInteractive) {
            if (keyCode != -3 && keyCode != -4) {
               if (this.keyStatus == 0) {
                  if (!isInline && this.owner instanceof Form) {
                     this.startGaugeScreen(keyCode, keyDataIdx);
                  } else {
                     this.processKeyPress(keyCode);
                  }
               }
            } else if (this.keyStatus == 0) {
               if (!isInline && this.owner instanceof Form) {
                  this.startGaugeScreen(keyCode, keyDataIdx);
               } else {
                  this.keyStatus = 1;
                  this.startKeyRepeatTimer(keyCode);
                  this.processKeyPress(keyCode);
               }
            }
         }

      }
   }

   void callKeyRepeated(int keyCode, int keyDataIdx) {
      this.callKeyPressed(keyCode, keyDataIdx);
   }

   void callKeyReleased(int keyCode, int keyDataIdx) {
      synchronized(Display.LCDUILock) {
         if (keyCode == -3 || keyCode == -4) {
            this.keyStatus = 0;
            this.stopKeyRepeatTimer();
         }

      }
   }

   private void keyRepeatTimeOut(int keyCode) {
      switch(this.keyStatus) {
      case 1:
         this.keyStatus = 2;
         break;
      case 2:
         this.keyStatus = 3;
      case 3:
         break;
      default:
         this.stopKeyRepeatTimer();
         return;
      }

      this.processKeyPress(keyCode);
   }

   private void processKeyPress(int keyCode) {
      boolean isRepaintRequired = false;
      if (this.isInteractive) {
         switch(keyCode) {
         case -4:
         case -1:
            if (FOUR_WAY_SCROLL && -1 == keyCode) {
               break;
            }

            this.lastKeyPressedTime = 0L;
            if (this.value < this.maxValue) {
               if (this.keyStatus == 2) {
                  this.setValueImpl(this.roundTheInitialValue(true), true);
               } else if (this.keyStatus == 3) {
                  this.setValueImpl(this.value + this.incrementalValue, true);
               } else {
                  this.setValueImpl(this.value + 1, true);
               }

               isRepaintRequired = true;
            }
            break;
         case -3:
         case -2:
            if (FOUR_WAY_SCROLL && -2 == keyCode) {
               break;
            }

            this.lastKeyPressedTime = 0L;
            if (this.value > 0) {
               if (this.keyStatus == 2) {
                  this.setValueImpl(this.roundTheInitialValue(false), true);
               } else if (this.keyStatus == 3) {
                  this.setValueImpl(this.value - this.incrementalValue, true);
               } else {
                  this.setValueImpl(this.value - 1, true);
               }

               isRepaintRequired = true;
            }
            break;
         default:
            if (keyCode >= 48 && keyCode <= 57) {
               long keyPressedTime = System.currentTimeMillis();
               if (keyPressedTime - this.lastKeyPressedTime < this.delay && 10 * this.value + keyCode - 48 <= this.maxValue) {
                  this.setValueImpl(10 * this.value + keyCode - 48, true);
               } else if (keyCode - 48 <= this.maxValue) {
                  this.setValueImpl(keyCode - 48, true);
               }

               isRepaintRequired = true;
               this.lastKeyPressedTime = keyPressedTime;
            }
         }

         if (isRepaintRequired && this.owner != null) {
            this.valueText = this.getText(this.value);
            this.highlightedWidth = Displayable.uistyle.getEditableTextWidth(this.inlineEditZone, this.valueText, 2);
            this.highlightedX = Displayable.uistyle.getEditableTextX(this.inlineEditZone, this.valueText, 2);
            this.owner.repaintRequest();
         }
      }

   }

   boolean hasFocus(int pos) {
      int tickerH = this.owner.getTickerHeight();
      int screenH = DeviceInfo.getDisplayHeight(0);
      int itemH = this.getHeight();
      return screenH - tickerH - pos >= itemH && pos >= 0;
   }

   Command[] getExtraCommands() {
      return this.isInteractive && !isInline ? extraCommands : null;
   }

   boolean launchExtraCommand(Command c) {
      if (this.isInteractive) {
         this.startGaugeScreen(0, 0);
         return true;
      } else {
         return false;
      }
   }

   private void startGaugeScreen(int keyId, int keyDataIdx) {
      Screen gaugeScreen = null;

      try {
         Class gsClass = Class.forName("javax.microedition.lcdui.GaugeScreen");
         gaugeScreen = (Screen)gsClass.newInstance();
      } catch (Exception var5) {
      }

      GaugeEditor ge = (GaugeEditor)gaugeScreen;
      ge.initialize(this, keyId, keyDataIdx);
      this.owner.myDisplay.setCurrentInternal((Displayable)null, gaugeScreen);
   }

   private void startKeyRepeatTimer(int keyId) {
      if (this.keyRepeatTimer == null) {
         this.keyRepeatTimer = new Timer();
         this.keyRepeatTimerTask = new Gauge.KeyRepeatTimerTask(keyId);
         this.keyRepeatTimer.schedule(this.keyRepeatTimerTask, 350L, 350L);
      }

   }

   private void stopKeyRepeatTimer() {
      if (this.keyRepeatTimer != null) {
         this.keyRepeatTimer.cancel();
         this.keyRepeatTimerTask = null;
         this.keyRepeatTimer = null;
      }

   }

   private TextLine getText(int value) {
      StringBuffer valueTextBuff = new StringBuffer();
      valueTextBuff.append(value);
      TextLine tLine = TextBreaker.breakOneLineTextInZone(this.inlineEditZone, true, true, valueTextBuff.toString(), 0, false);
      return tLine;
   }

   private int roundToValues(int number) {
      int power = 1;
      int upper = 0;
      int lower = 0;

      for(int intValue = number; power < 100000000; power *= 10) {
         for(int i = 0; i < roundingValues.length; ++i) {
            lower = upper;
            upper = roundingValues[i] * power;
            if (intValue <= upper) {
               if (intValue - lower < upper - intValue) {
                  intValue = lower;
               } else {
                  intValue = upper;
               }

               return intValue;
            }
         }
      }

      return lower;
   }

   private int roundTheInitialValue(boolean scrollingUp) {
      int roundedValue;
      if (scrollingUp) {
         if (this.value % this.incrementalValue == 0) {
            roundedValue = this.value + this.incrementalValue;
         } else {
            roundedValue = this.value - this.value % this.incrementalValue + this.incrementalValue;
         }
      } else if (this.value % this.incrementalValue == 0) {
         roundedValue = this.value - this.incrementalValue;
      } else {
         roundedValue = this.value - this.value % this.incrementalValue;
      }

      return roundedValue;
   }

   private void computeIncrementalValue() {
      int incValue = 10;
      if (this.maxValue > 50 && this.maxValue <= 100) {
         incValue = (int)(350L * (long)this.maxValue * 10L / 8000L + 5L);
      } else if (this.maxValue > 100 && this.maxValue <= 1000) {
         incValue = (int)(350L * (long)this.maxValue * 10L / 12000L + 5L);
      } else if (this.maxValue > 1000) {
         incValue = (int)(350L * (long)this.maxValue * 10L / 20000L + 5L);
      }

      this.incrementalValue = this.roundToValues(incValue / 10);
   }

   private void setPixmap() {
      if (this.gaugePixmap != null && this.gaugePixmap.isAnimatedPixmap() && this.value == 2) {
         this.gaugePixmap.stopAnimationTimer();
         this.gaugePixmap.setAnimationListener((AnimationListener)null);
      }

      if (this.maxValue == -1) {
         if (this.value != 3 && this.value != 2) {
            if (this.value == 1 || this.value == 0) {
               this.gaugePixmap = Pixmap.createPixmap(17);
            }
         } else {
            this.gaugePixmap = Pixmap.createPixmap(10);
         }

         if (this.gaugePixmap != null && this.gaugePixmap.isAnimatedPixmap()) {
            this.gaugePixmap.resetAnimation();
            if (this.value == 2) {
               this.gaugePixmap.setAnimationListener(new Gauge.GaugeAnimationListener());
               this.gaugePixmap.startAnimationTimer();
            }
         }
      }

   }

   static {
      extraCommands = new Command[]{EDIT};
      valueGraphicZone = Displayable.uistyle.getZone(22);
      valueSmallTextZone = Displayable.uistyle.getZone(25);
      valueLargeTextZone = Displayable.uistyle.getZone(26);
      minValueTextZone = Displayable.uistyle.getZone(23);
      maxValueTextZone = Displayable.uistyle.getZone(24);
      FOUR_WAY_SCROLL = UIStyle.isFourWayScroll();
      roundingValues = new int[]{1, 2, 5};
      isInline = UIStyle.isInline();
      interactiveBackgroundPixmap = Pixmap.createPixmap(14);
      interactiveNotchPixmap = Pixmap.createPixmap(15);
      interactiveKnobPixmap = Pixmap.createPixmap(16);
   }

   private class KeyRepeatTimerTask extends TimerTask {
      private int keyId;

      KeyRepeatTimerTask(int keyId) {
         this.keyId = keyId;
      }

      public final void run() {
         synchronized(Display.LCDUILock) {
            Gauge.this.keyRepeatTimeOut(this.keyId);
         }
      }
   }

   private class GaugeAnimationListener implements AnimationListener {
      private GaugeAnimationListener() {
      }

      public void frameAdvanced(Pixmap p) {
         synchronized(Display.LCDUILock) {
            if (Gauge.this.gaugePixmap != null && p == Gauge.this.gaugePixmap && Gauge.this.owner != null) {
               Gauge.this.owner.repaintFull();
            }

         }
      }

      // $FF: synthetic method
      GaugeAnimationListener(Object x1) {
         this();
      }
   }
}
