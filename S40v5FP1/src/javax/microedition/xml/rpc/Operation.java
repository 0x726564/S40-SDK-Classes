package javax.microedition.xml.rpc;

import com.sun.j2mews.xml.rpc.OperationImpl;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;

public class Operation {
   public static final String SOAPACTION_URI_PROPERTY = "javax.xml.rpc.soap.http.soapaction.uri";

   protected Operation() {
   }

   public static Operation newInstance(QName name, Element input, Element output) {
      return new OperationImpl(name, input, output);
   }

   public static Operation newInstance(QName name, Element input, Element output, FaultDetailHandler faultDetailHandler) {
      return new OperationImpl(name, input, output, faultDetailHandler);
   }

   public void setProperty(String name, String value) throws IllegalArgumentException {
   }

   public Object invoke(Object inParams) throws JAXRPCException {
      return null;
   }
}
