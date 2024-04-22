package com.nokia.mid.impl.isa.crypto;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.MessageDigestProxy;
import java.security.NoSuchAlgorithmException;
import javax.crypto.ShortBufferException;

public class MessageDigestImpl extends MessageDigestProxy {
   private Algorithm algorithm;
   private NativeObjectPointer nativeObject;

   MessageDigestImpl(Algorithm algorithm) {
      this.algorithm = algorithm;
      this.nativeObject = new NativeObjectPointer(this.nativeGetDigest(algorithm.algorithm()));
   }

   public static MessageDigest getInstance(String algorithmString) throws NoSuchAlgorithmException {
      Algorithm localAlgorithm = new Algorithm(algorithmString);
      return new MessageDigestImpl(localAlgorithm);
   }

   public void update(byte[] input, int offset, int len) {
      if (len > 0) {
         this.nativeUpdate(this.nativeObject.get(), input, offset, len);
      }

   }

   public int digest(byte[] buf, int offset, int len) throws DigestException {
      try {
         return this.nativeDigest(this.nativeObject.get(), buf, offset, len);
      } catch (ShortBufferException var5) {
         throw new DigestException();
      }
   }

   public void reset() {
      this.nativeReset(this.nativeObject.get());
   }

   private native int nativeGetDigest(int var1);

   private native void nativeUpdate(int var1, byte[] var2, int var3, int var4);

   private native int nativeDigest(int var1, byte[] var2, int var3, int var4) throws ShortBufferException;

   private native void nativeReset(int var1);

   private native void nativeDestruct(int var1);
}
