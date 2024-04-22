package java.security;

import com.nokia.mid.impl.isa.crypto.MessageDigestImpl;

public abstract class MessageDigest {
   public static MessageDigest getInstance(String algorithm) throws NoSuchAlgorithmException {
      return MessageDigestImpl.getInstance(algorithm);
   }

   public void update(byte[] input, int offset, int len) {
   }

   public int digest(byte[] buf, int offset, int len) throws DigestException {
      return 0;
   }

   public void reset() {
   }

   MessageDigest() {
   }
}
