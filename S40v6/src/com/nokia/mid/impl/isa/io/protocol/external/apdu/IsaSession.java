package com.nokia.mid.impl.isa.io.protocol.external.apdu;

import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.io.ConnectionNotFoundException;

public class IsaSession {
   public static final IsaSession INSTANCE;
   final Object closeLock = new Object();

   private IsaSession() {
   }

   synchronized AIDConnection getAidConnection(byte[] aid) throws ConnectionNotFoundException {
      nativeCheckAidPermission();
      return nativeGetAidConnection(aid);
   }

   synchronized SATConnection getSatConnection() throws ConnectionNotFoundException {
      nativeCheckSatPermission();
      return nativeGetSatConnection();
   }

   synchronized byte getSatCLA() {
      return nativeGetSatCLA();
   }

   synchronized byte[] getATR(int channelId) {
      return nativeGetATR(channelId);
   }

   synchronized byte[] exchangeAPDU(int channelId, byte[] commandAPDU) throws InterruptedIOException, IllegalArgumentException {
      return nativeExchangeAPDU(channelId, commandAPDU);
   }

   synchronized byte[] changePin(int channelId, int pinID) throws IOException, InterruptedIOException, SecurityException {
      return nativeChangePin(channelId, pinID);
   }

   synchronized byte[] disablePin(int channelId, int pinID) throws IOException, InterruptedIOException, SecurityException {
      return nativeDisablePin(channelId, pinID);
   }

   synchronized byte[] enablePin(int channelId, int pinID) throws IOException, InterruptedIOException, SecurityException {
      return nativeEnablePin(channelId, pinID);
   }

   synchronized byte[] enterPin(int channelId, int pinID) throws IOException, InterruptedIOException, SecurityException {
      return nativeEnterPin(channelId, pinID);
   }

   synchronized byte[] unblockPin(int channelId, int blockedPinID, int unblockedPinID) throws IOException, InterruptedIOException, SecurityException {
      return nativeUnblockPin(channelId, blockedPinID, unblockedPinID);
   }

   void close(int channelId) throws InterruptedIOException {
      synchronized(this.closeLock) {
         nativeClose(channelId);
      }
   }

   private static native byte nativeGetSatCLA();

   private static native void nativeCheckAidPermission();

   private static native AIDConnection nativeGetAidConnection(byte[] var0);

   private static native void nativeCheckSatPermission();

   private static native SATConnection nativeGetSatConnection();

   private static native void nativeClose(int var0);

   private static native byte[] nativeGetATR(int var0);

   private static native byte[] nativeExchangeAPDU(int var0, byte[] var1);

   private static native byte[] nativeChangePin(int var0, int var1);

   private static native byte[] nativeDisablePin(int var0, int var1);

   private static native byte[] nativeEnablePin(int var0, int var1);

   private static native byte[] nativeEnterPin(int var0, int var1);

   private static native byte[] nativeUnblockPin(int var0, int var1, int var2);

   private static native void nativeInitApdu();

   static {
      nativeInitApdu();
      INSTANCE = new IsaSession();
   }
}
