package javax.crypto;

import com.nokia.mid.impl.isa.crypto.Algorithm;
import com.nokia.mid.impl.isa.crypto.CipherCommon;
import com.nokia.mid.impl.isa.crypto.RSACipher;
import com.nokia.mid.impl.isa.crypto.SymCipher;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

public class Cipher {
   public static final int ENCRYPT_MODE = 1;
   public static final int DECRYPT_MODE = 2;
   private CipherCommon myCipher;
   private byte[] dummyInput = new byte[0];
   private byte[] dummyOutput = new byte[0];
   private boolean initCalled = false;

   private Cipher() {
   }

   private Cipher(CipherCommon cipher) {
      this.myCipher = cipher;
   }

   public static final Cipher getInstance(String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException {
      Algorithm alg = new Algorithm(transformation, true);
      boolean RSAblockOK = alg.isMode(19) || alg.isMode(20);
      boolean RSApaddingOK = alg.isPadding(35) || alg.isPadding(34);
      if (alg.isAlgorithm(4) && RSAblockOK && RSApaddingOK) {
         return new Cipher(new RSACipher());
      } else {
         boolean symmetric = alg.isAlgorithm(7) || alg.isAlgorithm(8) || alg.isAlgorithm(9);
         boolean blockOk = alg.isMode(18) || alg.isMode(17) || alg.isMode(20);
         boolean pkcs5 = alg.isPadding(33) || alg.isPadding(35);
         boolean paddingOk = alg.isPadding(32) || pkcs5;
         if (symmetric && blockOk && paddingOk) {
            return new Cipher(new SymCipher(alg));
         } else {
            throw new NoSuchAlgorithmException(transformation);
         }
      }
   }

   public final void init(int opmode, Key key) throws InvalidKeyException {
      try {
         this.init(opmode, key, (AlgorithmParameterSpec)null);
      } catch (InvalidAlgorithmParameterException var4) {
         throw new InvalidKeyException();
      }
   }

   public final void init(int opmode, Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
      if (opmode != 2 && opmode != 1) {
         throw new IllegalArgumentException("");
      } else {
         this.myCipher.init(opmode, key, params);
         this.initCalled = true;
      }
   }

   public final int update(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalStateException, ShortBufferException {
      if (!this.initCalled) {
         throw new IllegalStateException();
      } else {
         if (output == null) {
            output = this.dummyOutput;
         }

         this.checkBounds(input, inputOffset, inputLen, output, outputOffset);
         return this.myCipher.update(input, inputOffset, inputLen, output, outputOffset);
      }
   }

   public final int doFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalStateException, ShortBufferException, IllegalBlockSizeException, BadPaddingException {
      if (!this.initCalled) {
         throw new IllegalStateException();
      } else {
         if (input == null) {
            input = this.dummyInput;
         }

         this.checkBounds(input, inputOffset, inputLen, output, outputOffset);
         return this.myCipher.doFinal(input, inputOffset, inputLen, output, outputOffset);
      }
   }

   public final byte[] getIV() {
      return !this.initCalled ? null : this.myCipher.getIV();
   }

   private void checkBounds(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) {
      if (input == null || inputOffset < 0 || inputLen < 0 || inputOffset + inputLen > input.length || output == null || outputOffset < 0) {
         throw new IllegalArgumentException();
      }
   }
}
