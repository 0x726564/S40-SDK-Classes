package com.nokia.mid.impl.isa.util;

public final class DigitsConverter {
   public static char[] getDigitsInMenuLanguage(char[] inputDigits) {
      return inputDigits != null && inputDigits.length != 0 ? text_translate_digits(inputDigits) : null;
   }

   private static native char[] text_translate_digits(char[] var0);
}
