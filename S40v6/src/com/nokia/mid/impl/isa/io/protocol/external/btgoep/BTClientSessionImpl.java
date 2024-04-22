package com.nokia.mid.impl.isa.io.protocol.external.btgoep;

import com.nokia.mid.impl.isa.bluetooth.RemoteDeviceAccessor;
import com.nokia.mid.impl.isa.obex.ClientSessionImpl;
import java.io.IOException;
import javax.microedition.io.StreamConnection;

public class BTClientSessionImpl extends ClientSessionImpl implements RemoteDeviceAccessor {
   private RemoteDeviceAccessor accessor;

   public BTClientSessionImpl(StreamConnection conn) {
      super(conn);

      try {
         this.accessor = (RemoteDeviceAccessor)conn;
      } catch (ClassCastException var3) {
      }

   }

   public String getRemoteDeviceAddress() throws IOException {
      return this.accessor.getRemoteDeviceAddress();
   }
}
