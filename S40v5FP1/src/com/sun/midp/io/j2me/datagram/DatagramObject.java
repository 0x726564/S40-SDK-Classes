package com.sun.midp.io.j2me.datagram;

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

   public DatagramObject(byte[] buf, int len) {
      this.setData(buf, 0, len);
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

   public void setAddress(String addr) {
      if (addr == null) {
         throw new IllegalArgumentException("Invalid address");
      } else {
         HttpUrl url = new HttpUrl(addr);
         if (url.scheme != null && url.scheme.equals("datagram")) {
            if (url.path == null && url.query == null && url.fragment == null) {
               this.port = url.port;
               if (url.host == null) {
                  throw new IllegalArgumentException("Missing host");
               } else if (System.getProperty("sprintpcs.profiles") == null || !System.getProperty("sprintpcs.profiles").equals("SPRINTPCS-1.0") || !url.host.equals("127.0.0.1") && !url.host.equals("localhost")) {
                  if (this.port == -1) {
                     throw new IllegalArgumentException("Missing port");
                  } else {
                     this.address = addr;
                  }
               } else {
                  throw new IllegalArgumentException("localhost not allowed");
               }
            } else {
               throw new IllegalArgumentException("Malformed address");
            }
         } else {
            throw new IllegalArgumentException("Invalid scheme");
         }
      }
   }

   public void setAddress(Datagram reference) {
      this.setAddress(reference.getAddress());
   }

   public void setLength(int len) {
      this.setData(this.buffer, this.offset, len);
   }

   public void setData(byte[] buf, int off, int len) {
      if (len >= 0 && off >= 0 && buf != null && (off <= 0 || off != buf.length) && len + off <= buf.length && len + off >= 0) {
         this.buffer = buf;
         this.offset = off;
         this.length = len;
      } else {
         throw new IllegalArgumentException("Illegal length or offset");
      }
   }

   public void reset() {
      this.readWritePosition = 0;
      this.offset = 0;
      this.length = 0;
   }

   public long skip(long n) throws IOException {
      if (n < 0L) {
         return 0L;
      } else if (this.readWritePosition >= this.length) {
         return 0L;
      } else {
         int min = Math.min((int)n, this.length - this.readWritePosition);
         this.readWritePosition += min;
         return (long)min;
      }
   }

   public int read() {
      return this.readWritePosition >= this.length ? -1 : this.buffer[this.offset + this.readWritePosition++] & 255;
   }

   public void write(int ch) throws IOException {
      if (this.offset + this.readWritePosition >= this.buffer.length) {
         throw new IOException("Buffer full");
      } else {
         this.buffer[this.offset + this.readWritePosition++] = (byte)ch;
         this.length = this.readWritePosition;
      }
   }
}
