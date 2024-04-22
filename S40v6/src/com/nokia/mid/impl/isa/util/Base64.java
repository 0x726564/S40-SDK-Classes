package com.nokia.mid.impl.isa.util;

public class Base64 {
   private static final char[] ALPHABET = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

   public static String encode(byte[] data, int offset, int length) {
      char[] ret = new char[(length + 2) / 3 * 4];
      int inIndex = 0;

      for(int outIndex = 0; outIndex < ret.length; outIndex += 4) {
         byte b0 = false;
         byte b1 = 0;
         byte b2 = 0;
         byte b0 = data[offset + inIndex];
         ret[outIndex] = ALPHABET[b0 >>> 2 & 63];
         byte b1;
         if (length - inIndex > 2) {
            b1 = data[offset + inIndex + 1];
            byte b2 = data[offset + inIndex + 2];
            ret[outIndex + 1] = ALPHABET[(b0 << 4 & 48) + (b1 >>> 4 & 15)];
            ret[outIndex + 2] = ALPHABET[(b1 << 2 & 60) + (b2 >>> 6 & 3)];
            ret[outIndex + 3] = ALPHABET[b2 & 63];
         } else if (length - inIndex > 1) {
            b1 = data[offset + inIndex + 1];
            ret[outIndex + 1] = ALPHABET[(b0 << 4 & 48) + (b1 >>> 4 & 15)];
            ret[outIndex + 2] = ALPHABET[(b1 << 2 & 60) + (b2 >>> 6 & 3)];
            ret[outIndex + 3] = '=';
         } else {
            ret[outIndex + 1] = ALPHABET[(b0 << 4 & 48) + (b1 >>> 4 & 15)];
            ret[outIndex + 2] = '=';
            ret[outIndex + 3] = '=';
         }

         inIndex += 3;
      }

      return new String(ret);
   }
}
