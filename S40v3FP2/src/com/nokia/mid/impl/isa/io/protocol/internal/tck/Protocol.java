package com.nokia.mid.impl.isa.io.protocol.internal.tck;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;

public class Protocol implements ConnectionBaseInterface, StreamConnection {
   int handle;
   private int mode;
   int opens = 0;
   private boolean copen = false;
   protected boolean isopen = false;
   protected boolean osopen = false;

   public void open(String var1, int var2, boolean var3) throws IOException {
      throw new RuntimeException("Should not be called");
   }

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      try {
         this.open0(var1, var2, var3);
         ++this.opens;
         this.copen = true;
         this.mode = var2;
         return this;
      } catch (InterruptedException var5) {
         throw new IOException(var5.toString());
      }
   }

   public void open(int var1, int var2) throws IOException {
      this.handle = var1;
      ++this.opens;
      this.copen = true;
      this.mode = var2;
   }

   void ensureOpen() throws IOException {
      if (!this.copen) {
         throw new IOException("Connection closed");
      }
   }

   public synchronized InputStream openInputStream() throws IOException {
      this.ensureOpen();
      if ((this.mode & 1) == 0) {
         throw new IOException("Connection not open for reading");
      } else if (this.isopen) {
         throw new IOException("Input stream already opened");
      } else {
         this.isopen = true;
         PrivateInputStream var1 = new PrivateInputStream(this);
         ++this.opens;
         return var1;
      }
   }

   public synchronized OutputStream openOutputStream() throws IOException {
      this.ensureOpen();
      if ((this.mode & 2) == 0) {
         throw new IOException("Connection not open for writing");
      } else if (this.osopen) {
         throw new IOException("Output stream already opened");
      } else {
         this.osopen = true;
         PrivateOutputStream var1 = new PrivateOutputStream(this);
         ++this.opens;
         return var1;
      }
   }

   public synchronized void close() throws IOException {
      if (this.copen) {
         this.copen = false;
         this.realClose();
      }

   }

   synchronized void realClose() throws IOException {
      if (--this.opens == 0) {
         this.close0();
      }

   }

   public DataInputStream openDataInputStream() throws IOException {
      return new DataInputStream(this.openInputStream());
   }

   public DataOutputStream openDataOutputStream() throws IOException {
      return new DataOutputStream(this.openOutputStream());
   }

   protected native void open0(String var1, int var2, boolean var3) throws IOException, InterruptedException;

   protected native int read0(byte[] var1, int var2, int var3) throws IOException;

   protected native int write0(byte[] var1, int var2, int var3) throws IOException;

   protected native int available0() throws IOException;

   protected native void close0() throws IOException;
}
