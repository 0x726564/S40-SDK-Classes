package javax.microedition.lcdui;

public class Spacer extends Item {
   private int width;
   private int height;

   public Spacer(int var1, int var2) {
      super((String)null);
      this.updateSizes(var1, var2);
   }

   public void setMinimumSize(int var1, int var2) {
      this.updateSizes(var1, var2);
   }

   public void addCommand(Command var1) {
      throw new IllegalStateException();
   }

   public void setDefaultCommand(Command var1) {
      throw new IllegalStateException();
   }

   public void setLabel(String var1) {
      throw new IllegalStateException();
   }

   int callMinimumWidth() {
      return this.width;
   }

   int callPreferredWidth(int var1) {
      return this.width;
   }

   int callMinimumHeight() {
      return this.height;
   }

   int callPreferredHeight(int var1) {
      return this.height;
   }

   boolean shouldSkipTraverse() {
      return true;
   }

   void callPaint(Graphics var1, int var2, int var3, boolean var4) {
   }

   private void updateSizes(int var1, int var2) {
      if (var1 >= 0 && var2 >= 0) {
         synchronized(Display.LCDUILock) {
            if (this.width != var1 || this.height != var2) {
               this.width = var1;
               this.height = var2;
               this.invalidate();
            }

         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
