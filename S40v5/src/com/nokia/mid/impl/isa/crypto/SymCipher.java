package com.nokia.mid.impl.isa.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SymCipher extends CipherCommon {
   private Algorithm hi;
   private boolean hj;
   private boolean hk;
   private boolean ec = false;
   private byte[] hl;
   private byte[] hm = null;
   private NativeObjectPointer dP;

   public SymCipher(Algorithm var1) {
      this.hi = var1;
      this.hj = var1.isMode(17);
   }

   public void init(int var1, Key var2, AlgorithmParameterSpec var3) throws InvalidKeyException, InvalidAlgorithmParameterException {
      this.hm = null;
      this.hl = null;
      this.hk = var1 == 1;
      this.ec = false;
      if (this.hj) {
         if (var3 == null) {
            if (!this.hk) {
               throw new InvalidAlgorithmParameterException();
            }

            int var4 = this.hi.isAlgorithm(9) ? 16 : 8;
            this.hl = new byte[var4];
            this.nativeGetRandomBytes(this.hl);
         } else {
            if (!(var3 instanceof IvParameterSpec)) {
               throw new InvalidAlgorithmParameterException();
            }

            IvParameterSpec var6 = (IvParameterSpec)var3;
            this.hl = var6.getIV();
         }
      } else if (var3 != null) {
         throw new InvalidAlgorithmParameterException();
      }

      boolean var7 = var2 instanceof SecretKeySpec;
      boolean var5 = this.hi.getString().equals(var2.getAlgorithm());
      if (var7 && var5) {
         this.hm = var2.getEncoded();
         this.dP = new NativeObjectPointer(this.nativeConstruct(this.hi.algorithm(), this.hi.blockMode(), this.hi.padding(), this.hk, this.hm, this.hl));
      } else {
         throw new InvalidKeyException("Sym");
      }
   }

   public int update(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IllegalStateException, ShortBufferException {
      return var3 == 0 ? 0 : this.a(var1, var2, var3, var4, var5, false);
   }

   public int doFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IllegalStateException, ShortBufferException {
      if (var3 == 0 && !this.ec) {
         throw new IllegalStateException();
      } else {
         return this.a(var1, var2, var3, var4, var5, true);
      }
   }

   public byte[] getIV() {
      if (this.hl != null && this.hl.length > 0) {
         byte[] var1 = new byte[this.hl.length];
         System.arraycopy(this.hl, 0, var1, 0, this.hl.length);
         return var1;
      } else {
         return null;
      }
   }

   private int a(byte[] var1, int var2, int var3, byte[] var4, int var5, boolean var6) throws ShortBufferException {
      boolean var7 = false;
      var7 = false;
      if (this.hm == null) {
         throw new IllegalStateException();
      } else {
         if (var1 == var4) {
            var1 = new byte[var3];
            System.arraycopy(var4, var2, var1, 0, var3);
            var2 = 0;
            var7 = true;
         }

         this.ec = true;
         if (var3 > 0) {
            var2 = this.nativeUpdate(this.dP.get(), var1, var2, var3, var4, var5);
         } else {
            var2 = 0;
         }

         int var8;
         if (var7) {
            for(var8 = 0; var8 < var3; ++var8) {
               var1[var8] = 0;
            }
         }

         int var9 = 0 + var2;
         if (var6) {
            var8 = var5 + var2;
            var2 = this.nativeFinal(this.dP.get(), var4, var8);
            var9 += var2;
         }

         return var9;
      }
   }

   private native int nativeUpdate(int var1, byte[] var2, int var3, int var4, byte[] var5, int var6);

   private native int nativeFinal(int var1, byte[] var2, int var3);

   private native int nativeConstruct(int var1, int var2, int var3, boolean var4, byte[] var5, byte[] var6);

   private native void nativeGetRandomBytes(byte[] var1);
}
