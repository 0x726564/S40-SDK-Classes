package com.nokia.mid.impl.isa.jbasslowertester;

import javax.microedition.io.StreamConnection;

public class JBassLowerTester {
   public static final int JBLT_OK = 0;
   public static final int JBLT_FAIL = 1;
   public static final byte LOCOS_HANDLE_OFFSET = 2;
   private JBassLowerTester.LowerJavaTesterThread cE = null;
   private Object cF = null;

   public JBassLowerTester() {
      this.cF = new Object();
   }

   public void init() {
      synchronized(this.cF) {
         this.cE = new JBassLowerTester.LowerJavaTesterThread(this);
         this.cE.start();
         Thread.yield();
      }
   }

   public void reset() {
      synchronized(this.cF) {
         this.cE = null;
         this.reset0();
      }
   }

   public int responderMsgAdd(int var1, String var2) {
      synchronized(this.cF) {
         return this.responderMsgAdd0(var1, var2.getBytes(), var2.length());
      }
   }

   public int pipeMsgAdd(int var1, String var2) {
      synchronized(this.cF) {
         return this.pipeMsgAdd0(var1, var2.getBytes(), var2.length());
      }
   }

   public int currentGlobalThreadRefCreate() {
      synchronized(this.cF) {
         return this.currentGlobalThreadRefCreate0();
      }
   }

   public void globalThreadRefDestroy(int var1) {
      synchronized(this.cF) {
         this.globalThreadRefDestroy0(var1);
      }
   }

   public void msgSetByte(String var1, byte var2, byte var3) {
      synchronized(this.cF) {
         this.msgSetByte0(var1.getBytes(), var1.length(), var2, var3);
      }
   }

   public int getPepHandleOfConnection(StreamConnection var1) {
      synchronized(this.cF) {
         return this.getPepHandleOfConnection0(var1);
      }
   }

   public int getPepHandleByIndex(int var1) {
      synchronized(this.cF) {
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
      private final JBassLowerTester bb;

      public LowerJavaTesterThread(JBassLowerTester var1) {
         this.bb = var1;
      }

      public void run() {
         this.bb.init0();

         while(true) {
            this.bb.processNextResponderMsg0();
         }
      }
   }
}
