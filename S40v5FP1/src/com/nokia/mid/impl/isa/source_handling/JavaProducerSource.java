package com.nokia.mid.impl.isa.source_handling;

import com.nokia.mid.impl.isa.util.SharedObjects;
import com.sun.midp.io.j2me.http.Protocol;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class JavaProducerSource implements Runnable {
   static final int JAVA_RAM_BUFFER_SIZE = 131072;
   private int activeSourceHigh = 262144;
   private int activeSourceLow = 16384;
   private InputStream inputStream = null;
   private String URI;
   private int buffer_size = 131072;
   private byte[] buffer = null;
   private int number_of_bytes_in_buffer = -1;
   private int CM_Source;
   private int CM_HTTP_Source = 0;
   private Object httpToken = null;
   private static final int SOURCE_UNDEFINED = 0;
   private static final int SOURCE_RAM = 1;
   private static final int SOURCE_ACTIVE = 2;
   private static final int SOURCE_FILE = 3;
   private static final int SOURCE_HTTP = 4;
   private int SOURCE_CATEGORY = 0;
   private static final String HTTP_PREFIX = "http://";
   private static final String FILE_PREFIX = "file://";
   private boolean connected = false;
   private boolean generatedSourceID = false;
   protected Vector listeners = new Vector(2);
   public Thread thread;
   public boolean useActiveSource = false;
   private static final int CHUNK_SIZE = 512;
   private static final Object sharedLock = SharedObjects.getLock("com.nokia.mid.impl.isa.source_handling.JavaProducerSource");

   public JavaProducerSource() {
      this.SOURCE_CATEGORY = 1;
   }

   public JavaProducerSource(InputStream in) {
      this.inputStream = in;
      this.SOURCE_CATEGORY = 1;
      this.connected = true;
   }

   public JavaProducerSource(InputStream in, int buffer_size) {
      this.inputStream = in;
      this.buffer_size = buffer_size;
      this.SOURCE_CATEGORY = 1;
      this.connected = true;
   }

   public JavaProducerSource(String URI, boolean useActiveSource) throws IOException, SourceHandlingException {
      this.useActiveSource = useActiveSource;
      this.commonURLcode(URI);
   }

   public JavaProducerSource(String URI, int buffer_size, boolean useActiveSource) throws IOException, SourceHandlingException {
      this.useActiveSource = useActiveSource;
      this.buffer_size = buffer_size;
      this.commonURLcode(URI);
   }

   public void setHighLowWaterMarks(int high, int low) {
      if (high > 131072) {
         this.activeSourceHigh = high;
      }

      if (low < this.activeSourceHigh) {
         this.activeSourceLow = low;
      }

   }

   private void commonURLcode(String URI) throws IOException, SourceHandlingException {
      this.URI = URI;
      String lower_case_URI = URI.toLowerCase();
      if (lower_case_URI.startsWith("http://")) {
         this.SOURCE_CATEGORY = 1;
         HttpConnection httpCon = (HttpConnection)Connector.open(URI);
         int response;
         if (this.useActiveSource) {
            this.httpToken = ((Protocol)httpCon).prepareCMSourceId();
            if ((response = httpCon.getResponseCode()) != 200) {
               httpCon.close();
               throw new SourceHandlingException("Invalid resource: " + URI + " caused error: " + response);
            }

            this.SOURCE_CATEGORY = 4;
         } else {
            if ((response = httpCon.getResponseCode()) != 200) {
               httpCon.close();
               throw new SourceHandlingException("Invalid resource: " + URI + " caused error: " + response);
            }

            this.inputStream = httpCon.openInputStream();
            long contentLength = httpCon.getLength();
            if (contentLength != -1L && contentLength < (long)this.buffer_size) {
               this.buffer_size = (int)contentLength;
            }

            httpCon.close();
            this.connected = true;
         }
      } else {
         if (!lower_case_URI.startsWith("file://")) {
            throw new SourceHandlingException("URI protocol not supported");
         }

         this.SOURCE_CATEGORY = 3;
         if (!nFileIsValid(URI.substring("file://".length()))) {
            throw new SourceHandlingException("Invalid resource: " + URI);
         }
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

   public byte[] generateSourceId() throws SourceHandlingException {
      byte[] sourceId;
      switch(this.SOURCE_CATEGORY) {
      case 0:
      default:
         throw new SourceHandlingException();
      case 1:
         synchronized(sharedLock) {
            sourceId = this.createRAMSource(this.buffer, this.number_of_bytes_in_buffer);
            break;
         }
      case 2:
         synchronized(sharedLock) {
            sourceId = this.createActiveSource();
            this.writeData(this.buffer, this.number_of_bytes_in_buffer);
            break;
         }
      case 3:
         synchronized(sharedLock) {
            sourceId = this.createFileSource(this.URI.substring("file://".length()));
            break;
         }
      case 4:
         synchronized(sharedLock) {
            sourceId = this.obtainHTTPSource(this.httpToken);
         }
      }

      this.generatedSourceID = true;
      return sourceId;
   }

   public void setData(byte[] data) {
      this.buffer = data;
      this.number_of_bytes_in_buffer = this.buffer.length;
   }

   public void fetchData() throws IOException {
      if (this.inputStream != null && this.buffer_size != 0) {
         int dataRead;
         if (this.useActiveSource) {
            this.buffer = new byte[this.buffer_size];
            this.number_of_bytes_in_buffer = this.inputStream.read(this.buffer, 0, this.buffer_size - 1);
            dataRead = this.inputStream.read();
            if (dataRead == -1) {
               this.inputStream.close();
               this.connected = false;
            } else {
               this.buffer[this.number_of_bytes_in_buffer] = (byte)dataRead;
               ++this.number_of_bytes_in_buffer;
               this.SOURCE_CATEGORY = 2;
            }
         } else {
            boolean someData = false;
            Vector chunkVector = new Vector(5);

            byte[] chunk;
            do {
               chunk = new byte[512];
               if ((dataRead = this.readData(chunk, 0, 512)) != 0) {
                  if (dataRead > 0) {
                     chunkVector.addElement(chunk);
                  }

                  someData = true;
               }
            } while(dataRead > 0);

            if (someData) {
               int pos = 0;
               this.number_of_bytes_in_buffer = chunkVector.size() * 512 + -dataRead;
               this.buffer = new byte[this.number_of_bytes_in_buffer];

               for(int i = 0; i < chunkVector.size(); ++i) {
                  byte[] element = (byte[])chunkVector.elementAt(i);
                  System.arraycopy(element, 0, this.buffer, pos, 512);
                  pos += 512;
               }

               if (dataRead < 0) {
                  System.arraycopy(chunk, 0, this.buffer, pos, -dataRead);
               }
            }

            this.inputStream.close();
            this.connected = false;
         }
      }

   }

   public void start() {
      if (!this.generatedSourceID) {
         this.notifyListeners(1, new SourceHandlingException("Failed to called generateSourceID"));
      } else {
         if ((this.SOURCE_CATEGORY == 2 || this.SOURCE_CATEGORY == 4) && this.thread == null) {
            this.thread = new Thread(this);
            this.thread.start();
         } else {
            this.notifyListeners(0, (SourceHandlingException)null);
         }

      }
   }

   public void disconnect() {
      if (this.SOURCE_CATEGORY == 2) {
         if (this.thread != null) {
            synchronized(sharedLock) {
               this.nAbortSuspendedThread();
            }
         }
      } else if (this.connected) {
         try {
            this.inputStream.close();
            this.connected = false;
         } catch (IOException var3) {
         }
      }

   }

   public void run() {
      boolean cont;
      if (this.SOURCE_CATEGORY == 4) {
         cont = true;

         while(cont) {
            synchronized(sharedLock) {
               cont = this.processHTTPdata();
            }
         }
      } else {
         try {
            cont = false;
            boolean okay = true;

            int dataRead;
            do {
               dataRead = this.inputStream.read(this.buffer);
               if (dataRead > 0) {
                  synchronized(sharedLock) {
                     okay = this.writeData(this.buffer, dataRead);
                  }
               }
            } while(dataRead != -1 && okay);

            this.inputStream.close();
            this.connected = false;
         } catch (IOException var9) {
            this.notifyListeners(1, new SourceHandlingException(var9.toString()));
         }
      }

      synchronized(sharedLock) {
         this.setEOF();
      }

      this.notifyListeners(0, (SourceHandlingException)null);
      this.thread = null;
   }

   private void notifyListeners(int type, SourceHandlingException exception) {
      for(int i = 0; i < this.listeners.size(); ++i) {
         SourceHandlingListener l = (SourceHandlingListener)this.listeners.elementAt(i);
         l.sourceHandlingEvent(type, exception);
      }

   }

   private int readData(byte[] store, int offset, int length) throws IOException {
      int len = length;

      int bytesRead;
      do {
         bytesRead = this.inputStream.read(store, offset, len);
         if (bytesRead == -1) {
            break;
         }

         offset += bytesRead;
         len -= bytesRead;
         if (len != 0) {
            Thread.yield();
         }
      } while(len != 0);

      return bytesRead == -1 ? -(length - len) : length - len;
   }

   private native byte[] createRAMSource(byte[] var1, int var2) throws SourceHandlingException;

   private native byte[] createFileSource(String var1);

   private native byte[] createActiveSource();

   private native byte[] obtainHTTPSource(Object var1);

   private native boolean processHTTPdata();

   private native boolean writeData(byte[] var1, int var2);

   private native void setEOF();

   private native void nAbortSuspendedThread();

   private static native void init();

   private static native boolean nFileIsValid(String var0);

   static {
      synchronized(sharedLock) {
         init();
      }
   }
}
