package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;

public abstract class CustomItem extends Item {
   protected static final int TRAVERSE_HORIZONTAL = 1;
   protected static final int TRAVERSE_VERTICAL = 2;
   protected static final int KEY_PRESS = 4;
   protected static final int KEY_RELEASE = 8;
   protected static final int KEY_REPEAT = 16;
   protected static final int POINTER_PRESS = 32;
   protected static final int POINTER_RELEASE = 64;
   protected static final int POINTER_DRAG = 128;
   protected static final int NONE = 0;
   private static final int de;
   private static final Command df;
   private static Command[] bd;
   private int D = 0;
   private ImageItem dg = null;
   private String dh = null;
   private boolean di = false;
   private boolean dj = false;
   private static final boolean JMS_BLENDED_HIGHLIGHT_SUPPORT;

   protected CustomItem(String var1) {
      super(var1);
   }

   final boolean n() {
      return false;
   }

   public int getGameAction(int var1) {
      int var2 = Displayable.eK.getGameAction(var1);
      if (-127 == var2) {
         throw new IllegalArgumentException("getGameAction: Invalid keyCode");
      } else {
         return var2;
      }
   }

   protected final int getInteractionModes() {
      if (!Form.eh) {
         return UIStyle.isRotator() ? 2 : 30;
      } else {
         return 31;
      }
   }

   protected abstract int getMinContentWidth();

   protected abstract int getMinContentHeight();

   protected abstract int getPrefContentWidth(int var1);

   protected abstract int getPrefContentHeight(int var1);

   protected void sizeChanged(int var1, int var2) {
   }

   protected final void invalidate() {
      super.invalidate();
   }

   protected abstract void paint(Graphics var1, int var2, int var3);

   protected final void repaint() {
      if (this.dg != null) {
         this.dg.repaint();
      } else {
         super.repaint();
      }
   }

   protected final void repaint(int var1, int var2, int var3, int var4) {
      if (this.au != null && this.ar != null) {
         if (this.dg != null) {
            this.dg.repaint(var1, var2, var3, var4);
         } else if (var1 <= this.ar[2]) {
            var1 = var1 < 0 ? 0 : var1;
            int var10000 = var2 < 0 ? 0 : var2;
            int var10001 = var2 < 0 ? 0 : var2;
            if ((var2 = var10000 + this.D) <= this.ar[3]) {
               var3 = var1 + var3 > this.ar[2] ? this.ar[2] - var1 : var3;
               var4 = var2 + var4 > this.ar[3] ? this.ar[3] - var2 : var4;
               super.repaint(var1, var2, var3, var4);
            }
         }
      }
   }

   protected boolean traverse(int var1, int var2, int var3, int[] var4) {
      return false;
   }

   protected void traverseOut() {
   }

   protected void keyPressed(int var1) {
   }

   protected void keyReleased(int var1) {
   }

   protected void keyRepeated(int var1) {
   }

   protected void pointerPressed(int var1, int var2) {
   }

   protected void pointerReleased(int var1, int var2) {
   }

   protected void pointerDragged(int var1, int var2) {
   }

   protected void showNotify() {
   }

   protected void hideNotify() {
   }

   final int a(int var1) {
      if (this.dg != null) {
         return this.dg.a(var1) + 2 * de;
      } else {
         try {
            boolean var2;
            int var3;
            synchronized(Display.hH) {
               var2 = (var3 = this.getPrefContentWidth(var1)) == 0 || this.getPrefContentHeight(this.aD) == 0;
            }

            synchronized(Display.hG) {
               return var2 && this.aG.length() >= 1 ? this.a((Font)null) + 2 * de : var3 + 2 * de;
            }
         } catch (Throwable var7) {
            this.a(var7);
            return -1;
         }
      }
   }

   final int b(int var1) {
      if (this.dg != null) {
         return this.dg.b(var1) + 2 * de;
      } else {
         try {
            boolean var2;
            int var3;
            synchronized(Display.hH) {
               var2 = (var3 = this.getPrefContentHeight(var1)) == 0 || this.getPrefContentWidth(var3) == 0;
            }

            synchronized(Display.hG) {
               return var2 && this.aG.length() >= 1 ? this.a(ak, (Font)null) + 2 * de : var3 + 2 * de;
            }
         } catch (Throwable var7) {
            this.a(var7);
            return -1;
         }
      }
   }

   final int a() {
      if (this.dg != null) {
         return this.dg.a() + 2 * de;
      } else {
         try {
            boolean var1;
            int var2;
            synchronized(Display.hH) {
               var1 = (var2 = this.getMinContentWidth()) == 0 || this.getPrefContentHeight(this.aD) == 0;
            }

            synchronized(Display.hG) {
               var2 = var1 && this.aG.length() >= 1 ? this.a((Font)null) + 2 * de : var2 + 2 * de;
               return this.aJ != null && var2 < Item.aq ? Item.aq : var2;
            }
         } catch (Throwable var7) {
            this.a(var7);
            return -1;
         }
      }
   }

   final int b() {
      if (this.dg != null) {
         return this.dg.b() + 2 * de;
      } else {
         try {
            boolean var1;
            int var2;
            synchronized(Display.hH) {
               var1 = (var2 = this.getMinContentHeight()) == 0 || this.getPrefContentWidth(var2) == 0;
            }

            synchronized(Display.hG) {
               return var1 && this.aG.length() >= 1 ? this.a(ak, (Font)null) + 2 * de : var2 + 2 * de;
            }
         } catch (Throwable var7) {
            this.a(var7);
            return -1;
         }
      }
   }

   final void g(int var1, int var2) {
      if (this.dg != null) {
         this.dg.g(var1, var2);
      } else {
         super.g(var1, var2);
         this.D = this.getLabelHeight(var1);

         try {
            synchronized(Display.hH) {
               this.sizeChanged(var1 - 2 * de, var2 - this.D - 2 * de);
            }
         } catch (Throwable var5) {
            this.a(var5);
         }

      }
   }

   final void a(Graphics var1, int var2, int var3, boolean var4) {
      if (this.visible) {
         int var6;
         if (this.dg != null) {
            synchronized(Display.hG) {
               if (var4 && !this.dj) {
                  var6 = var1.getColor();
                  var1.setColor(UIStyle.COLOUR_HIGHLIGHT);
                  Displayable.eI.drawBorder(var1.getImpl(), var1.getTranslateX(), var1.getTranslateY() + this.dg.getLabelHeight(var2), this.aD, var3 - this.dg.getLabelHeight(var2), UIStyle.BORDER_IMAGE_HIGHLIGHT, var4);
                  var1.setColor(var6);
                  var1.translate(de, de);
               }
            }

            this.dg.ar = new int[4];
            this.dg.ar[0] = this.ar[0];
            this.dg.ar[1] = this.ar[1];
            this.dg.ar[2] = this.ar[2];
            this.dg.ar[3] = this.ar[3];
            if (this.hasFocus && !var4) {
               this.dg.ab();
            } else if ((!this.hasFocus || this.as) && var4) {
               this.dg.ac();
            }

            this.dg.a(var1, var2 - 2 * de, var3 - 2 * de, var4);
            this.hasFocus = var4;
         } else {
            super.a(var1, var2, var3, var4);
            this.hasFocus = var4;

            try {
               int var5 = this.getPrefContentHeight(this.aD);
               var5 = var3 - this.D > var5 + 2 * de ? var5 + 2 * de : var3 - this.D;
               var6 = this.getPrefContentWidth(var5) + 2 * de;
               var6 = this.aD > var6 ? var6 : this.aD;
               var6 = this.v() ? this.ar[2] : var6;
               boolean var7 = this.getPrefContentWidth(var3 - this.D - 2 * de) == 0 || this.getPrefContentHeight(0) == 0;
               synchronized(Display.hG) {
                  if (var4 && !this.dj && (!var7 || this.aG.length() >= 1)) {
                     int var9 = var1.getColor();
                     var1.setColor(UIStyle.COLOUR_HIGHLIGHT);
                     Displayable.eI.drawBorder(var1.getImpl(), var1.getTranslateX(), var1.getTranslateY(), var7 ? var2 : var6, var7 ? var3 - this.D : var5, UIStyle.BORDER_IMAGE_HIGHLIGHT, var4);
                     var1.setColor(var9);
                  }

                  var1.translate(de, de);
               }

               if (!var7) {
                  var1.setClip(0, 0, this.aD - 2 * de, var3 - this.D - 2 * de);
                  this.paint(var1, this.aD - 2 * de, var3 - this.D - 2 * de);
                  return;
               }

               synchronized(Display.hG) {
                  if (this.aG.length() >= 1) {
                     this.a(var1, var1.getTranslateX(), var1.getTranslateY(), var2 - 2 * de, var3 - this.D - 2 * de, var4);
                     return;
                  }
               }
            } catch (Throwable var13) {
               this.a(var13);
            }

         }
      }
   }

   final boolean a(int var1, int var2, int var3, int[] var4) {
      if (this.dg != null) {
         return this.dg.a(var1, var2, var3, var4);
      } else {
         this.hasFocus = true;

         try {
            synchronized(Display.hH) {
               boolean var9 = this.traverse(var1, var2, var3 - this.D, var4);
               synchronized(Display.hG) {
                  if (!this.di) {
                     this.dj = var9;
                     if (this.dj) {
                        this.di = true;
                     }
                  }
               }

               if (var9) {
                  var4[1] = var4[1] + this.getLabelHeight(-1) + de;
                  if (var4[1] > this.ar[3]) {
                     var4[1] = this.ar[3];
                  } else if (var4[1] < 0) {
                     var4[1] = 0;
                  }

                  if (var4[0] > this.ar[2]) {
                     var4[0] = this.ar[2];
                  } else if (var4[0] < 0) {
                     var4[0] = 0;
                  }

                  if (var4[1] + var4[3] > this.ar[3]) {
                     var4[3] = this.ar[3] - var4[1];
                  } else if (var4[3] < 0) {
                     var4[3] = 0;
                  }

                  if (var4[0] + var4[2] > this.ar[2]) {
                     var4[2] = this.ar[2] - var4[0];
                  } else if (var4[2] < 0) {
                     var4[2] = 0;
                  }
               }

               this.repaint();
               return var9;
            }
         } catch (Throwable var8) {
            this.a(var8);
            return false;
         }
      }
   }

   final void f() {
      super.f();
      synchronized(Display.hG) {
         this.di = false;
         this.dj = false;
      }

      if (this.dg != null) {
         this.dg.f();
      } else {
         try {
            synchronized(Display.hH) {
               this.traverseOut();
               this.repaint(0, 0, this.ar[2], this.ar[3]);
            }
         } catch (Throwable var4) {
            this.a(var4);
         }

      }
   }

   final void c(int var1, int var2) {
      if (var1 == -10) {
         super.c(var1, var2);
      } else {
         try {
            synchronized(Display.hH) {
               this.keyPressed(var1);
            }
         } catch (Throwable var4) {
            this.a(var4);
         }

      }
   }

   final void h(int var1, int var2) {
      if (this.dg != null) {
         this.dg.h(var1, var2);
      } else {
         try {
            synchronized(Display.hH) {
               this.keyReleased(var1);
            }
         } catch (Throwable var4) {
            this.a(var4);
         }

      }
   }

   final void i(int var1, int var2) {
      if (this.dg != null) {
         this.dg.i(var1, var2);
      } else {
         try {
            synchronized(Display.hH) {
               this.keyRepeated(var1);
            }
         } catch (Throwable var4) {
            this.a(var4);
         }

      }
   }

   final void o() {
      if (this.dg != null) {
         this.visible = true;
         this.dg.o();
      } else {
         super.o();

         try {
            synchronized(Display.hH) {
               this.showNotify();
            }
         } catch (Throwable var4) {
            this.a(var4);
         }

      }
   }

   final void x() {
      if (this.dg != null) {
         this.dg.x();
      } else {
         super.x();

         try {
            synchronized(Display.hH) {
               this.hideNotify();
            }
         } catch (Throwable var4) {
            this.a(var4);
         }

      }
   }

   void setOwner(Screen var1) {
      super.setOwner(var1);
      synchronized(Display.hG) {
         this.di = false;
         this.dj = false;
      }
   }

   final boolean isFocusable() {
      int var1 = this.getPrefContentHeight(this.aD);
      return this.aG.length() >= 1 || var1 != 0 && this.getPrefContentWidth(var1) != 0;
   }

   final boolean b(Command var1) {
      return this.dg == null ? super.b(var1) : false;
   }

   final boolean d(Command var1) {
      return this.dg == null ? super.d(var1) : false;
   }

   final boolean c(Command var1) {
      return this.dg == null ? super.c(var1) : false;
   }

   private void a(Throwable var1) {
      this.dh = var1.toString();
      if (this.dg == null && this.au != null) {
         synchronized(Display.hG) {
            this.aw = null;
            this.aG.reset();
            this.dg = new ImageItem(this.getLabel(), new Image(Pixmap.createPixmap(13)), this.av, (String)null, 1);
            this.dg.setOwner(this.au);
            if (this.visible) {
               this.dg.o();
            }

            if (!((Form)this.au).en) {
               ((Form)this.au).en = true;
               this.U();
            }
         }

         super.invalidate();
      }

   }

   Command[] getExtraCommands() {
      return this.dg != null ? bd : null;
   }

   final boolean a(Command var1) {
      if (this.dg != null) {
         this.U();
         return true;
      } else {
         return false;
      }
   }

   private void U() {
      Alert var1;
      (var1 = new Alert(this.au.getTitle(), TextDatabase.getText(1), (Image)null, (AlertType)null)).setLastPageText(this.dh);
      this.au.eV.setCurrent(var1);
   }

   static {
      de = UIStyle.CUSTOMITEM_BORDER_PAD;
      df = new Command(9, 73);
      bd = new Command[]{df};
      JMS_BLENDED_HIGHLIGHT_SUPPORT = false;
   }
}
