package java.lang;

class FloatingDecimal {
   private boolean isExceptional;
   private boolean isNegative;
   private int decExponent;
   private char[] digits;
   private int nDigits;
   private int bigIntExp;
   private int bigIntNBits;
   private boolean mustSetRoundDir = false;
   private int roundDir;
   private static FDBigInt[] fx;
   private static final double[] fy = new double[]{1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 1.0E7D, 1.0E8D, 1.0E9D, 1.0E10D, 1.0E11D, 1.0E12D, 1.0E13D, 1.0E14D, 1.0E15D, 1.0E16D, 1.0E17D, 1.0E18D, 1.0E19D, 1.0E20D, 1.0E21D, 1.0E22D};
   private static final float[] fz = new float[]{1.0F, 10.0F, 100.0F, 1000.0F, 10000.0F, 100000.0F, 1000000.0F, 1.0E7F, 1.0E8F, 1.0E9F, 1.0E10F};
   private static final double[] fA = new double[]{1.0E16D, 1.0E32D, 1.0E64D, 1.0E128D, 1.0E256D};
   private static final double[] fB = new double[]{1.0E-16D, 1.0E-32D, 1.0E-64D, 1.0E-128D, 1.0E-256D};
   private static final int fC;
   private static final int fD;
   private static final int[] fE;
   private static final long[] fF;
   private static final int[] fG;
   private static final char[] fH;
   private static final char[] fI;
   private static final char[] fJ;

   private FloatingDecimal(boolean var1, int var2, char[] var3, int var4, boolean var5) {
      this.isNegative = var1;
      this.isExceptional = false;
      this.decExponent = var2;
      this.digits = var3;
      this.nDigits = var4;
   }

   private static int b(long var0) {
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

   private static synchronized FDBigInt k(int var0) {
      if (var0 < 0) {
         throw new RuntimeException("Assertion botch: negative power of 5");
      } else {
         if (fx == null) {
            fx = new FDBigInt[var0 + 1];
         } else if (fx.length <= var0) {
            FDBigInt[] var1 = new FDBigInt[var0 + 1];
            System.arraycopy(fx, 0, var1, 0, fx.length);
            fx = var1;
         }

         if (fx[var0] != null) {
            return fx[var0];
         } else if (var0 < fE.length) {
            return fx[var0] = new FDBigInt(fE[var0]);
         } else if (var0 < fF.length) {
            return fx[var0] = new FDBigInt(fF[var0]);
         } else {
            int var4 = var0 >> 1;
            int var2 = var0 - var4;
            FDBigInt var3;
            if ((var3 = fx[var4]) == null) {
               var3 = k(var4);
            }

            if (var2 < fE.length) {
               return fx[var0] = var3.l(fE[var2]);
            } else {
               FDBigInt var5;
               if ((var5 = fx[var2]) == null) {
                  var5 = k(var2);
               }

               return fx[var0] = var3.a(var5);
            }
         }
      }
   }

   private static FDBigInt a(FDBigInt var0, int var1, int var2) {
      if (var1 != 0) {
         if (var1 < fE.length) {
            var0 = var0.l(fE[var1]);
         } else {
            var0 = var0.a(k(var1));
         }
      }

      if (var2 != 0) {
         var0.lshiftMe(var2);
      }

      return var0;
   }

   private static FDBigInt g(int var0, int var1) {
      FDBigInt var2 = new FDBigInt(k(var0));
      if (var1 != 0) {
         var2.lshiftMe(var1);
      }

      return var2;
   }

   private static double a(double var0, boolean var2) {
      long var3;
      int var8 = (int)((var3 = Double.doubleToLongBits(var0) & Long.MAX_VALUE) >>> 52);
      if (var2 && var8 >= 52 && (var3 & 4503599627370495L) == 0L) {
         --var8;
      }

      double var6;
      if (var8 > 52) {
         var6 = Double.longBitsToDouble((long)(var8 - 52) << 52);
      } else if (var8 == 0) {
         var6 = Double.MIN_VALUE;
      } else {
         var6 = Double.longBitsToDouble(1L << var8 - 1);
      }

      if (var2) {
         var6 = -var6;
      }

      return var6;
   }

   private void P() {
      int var1;
      char var2;
      if ((var2 = this.digits[var1 = this.nDigits - 1]) == '9') {
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
      long var3;
      if (((var3 = Double.doubleToLongBits(var1)) & Long.MIN_VALUE) != 0L) {
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
            this.digits = fH;
         } else {
            this.digits = fI;
            this.isNegative = false;
         }

         this.nDigits = this.digits.length;
      } else {
         this.isExceptional = false;
         int var2;
         if (var7 == 0) {
            if (var5 == 0L) {
               this.decExponent = 0;
               this.digits = fJ;
               this.nDigits = 1;
               return;
            }

            while((var5 & 4503599627370496L) == 0L) {
               var5 <<= 1;
               --var7;
            }

            var2 = 52 + var7 + 1;
            ++var7;
         } else {
            var5 |= 4503599627370496L;
            var2 = 53;
         }

         var7 -= 1023;
         this.a(var7, var5, var2);
      }
   }

   public FloatingDecimal(float var1) {
      int var4;
      if (((var4 = Float.floatToIntBits(var1)) & Integer.MIN_VALUE) != 0) {
         this.isNegative = true;
         var4 ^= Integer.MIN_VALUE;
      } else {
         this.isNegative = false;
      }

      int var2 = (var4 & 2139095040) >> 23;
      var4 &= 8388607;
      if (var2 == 255) {
         this.isExceptional = true;
         if ((long)var4 == 0L) {
            this.digits = fH;
         } else {
            this.digits = fI;
            this.isNegative = false;
         }

         this.nDigits = this.digits.length;
      } else {
         this.isExceptional = false;
         int var3;
         if (var2 == 0) {
            if (var4 == 0) {
               this.decExponent = 0;
               this.digits = fJ;
               this.nDigits = 1;
               return;
            }

            while((var4 & 8388608) == 0) {
               var4 <<= 1;
               --var2;
            }

            var3 = 23 + var2 + 1;
            ++var2;
         } else {
            var4 |= 8388608;
            var3 = 24;
         }

         var2 -= 127;
         this.a(var2, (long)var4 << 29, var3);
      }
   }

   private void a(int var1, long var2, int var4) {
      int var5 = b(var2);
      int var6 = Math.max(0, var5 - var1 - 1);
      int var7;
      int var9;
      int var11;
      if (var1 <= 62 && var1 >= -21 && var6 < fF.length && var5 + fG[var6] < 64 && var6 == 0) {
         long var56;
         if (var1 > var4) {
            var56 = 1L << var1 - var4 - 1;
         } else {
            var56 = 0L;
         }

         if (var1 >= 52) {
            var2 <<= var1 - 52;
         } else {
            var2 >>>= 52 - var1;
         }

         long var65 = var56;
         long var64 = var2;
         var9 = 0;

         for(var7 = 0; var65 >= 10L; ++var7) {
            var65 /= 10L;
         }

         if (var7 != 0) {
            long var48 = fF[var7] << var7;
            long var50 = var2 % var48;
            var64 = var2 / var48;
            var9 = 0 + var7;
            if (var50 >= var48 >> 1) {
               ++var64;
            }
         }

         byte var10;
         char[] var57;
         int var60;
         if (var64 <= 2147483647L) {
            if (var64 <= 0L) {
               throw new RuntimeException("Assertion botch: value " + var64 + " <= 0");
            }

            int var66 = (int)var64;
            var10 = 10;
            var57 = new char[10];
            var11 = 9;
            var60 = var66 % 10;

            for(var66 /= 10; var60 == 0; var66 /= 10) {
               ++var9;
               var60 = var66 % 10;
            }

            while(var66 != 0) {
               var57[var11--] = (char)(var60 + 48);
               ++var9;
               var60 = var66 % 10;
               var66 /= 10;
            }

            var57[var11] = (char)(var60 + 48);
         } else {
            var10 = 20;
            var57 = new char[20];
            var11 = 19;
            var60 = (int)(var64 % 10L);

            for(var64 /= 10L; var60 == 0; var64 /= 10L) {
               ++var9;
               var60 = (int)(var64 % 10L);
            }

            while(var64 != 0L) {
               var57[var11--] = (char)(var60 + 48);
               ++var9;
               var60 = (int)(var64 % 10L);
               var64 /= 10L;
            }

            var57[var11] = (char)(var60 + 48);
         }

         int var58 = var10 - var11;
         char[] var67;
         if (var11 == 0) {
            var67 = var57;
         } else {
            var67 = new char[var58];
            System.arraycopy(var57, var11, var67, 0, var58);
         }

         this.digits = var67;
         this.decExponent = var9 + 1;
         this.nDigits = var58;
      } else {
         var7 = (int)Math.floor((Double.longBitsToDouble(4607182418800017408L | var2 & -4503599627370497L) - 1.5D) * 0.289529654D + 0.176091259D + (double)var1 * 0.301029995663981D);
         int var8;
         var1 += (var8 = Math.max(0, -var7)) + var6;
         var6 += var9 = Math.max(0, var7);
         var4 = var1 - var4;
         var2 >>>= 53 - var5;
         var11 = Math.min(var1 -= var5 - 1, var6);
         var1 -= var11;
         var6 -= var11;
         var4 -= var11;
         if (var5 == 1) {
            --var4;
         }

         if (var4 < 0) {
            var1 -= var4;
            var6 -= var4;
            var4 = 0;
         }

         char[] var12 = this.digits = new char[18];
         boolean var59 = false;
         var5 = var5 + var1 + (var8 < fG.length ? fG[var8] : var8 * 3);
         var11 = var6 + 1 + (var9 + 1 < fG.length ? fG[var9 + 1] : (var9 + 1) * 3);
         boolean var13;
         int var14;
         long var26;
         boolean var54;
         if (var5 < 64 && var11 < 64) {
            if (var5 < 32 && var11 < 32) {
               int var62 = (int)var2 * fE[var8] << var1;
               int var30 = fE[var9] << var6;
               int var63 = fE[var8] << var4;
               int var32 = var30 * 10;
               var11 = 0;
               var14 = var62 / var30;
               var62 = 10 * (var62 % var30);
               var63 *= 10;
               var54 = var62 < var63;
               var13 = var62 + var63 > var32;
               if (var14 >= 10) {
                  throw new RuntimeException("Assertion botch: excessivly large digit " + var14);
               }

               if (var14 == 0 && !var13) {
                  --var7;
               } else {
                  ++var11;
                  var12[0] = (char)(48 + var14);
               }

               if (var7 <= -3 || var7 >= 8) {
                  var54 = false;
                  var13 = false;
               }

               for(; !var54 && !var13; var12[var11++] = (char)(48 + var14)) {
                  var14 = var62 / var30;
                  var62 = 10 * (var62 % var30);
                  var63 *= 10;
                  if (var14 >= 10) {
                     throw new RuntimeException("Assertion botch: excessivly large digit " + var14);
                  }

                  if ((long)var63 > 0L) {
                     var54 = var62 < var63;
                     var13 = var62 + var63 > var32;
                  } else {
                     var54 = true;
                     var13 = true;
                  }
               }

               var26 = (long)((var62 << 1) - var32);
            } else {
               long var61 = var2 * fF[var8] << var1;
               long var31 = fF[var9] << var6;
               long var33 = fF[var8] << var4;
               long var35 = var31 * 10L;
               var11 = 0;
               var14 = (int)(var61 / var31);
               var61 = 10L * (var61 % var31);
               var33 *= 10L;
               var54 = var61 < var33;
               var13 = var61 + var33 > var35;
               if (var14 >= 10) {
                  throw new RuntimeException("Assertion botch: excessivly large digit " + var14);
               }

               if (var14 == 0 && !var13) {
                  --var7;
               } else {
                  ++var11;
                  var12[0] = (char)(48 + var14);
               }

               if (var7 <= -3 || var7 >= 8) {
                  var54 = false;
                  var13 = false;
               }

               for(; !var54 && !var13; var12[var11++] = (char)(48 + var14)) {
                  var14 = (int)(var61 / var31);
                  var61 = 10L * (var61 % var31);
                  var33 *= 10L;
                  if (var14 >= 10) {
                     throw new RuntimeException("Assertion botch: excessivly large digit " + var14);
                  }

                  if (var33 > 0L) {
                     var54 = var61 < var33;
                     var13 = var61 + var33 > var35;
                  } else {
                     var54 = true;
                     var13 = true;
                  }
               }

               var26 = (var61 << 1) - var35;
            }
         } else {
            FDBigInt var52 = a(new FDBigInt(var2), var8, var1);
            FDBigInt var53 = g(var9, var6);
            FDBigInt var3 = g(var8, var4);
            FDBigInt var55 = var53;
            int var39 = 0;
            int var40 = 0;
            int var41 = 0;

            for(var9 = var53.nWords - 1; var9 >= 0 && (var41 = var55.data[var9]) == 0; --var9) {
               ++var39;
            }

            if (var9 < 0) {
               throw new IllegalArgumentException("zero value");
            }

            var55.nWords -= var39;
            if ((var41 & -268435456) != 0) {
               for(var40 = 32; (var41 & -268435456) != 0; --var40) {
                  var41 >>>= 1;
               }
            } else {
               while(var41 <= 1048575) {
                  var41 <<= 8;
                  var40 += 8;
               }

               while(var41 <= 134217727) {
                  var41 <<= 1;
                  ++var40;
               }
            }

            if (var40 != 0) {
               var55.lshiftMe(var40);
            }

            var52.lshiftMe(var40);
            var3.lshiftMe(var40);
            FDBigInt var29 = var53.l(10);
            var11 = 0;
            var14 = var52.e(var53);
            var3 = var3.l(10);
            var54 = var52.d(var3) < 0;
            var13 = var52.b(var3).d(var29) > 0;
            if (var14 >= 10) {
               throw new RuntimeException("Assertion botch: excessivly large digit " + var14);
            }

            if (var14 == 0 && !var13) {
               --var7;
            } else {
               ++var11;
               var12[0] = (char)(48 + var14);
            }

            if (var7 <= -3 || var7 >= 8) {
               var54 = false;
               var13 = false;
            }

            while(!var54 && !var13) {
               var14 = var52.e(var53);
               var3 = var3.l(10);
               if (var14 >= 10) {
                  throw new RuntimeException("Assertion botch: excessivly large digit " + var14);
               }

               var54 = var52.d(var3) < 0;
               var13 = var52.b(var3).d(var29) > 0;
               var12[var11++] = (char)(48 + var14);
            }

            if (var13 && var54) {
               var52.lshiftMe(1);
               var26 = (long)var52.d(var29);
            } else {
               var26 = 0L;
            }
         }

         this.decExponent = var7 + 1;
         this.digits = var12;
         this.nDigits = var11;
         if (var13) {
            if (var54) {
               if (var26 == 0L) {
                  if ((var12[this.nDigits - 1] & 1) == 0) {
                     return;
                  }
               } else if (var26 <= 0L) {
                  return;
               }
            }

            this.P();
         }

      }
   }

   public final String toString() {
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

   public final String toJavaFormatString() {
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
               System.arraycopy(fJ, 0, var1, var5, var3);
               var5 += var3;
               var1[var5++] = '.';
            } else {
               var1[var5++] = '.';
               if (var3 < this.nDigits) {
                  int var4 = this.nDigits - var3;
                  System.arraycopy(this.digits, var3, var1, var5, var4);
                  var5 += var4;
                  return new String(var1, 0, var5);
               }
            }

            var1[var5++] = '0';
         } else if (this.decExponent <= 0 && this.decExponent > -3) {
            var5 = var2 + 1;
            var1[var2] = '0';
            var1[var5++] = '.';
            if (this.decExponent != 0) {
               System.arraycopy(fJ, 0, var1, var5, -this.decExponent);
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

   public static FloatingDecimal K(String var0) throws NumberFormatException {
      boolean var1 = false;
      boolean var2 = false;

      try {
         int var4;
         if ((var4 = (var0 = var0.trim()).length()) == 0) {
            throw new NumberFormatException("empty String");
         }

         int var5 = 0;
         char var3;
         char[] var6;
         int var7;
         boolean var8;
         int var9;
         int var10;
         int var11;
         switch(var3 = var0.charAt(0)) {
         case '-':
            var1 = true;
         case '+':
            ++var5;
            var2 = true;
         default:
            var6 = new char[var4];
            var7 = 0;
            var8 = false;
            var9 = 0;
            var10 = 0;
            var11 = 0;
         }

         label94:
         for(; var5 < var4; ++var5) {
            switch(var3 = var0.charAt(var5)) {
            case '.':
               if (var8) {
                  throw new NumberFormatException("multiple points");
               }

               var9 = var5;
               if (var2) {
                  var9 = var5 - 1;
               }

               var8 = true;
               continue;
            case '/':
            default:
               break label94;
            case '0':
               if (var7 > 0) {
                  ++var11;
               } else {
                  ++var10;
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

            while(var11 > 0) {
               var6[var7++] = '0';
               --var11;
            }

            var6[var7++] = var3;
         }

         if (var7 == 0) {
            var6 = fJ;
            var7 = 1;
            if (var10 == 0) {
               throw new NumberFormatException(var0);
            }
         }

         int var15;
         if (var8) {
            var15 = var9 - var10;
         } else {
            var15 = var7 + var11;
         }

         if (var5 < var4 && (var3 = var0.charAt(var5)) == 'e' || var3 == 'E') {
            byte var17 = 1;
            var9 = 0;
            var10 = 214748364;
            boolean var12 = false;
            ++var5;
            int var13;
            switch(var0.charAt(var5)) {
            case '-':
               var17 = -1;
            case '+':
               ++var5;
            default:
               var13 = var5;
            }

            label119:
            while(var5 < var4) {
               if (var9 >= var10) {
                  var12 = true;
               }

               switch(var3 = var0.charAt(var5++)) {
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
                  var9 = var9 * 10 + (var3 - 48);
                  break;
               default:
                  --var5;
                  break label119;
               }
            }

            int var16 = 324 + var7 + var11;
            if (!var12 && var9 <= var16) {
               var15 += var17 * var9;
            } else {
               var15 = var17 * var16;
            }

            if (var5 == var13) {
               throw new NumberFormatException(var0);
            }
         }

         if (var5 >= var4 || var5 == var4 - 1 && (var0.charAt(var5) == 'f' || var0.charAt(var5) == 'F' || var0.charAt(var5) == 'd' || var0.charAt(var5) == 'D')) {
            return new FloatingDecimal(var1, var15, var6, var7, false);
         }
      } catch (StringIndexOutOfBoundsException var14) {
      }

      throw new NumberFormatException(var0);
   }

   public final double doubleValue() {
      int var1 = Math.min(this.nDigits, 16);
      this.roundDir = 0;
      int var2 = this.digits[0] - 48;
      int var4 = Math.min(var1, 9);

      int var10;
      for(var10 = 1; var10 < var4; ++var10) {
         var2 = var2 * 10 + this.digits[var10] - 48;
      }

      long var33 = (long)var2;

      for(var10 = var4; var10 < var1; ++var10) {
         var33 = var33 * 10L + (long)(this.digits[var10] - 48);
      }

      double var34 = (double)var33;
      var10 = this.decExponent - var1;
      int var8;
      if (this.nDigits <= 15) {
         if (var10 == 0 || var34 == 0.0D) {
            if (this.isNegative) {
               return -var34;
            }

            return var34;
         }

         double var6;
         double var38;
         if (var10 >= 0) {
            if (var10 <= fC) {
               var6 = var34 * fy[var10];
               if (this.mustSetRoundDir) {
                  var38 = var6 / fy[var10];
                  this.roundDir = var38 == var34 ? 0 : (var38 < var34 ? 1 : -1);
               }

               if (this.isNegative) {
                  return -var6;
               }

               return var6;
            }

            var8 = 15 - var1;
            if (var10 <= fC + var8) {
               var6 = (var34 *= fy[var8]) * fy[var10 - var8];
               if (this.mustSetRoundDir) {
                  var38 = var6 / fy[var10 - var8];
                  this.roundDir = var38 == var34 ? 0 : (var38 < var34 ? 1 : -1);
               }

               if (this.isNegative) {
                  return -var6;
               }

               return var6;
            }
         } else if (var10 >= -fC) {
            var38 = (var6 = var34 / fy[-var10]) * fy[-var10];
            if (this.mustSetRoundDir) {
               this.roundDir = var38 == var34 ? 0 : (var38 < var34 ? 1 : -1);
            }

            if (this.isNegative) {
               return -var6;
            }

            return var6;
         }
      }

      double var14;
      if (var10 > 0) {
         if (this.decExponent > 309) {
            if (this.isNegative) {
               return Double.NEGATIVE_INFINITY;
            }

            return Double.POSITIVE_INFINITY;
         }

         if ((var10 & 15) != 0) {
            var34 *= fy[var10 & 15];
         }

         if ((var10 >>= 4) != 0) {
            for(var8 = 0; var10 > 1; var10 >>= 1) {
               if ((var10 & 1) != 0) {
                  var34 *= fA[var8];
               }

               ++var8;
            }

            if (Double.isInfinite(var14 = var34 * fA[var8])) {
               if (Double.isInfinite((var14 = var34 / 2.0D) * fA[var8])) {
                  if (this.isNegative) {
                     return Double.NEGATIVE_INFINITY;
                  }

                  return Double.POSITIVE_INFINITY;
               }

               var14 = Double.MAX_VALUE;
            }

            var34 = var14;
         }
      } else if (var10 < 0) {
         var10 = -var10;
         if (this.decExponent < -325) {
            if (this.isNegative) {
               return -0.0D;
            }

            return 0.0D;
         }

         if ((var10 & 15) != 0) {
            var34 /= fy[var10 & 15];
         }

         if ((var10 >>= 4) != 0) {
            for(var8 = 0; var10 > 1; var10 >>= 1) {
               if ((var10 & 1) != 0) {
                  var34 *= fB[var8];
               }

               ++var8;
            }

            if ((var14 = var34 * fB[var8]) == 0.0D) {
               if ((var14 = var34 * 2.0D) * fB[var8] == 0.0D) {
                  if (this.isNegative) {
                     return -0.0D;
                  }

                  return 0.0D;
               }

               var14 = Double.MIN_VALUE;
            }

            var34 = var14;
         }
      }

      FDBigInt var39 = new FDBigInt(var33, this.digits, var1, this.nDigits);
      var10 = this.decExponent - this.nDigits;

      boolean var9;
      do {
         long var30;
         var2 = (int)((var30 = Double.doubleToLongBits(var34) & Long.MAX_VALUE) >>> 52);
         var30 &= 4503599627370495L;
         if (var2 > 0) {
            var30 |= 4503599627370496L;
         } else {
            if (var30 == 0L) {
               throw new RuntimeException("Assertion botch: doubleToBigInt(0.0)");
            }

            ++var2;

            while((var30 & 4503599627370496L) == 0L) {
               var30 <<= 1;
               --var2;
            }
         }

         var2 -= 1023;
         int var3 = b(var30);
         int var36 = 53 - var3;
         var30 >>>= var36;
         this.bigIntExp = var2 + 1 - var3;
         this.bigIntNBits = var3;
         FDBigInt var40 = new FDBigInt(var30);
         int var15;
         if (var10 >= 0) {
            var1 = 0;
            var15 = 0;
            var3 = var10;
            var2 = var10;
         } else {
            var15 = var1 = -var10;
            var3 = 0;
            var2 = 0;
         }

         if (this.bigIntExp >= 0) {
            var15 += this.bigIntExp;
         } else {
            var2 -= this.bigIntExp;
         }

         var36 = var15;
         int var7;
         if (this.bigIntExp + this.bigIntNBits <= -1022) {
            var7 = this.bigIntExp + 1023 + 52;
         } else {
            var7 = 54 - this.bigIntNBits;
         }

         var15 += var7;
         var2 += var7;
         var7 = Math.min(var15, Math.min(var2, var36));
         var15 -= var7;
         var2 -= var7;
         var36 -= var7;
         var40 = a(var40, var1, var15);
         FDBigInt var37 = a(new FDBigInt(var39), var3, var2);
         FDBigInt var35;
         if ((var7 = var40.d(var37)) > 0) {
            var9 = true;
            var35 = var40.c(var37);
            if (this.bigIntNBits == 1 && this.bigIntExp > -1023) {
               --var36;
               if (var36 < 0) {
                  var36 = 0;
                  var35.lshiftMe(1);
               }
            }
         } else {
            if (var7 >= 0) {
               break;
            }

            var9 = false;
            var35 = var37.c(var40);
         }

         FDBigInt var32 = g(var1, var36);
         if ((var7 = var35.d(var32)) < 0) {
            this.roundDir = var9 ? -1 : 1;
            break;
         }

         if (var7 == 0) {
            var34 += 0.5D * a(var34, var9);
            this.roundDir = var9 ? -1 : 1;
            break;
         }
      } while((var34 += a(var34, var9)) != 0.0D && var34 != Double.POSITIVE_INFINITY);

      return this.isNegative ? -var34 : var34;
   }

   public final float floatValue() {
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
            if (this.isNegative) {
               return -var3;
            }

            return var3;
         }

         if (var4 >= 0) {
            if (var4 <= fD) {
               var3 *= fz[var4];
               if (this.isNegative) {
                  return -var3;
               }

               return var3;
            }

            int var16 = 7 - var1;
            if (var4 <= fD + var16) {
               var3 = var3 * fz[var16] * fz[var4 - var16];
               if (this.isNegative) {
                  return -var3;
               }

               return var3;
            }
         } else if (var4 >= -fD) {
            var3 /= fz[-var4];
            if (this.isNegative) {
               return -var3;
            }

            return var3;
         }
      } else if (this.decExponent >= this.nDigits && this.nDigits + this.decExponent <= 15) {
         long var5 = (long)var2;

         for(int var7 = var1; var7 < this.nDigits; ++var7) {
            var5 = var5 * 10L + (long)(this.digits[var7] - 48);
         }

         double var18 = (double)var5;
         var4 = this.decExponent - this.nDigits;
         var3 = (float)(var18 * fy[var4]);
         if (this.isNegative) {
            return -var3;
         }

         return var3;
      }

      if (this.decExponent > 39) {
         return this.isNegative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
      } else if (this.decExponent < -46) {
         return this.isNegative ? -0.0F : 0.0F;
      } else {
         this.mustSetRoundDir = true;
         double var17 = this.doubleValue();
         long var12;
         long var14;
         return (var14 = (var12 = Double.doubleToLongBits(var17)) & 9218868437227405312L) != 0L && var14 != 9218868437227405312L ? (float)Double.longBitsToDouble(var12 + (long)this.roundDir) : (float)var17;
      }
   }

   static {
      fC = fy.length - 1;
      fD = fz.length - 1;
      fE = new int[]{1, 5, 25, 125, 625, 3125, 15625, 78125, 390625, 1953125, 9765625, 48828125, 244140625, 1220703125};
      fF = new long[]{1L, 5L, 25L, 125L, 625L, 3125L, 15625L, 78125L, 390625L, 1953125L, 9765625L, 48828125L, 244140625L, 1220703125L, 6103515625L, 30517578125L, 152587890625L, 762939453125L, 3814697265625L, 19073486328125L, 95367431640625L, 476837158203125L, 2384185791015625L, 11920928955078125L, 59604644775390625L, 298023223876953125L, 1490116119384765625L};
      fG = new int[]{0, 3, 5, 7, 10, 12, 14, 17, 19, 21, 24, 26, 28, 31, 33, 35, 38, 40, 42, 45, 47, 49, 52, 54, 56, 59, 61};
      fH = new char[]{'I', 'n', 'f', 'i', 'n', 'i', 't', 'y'};
      fI = new char[]{'N', 'a', 'N'};
      fJ = new char[]{'0', '0', '0', '0', '0', '0', '0', '0'};
   }
}
