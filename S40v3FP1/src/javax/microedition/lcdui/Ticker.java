package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import java.util.Timer;
import java.util.TimerTask;

public class Ticker {
   private static final int UNINITIALISED_OFFSET = 300000;
   private static final int tickerScrollInterval = DeviceInfo.getTickerPollInterval();
   private static final int tickerScrollIncrement = DeviceInfo.getTickerIncrement();
   private Displayable parent = null;
   private Zone tickerZone = null;
   private Timer timer;
   private String tickerString;
   private TextLine tickerText = null;
   private int offsetX;
   private int textWidth;
   private int textHeight;
   boolean incrementTickerPosFlag = false;
   private boolean isTextFlowLTR = true;

   public Ticker(String var1) {
      this.setString(var1);
   }

   public String getString() {
      return this.tickerString;
   }

   public void setString(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         synchronized(Display.LCDUILock) {
            this.setStringImpl(var1);
         }
      }
   }

   final void setStringImpl(String var1) {
      var1 = var1.replace('\n', ' ');
      var1 = var1.replace('\r', '�');
      var1 = var1.replace('\b', '�');
      var1 = var1.replace('\f', '�');
      this.tickerString = var1;
      if (this.tickerZone == null) {
         this.tickerZone = Displayable.screenTickTickerZone;
      }

      this.tickerText = TextBreaker.breakOneLineTextInZone(this.tickerZone, false, true, var1, 0, true);
      if (this.tickerText != null) {
         this.textWidth = this.tickerText.getTextLineWidth();
         this.textHeight = this.tickerText.getTextLineHeight();
         this.isTextFlowLTR = this.tickerText.isTextFlowLTR();
      } else {
         this.textWidth = this.textHeight = 0;
      }

      this.offsetX = 300000;
      this.repaintTicker();
   }

   final void showOn(Displayable var1) {
      this.parent = var1;
      this.tickerZone = this.parent.getTickerZone();
      if (this.timer == null) {
         this.timer = new Timer();
         Ticker.TickerTimerTask var2 = new Ticker.TickerTimerTask();
         this.timer.schedule(var2, (long)tickerScrollInterval, (long)tickerScrollInterval);
      }
   }

   final void hideFrom(Displayable var1) {
      if (var1 == this.parent) {
         this.parent = null;
         this.tickerZone = null;
         if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
            this.incrementTickerPosFlag = false;
         }
      }
   }

   final void paint(Graphics var1) {
      com.nokia.mid.impl.isa.ui.gdi.Graphics var2 = var1.getImpl();
      ColorCtrl var3 = var2.getColorCtrl();
      int var4 = this.tickerZone.width;
      int var5 = this.tickerZone.height;
      int var6 = var1.getClipX();
      int var7 = var1.getClipY();
      int var8 = var1.getClipWidth();
      int var9 = var1.getClipHeight();
      var1.clipRect(this.tickerZone.x, this.tickerZone.y, var4, var5);
      var1.clearScreen(true, false);
      var1.setClip(var6, var7, var8, var9);
      int var10 = var3.getFgColor();
      var3.setFgColor(UIStyle.COLOUR_TICKER_BACKGROUND);
      var3.setBlendFactor(70);
      var2.fillRect((short)this.tickerZone.x, (short)this.tickerZone.y, (short)var4, (short)var5);
      if (this.tickerText != null) {
         int var11 = this.isTextFlowLTR ? -tickerScrollIncrement : tickerScrollIncrement;
         int var12 = 0;
         boolean var13 = this.incrementTickerPosFlag;
         this.incrementTickerPosFlag = false;
         if (this.offsetX == 300000) {
            this.offsetX = this.isTextFlowLTR ? var4 : -this.textWidth;
         } else if (var13) {
            this.offsetX += var11;
            if (this.isTextFlowLTR) {
               if (this.offsetX < -this.textWidth) {
                  this.offsetX = var4;
               }
            } else if (this.offsetX >= var4) {
               this.offsetX = -this.textWidth;
            }
         }

         if (var5 >= this.textHeight) {
            var12 = (var5 - this.textHeight) / 2;
         }

         var3.setFgColor(UIStyle.COLOUR_TICKER_TEXT);
         var2.drawText(this.tickerText, (short)this.offsetX, (short)(this.tickerZone.y + var12));
      }

      var3.setFgColor(var10);
   }

   final void repaintTicker() {
      if (this.parent != null) {
         this.parent.repaintArea(this.tickerZone.x, this.tickerZone.y, this.tickerZone.width, this.tickerZone.height);
      }

   }

   private class TickerTimerTask extends TimerTask {
      private TickerTimerTask() {
      }

      public final void run() {
         synchronized(Display.LCDUILock) {
            Ticker.this.incrementTickerPosFlag = true;
            Ticker.this.repaintTicker();
         }
      }

      // $FF: synthetic method
      TickerTimerTask(Object var2) {
         this();
      }
   }
}
