package com.sun.midp.io;

import com.nokia.mid.impl.isa.io.GeneralSharedIO;
import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;

public abstract class ConnectionBaseAdapter implements ConnectionBaseInterface, StreamConnection {
   protected boolean connectionOpen = false;
   protected int iStreams = 0;
   protected int maxIStreams = 1;
   protected int oStreams = 0;
   protected int maxOStreams = 1;
   protected int requiredPermission = -1;
   protected String protocol;
   private boolean permissionChecked;

   protected void verifyPermissionCheck() {
      if (!this.permissionChecked) {
         throw new SecurityException("The permission check was bypassed");
      }
   }

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      return this.openPrim(var1, var2, var3, true);
   }

   public Connection openPrim(String var1, int var2, boolean var3, boolean var4) throws IOException {
      if (var4) {
         this.checkForPermission(var1);
      } else {
         this.permissionChecked = true;
      }

      switch(var2) {
      case 1:
      case 2:
      case 3:
         this.connect(var1, var2, var3);
         this.connectionOpen = true;
         return this;
      default:
         throw new IllegalArgumentException("Illegal mode");
      }
   }

   public void checkForPermission(String var1) throws InterruptedIOException, IOException {
      synchronized(GeneralSharedIO.networkPermissionLock) {
         this.checkProtocolPermission0(this.protocol, var1.toUpperCase());
      }

      this.permissionChecked = true;
   }

   public InputStream openInputStream() throws IOException {
      this.ensureOpen();
      if (this.maxIStreams == 0) {
         throw new IOException("no more input streams available");
      } else {
         BaseInputStream var1 = new BaseInputStream(this);
         --this.maxIStreams;
         ++this.iStreams;
         return var1;
      }
   }

   public DataInputStream openDataInputStream() throws IOException {
      return new DataInputStream(this.openInputStream());
   }

   public OutputStream openOutputStream() throws IOException {
      this.ensureOpen();
      if (this.maxOStreams == 0) {
         throw new IOException("no more output streams available");
      } else {
         BaseOutputStream var1 = new BaseOutputStream(this);
         --this.maxOStreams;
         ++this.oStreams;
         return var1;
      }
   }

   public DataOutputStream openDataOutputStream() throws IOException {
      return new DataOutputStream(this.openOutputStream());
   }

   public void close() throws IOException {
      if (this.connectionOpen) {
         this.connectionOpen = false;
         this.closeCommon();
      }

   }

   protected void closeInputStream() throws IOException {
      --this.iStreams;
      this.closeCommon();
   }

   protected void closeOutputStream() throws IOException {
      --this.oStreams;
      this.closeCommon();
   }

   void closeCommon() throws IOException {
      if (!this.connectionOpen && this.iStreams == 0 && this.oStreams == 0) {
         this.disconnect();
      }

   }

   protected void ensureOpen() throws IOException {
      if (!this.connectionOpen) {
         throw new IOException("Connection closed");
      }
   }

   protected abstract void connect(String var1, int var2, boolean var3) throws IOException;

   protected abstract void disconnect() throws IOException;

   protected abstract int readBytes(byte[] var1, int var2, int var3) throws IOException;

   public int available() throws IOException {
      return 0;
   }

   protected abstract int writeBytes(byte[] var1, int var2, int var3) throws IOException;

   protected void flush() throws IOException {
   }

   native void checkProtocolPermission0(String var1, String var2) throws IOException;
}
