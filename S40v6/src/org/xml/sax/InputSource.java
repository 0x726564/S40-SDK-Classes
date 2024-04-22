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

   public InputSource(String systemId) {
      this.setSystemId(systemId);
   }

   public InputSource(InputStream byteStream) {
      this.setByteStream(byteStream);
   }

   public InputSource(Reader characterStream) {
      this.setCharacterStream(characterStream);
   }

   public void setPublicId(String publicId) {
      this.publicId = publicId;
   }

   public String getPublicId() {
      return this.publicId;
   }

   public void setSystemId(String systemId) {
      this.systemId = systemId;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public void setByteStream(InputStream byteStream) {
      this.byteStream = byteStream;
   }

   public InputStream getByteStream() {
      return this.byteStream;
   }

   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   public String getEncoding() {
      return this.encoding;
   }

   public void setCharacterStream(Reader characterStream) {
      this.characterStream = characterStream;
   }

   public Reader getCharacterStream() {
      return this.characterStream;
   }
}
