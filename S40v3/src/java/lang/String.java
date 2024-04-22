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

   public String(String var1) {
      this.count = var1.length();
      this.value = new char[this.count];
      var1.getChars(0, this.count, this.value, 0);
   }

   public String(char[] var1) {
      this.count = var1.length;
      this.value = new char[this.count];
      System.arraycopy(var1, 0, this.value, 0, this.count);
   }

   public String(char[] var1, int var2, int var3) {
      if (var2 < 0) {
         throw new StringIndexOutOfBoundsException(var2);
      } else if (var3 < 0) {
         throw new StringIndexOutOfBoundsException(var3);
      } else if (var2 > var1.length - var3) {
         throw new StringIndexOutOfBoundsException(var2 + var3);
      } else {
         this.value = new char[var3];
         this.count = var3;
         System.arraycopy(var1, var2, this.value, 0, var3);
      }
   }

   private static char[] byteToCharArray(byte[] var0, int var1, int var2, String var3) throws UnsupportedEncodingException {
      Object var4 = null;
      if (var3 == null) {
         throw new NullPointerException();
      } else if (var1 >= 0 && var2 >= 0 && var1 <= var0.length - var2) {
         InputStreamReader var5 = new InputStreamReader(new ByteArrayInputStream(var0, var1, var2), var3);
         char[] var6 = new char[var2];
         int var7 = 0;

         try {
            while(true) {
               int var8 = var5.read(var6, var7, var2 - var7);
               if (var8 <= 0) {
                  var5.close();
                  break;
               }

               var7 += var8;
            }
         } catch (IOException var9) {
            var7 = 0;
         }

         char[] var10;
         if (var7 > 0) {
            var10 = new char[var7];
            System.arraycopy(var6, 0, var10, 0, var7);
         } else {
            var10 = new char[0];
         }

         return var10;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private static char[] byteToCharArray(byte[] var0, int var1, int var2) {
      try {
         return byteToCharArray(var0, var1, var2, CharsetConv.defaultEncoding);
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException("Missing default encoding " + CharsetConv.defaultEncoding);
      }
   }

   public String(byte[] var1, int var2, int var3, String var4) throws UnsupportedEncodingException {
      this(byteToCharArray(var1, var2, var3, var4));
   }

   public String(byte[] var1, String var2) throws UnsupportedEncodingException {
      this(var1, 0, var1.length, var2);
   }

   public String(byte[] var1, int var2, int var3) {
      this(byteToCharArray(var1, var2, var3));
   }

   public String(byte[] var1) {
      this((byte[])var1, 0, var1.length);
   }

   public String(StringBuffer var1) {
      synchronized(var1) {
         var1.setShared();
         this.value = var1.getValue();
         this.offset = 0;
         this.count = var1.length();
      }
   }

   String(int var1, int var2, char[] var3) {
      this.value = var3;
      this.offset = var1;
      this.count = var2;
   }

   public int length() {
      return this.count;
   }

   public native char charAt(int var1);

   public void getChars(int var1, int var2, char[] var3, int var4) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else if (var2 > this.count) {
         throw new StringIndexOutOfBoundsException(var2);
      } else if (var1 > var2) {
         throw new StringIndexOutOfBoundsException(var2 - var1);
      } else {
         System.arraycopy(this.value, this.offset + var1, var3, var4, var2 - var1);
      }
   }

   public byte[] getBytes(String var1) throws UnsupportedEncodingException {
      Object var2 = null;
      if (var1 != null) {
         if (this.offset >= 0 && this.count >= 0 && this.offset <= this.value.length - this.count) {
            byte[] var3 = CharsetConv.isSupportedEncoding(var1);
            if (var3 != null) {
               int var4 = CharsetConv.getMaxByteLength(var3);
               byte[] var5 = new byte[this.count * var4];
               int var6 = CharsetConv.charArrayToByte(var3, this.value, this.offset, this.count, var5, 0, var5.length);
               if (var6 >= 0) {
                  byte[] var7 = new byte[var6];
                  System.arraycopy(var5, 0, var7, 0, var6);
                  return var7;
               } else {
                  throw new UnsupportedEncodingException("Problem during conversion: " + var1);
               }
            } else {
               throw new UnsupportedEncodingException(var1);
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

   public boolean equalsIgnoreCase(String var1) {
      return var1 != null && var1.count == this.count && this.regionMatches(true, 0, var1, 0, this.count);
   }

   public int compareTo(String var1) {
      int var2 = this.count;
      int var3 = var1.count;
      int var4 = Math.min(var2, var3);
      char[] var5 = this.value;
      char[] var6 = var1.value;
      int var7 = this.offset;
      int var8 = var1.offset;
      if (var7 == var8) {
         int var14 = var7;

         for(int var13 = var4 + var7; var14 < var13; ++var14) {
            char var11 = var5[var14];
            char var12 = var6[var14];
            if (var11 != var12) {
               return var11 - var12;
            }
         }
      } else {
         while(var4-- != 0) {
            char var9 = var5[var7++];
            char var10 = var6[var8++];
            if (var9 != var10) {
               return var9 - var10;
            }
         }
      }

      return var2 - var3;
   }

   public boolean regionMatches(boolean var1, int var2, String var3, int var4, int var5) {
      char[] var6 = this.value;
      int var7 = this.offset + var2;
      int var10000 = this.offset + this.count;
      char[] var9 = var3.value;
      int var10 = var3.offset + var4;
      if (var4 >= 0 && var2 >= 0 && (long)var2 <= (long)this.count - (long)var5 && (long)var4 <= (long)var3.count - (long)var5) {
         char var13;
         char var14;
         do {
            char var11;
            char var12;
            do {
               if (var5-- <= 0) {
                  return true;
               }

               var11 = var6[var7++];
               var12 = var9[var10++];
            } while(var11 == var12);

            if (!var1) {
               break;
            }

            var13 = Character.toUpperCase(var11);
            var14 = Character.toUpperCase(var12);
         } while(var13 == var14 || Character.toLowerCase(var13) == Character.toLowerCase(var14));

         return false;
      } else {
         return false;
      }
   }

   public boolean startsWith(String var1, int var2) {
      char[] var3 = this.value;
      int var4 = this.offset + var2;
      int var10000 = this.offset + this.count;
      char[] var6 = var1.value;
      int var7 = var1.offset;
      int var8 = var1.count;
      if (var2 >= 0 && var2 <= this.count - var8) {
         do {
            --var8;
            if (var8 < 0) {
               return true;
            }
         } while(var3[var4++] == var6[var7++]);

         return false;
      } else {
         return false;
      }
   }

   public boolean startsWith(String var1) {
      return this.startsWith(var1, 0);
   }

   public boolean endsWith(String var1) {
      return this.startsWith(var1, this.count - var1.count);
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.offset;
      char[] var3 = this.value;
      int var4 = this.count;

      for(int var5 = 0; var5 < var4; ++var5) {
         var1 = 31 * var1 + var3[var2++];
      }

      return var1;
   }

   public native int indexOf(int var1);

   public native int indexOf(int var1, int var2);

   public int lastIndexOf(int var1) {
      return this.lastIndexOf(var1, this.count - 1);
   }

   public int lastIndexOf(int var1, int var2) {
      int var3 = this.offset;
      char[] var4 = this.value;

      for(int var5 = this.offset + (var2 >= this.count ? this.count - 1 : var2); var5 >= var3; --var5) {
         if (var4[var5] == var1) {
            return var5 - this.offset;
         }
      }

      return -1;
   }

   public int indexOf(String var1) {
      return this.indexOf(var1, 0);
   }

   public int indexOf(String var1, int var2) {
      char[] var3 = this.value;
      char[] var4 = var1.value;
      int var5 = this.offset + (this.count - var1.count);
      if (var2 >= this.count) {
         return this.count == 0 && var2 == 0 && var1.count == 0 ? 0 : -1;
      } else {
         if (var2 < 0) {
            var2 = 0;
         }

         if (var1.count == 0) {
            return var2;
         } else {
            int var6 = var1.offset;
            char var7 = var4[var6];
            int var8 = this.offset + var2;

            while(true) {
               while(var8 > var5 || var3[var8] == var7) {
                  if (var8 > var5) {
                     return -1;
                  }

                  int var9 = var8 + 1;
                  int var10 = var9 + var1.count - 1;
                  int var11 = var6 + 1;

                  do {
                     if (var9 >= var10) {
                        return var8 - this.offset;
                     }
                  } while(var3[var9++] == var4[var11++]);

                  ++var8;
               }

               ++var8;
            }
         }
      }
   }

   public String substring(int var1) {
      return this.substring(var1, this.count);
   }

   public String substring(int var1, int var2) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else if (var2 > this.count) {
         throw new StringIndexOutOfBoundsException(var2);
      } else if (var1 > var2) {
         throw new StringIndexOutOfBoundsException(var2 - var1);
      } else {
         return var1 == 0 && var2 == this.count ? this : new String(this.offset + var1, var2 - var1, this.value);
      }
   }

   public String concat(String var1) {
      int var2 = var1.length();
      if (var2 == 0) {
         return this;
      } else {
         char[] var3 = new char[this.count + var2];
         this.getChars(0, this.count, var3, 0);
         var1.getChars(0, var2, var3, this.count);
         return new String(0, this.count + var2, var3);
      }
   }

   public String replace(char var1, char var2) {
      if (var1 != var2) {
         int var3 = this.count;
         int var4 = -1;
         char[] var5 = this.value;
         int var6 = this.offset;

         do {
            ++var4;
         } while(var4 < var3 && var5[var6 + var4] != var1);

         if (var4 < var3) {
            char[] var7 = new char[var3];

            for(int var8 = 0; var8 < var4; ++var8) {
               var7[var8] = var5[var6 + var8];
            }

            while(var4 < var3) {
               char var9 = var5[var6 + var4];
               var7[var4] = var9 == var1 ? var2 : var9;
               ++var4;
            }

            return new String(0, var3, var7);
         }
      }

      return this;
   }

   public String toLowerCase() {
      for(int var1 = 0; var1 < this.count; ++var1) {
         char var2 = this.value[this.offset + var1];
         if (var2 != Character.toLowerCase(var2)) {
            char[] var3 = new char[this.count];
            System.arraycopy(this.value, this.offset, var3, 0, var1);

            while(var1 < this.count) {
               var3[var1] = Character.toLowerCase(this.value[this.offset + var1]);
               ++var1;
            }

            return new String(0, this.count, var3);
         }
      }

      return this;
   }

   public String toUpperCase() {
      for(int var1 = 0; var1 < this.count; ++var1) {
         char var2 = this.value[this.offset + var1];
         if (var2 != Character.toUpperCase(var2)) {
            char[] var3 = new char[this.count];
            System.arraycopy(this.value, this.offset, var3, 0, var1);

            while(var1 < this.count) {
               var3[var1] = Character.toUpperCase(this.value[this.offset + var1]);
               ++var1;
            }

            return new String(0, this.count, var3);
         }
      }

      return this;
   }

   public String trim() {
      int var1 = this.count;
      int var2 = 0;
      int var3 = this.offset;

      char[] var4;
      for(var4 = this.value; var2 < var1 && var4[var3 + var2] <= ' '; ++var2) {
      }

      while(var2 < var1 && var4[var3 + var1 - 1] <= ' ') {
         --var1;
      }

      return var2 <= 0 && var1 >= this.count ? this : this.substring(var2, var1);
   }

   public String toString() {
      return this;
   }

   public char[] toCharArray() {
      char[] var1 = new char[this.count];
      this.getChars(0, this.count, var1, 0);
      return var1;
   }

   public static String valueOf(Object var0) {
      return var0 == null ? "null" : var0.toString();
   }

   public static String valueOf(char[] var0) {
      return new String(var0);
   }

   public static String valueOf(char[] var0, int var1, int var2) {
      return new String(var0, var1, var2);
   }

   public static String valueOf(boolean var0) {
      return var0 ? "true" : "false";
   }

   public static String valueOf(char var0) {
      char[] var1 = new char[]{var0};
      return new String(0, 1, var1);
   }

   public static String valueOf(int var0) {
      return Integer.toString(var0, 10);
   }

   public static String valueOf(long var0) {
      return Long.toString(var0, 10);
   }

   public static String valueOf(float var0) {
      return Float.toString(var0);
   }

   public static String valueOf(double var0) {
      return Double.toString(var0);
   }

   public native String intern();
}
