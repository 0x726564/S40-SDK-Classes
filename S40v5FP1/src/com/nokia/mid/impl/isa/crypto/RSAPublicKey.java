package com.nokia.mid.impl.isa.crypto;

import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAPublicKey implements PublicKey {
   private NativeObjectPointer nativeKey;
   private X509EncodedKeySpec keySpec;

   public RSAPublicKey(KeySpec keySpec) throws InvalidKeySpecException {
      if (!(keySpec instanceof X509EncodedKeySpec)) {
         throw new InvalidKeySpecException();
      } else {
         byte[] encoded = ((X509EncodedKeySpec)keySpec).getEncoded();
         if (this.nativeIsSubjectPublicKeyInfo(encoded) == 0) {
            throw new InvalidKeySpecException();
         } else {
            this.keySpec = (X509EncodedKeySpec)keySpec;
            this.nativeKey = new NativeObjectPointer(this.nativeKeyConstruct(this.keySpec.getEncoded()));
         }
      }
   }

   public int getSize() {
      return this.nativeKeySize(this.nativeKey.get());
   }

   public String getAlgorithm() {
      return "RSA";
   }

   public String getFormat() {
      return this.keySpec.getFormat();
   }

   public byte[] getEncoded() {
      return this.keySpec.getEncoded();
   }

   public int getNative() {
      return this.nativeKey.get();
   }

   private native int nativeIsSubjectPublicKeyInfo(byte[] var1);

   private native int nativeKeyConstruct(byte[] var1);

   private native int nativeKeySize(int var1);
}
