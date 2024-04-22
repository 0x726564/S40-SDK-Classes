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
   private boolean namespaces = false;
   private boolean prefixes = true;

   public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
      if (this.namespaces && !this.prefixes) {
         return new Parser(true);
      } else if (!this.namespaces && this.prefixes) {
         return new Parser(false);
      } else {
         throw new ParserConfigurationException("");
      }
   }

   public void setNamespaceAware(boolean var1) {
      super.setNamespaceAware(var1);
      if (var1) {
         this.namespaces = true;
         this.prefixes = false;
      } else {
         this.namespaces = false;
         this.prefixes = true;
      }

   }

   public void setFeature(String var1, boolean var2) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
      if ("http://xml.org/sax/features/namespaces".equals(var1)) {
         this.namespaces = var2;
      } else {
         if (!"http://xml.org/sax/features/namespace-prefixes".equals(var1)) {
            throw new SAXNotRecognizedException(var1);
         }

         this.prefixes = var2;
      }

   }

   public boolean getFeature(String var1) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
      if ("http://xml.org/sax/features/namespaces".equals(var1)) {
         return this.namespaces;
      } else if ("http://xml.org/sax/features/namespace-prefixes".equals(var1)) {
         return this.prefixes;
      } else {
         throw new SAXNotRecognizedException(var1);
      }
   }
}
