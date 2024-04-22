package com.nokia.mid.impl.isa.io.protocol.external.btgoep;

import com.nokia.mid.impl.isa.bluetooth.LocalServiceRecord;
import com.nokia.mid.impl.isa.bluetooth.ServiceRecordAccessor;
import com.nokia.mid.impl.isa.obex.SessionNotifierImpl;
import javax.microedition.io.StreamConnectionNotifier;

public class BTSessionNotifierImpl extends SessionNotifierImpl implements ServiceRecordAccessor {
   private StreamConnectionNotifier notifier;

   public BTSessionNotifierImpl(StreamConnectionNotifier notifier) {
      super(notifier);
      this.notifier = notifier;
   }

   public boolean isClosed() {
      return ((ServiceRecordAccessor)this.notifier).isClosed();
   }

   public LocalServiceRecord getLocalServiceRecord() {
      return ((ServiceRecordAccessor)this.notifier).getLocalServiceRecord();
   }
}
