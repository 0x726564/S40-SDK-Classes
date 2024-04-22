package javax.microedition.lcdui;

class JavaTextHandler extends InlineTextHandler {
   private String text;
   private int cursorPosition;

   JavaTextHandler(int var1, int var2, TextEditor var3) {
      this.text = "";
      this.I = var1;
      this.H = var2;
      this.J = var3;
   }

   JavaTextHandler(InlineTextHandler var1) {
      this(var1.getMaxSize(), var1.getConstraints(), var1.getOwner());
      this.setFocus(var1.isFocused());
      this.setInitialInputMode(var1.getInitialInputMode());
      this.a(var1.getString(), var1.getCursorPosition());
   }

   public final void a(String var1, int var2) {
      this.text = var1;
      this.cursorPosition = var2;
   }

   public int getCursorPosition() {
      return this.cursorPosition;
   }

   public String getString() {
      return this.text;
   }

   public final int a(int var1, int var2) {
      return NativeTextHandler.a(this.getMaxSize(), this.getString(), var1, var2);
   }

   public final int size() {
      return this.text.length();
   }

   public final void destroy() {
      super.destroy();
      this.text = null;
   }
}
