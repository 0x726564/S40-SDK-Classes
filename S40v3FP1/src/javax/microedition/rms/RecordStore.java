package javax.microedition.rms;

import java.io.IOException;
import java.util.Vector;

public class RecordStore {
   public static final int AUTHMODE_PRIVATE = 0;
   public static final int AUTHMODE_ANY = 1;
   private static final int AUTHMODE_ANY_RO = 2;
   private static final int DB_RECORD_HEADER_LENGTH = 16;
   private static final int DB_BLOCK_SIZE = 16;
   private static final int DB_COMPACTBUFFER_SIZE = 64;
   private static Vector dbCache = new Vector(3);
   private static final Object dbCacheLock = new Object();
   private String record = null;
   private String vendor = null;
   private String suite = null;
   private String uniqueIdPath;
   private int opencount;
   private RecordStoreFile dbraf;
   Object rsLock;
   private Vector recordListener;
   private RecordStore.RecordHeaderCache recHeadCache;
   private static int INITIAL_CACHE_SIZE = 16;
   private static int MAX_CACHE_SIZE = 128;
   private static byte[] recHeadBuf = new byte[16];
   private static final Object staticBufferLock = new Object();
   private int dbNextRecordID = 1;
   private int dbVersion;
   private int dbAuthMode;
   private int dbNumLiveRecords;
   private long dbLastModified;
   private int dbFirstRecordOffset;
   private int dbFirstFreeBlockOffset;
   private int dbDataStart = 72;
   private int dbDataEnd = 72;
   private static final int RS_MIDLET_RS_HEADER_SIZE = 40;
   private static byte[] dbState = new byte[40];
   private static final int RS_NUM_LIVE = 0;
   private static final int RS_AUTHMODE = 4;
   private static final int RS_VERSION = 8;
   private static final int RS_NEXT_ID = 12;
   private static final int RS_REC_START = 16;
   private static final int RS_FREE_START = 20;
   private static final int RS_LAST_MODIFIED = 24;
   private static final int RS_DATA_START = 32;
   private static final int RS_DATA_END = 36;
   private static final int RS_COMMON_HEADER_END = 32;

   private RecordStore() {
   }

   public static void deleteRecordStore(String var0) throws RecordStoreException, RecordStoreNotFoundException {
      String var1 = RecordStoreFile.getUniqueIdPath(var0);
      synchronized(dbCacheLock) {
         for(int var4 = 0; var4 < dbCache.size(); ++var4) {
            RecordStore var3 = (RecordStore)dbCache.elementAt(var4);
            if (var3.uniqueIdPath.equals(var1)) {
               throw new RecordStoreException("deleteRecordStore error: record store is still open");
            }
         }

         if (RecordStoreFile.exists(var0, RecordStoreFile.getCurrentMidletSuiteVendor(), RecordStoreFile.getCurrentMidletSuiteName())) {
            boolean var7 = RecordStoreFile.deleteFile(var0);
            if (!var7) {
               throw new RecordStoreException("deleteRecordStore failed");
            }
         } else {
            throw new RecordStoreNotFoundException("deleteRecordStore error: file not found");
         }
      }
   }

   public static RecordStore openRecordStore(String var0, boolean var1) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
      String var2 = RecordStoreFile.getUniqueIdPath(var0);
      synchronized(dbCacheLock) {
         if (var0.length() <= 32 && var0.length() != 0) {
            RecordStore var4;
            for(int var5 = 0; var5 < dbCache.size(); ++var5) {
               var4 = (RecordStore)dbCache.elementAt(var5);
               if (var4.uniqueIdPath.equals(var2)) {
                  ++var4.opencount;
                  return var4;
               }
            }

            var4 = new RecordStore(var2, var0, var1, (String)null, (String)null);
            var4.opencount = 1;
            dbCache.addElement(var4);
            return var4;
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public static RecordStore openRecordStore(String var0, boolean var1, int var2, boolean var3) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
      RecordStore var4 = openRecordStore(var0, var1);
      var4.setMode(var2, var3);
      return var4;
   }

   public static RecordStore openRecordStore(String var0, String var1, String var2) throws RecordStoreException, RecordStoreNotFoundException {
      if (var1 != null && var2 != null) {
         synchronized(dbCacheLock) {
            if (var0.length() <= 32 && var0.length() != 0) {
               String var5 = RecordStoreFile.getUniqueIdPath(var1, var2, var0);

               RecordStore var4;
               for(int var6 = 0; var6 < dbCache.size(); ++var6) {
                  var4 = (RecordStore)dbCache.elementAt(var6);
                  if (var4.uniqueIdPath.equals(var5)) {
                     if (!var4.checkOwner() && var4.dbAuthMode == 0) {
                        throw new SecurityException();
                     }

                     ++var4.opencount;
                     return var4;
                  }
               }

               var4 = new RecordStore(var5, var0, false, var1, var2);
               var4.opencount = 1;
               dbCache.addElement(var4);
               if (!var4.checkOwner() && var4.dbAuthMode == 0) {
                  var4.closeRecordStore();
                  throw new SecurityException();
               } else {
                  return var4;
               }
            } else {
               throw new IllegalArgumentException();
            }
         }
      } else {
         throw new IllegalArgumentException("vendorName and suiteName must be non null");
      }
   }

   public void setMode(int var1, boolean var2) throws RecordStoreException {
      synchronized(this.rsLock) {
         if (!this.checkOwner()) {
            throw new SecurityException();
         } else if (var1 != 0 && var1 != 1) {
            throw new IllegalArgumentException();
         } else {
            this.dbAuthMode = var1;
            if (var1 == 1 && !var2) {
               this.dbAuthMode = 2;
            }

            this.storeDBState();
         }
      }
   }

   public void closeRecordStore() throws RecordStoreNotOpenException, RecordStoreException {
      synchronized(this.rsLock) {
         synchronized(dbCacheLock) {
            this.checkOpen();
            RecordStore var3 = null;

            for(int var4 = 0; var4 < dbCache.size(); ++var4) {
               var3 = (RecordStore)dbCache.elementAt(var4);
               if (var3 == this) {
                  --var3.opencount;
                  break;
               }
            }

            if (var3.opencount <= 0) {
               dbCache.removeElement(var3);

               try {
                  if (!this.recordListener.isEmpty()) {
                     this.recordListener.removeAllElements();
                  }

                  if (this.dbFirstFreeBlockOffset != 0) {
                     this.compactRecords();
                     this.dbraf.truncate(this.dbDataEnd);
                  }

                  this.dbraf.close();
               } catch (IOException var12) {
                  throw new RecordStoreException("error closing .db file");
               } finally {
                  this.dbraf = null;
                  this.recHeadCache = null;
               }
            }
         }

      }
   }

   public static String[] listRecordStores() {
      synchronized(dbCacheLock) {
         String[] var1 = RecordStoreFile.listRecordStores();
         return var1;
      }
   }

   public String getName() throws RecordStoreNotOpenException {
      this.checkOpen();
      return this.record;
   }

   public int getVersion() throws RecordStoreNotOpenException {
      this.checkOpen();
      return this.dbVersion;
   }

   public int getNumRecords() throws RecordStoreNotOpenException {
      this.checkOpen();
      return this.dbNumLiveRecords;
   }

   public int getSize() throws RecordStoreNotOpenException {
      this.checkOpen();
      return this.dbDataEnd;
   }

   public int getSizeAvailable() throws RecordStoreNotOpenException {
      this.checkOpen();
      int var1 = this.dbraf.spaceAvailable() - 16 - 16;
      return var1 < 0 ? 0 : var1;
   }

   public long getLastModified() throws RecordStoreNotOpenException {
      this.checkOpen();
      return this.dbLastModified;
   }

   public void addRecordListener(RecordListener var1) {
      synchronized(this.rsLock) {
         if (!this.recordListener.contains(var1)) {
            this.recordListener.addElement(var1);
         }

      }
   }

   public void removeRecordListener(RecordListener var1) {
      synchronized(this.rsLock) {
         this.recordListener.removeElement(var1);
      }
   }

   public int getNextRecordID() throws RecordStoreNotOpenException, RecordStoreException {
      this.checkOpen();
      return this.dbNextRecordID;
   }

   public int addRecord(byte[] var1, int var2, int var3) throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException {
      if (var2 < 0 || var3 < 0 || var3 > 0 && var3 + var2 > var1.length) {
         throw new ArrayIndexOutOfBoundsException();
      } else {
         synchronized(this.rsLock) {
            this.checkOpen();
            if (!this.checkWritable()) {
               throw new SecurityException();
            } else {
               int var5 = this.dbNextRecordID++;
               RecordStore.RecordHeader var6 = this.allocateNewRecordStorage(var5, var3);

               try {
                  if (var3 > 0) {
                     var6.write(var1, var2);
                  }
               } catch (IOException var9) {
                  throw new RecordStoreException("error writing new record data");
               }

               ++this.dbNumLiveRecords;
               ++this.dbVersion;
               this.storeDBState();
               this.notifyRecordAddedListeners(var5);
               return var5;
            }
         }
      }
   }

   public void deleteRecord(int var1) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
      synchronized(this.rsLock) {
         this.checkOpen();
         if (!this.checkWritable()) {
            throw new SecurityException();
         } else {
            RecordStore.RecordHeader var3 = null;

            try {
               var3 = this.findRecord(var1, false);
               this.freeRecord(var3);
               this.recHeadCache.invalidate(var3.id);
            } catch (IOException var6) {
               throw new RecordStoreException("error updating file after record deletion");
            }

            --this.dbNumLiveRecords;
            ++this.dbVersion;
            this.storeDBState();
            this.notifyRecordDeletedListeners(var1);
         }
      }
   }

   public int getRecordSize(int var1) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
      synchronized(this.rsLock) {
         this.checkOpen();

         int var10000;
         try {
            RecordStore.RecordHeader var3 = this.findRecord(var1, true);
            var10000 = var3.dataLenOrNextFree;
         } catch (IOException var5) {
            throw new RecordStoreException("error reading record data");
         }

         return var10000;
      }
   }

   public int getRecord(int var1, byte[] var2, int var3) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
      synchronized(this.rsLock) {
         this.checkOpen();

         RecordStore.RecordHeader var5;
         try {
            var5 = this.findRecord(var1, true);
            var5.read(var2, var3);
         } catch (IOException var8) {
            throw new RecordStoreException("error reading record data");
         }

         return var5.dataLenOrNextFree;
      }
   }

   public byte[] getRecord(int var1) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
      synchronized(this.rsLock) {
         this.checkOpen();
         boolean var3 = false;
         Object var4 = null;

         byte[] var9;
         try {
            RecordStore.RecordHeader var5 = this.findRecord(var1, true);
            if (var5.dataLenOrNextFree == 0) {
               Object var10000 = null;
               return (byte[])var10000;
            }

            var9 = new byte[var5.dataLenOrNextFree];
            var5.read(var9, 0);
         } catch (IOException var7) {
            throw new RecordStoreException("error reading record data");
         }

         return var9;
      }
   }

   public void setRecord(int var1, byte[] var2, int var3, int var4) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException, RecordStoreFullException {
      if (var3 >= 0 && var4 >= 0 && (var4 <= 0 || var4 + var3 <= var2.length)) {
         synchronized(this.rsLock) {
            this.checkOpen();
            if (!this.checkWritable()) {
               throw new SecurityException();
            } else {
               RecordStore.RecordHeader var6 = null;
               RecordStore.RecordHeader var7 = null;

               try {
                  var6 = this.findRecord(var1, false);
               } catch (IOException var13) {
                  throw new RecordStoreException("error finding record data");
               }

               if (var4 <= var6.blockSize - 16) {
                  int var8 = this.getAllocSize(var4);
                  if (var6.blockSize - var8 >= 32) {
                     this.splitRecord(var6, var8);
                  }

                  var6.dataLenOrNextFree = var4;

                  try {
                     var6.store();
                     this.recHeadCache.insert(var6);
                     if (var4 > 0) {
                        var6.write(var2, var3);
                     }
                  } catch (IOException var12) {
                     throw new RecordStoreException("error writing record data");
                  }
               } else {
                  this.freeRecord(var6);
                  var7 = this.allocateNewRecordStorage(var1, var4);

                  try {
                     if (var4 > 0) {
                        var7.write(var2, var3);
                     }
                  } catch (IOException var11) {
                     throw new RecordStoreException("error moving record data");
                  }
               }

               ++this.dbVersion;
               this.storeDBState();
               this.notifyRecordChangedListeners(var1);
            }
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public RecordEnumeration enumerateRecords(RecordFilter var1, RecordComparator var2, boolean var3) throws RecordStoreNotOpenException {
      this.checkOpen();
      return new RecordEnumerationImpl(this, var1, var2, var3);
   }

   private RecordStore.RecordHeader findRecord(int var1, boolean var2) throws InvalidRecordIDException, IOException {
      int var4 = this.dbFirstRecordOffset;
      if (var4 == 0) {
         throw new InvalidRecordIDException();
      } else {
         RecordStore.RecordHeader var3 = this.recHeadCache.get(var1);
         if (var3 != null) {
            return var3;
         } else {
            for(var3 = new RecordStore.RecordHeader(); var4 != 0; var4 = var3.nextOffset) {
               var3.load(var4);
               if (var3.id == var1) {
                  break;
               }
            }

            if (var4 == 0) {
               throw new InvalidRecordIDException();
            } else {
               if (var2) {
                  this.recHeadCache.insert(var3);
               }

               return var3;
            }
         }
      }
   }

   private int getAllocSize(int var1) {
      int var2 = 16 + var1;
      int var3 = 16 - var2 % 16;
      if (var3 != 16) {
         var2 += var3;
      }

      return var2;
   }

   private RecordStore.RecordHeader allocateNewRecordStorage(int var1, int var2) throws RecordStoreException, RecordStoreFullException {
      int var3 = this.getAllocSize(var2);
      boolean var4 = false;
      RecordStore.RecordHeader var5 = new RecordStore.RecordHeader();

      try {
         for(int var6 = this.dbFirstFreeBlockOffset; var6 != 0; var6 = var5.dataLenOrNextFree) {
            var5.load(var6);
            if (var5.blockSize >= var3) {
               var4 = true;
               break;
            }
         }
      } catch (IOException var9) {
         throw new RecordStoreException("error finding first fit block");
      }

      if (!var4) {
         if (this.dbraf.spaceAvailable() < var3) {
            throw new RecordStoreFullException();
         }

         var5 = new RecordStore.RecordHeader(this.dbDataEnd, var1, this.dbFirstRecordOffset, var3, var2);

         try {
            var5.store();
         } catch (IOException var8) {
            throw new RecordStoreException("error writing new record data");
         }

         this.dbFirstRecordOffset = this.dbDataEnd;
         this.dbDataEnd += var3;
      } else {
         if (var5.id != -1) {
            throw new RecordStoreException("ALLOC ERR " + var5.id + " is not a free block!");
         }

         this.removeFreeBlock(var5);
         var5.id = var1;
         if (var5.blockSize - var3 >= 32) {
            this.splitRecord(var5, var3);
         }

         var5.dataLenOrNextFree = var2;

         try {
            var5.store();
         } catch (IOException var7) {
            throw new RecordStoreException("error writing free block after alloc");
         }
      }

      this.recHeadCache.insert(var5);
      return var5;
   }

   private void splitRecord(RecordStore.RecordHeader var1, int var2) throws RecordStoreException {
      int var4 = var1.blockSize - var2;
      int var5 = var1.blockSize;
      var1.blockSize = var2;
      if (var1.offset != this.dbFirstRecordOffset) {
         int var6 = var1.offset + var2;
         RecordStore.RecordHeader var3 = new RecordStore.RecordHeader(var6, -1, var1.offset, var4, 0);

         try {
            this.freeRecord(var3);
            RecordStore.RecordHeader var7 = new RecordStore.RecordHeader(var1.offset + var5);
            var7.nextOffset = var6;
            var7.store();
            this.recHeadCache.invalidate(var7.id);
            this.storeDBState();
         } catch (IOException var8) {
            throw new RecordStoreException("splitRecord error");
         }
      } else {
         this.dbDataEnd = var1.offset + var1.blockSize;
      }

   }

   private void freeRecord(RecordStore.RecordHeader var1) throws RecordStoreException {
      try {
         if (var1.offset == this.dbFirstRecordOffset) {
            int var2 = -1;
            RecordStore.RecordHeader var3 = var1;

            while(var3.nextOffset != 0 && var2 == -1) {
               var3 = new RecordStore.RecordHeader(var3.nextOffset);
               var2 = var3.id;
               if (var2 == -1) {
                  this.dbFirstFreeBlockOffset = var3.dataLenOrNextFree;
               }
            }

            if (var2 == -1) {
               this.dbFirstRecordOffset = 0;
               this.dbDataEnd = this.dbDataStart;
               this.dbFirstFreeBlockOffset = 0;
            } else {
               this.dbFirstRecordOffset = var3.offset;
               this.dbDataEnd = var3.offset + var3.blockSize;
               if (this.dbFirstFreeBlockOffset > this.dbDataEnd) {
                  RecordStore.RecordHeader var4 = new RecordStore.RecordHeader(this.dbFirstFreeBlockOffset);
                  this.dbFirstFreeBlockOffset = var4.dataLenOrNextFree;
               }
            }

            this.dbraf.truncate(this.dbDataEnd);
         } else {
            var1.id = -1;
            var1.dataLenOrNextFree = this.dbFirstFreeBlockOffset;
            this.dbFirstFreeBlockOffset = var1.offset;
            var1.store();
         }

      } catch (IOException var5) {
         throw new RecordStoreException("free record failed");
      }
   }

   private void removeFreeBlock(RecordStore.RecordHeader var1) throws RecordStoreException {
      RecordStore.RecordHeader var2 = new RecordStore.RecordHeader();
      RecordStore.RecordHeader var3 = new RecordStore.RecordHeader();
      RecordStore.RecordHeader var4 = null;

      try {
         for(int var5 = this.dbFirstFreeBlockOffset; var5 != 0; var2 = var4) {
            var2.load(var5);
            if (var2.offset == var1.offset) {
               if (var2.id != -1) {
                  throw new RecordStoreException("removeFreeBlock id is not -1");
               }

               if (var3.offset == 0) {
                  this.dbFirstFreeBlockOffset = var2.dataLenOrNextFree;
               } else {
                  var3.dataLenOrNextFree = var2.dataLenOrNextFree;
                  var3.store();
               }
            }

            var5 = var2.dataLenOrNextFree;
            var4 = var3;
            var3 = var2;
         }

      } catch (IOException var6) {
         throw new RecordStoreException("removeFreeBlock block not found");
      }
   }

   private void storeDBState() throws RecordStoreException {
      synchronized(staticBufferLock) {
         try {
            this.dbLastModified = System.currentTimeMillis();
            putInt(this.dbNumLiveRecords, dbState, 0);
            putInt(this.dbAuthMode, dbState, 4);
            putInt(this.dbVersion, dbState, 8);
            putInt(this.dbNextRecordID, dbState, 12);
            putInt(this.dbFirstRecordOffset, dbState, 16);
            putInt(this.dbFirstFreeBlockOffset, dbState, 20);
            putLong(this.dbLastModified, dbState, 24);
            putInt(this.dbDataStart, dbState, 32);
            putInt(this.dbDataEnd, dbState, 36);
            this.dbraf.seek(32);
            this.dbraf.write(dbState, 0, dbState.length);
         } catch (IOException var4) {
            throw new RecordStoreException("error writing record store attributes");
         }

      }
   }

   boolean isOpen() {
      return this.dbraf != null;
   }

   private void checkOpen() throws RecordStoreNotOpenException {
      if (this.dbraf == null) {
         throw new RecordStoreNotOpenException();
      }
   }

   private void notifyRecordChangedListeners(int var1) {
      for(int var2 = 0; var2 < this.recordListener.size(); ++var2) {
         RecordListener var3 = (RecordListener)this.recordListener.elementAt(var2);
         var3.recordChanged(this, var1);
      }

   }

   private void notifyRecordAddedListeners(int var1) {
      for(int var2 = 0; var2 < this.recordListener.size(); ++var2) {
         RecordListener var3 = (RecordListener)this.recordListener.elementAt(var2);
         var3.recordAdded(this, var1);
      }

   }

   private void notifyRecordDeletedListeners(int var1) {
      for(int var2 = 0; var2 < this.recordListener.size(); ++var2) {
         RecordListener var3 = (RecordListener)this.recordListener.elementAt(var2);
         var3.recordDeleted(this, var1);
      }

   }

   static int getInt(byte[] var0, int var1) {
      byte var2 = var0[var1++];
      int var3 = var2 << 8 | var0[var1++] & 255;
      var3 = var3 << 8 | var0[var1++] & 255;
      var3 = var3 << 8 | var0[var1++] & 255;
      return var3;
   }

   static long getLong(byte[] var0, int var1) {
      long var2 = (long)var0[var1++];
      var2 = var2 << 8 | (long)var0[var1++] & 255L;
      var2 = var2 << 8 | (long)var0[var1++] & 255L;
      var2 = var2 << 8 | (long)var0[var1++] & 255L;
      var2 = var2 << 8 | (long)var0[var1++] & 255L;
      var2 = var2 << 8 | (long)var0[var1++] & 255L;
      var2 = var2 << 8 | (long)var0[var1++] & 255L;
      var2 = var2 << 8 | (long)var0[var1++] & 255L;
      return var2;
   }

   static int putInt(int var0, byte[] var1, int var2) {
      var1[var2++] = (byte)(var0 >> 24 & 255);
      var1[var2++] = (byte)(var0 >> 16 & 255);
      var1[var2++] = (byte)(var0 >> 8 & 255);
      var1[var2] = (byte)(var0 & 255);
      return 4;
   }

   static int putLong(long var0, byte[] var2, int var3) {
      var2[var3++] = (byte)((int)(var0 >> 56 & 255L));
      var2[var3++] = (byte)((int)(var0 >> 48 & 255L));
      var2[var3++] = (byte)((int)(var0 >> 40 & 255L));
      var2[var3++] = (byte)((int)(var0 >> 32 & 255L));
      var2[var3++] = (byte)((int)(var0 >> 24 & 255L));
      var2[var3++] = (byte)((int)(var0 >> 16 & 255L));
      var2[var3++] = (byte)((int)(var0 >> 8 & 255L));
      var2[var3] = (byte)((int)(var0 & 255L));
      return 8;
   }

   int[] getRecordIDs() {
      if (this.dbraf == null) {
         return null;
      } else {
         int var1 = 0;
         int[] var2 = new int[this.dbNumLiveRecords];
         int var3 = this.dbFirstRecordOffset;
         RecordStore.RecordHeader var4 = new RecordStore.RecordHeader();

         try {
            for(; var3 != 0; var3 = var4.nextOffset) {
               var4.load(var3);
               if (var4.id > 0) {
                  var2[var1++] = var4.id;
               }
            }

            return var2;
         } catch (IOException var6) {
            return null;
         }
      }
   }

   private void refreshCache() throws RecordStoreException {
      if (this.dbraf != null) {
         int var1 = this.dbFirstRecordOffset;

         try {
            RecordStore.RecordHeader var2;
            for(; var1 != 0; var1 = var2.nextOffset) {
               var2 = new RecordStore.RecordHeader();
               var2.load(var1);
               if (var2.nextOffset >= var1) {
                  throw new RecordStoreException("Corrupt RMS file detected");
               }

               if (var2.id > 0) {
                  this.recHeadCache.insert(var2);
               }
            }

         } catch (IOException var3) {
         }
      }
   }

   private void compactRecords() throws RecordStoreNotOpenException, RecordStoreException {
      int var1 = this.dbDataStart;
      int var2 = 0;
      byte[] var5 = new byte[64];
      RecordStore.RecordHeader var6 = new RecordStore.RecordHeader();
      int var7 = 0;

      while(var1 < this.dbDataEnd) {
         try {
            var6.load(var1);
         } catch (IOException var10) {
         }

         if (var6.id == -1) {
            if (var2 == 0) {
               var2 = var1;
            }

            var1 += var6.blockSize;
         } else if (var2 == 0) {
            var7 = var1;
            var1 += var6.blockSize;
         } else {
            var6.offset = var2;
            var6.nextOffset = var7;

            try {
               var6.store();
               var1 += 16;
               var2 += 16;

               int var4;
               for(int var3 = var6.blockSize - 16; var3 > 0; var3 -= var4) {
                  if (var3 < 64) {
                     var4 = var3;
                  } else {
                     var4 = 64;
                  }

                  this.dbraf.seek(var1);
                  this.dbraf.read(var5, 0, var4);
                  this.dbraf.seek(var2);
                  this.dbraf.write(var5, 0, var4);
                  var1 += var4;
                  var2 += var4;
               }
            } catch (IOException var11) {
            }

            var7 = var2;
         }
      }

      if (var6.offset != 0) {
         this.dbDataEnd = var6.offset + var6.blockSize;
      }

      this.dbFirstRecordOffset = var6.offset;
      this.dbFirstFreeBlockOffset = 0;
      this.storeDBState();
   }

   private RecordStore(String var1, String var2, boolean var3, String var4, String var5) throws RecordStoreException, RecordStoreNotFoundException {
      this.record = var2;
      if (var4 == null) {
         this.vendor = RecordStoreFile.getCurrentMidletSuiteVendor();
      } else {
         this.vendor = var4;
      }

      if (var5 == null) {
         this.suite = RecordStoreFile.getCurrentMidletSuiteName();
      } else {
         this.suite = var5;
      }

      this.uniqueIdPath = var1;
      this.rsLock = new Object();
      this.recordListener = new Vector(3);
      boolean var6 = RecordStoreFile.exists(this.record, this.vendor, this.suite);
      if (!var3 && !var6) {
         throw new RecordStoreNotFoundException("cannot find record store file");
      } else if (var3 && !var6 && (RecordStoreFile.spaceAvailableForCreation(this.record) - 40 < 0 || RecordStoreFile.isMaxNbrMidletRsReached())) {
         throw new RecordStoreFullException();
      } else {
         try {
            this.dbraf = new RecordStoreFile(this.record, this.vendor, this.suite);
            if (var3 && !var6) {
               this.recHeadCache = new RecordStore.RecordHeaderCache(INITIAL_CACHE_SIZE);
               this.storeDBState();
            } else {
               this.dbraf.seek(32);
               synchronized(staticBufferLock) {
                  this.dbraf.read(dbState);
                  this.dbNumLiveRecords = getInt(dbState, 0);
                  this.dbVersion = getInt(dbState, 8);
                  this.dbAuthMode = getInt(dbState, 4);
                  this.dbNextRecordID = getInt(dbState, 12);
                  this.dbFirstRecordOffset = getInt(dbState, 16);
                  this.dbFirstFreeBlockOffset = getInt(dbState, 20);
                  this.dbLastModified = getLong(dbState, 24);
                  this.dbDataStart = getInt(dbState, 32);
                  this.dbDataEnd = getInt(dbState, 36);
                  if (this.dbNumLiveRecords <= INITIAL_CACHE_SIZE) {
                     this.recHeadCache = new RecordStore.RecordHeaderCache(INITIAL_CACHE_SIZE);
                  } else {
                     int var8;
                     for(var8 = INITIAL_CACHE_SIZE << 1; var8 < this.dbNumLiveRecords && var8 <= MAX_CACHE_SIZE; var8 <<= 1) {
                     }

                     this.recHeadCache = new RecordStore.RecordHeaderCache(var8);
                  }

                  if (this.dbNumLiveRecords > 0) {
                     this.refreshCache();
                  }
               }
            }

         } catch (IOException var18) {
            try {
               if (this.dbraf != null) {
                  this.dbraf.close();
               }
            } catch (IOException var15) {
            } finally {
               this.dbraf = null;
            }

            throw new RecordStoreException("error opening record store file");
         }
      }
   }

   private boolean checkOwner() {
      String var1 = RecordStoreFile.getUniqueIdPath(this.record);
      String var2 = this.dbraf.getUniqueIdPath();
      return var1.equals(var2);
   }

   private boolean checkWritable() {
      if (this.checkOwner()) {
         return true;
      } else {
         return this.dbAuthMode == 1;
      }
   }

   private class RecordHeaderCache {
      private RecordStore.RecordHeader[] mCache;

      RecordHeaderCache(int var2) {
         this.mCache = new RecordStore.RecordHeader[var2];
      }

      RecordStore.RecordHeader get(int var1) {
         int var2 = var1 & this.mCache.length - 1;
         RecordStore.RecordHeader var3 = this.mCache[var2];
         return this.mCache[var2] != null && this.mCache[var2].id != var1 ? null : var3;
      }

      void insert(RecordStore.RecordHeader var1) {
         int var2 = var1.id & this.mCache.length - 1;
         if (this.mCache[var2] == null) {
            this.mCache[var2] = var1;
         } else {
            this.mCache[var2] = var1;
            if (this.mCache.length != RecordStore.MAX_CACHE_SIZE) {
               this.growCache();
            }
         }

      }

      void invalidate(int var1) {
         if (var1 > 0) {
            int var2 = var1 & this.mCache.length - 1;
            if (this.mCache[var2] != null && this.mCache[var2].id == var1) {
               this.mCache[var2] = null;
            }
         }

      }

      private void growCache() {
         RecordStore.RecordHeader[] var1 = new RecordStore.RecordHeader[this.mCache.length << 1];

         for(int var2 = 0; var2 < this.mCache.length; ++var2) {
            if (this.mCache[var2] != null) {
               RecordStore.RecordHeader var3 = this.mCache[var2];
               int var4 = var3.id & var1.length - 1;
               var1[var4] = var3;
            }
         }

         this.mCache = var1;
         var1 = null;
      }
   }

   private class RecordHeader {
      private static final int REC_ID = 0;
      private static final int NEXT_OFFSET = 4;
      private static final int BLOCK_SIZE = 8;
      private static final int DATALEN_OR_NEXTFREE = 12;
      private static final int DATA_OFFSET = 16;
      int offset;
      int id;
      int nextOffset;
      int blockSize;
      int dataLenOrNextFree;

      RecordHeader() {
      }

      RecordHeader(int var2) throws IOException {
         this.load(var2);
      }

      RecordHeader(int var2, int var3, int var4, int var5, int var6) {
         this.offset = var2;
         this.id = var3;
         this.nextOffset = var4;
         this.blockSize = var5;
         this.dataLenOrNextFree = var6;
      }

      void load(int var1) throws IOException {
         this.offset = var1;
         RecordStore.this.dbraf.seek(this.offset);
         synchronized(RecordStore.staticBufferLock) {
            RecordStore.this.dbraf.read(RecordStore.recHeadBuf, 0, 16);
            this.id = RecordStore.getInt(RecordStore.recHeadBuf, 0);
            this.nextOffset = RecordStore.getInt(RecordStore.recHeadBuf, 4);
            this.blockSize = RecordStore.getInt(RecordStore.recHeadBuf, 8);
            this.dataLenOrNextFree = RecordStore.getInt(RecordStore.recHeadBuf, 12);
         }
      }

      void store() throws IOException {
         RecordStore.this.dbraf.seek(this.offset);
         synchronized(RecordStore.staticBufferLock) {
            RecordStore.putInt(this.id, RecordStore.recHeadBuf, 0);
            RecordStore.putInt(this.nextOffset, RecordStore.recHeadBuf, 4);
            RecordStore.putInt(this.blockSize, RecordStore.recHeadBuf, 8);
            RecordStore.putInt(this.dataLenOrNextFree, RecordStore.recHeadBuf, 12);
            RecordStore.this.dbraf.write(RecordStore.recHeadBuf, 0, 16);
         }
      }

      int read(byte[] var1, int var2) throws IOException {
         RecordStore.this.dbraf.seek(this.offset + 16);
         return RecordStore.this.dbraf.read(var1, var2, this.dataLenOrNextFree);
      }

      void write(byte[] var1, int var2) throws IOException {
         RecordStore.this.dbraf.seek(this.offset + 16);
         RecordStore.this.dbraf.write(var1, var2, this.dataLenOrNextFree);
      }
   }
}
