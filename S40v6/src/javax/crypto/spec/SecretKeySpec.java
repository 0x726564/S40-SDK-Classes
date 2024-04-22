package javax.crypto.spec;

import java.security.Key;
import java.security.spec.KeySpec;

public class SecretKeySpec implements KeySpec, Key {
   private byte[] keyData;
   private String algorithm;

   public SecretKeySpec(byte[] key, int offset, int len, String algorithm) {
      this.keyData = new byte[len];
      System.arraycopy(key, offset, this.keyData, 0, len);
      this.algorithm = algorithm.toUpperCase();
   }

   public String getAlgorithm() {
      return this.algorithm;
   }

   public String getFormat() {
      return "RAW";
   }

   public byte[] getEncoded() {
      byte[] encoded = new byte[this.keyData.length];
      System.arraycopy(this.keyData, 0, encoded, 0, this.keyData.length);
      return encoded;
   }
}
