package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import java.util.Vector;

public class StringItem extends Item {
   private static final String fr = TextDatabase.getText(34);
   boolean fs;
   int ft;
   int fu;
   int fv;
   int fw;
   int fx;
   int fy;
   private int fz;
   int fA;
   boolean fB;
   private int fC;
   private Font fD;
   private int fE;
   private int fF;
   private int location;
   private boolean fG;
   private int D;
   private int fH;
   private String str;
   private String fI;
   private Font db;
   private Font fJ;
   private Font fK;
   private int ec;
   private int ed;
   private int fL;
   private int fM;
   private boolean fN;
   Vector B;
   Vector fO;
   private int fP;
   private boolean fQ;
   private boolean fR;
   private int fS;

   public StringItem(String var1, String var2) {
      this(var1, var2, 0);
   }

   public StringItem(String var1, String var2, int var3) {
      super(var1);
      this.fs = true;
      this.ft = 0;
      this.fu = 0;
      this.fv = 0;
      this.fw = 0;
      this.fx = 0;
      this.fy = 0;
      this.fz = -1;
      this.fA = 0;
      this.fB = false;
      this.fC = -1;
      this.fE = -1;
      this.fF = 0;
      this.location = 4;
      this.fG = false;
      this.D = 0;
      this.fH = 0;
      this.fL = -1;
      this.B = new Vector();
      this.fO = new Vector();
      this.fQ = false;
      this.fR = false;
      this.fS = -1;
      synchronized(Display.hG) {
         switch(var3) {
         case 0:
         case 1:
         case 2:
            this.ed = var3;
            this.ec = 0;
            this.setFontImpl((Font)null);
            this.fD = this.db;
            this.setTextImpl(var2);
            this.ao();
            return;
         default:
            throw new IllegalArgumentException();
         }
      }
   }

   public String getText() {
      return this.str;
   }

   public void setText(String var1) {
      synchronized(Display.hG) {
         this.setTextImpl(var1);
         this.ao();
         this.invalidate();
      }
   }

   public int getAppearanceMode() {
      return this.ed;
   }

   public void setFont(Font var1) {
      synchronized(Display.hG) {
         this.setFontImpl(var1);
      }
   }

   public Font getFont() {
      return this.db;
   }

   final int a() {
      int var1 = this.an() ? 2 * BUTTON_BORDER_WIDTH : 0;
      Font var2 = this.an() ? this.getButtonFont() : this.db;
      var1 += this.str != null && !a(this.fI) ? this.fP : (this.isFocusable() ? this.a(var2) : this.fP);
      return this.aJ != null && var1 < Item.aq ? Item.aq : var1;
   }

   final int a(int var1) {
      if (this.ax != -1) {
         var1 = this.getMinimumWidth();
         this.ax = this.ax > var1 ? this.ax : var1;
         return this.ax;
      } else {
         var1 = this.an() ? 2 * BUTTON_BORDER_WIDTH : 0;
         Font var2 = this.an() ? this.getButtonFont() : this.db;
         if (this.fI != null && !a(this.fI)) {
            var1 += var2.stringWidth(this.fI);
            if (this.aE && var1 < ak) {
               var1 += this.fw;
            }

            if (this.an() && UIStyle.MAX_BUTTON_WIDTH > 0) {
               if (this.fR && this.fS >= 0) {
                  var1 = 2 * BUTTON_BORDER_WIDTH;
                  if (this.aE) {
                     var1 += this.fw;
                  }

                  var1 = var1 + var2.stringWidth(this.fI.substring(0, this.fS)) + this.fP;
               }

               if (var1 > UIStyle.MAX_BUTTON_WIDTH) {
                  var1 = UIStyle.MAX_BUTTON_WIDTH;
                  this.fR = true;
               }
            }

            if (var1 < ak && this.ft <= 0) {
               return var1 > this.fP ? var1 : this.fP;
            } else {
               return ak;
            }
         } else {
            return (var1 = this.isFocusable() ? var1 + this.a(var2) : var1) > ak ? ak : var1;
         }
      }
   }

   final int b() {
      int var1 = this.an() ? 2 * BUTTON_BORDER_HEIGHT : 0;
      if (this.ec == 1 || this.an()) {
         var1 += TextBreaker.DEFAULT_TEXT_LEADING;
      }

      if ((this.fI == null || a(this.fI)) && this.isFocusable()) {
         Font var2 = this.ec == 1 ? this.getPaintFont() : (this.an() ? this.getButtonFont() : this.db);
         var1 += this.a(ak, var2);
      } else {
         var1 += this.fM;
      }

      if (this.ay != -1 && this.ay < var1 + this.getLabelHeight(this.ax)) {
         this.ay = var1 + this.getLabelHeight(this.ax);
      }

      return var1;
   }

   final int b(int var1) {
      var1 = var1 == -1 ? ak : var1;
      int var2;
      if (this.ay != -1) {
         var2 = this.getMinimumHeight();
         this.ay = this.ay > var2 ? this.ay : var2;
         return this.ay - this.getLabelHeight(var1);
      } else {
         var2 = this.an() ? 2 * BUTTON_BORDER_HEIGHT : 0;
         if (this.ec == 1 || this.an()) {
            var2 += TextBreaker.DEFAULT_TEXT_LEADING;
         }

         if (this.fI != null && !a(this.fI)) {
            this.e(var1, -1, this.ft, this.fu);
            var2 += this.fs ? this.fx : this.fF;
            if (this.an() && UIStyle.MAX_BUTTON_WIDTH > 0 && var2 > this.getMinimumHeight() - this.getLabelHeight(var1)) {
               var2 = this.getMinimumHeight() - this.getLabelHeight(var1);
               this.fR = true;
            }

            return var2 > this.fM ? var2 : this.fM;
         } else if (this.isFocusable()) {
            Font var3 = this.ec == 1 ? this.getPaintFont() : (this.an() ? this.getButtonFont() : this.db);
            return var2 + this.a(var1, var3);
         } else {
            return this.fM;
         }
      }
   }

   final void am() {
      if (!this.B.isEmpty()) {
         this.B.removeAllElements();
      }

      if (!this.fO.isEmpty()) {
         this.fO.removeAllElements();
      }

      this.location = 4;
      this.ft = 0;
      this.fw = 0;
      this.fC = -1;
      this.fE = -1;
      this.fF = 0;
      this.fB = false;
   }

   final boolean e() {
      if (this.label != null && this.label.length() > 0) {
         if (this.label.charAt(0) == '\n') {
            return true;
         }
      } else {
         if (this.str == null || this.str.length() <= 0) {
            return false;
         }

         if (this.str.charAt(0) == '\n') {
            return true;
         }
      }

      if ((this.av & 16384) == 16384) {
         if ((this.av & 256) == 256) {
            return true;
         } else {
            return false;
         }
      } else if (this.label != null && this.label.length() > 0) {
         return true;
      } else {
         return false;
      }
   }

   final boolean d() {
      if (this.str != null && this.str.length() > 0) {
         if (this.str.charAt(this.str.length() - 1) == '\n') {
            return true;
         }
      } else {
         if (this.label == null || this.label.length() <= 0) {
            return false;
         }

         if (this.label.charAt(this.label.length() - 1) == '\n') {
            return true;
         }
      }

      if ((this.av & 16384) == 16384) {
         return (this.av & 512) == 512;
      } else {
         return false;
      }
   }

   final boolean c() {
      return this.fN;
   }

   final void a(Graphics var1, int var2, int var3, boolean var4) {
      super.a(var1, var2, var3, var4);
      if (this.fI != null && !a(this.fI) || this.isFocusable()) {
         var2 = this.aD;
         var3 -= this.getLabelHeight(this.ax);
         int var5 = 0;
         int var6 = 0;
         int var7 = var1.getTranslateX();
         int var8 = var1.getTranslateY();
         com.nokia.mid.impl.isa.ui.gdi.Graphics var9;
         ColorCtrl var10;
         int var11 = (var10 = (var9 = var1.getImpl()).getColorCtrl()).getFgColor();
         if (this.an()) {
            var5 = BUTTON_BORDER_HEIGHT;
            var6 = BUTTON_BORDER_WIDTH;
            Displayable.eI.drawBorder(var9, var7, var8, this.aD, var3, UIStyle.BORDER_BUTTON, var4);
            var2 -= 2 * BUTTON_BORDER_WIDTH;
            var3 -= 2 * BUTTON_BORDER_HEIGHT;
         }

         if (this.aG.length() > 0 && this.ec == 1) {
            var9.setFont(this.getPaintFont().getImpl());
            var10.setFgColor(UIStyle.COLOUR_HIGHLIGHT);
         }

         if (this.an()) {
            var9.setFont(this.getButtonFont().getImpl());
         }

         if ((this.fI == null || a(this.fI)) && this.isFocusable()) {
            this.a(var1, var7 + var6, var8 + var5, var2, var3, var4);
         } else {
            if (this.fI != null && !a(this.fI)) {
               this.e(var2, var3, this.ft, this.fu);
               int var12;
               int var13;
               int var10002;
               if (this.fO != null && this.fu != 0 && this.ft != 0) {
                  var6 = this.fw;
                  var10002 = var7 + var6;
                  var13 = var8 + var5;
                  var12 = var10002;
                  int var16 = this.fx - this.fA;
                  if (this.ec == 1 && this.fO.size() > 0) {
                     var10.setFgColor(UIStyle.COLOUR_HIGHLIGHT);
                     if (var4) {
                        if (this.B != null && !this.B.isEmpty()) {
                           var9.fillRect((short)(var12 - 1), (short)var13, (short)(this.ft + 2), (short)(var16 + TextBreaker.DEFAULT_TEXT_LEADING));
                        } else {
                           int var17 = this.fv;
                           if (this.fO.size() > 1) {
                              var9.fillRect((short)(var12 - 1), (short)var13, (short)(this.ft + 2), (short)(var16 - this.fy + 1));
                           } else {
                              var17 = this.fv - this.fw;
                           }

                           var9.fillRect((short)(var12 - 1), (short)(var13 + var16 - this.fy), (short)(var17 + 2), (short)(this.fy + TextBreaker.DEFAULT_TEXT_LEADING));
                        }

                        var10.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
                     }
                  } else {
                     var10.setFgColor(UIStyle.COLOUR_TEXT);
                  }

                  var9.drawTextInArea(var12, var13, this.ft, var16, this.fO, UIStyle.isAlignedLeftToRight ? 1 : 3);
                  var6 = 0;
                  var5 = this.fx < this.fu ? var5 + this.fu - this.fA : var5 + this.fx - this.fA;
               } else {
                  var5 += this.fu;
               }

               var3 -= this.fx < this.fu ? this.fu - this.fA : this.fx - this.fA;
               if (this.B != null && !this.B.isEmpty()) {
                  var10002 = var7 + var6;
                  var13 = var8 + var5;
                  var12 = var10002;
                  StringItem var19 = this;
                  if (!this.B.isEmpty()) {
                     int var18 = this.fx < this.fu ? this.fu - this.fA : this.fx - this.fA;
                     if (var19.ec == 1) {
                        var10.setFgColor(UIStyle.COLOUR_HIGHLIGHT);
                        if (var4) {
                           if (var19.B.size() > 1) {
                              var9.fillRect((short)(var12 - 1), (short)var13, (short)(var2 + 2), (short)(var19.fF - var18 - var19.fy + 1));
                           }

                           var9.fillRect((short)(var12 - 1), (short)(var13 + var19.fF - var18 - var19.fy), (short)(var19.fv + 2), (short)(var19.fy + TextBreaker.DEFAULT_TEXT_LEADING));
                           var10.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
                        }
                     } else if (var19.ec == 2 && var4) {
                        var10.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
                     } else {
                        var10.setFgColor(UIStyle.COLOUR_TEXT);
                     }

                     var9.drawTextInArea(var12, var13, var2, var3, var19.B, UIStyle.isAlignedLeftToRight ? 1 : 3);
                     var9.setFont(var19.db.getImpl());
                  }
               }
            }

            var10.setFgColor(var11);
         }
      }
   }

   void invalidate() {
      super.invalidate();
      this.location = 4;
   }

   final boolean a(int var1, int var2, int var3, int[] var4) {
      boolean var11 = false;
      synchronized(Display.hG) {
         label221: {
            if (this.str == null || this.B.isEmpty() && this.fO.isEmpty() || this.ar[3] <= this.au.eR[3]) {
               return false;
            }

            int var5 = this.fu - this.fA;
            boolean var10000;
            int var8;
            switch(var1) {
            case 0:
               if (this.location == 4) {
                  var4[1] = 0;
                  var4[3] = this.au.eR[3];
                  this.location = 1;
               }

               this.fG = true;
               var10000 = true;
               break;
            case 1:
               if (this.location == 1) {
                  this.fG = false;
                  var10000 = false;
               } else {
                  if ((var8 = (var4[1] - this.D) % this.fy) != 0) {
                     var4[1] -= var8;
                  }

                  if (!this.fG && ((Form)this.au).em > this.ar[1] + this.au.eR[3]) {
                     this.location = 3;
                  }

                  if (this.location != 3) {
                     if (var4[1] < var5) {
                        if (var4[1] <= this.D + this.fy) {
                           var4[1] = 0;
                           this.location = 1;
                        } else {
                           var4[1] -= this.fy;
                        }

                        this.fG = true;
                     } else {
                        this.fG = true;
                        if (var4[1] > this.au.eR[3]) {
                           var4[1] -= this.fH;
                        } else {
                           var4[1] = 0;
                           this.location = 1;
                        }
                     }
                  } else {
                     if (!this.fG && ((Form)this.au).em > this.ar[1] + this.ar[3] - this.au.eR[3]) {
                        var4[1] = this.ar[3] - this.au.eR[3];
                     } else {
                        var4[1] -= this.fH;
                     }

                     this.location = 2;
                     this.fG = true;
                  }

                  if (var4[1] <= 0) {
                     var4[1] = 0;
                     this.location = 1;
                  }

                  if (var4[1] >= this.ar[3] - this.fH) {
                     var4[1] = this.ar[3] - this.fH;
                     this.location = 3;
                  }

                  var8 = this.au.eR[3];
                  var4[3] = var8 < this.ar[3] - var4[1] ? var8 : this.ar[3] - var4[1];
                  var10000 = true;
               }

               var11 = var10000;
               break label221;
            case 6:
               StringItem var10 = this;
               if (this.location == 3) {
                  this.fG = false;
                  var10000 = false;
               } else {
                  if ((var8 = (var4[1] + this.au.eR[3] - this.D) % this.fy) != 0) {
                     var4[1] += this.fy - var8;
                  }

                  if (!this.fG) {
                     this.location = 4;
                  }

                  if (!this.fG && this.location == 4 && ((Form)this.au).em < this.ar[1]) {
                     this.location = 1;
                     this.fG = true;
                     var4[1] = 0;
                  } else if (this.location != 1 && this.location != 4) {
                     if (var4[1] + this.fy < var5) {
                        var4[1] += this.fy;
                     } else if (this.ar[3] - (var4[1] + var4[3]) > this.fH) {
                        var4[1] += this.fH;
                        if (this.ar[3] - var4[1] <= this.fH) {
                           this.location = 3;
                           this.fG = true;
                        }
                     } else {
                        var4[1] = this.ar[3] - this.au.eR[3];
                        this.location = 3;
                        this.fG = true;
                     }
                  } else {
                     this.location = 2;
                     this.fG = true;
                     if (var4[1] < var5) {
                        var4[1] += this.fy;
                     } else if (this.D > 0) {
                        boolean var12 = false;

                        for(var8 = 1; var10.D + var8 * var10.fy < var10.au.eR[3]; ++var8) {
                        }

                        var4[1] += var10.D + (var8 - 2) * var10.fy;
                     } else {
                        var4[1] += this.fH;
                     }

                     if (var10.ar[3] - var4[1] <= var10.fH) {
                        var10.location = 3;
                        var10.fG = true;
                     }
                  }

                  if (var4[1] >= var10.ar[3] - var10.au.eR[3]) {
                     var4[1] = var10.ar[3] - var10.au.eR[3];
                     var4[3] = var10.au.eR[3];
                     var10.location = 3;
                  }

                  var8 = var10.au.eR[3];
                  var4[3] = var8 < var10.ar[3] - var4[1] ? var8 : var10.ar[3] - var4[1];
                  var10000 = true;
               }

               var11 = var10000;
               break label221;
            default:
               if (!this.fG && (!((Form)this.au).el || ((Form)this.au).em > this.ar[1] && ((Form)this.au).em < this.ar[1] + this.ar[3])) {
                  var4[1] = ((Form)this.au).em - this.ar[1];
                  this.fG = true;
                  this.location = 2;
                  if (var4[1] <= this.au.eR[3] - this.fH - this.fy) {
                     var4[1] = 0;
                     this.location = 1;
                  } else if (var4[1] + this.au.eR[3] >= this.ar[3]) {
                     var4[1] = this.ar[3] - this.au.eR[3];
                     this.location = 3;
                  }

                  int var7 = this.au.eR[3];
                  var4[3] = var7 < this.ar[3] - var4[1] ? var7 : this.ar[3] - var4[1];
                  var10000 = true;
               } else {
                  this.fG = false;
                  var10000 = false;
               }
            }

            var11 = var10000;
         }
      }

      if (var11) {
         this.repaint();
      }

      return var11;
   }

   final boolean isFocusable() {
      return this.aG != null && this.aG.length() >= 1;
   }

   final int y() {
      int var1 = this.av & 255;
      int var2 = this.getVerticalTemporaryLayout();
      int var3 = UIStyle.isAlignedLeftToRight ? 1 : 2;
      switch(var1) {
      case 0:
         var1 = var2 | var3;
         break;
      case 1:
      case 2:
      case 3:
         var1 |= var2;
         break;
      case 16:
      case 32:
      case 48:
         var1 |= var3;
      }

      this.aF = var1 & 240;
      return var1;
   }

   Command[] getExtraCommands() {
      Command[] var1 = null;
      if (this.an() && this.fR && UIStyle.MAX_BUTTON_WIDTH > 0) {
         (var1 = new Command[1])[0] = ChoiceGroup.K;
      }

      return var1;
   }

   final boolean a(Command var1) {
      if (var1.equals(ChoiceGroup.K) && this.an() && this.fR && UIStyle.MAX_BUTTON_WIDTH > 0) {
         Display var3;
         (var3 = this.au.eV).getTruncatedItemScreen().a(var3, this.au, this.str, (Image)null, true);
         return true;
      } else {
         return false;
      }
   }

   final void c(int var1, int var2) {
      synchronized(Display.hG) {
         if (this.an() && this.fR && UIStyle.MAX_BUTTON_WIDTH > 0 && var1 == 35) {
            Display var5;
            (var5 = this.au.eV).getTruncatedItemScreen().a(var5, this.au, this.str, (Image)null, false);
         }

      }
   }

   final boolean b(Command var1) {
      boolean var2;
      if ((var2 = super.b(var1)) && this.aG.length() == 1 && this.ec == 0) {
         this.ec = this.ed == 2 ? 2 : 1;
         this.ao();
         this.invalidate();
      }

      return var2;
   }

   final boolean d(Command var1) {
      boolean var2;
      if ((var2 = super.d(var1)) && this.aG.length() < 1 && this.ec != 0) {
         this.ec = 0;
         this.ao();
         this.invalidate();
      }

      return var2;
   }

   void setOffsetWidth(int var1) {
      this.ft = var1;
   }

   void setOffsetPosition(int var1) {
      this.fw = var1;
   }

   void setOffsetHeight(int var1) {
      this.fu = var1;
   }

   final boolean a(boolean var1, int var2, int var3) {
      StringItem var10000;
      boolean var10001;
      label92: {
         if ((this.au == null || this.aB > 0) && !this.e()) {
            Item var4 = ((Form)this.au).ej[this.aB - 1];
            boolean var5 = true;
            if (var4 instanceof StringItem) {
               int var6 = this.db.charWidth(' ') - Form.CELL_SPACING;
               int[] var7 = ((Form)this.au).er;
               var7[0] += var6;
               var7 = ((Form)this.au).er;
               var7[2] -= var6;
               ((StringItem)var4).fv += var6;
               var5 = (var6 = this.au.eR[2] - ((StringItem)var4).fv) >= this.a() && (var6 < this.a(-1) || var4.aE || var4.ar[2] >= this.au.eR[2]);
            }

            var10000 = this;
            if (!var1 && var5 && this.getLabel() == null && !this.an() && this.ax == -1 && !this.u() && this.aB > 0 && (this.a() <= var3 - Form.CELL_SPACING && var2 > var3 - Form.CELL_SPACING || var4 instanceof StringItem && (this.au.eR[2] - Form.CELL_SPACING - ((StringItem)var4).fv >= this.a() || ((StringItem)var4).fu - ((StringItem)var4).fx > 0) && !((StringItem)var4).fB && !((StringItem)var4).d() && !((StringItem)var4).an()) && var2 > var3 - Form.CELL_SPACING) {
               var10001 = true;
               break label92;
            }
         } else {
            var10000 = this;
         }

         var10001 = false;
      }

      var10000.aE = var10001;
      return this.aE;
   }

   int getMinimumScroll(int var1) {
      int var3 = (var3 = ((Form)this.au).em - this.ar[1] - this.getLabelHeight(this.ar[2])) < 0 ? 0 : var3;
      if (this.fy == 0) {
         this.fy = this.fM = this.db.getHeight() + TextBreaker.DEFAULT_TEXT_LEADING;
      }

      int var2;
      if (var1 == 6) {
         var2 = (var3 + this.au.eR[3]) % this.fy;
      } else if (var1 == 1) {
         if ((var2 = var3 % this.fy) != 0) {
            var2 = this.fy - var2;
         }
      } else {
         var2 = 0;
      }

      if (this.ar[1] > ((Form)this.au).em && this.ar[1] + this.ar[3] < ((Form)this.au).em + this.au.eR[3]) {
         return this.fH - var2 + this.fy;
      } else {
         return (var2 = (var2 = ((Form)this.au).ei[1] < this.ar[1] + this.fu - this.fA ? (2 * this.fy - var2 > Form.FORM_MAX_SCROLL ? this.fy - var2 : 2 * this.fy - var2) : this.fH - var2 + this.fy) < 0 ? var2 + this.fy : var2) <= 0 ? var2 + this.fy : var2;
      }
   }

   int getVerticalTemporaryLayout() {
      char var1 = '／';
      int var2 = this.av | var1;
      if (this.au == null) {
         return var2;
      } else if (var2 == var1) {
         Item var4 = this.aB > 0 ? ((Form)this.au).ej[this.aB - 1] : null;
         Item var3 = this.aB < ((Form)this.au).ek - 1 ? ((Form)this.au).ej[this.aB + 1] : null;
         if (var4 != null && var4.aA == this.aA && (var4.aF | var1) != var1) {
            this.aF = var4.aF;
            return this.aF;
         } else if (var3 == null || var3.aA != this.aA || (var3.aF | var1) == var1 || this.fu != 0 && this.ft != 0) {
            this.aF = 16;
            return this.aF;
         } else {
            this.aF = var3.aF;
            return this.aF;
         }
      } else {
         this.aF = this.av & 240;
         return this.aF;
      }
   }

   private boolean an() {
      return this.aG.length() > 0 && this.ec == 2;
   }

   final boolean A() {
      return true;
   }

   private void e(int var1, int var2, int var3, int var4) {
      int var5 = this.getVerticalTemporaryLayout();
      StringItem var6 = this;
      int var9;
      boolean var10000;
      if (var1 == this.fC && var2 == this.fE && var3 == this.ft && var4 == this.fu && this.ec == this.fL && this.fz == var5 && this.db == this.fD && !this.fQ) {
         if (this.au != null) {
            for(var9 = 0; var6.fy != 0 && var9 * var6.fy < var6.au.eR[3]; ++var9) {
            }

            var6.fH = (var9 - 2) * var6.fy;
         }

         var10000 = false;
      } else {
         var10000 = true;
      }

      if (var10000) {
         this.fQ = false;
         this.fD = this.db;
         if (this.fI != null && !a(this.fI)) {
            this.fy = 0;
            this.fC = var1;
            this.fE = var2;
            this.ft = var3;
            this.fu = var4;
            this.fL = this.ec;
            this.fz = var5;
            TextBreaker var12 = this.getInitializedTextBreakerAndInitializeTextLines();
            this.fz = var5;
            TextLine var7;
            TextBreaker var8;
            int var10;
            if (this.aE || this.ft != 0 && this.fu != 0) {
               label167: {
                  int var11 = var2;
                  var10 = var4;
                  var9 = this.fz;
                  var8 = var12;
                  var7 = null;
                  var6 = this;
                  this.fx = 0;
                  switch(var9 & 255 & 48) {
                  case 32:
                     if ((var7 = var12.getTextLine(this.ft)) != null) {
                        this.fy = var7.getTextLineHeight();
                        this.fv = var7.getTextLineWidth();
                        this.fO.addElement(var7);
                        this.fx += this.fy;
                        this.fA = this.fu - this.fy;
                        this.fF = var4 - this.fA;
                     } else {
                        this.fA = this.fu;
                     }
                     break label167;
                  case 48:
                     if ((var7 = var12.getTextLine(this.ft)) != null) {
                        this.fy = var7.getTextLineHeight();
                        this.fv = var7.getTextLineWidth();
                        this.fO.addElement(var7);
                        this.fx += this.fy;
                        this.fA = (this.fu - this.fy) / 2;
                        this.fF = var4 - this.fA;
                     } else {
                        this.fA = this.fu;
                     }
                     break label167;
                  default:
                     this.fA = 0;
                     if (this.fy <= 0) {
                        var9 = (this.ec == 1 ? this.fJ.getHeight() : this.db.getHeight()) + TextBreaker.DEFAULT_TEXT_LEADING;
                     } else {
                        var9 = this.fy;
                     }

                     if (this.ay != -1 && this.fF + (var9 << 1) > var2) {
                        var12.setTruncation(true);
                     }
                  }

                  while(var6.fx < var10 && (var7 = var8.getTextLine(var6.ft)) != null) {
                     var6.fy = var7.getTextLineHeight();
                     var6.fv = var7.getTextLineWidth();
                     var6.fO.addElement(var7);
                     var6.fx += var6.fy;
                     if (var6.ay != -1 && var6.fF + 2 * (var9 + TextBreaker.DEFAULT_TEXT_LEADING) > var11) {
                        var8.setTruncation(true);
                     }
                  }

                  var6.fF = var6.fx;
                  var8.setTruncation(false);
               }

               var6.fv += var6.fw;
               if (var6.fF < var6.fu) {
                  var6.fF = var10 - var6.fA;
               }
            } else {
               this.fx = 0;
               this.fu = 0;
               this.fA = 0;
            }

            var9 = var2;
            var8 = var12;
            var7 = null;
            var6 = this;
            if (this.fy <= 0) {
               var10 = (this.ec == 1 ? this.fJ.getHeight() : this.db.getHeight()) + TextBreaker.DEFAULT_TEXT_LEADING;
            } else {
               var10 = this.fy;
            }

            if (this.ay != -1 && this.fF + (var10 << 1) > var2) {
               var12.setTruncation(true);
            }

            while((var7 = var8.getTextLine(var6.fC)) != null && (var6.fF + var6.fy <= var9 || var9 == -1)) {
               var6.fy = var7.getTextLineHeight();
               var6.fv = var7.getTextLineWidth();
               var6.B.addElement(var7);
               var6.fF += var6.fy;
               var6.fs = false;
               if (var6.ay != -1 && var6.fF + 2 * var6.fy > var9) {
                  var8.setTruncation(true);
               }
            }

            var8.setTruncation(false);
            var12.destroyBreaker();
            int var14 = this.fy;
            var6 = this;
            this.D = this.getLabelHeight(this.ax);
            if (this.au != null) {
               boolean var13 = false;

               int var15;
               for(var15 = 0; var14 != 0 && var15 * var14 < var6.au.eR[3]; ++var15) {
               }

               var6.fH = (var15 - 2) * var14;
            }

         } else {
            this.B.removeAllElements();
            this.fO.removeAllElements();
         }
      }
   }

   private void setTextImpl(String var1) {
      this.fQ = true;
      this.str = var1;
      this.fR = false;
      if (var1 == null) {
         this.fI = null;
         this.str = null;
      } else {
         byte var2 = 0;
         int var3 = var1.length();
         if ((this.label == null || this.label.length() == 0) && (this.av & 256) != 256 && var1 != null && var3 > 0 && var1.charAt(0) == '\n') {
            var2 = 1;
         }

         if (var1 != null && var3 > 1 && (this.av & 512) != 512 && var1.charAt(var3 - 1) == '\n') {
            --var3;
         }

         if (var3 > var2) {
            this.fI = var1.substring(var2, var3);
            if (UIStyle.MAX_BUTTON_WIDTH > 0 && this.an()) {
               this.fS = this.fI.indexOf(10);
               if (this.fS >= 0) {
                  this.fR = true;
                  return;
               }
            }
         } else {
            this.fI = "";
         }

      }
   }

   private void setFontImpl(Font var1) {
      boolean var2 = false;
      this.fR = false;
      if (var1 != this.db || var1 == null) {
         var2 = true;
         if (var1 == null) {
            this.db = new Font(ao.getFont());
            this.fK = null;
         } else {
            this.db = var1;
            this.fK = this.db;
         }

         this.fJ = null;
         this.fP = this.db.stringWidth(fr);
         this.aC = this.a(this.db);
         this.fy = this.fM = this.db.getHeight() + TextBreaker.DEFAULT_TEXT_LEADING;
         if (UIStyle.MAX_BUTTON_WIDTH > 0 && this.an() && this.fI != null) {
            this.fS = this.fI.indexOf(10);
            if (this.fS >= 0) {
               this.fR = true;
            }
         }
      }

      if (var2) {
         this.invalidate();
      }

   }

   private void ao() {
      if (this.isFocusable() || (this.str != null || this.label != null) && (this.str != null || !a(this.label)) && (this.label != null || !a(this.fI)) && (!a(this.label) || !a(this.fI))) {
         this.fN = false;
      } else {
         this.fN = true;
      }
   }

   private Font getPaintFont() {
      if (this.fJ == null) {
         this.fJ = Font.getFont(this.db.getFace(), this.db.getStyle() | 4, this.db.getSize());
      }

      this.fy = this.fM = this.fJ.getHeight() + TextBreaker.DEFAULT_TEXT_LEADING;
      return this.fJ;
   }

   private Font getButtonFont() {
      if (this.fK == null) {
         this.fK = new Font(UIStyle.getUIStyle().getButtonFont());
      }

      this.fy = this.fM = this.fK.getHeight() + TextBreaker.DEFAULT_TEXT_LEADING;
      return this.fK;
   }

   private TextBreaker getInitializedTextBreakerAndInitializeTextLines() {
      TextBreaker var1 = TextBreaker.getBreaker();
      if (this.aG.length() > 0 && this.ec == 1) {
         var1.setFont(this.getPaintFont().getImpl());
      } else if (this.an()) {
         var1.setFont(this.getButtonFont().getImpl());
      } else {
         var1.setFont(this.db.getImpl());
      }

      var1.setLeading(TextBreaker.DEFAULT_TEXT_LEADING, true);
      var1.setText(this.fI);
      if (this.an() && UIStyle.MAX_BUTTON_WIDTH > 0 && this.fR) {
         var1.setTruncation(true);
      } else {
         var1.setTruncation(false);
      }

      var1.enableWordWrapping(false);
      this.fF = 0;
      this.B.removeAllElements();
      this.fO.removeAllElements();
      this.fx = 0;
      return var1;
   }
}
