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

   public TiledLayer(int cols, int rows, Image img, int newTileW, int newTileH) {
      if (cols > 0 && rows > 0) {
         this.validateTileImage(img, newTileW, newTileH);
         this.createNativeTiledLayer(cols, rows);
         this.arrayW = cols;
         this.arrayH = rows;
         this.storeStaticTileSet(img, newTileW, newTileH);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int createAnimatedTile(int staticTileIndex) {
      if (staticTileIndex >= 0 && staticTileIndex <= this.numTiles) {
         int[] tileArray = new int[this.numAnimTiles + 1];

         for(int n = 0; n < this.numAnimTiles; ++n) {
            tileArray[n] = this.animatedTiles[n];
         }

         tileArray[this.numAnimTiles] = staticTileIndex;
         this.animatedTiles = tileArray;
         ++this.numAnimTiles;
         return -this.numAnimTiles;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void fillCells(int col, int row, int numCols, int numRows, int tileIndex) {
      if (tileIndex <= this.numTiles && tileIndex >= -this.numAnimTiles) {
         if (numCols >= 0 && numRows >= 0) {
            if (col >= 0 && row >= 0 && col + numCols <= this.arrayW && row + numRows <= this.arrayH) {
               int skip = this.arrayW - numCols;

               int saveX;
               for(int current = row * this.arrayW + col; numRows-- > 0; numCols = saveX) {
                  for(saveX = numCols; numCols-- > 0; this.staticTiles[current++] = tileIndex) {
                  }

                  current += skip;
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

   public int getAnimatedTile(int animatedTileIndex) {
      if (animatedTileIndex < 0 && -animatedTileIndex <= this.numAnimTiles) {
         return this.animatedTiles[-1 - animatedTileIndex];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getCell(int col, int row) {
      if (col >= 0 && col < this.arrayW && row >= 0 && row < this.arrayH) {
         return this.staticTiles[row * this.arrayW + col];
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

   public final void paint(Graphics g) {
      if (g == null) {
         throw new NullPointerException();
      } else {
         if (this.visible) {
            this.paint(g, this.x, this.y, this.w, this.h);
         }

      }
   }

   public void setAnimatedTile(int animatedTileIndex, int staticTileIndex) {
      if (animatedTileIndex < 0 && animatedTileIndex >= -this.numAnimTiles && staticTileIndex >= 0 && staticTileIndex <= this.numTiles) {
         this.animatedTiles[-1 - animatedTileIndex] = staticTileIndex;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setCell(int col, int row, int tileIndex) {
      if (col >= 0 && col < this.arrayW && row >= 0 && row < this.arrayH && tileIndex <= this.numTiles && tileIndex >= -this.numAnimTiles) {
         this.staticTiles[row * this.arrayW + col] = tileIndex;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setStaticTileSet(Image img, int newTileW, int newTileH) {
      this.validateTileImage(img, newTileW, newTileH);
      this.storeStaticTileSet(img, newTileW, newTileH);
   }

   private void storeStaticTileSet(Image img, int tW, int tH) {
      int oldNumTiles = this.numTiles;
      this.numTiles = img.getWidth() / tW * (img.getHeight() / tH);
      this.setImage(img, tW, tH);
      if (this.tileImage == null || this.numTiles < oldNumTiles) {
         this.numAnimTiles = 0;
         this.animatedTiles = null;
         this.staticTiles = new int[this.arrayW * this.arrayH];
      }

      this.setSize(tW * this.arrayW, tH * this.arrayH);
      this.tileImage = img;
      this.tileW = tW;
      this.tileH = tH;
   }

   private void validateTileImage(Image img, int tW, int tH) {
      if (img == null) {
         throw new NullPointerException();
      } else if (tW <= 0 || tH <= 0 || img.getWidth() == 0 || img.getHeight() == 0 || img.getWidth() % tW != 0 || img.getHeight() % tH != 0) {
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
