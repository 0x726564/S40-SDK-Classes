package com.nokia.mid.s40.service_api_utils;

public class ProtocolVersion {
   private long majorVersion = 0L;
   private long minorVersion = 0L;

   ProtocolVersion(long majorVersionNumber, long minorVersionNumber) {
      this.majorVersion = majorVersionNumber;
      this.minorVersion = minorVersionNumber;
   }

   public long getMajorVersion() {
      return this.majorVersion;
   }

   public long getMinorVersion() {
      return this.minorVersion;
   }
}
