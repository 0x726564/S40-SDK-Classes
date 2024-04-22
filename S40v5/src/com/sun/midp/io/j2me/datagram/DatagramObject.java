package com.sun.midp.io.j2me.datagram;

import com.sun.cldc.io.GeneralBase;
import com.sun.midp.io.HttpUrl;
import java.io.IOException;
import javax.microedition.io.Datagram;

public class DatagramObject extends GeneralBase implements Datagram {
   private byte[] buffer;
   private int offset;
   private int length;
   private String dV;
   private int port;
   private int mE;

   public DatagramObject(byte[] var1, int var2) {
      this.setData(var1, 0, var2);
   }

   public String getAddress() {
      return this.dV;
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
         HttpUrl var2;
         if ((var2 = new HttpUrl(var1)).scheme != null && var2.scheme.equals("datagram")) {
            if (var2.path == null && var2.query == null && var2.fragment == null) {
               this.port = var2.port;
               if (var2.host == null) {
                  throw new IllegalArgumentException("Missing host");
               } else if (System.getProperty("sprintpcs.profiles") == null || !System.getProperty("sprintpcs.profiles").equals("SPRINTPCS-1.0") || !var2.host.equals("127.0.0.1") && !var2.host.equals("localhost")) {
                  if (this.port == -1) {
                     throw new IllegalArgumentException("Missing port");
                  } else {
                     this.dV = var1;
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
      this.mE = 0;
      this.offset = 0;
      this.length = 0;
   }

   public long skip(long var1) throws IOException {
      if (var1 < 0L) {
         return 0L;
      } else if (this.mE >= this.length) {
         return 0L;
      } else {
         int var3 = Math.min((int)var1, this.length - this.mE);
         this.mE += var3;
         return (long)var3;
      }
   }

   public int read() {
      return this.mE >= this.length ? -1 : this.buffer[this.offset + this.mE++] & 255;
   }

   public void write(int var1) throws IOException {
      if (this.offset + this.mE >= this.buffer.length) {
         throw new IOException("Buffer full");
      } else {
         this.buffer[this.offset + this.mE++] = (byte)var1;
         this.length = this.mE;
      }
   }
}
