package java.security;

import com.nokia.mid.impl.isa.crypto.Algorithm;
import com.nokia.mid.impl.isa.crypto.NativeObjectPointer;
import com.nokia.mid.impl.isa.crypto.RSAPublicKey;

public abstract class Signature {
   Algorithm e;
   NativeObjectPointer f;
   private RSAPublicKey g = null;

   public static Signature getInstance(String var0) throws NoSuchAlgorithmException {
      return new SignatureReal(var0);
   }

   public final void initVerify(PublicKey var1) throws InvalidKeyException {
      if (!(var1 instanceof RSAPublicKey)) {
         throw new InvalidKeyException();
      } else {
         this.g = (RSAPublicKey)var1;
         this.nativeInit(this.f.get(), this.g.getNative());
      }
   }

   public final boolean verify(byte[] var1) throws SignatureException {
      if (this.g == null) {
         throw new SignatureException();
      } else {
         return this.nativeVerify(this.f.get(), var1) > 0;
      }
   }

   public final void update(byte[] var1, int var2, int var3) throws SignatureException {
      boolean var4 = var2 < 0;
      boolean var5 = var3 < 0;
      boolean var6 = var2 + var3 > var1.length;
      if (!var4 && !var6 && !var5) {
         if (this.g == null) {
            throw new SignatureException();
         } else {
            if (var3 > 0) {
               this.nativeUpdate(this.f.get(), var1, var2, var3);
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
