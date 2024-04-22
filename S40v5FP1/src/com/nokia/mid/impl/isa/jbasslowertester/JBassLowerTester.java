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

   public int responderMsgAdd(int thread_ref, String msg_name) {
      synchronized(this.lowerTesterIfLock) {
         return this.responderMsgAdd0(thread_ref, msg_name.getBytes(), msg_name.length());
      }
   }

   public int pipeMsgAdd(int pepHandle, String msg_name) {
      synchronized(this.lowerTesterIfLock) {
         return this.pipeMsgAdd0(pepHandle, msg_name.getBytes(), msg_name.length());
      }
   }

   public int currentGlobalThreadRefCreate() {
      synchronized(this.lowerTesterIfLock) {
         return this.currentGlobalThreadRefCreate0();
      }
   }

   public void globalThreadRefDestroy(int thread_ref) {
      synchronized(this.lowerTesterIfLock) {
         this.globalThreadRefDestroy0(thread_ref);
      }
   }

   public void msgSetByte(String msg_name, byte index, byte newValue) {
      synchronized(this.lowerTesterIfLock) {
         this.msgSetByte0(msg_name.getBytes(), msg_name.length(), index, newValue);
      }
   }

   public int getPepHandleOfConnection(StreamConnection connectionObject) {
      synchronized(this.lowerTesterIfLock) {
         return this.getPepHandleOfConnection0(connectionObject);
      }
   }

   public int getPepHandleByIndex(int index) {
      synchronized(this.lowerTesterIfLock) {
         return this.getPepHandleByIndex0(index);
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
