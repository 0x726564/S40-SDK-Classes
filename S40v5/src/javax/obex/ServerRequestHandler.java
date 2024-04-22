package javax.obex;

import com.nokia.mid.impl.isa.obex.HeaderSetImpl;

public class ServerRequestHandler {
   private long p = -1L;

   protected ServerRequestHandler() {
   }

   public final HeaderSet createHeaderSet() {
      return new HeaderSetImpl();
   }

   public void setConnectionID(long var1) {
      if (var1 >= 0L && var1 <= 2147483647L) {
         this.p = var1;
      } else {
         throw new IllegalArgumentException("Invalid connection ID");
      }
   }

   public long getConnectionID() {
      return this.p;
   }

   public int onConnect(HeaderSet var1, HeaderSet var2) {
      return 160;
   }

   public void onDisconnect(HeaderSet var1, HeaderSet var2) {
   }

   public int onSetPath(HeaderSet var1, HeaderSet var2, boolean var3, boolean var4) {
      return 209;
   }

   public int onDelete(HeaderSet var1, HeaderSet var2) {
      return 209;
   }

   public int onPut(Operation var1) {
      return 209;
   }

   public int onGet(Operation var1) {
      return 209;
   }

   public void onAuthenticationFailure(byte[] var1) {
   }
}
