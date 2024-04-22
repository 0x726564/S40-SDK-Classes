package com.nokia.mid.impl.isa.io.protocol.external.comm;

import com.sun.midp.io.NetworkConnectionBase;
import java.io.IOException;
import javax.microedition.io.CommConnection;

public final class ComProtocol extends NetworkConnectionBase implements CommConnection {
   private static int[] read_lock = new int[1];
   private static int[] write_lock = new int[1];
   private static int[] classLock = new int[1];
   private int com_port_number = 0;
   private int instanceID = 0;
   private static int nextUniqueInstanceID = 1;

   public ComProtocol() {
      super(0);
      super.protocol = "javax.microedition.io.Connector.comm";
   }

   public int getBaudRate() {
      return this.getBaudRate0(this.com_port_number);
   }

   public int setBaudRate(int baudrate) {
      return this.getBaudRate0(this.com_port_number);
   }

   protected void connect(String name, int mode, boolean timeouts) throws IOException {
      String port_number_str = name.substring(0, 3);
      String number_str = null;
      int port = false;
      String uri = name.substring(3);

      try {
         number_str = CCUriParser.extractNumberOnlyString(uri);
      } catch (IllegalArgumentException var16) {
         throw new IllegalArgumentException("Cannot extract COM port number");
      }

      int port;
      try {
         port = Integer.parseInt(number_str);
         port_number_str = port_number_str.concat(number_str);
      } catch (NumberFormatException var15) {
         throw new IllegalArgumentException("Cannot extract COM port ID");
      }

      CCUriParser.validateURI(uri.substring(number_str.length()));
      synchronized(classLock) {
         this.instanceID = nextUniqueInstanceID++;
      }

      synchronized(read_lock) {
         synchronized(write_lock) {
            String comports = System.getProperty("microedition.commports");
            int i = comports.indexOf(port_number_str);
            if (-1 == i) {
               throw new IOException("Device not found");
            }

            this.open0(port);
            this.com_port_number = port;
         }

      }
   }

   protected int nonBufferedRead(byte[] b, int off, int len) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off >= 0 && len >= 0 && b.length >= off + len) {
         if (len == 0) {
            return 0;
         } else {
            synchronized(read_lock) {
               return this.readBytes0(this.com_port_number, b, off, len, true);
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   protected int readBytesNonBlocking(byte[] b, int off, int len) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off >= 0 && len >= 0 && b.length >= off + len) {
         if (len == 0) {
            return 0;
         } else {
            synchronized(read_lock) {
               return this.readBytes0(this.com_port_number, b, off, len, false);
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int available() throws IOException {
      synchronized(read_lock) {
         return this.availableBytes0(this.com_port_number);
      }
   }

   protected int writeBytes(byte[] b, int off, int len) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off >= 0 && len >= 0 && b.length >= off + len) {
         if (len == 0) {
            return 0;
         } else {
            synchronized(write_lock) {
               return this.writeBytes0(this.com_port_number, b, off, len);
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   protected void disconnect() throws IOException {
      synchronized(write_lock) {
         this.disconnect0(this.com_port_number);

         try {
            Thread.sleep(200L);
         } catch (InterruptedException var4) {
         }

      }
   }

   native void open0(int var1) throws IOException;

   native void disconnect0(int var1) throws IOException;

   native int readBytes0(int var1, byte[] var2, int var3, int var4, boolean var5) throws IOException;

   native int availableBytes0(int var1);

   native int writeBytes0(int var1, byte[] var2, int var3, int var4) throws IOException;

   native int getBaudRate0(int var1);
}
