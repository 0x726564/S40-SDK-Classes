package java.security.spec;

public abstract class EncodedKeySpec implements KeySpec {
   private byte[] encodedKey;

   public EncodedKeySpec(byte[] var1) {
      try {
         this.encodedKey = new byte[var1.length];
         System.arraycopy(var1, 0, this.encodedKey, 0, var1.length);
      } catch (Exception var2) {
         throw new IllegalArgumentException("EncodedKeySpec");
      }
   }

   public byte[] getEncoded() {
      byte[] var1 = new byte[this.encodedKey.length];
      System.arraycopy(this.encodedKey, 0, var1, 0, this.encodedKey.length);
      return var1;
   }

   public abstract String getFormat();
}
