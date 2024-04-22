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

   public Ticker(String str) {
      this.setString(str);
   }

   public String getString() {
      return this.tickerString;
   }

   public void setString(String str) {
      if (str == null) {
         throw new NullPointerException();
      } else {
         synchronized(Display.LCDUILock) {
            this.setStringImpl(str);
         }
      }
   }

   final void setStringImpl(String str) {
      str = str.replace('\n', ' ');
      str = str.replace('\r', '�');
      str = str.replace('\b', '�');
      str = str.replace('\f', '�');
      this.tickerString = str;
      if (this.tickerZone == null) {
         this.tickerZone = Displayable.screenTickTickerZone;
      }

      this.tickerText = TextBreaker.breakOneLineTextInZone(this.tickerZone, false, true, str, 0, true);
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

   final void showOn(Displayable newParent) {
      this.parent = newParent;
      this.tickerZone = this.parent.getTickerZone();
      if (this.timer == null) {
         this.timer = new Timer();
         Ticker.TickerTimerTask timerClient = new Ticker.TickerTimerTask();
         this.timer.schedule(timerClient, (long)tickerScrollInterval, (long)tickerScrollInterval);
      }
   }

   final void hideFrom(Displayable displayable) {
      if (displayable == this.parent) {
         this.parent = null;
         this.tickerZone = null;
         if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
            this.incrementTickerPosFlag = false;
         }
      }
   }

   final void paint(Graphics g) {
      com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
      ColorCtrl colorControl = ng.getColorCtrl();
      int zoneW = this.tickerZone.width;
      int zoneH = this.tickerZone.height;
      int clipX = g.getClipX();
      int clipY = g.getClipY();
      int clipW = g.getClipWidth();
      int clipH = g.getClipHeight();
      g.clipRect(this.tickerZone.x, this.tickerZone.y, zoneW, zoneH);
      g.clearClipArea(true, false, false, (Zone)null);
      g.setClip(clipX, clipY, clipW, clipH);
      int originalFgColor = colorControl.getFgColor();
      colorControl.setFgColor(UIStyle.COLOUR_TICKER_BACKGROUND);
      colorControl.setBlendFactor(70);
      ng.fillRect((short)this.tickerZone.x, (short)this.tickerZone.y, (short)zoneW, (short)zoneH);
      if (this.tickerText != null) {
         int increment = this.isTextFlowLTR ? -tickerScrollIncrement : tickerScrollIncrement;
         int offsetY = 0;
         boolean incTicPos = this.incrementTickerPosFlag;
         this.incrementTickerPosFlag = false;
         if (this.offsetX == 300000) {
            this.offsetX = this.isTextFlowLTR ? zoneW : -this.textWidth;
         } else if (incTicPos) {
            this.offsetX += increment;
            if (this.isTextFlowLTR) {
               if (this.offsetX < -this.textWidth) {
                  this.offsetX = zoneW;
               }
            } else if (this.offsetX >= zoneW) {
               this.offsetX = -this.textWidth;
            }
         }

         if (zoneH >= this.textHeight) {
            offsetY = (zoneH - this.textHeight) / 2;
         }

         colorControl.setFgColor(UIStyle.COLOUR_TICKER_TEXT);
         ng.drawText(this.tickerText, (short)this.offsetX, (short)(this.tickerZone.y + offsetY));
      }

      colorControl.setFgColor(originalFgColor);
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
      TickerTimerTask(Object x1) {
         this();
      }
   }
}
