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
   int handle;
   private String name;
   int opens = 0;
   private boolean copen = false;
   private boolean connected = false;
   boolean isopen = false;
   boolean osopen = false;
   boolean chunkTransferMode = false;

   public void open(String name, int mode, boolean timeouts) throws IOException {
      throw new RuntimeException("Should not be called");
   }

   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      boolean httpIndication = false;
      boolean httpsIndication = false;
      String modifiedName;
      if (name.substring(0, 9).compareTo("//http://") == 0) {
         httpIndication = true;
         modifiedName = name.substring(7);
      } else if (name.substring(0, 10).compareTo("//https://") == 0) {
         httpsIndication = true;
         modifiedName = name.substring(8);
      } else {
         modifiedName = name;
      }

      this.open0(modifiedName, mode, timeouts);
      ++this.opens;
      this.copen = true;
      if (httpIndication) {
         this.name = "http:" + modifiedName;
      } else if (httpsIndication) {
         this.name = "https:" + modifiedName;
      } else {
         this.name = "http:" + modifiedName;
      }

      return this;
   }

   void ensureOpen() throws IOException {
      if (!this.copen) {
         throw new IOException("Connection closed");
      }
   }

   void ensureConnected() throws IOException {
      if (!this.connected) {
         this.connect0(this.name);
         this.connected = true;
      }

   }

   public InputStream openInputStream() throws IOException {
      this.ensureOpen();
      this.ensureConnected();
      if (this.isopen) {
         throw new IOException("Input stream already opened");
      } else {
         this.isopen = true;
         InputStream in = new PrivateInputStream(this);
         ++this.opens;
         return in;
      }
   }

   public void prepareCMSourceId() throws IOException {
      this.ensureOpen();
      this.ensureConnected();
      if (this.isopen) {
         throw new IOException("Input stream already opened");
      } else {
         this.isopen = true;
         ++this.opens;
      }
   }

   public OutputStream openOutputStream() throws IOException {
      this.ensureOpen();
      if (this.osopen) {
         throw new IOException("Output stream already opened");
      } else {
         this.osopen = true;
         OutputStream os = new PrivateOutputStream(this);
         ++this.opens;
         return os;
      }
   }

   public void close() throws IOException {
      if (this.copen) {
         this.copen = false;
         this.realClose();
      }

   }

   void realClose() throws IOException {
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

   native void open0(String var1, int var2, boolean var3) throws IOException;

   native void connect0(String var1) throws IOException;

   native int write0(byte[] var1, int var2, int var3) throws IOException;

   native int read0(byte[] var1, int var2, int var3) throws IOException;

   native void close0() throws IOException;

   native int available0() throws IOException;

   native void closeCMSourceId0() throws IOException;

   public void setChunkTransferMode(boolean transferMode) {
      this.chunkTransferMode = transferMode;
   }

   public boolean getChunkTransferMode() {
      return this.chunkTransferMode;
   }
}
