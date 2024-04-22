package javax.microedition.lcdui;

abstract class InlineTextHandler {
   protected String initialInputMode;
   protected int constraints;
   protected int maxSize;
   protected TextEditor owner;

   public abstract String getString();

   public abstract void setString(String var1, int var2);

   public abstract int getCursorPosition();

   public abstract int size();

   public int getConstraints() {
      return this.constraints;
   }

   public void setConstraints(int newConstraints) {
      this.constraints = newConstraints;
   }

   public void setFocus(boolean newFocus) {
   }

   public String getInitialInputMode() {
      return this.initialInputMode;
   }

   public void setInitialInputMode(String newInitialInputMode) {
      this.initialInputMode = newInitialInputMode;
   }

   public int getMaxSize() {
      return this.maxSize;
   }

   public void setMaxSize(int newMaxSize) {
      this.maxSize = newMaxSize;
   }

   public void keyPressed(int keyCode, int keyDataIdx) {
   }

   public void keyReleased(int keyCode, int keyDataIdx) {
   }

   public void keyRepeated(int keyCode, int keyDataIdx) {
   }

   public Command[] getExtraCommands() {
      return null;
   }

   public void reconstructExtraCommands() {
   }

   public boolean launchExtraCommand(Command command) {
      return false;
   }

   public void paint(com.nokia.mid.impl.isa.ui.gdi.Graphics ng, int x, int y, int width, int height) {
   }

   public int getHeight(int width, int maxAvailableHeight) {
      return 0;
   }

   public void destroy() {
      this.owner = null;
   }

   public boolean midletCommandsSupported() {
      return true;
   }

   protected boolean isFocused() {
      return this.owner != null ? this.owner.isFocused() : false;
   }

   protected TextEditor getOwner() {
      return this.owner;
   }

   void initiateCall() {
   }
}
