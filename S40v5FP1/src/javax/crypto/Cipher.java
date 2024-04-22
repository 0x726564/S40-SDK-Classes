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
   private CipherCommon a;
   private byte[] b = new byte[0];
   private byte[] c = new byte[0];
   private boolean d = false;

   private Cipher() {
   }

   private Cipher(CipherCommon var1) {
      this.a = var1;
   }

   public static final Cipher getInstance(String var0) throws NoSuchAlgorithmException, NoSuchPaddingException {
      Algorithm var1;
      boolean var2 = (var1 = new Algorithm(var0, true)).isMode(19) || var1.isMode(20);
      boolean var3 = var1.isPadding(35) || var1.isPadding(34);
      if (var1.isAlgorithm(4) && var2 && var3) {
         return new Cipher(new RSACipher());
      } else {
         var2 = var1.isAlgorithm(7) || var1.isAlgorithm(8) || var1.isAlgorithm(9);
         var3 = var1.isMode(18) || var1.isMode(17) || var1.isMode(20);
         boolean var4 = var1.isPadding(33) || var1.isPadding(35);
         var4 = var1.isPadding(32) || var4;
         if (var2 && var3 && var4) {
            return new Cipher(new SymCipher(var1));
         } else {
            throw new NoSuchAlgorithmException(var0);
         }
      }
   }

   public final void init(int var1, Key var2) throws InvalidKeyException {
      try {
         this.init(var1, var2, (AlgorithmParameterSpec)null);
      } catch (InvalidAlgorithmParameterException var3) {
         throw new InvalidKeyException();
      }
   }

   public final void init(int var1, Key var2, AlgorithmParameterSpec var3) throws InvalidKeyException, InvalidAlgorithmParameterException {
      if (var1 != 2 && var1 != 1) {
         throw new IllegalArgumentException("");
      } else {
         this.a.init(var1, var2, var3);
         this.d = true;
      }
   }

   public final int update(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IllegalStateException, ShortBufferException {
      if (!this.d) {
         throw new IllegalStateException();
      } else {
         if (var4 == null) {
            var4 = this.c;
         }

         a(var1, var2, var3, var4, var5);
         return this.a.update(var1, var2, var3, var4, var5);
      }
   }

   public final int doFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IllegalStateException, ShortBufferException, IllegalBlockSizeException, BadPaddingException {
      if (!this.d) {
         throw new IllegalStateException();
      } else {
         if (var1 == null) {
            var1 = this.b;
         }

         a(var1, var2, var3, var4, var5);
         return this.a.doFinal(var1, var2, var3, var4, var5);
      }
   }

   public final byte[] getIV() {
      return !this.d ? null : this.a.getIV();
   }

   private static void a(byte[] var0, int var1, int var2, byte[] var3, int var4) {
      if (var0 == null || var1 < 0 || var2 < 0 || var1 + var2 > var0.length || var3 == null || var4 < 0) {
         throw new IllegalArgumentException();
      }
   }
}
