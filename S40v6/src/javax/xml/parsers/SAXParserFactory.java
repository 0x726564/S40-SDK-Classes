package javax.xml.parsers;

import com.sun.ukit.jaxp.ParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class SAXParserFactory {
   private boolean namespaceAware = false;
   private boolean validating = false;

   protected SAXParserFactory() {
   }

   public static SAXParserFactory newInstance() throws FactoryConfigurationError {
      return new ParserFactory();
   }

   public abstract SAXParser newSAXParser() throws ParserConfigurationException, SAXException;

   public void setNamespaceAware(boolean awareness) {
      this.namespaceAware = awareness;
   }

   public boolean isNamespaceAware() {
      return this.namespaceAware;
   }

   public void setValidating(boolean validating) {
      validating = false;
   }

   public boolean isValidating() {
      return this.validating;
   }

   public abstract void setFeature(String var1, boolean var2) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException;

   public abstract boolean getFeature(String var1) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException;
}
