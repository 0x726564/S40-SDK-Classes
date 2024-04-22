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

   public Sprite(Image image) {
      this(image, image.getWidth(), image.getHeight());
   }

   public Sprite(Image newImg, int frameW, int frameH) {
      this.setImageData(newImg, frameW, frameH, true);
      this.setSize(frameW, frameH);
      this.initialiseFrameSeq((int[])null);
   }

   public Sprite(Sprite s) {
      if (s == null) {
         throw new NullPointerException();
      } else {
         this.x = s.x;
         this.y = s.y;
         this.w = s.w;
         this.h = s.h;
         this.visible = s.visible;
         this.image = s.image;
         this.nativeData = new byte[s.nativeData.length];
         System.arraycopy(s.nativeData, 0, this.nativeData, 0, s.nativeData.length);
         this.refPixelDltX = s.refPixelDltX;
         this.refPixelDltY = s.refPixelDltY;
         this.untransformedX = s.untransformedX;
         this.untransformedY = s.untransformedY;
         this.frameSeq = new int[s.frameSeq.length];
         System.arraycopy(s.frameSeq, 0, this.frameSeq, 0, s.frameSeq.length);
         this.sequenceIdx = s.sequenceIdx;
         this.currentFrame = s.currentFrame;
         this.frameCount = s.frameCount;
         this.customSeq = s.customSeq;
      }
   }

   public final boolean collidesWith(Image img, int imgX, int imgY, boolean pixelLevel) {
      boolean b = false;
      if (img == null) {
         throw new NullPointerException();
      } else {
         if (this.visible) {
            b = this.collidesWithImage(img, imgX, imgY, this.x, this.y, pixelLevel);
         }

         return b;
      }
   }

   public final boolean collidesWith(Sprite s, boolean pixelLevel) {
      boolean b = false;
      if (s == null) {
         throw new NullPointerException();
      } else {
         if (this.visible && s.visible) {
            b = this.collidesWithSprite(s, s.x, s.y, this.x, this.y, pixelLevel);
         }

         return b;
      }
   }

   public final boolean collidesWith(TiledLayer t, boolean pixelLevel) {
      boolean b = false;
      if (t == null) {
         throw new NullPointerException();
      } else {
         if (this.visible && t.visible) {
            b = this.collidesWithTiledLayer(t, t.x, t.y, this.x, this.y, pixelLevel);
         }

         return b;
      }
   }

   public void defineCollisionRectangle(int x, int y, int w, int h) {
      if (w >= 0 && h >= 0) {
         this.nativeDefineCollisionRectangle(x, y, w, h);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void defineReferencePixel(int newX, int newY) {
      int[] translateXY = new int[2];
      this.refPixelDltX = newX;
      this.refPixelDltY = newY;
      this.nativeDefineReferencePixel(this.refPixelDltX, this.refPixelDltY, translateXY);
      this.untransformedX += translateXY[0];
      this.untransformedY += translateXY[1];
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

   public void move(int dx, int dy) {
      super.move(dx, dy);
      this.untransformedX += dx;
      this.untransformedY += dy;
   }

   public void setPosition(int newX, int newY) {
      this.move(newX - this.x, newY - this.y);
   }

   public void nextFrame() {
      this.sequenceIdx = (this.sequenceIdx + 1) % this.frameSeq.length;
      this.currentFrame = this.frameSeq[this.sequenceIdx];
   }

   public final void paint(Graphics g) {
      if (g == null) {
         throw new NullPointerException();
      } else {
         if (this.visible) {
            this.paint(g, this.untransformedX, this.untransformedY);
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

   public void setFrame(int newSeqIndex) {
      if (newSeqIndex >= 0 && newSeqIndex < this.frameSeq.length) {
         this.sequenceIdx = newSeqIndex;
         this.currentFrame = this.frameSeq[this.sequenceIdx];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setFrameSequence(int[] newSeq) {
      if (newSeq != null) {
         if (newSeq.length == 0) {
            throw new IllegalArgumentException();
         }

         for(int i = 0; i < newSeq.length; ++i) {
            if (newSeq[i] < 0 || newSeq[i] >= this.frameCount) {
               throw new ArrayIndexOutOfBoundsException();
            }
         }
      }

      this.initialiseFrameSeq(newSeq);
   }

   public void setImage(Image img, int frameW, int frameH) {
      int oldNumFrames = this.frameCount;
      this.setImageData(img, frameW, frameH, false);
      if (this.frameCount > oldNumFrames) {
         if (!this.customSeq) {
            this.createDefaultFrameSeq();
         }
      } else if (this.frameCount < oldNumFrames) {
         this.initialiseFrameSeq((int[])null);
      }

      this.calculatePositionAndDimensions();
   }

   public void setRefPixelPosition(int newX, int newY) {
      this.move(newX - (this.untransformedX + this.refPixelDltX), newY - (this.untransformedY + this.refPixelDltY));
   }

   public void setTransform(int newTransform) {
      if (newTransform >= 0 && newTransform <= 7) {
         this.nativeSetTransform(newTransform);
         this.calculatePositionAndDimensions();
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void createDefaultFrameSeq() {
      this.frameSeq = new int[this.frameCount];

      for(int x = 0; x < this.frameCount; this.frameSeq[x] = x++) {
      }

      this.customSeq = false;
   }

   private void initialiseFrameSeq(int[] newSeq) {
      if (newSeq != null) {
         this.frameSeq = new int[newSeq.length];
         System.arraycopy(newSeq, 0, this.frameSeq, 0, newSeq.length);
         this.customSeq = true;
      } else {
         this.createDefaultFrameSeq();
      }

      this.sequenceIdx = 0;
      this.currentFrame = this.frameSeq[0];
   }

   private void setImageData(Image newImg, int frameW, int frameH, boolean newSprite) {
      if (newImg == null) {
         throw new NullPointerException();
      } else if (frameW > 0 && frameH > 0 && newImg.getWidth() != 0 && newImg.getHeight() != 0 && newImg.getWidth() % frameW == 0 && newImg.getHeight() % frameH == 0) {
         if (newSprite) {
            this.createNativeSprite();
         }

         this.image = newImg;
         this.frameCount = newImg.getWidth() / frameW * (newImg.getHeight() / frameH);
         this.setNativeImage(newImg, frameW, frameH);
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void calculatePositionAndDimensions() {
      int[] sizeXY = new int[2];
      int[] posXY = new int[2];
      this.calculatePositionAndDimensions(this.untransformedX, this.untransformedY, posXY, sizeXY);
      this.setSize(sizeXY[0], sizeXY[1]);
      super.setPosition(posXY[0], posXY[1]);
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
