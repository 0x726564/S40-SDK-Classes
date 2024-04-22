package javax.microedition.lcdui;

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
   private static Command fT = new Command(9, 29);
   private static Command[] bd;
   private static final Zone fU;
   private static final Zone fV;
   private static final Zone fW;
   private static final Zone fX;
   private static final Zone fY;
   private static final boolean fZ;
   public static final int INDEFINITE = -1;
   public static final int CONTINUOUS_IDLE = 0;
   public static final int INCREMENTAL_IDLE = 1;
   public static final int CONTINUOUS_RUNNING = 2;
   public static final int INCREMENTAL_UPDATING = 3;
   private boolean ga = false;
   private int gb = 0;
   private int value = -2;
   private boolean gc = true;
   private TextLine gd = null;
   private Pixmap ge;
   private int gf;
   private int gg;
   private Gauge.KeyRepeatTimerTask gh;
   private long gi = 1000L;
   private Zone gj = null;
   private Timer gk;
   private long gl;
   private static final int[] gm;
   private int gn = 0;
   private int go = 1;
   private static final boolean r;
   private static Pixmap gp;
   private static Pixmap gq;
   private static Pixmap gr;

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

      synchronized(Display.hG) {
         this.gc = var2;
         this.gb = var3;
         this.gj = fW;
         this.setValueImpl(var4, false);
         this.setMaxValueImpl(var3);
      }
   }

   public int getMaxValue() {
      return this.gb;
   }

   public int getValue() {
      return this.value;
   }

   public boolean isInteractive() {
      return this.gc;
   }

   public void setMaxValue(int var1) {
      synchronized(Display.hG) {
         if (this.gc) {
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
      synchronized(Display.hG) {
         if (this.gc || this.gb != -1 || var1 >= 0 && var1 <= 3) {
            this.setValueImpl(var1, false);
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public void setLabel(String var1) {
      if (this.au instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setLabel(var1);
      }
   }

   public void setLayout(int var1) {
      if (this.au instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setLayout(var1);
      }
   }

   public void addCommand(Command var1) {
      if (this.au instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.addCommand(var1);
      }
   }

   public void setItemCommandListener(ItemCommandListener var1) {
      if (this.au instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setItemCommandListener(var1);
      }
   }

   public void setPreferredSize(int var1, int var2) {
      if (this.au instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setPreferredSize(var1, var2);
      }
   }

   public void setDefaultCommand(Command var1) {
      if (this.au instanceof Alert) {
         throw new IllegalStateException("Gauge contained within an Alert");
      } else {
         super.setDefaultCommand(var1);
      }
   }

   final boolean d() {
      if (super.d()) {
         return true;
      } else {
         return (this.av & 16384) != 16384;
      }
   }

   final boolean e() {
      if (super.e()) {
         return true;
      } else {
         return (this.av & 16384) != 16384;
      }
   }

   void setMaxValueImpl(int var1) {
      if (var1 > 0) {
         if (this.gb > 0) {
            if (this.value > var1) {
               this.value = var1;
            }
         } else {
            this.value = 0;
         }
      }

      if (!this.gc && var1 == -1 && this.gb > 0) {
         this.value = 0;
      }

      this.gb = var1;
      int var2 = 10;
      if (this.gb > 50 && this.gb <= 100) {
         var2 = (int)(350L * (long)this.gb * 10L / 8000L + 5L);
      } else if (this.gb > 100 && this.gb <= 1000) {
         var2 = (int)(350L * (long)this.gb * 10L / 12000L + 5L);
      } else if (this.gb > 1000) {
         var2 = (int)(350L * (long)this.gb * 10L / 20000L + 5L);
      }

      var1 = var2 / 10;
      var2 = 1;
      int var3 = 0;
      int var4 = 0;
      var1 = var1;

      int var10001;
      label72:
      while(true) {
         if (var2 >= 100000000) {
            var10001 = var4;
            break;
         }

         for(int var5 = 0; var5 < gm.length; ++var5) {
            var4 = var3;
            var3 = gm[var5] * var2;
            if (var1 <= var3) {
               if (var1 - var4 < var3 - var1) {
                  var1 = var4;
               } else {
                  var1 = var3;
               }

               var10001 = var1;
               break label72;
            }
         }

         var2 *= 10;
      }

      this.go = var10001;
      this.aq();
      if (this.gc) {
         if (this.gj != fW && this.gb >= 1000) {
            this.gj = fW;
            this.invalidate();
         } else if (this.gj != fV && this.gb < 1000) {
            this.gj = fV;
            this.invalidate();
         }

         this.gd = this.getText(this.value);
         this.gf = Displayable.eI.getEditableTextWidth(this.gj, this.gd, 2);
         this.gg = Displayable.eI.getEditableTextX(this.gj, this.gd, 2);
      }

      if (this.au != null) {
         this.au.ag();
      }

   }

   void setValueImpl(int var1, boolean var2) {
      if (this.gb > 0) {
         if (var1 < 0) {
            var1 = 0;
         } else if (var1 > this.gb) {
            var1 = this.gb;
         }
      }

      if (this.au instanceof Form && this.value != var1 && var2) {
         this.au.b(this);
      }

      if (this.value != var1) {
         this.value = var1;
         this.aq();
      } else if (this.gb == -1 && this.value == 3 && this.ge != null) {
         this.ge.advanceFrame();
      }

      if (this.gc) {
         this.gd = this.getText(this.value);
         this.gf = Displayable.eI.getEditableTextWidth(this.gj, this.gd, 2);
         this.gg = Displayable.eI.getEditableTextX(this.gj, this.gd, 2);
      }

      if (this.au != null) {
         this.au.ag();
      }

   }

   final void a(Graphics var1, int var2, int var3, boolean var4) {
      super.a(var1, var2, var3, var4);
      synchronized(Display.hG) {
         this.ga = var4;
         com.nokia.mid.impl.isa.ui.gdi.Graphics var24 = var1.getImpl();
         int var5 = var1.getTranslateX();
         int var6 = var1.getTranslateY();
         if (this.au instanceof Form) {
            var6 += TextBreaker.DEFAULT_TEXT_LEADING;
         }

         int var8;
         com.nokia.mid.impl.isa.ui.gdi.Graphics var10;
         int var11;
         if (this.gc) {
            Zone var9 = fU;
            var10 = var1.getImpl();
            var11 = var1.getTranslateX() + var9.x;
            var8 = var1.getTranslateY() + var9.y;
            int var12 = 0;
            int var25 = (var9.width - gp.getWidth()) / 2;
            var11 += var25;
            if (this.au instanceof Form) {
               var12 = 0 + TextBreaker.DEFAULT_TEXT_LEADING + 2;
            }

            var10.drawPixmap(gp, var11, var8);
            var25 = this.gb <= 10 ? this.gb + 1 : 5;
            int var13;
            int var14 = ((var13 = gp.getWidth()) - gq.getWidth() << 5) / (var25 - 1);

            for(int var21 = 1; var21 < var25 - 1; ++var21) {
               int var22 = var11 + (var14 * var21 >> 5);
               var10.drawPixmap(gq, var22, var8);
            }

            long var28 = (long)this.value * (long)(var13 - gq.getWidth()) / (long)this.gb;
            var10.drawPixmap(gr, var11 + (int)var28, var8);
            ColorCtrl var26;
            var13 = (var26 = var10.getColorCtrl()).getFgColor();
            var14 = var26.getBgColor();
            com.nokia.mid.impl.isa.ui.gdi.Font var15 = var10.getFont();
            var10.setFont(fX.getFont());
            var26.setFgColor(UIStyle.COLOUR_TEXT);
            var26.setBgColor(UIStyle.COLOUR_BACKGROUND);
            var10.drawTextLine(fX, var11, var8 + var12, TextBreaker.breakOneLineTextInZone(fX, "0"), 1);
            var10.drawTextLine(fY, 0 - var11, var8 + var12, TextBreaker.breakOneLineTextInZone(fY, Integer.toString(this.gb)), 3);
            var26.setFgColor(var13);
            var26.setBgColor(var14);
            var10.setFont(var15);
            Displayable.eI.drawEditableTextWithBorder(var24, this.gj, var5, var6, this.gd, fV.getBorderType(), 2, this.ga);
         } else {
            if (!(this.au instanceof Alert) && this.isFocusable()) {
               UIStyle.getUIStyle().drawBorder(var24, var5, var6, fU.width, fU.height, fV.getBorderType(), this.ga);
            }

            var10 = var1.getImpl();
            var11 = var1.getTranslateX();
            var8 = var1.getTranslateY();
            boolean var27 = false;
            if (this.au instanceof Form) {
               var8 += TextBreaker.DEFAULT_TEXT_LEADING;
               var27 = true;
            }

            if (this.gb == -1) {
               if (this.ge != null) {
                  var10.drawPixmap(this.ge, (short)((fU.width - this.ge.getWidth()) / 2 + var11), (short)(fU.y + fU.getMarginTop() + var8));
               } else {
                  Displayable.eI.drawGauge(var10, fU, var11, var8, 0, this.gb, var4, var27);
               }
            } else {
               Displayable.eI.drawGauge(var10, fU, var11, var8, this.value, this.gb, var4, var27);
            }
         }

      }
   }

   final void o() {
      super.o();
      synchronized(Display.hG) {
         this.gn = 0;
         if (this.ge != null && this.ge.isAnimatedPixmap()) {
            this.ge.resetAnimation();
            if (this.value == 2) {
               this.ge.setAnimationListener(new Gauge.GaugeAnimationListener(this));
               this.ge.startAnimationTimer();
            }
         }

      }
   }

   final void x() {
      super.x();
      synchronized(Display.hG) {
         this.ab();
         this.ap();
         this.gn = 0;
      }
   }

   void setOwner(Screen var1) {
      super.setOwner(var1);
      synchronized(Display.hG) {
         if (var1 == null) {
            this.ab();
         }

      }
   }

   private void ab() {
      if (this.ge != null && this.ge.isAnimatedPixmap()) {
         this.ge.stopAnimationTimer();
         this.ge.setAnimationListener((AnimationListener)null);
      }

   }

   int getHeight() {
      synchronized(Display.hG) {
         int var2;
         if (null != this.gd && this.isInteractive()) {
            var2 = this.gj.y + this.gj.height;
         } else {
            var2 = fU.height;
         }

         if (this.au instanceof Form) {
            var2 += TextBreaker.DEFAULT_TEXT_LEADING;
         }

         return var2;
      }
   }

   final int a(int var1) {
      return fU.width;
   }

   final int b(int var1) {
      return this.getHeight();
   }

   final int a() {
      return fU.width;
   }

   final int b() {
      return this.getHeight();
   }

   final int s() {
      return this.gc ? this.gf : this.ar[2];
   }

   final int t() {
      return this.gc ? this.gg + this.ar[0] : this.ar[0];
   }

   final boolean isFocusable() {
      return this.gc || this.aG.ar();
   }

   void setFocus(boolean var1) {
      if (this.gc || this.aG.ar()) {
         this.ga = true;
      }

   }

   final boolean n() {
      return true;
   }

   final void c(int var1, int var2) {
      synchronized(Display.hG) {
         if (this.gc) {
            if (var1 != -3 && var1 != -4) {
               if (this.gn == 0) {
                  if (!r && this.au instanceof Form) {
                     this.s(var1, var2);
                  } else {
                     this.C(var1);
                  }
               }
            } else if (this.gn == 0) {
               if (!r && this.au instanceof Form) {
                  this.s(var1, var2);
               } else {
                  this.gn = 1;
                  if (this.gk == null) {
                     this.gk = new Timer();
                     this.gh = new Gauge.KeyRepeatTimerTask(this, var1);
                     this.gk.schedule(this.gh, 350L, 350L);
                  }

                  this.C(var1);
               }
            }
         }

      }
   }

   final void i(int var1, int var2) {
      this.c(var1, var2);
   }

   final void h(int var1, int var2) {
      synchronized(Display.hG) {
         if (var1 == -3 || var1 == -4) {
            this.gn = 0;
            this.ap();
         }

      }
   }

   private void C(int var1) {
      boolean var2 = false;
      if (this.gc) {
         switch(var1) {
         case -4:
         case -1:
            if (fZ && -1 == var1) {
               break;
            }

            this.gl = 0L;
            if (this.value < this.gb) {
               if (this.gn == 2) {
                  this.setValueImpl(this.d(true), true);
               } else if (this.gn == 3) {
                  this.setValueImpl(this.value + this.go, true);
               } else {
                  this.setValueImpl(this.value + 1, true);
               }

               var2 = true;
            }
            break;
         case -3:
         case -2:
            if (fZ && -2 == var1) {
               break;
            }

            this.gl = 0L;
            if (this.value > 0) {
               if (this.gn == 2) {
                  this.setValueImpl(this.d(false), true);
               } else if (this.gn == 3) {
                  this.setValueImpl(this.value - this.go, true);
               } else {
                  this.setValueImpl(this.value - 1, true);
               }

               var2 = true;
            }
            break;
         default:
            if (var1 >= 48 && var1 <= 57) {
               long var3;
               if ((var3 = System.currentTimeMillis()) - this.gl < this.gi && 10 * this.value + var1 - 48 <= this.gb) {
                  this.setValueImpl(10 * this.value + var1 - 48, true);
               } else if (var1 - 48 <= this.gb) {
                  this.setValueImpl(var1 - 48, true);
               }

               var2 = true;
               this.gl = var3;
            }
         }

         if (var2 && this.au != null) {
            this.gd = this.getText(this.value);
            this.gf = Displayable.eI.getEditableTextWidth(this.gj, this.gd, 2);
            this.gg = Displayable.eI.getEditableTextX(this.gj, this.gd, 2);
            this.au.ag();
         }
      }

   }

   Command[] getExtraCommands() {
      return this.gc && !r ? bd : null;
   }

   final boolean a(Command var1) {
      if (this.gc) {
         this.s(0, 0);
         return true;
      } else {
         return false;
      }
   }

   private void s(int var1, int var2) {
      Screen var4 = null;

      try {
         var4 = (Screen)Class.forName("javax.microedition.lcdui.GaugeScreen").newInstance();
      } catch (Exception var3) {
      }

      this.au.eV.c((Displayable)null, var4);
   }

   private void ap() {
      if (this.gk != null) {
         this.gk.cancel();
         this.gh = null;
         this.gk = null;
      }

   }

   private TextLine getText(int var1) {
      StringBuffer var2;
      (var2 = new StringBuffer()).append(var1);
      return TextBreaker.breakOneLineTextInZone(this.gj, true, true, var2.toString(), 0, false);
   }

   private int d(boolean var1) {
      int var2;
      if (var1) {
         if (this.value % this.go == 0) {
            var2 = this.value + this.go;
         } else {
            var2 = this.value - this.value % this.go + this.go;
         }
      } else if (this.value % this.go == 0) {
         var2 = this.value - this.go;
      } else {
         var2 = this.value - this.value % this.go;
      }

      return var2;
   }

   private void aq() {
      if (this.ge != null && this.ge.isAnimatedPixmap() && this.value == 2) {
         this.ge.stopAnimationTimer();
         this.ge.setAnimationListener((AnimationListener)null);
      }

      if (this.gb == -1) {
         if (this.value != 3 && this.value != 2) {
            if (this.value == 1 || this.value == 0) {
               this.ge = Pixmap.createPixmap(17);
            }
         } else {
            this.ge = Pixmap.createPixmap(10);
         }

         if (this.ge != null && this.ge.isAnimatedPixmap()) {
            this.ge.resetAnimation();
            if (this.value == 2) {
               this.ge.setAnimationListener(new Gauge.GaugeAnimationListener(this));
               this.ge.startAnimationTimer();
            }
         }
      }

   }

   static Pixmap a(Gauge var0) {
      return var0.ge;
   }

   static void a(Gauge var0, int var1) {
      Gauge var2;
      switch((var2 = var0).gn) {
      case 1:
         var2.gn = 2;
         break;
      case 2:
         var2.gn = 3;
      case 3:
         break;
      default:
         var2.ap();
         return;
      }

      var2.C(var1);
   }

   static {
      bd = new Command[]{fT};
      fU = Displayable.eI.getZone(22);
      fV = Displayable.eI.getZone(25);
      fW = Displayable.eI.getZone(26);
      fX = Displayable.eI.getZone(23);
      fY = Displayable.eI.getZone(24);
      fZ = UIStyle.isFourWayScroll();
      gm = new int[]{1, 2, 5};
      r = UIStyle.isInline();
      gp = Pixmap.createPixmap(14);
      gq = Pixmap.createPixmap(15);
      gr = Pixmap.createPixmap(16);
   }

   private class KeyRepeatTimerTask extends TimerTask {
      private int iM;
      private final Gauge a;

      KeyRepeatTimerTask(Gauge var1, int var2) {
         this.a = var1;
         this.iM = var2;
      }

      public final void run() {
         synchronized(Display.hG) {
            Gauge.a(this.a, this.iM);
         }
      }
   }

   private class GaugeAnimationListener implements AnimationListener {
      private final Gauge a;

      public void frameAdvanced(Pixmap var1) {
         synchronized(Display.hG) {
            if (Gauge.a(this.a) != null && var1 == Gauge.a(this.a) && this.a.au != null) {
               this.a.au.ag();
            }

         }
      }

      GaugeAnimationListener(Gauge var1, Object var2) {
         this.a = var1;
      }
   }
}
