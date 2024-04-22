package java.lang;

class FDBigInt {
   int nWords;
   int[] data;

   public FDBigInt(int var1) {
      this.nWords = 1;
      this.data = new int[1];
      this.data[0] = var1;
   }

   public FDBigInt(long var1) {
      this.data = new int[2];
      this.data[0] = (int)var1;
      this.data[1] = (int)(var1 >>> 32);
      this.nWords = this.data[1] == 0 ? 1 : 2;
   }

   public FDBigInt(FDBigInt var1) {
      this.data = new int[this.nWords = var1.nWords];
      System.arraycopy(var1.data, 0, this.data, 0, this.nWords);
   }

   private FDBigInt(int[] var1, int var2) {
      this.data = var1;
      this.nWords = var2;
   }

   public FDBigInt(long var1, char[] var3, int var4, int var5) {
      int var6;
      if ((var6 = (var5 + 8) / 9) < 2) {
         var6 = 2;
      }

      this.data = new int[var6];
      this.data[0] = (int)var1;
      this.data[1] = (int)(var1 >>> 32);
      this.nWords = this.data[1] == 0 ? 1 : 2;
      int var7 = var4;
      int var2 = var5 - 5;

      while(var7 < var2) {
         var6 = var7 + 5;

         for(var4 = var3[var7++] - 48; var7 < var6; var4 = 10 * var4 + var3[var7++] - 48) {
         }

         this.multaddMe(100000, var4);
      }

      var6 = 1;

      for(var4 = 0; var7 < var5; var6 *= 10) {
         var4 = 10 * var4 + var3[var7++] - 48;
      }

      if (var6 != 1) {
         this.multaddMe(var6, var4);
      }

   }

   public final void lshiftMe(int var1) throws IllegalArgumentException {
      if (var1 <= 0) {
         if (var1 != 0) {
            throw new IllegalArgumentException("negative shift count");
         }
      } else {
         int var2 = var1 >> 5;
         var1 &= 31;
         int var3 = 32 - var1;
         int[] var4 = this.data;
         int[] var5 = this.data;
         if (this.nWords + var2 + 1 > var4.length) {
            var4 = new int[this.nWords + var2 + 1];
         }

         int var6 = this.nWords + var2;
         int var7 = this.nWords - 1;
         if (var1 == 0) {
            System.arraycopy(var5, 0, var4, var2, this.nWords);
            var6 = var2 - 1;
         } else {
            int var10001;
            int var10002;
            for(var4[var6--] = var5[var7] >>> var3; var7 >= 1; var4[var10001] = var10002 | var5[var7] >>> var3) {
               var10001 = var6--;
               var10002 = var5[var7] << var1;
               --var7;
            }

            var4[var6--] = var5[var7] << var1;
         }

         while(var6 >= 0) {
            var4[var6--] = 0;
         }

         this.data = var4;

         for(this.nWords += var2 + 1; this.nWords > 1 && this.data[this.nWords - 1] == 0; --this.nWords) {
         }

      }
   }

   public final FDBigInt l(int var1) {
      long var2;
      int[] var7 = new int[(var2 = (long)var1) * ((long)this.data[this.nWords - 1] & 4294967295L) > 268435455L ? this.nWords + 1 : this.nWords];
      long var5 = 0L;

      for(int var4 = 0; var4 < this.nWords; ++var4) {
         var5 += var2 * ((long)this.data[var4] & 4294967295L);
         var7[var4] = (int)var5;
         var5 >>>= 32;
      }

      if (var5 == 0L) {
         return new FDBigInt(var7, this.nWords);
      } else {
         var7[this.nWords] = (int)var5;
         return new FDBigInt(var7, this.nWords + 1);
      }
   }

   private void multaddMe(int var1, int var2) {
      long var3;
      long var5 = (var3 = (long)var1) * ((long)this.data[0] & 4294967295L) + ((long)var2 & 4294967295L);
      this.data[0] = (int)var5;
      var5 >>>= 32;

      for(var1 = 1; var1 < this.nWords; ++var1) {
         var5 += var3 * ((long)this.data[var1] & 4294967295L);
         this.data[var1] = (int)var5;
         var5 >>>= 32;
      }

      if (var5 != 0L) {
         this.data[this.nWords] = (int)var5;
         ++this.nWords;
      }

   }

   public final FDBigInt a(FDBigInt var1) {
      int[] var2 = new int[this.nWords + var1.nWords];

      int var3;
      for(var3 = 0; var3 < this.nWords; ++var3) {
         long var4 = (long)this.data[var3] & 4294967295L;
         long var6 = 0L;

         int var8;
         for(var8 = 0; var8 < var1.nWords; ++var8) {
            var6 += ((long)var2[var3 + var8] & 4294967295L) + var4 * ((long)var1.data[var8] & 4294967295L);
            var2[var3 + var8] = (int)var6;
            var6 >>>= 32;
         }

         var2[var3 + var8] = (int)var6;
      }

      for(var3 = var2.length - 1; var3 > 0 && var2[var3] == 0; --var3) {
      }

      return new FDBigInt(var2, var3 + 1);
   }

   public final FDBigInt b(FDBigInt var1) {
      long var7 = 0L;
      int[] var2;
      int[] var3;
      int var4;
      int var5;
      if (this.nWords >= var1.nWords) {
         var2 = this.data;
         var4 = this.nWords;
         var3 = var1.data;
         var5 = var1.nWords;
      } else {
         var2 = var1.data;
         var4 = var1.nWords;
         var3 = this.data;
         var5 = this.nWords;
      }

      int[] var10 = new int[var4];

      int var9;
      for(var9 = 0; var9 < var4; ++var9) {
         var7 += (long)var2[var9] & 4294967295L;
         if (var9 < var5) {
            var7 += (long)var3[var9] & 4294967295L;
         }

         var10[var9] = (int)var7;
         var7 >>= 32;
      }

      if (var7 != 0L) {
         var2 = new int[var10.length + 1];
         System.arraycopy(var10, 0, var2, 0, var10.length);
         var2[var9++] = (int)var7;
         return new FDBigInt(var2, var9);
      } else {
         return new FDBigInt(var10, var9);
      }
   }

   public final FDBigInt c(FDBigInt var1) {
      int[] var2 = new int[this.nWords];
      int var4 = this.nWords;
      int var5 = var1.nWords;
      int var6 = 0;
      long var7 = 0L;

      int var3;
      for(var3 = 0; var3 < var4; ++var3) {
         var7 += (long)this.data[var3] & 4294967295L;
         if (var3 < var5) {
            var7 -= (long)var1.data[var3] & 4294967295L;
         }

         if ((var2[var3] = (int)var7) == 0) {
            ++var6;
         } else {
            var6 = 0;
         }

         var7 >>= 32;
      }

      if (var7 != 0L) {
         throw new RuntimeException("Assertion botch: borrow out of subtract");
      } else {
         do {
            if (var3 >= var5) {
               return new FDBigInt(var2, var4 - var6);
            }
         } while(var1.data[var3++] == 0);

         throw new RuntimeException("Assertion botch: negative result of subtract");
      }
   }

   public final int d(FDBigInt var1) {
      int var2;
      int var3;
      if (this.nWords > var1.nWords) {
         var3 = var1.nWords - 1;

         for(var2 = this.nWords - 1; var2 > var3; --var2) {
            if (this.data[var2] != 0) {
               return 1;
            }
         }
      } else if (this.nWords < var1.nWords) {
         var3 = this.nWords - 1;

         for(var2 = var1.nWords - 1; var2 > var3; --var2) {
            if (var1.data[var2] != 0) {
               return -1;
            }
         }
      } else {
         var2 = this.nWords - 1;
      }

      while(var2 > 0 && this.data[var2] == var1.data[var2]) {
         --var2;
      }

      var3 = this.data[var2];
      int var4 = var1.data[var2];
      if (var3 < 0) {
         return var4 < 0 ? var3 - var4 : 1;
      } else {
         return var4 < 0 ? -1 : var3 - var4;
      }
   }

   public final int e(FDBigInt var1) throws IllegalArgumentException {
      if (this.nWords != var1.nWords) {
         throw new IllegalArgumentException("disparate values");
      } else {
         int var2 = this.nWords - 1;
         long var3 = ((long)this.data[var2] & 4294967295L) / (long)var1.data[var2];
         long var5 = 0L;

         for(int var7 = 0; var7 <= var2; ++var7) {
            var5 += ((long)this.data[var7] & 4294967295L) - var3 * ((long)var1.data[var7] & 4294967295L);
            this.data[var7] = (int)var5;
            var5 >>= 32;
         }

         int var9;
         long var10;
         if (var5 != 0L) {
            for(var10 = 0L; var10 == 0L; --var3) {
               var10 = 0L;

               for(var9 = 0; var9 <= var2; ++var9) {
                  var10 += ((long)this.data[var9] & 4294967295L) + ((long)var1.data[var9] & 4294967295L);
                  this.data[var9] = (int)var10;
                  var10 >>= 32;
               }

               if (var10 != 0L && var10 != 1L) {
                  throw new RuntimeException("Assertion botch: " + var10 + " carry out of division correction");
               }
            }
         }

         var10 = 0L;

         for(var9 = 0; var9 <= var2; ++var9) {
            var10 += 10L * ((long)this.data[var9] & 4294967295L);
            this.data[var9] = (int)var10;
            var10 >>= 32;
         }

         if (var10 != 0L) {
            throw new RuntimeException("Assertion botch: carry out of *10");
         } else {
            return (int)var3;
         }
      }
   }

   public final String toString() {
      StringBuffer var1;
      (var1 = new StringBuffer(30)).append('[');
      int var2 = Math.min(this.nWords - 1, this.data.length - 1);
      if (this.nWords > this.data.length) {
         var1.append("(" + this.data.length + "<" + this.nWords + "!)");
      }

      while(var2 > 0) {
         var1.append(Integer.toHexString(this.data[var2]));
         var1.append(' ');
         --var2;
      }

      var1.append(Integer.toHexString(this.data[0]));
      var1.append(']');
      return new String(var1);
   }
}
