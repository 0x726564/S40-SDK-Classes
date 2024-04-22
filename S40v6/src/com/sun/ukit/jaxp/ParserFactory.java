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

   public void setNamespaceAware(boolean awareness) {
      super.setNamespaceAware(awareness);
      if (awareness) {
         this.namespaces = true;
         this.prefixes = false;
      } else {
         this.namespaces = false;
         this.prefixes = true;
      }

   }

   public void setFeature(String name, boolean value) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
      if ("http://xml.org/sax/features/namespaces".equals(name)) {
         this.namespaces = value;
      } else {
         if (!"http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            throw new SAXNotRecognizedException(name);
         }

         this.prefixes = value;
      }

   }

   public boolean getFeature(String name) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
      if ("http://xml.org/sax/features/namespaces".equals(name)) {
         return this.namespaces;
      } else if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
         return this.prefixes;
      } else {
         throw new SAXNotRecognizedException(name);
      }
   }
}
