package com.nokia.mid.impl.isa.source_handling;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

public class JavaConsumerSource implements Runnable {
   OutputStream outputStream = null;
   String filename = null;
   long sourceID;
   int CM_Source;
   int maxBufferSize;
   public Thread thread;
   byte[] buffer = null;
   protected Vector listeners = new Vector(2);

   public JavaConsumerSource(long var1, OutputStream var3, int var4) {
      this.outputStream = var3;
      this.sourceID = var1;
      this.maxBufferSize = var4;
   }

   public JavaConsumerSource(String var1, OutputStream var2, int var3) {
      this.outputStream = var2;
      this.filename = var1;
      this.maxBufferSize = var3;
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

   private void notifyListeners(int var1, SourceHandlingException var2) {
      for(int var3 = 0; var3 < this.listeners.size(); ++var3) {
         SourceHandlingListener var4 = (SourceHandlingListener)this.listeners.elementAt(var3);
         var4.sourceHandlingEvent(var1, var2);
      }

   }

   public void consumeData() throws SourceHandlingException, IOException {
      boolean var1 = false;

      int var2;
      do {
         var2 = this.readData();
         if (this.buffer != null && var2 > 0) {
            this.outputStream.write(this.buffer, 0, var2);
         }
      } while(var2 != -1);

   }

   public void run() {
      try {
         this.consumeData();
      } catch (IOException var2) {
         this.notifyListeners(1, new SourceHandlingException(var2.toString()));
      } catch (SourceHandlingException var3) {
         this.notifyListeners(1, var3);
      } catch (Exception var4) {
         this.notifyListeners(1, new SourceHandlingException(var4.toString()));
      }

      this.notifyListeners(0, (SourceHandlingException)null);
      this.thread = null;
   }

   private native int readData() throws SourceHandlingException;

   private static native void init();

   static {
      init();
   }
}
