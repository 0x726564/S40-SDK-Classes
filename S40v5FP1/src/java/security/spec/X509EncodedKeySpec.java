package java.security.spec;

public class X509EncodedKeySpec extends EncodedKeySpec {
   public X509EncodedKeySpec(byte[] var1) {
      super(var1);
   }

   public byte[] getEncoded() {
      return super.getEncoded();
   }

   public final String getFormat() {
      return "X.509";
   }
}
