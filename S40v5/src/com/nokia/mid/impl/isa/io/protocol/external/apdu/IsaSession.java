package com.nokia.mid.impl.isa.io.protocol.external.apdu;

import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.io.ConnectionNotFoundException;

public class IsaSession {
   public static final IsaSession INSTANCE;
   private Object ag = new Object();

   private IsaSession() {
   }

   static synchronized AIDConnection a(byte[] var0) throws ConnectionNotFoundException {
      nativeCheckAidPermission();
      return nativeGetAidConnection(var0);
   }

   synchronized SATConnection getSatConnection() throws ConnectionNotFoundException {
      nativeCheckSatPermission();
      return nativeGetSatConnection();
   }

   synchronized byte getSatCLA() {
      return nativeGetSatCLA();
   }

   static synchronized byte[] b(int var0) {
      return nativeGetATR(var0);
   }

   static synchronized byte[] a(int var0, byte[] var1) throws InterruptedIOException, IllegalArgumentException {
      return nativeExchangeAPDU(var0, var1);
   }

   static synchronized byte[] c(int var0, int var1) throws IOException, InterruptedIOException, SecurityException {
      return nativeChangePin(var0, var1);
   }

   static synchronized byte[] d(int var0, int var1) throws IOException, InterruptedIOException, SecurityException {
      return nativeDisablePin(var0, var1);
   }

   static synchronized byte[] e(int var0, int var1) throws IOException, InterruptedIOException, SecurityException {
      return nativeEnablePin(var0, var1);
   }

   static synchronized byte[] f(int var0, int var1) throws IOException, InterruptedIOException, SecurityException {
      return nativeEnterPin(var0, var1);
   }

   static synchronized byte[] a(int var0, int var1, int var2) throws IOException, InterruptedIOException, SecurityException {
      return nativeUnblockPin(var0, var1, var2);
   }

   final void c(int var1) throws InterruptedIOException {
      synchronized(this.ag) {
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
