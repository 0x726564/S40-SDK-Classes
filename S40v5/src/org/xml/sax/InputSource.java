package org.xml.sax;

import java.io.InputStream;
import java.io.Reader;

public class InputSource {
   private String i;
   private String j;
   private InputStream k;
   private String l;
   private Reader m;

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
      this.i = var1;
   }

   public String getPublicId() {
      return this.i;
   }

   public void setSystemId(String var1) {
      this.j = var1;
   }

   public String getSystemId() {
      return this.j;
   }

   public void setByteStream(InputStream var1) {
      this.k = var1;
   }

   public InputStream getByteStream() {
      return this.k;
   }

   public void setEncoding(String var1) {
      this.l = var1;
   }

   public String getEncoding() {
      return this.l;
   }

   public void setCharacterStream(Reader var1) {
      this.m = var1;
   }

   public Reader getCharacterStream() {
      return this.m;
   }
}
