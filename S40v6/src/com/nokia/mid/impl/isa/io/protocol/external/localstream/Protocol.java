package com.nokia.mid.impl.isa.io.protocol.external.localstream;

import com.nokia.mid.s40.io.LocalStreamProtocolConnection;
import com.sun.midp.io.BufferedConnectionAdapter;
import java.io.IOException;
import javax.microedition.io.Connection;

public class Protocol extends BufferedConnectionAdapter implements LocalStreamProtocolConnection {
   private int nativeHandle;
   private static int BUFFER_SIZE = 256;

   public Protocol() {
      super(BUFFER_SIZE);
      this.protocol = "com.nokia.mid.s40.io.Connector.localstream";
   }

   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      this.checkForPermission(name);
      if (name.startsWith("//:")) {
         return (new ServerProtocol()).openPrim(name, mode, timeouts);
      } else {
         String address = name.substring(2);
         this.connect(address, mode, timeouts);
         return this;
      }
   }

   protected void connect(String name, int mode, boolean timeouts) throws IOException {
      this.open0(name);
      this.init();
   }

   protected void init() {
      this.connectionOpen = true;
      this.eof = false;
   }

   protected void disconnect() throws IOException {
      this.close0();
   }

   protected int nonBufferedRead(byte[] b, int off, int len) throws IOException {
      int bytesReceived = false;
      if (b == null) {
         throw new NullPointerException();
      } else if (off >= 0 && len >= 0 && off + len <= b.length) {
         if (len == 0) {
            return 0;
         } else {
            int bytesReceived = this.receive0(b, off, len);
            if (bytesReceived == -1) {
               this.eof = true;
            }

            return bytesReceived;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   protected int readBytesNonBlocking(byte[] b, int off, int len) throws IOException {
      return this.nonBufferedRead(b, off, len);
   }

   protected int writeBytes(byte[] b, int off, int len) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off >= 0 && len >= 0 && off + len <= b.length) {
         return this.send0(b, off, len);
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   protected void flush() throws IOException {
   }

   private native void open0(String var1) throws IOException;

   private native void close0() throws IOException;

   public native String getLocalName() throws IOException;

   public native String getRemoteName() throws IOException;

   private native int receive0(byte[] var1, int var2, int var3) throws IOException;

   private native int send0(byte[] var1, int var2, int var3) throws IOException;
}
