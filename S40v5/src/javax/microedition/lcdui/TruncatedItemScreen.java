package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import java.util.Vector;

final class TruncatedItemScreen extends Screen {
   private static final TruncatedItemScreen hu = new TruncatedItemScreen();
   private static final Command hv = new Command(10, 3);
   private static final Command hw = new Command(9, 7);
   private static final Command[] hx;
   private static final Command[] hy;
   private boolean hz = false;
   private boolean hA = false;
   private int hB = 0;
   private Vector B = new Vector();
   private Image hC = null;
   private Zone hD = null;
   private Zone hE = null;

   private TruncatedItemScreen() {
      this.setSystemScreen(true);
   }

   static TruncatedItemScreen getTruncatedItemScreen() {
      return hu;
   }

   final void a(Display var1, Screen var2, String var3, Image var4, boolean var5) {
      this.hz = var5;
      this.B.removeAllElements();
      this.hB = 0;
      if (var4 == null) {
         this.hD = eI.getZone(42);
         this.hE = null;
      } else {
         this.hD = eI.getZone(43);
         this.hE = eI.getZone(44);
      }

      this.hC = var4;
      TextBreaker.breakTextInZone(this.hD, var5 ? TextBreaker.NBR_OF_AREAS_AS_NEEDED : 1, var3, TextBreaker.DEFAULT_TEXT_LEADING, false, false, this.B, false, true);
      this.hA = this.B.elementAt(0) instanceof Vector;
      this.setTitleImpl(var2.getTitle());
      this.c(false);
      if (var1 != null) {
         var1.c(var2, this);
      }

   }

   private boolean aD() {
      if (this.eU) {
         this.eV.c((Displayable)null, this.getParentDisplayable());
         return true;
      } else {
         return false;
      }
   }

   private void J(int var1) {
      if (this.hz && this.hA) {
         this.hB = (this.hB + var1) % this.B.size();
         if (this.hB < 0) {
            this.hB += this.B.size();
         }

         this.ag();
      }
   }

   final void b(Display var1) {
      super.b(var1);
      synchronized(Display.hG) {
         if (!this.hz && !var1.aE()) {
            this.eV.c((Displayable)null, this.getParentDisplayable());
         }

      }
   }

   final void d(Display var1) {
      this.setTitleImpl((String)null);
      this.B.removeAllElements();
      this.B.trimToSize();
      this.hC = null;
      this.hD = null;
      this.hE = null;
   }

   final void b(Graphics var1) {
      super.b(var1);
      synchronized(Display.hG) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics var5 = var1.getImpl();
         Vector var3 = this.B;
         if (this.hA) {
            var3 = (Vector)var3.elementAt(this.hB);
         }

         if (this.hC != null && this.hB == 0) {
            eI.drawPixmapInZone(var5, this.hE, 0, 0, this.hC.gw);
         }

         var5.getColorCtrl().setFgColor(UIStyle.COLOUR_TEXT);
         var5.drawTextInZone(this.hD, 0, 0, var3, UIStyle.isAlignedLeftToRight ? 1 : 3);
      }
   }

   final void c(int var1, int var2) {
      super.c(var1, var2);
      if (this.hz) {
         synchronized(Display.hG) {
            switch(var1) {
            case -2:
               this.J(1);
               break;
            case -1:
               this.J(-1);
            }

         }
      }
   }

   final void h(int var1, int var2) {
      super.h(var1, var2);
      if (!this.hz && var1 == 35) {
         synchronized(Display.hG) {
            this.aD();
         }
      }
   }

   final Command[] getExtraCommands() {
      if (this.hz) {
         return this.hA && this.B.size() > 1 ? hy : hx;
      } else {
         return null;
      }
   }

   final boolean a(Command var1) {
      if (var1 == hv) {
         return this.aD();
      } else {
         if (var1 == hw) {
            this.J(1);
         }

         return false;
      }
   }

   static {
      hx = new Command[]{hv};
      hy = new Command[]{hw, hv};
   }
}
