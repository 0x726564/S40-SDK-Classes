package org.xml.sax;

public class SAXParseException extends SAXException {
   private String publicId;
   private String systemId;
   private int lineNumber;
   private int columnNumber;

   public SAXParseException(String var1, Locator var2) {
      super(var1);
      if (var2 != null) {
         this.publicId = var2.getPublicId();
         this.systemId = var2.getSystemId();
         this.lineNumber = var2.getLineNumber();
         this.columnNumber = var2.getColumnNumber();
      } else {
         this.lineNumber = -1;
         this.columnNumber = -1;
      }

   }

   public String getPublicId() {
      return this.publicId;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   public int getColumnNumber() {
      return this.columnNumber;
   }
}
