package com.nokia.mid.impl.isa.pki;

import javax.microedition.pki.Certificate;

public class NetworkCertificate implements Certificate {
   private int certificate_id = 0;
   private String subject;
   private String issuer;
   private String type;
   private Integer version;
   private String sigAlgName;
   private long notBefore;
   private long notAfter;
   private String serialNumber;

   public NetworkCertificate(int var1) {
      this.init0(var1);
      this.subject = this.getSubject0();
      this.issuer = this.getIssuer0();
      this.type = this.getType0();
      this.version = new Integer(this.getVersion0());
      this.sigAlgName = this.getSigAlgName0();
      this.notBefore = this.getNotBefore0() * 1000L;
      this.notAfter = this.getNotAfter0() * 1000L;
      this.serialNumber = this.getSerialNumber0();
      this.finished0();
   }

   public String getSubject() {
      return this.subject;
   }

   public String getIssuer() {
      return this.issuer;
   }

   public String getType() {
      return this.type;
   }

   public String getVersion() {
      return this.version.toString();
   }

   public String getSigAlgName() {
      return this.sigAlgName;
   }

   public long getNotBefore() {
      return this.notBefore;
   }

   public long getNotAfter() {
      return this.notAfter;
   }

   public String getSerialNumber() {
      return this.serialNumber;
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
