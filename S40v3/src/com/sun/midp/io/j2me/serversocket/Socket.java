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

   public void open(int var1) throws IOException {
      synchronized(GeneralSharedIO.networkPermissionLock) {
         this.checkPermission0();
      }

      int var2;
      synchronized(SocketCommon.socketLock) {
         var2 = this.open0("", var1 > 0 ? var1 : 0);
      }

      if (var2 <= 0) {
         if (var2 == -23) {
            throw new InterruptedIOException("timed out");
         } else {
            throw new IOException("Error occured whilst opening connection");
         }
      } else {
         this.handle = var2;
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
         int var2 = this.accept0();
         if (var2 >= 0) {
            Protocol var1 = new Protocol();
            var1.open(var2);
            return var1;
         }

         if (var2 != -45) {
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
      String var1 = "";
      boolean var2 = false;
      this.ensureOpen();

      while(!var2) {
         var1 = this.getLocalAddress0();
         if (var1.equals("retry")) {
            try {
               Thread.sleep(100L);
            } catch (InterruptedException var4) {
               throw new IOException();
            }
         } else {
            if (var1.equals("")) {
               throw new IOException();
            }

            var2 = true;
         }
      }

      return var1;
   }

   public int getLocalPort() throws IOException {
      int var1 = -2;
      this.ensureOpen();

      while(var1 <= 0) {
         if ((var1 = this.getLocalPort0()) <= 0) {
            if (var1 != -45) {
               throw new IOException();
            }

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var3) {
               throw new IOException();
            }
         }
      }

      return var1;
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
