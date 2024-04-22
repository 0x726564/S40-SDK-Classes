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
   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      Connection conn = Connector.open("btspp:" + name + "?nokia_goep_protocol=true", mode, timeouts);
      if (conn instanceof StreamConnectionNotifier) {
         LocalDevice localDev = LocalDevice.getLocalDevice();
         ServiceRecord sr = localDev.getRecord(conn);
         DataElement protocolDescriptorList = sr.getAttributeValue(4);
         DataElement protocol = new DataElement(48);
         DataElement obex = new DataElement(24, new UUID(8L));
         protocol.addElement(obex);
         protocolDescriptorList.addElement(protocol);
         sr.setAttributeValue(4, protocolDescriptorList);
         return new BTSessionNotifierImpl((StreamConnectionNotifier)conn);
      } else {
         return new BTClientSessionImpl((StreamConnection)conn);
      }
   }
}
