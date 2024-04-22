package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class TiledLayer extends Layer {
   private Image fi;
   private int[] fj;
   private int[] fk;
   private int fl;
   private int fm;
   private int fn;
   private int fo;
   private int fp;
   private int fq;

   public TiledLayer(int var1, int var2, Image var3, int var4, int var5) {
      if (var1 > 0 && var2 > 0) {
         b(var3, var4, var5);
         this.createNativeTiledLayer(var1, var2);
         this.fn = var1;
         this.fo = var2;
         this.a(var3, var4, var5);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int createAnimatedTile(int var1) {
      if (var1 >= 0 && var1 <= this.fm) {
         int[] var2 = new int[this.fl + 1];

         for(int var3 = 0; var3 < this.fl; ++var3) {
            var2[var3] = this.fk[var3];
         }

         var2[this.fl] = var1;
         this.fk = var2;
         ++this.fl;
         return -this.fl;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void fillCells(int var1, int var2, int var3, int var4, int var5) {
      if (var5 <= this.fm && var5 >= -this.fl) {
         if (var3 >= 0 && var4 >= 0) {
            if (var1 >= 0 && var2 >= 0 && var1 + var3 <= this.fn && var2 + var4 <= this.fo) {
               int var6 = this.fn - var3;

               for(var2 = var2 * this.fn + var1; var4-- > 0; var3 = var1) {
                  for(var1 = var3; var3-- > 0; this.fj[var2++] = var5) {
                  }

                  var2 += var6;
               }

            } else {
               throw new IndexOutOfBoundsException();
            }
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getAnimatedTile(int var1) {
      if (var1 < 0 && -var1 <= this.fl) {
         return this.fk[-1 - var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getCell(int var1, int var2) {
      if (var1 >= 0 && var1 < this.fn && var2 >= 0 && var2 < this.fo) {
         return this.fj[var2 * this.fn + var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final int getCellHeight() {
      return this.fq;
   }

   public final int getCellWidth() {
      return this.fp;
   }

   public final int getColumns() {
      return this.fn;
   }

   public final int getRows() {
      return this.fo;
   }

   public final void paint(Graphics var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (this.visible) {
            this.paint(var1, this.x, this.y, this.w, this.h);
         }

      }
   }

   public void setAnimatedTile(int var1, int var2) {
      if (var1 < 0 && var1 >= -this.fl && var2 >= 0 && var2 <= this.fm) {
         this.fk[-1 - var1] = var2;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setCell(int var1, int var2, int var3) {
      if (var1 >= 0 && var1 < this.fn && var2 >= 0 && var2 < this.fo && var3 <= this.fm && var3 >= -this.fl) {
         this.fj[var2 * this.fn + var1] = var3;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setStaticTileSet(Image var1, int var2, int var3) {
      b(var1, var2, var3);
      this.a(var1, var2, var3);
   }

   private void a(Image var1, int var2, int var3) {
      int var4 = this.fm;
      this.fm = var1.getWidth() / var2 * (var1.getHeight() / var3);
      this.setImage(var1, var2, var3);
      if (this.fi == null || this.fm < var4) {
         this.fl = 0;
         this.fk = null;
         this.fj = new int[this.fn * this.fo];
      }

      this.setSize(var2 * this.fn, var3 * this.fo);
      this.fi = var1;
      this.fp = var2;
      this.fq = var3;
   }

   private static void b(Image var0, int var1, int var2) {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (var1 <= 0 || var2 <= 0 || var0.getWidth() == 0 || var0.getHeight() == 0 || var0.getWidth() % var1 != 0 || var0.getHeight() % var2 != 0) {
         throw new IllegalArgumentException();
      }
   }

   private native void createNativeTiledLayer(int var1, int var2);

   private static native void initFields();

   private native void paint(Graphics var1, int var2, int var3, int var4, int var5);

   private native void setImage(Image var1, int var2, int var3);

   static {
      initFields();
   }
}
