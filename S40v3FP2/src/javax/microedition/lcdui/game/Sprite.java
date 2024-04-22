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
   private Image image;
   private byte[] nativeData;
   private int refPixelDltX;
   private int refPixelDltY;
   private int untransformedX;
   private int untransformedY;
   private int[] frameSeq;
   private int sequenceIdx;
   private int currentFrame;
   private int frameCount;
   private boolean customSeq;

   public Sprite(Image var1) {
      this(var1, var1.getWidth(), var1.getHeight());
   }

   public Sprite(Image var1, int var2, int var3) {
      this.setImageData(var1, var2, var3, true);
      this.setSize(var2, var3);
      this.initialiseFrameSeq((int[])null);
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
         this.image = var1.image;
         this.nativeData = new byte[var1.nativeData.length];
         System.arraycopy(var1.nativeData, 0, this.nativeData, 0, var1.nativeData.length);
         this.refPixelDltX = var1.refPixelDltX;
         this.refPixelDltY = var1.refPixelDltY;
         this.untransformedX = var1.untransformedX;
         this.untransformedY = var1.untransformedY;
         this.frameSeq = new int[var1.frameSeq.length];
         System.arraycopy(var1.frameSeq, 0, this.frameSeq, 0, var1.frameSeq.length);
         this.sequenceIdx = var1.sequenceIdx;
         this.currentFrame = var1.currentFrame;
         this.frameCount = var1.frameCount;
         this.customSeq = var1.customSeq;
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
      this.refPixelDltX = var1;
      this.refPixelDltY = var2;
      this.nativeDefineReferencePixel(this.refPixelDltX, this.refPixelDltY, var3);
      this.untransformedX += var3[0];
      this.untransformedY += var3[1];
   }

   public final int getFrame() {
      return this.sequenceIdx;
   }

   public int getFrameSequenceLength() {
      return this.frameSeq.length;
   }

   public int getRawFrameCount() {
      return this.frameCount;
   }

   public int getRefPixelX() {
      return this.untransformedX + this.refPixelDltX;
   }

   public int getRefPixelY() {
      return this.untransformedY + this.refPixelDltY;
   }

   public void move(int var1, int var2) {
      super.move(var1, var2);
      this.untransformedX += var1;
      this.untransformedY += var2;
   }

   public void setPosition(int var1, int var2) {
      this.move(var1 - this.x, var2 - this.y);
   }

   public void nextFrame() {
      this.sequenceIdx = (this.sequenceIdx + 1) % this.frameSeq.length;
      this.currentFrame = this.frameSeq[this.sequenceIdx];
   }

   public final void paint(Graphics var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (this.visible) {
            this.paint(var1, this.untransformedX, this.untransformedY);
         }

      }
   }

   public void prevFrame() {
      if (this.sequenceIdx == 0) {
         this.sequenceIdx = this.frameSeq.length - 1;
      } else {
         --this.sequenceIdx;
      }

      this.currentFrame = this.frameSeq[this.sequenceIdx];
   }

   public void setFrame(int var1) {
      if (var1 >= 0 && var1 < this.frameSeq.length) {
         this.sequenceIdx = var1;
         this.currentFrame = this.frameSeq[this.sequenceIdx];
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
            if (var1[var2] < 0 || var1[var2] >= this.frameCount) {
               throw new ArrayIndexOutOfBoundsException();
            }
         }
      }

      this.initialiseFrameSeq(var1);
   }

   public void setImage(Image var1, int var2, int var3) {
      int var4 = this.frameCount;
      this.setImageData(var1, var2, var3, false);
      if (this.frameCount > var4) {
         if (!this.customSeq) {
            this.createDefaultFrameSeq();
         }
      } else if (this.frameCount < var4) {
         this.initialiseFrameSeq((int[])null);
      }

      this.calculatePositionAndDimensions();
   }

   public void setRefPixelPosition(int var1, int var2) {
      this.move(var1 - (this.untransformedX + this.refPixelDltX), var2 - (this.untransformedY + this.refPixelDltY));
   }

   public void setTransform(int var1) {
      if (var1 >= 0 && var1 <= 7) {
         this.nativeSetTransform(var1);
         this.calculatePositionAndDimensions();
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void createDefaultFrameSeq() {
      this.frameSeq = new int[this.frameCount];

      for(int var1 = 0; var1 < this.frameCount; this.frameSeq[var1] = var1++) {
      }

      this.customSeq = false;
   }

   private void initialiseFrameSeq(int[] var1) {
      if (var1 != null) {
         this.frameSeq = new int[var1.length];
         System.arraycopy(var1, 0, this.frameSeq, 0, var1.length);
         this.customSeq = true;
      } else {
         this.createDefaultFrameSeq();
      }

      this.sequenceIdx = 0;
      this.currentFrame = this.frameSeq[0];
   }

   private void setImageData(Image var1, int var2, int var3, boolean var4) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 > 0 && var3 > 0 && var1.getWidth() != 0 && var1.getHeight() != 0 && var1.getWidth() % var2 == 0 && var1.getHeight() % var3 == 0) {
         if (var4) {
            this.createNativeSprite();
         }

         this.image = var1;
         this.frameCount = var1.getWidth() / var2 * (var1.getHeight() / var3);
         this.setNativeImage(var1, var2, var3);
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void calculatePositionAndDimensions() {
      int[] var1 = new int[2];
      int[] var2 = new int[2];
      this.calculatePositionAndDimensions(this.untransformedX, this.untransformedY, var2, var1);
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
