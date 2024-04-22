package com.nokia.mid.impl.isa.bluetooth;

import java.io.IOException;
import javax.bluetooth.ServiceRegistrationException;

public interface CommonNotifier {
   LocalServiceRecord getLocalServiceRecord();

   boolean checkServiceOkToUpdate(LocalServiceRecord var1);

   void unregisterService() throws IOException;

   void registerService(LocalServiceRecord var1) throws ServiceRegistrationException, NullPointerException, IOException;

   void updateService(LocalServiceRecord var1) throws ServiceRegistrationException, NullPointerException;

   long getChannelID();

   String getProtocol();
}
