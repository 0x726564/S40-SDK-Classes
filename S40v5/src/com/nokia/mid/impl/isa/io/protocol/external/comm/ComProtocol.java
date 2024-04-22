package com.nokia.mid.impl.isa.io.protocol.external.comm;

import com.sun.midp.io.NetworkConnectionBase;
import java.io.IOException;
import javax.microedition.io.CommConnection;

public final class ComProtocol extends NetworkConnectionBase implements CommConnection {
   private static int[] dm = new int[1];
   private static int[] dn = new int[1];
   private static int[] do = new int[1];
   private int dp = 0;
   private static int dq = 1;

   public ComProtocol() {
      super(0);
      super.protocol = "javax.microedition.io.Connector.comm";
   }

   public final int getBaudRate() {
      return this.getBaudRate0(this.dp);
   }

   public final int setBaudRate(int var1) {
      return this.getBaudRate0(this.dp);
   }

   protected final void connect(String var1, int var2, boolean var3) throws IOException {
      String var11 = var1.substring(0, 3);
      String var12 = null;
      boolean var4 = false;
      var1 = var1.substring(3);

      try {
         var12 = CCUriParser.T(var1);
      } catch (IllegalArgumentException var8) {
         throw new IllegalArgumentException("Cannot extract COM port number");
      }

      int var13;
      try {
         var13 = Integer.parseInt(var12);
         var11 = var11.concat(var12);
      } catch (NumberFormatException var7) {
         throw new IllegalArgumentException("Cannot extract COM port ID");
      }

      CCUriParser.R(var1.substring(var12.length()));
      synchronized(do) {
         ++dq;
      }

      synchronized(dm) {
         synchronized(dn) {
            var2 = System.getProperty("microedition.commports").indexOf(var11);
            if (-1 == var2) {
               throw new IOException("Device not found");
            }

            this.open0(var13);
            this.dp = var13;
         }

      }
   }

   protected final int nonBufferedRead(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var1.length >= var2 + var3) {
         if (var3 == 0) {
            return 0;
         } else {
            synchronized(dm) {
               return this.readBytes0(this.dp, var1, var2, var3, true);
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   protected final int readBytesNonBlocking(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var1.length >= var2 + var3) {
         if (var3 == 0) {
            return 0;
         } else {
            synchronized(dm) {
               return this.readBytes0(this.dp, var1, var2, var3, false);
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final int available() throws IOException {
      synchronized(dm) {
         return this.availableBytes0(this.dp);
      }
   }

   protected final int writeBytes(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var1.length >= var2 + var3) {
         if (var3 == 0) {
            return 0;
         } else {
            synchronized(dn) {
               return this.writeBytes0(this.dp, var1, var2, var3);
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   protected final void disconnect() throws IOException {
      synchronized(dn) {
         this.disconnect0(this.dp);

         try {
            Thread.sleep(200L);
         } catch (InterruptedException var2) {
         }

      }
   }

   final native void open0(int var1) throws IOException;

   final native void disconnect0(int var1) throws IOException;

   final native int readBytes0(int var1, byte[] var2, int var3, int var4, boolean var5) throws IOException;

   final native int availableBytes0(int var1);

   final native int writeBytes0(int var1, byte[] var2, int var3, int var4) throws IOException;

   final native int getBaudRate0(int var1);
}
