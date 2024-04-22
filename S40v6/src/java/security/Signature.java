package java.security;

import com.nokia.mid.impl.isa.crypto.Algorithm;
import com.nokia.mid.impl.isa.crypto.NativeObjectPointer;
import com.nokia.mid.impl.isa.crypto.RSAPublicKey;

public abstract class Signature {
   Algorithm localAlgorithm;
   NativeObjectPointer nativeObject;
   RSAPublicKey publicKey = null;

   public static Signature getInstance(String algorithm) throws NoSuchAlgorithmException {
      return new SignatureReal(algorithm);
   }

   public final void initVerify(PublicKey publicKey) throws InvalidKeyException {
      if (!(publicKey instanceof RSAPublicKey)) {
         throw new InvalidKeyException();
      } else {
         this.publicKey = (RSAPublicKey)publicKey;
         this.nativeInit(this.nativeObject.get(), this.publicKey.getNative());
      }
   }

   public final boolean verify(byte[] signature) throws SignatureException {
      if (this.publicKey == null) {
         throw new SignatureException();
      } else {
         return this.nativeVerify(this.nativeObject.get(), signature) > 0;
      }
   }

   public final void update(byte[] data, int off, int len) throws SignatureException {
      boolean illegal_offset = off < 0;
      boolean illegal_len = len < 0;
      boolean illegal_len_plus_off = off + len > data.length;
      if (!illegal_offset && !illegal_len_plus_off && !illegal_len) {
         if (this.publicKey == null) {
            throw new SignatureException();
         } else {
            if (len > 0) {
               this.nativeUpdate(this.nativeObject.get(), data, off, len);
            }

         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   Signature() {
   }

   private native void nativeInit(int var1, int var2);

   private native void nativeUpdate(int var1, byte[] var2, int var3, int var4);

   private native int nativeVerify(int var1, byte[] var2);
}
