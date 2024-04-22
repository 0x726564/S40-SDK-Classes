package javax.xml.parsers;

import com.sun.ukit.jaxp.ParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class SAXParserFactory {
   private boolean g = false;
   private boolean h = false;

   protected SAXParserFactory() {
   }

   public static SAXParserFactory newInstance() throws FactoryConfigurationError {
      return new ParserFactory();
   }

   public abstract SAXParser newSAXParser() throws ParserConfigurationException, SAXException;

   public void setNamespaceAware(boolean var1) {
      this.g = var1;
   }

   public boolean isNamespaceAware() {
      return this.g;
   }

   public void setValidating(boolean var1) {
   }

   public boolean isValidating() {
      return false;
   }

   public abstract void setFeature(String var1, boolean var2) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException;

   public abstract boolean getFeature(String var1) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException;
}
