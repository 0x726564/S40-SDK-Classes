package com.nokia.mid.impl.isa.io.protocol.external.apdu;

import java.io.IOException;
import javax.microedition.apdu.APDUConnection;
import javax.microedition.io.ConnectionNotFoundException;

public class SATConnection extends APDUConnectionImpl {
   private static byte CLASS_BYTE = 0;
   private static final byte SW1_SIM_MORE_DATA_9E = -98;
   private static final byte SW1_SIM_MORE_DATA_62 = 98;
   private static final byte SW1_SIM_MORE_DATA_63 = 99;
   private static final byte SW1_OK = -112;
   private static final byte SW2_OK = 0;

   public static APDUConnection getInstance() throws ConnectionNotFoundException {
      SATConnection var0 = null;
      updateClassByte();
      var0 = IsaSession.INSTANCE.getSatConnection();
      return var0;
   }

   public byte[] exchangeAPDU(byte[] var1) throws IOException {
      checkAPDU(var1);
      insertClaByte(var1);
      byte[] var2 = this.commonExchangeAPDU(var1);
      filterMoreDataTo9000(var2);
      return var2;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer(64);
      var1.append("Nokia ApduConnection Type SAT [");
      var1.append(this.getStateString());
      var1.append(", ");
      var1.append(this.getSatTypeString());
      var1.append(']');
      return var1.toString();
   }

   private static void checkAPDU(byte[] var0) {
      checkFormatApdu(var0);
      if (!isEnvelope(var0)) {
         throw new IllegalArgumentException();
      }
   }

   private static void insertClaByte(byte[] var0) {
      var0[0] = CLASS_BYTE;
   }

   private static void filterMoreDataTo9000(byte[] var0) {
      int var1 = var0.length;
      byte var2 = var0[var1 - 2];
      boolean var3 = var2 == -98 || var2 == 98 || var2 == 99;
      if (var3) {
         var0[var1 - 2] = -112;
         var0[var1 - 1] = 0;
      }

   }

   private static void updateClassByte() throws ConnectionNotFoundException {
      if (CLASS_BYTE == 0 && (CLASS_BYTE = IsaSession.INSTANCE.getSatCLA()) == 0) {
         throw new ConnectionNotFoundException();
      }
   }

   private String getSatTypeString() {
      if (CLASS_BYTE != 0) {
         return CLASS_BYTE == 128 ? "UICC" : "ICC";
      } else {
         return "";
      }
   }
}
