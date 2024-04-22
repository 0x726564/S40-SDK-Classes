package java.security.spec;

public abstract class EncodedKeySpec implements KeySpec {
   private byte[] encodedKey;

   public EncodedKeySpec(byte[] encodedKey) {
      try {
         this.encodedKey = new byte[encodedKey.length];
         System.arraycopy(encodedKey, 0, this.encodedKey, 0, encodedKey.length);
      } catch (Exception var3) {
         throw new IllegalArgumentException("EncodedKeySpec");
      }
   }

   public byte[] getEncoded() {
      byte[] clone = new byte[this.encodedKey.length];
      System.arraycopy(this.encodedKey, 0, clone, 0, this.encodedKey.length);
      return clone;
   }

   public abstract String getFormat();
}
