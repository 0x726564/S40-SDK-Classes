package com.nokia.mid.impl.isa.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SymCipher extends CipherCommon {
   Algorithm algorithm;
   boolean ivNeeded;
   boolean forEncryption;
   boolean cipherUsed = false;
   byte[] IV;
   byte[] keyData = null;
   private NativeObjectPointer nativeObject;

   public SymCipher(Algorithm alg) {
      this.algorithm = alg;
      this.ivNeeded = alg.isMode(17);
   }

   private void getRandomBytes() {
      int noOfIvBytes = this.algorithm.isAlgorithm(9) ? 16 : 8;
      this.IV = new byte[noOfIvBytes];
      this.nativeGetRandomBytes(this.IV);
   }

   private void checkParams(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
      if (this.ivNeeded) {
         if (params == null) {
            if (!this.forEncryption) {
               throw new InvalidAlgorithmParameterException();
            }

            this.getRandomBytes();
         } else {
            if (!(params instanceof IvParameterSpec)) {
               throw new InvalidAlgorithmParameterException();
            }

            IvParameterSpec ivSpec = (IvParameterSpec)params;
            this.IV = ivSpec.getIV();
         }
      } else if (params != null) {
         throw new InvalidAlgorithmParameterException();
      }

   }

   private void checkKey(Key key) throws InvalidKeyException {
      boolean secretKey = key instanceof SecretKeySpec;
      boolean algorithmMatch = this.algorithm.getString().equals(key.getAlgorithm());
      if (secretKey && algorithmMatch) {
         this.keyData = key.getEncoded();
      } else {
         throw new InvalidKeyException("Sym");
      }
   }

   public void init(int opmode, Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
      this.keyData = null;
      this.IV = null;
      this.forEncryption = opmode == 1;
      this.cipherUsed = false;
      this.checkParams(params);
      this.checkKey(key);
      this.nativeObject = new NativeObjectPointer(this.nativeConstruct(this.algorithm.algorithm(), this.algorithm.blockMode(), this.algorithm.padding(), this.forEncryption, this.keyData, this.IV));
   }

   public int update(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalStateException, ShortBufferException {
      return inputLen == 0 ? 0 : this.doUpdate(input, inputOffset, inputLen, output, outputOffset, false);
   }

   public int doFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalStateException, ShortBufferException {
      if (inputLen == 0 && !this.cipherUsed) {
         throw new IllegalStateException();
      } else {
         return this.doUpdate(input, inputOffset, inputLen, output, outputOffset, true);
      }
   }

   public byte[] getIV() {
      if (this.IV != null && this.IV.length > 0) {
         byte[] returnedIV = new byte[this.IV.length];
         System.arraycopy(this.IV, 0, returnedIV, 0, this.IV.length);
         return returnedIV;
      } else {
         return null;
      }
   }

   private int doUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset, boolean isFinal) throws ShortBufferException {
      int totalOutputBytes = 0;
      boolean doClean = false;
      if (this.keyData == null) {
         throw new IllegalStateException();
      } else {
         if (input == output) {
            input = new byte[inputLen];
            System.arraycopy(output, inputOffset, input, 0, inputLen);
            inputOffset = 0;
            doClean = true;
         }

         this.cipherUsed = true;
         int outputBytes;
         if (inputLen > 0) {
            outputBytes = this.nativeUpdate(this.nativeObject.get(), input, inputOffset, inputLen, output, outputOffset);
         } else {
            outputBytes = 0;
         }

         int finalOffset;
         if (doClean) {
            for(finalOffset = 0; finalOffset < inputLen; ++finalOffset) {
               input[finalOffset] = 0;
            }
         }

         int totalOutputBytes = totalOutputBytes + outputBytes;
         if (isFinal) {
            finalOffset = outputOffset + outputBytes;
            outputBytes = this.nativeFinal(this.nativeObject.get(), output, finalOffset);
            totalOutputBytes += outputBytes;
         }

         return totalOutputBytes;
      }
   }

   private native int nativeUpdate(int var1, byte[] var2, int var3, int var4, byte[] var5, int var6);

   private native int nativeFinal(int var1, byte[] var2, int var3);

   private native int nativeConstruct(int var1, int var2, int var3, boolean var4, byte[] var5, byte[] var6);

   private native void nativeGetRandomBytes(byte[] var1);
}
