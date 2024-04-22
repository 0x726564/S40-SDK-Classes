package com.nokia.mid.impl.isa.obex;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.obex.HeaderSet;
import javax.obex.Operation;

public class OperationImpl implements Operation {
   private boolean S;
   private int opcode;
   private boolean closed = false;
   private boolean T = false;
   private OperationImpl.ObexInputStream U;
   private OperationImpl.ObexOutputStream V;
   private AbstractObexConnection W;
   private String contentType;
   private long X = -1L;
   private int Y = 11;
   private boolean aborted = false;

   public OperationImpl(AbstractObexConnection var1) {
      this.W = var1;
      var1.setInOperation(false);
      this.closed = true;
   }

   public OperationImpl(AbstractObexConnection var1, boolean var2) {
      if (var1 != null && var1.getIncomingHeaders() != null) {
         this.W = var1;
         this.S = var2;
         this.opcode = var2 ? 3 : 2;
         this.j();
      } else {
         throw new NullPointerException();
      }
   }

   public void finish(int var1) {
      if (!this.aborted) {
         HeaderSetImpl var2;
         (var2 = this.W.getOutgoingHeaders()).setResponseCode(var1);
         Packet var5;
         (var5 = new Packet()).packetSort = this.opcode;
         if (this.V != null && OperationImpl.ObexOutputStream.a(this.V) > 0) {
            byte[] var3 = new byte[OperationImpl.ObexOutputStream.a(this.V)];
            System.arraycopy(OperationImpl.ObexOutputStream.b(this.V), 0, var3, 0, OperationImpl.ObexOutputStream.a(this.V));
            var2.setHeaderPrivate(73, var3);
         }

         var5.isFinal = true;

         try {
            this.sendPacket(var5);
            return;
         } catch (IOException var4) {
         }
      }

   }

   public void abort() throws IOException {
      this.b(true);
      if (!this.W.isClient()) {
         throw new IOException("Not allowed");
      } else {
         this.aborted = true;
         Packet var1;
         (var1 = new Packet()).packetSort = 255;
         var1.isFinal = true;
         this.sendPacket(var1);
         this.i();
         this.close();
      }
   }

   public HeaderSet getReceivedHeaders() throws IOException {
      this.b(false);
      return this.W.getIncomingHeaders().clone();
   }

   public synchronized void sendHeaders(HeaderSet var1) throws IOException {
      this.b(true);
      this.h();
      if (var1 == null) {
         throw new NullPointerException("headers cannot be null");
      } else {
         try {
            if (((HeaderSetImpl)var1).isReceivedHeaderSet()) {
               throw new IllegalArgumentException("HeaderSet not created by a call to createHeaderSet()");
            }
         } catch (ClassCastException var9) {
            throw new IllegalArgumentException("HeaderSet not created by a call to createHeaderSet()");
         }

         HeaderSetImpl var2;
         int[] var3;
         if ((var3 = (var2 = this.W.getOutgoingHeaders()).getHeaderList()) != null) {
            for(int var4 = 0; var4 < var3.length; ++var4) {
               int var5 = var3[var4];
               Object var6 = var2.getHeader(var5);
               this.Y -= HeaderSetImpl.headerSize(var5, HeaderSetImpl.getHeaderType(var5, false), var6);
            }

            var2 = new HeaderSetImpl();
            this.W.setOutgoingHeaders(var2);
         }

         int[] var12 = var1.getHeaderList();
         HeaderSetImpl var13 = new HeaderSetImpl();
         if (var12 != null) {
            for(int var11 = 0; var11 < var12.length; ++var11) {
               int var7 = var12[var11];
               Object var8 = var1.getHeader(var7);
               int var15 = HeaderSetImpl.headerSize(var7, HeaderSetImpl.getHeaderType(var7, false), var8);
               if (this.Y + var15 > this.W.REMOTE_MAX_OBEX_PACKET_SIZE) {
                  var2.includeHeaders(var13);
                  Packet var14 = new Packet();
                  if (this.W.isClient()) {
                     var14.isFinal = false;
                     var14.packetSort = this.opcode;
                  } else {
                     var14.isFinal = true;
                     var2.setResponseCodePrivate(144);
                  }

                  this.sendPacket(var14);
                  Packet var10 = this.i();
                  if (this.W.isClient()) {
                     if (var10.respCode != 144) {
                        throw new IOException("Error while sending headers");
                     }
                  } else if (var10.packetSort != var14.packetSort) {
                     throw new IOException("Error while sending headers");
                  }

                  (var13 = new HeaderSetImpl()).setHeader(var7, var8);
                  var2 = this.W.getOutgoingHeaders();
                  this.Y += var15;
               } else {
                  var13.setHeader(var7, var8);
                  this.Y += var15;
               }
            }
         }

         if (var13.getHeaderList() != null) {
            var2.includeHeaders(var13);
         }

      }
   }

   public int getResponseCode() throws IOException {
      if (!this.W.isClient()) {
         throw new IOException("Not allowed");
      } else {
         if (!this.closed) {
            this.g();
            if (this.S) {
               if (this.U != null && !OperationImpl.ObexInputStream.a(this.U)) {
                  this.U.close();
               }
            } else if (this.V != null && !OperationImpl.ObexOutputStream.c(this.V)) {
               this.V.close();
            }

            this.T = true;
         }

         if (this.W.getIncomingHeaders() == null) {
            throw new IOException("Error retrieving received headers");
         } else {
            return this.W.getIncomingHeaders().getResponseCode();
         }
      }
   }

   public String getType() {
      return this.contentType;
   }

   public String getEncoding() {
      return null;
   }

   public long getLength() {
      return this.X;
   }

   public synchronized InputStream openInputStream() throws IOException {
      this.b(true);
      if (this.U != null) {
         throw new IOException("InputStream already open");
      } else {
         HeaderSetImpl var1 = this.W.getIncomingHeaders();
         this.U = new OperationImpl.ObexInputStream(this);
         Object var2 = null;
         byte[] var3;
         if ((var3 = (byte[])var1.getHeaderPrivate(72)) == null && (var3 = (byte[])var1.getHeaderPrivate(73)) != null) {
            OperationImpl.ObexInputStream.a(this.U, true);
         }

         OperationImpl.ObexInputStream.a(this.U, var3);
         return this.U;
      }
   }

   public DataInputStream openDataInputStream() throws IOException {
      return new DataInputStream(this.openInputStream());
   }

   public synchronized OutputStream openOutputStream() throws IOException {
      this.b(true);
      if (this.V != null) {
         throw new IOException("OutputStream already open");
      } else {
         Packet var1;
         if (this.W.isClient()) {
            if (!this.S) {
               (var1 = new Packet()).packetSort = this.opcode;
               this.sendPacket(var1);
               if ((var1 = this.W.getPacket()).packetSort != this.opcode || var1.respCode != 144 || !var1.isFinal) {
                  this.W.setInOperation(false);
                  this.closed = true;
               }
            }
         } else {
            (var1 = new Packet()).packetSort = this.opcode;
            var1.isFinal = true;
            this.W.getOutgoingHeaders().setResponseCodePrivate(144);
            this.sendPacket(var1);
            if (this.i().packetSort != this.opcode) {
               throw new IOException("Unexpected packet received during operation");
            }
         }

         this.V = new OperationImpl.ObexOutputStream(this);
         return this.V;
      }
   }

   public DataOutputStream openDataOutputStream() throws IOException {
      return new DataOutputStream(this.openOutputStream());
   }

   private void g() throws IOException {
      if (this.W.isClient() && !this.closed && !this.S && !this.aborted && this.V == null) {
         Packet var1;
         (var1 = new Packet()).packetSort = 2;
         var1.isFinal = true;
         this.sendPacket(var1);
         Packet var2;
         if ((var2 = this.i()).packetSort != 2 || !var2.isFinal) {
            throw new IOException("Error in DELETE operation");
         }
      }

   }

   public void close() throws IOException {
      if (!this.closed) {
         this.g();
         if (this.V != null && !OperationImpl.ObexOutputStream.c(this.V)) {
            this.V.close();
         }

         this.closed = true;
         this.W.setInOperation(false);
      }
   }

   private void b(boolean var1) throws IOException {
      if (this.closed) {
         throw new IOException("Operation closed");
      } else if (var1 && (this.T || this.S && this.U != null && OperationImpl.ObexInputStream.a(this.U) || !this.S && this.V != null && OperationImpl.ObexOutputStream.c(this.V))) {
         throw new IOException("Transaction already ended");
      }
   }

   private void h() throws IOException {
      if (this.W.isClient() && this.W.getIncomingHeaders() != null) {
         if (this.W.getIncomingHeaders().getResponseCode() != 144 && this.W.getIncomingHeaders().getResponseCode() != 160) {
            throw new IOException("Error code received");
         }
      }
   }

   private void sendPacket(Packet var1) throws IOException {
      this.W.sendPacket(var1);
      this.Y = 11;
   }

   private Packet i() throws IOException {
      Packet var1 = this.W.getPacket();
      this.j();
      if (var1.packetSort == 255 && !this.W.isClient() && var1.isFinal) {
         Packet var2;
         (var2 = new Packet()).isFinal = true;
         var2.packetSort = 255;
         this.W.getOutgoingHeaders().setResponseCode(160);
         this.sendPacket(var2);
         this.close();
         this.aborted = true;
      }

      return var1;
   }

   private void j() {
      try {
         Object var1;
         if ((var1 = this.W.getIncomingHeaders().getHeader(195)) != null) {
            this.X = (Long)var1;
         }

         this.contentType = (String)this.W.getIncomingHeaders().getHeader(66);
      } catch (IOException var2) {
      }
   }

   static AbstractObexConnection a(OperationImpl var0) {
      return var0.W;
   }

   static boolean b(OperationImpl var0) {
      return var0.aborted;
   }

   static void c(OperationImpl var0) throws IOException {
      var0.h();
   }

   static int d(OperationImpl var0) {
      return var0.Y;
   }

   static int e(OperationImpl var0) {
      return var0.opcode;
   }

   static void a(OperationImpl var0, Packet var1) throws IOException {
      var0.sendPacket(var1);
   }

   static Packet f(OperationImpl var0) throws IOException {
      return var0.i();
   }

   class ObexInputStream extends InputStream {
      private boolean e;
      private byte[] buffer;
      private int f;
      private boolean g;
      private final OperationImpl h;

      ObexInputStream(OperationImpl var1) {
         this.h = var1;
         this.e = false;
         this.f = 0;
         this.f = 0;
         this.g = false;
      }

      private void b() throws IOException {
         if (this.g) {
            throw new IOException("Stream closed");
         }
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         this.b();
         OperationImpl.c(this.h);
         if (var1 == null) {
            throw new NullPointerException("The array cannot be null");
         } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
            return var3 == 0 ? 0 : this.a(var1, var2, var3);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public int read(byte[] var1) throws IOException {
         if (var1 == null) {
            throw new NullPointerException("The array cannot be null");
         } else {
            return this.read(var1, 0, var1.length);
         }
      }

      public int read() throws IOException {
         byte[] var1 = new byte[1];
         return this.read(var1, 0, 1) == -1 ? -1 : (256 + var1[0]) % 256;
      }

      private synchronized int a(byte[] var1, int var2, int var3) throws IOException {
         if (this.e && this.buffer != null && this.f == this.buffer.length) {
            return -1;
         } else {
            if (this.buffer == null || this.buffer.length - this.f == 0) {
               byte[] var4 = null;
               Packet var5;
               HeaderSetImpl var6;
               Packet var7;
               if (OperationImpl.a(this.h).isClient()) {
                  (var7 = new Packet()).packetSort = OperationImpl.e(this.h);
                  var7.isFinal = true;
                  OperationImpl.a(this.h, var7);
                  var5 = OperationImpl.f(this.h);
                  var6 = OperationImpl.a(this.h).getIncomingHeaders();
                  switch(var5.respCode) {
                  case 144:
                     var4 = (byte[])var6.getHeaderPrivate(72);
                     break;
                  case 160:
                     if (!var5.isFinal) {
                        throw new IOException("Error in operation");
                     }

                     this.e = true;
                     var4 = (byte[])var6.getHeaderPrivate(73);
                     break;
                  default:
                     this.close();
                     throw new IOException("Error in operation - code is " + var5.respCode);
                  }
               } else {
                  (var7 = new Packet()).packetSort = OperationImpl.e(this.h);
                  var7.isFinal = true;

                  while(var4 == null) {
                     OperationImpl.a(this.h).getOutgoingHeaders().setResponseCodePrivate(144);
                     OperationImpl.a(this.h, var7);
                     if ((var5 = OperationImpl.f(this.h)).packetSort == 255) {
                        return -1;
                     }

                     var6 = new HeaderSetImpl(var5.serializedHeaders);
                     if (var5.packetSort != OperationImpl.e(this.h)) {
                        this.close();
                        throw new IOException("Error in operation - packet sort " + var5.packetSort);
                     }

                     if (var5.isFinal) {
                        this.e = true;
                        var4 = (byte[])var6.getHeaderPrivate(73);
                     } else {
                        var4 = (byte[])var6.getHeaderPrivate(72);
                     }
                  }
               }

               if (var4 == null) {
                  return -1;
               }

               this.buffer = new byte[var4.length];
               this.f = 0;
               System.arraycopy(var4, 0, this.buffer, 0, this.buffer.length);
            }

            int var9 = this.buffer.length - this.f;
            int var8 = var3 - var2;
            int var10 = Math.min(var9, var8);
            System.arraycopy(this.buffer, this.f, var1, var2, var10);
            this.f += var10;
            return var10;
         }
      }

      public void close() throws IOException {
         this.b();
         this.g = true;
      }

      static boolean a(OperationImpl.ObexInputStream var0) {
         return var0.g;
      }

      static boolean a(OperationImpl.ObexInputStream var0, boolean var1) {
         return var0.e = true;
      }

      static byte[] a(OperationImpl.ObexInputStream var0, byte[] var1) {
         return var0.buffer = var1;
      }
   }

   class ObexOutputStream extends OutputStream {
      private byte[] buffer;
      private int f;
      private boolean g;
      private final OperationImpl h;

      ObexOutputStream(OperationImpl var1) {
         this.h = var1;
         this.f = 0;
         this.g = false;
      }

      private void b() throws IOException {
         if (this.g) {
            throw new IOException("Stream closed");
         }
      }

      public void close() throws IOException {
         if (!this.g) {
            if (OperationImpl.a(this.h).isClient() && !OperationImpl.b(this.h)) {
               this.k(true);
            }

            this.g = true;
         }
      }

      public void flush() throws IOException {
         this.b();
         this.k(false);
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.b();
         OperationImpl.c(this.h);
         if (var1 == null) {
            throw new NullPointerException("The array cannot be null");
         } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
            if (var3 != 0) {
               this.d(var1, var2, var3);
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void write(byte[] var1) throws IOException {
         if (var1 == null) {
            throw new NullPointerException("The array cannot be null");
         } else {
            this.write(var1, 0, var1.length);
         }
      }

      public void write(int var1) throws IOException {
         this.write(new byte[]{(byte)var1}, 0, 1);
      }

      private synchronized void d(byte[] var1, int var2, int var3) throws IOException {
         if (this.buffer == null) {
            this.buffer = new byte[OperationImpl.a(this.h).REMOTE_MAX_OBEX_PACKET_SIZE];
            this.f = 0;
         }

         while(var3 - var2 > 0) {
            int var4 = Math.min(var3 - var2, this.buffer.length - this.f - OperationImpl.d(this.h));
            System.arraycopy(var1, var2, this.buffer, this.f, var4);
            var2 += var4;
            this.f += var4;
            if (this.f + OperationImpl.d(this.h) >= this.buffer.length - 1) {
               this.k(false);
            }
         }

      }

      private synchronized void k(boolean var1) throws IOException {
         if (!var1) {
            OperationImpl.c(this.h);
         }

         HeaderSetImpl var2 = OperationImpl.a(this.h).getOutgoingHeaders();
         Packet var3;
         (var3 = new Packet()).packetSort = OperationImpl.e(this.h);
         int var4 = 0;
         byte[] var5;
         Packet var6;
         if (this.f + OperationImpl.d(this.h) > OperationImpl.a(this.h).REMOTE_MAX_OBEX_PACKET_SIZE) {
            var5 = new byte[var4 = OperationImpl.a(this.h).REMOTE_MAX_OBEX_PACKET_SIZE - OperationImpl.d(this.h)];
            System.arraycopy(this.buffer, 0, var5, 0, var4);
            if (!OperationImpl.a(this.h).isClient()) {
               var2.setResponseCodePrivate(144);
            }

            var2.setHeaderPrivate(72, var5);
            OperationImpl.a(this.h, var3);
            var2 = OperationImpl.a(this.h).getOutgoingHeaders();
            var6 = OperationImpl.f(this.h);
            if (OperationImpl.a(this.h).isClient()) {
               if (OperationImpl.a(this.h).getIncomingHeaders().getResponseCode() != 144) {
                  this.close();
                  throw new IOException("Error in operation");
               }
            } else if (var6.packetSort != OperationImpl.e(this.h) && var6.packetSort != 255) {
               this.close();
               throw new IOException("Unexpected packet received during GET operation");
            }
         }

         var5 = null;
         if (this.buffer != null && this.f > 0) {
            int var7;
            var5 = new byte[var7 = this.f - var4];
            System.arraycopy(this.buffer, var4, var5, 0, var7);
            this.f = 0;
         }

         if (var1) {
            var3.isFinal = true;
            if (var5 == null) {
               var5 = new byte[0];
            }

            var2.setHeaderPrivate(73, var5);
         } else if (var5 != null) {
            var2.setHeaderPrivate(72, var5);
         }

         if (!OperationImpl.a(this.h).isClient()) {
            var2.setResponseCodePrivate(144);
         }

         OperationImpl.a(this.h, var3);
         var6 = OperationImpl.f(this.h);
         if (OperationImpl.a(this.h).isClient()) {
            if (var1) {
               if (!var6.isFinal) {
                  this.close();
                  throw new IOException("Error closing OBEX connection");
               }
            } else if (OperationImpl.a(this.h).getIncomingHeaders().getResponseCode() != 144) {
               this.g = true;
               return;
            }
         } else if (var6.packetSort != OperationImpl.e(this.h) && var6.packetSort != 255) {
            this.close();
            throw new IOException("Unexpected packet received during GET operation");
         }

      }

      static int a(OperationImpl.ObexOutputStream var0) {
         return var0.f;
      }

      static byte[] b(OperationImpl.ObexOutputStream var0) {
         return var0.buffer;
      }

      static boolean c(OperationImpl.ObexOutputStream var0) {
         return var0.g;
      }
   }
}
