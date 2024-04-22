package com.nokia.mid.impl.isa.bluetooth;

import java.io.IOException;
import javax.bluetooth.BluetoothConnectionException;

public class CommonBluetooth {
   public static final byte MASTER_MASK = 8;
   public static final byte ENCRYPT_MASK = 4;
   public static final byte AUTHORIZE_MASK = 2;
   public static final byte AUTHENTICATE_MASK = 1;
   public static final String SPP = "btspp";
   public static final String L2CAP = "btl2cap";
   public static final byte ACTIVE = 0;
   public static final byte INACTIVE = 1;

   private CommonBluetooth() {
   }

   public static URLParser parseConnectionString(String var0, String var1) throws BluetoothConnectionException {
      URLParser var2 = new URLParser(var0, var1);
      return var2;
   }

   public static boolean validHexNumber(String var0) {
      String var1 = new String("0123456789abcdef");
      if (var0 != null && var0.length() != 0) {
         for(int var2 = 0; var2 < var0.length(); ++var2) {
            if (var1.indexOf(var0.charAt(var2)) == -1) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean validDecNumber(String var0) {
      String var1 = new String("0123456789");
      if (var0 != null && var0.length() != 0) {
         for(int var2 = 0; var2 < var0.length(); ++var2) {
            if (var1.indexOf(var0.charAt(var2)) == -1) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static byte[] getByteAddress(String var0) {
      if (var0 == null) {
         throw new NullPointerException("Null BD address");
      } else if (var0.length() != 12) {
         throw new IllegalArgumentException("invalid BD address length");
      } else {
         int var1 = 0;
         byte[] var2 = new byte[6];

         for(int var3 = 0; var3 < 6; ++var3) {
            var2[var3] = (byte)Integer.parseInt(var0.substring(var1, var1 + 2), 16);
            var1 += 2;
         }

         return var2;
      }
   }

   public static String getStringAddress(byte[] var0) {
      if (var0 == null) {
         throw new NullPointerException("Null BD address");
      } else if (var0.length != 6) {
         throw new IllegalArgumentException("invalid BD address length");
      } else {
         StringBuffer var1 = new StringBuffer(12);

         for(int var2 = 0; var2 < 6; ++var2) {
            var1.append(Integer.toHexString((var0[var2] & 240) >> 4));
            var1.append(Integer.toHexString(var0[var2] & 15));
         }

         return var1.toString();
      }
   }

   public static synchronized void checkPermission(boolean var0) throws IOException {
      checkPermission0(var0);
   }

   public static synchronized byte activateMedia() {
      return (byte)(!activateMedia0() ? 1 : 0);
   }

   private static native void checkPermission0(boolean var0) throws IOException;

   private static native boolean activateMedia0();
}
