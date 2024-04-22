package javax.microedition.lcdui;

public class Spacer extends Item {
   private int width;
   private int height;

   public Spacer(int var1, int var2) {
      super((String)null);
      this.b(var1, var2);
   }

   public void setMinimumSize(int var1, int var2) {
      this.b(var1, var2);
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

   final int a() {
      return this.width;
   }

   final int a(int var1) {
      return this.width;
   }

   final int b() {
      return this.height;
   }

   final int b(int var1) {
      return this.height;
   }

   final boolean c() {
      return true;
   }

   final void a(Graphics var1, int var2, int var3, boolean var4) {
   }

   private void b(int var1, int var2) {
      if (var1 >= 0 && var2 >= 0) {
         synchronized(Display.hG) {
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
