package com.nokia.mid.impl.isa.io.protocol.external.comm;

import com.sun.cldc.io.ConnectionBaseInterface;
import com.sun.midp.io.ConnectionBaseAdapter;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;

public final class Protocol implements ConnectionBaseInterface {
   private static String commConnectionRoot = "com.nokia.mid.impl.isa.io.protocol.external.comm";

   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      Class clazz;
      ConnectionBaseAdapter cc_class;
      switch(CCUriParser.findPortIDIndex(name)) {
      case 0:
         try {
            clazz = Class.forName(commConnectionRoot + ".IrProtocol");
            cc_class = (ConnectionBaseAdapter)clazz.newInstance();
            return cc_class.openPrim(name, mode, timeouts);
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
      case 2:
         try {
            clazz = Class.forName(commConnectionRoot + ".ComProtocol");
            cc_class = (ConnectionBaseAdapter)clazz.newInstance();
            return cc_class.openPrim(name, mode, timeouts);
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
