package java.security;

import com.nokia.mid.impl.isa.crypto.Algorithm;
import com.nokia.mid.impl.isa.crypto.NativeObjectPointer;

class SignatureReal extends Signature {
   SignatureReal(String var1) throws NoSuchAlgorithmException {
      this.e = new Algorithm(var1);
      this.f = new NativeObjectPointer(this.nativeGetSignature(this.e.algorithm()));
   }

   private native int nativeGetSignature(int var1);
}
