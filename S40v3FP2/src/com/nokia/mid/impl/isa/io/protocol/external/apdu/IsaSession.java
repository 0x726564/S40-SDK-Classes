package com.nokia.mid.impl.isa.io.protocol.external.apdu;

import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.io.ConnectionNotFoundException;

public class IsaSession {
   public static final IsaSession INSTANCE;
   final Object closeLock = new Object();

   private IsaSession() {
   }

   synchronized AIDConnection getAidConnection(byte[] var1) throws ConnectionNotFoundException {
      nativeCheckAidPermission();
      return nativeGetAidConnection(var1);
   }

   synchronized SATConnection getSatConnection() throws ConnectionNotFoundException {
      nativeCheckSatPermission();
      return nativeGetSatConnection();
   }

   synchronized byte getSatCLA() {
      return nativeGetSatCLA();
   }

   synchronized byte[] getATR(int var1) {
      return nativeGetATR(var1);
   }

   synchronized byte[] exchangeAPDU(int var1, byte[] var2) throws InterruptedIOException, IllegalArgumentException {
      return nativeExchangeAPDU(var1, var2);
   }

   synchronized byte[] changePin(int var1, int var2) throws IOException, InterruptedIOException, SecurityException {
      return nativeChangePin(var1, var2);
   }

   synchronized byte[] disablePin(int var1, int var2) throws IOException, InterruptedIOException, SecurityException {
      return nativeDisablePin(var1, var2);
   }

   synchronized byte[] enablePin(int var1, int var2) throws IOException, InterruptedIOException, SecurityException {
      return nativeEnablePin(var1, var2);
   }

   synchronized byte[] enterPin(int var1, int var2) throws IOException, InterruptedIOException, SecurityException {
      return nativeEnterPin(var1, var2);
   }

   synchronized byte[] unblockPin(int var1, int var2, int var3) throws IOException, InterruptedIOException, SecurityException {
      return nativeUnblockPin(var1, var2, var3);
   }

   void close(int var1) throws InterruptedIOException {
      synchronized(this.closeLock) {
         nativeClose(var1);
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
