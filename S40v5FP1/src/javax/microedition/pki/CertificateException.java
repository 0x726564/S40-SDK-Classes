package javax.microedition.pki;

import java.io.IOException;

public class CertificateException extends IOException {
   private byte reason;
   private Certificate cert;
   public static final byte BAD_EXTENSIONS = 1;
   public static final byte CERTIFICATE_CHAIN_TOO_LONG = 2;
   public static final byte EXPIRED = 3;
   public static final byte UNAUTHORIZED_INTERMEDIATE_CA = 4;
   public static final byte MISSING_SIGNATURE = 5;
   public static final byte NOT_YET_VALID = 6;
   public static final byte SITENAME_MISMATCH = 7;
   public static final byte UNRECOGNIZED_ISSUER = 8;
   public static final byte UNSUPPORTED_SIGALG = 9;
   public static final byte INAPPROPRIATE_KEY_USAGE = 10;
   public static final byte BROKEN_CHAIN = 11;
   public static final byte ROOT_CA_EXPIRED = 12;
   public static final byte UNSUPPORTED_PUBLIC_KEY_TYPE = 13;
   public static final byte VERIFICATION_FAILED = 14;

   public CertificateException(Certificate certificate, byte status) {
      super(getMessageForReason(status));
      this.cert = certificate;
      this.reason = status;
   }

   public CertificateException(String message, Certificate certificate, byte status) {
      super(message);
      this.cert = certificate;
      this.reason = status;
   }

   public Certificate getCertificate() {
      return this.cert;
   }

   public byte getReason() {
      return this.reason;
   }

   static String getMessageForReason(int reason) {
      switch(reason) {
      case 1:
         return "Certificate has unrecognized critical extensions";
      case 2:
         return "Server certificate chain exceeds the length allowed by an issuer's policy";
      case 3:
         return "Certificate is expired";
      case 4:
         return "Intermediate certificate in the chain does not have the authority to be an intermediate CA";
      case 5:
         return "Certificate object does not contain a signature";
      case 6:
         return "Certificate is not yet valid";
      case 7:
         return "Certificate does not contain the correct site name";
      case 8:
         return "Certificate was issued by an unrecognized entity";
      case 9:
         return "Certificate was signed using an unsupported algorithm";
      case 10:
         return "Certificate's public key has been used in a way deemed inappropriate by the issuer";
      case 11:
         return "Certificate in a chain was not issued by the next authority in the chain";
      case 12:
         return "Root CA's public key is expired";
      case 13:
         return "Certificate has a public key that is not a supported type";
      case 14:
         return "Certificate failed verification";
      default:
         return "Unknown reason (" + reason + ")";
      }
   }
}
