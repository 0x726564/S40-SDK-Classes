package com.nokia.mid.impl.isa.io;

import com.nokia.mid.impl.isa.pki.NetworkCertificate;
import com.sun.midp.io.j2me.ssl.Protocol;
import javax.microedition.io.SecurityInfo;
import javax.microedition.pki.Certificate;

public class SslSecurityInfo implements SecurityInfo {
   private int hE;
   private Certificate hF;

   private native int getCipherSuite0(Protocol var1);

   private native int getCertificateId0(Protocol var1);

   public SslSecurityInfo(Protocol var1) {
      this.hE = this.getCipherSuite0(var1);
      this.hF = new NetworkCertificate(this.getCertificateId0(var1));
   }

   public Certificate getServerCertificate() {
      return this.hF;
   }

   public String getProtocolVersion() {
      return "3.1";
   }

   public String getProtocolName() {
      return "TLS";
   }

   public String getCipherSuite() {
      String var1;
      switch(this.hE) {
      case 0:
         var1 = "SSL_NULL_WITH_NULL_NULL";
         break;
      case 1:
         var1 = "SSL_RSA_WITH_NULL_MD5";
         break;
      case 2:
         var1 = "SSL_RSA_WITH_NULL_SHA";
         break;
      case 3:
         var1 = "SSL_RSA_EXPORT_WITH_RC4_40_MD5";
         break;
      case 4:
         var1 = "SSL_RSA_WITH_RC4_128_MD5";
         break;
      case 5:
         var1 = "SSL_RSA_WITH_RC4_128_SHA";
         break;
      case 6:
         var1 = "SSL_RSA_EXPORT_WITH_RC2_CBC_40_MD5";
         break;
      case 7:
         var1 = "SSL_RSA_WITH_IDEA_CBC_SHA";
         break;
      case 8:
         var1 = "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA";
         break;
      case 9:
         var1 = "SSL_RSA_WITH_DES_CBC_SHA";
         break;
      case 10:
         var1 = "SSL_RSA_WITH_3DES_EDE_CBC_SHA";
         break;
      case 11:
         var1 = "SSL_DH_DSS_EXPORT_WITH_DES40_CBC_SHA";
         break;
      case 12:
         var1 = "SSL_DH_DSS_WITH_DES_CBC_SHA";
         break;
      case 13:
         var1 = "SSL_DH_DSS_WITH_3DES_EDE_CBC_SHA";
         break;
      case 14:
         var1 = "SSL_DH_RSA_EXPORT_WITH_DES40_CBC_SHA";
         break;
      case 15:
         var1 = "SSL_DH_RSA_WITH_DES_CBC_SHA";
         break;
      case 16:
         var1 = "SSL_DH_RSA_WITH_3DES_EDE_CBC_SHA";
         break;
      case 17:
         var1 = "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA";
         break;
      case 18:
         var1 = "SSL_DHE_DSS_WITH_DES_CBC_SHA";
         break;
      case 19:
         var1 = "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA";
         break;
      case 20:
         var1 = "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA";
         break;
      case 21:
         var1 = "SSL_DHE_RSA_WITH_DES_CBC_SHA";
         break;
      case 22:
         var1 = "SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA";
         break;
      case 23:
         var1 = "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5";
         break;
      case 24:
         var1 = "SSL_DH_anon_WITH_RC4_128_MD5";
         break;
      case 25:
         var1 = "SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA";
         break;
      case 26:
         var1 = "SSL_DH_anon_WITH_DES_CBC_SHA";
         break;
      case 27:
         var1 = "SSL_DH_anon_WITH_3DES_EDE_CBC_SHA";
         break;
      case 28:
      case 29:
      case 30:
      case 31:
      case 32:
      case 33:
      case 34:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 46:
      default:
         var1 = "Unkown";
         break;
      case 47:
         var1 = "SSL_RSA_WITH_AES_128_CBC_SHA";
         break;
      case 48:
         var1 = "SSL_DH_DSS_WITH_AES_128_CBC_SHA";
         break;
      case 49:
         var1 = "SSL_DH_RSA_WITH_AES_128_CBC_SHA";
         break;
      case 50:
         var1 = "SSL_DHE_DSS_WITH_AES_128_CBC_SHA";
         break;
      case 51:
         var1 = "SSL_DHE_RSA_WITH_AES_128_CBC_SHA";
         break;
      case 52:
         var1 = "SSL_DH_anon_WITH_AES_128_CBC_SHA";
         break;
      case 53:
         var1 = "SSL_RSA_WITH_AES_256_CBC_SHA";
         break;
      case 54:
         var1 = "SSL_DH_DSS_WITH_AES_256_CBC_SHA";
         break;
      case 55:
         var1 = "SSL_DH_RSA_WITH_AES_256_CBC_SHA";
         break;
      case 56:
         var1 = "SSL_DHE_DSS_WITH_AES_256_CBC_SHA";
         break;
      case 57:
         var1 = "SSL_DHE_RSA_WITH_AES_256_CBC_SHA ";
         break;
      case 58:
         var1 = "SSL_DH_anon_WITH_AES_256_CBC_SHA";
      }

      return var1;
   }
}
