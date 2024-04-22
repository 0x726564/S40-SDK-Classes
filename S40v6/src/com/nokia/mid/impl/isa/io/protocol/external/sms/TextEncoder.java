package com.nokia.mid.impl.isa.io.protocol.external.sms;

public class TextEncoder {
   private static final byte[] _chars7Bits = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127};
   private static final char[] _charsUCS2 = new char[]{'@', '£', '$', '¥', 'è', 'é', 'ù', 'ì', 'ò', 'Ç', '\n', 'Ø', 'ø', '\r', 'Å', 'å', 'Δ', '_', 'Φ', 'Γ', 'Λ', 'Ω', 'Π', 'Ψ', 'Σ', 'Θ', 'Ξ', 'Æ', 'æ', 'ß', 'É', ' ', '!', '"', '#', '¤', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '¡', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Ä', 'Ö', 'Ñ', 'Ü', '§', '¿', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'ä', 'ö', 'ñ', 'ü', 'à'};
   private static final byte[] _escaped7BitChars = new byte[]{20, 40, 41, 47, 60, 61, 62, 64, 101};
   private static final char[] _escapedUCS2 = new char[]{'^', '{', '}', '\\', '[', '~', ']', '|', '€'};
   private static final byte ESC = 27;
   private static final int SIGN_MASK = 255;

   TextEncoder() {
   }

   public static byte[] encode(byte[] ucsbytes) {
      byte[] enc7Bit = null;
      if (ucsbytes != null) {
         int ucsLength = ucsbytes.length;
         byte[] tmpEnc7Bit = new byte[ucsLength];
         int k = 0;

         for(int i = 0; i < ucsLength; i += 2) {
            char curChar = (char)(ucsbytes[i] << 8 | ucsbytes[i + 1] & 255);

            int j;
            for(j = 0; j < _charsUCS2.length; ++j) {
               if (curChar == _charsUCS2[j]) {
                  tmpEnc7Bit[k++] = _chars7Bits[j];
                  break;
               }
            }

            if (j >= _charsUCS2.length) {
               for(j = 0; j < _escapedUCS2.length; ++j) {
                  if (curChar == _escapedUCS2[j]) {
                     tmpEnc7Bit[k++] = 27;
                     tmpEnc7Bit[k++] = _escaped7BitChars[j];
                     break;
                  }
               }

               if (j >= _escapedUCS2.length) {
                  return null;
               }
            }
         }

         enc7Bit = new byte[k];
         System.arraycopy(tmpEnc7Bit, 0, enc7Bit, 0, k);
      }

      return enc7Bit;
   }
}
