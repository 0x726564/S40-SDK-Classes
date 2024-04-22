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
      SATConnection conn = null;
      updateClassByte();
      conn = IsaSession.INSTANCE.getSatConnection();
      return conn;
   }

   public byte[] exchangeAPDU(byte[] commandAPDU) throws IOException {
      checkAPDU(commandAPDU);
      insertClaByte(commandAPDU);
      byte[] respApdu = this.commonExchangeAPDU(commandAPDU);
      filterMoreDataTo9000(respApdu);
      return respApdu;
   }

   public String toString() {
      StringBuffer s = new StringBuffer(64);
      s.append("Nokia ApduConnection Type SAT [");
      s.append(this.getStateString());
      s.append(", ");
      s.append(this.getSatTypeString());
      s.append(']');
      return s.toString();
   }

   private static void checkAPDU(byte[] apdu) {
      checkFormatApdu(apdu);
      if (!isEnvelope(apdu)) {
         throw new IllegalArgumentException();
      }
   }

   private static void insertClaByte(byte[] commandAPDU) {
      commandAPDU[0] = CLASS_BYTE;
   }

   private static void filterMoreDataTo9000(byte[] respApdu) {
      int len = respApdu.length;
      byte sw1 = respApdu[len - 2];
      boolean swNeedsToBeChanged = sw1 == -98 || sw1 == 98 || sw1 == 99;
      if (swNeedsToBeChanged) {
         respApdu[len - 2] = -112;
         respApdu[len - 1] = 0;
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
