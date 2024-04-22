package java.lang;

public final class StringBuffer {
   private char[] value;
   private int count;
   private boolean iT;

   public StringBuffer() {
      this(16);
   }

   public StringBuffer(int var1) {
      this.value = new char[var1];
      this.iT = false;
   }

   public StringBuffer(String var1) {
      this(var1.length() + 16);
      this.append(var1);
   }

   public final int length() {
      return this.count;
   }

   public final int capacity() {
      return this.value.length;
   }

   private final void copy() {
      char[] var1 = new char[this.value.length];
      System.arraycopy(this.value, 0, var1, 0, this.count);
      this.value = var1;
      this.iT = false;
   }

   public final synchronized void ensureCapacity(int var1) {
      if (var1 > this.value.length) {
         this.expandCapacity(var1);
      }

   }

   private void expandCapacity(int var1) {
      int var2;
      if ((var2 = this.value.length + 1 << 1) < 0) {
         var2 = Integer.MAX_VALUE;
      } else if (var1 > var2) {
         var2 = var1;
      }

      char[] var3 = new char[var2];
      System.arraycopy(this.value, 0, var3, 0, this.count);
      this.value = var3;
      this.iT = false;
   }

   public final synchronized void setLength(int var1) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else {
         if (var1 > this.value.length) {
            this.expandCapacity(var1);
         }

         if (this.count < var1) {
            if (this.iT) {
               this.copy();
            }

            while(this.count < var1) {
               this.value[this.count] = 0;
               ++this.count;
            }
         } else {
            this.count = var1;
            if (this.iT) {
               if (var1 > 0) {
                  this.copy();
                  return;
               }

               this.value = new char[16];
               this.iT = false;
            }
         }

      }
   }

   public final synchronized char charAt(int var1) {
      if (var1 >= 0 && var1 < this.count) {
         return this.value[var1];
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public final synchronized void getChars(int var1, int var2, char[] var3, int var4) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else if (var2 >= 0 && var2 <= this.count) {
         if (var1 > var2) {
            throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
         } else {
            System.arraycopy(this.value, var1, var3, var4, var2 - var1);
         }
      } else {
         throw new StringIndexOutOfBoundsException(var2);
      }
   }

   public final synchronized void setCharAt(int var1, char var2) {
      if (var1 >= 0 && var1 < this.count) {
         if (this.iT) {
            this.copy();
         }

         this.value[var1] = var2;
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   public final synchronized StringBuffer append(Object var1) {
      return this.append(String.valueOf(var1));
   }

   public final synchronized native StringBuffer append(String var1);

   public final synchronized StringBuffer append(char[] var1) {
      int var2 = var1.length;
      int var3;
      if ((var3 = this.count + var2) > this.value.length) {
         this.expandCapacity(var3);
      }

      System.arraycopy(var1, 0, this.value, this.count, var2);
      this.count = var3;
      return this;
   }

   public final synchronized StringBuffer append(char[] var1, int var2, int var3) {
      int var4;
      if ((var4 = this.count + var3) > this.value.length) {
         this.expandCapacity(var4);
      }

      System.arraycopy(var1, var2, this.value, this.count, var3);
      this.count = var4;
      return this;
   }

   public final StringBuffer append(boolean var1) {
      return this.append(String.valueOf(var1));
   }

   public final synchronized StringBuffer append(char var1) {
      int var2;
      if ((var2 = this.count + 1) > this.value.length) {
         this.expandCapacity(var2);
      }

      this.value[this.count++] = var1;
      return this;
   }

   public final native StringBuffer append(int var1);

   public final StringBuffer append(long var1) {
      return this.append(String.valueOf(var1));
   }

   public final StringBuffer append(float var1) {
      return this.append(String.valueOf(var1));
   }

   public final StringBuffer append(double var1) {
      return this.append(String.valueOf(var1));
   }

   public final synchronized StringBuffer delete(int var1, int var2) {
      if (var1 < 0) {
         throw new StringIndexOutOfBoundsException(var1);
      } else {
         if (var2 > this.count) {
            var2 = this.count;
         }

         if (var1 > var2) {
            throw new StringIndexOutOfBoundsException();
         } else {
            int var3;
            if ((var3 = var2 - var1) > 0) {
               if (this.iT) {
                  this.copy();
               }

               System.arraycopy(this.value, var1 + var3, this.value, var1, this.count - var2);
               this.count -= var3;
            }

            return this;
         }
      }
   }

   public final synchronized StringBuffer deleteCharAt(int var1) {
      if (var1 >= 0 && var1 < this.count) {
         if (this.iT) {
            this.copy();
         }

         System.arraycopy(this.value, var1 + 1, this.value, var1, this.count - var1 - 1);
         --this.count;
         return this;
      } else {
         throw new StringIndexOutOfBoundsException();
      }
   }

   public final synchronized StringBuffer insert(int var1, Object var2) {
      return this.insert(var1, String.valueOf(var2));
   }

   public final synchronized StringBuffer insert(int var1, String var2) {
      if (var1 >= 0 && var1 <= this.count) {
         if (var2 == null) {
            var2 = String.valueOf((Object)var2);
         }

         int var3 = var2.length();
         int var4;
         if ((var4 = this.count + var3) > this.value.length) {
            this.expandCapacity(var4);
         } else if (this.iT) {
            this.copy();
         }

         System.arraycopy(this.value, var1, this.value, var1 + var3, this.count - var1);
         var2.getChars(0, var3, this.value, var1);
         this.count = var4;
         return this;
      } else {
         throw new StringIndexOutOfBoundsException();
      }
   }

   public final synchronized StringBuffer insert(int var1, char[] var2) {
      if (var1 >= 0 && var1 <= this.count) {
         int var3 = var2.length;
         int var4;
         if ((var4 = this.count + var3) > this.value.length) {
            this.expandCapacity(var4);
         } else if (this.iT) {
            this.copy();
         }

         System.arraycopy(this.value, var1, this.value, var1 + var3, this.count - var1);
         System.arraycopy(var2, 0, this.value, var1, var3);
         this.count = var4;
         return this;
      } else {
         throw new StringIndexOutOfBoundsException();
      }
   }

   public final StringBuffer insert(int var1, boolean var2) {
      return this.insert(var1, String.valueOf(var2));
   }

   public final synchronized StringBuffer insert(int var1, char var2) {
      int var3;
      if ((var3 = this.count + 1) > this.value.length) {
         this.expandCapacity(var3);
      } else if (this.iT) {
         this.copy();
      }

      System.arraycopy(this.value, var1, this.value, var1 + 1, this.count - var1);
      this.value[var1] = var2;
      this.count = var3;
      return this;
   }

   public final StringBuffer insert(int var1, int var2) {
      return this.insert(var1, String.valueOf(var2));
   }

   public final StringBuffer insert(int var1, long var2) {
      return this.insert(var1, String.valueOf(var2));
   }

   public final StringBuffer insert(int var1, float var2) {
      return this.insert(var1, String.valueOf(var2));
   }

   public final StringBuffer insert(int var1, double var2) {
      return this.insert(var1, String.valueOf(var2));
   }

   public final synchronized StringBuffer reverse() {
      if (this.iT) {
         this.copy();
      }

      int var1;
      for(int var2 = (var1 = this.count - 1) - 1 >> 1; var2 >= 0; --var2) {
         char var3 = this.value[var2];
         this.value[var2] = this.value[var1 - var2];
         this.value[var1 - var2] = var3;
      }

      return this;
   }

   public final native String toString();

   final void aa() {
      this.iT = true;
   }

   final char[] getValue() {
      return this.value;
   }
}
