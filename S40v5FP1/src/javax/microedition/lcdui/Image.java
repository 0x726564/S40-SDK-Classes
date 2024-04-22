package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import java.io.IOException;
import java.io.InputStream;

public class Image {
   Pixmap pixmap;

   public static Image createImage(int width, int height) {
      if (width > 0 && height > 0) {
         return new Image(Pixmap.createPixmap(width, height, true, UIStyle.COLOUR_WHITE));
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static Image createImage(String name) throws IOException {
      Image imgRetVal = null;
      if (name == null) {
         throw new NullPointerException();
      } else {
         InputStream res = Runtime.getRuntime().getClass().getResourceAsStream(name);
         if (res == null) {
            throw new IOException("Cannot read " + name);
         } else {
            imgRetVal = new Image(Pixmap.createPixmap(res, false));
            return imgRetVal;
         }
      }
   }

   public static Image createImage(InputStream stream) throws IOException {
      return new Image(Pixmap.createPixmap(stream, false));
   }

   public static Image createImage(byte[] imageData, int imageOffset, int imageLength) {
      return new Image(Pixmap.createPixmap(imageData, imageOffset, imageLength, false));
   }

   public static Image createImage(Image source) {
      Image imgRetVal = source;
      if (source == null) {
         throw new NullPointerException();
      } else {
         if (source.pixmap.isMutable()) {
            imgRetVal = new Image(source);
            imgRetVal.pixmap.setMutable(false);
         }

         return imgRetVal;
      }
   }

   public static Image createImage(Image image, int x, int y, int width, int height, int transform) {
      if (image == null) {
         throw new NullPointerException();
      } else if (width > 0 && height > 0 && x >= 0 && y >= 0 && x + width <= image.pixmap.getWidth() && y + height <= image.pixmap.getHeight() && transform >= 0 && transform <= 7) {
         return new Image(new Pixmap(image.pixmap, x, y, width, height, transform));
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
      if (rgb == null) {
         throw new NullPointerException();
      } else if (width > 0 && height > 0) {
         if (width * height > rgb.length) {
            throw new ArrayIndexOutOfBoundsException();
         } else {
            return new Image(Pixmap.createPixmap(rgb, width, height, processAlpha));
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height) {
      if (rgbData == null) {
         throw new NullPointerException();
      } else if (x < 0 || y < 0 || x + width > this.pixmap.getWidth() || y + height > this.pixmap.getHeight() || scanlength >= 0 && scanlength < width || scanlength < 0 && 0 - scanlength < width) {
         throw new IllegalArgumentException();
      } else {
         if (width >= 0 && height >= 0) {
            if (offset < 0 || offset + width + (height - 1) * scanlength > rgbData.length || offset + (height - 1) * scanlength < 0) {
               throw new ArrayIndexOutOfBoundsException();
            }

            this.pixmap.nativeGetRGBFromPixmap(rgbData, offset, scanlength, x, y, width, height);
         }

      }
   }

   Image(Image img) {
      this.pixmap = new Pixmap(img.pixmap);
   }

   Image(Pixmap pixmap) {
      this.pixmap = pixmap;
   }

   public Graphics getGraphics() {
      if (!this.pixmap.isMutable()) {
         throw new IllegalStateException();
      } else {
         Graphics gr = new DirectGraphicsImpl(this);
         gr.setTextTransparency(true);
         return gr;
      }
   }

   public int getHeight() {
      return this.pixmap.getHeight();
   }

   public int getWidth() {
      return this.pixmap.getWidth();
   }

   public boolean isMutable() {
      return this.pixmap.isMutable();
   }

   Pixmap getPixmap() {
      return this.pixmap;
   }
}
