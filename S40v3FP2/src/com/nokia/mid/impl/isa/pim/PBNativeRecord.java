package com.nokia.mid.impl.isa.pim;

class PBNativeRecord {
   protected static final char PND_LOCATION_ANY = '\uffff';
   private char pbLocationIndex;
   private byte[] byPBRecord;

   public PBNativeRecord() {
      this.pbLocationIndex = '\uffff';
      this.byPBRecord = null;
      this.byPBRecord = null;
      this.removeLocationIndex();
   }

   public PBNativeRecord(PBNativeRecord var1) {
      this();
      if (var1 != null) {
         this.byPBRecord = new byte[var1.byPBRecord.length];
         System.arraycopy(var1.byPBRecord, 0, this.byPBRecord, 0, this.byPBRecord.length);
      }

   }

   protected void removeLocationIndex() {
      this.pbLocationIndex = '\uffff';
   }

   protected int getLocationIndex() {
      return this.pbLocationIndex;
   }

   protected boolean IsValidItem() {
      return this.byPBRecord != null;
   }

   protected void releaseRecord() {
      this.byPBRecord = null;
      this.removeLocationIndex();
   }

   protected byte[] getMessage() {
      return this.byPBRecord;
   }
}
