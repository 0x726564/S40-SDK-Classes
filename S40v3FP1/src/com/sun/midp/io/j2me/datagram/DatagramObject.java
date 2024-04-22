package com.sun.midp.io.j2me.datagram;

import com.nokia.mid.pri.PriAccess;
import com.sun.cldc.io.GeneralBase;
import com.sun.midp.io.HttpUrl;
import java.io.IOException;
import javax.microedition.io.Datagram;

public class DatagramObject extends GeneralBase implements Datagram {
   private static final int MAX_HOST_LENGTH = 256;
   private byte[] buffer;
   private int offset;
   private int length;
   String address;
   int ipNumber;
   int port;
   private int readWritePosition;

   public DatagramObject(byte[] var1, int var2) {
      this.setData(var1, 0, var2);
   }

   public String getAddress() {
      return this.address;
   }

   public byte[] getData() {
      return this.buffer;
   }

   public int getLength() {
      return this.length;
   }

   public int getOffset() {
      return this.offset;
   }

   public void setAddress(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid address");
      } else {
         HttpUrl var2 = new HttpUrl(var1);
         if (var2.scheme != null && var2.scheme.equals("datagram")) {
            if (var2.path == null && var2.query == null && var2.fragment == null) {
               this.port = var2.port;
               if (var2.host == null) {
                  throw new IllegalArgumentException("Missing host");
               } else if (PriAccess.getInt(5) == 1 && (var2.host.equals("127.0.0.1") || var2.host.equals("localhost"))) {
                  throw new IllegalArgumentException("localhost not allowed");
               } else if (this.port == -1) {
                  throw new IllegalArgumentException("Missing port");
               } else {
                  this.address = var1;
               }
            } else {
               throw new IllegalArgumentException("Malformed address");
            }
         } else {
            throw new IllegalArgumentException("Invalid scheme");
         }
      }
   }

   public void setAddress(Datagram var1) {
      this.setAddress(var1.getAddress());
   }

   public void setLength(int var1) {
      this.setData(this.buffer, this.offset, var1);
   }

   public void setData(byte[] var1, int var2, int var3) {
      if (var3 >= 0 && var2 >= 0 && var1 != null && (var2 <= 0 || var2 != var1.length) && var3 + var2 <= var1.length && var3 + var2 >= 0) {
         this.buffer = var1;
         this.offset = var2;
         this.length = var3;
      } else {
         throw new IllegalArgumentException("Illegal length or offset");
      }
   }

   public void reset() {
      this.readWritePosition = 0;
      this.offset = 0;
      this.length = 0;
   }

   public long skip(long var1) throws IOException {
      if (var1 < 0L) {
         return 0L;
      } else if (this.readWritePosition >= this.length) {
         return 0L;
      } else {
         int var3 = Math.min((int)var1, this.length - this.readWritePosition);
         this.readWritePosition += var3;
         return (long)var3;
      }
   }

   public int read() {
      return this.readWritePosition >= this.length ? -1 : this.buffer[this.offset + this.readWritePosition++] & 255;
   }

   public void write(int var1) throws IOException {
      if (this.offset + this.readWritePosition >= this.buffer.length) {
         throw new IOException("Buffer full");
      } else {
         this.buffer[this.offset + this.readWritePosition++] = (byte)var1;
         this.length = this.readWritePosition;
      }
   }
}
