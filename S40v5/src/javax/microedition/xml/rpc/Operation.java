package javax.microedition.xml.rpc;

import com.sun.j2mews.xml.rpc.OperationImpl;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;

public class Operation {
   public static final String SOAPACTION_URI_PROPERTY = "javax.xml.rpc.soap.http.soapaction.uri";

   protected Operation() {
   }

   public static Operation newInstance(QName var0, Element var1, Element var2) {
      return new OperationImpl(var0, var1, var2);
   }

   public static Operation newInstance(QName var0, Element var1, Element var2, FaultDetailHandler var3) {
      return new OperationImpl(var0, var1, var2, var3);
   }

   public void setProperty(String var1, String var2) throws IllegalArgumentException {
   }

   public Object invoke(Object var1) throws JAXRPCException {
      return null;
   }
}
