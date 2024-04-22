package javax.microedition.lcdui;

abstract class InlineTextHandler {
   protected String G;
   protected int H;
   protected int I;
   protected TextEditor J;

   public abstract String getString();

   public abstract void a(String var1, int var2);

   public abstract int getCursorPosition();

   public abstract int size();

   public int getConstraints() {
      return this.H;
   }

   public void setConstraints(int var1) {
      this.H = var1;
   }

   public void setFocus(boolean var1) {
   }

   public String getInitialInputMode() {
      return this.G;
   }

   public void setInitialInputMode(String var1) {
      this.G = var1;
   }

   public int getMaxSize() {
      return this.I;
   }

   public void setMaxSize(int var1) {
      this.I = var1;
   }

   public void d(int var1, int var2) {
   }

   public void e(int var1, int var2) {
   }

   public void f(int var1, int var2) {
   }

   public Command[] getExtraCommands() {
      return null;
   }

   public void l() {
   }

   public boolean a(Command var1) {
      return false;
   }

   public void a(com.nokia.mid.impl.isa.ui.gdi.Graphics var1, int var2, int var3, int var4, int var5) {
   }

   public int a(int var1, int var2) {
      return 0;
   }

   public void destroy() {
      this.J = null;
   }

   public boolean m() {
      return true;
   }

   protected final boolean isFocused() {
      return this.J != null ? this.J.isFocused() : false;
   }

   protected TextEditor getOwner() {
      return this.J;
   }
}
