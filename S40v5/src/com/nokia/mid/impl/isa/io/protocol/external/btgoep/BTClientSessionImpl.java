package com.nokia.mid.impl.isa.io.protocol.external.btgoep;

import com.nokia.mid.impl.isa.bluetooth.RemoteDeviceAccessor;
import com.nokia.mid.impl.isa.obex.ClientSessionImpl;
import java.io.IOException;
import javax.microedition.io.StreamConnection;

public class BTClientSessionImpl extends ClientSessionImpl implements RemoteDeviceAccessor {
   private RemoteDeviceAccessor dK;

   public BTClientSessionImpl(StreamConnection var1) {
      super(var1);

      try {
         this.dK = (RemoteDeviceAccessor)var1;
      } catch (ClassCastException var2) {
      }
   }

   public String getRemoteDeviceAddress() throws IOException {
      return this.dK.getRemoteDeviceAddress();
   }
}
