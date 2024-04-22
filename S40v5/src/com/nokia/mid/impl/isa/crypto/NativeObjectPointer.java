package com.nokia.mid.impl.isa.crypto;

public class NativeObjectPointer {
   private int eU;

   public NativeObjectPointer(int var1) {
      this.eU = var1;
      if (var1 != 0) {
         this.nativeRegisterToFinalize();
      }

   }

   public int get() {
      return this.eU;
   }

   private native void nativeRegisterToFinalize();
}
