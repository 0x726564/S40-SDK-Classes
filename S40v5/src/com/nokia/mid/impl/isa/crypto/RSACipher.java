package com.nokia.mid.impl.isa.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.ShortBufferException;

public class RSACipher extends CipherCommon {
   private RSAPublicKey ea;
   private byte[] buffer;
   private int eb;
   private boolean ec;

   public void init(int var1, Key var2, AlgorithmParameterSpec var3) throws InvalidKeyException, InvalidAlgorithmParameterException {
      if (var3 != null) {
         throw new InvalidAlgorithmParameterException();
      } else if (var1 == 1 && var2 instanceof RSAPublicKey) {
         this.ea = (RSAPublicKey)var2;
         var1 = this.ea.getSize() - 11;
         if (this.buffer == null || this.buffer.length != var1) {
            this.buffer = new byte[var1];
         }

         this.eb = 0;
         this.ec = false;
      } else {
         throw new InvalidKeyException();
      }
   }

   public int update(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IllegalStateException, ShortBufferException {
      return var3 == 0 ? 0 : this.a(var1, var2, var3, var4, var5, false);
   }

   public int doFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IllegalStateException, ShortBufferException {
      if (var3 == 0 && this.eb == 0) {
         if (!this.ec) {
            throw new IllegalStateException("");
         } else {
            return 0;
         }
      } else {
         int var8 = this.a(var1, var2, var3, var4, var5, true);

         try {
            this.init(1, this.ea, (AlgorithmParameterSpec)null);
         } catch (InvalidKeyException var6) {
         } catch (InvalidAlgorithmParameterException var7) {
         }

         return var8;
      }
   }

   private int a(byte[] var1, int var2, int var3, byte[] var4, int var5, boolean var6) throws ShortBufferException {
      int var7 = 0;
      if (this.ea == null) {
         throw new IllegalStateException();
      } else {
         if (var1 == var4) {
            var1 = new byte[var3];
            System.arraycopy(var4, var2, var1, 0, var3);
            var2 = 0;
         }

         do {
            int var12;
            int var11 = (var12 = this.buffer.length - this.eb) < var3 ? var12 : var3;
            System.arraycopy(var1, var2, this.buffer, this.eb, var11);
            this.eb += var11;
            var2 += var11;
            var3 -= var11;
            if (this.eb == this.buffer.length || var6) {
               if (var5 + this.buffer.length + 11 > var4.length) {
                  this.eb -= var11;
                  throw new ShortBufferException();
               }

               int var8 = this.nativeEncrypt(this.ea.getNative(), this.buffer, 0, this.eb, var4, var5);
               this.ec = true;
               var5 += var8;
               var7 += var8;
               this.eb = 0;
            }
         } while(var3 != 0);

         return var7;
      }
   }

   private native int nativeEncrypt(int var1, byte[] var2, int var3, int var4, byte[] var5, int var6);
}
