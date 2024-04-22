package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import com.nokia.mid.ui.lcdui.VisibilityListener;

public class Form extends Screen {
   static final int CELL_SPACING;
   static final int FORM_MAX_SCROLL;
   static boolean eh;
   int[] ei;
   Item[] ej;
   int ek;
   boolean el;
   int em;
   boolean en;
   private int eo;
   private int ep;
   private boolean eq;
   int[] er;
   private int[] es;
   private ItemStateListener et;
   private int eu;
   private boolean aE;
   private int ev;
   private int ew;
   private int ex;
   private int ey;
   private int ez;
   private int eA;
   private int eB;
   private int eC;
   private boolean eD;
   static final boolean eE;
   private boolean eF;
   private boolean eG;
   private boolean eH;

   public Form(String var1) {
      this(var1, (Item[])null);
   }

   public Form(String var1, Item[] var2) {
      super(var1);
      this.el = false;
      this.em = 0;
      this.en = false;
      this.ep = -1;
      this.eq = true;
      this.eu = 0;
      this.aE = false;
      this.ez = -1;
      this.eD = false;
      this.eF = false;
      this.eG = false;
      this.eH = false;
      synchronized(Display.hG) {
         this.ad();
         this.ek = 0;
         this.gY = true;
         this.ei = new int[4];
         this.es = new int[4];
         if (var2 == null) {
            this.ej = new Item[4];
         } else {
            this.ej = new Item[var2.length > 4 ? var2.length : 4];

            int var3;
            for(var3 = 0; var3 < var2.length; ++var3) {
               if (var2[var3].au != null) {
                  throw new IllegalStateException();
               }
            }

            for(var3 = 0; var3 < var2.length; ++var3) {
               this.a(this.ek, var2[var3]);
            }

         }
      }
   }

   public int append(Item var1) {
      synchronized(Display.hG) {
         if (var1.au != null) {
            throw new IllegalStateException();
         } else {
            return this.a(this.ek, var1);
         }
      }
   }

   public int append(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.append((Item)(new StringItem((String)null, var1)));
      }
   }

   public int append(Image var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.append((Item)(new ImageItem((String)null, var1, 0, (String)null)));
      }
   }

   public void insert(int var1, Item var2) {
      synchronized(Display.hG) {
         if (var2.au != null) {
            throw new IllegalStateException();
         } else if (var1 >= 0 && var1 <= this.ek) {
            this.a(var1, var2);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void delete(int var1) {
      synchronized(Display.hG) {
         if (var1 >= 0 && var1 < this.ek) {
            this.y(var1);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void deleteAll() {
      synchronized(Display.hG) {
         this.ad();
      }
   }

   public void set(int var1, Item var2) {
      synchronized(Display.hG) {
         if (var2.au != null) {
            throw new IllegalStateException();
         } else if (var1 >= 0 && var1 < this.ek) {
            this.setImpl(var1, var2);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public Item get(int var1) {
      synchronized(Display.hG) {
         if (var1 >= 0 && var1 < this.ek) {
            return this.ej[var1];
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void setItemStateListener(ItemStateListener var1) {
      synchronized(Display.hG) {
         this.et = var1;
      }
   }

   public int size() {
      return this.ek;
   }

   public int getWidth() {
      return this.eR[2] - 2 * UIStyle.CUSTOMITEM_BORDER_PAD;
   }

   public int getHeight() {
      return this.eR[3];
   }

   final boolean B() {
      return true;
   }

   final boolean m() {
      return this.ek != 0 && this.ep >= 0 ? this.ej[this.ep].m() : true;
   }

   void setCurrentItem(Item var1) {
      if (!this.fh) {
         this.layout();
      }

      if (this.ep == -1 || this.ej[this.ep] != var1) {
         this.c(0, this.ep, var1.aB);
         this.gY = false;
      }
   }

   final void a(Display var1) {
      this.eH = true;
      super.a(var1);
      Item var5 = null;
      synchronized(Display.hG) {
         if (!this.fh) {
            this.layout();
         }

         if (this.gY) {
            if (this.ep >= 0 && this.ep < this.ek && this.ej[this.ep].hasFocus) {
               var5 = this.ej[this.ep];
            }

            this.ep = -1;
            this.ei[0] = 0;
            this.ei[1] = this.eB;
            this.em = this.ei[1];
            this.gY = false;
         }
      }

      if (var5 != null) {
         var5.f();
      }

      this.traverse(0);
      Displayable.eI.hideIndex();
      this.eH = false;
   }

   final void b(Display var1) {
      VisibilityListener var2 = this.fb;
      super.b(var1);

      for(int var3 = 0; var3 < this.ek; ++var3) {
         try {
            if (this.ej[var3].visible) {
               this.ej[var3].x();
            }
         } catch (Exception var5) {
         }
      }

      if (var2 != null) {
         synchronized(Display.hH) {
            var2.hideNotify(var1, this);
         }
      }
   }

   final void d(Display var1) {
      super.d(var1);
      Item var4 = null;
      synchronized(Display.hG) {
         if (this.ep != -1 && this.ej[this.ep].hasFocus) {
            var4 = this.ej[this.ep];
         }

         this.eo = 0;
      }

      if (var4 != null) {
         var4.f();
      }

   }

   final void c(int var1, int var2) {
      super.c(var1, var2);
      Item var3 = null;
      byte var4 = -1;
      switch(var1) {
      case -4:
         var4 = 5;
         break;
      case -3:
         var4 = 2;
         break;
      case -2:
         var4 = 6;
         break;
      case -1:
         var4 = 1;
      }

      synchronized(Display.hG) {
         if (this.ek == 0 || this.ep < 0) {
            return;
         }

         var3 = this.ej[this.ep];
      }

      if (var3.z()) {
         var3.c(var1, var2);
      } else if (var4 != 1 && var4 != 6) {
         if (var4 != 2 && var4 != 5) {
            if (var1 != -6 && var1 != -7 && var1 != -5) {
               var3.c(var1, var2);
            }

         } else if (var3.n()) {
            var3.c(var1, var2);
         } else {
            this.traverse(var4);
         }
      } else {
         this.traverse(var4);
      }
   }

   final void h(int var1, int var2) {
      super.h(var1, var2);
      Item var3 = null;
      synchronized(Display.hG) {
         if (this.ek == 0 || this.ep < 0) {
            return;
         }

         var3 = this.ej[this.ep];
      }

      if (var3 != null) {
         var3.h(var1, var2);
      }

   }

   final void i(int var1, int var2) {
      super.i(var1, var2);
      Item var3 = null;
      byte var4 = -1;
      switch(var1) {
      case -4:
         var4 = 5;
         break;
      case -3:
         var4 = 2;
         break;
      case -2:
         var4 = 6;
         break;
      case -1:
         var4 = 1;
      }

      synchronized(Display.hG) {
         if (this.ek == 0 || this.ep < 0) {
            return;
         }

         var3 = this.ej[this.ep];
      }

      if (var3.z()) {
         var3.i(var1, var2);
      } else if (var4 != 1 && var4 != 6) {
         if (var4 != 2 && var4 != 5) {
            if (var1 != -6 && var1 != -7 && var1 != -5) {
               var3.i(var1, var2);
            }

         } else if (var3.n()) {
            var3.i(var1, var2);
         } else {
            this.traverse(var4);
         }
      } else {
         this.traverse(var4);
      }
   }

   private void traverse(int var1) {
      switch(this.eo) {
      case 0:
         try {
            this.w(var1);
            return;
         } catch (Throwable var2) {
            return;
         }
      case 2:
         try {
            if (!this.x(var1)) {
               this.w(var1);
            }

            return;
         } catch (Throwable var3) {
         }
      default:
      }
   }

   final void w(int var1) {
      if (this.ek == 0) {
         this.c(true);
      } else {
         Form var30;
         Item var33;
         if (var1 == 0) {
            var30 = this;
            if (this.ep != -1 && this.ep != 0) {
               this.c(0, this.ep, this.ep);
            } else if (!this.ej[0].isFocusable() && this.ek != 1) {
               int var31 = 1;
               boolean var40 = false;

               for(var33 = this.ej[1]; var31 < var30.ek && var33.aA < var30.eR[3] && !var40; ++var31) {
                  var40 = ((var33 = var30.ej[var31]).aA == var30.ej[0].aA || var33.ar[1] + var33.ar[3] <= var30.eR[3]) && var33.isFocusable();
               }

               if (var40) {
                  var30.c(0, var30.ep, var31 - 1);
               } else {
                  var30.c(0, var30.ep, 0);
               }
            } else {
               this.c(0, this.ep, 0);
            }
         } else if (this.ep >= 0 && !this.ej[this.ep].c() && this.b(var1, this.ej[this.ep].ar)) {
            this.eq = true;
            this.ag();
         } else {
            int var2 = this.getScrollingDirection(var1);
            int var4 = this.ep;

            do {
               int var3 = var4;
               int var6;
               int var7;
               boolean var10;
               int var11;
               int var12;
               int var13;
               int var10000;
               int var17;
               int var19;
               int var20;
               int var21;
               int var22;
               int var23;
               int var24;
               int var26;
               int var27;
               int var28;
               int var29;
               Item var34;
               int var38;
               Item var48;
               Item var49;
               int var52;
               int[] var54;
               switch(var2) {
               case 1:
                  label696: {
                     var6 = var4;
                     if (this.el) {
                        if (this.eu <= this.ej[this.ep].aA) {
                           this.el = false;
                        } else if (!this.ej[this.ep].aE) {
                           this.el = true;
                           var54 = this.ei;
                           var54[1] -= this.getMinimumScroll(1);
                           if (this.ei[1] < this.ej[this.ep].aA) {
                              this.ei[1] = this.ej[this.ep].aA;
                           }

                           this.ae();
                           this.eu = this.em = this.ei[1];
                           this.ag();
                           var10000 = -1;
                           break label696;
                        }
                     }

                     Form var35 = this;
                     if (this.ep == -1) {
                        var10000 = 0;
                     } else {
                        boolean var37 = false;
                        Item var32;
                        var11 = (var32 = this.ej[this.ep]).aA;
                        var10 = false;
                        var12 = var32.rowHeight;
                        var13 = var32.t() + var32.s() / 2;
                        int var43 = var32.ar[1];
                        this.ez = this.ep;
                        boolean var44 = false;
                        Item var45 = this.z(var11);
                        var17 = -1;

                        while(true) {
                           if (var35.ez >= 0 && (var44 || var43 - (var11 + var12) <= var35.eR[3] - FORM_MAX_SCROLL || var35.ei[1] - var45.aA <= var35.eR[3] - FORM_MAX_SCROLL)) {
                              label724: {
                                 Form var47;
                                 if (!var44) {
                                    var44 = var35.l(var11, -1);
                                    if (var35.ez < 0) {
                                       break label724;
                                    }

                                    if (!var44) {
                                       if ((var44 = var35.l(var35.ej[var35.ez].aA, var35.ez)) && var17 != -1 && var11 < var35.ei[1]) {
                                          break label724;
                                       }
                                    } else {
                                       var19 = var43;
                                       var11 = var11;
                                       var47 = var35;
                                       var20 = -1;
                                       var21 = -1;
                                       var23 = -1;
                                       var24 = -1;
                                       var52 = var35.ez + 1;

                                       for(Item var53 = var35.ej[var52]; var53 != null && var53.aA == var11 && !var53.c(); var53 = var52 < var47.ek ? var47.ej[var52] : null) {
                                          if (var53.ar[1] < var47.ei[1]) {
                                             var22 = e(var19, var53.ar[1], var53.ar[3]);
                                             if (var52 != var47.ep && (var20 == -1 || var20 > var22)) {
                                                var20 = var22;
                                                var23 = var52;
                                             }

                                             if (var52 != var47.ep && var53.isFocusable() && (var21 == -1 || var21 > var22)) {
                                                var21 = var22;
                                                var24 = var52;
                                             }
                                          }

                                          ++var52;
                                       }

                                       if (var24 == -1) {
                                          var24 = var23;
                                       }

                                       var17 = var24;
                                       if (var24 != -1) {
                                          var10000 = var24;
                                          break;
                                       }
                                    }
                                 }

                                 if (var35.ez >= 0) {
                                    var45 = var35.ej[var35.ez];
                                    var19 = var13;
                                    boolean var42 = var44;
                                    var47 = var35;
                                    var49 = var35.ej[var35.ez];
                                    Item var50 = var35.ej[var35.ep];
                                    var22 = var49.aA;
                                    var23 = -1;
                                    var24 = -1;
                                    var26 = -1;
                                    var27 = -1;

                                    while(true) {
                                       if (var47.ez >= 0 && var49.aA == var22) {
                                          if (var47.ez != var47.ep && !var49.c() && (var49.ar[1] + var49.ar[3] < var50.ar[1] + var50.ar[3] || var49.ar[1] < var50.ar[1]) && (var49.ar[1] + var49.ar[3] > var47.ei[1] - FORM_MAX_SCROLL + CELL_SPACING || var47.ei[1] - var49.aA <= var47.eR[3] - FORM_MAX_SCROLL || var42)) {
                                             var28 = var49.t();
                                             var29 = var49.s();
                                             if (d(var19, var28, var29)) {
                                                if (var49.isFocusable()) {
                                                   var10000 = var47.ez;
                                                   break;
                                                }

                                                var26 = var47.ez;
                                                var23 = 0;
                                             }

                                             var52 = e(var19, var28, var29);
                                             if (var49.isFocusable() && (var24 == -1 || var52 < var24)) {
                                                var24 = var52;
                                                var27 = var47.ez;
                                             }

                                             if (var23 == -1 || var52 < var23) {
                                                var23 = var52;
                                                var26 = var47.ez;
                                             }
                                          }

                                          --var47.ez;
                                          if (var47.ez >= 0) {
                                             var49 = var47.ej[var47.ez];
                                             continue;
                                          }
                                       }

                                       var10000 = var27 != -1 ? var27 : var26;
                                       break;
                                    }

                                    int var39 = var10000;
                                    var17 = var10000 != -1 ? var39 : var17;
                                    var39 = var45.aA;
                                    var38 = var45.rowHeight;
                                    if ((var17 == -1 || !var35.ej[var17].isFocusable()) && (var17 == -1 || !var44 || var35.ev <= var35.eR[3] || var35.ei[1] <= 0)) {
                                       var11 = var39;
                                       var12 = var38;
                                       continue;
                                    }

                                    var10000 = var17;
                                    break;
                                 }
                              }
                           }

                           var48 = null;
                           if (var17 >= 0 && var17 < var35.ek) {
                              var48 = var35.ej[var17];
                           }

                           if (var48 != null && var35.ev > var35.eR[3] && var35.ei[1] > 0 && var48.ar[1] < var35.ei[1]) {
                              var10000 = var17;
                              break;
                           }

                           var10000 = -1;
                           break;
                        }
                     }

                     var7 = var10000;
                     this.el = false;
                     if (var7 != -1) {
                        var6 = var7;
                     } else if (this.eu >= this.ej[this.ep].aA - FORM_MAX_SCROLL) {
                        this.el = true;
                        var54 = this.ei;
                        var54[1] -= this.getMinimumScroll(1);
                        this.ae();
                        this.eu = this.em = this.ei[1];
                        this.ag();
                        var10000 = -1;
                        break label696;
                     }

                     var10000 = var6;
                  }

                  var4 = var10000;
                  break;
               case 2:
                  var30 = this;
                  var6 = var4;
                  if (var4 <= 0) {
                     var10000 = -1;
                  } else {
                     while(true) {
                        var33 = var30.ej[var6 - 1];
                        var34 = var30.ej[var6];
                        if (var33.isFocusable() || var34.aA != var33.aA || var6 <= 0) {
                           var6 = var34.aA == var33.aA && var6 > 0 ? var33.aB : var4;
                           if (!var33.isFocusable()) {
                              var6 = var4;
                           }

                           var10000 = var6;
                           break;
                        }

                        --var6;
                     }
                  }

                  var4 = var10000;
               case 3:
               case 4:
               default:
                  break;
               case 5:
                  var30 = this;
                  var6 = var4;
                  if (var4 >= this.ek - 1) {
                     var10000 = -1;
                  } else {
                     while(true) {
                        var33 = var30.ej[var6 + 1];
                        var34 = var30.ej[var6];
                        if (var33.isFocusable() || var34.aA != var33.aA || var6 >= var30.ek - 1) {
                           var6 = var34.aA == var33.aA && var6 < var30.ek - 1 ? var33.aB : var4;
                           if (!var33.isFocusable()) {
                              var6 = var4;
                           }

                           var10000 = var6;
                           break;
                        }

                        ++var6;
                     }
                  }

                  var4 = var10000;
                  break;
               case 6:
                  label692: {
                     var6 = var4;
                     var7 = this.ej[this.ep].aA + this.ej[this.ep].rowHeight - this.eR[3] > 0 ? this.ej[this.ep].aA + this.ej[this.ep].rowHeight - this.eR[3] : 0;
                     if (this.el) {
                        if (this.eu >= var7) {
                           this.el = false;
                        } else if (this.ep < this.ek - 1) {
                           var54 = this.ei;
                           var54[1] += this.getMinimumScroll(6);
                           if (this.ei[1] > var7 + FORM_MAX_SCROLL) {
                              this.ei[1] = var7 + FORM_MAX_SCROLL;
                           }

                           this.ae();
                           this.eu = this.em = this.ei[1];
                           this.ag();
                           var10000 = -1;
                           break label692;
                        }
                     }

                     Form var5 = this;
                     if (this.ep == -1) {
                        var10000 = 0;
                     } else {
                        var10 = false;
                        Item var9;
                        var11 = (var9 = this.ej[this.ep]).aA;
                        var12 = var9.t() + var9.s() / 2;
                        var13 = var9.ar[1] + var9.ar[3];
                        this.ez = this.ep;
                        boolean var14 = false;
                        int var18 = var11;

                        Form var36;
                        for(var36 = this; var36.ez >= 0 && var36.ej[var36.ez].aA == var18; --var36.ez) {
                        }

                        ++var36.ez;
                        Item var15 = var36.ej[var36.ez];
                        int var16 = -1;

                        while(true) {
                           if (var5.ez < var5.ek && (var14 || var11 - var13 <= var5.eR[3] - FORM_MAX_SCROLL || var15.aA + var15.rowHeight <= var5.ei[1] + 2 * var5.eR[3] - FORM_MAX_SCROLL)) {
                              label722: {
                                 if (!var14) {
                                    var14 = var5.k(var11, -1);
                                    if (var5.ez >= var5.ek) {
                                       break label722;
                                    }

                                    if (!var14) {
                                       if ((var14 = var5.k(var5.ej[var5.ez].aA, var5.ez)) && var16 != -1 && var11 > var5.ei[1] + var5.eR[3]) {
                                          break label722;
                                       }
                                    } else {
                                       int var10001 = var11;
                                       var11 = var13;
                                       var18 = var10001;
                                       var36 = var5;
                                       var19 = -1;
                                       var20 = -1;
                                       var22 = -1;
                                       var23 = -1;
                                       var24 = var5.ez - 1;

                                       for(Item var25 = var5.ej[var24]; var25 != null && var25.aA == var18 && !var25.c(); var25 = var24 >= 0 ? var36.ej[var24] : null) {
                                          if (var25.ar[1] >= var36.ei[1] + var36.eR[3] - FORM_MAX_SCROLL) {
                                             var21 = e(var11, var25.ar[1], var25.ar[3]);
                                             if (var24 != var36.ep && (var19 == -1 || var19 > var21)) {
                                                var19 = var21;
                                                var22 = var24;
                                             }

                                             if (var24 != var36.ep && var25.isFocusable() && (var20 == -1 || var20 > var21)) {
                                                var20 = var21;
                                                var23 = var24;
                                             }
                                          }

                                          --var24;
                                       }

                                       if (var23 == -1) {
                                          var23 = var22;
                                       }

                                       var16 = var23;
                                       if (var23 != -1) {
                                          var10000 = var23;
                                          break;
                                       }
                                    }
                                 }

                                 if (var5.ez < var5.ek) {
                                    var38 = (var15 = var5.ej[var5.ez]).aA;
                                    var11 = var12;
                                    boolean var46 = var14;
                                    var36 = var5;
                                    var48 = var5.ej[var5.ez];
                                    var49 = var5.ej[var5.ep];
                                    var21 = var48.aA;
                                    var22 = -1;
                                    var23 = -1;
                                    boolean var51 = var48.aA == var21;
                                    var52 = -1;
                                    var26 = -1;

                                    while(true) {
                                       if (var36.ez < var36.ek && var51) {
                                          if (var36.ez != var36.ep && !var48.c() && var48.ar[1] + var48.ar[3] > var49.ar[1] + var49.ar[3] && (var48.ar[1] < var36.ei[1] + (var36.eR[3] - FORM_MAX_SCROLL) + CELL_SPACING || var48.aA + var48.rowHeight <= var36.ei[1] + 2 * var36.eR[3] - FORM_MAX_SCROLL || var46)) {
                                             var27 = var48.t();
                                             var28 = var48.s();
                                             if (d(var11, var27, var28)) {
                                                if (var48.isFocusable()) {
                                                   var10000 = var36.ez;
                                                   break;
                                                }

                                                var52 = var36.ez;
                                                var22 = 0;
                                             }

                                             var29 = e(var11, var27, var28);
                                             if (var48.isFocusable() && (var23 == -1 || var29 < var23)) {
                                                var23 = var29;
                                                var26 = var36.ez;
                                             }

                                             if (var22 == -1 || var29 < var22) {
                                                var22 = var29;
                                                var52 = var36.ez;
                                             }
                                          }

                                          ++var36.ez;
                                          if (var36.ez < var36.ek) {
                                             var51 = (var48 = var36.ej[var36.ez]).aA == var21;
                                             continue;
                                          }
                                       }

                                       var10000 = var26 != -1 ? var26 : var52;
                                       break;
                                    }

                                    var17 = var10000;
                                    if (((var16 = var10000 != -1 ? var17 : var16) == -1 || !var5.ej[var16].isFocusable()) && (var16 == -1 || !var14 || var5.ei[1] >= var5.ev - var5.eR[3] || var5.ev <= var5.eR[3])) {
                                       var11 = var38;
                                       continue;
                                    }

                                    var10000 = var16;
                                    break;
                                 }
                              }
                           }

                           Item var41 = null;
                           if (var16 >= 0 && var16 < var5.ek) {
                              var41 = var5.ej[var16];
                           }

                           if (var41 != null && var5.ei[1] < var5.ev - var5.eR[3] && var5.ev > var5.eR[3] && var41.ar[1] + var41.ar[3] > var5.ei[1] + var5.eR[3] - FORM_MAX_SCROLL) {
                              if (var41.ar[1] <= var5.ei[1] + var5.eR[3] - FORM_MAX_SCROLL) {
                                 var5.ei[1] = var41.ar[1] + var5.eR[3] <= var5.ev ? var41.ar[1] : var5.ev - var5.eR[3];
                              }

                              var10000 = var16;
                              break;
                           }

                           var10000 = -1;
                           break;
                        }
                     }

                     int var8 = var10000;
                     this.el = false;
                     if (var8 != -1) {
                        var6 = var8;
                     } else if (this.eu <= var7 + FORM_MAX_SCROLL) {
                        this.el = true;
                        var54 = this.ei;
                        var54[1] += this.getMinimumScroll(6);
                        this.ae();
                        this.eu = this.em = this.ei[1];
                        this.ag();
                        var10000 = -1;
                        break label692;
                     }

                     var10000 = var6;
                  }

                  var4 = var10000;
               }

               if (var4 == -1 || var3 == var4) {
                  return;
               }
            } while(this.ej[var4].c());

            this.c(var1, this.ep, var4);
         }
      }
   }

   private boolean x(int var1) {
      if (this.ep == -1) {
         return false;
      } else {
         Item var2 = this.ej[this.ep];
         int[] var3 = new int[]{this.es[0] + var2.ar[0], this.es[1] + var2.ar[1], this.es[2], this.es[3]};
         if (this.eo == 2 && this.b(var1, var3)) {
            this.eq = true;
            this.ag();
            return true;
         } else if (var2.a(var1, this.eR[2], this.eR[3], this.es)) {
            var3[1] = this.es[1] + var2.ar[1];
            var3[3] = this.es[3];
            this.eo = 2;
            if (this.c(var1, var3)) {
               this.eq = true;
               this.ag();
            }

            if (!this.ej[this.ep].isFocusable()) {
               this.A(var1);
            }

            this.em = this.ei[1];
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean b(int var1, int[] var2) {
      if (this.ev <= this.eR[3]) {
         this.ei[1] = this.eB;
      } else if (this.el) {
         this.ei[1] = this.ei[1] > this.eC - this.eR[3] ? this.eC - this.eR[3] : this.ei[1];
         return false;
      }

      int[] var10000;
      switch(var1) {
      case 1:
         if (var2[1] >= this.ei[1]) {
            return false;
         }

         var10000 = this.ei;
         var10000[1] -= this.getMinimumScroll(var1);
         this.ei[1] = this.ei[1] <= this.eB ? this.eB : this.ei[1];
         return true;
      case 2:
      case 3:
      case 4:
      case 5:
      default:
         return false;
      case 6:
         if (var2[1] + var2[3] <= this.ei[1] + this.eR[3]) {
            return false;
         } else {
            var10000 = this.ei;
            var10000[1] += FORM_MAX_SCROLL;
            this.ei[1] = this.ei[1] > this.eC - this.eR[3] ? this.eC - this.eR[3] : this.ei[1];
            return true;
         }
      }
   }

   private boolean c(int var1, int[] var2) {
      if (this.ev < this.eR[3]) {
         if (this.ei[1] != this.eB) {
            this.ei[1] = this.eB;
            return true;
         } else {
            return false;
         }
      } else {
         Item var3;
         if ((var3 = this.ej[this.ep]).visible && (var1 == 2 || var1 == 5) && var3.ar[3] > this.eR[3]) {
            return false;
         } else if (var2[1] >= this.ei[1] && var2[1] + var2[3] <= this.ei[1] + this.eR[3]) {
            return false;
         } else {
            if (var2[3] > this.eR[3] - FORM_MAX_SCROLL) {
               if (var1 == 6 || var1 == 0 || var1 == 5 && UIStyle.isAlignedLeftToRight || var1 == 2 && !UIStyle.isAlignedLeftToRight) {
                  this.ei[1] = var2[1];
               } else if (var1 == 1 || var1 == 5 && !UIStyle.isAlignedLeftToRight || var1 == 2 && UIStyle.isAlignedLeftToRight) {
                  this.ei[1] = var2[1] + var2[3] - this.eR[3];
               }
            } else if (var1 != 6 && var1 != 0 && (var1 != 5 || !UIStyle.isAlignedLeftToRight) && (var1 != 2 || UIStyle.isAlignedLeftToRight)) {
               if (var1 == 1 || var1 == 5 && !UIStyle.isAlignedLeftToRight || var1 == 2 && UIStyle.isAlignedLeftToRight) {
                  this.ei[1] = var2[1];
               }
            } else {
               this.ei[1] = var2[1] + var2[3] - this.eR[3];
            }

            this.ae();
            return false;
         }
      }
   }

   final void layout() {
      super.layout();
      if (this.ek != 0) {
         this.ew = this.ej[0].y() & 3;
         if (this.ej[0] instanceof ImageItem && (this.ej[0].av & 16384) == 0) {
            this.ey = (UIStyle.isAlignedLeftToRight ? 1 : 2) | 16384;
         } else {
            this.ew |= 16384;
         }

         this.ex = this.ew;
         int var1 = 0;
         if (this.er == null) {
            this.er = new int[4];
         }

         this.er[0] = 0;
         this.er[1] = 0;
         this.er[2] = this.eR[2];
         this.er[3] = this.eR[3];
         this.ei[2] = this.er[2];
         this.ei[3] = 0;
         int var2 = 0;
         int var3 = 0;
         boolean var4 = false;

         for(int var6 = 0; var6 < this.ek; ++var6) {
            Item var5;
            boolean var10000;
            label367: {
               if ((var5 = this.ej[var6]).visible) {
                  var5.x();
               }

               if (var5.ar == null) {
                  var5.ar = new int[4];
               } else {
                  if (var5 instanceof StringItem) {
                     ((StringItem)var5).am();
                  }

                  var5.ar[0] = 0;
                  var5.ar[1] = 0;
                  var5.ar[2] = 0;
                  var5.ar[3] = 0;
               }

               var3 = this.m(var6, var3);
               this.aE = false;
               boolean var10 = false;
               Item var11 = this.ej[var6];
               if (var6 == var1) {
                  this.aE = var11.a(false, var3, this.er[2]);
                  if (!this.aE) {
                     this.er[0] = 0;
                     this.er[2] = this.eR[2];
                     this.er[1] = var6 > 0 ? this.ej[var6 - 1].aA + this.ej[var6 - 1].rowHeight : this.eB;
                     this.eD = var11.isFocusable();
                     var10000 = false;
                     break label367;
                  }
               }

               var10 = !eh && this.eD && var11.isFocusable();
               this.eD = var11.isFocusable() ? true : this.eD;
               int var12 = var11.y() & 3;
               var10 = var10 || var6 > 0 && this.ej[var6 - 1].d() || var11.e();
               boolean var13 = false;
               if ((var11.av & 3) != 0) {
                  var13 = var12 != (this.ew & 3);
               } else if ((this.ew & 16384) == 0) {
                  var13 = (this.ey & 3) != (this.ew & 3);
               }

               if (var6 > var1 && !var10) {
                  this.aE = var11.a(var13, var3, this.er[2]);
                  var10 = var13 || var3 > this.er[2] - CELL_SPACING || this.aE;
               }

               if (var13) {
                  if (var11 instanceof ImageItem && (var11.av & 16384) == 0) {
                     if ((this.ew & 16384) != 0) {
                        this.ey = this.ew;
                     }

                     this.ex = var12 & 3;
                  } else {
                     if ((var11.av & 3) == 0) {
                        this.ex = this.ey;
                     } else {
                        this.ex = var12 & 3 | 16384;
                     }

                     this.ey = 0;
                  }
               }

               var10000 = var10;
            }

            int[] var26;
            if (var10000 && var2 > 0 || this.aE) {
               int var22 = var6;
               int var7;
               if (var6 > 0 && this.aE) {
                  int var10001 = var2;
                  var2 = var6;
                  int var25 = var10001;
                  Form var24 = this;
                  StringItem var23 = (StringItem)this.ej[var6];
                  if (this.ej[var6 - 1] instanceof StringItem) {
                     var25 = this.c(var1, var6 - 1, 0, var25);

                     for(var7 = var1; var7 < var2; ++var7) {
                        var24.ej[var7].rowHeight = var25;
                     }

                     StringItem var16 = (StringItem)var24.ej[var2];
                     StringItem var18 = (StringItem)var24.ej[var2 - 1];
                     var16.ar[0] = 0;
                     if (var18.fu > var18.fx && var18.fs) {
                        int var17 = var18.fu - var18.fx;
                        var16.setOffsetPosition(var18.fw);
                        var16.setOffsetWidth(var18.ft);
                        var16.setOffsetHeight(var17);
                        var16.ar[1] = var18.ar[1] + var18.ar[3] + CELL_SPACING;
                     } else {
                        if (!var18.aE) {
                           var24.f(var1, var2 - 1, var25);
                        }

                        var22 = var18.ar[0] + (var18.fv <= 0 ? 0 : var18.fv + CELL_SPACING);
                        int var14;
                        if ((var14 = var18.rowHeight + var18.aA - (var18.ar[1] + var18.ar[3])) > 0) {
                           var16.ar[1] = var18.aA + var18.ar[3];
                           var16.setOffsetPosition(var18.ar[0]);
                           var16.setOffsetWidth(var24.eR[2] - var18.ar[0]);
                           var16.setOffsetHeight(var14);
                        } else {
                           var16.setOffsetPosition(var22);
                           var16.setOffsetWidth(var24.eR[2] - var22);
                           var16.setOffsetHeight(var16.fy);
                           var16.ar[1] = var18.ar[1] + var18.ar[3] - var18.fy;
                        }
                     }

                     var16.aA = var16.ar[1];
                     var7 = var23.h(var23.ay);
                     var23.aD = var23.a(var23.ay);
                     var7 = var24.n(var2, var7);
                     var23.ar[3] = var7;
                     var7 = var2 - 1;

                     for(var23.rowHeight = var23.ar[3]; var7 > 0 && var24.ej[var7].ar[1] == var23.ar[1]; --var7) {
                        var24.ej[var7].rowHeight = var23.rowHeight;
                     }

                     var25 = var24.c(var1, var7, 0, var25);

                     for(var22 = var1; var22 <= var7; ++var22) {
                        var24.ej[var22].rowHeight = var25;
                     }
                  } else {
                     var25 = this.c(var1, var6 - 1, 0, var25);
                     this.f(var1, var6 - 1, var25);
                     var23.aA = this.ej[var1].aA;
                     var23.ar[0] = 0;
                     var23.setOffsetPosition(this.eR[2] - this.er[2]);
                     var23.setOffsetWidth(this.eR[2] - (this.ej[var6 - 1].ar[0] + this.ej[var6 - 1].ar[2]) - CELL_SPACING);
                     var23.setOffsetHeight(var25);
                     var7 = var23.h(var23.ay);
                     var23.aD = var23.a(var23.ay);
                     var7 = this.n(var6, var7);
                     var23.ar[3] = var7;
                     var23.ar[1] = var23.aA + var23.fA;
                     var25 = var23.ar[1] + var23.ar[3] - var23.aA > var25 ? var23.ar[1] + var23.ar[3] - var23.aA : var25;

                     for(var7 = var1; var7 <= var2; ++var7) {
                        var24.ej[var7].rowHeight = var25;
                     }

                     if (var24.er[2] > 0 && var24.er[2] < var24.eR[2] && (var23.B.isEmpty() || var23.fO.isEmpty())) {
                        var23.fB = true;
                     }
                  }

                  var7 = var25;
                  var22 = var6 < this.ek - 1 ? var6 + 1 : var6;
               } else {
                  var7 = this.c(var1, var6 - 1, this.er[2], var2);
                  this.f(var1, var6 - 1, var7);
               }

               this.ei[3] = this.ej[var6 - 1].aA + var7 + CELL_SPACING;
               if (!this.aE) {
                  this.er[0] = 0;
                  this.er[2] = this.eR[2];
               }

               this.er[1] = this.ei[3];
               var26 = this.er;
               var26[3] -= var7 + CELL_SPACING;
               var1 = var22;
               this.ew = this.ex;
               var3 = this.m(var6, var3);
               var2 = 0;
            }

            int var19 = this.n(var6, var3);
            if (var5.ar[2] != var3 || var5.ar[3] != var19) {
               var5.as = true;
            }

            if (!this.aE) {
               var5.ar[0] = this.er[0];
               var5.ar[3] = var19;
               var5.ar[1] = this.er[1];
            }

            var5.ar[2] = var3;
            if (var3 > 0) {
               if (this.aE) {
                  this.er[0] = var5.ar[0] + var5.ar[2] + CELL_SPACING;
                  this.er[2] = this.eR[2] - this.er[0];
               } else {
                  var26 = this.er;
                  var26[2] -= var3 + CELL_SPACING;
                  var26 = this.er;
                  var26[0] += var3 + CELL_SPACING;
               }
            }

            var2 = var19 > var2 ? var19 : var2;
         }

         int var9 = this.er[2];
         int var8;
         if (!this.aE) {
            var8 = this.c(var1, this.ek - 1, var9, var2);
            this.f(var1, this.ek - 1, var8);
            this.ei[3] = this.ej[this.ek - 1].aA + var8;
         }

         Form var20 = this;

         for(var8 = 0; !UIStyle.isAlignedLeftToRight && var8 < var20.ek; ++var8) {
            Item var21;
            (var21 = var20.ej[var8]).ar[0] = var20.eR[2] - var21.ar[0] - var21.ar[2];
            if (var21.aE) {
               ((StringItem)var21).setOffsetPosition(0);
            }
         }

         this.ev = this.getFormHeight();
         this.ae();
      }
   }

   final void b(Graphics var1) {
      super.b(var1);
      var1.setClip(this.eR[0], this.eR[1], this.eR[2], this.eR[3]);

      try {
         if (this.ek == 0) {
            TextBreaker var11 = TextBreaker.getBreaker(Font.getDefaultFont().getImpl(), TextDatabase.getText(33), false);
            TextLine var12 = null;
            (var12 = var11.getTextLine(32767)).setAlignment(2);
            ColorCtrl var4;
            int var13 = (var4 = var1.getImpl().getColorCtrl()).getFgColor();
            var4.setFgColor(UIStyle.COLOUR_TEXT);
            var1.getImpl().drawText(var12, (short)(eL.width / 2), (short)this.getMainZone().y);
            var4.setFgColor(var13);
            var11.destroyBreaker();
         } else {
            int[] var2;
            (var2 = new int[4])[0] = var1.getClipX();
            var2[1] = var1.getClipY();
            var2[2] = var1.getClipWidth();
            var2[3] = var1.getClipHeight();
            if (var2[1] + var2[3] > this.eR[1]) {
               this.eu = this.em = this.ei[1];

               for(int var3 = 0; var3 < this.ek; ++var3) {
                  Item var5 = this.ej[var3];
                  if (var5.ar != null) {
                     int var8 = var5.ar[0] + this.eR[0] - this.ei[0];
                     int var9;
                     if ((var9 = var5.ar[1] + this.eR[1] - this.ei[1]) + var5.ar[3] >= var2[1] && var9 <= var2[1] + var2[3] || this.eq) {
                        var1.clipRect(var8, var9, var5.ar[2], var5.ar[3]);
                        if (var1.getClipWidth() > 0 && var1.getClipHeight() > 0) {
                           if (this.eq && !var5.visible) {
                              var5.o();
                           }

                           if (var5.as) {
                              var5.g(var5.ar[2], var5.ar[3]);
                              var5.as = false;
                           }

                           var1.translate(var8, var9);
                           if (var5.aB == this.ep) {
                              var5.a(var1, var5.ar[2], var5.ar[3], true);
                           } else {
                              var5.a(var1, var5.ar[2], var5.ar[3], false);
                           }
                        } else if (this.eq && var5.visible) {
                           var5.x();
                        }

                        var1.a(var2[0], var2[1], var2[2], var2[3]);
                     }
                  }
               }

               if (this.ev > this.eR[3]) {
                  Zone var6 = this.getScrollbarZone();
                  var1.setClip(var6.x, var6.y, var6.width, var6.height);
                  Displayable.eI.drawScrollbar(var1.getImpl(), var6, 1, this.ev, this.eR[3], this.eu - this.eB + 1, true);
               }

               var1.a(var2[0], var2[1], var2[2], var2[3]);
            }
         }
      } catch (Throwable var10) {
      }
   }

   ItemStateListener getItemStateListener() {
      return this.et;
   }

   Item getCurrentItem() {
      return this.ep < 0 ? null : this.ej[this.ep];
   }

   final void D() {
      if (!this.isShown()) {
         this.fh = false;
      } else {
         super.D();
         if (!this.eH) {
            synchronized(Display.hG) {
               if (!this.fh) {
                  this.layout();
               }
            }

            this.traverse(0);
            this.ag();
         }

      }
   }

   final void a(Item var1) {
      ItemStateListener var4;
      if ((var4 = this.et) != null && var1 != null) {
         synchronized(Display.hH) {
            var4.itemStateChanged(var1);
         }
      }
   }

   private int a(int var1, Item var2) {
      if (this.eV != null && this.eV.getCurrentTopOfStackDisplayable().c((Displayable)this)) {
         if (this.ep >= var1 || this.ep == -1) {
            ++this.ep;
         }
      } else {
         this.gY = true;
      }

      if (this.ej.length == this.ek) {
         Item[] var3 = new Item[this.ek + 4];
         System.arraycopy(this.ej, 0, var3, 0, var1);
         System.arraycopy(this.ej, var1, var3, var1 + 1, this.ek - var1);
         this.ej = var3;
      } else if (var1 != this.ek) {
         System.arraycopy(this.ej, var1, this.ej, var1 + 1, this.ek - var1);
      }

      ++this.ek;
      this.ej[var1] = null;
      this.setImpl(var1, var2);
      return var1;
   }

   void setImpl(int var1, Item var2) {
      Item var3;
      if ((var3 = this.ej[var1]) != null) {
         var3.setOwner((Screen)null);
      }

      var2.setOwner(this);
      var2.aB = var1;
      this.ej[var1] = var2;
      ++var1;

      while(var1 < this.ek) {
         this.ej[var1].aB = var1++;
      }

      this.invalidate();
   }

   private void y(int var1) {
      if (this.eV == null || !this.eV.getCurrentTopOfStackDisplayable().c((Displayable)this)) {
         this.gY = true;
      }

      this.ej[var1].setOwner((Screen)null);
      --this.ek;
      if (this.ep == var1) {
         this.eo = 0;
         this.el = false;
      }

      if (this.ep > var1 || this.ep == this.ek) {
         --this.ep;
      }

      if (var1 < this.ek) {
         System.arraycopy(this.ej, var1 + 1, this.ej, var1, this.ek - var1);
      }

      this.ej[this.ek] = null;
      if (this.ek == 0 && this.ej.length > 4) {
         this.ej = new Item[4];
      }

      for(int var2 = var1; var2 < this.ek; this.ej[var2].aB = var2++) {
      }

      if (var1 == 0) {
         this.c(true);
      }

      this.invalidate();
   }

   private void ad() {
      this.el = false;
      this.en = false;
      if (this.ek != 0) {
         for(int var1 = 0; var1 < this.ek; ++var1) {
            this.ej[var1].aB = -1;
            this.ej[var1].setOwner((Screen)null);
            this.ej[var1] = null;
         }

         if (this.ej.length > 4) {
            this.ej = new Item[4];
         }

         this.ek = 0;
         this.eo = 0;
         this.ep = -1;
         this.invalidate();
      }
   }

   private void c(int var1, int var2, int var3) {
      this.el = false;
      this.eo = 0;
      int var4;
      if (var1 == 0) {
         Form var7 = this;
         int var5 = var4 = var3;
         boolean var6 = false;

         while(!var6 && var7.ej[var4].c()) {
            ++var4;
            if (var4 == var7.ek) {
               var6 = true;
            }
         }

         int var10000;
         label59: {
            if (var6) {
               if (var5 <= 0) {
                  var10000 = -1;
                  break label59;
               }

               var4 = var5 - 1;

               while(var7.ej[var4].c()) {
                  --var4;
                  if (var4 == -1) {
                     var10000 = -1;
                     break label59;
                  }
               }
            }

            var7.eo = 2;
            var10000 = var4;
         }

         var3 = var10000;
         if (var10000 == -1) {
            this.c(true);
            return;
         }
      }

      this.ep = var3;
      if (this.ej[this.ep].ar != null) {
         var4 = this.ep;
         Item var8 = this.ej[var4];
         this.es[0] = this.es[1] = 0;
         this.es[2] = this.eR[2] < var8.ar[2] ? this.eR[2] : var8.ar[2];
         this.es[3] = this.eR[3] < var8.ar[3] ? this.eR[3] : var8.ar[3];
         if (var2 >= 0 && var2 < this.ek && this.ej[var2].hasFocus) {
            this.ej[var2].f();
         }

         this.c(var1, this.ej[this.ep].ar);
         if (!this.ej[this.ep].isFocusable()) {
            this.A(var1);
         }

         this.x(var1);
         this.c(true);
         this.eq = true;
         this.em = this.ei[1];
         this.ag();
      }
   }

   private int getMinimumScroll(int var1) {
      int var2 = -1;
      int var3 = this.ej[this.ep].aA;

      int var4;
      for(var4 = this.ep - 1; var4 >= 0 && var3 == this.ej[var4].aA; --var4) {
      }

      ++var4;

      for(; var4 < this.ek && var3 == this.ej[var4].aA; ++var4) {
         int var5 = this.ej[var4].getMinimumScroll(var1);
         if ((this.ej[var4].j(this.ei[1] - var5, this.eR[3]) && var1 == 1 || this.ej[var4].j(this.ei[1] + var5, this.eR[3]) && var1 == 6 || this.ej[var4].visible) && (var2 == -1 || var5 < var2)) {
            var2 = var5;
         }
      }

      return var2;
   }

   private Item z(int var1) {
      while(this.ez <= this.ek - 1 && this.ej[this.ez].aA == var1) {
         ++this.ez;
      }

      --this.ez;
      return this.ej[this.ez];
   }

   private boolean k(int var1, int var2) {
      int var3;
      if ((var3 = var2 == -1 ? this.ez : var2) > this.ek - 1) {
         return false;
      } else {
         boolean var4 = false;

         for(Item var5 = this.ej[var3]; var5.aA == var1; var5 = this.ej[var3]) {
            if (!var4 && var5.c() && var5.ar[3] > this.eR[3] - FORM_MAX_SCROLL) {
               var4 = true;
            }

            ++var3;
            if (var3 > this.ek - 1) {
               break;
            }
         }

         this.ez = var2 == -1 ? var3 : this.ez;
         return var4;
      }
   }

   private boolean l(int var1, int var2) {
      int var3;
      if ((var3 = var2 == -1 ? this.ez : var2) < 0) {
         return false;
      } else {
         Item var4 = this.ej[var3];

         boolean var5;
         for(var5 = false; var4.aA == var1; var4 = this.ej[var3]) {
            if (!var5 && var4.c() && var4.ar[3] > this.eR[3] - FORM_MAX_SCROLL) {
               var5 = true;
            }

            --var3;
            if (var3 < 0) {
               break;
            }
         }

         this.ez = var2 == -1 ? var3 : this.ez;
         return var5;
      }
   }

   private static boolean d(int var0, int var1, int var2) {
      return var0 >= var1 && var0 <= var1 + var2;
   }

   private static int e(int var0, int var1, int var2) {
      int var3 = var1 - var0;
      var0 = var1 + var2 - var0;
      var3 = var3 >= 0 ? var3 : -var3;
      var0 = var0 >= 0 ? var0 : -var0;
      return var3 < var0 ? var3 : var0;
   }

   private int m(int var1, int var2) {
      Item var3;
      if ((var3 = this.ej[var1]).u()) {
         var2 = var3.aJ == null ? var3.a() : Math.max(var3.a(), var3.getLabelWidth());
         var3.aD = var3.a();
      } else if (var3.ax != -1) {
         var2 = var3.ax;
         var3.aD = var2;
      } else {
         var2 = var3.h(var3.ay);
         var3.aD = var3.a(var3.ay);
      }

      return var2 > this.eR[2] ? this.eR[2] : var2;
   }

   private int n(int var1, int var2) {
      int var3;
      Item var4;
      if ((var4 = this.ej[var1]).w()) {
         var3 = var4.b() + var4.getLabelHeight(-1);
      } else if ((var3 = var4.ay) == -1) {
         var3 = var4.b(var2) + var4.getLabelHeight(-1);
      }

      return var3;
   }

   private int c(int var1, int var2, int var3, int var4) {
      if (var3 < 0) {
         var3 = 0;
      }

      int var5 = var2;
      var4 = var1;
      Form var17 = this;
      int var6;
      int var7;
      int var9;
      int var11;
      int var12;
      Item var13;
      int var14;
      int var15;
      int var10000;
      int var16;
      int[] var20;
      if (var3 == 0) {
         this.p(var1, var2);
         var10000 = 0;
      } else {
         var6 = var3;
         var7 = 0;
         int[] var8 = new int[var2 - var1 + 1];

         for(var14 = var1; var14 <= var5; ++var14) {
            if ((var13 = var17.ej[var14]).u()) {
               var8[var14 - var4] = var13.a(-1) - var13.a();
               var7 += var8[var14 - var4];
               var6 = (var11 = var13.ar[2] - var13.aD) > 0 ? var6 + var11 : var6;
            }
         }

         if (var7 == 0) {
            var17.p(var4, var5);
         } else {
            var15 = var7;
            var11 = var6;
            var16 = var4;

            label213:
            while(true) {
               if (var16 > var5) {
                  var16 = var4;

                  while(true) {
                     if (var16 > var5) {
                        break label213;
                     }

                     if ((var13 = var17.ej[var16]).u() && var7 != 0) {
                        var9 = var8[var16 - var4] * var6 / var7;
                        var9 = var8[var16 - var4] < var9 ? var8[var16 - var4] : var9;
                        var20 = var13.ar;
                        var20[2] += var9;

                        for(var12 = var16 + 1; var12 <= var5; ++var12) {
                           var20 = var17.ej[var12].ar;
                           var20[0] += var9;
                        }

                        var13.aD = var13.ar[2];
                        var13.ar[3] = var17.n(var16, var13.ar[2]);
                     }

                     ++var16;
                  }
               }

               var14 = (var13 = var17.ej[var16]).ar[2] - var13.aD;
               if (var13.u() && var14 > 0) {
                  int var10 = var13.a() + var8[var16 - var4] * var11 / var15;
                  var10 = var13.a(-1) < var10 ? var13.a(-1) : var10;
                  if ((var12 = var13.ar[2] - var10) > 0) {
                     var6 -= var12;
                     var13.aD = var13.ar[2] > var13.a(-1) ? var13.a(-1) : var13.ar[2];
                  } else {
                     var9 = var10 - var13.ar[2];
                     var13.ar[2] = var13.aD = var10;

                     for(var12 = var16 + 1; var12 <= var5; ++var12) {
                        var20 = var17.ej[var12].ar;
                        var20[0] += var9;
                     }
                  }

                  var7 -= var8[var16 - var4];
                  var8[var16 - var4] = 0;
                  var13.ar[3] = var17.n(var16, var13.ar[2]);
               }

               ++var16;
            }
         }

         var10000 = var17.eR[2] - CELL_SPACING - (var17.ej[var5].ar[0] + var17.ej[var5].ar[2]);
      }

      var3 = var10000;
      var5 = var2;
      var4 = var1;
      var17 = this;
      if (var3 == 0) {
         this.o(var1, var2);
         var10000 = 0;
      } else {
         var6 = var3;
         var9 = 0;
         int[] var19 = new int[var2 - var1 + 1];

         for(var14 = var1; var14 <= var5; ++var14) {
            if ((var13 = var17.ej[var14]).v()) {
               var19[var14 - var4] = var13.a(-1);
               var9 += var19[var14 - var4];
               int var18;
               if ((var18 = var13.ar[2] - var13.aD) > 0) {
                  var6 += var18;
               }
            }
         }

         if (var9 == 0) {
            var17.o(var4, var5);
         } else {
            var14 = var9;
            var15 = var6;
            var16 = var4;

            label167:
            while(true) {
               if (var16 > var5) {
                  var16 = var4;

                  while(true) {
                     if (var16 > var5) {
                        break label167;
                     }

                     if ((var13 = var17.ej[var16]).v() && var9 != 0) {
                        var7 = var19[var16 - var4] * var6 / var9;
                        var13.ar[2] = var13.aD + var7;

                        for(var12 = var16 + 1; var12 <= var5; ++var12) {
                           var20 = var17.ej[var12].ar;
                           var20[0] += var7;
                        }

                        var13.aD = var13.ar[2];
                        var13.ar[3] = var17.n(var16, var13.ar[2]);
                     }

                     ++var16;
                  }
               }

               if ((var13 = var17.ej[var16]).v()) {
                  var11 = var13.ar[2] - var13.aD;
                  if (var13.v() && var11 > 0) {
                     var11 = var13.a(-1) + var19[var16 - var4] * var15 / var14;
                     if ((var12 = var13.ar[2] - var11) > 0) {
                        var6 -= var12;
                        var13.aD = var13.ar[2];
                     } else {
                        var7 = var11 - var13.ar[2];
                        var13.aD = var13.ar[2] = var13.aD = var11;

                        for(var12 = var16 + 1; var12 <= var5; ++var12) {
                           var20 = var17.ej[var12].ar;
                           var20[0] += var7;
                        }
                     }

                     var19[var16 - var4] = 0;
                     var13.ar[3] = var17.n(var16, var13.ar[2]);
                  }
               }

               ++var16;
            }
         }

         var10000 = var17.eR[2] - CELL_SPACING - (var17.ej[var5].ar[0] + var17.ej[var5].ar[2]);
      }

      var3 = var10000;
      var4 = 0;

      for(var5 = var1; var5 <= var2; ++var5) {
         if (var4 < this.ej[var5].ar[3]) {
            var4 = this.ej[var5].ar[3];
         }
      }

      if (var3 <= 0) {
         return var4;
      } else {
         if ((this.ew & 3) == 2) {
            while(var1 <= var2) {
               var20 = this.ej[var2].ar;
               var20[0] += var3;
               --var2;
            }
         } else if ((this.ew & 3) == 3) {
            for(var3 /= 2; var1 <= var2; ++var1) {
               var20 = this.ej[var1].ar;
               var20[0] += var3;
            }
         }

         return var4;
      }
   }

   private void o(int var1, int var2) {
      for(int var3 = var1; var3 <= var2; ++var3) {
         Item var4;
         if ((var4 = this.ej[var3]).v() && var4.ar[2] - var4.aD > 0) {
            var4.aD = var4.ar[2];
         }
      }

   }

   private void p(int var1, int var2) {
      for(int var3 = var1; var3 <= var2; ++var3) {
         Item var4;
         if ((var4 = this.ej[var3]).u() && var4.ar[2] - var4.aD > 0) {
            var4.aD = var4.ar[2] > var4.a(-1) ? (var4.aD = var4.a(-1)) : var4.ar[2];
         }
      }

   }

   private void f(int var1, int var2, int var3) {
      boolean var4 = false;
      var4 = false;

      for(int var7 = var1; var7 <= var2; ++var7) {
         Item var6;
         (var6 = this.ej[var7]).rowHeight = var3;
         int var8;
         if ((var6.av & 8192) == 8192) {
            var6.ar[3] = var3;
         } else if (var6.w()) {
            if ((var8 = var6.ay) == -1) {
               var8 = var6.b(var6.ar[2]) + var6.getLabelHeight(-1);
            }

            if (var8 > var3) {
               var8 = var3;
            }

            var6.ar[3] = var8;
         }

         int var5 = var6.ar[1];
         if (var1 != var2) {
            int[] var10000;
            switch(var6.y() & 48) {
            case 16:
            default:
               break;
            case 32:
               if ((var8 = var3 - var6.ar[3]) > 0) {
                  var10000 = var6.ar;
                  var10000[1] += var8;
               }
               break;
            case 48:
               if ((var8 = var3 - var6.ar[3]) > 0) {
                  var10000 = var6.ar;
                  var10000[1] += var8 / 2;
               }
            }
         }

         var6.aA = var5;
      }

   }

   private int getFormHeight() {
      Form var1 = this;

      int var2;
      for(var2 = 0; var2 < var1.ek && var1.ej[var2].c(); ++var2) {
      }

      int var4;
      int var5;
      int var6;
      if (var2 >= var1.ek) {
         var1.eB = 0;
      } else {
         var4 = var1.ej[var2].aA;
         var1.ez = var2;
         var1.eA = var1.z(var4).aB;
         if (var1.q(var4, var1.eA)) {
            var5 = var4;
            Form var3 = var1;
            var2 = var1.z(var4).aB;

            for(var4 = var1.ej[var2].rowHeight; var2 >= 0 && var3.ej[var2].aA == var5; --var2) {
               if (!var3.ej[var2].c()) {
                  var4 = (var6 = var3.ej[var2].ar[1]) < var4 ? var6 : var4;
               }
            }

            var4 = var4;
         }

         var1.eB = var4;
      }

      var1 = this;

      for(var2 = this.ek - 1; var2 > 0 && var1.ej[var2].c(); --var2) {
      }

      if (var2 < 0) {
         var1.eC = var1.eR[3];
      } else {
         Item var7;
         var4 = (var7 = var1.ej[var2]).aA;
         int var8 = var7.rowHeight;
         var5 = var4 + var8;
         var1.ez = var2;
         var2 = var1.z(var4).aB;
         if (var1.q(var4, var2)) {
            var6 = var4;
            Form var9 = var1;
            var2 = var2;

            for(var8 = var1.ej[var2].aA; var2 < var9.ek && var9.ej[var2].aA == var6; ++var2) {
               if (!var9.ej[var2].c()) {
                  var8 = (var5 = var9.ej[var2].ar[1] + var9.ej[var2].ar[3]) > var8 ? var5 : var8;
               }
            }

            var5 = var8;
         }

         var1.eC = var5;
      }

      return this.eC - this.eB;
   }

   private int getScrollingDirection(int var1) {
      int var2 = var1;
      if (var1 == 2 && !UIStyle.isAlignedLeftToRight) {
         var2 = 5;
      } else if (var1 == 5 && !UIStyle.isAlignedLeftToRight) {
         var2 = 2;
      }

      return var2;
   }

   private void ae() {
      if (this.ev > this.eR[3]) {
         this.ei[1] = this.ei[1] > this.eC - this.eR[3] ? this.eC - this.eR[3] : this.ei[1];
         this.ei[1] = this.ei[1] < this.eB ? this.eB : this.ei[1];
      } else {
         this.ei[1] = this.eB;
      }
   }

   private void A(int var1) {
      if (var1 == 1) {
         if (this.em - this.ei[1] < FORM_MAX_SCROLL) {
            this.ei[1] = this.em - FORM_MAX_SCROLL;
            this.eu = this.ei[1];
         }
      } else if (var1 == 6 && this.ei[1] - this.em < FORM_MAX_SCROLL) {
         this.ei[1] = this.em + FORM_MAX_SCROLL;
         this.eu = this.ei[1];
      }

      this.ae();
   }

   Zone getScrollbarZone() {
      return this.eQ != null ? Displayable.eO : Displayable.eM;
   }

   Command[] getExtraCommands() {
      Command[] var1 = null;
      if (this.ep >= 0) {
         var1 = this.ej[this.ep].getExtraCommands();
      }

      return var1;
   }

   final boolean a(Command var1) {
      return this.ep >= 0 ? this.ej[this.ep].a(var1) : false;
   }

   private boolean q(int var1, int var2) {
      for(var2 = var2; var2 >= 0 && !this.ej[var2].c() && this.ej[var2].aA == var1; --var2) {
      }

      return var2 > 0 && this.ej[var2].aA == var1;
   }

   private void setHighlightedItem(Item var1) {
   }

   private void setTopItem(Item var1) {
   }

   private void setTopAndHighlightedItems(Item var1) {
   }

   int getViewPortHeight() {
      return this.eR[3];
   }

   static {
      CELL_SPACING = UIStyle.CELL_SPACING;
      FORM_MAX_SCROLL = UIStyle.FORM_MAX_SCROLL;
      eh = UIStyle.isFourWayScroll();
      eE = false;
   }
}
