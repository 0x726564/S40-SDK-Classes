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
   public Connection openPrim(String uri, int mode, boolean timeouts) throws IOException {
      int slotIndex = uri.indexOf(":");
      int targetIndex = uri.indexOf(";target=");
      if (targetIndex < 0) {
         throw new IllegalArgumentException();
      } else {
         int var6;
         if (targetIndex == slotIndex + 1) {
            var6 = 0;
         } else {
            try {
               var6 = Integer.parseInt(uri.substring(slotIndex + 1, targetIndex), 16);
            } catch (NumberFormatException var8) {
               throw new IllegalArgumentException("Invalid slot number");
            }
         }

         if (var6 != 0) {
            throw new ConnectionNotFoundException();
         } else {
            return APDUConnectionImpl.getInstance(uri.substring(targetIndex + 8));
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
