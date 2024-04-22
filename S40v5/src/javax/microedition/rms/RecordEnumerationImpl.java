package javax.microedition.rms;

class RecordEnumerationImpl implements RecordEnumeration, RecordListener {
   private RecordStore t;
   private RecordFilter u;
   private RecordComparator v;
   private boolean z;
   private int index;
   private int[] A;

   private RecordEnumerationImpl() {
   }

   RecordEnumerationImpl(RecordStore var1, RecordFilter var2, RecordComparator var3, boolean var4) {
      this.t = var1;
      this.u = var2;
      this.v = var3;
      this.A = new int[0];
      this.keepUpdated(var4);
      if (!var4) {
         this.rebuild();
      }

   }

   public synchronized int numRecords() {
      this.k();
      return this.A.length;
   }

   public synchronized byte[] nextRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException {
      this.k();
      return this.t.getRecord(this.nextRecordId());
   }

   public synchronized int nextRecordId() throws InvalidRecordIDException {
      this.k();
      if (this.index == this.A.length - 1) {
         throw new InvalidRecordIDException();
      } else {
         if (this.index == -1) {
            this.index = 0;
         } else {
            ++this.index;
         }

         return this.A[this.index];
      }
   }

   public synchronized byte[] previousRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException {
      this.k();
      return this.t.getRecord(this.previousRecordId());
   }

   public synchronized int previousRecordId() throws InvalidRecordIDException {
      this.k();
      if (this.index != 0 && this.A.length != 0) {
         if (this.index == -1) {
            this.index = this.A.length - 1;
         } else {
            --this.index;
         }

         return this.A[this.index];
      } else {
         throw new InvalidRecordIDException();
      }
   }

   public boolean hasNextElement() {
      this.k();
      if (!this.t.isOpen()) {
         return false;
      } else {
         return this.index != this.A.length - 1;
      }
   }

   public boolean hasPreviousElement() {
      this.k();
      if (this.A.length != 0 && this.t.isOpen()) {
         return this.index != 0;
      } else {
         return false;
      }
   }

   public void reset() {
      this.k();
      this.index = -1;
   }

   public void rebuild() {
      this.k();
      synchronized(this.t.gH) {
         int[] var2 = this.t.getRecordIDs();
         var2 = var2;
         RecordEnumerationImpl var8 = this;
         int var3 = 0;
         if (this.u == null) {
            this.A = var2;
         } else {
            for(int var4 = 0; var4 < var2.length; ++var4) {
               try {
                  if (var8.u.matches(var8.t.getRecord(var2[var4]))) {
                     if (var3 != var4) {
                        var2[var3++] = var2[var4];
                     } else {
                        ++var3;
                     }
                  }
               } catch (RecordStoreException var6) {
               }
            }

            var8.A = new int[var3];
            System.arraycopy(var2, 0, var8.A, 0, var3);
         }

         if (var8.v != null) {
            try {
               var8.a(var8.A, 0, var8.A.length - 1, var8.v);
            } catch (RecordStoreException var5) {
            }
         }

         var8.reset();
      }
   }

   public void keepUpdated(boolean var1) {
      this.k();
      if (var1 != this.z) {
         this.z = var1;
         if (var1) {
            this.t.addRecordListener(this);
            this.rebuild();
            return;
         }

         this.t.removeRecordListener(this);
      }

   }

   public boolean isKeptUpdated() {
      this.k();
      return this.z;
   }

   public synchronized void recordAdded(RecordStore var1, int var2) {
      this.k();
      synchronized(var1.gH) {
         this.d(var2);
      }
   }

   public synchronized void recordChanged(RecordStore var1, int var2) {
      this.k();
      int var3;
      if ((var3 = this.e(var2)) >= 0) {
         this.f(var3);
         synchronized(var1.gH) {
            this.d(var2);
         }
      }
   }

   public synchronized void recordDeleted(RecordStore var1, int var2) {
      this.k();
      int var3;
      if ((var3 = this.e(var2)) >= 0) {
         this.f(var3);
      }
   }

   public synchronized void destroy() {
      this.k();
      if (this.z) {
         this.t.removeRecordListener(this);
      }

      this.u = null;
      this.v = null;
      this.A = null;
      this.t = null;
   }

   private void k() {
      if (this.t == null) {
         throw new IllegalStateException();
      }
   }

   private void d(int var1) {
      int var2 = -1;
      if (this.u != null) {
         try {
            if (!this.u.matches(this.t.getRecord(var1))) {
               return;
            }
         } catch (RecordStoreException var6) {
            return;
         }
      }

      int[] var3;
      (var3 = new int[this.A.length + 1])[0] = var1;
      System.arraycopy(this.A, 0, var3, 1, this.A.length);
      this.A = var3;
      if (this.v != null) {
         try {
            RecordEnumerationImpl var8 = this;
            int var4 = 0;

            for(int var5 = 1; var4 < var8.A.length - 1 && var8.v.compare(var8.t.getRecord(var8.A[var4]), var8.t.getRecord(var8.A[var5])) == 1; ++var5) {
               int var9 = var8.A[var4];
               var8.A[var4] = var8.A[var5];
               var8.A[var5] = var9;
               ++var4;
            }

            var2 = var4;
         } catch (RecordStoreException var7) {
         }
      }

      if (this.index != -1 && var2 != -1 && var2 < this.index) {
         ++this.index;
      }

   }

   private int e(int var1) {
      int var3 = -1;

      for(int var2 = this.A.length - 1; var2 >= 0; --var2) {
         if (this.A[var2] == var1) {
            var3 = var2;
            break;
         }
      }

      return var3;
   }

   private void f(int var1) {
      int[] var2 = new int[this.A.length - 1];
      if (var1 < this.A.length) {
         System.arraycopy(this.A, 0, var2, 0, var1);
         System.arraycopy(this.A, var1 + 1, var2, var1, this.A.length - var1 - 1);
      } else {
         System.arraycopy(this.A, 0, var2, 0, this.A.length - 1);
      }

      this.A = var2;
      if (this.index != -1 && var1 <= this.index) {
         --this.index;
      } else {
         if (this.index == this.A.length) {
            --this.index;
         }

      }
   }

   private void a(int[] var1, int var2, int var3, RecordComparator var4) throws RecordStoreException {
      int var5 = var2;
      int var6 = var3;
      if (var3 > var2) {
         int var7 = (var2 + var3) / 2;
         var7 = var1[var7];
         byte[] var9 = this.t.getRecord(var7);

         while(var5 <= var6) {
            while(var5 < var3 && var4.compare(this.t.getRecord(var1[var5]), var9) == -1) {
               ++var5;
            }

            while(var6 > var2 && var4.compare(this.t.getRecord(var1[var6]), var9) == 1) {
               --var6;
            }

            if (var5 <= var6) {
               int var8 = var1[var5];
               var1[var5] = var1[var6];
               var1[var6] = var8;
               ++var5;
               --var6;
            }
         }

         if (var2 < var6) {
            this.a(var1, var2, var6, var4);
         }

         if (var5 < var3) {
            this.a(var1, var5, var3, var4);
         }
      }

   }
}
