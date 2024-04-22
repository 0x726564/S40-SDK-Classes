package javax.obex;

import com.nokia.mid.impl.isa.obex.HeaderSetImpl;

public class ServerRequestHandler {
   private long connectionID = -1L;

   protected ServerRequestHandler() {
   }

   public final HeaderSet createHeaderSet() {
      return new HeaderSetImpl();
   }

   public void setConnectionID(long id) {
      if (id >= 0L && id <= 2147483647L) {
         this.connectionID = id;
      } else {
         throw new IllegalArgumentException("Invalid connection ID");
      }
   }

   public long getConnectionID() {
      return this.connectionID;
   }

   public int onConnect(HeaderSet request, HeaderSet reply) {
      return 160;
   }

   public void onDisconnect(HeaderSet request, HeaderSet reply) {
   }

   public int onSetPath(HeaderSet request, HeaderSet reply, boolean backup, boolean create) {
      return 209;
   }

   public int onDelete(HeaderSet request, HeaderSet reply) {
      return 209;
   }

   public int onPut(Operation op) {
      return 209;
   }

   public int onGet(Operation op) {
      return 209;
   }

   public void onAuthenticationFailure(byte[] userName) {
   }
}
