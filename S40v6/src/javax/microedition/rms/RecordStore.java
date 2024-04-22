package javax.microedition.rms;

import com.nokia.mid.impl.isa.util.SharedObjects;
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
   static final Object rmsAPILock = SharedObjects.getLock("rmsAPILock");
   private String record = null;
   private String vendor = null;
   private String suite = null;
   private String uniqueIdPath;
   private int opencount;
   private RecordStoreFile dbraf;
   private int recordStoreMidletId = 0;
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

   public static void deleteRecordStore(String recordStoreName) throws RecordStoreException, RecordStoreNotFoundException {
      String uidPath = RecordStoreFile.getUniqueIdPath(recordStoreName);
      synchronized(rmsAPILock) {
         for(int n = 0; n < dbCache.size(); ++n) {
            RecordStore db = (RecordStore)dbCache.elementAt(n);
            if (db.uniqueIdPath.equals(uidPath)) {
               throw new RecordStoreException("deleteRecordStore error: record store is still open");
            }
         }

         if (RecordStoreFile.exists(recordStoreName, RecordStoreFile.getCurrentMidletSuiteVendor(), RecordStoreFile.getCurrentMidletSuiteName())) {
            boolean success = RecordStoreFile.deleteFile(recordStoreName);
            if (!success) {
               throw new RecordStoreException("deleteRecordStore failed");
            }
         } else {
            throw new RecordStoreNotFoundException("deleteRecordStore error: file not found");
         }
      }
   }

   public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
      String uidPath = RecordStoreFile.getUniqueIdPath(recordStoreName);
      synchronized(rmsAPILock) {
         if (recordStoreName.length() <= 32 && recordStoreName.length() != 0) {
            RecordStore db;
            for(int n = 0; n < dbCache.size(); ++n) {
               db = (RecordStore)dbCache.elementAt(n);
               if (db.uniqueIdPath.equals(uidPath)) {
                  if (db.recordStoreMidletId == db.dbraf.getCurrentMidletId()) {
                     ++db.opencount;
                     return db;
                  }

                  throw new RecordStoreException("In use by another MIDlet");
               }
            }

            if (!CheckRecordStoreOpen(recordStoreName, (String)null, (String)null)) {
               db = new RecordStore(uidPath, recordStoreName, createIfNecessary, (String)null, (String)null);
               db.opencount = 1;
               dbCache.addElement(db);
               return db;
            } else {
               throw new RecordStoreException("In use by another MIDlet");
            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary, int authmode, boolean writable) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
      RecordStore rs = openRecordStore(recordStoreName, createIfNecessary);
      rs.setMode(authmode, writable);
      return rs;
   }

   public static RecordStore openRecordStore(String recordStoreName, String vendorName, String suiteName) throws RecordStoreException, RecordStoreNotFoundException {
      if (vendorName != null && suiteName != null) {
         synchronized(rmsAPILock) {
            if (recordStoreName.length() <= 32 && recordStoreName.length() != 0) {
               String uidPath = RecordStoreFile.getUniqueIdPath(vendorName, suiteName, recordStoreName);

               RecordStore db;
               for(int n = 0; n < dbCache.size(); ++n) {
                  db = (RecordStore)dbCache.elementAt(n);
                  if (db.uniqueIdPath.equals(uidPath)) {
                     if (!db.checkOwner() && db.dbAuthMode == 0) {
                        throw new SecurityException();
                     }

                     if (db.recordStoreMidletId == db.dbraf.getCurrentMidletId()) {
                        ++db.opencount;
                        return db;
                     }

                     throw new RecordStoreException("In use by another MIDlet");
                  }
               }

               if (!CheckRecordStoreOpen(recordStoreName, vendorName, suiteName)) {
                  db = new RecordStore(uidPath, recordStoreName, false, vendorName, suiteName);
                  db.opencount = 1;
                  dbCache.addElement(db);
                  if (!db.checkOwner() && db.dbAuthMode == 0) {
                     db.closeRecordStore();
                     throw new SecurityException();
                  } else {
                     return db;
                  }
               } else {
                  throw new RecordStoreException("In use by another MIDlet");
               }
            } else {
               throw new IllegalArgumentException();
            }
         }
      } else {
         throw new IllegalArgumentException("vendorName and suiteName must be non null");
      }
   }

   public void setMode(int authmode, boolean writable) throws RecordStoreException {
      synchronized(rmsAPILock) {
         if (!this.checkOwner()) {
            throw new SecurityException();
         } else if (authmode != 0 && authmode != 1) {
            throw new IllegalArgumentException();
         } else {
            this.dbAuthMode = authmode;
            if (authmode == 1 && !writable) {
               this.dbAuthMode = 2;
            }

            this.storeDBState();
         }
      }
   }

   public void closeRecordStore() throws RecordStoreNotOpenException, RecordStoreException {
      synchronized(rmsAPILock) {
         this.checkOpen();
         RecordStore db = null;

         for(int n = 0; n < dbCache.size(); ++n) {
            db = (RecordStore)dbCache.elementAt(n);
            if (db == this) {
               --db.opencount;
               break;
            }
         }

         if (db.opencount <= 0) {
            dbCache.removeElement(db);

            try {
               if (!this.recordListener.isEmpty()) {
                  this.recordListener.removeAllElements();
               }

               if (this.dbFirstFreeBlockOffset != 0) {
                  this.compactRecords();
                  this.dbraf.truncate(this.dbDataEnd);
               }

               this.dbraf.close();
            } catch (IOException var9) {
               throw new RecordStoreException("error closing .db file");
            } finally {
               this.dbraf = null;
               this.recHeadCache = null;
            }
         }

      }
   }

   public static String[] listRecordStores() {
      synchronized(rmsAPILock) {
         String[] returnstrings = RecordStoreFile.listRecordStores();
         return returnstrings;
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
      int rv = this.dbraf.spaceAvailable() - 16;
      return rv < 0 ? 0 : rv;
   }

   public long getLastModified() throws RecordStoreNotOpenException {
      this.checkOpen();
      return this.dbLastModified;
   }

   public void addRecordListener(RecordListener listener) {
      synchronized(rmsAPILock) {
         if (!this.recordListener.contains(listener)) {
            this.recordListener.addElement(listener);
         }

      }
   }

   public void removeRecordListener(RecordListener listener) {
      synchronized(rmsAPILock) {
         this.recordListener.removeElement(listener);
      }
   }

   public int getNextRecordID() throws RecordStoreNotOpenException, RecordStoreException {
      this.checkOpen();
      return this.dbNextRecordID;
   }

   public int addRecord(byte[] data, int offset, int numBytes) throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException {
      if (offset < 0 || numBytes < 0 || numBytes > 0 && numBytes + offset > data.length) {
         throw new ArrayIndexOutOfBoundsException();
      } else {
         synchronized(rmsAPILock) {
            this.checkOpen();
            if (!this.checkWritable()) {
               throw new SecurityException();
            } else {
               int id = this.dbNextRecordID++;
               RecordStore.RecordHeader rh = this.allocateNewRecordStorage(id, numBytes);

               try {
                  if (numBytes > 0) {
                     rh.write(data, offset);
                  }
               } catch (IOException var9) {
                  throw new RecordStoreException("error writing new record data");
               }

               ++this.dbNumLiveRecords;
               ++this.dbVersion;
               this.storeDBState();
               this.notifyRecordAddedListeners(id);
               return id;
            }
         }
      }
   }

   public void deleteRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
      synchronized(rmsAPILock) {
         this.checkOpen();
         if (!this.checkWritable()) {
            throw new SecurityException();
         } else {
            RecordStore.RecordHeader rh = null;

            try {
               rh = this.findRecord(recordId, false);
               this.freeRecord(rh);
               this.recHeadCache.invalidate(rh.id);
            } catch (IOException var6) {
               throw new RecordStoreException("error updating file after record deletion");
            }

            --this.dbNumLiveRecords;
            ++this.dbVersion;
            this.storeDBState();
            this.notifyRecordDeletedListeners(recordId);
         }
      }
   }

   public int getRecordSize(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
      synchronized(rmsAPILock) {
         this.checkOpen();

         int var10000;
         try {
            RecordStore.RecordHeader rh = this.findRecord(recordId, true);
            var10000 = rh.dataLenOrNextFree;
         } catch (IOException var5) {
            throw new RecordStoreException("error reading record data");
         }

         return var10000;
      }
   }

   public int getRecord(int recordId, byte[] buffer, int offset) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
      synchronized(rmsAPILock) {
         this.checkOpen();

         RecordStore.RecordHeader rh;
         try {
            rh = this.findRecord(recordId, true);
            rh.read(buffer, offset);
         } catch (IOException var8) {
            throw new RecordStoreException("error reading record data");
         }

         return rh.dataLenOrNextFree;
      }
   }

   public byte[] getRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
      synchronized(rmsAPILock) {
         this.checkOpen();
         Object var3 = null;

         byte[] data;
         try {
            RecordStore.RecordHeader rh = this.findRecord(recordId, true);
            if (rh.dataLenOrNextFree == 0) {
               Object var10000 = null;
               return (byte[])var10000;
            }

            data = new byte[rh.dataLenOrNextFree];
            rh.read(data, 0);
         } catch (IOException var6) {
            throw new RecordStoreException("error reading record data");
         }

         return data;
      }
   }

   public void setRecord(int recordId, byte[] newData, int offset, int numBytes) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException, RecordStoreFullException {
      if (offset >= 0 && numBytes >= 0 && (numBytes <= 0 || numBytes + offset <= newData.length)) {
         synchronized(rmsAPILock) {
            this.checkOpen();
            if (!this.checkWritable()) {
               throw new SecurityException();
            } else {
               RecordStore.RecordHeader rh = null;
               RecordStore.RecordHeader newrh = null;

               try {
                  rh = this.findRecord(recordId, false);
               } catch (IOException var13) {
                  throw new RecordStoreException("error finding record data");
               }

               if (numBytes <= rh.blockSize - 16) {
                  int allocSize = this.getAllocSize(numBytes);
                  if (rh.blockSize - allocSize >= 32) {
                     this.splitRecord(rh, allocSize);
                  }

                  rh.dataLenOrNextFree = numBytes;

                  try {
                     rh.store();
                     this.recHeadCache.insert(rh);
                     if (numBytes > 0) {
                        rh.write(newData, offset);
                     }
                  } catch (IOException var12) {
                     throw new RecordStoreException("error writing record data");
                  }
               } else {
                  this.freeRecord(rh);
                  newrh = this.allocateNewRecordStorage(recordId, numBytes);

                  try {
                     if (numBytes > 0) {
                        newrh.write(newData, offset);
                     }
                  } catch (IOException var11) {
                     throw new RecordStoreException("error moving record data");
                  }
               }

               ++this.dbVersion;
               this.storeDBState();
               this.notifyRecordChangedListeners(recordId);
            }
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public RecordEnumeration enumerateRecords(RecordFilter filter, RecordComparator comparator, boolean keepUpdated) throws RecordStoreNotOpenException {
      this.checkOpen();
      return new RecordEnumerationImpl(this, filter, comparator, keepUpdated);
   }

   private RecordStore.RecordHeader findRecord(int recordId, boolean addToCache) throws InvalidRecordIDException, IOException {
      int cur_offset = this.dbFirstRecordOffset;
      if (cur_offset == 0) {
         throw new InvalidRecordIDException();
      } else {
         RecordStore.RecordHeader rh = this.recHeadCache.get(recordId);
         if (rh != null) {
            return rh;
         } else {
            for(rh = new RecordStore.RecordHeader(); cur_offset != 0; cur_offset = rh.nextOffset) {
               rh.load(cur_offset);
               if (rh.id == recordId) {
                  break;
               }
            }

            if (cur_offset == 0) {
               throw new InvalidRecordIDException();
            } else {
               if (addToCache) {
                  this.recHeadCache.insert(rh);
               }

               return rh;
            }
         }
      }
   }

   private int getAllocSize(int numBytes) {
      int rv = 16 + numBytes;
      int pad = 16 - rv % 16;
      if (pad != 16) {
         rv += pad;
      }

      return rv;
   }

   private RecordStore.RecordHeader allocateNewRecordStorage(int id, int dataSize) throws RecordStoreException, RecordStoreFullException {
      int allocSize = this.getAllocSize(dataSize);
      boolean foundBlock = false;
      RecordStore.RecordHeader block = new RecordStore.RecordHeader();

      try {
         for(int offset = this.dbFirstFreeBlockOffset; offset != 0; offset = block.dataLenOrNextFree) {
            block.load(offset);
            if (block.blockSize >= allocSize) {
               foundBlock = true;
               break;
            }
         }
      } catch (IOException var9) {
         throw new RecordStoreException("error finding first fit block");
      }

      if (!foundBlock) {
         if (this.dbraf.spaceAvailable() < allocSize) {
            throw new RecordStoreFullException();
         }

         block = new RecordStore.RecordHeader(this.dbDataEnd, id, this.dbFirstRecordOffset, allocSize, dataSize);

         try {
            block.store();
         } catch (IOException var8) {
            throw new RecordStoreException("error writing new record data");
         }

         this.dbFirstRecordOffset = this.dbDataEnd;
         this.dbDataEnd += allocSize;
      } else {
         if (block.id != -1) {
            throw new RecordStoreException("ALLOC ERR " + block.id + " is not a free block!");
         }

         this.removeFreeBlock(block);
         block.id = id;
         if (block.blockSize - allocSize >= 32) {
            this.splitRecord(block, allocSize);
         }

         block.dataLenOrNextFree = dataSize;

         try {
            block.store();
         } catch (IOException var7) {
            throw new RecordStoreException("error writing free block after alloc");
         }
      }

      this.recHeadCache.insert(block);
      return block;
   }

   private void splitRecord(RecordStore.RecordHeader recHead, int allocSize) throws RecordStoreException {
      int extraSpace = recHead.blockSize - allocSize;
      int oldBlockSize = recHead.blockSize;
      recHead.blockSize = allocSize;
      if (recHead.offset != this.dbFirstRecordOffset) {
         int fboffset = recHead.offset + allocSize;
         RecordStore.RecordHeader newfb = new RecordStore.RecordHeader(fboffset, -1, recHead.offset, extraSpace, 0);

         try {
            this.freeRecord(newfb);
            RecordStore.RecordHeader prh = new RecordStore.RecordHeader(recHead.offset + oldBlockSize);
            prh.nextOffset = fboffset;
            prh.store();
            this.recHeadCache.invalidate(prh.id);
            this.storeDBState();
         } catch (IOException var8) {
            throw new RecordStoreException("splitRecord error");
         }
      } else {
         this.dbDataEnd = recHead.offset + recHead.blockSize;
      }

   }

   private void freeRecord(RecordStore.RecordHeader rh) throws RecordStoreException {
      try {
         if (rh.offset == this.dbFirstRecordOffset) {
            int recordId = -1;

            RecordStore.RecordHeader tmpRh;
            for(tmpRh = rh; tmpRh.nextOffset != 0 && recordId == -1; recordId = tmpRh.id) {
               tmpRh = new RecordStore.RecordHeader(tmpRh.nextOffset);
            }

            if (recordId == -1) {
               this.dbFirstRecordOffset = 0;
               this.dbDataEnd = this.dbDataStart;
               this.dbFirstFreeBlockOffset = 0;
            } else {
               this.dbFirstRecordOffset = tmpRh.offset;
               this.dbDataEnd = tmpRh.offset + tmpRh.blockSize;
               rh.id = -1;
               rh.dataLenOrNextFree = this.dbFirstFreeBlockOffset;
               this.dbFirstFreeBlockOffset = rh.offset;
               rh.store();
               this.cleanupFreeList();
               this.storeDBState();
            }

            this.dbraf.truncate(this.dbDataEnd);
         } else {
            rh.id = -1;
            rh.dataLenOrNextFree = this.dbFirstFreeBlockOffset;
            this.dbFirstFreeBlockOffset = rh.offset;
            rh.store();
         }

      } catch (IOException var4) {
         throw new RecordStoreException("free record failed");
      }
   }

   private void removeFreeBlock(RecordStore.RecordHeader blockToFree) throws RecordStoreException {
      RecordStore.RecordHeader block = new RecordStore.RecordHeader();
      RecordStore.RecordHeader prev = new RecordStore.RecordHeader();
      RecordStore.RecordHeader tmp = null;

      try {
         for(int offset = this.dbFirstFreeBlockOffset; offset != 0; block = tmp) {
            block.load(offset);
            if (block.offset == blockToFree.offset) {
               if (block.id != -1) {
                  throw new RecordStoreException("removeFreeBlock id is not -1");
               }

               if (prev.offset == 0) {
                  this.dbFirstFreeBlockOffset = block.dataLenOrNextFree;
               } else {
                  prev.dataLenOrNextFree = block.dataLenOrNextFree;
                  prev.store();
               }
            }

            offset = block.dataLenOrNextFree;
            tmp = prev;
            prev = block;
         }

      } catch (IOException var6) {
         throw new RecordStoreException("removeFreeBlock block not found");
      }
   }

   private void cleanupFreeList() throws RecordStoreException {
      RecordStore.RecordHeader block = new RecordStore.RecordHeader();
      RecordStore.RecordHeader prev = new RecordStore.RecordHeader();
      RecordStore.RecordHeader tmp = null;

      try {
         int offset = this.dbFirstFreeBlockOffset;

         while(offset != 0) {
            block.load(offset);
            if (block.offset >= this.dbDataEnd) {
               if (block.id != -1) {
                  throw new RecordStoreException("cleanupFreeList id is not -1");
               }

               if (prev.offset == 0) {
                  this.dbFirstFreeBlockOffset = block.dataLenOrNextFree;
               } else {
                  prev.dataLenOrNextFree = block.dataLenOrNextFree;
                  prev.store();
               }

               offset = block.dataLenOrNextFree;
            } else {
               offset = block.dataLenOrNextFree;
               tmp = prev;
               prev = block;
               block = tmp;
            }
         }

      } catch (IOException var5) {
         throw new RecordStoreException("cleanupFreeList block not found");
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

   private void notifyRecordChangedListeners(int recordId) {
      for(int i = 0; i < this.recordListener.size(); ++i) {
         RecordListener rl = (RecordListener)this.recordListener.elementAt(i);
         rl.recordChanged(this, recordId);
      }

   }

   private void notifyRecordAddedListeners(int recordId) {
      for(int i = 0; i < this.recordListener.size(); ++i) {
         RecordListener rl = (RecordListener)this.recordListener.elementAt(i);
         rl.recordAdded(this, recordId);
      }

   }

   private void notifyRecordDeletedListeners(int recordId) {
      for(int i = 0; i < this.recordListener.size(); ++i) {
         RecordListener rl = (RecordListener)this.recordListener.elementAt(i);
         rl.recordDeleted(this, recordId);
      }

   }

   static int getInt(byte[] data, int offset) {
      int r = data[offset++];
      int r = r << 8 | data[offset++] & 255;
      r = r << 8 | data[offset++] & 255;
      r = r << 8 | data[offset++] & 255;
      return r;
   }

   static long getLong(byte[] data, int offset) {
      long r = (long)data[offset++];
      r = r << 8 | (long)data[offset++] & 255L;
      r = r << 8 | (long)data[offset++] & 255L;
      r = r << 8 | (long)data[offset++] & 255L;
      r = r << 8 | (long)data[offset++] & 255L;
      r = r << 8 | (long)data[offset++] & 255L;
      r = r << 8 | (long)data[offset++] & 255L;
      r = r << 8 | (long)data[offset++] & 255L;
      return r;
   }

   static int putInt(int i, byte[] data, int offset) {
      data[offset++] = (byte)(i >> 24 & 255);
      data[offset++] = (byte)(i >> 16 & 255);
      data[offset++] = (byte)(i >> 8 & 255);
      data[offset] = (byte)(i & 255);
      return 4;
   }

   static int putLong(long l, byte[] data, int offset) {
      data[offset++] = (byte)((int)(l >> 56 & 255L));
      data[offset++] = (byte)((int)(l >> 48 & 255L));
      data[offset++] = (byte)((int)(l >> 40 & 255L));
      data[offset++] = (byte)((int)(l >> 32 & 255L));
      data[offset++] = (byte)((int)(l >> 24 & 255L));
      data[offset++] = (byte)((int)(l >> 16 & 255L));
      data[offset++] = (byte)((int)(l >> 8 & 255L));
      data[offset] = (byte)((int)(l & 255L));
      return 8;
   }

   int[] getRecordIDs() {
      if (this.dbraf == null) {
         return null;
      } else {
         int index = 0;
         int[] tmp = new int[this.dbNumLiveRecords];
         int offset = this.dbFirstRecordOffset;
         RecordStore.RecordHeader rh = new RecordStore.RecordHeader();

         try {
            for(; offset != 0; offset = rh.nextOffset) {
               rh.load(offset);
               if (rh.id > 0) {
                  tmp[index++] = rh.id;
               }
            }

            return tmp;
         } catch (IOException var6) {
            return null;
         }
      }
   }

   private void refreshCache() throws RecordStoreException {
      if (this.dbraf != null) {
         int offset = this.dbFirstRecordOffset;

         try {
            RecordStore.RecordHeader rh;
            for(; offset != 0; offset = rh.nextOffset) {
               rh = new RecordStore.RecordHeader();
               rh.load(offset);
               if (rh.nextOffset >= offset) {
                  throw new RecordStoreException("Corrupt RMS file detected");
               }

               if (rh.id > 0) {
                  this.recHeadCache.insert(rh);
               }
            }

         } catch (IOException var3) {
         }
      }
   }

   private void compactRecords() throws RecordStoreNotOpenException, RecordStoreException {
      int offset = this.dbDataStart;
      int target = 0;
      byte[] chunkBuffer = new byte[64];
      RecordStore.RecordHeader rh = new RecordStore.RecordHeader();
      int prevRec = 0;

      while(offset < this.dbDataEnd) {
         try {
            rh.load(offset);
         } catch (IOException var10) {
         }

         if (rh.id == -1) {
            if (target == 0) {
               target = offset;
            }

            offset += rh.blockSize;
         } else if (target == 0) {
            prevRec = offset;
            offset += rh.blockSize;
         } else {
            rh.offset = target;
            rh.nextOffset = prevRec;

            try {
               rh.store();
               offset += 16;
               target += 16;

               int numToMove;
               for(int bytesLeft = rh.blockSize - 16; bytesLeft > 0; bytesLeft -= numToMove) {
                  if (bytesLeft < 64) {
                     numToMove = bytesLeft;
                  } else {
                     numToMove = 64;
                  }

                  this.dbraf.seek(offset);
                  this.dbraf.read(chunkBuffer, 0, numToMove);
                  this.dbraf.seek(target);
                  this.dbraf.write(chunkBuffer, 0, numToMove);
                  offset += numToMove;
                  target += numToMove;
               }
            } catch (IOException var11) {
            }

            prevRec = target;
         }
      }

      if (rh.offset != 0) {
         this.dbDataEnd = rh.offset + rh.blockSize;
      }

      this.dbFirstRecordOffset = rh.offset;
      this.dbFirstFreeBlockOffset = 0;
      this.storeDBState();
   }

   private static boolean CheckRecordStoreOpen(String recordStoreName, String in_vendor, String in_suite) {
      int rv = true;
      String record = null;
      String vendor = null;
      String suite = null;
      if (in_vendor == null) {
         vendor = RecordStoreFile.getCurrentMidletSuiteVendor();
      } else {
         vendor = in_vendor;
      }

      if (in_suite == null) {
         suite = RecordStoreFile.getCurrentMidletSuiteName();
      } else {
         suite = in_suite;
      }

      int rv = isRecordStoreOpen(vendor, suite, recordStoreName);
      return rv != -1;
   }

   private RecordStore(String uidPath, String recordStoreName, boolean create, String in_vendor, String in_suite) throws RecordStoreException, RecordStoreNotFoundException {
      this.record = recordStoreName;
      if (in_vendor == null) {
         this.vendor = RecordStoreFile.getCurrentMidletSuiteVendor();
      } else {
         this.vendor = in_vendor;
      }

      if (in_suite == null) {
         this.suite = RecordStoreFile.getCurrentMidletSuiteName();
      } else {
         this.suite = in_suite;
      }

      this.uniqueIdPath = uidPath;
      this.recordListener = new Vector(3);
      boolean exists = RecordStoreFile.exists(this.record, this.vendor, this.suite);
      if (!create && !exists) {
         throw new RecordStoreNotFoundException("cannot find record store file");
      } else if (create && !exists && (RecordStoreFile.spaceAvailableForCreation(this.record) - 40 < 0 || RecordStoreFile.isMaxNbrMidletRsReached())) {
         throw new RecordStoreFullException();
      } else {
         try {
            this.dbraf = new RecordStoreFile(this.record, this.vendor, this.suite);
            if (create && !exists) {
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
                     int size;
                     for(size = INITIAL_CACHE_SIZE << 1; size < this.dbNumLiveRecords && size < MAX_CACHE_SIZE; size <<= 1) {
                     }

                     this.recHeadCache = new RecordStore.RecordHeaderCache(size);
                  }

                  if (this.dbNumLiveRecords > 0) {
                     this.refreshCache();
                  }
               }
            }

            this.recordStoreMidletId = this.dbraf.getCurrentMidletId();
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
      String myUid = RecordStoreFile.getUniqueIdPath(this.record);
      String rsfUid = this.dbraf.getUniqueIdPath();
      return myUid.equals(rsfUid);
   }

   private boolean checkWritable() {
      if (this.checkOwner()) {
         return true;
      } else {
         return this.dbAuthMode == 1;
      }
   }

   private static native int isRecordStoreOpen(String var0, String var1, String var2);

   private class RecordHeaderCache {
      private RecordStore.RecordHeader[] mCache;

      RecordHeaderCache(int size) {
         this.mCache = new RecordStore.RecordHeader[size];
      }

      RecordStore.RecordHeader get(int rec_id) {
         int idx = rec_id & this.mCache.length - 1;
         RecordStore.RecordHeader rh = this.mCache[idx];
         return this.mCache[idx] != null && this.mCache[idx].id != rec_id ? null : rh;
      }

      void insert(RecordStore.RecordHeader rh) {
         int idx = rh.id & this.mCache.length - 1;
         if (this.mCache[idx] == null) {
            this.mCache[idx] = rh;
         } else {
            this.mCache[idx] = rh;
            if (this.mCache.length < RecordStore.MAX_CACHE_SIZE) {
               this.growCache();
            }
         }

      }

      void invalidate(int rec_id) {
         if (rec_id > 0) {
            int idx = rec_id & this.mCache.length - 1;
            if (this.mCache[idx] != null && this.mCache[idx].id == rec_id) {
               this.mCache[idx] = null;
            }
         }

      }

      private void growCache() {
         RecordStore.RecordHeader[] mNewCache = new RecordStore.RecordHeader[this.mCache.length << 1];

         for(int i = 0; i < this.mCache.length; ++i) {
            if (this.mCache[i] != null) {
               RecordStore.RecordHeader rh = this.mCache[i];
               int newIdx = rh.id & mNewCache.length - 1;
               mNewCache[newIdx] = rh;
            }
         }

         this.mCache = mNewCache;
         mNewCache = null;
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

      RecordHeader(int _offset) throws IOException {
         this.load(_offset);
      }

      RecordHeader(int _offset, int _id, int next_offset, int size, int len_or_free) {
         this.offset = _offset;
         this.id = _id;
         this.nextOffset = next_offset;
         this.blockSize = size;
         this.dataLenOrNextFree = len_or_free;
      }

      void load(int _offset) throws IOException {
         this.offset = _offset;
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

      int read(byte[] buf, int _offset) throws IOException {
         RecordStore.this.dbraf.seek(this.offset + 16);
         return RecordStore.this.dbraf.read(buf, _offset, this.dataLenOrNextFree);
      }

      void write(byte[] buf, int _offset) throws IOException {
         RecordStore.this.dbraf.seek(this.offset + 16);
         RecordStore.this.dbraf.write(buf, _offset, this.dataLenOrNextFree);
      }
   }
}
