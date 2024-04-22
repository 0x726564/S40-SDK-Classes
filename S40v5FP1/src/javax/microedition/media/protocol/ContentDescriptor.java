package javax.microedition.media.protocol;

public class ContentDescriptor {
   private String encoding;

   public String getContentType() {
      return this.encoding;
   }

   public ContentDescriptor(String contentType) {
      this.encoding = contentType;
   }
}
