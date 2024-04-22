package com.nokia.mid.impl.isa.crypto;

import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAPublicKey implements PublicKey {
   private NativeObjectPointer jm;
   private X509EncodedKeySpec jn;

   public RSAPublicKey(KeySpec var1) throws InvalidKeySpecException {
      if (!(var1 instanceof X509EncodedKeySpec)) {
         throw new InvalidKeySpecException();
      } else {
         byte[] var2 = ((X509EncodedKeySpec)var1).getEncoded();
         if (this.nativeIsSubjectPublicKeyInfo(var2) == 0) {
            throw new InvalidKeySpecException();
         } else {
            this.jn = (X509EncodedKeySpec)var1;
            this.jm = new NativeObjectPointer(this.nativeKeyConstruct(this.jn.getEncoded()));
         }
      }
   }

   public int getSize() {
      return this.nativeKeySize(this.jm.get());
   }

   public String getAlgorithm() {
      return "RSA";
   }

   public String getFormat() {
      return this.jn.getFormat();
   }

   public byte[] getEncoded() {
      return this.jn.getEncoded();
   }

   public int getNative() {
      return this.jm.get();
   }

   private native int nativeIsSubjectPublicKeyInfo(byte[] var1);

   private native int nativeKeyConstruct(byte[] var1);

   private native int nativeKeySize(int var1);
}
