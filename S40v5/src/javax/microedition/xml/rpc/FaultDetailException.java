package javax.microedition.xml.rpc;

import javax.xml.namespace.QName;

public class FaultDetailException extends Exception {
   private Object e;
   private QName f;

   public FaultDetailException(QName var1, Object var2) {
      this.e = var2;
      this.f = var1;
   }

   public Object getFaultDetail() {
      return this.e;
   }

   public QName getFaultDetailName() {
      return this.f;
   }
}
