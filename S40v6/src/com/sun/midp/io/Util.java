package com.sun.midp.io;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

public abstract class Util {
   public static byte[] toCString(String string) {
      int length = string.length();
      byte[] cString = new byte[length + 1];

      for(int i = 0; i < length; ++i) {
         cString[i] = (byte)string.charAt(i);
      }

      return cString;
   }

   public static String toJavaString(byte[] cString) {
      int i;
      for(i = 0; cString[i] != 0; ++i) {
      }

      try {
         return new String(cString, 0, i, "ISO8859_1");
      } catch (UnsupportedEncodingException var3) {
         return null;
      }
   }

   public static Vector getCommaSeparatedValues(String input) {
      Vector output = new Vector(5, 5);
      int len = input.length();
      if (len == 0) {
         return output;
      } else {
         int start = 0;

         while(true) {
            int end = input.indexOf(44, start);
            if (end == -1) {
               output.addElement(input.substring(start, len).trim());
               return output;
            }

            output.addElement(input.substring(start, end).trim());
            start = end + 1;
         }
      }
   }
}
