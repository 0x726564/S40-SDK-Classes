package javax.microedition.lcdui.game;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;

public class LayerManager {
   private Vector displayList = new Vector(5, 1);
   private int viewX;
   private int viewY;
   private int viewW;
   private int viewH;

   public LayerManager() {
      this.setViewWindow(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   public void append(Layer l) {
      if (l == null) {
         throw new NullPointerException();
      } else {
         this.displayList.removeElement(l);
         this.displayList.addElement(l);
      }
   }

   public Layer getLayerAt(int index) {
      return (Layer)this.displayList.elementAt(index);
   }

   public int getSize() {
      return this.displayList.size();
   }

   public void insert(Layer l, int idx) {
      if (l == null) {
         throw new NullPointerException();
      } else {
         this.displayList.removeElement(l);
         this.displayList.insertElementAt(l, idx);
      }
   }

   public void paint(Graphics g, int x, int y) {
      if (g == null) {
         throw new NullPointerException();
      } else {
         int clipX = g.getClipX();
         int clipY = g.getClipY();
         int clipW = g.getClipWidth();
         int clipH = g.getClipHeight();
         int i = this.displayList.size();
         g.translate(x - this.viewX, y - this.viewY);
         g.clipRect(this.viewX, this.viewY, this.viewW, this.viewH);

         while(true) {
            --i;
            if (i < 0) {
               g.translate(-x + this.viewX, -y + this.viewY);
               g.setClip(clipX, clipY, clipW, clipH);
               return;
            }

            Layer l = (Layer)this.displayList.elementAt(i);
            l.paint(g);
         }
      }
   }

   public void remove(Layer l) {
      if (l == null) {
         throw new NullPointerException();
      } else {
         this.displayList.removeElement(l);
      }
   }

   public void setViewWindow(int x, int y, int w, int h) {
      if (w >= 0 && h >= 0) {
         this.viewX = x;
         this.viewY = y;
         this.viewW = w;
         this.viewH = h;
      } else {
         throw new IllegalArgumentException();
      }
   }
}
