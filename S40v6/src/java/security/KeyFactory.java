package java.security;

import com.nokia.mid.impl.isa.crypto.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class KeyFactory {
   private KeyFactory() {
   }

   public static KeyFactory getInstance(String algorithm) throws NoSuchAlgorithmException {
      if (algorithm.toUpperCase().equals("RSA")) {
         return new KeyFactory();
      } else {
         throw new NoSuchAlgorithmException();
      }
   }

   public final PublicKey generatePublic(KeySpec keySpec) throws InvalidKeySpecException {
      return new RSAPublicKey(keySpec);
   }
}
