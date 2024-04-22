package org.xml.sax.helpers;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DefaultHandler {
   public InputSource resolveEntity(String var1, String var2) throws SAXException {
      return null;
   }

   public void notationDecl(String var1, String var2, String var3) throws SAXException {
   }

   public void unparsedEntityDecl(String var1, String var2, String var3, String var4) throws SAXException {
   }

   public void setDocumentLocator(Locator var1) {
   }

   public void startDocument() throws SAXException {
   }

   public void endDocument() throws SAXException {
   }

   public void startPrefixMapping(String var1, String var2) throws SAXException {
   }

   public void endPrefixMapping(String var1) throws SAXException {
   }

   public void startElement(String var1, String var2, String var3, Attributes var4) throws SAXException {
   }

   public void endElement(String var1, String var2, String var3) throws SAXException {
   }

   public void characters(char[] var1, int var2, int var3) throws SAXException {
   }

   public void ignorableWhitespace(char[] var1, int var2, int var3) throws SAXException {
   }

   public void processingInstruction(String var1, String var2) throws SAXException {
   }

   public void skippedEntity(String var1) throws SAXException {
   }

   public void warning(SAXParseException var1) throws SAXException {
   }

   public void error(SAXParseException var1) throws SAXException {
   }

   public void fatalError(SAXParseException var1) throws SAXException {
      throw var1;
   }
}
