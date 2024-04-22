package com.sun.ukit.jaxp;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class ParserFactory extends SAXParserFactory {
   public static final String FEATURE_NS = "http://xml.org/sax/features/namespaces";
   public static final String FEATURE_PREF = "http://xml.org/sax/features/namespace-prefixes";
   private boolean fo = false;
   private boolean fp = true;

   public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
      if (this.fo && !this.fp) {
         return new Parser(true);
      } else if (!this.fo && this.fp) {
         return new Parser(false);
      } else {
         throw new ParserConfigurationException("");
      }
   }

   public void setNamespaceAware(boolean var1) {
      super.setNamespaceAware(var1);
      if (var1) {
         this.fo = true;
         this.fp = false;
      } else {
         this.fo = false;
         this.fp = true;
      }
   }

   public void setFeature(String var1, boolean var2) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
      if ("http://xml.org/sax/features/namespaces".equals(var1)) {
         this.fo = var2;
      } else if ("http://xml.org/sax/features/namespace-prefixes".equals(var1)) {
         this.fp = var2;
      } else {
         throw new SAXNotRecognizedException(var1);
      }
   }

   public boolean getFeature(String var1) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
      if ("http://xml.org/sax/features/namespaces".equals(var1)) {
         return this.fo;
      } else if ("http://xml.org/sax/features/namespace-prefixes".equals(var1)) {
         return this.fp;
      } else {
         throw new SAXNotRecognizedException(var1);
      }
   }
}
