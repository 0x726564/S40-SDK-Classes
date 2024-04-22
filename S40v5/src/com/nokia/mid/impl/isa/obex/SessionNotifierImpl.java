package com.nokia.mid.impl.isa.obex;

import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.obex.Authenticator;
import javax.obex.ServerRequestHandler;
import javax.obex.SessionNotifier;

public class SessionNotifierImpl implements SessionNotifier {
   private StreamConnectionNotifier du;

   public SessionNotifierImpl(StreamConnectionNotifier var1) {
      this.du = var1;
   }

   public Connection acceptAndOpen(ServerRequestHandler var1) throws IOException {
      return this.acceptAndOpen(var1, (Authenticator)null);
   }

   public Connection acceptAndOpen(ServerRequestHandler var1, Authenticator var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("handler cannot be null");
      } else {
         StreamConnection var3 = this.du.acceptAndOpen();
         ClientConnectionHandler var4;
         (var4 = new ClientConnectionHandler(var3, var2)).startThread(var1);
         return var4;
      }
   }

   public void close() throws IOException {
      this.du.close();
   }
}
