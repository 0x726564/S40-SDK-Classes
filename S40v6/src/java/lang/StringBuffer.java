package java.lang;

public final class StringBuffer {
   private char[] value;
   private int count;
   private boolean shared;

   public StringBuffer() {
      this(16);
   }

   public StringBuffer(int length) {
      this.value = new char[length];
      this.shared = false;
   }

   public StringBuffer(String str) {
      this(str.length() + 16);
      this.append(str);
   }

   public int length() {
      return this.count;
   }

   public int capacity() {
      return this.value.length;
   }

   private final void copy() {
      char[] newValue = new char[this.value.length];
      System.arraycopy(this.value, 0, newValue, 0, this.count);
      this.value = newValue;
      this.shared = false;
   }

   public synchronized void ensureCapacity(int minimumCapacity) {
      if (minimumCapacity > this.value.length) {
         this.expandCapacity(minimumCapacity);
      }

   }

   private void expandCapacity(int minimumCapacity) {
      int newCapacity = (this.value.length + 1) * 2;
      if (newCapacity < 0) {
         newCapacity = Integer.MAX_VALUE;
      } else if (minimumCapacity > newCapacity) {
         newCapacity = minimumCapacity;
      }

      char[] newValue = new char[newCapacity];
      System.arraycopy(this.value, 0, newValue, 0, this.count);
      this.value = newValue;
      this.shared = false;
   }

   public synchronized void setLength(int newLength) {
      if (newLength < 0) {
         throw new StringIndexOutOfBoundsException(newLength);
      } else {
         if (newLength > this.value.length) {
            this.expandCapacity(newLength);
         }

         if (this.count < newLength) {
            if (this.shared) {
               this.copy();
            }

            while(this.count < newLength) {
               this.value[this.count] = 0;
               ++this.count;
            }
         } else {
            this.count = newLength;
            if (this.shared) {
               if (newLength > 0) {
                  this.copy();
               } else {
                  this.value = new char[16];
                  this.shared = false;
               }
            }
         }

      }
   }

   public synchronized char charAt(int index) {
      if (index >= 0 && index < this.count) {
         return this.value[index];
      } else {
         throw new StringIndexOutOfBoundsException(index);
      }
   }

   public synchronized void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      if (srcBegin < 0) {
         throw new StringIndexOutOfBoundsException(srcBegin);
      } else if (srcEnd >= 0 && srcEnd <= this.count) {
         if (srcBegin > srcEnd) {
            throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
         } else {
            System.arraycopy(this.value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
         }
      } else {
         throw new StringIndexOutOfBoundsException(srcEnd);
      }
   }

   public synchronized void setCharAt(int index, char ch) {
      if (index >= 0 && index < this.count) {
         if (this.shared) {
            this.copy();
         }

         this.value[index] = ch;
      } else {
         throw new StringIndexOutOfBoundsException(index);
      }
   }

   public synchronized StringBuffer append(Object obj) {
      return this.append(String.valueOf(obj));
   }

   public synchronized native StringBuffer append(String var1);

   public synchronized StringBuffer append(char[] str) {
      int len = str.length;
      int newcount = this.count + len;
      if (newcount > this.value.length) {
         this.expandCapacity(newcount);
      }

      System.arraycopy(str, 0, this.value, this.count, len);
      this.count = newcount;
      return this;
   }

   public synchronized StringBuffer append(char[] str, int offset, int len) {
      int newcount = this.count + len;
      if (newcount > this.value.length) {
         this.expandCapacity(newcount);
      }

      System.arraycopy(str, offset, this.value, this.count, len);
      this.count = newcount;
      return this;
   }

   public StringBuffer append(boolean b) {
      return this.append(String.valueOf(b));
   }

   public synchronized StringBuffer append(char c) {
      int newcount = this.count + 1;
      if (newcount > this.value.length) {
         this.expandCapacity(newcount);
      }

      this.value[this.count++] = c;
      return this;
   }

   public native StringBuffer append(int var1);

   public StringBuffer append(long l) {
      return this.append(String.valueOf(l));
   }

   public StringBuffer append(float f) {
      return this.append(String.valueOf(f));
   }

   public StringBuffer append(double d) {
      return this.append(String.valueOf(d));
   }

   public synchronized StringBuffer delete(int start, int end) {
      if (start < 0) {
         throw new StringIndexOutOfBoundsException(start);
      } else {
         if (end > this.count) {
            end = this.count;
         }

         if (start > end) {
            throw new StringIndexOutOfBoundsException();
         } else {
            int len = end - start;
            if (len > 0) {
               if (this.shared) {
                  this.copy();
               }

               System.arraycopy(this.value, start + len, this.value, start, this.count - end);
               this.count -= len;
            }

            return this;
         }
      }
   }

   public synchronized StringBuffer deleteCharAt(int index) {
      if (index >= 0 && index < this.count) {
         if (this.shared) {
            this.copy();
         }

         System.arraycopy(this.value, index + 1, this.value, index, this.count - index - 1);
         --this.count;
         return this;
      } else {
         throw new StringIndexOutOfBoundsException();
      }
   }

   public synchronized StringBuffer insert(int offset, Object obj) {
      return this.insert(offset, String.valueOf(obj));
   }

   public synchronized StringBuffer insert(int offset, String str) {
      if (offset >= 0 && offset <= this.count) {
         if (str == null) {
            str = String.valueOf((Object)str);
         }

         int len = str.length();
         int newcount = this.count + len;
         if (newcount > this.value.length) {
            this.expandCapacity(newcount);
         } else if (this.shared) {
            this.copy();
         }

         System.arraycopy(this.value, offset, this.value, offset + len, this.count - offset);
         str.getChars(0, len, this.value, offset);
         this.count = newcount;
         return this;
      } else {
         throw new StringIndexOutOfBoundsException();
      }
   }

   public synchronized StringBuffer insert(int offset, char[] str) {
      if (offset >= 0 && offset <= this.count) {
         int len = str.length;
         int newcount = this.count + len;
         if (newcount > this.value.length) {
            this.expandCapacity(newcount);
         } else if (this.shared) {
            this.copy();
         }

         System.arraycopy(this.value, offset, this.value, offset + len, this.count - offset);
         System.arraycopy(str, 0, this.value, offset, len);
         this.count = newcount;
         return this;
      } else {
         throw new StringIndexOutOfBoundsException();
      }
   }

   public StringBuffer insert(int offset, boolean b) {
      return this.insert(offset, String.valueOf(b));
   }

   public synchronized StringBuffer insert(int offset, char c) {
      int newcount = this.count + 1;
      if (newcount > this.value.length) {
         this.expandCapacity(newcount);
      } else if (this.shared) {
         this.copy();
      }

      System.arraycopy(this.value, offset, this.value, offset + 1, this.count - offset);
      this.value[offset] = c;
      this.count = newcount;
      return this;
   }

   public StringBuffer insert(int offset, int i) {
      return this.insert(offset, String.valueOf(i));
   }

   public StringBuffer insert(int offset, long l) {
      return this.insert(offset, String.valueOf(l));
   }

   public StringBuffer insert(int offset, float f) {
      return this.insert(offset, String.valueOf(f));
   }

   public StringBuffer insert(int offset, double d) {
      return this.insert(offset, String.valueOf(d));
   }

   public synchronized StringBuffer reverse() {
      if (this.shared) {
         this.copy();
      }

      int n = this.count - 1;

      for(int j = n - 1 >> 1; j >= 0; --j) {
         char temp = this.value[j];
         this.value[j] = this.value[n - j];
         this.value[n - j] = temp;
      }

      return this;
   }

   public native String toString();

   final void setShared() {
      this.shared = true;
   }

   final char[] getValue() {
      return this.value;
   }
}
