package com.nokia.mid.impl.isa.io.protocol.external.apdu;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;

public class Protocol implements ConnectionBaseInterface {
   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      int var5 = var1.indexOf(":");
      if ((var2 = var1.indexOf(";target=")) < 0) {
         throw new IllegalArgumentException();
      } else {
         int var6;
         if (var2 == var5 + 1) {
            var6 = 0;
         } else {
            try {
               var6 = Integer.parseInt(var1.substring(var5 + 1, var2), 16);
            } catch (NumberFormatException var4) {
               throw new IllegalArgumentException("Invalid slot number");
            }
         }

         if (var6 != 0) {
            throw new ConnectionNotFoundException();
         } else {
            return APDUConnectionImpl.getInstance(var1.substring(var2 + 8));
         }
      }
   }

   public InputStream openInputStream() throws IOException {
      throw new IllegalArgumentException("");
   }

   public DataInputStream openDataInputStream() throws IOException {
      throw new IllegalArgumentException("");
   }

   public OutputStream openOutputStream() throws IOException {
      throw new IllegalArgumentException("");
   }

   public DataOutputStream openDataOutputStream() throws IOException {
      throw new IllegalArgumentException("");
   }
}
