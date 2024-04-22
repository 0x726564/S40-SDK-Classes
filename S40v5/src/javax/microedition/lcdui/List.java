package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;

public class List extends Screen implements Choice {
   public static final Command SELECT_COMMAND = new Command("", 1, 0);
   private static List bJ = new List((String)null, 3);
   private static final com.nokia.mid.impl.isa.ui.gdi.Font bK;
   private Command bL;
   private static final int P;
   private int ac;
   private int type;
   private Zone bM;
   private Zone bN;
   private ChoiceHandler ad;

   public List(String var1, int var2) {
      this.bL = SELECT_COMMAND;
      if (var2 != 1 && var2 != 2 && var2 != 3) {
         throw new IllegalArgumentException();
      } else {
         synchronized(Display.hG) {
            this.setTitleImpl(var1);
            this.type = var2;
            this.ac = P;
            this.M();
         }
      }
   }

   public List(String var1, int var2, String[] var3, Image[] var4) {
      this(var1, var2);
      if (var3 == null) {
         throw new NullPointerException();
      } else if (var4 != null && var4.length != var3.length) {
         throw new IllegalArgumentException();
      } else {
         synchronized(Display.hG) {
            this.fh = true;
            Image var7 = null;

            for(int var5 = 0; var5 < var3.length; ++var5) {
               if (var4 != null) {
                  var7 = var4[var5];
               }

               this.append(var3[var5], var7);
            }

         }
      }
   }

   List(String var1, ChoiceHandler var2) {
      this(var1, 3, true);
      synchronized(Display.hG) {
         this.ad = var2;
      }
   }

   private List(String var1, int var2, boolean var3) {
      this(var1, 3);
      synchronized(Display.hG) {
         this.setSystemScreen(true);
      }
   }

   public int size() {
      synchronized(Display.hG) {
         return this.ad != null && this.ad.dx != null ? this.ad.dx.length : 0;
      }
   }

   public String getString(int var1) {
      synchronized(Display.hG) {
         if (this.size() != 0 && var1 >= 0 && var1 < this.ad.dx.length) {
            return this.ad.dx[var1].dM;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public Image getImage(int var1) {
      synchronized(Display.hG) {
         if (this.size() != 0 && var1 >= 0 && var1 < this.ad.dx.length) {
            ChoiceHandler.ChoiceElement var4;
            return (var4 = this.ad.dx[var1]).dO != null ? var4.dO : var4.dN;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public int append(String var1, Image var2) {
      synchronized(Display.hG) {
         boolean var4 = false;
         boolean var5 = false;
         int var7;
         if (this.ad == null) {
            var5 = true;
            this.p();
            var7 = this.ad.append(var1, var2);
            if (this.type != 2) {
               this.ad.dx[0].selected = true;
            }
         } else {
            var7 = this.ad.append(var1, var2);
         }

         if (this.ad.Y()) {
            this.ad.setStarting(this.bM.height);
         } else {
            this.ad.q(this.bM.height);
         }

         if (this.isShown()) {
            if (var5) {
               this.ad.W();
            }

            this.ag();
         }

         return var7;
      }
   }

   public void insert(int var1, String var2, Image var3) {
      synchronized(Display.hG) {
         boolean var5 = false;
         if (this.ad == null) {
            var5 = true;
            this.p();
            this.ad.insert(var1, var2, var3);
            if (this.type != 2) {
               this.ad.dx[0].selected = true;
            }
         } else {
            this.ad.insert(var1, var2, var3);
         }

         if (this.ad.Y()) {
            this.ad.setStarting(this.bM.height);
         } else {
            this.ad.q(this.bM.height);
         }

         if (this.isShown()) {
            if (var5) {
               this.ad.W();
            }

            this.ag();
         }

      }
   }

   public void delete(int var1) {
      synchronized(Display.hG) {
         if (this.ad == null) {
            throw new IndexOutOfBoundsException();
         } else {
            this.ad.delete(var1);
            if (this.ad.dx != null && this.ad.dx.length > 0) {
               if (this.type != 2 && this.getSelectedIndexImpl() == -1) {
                  int var3 = this.ad.dx.length - 1;
                  this.ad.dx[var1 > var3 ? var3 : var1].selected = true;
               }
            } else {
               this.ad.X();
               this.ad = null;
            }

            if (this.ad != null && this.ad.dy <= this.bM.height) {
               this.ad.dz = 0;
            }

            if (this.isShown()) {
               this.ag();
            }

         }
      }
   }

   public void deleteAll() {
      synchronized(Display.hG) {
         if (this.ad != null) {
            this.ad.X();
            this.ad = null;
            if (this.isShown()) {
               this.ag();
            }
         }

      }
   }

   public void set(int var1, String var2, Image var3) {
      synchronized(Display.hG) {
         if (this.ad == null) {
            throw new IndexOutOfBoundsException();
         } else {
            this.ad.set(var1, var2, var3);
            if (this.ad.Y()) {
               this.ad.setStarting(this.bM.height);
            } else {
               this.ad.q(this.bM.height);
            }

            if (this.isShown()) {
               this.ag();
            }

         }
      }
   }

   public boolean isSelected(int var1) {
      synchronized(Display.hG) {
         if (this.size() != 0 && var1 >= 0 && var1 < this.ad.dx.length) {
            return this.ad.dx[var1].selected;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public int getSelectedIndex() {
      synchronized(Display.hG) {
         return this.getSelectedIndexImpl();
      }
   }

   public int getSelectedFlags(boolean[] var1) {
      synchronized(Display.hG) {
         if (this.ad != null) {
            return this.ad.getSelectedFlags(var1);
         } else {
            for(int var3 = 0; var3 < var1.length; ++var3) {
               var1[var3] = false;
            }

            return 0;
         }
      }
   }

   public void setSelectedIndex(int var1, boolean var2) {
      synchronized(Display.hG) {
         if (this.size() != 0 && var1 >= 0 && var1 <= this.ad.dx.length - 1) {
            this.setSelectedIndexImpl(var1, var2);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void setSelectedFlags(boolean[] var1) {
      synchronized(Display.hG) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (this.size() != 0) {
            if (this.type == 2) {
               this.ad.setSelectedFlags(var1);
            } else {
               if (var1.length < this.ad.dx.length) {
                  throw new IllegalArgumentException();
               }

               int var3 = 0;
               boolean var4 = false;

               for(int var5 = 0; var5 < this.ad.dx.length; ++var5) {
                  this.ad.dx[var5].selected = false;
                  if (var1[var5] && !var4) {
                     var4 = true;
                     var3 = var5;
                  }
               }

               this.setSelectedIndexImpl(var3, true);
            }

            if (this.isShown()) {
               this.ag();
            }

         }
      }
   }

   public void removeCommand(Command var1) {
      synchronized(Display.hG) {
         if (var1 == this.bL) {
            this.bL = null;
         } else {
            super.d(var1);
         }

      }
   }

   public void setSelectCommand(Command var1) {
      if (this.type == 3) {
         synchronized(Display.hG) {
            if (var1 == SELECT_COMMAND) {
               this.bL = var1;
               this.c(true);
            } else if (var1 == null) {
               this.bL = null;
               this.c(true);
            } else {
               if (this.bL != SELECT_COMMAND && this.bL != var1) {
                  this.b(this.bL);
               }

               this.bL = var1;
               int var4;
               if ((var4 = this.eW.i(var1)) != -1) {
                  this.eW.D(var4);
               }

               this.c(true);
            }
         }
      }
   }

   public void setFitPolicy(int var1) {
      if (var1 >= 0 && var1 <= 2) {
         synchronized(Display.hG) {
            this.setFitPolicyImpl(var1);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getFitPolicy() {
      return this.ac;
   }

   public void setFont(int var1, Font var2) {
      synchronized(Display.hG) {
         if (this.ad.b(var1, var2)) {
            if (this.ad.Y()) {
               this.ad.setStarting(this.bM.height);
            } else {
               this.ad.q(this.bM.height);
            }

            if (this.isShown()) {
               this.ag();
            }
         }

      }
   }

   public Font getFont(int var1) {
      synchronized(Display.hG) {
         if (this.size() != 0 && var1 >= 0 && var1 < this.ad.dx.length) {
            Font var4;
            return (var4 = this.ad.dx[var1].dQ) == null ? Font.getDefaultFont() : var4;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   final void b(Graphics var1) {
      super.b(var1);
      synchronized(Display.hG) {
         boolean var3 = false;
         if (this.getTicker() != null && this.bM.y >= var1.getClipY() + var1.getClipHeight()) {
            var3 = true;
         } else {
            var1.setClip(this.bM.x, this.bM.y, this.bM.width, this.bM.height);
            if (this.ad == null || this.size() == 0) {
               TextLine var5;
               TextBreaker var6;
               (var5 = (var6 = TextBreaker.getBreaker(bK, TextDatabase.getText(33), false)).getTextLine(this.bM.width)).setAlignment(2);
               ColorCtrl var7;
               int var8 = (var7 = var1.getImpl().getColorCtrl()).getFgColor();
               var7.setFgColor(UIStyle.COLOUR_TEXT);
               var1.getImpl().drawText(var5, (short)((this.bM.x + this.bM.width) / 2), (short)this.bM.y);
               var7.setFgColor(var8);
               var6.destroyBreaker();
               this.c(false);
               var1.setClip(this.bN.x, this.bN.y, this.bN.width, this.bN.height);
               Displayable.eI.drawScrollbar(var1.getImpl(), this.bN, 0, 1, 1, 0, false);
               Displayable.eI.hideIndex();
               var3 = true;
            }
         }

         if (!var3) {
            this.ad.a(this.bM.x, this.bM.y, var1, 0);
            this.c(false);
            var1.setClip(this.bN.x, this.bN.y, this.bN.width, this.bN.height);
            Displayable.eI.drawScrollbar(var1.getImpl(), this.bN, 1, this.size(), 1, this.ad.dw + 1, false);
            Displayable.eI.setIndex(this.ad.dw + 1);
         }

      }
   }

   final void i(int var1, int var2) {
      this.c(var1, var2);
   }

   final void c(int var1, int var2) {
      boolean var4 = false;
      boolean var3;
      synchronized(Display.hG) {
         var3 = this.type == 3 && var1 == -10 && this.size() != 0 && this.fa != null && this.bL != null;
      }

      if (var3) {
         synchronized(Display.hH) {
            this.fa.commandAction(this.bL, this);
         }
      } else {
         switch(var1) {
         case -10:
            boolean var5 = false;
            synchronized(Display.hG) {
               if (this.size() != 0) {
                  var5 = this.ad.dx[this.ad.dw].selected;
                  this.setSelectedIndexImpl(this.ad.dw, !var5);
               }
               break;
            }
         case -2:
            synchronized(Display.hG) {
               if (this.size() != 0) {
                  this.ad.dx[this.ad.dw].dR = false;
                  if (this.type == 3) {
                     this.ad.dx[this.ad.dw].selected = false;
                  }

                  this.ad.s(this.bM.height);
                  var4 = true;
                  if (this.type == 3) {
                     this.ad.dx[this.ad.dw].selected = true;
                  }

                  this.ad.dx[this.ad.dw].dR = true;
               }
               break;
            }
         case -1:
            synchronized(Display.hG) {
               if (this.size() != 0) {
                  this.ad.dx[this.ad.dw].dR = false;
                  if (this.type == 3) {
                     this.ad.dx[this.ad.dw].selected = false;
                  }

                  this.ad.r(this.bM.height);
                  if (this.type == 3) {
                     this.ad.dx[this.ad.dw].selected = true;
                  }

                  this.ad.dx[this.ad.dw].dR = true;
                  var4 = true;
               }
               break;
            }
         case 35:
            synchronized(Display.hG) {
               if (this.size() != 0) {
                  Display var15;
                  TruncatedItemScreen var7 = (var15 = this.eV).getTruncatedItemScreen();
                  ChoiceHandler.ChoiceElement var8 = this.ad.dx[this.ad.dw];
                  var7.a(var15, this, var8.dM, var8.dN, false);
                  break;
               }
            }
         case -7:
         case -6:
         case -5:
         default:
            super.c(var1, var2);
         }
      }

      if (var4) {
         this.ag();
      }

   }

   final void b(Display var1) {
      super.b(var1);
      if (this.size() > 0) {
         this.ad.X();
      }

   }

   final void a(Display var1) {
      super.a(var1);
      if (this.size() > 0) {
         this.ad.dx[this.ad.dw].dR = true;
         this.ad.W();
      }

   }

   final void D() {
      super.D();
      synchronized(Display.hG) {
         this.fh = true;
         this.M();
         if (this.ad != null) {
            this.ad.setMainZone(this.bM);
            if (this.ad.Y()) {
               this.ad.setStarting(this.bM.height);
            }
         }

         if (!(this instanceof OptionsMenu)) {
            this.ag();
         }

      }
   }

   Command[] getExtraCommands() {
      Command[] var1 = null;
      if (this.size() != 0) {
         ChoiceHandler.ChoiceElement[] var2;
         ChoiceHandler.ChoiceElement var3 = (var2 = this.ad.dx)[this.ad.dw];
         if (var2.length != 0) {
            switch(this.type) {
            case 1:
               var1 = new Command[]{ChoiceGroup.L};
               break;
            case 2:
               if (var3.selected) {
                  var1 = new Command[]{ChoiceGroup.N};
               } else {
                  var1 = new Command[]{ChoiceGroup.M};
               }
               break;
            case 3:
               if (this.bL != null) {
                  var1 = new Command[]{this.bL};
               }
            }

            if ((!this.ad.du || this.ad.dv) && var3.isTruncated()) {
               if (var1 != null) {
                  Command[] var4 = new Command[var1.length + 1];
                  System.arraycopy(var1, 0, var4, 0, var1.length);
                  var4[var1.length] = ChoiceGroup.K;
                  var1 = var4;
               } else {
                  var1 = new Command[]{ChoiceGroup.K};
               }
            }
         }
      }

      return var1;
   }

   final boolean a(Command var1) {
      if (this.type == 3 && this.size() != 0 && this.fa != null && this.bL != null && var1 == this.bL) {
         this.eV.a((Command)var1, (Object)this, (Object)this.fa);
      } else {
         if (this.size() == 0) {
            return false;
         }

         ChoiceHandler.ChoiceElement[] var2 = this.ad.dx;
         if (var1 == ChoiceGroup.K) {
            Display var6;
            TruncatedItemScreen var3 = (var6 = this.eV).getTruncatedItemScreen();
            ChoiceHandler.ChoiceElement var4 = this.ad.dx[this.ad.dw];
            var3.a(var6, this, var4.dM, var4.dN, true);
            return true;
         }

         boolean var5 = var2[this.ad.dw].selected;
         this.setSelectedIndexImpl(this.ad.dw, !var5);
      }

      return false;
   }

   Zone getScrollbarZone() {
      return this.eQ != null ? Displayable.eO : Displayable.eM;
   }

   final boolean b(Command var1) {
      return var1 != this.bL ? super.b(var1) : false;
   }

   int getSelectedIndexImpl() {
      return this.type != 2 && this.ad != null ? this.ad.getSelectedIndex() : -1;
   }

   void setSelectedIndexImpl(int var1, boolean var2) {
      ChoiceHandler.ChoiceElement[] var3 = this.ad.dx;
      if (this.type != 2) {
         if (!var2 || var3[var1].selected) {
            return;
         }

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4].selected = false;
         }

         this.ad.setSelectedIndex(var1, var2);
         if (this.type == 3) {
            var3[this.ad.dw].dR = false;
            this.ad.dw = var1;
            var3[var1].dR = true;
            if (var3[var1].dS != 1 && this.ad.Y()) {
               this.ad.setStarting(this.bM.height);
            }
         }
      } else {
         this.ad.setSelectedIndex(var1, var2);
      }

      if (this.isShown()) {
         this.ag();
      }

   }

   void setFitPolicyImpl(int var1) {
      if (this.ac != var1) {
         if (var1 == 0) {
            if (this.ac != P && this.ad != null) {
               this.setWrapping(P == 1);
            }

            this.ac = var1;
            return;
         }

         this.ac = var1;
         if (this.ad != null) {
            this.setWrapping(this.ac == 1);
         }
      }

   }

   private void M() {
      this.bN = this.getScrollbarZone();
      this.bM = this.eQ != null ? eI.getZone(54) : eI.getZone(53);
   }

   private void p() {
      UIStyle var1 = Displayable.eI;
      this.ad = new ChoiceHandler(true, false, this.bM);
      this.ad.du = this.ac == 1 || this.ac == 0 && P == 1;
      if (this.type == 3) {
         this.ad.dm = var1.getZone(17);
         this.ad.dn = null;
         this.ad.do = null;
         this.ad.dp = var1.getZone(18);
         this.ad.dq = var1.getZone(19);
         this.ad.dr = null;
         this.ad.dt = null;
         this.ad.ds = null;
      } else {
         this.ad.dm = var1.getZone(13);
         this.ad.dn = null;
         this.ad.do = var1.getZone(12);
         this.ad.dp = var1.getZone(15);
         this.ad.dq = var1.getZone(16);
         this.ad.dr = var1.getZone(14);
         if (this.type == 1) {
            this.ad.dt = Pixmap.createPixmap(9);
            this.ad.ds = Pixmap.createPixmap(8);
         } else {
            this.ad.dt = Pixmap.createPixmap(7);
            this.ad.ds = Pixmap.createPixmap(6);
         }
      }
   }

   private void setWrapping(boolean var1) {
      this.ad.du = var1;
      if (this.ad.dx != null) {
         this.ad.dx[this.ad.dz].dT = 0;
         this.ad.V();
         if (this.ad.Y()) {
            this.ad.setStarting(this.bM.height);
         }

         if (this.ad.dy <= this.bM.height) {
            this.ad.dz = 0;
            this.ad.dx[this.ad.dz].dT = 0;
         }

         if (this.isShown()) {
            this.ag();
         }
      }

   }

   static void N() {
      bJ.eV = null;
   }

   static {
      synchronized(Display.hG) {
         bJ.setFitPolicyImpl(2);
      }

      InitJALM.s_setMIDletList(bJ);
      bK = Displayable.eI.getZone(17).getFont();
      P = UIStyle.getNumberOfSoftKeys() > 2 ? 2 : 1;
   }
}
