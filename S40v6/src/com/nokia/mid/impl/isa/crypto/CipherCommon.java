package com.nokia.mid.impl.isa.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

public abstract class CipherCommon {
   public abstract void init(int var1, Key var2, AlgorithmParameterSpec var3) throws InvalidKeyException, InvalidAlgorithmParameterException;

   public abstract int update(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IllegalStateException, ShortBufferException;

   public abstract int doFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IllegalStateException, ShortBufferException, IllegalBlockSizeException, BadPaddingException;

   public byte[] getIV() {
      return null;
   }
}
