package com.nokia.mid.impl.isa.jbasslowertester;

import javax.microedition.io.StreamConnection;

public class JBassLowerTester {
   public static final int JBLT_OK = 0;
   public static final int JBLT_FAIL = 1;
   public static final byte LOCOS_HANDLE_OFFSET = 2;
   private JBassLowerTester.LowerJavaTesterThread myLowerJavaTesterThread = null;
   private Object lowerTesterIfLock = null;

   public JBassLowerTester() {
      this.lowerTesterIfLock = new Object();
   }

   public void init() {
      synchronized(this.lowerTesterIfLock) {
         this.myLowerJavaTesterThread = new JBassLowerTester.LowerJavaTesterThread();
         this.myLowerJavaTesterThread.start();
         Thread.yield();
      }
   }

   public void reset() {
      synchronized(this.lowerTesterIfLock) {
         this.myLowerJavaTesterThread = null;
         this.reset0();
      }
   }

   public int responderMsgAdd(int var1, String var2) {
      synchronized(this.lowerTesterIfLock) {
         return this.responderMsgAdd0(var1, var2.getBytes(), var2.length());
      }
   }

   public int pipeMsgAdd(int var1, String var2) {
      synchronized(this.lowerTesterIfLock) {
         return this.pipeMsgAdd0(var1, var2.getBytes(), var2.length());
      }
   }

   public int currentGlobalThreadRefCreate() {
      synchronized(this.lowerTesterIfLock) {
         return this.currentGlobalThreadRefCreate0();
      }
   }

   public void globalThreadRefDestroy(int var1) {
      synchronized(this.lowerTesterIfLock) {
         this.globalThreadRefDestroy0(var1);
      }
   }

   public void msgSetByte(String var1, byte var2, byte var3) {
      synchronized(this.lowerTesterIfLock) {
         this.msgSetByte0(var1.getBytes(), var1.length(), var2, var3);
      }
   }

   public int getPepHandleOfConnection(StreamConnection var1) {
      synchronized(this.lowerTesterIfLock) {
         return this.getPepHandleOfConnection0(var1);
      }
   }

   public int getPepHandleByIndex(int var1) {
      synchronized(this.lowerTesterIfLock) {
         return this.getPepHandleByIndex0(var1);
      }
   }

   native void init0();

   native void reset0();

   native int currentGlobalThreadRefCreate0();

   native void globalThreadRefDestroy0(int var1);

   native int responderMsgAdd0(int var1, byte[] var2, int var3);

   native void processNextResponderMsg0();

   native void msgSetByte0(byte[] var1, int var2, byte var3, byte var4);

   native int getPepHandleOfConnection0(StreamConnection var1);

   native int getPepHandleByIndex0(int var1);

   native int pipeMsgAdd0(int var1, byte[] var2, int var3);

   private class LowerJavaTesterThread extends Thread {
      public LowerJavaTesterThread() {
      }

      public void run() {
         JBassLowerTester.this.init0();

         while(true) {
            JBassLowerTester.this.processNextResponderMsg0();
         }
      }
   }
}
