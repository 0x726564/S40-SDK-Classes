package com.nokia.mid.impl.isa.io.protocol.external.btgoep;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.bluetooth.DataElement;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class Protocol implements ConnectionBaseInterface {
   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      Connection var5;
      if ((var5 = Connector.open("btspp:" + var1, var2, var3)) instanceof StreamConnectionNotifier) {
         ServiceRecord var6;
         DataElement var7 = (var6 = LocalDevice.getLocalDevice().getRecord(var5)).getAttributeValue(4);
         DataElement var8 = new DataElement(48);
         DataElement var4 = new DataElement(24, new UUID(8L));
         var8.addElement(var4);
         var7.addElement(var8);
         var6.setAttributeValue(4, var7);
         return new BTSessionNotifierImpl((StreamConnectionNotifier)var5);
      } else {
         return new BTClientSessionImpl((StreamConnection)var5);
      }
   }
}
