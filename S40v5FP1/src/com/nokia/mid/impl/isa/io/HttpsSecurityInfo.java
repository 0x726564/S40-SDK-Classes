package com.nokia.mid.impl.isa.io;

import com.nokia.mid.impl.isa.pki.NetworkCertificate;
import com.sun.midp.io.j2me.http.Protocol;
import javax.microedition.io.SecurityInfo;
import javax.microedition.pki.Certificate;

public class HttpsSecurityInfo implements SecurityInfo {
   private int cipher_suite;
   private Certificate cert;

   private native int getCipherSuite0(Protocol var1);

   private native int getCertificateId0(Protocol var1);

   public HttpsSecurityInfo(Protocol objectInst) {
      this.cipher_suite = this.getCipherSuite0(objectInst);
      this.cert = new NetworkCertificate(this.getCertificateId0(objectInst));
   }

   public Certificate getServerCertificate() {
      return this.cert;
   }

   public String getProtocolVersion() {
      return "3.1";
   }

   public String getProtocolName() {
      return "TLS";
   }

   public String getCipherSuite() {
      String retVal;
      switch(this.cipher_suite) {
      case 0:
         retVal = "SSL_NULL_WITH_NULL_NULL";
         break;
      case 1:
         retVal = "SSL_RSA_WITH_NULL_MD5";
         break;
      case 2:
         retVal = "SSL_RSA_WITH_NULL_SHA";
         break;
      case 3:
         retVal = "SSL_RSA_EXPORT_WITH_RC4_40_MD5";
         break;
      case 4:
         retVal = "SSL_RSA_WITH_RC4_128_MD5";
         break;
      case 5:
         retVal = "SSL_RSA_WITH_RC4_128_SHA";
         break;
      case 6:
         retVal = "SSL_RSA_EXPORT_WITH_RC2_CBC_40_MD5";
         break;
      case 7:
         retVal = "SSL_RSA_WITH_IDEA_CBC_SHA";
         break;
      case 8:
         retVal = "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA";
         break;
      case 9:
         retVal = "SSL_RSA_WITH_DES_CBC_SHA";
         break;
      case 10:
         retVal = "SSL_RSA_WITH_3DES_EDE_CBC_SHA";
         break;
      case 11:
         retVal = "SSL_DH_DSS_EXPORT_WITH_DES40_CBC_SHA";
         break;
      case 12:
         retVal = "SSL_DH_DSS_WITH_DES_CBC_SHA";
         break;
      case 13:
         retVal = "SSL_DH_DSS_WITH_3DES_EDE_CBC_SHA";
         break;
      case 14:
         retVal = "SSL_DH_RSA_EXPORT_WITH_DES40_CBC_SHA";
         break;
      case 15:
         retVal = "SSL_DH_RSA_WITH_DES_CBC_SHA";
         break;
      case 16:
         retVal = "SSL_DH_RSA_WITH_3DES_EDE_CBC_SHA";
         break;
      case 17:
         retVal = "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA";
         break;
      case 18:
         retVal = "SSL_DHE_DSS_WITH_DES_CBC_SHA";
         break;
      case 19:
         retVal = "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA";
         break;
      case 20:
         retVal = "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA";
         break;
      case 21:
         retVal = "SSL_DHE_RSA_WITH_DES_CBC_SHA";
         break;
      case 22:
         retVal = "SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA";
         break;
      case 23:
         retVal = "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5";
         break;
      case 24:
         retVal = "SSL_DH_anon_WITH_RC4_128_MD5";
         break;
      case 25:
         retVal = "SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA";
         break;
      case 26:
         retVal = "SSL_DH_anon_WITH_DES_CBC_SHA";
         break;
      case 27:
         retVal = "SSL_DH_anon_WITH_3DES_EDE_CBC_SHA";
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
         retVal = "Unkown";
         break;
      case 47:
         retVal = "SSL_RSA_WITH_AES_128_CBC_SHA";
         break;
      case 48:
         retVal = "SSL_DH_DSS_WITH_AES_128_CBC_SHA";
         break;
      case 49:
         retVal = "SSL_DH_RSA_WITH_AES_128_CBC_SHA";
         break;
      case 50:
         retVal = "SSL_DHE_DSS_WITH_AES_128_CBC_SHA";
         break;
      case 51:
         retVal = "SSL_DHE_RSA_WITH_AES_128_CBC_SHA";
         break;
      case 52:
         retVal = "SSL_DH_anon_WITH_AES_128_CBC_SHA";
         break;
      case 53:
         retVal = "SSL_RSA_WITH_AES_256_CBC_SHA";
         break;
      case 54:
         retVal = "SSL_DH_DSS_WITH_AES_256_CBC_SHA";
         break;
      case 55:
         retVal = "SSL_DH_RSA_WITH_AES_256_CBC_SHA";
         break;
      case 56:
         retVal = "SSL_DHE_DSS_WITH_AES_256_CBC_SHA";
         break;
      case 57:
         retVal = "SSL_DHE_RSA_WITH_AES_256_CBC_SHA ";
         break;
      case 58:
         retVal = "SSL_DH_anon_WITH_AES_256_CBC_SHA";
      }

      return retVal;
   }
}
