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
   private static Command bO = new Command(9, 7);
   private static int bP = -1;
   private static int bQ = 1;
   private static int bR = 2;
   private static final Command[] bS = new Command[0];
   private static final Command[] bT;
   private static final Command[] bU;
   private static final Command[] bV;
   private Displayable bW;
   private int bX;
   private String bY;
   Pixmap bZ;
   private Image ca;
   private Gauge cb;
   private boolean cc;
   private boolean cd;
   private AlertType ce;
   private int cf;
   private boolean cg;
   private Vector ch;
   private int ci;
   private int cj;
   private Timer ck;
   Alert.TimerClient cl;
   private Zone cm;
   private Zone cn;
   private Zone co;
   private Zone cp;
   private Zone cq;
   private boolean cr;
   private String cs;

   public Alert(String var1) {
      this(var1, (String)null, (Image)null, (AlertType)null);
   }

   public Alert(String var1, String var2, Image var3, AlertType var4) {
      super(var1);
      this.bW = null;
      this.bX = 5000;
      this.bY = null;
      this.bZ = null;
      this.ca = null;
      this.cb = null;
      this.cc = false;
      this.cd = false;
      this.ce = null;
      this.cf = bP;
      this.cg = false;
      this.ch = new Vector();
      this.ci = 1;
      this.cj = 0;
      synchronized(Display.hG) {
         this.M();
         this.ce = var4;
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
      synchronized(Display.hG) {
         return this.cc ? null : this.ca;
      }
   }

   public Gauge getIndicator() {
      return this.cb;
   }

   public String getString() {
      synchronized(Display.hG) {
         return this.cd ? null : this.bY;
      }
   }

   public int getTimeout() {
      synchronized(Display.hG) {
         return this.getTimeoutImpl();
      }
   }

   public AlertType getType() {
      return this.ce;
   }

   public void setCommandListener(CommandListener var1) {
      if (var1 == null) {
         var1 = new Alert.AlertCommandListener(this);
      }

      super.setCommandListener((CommandListener)var1);
   }

   public void setImage(Image var1) {
      synchronized(Display.hG) {
         this.setImageImpl(var1);
      }
   }

   public void setIndicator(Gauge var1) {
      synchronized(Display.hG) {
         this.setIndicatorImpl(var1);
      }
   }

   public void setString(String var1) {
      synchronized(Display.hG) {
         this.setStringImpl(var1);
      }
   }

   public void setTimeout(int var1) {
      if (var1 <= 0 && var1 != -2) {
         throw new IllegalArgumentException();
      } else {
         synchronized(Display.hG) {
            this.setTimeoutImpl(var1);
         }
      }
   }

   public void setType(AlertType var1) {
      synchronized(Display.hG) {
         this.setTypeImpl(var1);
      }
   }

   final boolean e(Command var1) {
      this.Q();
      this.cr = true;
      if (var1 == null) {
         var1 = DISMISS_COMMAND;
         if (this.eW.ar()) {
            var1 = this.eW.getCommand(0);
         }
      }

      if (this.fa instanceof Alert.AlertCommandListener) {
         this.eV.c((Displayable)null, this.bW);
         return true;
      } else {
         this.eV.a((Command)var1, (Object)this, (Object)this.fa);
         return false;
      }
   }

   void setReturnScreen(Displayable var1) {
      this.bW = var1;
   }

   final void O() {
      if (this.ck != null) {
         this.Q();
         this.P();
      }

   }

   final void c(Display var1) {
      if (this.ce != null) {
         this.ce.h(var1);
         this.ce.playSound(var1);
      }

   }

   final boolean b(Command var1) {
      boolean var2;
      if (var2 = super.b(var1)) {
         this.b(false);
      }

      return var2;
   }

   final boolean d(Command var1) {
      boolean var2;
      if (var2 = super.d(var1)) {
         this.b(false);
      }

      return var2;
   }

   void setImageImpl(Image var1) {
      Pixmap var2 = this.bZ;
      this.ca = var1;
      if (var1 == null) {
         this.cc = true;
         if (this.eU && this.ce != null) {
            this.bZ = this.ce.getPixmap();
         } else {
            this.bZ = null;
         }
      } else {
         this.cc = false;
         if (var1.isMutable()) {
            this.bZ = new Pixmap(var1.getPixmap());
            this.bZ.setMutable(false);
         } else {
            this.bZ = var1.getPixmap();
         }
      }

      this.R();
      if (var2 != null) {
         var2.stopAnimationTimer();
      }

      if (this.bZ != null && this.bZ.isAnimatedPixmap()) {
         this.bZ.resetAnimation();
         this.bZ.setAnimationListener(new Alert.AlertAnimationListener(this));
         if (this.eU) {
            this.bZ.startAnimationTimer();
         }
      }

      this.invalidate();
   }

   void setIndicatorImpl(Gauge var1) {
      if (var1 != null) {
         if (var1.isInteractive() || var1.au != null || var1.aG.length() != 0 || var1.getItemCommandListener() != null || var1.getLabel() != null || var1.getLayout() != 0 || var1.ax != -1 || var1.ay != -1) {
            throw new IllegalArgumentException();
         }

         var1.setOwner(this);
      }

      if (this.cb != null) {
         this.cb.setOwner((Screen)null);
      }

      this.cb = var1;
      this.invalidate();
   }

   void setStringImpl(String var1) {
      if (var1 == null) {
         this.cd = true;
         this.bY = this.getSystemText();
      } else {
         this.bY = var1;
         this.cd = false;
      }

      this.R();
      this.invalidate();
   }

   void setTypeImpl(AlertType var1) {
      if (this.ce != var1) {
         this.ce = var1;
         if (this.cc) {
            this.setImageImpl((Image)null);
         }

         if (this.cd) {
            this.setStringImpl((String)null);
         }

         this.R();
      }
   }

   void setTimeoutImpl(int var1) {
      this.bX = var1;
      this.cr = false;
      this.Q();
      this.b(true);
   }

   int getTimeoutImpl() {
      if (!this.fh) {
         this.layout();
      }

      return this.isModal() ? -2 : this.bX;
   }

   void setLastPageText(String var1) {
      this.cs = var1;
   }

   final void layout() {
      boolean var1 = this.bZ != null || this.cc && this.ce != null;
      super.layout();
      this.M();
      this.ch.removeAllElements();
      Vector var2 = new Vector();
      TextBreaker.breakTextInArea(var1 ? this.cm.width : this.cn.width, this.cb != null ? this.cm.height : this.cn.height, TextBreaker.NBR_OF_AREAS_AS_NEEDED, this.cm.getFont(), this.bY, TextBreaker.DEFAULT_TEXT_LEADING, false, false, var2, false, true);
      this.cj = 0;
      this.ci = var2.size() > 0 && var2.elementAt(0) instanceof Vector ? var2.size() : 1;
      int var3;
      if (this.ci > 1) {
         for(var3 = 0; var3 < this.ci; ++var3) {
            this.ch.addElement(var2.elementAt(var3));
         }
      } else {
         this.ch.addElement(var2);
      }

      if (this.cs != null) {
         var2 = new Vector();
         TextBreaker.breakTextInArea(var1 ? this.cm.width : this.cn.width, this.cb != null ? this.cm.height : this.cn.height, TextBreaker.NBR_OF_AREAS_AS_NEEDED, this.cm.getFont(), this.cs, TextBreaker.DEFAULT_TEXT_LEADING, false, false, var2, false, true);
         int var10000 = var2.size() > 0 && var2.elementAt(0) instanceof Vector ? var2.size() : 1;
         var3 = var10000;
         if (var10000 > 1) {
            for(int var4 = 0; var4 < var3; ++var4) {
               this.ch.addElement(var2.elementAt(var4));
            }
         } else {
            this.ch.addElement(var2);
         }

         this.ci += var3;
      }

      this.b(true);
   }

   Command[] getExtraCommands() {
      boolean var1 = this.ci > 1 && !super.eW.as();
      if (this.cf == bR) {
         if (this.eW.length() == 0 && var1) {
            return bV;
         } else if (this.eW.length() == 0) {
            return bT;
         } else {
            return var1 ? bU : bS;
         }
      } else {
         return bS;
      }
   }

   final boolean a(Command var1) {
      if (var1 == bO) {
         this.cj = (this.cj + 1) % this.ci;
         this.ag();
      } else if (var1 == DISMISS_COMMAND) {
         return this.e(var1);
      }

      return false;
   }

   final void c(int var1, int var2) {
      boolean var3 = false;
      boolean var4 = true;
      synchronized(Display.hG) {
         if (!this.isModal() && (!UIStyle.isRotator() || var1 != -2 && var1 != -1)) {
            var4 = false;
            this.e((Command)null);
         } else if (this.ci > 1 && var1 == -2) {
            this.cj = (this.cj + 1) % this.ci;
            var3 = true;
         } else if (var1 == -1) {
            this.cj = (this.cj + this.ci - 1) % this.ci;
            var3 = true;
         }
      }

      if (var4) {
         super.c(var1, var2);
      }

      if (var3) {
         this.ag();
      }

   }

   final void a(Display var1) {
      super.a(var1);
      synchronized(Display.hG) {
         this.cr = false;
         if (!this.fh) {
            this.layout();
         }

         this.cj = 0;
         this.c(this.eV);
         if (this.cb != null) {
            this.cb.o();
         }

         if (this.cc && this.ce != null) {
            this.bZ = this.ce.getPixmap();
         }

         if (this.bZ != null && this.bZ.isAnimatedPixmap()) {
            this.bZ.resetAnimation();
            this.bZ.setAnimationListener(new Alert.AlertAnimationListener(this));
            this.bZ.startAnimationTimer();
         }

         DeviceControl.switchOnBacklightForDefaultPeriod();
      }
   }

   final void b(Display var1) {
      super.b(var1);
      synchronized(Display.hG) {
         if (this.ce != null) {
            this.ce.h(this.eV);
         }

         if (!this.isModal()) {
            this.Q();
         }

         if (this.cb != null) {
            this.cb.x();
         }

         if (this.bZ != null) {
            if (this.bZ.isAnimatedPixmap()) {
               this.bZ.stopAnimationTimer();
               this.bZ.setAnimationListener((AnimationListener)null);
            }

            if (this.cc) {
               this.bZ = null;
            }
         }

      }
   }

   final void d(Display var1) {
      this.bW = null;
   }

   final void b(Graphics var1) {
      super.b(var1);
      synchronized(Display.hG) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics var3 = var1.getImpl();
         if (this.getTicker() == null || var1.getClipY() + var1.getClipHeight() >= this.cp.y) {
            eI.drawBorder(var3, this.cp);
            if (this.cg) {
               eI.drawPixmapInArea(var3, this.cp.x + this.cn.x, this.cp.y + this.cn.y, this.cn.width, this.cb == null ? this.cn.height : this.cm.height, this.bZ);
            } else if (this.bZ != null) {
               eI.drawPixmapInArea(var3, this.co.x + this.cp.x, this.co.y + this.cp.y, this.co.width, this.co.height, this.bZ);
            }

            if (this.ch.size() > 0 && !this.cg) {
               boolean var8 = var3.getTextTransparency();
               var3.setTextTransparency(true);
               Vector var9 = this.ci == 1 ? (Vector)this.ch.elementAt(0) : (Vector)this.ch.elementAt(this.cj);
               int var4;
               int var6;
               if (this.bZ != null || this.cc && this.ce != null) {
                  var4 = this.cm.x + this.cp.x;
                  var6 = this.cm.width;
               } else {
                  var4 = this.cn.x + this.cp.x;
                  var6 = this.cn.width;
               }

               int var5;
               int var7;
               if (this.cb == null) {
                  var5 = this.cn.y + this.cp.y;
                  var7 = this.cn.height;
               } else {
                  var5 = this.cm.y + this.cp.y;
                  var7 = this.cm.height;
               }

               ColorCtrl var10;
               int var11 = (var10 = var3.getColorCtrl()).getFgColor();
               var10.setFgColor(UIStyle.COLOUR_NOTE_TEXT);
               var3.drawTextInArea(var4, var5, var6, var7, var9, UIStyle.isAlignedLeftToRight ? 1 : 3);
               var10.setFgColor(var11);
               var3.setTextTransparency(var8);
            }

            if (this.cb != null) {
               var1.translate(this.cp.x, this.cq.y + this.cp.y);
               this.cb.a(var1, this.cq.width, this.cq.height, false);
            }

            if (bR == this.cf) {
               this.Q();
            } else {
               this.P();
            }

         }
      }
   }

   final void D() {
      super.D();
      synchronized(Display.hG) {
         if (!this.fh) {
            this.layout();
         }
      }

      this.ag();
   }

   Zone getMainZone() {
      return this.eQ != null ? eI.getZone(31) : eI.getZone(30);
   }

   private String getSystemText() {
      byte var1 = 21;
      if (this.ce != null) {
         switch(this.ce.type) {
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

   private void M() {
      if (this.getTicker() != null) {
         this.cp = eI.getZone(31);
      } else {
         this.cp = eI.getZone(30);
      }

      this.co = eI.getZone(32);
      this.cq = eI.getZone(33);
      this.cm = eI.getZone(34);
      this.cn = eI.getZone(35);
   }

   final boolean isModal() {
      return bR == this.cf;
   }

   private void P() {
      int var1;
      if (this.ck == null && !this.cr && (var1 = this.getTimeoutImpl()) > 0) {
         this.ck = new Timer();
         this.eV.a(this.ck);
         this.cl = new Alert.TimerClient(this);
         this.ck.schedule(this.cl, (long)var1);
      }

   }

   private void Q() {
      if (this.ck != null) {
         this.ck.cancel();
         this.eV.b(this.ck);
         this.ck = null;
         this.cl = null;
      }

   }

   private void b(boolean var1) {
      if (!this.fh) {
         this.layout();
      } else {
         this.cf = bQ;
         if (this.ci > 1 || this.bX == -2 || this.eW.length() > 1) {
            this.cf = bR;
         }

         if (this.eU && this.cf == bQ) {
            this.P();
         } else if (this.eU && this.cf == bR) {
            this.Q();
         }

         if (var1) {
            this.c(true);
         }

      }
   }

   private void R() {
      this.cg = false;
      if (!this.cc && this.cd && this.ce == null && this.bZ != null && (this.bZ.getWidth() > this.co.width || this.bZ.getHeight() > this.co.height)) {
         this.cg = true;
      }

   }

   static {
      bT = new Command[]{DISMISS_COMMAND};
      bU = new Command[]{bO};
      bV = new Command[]{bO, DISMISS_COMMAND};
   }

   private class AlertCommandListener implements CommandListener {
      private final Alert cv;

      public void commandAction(Command var1, Displayable var2) {
         synchronized(Display.hG) {
            this.cv.e(var1);
         }
      }

      AlertCommandListener(Alert var1, Object var2) {
         this.cv = var1;
      }
   }

   private class AlertAnimationListener implements AnimationListener {
      private final Alert cv;

      public void frameAdvanced(Pixmap var1) {
         synchronized(Display.hG) {
            if (this.cv.bZ != null && var1 == this.cv.bZ) {
               this.cv.ag();
            }

         }
      }

      AlertAnimationListener(Alert var1, Object var2) {
         this.cv = var1;
      }
   }

   private class TimerClient extends TimerTask {
      private final Alert cv;

      public final void run() {
         synchronized(Display.hG) {
            if (this == this.cv.cl && this.cv.eU) {
               this.cv.e((Command)null);
            }

         }
      }

      TimerClient(Alert var1, Object var2) {
         this.cv = var1;
      }
   }
}
