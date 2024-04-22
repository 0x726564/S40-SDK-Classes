package javax.microedition.lcdui.game;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;

public class LayerManager {
   private Vector dG = new Vector(5, 1);
   private int dH;
   private int dI;
   private int dJ;
   private int dK;

   public LayerManager() {
      this.setViewWindow(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   public void append(Layer var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.dG.removeElement(var1);
         this.dG.addElement(var1);
      }
   }

   public Layer getLayerAt(int var1) {
      return (Layer)this.dG.elementAt(var1);
   }

   public int getSize() {
      return this.dG.size();
   }

   public void insert(Layer var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.dG.removeElement(var1);
         this.dG.insertElementAt(var1, var2);
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
         int var9 = this.dG.size();
         var1.translate(var2 - this.dH, var3 - this.dI);
         var1.clipRect(this.dH, this.dI, this.dJ, this.dK);

         while(true) {
            --var9;
            if (var9 < 0) {
               var1.translate(-var2 + this.dH, -var3 + this.dI);
               var1.setClip(var5, var6, var7, var8);
               return;
            }

            ((Layer)this.dG.elementAt(var9)).paint(var1);
         }
      }
   }

   public void remove(Layer var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.dG.removeElement(var1);
      }
   }

   public void setViewWindow(int var1, int var2, int var3, int var4) {
      if (var3 >= 0 && var4 >= 0) {
         this.dH = var1;
         this.dI = var2;
         this.dJ = var3;
         this.dK = var4;
      } else {
         throw new IllegalArgumentException();
      }
   }
}
