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

   public PBNativeRecord(PBNativeRecord other) {
      this();
      if (other != null) {
         this.byPBRecord = new byte[other.byPBRecord.length];
         System.arraycopy(other.byPBRecord, 0, this.byPBRecord, 0, this.byPBRecord.length);
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
