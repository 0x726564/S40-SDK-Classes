package javax.microedition.rms;

class RecordEnumerationImpl implements RecordEnumeration, RecordListener {
   private RecordStore recordStore;
   private RecordFilter filter;
   private RecordComparator comparator;
   private boolean beObserver;
   private int index;
   private int[] records;
   private static final int NO_SUCH_RECORD = -1;

   private RecordEnumerationImpl() {
   }

   RecordEnumerationImpl(RecordStore var1, RecordFilter var2, RecordComparator var3, boolean var4) {
      this.recordStore = var1;
      this.filter = var2;
      this.comparator = var3;
      this.records = new int[0];
      this.keepUpdated(var4);
      if (!var4) {
         this.rebuild();
      }

   }

   public synchronized int numRecords() {
      this.checkDestroyed();
      return this.records.length;
   }

   public synchronized byte[] nextRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException {
      this.checkDestroyed();
      return this.recordStore.getRecord(this.nextRecordId());
   }

   public synchronized int nextRecordId() throws InvalidRecordIDException {
      this.checkDestroyed();
      if (this.index == this.records.length - 1) {
         throw new InvalidRecordIDException();
      } else {
         if (this.index == -1) {
            this.index = 0;
         } else {
            ++this.index;
         }

         return this.records[this.index];
      }
   }

   public synchronized byte[] previousRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException {
      this.checkDestroyed();
      return this.recordStore.getRecord(this.previousRecordId());
   }

   public synchronized int previousRecordId() throws InvalidRecordIDException {
      this.checkDestroyed();
      if (this.index != 0 && this.records.length != 0) {
         if (this.index == -1) {
            this.index = this.records.length - 1;
         } else {
            --this.index;
         }

         return this.records[this.index];
      } else {
         throw new InvalidRecordIDException();
      }
   }

   public boolean hasNextElement() {
      this.checkDestroyed();
      if (!this.recordStore.isOpen()) {
         return false;
      } else {
         return this.index != this.records.length - 1;
      }
   }

   public boolean hasPreviousElement() {
      this.checkDestroyed();
      if (this.records.length != 0 && this.recordStore.isOpen()) {
         return this.index != 0;
      } else {
         return false;
      }
   }

   public void reset() {
      this.checkDestroyed();
      this.index = -1;
   }

   public void rebuild() {
      this.checkDestroyed();
      synchronized(this.recordStore.rsLock) {
         int[] var2 = this.recordStore.getRecordIDs();
         this.reFilterSort(var2);
      }
   }

   public void keepUpdated(boolean var1) {
      this.checkDestroyed();
      if (var1 != this.beObserver) {
         this.beObserver = var1;
         if (var1) {
            this.recordStore.addRecordListener(this);
            this.rebuild();
         } else {
            this.recordStore.removeRecordListener(this);
         }
      }

   }

   public boolean isKeptUpdated() {
      this.checkDestroyed();
      return this.beObserver;
   }

   public synchronized void recordAdded(RecordStore var1, int var2) {
      this.checkDestroyed();
      synchronized(var1.rsLock) {
         this.filterAdd(var2);
      }
   }

   public synchronized void recordChanged(RecordStore var1, int var2) {
      this.checkDestroyed();
      int var3 = this.findIndexOfRecord(var2);
      if (var3 >= 0) {
         this.removeRecordAtIndex(var3);
         synchronized(var1.rsLock) {
            this.filterAdd(var2);
         }
      }
   }

   public synchronized void recordDeleted(RecordStore var1, int var2) {
      this.checkDestroyed();
      int var3 = this.findIndexOfRecord(var2);
      if (var3 >= 0) {
         this.removeRecordAtIndex(var3);
      }
   }

   public synchronized void destroy() {
      this.checkDestroyed();
      if (this.beObserver) {
         this.recordStore.removeRecordListener(this);
      }

      this.filter = null;
      this.comparator = null;
      this.records = null;
      this.recordStore = null;
   }

   private void checkDestroyed() {
      if (this.recordStore == null) {
         throw new IllegalStateException();
      }
   }

   private void filterAdd(int var1) {
      int var2 = -1;
      if (this.filter != null) {
         try {
            if (!this.filter.matches(this.recordStore.getRecord(var1))) {
               return;
            }
         } catch (RecordStoreException var6) {
            return;
         }
      }

      int[] var3 = new int[this.records.length + 1];
      var3[0] = var1;
      System.arraycopy(this.records, 0, var3, 1, this.records.length);
      this.records = var3;
      if (this.comparator != null) {
         try {
            var2 = this.sortInsert();
         } catch (RecordStoreException var5) {
         }
      }

      if (this.index != -1 && var2 != -1 && var2 < this.index) {
         ++this.index;
      }

   }

   private int sortInsert() throws RecordStoreException {
      int var2 = 0;

      for(int var3 = 1; var2 < this.records.length - 1 && this.comparator.compare(this.recordStore.getRecord(this.records[var2]), this.recordStore.getRecord(this.records[var3])) == 1; ++var3) {
         int var1 = this.records[var2];
         this.records[var2] = this.records[var3];
         this.records[var3] = var1;
         ++var2;
      }

      return var2;
   }

   private int findIndexOfRecord(int var1) {
      int var3 = -1;

      for(int var2 = this.records.length - 1; var2 >= 0; --var2) {
         if (this.records[var2] == var1) {
            var3 = var2;
            break;
         }
      }

      return var3;
   }

   private void removeRecordAtIndex(int var1) {
      int[] var2 = new int[this.records.length - 1];
      if (var1 < this.records.length) {
         System.arraycopy(this.records, 0, var2, 0, var1);
         System.arraycopy(this.records, var1 + 1, var2, var1, this.records.length - var1 - 1);
      } else {
         System.arraycopy(this.records, 0, var2, 0, this.records.length - 1);
      }

      this.records = var2;
      if (this.index != -1 && var1 <= this.index) {
         --this.index;
      } else if (this.index == this.records.length) {
         --this.index;
      }

   }

   private void reFilterSort(int[] var1) {
      int var2 = 0;
      if (this.filter == null) {
         this.records = var1;
      } else {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            try {
               if (this.filter.matches(this.recordStore.getRecord(var1[var3]))) {
                  if (var2 != var3) {
                     var1[var2++] = var1[var3];
                  } else {
                     ++var2;
                  }
               }
            } catch (RecordStoreException var6) {
            }
         }

         this.records = new int[var2];
         System.arraycopy(var1, 0, this.records, 0, var2);
      }

      if (this.comparator != null) {
         try {
            this.QuickSort(this.records, 0, this.records.length - 1, this.comparator);
         } catch (RecordStoreException var5) {
         }
      }

      this.reset();
   }

   private void QuickSort(int[] var1, int var2, int var3, RecordComparator var4) throws RecordStoreException {
      int var5 = var2;
      int var6 = var3;
      if (var3 > var2) {
         int var7 = (var2 + var3) / 2;
         int var8 = var1[var7];
         byte[] var9 = this.recordStore.getRecord(var8);

         while(var5 <= var6) {
            while(var5 < var3 && var4.compare(this.recordStore.getRecord(var1[var5]), var9) == -1) {
               ++var5;
            }

            while(var6 > var2 && var4.compare(this.recordStore.getRecord(var1[var6]), var9) == 1) {
               --var6;
            }

            if (var5 <= var6) {
               int var10 = var1[var5];
               var1[var5] = var1[var6];
               var1[var6] = var10;
               ++var5;
               --var6;
            }
         }

         if (var2 < var6) {
            this.QuickSort(var1, var2, var6, var4);
         }

         if (var5 < var3) {
            this.QuickSort(var1, var5, var3, var4);
         }
      }

   }
}
