package com.nokia.mid.impl.isa.pki;

import javax.microedition.pki.Certificate;

public class NetworkCertificate implements Certificate {
   private String fi;
   private String jg;
   private String type;
   private Integer jh;
   private String ji;
   private long jj;
   private long jk;
   private String jl;

   public NetworkCertificate(int var1) {
      this.init0(var1);
      this.fi = this.getSubject0();
      this.jg = this.getIssuer0();
      this.type = this.getType0();
      this.jh = new Integer(this.getVersion0());
      this.ji = this.getSigAlgName0();
      this.jj = this.getNotBefore0() * 1000L;
      this.jk = this.getNotAfter0() * 1000L;
      this.jl = this.getSerialNumber0();
      this.finished0();
   }

   public String getSubject() {
      return this.fi;
   }

   public String getIssuer() {
      return this.jg;
   }

   public String getType() {
      return this.type;
   }

   public String getVersion() {
      return this.jh.toString();
   }

   public String getSigAlgName() {
      return this.ji;
   }

   public long getNotBefore() {
      return this.jj;
   }

   public long getNotAfter() {
      return this.jk;
   }

   public String getSerialNumber() {
      return this.jl;
   }

   private native void init0(int var1);

   private native void finished0();

   private native String getSubject0();

   private native String getIssuer0();

   private native String getType0();

   private native int getVersion0();

   private native String getSigAlgName0();

   private native long getNotBefore0();

   private native long getNotAfter0();

   private native String getSerialNumber0();
}
