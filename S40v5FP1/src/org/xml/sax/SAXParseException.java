package org.xml.sax;

public class SAXParseException extends SAXException {
   private String publicId;
   private String systemId;
   private int lineNumber;
   private int columnNumber;

   public SAXParseException(String message, Locator locator) {
      super(message);
      if (locator != null) {
         this.publicId = locator.getPublicId();
         this.systemId = locator.getSystemId();
         this.lineNumber = locator.getLineNumber();
         this.columnNumber = locator.getColumnNumber();
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
