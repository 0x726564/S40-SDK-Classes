package com.nokia.mid.impl.isa.io.protocol.external.apdu;

import java.io.IOException;
import javax.microedition.apdu.APDUConnection;
import javax.microedition.io.ConnectionNotFoundException;

public class AIDConnection extends APDUConnectionImpl {
   private static final int MAX_AID_SIZE = 16;
   private static final int MIN_AID_SIZE = 5;

   private AIDConnection() {
   }

   public static APDUConnection getInstance(String var0) throws ConnectionNotFoundException {
      AIDConnection var1 = null;
      byte[] var2 = parseAID(var0);
      if (var2.length >= 5 && var2.length <= 16) {
         var1 = IsaSession.INSTANCE.getAidConnection(var2);
      }

      return var1;
   }

   public byte[] exchangeAPDU(byte[] var1) throws IOException {
      checkFormatApdu(var1);
      if (isEnvelope(var1)) {
         throw new IllegalArgumentException();
      } else {
         return this.commonExchangeAPDU(var1);
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer(64);
      var1.append("Nokia ApduConnection Type AID [");
      var1.append(this.getStateString());
      var1.append(']');
      return var1.toString();
   }

   private static byte[] parseAID(String var0) {
      int var1 = var0.length();
      byte[] var2 = new byte[var1];
      int var3 = 0;
      int var4 = 0;
      boolean var5 = false;

      boolean var6;
      do {
         int var10 = var0.indexOf(46, var3);
         var6 = var10 > -1;

         try {
            int var7 = var6 ? var10 : var1;
            int var8 = Integer.parseInt(var0.substring(var3, var7), 16);
            if (0 > var8 || var8 > 255) {
               var4 = 0;
               break;
            }

            var2[var4] = (byte)var8;
         } catch (NumberFormatException var9) {
            break;
         }

         ++var4;
         var3 = var10 + 1;
      } while(var6);

      byte[] var11 = new byte[var4];
      System.arraycopy(var2, 0, var11, 0, var4);
      return var11;
   }
}
