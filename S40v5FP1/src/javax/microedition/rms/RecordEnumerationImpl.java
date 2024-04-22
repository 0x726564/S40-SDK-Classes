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

   RecordEnumerationImpl(RecordStore recordStore, RecordFilter filter, RecordComparator comparator, boolean keepUpdated) {
      this.recordStore = recordStore;
      this.filter = filter;
      this.comparator = comparator;
      this.records = new int[0];
      this.keepUpdated(keepUpdated);
      if (!keepUpdated) {
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
      RecordStore var10000 = this.recordStore;
      synchronized(RecordStore.rmsAPILock) {
         int[] tmp = this.recordStore.getRecordIDs();
         this.reFilterSort(tmp);
      }
   }

   public void keepUpdated(boolean keepUpdated) {
      this.checkDestroyed();
      if (keepUpdated != this.beObserver) {
         this.beObserver = keepUpdated;
         if (keepUpdated) {
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

   public synchronized void recordAdded(RecordStore recordStore, int recordId) {
      this.checkDestroyed();
      synchronized(RecordStore.rmsAPILock) {
         this.filterAdd(recordId);
      }
   }

   public synchronized void recordChanged(RecordStore recordStore, int recordId) {
      this.checkDestroyed();
      int recIndex = this.findIndexOfRecord(recordId);
      if (recIndex >= 0) {
         this.removeRecordAtIndex(recIndex);
         synchronized(RecordStore.rmsAPILock) {
            this.filterAdd(recordId);
         }
      }
   }

   public synchronized void recordDeleted(RecordStore recordStore, int recordId) {
      this.checkDestroyed();
      int recIndex = this.findIndexOfRecord(recordId);
      if (recIndex >= 0) {
         this.removeRecordAtIndex(recIndex);
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

   private void filterAdd(int recordId) {
      int insertPoint = -1;
      if (this.filter != null) {
         try {
            if (!this.filter.matches(this.recordStore.getRecord(recordId))) {
               return;
            }
         } catch (RecordStoreException var6) {
            return;
         }
      }

      int[] newrecs = new int[this.records.length + 1];
      newrecs[0] = recordId;
      System.arraycopy(this.records, 0, newrecs, 1, this.records.length);
      this.records = newrecs;
      if (this.comparator != null) {
         try {
            insertPoint = this.sortInsert();
         } catch (RecordStoreException var5) {
         }
      }

      if (this.index != -1 && insertPoint != -1 && insertPoint < this.index) {
         ++this.index;
      }

   }

   private int sortInsert() throws RecordStoreException {
      int i = 0;

      for(int j = 1; i < this.records.length - 1 && this.comparator.compare(this.recordStore.getRecord(this.records[i]), this.recordStore.getRecord(this.records[j])) == 1; ++j) {
         int tmp = this.records[i];
         this.records[i] = this.records[j];
         this.records[j] = tmp;
         ++i;
      }

      return i;
   }

   private int findIndexOfRecord(int recordId) {
      int recIndex = -1;

      for(int idx = this.records.length - 1; idx >= 0; --idx) {
         if (this.records[idx] == recordId) {
            recIndex = idx;
            break;
         }
      }

      return recIndex;
   }

   private void removeRecordAtIndex(int recIndex) {
      int[] tmp = new int[this.records.length - 1];
      if (recIndex < this.records.length) {
         System.arraycopy(this.records, 0, tmp, 0, recIndex);
         System.arraycopy(this.records, recIndex + 1, tmp, recIndex, this.records.length - recIndex - 1);
      } else {
         System.arraycopy(this.records, 0, tmp, 0, this.records.length - 1);
      }

      this.records = tmp;
      if (this.index != -1 && recIndex <= this.index) {
         --this.index;
      } else if (this.index == this.records.length) {
         --this.index;
      }

   }

   private void reFilterSort(int[] filtered) {
      int filteredIndex = 0;
      if (this.filter == null) {
         this.records = filtered;
      } else {
         for(int i = 0; i < filtered.length; ++i) {
            try {
               if (this.filter.matches(this.recordStore.getRecord(filtered[i]))) {
                  if (filteredIndex != i) {
                     filtered[filteredIndex++] = filtered[i];
                  } else {
                     ++filteredIndex;
                  }
               }
            } catch (RecordStoreException var6) {
            }
         }

         this.records = new int[filteredIndex];
         System.arraycopy(filtered, 0, this.records, 0, filteredIndex);
      }

      if (this.comparator != null) {
         try {
            this.QuickSort(this.records, 0, this.records.length - 1, this.comparator);
         } catch (RecordStoreException var5) {
         }
      }

      this.reset();
   }

   private void QuickSort(int[] a, int lowIndex, int highIndex, RecordComparator comparator) throws RecordStoreException {
      int left = lowIndex;
      int right = highIndex;
      if (highIndex > lowIndex) {
         int ind = (lowIndex + highIndex) / 2;
         int pivotIndex = a[ind];
         byte[] pivotData = this.recordStore.getRecord(pivotIndex);

         while(left <= right) {
            while(left < highIndex && comparator.compare(this.recordStore.getRecord(a[left]), pivotData) == -1) {
               ++left;
            }

            while(right > lowIndex && comparator.compare(this.recordStore.getRecord(a[right]), pivotData) == 1) {
               --right;
            }

            if (left <= right) {
               int tmp = a[left];
               a[left] = a[right];
               a[right] = tmp;
               ++left;
               --right;
            }
         }

         if (lowIndex < right) {
            this.QuickSort(a, lowIndex, right, comparator);
         }

         if (left < highIndex) {
            this.QuickSort(a, left, highIndex, comparator);
         }
      }

   }
}
