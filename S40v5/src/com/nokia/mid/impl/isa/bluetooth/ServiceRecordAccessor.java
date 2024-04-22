package com.nokia.mid.impl.isa.bluetooth;

public interface ServiceRecordAccessor {
   LocalServiceRecord getLocalServiceRecord();

   boolean isClosed();
}
