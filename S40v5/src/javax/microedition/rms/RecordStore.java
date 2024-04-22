package javax.microedition.rms;

import com.nokia.mid.impl.isa.util.SharedObjects;
import java.io.IOException;
import java.util.Vector;

public class RecordStore {
   public static final int AUTHMODE_PRIVATE = 0;
   public static final int AUTHMODE_ANY = 1;
   private static Vector gz = new Vector(3);
   private static final Object gA = SharedObjects.get("dbCacheLock", "java.lang.Object");
   private String gB = null;
   private String vendor = null;
   private String gC = null;
   private String gD;
   private int gE;
   private RecordStoreFile gF;
   private int gG = 0;
   Object gH;
   private Vector gI;
   private RecordStore.RecordHeaderCache gJ;
   private static int gK = 16;
   private static int gL = 128;
   private static byte[] gM = new byte[16];
   private static final Object gN = new Object();
   private int gO = 1;
   private int gP;
   private int gQ;
   private int gR;
   private long gS;
   private int gT;
   private int gU;
   private int gV = 72;
   private int gW = 72;
   private static byte[] gX = new byte[40];

   private RecordStore() {
   }

   public static void deleteRecordStore(String var0) throws RecordStoreException, RecordStoreNotFoundException {
      String var1 = RecordStoreFile.d(var0);
      synchronized(gA) {
         for(int var4 = 0; var4 < gz.size(); ++var4) {
            if (((RecordStore)gz.elementAt(var4)).gD.equals(var1)) {
               throw new RecordStoreException("deleteRecordStore error: record store is still open");
            }
         }

         if (RecordStoreFile.b(var0, RecordStoreFile.getCurrentMidletSuiteVendor(), RecordStoreFile.getCurrentMidletSuiteName())) {
            if (!RecordStoreFile.e(var0)) {
               throw new RecordStoreException("deleteRecordStore failed");
            }
         } else {
            throw new RecordStoreNotFoundException("deleteRecordStore error: file not found");
         }
      }
   }

   public static RecordStore openRecordStore(String var0, boolean var1) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
      String var2 = RecordStoreFile.d(var0);
      synchronized(gA) {
         if (var0.length() <= 32 && var0.length() != 0) {
            RecordStore var4;
            for(int var5 = 0; var5 < gz.size(); ++var5) {
               if ((var4 = (RecordStore)gz.elementAt(var5)).gD.equals(var2)) {
                  if (var4.gG == var4.gF.getCurrentMidletId()) {
                     ++var4.gE;
                     return var4;
                  }

                  throw new RecordStoreException("In use by another MIDlet");
               }
            }

            if (!c(var0, (String)null, (String)null)) {
               (var4 = new RecordStore(var2, var0, var1, (String)null, (String)null)).gE = 1;
               gz.addElement(var4);
               return var4;
            } else {
               throw new RecordStoreException("In use by another MIDlet");
            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public static RecordStore openRecordStore(String var0, boolean var1, int var2, boolean var3) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
      RecordStore var4;
      (var4 = openRecordStore(var0, var1)).setMode(var2, var3);
      return var4;
   }

   public static RecordStore openRecordStore(String var0, String var1, String var2) throws RecordStoreException, RecordStoreNotFoundException {
      if (var1 != null && var2 != null) {
         synchronized(gA) {
            if (var0.length() <= 32 && var0.length() != 0) {
               String var5 = RecordStoreFile.a(var1, var2, var0);

               RecordStore var4;
               for(int var6 = 0; var6 < gz.size(); ++var6) {
                  if ((var4 = (RecordStore)gz.elementAt(var6)).gD.equals(var5)) {
                     if (!var4.av() && var4.gQ == 0) {
                        throw new SecurityException();
                     }

                     if (var4.gG == var4.gF.getCurrentMidletId()) {
                        ++var4.gE;
                        return var4;
                     }

                     throw new RecordStoreException("In use by another MIDlet");
                  }
               }

               if (!c(var0, var1, var2)) {
                  (var4 = new RecordStore(var5, var0, false, var1, var2)).gE = 1;
                  gz.addElement(var4);
                  if (!var4.av() && var4.gQ == 0) {
                     var4.closeRecordStore();
                     throw new SecurityException();
                  } else {
                     return var4;
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

   public void setMode(int var1, boolean var2) throws RecordStoreException {
      synchronized(this.gH) {
         if (!this.av()) {
            throw new SecurityException();
         } else if (var1 != 0 && var1 != 1) {
            throw new IllegalArgumentException();
         } else {
            this.gQ = var1;
            if (var1 == 1 && !var2) {
               this.gQ = 2;
            }

            this.at();
         }
      }
   }

   public void closeRecordStore() throws RecordStoreNotOpenException, RecordStoreException {
      synchronized(this.gH) {
         synchronized(gA) {
            this.checkOpen();
            RecordStore var3 = null;
            int var4 = 0;

            while(true) {
               if (var4 < gz.size()) {
                  if ((var3 = (RecordStore)gz.elementAt(var4)) != this) {
                     ++var4;
                     continue;
                  }

                  --var3.gE;
               }

               if (var3.gE <= 0) {
                  gz.removeElement(var3);

                  try {
                     if (!this.gI.isEmpty()) {
                        this.gI.removeAllElements();
                     }

                     if (this.gU != 0) {
                        var3 = this;
                        var4 = this.gV;
                        int var5 = 0;
                        byte[] var8 = new byte[64];
                        RecordStore.RecordHeader var9 = new RecordStore.RecordHeader(this);
                        int var6 = 0;

                        while(var4 < var3.gW) {
                           try {
                              var9.n(var4);
                           } catch (IOException var17) {
                           }

                           if (var9.id == -1) {
                              if (var5 == 0) {
                                 var5 = var4;
                              }

                              var4 += var9.cQ;
                           } else if (var5 == 0) {
                              var6 = var4;
                              var4 += var9.cQ;
                           } else {
                              var9.offset = var5;
                              var9.cP = var6;

                              try {
                                 var9.store();
                                 var4 += 16;
                                 var5 += 16;

                                 int var7;
                                 for(var6 = var9.cQ - 16; var6 > 0; var6 -= var7) {
                                    if (var6 < 64) {
                                       var7 = var6;
                                    } else {
                                       var7 = 64;
                                    }

                                    var3.gF.seek(var4);
                                    var3.gF.read(var8, 0, var7);
                                    var3.gF.seek(var5);
                                    var3.gF.write(var8, 0, var7);
                                    var4 += var7;
                                    var5 += var7;
                                 }
                              } catch (IOException var18) {
                              }

                              var6 = var5;
                           }
                        }

                        if (var9.offset != 0) {
                           var3.gW = var9.offset + var9.cQ;
                        }

                        var3.gT = var9.offset;
                        var3.gU = 0;
                        var3.at();
                        this.gF.truncate(this.gW);
                     }

                     RecordStoreFile var23 = this.gF;
                  } catch (IOException var19) {
                     throw new RecordStoreException("error closing .db file");
                  } finally {
                     this.gF = null;
                     this.gJ = null;
                  }
               }
               break;
            }
         }

      }
   }

   public static String[] listRecordStores() {
      synchronized(gA) {
         return RecordStoreFile.listRecordStores();
      }
   }

   public String getName() throws RecordStoreNotOpenException {
      this.checkOpen();
      return this.gB;
   }

   public int getVersion() throws RecordStoreNotOpenException {
      this.checkOpen();
      return this.gP;
   }

   public int getNumRecords() throws RecordStoreNotOpenException {
      this.checkOpen();
      return this.gR;
   }

   public int getSize() throws RecordStoreNotOpenException {
      this.checkOpen();
      return this.gW;
   }

   public int getSizeAvailable() throws RecordStoreNotOpenException {
      this.checkOpen();
      int var1;
      return (var1 = this.gF.spaceAvailable() - 16) < 0 ? 0 : var1;
   }

   public long getLastModified() throws RecordStoreNotOpenException {
      this.checkOpen();
      return this.gS;
   }

   public void addRecordListener(RecordListener var1) {
      synchronized(this.gH) {
         if (!this.gI.contains(var1)) {
            this.gI.addElement(var1);
         }

      }
   }

   public void removeRecordListener(RecordListener var1) {
      synchronized(this.gH) {
         this.gI.removeElement(var1);
      }
   }

   public int getNextRecordID() throws RecordStoreNotOpenException, RecordStoreException {
      this.checkOpen();
      return this.gO;
   }

   public int addRecord(byte[] var1, int var2, int var3) throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException {
      if (var2 < 0 || var3 < 0 || var3 > 0 && var3 + var2 > var1.length) {
         throw new ArrayIndexOutOfBoundsException();
      } else {
         synchronized(this.gH) {
            this.checkOpen();
            if (!this.aw()) {
               throw new SecurityException();
            } else {
               int var5 = this.gO++;
               RecordStore.RecordHeader var6 = this.t(var5, var3);

               try {
                  if (var3 > 0) {
                     var6.b(var1, var2);
                  }
               } catch (IOException var7) {
                  throw new RecordStoreException("error writing new record data");
               }

               ++this.gR;
               ++this.gP;
               this.at();
               this.F(var5);
               return var5;
            }
         }
      }
   }

   public void deleteRecord(int var1) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
      synchronized(this.gH) {
         this.checkOpen();
         if (!this.aw()) {
            throw new SecurityException();
         } else {
            RecordStore.RecordHeader var3 = null;

            try {
               var3 = this.b(var1, false);
               this.b(var3);
               this.gJ.m(var3.id);
            } catch (IOException var4) {
               throw new RecordStoreException("error updating file after record deletion");
            }

            --this.gR;
            ++this.gP;
            this.at();
            this.G(var1);
         }
      }
   }

   public int getRecordSize(int var1) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
      synchronized(this.gH) {
         this.checkOpen();

         int var10000;
         try {
            var10000 = this.b(var1, true).cR;
         } catch (IOException var3) {
            throw new RecordStoreException("error reading record data");
         }

         return var10000;
      }
   }

   public int getRecord(int var1, byte[] var2, int var3) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
      synchronized(this.gH) {
         this.checkOpen();

         RecordStore.RecordHeader var5;
         try {
            (var5 = this.b(var1, true)).a(var2, var3);
         } catch (IOException var6) {
            throw new RecordStoreException("error reading record data");
         }

         return var5.cR;
      }
   }

   public byte[] getRecord(int var1) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
      synchronized(this.gH) {
         this.checkOpen();
         Object var3 = null;

         byte[] var7;
         try {
            RecordStore.RecordHeader var6;
            if ((var6 = this.b(var1, true)).cR == 0) {
               Object var10000 = null;
               return (byte[])var10000;
            }

            var7 = new byte[var6.cR];
            var6.a(var7, 0);
         } catch (IOException var4) {
            throw new RecordStoreException("error reading record data");
         }

         return var7;
      }
   }

   public void setRecord(int var1, byte[] var2, int var3, int var4) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException, RecordStoreFullException {
      if (var3 >= 0 && var4 >= 0 && (var4 <= 0 || var4 + var3 <= var2.length)) {
         synchronized(this.gH) {
            this.checkOpen();
            if (!this.aw()) {
               throw new SecurityException();
            } else {
               RecordStore.RecordHeader var6 = null;
               var6 = null;

               try {
                  var6 = this.b(var1, false);
               } catch (IOException var10) {
                  throw new RecordStoreException("error finding record data");
               }

               if (var4 <= var6.cQ - 16) {
                  int var7 = this.getAllocSize(var4);
                  if (var6.cQ - var7 >= 32) {
                     this.a(var6, var7);
                  }

                  var6.cR = var4;

                  try {
                     var6.store();
                     this.gJ.a(var6);
                     if (var4 > 0) {
                        var6.b(var2, var3);
                     }
                  } catch (IOException var9) {
                     throw new RecordStoreException("error writing record data");
                  }
               } else {
                  this.b(var6);
                  var6 = this.t(var1, var4);

                  try {
                     if (var4 > 0) {
                        var6.b(var2, var3);
                     }
                  } catch (IOException var8) {
                     throw new RecordStoreException("error moving record data");
                  }
               }

               ++this.gP;
               this.at();
               this.E(var1);
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

   private RecordStore.RecordHeader b(int var1, boolean var2) throws InvalidRecordIDException, IOException {
      int var4;
      if ((var4 = this.gT) == 0) {
         throw new InvalidRecordIDException();
      } else {
         RecordStore.RecordHeader var3;
         if ((var3 = this.gJ.get(var1)) != null) {
            return var3;
         } else {
            for(var3 = new RecordStore.RecordHeader(this); var4 != 0; var4 = var3.cP) {
               var3.n(var4);
               if (var3.id == var1) {
                  break;
               }
            }

            if (var4 == 0) {
               throw new InvalidRecordIDException();
            } else {
               if (var2) {
                  this.gJ.a(var3);
               }

               return var3;
            }
         }
      }
   }

   private int getAllocSize(int var1) {
      int var2 = 16 + var1;
      if ((var1 = 16 - var2 % 16) != 16) {
         var2 += var1;
      }

      return var2;
   }

   private RecordStore.RecordHeader t(int var1, int var2) throws RecordStoreException, RecordStoreFullException {
      int var3 = this.getAllocSize(var2);
      boolean var4 = false;
      RecordStore.RecordHeader var5 = new RecordStore.RecordHeader(this);

      try {
         for(int var6 = this.gU; var6 != 0; var6 = var5.cR) {
            var5.n(var6);
            if (var5.cQ >= var3) {
               var4 = true;
               break;
            }
         }
      } catch (IOException var9) {
         throw new RecordStoreException("error finding first fit block");
      }

      if (!var4) {
         if (this.gF.spaceAvailable() < var3) {
            throw new RecordStoreFullException();
         }

         var5 = new RecordStore.RecordHeader(this, this.gW, var1, this.gT, var3, var2);

         try {
            var5.store();
         } catch (IOException var8) {
            throw new RecordStoreException("error writing new record data");
         }

         this.gT = this.gW;
         this.gW += var3;
      } else {
         if (var5.id != -1) {
            throw new RecordStoreException("ALLOC ERR " + var5.id + " is not a free block!");
         }

         this.c(var5);
         var5.id = var1;
         if (var5.cQ - var3 >= 32) {
            this.a(var5, var3);
         }

         var5.cR = var2;

         try {
            var5.store();
         } catch (IOException var7) {
            throw new RecordStoreException("error writing free block after alloc");
         }
      }

      this.gJ.a(var5);
      return var5;
   }

   private void a(RecordStore.RecordHeader var1, int var2) throws RecordStoreException {
      int var3 = var1.cQ - var2;
      int var4 = var1.cQ;
      var1.cQ = var2;
      if (var1.offset != this.gT) {
         int var5 = var1.offset + var2;
         RecordStore.RecordHeader var7 = new RecordStore.RecordHeader(this, var5, -1, var1.offset, var3, 0);

         try {
            this.b(var7);
            (var7 = new RecordStore.RecordHeader(this, var1.offset + var4)).cP = var5;
            var7.store();
            this.gJ.m(var7.id);
            this.at();
         } catch (IOException var6) {
            throw new RecordStoreException("splitRecord error");
         }
      } else {
         this.gW = var1.offset + var1.cQ;
      }

   }

   private void b(RecordStore.RecordHeader var1) throws RecordStoreException {
      try {
         if (var1.offset != this.gT) {
            var1.id = -1;
            var1.cR = this.gU;
            this.gU = var1.offset;
            var1.store();
         } else {
            int var2 = -1;

            RecordStore.RecordHeader var3;
            for(var3 = var1; var3.cP != 0 && var2 == -1; var2 = (var3 = new RecordStore.RecordHeader(this, var3.cP)).id) {
            }

            if (var2 == -1) {
               this.gT = 0;
               this.gW = this.gV;
               this.gU = 0;
            } else {
               this.gT = var3.offset;
               this.gW = var3.offset + var3.cQ;
               var1.id = -1;
               var1.cR = this.gU;
               this.gU = var1.offset;
               var1.store();
               RecordStore var9 = this;
               var3 = new RecordStore.RecordHeader(this);
               RecordStore.RecordHeader var4 = new RecordStore.RecordHeader(this);
               RecordStore.RecordHeader var5 = null;

               try {
                  int var6 = var9.gU;

                  while(var6 != 0) {
                     var3.n(var6);
                     if (var3.offset >= var9.gW) {
                        if (var3.id != -1) {
                           throw new RecordStoreException("cleanupFreeList id is not -1");
                        }

                        if (var4.offset == 0) {
                           var9.gU = var3.cR;
                        } else {
                           var4.cR = var3.cR;
                           var4.store();
                        }

                        var6 = var3.cR;
                     } else {
                        var6 = var3.cR;
                        var5 = var4;
                        var4 = var3;
                        var3 = var5;
                     }
                  }
               } catch (IOException var7) {
                  throw new RecordStoreException("cleanupFreeList block not found");
               }

               this.at();
            }

            this.gF.truncate(this.gW);
         }
      } catch (IOException var8) {
         throw new RecordStoreException("free record failed");
      }
   }

   private void c(RecordStore.RecordHeader var1) throws RecordStoreException {
      RecordStore.RecordHeader var2 = new RecordStore.RecordHeader(this);
      RecordStore.RecordHeader var3 = new RecordStore.RecordHeader(this);
      RecordStore.RecordHeader var4 = null;

      try {
         for(int var5 = this.gU; var5 != 0; var2 = var4) {
            var2.n(var5);
            if (var2.offset == var1.offset) {
               if (var2.id != -1) {
                  throw new RecordStoreException("removeFreeBlock id is not -1");
               }

               if (var3.offset == 0) {
                  this.gU = var2.cR;
               } else {
                  var3.cR = var2.cR;
                  var3.store();
               }
            }

            var5 = var2.cR;
            var4 = var3;
            var3 = var2;
         }

      } catch (IOException var6) {
         throw new RecordStoreException("removeFreeBlock block not found");
      }
   }

   private void at() throws RecordStoreException {
      synchronized(gN) {
         try {
            this.gS = System.currentTimeMillis();
            a(this.gR, gX, 0);
            a(this.gQ, gX, 4);
            a(this.gP, gX, 8);
            a(this.gO, gX, 12);
            a(this.gT, gX, 16);
            a(this.gU, gX, 20);
            boolean var2 = true;
            byte[] var7 = gX;
            long var3 = this.gS;
            var7[24] = (byte)((int)(var3 >> 56 & 255L));
            var7[25] = (byte)((int)(var3 >> 48 & 255L));
            var7[26] = (byte)((int)(var3 >> 40 & 255L));
            var7[27] = (byte)((int)(var3 >> 32 & 255L));
            var7[28] = (byte)((int)(var3 >> 24 & 255L));
            var7[29] = (byte)((int)(var3 >> 16 & 255L));
            var7[30] = (byte)((int)(var3 >> 8 & 255L));
            var7[31] = (byte)((int)(var3 & 255L));
            boolean var10000 = true;
            a(this.gV, gX, 32);
            a(this.gW, gX, 36);
            this.gF.seek(32);
            this.gF.write(gX, 0, gX.length);
         } catch (IOException var5) {
            throw new RecordStoreException("error writing record store attributes");
         }

      }
   }

   final boolean isOpen() {
      return this.gF != null;
   }

   private void checkOpen() throws RecordStoreNotOpenException {
      if (this.gF == null) {
         throw new RecordStoreNotOpenException();
      }
   }

   private void E(int var1) {
      for(int var2 = 0; var2 < this.gI.size(); ++var2) {
         ((RecordListener)this.gI.elementAt(var2)).recordChanged(this, var1);
      }

   }

   private void F(int var1) {
      for(int var2 = 0; var2 < this.gI.size(); ++var2) {
         ((RecordListener)this.gI.elementAt(var2)).recordAdded(this, var1);
      }

   }

   private void G(int var1) {
      for(int var2 = 0; var2 < this.gI.size(); ++var2) {
         ((RecordListener)this.gI.elementAt(var2)).recordDeleted(this, var1);
      }

   }

   static int getInt(byte[] var0, int var1) {
      int var10001 = var1++;
      byte var10000 = var0[var10001];
      byte var4 = var0[var10001];
      return ((var10000 << 8 | var0[var1++] & 255) << 8 | var0[var1++] & 255) << 8 | var0[var1] & 255;
   }

   static int a(int var0, byte[] var1, int var2) {
      var1[var2++] = (byte)(var0 >> 24);
      var1[var2++] = (byte)(var0 >> 16);
      var1[var2++] = (byte)(var0 >> 8);
      var1[var2] = (byte)var0;
      return 4;
   }

   int[] getRecordIDs() {
      if (this.gF == null) {
         return null;
      } else {
         int var1 = 0;
         int[] var2 = new int[this.gR];
         int var3 = this.gT;
         RecordStore.RecordHeader var5 = new RecordStore.RecordHeader(this);

         try {
            for(; var3 != 0; var3 = var5.cP) {
               var5.n(var3);
               if (var5.id > 0) {
                  var2[var1++] = var5.id;
               }
            }

            return var2;
         } catch (IOException var4) {
            return null;
         }
      }
   }

   private void au() throws RecordStoreException {
      if (this.gF != null) {
         int var1 = this.gT;

         try {
            RecordStore.RecordHeader var2;
            for(; var1 != 0; var1 = var2.cP) {
               (var2 = new RecordStore.RecordHeader(this)).n(var1);
               if (var2.cP >= var1) {
                  throw new RecordStoreException("Corrupt RMS file detected");
               }

               if (var2.id > 0) {
                  this.gJ.a(var2);
               }
            }

         } catch (IOException var3) {
         }
      }
   }

   private static boolean c(String var0, String var1, String var2) {
      boolean var3 = false;
      String var5 = null;
      String var4 = null;
      if (var1 == null) {
         var5 = RecordStoreFile.getCurrentMidletSuiteVendor();
      } else {
         var5 = var1;
      }

      if (var2 == null) {
         var4 = RecordStoreFile.getCurrentMidletSuiteName();
      } else {
         var4 = var2;
      }

      return isRecordStoreOpen(var5, var4, var0) != -1;
   }

   private RecordStore(String var1, String var2, boolean var3, String var4, String var5) throws RecordStoreException, RecordStoreNotFoundException {
      this.gB = var2;
      if (var4 == null) {
         this.vendor = RecordStoreFile.getCurrentMidletSuiteVendor();
      } else {
         this.vendor = var4;
      }

      if (var5 == null) {
         this.gC = RecordStoreFile.getCurrentMidletSuiteName();
      } else {
         this.gC = var5;
      }

      this.gD = var1;
      this.gH = new Object();
      this.gI = new Vector(3);
      boolean var23 = RecordStoreFile.b(this.gB, this.vendor, this.gC);
      if (!var3 && !var23) {
         throw new RecordStoreNotFoundException("cannot find record store file");
      } else if (var3 && !var23 && RecordStoreFile.h(this.gB) - 40 < 0) {
         throw new RecordStoreFullException();
      } else {
         try {
            this.gF = new RecordStoreFile(this.gB, this.vendor, this.gC);
            if (var3 && !var23) {
               this.gJ = new RecordStore.RecordHeaderCache(this, gK);
               this.at();
            } else {
               this.gF.seek(32);
               synchronized(gN) {
                  this.gF.read(gX);
                  this.gR = getInt(gX, 0);
                  this.gP = getInt(gX, 8);
                  this.gQ = getInt(gX, 4);
                  this.gO = getInt(gX, 12);
                  this.gT = getInt(gX, 16);
                  this.gU = getInt(gX, 20);
                  byte[] var10001 = gX;
                  boolean var25 = true;
                  byte[] var26 = var10001;
                  this.gS = (((((((long)var10001[24] << 8 | (long)var26[25] & 255L) << 8 | (long)var26[26] & 255L) << 8 | (long)var26[27] & 255L) << 8 | (long)var26[28] & 255L) << 8 | (long)var26[29] & 255L) << 8 | (long)var26[30] & 255L) << 8 | (long)var26[31] & 255L;
                  this.gV = getInt(gX, 32);
                  this.gW = getInt(gX, 36);
                  if (this.gR <= gK) {
                     this.gJ = new RecordStore.RecordHeaderCache(this, gK);
                  } else {
                     int var27;
                     for(var27 = gK << 1; var27 < this.gR && var27 <= gL; var27 <<= 1) {
                     }

                     this.gJ = new RecordStore.RecordHeaderCache(this, var27);
                  }

                  if (this.gR > 0) {
                     this.au();
                  }
               }
            }

            this.gG = this.gF.getCurrentMidletId();
         } catch (IOException var22) {
            try {
               if (this.gF != null) {
                  RecordStoreFile var24 = this.gF;
               }
            } catch (IOException var19) {
            } finally {
               this.gF = null;
            }

            throw new RecordStoreException("error opening record store file");
         }
      }
   }

   private boolean av() {
      String var1 = RecordStoreFile.d(this.gB);
      String var2 = this.gF.getUniqueIdPath();
      return var1.equals(var2);
   }

   private boolean aw() {
      if (this.av()) {
         return true;
      } else {
         return this.gQ == 1;
      }
   }

   private static native int isRecordStoreOpen(String var0, String var1, String var2);

   static RecordStoreFile a(RecordStore var0) {
      return var0.gF;
   }

   static Object access$100() {
      return gN;
   }

   static byte[] ax() {
      return gM;
   }

   static int ay() {
      return gL;
   }

   private class RecordHeaderCache {
      private RecordStore.RecordHeader[] bq;

      RecordHeaderCache(RecordStore var1, int var2) {
         this.bq = new RecordStore.RecordHeader[var2];
      }

      RecordStore.RecordHeader get(int var1) {
         int var2 = var1 & this.bq.length - 1;
         RecordStore.RecordHeader var3 = this.bq[var2];
         return this.bq[var2] != null && this.bq[var2].id != var1 ? null : var3;
      }

      final void a(RecordStore.RecordHeader var1) {
         int var2 = var1.id & this.bq.length - 1;
         if (this.bq[var2] == null) {
            this.bq[var2] = var1;
         } else {
            this.bq[var2] = var1;
            if (this.bq.length != RecordStore.ay()) {
               RecordStore.RecordHeaderCache var5;
               RecordStore.RecordHeader[] var6 = new RecordStore.RecordHeader[(var5 = this).bq.length << 1];

               for(var2 = 0; var2 < var5.bq.length; ++var2) {
                  if (var5.bq[var2] != null) {
                     RecordStore.RecordHeader var3;
                     int var4 = (var3 = var5.bq[var2]).id & var6.length - 1;
                     var6[var4] = var3;
                  }
               }

               var5.bq = var6;
            }

         }
      }

      final void m(int var1) {
         if (var1 > 0) {
            int var2 = var1 & this.bq.length - 1;
            if (this.bq[var2] != null && this.bq[var2].id == var1) {
               this.bq[var2] = null;
            }
         }

      }
   }

   private class RecordHeader {
      int offset;
      int id;
      int cP;
      int cQ;
      int cR;
      private final RecordStore cS;

      RecordHeader(RecordStore var1) {
         this.cS = var1;
      }

      RecordHeader(RecordStore var1, int var2) throws IOException {
         this.cS = var1;
         this.n(var2);
      }

      RecordHeader(RecordStore var1, int var2, int var3, int var4, int var5, int var6) {
         this.cS = var1;
         this.offset = var2;
         this.id = var3;
         this.cP = var4;
         this.cQ = var5;
         this.cR = var6;
      }

      final void n(int var1) throws IOException {
         this.offset = var1;
         RecordStore.a(this.cS).seek(this.offset);
         synchronized(RecordStore.access$100()) {
            RecordStore.a(this.cS).read(RecordStore.ax(), 0, 16);
            this.id = RecordStore.getInt(RecordStore.ax(), 0);
            this.cP = RecordStore.getInt(RecordStore.ax(), 4);
            this.cQ = RecordStore.getInt(RecordStore.ax(), 8);
            this.cR = RecordStore.getInt(RecordStore.ax(), 12);
         }
      }

      final void store() throws IOException {
         RecordStore.a(this.cS).seek(this.offset);
         synchronized(RecordStore.access$100()) {
            RecordStore.a(this.id, RecordStore.ax(), 0);
            RecordStore.a(this.cP, RecordStore.ax(), 4);
            RecordStore.a(this.cQ, RecordStore.ax(), 8);
            RecordStore.a(this.cR, RecordStore.ax(), 12);
            RecordStore.a(this.cS).write(RecordStore.ax(), 0, 16);
         }
      }

      final int a(byte[] var1, int var2) throws IOException {
         RecordStore.a(this.cS).seek(this.offset + 16);
         return RecordStore.a(this.cS).read(var1, var2, this.cR);
      }

      final void b(byte[] var1, int var2) throws IOException {
         RecordStore.a(this.cS).seek(this.offset + 16);
         RecordStore.a(this.cS).write(var1, var2, this.cR);
      }
   }
}
