package com.nokia.mid.impl.isa.bluetooth;

import java.io.IOException;
import javax.bluetooth.BluetoothConnectionException;

public abstract class ProtocolExceptionOfType extends IOException {
   public abstract void handleStatus() throws BluetoothConnectionException;
}
