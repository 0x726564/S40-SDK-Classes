package com.nokia.mid.impl.isa.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.ShortBufferException;

public class RSACipher extends CipherCommon {
   private RSAPublicKey key;
   private byte[] buffer;
   private int bytesInBuffer;
   private boolean cipherUsed;

   public void init(int opmode, Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
      if (params != null) {
         throw new InvalidAlgorithmParameterException();
      } else if (opmode == 1 && key instanceof RSAPublicKey) {
         this.key = (RSAPublicKey)key;
         int bufferSize = this.key.getSize() - 11;
         if (this.buffer == null || this.buffer.length != bufferSize) {
            this.buffer = new byte[bufferSize];
         }

         this.bytesInBuffer = 0;
         this.cipherUsed = false;
      } else {
         throw new InvalidKeyException();
      }
   }

   public int update(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalStateException, ShortBufferException {
      return inputLen == 0 ? 0 : this.doUpdate(input, inputOffset, inputLen, output, outputOffset, false);
   }

   public int doFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalStateException, ShortBufferException {
      if (inputLen == 0 && this.bytesInBuffer == 0) {
         if (!this.cipherUsed) {
            throw new IllegalStateException("");
         } else {
            return 0;
         }
      } else {
         int bytesInOutput = this.doUpdate(input, inputOffset, inputLen, output, outputOffset, true);

         try {
            this.init(1, this.key, (AlgorithmParameterSpec)null);
         } catch (InvalidKeyException var8) {
         } catch (InvalidAlgorithmParameterException var9) {
         }

         return bytesInOutput;
      }
   }

   private int copyToBuffer(byte[] input, int inputOffset, int inputLen) {
      int missingBytesInBuffer = this.buffer.length - this.bytesInBuffer;
      int newBytesInBuffer = missingBytesInBuffer < inputLen ? missingBytesInBuffer : inputLen;
      System.arraycopy(input, inputOffset, this.buffer, this.bytesInBuffer, newBytesInBuffer);
      this.bytesInBuffer += newBytesInBuffer;
      return newBytesInBuffer;
   }

   private int doUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset, boolean isFinal) throws ShortBufferException {
      int totalOutputBytes = 0;
      if (this.key == null) {
         throw new IllegalStateException();
      } else {
         if (input == output) {
            input = new byte[inputLen];
            System.arraycopy(output, inputOffset, input, 0, inputLen);
            inputOffset = 0;
         }

         do {
            int bytesCopied = this.copyToBuffer(input, inputOffset, inputLen);
            inputOffset += bytesCopied;
            inputLen -= bytesCopied;
            if (this.bytesInBuffer == this.buffer.length || isFinal) {
               if (outputOffset + this.buffer.length + 11 > output.length) {
                  this.bytesInBuffer -= bytesCopied;
                  throw new ShortBufferException();
               }

               int outputBytes = this.nativeEncrypt(this.key.getNative(), this.buffer, 0, this.bytesInBuffer, output, outputOffset);
               this.cipherUsed = true;
               outputOffset += outputBytes;
               totalOutputBytes += outputBytes;
               this.bytesInBuffer = 0;
            }
         } while(inputLen != 0);

         return totalOutputBytes;
      }
   }

   private native int nativeEncrypt(int var1, byte[] var2, int var3, int var4, byte[] var5, int var6);
}
