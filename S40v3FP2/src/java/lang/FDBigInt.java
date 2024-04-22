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
      int var6 = (var5 + 8) / 9;
      if (var6 < 2) {
         var6 = 2;
      }

      this.data = new int[var6];
      this.data[0] = (int)var1;
      this.data[1] = (int)(var1 >>> 32);
      this.nWords = this.data[1] == 0 ? 1 : 2;
      int var7 = var4;
      int var8 = var5 - 5;

      int var9;
      int var10;
      while(var7 < var8) {
         var10 = var7 + 5;

         for(var9 = var3[var7++] - 48; var7 < var10; var9 = 10 * var9 + var3[var7++] - 48) {
         }

         this.multaddMe(100000, var9);
      }

      var10 = 1;

      for(var9 = 0; var7 < var5; var10 *= 10) {
         var9 = 10 * var9 + var3[var7++] - 48;
      }

      if (var10 != 1) {
         this.multaddMe(var10, var9);
      }

   }

   public void lshiftMe(int var1) throws IllegalArgumentException {
      if (var1 <= 0) {
         if (var1 != 0) {
            throw new IllegalArgumentException("negative shift count");
         }
      } else {
         int var2 = var1 >> 5;
         int var3 = var1 & 31;
         int var4 = 32 - var3;
         int[] var5 = this.data;
         int[] var6 = this.data;
         if (this.nWords + var2 + 1 > var5.length) {
            var5 = new int[this.nWords + var2 + 1];
         }

         int var7 = this.nWords + var2;
         int var8 = this.nWords - 1;
         if (var3 == 0) {
            System.arraycopy(var6, 0, var5, var2, this.nWords);
            var7 = var2 - 1;
         } else {
            int var10001;
            int var10002;
            for(var5[var7--] = var6[var8] >>> var4; var8 >= 1; var5[var10001] = var10002 | var6[var8] >>> var4) {
               var10001 = var7--;
               var10002 = var6[var8] << var3;
               --var8;
            }

            var5[var7--] = var6[var8] << var3;
         }

         while(var7 >= 0) {
            var5[var7--] = 0;
         }

         this.data = var5;

         for(this.nWords += var2 + 1; this.nWords > 1 && this.data[this.nWords - 1] == 0; --this.nWords) {
         }

      }
   }

   public int normalizeMe() throws IllegalArgumentException {
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      int var1;
      for(var1 = this.nWords - 1; var1 >= 0 && (var4 = this.data[var1]) == 0; --var1) {
         ++var2;
      }

      if (var1 < 0) {
         throw new IllegalArgumentException("zero value");
      } else {
         this.nWords -= var2;
         if ((var4 & -268435456) != 0) {
            for(var3 = 32; (var4 & -268435456) != 0; --var3) {
               var4 >>>= 1;
            }
         } else {
            while(var4 <= 1048575) {
               var4 <<= 8;
               var3 += 8;
            }

            while(var4 <= 134217727) {
               var4 <<= 1;
               ++var3;
            }
         }

         if (var3 != 0) {
            this.lshiftMe(var3);
         }

         return var3;
      }
   }

   public FDBigInt mult(int var1) {
      long var2 = (long)var1;
      int[] var4 = new int[var2 * ((long)this.data[this.nWords - 1] & 4294967295L) > 268435455L ? this.nWords + 1 : this.nWords];
      long var5 = 0L;

      for(int var7 = 0; var7 < this.nWords; ++var7) {
         var5 += var2 * ((long)this.data[var7] & 4294967295L);
         var4[var7] = (int)var5;
         var5 >>>= 32;
      }

      if (var5 == 0L) {
         return new FDBigInt(var4, this.nWords);
      } else {
         var4[this.nWords] = (int)var5;
         return new FDBigInt(var4, this.nWords + 1);
      }
   }

   public void multaddMe(int var1, int var2) {
      long var3 = (long)var1;
      long var5 = var3 * ((long)this.data[0] & 4294967295L) + ((long)var2 & 4294967295L);
      this.data[0] = (int)var5;
      var5 >>>= 32;

      for(int var7 = 1; var7 < this.nWords; ++var7) {
         var5 += var3 * ((long)this.data[var7] & 4294967295L);
         this.data[var7] = (int)var5;
         var5 >>>= 32;
      }

      if (var5 != 0L) {
         this.data[this.nWords] = (int)var5;
         ++this.nWords;
      }

   }

   public FDBigInt mult(FDBigInt var1) {
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

   public FDBigInt add(FDBigInt var1) {
      long var7 = 0L;
      int[] var3;
      int[] var4;
      int var5;
      int var6;
      if (this.nWords >= var1.nWords) {
         var3 = this.data;
         var5 = this.nWords;
         var4 = var1.data;
         var6 = var1.nWords;
      } else {
         var3 = var1.data;
         var5 = var1.nWords;
         var4 = this.data;
         var6 = this.nWords;
      }

      int[] var9 = new int[var5];

      int var2;
      for(var2 = 0; var2 < var5; ++var2) {
         var7 += (long)var3[var2] & 4294967295L;
         if (var2 < var6) {
            var7 += (long)var4[var2] & 4294967295L;
         }

         var9[var2] = (int)var7;
         var7 >>= 32;
      }

      if (var7 != 0L) {
         int[] var10 = new int[var9.length + 1];
         System.arraycopy(var9, 0, var10, 0, var9.length);
         var10[var2++] = (int)var7;
         return new FDBigInt(var10, var2);
      } else {
         return new FDBigInt(var9, var2);
      }
   }

   public FDBigInt sub(FDBigInt var1) {
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

   public int cmp(FDBigInt var1) {
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

   public int quoRemIteration(FDBigInt var1) throws IllegalArgumentException {
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

   public long longValue() {
      int var1;
      for(var1 = this.nWords - 1; var1 > 1; --var1) {
         if (this.data[var1] != 0) {
            throw new RuntimeException("Assertion botch: value too big");
         }
      }

      switch(var1) {
      case 0:
         return (long)this.data[0] & 4294967295L;
      case 1:
         if (this.data[1] < 0) {
            throw new RuntimeException("Assertion botch: value too big");
         }

         return (long)this.data[1] << 32 | (long)this.data[0] & 4294967295L;
      default:
         throw new RuntimeException("Assertion botch: longValue confused");
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer(30);
      var1.append('[');
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
