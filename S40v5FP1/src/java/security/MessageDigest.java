package java.security;

import com.nokia.mid.impl.isa.crypto.MessageDigestImpl;

public abstract class MessageDigest {
   public static MessageDigest getInstance(String var0) throws NoSuchAlgorithmException {
      return MessageDigestImpl.getInstance(var0);
   }

   public void update(byte[] var1, int var2, int var3) {
   }

   public int digest(byte[] var1, int var2, int var3) throws DigestException {
      return 0;
   }

   public void reset() {
   }

   MessageDigest() {
   }
}
