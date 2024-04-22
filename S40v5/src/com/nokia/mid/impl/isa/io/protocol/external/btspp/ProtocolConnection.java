package com.nokia.mid.impl.isa.io.protocol.external.btspp;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import com.nokia.mid.impl.isa.bluetooth.RemoteDeviceAccessor;
import com.nokia.mid.impl.isa.bluetooth.Tracer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.StreamConnection;

public class ProtocolConnection extends Protocol implements RemoteDeviceAccessor, StreamConnection {
   private static final int kg = maxPacketSize0();
   private int gG;
   private byte[] gH = new byte[6];
   private byte[] gI = null;
   private int gK = 0;
   private final boolean gL;
   private boolean kh = false;
   private boolean ki = false;
   private boolean connectionOpen = false;
   private boolean gM = false;
   private final Object kj = new Object();

   ProtocolConnection(boolean var1, int var2) {
      Tracer.println("ProtocolConnection(isClient=" + String.valueOf(var1) + "mode=" + var2 + ")");
      this.gL = var1;
      this.mode = var2;
      this.connectionOpen = true;
   }

   public InputStream openInputStream() throws IOException {
      Tracer.println("openInputStream");
      if (this.mode != 1 && this.mode != 3) {
         Tracer.println("IOException WO");
         throw new IOException("write-only connection");
      } else {
         synchronized(this.kj) {
            if (!this.connectionOpen) {
               Tracer.println("IOException closed");
               throw new IOException("connection already closed");
            } else if (this.kh) {
               Tracer.println("IOException already open");
               throw new IOException("input stream already opened");
            } else {
               ProtocolConnection.PrivateInputStream var2 = new ProtocolConnection.PrivateInputStream(this, this);
               this.kh = true;
               return var2;
            }
         }
      }
   }

   public OutputStream openOutputStream() throws IOException {
      Tracer.println("openOutputStream");
      if (this.mode != 2 && this.mode != 3) {
         Tracer.println("IOException RO");
         throw new IOException("read-only connection");
      } else {
         synchronized(this.kj) {
            if (!this.isClosed && this.connectionOpen) {
               if (this.ki) {
                  Tracer.println("IOException already open");
                  throw new IOException("output stream already opened");
               } else {
                  ProtocolConnection.PrivateOutputStream var2 = new ProtocolConnection.PrivateOutputStream(this, this);
                  this.ki = true;
                  return var2;
               }
            } else {
               Tracer.println("IOException closed");
               throw new IOException("connection already closed");
            }
         }
      }
   }

   public DataInputStream openDataInputStream() throws IOException {
      Tracer.println("openDataInputStream");
      return new DataInputStream(this.openInputStream());
   }

   public DataOutputStream openDataOutputStream() throws IOException {
      Tracer.println("openDataOutputStream");
      return new DataOutputStream(this.openOutputStream());
   }

   public void close() throws IOException {
      synchronized(this.kj) {
         Tracer.println("Connection.close");
         if (this.connectionOpen) {
            this.connectionOpen = false;
            if (!this.isClosed) {
               this.ab();
            }
         }

      }
   }

   public boolean isClient() {
      Tracer.println("isClient()=" + String.valueOf(this.gL));
      return this.gL;
   }

   public String getRemoteDeviceAddress() throws IOException {
      if (this.isClosed() && !this.isPushConnection()) {
         throw new IOException("Connection is closed");
      } else {
         Tracer.println("getRemoteDeviceAddress");
         String var1 = CommonBluetooth.getStringAddress(this.gH);
         Tracer.println("returns " + var1);
         return var1;
      }
   }

   private void ab() throws IOException {
      Tracer.println("closeServiceConnection");
      if (!this.connectionOpen && !this.kh && !this.ki) {
         try {
            Tracer.println("sleep(1000)");
            Thread.sleep(1000L);
         } catch (InterruptedException var1) {
         }

         if (!this.isClosed) {
            Tracer.println("close0(conHandle=" + String.valueOf((int)0) + ")");
            this.close0((byte)0);
            this.isClosed = true;
         }

         this.gG = 0;
      }

   }

   public boolean isPushConnection() {
      return this.gM;
   }

   protected void setPushConnection() {
      this.gM = true;
   }

   private native int read0(int var1, byte[] var2, int var3, int var4);

   private native int write0(int var1, byte[] var2, int var3, int var4);

   private native int available0(int var1);

   private native int flush0(int var1);

   private native void close0(byte var1);

   private static native int maxPacketSize0();

   static int access$000() {
      return kg;
   }

   static int a(ProtocolConnection var0, int var1, byte[] var2, int var3, int var4) {
      return var0.read0(var1, var2, 0, var4);
   }

   static byte[] a(ProtocolConnection var0) {
      return var0.gI;
   }

   static int b(ProtocolConnection var0) {
      return var0.gK;
   }

   static int a(ProtocolConnection var0, int var1) {
      return var0.gK = 0;
   }

   static byte[] a(ProtocolConnection var0, byte[] var1) {
      return var0.gI = null;
   }

   static int b(ProtocolConnection var0, int var1) {
      return var0.available0(var1);
   }

   static Object c(ProtocolConnection var0) {
      return var0.kj;
   }

   static boolean a(ProtocolConnection var0, boolean var1) {
      return var0.kh = false;
   }

   static void d(ProtocolConnection var0) throws IOException {
      var0.ab();
   }

   static int b(ProtocolConnection var0, int var1, byte[] var2, int var3, int var4) {
      return var0.write0(var1, var2, var3, var4);
   }

   static int c(ProtocolConnection var0, int var1) {
      return var0.flush0(var1);
   }

   static boolean b(ProtocolConnection var0, boolean var1) {
      return var0.ki = false;
   }

   private class PrivateOutputStream extends OutputStream {
      private ProtocolConnection ae;
      private final Object mw;
      private final Object ag;
      private byte[] ah;
      private int mx;
      private final ProtocolConnection my;

      PrivateOutputStream(ProtocolConnection var1, ProtocolConnection var2) {
         this.my = var1;
         this.mw = new Object();
         this.ag = new Object();
         this.ah = new byte[ProtocolConnection.access$000() - 1];
         this.mx = 0;
         this.ae = var2;
         Tracer.println("PrivateOutputStream()");
      }

      public void write(int var1) throws IOException {
         byte[] var2 = new byte[]{(byte)var1};
         this.write(var2, 0, 1);
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         if (var1 == null) {
            Tracer.println("write throws NullPointerException");
            throw new NullPointerException("Null b");
         } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
            if (var3 != 0) {
               synchronized(this.mw) {
                  for(int var5 = this.ah.length - this.mx; var3 >= var5; this.mx = 0) {
                     int var8;
                     int var9 = (var8 = var3 + this.mx < ProtocolConnection.access$000() ? var3 + this.mx : ProtocolConnection.access$000()) - this.mx;
                     int var7;
                     byte[] var14;
                     if (this.mx <= 0 && var3 <= ProtocolConnection.access$000()) {
                        var14 = var1;
                        var7 = var2;
                     } else {
                        var14 = new byte[var8];
                        System.arraycopy(this.ah, 0, var14, 0, this.mx);
                        System.arraycopy(var1, var2, var14, this.mx, var9);
                        var7 = 0;
                     }

                     int var6;
                     do {
                        synchronized(this.ag) {
                           this.ensureOpen();
                           ProtocolConnection var11 = this.ae;
                           var6 = ProtocolConnection.b(this.ae, 0, var14, var7, var8);
                        }

                        Thread.yield();
                     } while(1 == var6);

                     var2 += var9;
                     var3 -= var9;
                     var5 = this.ah.length;
                  }

                  if (var3 > 0) {
                     System.arraycopy(var1, var2, this.ah, this.mx, var3);
                     this.mx += var3;
                  }

               }
            }
         } else {
            Tracer.println("write throws IndexOutOfBoundsException");
            throw new IndexOutOfBoundsException();
         }
      }

      public void flush() throws IOException {
         synchronized(this.mw) {
            int var2;
            ProtocolConnection var4;
            if (this.mx > 0) {
               do {
                  synchronized(this.ag) {
                     this.ensureOpen();
                     var4 = this.ae;
                     var2 = ProtocolConnection.b(this.ae, 0, this.ah, 0, this.mx);
                  }

                  Thread.yield();
               } while(1 == var2);

               this.mx = 0;
            }

            do {
               synchronized(this.ag) {
                  this.ensureOpen();
                  var4 = this.ae;
                  var2 = ProtocolConnection.c(this.my, 0);
               }

               Thread.yield();
            } while(1 == var2);

         }
      }

      public void close() throws IOException {
         Tracer.println("OutputStream.close");
         this.flush();
         ProtocolConnection.PrivateOutputStream var5;
         synchronized((var5 = this).ag) {
            if (var5.ae != null) {
               synchronized(ProtocolConnection.c(var5.ae)) {
                  Tracer.println("streamClose");
                  ProtocolConnection.b(var5.ae, false);
                  ProtocolConnection.d(var5.ae);
                  var5.ae = null;
               }
            }

         }
      }

      private void ensureOpen() throws IOException {
         if (this.ae == null || this.ae.isClosed()) {
            Tracer.println("ensureOpen throws IOException");
            throw new IOException("output stream is closed");
         }
      }
   }

   private class PrivateInputStream extends InputStream {
      private ProtocolConnection ae;
      private final Object af = new Object();
      private final Object ag = new Object();
      private byte[] ah = new byte[ProtocolConnection.access$000()];
      private int ai = 0;
      private int aj = 0;

      PrivateInputStream(ProtocolConnection var1, ProtocolConnection var2) {
         this.ae = var2;
         Tracer.println("PrivateInputStream()");
      }

      public int read() throws IOException {
         byte[] var1 = new byte[]{0};
         return -1 == this.read(var1) ? -1 : var1[0] & 255;
      }

      public int read(byte[] var1) throws IOException {
         if (var1 == null) {
            Tracer.println("read throws NullPointerException");
            throw new NullPointerException();
         } else if (var1.length == 0) {
            Tracer.println("read got 0 array");
            return 0;
         } else {
            synchronized(this.af) {
               while(true) {
                  label68: {
                     int var7;
                     if (this.aj == 0) {
                        synchronized(this.ag) {
                           this.ensureOpen();
                           if (!this.ae.isClosed()) {
                              ProtocolConnection var3 = this.ae;
                              if ((var7 = ProtocolConnection.a(this.ae, 0, this.ah, 0, this.ah.length)) <= 0) {
                                 if (var7 == -1) {
                                    Tracer.println("read throws IOException read0 FAILURE");
                                    throw new IOException("error in read");
                                 }
                                 break label68;
                              }

                              this.aj = var7;
                           } else {
                              if (ProtocolConnection.a(this.ae) == null) {
                                 this.ae = null;
                                 byte var10000 = -1;
                                 return var10000;
                              }

                              this.ai = 0;
                              this.ah = ProtocolConnection.a(this.ae);
                              this.aj = ProtocolConnection.b(this.ae);
                              ProtocolConnection.a(this.ae, 0);
                              ProtocolConnection.a(this.ae, (byte[])null);
                           }
                        }
                     }

                     if (var1.length >= this.aj) {
                        var7 = this.aj;
                        System.arraycopy(this.ah, this.ai, var1, 0, var7);
                        this.aj = 0;
                        this.ai = 0;
                     } else {
                        var7 = var1.length;
                        System.arraycopy(this.ah, this.ai, var1, 0, var7);
                        this.aj -= var7;
                        this.ai += var7;
                     }

                     return var7;
                  }

                  Thread.yield();
               }
            }
         }
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         if (var1 == null) {
            Tracer.println("read throws NullPointerException");
            throw new NullPointerException();
         } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
            if (var3 == 0) {
               Tracer.println("read got len=0");
               return 0;
            } else {
               synchronized(this.af) {
                  int var6 = 0;

                  while(var3 != 0) {
                     while(true) {
                        label114: {
                           if (this.aj == 0) {
                              label107: {
                                 byte var10000;
                                 synchronized(this.ag) {
                                    this.ensureOpen();
                                    if (!this.ae.isClosed()) {
                                       ProtocolConnection var5 = this.ae;
                                       int var10;
                                       if ((var10 = ProtocolConnection.a(this.ae, 0, this.ah, 0, this.ah.length)) <= 0) {
                                          if (var10 == 0) {
                                             if (var6 > 0) {
                                                int var11 = var6;
                                                return var11;
                                             }
                                          } else if (var10 == -1) {
                                             Tracer.println("read throws IOException read0 FAILURE");
                                             throw new IOException("error in read");
                                          }
                                          break label114;
                                       }

                                       this.aj = var10;
                                       break label107;
                                    }

                                    if (ProtocolConnection.a(this.ae) != null) {
                                       this.ai = 0;
                                       this.ah = ProtocolConnection.a(this.ae);
                                       this.aj = ProtocolConnection.b(this.ae);
                                       ProtocolConnection.a(this.ae, 0);
                                       ProtocolConnection.a(this.ae, (byte[])null);
                                       break label107;
                                    }

                                    this.ae = null;
                                    var10000 = -1;
                                 }

                                 return var10000;
                              }
                           }

                           if (var3 < this.aj) {
                              System.arraycopy(this.ah, this.ai, var1, var2, var3);
                              var6 += var3;
                              this.aj -= var3;
                              this.ai += var3;
                              return var6;
                           }

                           System.arraycopy(this.ah, this.ai, var1, var2, this.aj);
                           var6 += this.aj;
                           var2 += this.aj;
                           var3 -= this.aj;
                           this.aj = 0;
                           this.ai = 0;
                           continue;
                        }

                        Thread.yield();
                     }
                  }

                  return var6;
               }
            }
         } else {
            Tracer.println("read throws IndexOutOfBoundsException");
            throw new IndexOutOfBoundsException();
         }
      }

      public int available() throws IOException {
         synchronized(this.af) {
            int var10000;
            synchronized(this.ag) {
               this.ensureOpen();
               if (!this.ae.isClosed()) {
                  ProtocolConnection var3 = this.ae;
                  int var6;
                  if ((var6 = ProtocolConnection.b(this.ae, 0)) == -1) {
                     Tracer.println("available throws IOException");
                     throw new IOException("error in available");
                  }

                  var10000 = var6;
                  return var10000;
               }

               if (this.aj <= 0 && ProtocolConnection.b(this.ae) <= 0) {
                  this.ae = null;
                  Tracer.println("available throws IOException");
                  throw new IOException("input stream is closed");
               }

               var10000 = this.aj + ProtocolConnection.b(this.ae);
            }

            return var10000;
         }
      }

      public void close() throws IOException {
         synchronized(this.ag) {
            if (this.ae != null) {
               synchronized(ProtocolConnection.c(this.ae)) {
                  Tracer.println("InputStream.close");
                  ProtocolConnection.a(this.ae, false);
                  ProtocolConnection.d(this.ae);
                  this.ae = null;
               }
            }

         }
      }

      private void ensureOpen() throws IOException {
         if (this.ae == null) {
            Tracer.println("ensureOpen throws IOException");
            throw new IOException("input stream is closed");
         }
      }
   }
}
