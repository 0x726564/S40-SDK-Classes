package javax.xml.parsers;

import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SAXParser {
   protected SAXParser() {
   }

   public abstract void parse(InputStream var1, DefaultHandler var2) throws SAXException, IOException;

   public abstract void parse(InputSource var1, DefaultHandler var2) throws SAXException, IOException;

   public abstract boolean isNamespaceAware();

   public abstract boolean isValidating();
}
