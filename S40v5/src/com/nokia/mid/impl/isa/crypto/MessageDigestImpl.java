package com.nokia.mid.impl.isa.crypto;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.MessageDigestProxy;
import java.security.NoSuchAlgorithmException;
import javax.crypto.ShortBufferException;

public class MessageDigestImpl extends MessageDigestProxy {
   private NativeObjectPointer dP;

   private MessageDigestImpl(Algorithm var1) {
      this.dP = new NativeObjectPointer(this.nativeGetDigest(var1.algorithm()));
   }

   public static MessageDigest getInstance(String var0) throws NoSuchAlgorithmException {
      Algorithm var1 = new Algorithm(var0);
      return new MessageDigestImpl(var1);
   }

   public void update(byte[] var1, int var2, int var3) {
      if (var3 > 0) {
         this.nativeUpdate(this.dP.get(), var1, var2, var3);
      }

   }

   public int digest(byte[] var1, int var2, int var3) throws DigestException {
      try {
         return this.nativeDigest(this.dP.get(), var1, var2, var3);
      } catch (ShortBufferException var4) {
         throw new DigestException();
      }
   }

   public void reset() {
      this.nativeReset(this.dP.get());
   }

   private native int nativeGetDigest(int var1);

   private native void nativeUpdate(int var1, byte[] var2, int var3, int var4);

   private native int nativeDigest(int var1, byte[] var2, int var3, int var4) throws ShortBufferException;

   private native void nativeReset(int var1);
}
