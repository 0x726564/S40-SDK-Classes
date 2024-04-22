package com.nokia.mid.impl.isa.io.protocol.external.comm;

import com.sun.midp.io.NetworkConnectionBase;
import java.io.IOException;
import javax.microedition.io.CommConnection;

public final class IrProtocol extends NetworkConnectionBase implements CommConnection {
   private static int[] read_lock = new int[1];
   private static int[] write_lock = new int[1];
   private static int[] classLock = new int[1];
   private static int[] dataAvailable_lock = new int[1];
   private int ir_port_number = 0;
   private int instanceID = 0;
   private static int nextUniqueInstanceID = 1;

   public IrProtocol() {
      super(100);
      super.protocol = "javax.microedition.io.Connector.comm";
   }

   protected void connect(String var1, int var2, boolean var3) throws IOException {
      String var4 = var1.substring(2);
      String var5 = null;

      try {
         var5 = CCUriParser.extractNumberOnlyString(var4);
      } catch (IllegalArgumentException var14) {
         throw new IllegalArgumentException("Cannot extract IR port number");
      }

      try {
         this.ir_port_number = Integer.parseInt(var5);
      } catch (NumberFormatException var13) {
         throw new IllegalArgumentException("Cannot extract IR port number");
      }

      CCUriParser.validateURI(var4.substring(var5.length()));
      synchronized(classLock) {
         this.instanceID = nextUniqueInstanceID++;
      }

      if (this.ir_port_number == 0) {
         synchronized(read_lock) {
            synchronized(write_lock) {
               this.open(this.ir_port_number);
            }

         }
      } else {
         throw new IOException("Device not found");
      }
   }

   protected int nonBufferedRead(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var1.length >= var2 + var3) {
         if (var3 == 0) {
            return 0;
         } else {
            synchronized(read_lock) {
               return this.readBytes0(this.ir_port_number, var1, var2, var3, true);
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   protected int readBytesNonBlocking(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var1.length >= var2 + var3) {
         if (var3 == 0) {
            return 0;
         } else {
            synchronized(dataAvailable_lock) {
               return this.readBytes0(this.ir_port_number, var1, var2, var3, false);
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   protected int writeBytes(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var1.length >= var2 + var3) {
         if (var3 == 0) {
            return 0;
         } else {
            synchronized(write_lock) {
               return this.writeBytes0(this.ir_port_number, var1, var2, var3);
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   protected void disconnect() throws IOException {
      synchronized(write_lock) {
         this.disconnect0(this.ir_port_number);
      }
   }

   public int getBaudRate() {
      return this.getBaudRate0(this.ir_port_number);
   }

   public int setBaudRate(int var1) {
      return this.getBaudRate0(this.ir_port_number);
   }

   private void open(int var1) throws IOException {
      long var2 = System.currentTimeMillis();

      try {
         this.open0(var1);
      } catch (IOException var9) {
         if (System.currentTimeMillis() >= var2 + 2000L) {
            throw var9;
         }

         try {
            Thread.sleep(500L);
         } catch (Exception var8) {
         }

         this.open0(var1);
      }

   }

   native void open0(int var1) throws IOException;

   native void disconnect0(int var1) throws IOException;

   native int readBytes0(int var1, byte[] var2, int var3, int var4, boolean var5) throws IOException;

   native int writeBytes0(int var1, byte[] var2, int var3, int var4) throws IOException;

   native int getBaudRate0(int var1);

   static native void registerForNotifications();

   static {
      registerForNotifications();
   }
}
