package com.nokia.mid.impl.isa.ui;

final class MIDletDestroyFailedException extends Exception {
   private Exception ay;

   MIDletDestroyFailedException() {
   }

   MIDletDestroyFailedException(Exception var1) {
      this.ay = var1;
   }

   final Exception getException() {
      return this.ay;
   }
}
