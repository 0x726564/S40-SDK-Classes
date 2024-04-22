package com.nokia.mid.impl.isa.io.protocol.internal.wap;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;

public class Protocol implements ConnectionBaseInterface, StreamConnection {
   private String name;
   private int ek = 0;
   private boolean dU = false;
   private boolean connected = false;
   boolean el = false;
   boolean em = false;

   public void open(String var1, int var2, boolean var3) throws IOException {
      throw new RuntimeException("Should not be called");
   }

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      boolean var4 = false;
      boolean var5 = false;
      String var6;
      if (var1.substring(0, 9).compareTo("//http://") == 0) {
         var4 = true;
         var6 = var1.substring(7);
      } else if (var1.substring(0, 10).compareTo("//https://") == 0) {
         var5 = true;
         var6 = var1.substring(8);
      } else {
         var6 = var1;
      }

      this.open0(var6, var2, var3);
      ++this.ek;
      this.dU = true;
      if (var4) {
         this.name = "http:" + var6;
      } else if (var5) {
         this.name = "https:" + var6;
      } else {
         this.name = "http:" + var6;
      }

      return this;
   }

   private void ensureOpen() throws IOException {
      if (!this.dU) {
         throw new IOException("Connection closed");
      }
   }

   private void H() throws IOException {
      if (!this.connected) {
         this.connect0(this.name);
         this.connected = true;
      }

   }

   public InputStream openInputStream() throws IOException {
      this.ensureOpen();
      this.H();
      if (this.el) {
         throw new IOException("Input stream already opened");
      } else {
         this.el = true;
         PrivateInputStream var1 = new PrivateInputStream(this);
         ++this.ek;
         return var1;
      }
   }

   public void prepareCMSourceId() throws IOException {
      this.ensureOpen();
      this.H();
      if (this.el) {
         throw new IOException("Input stream already opened");
      } else {
         this.el = true;
         ++this.ek;
      }
   }

   public OutputStream openOutputStream() throws IOException {
      this.ensureOpen();
      if (this.em) {
         throw new IOException("Output stream already opened");
      } else {
         this.em = true;
         PrivateOutputStream var1 = new PrivateOutputStream(this);
         ++this.ek;
         return var1;
      }
   }

   public void close() throws IOException {
      if (this.dU) {
         this.dU = false;
         this.I();
      }

   }

   final void I() throws IOException {
      if (--this.ek == 0) {
         this.close0();
      }

   }

   public DataInputStream openDataInputStream() throws IOException {
      return new DataInputStream(this.openInputStream());
   }

   public DataOutputStream openDataOutputStream() throws IOException {
      return new DataOutputStream(this.openOutputStream());
   }

   native void open0(String var1, int var2, boolean var3) throws IOException;

   native void connect0(String var1) throws IOException;

   native int write0(byte[] var1, int var2, int var3) throws IOException;

   native int read0(byte[] var1, int var2, int var3) throws IOException;

   native void close0() throws IOException;

   native int available0() throws IOException;
}
