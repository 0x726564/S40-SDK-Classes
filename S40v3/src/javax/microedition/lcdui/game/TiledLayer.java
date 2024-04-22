package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class TiledLayer extends Layer {
   private Image tileImage;
   private byte[] nativeData;
   private int[] staticTiles;
   private int[] animatedTiles;
   private int numAnimTiles;
   private int numTiles;
   private int arrayW;
   private int arrayH;
   private int tileW;
   private int tileH;

   public TiledLayer(int var1, int var2, Image var3, int var4, int var5) {
      if (var1 > 0 && var2 > 0) {
         this.validateTileImage(var3, var4, var5);
         this.createNativeTiledLayer(var1, var2);
         this.arrayW = var1;
         this.arrayH = var2;
         this.storeStaticTileSet(var3, var4, var5);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int createAnimatedTile(int var1) {
      if (var1 >= 0 && var1 <= this.numTiles) {
         int[] var2 = new int[this.numAnimTiles + 1];

         for(int var3 = 0; var3 < this.numAnimTiles; ++var3) {
            var2[var3] = this.animatedTiles[var3];
         }

         var2[this.numAnimTiles] = var1;
         this.animatedTiles = var2;
         ++this.numAnimTiles;
         return -this.numAnimTiles;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void fillCells(int var1, int var2, int var3, int var4, int var5) {
      if (var5 <= this.numTiles && var5 >= -this.numAnimTiles) {
         if (var3 >= 0 && var4 >= 0) {
            if (var1 >= 0 && var2 >= 0 && var1 + var3 <= this.arrayW && var2 + var4 <= this.arrayH) {
               int var6 = this.arrayW - var3;

               int var7;
               for(int var8 = var2 * this.arrayW + var1; var4-- > 0; var3 = var7) {
                  for(var7 = var3; var3-- > 0; this.staticTiles[var8++] = var5) {
                  }

                  var8 += var6;
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
      if (var1 < 0 && -var1 <= this.numAnimTiles) {
         return this.animatedTiles[-1 - var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getCell(int var1, int var2) {
      if (var1 >= 0 && var1 < this.arrayW && var2 >= 0 && var2 < this.arrayH) {
         return this.staticTiles[var2 * this.arrayW + var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final int getCellHeight() {
      return this.tileH;
   }

   public final int getCellWidth() {
      return this.tileW;
   }

   public final int getColumns() {
      return this.arrayW;
   }

   public final int getRows() {
      return this.arrayH;
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
      if (var1 < 0 && var1 >= -this.numAnimTiles && var2 >= 0 && var2 <= this.numTiles) {
         this.animatedTiles[-1 - var1] = var2;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setCell(int var1, int var2, int var3) {
      if (var1 >= 0 && var1 < this.arrayW && var2 >= 0 && var2 < this.arrayH && var3 <= this.numTiles && var3 >= -this.numAnimTiles) {
         this.staticTiles[var2 * this.arrayW + var1] = var3;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setStaticTileSet(Image var1, int var2, int var3) {
      this.validateTileImage(var1, var2, var3);
      this.storeStaticTileSet(var1, var2, var3);
   }

   private void storeStaticTileSet(Image var1, int var2, int var3) {
      int var4 = this.numTiles;
      this.numTiles = var1.getWidth() / var2 * (var1.getHeight() / var3);
      this.setImage(var1, var2, var3);
      if (this.tileImage == null || this.numTiles < var4) {
         this.numAnimTiles = 0;
         this.animatedTiles = null;
         this.staticTiles = new int[this.arrayW * this.arrayH];
      }

      this.setSize(var2 * this.arrayW, var3 * this.arrayH);
      this.tileImage = var1;
      this.tileW = var2;
      this.tileH = var3;
   }

   private void validateTileImage(Image var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 <= 0 || var3 <= 0 || var1.getWidth() == 0 || var1.getHeight() == 0 || var1.getWidth() % var2 != 0 || var1.getHeight() % var3 != 0) {
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
