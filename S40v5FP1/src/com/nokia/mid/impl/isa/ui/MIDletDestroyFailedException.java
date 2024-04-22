package com.nokia.mid.impl.isa.ui;

final class MIDletDestroyFailedException extends Exception {
   private Exception exc;

   MIDletDestroyFailedException() {
   }

   MIDletDestroyFailedException(String str) {
      super(str);
   }

   MIDletDestroyFailedException(Exception x) {
      this.exc = x;
   }

   final Exception getException() {
      return this.exc;
   }
}
