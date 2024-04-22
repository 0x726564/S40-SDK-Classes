package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.sound.SoundDatabase;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import com.nokia.mid.sound.Sound;

class ChoiceHandler {
   Zone dl;
   Zone dm;
   Zone dn;
   Zone do;
   Zone dp;
   Zone dq;
   Zone dr;
   Pixmap ds;
   Pixmap dt;
   boolean du;
   boolean dv;
   int dw;
   ChoiceHandler.ChoiceElement[] dx;
   int dy;
   int dz;
   int dA;
   boolean dB;
   boolean dC;
   private int dD;
   private int dE;
   private Sound dF;

   ChoiceHandler(boolean var1, boolean var2, Zone var3) {
      this.dl = Displayable.eL;
      this.dm = Displayable.eI.getZone(17);
      this.dp = Displayable.eI.getZone(18);
      this.dq = Displayable.eI.getZone(19);
      this.dF = null;
      this.dB = var1;
      this.dC = var2;
      this.dl = var3;
   }

   final void set(int var1, String var2, Image var3) throws NullPointerException, IndexOutOfBoundsException {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         this.t(var1);
         ChoiceHandler.ChoiceElement var5;
         int var4 = (var5 = this.dx[var1]).height;
         if (var3 != null && var5.dN == null && ++this.dD == 1 || var3 == null && var5.dN != null && --this.dD == 0) {
            this.Z();
            this.V();
         }

         this.dE -= var5.isTruncated() ? 1 : 0;
         var5.a(var2, var3);
         this.dE += var5.isTruncated() ? 1 : 0;
         this.dy += var5.height - var4;
      }
   }

   final boolean b(int var1, Font var2) throws IndexOutOfBoundsException {
      if (this.dx != null && var1 >= 0 && var1 < this.dx.length) {
         ChoiceHandler.ChoiceElement var4 = this.dx[var1];
         boolean var3 = false;
         this.dy -= var4.height;
         this.dE -= var4.isTruncated() ? 1 : 0;
         var3 = var4.b(var2);
         this.dE += var4.isTruncated() ? 1 : 0;
         this.dy += var4.height;
         return var3;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   final int append(String var1, Image var2) throws NullPointerException {
      int var3 = this.dx == null ? 0 : this.dx.length;
      this.insert(var3, var1, var2);
      return this.dx.length - 1;
   }

   final void insert(int var1, String var2, Image var3) throws NullPointerException, IndexOutOfBoundsException {
      if (this.dx != null && (var1 < 0 || var1 > this.dx.length) || this.dx == null && var1 != 0) {
         throw new IndexOutOfBoundsException();
      } else if (var2 == null) {
         throw new NullPointerException();
      } else {
         if (var3 != null && ++this.dD == 1) {
            this.Z();
            this.V();
         }

         ChoiceHandler.ChoiceElement var4 = new ChoiceHandler.ChoiceElement(this, var2, var3);
         if (this.dx == null) {
            this.dx = new ChoiceHandler.ChoiceElement[]{var4};
            this.dy = var4.height;
            this.dw = 0;
            this.dx[0].dR = true;
            this.dz = 0;
         } else {
            ChoiceHandler.ChoiceElement[] var5 = new ChoiceHandler.ChoiceElement[this.dx.length + 1];
            System.arraycopy(this.dx, 0, var5, 0, var1);
            System.arraycopy(this.dx, var1, var5, var1 + 1, this.dx.length - var1);
            var5[var1] = var4;
            this.dy += var4.height;
            this.dx = var5;
            this.dw += var1 <= this.dw ? 1 : 0;
         }

         this.dE += var4.isTruncated() ? 1 : 0;
      }
   }

   final void delete(int var1) throws IndexOutOfBoundsException {
      this.t(var1);
      boolean var2 = false;
      ChoiceHandler.ChoiceElement var3;
      if ((var3 = this.dx[var1]).dN != null && --this.dD == 0) {
         this.Z();
         var2 = true;
      }

      if (this.dx.length <= 1) {
         this.dx = null;
         this.dy = 0;
      } else {
         if (var2) {
            this.V();
         }

         this.dE -= var3.isTruncated() ? 1 : 0;
         this.dy -= var3.height;
         this.dz -= this.dz == this.dx.length - 1 ? 1 : 0;
         ChoiceHandler.ChoiceElement[] var4 = new ChoiceHandler.ChoiceElement[this.dx.length - 1];
         System.arraycopy(this.dx, 0, var4, 0, var1);
         System.arraycopy(this.dx, var1 + 1, var4, var1, var4.length - var1);
         this.dx = var4;
         if (var1 == this.dw) {
            this.dw = this.dw > this.dx.length - 1 ? this.dx.length - 1 : this.dw;
            this.dx[this.dw].dR = true;
         } else {
            if (var1 < this.dw) {
               --this.dw;
            }

         }
      }
   }

   int getSelectedIndex() {
      if (this.dx == null) {
         return -1;
      } else {
         for(int var1 = 0; var1 < this.dx.length; ++var1) {
            if (this.dx[var1].selected) {
               return var1;
            }
         }

         return -1;
      }
   }

   final int getSelectedFlags(boolean[] var1) throws NullPointerException, IllegalArgumentException {
      int var2 = 0;
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.dx == null) {
         return 0;
      } else if (var1.length < this.dx.length) {
         throw new IllegalArgumentException();
      } else {
         int var3;
         for(var3 = 0; var3 < var1.length; ++var3) {
            var1[var3] = false;
         }

         for(var3 = 0; var3 < this.dx.length; ++var3) {
            var1[var3] = this.dx[var3].selected;
            var2 += var1[var3] ? 1 : 0;
         }

         return var2;
      }
   }

   void setSelectedIndex(int var1, boolean var2) throws IndexOutOfBoundsException {
      this.t(var1);
      this.dx[var1].selected = var2;
   }

   void setSelectedFlags(boolean[] var1) throws NullPointerException, IllegalArgumentException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.dx != null) {
         if (var1.length < this.dx.length) {
            throw new IllegalArgumentException();
         } else {
            for(int var2 = 0; var2 < this.dx.length; ++var2) {
               this.dx[var2].selected = var1[var2];
            }

         }
      }
   }

   final void a(int var1, int var2, Graphics var3, int var4) {
      var4 = this.dl.height - (var2 - this.dl.y);
      if (this.dx != null) {
         this.dA = 0;

         int var5;
         for(var5 = 0; var5 < this.dx.length; ++var5) {
            this.dx[var5].dS = -1;
         }

         for(var5 = this.dz; var5 < this.dx.length && this.dA <= var4; var5 = this.u(var5) != this.dz && this.u(var5) != 0 ? this.u(var5) : this.dx.length) {
            this.dx[var5].a(var1, var2 + this.dA, var4, var3, false);
         }

      }
   }

   final void q(int var1) {
      int var2 = 0;

      int var3;
      for(var3 = 0; var3 < this.dx.length; ++var3) {
         this.dx[var3].dS = -1;
      }

      for(var3 = this.dz; var3 < this.dx.length && var2 <= var1; var3 = this.u(var3) != this.dz && this.u(var3) != 0 ? this.u(var3) : this.dx.length) {
         var2 = ChoiceHandler.ChoiceElement.a(this.dx[var3], var1, var2);
      }

   }

   void setMainZone(Zone var1) {
      if (this.dx != null && this.dx.length > 0 && (var1.height != this.dl.height || var1.width != this.dl.width)) {
         this.V();
         this.dx[this.dz].dT = 0;
      }

      this.dl = var1;
   }

   void setStarting(int var1) {
      if (this.dx != null) {
         int var3;
         if (this.dy <= var1) {
            this.dz = 0;
            this.dx[this.dz].dT = 0;

            for(var3 = 0; var3 < this.dx.length; ++var3) {
               this.dx[var3].dS = 1;
            }

         } else {
            for(var3 = 0; var3 < this.dx.length; ++var3) {
               this.dx[var3].dS = -1;
            }

            ChoiceHandler.ChoiceElement var2 = null;
            var3 = 0;
            int var4 = 0;
            int var5;
            if (this.u(this.dw) != 0) {
               var5 = this.u(this.dw);
            } else {
               var5 = this.dw;
            }

            this.dx[this.dz].dT = 0;

            for(int var6 = var5; var3 < var1; var6 = this.v(var6)) {
               for(int var7 = (var2 = this.dx[var6]).dU.length - 1; var7 >= 0; --var7) {
                  var3 = var3 + var2.dU[var7].getTextLineHeight() + (var7 == 0 ? this.dm.getMarginTop() : 0) + (var7 == var2.dU.length - 1 ? this.dm.getMarginBottom() : 0);
                  if (var6 != var5 && var7 == var2.dU.length - 1) {
                     var3 += var2.dV;
                  }

                  if (var3 <= var1) {
                     this.dz = var6;
                     var4 = var7;
                     var2.dS = 0;
                  }
               }

               if (var3 <= var1 && var4 == 0) {
                  var2.dS = 1;
               }
            }

            this.dx[this.dz].dT = var4;
         }
      }
   }

   final void V() {
      this.dy = 0;
      if (this.dx != null) {
         for(int var2 = 0; var2 < this.dx.length; ++var2) {
            ChoiceHandler.ChoiceElement var1;
            (var1 = this.dx[var2]).aa();
            this.dy += var1.height;
            this.dE += var1.isTruncated() ? 1 : 0;
         }

      }
   }

   final void W() {
      if (UIStyle.isListEndToneOn()) {
         this.dF = SoundDatabase.getSound(5);
      }

   }

   final void X() {
      if (this.dF != null) {
         this.dF.stop();
         this.dF = null;
      }

   }

   final boolean Y() {
      int var1 = 0;
      boolean var2 = false;
      if (this.dx == null) {
         return false;
      } else {
         int var5 = this.dx[this.dz].dU[0].getTextLineHeight();

         for(int var4 = this.dz; var4 < this.dx.length; var4 = this.u(var4)) {
            ChoiceHandler.ChoiceElement var3 = this.dx[var4];
            int var10001 = var3.dT == 0 ? this.dm.getMarginTop() : 0;
            int var10000 = var1 + var10001;
            var10001 += var1;
            var1 = var10000 + (var3.dU.length - var3.dT) * var5 + this.dm.getMarginBottom() + var3.dV;
            if (var4 == this.dw) {
               break;
            }
         }

         return var1 > this.dl.height;
      }
   }

   private void Z() {
      Zone var1 = this.dm;
      this.dm = this.dp;
      this.dp = var1;
      var1 = this.do;
      this.do = this.dr;
      this.dr = var1;
      var1 = this.dn;
      this.dn = this.dq;
      this.dq = var1;
   }

   final void r(int var1) {
      this.dx[this.dw].dR = false;
      if (this.dB || this.dw != 0) {
         this.dw = this.v(this.dw);
         ChoiceHandler.ChoiceElement var2;
         (var2 = this.dx[this.dw]).dR = true;
         int var3 = this.v(this.dw);
         ChoiceHandler.ChoiceElement var4 = this.dx[var3];
         if (var2.dS != 1 || var4.dS == -1) {
            if (this.dw == this.dx.length - 1) {
               this.setStarting(var1);
            } else {
               if (var3 != this.dx.length - 1) {
                  this.dz = var3;
               } else {
                  this.dz = this.dw;
               }

               var2.dT = 0;
               this.q(var1);
            }
         }

         if (this.dw == this.dx.length - 1 && this.dF != null) {
            this.dF.stop();
            this.dF.play(1);
         }

      }
   }

   final void s(int var1) {
      this.dx[this.dw].dR = false;
      if (this.dB || this.dw != this.dx.length - 1) {
         this.dw = this.u(this.dw);
         ChoiceHandler.ChoiceElement var2;
         (var2 = this.dx[this.dw]).dR = true;
         int var3 = this.u(this.dw);
         ChoiceHandler.ChoiceElement var4 = this.dx[var3];
         if (var2.dS != 1 || var4.dS == -1) {
            this.dx[this.dz].dT = 0;
            if (this.dw == 0) {
               this.dz = this.dw;
               var2.dT = 0;
               this.q(var1);
            } else {
               this.setStarting(var1);
            }
         }

         if (this.dw == 0 && this.dF != null) {
            this.dF.stop();
            this.dF.play(1);
         }

      }
   }

   private void t(int var1) throws IndexOutOfBoundsException {
      if (this.dx == null || var1 < 0 || var1 >= this.dx.length) {
         throw new IndexOutOfBoundsException();
      }
   }

   private int u(int var1) {
      return this.dB ? (var1 + 1) % this.dx.length : var1 + 1;
   }

   private int v(int var1) {
      return this.dB ? (var1 - 1 + this.dx.length) % this.dx.length : var1 - 1;
   }

   class ChoiceElement {
      String dM;
      Image dN;
      Image dO;
      private com.nokia.mid.impl.isa.ui.gdi.Font dP;
      Font dQ;
      boolean selected;
      boolean dR;
      byte dS;
      int dT;
      TextLine[] dU;
      int height;
      int dV;
      private final ChoiceHandler dW;

      ChoiceElement(ChoiceHandler var1, String var2, Image var3) throws NullPointerException {
         this.dW = var1;
         this.dS = -1;
         this.dS = -1;
         this.dP = var1.dm.getFont();
         this.a(var2, var3);
      }

      final void a(String var1, Image var2) throws NullPointerException {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.dM = var1;
            if (var2 != null && var2.isMutable()) {
               this.dO = var2;
               this.dN = Image.createImage(var2);
            } else {
               this.dN = var2;
               this.dO = null;
            }

            this.aa();
         }
      }

      final boolean b(Font var1) {
         boolean var2 = false;
         this.dQ = var1;
         com.nokia.mid.impl.isa.ui.gdi.Font var3 = this.dW.dm.getFont();
         if (var1 == null) {
            if (this.dP.getMIDPSize() != var3.getMIDPSize() || this.dP.getMIDPStyle() != var3.getMIDPStyle()) {
               this.dP = var3;
               var2 = true;
            }
         } else {
            int var4;
            if ((var4 = this.dQ.getStyle() & -5) != this.dP.getMIDPStyle() || this.dQ.getSize() != this.dP.getMIDPSize()) {
               this.dP = new com.nokia.mid.impl.isa.ui.gdi.Font(var3.getMIDPSize(), var4, true);
               var2 = true;
            }
         }

         if (var2) {
            this.aa();
         }

         return var2;
      }

      final void a(int var1, int var2, int var3, Graphics var4, boolean var5) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics var11 = var4.getImpl();
         int var6 = 0;
         int var7 = this.dT;
         this.dS = -1;
         if (this.dW.dl != null && this.dW.dm != null) {
            int var8;
            if (this.dR && !var5) {
               var8 = this.dW.dm.x + this.dW.dl.x;
               int var9 = this.dW.dm.width;
               if (this.dW.do != null) {
                  if (var8 > this.dW.do.x) {
                     var8 = this.dW.do.x;
                  }

                  var9 += this.dW.do.width;
               }

               if (this.dW.dn != null) {
                  if (var8 > this.dW.dn.x) {
                     var8 = this.dW.dn.x;
                  }

                  var9 += this.dW.dn.width;
               }

               int var10 = this.dW.dm.height;

               while(true) {
                  ++var7;
                  if (var7 >= this.dU.length || var10 >= var3) {
                     UIStyle.getUIStyle().drawHighlightBar(var11, var8, var2 + this.dW.dm.y, var9, var10, true);
                     var7 = this.dT;
                     break;
                  }

                  var10 += this.dU[var7].getTextLineHeight();
               }
            }

            ChoiceHandler var10000;
            while(var7 < this.dU.length && this.dW.dA < var3) {
               if ((var8 = this.dU[var7].getTextLineHeight() + (var7 == this.dT ? this.dW.dm.getMarginTop() : 0) + (var7 == this.dU.length - 1 ? this.dW.dm.getMarginBottom() : 0)) + this.dW.dA <= var3) {
                  this.a(var7, var1, var2 + var6, var11, var5);
                  var6 += var8;
                  this.dS = 0;
                  ++var7;
               } else if (this.dW.dC) {
                  this.a(var7, var1, var2 + var6, var11, var5);
                  this.dS = 0;
               }

               var10000 = this.dW;
               var10000.dA += var8;
            }

            this.dS = this.dT == 0 && this.dU.length == var7 ? 1 : this.dS;
            var10000 = this.dW;
            var10000.dA += this.dV;
         }
      }

      final void aa() {
         if (this.dW.dl != null && this.dW.dm != null) {
            int var1 = this.dW.dm.width - (this.dW.dm.getMarginLeft() + this.dW.dm.getMarginRight());
            TextLine var2 = null;
            TextBreaker var3;
            (var3 = TextBreaker.getBreaker()).setFont(this.dP != null ? this.dP : this.dW.dm.getFont());
            var3.setLeading(0, false);
            var3.setText(this.dM.length() > 0 ? this.dM : " ");
            this.dS = -1;
            this.dU = null;
            if (!this.dW.du) {
               var3.setTruncation(true);
               this.dU = new TextLine[]{var3.getTextLine(var1)};
               this.height = this.dU[0].getTextLineHeight() + this.dW.dm.getMarginTop() + this.dW.dm.getMarginBottom();
            } else {
               boolean var4 = true;
               var3.setTruncation(false);

               while((var2 = var3.getTextLine(var1)) != null) {
                  if (var4) {
                     var4 = false;
                     this.dU = new TextLine[]{var2};
                     int[] var10000 = new int[]{0};
                     this.height = var2.getTextLineHeight() + this.dW.dm.getMarginTop();
                  } else {
                     if (this.dU.length < 3) {
                        if (this.dU.length == 2) {
                           this.dW.dv = true;
                        }

                        TextLine[] var5 = new TextLine[this.dU.length + 1];
                        System.arraycopy(this.dU, 0, var5, 0, this.dU.length);
                        var5[this.dU.length] = var2;
                        this.dU = var5;
                        this.height += var2.getTextLineHeight();
                     }

                     if (this.dU.length == 2) {
                        var3.setTruncation(true);
                     }
                  }
               }

               if (this.dU != null && this.dU.length > 0) {
                  this.height += this.dW.dm.getMarginBottom();
               }
            }

            if (this.height < this.dW.dm.height) {
               this.dV = this.dW.dm.height - this.height;
               this.height = this.dW.dm.height;
            }

            var3.destroyBreaker();
            this.dT = 0;
         }
      }

      final boolean isTruncated() {
         return !this.dW.du && this.dU[0].isTruncated() || this.dW.dv && this.dU[this.dU.length - 1].isTruncated();
      }

      private void a(int var1, int var2, int var3, com.nokia.mid.impl.isa.ui.gdi.Graphics var4, boolean var5) {
         if (this.dU[var1] != null && this.dU[var1].getTextLineHeight() > 0) {
            ColorCtrl var11;
            int var6 = (var11 = var4.getColorCtrl()).getFgColor();
            if (this.dR) {
               if (var5) {
                  var11.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
               } else {
                  var11.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
               }
            } else {
               var11.setFgColor(UIStyle.COLOUR_TEXT);
            }

            int var10 = this.dW.dm.x + this.dW.dl.x;
            int var8 = this.dW.dm.y + var3 + (var1 == 0 ? this.dW.dm.getMarginTop() : 0);
            if (UIStyle.isAlignedLeftToRight) {
               var10 += this.dW.dm.getMarginLeft();
               this.dU[var1].setAlignment(1);
            } else {
               var10 += this.dW.dm.width - this.dW.dm.getMarginRight();
               this.dU[var1].setAlignment(3);
            }

            var4.drawText(this.dU[var1], (short)var10, (short)var8, (short)(this.dW.dm.width - (this.dW.dm.getMarginLeft() + this.dW.dm.getMarginRight())));
            var11.setFgColor(var6);
         }

         if (var1 == 0) {
            Pixmap var9 = this.selected ? this.dW.ds : this.dW.dt;
            if (this.dW.do != null && var9 != null) {
               Displayable.eI.drawPixmapInZone(var4, this.dW.do, this.dW.dl.x, var3, var9);
            }

            Image var12 = this.dN;
            if (this.dW.dn == null || var12 == null) {
               return;
            }

            Displayable.eI.drawPixmapInZone(var4, this.dW.dn, this.dW.dl.x, var3, var12.getPixmap());
         }

      }

      static int a(ChoiceHandler.ChoiceElement var0, int var1, int var2) {
         int var10001 = var1;
         var1 = var2;
         int var5 = var10001;
         ChoiceHandler.ChoiceElement var3 = var0;
         var2 = var0.dT;

         int var4;
         for(var0.dS = -1; var2 < var3.dU.length && var1 < var5; var1 += var4) {
            if ((var4 = var3.dU[var2].getTextLineHeight() + (var2 == var3.dT ? var3.dW.dm.getMarginTop() : 0) + (var2 == var3.dU.length - 1 ? var3.dW.dm.getMarginBottom() : 0)) + var1 <= var5) {
               var3.dS = 0;
               ++var2;
            } else if (var3.dW.dC) {
               var3.dS = 0;
            }
         }

         var3.dS = var3.dT == 0 && var3.dU.length == var2 ? 1 : var3.dS;
         return var1 + var3.dV;
      }
   }
}
