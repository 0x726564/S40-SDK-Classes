package com.nokia.mid.impl.isa.ui;

final class MIDletDestroyFailedException extends Exception {
   private Exception exc;

   MIDletDestroyFailedException() {
   }

   MIDletDestroyFailedException(String var1) {
      super(var1);
   }

   MIDletDestroyFailedException(Exception var1) {
      this.exc = var1;
   }

   final Exception getException() {
      return this.exc;
   }
}
