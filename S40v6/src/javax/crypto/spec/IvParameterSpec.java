package javax.crypto.spec;

import java.security.spec.AlgorithmParameterSpec;

public class IvParameterSpec implements AlgorithmParameterSpec {
   private byte[] IV;

   public IvParameterSpec(byte[] iv, int offset, int len) {
      this.IV = new byte[len];
      System.arraycopy(iv, offset, this.IV, 0, len);
   }

   public byte[] getIV() {
      byte[] returnIV = new byte[this.IV.length];
      System.arraycopy(this.IV, 0, returnIV, 0, this.IV.length);
      return returnIV;
   }
}
