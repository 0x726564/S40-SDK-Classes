package com.nokia.mid.impl.isa.bluetooth;

import javax.bluetooth.BluetoothConnectionException;

public class ProtocolExceptionUnacceptableParams extends ProtocolExceptionOfType {
   public void handleStatus() throws BluetoothConnectionException {
      throw new BluetoothConnectionException(6);
   }
}
