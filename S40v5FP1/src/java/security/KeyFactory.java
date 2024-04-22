package java.security;

import com.nokia.mid.impl.isa.crypto.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class KeyFactory {
   private KeyFactory() {
   }

   public static KeyFactory getInstance(String var0) throws NoSuchAlgorithmException {
      if (var0.toUpperCase().equals("RSA")) {
         return new KeyFactory();
      } else {
         throw new NoSuchAlgorithmException();
      }
   }

   public final PublicKey generatePublic(KeySpec var1) throws InvalidKeySpecException {
      return new RSAPublicKey(var1);
   }
}
