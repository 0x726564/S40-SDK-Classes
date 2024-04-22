package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;

public abstract class Layer {
   int x;
   int y;
   int w;
   int h;
   boolean visible = true;

   Layer() {
   }

   public final int getHeight() {
      return this.h;
   }

   public final int getWidth() {
      return this.w;
   }

   public final int getX() {
      return this.x;
   }

   public final int getY() {
      return this.y;
   }

   public final boolean isVisible() {
      return this.visible;
   }

   public void move(int var1, int var2) {
      this.x += var1;
      this.y += var2;
   }

   public abstract void paint(Graphics var1);

   public void setPosition(int var1, int var2) {
      this.x = var1;
      this.y = var2;
   }

   public void setVisible(boolean var1) {
      this.visible = var1;
   }

   void setSize(int var1, int var2) {
      this.w = var1;
      this.h = var2;
   }
}
