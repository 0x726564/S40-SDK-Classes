package com.nokia.mid.impl.isa.io.protocol.external.localmsg;

import com.nokia.mid.s40.io.LocalMessageProtocolConnection;
import com.nokia.mid.s40.io.LocalMessageProtocolMessage;
import com.sun.cldc.io.ConnectionBaseInterface;
import com.sun.midp.io.ConnectionBaseAdapter;
import java.io.IOException;
import javax.microedition.io.Connection;

public class Protocol implements LocalMessageProtocolConnection, ConnectionBaseInterface {
   private int nativeHandle;

   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      (new Protocol.SecurityInnerClass()).openPrim(name, mode, timeouts, true);
      if (name.startsWith("//:")) {
         return (new ServerProtocol()).openPrim(name, mode, timeouts);
      } else {
         String address = name.substring(2);
         this.open0(address);
         return this;
      }
   }

   public LocalMessageProtocolMessage newMessage(byte[] data) {
      return new Message(data);
   }

   public int receive(byte[] data) throws IOException {
      if (data == null) {
         throw new NullPointerException();
      } else {
         Message message = new Message(data);
         this.receive((LocalMessageProtocolMessage)message);
         return message.getLength();
      }
   }

   public void receive(LocalMessageProtocolMessage message) throws IOException {
      if (!(message instanceof Message)) {
         throw new IllegalArgumentException("not obtained from newMessage");
      } else {
         this.receive0((Message)message);
      }
   }

   public void send(byte[] data, int offset, int length) throws IOException {
      if (data == null) {
         throw new NullPointerException();
      } else if (offset >= 0 && length >= 0 && offset + length <= data.length) {
         this.send0(data, offset, length);
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public void close() throws IOException {
      this.close0();
   }

   public native String getLocalName() throws IOException;

   public native String getRemoteName() throws IOException;

   private native void open0(String var1) throws IOException;

   private native void receive0(Message var1) throws IOException;

   private native void send0(byte[] var1, int var2, int var3) throws IOException;

   private native void close0() throws IOException;

   class SecurityInnerClass extends ConnectionBaseAdapter {
      SecurityInnerClass() {
         this.protocol = "com.nokia.mid.s40.io.Connector.localmsg";
      }

      protected void connect(String name, int mode, boolean timeouts) {
      }

      protected void disconnect() throws IOException {
      }

      protected int readBytes(byte[] b, int off, int len) throws IOException {
         throw new IOException();
      }

      protected int writeBytes(byte[] b, int off, int len) throws IOException {
         throw new IOException();
      }
   }
}
