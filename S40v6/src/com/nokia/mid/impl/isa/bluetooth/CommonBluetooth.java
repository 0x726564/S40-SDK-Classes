package com.nokia.mid.impl.isa.bluetooth;

import com.nokia.mid.impl.isa.util.SharedObjects;
import java.io.IOException;
import javax.bluetooth.BluetoothConnectionException;

public class CommonBluetooth {
   public static final byte MASTER_MASK = 8;
   public static final byte ENCRYPT_MASK = 4;
   public static final byte AUTHORIZE_MASK = 2;
   public static final byte AUTHENTICATE_MASK = 1;
   public static final String SPP = "btspp";
   public static final String L2CAP = "btl2cap";
   public static final String GOEP = "btgoep";
   public static final byte ACTIVE = 0;
   public static final byte INACTIVE = 1;
   private static final Object sharedLock = SharedObjects.getLock("com.nokia.mid.impl.isa.bluetooth.CommonBluetooth.permission");

   private CommonBluetooth() {
   }

   public static URLParser parseConnectionString(String connectionURL, String protocol) throws BluetoothConnectionException {
      URLParser parser = new URLParser(connectionURL, protocol);
      return parser;
   }

   public static boolean validHexNumber(String num) {
      String validHexValues = new String("0123456789abcdef");
      if (num != null && num.length() != 0) {
         for(int i = 0; i < num.length(); ++i) {
            if (validHexValues.indexOf(num.charAt(i)) == -1) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean validDecNumber(String num) {
      String validDecValues = new String("0123456789");
      if (num != null && num.length() != 0) {
         for(int i = 0; i < num.length(); ++i) {
            if (validDecValues.indexOf(num.charAt(i)) == -1) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static byte[] getByteAddress(String address) {
      if (address == null) {
         throw new NullPointerException("Null BD address");
      } else if (address.length() != 12) {
         throw new IllegalArgumentException("invalid BD address length");
      } else {
         int index = 0;
         byte[] byteArray = new byte[6];

         for(int i = 0; i < 6; ++i) {
            byteArray[i] = (byte)Integer.parseInt(address.substring(index, index + 2), 16);
            index += 2;
         }

         return byteArray;
      }
   }

   public static String getStringAddress(byte[] address) {
      if (address == null) {
         throw new NullPointerException("Null BD address");
      } else if (address.length != 6) {
         throw new IllegalArgumentException("invalid BD address length");
      } else {
         StringBuffer stringAddress = new StringBuffer(12);

         for(int i = 0; i < 6; ++i) {
            stringAddress.append(Integer.toHexString((address[i] & 240) >> 4));
            stringAddress.append(Integer.toHexString(address[i] & 15));
         }

         return stringAddress.toString();
      }
   }

   public static void checkPermission(boolean isClient) throws IOException {
      synchronized(sharedLock) {
         checkPermission0(isClient, false);
      }
   }

   public static synchronized void checkPermission(boolean isClient, boolean goep) throws IOException {
      checkPermission0(isClient, goep);
   }

   public static byte activateMedia() {
      synchronized(sharedLock) {
         return (byte)(!activateMedia0() ? 1 : 0);
      }
   }

   private static native void checkPermission0(boolean var0, boolean var1) throws IOException;

   private static native boolean activateMedia0();
}
