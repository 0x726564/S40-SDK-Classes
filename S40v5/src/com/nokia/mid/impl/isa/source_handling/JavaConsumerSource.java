package com.nokia.mid.impl.isa.source_handling;

import com.nokia.mid.impl.isa.util.SharedObjects;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

public class JavaConsumerSource implements Runnable {
   private static final Object eT;
   private OutputStream outputStream = null;
   public Thread thread;
   private byte[] buffer = null;
   protected Vector listeners = new Vector(2);

   public JavaConsumerSource(long var1, OutputStream var3, int var4) {
      this.outputStream = var3;
   }

   public JavaConsumerSource(String var1, OutputStream var2, int var3) {
      this.outputStream = var2;
   }

   public void start() {
      if (this.thread == null) {
         this.thread = new Thread(this);
         this.thread.start();
      }

   }

   public void addSourceHandlingListener(SourceHandlingListener var1) {
      if (var1 != null) {
         this.listeners.addElement(var1);
      }

   }

   public void removePlayerListener(SourceHandlingListener var1) {
      this.listeners.removeElement(var1);
   }

   private void a(int var1, SourceHandlingException var2) {
      for(int var3 = 0; var3 < this.listeners.size(); ++var3) {
         ((SourceHandlingListener)this.listeners.elementAt(var3)).sourceHandlingEvent(var1, var2);
      }

   }

   public void consumeData() throws SourceHandlingException, IOException {
      boolean var1 = false;

      int var5;
      do {
         synchronized(eT) {
            var5 = this.readData();
         }
      } while(var5 != -1);

   }

   public void run() {
      try {
         this.consumeData();
      } catch (IOException var2) {
         this.a(1, new SourceHandlingException(var2.toString()));
      } catch (SourceHandlingException var3) {
         this.a(1, var3);
      } catch (Exception var4) {
         this.a(1, new SourceHandlingException(var4.toString()));
      }

      this.a(0, (SourceHandlingException)null);
      this.thread = null;
   }

   private native int readData() throws SourceHandlingException;

   private static native void init();

   static {
      synchronized(eT = SharedObjects.getLock("com.nokia.mid.impl.isa.source_handling.JavaConsumerSource")) {
         init();
      }
   }
}
