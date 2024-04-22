package javax.microedition.lcdui;

public class Spacer extends Item {
   private int width;
   private int height;

   public Spacer(int minWidth, int minHeight) {
      super((String)null);
      this.updateSizes(minWidth, minHeight);
   }

   public void setMinimumSize(int minWidth, int minHeight) {
      this.updateSizes(minWidth, minHeight);
   }

   public void addCommand(Command cmd) {
      throw new IllegalStateException();
   }

   public void setDefaultCommand(Command cmd) {
      throw new IllegalStateException();
   }

   public void setLabel(String label) {
      throw new IllegalStateException();
   }

   int callMinimumWidth() {
      return this.width;
   }

   int callPreferredWidth(int h) {
      return this.width;
   }

   int callMinimumHeight() {
      return this.height;
   }

   int callPreferredHeight(int w) {
      return this.height;
   }

   boolean shouldSkipTraverse() {
      return true;
   }

   void callPaint(Graphics g, int w, int h, boolean isFocused) {
   }

   private void updateSizes(int minW, int minH) {
      if (minW >= 0 && minH >= 0) {
         synchronized(Display.LCDUILock) {
            if (this.width != minW || this.height != minH) {
               this.width = minW;
               this.height = minH;
               this.invalidate();
            }

         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
