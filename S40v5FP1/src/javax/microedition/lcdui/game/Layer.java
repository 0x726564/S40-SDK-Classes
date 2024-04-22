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

   public void move(int dx, int dy) {
      this.x += dx;
      this.y += dy;
   }

   public abstract void paint(Graphics var1);

   public void setPosition(int newX, int newY) {
      this.x = newX;
      this.y = newY;
   }

   public void setVisible(boolean newState) {
      this.visible = newState;
   }

   void setSize(int newW, int newH) {
      this.w = newW;
      this.h = newH;
   }
}
