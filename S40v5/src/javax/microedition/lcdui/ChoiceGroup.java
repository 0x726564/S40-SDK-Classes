package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;

public class ChoiceGroup extends Item implements Choice {
   static final Command K = new Command(12, 43);
   static final Command L = new Command(9, 9);
   static final Command M = new Command(9, 10);
   static final Command N = new Command(9, 11);
   private static Command O = new Command(9, 42);
   private static final int P = UIStyle.getNumberOfSoftKeys() > 2 ? 2 : 1;
   private static final Zone Q;
   private static final Zone R;
   private static final Zone S;
   private static final Zone T;
   private static final Zone U;
   private static final Zone V;
   private static final Pixmap W;
   private static final Pixmap X;
   private static final Zone Y;
   private static final Zone Z;
   private static final Zone aa;
   private static final int ab;
   private int ac;
   private int type;
   ChoiceHandler ad;
   private boolean p;
   private boolean ae;
   List af;
   private ChoiceHandler.ChoiceElement ag;
   private int ah;
   int ai;
   private ChoiceGroup.PopupListListener aj;

   public ChoiceGroup(String var1, int var2) {
      super(var1);
      this.ac = P;
      if (var2 != 1 && var2 != 2 && var2 != 4) {
         throw new IllegalArgumentException("Illegal type for creating a ChoiceGroup or a List.");
      } else {
         synchronized(Display.hG) {
            this.type = var2;
            this.ac = P;
            if (this.type == 4) {
               this.aj = new ChoiceGroup.PopupListListener(this);
            }

         }
      }
   }

   public ChoiceGroup(String var1, int var2, String[] var3, Image[] var4) {
      this(var1, var2);
      if (var3 == null) {
         throw new NullPointerException("ChoiceGroup: stringElements==null.");
      } else if (var4 != null && var4.length != var3.length) {
         throw new IllegalArgumentException("ChoiceGroup: length of stringElements and imageElements are not the same.");
      } else {
         synchronized(Display.hG) {
            for(var2 = 0; var2 < var3.length; ++var2) {
               this.append(var3[var2], var4 == null ? null : var4[var2]);
            }

         }
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
         int var6;
         if (this.af != null) {
            var6 = this.af.append(var1, var2);
         } else if (this.size() == 0) {
            this.p();
            var6 = this.ad.append(var1, var2);
            if (this.type != 2) {
               this.ad.dx[0].selected = true;
            }
         } else {
            var6 = this.ad.append(var1, var2);
         }

         this.ae = true;
         if (this.type != 4 || this.size() == 1) {
            this.invalidate();
         }

         return var6;
      }
   }

   public void insert(int var1, String var2, Image var3) {
      synchronized(Display.hG) {
         if (this.af != null) {
            this.af.insert(var1, var2, var3);
         } else if (this.size() == 0) {
            this.p();
            this.ad.insert(var1, var2, var3);
            if (this.type != 2) {
               this.ad.dx[0].selected = true;
            }
         } else {
            this.ad.insert(var1, var2, var3);
         }

         this.ae = true;
         if (this.type != 4 || this.size() == 1) {
            this.invalidate();
         }

      }
   }

   public void delete(int var1) {
      synchronized(Display.hG) {
         if (this.size() != 0 && var1 >= 0 && var1 < this.ad.dx.length) {
            int var3;
            label62: {
               var3 = this.ad.dw;
               if (this.af != null) {
                  this.af.delete(var1);
               } else {
                  this.ad.delete(var1);
                  if (this.ad.dx == null || this.ad.dx.length <= 0) {
                     this.ad = null;
                     break label62;
                  }

                  if (this.type != 2 && this.getSelectedIndex() == -1) {
                     int var4 = this.ad.dx.length - 1;
                     this.ad.dx[var1 > var4 ? var4 : var1].selected = true;
                  }
               }

               this.ad.dx[this.ad.dw].dR = this.hasFocus;
            }

            this.ae = true;
            if (this.type != 4 || var1 == var3 || this.ad == null) {
               this.ag = null;
               this.invalidate();
            }

         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void deleteAll() {
      synchronized(Display.hG) {
         if (this.af != null) {
            this.af.deleteAll();
            this.af = null;
         }

         this.ad = null;
         this.ag = null;
         this.invalidate();
      }
   }

   public void set(int var1, String var2, Image var3) {
      synchronized(Display.hG) {
         if (this.size() != 0 && var1 >= 0 && var1 < this.ad.dx.length) {
            if (this.af != null) {
               this.af.set(var1, var2, var3);
            } else {
               this.ad.set(var1, var2, var3);
            }

            if (var1 != this.ad.dw) {
               this.ae = true;
            }

            if (this.type != 4 || var1 == this.ad.dw) {
               if (this.type == 4) {
                  this.r();
               }

               this.invalidate();
            }

         } else {
            throw new IndexOutOfBoundsException();
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
         return this.type != 2 && this.size() != 0 ? this.ad.getSelectedIndex() : -1;
      }
   }

   public int getSelectedFlags(boolean[] var1) {
      synchronized(Display.hG) {
         if (this.size() != 0) {
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
            if (this.af != null) {
               this.af.setSelectedIndex(var1, var2);
            } else {
               if (this.type != 2) {
                  if (!var2 || this.ad.dx[var1].selected) {
                     return;
                  }

                  for(int var4 = 0; var4 < this.ad.dx.length; ++var4) {
                     this.ad.dx[var4].selected = false;
                  }
               }

               this.ad.setSelectedIndex(var1, var2);
               this.repaint();
            }

            if (this.type == 4) {
               this.ad.dx[this.ad.dw].dR = false;
               this.ad.dw = var1;
               this.ad.dx[var1].dR = true;
               this.r();
               this.invalidate();
            }

         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void setSelectedFlags(boolean[] var1) {
      synchronized(Display.hG) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (this.ad != null && this.ad.dx != null) {
            if (this.type == 2) {
               this.ad.setSelectedFlags(var1);
               this.repaint();
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

               this.setSelectedIndex(var3, true);
            }

         }
      }
   }

   public void setFitPolicy(int var1) {
      if (var1 >= 0 && var1 <= 2) {
         synchronized(Display.hG) {
            if (this.af != null) {
               this.af.setFitPolicy(var1);
            } else {
               if (this.ac != var1) {
                  if (var1 == 0) {
                     if (this.ac != P && this.ad != null) {
                        this.setWrapping(P == 1);
                     }

                     this.ac = var1;
                  } else {
                     this.ac = var1;
                     if (this.ad != null) {
                        this.setWrapping(this.ac == 1);
                     }
                  }

                  if (this.type == 4) {
                     this.r();
                  }

                  this.invalidate();
               }

            }
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
         if (this.af != null) {
            this.af.setFont(var1, var2);
         } else {
            this.ad.b(var1, var2);
         }

         this.ae = var1 != this.ad.dw ? true : this.ae;
         if (this.type != 4 || var1 == this.ad.dw) {
            if (this.type == 4) {
               this.r();
            }

            this.invalidate();
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

   final boolean isFocusable() {
      return this.aG != null && this.aG.length() > 0 || this.size() > 0;
   }

   Command[] getExtraCommands() {
      Command[] var1 = null;
      if (this.size() == 0) {
         return null;
      } else {
         ChoiceHandler.ChoiceElement var2 = this.ad.dx[this.ad.dw];
         switch(this.type) {
         case 1:
            var1 = new Command[]{L};
            break;
         case 2:
            var1 = var2.selected ? new Command[]{N} : new Command[]{M};
         case 3:
         default:
            break;
         case 4:
            var1 = new Command[]{O};
         }

         if ((!this.ad.du || this.ad.dv) && (this.type == 4 && this.ag != null && this.ag.isTruncated() || this.type != 4 && var2.isTruncated())) {
            if (var1 != null) {
               Command[] var3 = new Command[var1.length + 1];
               System.arraycopy(var1, 0, var3, 0, var1.length);
               var3[var1.length] = K;
               var1 = var3;
            } else {
               var1 = new Command[]{K};
            }
         }

         return var1;
      }
   }

   final boolean a(Command var1) {
      if (this.size() == 0) {
         return false;
      } else {
         ChoiceHandler.ChoiceElement[] var2 = this.ad.dx;
         if (var1.equals(K)) {
            Display var6;
            TruncatedItemScreen var4 = (var6 = this.au.eV).getTruncatedItemScreen();
            ChoiceHandler.ChoiceElement var5 = this.ad.dx[this.ad.dw];
            var4.a(var6, this.au, var5.dM, var5.dN, true);
            return true;
         } else {
            boolean var3;
            if (var1.equals(O)) {
               if (this.af == null) {
                  this.af = new List(this.au.getTitle(), this.ad);
                  this.af.addCommand(new Command(2, 3));
                  this.af.setCommandListener(this.aj);
               }

               this.getLabelHeight(this.au.getMainZone().width);
               var3 = false;
               this.ai = this.ad.dw;
               this.au.eV.c(this.au, this.af);
               return true;
            } else {
               var3 = var2[this.ad.dw].selected;
               this.setSelectedIndex(this.ad.dw, !var3);
               this.au.b(this);
               this.au.c(true);
               if (this.au.isShown()) {
                  this.au.ag();
               }

               return false;
            }
         }
      }
   }

   final boolean n() {
      return this.type == 4;
   }

   final void c(int var1, int var2) {
      synchronized(Display.hG) {
         if (this.size() != 0) {
            int var3 = this.ad.dw;
            switch(var1) {
            case -10:
               if (this.type != 4) {
                  boolean var8 = false;
                  var8 = this.ad.dx[var3].selected;
                  this.setSelectedIndex(var3, !var8);
                  this.au.b(this);
                  this.au.c(true);
               } else {
                  if (this.af == null) {
                     this.af = new List(this.au.getTitle(), this.ad);
                     this.af.addCommand(new Command(2, 3));
                     this.af.setCommandListener(this.aj);
                  }

                  this.getLabelHeight(this.au.getMainZone().width);
                  this.ai = this.ad.dw;
                  this.au.eV.c(this.au, this.af);
               }
               break;
            case -4:
               if (this.type == 4) {
                  if ((var1 = this.getSelectedIndex() + 1) >= this.size()) {
                     var1 = 0;
                  }

                  this.setSelectedIndex(var1, true);
                  this.au.b(this);
               }
               break;
            case -3:
               if (this.type == 4) {
                  if ((var1 = this.getSelectedIndex() - 1) < 0) {
                     var1 = this.size() - 1;
                  }

                  this.setSelectedIndex(var1, true);
                  this.au.b(this);
               }
               break;
            case 35:
               Display var6;
               TruncatedItemScreen var4 = (var6 = this.au.eV).getTruncatedItemScreen();
               ChoiceHandler.ChoiceElement var7 = this.ad.dx[var3];
               var4.a(var6, this.au, var7.dM, var7.dN, false);
            }

         }
      }
   }

   final int b() {
      return this.b(0);
   }

   final int a() {
      return this.a(-1);
   }

   final void a(Graphics var1, int var2, int var3, boolean var4) {
      super.a(var1, var2, var3, var4);
      synchronized(Display.hG) {
         int var6 = var1.getTranslateX();
         int var7 = var1.getTranslateY();
         this.hasFocus = var4;
         var3 -= this.getLabelHeight(-1);
         if (this.isFocusable() && var3 != 0) {
            if (this.type == 4) {
               int var13 = var3 - 2 * ab;
               Zone var14 = this.au.getMainZone();
               Displayable.eI.drawBorder(var1.getImpl(), var6, var7, var2, var3, V.getBorderType(), this.hasFocus);
               Displayable.eI.drawPixmapInZone(var1.getImpl(), T, var14.x, var7, W);
               Displayable.eI.drawPixmapInZone(var1.getImpl(), U, var14.x, var7, X);
               if (this.size() == 0) {
                  this.a(var1, Q.x + var6, var7 + Q.y, Q.width - 1, var13, this.hasFocus);
               } else {
                  this.a(true);
                  this.q();
                  this.ad.dA = 0;
                  this.ad.dx[this.ad.dw].dR = this.hasFocus;
                  this.ag.dR = this.hasFocus;
                  this.ag.a(var6, var7, var13, var1, true);
                  this.a(false);
               }
            } else {
               Zone var16 = this.au.getMainZone();
               if (this.size() == 0) {
                  this.a(var1, var16.x, var7, var2, var3, this.hasFocus);
               } else {
                  this.ad.dz = 0;
                  this.ad.dx[0].dT = 0;
                  this.ad.dx[this.ad.dw].dR = this.hasFocus;
                  this.ad.a(var6, var7, var1, 0);
               }
            }

         }
      }
   }

   final int b(int var1) {
      boolean var3 = false;
      if (!this.isFocusable()) {
         return 0;
      } else {
         Zone var10000;
         if (this.type == 4) {
            this.q();
            var1 = (this.ag == null ? 0 : this.ag.height) + 2 * ab;
            var10000 = Q;
         } else {
            var1 = (this.ad == null ? 0 : this.ad.dy) + (this.size() == 0 ? 2 : 0);
            var10000 = this.au == null ? Displayable.eL : this.au.getMainZone();
         }

         int var2 = var10000.width;
         if (this.size() == 0) {
            var1 += this.a(var2, (Font)null) + 1;
         }

         return var1;
      }
   }

   final int a(int var1) {
      var1 = this.au == null ? Displayable.eL.width : this.au.getWidth();
      if (this.type == 4) {
         var1 = V.width;
      }

      return !this.isFocusable() && this.label == null ? 0 : var1;
   }

   final boolean a(int var1, int var2, int var3, int[] var4) {
      super.a(var1, var2, var3, var4);
      synchronized(Display.hG) {
         int var5 = this.getLabelHeight(this.au.getMainZone().width);
         boolean var6 = false;
         int var12;
         boolean var15;
         boolean var10000;
         if (!this.p) {
            this.ae = false;
            if (this.size() == 0) {
               if (this.isFocusable()) {
                  var12 = this.b(-1) + var5;
                  var4[1] = 0;
                  var4[3] = var3 < var12 ? var3 : var12;
                  this.p = true;
                  this.repaint();
                  var10000 = true;
               } else {
                  var10000 = false;
               }
            } else {
               var15 = false;
               this.ad.setMainZone(this.au.getMainZone());
               this.ad.dx[this.ad.dw].dR = false;
               ChoiceHandler.ChoiceElement var17;
               if (this.type == 4) {
                  var17 = this.ag;
                  this.ag.dR = true;
                  var12 = var17.height + 2 * ab;
               } else {
                  ChoiceHandler.ChoiceElement[] var16 = this.ad.dx;
                  this.ad.dw = var1 == 6 ? 0 : this.ad.dw;
                  this.ad.dw = var1 == 1 ? var16.length - 1 : this.ad.dw;
                  var17 = null;
                  var12 = this.ad.dy;
               }

               if (var1 == 6) {
                  var4[1] = 0;
                  var4[3] = var3 < var12 + var5 ? var3 : var12 + var5;
               }

               if (var1 == 1) {
                  if (var3 >= var12 + var5) {
                     var4[1] = 0;
                     var4[3] = var12 + var5;
                  } else {
                     var4[1] = var12 + var5 - var3;
                     var4[3] = var3;
                  }
               }

               this.ad.dx[this.ad.dw].dR = true;
               this.repaint();
               var10000 = true;
            }

            var6 = var10000;
            this.p = true;
            if (var6) {
               this.au.c(true);
            }

            return var6;
         } else {
            ChoiceHandler.ChoiceElement[] var10;
            ChoiceHandler.ChoiceElement var11;
            ChoiceGroup var14;
            switch(var1) {
            case 0:
               if (this.size() == 0) {
                  if (this.isFocusable()) {
                     var1 = this.b(-1) + var5;
                     var4[1] = 0;
                     var4[3] = var3 < var1 ? var3 : var1;
                     this.repaint();
                     this.au.c(true);
                     return true;
                  }

                  return false;
               }

               if (this.type == 4) {
                  this.q();
                  this.ag.dR = true;
                  (var11 = this.ag).dU[0].getTextLineHeight();
                  this.ae = false;
                  var4[1] = 0;
                  var4[3] = var5 + var11.height + 2 * ab < var3 ? var5 + var11.height + 2 * ab : var3;
                  this.repaint();
               } else {
                  var14 = this;
                  var12 = var5;
                  (var11 = (var10 = this.ad.dx)[this.ad.dw]).dU[0].getTextLineHeight();
                  this.ad.setMainZone(this.au.getMainZone());

                  for(var1 = 0; var1 < var14.ad.dw; ++var1) {
                     var12 += var10[var1].height;
                  }

                  var1 = var12 + var11.height + var11.dV;
                  if (var4[1] > var12) {
                     var4[1] = var12;
                     var4[3] = var5 + var14.ad.dy - var4[1];
                     if (var4[3] > var3) {
                        var4[3] = var3;
                     }
                  } else if (var4[1] + var4[3] < var1) {
                     if (var3 < var1) {
                        var4[1] = var1 - var3;
                        var4[3] = var3;
                     } else {
                        var4[1] = 0;
                        var4[3] = var1;
                     }
                  }

                  var14.ae = false;
                  var14.repaint();
               }

               var6 = true;
               break;
            case 1:
               if (this.size() == 0) {
                  return false;
               }

               if (this.type != 4) {
                  var14 = this;
                  var15 = false;
                  var12 = 0;
                  (var11 = (var10 = this.ad.dx)[this.ad.dw]).dU[0].getTextLineHeight();
                  this.ad.setMainZone(this.au.getMainZone());

                  for(var1 = var10.length - 1; var1 > var14.ad.dw; --var1) {
                     var12 += var10[var1].height;
                  }

                  if (var14.ad.dw > 0) {
                     var12 += var11.height;
                     var11.dR = false;
                     (var11 = var10[--var14.ad.dw]).dR = true;
                     var12 += var11.height;
                     if (var5 + var14.ad.dy <= var3) {
                        var4[1] = 0;
                        var4[3] = var5 + var14.ad.dy;
                     } else {
                        if ((var1 = var5 + var14.ad.dy - var12) < var4[1]) {
                           var4[1] = var1;
                        }

                        var4[3] = var3;
                     }

                     var14.repaint();
                     var10000 = true;
                  } else if (var5 != 0 && var4[1] != 0) {
                     var4[1] = 0;
                     var4[3] = var5 + var14.ad.dy <= var3 ? var5 + var14.ad.dy : var3;
                     var14.repaint();
                     var10000 = true;
                  } else {
                     var10000 = false;
                  }

                  var6 = var10000;
               }
               break;
            case 6:
               if (this.size() == 0) {
                  return false;
               }

               if (this.type != 4) {
                  var14 = this;
                  var12 = var5;
                  (var11 = (var10 = this.ad.dx)[this.ad.dw]).dU[0].getTextLineHeight();
                  this.ad.setMainZone(this.au.getMainZone());
                  if (this.ad.dw >= var10.length - 1) {
                     var10000 = false;
                  } else {
                     var11.dR = false;
                     (var11 = var10[++this.ad.dw]).dR = true;

                     for(var1 = 0; var1 < var14.ad.dw; ++var1) {
                        var12 += var10[var1].height;
                     }

                     if (var12 + var11.height <= var3) {
                        var4[3] = var5 + var14.ad.dy < var3 ? var5 + var14.ad.dy : var3;
                     } else {
                        if (var12 + var11.height > var4[1] + var3) {
                           var4[1] = var12 + var11.height - var3;
                        }

                        var4[3] = var3;
                     }

                     var14.repaint();
                     var10000 = true;
                  }

                  var6 = var10000;
               }
               break;
            default:
               var6 = false;
            }

            var4[3] = var4[3] < var3 ? var4[3] : var3;
            if (var6) {
               this.au.c(true);
            }

            return var6;
         }
      }
   }

   final void f() {
      super.f();
      if (this.size() != 0) {
         this.ad.dx[this.ad.dw].dR = false;
      }

      this.p = false;
   }

   final void o() {
      super.o();
      if (this.size() > 0 && !this.hasFocus) {
         this.ad.dx[this.ad.dw].dR = false;
      }

   }

   final boolean c() {
      return (this.label == null || "".equals(this.label)) && this.size() == 0 && (this.aG == null || this.aG.length() <= 0);
   }

   void setOwner(Screen var1) {
      super.setOwner(var1);
      synchronized(Display.hG) {
         if (this.au != null) {
            this.ax = this.au.getMainZone().width;
            if (this.ad != null) {
               this.ad.setMainZone(this.au.getMainZone());
            }
         } else {
            this.ax = -1;
         }

      }
   }

   public void setPreferredSize(int var1, int var2) {
      if (var1 < -1 || var2 < -1) {
         throw new IllegalArgumentException();
      }
   }

   private void p() {
      UIStyle var1 = Displayable.eI;
      this.ad = new ChoiceHandler(false, true, Displayable.eL);
      this.ad.du = this.ac == 1 || this.ac == 0 && P == 1;
      if (this.type == 4) {
         this.ad.dm = var1.getZone(17);
         this.ad.dn = null;
         this.ad.do = null;
         this.ad.dp = var1.getZone(18);
         this.ad.dq = var1.getZone(19);
         this.ad.dr = null;
         this.ad.dt = null;
         this.ad.ds = null;
         this.ah = Displayable.eL.height;
      } else {
         this.ad.dm = var1.getZone(8);
         this.ad.dn = null;
         this.ad.do = var1.getZone(7);
         this.ad.dp = var1.getZone(10);
         this.ad.dq = var1.getZone(11);
         this.ad.dr = var1.getZone(9);
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
         this.ad.V();
      }

   }

   private void a(boolean var1) {
      if (var1) {
         this.ad.dl = this.au == null ? Displayable.eL : this.au.getMainZone();
         if (this.ad.dx[this.ad.dw].dN == null) {
            this.ad.dm = Q;
            this.ad.dn = null;
         } else {
            this.ad.dm = R;
            this.ad.dn = S;
         }

         this.ad.dC = true;
         this.ad.dB = false;
      } else {
         this.ad.dl = Displayable.eL;
         if (this.ad.dq == null) {
            this.ad.dm = Z;
            this.ad.dn = aa;
         } else {
            this.ad.dm = Y;
            this.ad.dn = null;
         }

         this.ad.dt = null;
         this.ad.dC = false;
         this.ad.dB = true;
      }
   }

   private void q() {
      if (this.size() != 0) {
         ChoiceHandler.ChoiceElement var1 = this.ad.dx[this.ad.dw];
         if (this.ag == null) {
            this.a(true);
            this.ag = this.ad.new ChoiceElement(var1.dM, var1.dN);
            this.ag.b(var1.dQ);
            this.ag.dR = true;
            this.ag.dS = 0;
            this.ag.dT = 0;
            this.ag.selected = var1.selected;
            this.a(false);
         }

         if (this.au != null && this.ah != this.au.getMainZone().height) {
            this.a(true);
            this.ah = this.au.getMainZone().height;
            this.a(false);
         }

      }
   }

   private void r() {
      if (this.size() != 0) {
         ChoiceHandler.ChoiceElement var1 = this.ad.dx[this.ad.dw];
         this.a(true);
         this.ah = this.au == null ? Displayable.eL.height : this.au.getMainZone().height;
         if (this.ag == null) {
            this.ag = this.ad.new ChoiceElement(var1.dM, var1.dN);
         } else {
            this.ag.a(var1.dM, var1.dN);
         }

         this.ag.b(var1.dQ);
         this.ag.dR = true;
         this.ag.dS = 0;
         this.ag.dT = 0;
         this.ag.selected = var1.selected;
         this.a(false);
      }
   }

   static {
      Q = Displayable.eI.getZone(37);
      R = Displayable.eI.getZone(38);
      S = Displayable.eI.getZone(39);
      T = Displayable.eI.getZone(40);
      U = Displayable.eI.getZone(41);
      V = Displayable.eI.getZone(36);
      W = Pixmap.createPixmap(11);
      X = Pixmap.createPixmap(12);
      Y = Displayable.eI.getZone(17);
      Z = Displayable.eI.getZone(18);
      aa = Displayable.eI.getZone(19);
      ab = V.getMarginTop();
   }

   private class PopupListListener implements CommandListener {
      private final ChoiceGroup dL;

      public void commandAction(Command var1, Displayable var2) {
         if (var2.equals(this.dL.af)) {
            int var3;
            if (var1 != null && var1.getCommandType() == 2) {
               if (this.dL.ad != null) {
                  var3 = this.dL.ad.dw;
                  this.dL.ad.dx[var3].dR = false;
                  this.dL.ad.dx[var3].selected = false;
                  this.dL.ad.dw = this.dL.ai;
                  this.dL.ad.dx[this.dL.ai].dR = true;
                  this.dL.ad.dx[this.dL.ai].selected = true;
               }

               if (this.dL.au != null) {
                  this.dL.au.eV.c((Displayable)null, this.dL.au);
               } else {
                  var2.eV.setCurrent(var2.eV.getCurrent());
               }
            }

            if (var1.equals(List.SELECT_COMMAND)) {
               var3 = this.dL.af.getSelectedIndex();
               this.dL.setSelectedIndex(var3, true);
               if (this.dL.au != null) {
                  this.dL.au.b(this.dL);
                  this.dL.au.eV.c((Displayable)null, this.dL.au);
               } else {
                  var2.eV.setCurrent(var2.eV.getCurrent());
               }
            }

            this.dL.af = null;
         }
      }

      PopupListListener(ChoiceGroup var1, Object var2) {
         this.dL = var1;
      }
   }
}
