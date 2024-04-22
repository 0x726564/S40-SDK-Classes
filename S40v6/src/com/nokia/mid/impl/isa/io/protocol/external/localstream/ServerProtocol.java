package com.nokia.mid.impl.isa.io.protocol.external.localstream;

import com.nokia.mid.s40.io.LocalStreamProtocolConnection;
import com.nokia.mid.s40.io.LocalStreamProtocolServerConnection;
import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.microedition.io.Connection;

public class ServerProtocol implements LocalStreamProtocolServerConnection, ConnectionBaseInterface {
   private int nativeHandle;
   private boolean registeredWithServiceRegistry;

   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      String address = name.substring(3);
      this.open0(address);
      return this;
   }

   public LocalStreamProtocolConnection acceptAndOpen() throws IOException {
      Protocol client = new Protocol();
      this.accept0(client);
      client.init();
      return client;
   }

   public void close() throws IOException {
      this.close0();
   }

   public void registerWithServiceRegistry(String service, String versions, String metadataEncoding, byte[] metadata) throws IOException {
      if (service != null && versions != null && metadataEncoding != null && metadata != null) {
         if (service.equals("")) {
            throw new IllegalArgumentException();
         } else if (this.registeredWithServiceRegistry) {
            throw new IllegalStateException();
         } else {
            this.registeredWithServiceRegistry = true;
            this.registerWithServiceRegistry0(service, versions, metadataEncoding, metadata);
         }
      } else {
         throw new NullPointerException();
      }
   }

   public String getClientSecurityPolicy() {
      return this.getClientSecurityPolicy0();
   }

   public String getClientDomain() {
      return this.getClientDomain0();
   }

   public native String getLocalName() throws IOException;

   private native void open0(String var1) throws IOException;

   private native void accept0(Protocol var1) throws IOException;

   private native void close0() throws IOException;

   private native void registerWithServiceRegistry0(String var1, String var2, String var3, byte[] var4) throws IOException;

   private native String getClientSecurityPolicy0();

   private native String getClientDomain0();
}
