package com.nokia.mid.impl.isa.io.protocol.external.comm;

import com.sun.cldc.io.ConnectionBaseInterface;
import com.sun.midp.io.ConnectionBaseAdapter;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;

public final class Protocol implements ConnectionBaseInterface {
   private static String fv = "com.nokia.mid.impl.isa.io.protocol.external.comm";

   public final Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      switch(CCUriParser.S(var1)) {
      case 0:
         try {
            return ((ConnectionBaseAdapter)Class.forName(fv + ".IrProtocol").newInstance()).openPrim(var1, var2, var3);
         } catch (IOException var8) {
            throw var8;
         } catch (SecurityException var9) {
            throw var9;
         } catch (IllegalArgumentException var10) {
            throw var10;
         } catch (Exception var11) {
            throw new ConnectionNotFoundException("CommConnection not found." + var11.toString());
         }
      case 1:
      case 2:
         try {
            return ((ConnectionBaseAdapter)Class.forName(fv + ".ComProtocol").newInstance()).openPrim(var1, var2, var3);
         } catch (IOException var4) {
            throw var4;
         } catch (SecurityException var5) {
            throw var5;
         } catch (IllegalArgumentException var6) {
            throw var6;
         } catch (Exception var7) {
            throw new ConnectionNotFoundException("CommConnection not found." + var7.toString());
         }
      default:
         throw new RuntimeException("Error in opening primary connection.");
      }
   }
}
