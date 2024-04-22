package java.lang;

class FDBigInt {
   int nWords;
   int[] data;

   public FDBigInt(int v) {
      this.nWords = 1;
      this.data = new int[1];
      this.data[0] = v;
   }

   public FDBigInt(long v) {
      this.data = new int[2];
      this.data[0] = (int)v;
      this.data[1] = (int)(v >>> 32);
      this.nWords = this.data[1] == 0 ? 1 : 2;
   }

   public FDBigInt(FDBigInt other) {
      this.data = new int[this.nWords = other.nWords];
      System.arraycopy(other.data, 0, this.data, 0, this.nWords);
   }

   private FDBigInt(int[] d, int n) {
      this.data = d;
      this.nWords = n;
   }

   public FDBigInt(long seed, char[] digit, int nd0, int nd) {
      int n = (nd + 8) / 9;
      if (n < 2) {
         n = 2;
      }

      this.data = new int[n];
      this.data[0] = (int)seed;
      this.data[1] = (int)(seed >>> 32);
      this.nWords = this.data[1] == 0 ? 1 : 2;
      int i = nd0;
      int limit = nd - 5;

      int v;
      int factor;
      while(i < limit) {
         factor = i + 5;

         for(v = digit[i++] - 48; i < factor; v = 10 * v + digit[i++] - 48) {
         }

         this.multaddMe(100000, v);
      }

      factor = 1;

      for(v = 0; i < nd; factor *= 10) {
         v = 10 * v + digit[i++] - 48;
      }

      if (factor != 1) {
         this.multaddMe(factor, v);
      }

   }

   public void lshiftMe(int c) throws IllegalArgumentException {
      if (c <= 0) {
         if (c != 0) {
            throw new IllegalArgumentException("negative shift count");
         }
      } else {
         int wordcount = c >> 5;
         int bitcount = c & 31;
         int anticount = 32 - bitcount;
         int[] t = this.data;
         int[] s = this.data;
         if (this.nWords + wordcount + 1 > t.length) {
            t = new int[this.nWords + wordcount + 1];
         }

         int target = this.nWords + wordcount;
         int src = this.nWords - 1;
         if (bitcount == 0) {
            System.arraycopy(s, 0, t, wordcount, this.nWords);
            target = wordcount - 1;
         } else {
            int var10001;
            int var10002;
            for(t[target--] = s[src] >>> anticount; src >= 1; t[var10001] = var10002 | s[src] >>> anticount) {
               var10001 = target--;
               var10002 = s[src] << bitcount;
               --src;
            }

            t[target--] = s[src] << bitcount;
         }

         while(target >= 0) {
            t[target--] = 0;
         }

         this.data = t;

         for(this.nWords += wordcount + 1; this.nWords > 1 && this.data[this.nWords - 1] == 0; --this.nWords) {
         }

      }
   }

   public int normalizeMe() throws IllegalArgumentException {
      int wordcount = 0;
      int bitcount = 0;
      int v = 0;

      int src;
      for(src = this.nWords - 1; src >= 0 && (v = this.data[src]) == 0; --src) {
         ++wordcount;
      }

      if (src < 0) {
         throw new IllegalArgumentException("zero value");
      } else {
         this.nWords -= wordcount;
         if ((v & -268435456) != 0) {
            for(bitcount = 32; (v & -268435456) != 0; --bitcount) {
               v >>>= 1;
            }
         } else {
            while(v <= 1048575) {
               v <<= 8;
               bitcount += 8;
            }

            while(v <= 134217727) {
               v <<= 1;
               ++bitcount;
            }
         }

         if (bitcount != 0) {
            this.lshiftMe(bitcount);
         }

         return bitcount;
      }
   }

   public FDBigInt mult(int iv) {
      long v = (long)iv;
      int[] r = new int[v * ((long)this.data[this.nWords - 1] & 4294967295L) > 268435455L ? this.nWords + 1 : this.nWords];
      long p = 0L;

      for(int i = 0; i < this.nWords; ++i) {
         p += v * ((long)this.data[i] & 4294967295L);
         r[i] = (int)p;
         p >>>= 32;
      }

      if (p == 0L) {
         return new FDBigInt(r, this.nWords);
      } else {
         r[this.nWords] = (int)p;
         return new FDBigInt(r, this.nWords + 1);
      }
   }

   public void multaddMe(int iv, int addend) {
      long v = (long)iv;
      long p = v * ((long)this.data[0] & 4294967295L) + ((long)addend & 4294967295L);
      this.data[0] = (int)p;
      p >>>= 32;

      for(int i = 1; i < this.nWords; ++i) {
         p += v * ((long)this.data[i] & 4294967295L);
         this.data[i] = (int)p;
         p >>>= 32;
      }

      if (p != 0L) {
         this.data[this.nWords] = (int)p;
         ++this.nWords;
      }

   }

   public FDBigInt mult(FDBigInt other) {
      int[] r = new int[this.nWords + other.nWords];

      int i;
      for(i = 0; i < this.nWords; ++i) {
         long v = (long)this.data[i] & 4294967295L;
         long p = 0L;

         int j;
         for(j = 0; j < other.nWords; ++j) {
            p += ((long)r[i + j] & 4294967295L) + v * ((long)other.data[j] & 4294967295L);
            r[i + j] = (int)p;
            p >>>= 32;
         }

         r[i + j] = (int)p;
      }

      for(i = r.length - 1; i > 0 && r[i] == 0; --i) {
      }

      return new FDBigInt(r, i + 1);
   }

   public FDBigInt add(FDBigInt other) {
      long c = 0L;
      int[] a;
      int[] b;
      int n;
      int m;
      if (this.nWords >= other.nWords) {
         a = this.data;
         n = this.nWords;
         b = other.data;
         m = other.nWords;
      } else {
         a = other.data;
         n = other.nWords;
         b = this.data;
         m = this.nWords;
      }

      int[] r = new int[n];

      int i;
      for(i = 0; i < n; ++i) {
         c += (long)a[i] & 4294967295L;
         if (i < m) {
            c += (long)b[i] & 4294967295L;
         }

         r[i] = (int)c;
         c >>= 32;
      }

      if (c != 0L) {
         int[] s = new int[r.length + 1];
         System.arraycopy(r, 0, s, 0, r.length);
         s[i++] = (int)c;
         return new FDBigInt(s, i);
      } else {
         return new FDBigInt(r, i);
      }
   }

   public FDBigInt sub(FDBigInt other) {
      int[] r = new int[this.nWords];
      int n = this.nWords;
      int m = other.nWords;
      int nzeros = 0;
      long c = 0L;

      int i;
      for(i = 0; i < n; ++i) {
         c += (long)this.data[i] & 4294967295L;
         if (i < m) {
            c -= (long)other.data[i] & 4294967295L;
         }

         if ((r[i] = (int)c) == 0) {
            ++nzeros;
         } else {
            nzeros = 0;
         }

         c >>= 32;
      }

      if (c != 0L) {
         throw new RuntimeException("Assertion botch: borrow out of subtract");
      } else {
         do {
            if (i >= m) {
               return new FDBigInt(r, n - nzeros);
            }
         } while(other.data[i++] == 0);

         throw new RuntimeException("Assertion botch: negative result of subtract");
      }
   }

   public int cmp(FDBigInt other) {
      int i;
      int a;
      if (this.nWords > other.nWords) {
         a = other.nWords - 1;

         for(i = this.nWords - 1; i > a; --i) {
            if (this.data[i] != 0) {
               return 1;
            }
         }
      } else if (this.nWords < other.nWords) {
         a = this.nWords - 1;

         for(i = other.nWords - 1; i > a; --i) {
            if (other.data[i] != 0) {
               return -1;
            }
         }
      } else {
         i = this.nWords - 1;
      }

      while(i > 0 && this.data[i] == other.data[i]) {
         --i;
      }

      a = this.data[i];
      int b = other.data[i];
      if (a < 0) {
         return b < 0 ? a - b : 1;
      } else {
         return b < 0 ? -1 : a - b;
      }
   }

   public int quoRemIteration(FDBigInt S) throws IllegalArgumentException {
      if (this.nWords != S.nWords) {
         throw new IllegalArgumentException("disparate values");
      } else {
         int n = this.nWords - 1;
         long q = ((long)this.data[n] & 4294967295L) / (long)S.data[n];
         long diff = 0L;

         for(int i = 0; i <= n; ++i) {
            diff += ((long)this.data[i] & 4294967295L) - q * ((long)S.data[i] & 4294967295L);
            this.data[i] = (int)diff;
            diff >>= 32;
         }

         int i;
         long sum;
         if (diff != 0L) {
            for(sum = 0L; sum == 0L; --q) {
               sum = 0L;

               for(i = 0; i <= n; ++i) {
                  sum += ((long)this.data[i] & 4294967295L) + ((long)S.data[i] & 4294967295L);
                  this.data[i] = (int)sum;
                  sum >>= 32;
               }

               if (sum != 0L && sum != 1L) {
                  throw new RuntimeException("Assertion botch: " + sum + " carry out of division correction");
               }
            }
         }

         sum = 0L;

         for(i = 0; i <= n; ++i) {
            sum += 10L * ((long)this.data[i] & 4294967295L);
            this.data[i] = (int)sum;
            sum >>= 32;
         }

         if (sum != 0L) {
            throw new RuntimeException("Assertion botch: carry out of *10");
         } else {
            return (int)q;
         }
      }
   }

   public long longValue() {
      int i;
      for(i = this.nWords - 1; i > 1; --i) {
         if (this.data[i] != 0) {
            throw new RuntimeException("Assertion botch: value too big");
         }
      }

      switch(i) {
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
      StringBuffer r = new StringBuffer(30);
      r.append('[');
      int i = Math.min(this.nWords - 1, this.data.length - 1);
      if (this.nWords > this.data.length) {
         r.append("(" + this.data.length + "<" + this.nWords + "!)");
      }

      while(i > 0) {
         r.append(Integer.toHexString(this.data[i]));
         r.append(' ');
         --i;
      }

      r.append(Integer.toHexString(this.data[0]));
      r.append(']');
      return new String(r);
   }
}
