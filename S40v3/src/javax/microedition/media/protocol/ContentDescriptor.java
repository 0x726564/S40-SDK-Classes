package javax.microedition.media.protocol;

public class ContentDescriptor {
   private String encoding;

   public String getContentType() {
      return this.encoding;
   }

   public ContentDescriptor(String var1) {
      this.encoding = var1;
   }
}
