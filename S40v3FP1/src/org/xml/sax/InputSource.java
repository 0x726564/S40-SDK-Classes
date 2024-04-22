package org.xml.sax;

import java.io.InputStream;
import java.io.Reader;

public class InputSource {
   private String publicId;
   private String systemId;
   private InputStream byteStream;
   private String encoding;
   private Reader characterStream;

   public InputSource() {
   }

   public InputSource(String var1) {
      this.setSystemId(var1);
   }

   public InputSource(InputStream var1) {
      this.setByteStream(var1);
   }

   public InputSource(Reader var1) {
      this.setCharacterStream(var1);
   }

   public void setPublicId(String var1) {
      this.publicId = var1;
   }

   public String getPublicId() {
      return this.publicId;
   }

   public void setSystemId(String var1) {
      this.systemId = var1;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public void setByteStream(InputStream var1) {
      this.byteStream = var1;
   }

   public InputStream getByteStream() {
      return this.byteStream;
   }

   public void setEncoding(String var1) {
      this.encoding = var1;
   }

   public String getEncoding() {
      return this.encoding;
   }

   public void setCharacterStream(Reader var1) {
      this.characterStream = var1;
   }

   public Reader getCharacterStream() {
      return this.characterStream;
   }
}
