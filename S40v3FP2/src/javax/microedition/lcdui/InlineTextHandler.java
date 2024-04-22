package javax.microedition.lcdui;

abstract class InlineTextHandler {
   protected String initialInputMode = null;
   protected int constraints = 0;
   protected int maxSize = 0;
   protected TextEditor owner = null;

   public abstract String getString();

   public abstract void setString(String var1, int var2);

   public abstract int getCursorPosition();

   public abstract void setCursorPosition(int var1);

   public abstract int size();

   public int getConstraints() {
      return this.constraints;
   }

   public void setConstraints(int var1) {
      this.constraints = var1;
   }

   protected boolean isFocused() {
      return this.owner != null ? this.owner.isFocused() : false;
   }

   public void setFocus(boolean var1) {
   }

   public String getInitialInputMode() {
      return this.initialInputMode;
   }

   public void setInitialInputMode(String var1) {
      this.initialInputMode = var1;
   }

   protected TextEditor getOwner() {
      return this.owner;
   }

   public int getMaxSize() {
      return this.maxSize;
   }

   public void setMaxSize(int var1) {
      this.maxSize = var1;
   }

   public void keyPressed(int var1, int var2) {
   }

   public void keyReleased(int var1, int var2) {
   }

   public void keyRepeated(int var1, int var2) {
   }

   public Command[] getExtraCommands() {
      return null;
   }

   public void reconstructExtraCommands() {
   }

   public boolean launchExtraCommand(Command var1) {
      return false;
   }

   public void paint(com.nokia.mid.impl.isa.ui.gdi.Graphics var1, int var2, int var3, int var4, int var5) {
   }

   public int getHeight(int var1, int var2) {
      return 0;
   }

   public void destroy() {
      this.owner = null;
   }

   public boolean midletCommandsSupported() {
      return true;
   }

   void initiateCall() {
   }
}
