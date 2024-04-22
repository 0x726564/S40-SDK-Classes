package javax.microedition.m2g;

public interface SVGEventListener {
   void keyPressed(int var1);

   void keyReleased(int var1);

   void pointerPressed(int var1, int var2);

   void pointerReleased(int var1, int var2);

   void hideNotify();

   void showNotify();

   void sizeChanged(int var1, int var2);
}
