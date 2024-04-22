package com.nokia.mid.impl.isa.io.protocol.external.apdu;

import java.io.IOException;
import javax.microedition.apdu.APDUConnection;
import javax.microedition.io.ConnectionNotFoundException;

public class AIDConnection extends APDUConnectionImpl {
   private AIDConnection() {
   }

   public static APDUConnection getInstance(String var0) throws ConnectionNotFoundException {
      AIDConnection var1 = null;
      byte[] var2;
      if ((var2 = u(var0)).length >= 5 && var2.length <= 16) {
         var1 = IsaSession.a(var2);
      }

      return var1;
   }

   public byte[] exchangeAPDU(byte[] var1) throws IOException {
      b(var1);
      if (isEnvelope(var1)) {
         throw new IllegalArgumentException();
      } else {
         return this.commonExchangeAPDU(var1);
      }
   }

   public final String toString() {
      StringBuffer var1;
      (var1 = new StringBuffer(64)).append("Nokia ApduConnection Type AID [");
      var1.append(this.getStateString());
      var1.append(']');
      return var1.toString();
   }

   private static byte[] u(String var0) {
      int var1;
      byte[] var2 = new byte[var1 = var0.length()];
      int var3 = 0;
      int var4 = 0;
      boolean var5 = false;

      boolean var6;
      do {
         int var9;
         var6 = (var9 = var0.indexOf(46, var3)) > -1;

         try {
            int var7 = var6 ? var9 : var1;
            var3 = Integer.parseInt(var0.substring(var3, var7), 16);
            if (0 > var3 || var3 > 255) {
               var4 = 0;
               break;
            }

            var2[var4] = (byte)var3;
         } catch (NumberFormatException var8) {
            break;
         }

         ++var4;
         var3 = var9 + 1;
      } while(var6);

      byte[] var10 = new byte[var4];
      System.arraycopy(var2, 0, var10, 0, var4);
      return var10;
   }
}
