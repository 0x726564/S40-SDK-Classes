package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;

public class TextBox extends Screen {
   private static final Zone iz;
   private static final Zone iA;
   private TextEditor cu;

   public TextBox(String var1, String var2, int var3, int var4) {
      super(var1);
      synchronized(Display.hG) {
         this.cu = new TextEditor(var2, var3, var4, new TextBox.TextEditorOwnerImpl(this, this));
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

   final void a(Display var1) {
      super.a(var1);
      synchronized(Display.hG) {
         this.cu.setOptionsChangedProcessed(true);
         this.cu.showNotify();
         this.cu.setFocus(true);
      }
   }

   final void b(Display var1) {
      super.b(var1);
      synchronized(Display.hG) {
         this.cu.hideNotify();
      }
   }

   final void d(Display var1) {
      super.d(var1);
      synchronized(Display.hG) {
         this.cu.setFocus(false);
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

   final void D() {
      super.D();
      synchronized(Display.hG) {
         if (!this.fh) {
            this.fh = true;
         }
      }

      this.ag();
   }

   final void b(Graphics var1) {
      super.b(var1);
      synchronized(Display.hG) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics var8 = var1.getImpl();
         Zone var3 = this.getMainZone();
         ColorCtrl var4;
         int var5 = (var4 = var8.getColorCtrl()).getFgColor();
         int var6 = var4.getBgColor();
         var4.setFgColor(UIStyle.COLOUR_WHITE);
         var4.setBgColor(UIStyle.COLOUR_WHITE);
         var8.fillRect(var3.x, var3.y, var3.width, var3.height);
         var4.setFgColor(var5);
         var4.setBgColor(var6);
         this.cu.a(var8, var3.x + var3.getMarginLeft(), var3.y + var3.getMarginTop(), var3.width - var3.getMarginLeft() - var3.getMarginRight(), var3.height - var3.getMarginTop() - var3.getMarginBottom());
      }
   }

   Zone getMainZone() {
      return this.eQ != null ? iA : iz;
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

   static TextEditor a(TextBox var0) {
      return var0.cu;
   }

   static {
      iz = eI.getZone(51);
      iA = eI.getZone(52);
   }

   class TextEditorOwnerImpl implements TextEditorOwner {
      private TextBox bo;
      private final TextBox bp;

      TextEditorOwnerImpl(TextBox var1, TextBox var2) {
         this.bp = var1;
         this.bo = var2;
      }

      public Displayable getDisplayable() {
         return this.bo;
      }

      public final boolean hasFocus() {
         return TextBox.a(this.bp).isFocused();
      }

      public final void I() {
      }

      public int getCursorWrap() {
         return 1;
      }

      public final void l(int var1) {
         if ((var1 & 1024) != 0) {
            this.bp.ah();
         }

         if ((var1 & 1) != 0) {
            TextBox.a(this.bp).F();
         }

         if ((var1 & 64) != 0) {
            this.bp.ag();
         }

         if ((var1 & 32) != 0) {
            this.bp.invalidate();
         }

         if ((var1 & 16) != 0 && TextBox.a(this.bp).isFocused()) {
            TextBox.a(this.bp).l();
            this.bp.c(true);
         }

      }
   }
}
