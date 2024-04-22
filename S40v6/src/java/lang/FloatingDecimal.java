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

   private FloatingDecimal(boolean negSign, int decExponent, char[] digits, int n, boolean e) {
      this.isNegative = negSign;
      this.isExceptional = e;
      this.decExponent = decExponent;
      this.digits = digits;
      this.nDigits = n;
   }

   private static int countBits(long v) {
      if (v == 0L) {
         return 0;
      } else {
         while((v & -72057594037927936L) == 0L) {
            v <<= 8;
         }

         while(v > 0L) {
            v <<= 1;
         }

         int n;
         for(n = 0; (v & 72057594037927935L) != 0L; n += 8) {
            v <<= 8;
         }

         while(v != 0L) {
            v <<= 1;
            ++n;
         }

         return n;
      }
   }

   private static synchronized FDBigInt big5pow(int p) {
      if (p < 0) {
         throw new RuntimeException("Assertion botch: negative power of 5");
      } else {
         if (b5p == null) {
            b5p = new FDBigInt[p + 1];
         } else if (b5p.length <= p) {
            FDBigInt[] t = new FDBigInt[p + 1];
            System.arraycopy(b5p, 0, t, 0, b5p.length);
            b5p = t;
         }

         if (b5p[p] != null) {
            return b5p[p];
         } else if (p < small5pow.length) {
            return b5p[p] = new FDBigInt(small5pow[p]);
         } else if (p < long5pow.length) {
            return b5p[p] = new FDBigInt(long5pow[p]);
         } else {
            int q = p >> 1;
            int r = p - q;
            FDBigInt bigq = b5p[q];
            if (bigq == null) {
               bigq = big5pow(q);
            }

            if (r < small5pow.length) {
               return b5p[p] = bigq.mult(small5pow[r]);
            } else {
               FDBigInt bigr = b5p[r];
               if (bigr == null) {
                  bigr = big5pow(r);
               }

               return b5p[p] = bigq.mult(bigr);
            }
         }
      }
   }

   private static FDBigInt multPow52(FDBigInt v, int p5, int p2) {
      if (p5 != 0) {
         if (p5 < small5pow.length) {
            v = v.mult(small5pow[p5]);
         } else {
            v = v.mult(big5pow(p5));
         }
      }

      if (p2 != 0) {
         v.lshiftMe(p2);
      }

      return v;
   }

   private static FDBigInt constructPow52(int p5, int p2) {
      FDBigInt v = new FDBigInt(big5pow(p5));
      if (p2 != 0) {
         v.lshiftMe(p2);
      }

      return v;
   }

   private FDBigInt doubleToBigInt(double dval) {
      long lbits = Double.doubleToLongBits(dval) & Long.MAX_VALUE;
      int binexp = (int)(lbits >>> 52);
      lbits &= 4503599627370495L;
      if (binexp > 0) {
         lbits |= 4503599627370496L;
      } else {
         if (lbits == 0L) {
            throw new RuntimeException("Assertion botch: doubleToBigInt(0.0)");
         }

         ++binexp;

         while((lbits & 4503599627370496L) == 0L) {
            lbits <<= 1;
            --binexp;
         }
      }

      binexp -= 1023;
      int nbits = countBits(lbits);
      int lowOrderZeros = 53 - nbits;
      lbits >>>= lowOrderZeros;
      this.bigIntExp = binexp + 1 - nbits;
      this.bigIntNBits = nbits;
      return new FDBigInt(lbits);
   }

   private static double ulp(double dval, boolean subtracting) {
      long lbits = Double.doubleToLongBits(dval) & Long.MAX_VALUE;
      int binexp = (int)(lbits >>> 52);
      if (subtracting && binexp >= 52 && (lbits & 4503599627370495L) == 0L) {
         --binexp;
      }

      double ulpval;
      if (binexp > 52) {
         ulpval = Double.longBitsToDouble((long)(binexp - 52) << 52);
      } else if (binexp == 0) {
         ulpval = Double.MIN_VALUE;
      } else {
         ulpval = Double.longBitsToDouble(1L << binexp - 1);
      }

      if (subtracting) {
         ulpval = -ulpval;
      }

      return ulpval;
   }

   float stickyRound(double dval) {
      long lbits = Double.doubleToLongBits(dval);
      long binexp = lbits & 9218868437227405312L;
      if (binexp != 0L && binexp != 9218868437227405312L) {
         lbits += (long)this.roundDir;
         return (float)Double.longBitsToDouble(lbits);
      } else {
         return (float)dval;
      }
   }

   private void developLongDigits(int decExponent, long lvalue, long insignificant) {
      int i;
      for(i = 0; insignificant >= 10L; ++i) {
         insignificant /= 10L;
      }

      if (i != 0) {
         long pow10 = long5pow[i] << i;
         long residue = lvalue % pow10;
         lvalue /= pow10;
         decExponent += i;
         if (residue >= pow10 >> 1) {
            ++lvalue;
         }
      }

      char[] digits;
      byte ndigits;
      int digitno;
      int c;
      if (lvalue <= 2147483647L) {
         if (lvalue <= 0L) {
            throw new RuntimeException("Assertion botch: value " + lvalue + " <= 0");
         }

         int ivalue = (int)lvalue;
         ndigits = 10;
         digits = new char[10];
         digitno = ndigits - 1;
         c = ivalue % 10;

         for(ivalue /= 10; c == 0; ivalue /= 10) {
            ++decExponent;
            c = ivalue % 10;
         }

         while(ivalue != 0) {
            digits[digitno--] = (char)(c + 48);
            ++decExponent;
            c = ivalue % 10;
            ivalue /= 10;
         }

         digits[digitno] = (char)(c + 48);
      } else {
         ndigits = 20;
         digits = new char[20];
         digitno = ndigits - 1;
         c = (int)(lvalue % 10L);

         for(lvalue /= 10L; c == 0; lvalue /= 10L) {
            ++decExponent;
            c = (int)(lvalue % 10L);
         }

         while(lvalue != 0L) {
            digits[digitno--] = (char)(c + 48);
            ++decExponent;
            c = (int)(lvalue % 10L);
            lvalue /= 10L;
         }

         digits[digitno] = (char)(c + 48);
      }

      int ndigits = ndigits - digitno;
      char[] result;
      if (digitno == 0) {
         result = digits;
      } else {
         result = new char[ndigits];
         System.arraycopy(digits, digitno, result, 0, ndigits);
      }

      this.digits = result;
      this.decExponent = decExponent + 1;
      this.nDigits = ndigits;
   }

   private void roundup() {
      int i;
      int q = this.digits[i = this.nDigits - 1];
      if (q == '9') {
         while(true) {
            if (q != '9' || i <= 0) {
               if (q == '9') {
                  ++this.decExponent;
                  this.digits[0] = '1';
                  return;
               }
               break;
            }

            this.digits[i] = '0';
            --i;
            q = this.digits[i];
         }
      }

      this.digits[i] = (char)(q + 1);
   }

   public FloatingDecimal(double d) {
      long dBits = Double.doubleToLongBits(d);
      if ((dBits & Long.MIN_VALUE) != 0L) {
         this.isNegative = true;
         dBits ^= Long.MIN_VALUE;
      } else {
         this.isNegative = false;
      }

      int binExp = (int)((dBits & 9218868437227405312L) >> 52);
      long fractBits = dBits & 4503599627370495L;
      if (binExp == 2047) {
         this.isExceptional = true;
         if (fractBits == 0L) {
            this.digits = infinity;
         } else {
            this.digits = notANumber;
            this.isNegative = false;
         }

         this.nDigits = this.digits.length;
      } else {
         this.isExceptional = false;
         int nSignificantBits;
         if (binExp == 0) {
            if (fractBits == 0L) {
               this.decExponent = 0;
               this.digits = zero;
               this.nDigits = 1;
               return;
            }

            while((fractBits & 4503599627370496L) == 0L) {
               fractBits <<= 1;
               --binExp;
            }

            nSignificantBits = 52 + binExp + 1;
            ++binExp;
         } else {
            fractBits |= 4503599627370496L;
            nSignificantBits = 53;
         }

         binExp -= 1023;
         this.dtoa(binExp, fractBits, nSignificantBits);
      }
   }

   public FloatingDecimal(float f) {
      int fBits = Float.floatToIntBits(f);
      if ((fBits & Integer.MIN_VALUE) != 0) {
         this.isNegative = true;
         fBits ^= Integer.MIN_VALUE;
      } else {
         this.isNegative = false;
      }

      int binExp = (fBits & 2139095040) >> 23;
      int fractBits = fBits & 8388607;
      if (binExp == 255) {
         this.isExceptional = true;
         if ((long)fractBits == 0L) {
            this.digits = infinity;
         } else {
            this.digits = notANumber;
            this.isNegative = false;
         }

         this.nDigits = this.digits.length;
      } else {
         this.isExceptional = false;
         int nSignificantBits;
         if (binExp == 0) {
            if (fractBits == 0) {
               this.decExponent = 0;
               this.digits = zero;
               this.nDigits = 1;
               return;
            }

            while((fractBits & 8388608) == 0) {
               fractBits <<= 1;
               --binExp;
            }

            nSignificantBits = 23 + binExp + 1;
            ++binExp;
         } else {
            fractBits |= 8388608;
            nSignificantBits = 24;
         }

         binExp -= 127;
         this.dtoa(binExp, (long)fractBits << 29, nSignificantBits);
      }
   }

   private void dtoa(int binExp, long fractBits, int nSignificantBits) {
      int nFractBits = countBits(fractBits);
      int nTinyBits = Math.max(0, nFractBits - binExp - 1);
      if (binExp <= 62 && binExp >= -21 && nTinyBits < long5pow.length && nFractBits + n5bits[nTinyBits] < 64 && nTinyBits == 0) {
         long halfULP;
         if (binExp > nSignificantBits) {
            halfULP = 1L << binExp - nSignificantBits - 1;
         } else {
            halfULP = 0L;
         }

         if (binExp >= 52) {
            fractBits <<= binExp - 52;
         } else {
            fractBits >>>= 52 - binExp;
         }

         this.developLongDigits(0, fractBits, halfULP);
      } else {
         double d2 = Double.longBitsToDouble(4607182418800017408L | fractBits & -4503599627370497L);
         int decExp = (int)Math.floor((d2 - 1.5D) * 0.289529654D + 0.176091259D + (double)binExp * 0.301029995663981D);
         int B5 = Math.max(0, -decExp);
         int B2 = B5 + nTinyBits + binExp;
         int S5 = Math.max(0, decExp);
         int S2 = S5 + nTinyBits;
         int M2 = B2 - nSignificantBits;
         fractBits >>>= 53 - nFractBits;
         B2 -= nFractBits - 1;
         int common2factor = Math.min(B2, S2);
         B2 -= common2factor;
         S2 -= common2factor;
         M2 -= common2factor;
         if (nFractBits == 1) {
            --M2;
         }

         if (M2 < 0) {
            B2 -= M2;
            S2 -= M2;
            M2 = 0;
         }

         char[] digits = this.digits = new char[18];
         int ndigit = false;
         int Bbits = nFractBits + B2 + (B5 < n5bits.length ? n5bits[B5] : B5 * 3);
         int tenSbits = S2 + 1 + (S5 + 1 < n5bits.length ? n5bits[S5 + 1] : (S5 + 1) * 3);
         boolean low;
         boolean high;
         long lowDigitDifference;
         int q;
         int s;
         int ndigit;
         if (Bbits < 64 && tenSbits < 64) {
            if (Bbits < 32 && tenSbits < 32) {
               int b = (int)fractBits * small5pow[B5] << B2;
               s = small5pow[S5] << S2;
               int m = small5pow[B5] << M2;
               int tens = s * 10;
               ndigit = 0;
               q = b / s;
               b = 10 * (b % s);
               m *= 10;
               low = b < m;
               high = b + m > tens;
               if (q >= 10) {
                  throw new RuntimeException("Assertion botch: excessivly large digit " + q);
               }

               if (q == 0 && !high) {
                  --decExp;
               } else {
                  digits[ndigit++] = (char)(48 + q);
               }

               if (decExp <= -3 || decExp >= 8) {
                  low = false;
                  high = false;
               }

               for(; !low && !high; digits[ndigit++] = (char)(48 + q)) {
                  q = b / s;
                  b = 10 * (b % s);
                  m *= 10;
                  if (q >= 10) {
                     throw new RuntimeException("Assertion botch: excessivly large digit " + q);
                  }

                  if ((long)m > 0L) {
                     low = b < m;
                     high = b + m > tens;
                  } else {
                     low = true;
                     high = true;
                  }
               }

               lowDigitDifference = (long)((b << 1) - tens);
            } else {
               long b = fractBits * long5pow[B5] << B2;
               long s = long5pow[S5] << S2;
               long m = long5pow[B5] << M2;
               long tens = s * 10L;
               ndigit = 0;
               q = (int)(b / s);
               b = 10L * (b % s);
               m *= 10L;
               low = b < m;
               high = b + m > tens;
               if (q >= 10) {
                  throw new RuntimeException("Assertion botch: excessivly large digit " + q);
               }

               if (q == 0 && !high) {
                  --decExp;
               } else {
                  digits[ndigit++] = (char)(48 + q);
               }

               if (decExp <= -3 || decExp >= 8) {
                  low = false;
                  high = false;
               }

               for(; !low && !high; digits[ndigit++] = (char)(48 + q)) {
                  q = (int)(b / s);
                  b = 10L * (b % s);
                  m *= 10L;
                  if (q >= 10) {
                     throw new RuntimeException("Assertion botch: excessivly large digit " + q);
                  }

                  if (m > 0L) {
                     low = b < m;
                     high = b + m > tens;
                  } else {
                     low = true;
                     high = true;
                  }
               }

               lowDigitDifference = (b << 1) - tens;
            }
         } else {
            FDBigInt Bval = multPow52(new FDBigInt(fractBits), B5, B2);
            FDBigInt Sval = constructPow52(S5, S2);
            FDBigInt Mval = constructPow52(B5, M2);
            Bval.lshiftMe(s = Sval.normalizeMe());
            Mval.lshiftMe(s);
            FDBigInt tenSval = Sval.mult(10);
            ndigit = 0;
            q = Bval.quoRemIteration(Sval);
            Mval = Mval.mult(10);
            low = Bval.cmp(Mval) < 0;
            high = Bval.add(Mval).cmp(tenSval) > 0;
            if (q >= 10) {
               throw new RuntimeException("Assertion botch: excessivly large digit " + q);
            }

            if (q == 0 && !high) {
               --decExp;
            } else {
               digits[ndigit++] = (char)(48 + q);
            }

            if (decExp <= -3 || decExp >= 8) {
               low = false;
               high = false;
            }

            while(!low && !high) {
               q = Bval.quoRemIteration(Sval);
               Mval = Mval.mult(10);
               if (q >= 10) {
                  throw new RuntimeException("Assertion botch: excessivly large digit " + q);
               }

               low = Bval.cmp(Mval) < 0;
               high = Bval.add(Mval).cmp(tenSval) > 0;
               digits[ndigit++] = (char)(48 + q);
            }

            if (high && low) {
               Bval.lshiftMe(1);
               lowDigitDifference = (long)Bval.cmp(tenSval);
            } else {
               lowDigitDifference = 0L;
            }
         }

         this.decExponent = decExp + 1;
         this.digits = digits;
         this.nDigits = ndigit;
         if (high) {
            if (low) {
               if (lowDigitDifference == 0L) {
                  if ((digits[this.nDigits - 1] & 1) != 0) {
                     this.roundup();
                  }
               } else if (lowDigitDifference > 0L) {
                  this.roundup();
               }
            } else {
               this.roundup();
            }
         }

      }
   }

   public String toString() {
      StringBuffer result = new StringBuffer(this.nDigits + 8);
      if (this.isNegative) {
         result.append('-');
      }

      if (this.isExceptional) {
         result.append(this.digits, 0, this.nDigits);
      } else {
         result.append("0.");
         result.append(this.digits, 0, this.nDigits);
         result.append('e');
         result.append(this.decExponent);
      }

      return new String(result);
   }

   public String toJavaFormatString() {
      char[] result = new char[this.nDigits + 10];
      int i = 0;
      if (this.isNegative) {
         result[0] = '-';
         i = 1;
      }

      int i;
      if (this.isExceptional) {
         System.arraycopy(this.digits, 0, result, i, this.nDigits);
         i = i + this.nDigits;
      } else {
         int e;
         if (this.decExponent > 0 && this.decExponent < 8) {
            e = Math.min(this.nDigits, this.decExponent);
            System.arraycopy(this.digits, 0, result, i, e);
            i = i + e;
            if (e < this.decExponent) {
               e = this.decExponent - e;
               System.arraycopy(zero, 0, result, i, e);
               i += e;
               result[i++] = '.';
               result[i++] = '0';
            } else {
               result[i++] = '.';
               if (e < this.nDigits) {
                  int t = this.nDigits - e;
                  System.arraycopy(this.digits, e, result, i, t);
                  i += t;
               } else {
                  result[i++] = '0';
               }
            }
         } else if (this.decExponent <= 0 && this.decExponent > -3) {
            i = i + 1;
            result[i] = '0';
            result[i++] = '.';
            if (this.decExponent != 0) {
               System.arraycopy(zero, 0, result, i, -this.decExponent);
               i -= this.decExponent;
            }

            System.arraycopy(this.digits, 0, result, i, this.nDigits);
            i += this.nDigits;
         } else {
            i = i + 1;
            result[i] = this.digits[0];
            result[i++] = '.';
            if (this.nDigits > 1) {
               System.arraycopy(this.digits, 1, result, i, this.nDigits - 1);
               i += this.nDigits - 1;
            } else {
               result[i++] = '0';
            }

            result[i++] = 'E';
            if (this.decExponent <= 0) {
               result[i++] = '-';
               e = -this.decExponent + 1;
            } else {
               e = this.decExponent - 1;
            }

            if (e <= 9) {
               result[i++] = (char)(e + 48);
            } else if (e <= 99) {
               result[i++] = (char)(e / 10 + 48);
               result[i++] = (char)(e % 10 + 48);
            } else {
               result[i++] = (char)(e / 100 + 48);
               e %= 100;
               result[i++] = (char)(e / 10 + 48);
               result[i++] = (char)(e % 10 + 48);
            }
         }
      }

      return new String(result, 0, i);
   }

   public static FloatingDecimal readJavaFormatString(String in) throws NumberFormatException {
      boolean isNegative = false;
      boolean signSeen = false;

      try {
         in = in.trim();
         int l = in.length();
         if (l == 0) {
            throw new NumberFormatException("empty String");
         }

         int i = 0;
         char c;
         char[] digits;
         int nDigits;
         boolean decSeen;
         int decPt;
         int nLeadZero;
         int nTrailZero;
         switch(c = in.charAt(i)) {
         case '-':
            isNegative = true;
         case '+':
            ++i;
            signSeen = true;
         default:
            digits = new char[l];
            nDigits = 0;
            decSeen = false;
            decPt = 0;
            nLeadZero = 0;
            nTrailZero = 0;
         }

         label94:
         for(; i < l; ++i) {
            switch(c = in.charAt(i)) {
            case '.':
               if (decSeen) {
                  throw new NumberFormatException("multiple points");
               }

               decPt = i;
               if (signSeen) {
                  decPt = i - 1;
               }

               decSeen = true;
               continue;
            case '/':
            default:
               break label94;
            case '0':
               if (nDigits > 0) {
                  ++nTrailZero;
               } else {
                  ++nLeadZero;
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

            while(nTrailZero > 0) {
               digits[nDigits++] = '0';
               --nTrailZero;
            }

            digits[nDigits++] = c;
         }

         if (nDigits == 0) {
            digits = zero;
            nDigits = 1;
            if (nLeadZero == 0) {
               throw new NumberFormatException(in);
            }
         }

         int decExp;
         if (decSeen) {
            decExp = decPt - nLeadZero;
         } else {
            decExp = nDigits + nTrailZero;
         }

         if (i < l && (c = in.charAt(i)) == 'e' || c == 'E') {
            int expSign = 1;
            int expVal = 0;
            int reallyBig = 214748364;
            boolean expOverflow = false;
            ++i;
            int expAt;
            switch(in.charAt(i)) {
            case '-':
               expSign = -1;
            case '+':
               ++i;
            default:
               expAt = i;
            }

            label119:
            while(i < l) {
               if (expVal >= reallyBig) {
                  expOverflow = true;
               }

               switch(c = in.charAt(i++)) {
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
                  expVal = expVal * 10 + (c - 48);
                  break;
               default:
                  --i;
                  break label119;
               }
            }

            int expLimit = 324 + nDigits + nTrailZero;
            if (!expOverflow && expVal <= expLimit) {
               decExp += expSign * expVal;
            } else {
               decExp = expSign * expLimit;
            }

            if (i == expAt) {
               throw new NumberFormatException(in);
            }
         }

         if (i >= l || i == l - 1 && (in.charAt(i) == 'f' || in.charAt(i) == 'F' || in.charAt(i) == 'd' || in.charAt(i) == 'D')) {
            return new FloatingDecimal(isNegative, decExp, digits, nDigits, false);
         }
      } catch (StringIndexOutOfBoundsException var19) {
      }

      throw new NumberFormatException(in);
   }

   public double doubleValue() {
      int kDigits = Math.min(this.nDigits, 16);
      this.roundDir = 0;
      int iValue = this.digits[0] - 48;
      int iDigits = Math.min(kDigits, 9);

      int exp;
      for(exp = 1; exp < iDigits; ++exp) {
         iValue = iValue * 10 + this.digits[exp] - 48;
      }

      long lValue = (long)iValue;

      for(exp = iDigits; exp < kDigits; ++exp) {
         lValue = lValue * 10L + (long)(this.digits[exp] - 48);
      }

      double dValue = (double)lValue;
      exp = this.decExponent - kDigits;
      int j;
      if (this.nDigits <= 15) {
         if (exp == 0 || dValue == 0.0D) {
            return this.isNegative ? -dValue : dValue;
         }

         double rValue;
         double tValue;
         if (exp >= 0) {
            if (exp <= maxSmallTen) {
               rValue = dValue * small10pow[exp];
               if (this.mustSetRoundDir) {
                  tValue = rValue / small10pow[exp];
                  this.roundDir = tValue == dValue ? 0 : (tValue < dValue ? 1 : -1);
               }

               return this.isNegative ? -rValue : rValue;
            }

            j = 15 - kDigits;
            if (exp <= maxSmallTen + j) {
               dValue *= small10pow[j];
               rValue = dValue * small10pow[exp - j];
               if (this.mustSetRoundDir) {
                  tValue = rValue / small10pow[exp - j];
                  this.roundDir = tValue == dValue ? 0 : (tValue < dValue ? 1 : -1);
               }

               return this.isNegative ? -rValue : rValue;
            }
         } else if (exp >= -maxSmallTen) {
            rValue = dValue / small10pow[-exp];
            tValue = rValue * small10pow[-exp];
            if (this.mustSetRoundDir) {
               this.roundDir = tValue == dValue ? 0 : (tValue < dValue ? 1 : -1);
            }

            return this.isNegative ? -rValue : rValue;
         }
      }

      double t;
      if (exp > 0) {
         if (this.decExponent > 309) {
            return this.isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
         }

         if ((exp & 15) != 0) {
            dValue *= small10pow[exp & 15];
         }

         if ((exp >>= 4) != 0) {
            for(j = 0; exp > 1; exp >>= 1) {
               if ((exp & 1) != 0) {
                  dValue *= big10pow[j];
               }

               ++j;
            }

            t = dValue * big10pow[j];
            if (Double.isInfinite(t)) {
               t = dValue / 2.0D;
               t *= big10pow[j];
               if (Double.isInfinite(t)) {
                  return this.isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
               }

               t = Double.MAX_VALUE;
            }

            dValue = t;
         }
      } else if (exp < 0) {
         exp = -exp;
         if (this.decExponent < -325) {
            return this.isNegative ? -0.0D : 0.0D;
         }

         if ((exp & 15) != 0) {
            dValue /= small10pow[exp & 15];
         }

         if ((exp >>= 4) != 0) {
            for(j = 0; exp > 1; exp >>= 1) {
               if ((exp & 1) != 0) {
                  dValue *= tiny10pow[j];
               }

               ++j;
            }

            t = dValue * tiny10pow[j];
            if (t == 0.0D) {
               t = dValue * 2.0D;
               t *= tiny10pow[j];
               if (t == 0.0D) {
                  return this.isNegative ? -0.0D : 0.0D;
               }

               t = Double.MIN_VALUE;
            }

            dValue = t;
         }
      }

      FDBigInt bigD0 = new FDBigInt(lValue, this.digits, kDigits, this.nDigits);
      exp = this.decExponent - this.nDigits;

      do {
         FDBigInt bigB = this.doubleToBigInt(dValue);
         int B2;
         int B5;
         int D2;
         int D5;
         if (exp >= 0) {
            B5 = 0;
            B2 = 0;
            D5 = exp;
            D2 = exp;
         } else {
            B2 = B5 = -exp;
            D5 = 0;
            D2 = 0;
         }

         if (this.bigIntExp >= 0) {
            B2 += this.bigIntExp;
         } else {
            D2 -= this.bigIntExp;
         }

         int Ulp2 = B2;
         int hulpbias;
         if (this.bigIntExp + this.bigIntNBits <= -1022) {
            hulpbias = this.bigIntExp + 1023 + 52;
         } else {
            hulpbias = 54 - this.bigIntNBits;
         }

         B2 += hulpbias;
         D2 += hulpbias;
         int common2 = Math.min(B2, Math.min(D2, Ulp2));
         B2 -= common2;
         D2 -= common2;
         Ulp2 -= common2;
         bigB = multPow52(bigB, B5, B2);
         FDBigInt bigD = multPow52(new FDBigInt(bigD0), D5, D2);
         FDBigInt diff;
         int cmpResult;
         boolean overvalue;
         if ((cmpResult = bigB.cmp(bigD)) > 0) {
            overvalue = true;
            diff = bigB.sub(bigD);
            if (this.bigIntNBits == 1 && this.bigIntExp > -1023) {
               --Ulp2;
               if (Ulp2 < 0) {
                  Ulp2 = 0;
                  diff.lshiftMe(1);
               }
            }
         } else {
            if (cmpResult >= 0) {
               break;
            }

            overvalue = false;
            diff = bigD.sub(bigB);
         }

         FDBigInt halfUlp = constructPow52(B5, Ulp2);
         if ((cmpResult = diff.cmp(halfUlp)) < 0) {
            this.roundDir = overvalue ? -1 : 1;
            break;
         }

         if (cmpResult == 0) {
            dValue += 0.5D * ulp(dValue, overvalue);
            this.roundDir = overvalue ? -1 : 1;
            break;
         }

         dValue += ulp(dValue, overvalue);
      } while(dValue != 0.0D && dValue != Double.POSITIVE_INFINITY);

      return this.isNegative ? -dValue : dValue;
   }

   public float floatValue() {
      int kDigits = Math.min(this.nDigits, 8);
      int iValue = this.digits[0] - 48;

      int exp;
      for(exp = 1; exp < kDigits; ++exp) {
         iValue = iValue * 10 + this.digits[exp] - 48;
      }

      float fValue = (float)iValue;
      exp = this.decExponent - kDigits;
      if (this.nDigits <= 7) {
         if (exp == 0 || fValue == 0.0F) {
            return this.isNegative ? -fValue : fValue;
         }

         if (exp >= 0) {
            if (exp <= singleMaxSmallTen) {
               fValue *= singleSmall10pow[exp];
               return this.isNegative ? -fValue : fValue;
            }

            int slop = 7 - kDigits;
            if (exp <= singleMaxSmallTen + slop) {
               fValue *= singleSmall10pow[slop];
               fValue *= singleSmall10pow[exp - slop];
               return this.isNegative ? -fValue : fValue;
            }
         } else if (exp >= -singleMaxSmallTen) {
            fValue /= singleSmall10pow[-exp];
            return this.isNegative ? -fValue : fValue;
         }
      } else if (this.decExponent >= this.nDigits && this.nDigits + this.decExponent <= 15) {
         long lValue = (long)iValue;

         for(int i = kDigits; i < this.nDigits; ++i) {
            lValue = lValue * 10L + (long)(this.digits[i] - 48);
         }

         double dValue = (double)lValue;
         exp = this.decExponent - this.nDigits;
         dValue *= small10pow[exp];
         fValue = (float)dValue;
         return this.isNegative ? -fValue : fValue;
      }

      if (this.decExponent > 39) {
         return this.isNegative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
      } else if (this.decExponent < -46) {
         return this.isNegative ? -0.0F : 0.0F;
      } else {
         this.mustSetRoundDir = true;
         double dValue = this.doubleValue();
         return this.stickyRound(dValue);
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
