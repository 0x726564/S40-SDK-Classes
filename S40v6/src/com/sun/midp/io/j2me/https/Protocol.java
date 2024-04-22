package com.sun.midp.io.j2me.https;

import com.nokia.mid.impl.isa.io.HttpsSecurityInfo;
import java.io.IOException;
import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SecurityInfo;

public class Protocol extends com.sun.midp.io.j2me.http.Protocol implements HttpsConnection {
   private HttpsSecurityInfo httpsSecurityInfo = null;

   public Protocol() {
      this.protocol = "javax.microedition.io.Connector.https";
      this.protocolType = "https";
   }

   protected String parseProtocol() throws IOException {
      int n = this.url.indexOf(58);
      if (n <= 0) {
         throw new IOException("malformed URL");
      } else {
         String token = this.url.substring(0, n);
         if (!token.equals("https")) {
            throw new IOException("protocol must be 'https'");
         } else {
            this.index = n + 1;
            return token;
         }
      }
   }

   public int getPort() {
      return this.port == -1 ? 443 : this.port;
   }

   public SecurityInfo getSecurityInfo() throws IOException {
      this.connect();
      this.protectedReadResponseMessage_wap();
      if (this.httpsSecurityInfo == null) {
         this.httpsSecurityInfo = new HttpsSecurityInfo(this);
      }

      return this.httpsSecurityInfo;
   }
}
