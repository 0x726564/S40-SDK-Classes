package java.security;

import com.nokia.mid.impl.isa.crypto.Algorithm;
import com.nokia.mid.impl.isa.crypto.NativeObjectPointer;

class SignatureReal extends Signature {
   SignatureReal(String algorithmString) throws NoSuchAlgorithmException {
      this.localAlgorithm = new Algorithm(algorithmString);
      this.nativeObject = new NativeObjectPointer(this.nativeGetSignature(this.localAlgorithm.algorithm()));
   }

   private native int nativeGetSignature(int var1);
}
