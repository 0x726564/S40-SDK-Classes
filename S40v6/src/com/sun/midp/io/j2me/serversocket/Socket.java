package com.sun.midp.io.j2me.serversocket;

import com.nokia.mid.impl.isa.io.GeneralSharedIO;
import com.sun.midp.io.j2me.datagram.SocketCommon;
import com.sun.midp.io.j2me.socket.Protocol;
import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;

public class Socket implements ServerSocketConnection {
   private int handle;
   boolean connectionOpen = false;

   public void open(int port) throws IOException {
      synchronized(GeneralSharedIO.networkPermissionLock) {
         this.checkPermission0();
      }

      int newHandle;
      synchronized(SocketCommon.socketLock) {
         newHandle = this.open0("", port > 0 ? port : 0);
      }

      if (newHandle <= 0) {
         if (newHandle == -23) {
            throw new InterruptedIOException("timed out");
         } else {
            throw new IOException("Error occured whilst opening connection");
         }
      } else {
         this.handle = newHandle;
         this.connectionOpen = true;
      }
   }

   void ensureOpen() throws IOException {
      if (!this.connectionOpen) {
         throw new IOException("Connection closed");
      }
   }

   public native int open0(String var1, int var2) throws IOException;

   public synchronized StreamConnection acceptAndOpen() throws IOException {
      this.ensureOpen();

      while(true) {
         int handle = this.accept0();
         if (handle >= 0) {
            Protocol con = new Protocol();
            con.open(handle);
            return con;
         }

         if (handle != -45) {
            throw new IOException("Accept failed");
         }

         try {
            Thread.sleep(100L);
         } catch (InterruptedException var4) {
            throw new IOException("Accept failed");
         }
      }
   }

   public String getLocalAddress() throws IOException {
      String address = "";
      boolean done = false;
      this.ensureOpen();

      while(!done) {
         address = this.getLocalAddress0();
         if (address.equals("retry")) {
            try {
               Thread.sleep(100L);
            } catch (InterruptedException var4) {
               throw new IOException();
            }
         } else {
            if (address.equals("")) {
               throw new IOException();
            }

            done = true;
         }
      }

      return address;
   }

   public int getLocalPort() throws IOException {
      int retVal = -2;
      this.ensureOpen();

      while(retVal <= 0) {
         if ((retVal = this.getLocalPort0()) <= 0) {
            if (retVal != -45) {
               throw new IOException();
            }

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var3) {
               throw new IOException();
            }
         }
      }

      return retVal;
   }

   public void close() throws IOException {
      if (this.connectionOpen) {
         this.close0();
         this.connectionOpen = false;
      }

   }

   private native int accept0() throws IOException;

   public native void close0() throws IOException;

   private native String getLocalAddress0();

   private native int getLocalPort0();

   private native void checkPermission0() throws IOException;
}
