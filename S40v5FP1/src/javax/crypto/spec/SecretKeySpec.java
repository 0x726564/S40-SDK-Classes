package javax.crypto.spec;

import java.security.Key;
import java.security.spec.KeySpec;

public class SecretKeySpec implements Key, KeySpec {
   private byte[] keyData;
   private String algorithm;

   public SecretKeySpec(byte[] var1, int var2, int var3, String var4) {
      this.keyData = new byte[var3];
      System.arraycopy(var1, var2, this.keyData, 0, var3);
      this.algorithm = var4.toUpperCase();
   }

   public String getAlgorithm() {
      return this.algorithm;
   }

   public String getFormat() {
      return "RAW";
   }

   public byte[] getEncoded() {
      byte[] var1 = new byte[this.keyData.length];
      System.arraycopy(this.keyData, 0, var1, 0, this.keyData.length);
      return var1;
   }
}
