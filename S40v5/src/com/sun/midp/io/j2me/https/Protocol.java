package com.sun.midp.io.j2me.https;

import com.nokia.mid.impl.isa.io.HttpsSecurityInfo;
import java.io.IOException;
import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SecurityInfo;

public class Protocol extends com.sun.midp.io.j2me.http.Protocol implements HttpsConnection {
   private HttpsSecurityInfo dZ = null;

   public Protocol() {
      this.protocol = "javax.microedition.io.Connector.https";
      this.protocolType = "https";
   }

   protected String parseProtocol() throws IOException {
      int var1;
      if ((var1 = this.url.indexOf(58)) <= 0) {
         throw new IOException("malformed URL");
      } else {
         String var2;
         if (!(var2 = this.url.substring(0, var1)).equals("https")) {
            throw new IOException("protocol must be 'https'");
         } else {
            this.index = var1 + 1;
            return var2;
         }
      }
   }

   public int getPort() {
      return this.port == -1 ? 443 : this.port;
   }

   public SecurityInfo getSecurityInfo() throws IOException {
      this.connect();
      if (this.dZ == null) {
         this.dZ = new HttpsSecurityInfo(this);
      }

      return this.dZ;
   }
}
