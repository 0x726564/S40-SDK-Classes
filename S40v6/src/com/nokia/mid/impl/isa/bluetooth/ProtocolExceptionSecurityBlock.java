package com.nokia.mid.impl.isa.bluetooth;

import javax.bluetooth.BluetoothConnectionException;

public class ProtocolExceptionSecurityBlock extends ProtocolExceptionOfType {
   public void handleStatus() throws BluetoothConnectionException {
      throw new BluetoothConnectionException(2);
   }
}
