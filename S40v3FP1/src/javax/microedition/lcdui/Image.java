package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import java.io.IOException;
import java.io.InputStream;

public class Image {
   Pixmap pixmap;

   public static Image createImage(int var0, int var1) {
      if (var0 > 0 && var1 > 0) {
         return new Image(Pixmap.createPixmap(var0, var1, true, UIStyle.COLOUR_WHITE));
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static Image createImage(String var0) throws IOException {
      Image var1 = null;
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         InputStream var2 = Runtime.getRuntime().getClass().getResourceAsStream(var0);
         if (var2 == null) {
            throw new IOException("Cannot read " + var0);
         } else {
            var1 = new Image(Pixmap.createPixmap(var2, false));
            return var1;
         }
      }
   }

   public static Image createImage(InputStream var0) throws IOException {
      return new Image(Pixmap.createPixmap(var0, false));
   }

   public static Image createImage(byte[] var0, int var1, int var2) {
      return new Image(Pixmap.createPixmap(var0, var1, var2, false));
   }

   public static Image createImage(Image var0) {
      Image var1 = var0;
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         if (var0.pixmap.isMutable()) {
            var1 = new Image(var0);
            var1.pixmap.setMutable(false);
         }

         return var1;
      }
   }

   public static Image createImage(Image var0, int var1, int var2, int var3, int var4, int var5) {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (var3 > 0 && var4 > 0 && var1 >= 0 && var2 >= 0 && var1 + var3 <= var0.pixmap.getWidth() && var2 + var4 <= var0.pixmap.getHeight() && var5 >= 0 && var5 <= 7) {
         return new Image(new Pixmap(var0.pixmap, var1, var2, var3, var4, var5));
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static Image createRGBImage(int[] var0, int var1, int var2, boolean var3) {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (var1 > 0 && var2 > 0) {
         if (var1 * var2 > var0.length) {
            throw new ArrayIndexOutOfBoundsException();
         } else {
            return new Image(Pixmap.createPixmap(var0, var1, var2, var3));
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void getRGB(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var4 < 0 || var5 < 0 || var4 + var6 > this.pixmap.getWidth() || var5 + var7 > this.pixmap.getHeight() || var3 >= 0 && var3 < var6 || var3 < 0 && 0 - var3 < var6) {
         throw new IllegalArgumentException();
      } else {
         if (var6 >= 0 && var7 >= 0) {
            if (var2 < 0 || var2 + var6 + (var7 - 1) * var3 > var1.length || var2 + (var7 - 1) * var3 < 0) {
               throw new ArrayIndexOutOfBoundsException();
            }

            this.pixmap.nativeGetRGBFromPixmap(var1, var2, var3, var4, var5, var6, var7);
         }

      }
   }

   Image(Image var1) {
      this.pixmap = new Pixmap(var1.pixmap);
   }

   Image(Pixmap var1) {
      this.pixmap = var1;
   }

   public Graphics getGraphics() {
      if (!this.pixmap.isMutable()) {
         throw new IllegalStateException();
      } else {
         DirectGraphicsImpl var1 = new DirectGraphicsImpl(this);
         var1.setTextTransparency(true);
         return var1;
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
