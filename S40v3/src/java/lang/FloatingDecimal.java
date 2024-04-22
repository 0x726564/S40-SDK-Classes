package java.lang;

class FloatingDecimal {
   boolean isExceptional;
   boolean isNegative;
   int decExponent;
   char[] digits;
   int nDigits;
   int bigIntExp;
   int bigIntNBits;
   boolean mustSetRoundDir = false;
   int roundDir;
   static final long signMask = Long.MIN_VALUE;
   static final long expMask = 9218868437227405312L;
   static final long fractMask = 4503599627370495L;
   static final int expShift = 52;
   static final int expBias = 1023;
   static final long fractHOB = 4503599627370496L;
   static final long expOne = 4607182418800017408L;
   static final int maxSmallBinExp = 62;
   static final int minSmallBinExp = -21;
   static final int maxDecimalDigits = 15;
   static final int maxDecimalExponent = 308;
   static final int minDecimalExponent = -324;
   static final int bigDecimalExponent = 324;
   static final long highbyte = -72057594037927936L;
   static final long highbit = Long.MIN_VALUE;
   static final long lowbytes = 72057594037927935L;
   static final int singleSignMask = Integer.MIN_VALUE;
   static final int singleExpMask = 2139095040;
   static final int singleFractMask = 8388607;
   static final int singleExpShift = 23;
   static final int singleFractHOB = 8388608;
   static final int singleExpBias = 127;
   static final int singleMaxDecimalDigits = 7;
   static final int singleMaxDecimalExponent = 38;
   static final int singleMinDecimalExponent = -45;
   static final int intDecimalDigits = 9;
   private static FDBigInt[] b5p;
   private static final double[] small10pow = new double[]{1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 1.0E7D, 1.0E8D, 1.0E9D, 1.0E10D, 1.0E11D, 1.0E12D, 1.0E13D, 1.0E14D, 1.0E15D, 1.0E16D, 1.0E17D, 1.0E18D, 1.0E19D, 1.0E20D, 1.0E21D, 1.0E22D};
   private static final float[] singleSmall10pow = new float[]{1.0F, 10.0F, 100.0F, 1000.0F, 10000.0F, 100000.0F, 1000000.0F, 1.0E7F, 1.0E8F, 1.0E9F, 1.0E10F};
   private static final double[] big10pow = new double[]{1.0E16D, 1.0E32D, 1.0E64D, 1.0E128D, 1.0E256D};
   private static final double[] tiny10pow = new double[]{1.0E-16D, 1.0E-32D, 1.0E-64D, 1.0E-128D, 1.0E-256D};
   private static final int maxSmallTen;
   private static final int singleMaxSmallTen;
   private static final int[] small5pow;
   private static final long[] long5pow;
   private static final int[] n5bits;
   private static final char[] infinity;
   private static final char[] notANumber;
   private static final char[] zero;

   private FloatingDecimal(boolean var1, int var2, char[] var3, int var4, boolean var5) {
      this.isNegative = var1;
      this.isExceptional = var5;
      this.decExponent = var2;
      this.digits = var3;
      this.nDigits = var4;
   }

   private static int countBits(long var0) {
      if (var0 == 0L) {
         return 0;
      } else {
         while((var0 & -72057594037927936L) == 0L) {
            var0 <<= 8;
         }

         while(var0 > 0L) {
            var0 <<= 1;
         }

         int var2;
         for(var2 = 0; (var0 & 72057594037927935L) != 0L; var2 += 8) {
            var0 <<= 8;
         }

         while(var0 != 0L) {
            var0 <<= 1;
            ++var2;
         }

         return var2;
      }
   }

   private static synchronized FDBigInt big5pow(int var0) {
      if (var0 < 0) {
         throw new RuntimeException("Assertion botch: negative power of 5");
      } else {
         if (b5p == null) {
            b5p = new FDBigInt[var0 + 1];
         } else if (b5p.length <= var0) {
            FDBigInt[] var1 = new FDBigInt[var0 + 1];
            System.arraycopy(b5p, 0, var1, 0, b5p.length);
            b5p = var1;
         }

         if (b5p[var0] != null) {
            return b5p[var0];
         } else if (var0 < small5pow.length) {
            return b5p[var0] = new FDBigInt(small5pow[var0]);
         } else if (var0 < long5pow.length) {
            return b5p[var0] = new FDBigInt(long5pow[var0]);
         } else {
            int var5 = var0 >> 1;
            int var2 = var0 - var5;
            FDBigInt var3 = b5p[var5];
            if (var3 == null) {
               var3 = big5pow(var5);
            }

            if (var2 < small5pow.length) {
               return b5p[var0] = var3.mult(small5pow[var2]);
            } else {
               FDBigInt var4 = b5p[var2];
               if (var4 == null) {
                  var4 = big5pow(var2);
               }

               return b5p[var0] = var3.mult(var4);
            }
         }
      }
   }

   private static FDBigInt multPow52(FDBigInt var0, int var1, int var2) {
      if (var1 != 0) {
         if (var1 < small5pow.length) {
            var0 = var0.mult(small5pow[var1]);
         } else {
            var0 = var0.mult(big5pow(var1));
         }
      }

      if (var2 != 0) {
         var0.lshiftMe(var2);
      }

      return var0;
   }

   private static FDBigInt constructPow52(int var0, int var1) {
      FDBigInt var2 = new FDBigInt(big5pow(var0));
      if (var1 != 0) {
         var2.lshiftMe(var1);
      }

      return var2;
   }

   private FDBigInt doubleToBigInt(double var1) {
      long var3 = Double.doubleToLongBits(var1) & Long.MAX_VALUE;
      int var5 = (int)(var3 >>> 52);
      var3 &= 4503599627370495L;
      if (var5 > 0) {
         var3 |= 4503599627370496L;
      } else {
         if (var3 == 0L) {
            throw new RuntimeException("Assertion botch: doubleToBigInt(0.0)");
         }

         ++var5;

         while((var3 & 4503599627370496L) == 0L) {
            var3 <<= 1;
            --var5;
         }
      }

      var5 -= 1023;
      int var6 = countBits(var3);
      int var7 = 53 - var6;
      var3 >>>= var7;
      this.bigIntExp = var5 + 1 - var6;
      this.bigIntNBits = var6;
      return new FDBigInt(var3);
   }

   private static double ulp(double var0, boolean var2) {
      long var3 = Double.doubleToLongBits(var0) & Long.MAX_VALUE;
      int var5 = (int)(var3 >>> 52);
      if (var2 && var5 >= 52 && (var3 & 4503599627370495L) == 0L) {
         --var5;
      }

      double var6;
      if (var5 > 52) {
         var6 = Double.longBitsToDouble((long)(var5 - 52) << 52);
      } else if (var5 == 0) {
         var6 = Double.MIN_VALUE;
      } else {
         var6 = Double.longBitsToDouble(1L << var5 - 1);
      }

      if (var2) {
         var6 = -var6;
      }

      return var6;
   }

   float stickyRound(double var1) {
      long var3 = Double.doubleToLongBits(var1);
      long var5 = var3 & 9218868437227405312L;
      if (var5 != 0L && var5 != 9218868437227405312L) {
         var3 += (long)this.roundDir;
         return (float)Double.longBitsToDouble(var3);
      } else {
         return (float)var1;
      }
   }

   private void developLongDigits(int var1, long var2, long var4) {
      int var10;
      for(var10 = 0; var4 >= 10L; ++var10) {
         var4 /= 10L;
      }

      if (var10 != 0) {
         long var11 = long5pow[var10] << var10;
         long var13 = var2 % var11;
         var2 /= var11;
         var1 += var10;
         if (var13 >= var11 >> 1) {
            ++var2;
         }
      }

      char[] var6;
      byte var7;
      int var8;
      int var9;
      if (var2 <= 2147483647L) {
         if (var2 <= 0L) {
            throw new RuntimeException("Assertion botch: value " + var2 + " <= 0");
         }

         int var16 = (int)var2;
         var7 = 10;
         var6 = new char[10];
         var8 = var7 - 1;
         var9 = var16 % 10;

         for(var16 /= 10; var9 == 0; var16 /= 10) {
            ++var1;
            var9 = var16 % 10;
         }

         while(var16 != 0) {
            var6[var8--] = (char)(var9 + 48);
            ++var1;
            var9 = var16 % 10;
            var16 /= 10;
         }

         var6[var8] = (char)(var9 + 48);
      } else {
         var7 = 20;
         var6 = new char[20];
         var8 = var7 - 1;
         var9 = (int)(var2 % 10L);

         for(var2 /= 10L; var9 == 0; var2 /= 10L) {
            ++var1;
            var9 = (int)(var2 % 10L);
         }

         while(var2 != 0L) {
            var6[var8--] = (char)(var9 + 48);
            ++var1;
            var9 = (int)(var2 % 10L);
            var2 /= 10L;
         }

         var6[var8] = (char)(var9 + 48);
      }

      int var15 = var7 - var8;
      char[] var17;
      if (var8 == 0) {
         var17 = var6;
      } else {
         var17 = new char[var15];
         System.arraycopy(var6, var8, var17, 0, var15);
      }

      this.digits = var17;
      this.decExponent = var1 + 1;
      this.nDigits = var15;
   }

   private void roundup() {
      int var1;
      char var2 = this.digits[var1 = this.nDigits - 1];
      if (var2 == '9') {
         while(true) {
            if (var2 != '9' || var1 <= 0) {
               if (var2 == '9') {
                  ++this.decExponent;
                  this.digits[0] = '1';
                  return;
               }
               break;
            }

            this.digits[var1] = '0';
            --var1;
            var2 = this.digits[var1];
         }
      }

      this.digits[var1] = (char)(var2 + 1);
   }

   public FloatingDecimal(double var1) {
      long var3 = Double.doubleToLongBits(var1);
      if ((var3 & Long.MIN_VALUE) != 0L) {
         this.isNegative = true;
         var3 ^= Long.MIN_VALUE;
      } else {
         this.isNegative = false;
      }

      int var7 = (int)((var3 & 9218868437227405312L) >> 52);
      long var5 = var3 & 4503599627370495L;
      if (var7 == 2047) {
         this.isExceptional = true;
         if (var5 == 0L) {
            this.digits = infinity;
         } else {
            this.digits = notANumber;
            this.isNegative = false;
         }

         this.nDigits = this.digits.length;
      } else {
         this.isExceptional = false;
         int var8;
         if (var7 == 0) {
            if (var5 == 0L) {
               this.decExponent = 0;
               this.digits = zero;
               this.nDigits = 1;
               return;
            }

            while((var5 & 4503599627370496L) == 0L) {
               var5 <<= 1;
               --var7;
            }

            var8 = 52 + var7 + 1;
            ++var7;
         } else {
            var5 |= 4503599627370496L;
            var8 = 53;
         }

         var7 -= 1023;
         this.dtoa(var7, var5, var8);
      }
   }

   public FloatingDecimal(float var1) {
      int var2 = Float.floatToIntBits(var1);
      if ((var2 & Integer.MIN_VALUE) != 0) {
         this.isNegative = true;
         var2 ^= Integer.MIN_VALUE;
      } else {
         this.isNegative = false;
      }

      int var4 = (var2 & 2139095040) >> 23;
      int var3 = var2 & 8388607;
      if (var4 == 255) {
         this.isExceptional = true;
         if ((long)var3 == 0L) {
            this.digits = infinity;
         } else {
            this.digits = notANumber;
            this.isNegative = false;
         }

         this.nDigits = this.digits.length;
      } else {
         this.isExceptional = false;
         int var5;
         if (var4 == 0) {
            if (var3 == 0) {
               this.decExponent = 0;
               this.digits = zero;
               this.nDigits = 1;
               return;
            }

            while((var3 & 8388608) == 0) {
               var3 <<= 1;
               --var4;
            }

            var5 = 23 + var4 + 1;
            ++var4;
         } else {
            var3 |= 8388608;
            var5 = 24;
         }

         var4 -= 127;
         this.dtoa(var4, (long)var3 << 29, var5);
      }
   }

   private void dtoa(int var1, long var2, int var4) {
      int var5 = countBits(var2);
      int var6 = Math.max(0, var5 - var1 - 1);
      if (var1 <= 62 && var1 >= -21 && var6 < long5pow.length && var5 + n5bits[var6] < 64 && var6 == 0) {
         long var37;
         if (var1 > var4) {
            var37 = 1L << var1 - var4 - 1;
         } else {
            var37 = 0L;
         }

         if (var1 >= 52) {
            var2 <<= var1 - 52;
         } else {
            var2 >>>= 52 - var1;
         }

         this.developLongDigits(0, var2, var37);
      } else {
         double var8 = Double.longBitsToDouble(4607182418800017408L | var2 & -4503599627370497L);
         int var7 = (int)Math.floor((var8 - 1.5D) * 0.289529654D + 0.176091259D + (double)var1 * 0.301029995663981D);
         int var11 = Math.max(0, -var7);
         int var10 = var11 + var6 + var1;
         int var13 = Math.max(0, var7);
         int var12 = var13 + var6;
         int var14 = var10 - var4;
         var2 >>>= 53 - var5;
         var10 -= var5 - 1;
         int var21 = Math.min(var10, var12);
         var10 -= var21;
         var12 -= var21;
         var14 -= var21;
         if (var5 == 1) {
            --var14;
         }

         if (var14 < 0) {
            var10 -= var14;
            var12 -= var14;
            var14 = 0;
         }

         char[] var22 = this.digits = new char[18];
         boolean var23 = false;
         int var16 = var5 + var10 + (var11 < n5bits.length ? n5bits[var11] : var11 * 3);
         int var17 = var12 + 1 + (var13 + 1 < n5bits.length ? n5bits[var13 + 1] : (var13 + 1) * 3);
         boolean var24;
         boolean var25;
         long var26;
         int var28;
         int var30;
         int var38;
         if (var16 < 64 && var17 < 64) {
            if (var16 < 32 && var17 < 32) {
               int var40 = (int)var2 * small5pow[var11] << var10;
               var30 = small5pow[var13] << var12;
               int var41 = small5pow[var11] << var14;
               int var32 = var30 * 10;
               var38 = 0;
               var28 = var40 / var30;
               var40 = 10 * (var40 % var30);
               var41 *= 10;
               var24 = var40 < var41;
               var25 = var40 + var41 > var32;
               if (var28 >= 10) {
                  throw new RuntimeException("Assertion botch: excessivly large digit " + var28);
               }

               if (var28 == 0 && !var25) {
                  --var7;
               } else {
                  var22[var38++] = (char)(48 + var28);
               }

               if (var7 <= -3 || var7 >= 8) {
                  var24 = false;
                  var25 = false;
               }

               for(; !var24 && !var25; var22[var38++] = (char)(48 + var28)) {
                  var28 = var40 / var30;
                  var40 = 10 * (var40 % var30);
                  var41 *= 10;
                  if (var28 >= 10) {
                     throw new RuntimeException("Assertion botch: excessivly large digit " + var28);
                  }

                  if ((long)var41 > 0L) {
                     var24 = var40 < var41;
                     var25 = var40 + var41 > var32;
                  } else {
                     var24 = true;
                     var25 = true;
                  }
               }

               var26 = (long)((var40 << 1) - var32);
            } else {
               long var39 = var2 * long5pow[var11] << var10;
               long var31 = long5pow[var13] << var12;
               long var33 = long5pow[var11] << var14;
               long var35 = var31 * 10L;
               var38 = 0;
               var28 = (int)(var39 / var31);
               var39 = 10L * (var39 % var31);
               var33 *= 10L;
               var24 = var39 < var33;
               var25 = var39 + var33 > var35;
               if (var28 >= 10) {
                  throw new RuntimeException("Assertion botch: excessivly large digit " + var28);
               }

               if (var28 == 0 && !var25) {
                  --var7;
               } else {
                  var22[var38++] = (char)(48 + var28);
               }

               if (var7 <= -3 || var7 >= 8) {
                  var24 = false;
                  var25 = false;
               }

               for(; !var24 && !var25; var22[var38++] = (char)(48 + var28)) {
                  var28 = (int)(var39 / var31);
                  var39 = 10L * (var39 % var31);
                  var33 *= 10L;
                  if (var28 >= 10) {
                     throw new RuntimeException("Assertion botch: excessivly large digit " + var28);
                  }

                  if (var33 > 0L) {
                     var24 = var39 < var33;
                     var25 = var39 + var33 > var35;
                  } else {
                     var24 = true;
                     var25 = true;
                  }
               }

               var26 = (var39 << 1) - var35;
            }
         } else {
            FDBigInt var19 = multPow52(new FDBigInt(var2), var11, var10);
            FDBigInt var18 = constructPow52(var13, var12);
            FDBigInt var20 = constructPow52(var11, var14);
            var19.lshiftMe(var30 = var18.normalizeMe());
            var20.lshiftMe(var30);
            FDBigInt var29 = var18.mult(10);
            var38 = 0;
            var28 = var19.quoRemIteration(var18);
            var20 = var20.mult(10);
            var24 = var19.cmp(var20) < 0;
            var25 = var19.add(var20).cmp(var29) > 0;
            if (var28 >= 10) {
               throw new RuntimeException("Assertion botch: excessivly large digit " + var28);
            }

            if (var28 == 0 && !var25) {
               --var7;
            } else {
               var22[var38++] = (char)(48 + var28);
            }

            if (var7 <= -3 || var7 >= 8) {
               var24 = false;
               var25 = false;
            }

            while(!var24 && !var25) {
               var28 = var19.quoRemIteration(var18);
               var20 = var20.mult(10);
               if (var28 >= 10) {
                  throw new RuntimeException("Assertion botch: excessivly large digit " + var28);
               }

               var24 = var19.cmp(var20) < 0;
               var25 = var19.add(var20).cmp(var29) > 0;
               var22[var38++] = (char)(48 + var28);
            }

            if (var25 && var24) {
               var19.lshiftMe(1);
               var26 = (long)var19.cmp(var29);
            } else {
               var26 = 0L;
            }
         }

         this.decExponent = var7 + 1;
         this.digits = var22;
         this.nDigits = var38;
         if (var25) {
            if (var24) {
               if (var26 == 0L) {
                  if ((var22[this.nDigits - 1] & 1) != 0) {
                     this.roundup();
                  }
               } else if (var26 > 0L) {
                  this.roundup();
               }
            } else {
               this.roundup();
            }
         }

      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer(this.nDigits + 8);
      if (this.isNegative) {
         var1.append('-');
      }

      if (this.isExceptional) {
         var1.append(this.digits, 0, this.nDigits);
      } else {
         var1.append("0.");
         var1.append(this.digits, 0, this.nDigits);
         var1.append('e');
         var1.append(this.decExponent);
      }

      return new String(var1);
   }

   public String toJavaFormatString() {
      char[] var1 = new char[this.nDigits + 10];
      byte var2 = 0;
      if (this.isNegative) {
         var1[0] = '-';
         var2 = 1;
      }

      int var5;
      if (this.isExceptional) {
         System.arraycopy(this.digits, 0, var1, var2, this.nDigits);
         var5 = var2 + this.nDigits;
      } else {
         int var3;
         if (this.decExponent > 0 && this.decExponent < 8) {
            var3 = Math.min(this.nDigits, this.decExponent);
            System.arraycopy(this.digits, 0, var1, var2, var3);
            var5 = var2 + var3;
            if (var3 < this.decExponent) {
               var3 = this.decExponent - var3;
               System.arraycopy(zero, 0, var1, var5, var3);
               var5 += var3;
               var1[var5++] = '.';
               var1[var5++] = '0';
            } else {
               var1[var5++] = '.';
               if (var3 < this.nDigits) {
                  int var4 = this.nDigits - var3;
                  System.arraycopy(this.digits, var3, var1, var5, var4);
                  var5 += var4;
               } else {
                  var1[var5++] = '0';
               }
            }
         } else if (this.decExponent <= 0 && this.decExponent > -3) {
            var5 = var2 + 1;
            var1[var2] = '0';
            var1[var5++] = '.';
            if (this.decExponent != 0) {
               System.arraycopy(zero, 0, var1, var5, -this.decExponent);
               var5 -= this.decExponent;
            }

            System.arraycopy(this.digits, 0, var1, var5, this.nDigits);
            var5 += this.nDigits;
         } else {
            var5 = var2 + 1;
            var1[var2] = this.digits[0];
            var1[var5++] = '.';
            if (this.nDigits > 1) {
               System.arraycopy(this.digits, 1, var1, var5, this.nDigits - 1);
               var5 += this.nDigits - 1;
            } else {
               var1[var5++] = '0';
            }

            var1[var5++] = 'E';
            if (this.decExponent <= 0) {
               var1[var5++] = '-';
               var3 = -this.decExponent + 1;
            } else {
               var3 = this.decExponent - 1;
            }

            if (var3 <= 9) {
               var1[var5++] = (char)(var3 + 48);
            } else if (var3 <= 99) {
               var1[var5++] = (char)(var3 / 10 + 48);
               var1[var5++] = (char)(var3 % 10 + 48);
            } else {
               var1[var5++] = (char)(var3 / 100 + 48);
               var3 %= 100;
               var1[var5++] = (char)(var3 / 10 + 48);
               var1[var5++] = (char)(var3 % 10 + 48);
            }
         }
      }

      return new String(var1, 0, var5);
   }

   public static FloatingDecimal readJavaFormatString(String var0) throws NumberFormatException {
      boolean var1 = false;
      boolean var2 = false;

      try {
         var0 = var0.trim();
         int var5 = var0.length();
         if (var5 == 0) {
            throw new NumberFormatException("empty String");
         }

         int var6 = 0;
         char var4;
         char[] var7;
         int var8;
         boolean var9;
         int var10;
         int var11;
         int var12;
         switch(var4 = var0.charAt(var6)) {
         case '-':
            var1 = true;
         case '+':
            ++var6;
            var2 = true;
         default:
            var7 = new char[var5];
            var8 = 0;
            var9 = false;
            var10 = 0;
            var11 = 0;
            var12 = 0;
         }

         label94:
         for(; var6 < var5; ++var6) {
            switch(var4 = var0.charAt(var6)) {
            case '.':
               if (var9) {
                  throw new NumberFormatException("multiple points");
               }

               var10 = var6;
               if (var2) {
                  var10 = var6 - 1;
               }

               var9 = true;
               continue;
            case '/':
            default:
               break label94;
            case '0':
               if (var8 > 0) {
                  ++var12;
               } else {
                  ++var11;
               }
               continue;
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            }

            while(var12 > 0) {
               var7[var8++] = '0';
               --var12;
            }

            var7[var8++] = var4;
         }

         if (var8 == 0) {
            var7 = zero;
            var8 = 1;
            if (var11 == 0) {
               throw new NumberFormatException(var0);
            }
         }

         int var3;
         if (var9) {
            var3 = var10 - var11;
         } else {
            var3 = var8 + var12;
         }

         if (var6 < var5 && (var4 = var0.charAt(var6)) == 'e' || var4 == 'E') {
            byte var13 = 1;
            int var14 = 0;
            int var15 = 214748364;
            boolean var16 = false;
            ++var6;
            int var17;
            switch(var0.charAt(var6)) {
            case '-':
               var13 = -1;
            case '+':
               ++var6;
            default:
               var17 = var6;
            }

            label119:
            while(var6 < var5) {
               if (var14 >= var15) {
                  var16 = true;
               }

               switch(var4 = var0.charAt(var6++)) {
               case '0':
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
                  var14 = var14 * 10 + (var4 - 48);
                  break;
               default:
                  --var6;
                  break label119;
               }
            }

            int var18 = 324 + var8 + var12;
            if (!var16 && var14 <= var18) {
               var3 += var13 * var14;
            } else {
               var3 = var13 * var18;
            }

            if (var6 == var17) {
               throw new NumberFormatException(var0);
            }
         }

         if (var6 >= var5 || var6 == var5 - 1 && (var0.charAt(var6) == 'f' || var0.charAt(var6) == 'F' || var0.charAt(var6) == 'd' || var0.charAt(var6) == 'D')) {
            return new FloatingDecimal(var1, var3, var7, var8, false);
         }
      } catch (StringIndexOutOfBoundsException var19) {
      }

      throw new NumberFormatException(var0);
   }

   public double doubleValue() {
      int var1 = Math.min(this.nDigits, 16);
      this.roundDir = 0;
      int var10 = this.digits[0] - 48;
      int var11 = Math.min(var1, 9);

      int var12;
      for(var12 = 1; var12 < var11; ++var12) {
         var10 = var10 * 10 + this.digits[var12] - 48;
      }

      long var2 = (long)var10;

      for(var12 = var11; var12 < var1; ++var12) {
         var2 = var2 * 10L + (long)(this.digits[var12] - 48);
      }

      double var4 = (double)var2;
      var12 = this.decExponent - var1;
      int var13;
      if (this.nDigits <= 15) {
         if (var12 == 0 || var4 == 0.0D) {
            return this.isNegative ? -var4 : var4;
         }

         double var6;
         double var8;
         if (var12 >= 0) {
            if (var12 <= maxSmallTen) {
               var6 = var4 * small10pow[var12];
               if (this.mustSetRoundDir) {
                  var8 = var6 / small10pow[var12];
                  this.roundDir = var8 == var4 ? 0 : (var8 < var4 ? 1 : -1);
               }

               return this.isNegative ? -var6 : var6;
            }

            var13 = 15 - var1;
            if (var12 <= maxSmallTen + var13) {
               var4 *= small10pow[var13];
               var6 = var4 * small10pow[var12 - var13];
               if (this.mustSetRoundDir) {
                  var8 = var6 / small10pow[var12 - var13];
                  this.roundDir = var8 == var4 ? 0 : (var8 < var4 ? 1 : -1);
               }

               return this.isNegative ? -var6 : var6;
            }
         } else if (var12 >= -maxSmallTen) {
            var6 = var4 / small10pow[-var12];
            var8 = var6 * small10pow[-var12];
            if (this.mustSetRoundDir) {
               this.roundDir = var8 == var4 ? 0 : (var8 < var4 ? 1 : -1);
            }

            return this.isNegative ? -var6 : var6;
         }
      }

      double var14;
      if (var12 > 0) {
         if (this.decExponent > 309) {
            return this.isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
         }

         if ((var12 & 15) != 0) {
            var4 *= small10pow[var12 & 15];
         }

         if ((var12 >>= 4) != 0) {
            for(var13 = 0; var12 > 1; var12 >>= 1) {
               if ((var12 & 1) != 0) {
                  var4 *= big10pow[var13];
               }

               ++var13;
            }

            var14 = var4 * big10pow[var13];
            if (Double.isInfinite(var14)) {
               var14 = var4 / 2.0D;
               var14 *= big10pow[var13];
               if (Double.isInfinite(var14)) {
                  return this.isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
               }

               var14 = Double.MAX_VALUE;
            }

            var4 = var14;
         }
      } else if (var12 < 0) {
         var12 = -var12;
         if (this.decExponent < -325) {
            return this.isNegative ? -0.0D : 0.0D;
         }

         if ((var12 & 15) != 0) {
            var4 /= small10pow[var12 & 15];
         }

         if ((var12 >>= 4) != 0) {
            for(var13 = 0; var12 > 1; var12 >>= 1) {
               if ((var12 & 1) != 0) {
                  var4 *= tiny10pow[var13];
               }

               ++var13;
            }

            var14 = var4 * tiny10pow[var13];
            if (var14 == 0.0D) {
               var14 = var4 * 2.0D;
               var14 *= tiny10pow[var13];
               if (var14 == 0.0D) {
                  return this.isNegative ? -0.0D : 0.0D;
               }

               var14 = Double.MIN_VALUE;
            }

            var4 = var14;
         }
      }

      FDBigInt var27 = new FDBigInt(var2, this.digits, var1, this.nDigits);
      var12 = this.decExponent - this.nDigits;

      do {
         FDBigInt var28 = this.doubleToBigInt(var4);
         int var15;
         int var16;
         int var17;
         int var18;
         if (var12 >= 0) {
            var16 = 0;
            var15 = 0;
            var18 = var12;
            var17 = var12;
         } else {
            var15 = var16 = -var12;
            var18 = 0;
            var17 = 0;
         }

         if (this.bigIntExp >= 0) {
            var15 += this.bigIntExp;
         } else {
            var17 -= this.bigIntExp;
         }

         int var19 = var15;
         int var20;
         if (this.bigIntExp + this.bigIntNBits <= -1022) {
            var20 = this.bigIntExp + 1023 + 52;
         } else {
            var20 = 54 - this.bigIntNBits;
         }

         var15 += var20;
         var17 += var20;
         int var21 = Math.min(var15, Math.min(var17, var19));
         var15 -= var21;
         var17 -= var21;
         var19 -= var21;
         var28 = multPow52(var28, var16, var15);
         FDBigInt var22 = multPow52(new FDBigInt(var27), var18, var17);
         FDBigInt var23;
         int var24;
         boolean var25;
         if ((var24 = var28.cmp(var22)) > 0) {
            var25 = true;
            var23 = var28.sub(var22);
            if (this.bigIntNBits == 1 && this.bigIntExp > -1023) {
               --var19;
               if (var19 < 0) {
                  var19 = 0;
                  var23.lshiftMe(1);
               }
            }
         } else {
            if (var24 >= 0) {
               break;
            }

            var25 = false;
            var23 = var22.sub(var28);
         }

         FDBigInt var26 = constructPow52(var16, var19);
         if ((var24 = var23.cmp(var26)) < 0) {
            this.roundDir = var25 ? -1 : 1;
            break;
         }

         if (var24 == 0) {
            var4 += 0.5D * ulp(var4, var25);
            this.roundDir = var25 ? -1 : 1;
            break;
         }

         var4 += ulp(var4, var25);
      } while(var4 != 0.0D && var4 != Double.POSITIVE_INFINITY);

      return this.isNegative ? -var4 : var4;
   }

   public float floatValue() {
      int var1 = Math.min(this.nDigits, 8);
      int var2 = this.digits[0] - 48;

      int var4;
      for(var4 = 1; var4 < var1; ++var4) {
         var2 = var2 * 10 + this.digits[var4] - 48;
      }

      float var3 = (float)var2;
      var4 = this.decExponent - var1;
      if (this.nDigits <= 7) {
         if (var4 == 0 || var3 == 0.0F) {
            return this.isNegative ? -var3 : var3;
         }

         if (var4 >= 0) {
            if (var4 <= singleMaxSmallTen) {
               var3 *= singleSmall10pow[var4];
               return this.isNegative ? -var3 : var3;
            }

            int var9 = 7 - var1;
            if (var4 <= singleMaxSmallTen + var9) {
               var3 *= singleSmall10pow[var9];
               var3 *= singleSmall10pow[var4 - var9];
               return this.isNegative ? -var3 : var3;
            }
         } else if (var4 >= -singleMaxSmallTen) {
            var3 /= singleSmall10pow[-var4];
            return this.isNegative ? -var3 : var3;
         }
      } else if (this.decExponent >= this.nDigits && this.nDigits + this.decExponent <= 15) {
         long var5 = (long)var2;

         for(int var7 = var1; var7 < this.nDigits; ++var7) {
            var5 = var5 * 10L + (long)(this.digits[var7] - 48);
         }

         double var11 = (double)var5;
         var4 = this.decExponent - this.nDigits;
         var11 *= small10pow[var4];
         var3 = (float)var11;
         return this.isNegative ? -var3 : var3;
      }

      if (this.decExponent > 39) {
         return this.isNegative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
      } else if (this.decExponent < -46) {
         return this.isNegative ? -0.0F : 0.0F;
      } else {
         this.mustSetRoundDir = true;
         double var10 = this.doubleValue();
         return this.stickyRound(var10);
      }
   }

   static {
      maxSmallTen = small10pow.length - 1;
      singleMaxSmallTen = singleSmall10pow.length - 1;
      small5pow = new int[]{1, 5, 25, 125, 625, 3125, 15625, 78125, 390625, 1953125, 9765625, 48828125, 244140625, 1220703125};
      long5pow = new long[]{1L, 5L, 25L, 125L, 625L, 3125L, 15625L, 78125L, 390625L, 1953125L, 9765625L, 48828125L, 244140625L, 1220703125L, 6103515625L, 30517578125L, 152587890625L, 762939453125L, 3814697265625L, 19073486328125L, 95367431640625L, 476837158203125L, 2384185791015625L, 11920928955078125L, 59604644775390625L, 298023223876953125L, 1490116119384765625L};
      n5bits = new int[]{0, 3, 5, 7, 10, 12, 14, 17, 19, 21, 24, 26, 28, 31, 33, 35, 38, 40, 42, 45, 47, 49, 52, 54, 56, 59, 61};
      infinity = new char[]{'I', 'n', 'f', 'i', 'n', 'i', 't', 'y'};
      notANumber = new char[]{'N', 'a', 'N'};
      zero = new char[]{'0', '0', '0', '0', '0', '0', '0', '0'};
   }
}
