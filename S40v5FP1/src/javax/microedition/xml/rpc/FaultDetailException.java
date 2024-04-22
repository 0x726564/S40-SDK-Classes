package javax.microedition.xml.rpc;

import javax.xml.namespace.QName;

public class FaultDetailException extends Exception {
   private Object faultDetail;
   private QName faultDetailName;

   public FaultDetailException(QName faultDetailName, Object faultDetail) {
      this.faultDetail = faultDetail;
      this.faultDetailName = faultDetailName;
   }

   public Object getFaultDetail() {
      return this.faultDetail;
   }

   public QName getFaultDetailName() {
      return this.faultDetailName;
   }
}
