package com.nokia.mid.impl.isa.bluetooth;

import java.io.IOException;

public interface RemoteDeviceAccessor {
   String getRemoteDeviceAddress() throws IOException;
}
