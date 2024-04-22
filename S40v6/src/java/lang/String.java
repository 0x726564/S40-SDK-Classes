package java.lang;

import com.nokia.mid.impl.isa.i18n.CharsetConv;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public final class String {
   private char[] value;
   private int offset;
   private int count;

   public String() {
      this.value = new char[0];
   }

   public String(String value) {
      this.count = value.length();
      this.value = new char[this.count];
      value.getChars(0, this.count, this.value, 0);
   }

   public String(char[] value) {
      this.count = value.length;
      this.value = new char[this.count];
      System.arraycopy(value, 0, this.value, 0, this.count);
   }

   public String(char[] value, int offset, int count) {
      if (offset < 0) {
         throw new StringIndexOutOfBoundsException(offset);
      } else if (count < 0) {
         throw new StringIndexOutOfBoundsException(count);
      } else if (offset > value.length - count) {
         throw new StringIndexOutOfBoundsException(offset + count);
      } else {
         this.value = new char[count];
         this.count = count;
         System.arraycopy(value, offset, this.value, 0, count);
      }
   }

   private static char[] byteToCharArray(byte[] buffer, int offset, int length, String enc) throws UnsupportedEncodingException {
      char[] ca = null;
      if (enc == null) {
         throw new NullPointerException();
      } else if (offset >= 0 && length >= 0 && offset <= buffer.length - length) {
         InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(buffer, offset, length), enc);
         char[] outbuf = new char[length];
         int nbChars = 0;

         try {
            while(true) {
               int ret = isr.read(outbuf, nbChars, length - nbChars);
               if (ret <= 0) {
                  isr.close();
                  break;
               }

               nbChars += ret;
            }
         } catch (IOException var9) {
            nbChars = 0;
         }

         char[] ca;
         if (nbChars > 0) {
            ca = new char[nbChars];
            System.arraycopy(outbuf, 0, ca, 0, nbChars);
         } else {
            ca = new char[0];
         }

         return ca;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private static char[] byteToCharArray(byte[] buffer, int offset, int length) {
      try {
         return byteToCharArray(buffer, offset, length, CharsetConv.defaultEncoding);
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException("Missing default encoding " + CharsetConv.defaultEncoding);
      }
   }

   public String(byte[] bytes, int off, int len, String enc) throws UnsupportedEncodingException {
      this(byteToCharArray(bytes, off, len, enc));
   }

   public String(byte[] bytes, String enc) throws UnsupportedEncodingException {
      this(bytes, 0, bytes.length, enc);
   }

   public String(byte[] bytes, int off, int len) {
      this(byteToCharArray(bytes, off, len));
   }

   public String(byte[] bytes) {
      this((byte[])bytes, 0, bytes.length);
   }

   public String(StringBuffer buffer) {
      synchronized(buffer) {
         buffer.setShared();
         this.value = buffer.getValue();
         this.offset = 0;
         this.count = buffer.length();
      }
   }

   String(int offset, int count, char[] value) {
      this.value = value;
      this.offset = offset;
      this.count = count;
   }

   public int length() {
      return this.count;
   }

   public native char charAt(int var1);

   public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      if (srcBegin < 0) {
         throw new StringIndexOutOfBoundsException(srcBegin);
      } else if (srcEnd > this.count) {
         throw new StringIndexOutOfBoundsException(srcEnd);
      } else if (srcBegin > srcEnd) {
         throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
      } else {
         System.arraycopy(this.value, this.offset + srcBegin, dst, dstBegin, srcEnd - srcBegin);
      }
   }

   public byte[] getBytes(String enc) throws UnsupportedEncodingException {
      byte[] ba = null;
      if (enc != null) {
         if (this.offset >= 0 && this.count >= 0 && this.offset <= this.value.length - this.count) {
            byte[] encoding = CharsetConv.isSupportedEncoding(enc);
            if (encoding != null) {
               int maxByteLen = CharsetConv.getMaxByteLength(encoding);
               byte[] outbuf = new byte[this.count * maxByteLen];
               int len = CharsetConv.charArrayToByte(encoding, this.value, this.offset, this.count, outbuf, 0, outbuf.length);
               if (len >= 0) {
                  byte[] ba = new byte[len];
                  System.arraycopy(outbuf, 0, ba, 0, len);
                  return ba;
               } else {
                  throw new UnsupportedEncodingException("Problem during conversion: " + enc);
               }
            } else {
               throw new UnsupportedEncodingException(enc);
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         throw new NullPointerException();
      }
   }

   public byte[] getBytes() {
      try {
         return this.getBytes(CharsetConv.defaultEncoding);
      } catch (UnsupportedEncodingException var2) {
         throw new RuntimeException("Missing default encoding " + CharsetConv.defaultEncoding);
      }
   }

   public native boolean equals(Object var1);

   public boolean equalsIgnoreCase(String anotherString) {
      return anotherString != null && anotherString.count == this.count && this.regionMatches(true, 0, anotherString, 0, this.count);
   }

   public int compareTo(String anotherString) {
      int len1 = this.count;
      int len2 = anotherString.count;
      int n = Math.min(len1, len2);
      char[] v1 = this.value;
      char[] v2 = anotherString.value;
      int i = this.offset;
      int j = anotherString.offset;
      if (i == j) {
         int k = i;

         for(int lim = n + i; k < lim; ++k) {
            char c1 = v1[k];
            char c2 = v2[k];
            if (c1 != c2) {
               return c1 - c2;
            }
         }
      } else {
         while(n-- != 0) {
            char c1 = v1[i++];
            char c2 = v2[j++];
            if (c1 != c2) {
               return c1 - c2;
            }
         }
      }

      return len1 - len2;
   }

   public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
      char[] ta = this.value;
      int to = this.offset + toffset;
      char[] pa = other.value;
      int po = other.offset + ooffset;
      if (ooffset >= 0 && toffset >= 0 && (long)toffset <= (long)this.count - (long)len && (long)ooffset <= (long)other.count - (long)len) {
         char u1;
         char u2;
         do {
            char c1;
            char c2;
            do {
               if (len-- <= 0) {
                  return true;
               }

               c1 = ta[to++];
               c2 = pa[po++];
            } while(c1 == c2);

            if (!ignoreCase) {
               break;
            }

            u1 = Character.toUpperCase(c1);
            u2 = Character.toUpperCase(c2);
         } while(u1 == u2 || Character.toLowerCase(u1) == Character.toLowerCase(u2));

         return false;
      } else {
         return false;
      }
   }

   public boolean startsWith(String prefix, int toffset) {
      char[] ta = this.value;
      int to = this.offset + toffset;
      char[] pa = prefix.value;
      int po = prefix.offset;
      int pc = prefix.count;
      if (toffset >= 0 && toffset <= this.count - pc) {
         do {
            --pc;
            if (pc < 0) {
               return true;
            }
         } while(ta[to++] == pa[po++]);

         return false;
      } else {
         return false;
      }
   }

   public boolean startsWith(String prefix) {
      return this.startsWith(prefix, 0);
   }

   public boolean endsWith(String suffix) {
      return this.startsWith(suffix, this.count - suffix.count);
   }

   public int hashCode() {
      int h = 0;
      int off = this.offset;
      char[] val = this.value;
      int len = this.count;

      for(int i = 0; i < len; ++i) {
         h = 31 * h + val[off++];
      }

      return h;
   }

   public native int indexOf(int var1);

   public native int indexOf(int var1, int var2);

   public int lastIndexOf(int ch) {
      return this.lastIndexOf(ch, this.count - 1);
   }

   public int lastIndexOf(int ch, int fromIndex) {
      int min = this.offset;
      char[] v = this.value;

      for(int i = this.offset + (fromIndex >= this.count ? this.count - 1 : fromIndex); i >= min; --i) {
         if (v[i] == ch) {
            return i - this.offset;
         }
      }

      return -1;
   }

   public int indexOf(String str) {
      return this.indexOf(str, 0);
   }

   public int indexOf(String str, int fromIndex) {
      char[] v1 = this.value;
      char[] v2 = str.value;
      int max = this.offset + (this.count - str.count);
      if (fromIndex >= this.count) {
         return this.count == 0 && fromIndex == 0 && str.count == 0 ? 0 : -1;
      } else {
         if (fromIndex < 0) {
            fromIndex = 0;
         }

         if (str.count == 0) {
            return fromIndex;
         } else {
            int strOffset = str.offset;
            char first = v2[strOffset];
            int i = this.offset + fromIndex;

            while(true) {
               while(i > max || v1[i] == first) {
                  if (i > max) {
                     return -1;
                  }

                  int j = i + 1;
                  int end = j + str.count - 1;
                  int var11 = strOffset + 1;

                  do {
                     if (j >= end) {
                        return i - this.offset;
                     }
                  } while(v1[j++] == v2[var11++]);

                  ++i;
               }

               ++i;
            }
         }
      }
   }

   public String substring(int beginIndex) {
      return this.substring(beginIndex, this.count);
   }

   public String substring(int beginIndex, int endIndex) {
      if (beginIndex < 0) {
         throw new StringIndexOutOfBoundsException(beginIndex);
      } else if (endIndex > this.count) {
         throw new StringIndexOutOfBoundsException(endIndex);
      } else if (beginIndex > endIndex) {
         throw new StringIndexOutOfBoundsException(endIndex - beginIndex);
      } else {
         return beginIndex == 0 && endIndex == this.count ? this : new String(this.offset + beginIndex, endIndex - beginIndex, this.value);
      }
   }

   public String concat(String str) {
      int otherLen = str.length();
      if (otherLen == 0) {
         return this;
      } else {
         char[] buf = new char[this.count + otherLen];
         this.getChars(0, this.count, buf, 0);
         str.getChars(0, otherLen, buf, this.count);
         return new String(0, this.count + otherLen, buf);
      }
   }

   public String replace(char oldChar, char newChar) {
      if (oldChar != newChar) {
         int len = this.count;
         int i = -1;
         char[] val = this.value;
         int off = this.offset;

         do {
            ++i;
         } while(i < len && val[off + i] != oldChar);

         if (i < len) {
            char[] buf = new char[len];

            for(int j = 0; j < i; ++j) {
               buf[j] = val[off + j];
            }

            while(i < len) {
               char c = val[off + i];
               buf[i] = c == oldChar ? newChar : c;
               ++i;
            }

            return new String(0, len, buf);
         }
      }

      return this;
   }

   public String toLowerCase() {
      for(int i = 0; i < this.count; ++i) {
         char c = this.value[this.offset + i];
         if (c != Character.toLowerCase(c)) {
            char[] buf = new char[this.count];
            System.arraycopy(this.value, this.offset, buf, 0, i);

            while(i < this.count) {
               buf[i] = Character.toLowerCase(this.value[this.offset + i]);
               ++i;
            }

            return new String(0, this.count, buf);
         }
      }

      return this;
   }

   public String toUpperCase() {
      for(int i = 0; i < this.count; ++i) {
         char c = this.value[this.offset + i];
         if (c != Character.toUpperCase(c)) {
            char[] buf = new char[this.count];
            System.arraycopy(this.value, this.offset, buf, 0, i);

            while(i < this.count) {
               buf[i] = Character.toUpperCase(this.value[this.offset + i]);
               ++i;
            }

            return new String(0, this.count, buf);
         }
      }

      return this;
   }

   public String trim() {
      int len = this.count;
      int st = 0;
      int off = this.offset;

      char[] val;
      for(val = this.value; st < len && val[off + st] <= ' '; ++st) {
      }

      while(st < len && val[off + len - 1] <= ' ') {
         --len;
      }

      return st <= 0 && len >= this.count ? this : this.substring(st, len);
   }

   public String toString() {
      return this;
   }

   public char[] toCharArray() {
      char[] result = new char[this.count];
      this.getChars(0, this.count, result, 0);
      return result;
   }

   public static String valueOf(Object obj) {
      return obj == null ? "null" : obj.toString();
   }

   public static String valueOf(char[] data) {
      return new String(data);
   }

   public static String valueOf(char[] data, int offset, int count) {
      return new String(data, offset, count);
   }

   public static String valueOf(boolean b) {
      return b ? "true" : "false";
   }

   public static String valueOf(char c) {
      char[] data = new char[]{c};
      return new String(0, 1, data);
   }

   public static String valueOf(int i) {
      return Integer.toString(i, 10);
   }

   public static String valueOf(long l) {
      return Long.toString(l, 10);
   }

   public static String valueOf(float f) {
      return Float.toString(f);
   }

   public static String valueOf(double d) {
      return Double.toString(d);
   }

   public native String intern();
}
