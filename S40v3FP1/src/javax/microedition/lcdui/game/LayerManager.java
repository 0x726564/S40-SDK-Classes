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

   public void append(Layer var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.displayList.removeElement(var1);
         this.displayList.addElement(var1);
      }
   }

   public Layer getLayerAt(int var1) {
      return (Layer)this.displayList.elementAt(var1);
   }

   public int getSize() {
      return this.displayList.size();
   }

   public void insert(Layer var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.displayList.removeElement(var1);
         this.displayList.insertElementAt(var1, var2);
      }
   }

   public void paint(Graphics var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int var5 = var1.getClipX();
         int var6 = var1.getClipY();
         int var7 = var1.getClipWidth();
         int var8 = var1.getClipHeight();
         int var9 = this.displayList.size();
         var1.translate(var2 - this.viewX, var3 - this.viewY);
         var1.clipRect(this.viewX, this.viewY, this.viewW, this.viewH);

         while(true) {
            --var9;
            if (var9 < 0) {
               var1.translate(-var2 + this.viewX, -var3 + this.viewY);
               var1.setClip(var5, var6, var7, var8);
               return;
            }

            Layer var4 = (Layer)this.displayList.elementAt(var9);
            var4.paint(var1);
         }
      }
   }

   public void remove(Layer var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.displayList.removeElement(var1);
      }
   }

   public void setViewWindow(int var1, int var2, int var3, int var4) {
      if (var3 >= 0 && var4 >= 0) {
         this.viewX = var1;
         this.viewY = var2;
         this.viewW = var3;
         this.viewH = var4;
      } else {
         throw new IllegalArgumentException();
      }
   }
}
