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
   private static final int br = DeviceInfo.getTickerPollInterval();
   private static final int bs = DeviceInfo.getTickerIncrement();
   private Displayable bt = null;
   private Zone bu = null;
   private Timer timer;
   private String bv;
   private TextLine bw = null;
   private int bx;
   private int by;
   private int bz;
   boolean bA = false;
   private boolean bB = true;

   public Ticker(String var1) {
      this.setString(var1);
   }

   public String getString() {
      return this.bv;
   }

   public void setString(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         synchronized(Display.hG) {
            this.setStringImpl(var1);
         }
      }
   }

   final void setStringImpl(String var1) {
      var1 = var1.replace('\n', ' ').replace('\r', '�').replace('\b', '�').replace('\f', '�');
      this.bv = var1;
      if (this.bu == null) {
         this.bu = Displayable.eP;
      }

      this.bw = TextBreaker.breakOneLineTextInZone(this.bu, false, true, var1, 0, true);
      if (this.bw != null) {
         this.by = this.bw.getTextLineWidth();
         this.bz = this.bw.getTextLineHeight();
         this.bB = this.bw.isTextFlowLTR();
      } else {
         this.by = this.bz = 0;
      }

      this.bx = 300000;
      this.J();
   }

   final void a(Displayable var1) {
      this.bt = var1;
      this.bu = this.bt.getTickerZone();
      if (this.timer == null) {
         this.timer = new Timer();
         Ticker.TickerTimerTask var2 = new Ticker.TickerTimerTask(this);
         this.timer.schedule(var2, (long)br, (long)br);
      }
   }

   final void b(Displayable var1) {
      if (var1 == this.bt) {
         this.bt = null;
         this.bu = null;
         if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
            this.bA = false;
         }
      }
   }

   final void paint(Graphics var1) {
      com.nokia.mid.impl.isa.ui.gdi.Graphics var2;
      ColorCtrl var3 = (var2 = var1.getImpl()).getColorCtrl();
      int var4 = this.bu.width;
      int var5 = this.bu.height;
      int var6 = var1.getClipX();
      int var7 = var1.getClipY();
      int var8 = var1.getClipWidth();
      int var9 = var1.getClipHeight();
      var1.clipRect(this.bu.x, this.bu.y, var4, var5);
      var1.a(true, false, false, (Zone)null);
      var1.setClip(var6, var7, var8, var9);
      int var10 = var3.getFgColor();
      var3.setFgColor(UIStyle.COLOUR_TICKER_BACKGROUND);
      var3.setBlendFactor(70);
      var2.fillRect((short)this.bu.x, (short)this.bu.y, (short)var4, (short)var5);
      if (this.bw != null) {
         var6 = this.bB ? -bs : bs;
         var7 = 0;
         boolean var11 = this.bA;
         this.bA = false;
         if (this.bx == 300000) {
            this.bx = this.bB ? var4 : -this.by;
         } else if (var11) {
            this.bx += var6;
            if (this.bB) {
               if (this.bx < -this.by) {
                  this.bx = var4;
               }
            } else if (this.bx >= var4) {
               this.bx = -this.by;
            }
         }

         if (var5 >= this.bz) {
            var7 = (var5 - this.bz) / 2;
         }

         var3.setFgColor(UIStyle.COLOUR_TICKER_TEXT);
         var2.drawText(this.bw, (short)this.bx, (short)(this.bu.y + var7));
      }

      var3.setFgColor(var10);
   }

   final void J() {
      if (this.bt != null) {
         this.bt.d(this.bu.x, this.bu.y, this.bu.width, this.bu.height);
      }

   }

   private class TickerTimerTask extends TimerTask {
      private final Ticker hb;

      public final void run() {
         synchronized(Display.hG) {
            this.hb.bA = true;
            this.hb.J();
         }
      }

      TickerTimerTask(Ticker var1, Object var2) {
         this.hb = var1;
      }
   }
}
