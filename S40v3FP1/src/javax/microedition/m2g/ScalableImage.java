package javax.microedition.m2g;

import java.io.IOException;
import java.io.InputStream;

public abstract class ScalableImage {
   protected ScalableImage() {
   }

   public static ScalableImage createImage(InputStream var0, ExternalResourceHandler var1) throws IOException {
      return SVGImage.createImage(var0, var1);
   }

   public static ScalableImage createImage(String var0, ExternalResourceHandler var1) throws IOException {
      return SVGImage.createImage(var0, var1);
   }

   public abstract void setViewportWidth(int var1);

   public abstract void setViewportHeight(int var1);

   public abstract int getViewportWidth();

   public abstract int getViewportHeight();

   public abstract void requestCompleted(String var1, InputStream var2) throws IOException;
}
