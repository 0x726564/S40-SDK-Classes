package com.nokia.mid.impl.isa.io.protocol.external.btgoep;

import com.nokia.mid.impl.isa.bluetooth.LocalServiceRecord;
import com.nokia.mid.impl.isa.bluetooth.ServiceRecordAccessor;
import com.nokia.mid.impl.isa.obex.SessionNotifierImpl;
import javax.microedition.io.StreamConnectionNotifier;

public class BTSessionNotifierImpl extends SessionNotifierImpl implements ServiceRecordAccessor {
   private StreamConnectionNotifier du;

   public BTSessionNotifierImpl(StreamConnectionNotifier var1) {
      super(var1);
      this.du = var1;
   }

   public boolean isClosed() {
      return ((ServiceRecordAccessor)this.du).isClosed();
   }

   public LocalServiceRecord getLocalServiceRecord() {
      return ((ServiceRecordAccessor)this.du).getLocalServiceRecord();
   }
}
