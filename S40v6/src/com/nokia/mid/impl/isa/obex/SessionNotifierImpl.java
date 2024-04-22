package com.nokia.mid.impl.isa.obex;

import com.nokia.mid.impl.isa.bluetooth.ObexConnection;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.obex.Authenticator;
import javax.obex.ServerRequestHandler;
import javax.obex.SessionNotifier;

public class SessionNotifierImpl implements SessionNotifier, ObexConnection {
   public StreamConnectionNotifier notifier;

   public SessionNotifierImpl(StreamConnectionNotifier notifier) {
      this.notifier = notifier;
   }

   public Connection getBluetoothConnection() {
      return this.notifier;
   }

   public Connection acceptAndOpen(ServerRequestHandler handler) throws IOException {
      return this.acceptAndOpen(handler, (Authenticator)null);
   }

   public Connection acceptAndOpen(ServerRequestHandler requestHandler, Authenticator authenticator) throws IOException {
      if (requestHandler == null) {
         throw new NullPointerException("handler cannot be null");
      } else {
         StreamConnection conn = this.notifier.acceptAndOpen();
         ClientConnectionHandler handler = new ClientConnectionHandler(conn, authenticator);
         handler.startThread(requestHandler);
         return handler;
      }
   }

   public void close() throws IOException {
      this.notifier.close();
   }
}
