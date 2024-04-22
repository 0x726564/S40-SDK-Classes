package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;

public class TextField extends Item {
   public static final int ANY = 0;
   public static final int EMAILADDR = 1;
   public static final int NUMERIC = 2;
   public static final int PHONENUMBER = 3;
   public static final int URL = 4;
   public static final int DECIMAL = 5;
   public static final int PASSWORD = 65536;
   public static final int UNEDITABLE = 131072;
   public static final int SENSITIVE = 262144;
   public static final int NON_PREDICTIVE = 524288;
   public static final int INITIAL_CAPS_WORD = 1048576;
   public static final int INITIAL_CAPS_SENTENCE = 2097152;
   public static final int CONSTRAINT_MASK = 65535;
   private static Zone ct;
   TextEditor cu;
   int preferredHeight;

   public TextField(String var1, String var2, int var3, int var4) {
      super(var1);
      synchronized(Display.hG) {
         this.cu = new TextEditor(var2, var3, var4, new TextField.TextEditorOwnerImpl(this, this));
      }
   }

   public String getString() {
      synchronized(Display.hG) {
         return this.cu.getString();
      }
   }

   public void setString(String var1) {
      synchronized(Display.hG) {
         this.cu.setString(var1);
      }
   }

   public int getChars(char[] var1) {
      synchronized(Display.hG) {
         return this.cu.getChars(var1);
      }
   }

   public void setChars(char[] var1, int var2, int var3) {
      synchronized(Display.hG) {
         this.cu.setChars(var1, var2, var3);
      }
   }

   public void insert(String var1, int var2) {
      synchronized(Display.hG) {
         this.cu.insert(var1, var2);
      }
   }

   public void insert(char[] var1, int var2, int var3, int var4) {
      synchronized(Display.hG) {
         this.cu.insert(var1, var2, var3, var4);
      }
   }

   public void delete(int var1, int var2) {
      synchronized(Display.hG) {
         this.cu.delete(var1, var2);
      }
   }

   public int getMaxSize() {
      synchronized(Display.hG) {
         return this.cu.getMaxSize();
      }
   }

   public int setMaxSize(int var1) {
      synchronized(Display.hG) {
         return this.cu.setMaxSize(var1);
      }
   }

   public int size() {
      synchronized(Display.hG) {
         return this.cu.size();
      }
   }

   public int getCaretPosition() {
      synchronized(Display.hG) {
         return this.cu.getCursorPosition();
      }
   }

   public void setConstraints(int var1) {
      synchronized(Display.hG) {
         this.cu.setConstraints(var1);
      }
   }

   public int getConstraints() {
      synchronized(Display.hG) {
         return this.cu.getConstraints();
      }
   }

   public void setInitialInputMode(String var1) {
      synchronized(Display.hG) {
         this.cu.setInitialInputMode(var1);
      }
   }

   void setOwner(Screen var1) {
      super.setOwner(var1);
      if (var1 == null) {
         this.cu.setFocus(false);
         this.cu.hideNotify();
      }

   }

   final void x() {
      super.x();
      synchronized(Display.hG) {
         this.cu.hideNotify();
      }
   }

   final void c(int var1, int var2) {
      super.c(var1, var2);
      synchronized(Display.hG) {
         this.cu.d(var1, var2);
      }
   }

   final void h(int var1, int var2) {
      super.h(var1, var2);
      synchronized(Display.hG) {
         this.cu.e(var1, var2);
      }
   }

   final void i(int var1, int var2) {
      super.i(var1, var2);
      synchronized(Display.hG) {
         this.cu.f(var1, var2);
      }
   }

   final int b() {
      synchronized(Display.hG) {
         this.S();
         return this.preferredHeight;
      }
   }

   final int a() {
      return ct.width;
   }

   final void a(Graphics var1, int var2, int var3, boolean var4) {
      synchronized(Display.hG) {
         super.a(var1, var2, var3, var4);
         var2 = var1.getTranslateX();
         if ((var3 = var1.getTranslateY()) <= var1.getHeight() && var3 + this.getHeight() >= 0) {
            ColorCtrl var6;
            com.nokia.mid.impl.isa.ui.gdi.Graphics var11;
            int var7 = (var6 = (var11 = var1.getImpl()).getColorCtrl()).getFgColor();
            int var8 = var6.getBgColor();
            var2 += ct.x;
            var3 += ct.y;
            Displayable.eI.drawBorder(var11, var2, var3, ct.width, this.getHeight(), ct.getBorderType(), var4);
            if (var4) {
               var6.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
               var6.setBgColor(UIStyle.COLOUR_SCHEME_HIGHLIGHT);
            } else {
               var6.setFgColor(UIStyle.COLOUR_EDITOR_UNFOCUSED);
               var6.setBgColor(UIStyle.COLOUR_BACKGROUND);
            }

            var2 += ct.getMarginLeft();
            var3 += ct.getMarginTop();
            int var12 = ct.width - ct.getMarginLeft() - ct.getMarginRight();
            int var9 = this.getHeight() - ct.getMarginTop() - ct.getMarginBottom();
            this.cu.a(var11, var2, var3, var12, var9);
            var6.setFgColor(var7);
            var6.setBgColor(var8);
         }
      }
   }

   final int b(int var1) {
      synchronized(Display.hG) {
         this.S();
         return this.preferredHeight;
      }
   }

   final int a(int var1) {
      return ct.width;
   }

   final void o() {
      super.o();
      synchronized(Display.hG) {
         this.cu.showNotify();
      }
   }

   final boolean a(int var1, int var2, int var3, int[] var4) {
      super.a(var1, var2, var3, var4);
      synchronized(Display.hG) {
         this.cu.setFocus(true);
         var4[0] = 0;
         var4[1] = 0;
         var4[2] = ct.width;
         var4[3] = this.preferredHeight + this.getLabelHeight(-1);
         return true;
      }
   }

   final void f() {
      super.f();
      synchronized(Display.hG) {
         this.cu.setFocus(false);
      }
   }

   final boolean d() {
      return true;
   }

   final boolean e() {
      return true;
   }

   final boolean isFocusable() {
      return true;
   }

   final boolean z() {
      return true;
   }

   final boolean m() {
      return this.cu.m();
   }

   final boolean a(Command var1) {
      return this.cu.a(var1);
   }

   Command[] getExtraCommands() {
      return this.cu.getExtraCommands();
   }

   private int getHeight() {
      return this.preferredHeight;
   }

   final void S() {
      this.preferredHeight = ct.getMarginTop() + ct.getMarginBottom();
      if (this.au != null && this.au instanceof Form) {
         int var1 = ((Form)this.au).getViewPortHeight() - this.getLabelHeight(-1) - ct.getMarginTop() - ct.getMarginBottom();
         int var2 = ct.width - ct.getMarginLeft() - ct.getMarginRight();
         this.preferredHeight += this.cu.a(var2, var1);
      }

      if (this.preferredHeight < ct.height) {
         this.preferredHeight = ct.height;
      }

   }

   static {
      ct = Item.ao;
   }

   class TextEditorOwnerImpl implements TextEditorOwner {
      private TextField dc;
      private final TextField dd;

      TextEditorOwnerImpl(TextField var1, TextField var2) {
         this.dd = var1;
         this.dc = var2;
      }

      public Displayable getDisplayable() {
         return this.dd.au;
      }

      public final boolean hasFocus() {
         return this.dd.hasFocus;
      }

      public final void I() {
         if (this.dd.au != null) {
            this.dd.au.b(this.dc);
         }

      }

      public int getCursorWrap() {
         return 2;
      }

      public final void l(int var1) {
         if (this.dd.au != null) {
            if ((var1 & 1024) != 0) {
               this.dd.au.ah();
            }

            if ((var1 & 1) != 0) {
               this.dd.cu.F();
               this.dd.au.b(this.dc);
            }

            if ((var1 & 64) != 0) {
               this.dd.repaint();
            }

            int var2;
            if ((var1 & 32) != 0) {
               var2 = this.dd.preferredHeight;
               this.dd.S();
               if (var2 != this.dd.preferredHeight) {
                  this.dd.invalidate();
               }
            }

            if ((var1 & 16) != 0 && this.dd.hasFocus) {
               this.dd.cu.l();
               this.dd.au.c(true);
            }

            if (((var1 & 4) != 0 || (var1 & 8) != 0) && this.dd.au instanceof Form) {
               var2 = (var1 & 4) != 0 ? 1 : 6;
               ((Form)this.dd.au).w(var2);
            }

         }
      }
   }
}
