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
   private static final int HIGHLIGHT_COLOUR;
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

   public Gauge(String var1, boolean var2, int var3, int var4) {
      super(var1);
      if (var2) {
         if (var3 <= 0) {
            throw new IllegalArgumentException();
         }
      } else {
         if (var3 <= 0 && var3 != -1) {
            throw new IllegalArgumentException();
         }

         if (var3 == -1 && (var4 < 0 || var4 > 3)) {
            throw new IllegalArgumentException();
         }
      }

      synchronized(Display.LCDUILock) {
         this.isInteractive = var2;
         this.maxValue = var3;
         this.inlineEditZone = valueLargeTextZone;
         this.setValueImpl(var4, false);
         this.setMaxValueImpl(var3);
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

   public void setMaxValue(int var1) {
      synchronized(Display.LCDUILock) {
         if (this.isInteractive) {
            if (var1 <= 0) {
               throw new IllegalArgumentException();
            }
         } else if (var1 <= 0 && var1 != -1) {
            throw new IllegalArgumentException();
         }

         this.setMaxValueImpl(var1);
      }
   }

   public void setValue(int var1) {
      synchronized(Display.LCDUILock) {
         if (this.isInteractive || this.maxValue != -1 || var1 >= 0 && var1 <= 3) {
            this.setValueImpl(var1, false);
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public void setLabel(String var1) {
      if (this.owner instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setLabel(var1);
      }
   }

   public void setLayout(int var1) {
      if (this.owner instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setLayout(var1);
      }
   }

   public void addCommand(Command var1) {
      if (this.owner instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.addCommand(var1);
      }
   }

   public void setItemCommandListener(ItemCommandListener var1) {
      if (this.owner instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setItemCommandListener(var1);
      }
   }

   public void setPreferredSize(int var1, int var2) {
      if (this.owner instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setPreferredSize(var1, var2);
      }
   }

   public void setDefaultCommand(Command var1) {
      if (this.owner instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setDefaultCommand(var1);
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

   void setMaxValueImpl(int var1) {
      if (var1 > 0) {
         if (this.maxValue > 0) {
            if (this.value > var1) {
               this.value = var1;
            }
         } else {
            this.value = 0;
         }
      }

      if (!this.isInteractive && var1 == -1 && this.maxValue > 0) {
         this.value = 0;
      }

      this.maxValue = var1;
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

   void setValueImpl(int var1, boolean var2) {
      if (this.maxValue > 0) {
         if (var1 < 0) {
            var1 = 0;
         } else if (var1 > this.maxValue) {
            var1 = this.maxValue;
         }
      }

      if (this.owner instanceof Form && this.value != var1 && var2) {
         this.owner.changedItemState(this);
      }

      if (this.value != var1) {
         this.value = var1;
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

   void callPaint(Graphics var1, int var2, int var3, boolean var4) {
      super.callPaint(var1, var2, var3, var4);
      synchronized(Display.LCDUILock) {
         this.focused = var4;
         com.nokia.mid.impl.isa.ui.gdi.Graphics var6 = var1.getImpl();
         int var7 = var1.getTranslateX();
         int var8 = var1.getTranslateY();
         if (this.owner instanceof Form) {
            var8 += TextBreaker.DEFAULT_TEXT_LEADING;
         }

         if (this.isInteractive) {
            this.renderInteractiveGaugeImage(var1, var2, var3, var4, valueGraphicZone);
            Displayable.uistyle.drawEditableTextWithBorder(var6, this.inlineEditZone, var7, var8, this.valueText, valueSmallTextZone.getBorderType(), 2, this.focused);
         } else {
            if (!(this.owner instanceof Alert) && this.isFocusable()) {
               UIStyle.getUIStyle().drawBorder(var6, var7, var8, valueGraphicZone.width, valueGraphicZone.height, valueSmallTextZone.getBorderType(), this.focused);
            }

            this.renderNonInteractiveGaugeImage(var1, var2, var3, var4);
         }

      }
   }

   void renderInteractiveGaugeImage(Graphics var1, int var2, int var3, boolean var4, Zone var5) {
      com.nokia.mid.impl.isa.ui.gdi.Graphics var6 = var1.getImpl();
      int var7 = var1.getTranslateX() + var5.x;
      int var8 = var1.getTranslateY() + var5.y;
      int var9 = 0;
      int var10 = (var5.width - interactiveBackgroundPixmap.getWidth()) / 2;
      var7 += var10;
      if (this.owner instanceof Form) {
         var9 += TextBreaker.DEFAULT_TEXT_LEADING + 2;
      }

      var6.drawPixmap(interactiveBackgroundPixmap, var7, var8);
      int var11 = this.maxValue <= 10 ? this.maxValue + 1 : 5;
      int var12 = interactiveBackgroundPixmap.getWidth();
      boolean var13 = false;
      int var14 = (var12 - interactiveNotchPixmap.getWidth() << 5) / (var11 - 1);

      for(int var15 = 1; var15 < var11 - 1; ++var15) {
         int var16 = var7 + (var14 * var15 >> 5);
         var6.drawPixmap(interactiveNotchPixmap, var16, var8);
      }

      long var21 = (long)this.value * (long)(var12 - interactiveNotchPixmap.getWidth());
      var21 /= (long)this.maxValue;
      var6.drawPixmap(interactiveKnobPixmap, var7 + (int)var21, var8);
      ColorCtrl var17 = var6.getColorCtrl();
      int var18 = var17.getFgColor();
      int var19 = var17.getBgColor();
      com.nokia.mid.impl.isa.ui.gdi.Font var20 = var6.getFont();
      var6.setFont(minValueTextZone.getFont());
      var17.setFgColor(UIStyle.COLOUR_TEXT);
      var17.setBgColor(UIStyle.COLOUR_BACKGROUND);
      var6.drawTextLine(minValueTextZone, var7, var8 + var9, TextBreaker.breakOneLineTextInZone(minValueTextZone, "0"), 1);
      var6.drawTextLine(maxValueTextZone, 0 - var7, var8 + var9, TextBreaker.breakOneLineTextInZone(maxValueTextZone, Integer.toString(this.maxValue)), 3);
      var17.setFgColor(var18);
      var17.setBgColor(var19);
      var6.setFont(var20);
   }

   void renderNonInteractiveGaugeImage(Graphics var1, int var2, int var3, boolean var4) {
      com.nokia.mid.impl.isa.ui.gdi.Graphics var5 = var1.getImpl();
      int var6 = var1.getTranslateX();
      int var7 = var1.getTranslateY();
      boolean var8 = false;
      if (this.owner instanceof Form) {
         var7 += TextBreaker.DEFAULT_TEXT_LEADING;
         var8 = true;
      }

      if (this.maxValue == -1) {
         if (this.gaugePixmap != null) {
            var5.drawPixmap(this.gaugePixmap, (short)((valueGraphicZone.width - this.gaugePixmap.getWidth()) / 2 + var6), (short)(valueGraphicZone.y + valueGraphicZone.getMarginTop() + var7));
         } else {
            Displayable.uistyle.drawGauge(var5, valueGraphicZone, var6, var7, 0, this.maxValue, var4, var8);
         }
      } else {
         Displayable.uistyle.drawGauge(var5, valueGraphicZone, var6, var7, this.value, this.maxValue, var4, var8);
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

   void setOwner(Screen var1) {
      super.setOwner(var1);
      synchronized(Display.LCDUILock) {
         if (var1 == null) {
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

   int getNextScrollPixels(boolean var1, int var2) {
      synchronized(Display.LCDUILock) {
         Zone var4 = this.owner.getMainZone();
         int var5 = this.getHeight();
         int var6 = var2;
         if (!var1) {
            var6 = var2 + var5 - var4.height + EDITABLE_TEXT_BOTTOM_MARGIN;
         }

         return var6;
      }
   }

   int getHeight() {
      synchronized(Display.LCDUILock) {
         int var2;
         if (null != this.valueText && this.isInteractive()) {
            var2 = this.inlineEditZone.y + this.inlineEditZone.height;
         } else {
            var2 = valueGraphicZone.height;
         }

         if (this.owner instanceof Form) {
            var2 += TextBreaker.DEFAULT_TEXT_LEADING;
         }

         return var2;
      }
   }

   int callPreferredWidth(int var1) {
      return valueGraphicZone.width;
   }

   int callPreferredHeight(int var1) {
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

   void setFocus(boolean var1) {
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

   void callKeyPressed(int var1, int var2) {
      synchronized(Display.LCDUILock) {
         if (this.isInteractive) {
            if (var1 != -3 && var1 != -4) {
               if (this.keyStatus == 0) {
                  if (!isInline && this.owner instanceof Form) {
                     this.startGaugeScreen(var1, var2);
                  } else {
                     this.processKeyPress(var1);
                  }
               }
            } else if (this.keyStatus == 0) {
               if (!isInline && this.owner instanceof Form) {
                  this.startGaugeScreen(var1, var2);
               } else {
                  this.keyStatus = 1;
                  this.startKeyRepeatTimer(var1);
                  this.processKeyPress(var1);
               }
            }
         }

      }
   }

   void callKeyRepeated(int var1, int var2) {
      this.callKeyPressed(var1, var2);
   }

   void callKeyReleased(int var1, int var2) {
      synchronized(Display.LCDUILock) {
         if (var1 == -3 || var1 == -4) {
            this.keyStatus = 0;
            this.stopKeyRepeatTimer();
         }

      }
   }

   private void keyRepeatTimeOut(int var1) {
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

      this.processKeyPress(var1);
   }

   private void processKeyPress(int var1) {
      boolean var2 = false;
      if (this.isInteractive) {
         switch(var1) {
         case -4:
         case -1:
            if (FOUR_WAY_SCROLL && -1 == var1) {
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

               var2 = true;
            }
            break;
         case -3:
         case -2:
            if (FOUR_WAY_SCROLL && -2 == var1) {
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

               var2 = true;
            }
            break;
         default:
            if (var1 >= 48 && var1 <= 57) {
               long var3 = System.currentTimeMillis();
               if (var3 - this.lastKeyPressedTime < this.delay && 10 * this.value + var1 - 48 <= this.maxValue) {
                  this.setValueImpl(10 * this.value + var1 - 48, true);
               } else if (var1 - 48 <= this.maxValue) {
                  this.setValueImpl(var1 - 48, true);
               }

               var2 = true;
               this.lastKeyPressedTime = var3;
            }
         }

         if (var2 && this.owner != null) {
            this.valueText = this.getText(this.value);
            this.highlightedWidth = Displayable.uistyle.getEditableTextWidth(this.inlineEditZone, this.valueText, 2);
            this.highlightedX = Displayable.uistyle.getEditableTextX(this.inlineEditZone, this.valueText, 2);
            this.owner.repaintRequest();
         }
      }

   }

   boolean hasFocus(int var1) {
      int var2 = this.owner.getTickerHeight();
      int var3 = DeviceInfo.getDisplayHeight(0);
      int var4 = this.getHeight();
      return var3 - var2 - var1 >= var4 && var1 >= 0;
   }

   Command[] getExtraCommands() {
      return this.isInteractive && !isInline ? extraCommands : null;
   }

   boolean launchExtraCommand(Command var1) {
      if (this.isInteractive) {
         this.startGaugeScreen(0, 0);
         return true;
      } else {
         return false;
      }
   }

   private void startGaugeScreen(int var1, int var2) {
      Screen var3 = null;

      try {
         Class var4 = Class.forName("javax.microedition.lcdui.GaugeScreen");
         var3 = (Screen)var4.newInstance();
      } catch (Exception var5) {
      }

      GaugeEditor var6 = (GaugeEditor)var3;
      var6.initialize(this, var1, var2);
      this.owner.myDisplay.setCurrentInternal((Displayable)null, var3);
   }

   private void startKeyRepeatTimer(int var1) {
      if (this.keyRepeatTimer == null) {
         this.keyRepeatTimer = new Timer();
         this.keyRepeatTimerTask = new Gauge.KeyRepeatTimerTask(var1);
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

   private TextLine getText(int var1) {
      StringBuffer var2 = new StringBuffer();
      var2.append(var1);
      TextLine var3 = TextBreaker.breakOneLineTextInZone(this.inlineEditZone, true, true, var2.toString(), 0, false);
      return var3;
   }

   private int roundToValues(int var1) {
      int var2 = 1;
      int var3 = 0;
      int var4 = 0;

      for(int var5 = var1; var2 < 100000000; var2 *= 10) {
         for(int var6 = 0; var6 < roundingValues.length; ++var6) {
            var4 = var3;
            var3 = roundingValues[var6] * var2;
            if (var5 <= var3) {
               if (var5 - var4 < var3 - var5) {
                  var5 = var4;
               } else {
                  var5 = var3;
               }

               return var5;
            }
         }
      }

      return var4;
   }

   private int roundTheInitialValue(boolean var1) {
      int var2;
      if (var1) {
         if (this.value % this.incrementalValue == 0) {
            var2 = this.value + this.incrementalValue;
         } else {
            var2 = this.value - this.value % this.incrementalValue + this.incrementalValue;
         }
      } else if (this.value % this.incrementalValue == 0) {
         var2 = this.value - this.incrementalValue;
      } else {
         var2 = this.value - this.value % this.incrementalValue;
      }

      return var2;
   }

   private void computeIncrementalValue() {
      int var1 = 10;
      if (this.maxValue > 50 && this.maxValue <= 100) {
         var1 = (int)(350L * (long)this.maxValue * 10L / 8000L + 5L);
      } else if (this.maxValue > 100 && this.maxValue <= 1000) {
         var1 = (int)(350L * (long)this.maxValue * 10L / 12000L + 5L);
      } else if (this.maxValue > 1000) {
         var1 = (int)(350L * (long)this.maxValue * 10L / 20000L + 5L);
      }

      this.incrementalValue = this.roundToValues(var1 / 10);
   }

   private void setPixmap() {
      if (this.gaugePixmap != null && this.gaugePixmap.isAnimatedPixmap() && this.value == 2) {
         this.gaugePixmap.stopAnimationTimer();
         this.gaugePixmap.setAnimationListener((AnimationListener)null);
      }

      if (this.maxValue == -1) {
         if (this.value != 3 && this.value != 2) {
            if (this.value == 1 || this.value == 0) {
               this.gaugePixmap = Pixmap.createPixmap(16);
            }
         } else {
            this.gaugePixmap = Pixmap.createPixmap(10);
         }

         if (this.gaugePixmap.isAnimatedPixmap()) {
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
      HIGHLIGHT_COLOUR = UIStyle.COLOUR_HIGHLIGHT;
      roundingValues = new int[]{1, 2, 5};
      UIStyle var10000 = Displayable.uistyle;
      isInline = UIStyle.isInline();
      interactiveBackgroundPixmap = Pixmap.createPixmap(13);
      interactiveNotchPixmap = Pixmap.createPixmap(14);
      interactiveKnobPixmap = Pixmap.createPixmap(15);
   }

   private class KeyRepeatTimerTask extends TimerTask {
      private int keyId;

      KeyRepeatTimerTask(int var2) {
         this.keyId = var2;
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

      public void frameAdvanced(Pixmap var1) {
         synchronized(Display.LCDUILock) {
            if (Gauge.this.gaugePixmap != null && var1 == Gauge.this.gaugePixmap && Gauge.this.owner != null) {
               Gauge.this.owner.repaintFull();
            }

         }
      }

      // $FF: synthetic method
      GaugeAnimationListener(Object var2) {
         this();
      }
   }
}
