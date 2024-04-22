package com.nokia.mid.impl.isa.pim;

class PBNativeRecord {
   private char an;
   private byte[] ao;

   public PBNativeRecord() {
      this.an = '\uffff';
      this.ao = null;
      this.ao = null;
      this.an = '\uffff';
   }

   public PBNativeRecord(PBNativeRecord var1) {
      this();
      if (var1 != null) {
         this.ao = new byte[var1.ao.length];
         System.arraycopy(var1.ao, 0, this.ao, 0, this.ao.length);
      }

   }

   protected final void l() {
      this.an = '\uffff';
   }

   protected int getLocationIndex() {
      return this.an;
   }

   protected final boolean m() {
      return this.ao != null;
   }

   protected final void n() {
      this.ao = null;
      this.an = '\uffff';
   }

   protected byte[] getMessage() {
      return this.ao;
   }
}
