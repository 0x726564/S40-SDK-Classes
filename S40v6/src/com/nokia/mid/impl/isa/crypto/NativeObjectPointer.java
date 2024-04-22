package com.nokia.mid.impl.isa.crypto;

public class NativeObjectPointer {
   int pointer;

   public NativeObjectPointer(int p) {
      this.pointer = p;
      if (p != 0) {
         this.nativeRegisterToFinalize();
      }

   }

   public int get() {
      return this.pointer;
   }

   private native void nativeRegisterToFinalize();
}
