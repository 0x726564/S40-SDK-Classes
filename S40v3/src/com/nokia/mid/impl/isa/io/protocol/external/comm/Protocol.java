package com.nokia.mid.impl.isa.io.protocol.external.comm;

import com.sun.cldc.io.ConnectionBaseInterface;
import com.sun.midp.io.ConnectionBaseAdapter;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;

public final class Protocol implements ConnectionBaseInterface {
   private static String commConnectionRoot = "com.nokia.mid.impl.isa.io.protocol.external.comm";

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      Class var4;
      ConnectionBaseAdapter var5;
      switch(CCUriParser.findPortIDIndex(var1)) {
      case 0:
         try {
            var4 = Class.forName(commConnectionRoot + ".IrProtocol");
            var5 = (ConnectionBaseAdapter)var4.newInstance();
            return var5.openPrim(var1, var2, var3);
         } catch (IOException var10) {
            throw var10;
         } catch (SecurityException var11) {
            throw var11;
         } catch (IllegalArgumentException var12) {
            throw var12;
         } catch (Exception var13) {
            throw new ConnectionNotFoundException("CommConnection not found." + var13.toString());
         }
      case 1:
         try {
            var4 = Class.forName(commConnectionRoot + ".ComProtocol");
            var5 = (ConnectionBaseAdapter)var4.newInstance();
            return var5.openPrim(var1, var2, var3);
         } catch (IOException var6) {
            throw var6;
         } catch (SecurityException var7) {
            throw var7;
         } catch (IllegalArgumentException var8) {
            throw var8;
         } catch (Exception var9) {
            throw new ConnectionNotFoundException("CommConnection not found." + var9.toString());
         }
      default:
         throw new RuntimeException("Error in opening primary connection.");
      }
   }
}
