package com.nokia.mid.impl.isa.io.protocol.external.apdu;

import java.io.IOException;
import javax.microedition.apdu.APDUConnection;
import javax.microedition.io.ConnectionNotFoundException;

public class AIDConnection extends APDUConnectionImpl {
   private static final int MAX_AID_SIZE = 16;
   private static final int MIN_AID_SIZE = 5;

   private AIDConnection() {
   }

   public static APDUConnection getInstance(String string_aid) throws ConnectionNotFoundException {
      AIDConnection conn = null;
      byte[] aid = parseAID(string_aid);
      if (aid.length >= 5 && aid.length <= 16) {
         conn = IsaSession.INSTANCE.getAidConnection(aid);
      }

      return conn;
   }

   public byte[] exchangeAPDU(byte[] commandAPDU) throws IOException {
      checkFormatApdu(commandAPDU);
      if (isEnvelope(commandAPDU)) {
         throw new IllegalArgumentException();
      } else {
         return this.commonExchangeAPDU(commandAPDU);
      }
   }

   public String toString() {
      StringBuffer s = new StringBuffer(64);
      s.append("Nokia ApduConnection Type AID [");
      s.append(this.getStateString());
      s.append(']');
      return s.toString();
   }

   private static byte[] parseAID(String string_aid) {
      int string_len = string_aid.length();
      byte[] aid_buf = new byte[string_len];
      int start_ix = 0;
      int byte_ix = 0;
      boolean var5 = false;

      boolean more_dots_in_string;
      do {
         int dot_ix = string_aid.indexOf(46, start_ix);
         more_dots_in_string = dot_ix > -1;

         try {
            int end_ix = more_dots_in_string ? dot_ix : string_len;
            int parsedInt = Integer.parseInt(string_aid.substring(start_ix, end_ix), 16);
            if (0 > parsedInt || parsedInt > 255) {
               byte_ix = 0;
               break;
            }

            aid_buf[byte_ix] = (byte)parsedInt;
         } catch (NumberFormatException var9) {
            break;
         }

         ++byte_ix;
         start_ix = dot_ix + 1;
      } while(more_dots_in_string);

      byte[] result_aid = new byte[byte_ix];
      System.arraycopy(aid_buf, 0, result_aid, 0, byte_ix);
      return result_aid;
   }
}
