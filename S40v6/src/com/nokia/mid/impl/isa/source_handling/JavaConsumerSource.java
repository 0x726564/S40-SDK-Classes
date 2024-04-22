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

   public JavaConsumerSource(long cmSourceID, OutputStream out, int max) {
      this.outputStream = out;
      this.sourceID = cmSourceID;
      this.maxBufferSize = max;
   }

   public JavaConsumerSource(String _filename, OutputStream out, int _maxBufferSize) {
      this.outputStream = out;
      this.filename = _filename;
      this.maxBufferSize = _maxBufferSize;
   }

   public void start() {
      if (this.thread == null) {
         this.thread = new Thread(this);
         this.thread.start();
      }

   }

   public void addSourceHandlingListener(SourceHandlingListener listener) {
      if (listener != null) {
         this.listeners.addElement(listener);
      }

   }

   public void removePlayerListener(SourceHandlingListener listener) {
      this.listeners.removeElement(listener);
   }

   private void notifyListeners(int type, SourceHandlingException exception) {
      for(int i = 0; i < this.listeners.size(); ++i) {
         SourceHandlingListener l = (SourceHandlingListener)this.listeners.elementAt(i);
         l.sourceHandlingEvent(type, exception);
      }

   }

   public void consumeData() throws SourceHandlingException, IOException {
      boolean var1 = false;

      int dataRead;
      do {
         dataRead = this.readData();
         if (this.buffer != null && dataRead > 0) {
            this.outputStream.write(this.buffer, 0, dataRead);
            this.outputStream.flush();
         }
      } while(dataRead != -1);

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
