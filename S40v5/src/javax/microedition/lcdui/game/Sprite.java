package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class Sprite extends Layer {
   public static final int TRANS_MIRROR = 2;
   public static final int TRANS_MIRROR_ROT180 = 1;
   public static final int TRANS_MIRROR_ROT270 = 4;
   public static final int TRANS_MIRROR_ROT90 = 7;
   public static final int TRANS_NONE = 0;
   public static final int TRANS_ROT180 = 3;
   public static final int TRANS_ROT270 = 6;
   public static final int TRANS_ROT90 = 5;
   private Image hC;
   private byte[] iP;
   private int iQ;
   private int iR;
   private int iS;
   private int iT;
   private int[] iU;
   private int iV;
   private int iW;
   private int iX;
   private boolean iY;

   public Sprite(Image var1) {
      this(var1, var1.getWidth(), var1.getHeight());
   }

   public Sprite(Image var1, int var2, int var3) {
      this.a(var1, var2, var3, true);
      this.setSize(var2, var3);
      this.a((int[])null);
   }

   public Sprite(Sprite var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.x = var1.x;
         this.y = var1.y;
         this.w = var1.w;
         this.h = var1.h;
         this.visible = var1.visible;
         this.hC = var1.hC;
         this.iP = new byte[var1.iP.length];
         System.arraycopy(var1.iP, 0, this.iP, 0, var1.iP.length);
         this.iQ = var1.iQ;
         this.iR = var1.iR;
         this.iS = var1.iS;
         this.iT = var1.iT;
         this.iU = new int[var1.iU.length];
         System.arraycopy(var1.iU, 0, this.iU, 0, var1.iU.length);
         this.iV = var1.iV;
         this.iW = var1.iW;
         this.iX = var1.iX;
         this.iY = var1.iY;
      }
   }

   public final boolean collidesWith(Image var1, int var2, int var3, boolean var4) {
      boolean var5 = false;
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (this.visible) {
            var5 = this.collidesWithImage(var1, var2, var3, this.x, this.y, var4);
         }

         return var5;
      }
   }

   public final boolean collidesWith(Sprite var1, boolean var2) {
      boolean var3 = false;
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (this.visible && var1.visible) {
            var3 = this.collidesWithSprite(var1, var1.x, var1.y, this.x, this.y, var2);
         }

         return var3;
      }
   }

   public final boolean collidesWith(TiledLayer var1, boolean var2) {
      boolean var3 = false;
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (this.visible && var1.visible) {
            var3 = this.collidesWithTiledLayer(var1, var1.x, var1.y, this.x, this.y, var2);
         }

         return var3;
      }
   }

   public void defineCollisionRectangle(int var1, int var2, int var3, int var4) {
      if (var3 >= 0 && var4 >= 0) {
         this.nativeDefineCollisionRectangle(var1, var2, var3, var4);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void defineReferencePixel(int var1, int var2) {
      int[] var3 = new int[2];
      this.iQ = var1;
      this.iR = var2;
      this.nativeDefineReferencePixel(this.iQ, this.iR, var3);
      this.iS += var3[0];
      this.iT += var3[1];
   }

   public final int getFrame() {
      return this.iV;
   }

   public int getFrameSequenceLength() {
      return this.iU.length;
   }

   public int getRawFrameCount() {
      return this.iX;
   }

   public int getRefPixelX() {
      return this.iS + this.iQ;
   }

   public int getRefPixelY() {
      return this.iT + this.iR;
   }

   public void move(int var1, int var2) {
      super.move(var1, var2);
      this.iS += var1;
      this.iT += var2;
   }

   public void setPosition(int var1, int var2) {
      this.move(var1 - this.x, var2 - this.y);
   }

   public void nextFrame() {
      this.iV = (this.iV + 1) % this.iU.length;
      this.iW = this.iU[this.iV];
   }

   public final void paint(Graphics var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (this.visible) {
            this.paint(var1, this.iS, this.iT);
         }

      }
   }

   public void prevFrame() {
      if (this.iV == 0) {
         this.iV = this.iU.length - 1;
      } else {
         --this.iV;
      }

      this.iW = this.iU[this.iV];
   }

   public void setFrame(int var1) {
      if (var1 >= 0 && var1 < this.iU.length) {
         this.iV = var1;
         this.iW = this.iU[this.iV];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setFrameSequence(int[] var1) {
      if (var1 != null) {
         if (var1.length == 0) {
            throw new IllegalArgumentException();
         }

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] < 0 || var1[var2] >= this.iX) {
               throw new ArrayIndexOutOfBoundsException();
            }
         }
      }

      this.a(var1);
   }

   public void setImage(Image var1, int var2, int var3) {
      int var4 = this.iX;
      this.a(var1, var2, var3, false);
      if (this.iX > var4) {
         if (!this.iY) {
            this.aO();
         }
      } else if (this.iX < var4) {
         this.a((int[])null);
      }

      this.aP();
   }

   public void setRefPixelPosition(int var1, int var2) {
      this.move(var1 - (this.iS + this.iQ), var2 - (this.iT + this.iR));
   }

   public void setTransform(int var1) {
      if (var1 >= 0 && var1 <= 7) {
         this.nativeSetTransform(var1);
         this.aP();
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void aO() {
      this.iU = new int[this.iX];

      for(int var1 = 0; var1 < this.iX; this.iU[var1] = var1++) {
      }

      this.iY = false;
   }

   private void a(int[] var1) {
      if (var1 != null) {
         this.iU = new int[var1.length];
         System.arraycopy(var1, 0, this.iU, 0, var1.length);
         this.iY = true;
      } else {
         this.aO();
      }

      this.iV = 0;
      this.iW = this.iU[0];
   }

   private void a(Image var1, int var2, int var3, boolean var4) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 > 0 && var3 > 0 && var1.getWidth() != 0 && var1.getHeight() != 0 && var1.getWidth() % var2 == 0 && var1.getHeight() % var3 == 0) {
         if (var4) {
            this.createNativeSprite();
         }

         this.hC = var1;
         this.iX = var1.getWidth() / var2 * (var1.getHeight() / var3);
         this.setNativeImage(var1, var2, var3);
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void aP() {
      int[] var1 = new int[2];
      int[] var2 = new int[2];
      this.calculatePositionAndDimensions(this.iS, this.iT, var2, var1);
      this.setSize(var1[0], var1[1]);
      super.setPosition(var2[0], var2[1]);
   }

   private native boolean collidesWithImage(Image var1, int var2, int var3, int var4, int var5, boolean var6);

   private native boolean collidesWithTiledLayer(TiledLayer var1, int var2, int var3, int var4, int var5, boolean var6);

   private native boolean collidesWithSprite(Sprite var1, int var2, int var3, int var4, int var5, boolean var6);

   private native void createNativeSprite();

   private native void setNativeImage(Image var1, int var2, int var3);

   private static native void initFields();

   private native void nativeDefineCollisionRectangle(int var1, int var2, int var3, int var4);

   private native void paint(Graphics var1, int var2, int var3);

   private native void calculatePositionAndDimensions(int var1, int var2, int[] var3, int[] var4);

   private native void nativeSetTransform(int var1);

   private native void nativeDefineReferencePixel(int var1, int var2, int[] var3);

   static {
      initFields();
   }
}
