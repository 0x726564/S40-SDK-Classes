package javax.microedition.lcdui;

class JavaTextHandler extends InlineTextHandler {
   private String text;
   private int cursorPosition;

   JavaTextHandler(int maxSize, int constraints, TextEditor owner) {
      this.text = "";
      this.maxSize = maxSize;
      this.constraints = constraints;
      this.owner = owner;
   }

   JavaTextHandler(InlineTextHandler textHandler) {
      this(textHandler.getMaxSize(), textHandler.getConstraints(), textHandler.getOwner());
      this.setFocus(textHandler.isFocused());
      this.setInitialInputMode(textHandler.getInitialInputMode());
      this.setString(textHandler.getString(), textHandler.getCursorPosition());
   }

   public void setString(String newText, int newCursorPosition) {
      this.text = newText;
      this.cursorPosition = newCursorPosition;
   }

   public int getCursorPosition() {
      return this.cursorPosition;
   }

   public String getString() {
      return this.text;
   }

   public int getHeight(int width, int maxAvailableHeight) {
      return NativeTextHandler.getTextHeight(this.getMaxSize(), this.getString(), width, maxAvailableHeight, this.constraints);
   }

   public int size() {
      return this.text.length();
   }

   public void destroy() {
      super.destroy();
      this.text = null;
   }
}
