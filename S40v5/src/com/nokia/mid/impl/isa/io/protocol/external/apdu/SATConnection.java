package com.nokia.mid.impl.isa.io.protocol.external.apdu;

import java.io.IOException;
import javax.microedition.apdu.APDUConnection;
import javax.microedition.io.ConnectionNotFoundException;

public class SATConnection extends APDUConnectionImpl {
   private static byte jo = 0;

   public static APDUConnection getInstance() throws ConnectionNotFoundException {
      Object var0 = null;
      if (jo == 0 && (jo = IsaSession.INSTANCE.getSatCLA()) == 0) {
         throw new ConnectionNotFoundException();
      } else {
         return IsaSession.INSTANCE.getSatConnection();
      }
   }

   public byte[] exchangeAPDU(byte[] var1) throws IOException {
      b(var1);
      if (!isEnvelope(var1)) {
         throw new IllegalArgumentException();
      } else {
         var1[0] = jo;
         byte[] var2;
         byte[] var4;
         int var5 = (var2 = var4 = this.commonExchangeAPDU(var1)).length;
         byte var3;
         if ((var3 = var2[var5 - 2]) == -98 || var3 == 98 || var3 == 99) {
            var2[var5 - 2] = -112;
            var2[var5 - 1] = 0;
         }

         return var4;
      }
   }

   public final String toString() {
      StringBuffer var1;
      (var1 = new StringBuffer(64)).append("Nokia ApduConnection Type SAT [");
      var1.append(this.getStateString());
      var1.append(", ");
      var1.append(this.getSatTypeString());
      var1.append(']');
      return var1.toString();
   }

   private String getSatTypeString() {
      if (jo != 0) {
         return jo == 128 ? "UICC" : "ICC";
      } else {
         return "";
      }
   }
}
