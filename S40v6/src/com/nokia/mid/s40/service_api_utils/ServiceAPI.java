package com.nokia.mid.s40.service_api_utils;

import com.nokia.mid.impl.isa.util.SharedObjects;

public class ServiceAPI {
   private static long majorVersionNumber;
   private static long minorVersionNumber;
   private static final Object sharedLock = SharedObjects.getLock("com.nokia.mid.s40.service_api_utils.ServiceAPIMatchVersion");

   private ServiceAPI() {
   }

   public static ProtocolVersion matchVersion(String clientVersion, String serverVersion) {
      synchronized(sharedLock) {
         return matchVersions0(clientVersion, serverVersion) ? new ProtocolVersion(majorVersionNumber, minorVersionNumber) : null;
      }
   }

   private static native boolean matchVersions0(String var0, String var1);
}
