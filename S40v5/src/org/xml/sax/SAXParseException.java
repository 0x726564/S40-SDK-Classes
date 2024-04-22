package org.xml.sax;

public class SAXParseException extends SAXException {
   private String i;
   private String j;
   private int n;
   private int o;

   public SAXParseException(String var1, Locator var2) {
      super(var1);
      if (var2 != null) {
         this.i = var2.getPublicId();
         this.j = var2.getSystemId();
         this.n = var2.getLineNumber();
         this.o = var2.getColumnNumber();
      } else {
         this.n = -1;
         this.o = -1;
      }
   }

   public String getPublicId() {
      return this.i;
   }

   public String getSystemId() {
      return this.j;
   }

   public int getLineNumber() {
      return this.n;
   }

   public int getColumnNumber() {
      return this.o;
   }
}
