package javax.microedition.lcdui;

class JavaTextHandler extends InlineTextHandler {
   private String text;
   private int cursorPosition;

   JavaTextHandler(int var1, int var2, TextEditor var3) {
      this.text = "";
      this.cursorPosition = 0;
      this.maxSize = var1;
      this.constraints = var2;
      this.owner = var3;
   }

   JavaTextHandler(InlineTextHandler var1) {
      this(var1.getMaxSize(), var1.getConstraints(), var1.getOwner());
      this.setFocus(var1.isFocused());
      this.setInitialInputMode(var1.getInitialInputMode());
      this.setString(var1.getString(), var1.getCursorPosition());
   }

   public void setString(String var1, int var2) {
      this.text = var1;
      this.cursorPosition = var2;
   }

   public int getCursorPosition() {
      return this.cursorPosition;
   }

   public String getString() {
      return this.text;
   }

   public int getHeight(int var1, int var2) {
      return NativeTextHandler.getTextHeight(this.getMaxSize(), this.getString(), var1, var2);
   }

   public int size() {
      return this.text.length();
   }

   public void destroy() {
      super.destroy();
      this.text = null;
   }

   public void setCursorPosition(int var1) {
      this.cursorPosition = var1;
   }
}
