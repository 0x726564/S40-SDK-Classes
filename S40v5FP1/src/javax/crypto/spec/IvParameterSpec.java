package javax.crypto.spec;

import java.security.spec.AlgorithmParameterSpec;

public class IvParameterSpec implements AlgorithmParameterSpec {
   private byte[] IV;

   public IvParameterSpec(byte[] var1, int var2, int var3) {
      this.IV = new byte[var3];
      System.arraycopy(var1, var2, this.IV, 0, var3);
   }

   public byte[] getIV() {
      byte[] var1 = new byte[this.IV.length];
      System.arraycopy(this.IV, 0, var1, 0, this.IV.length);
      return var1;
   }
}
