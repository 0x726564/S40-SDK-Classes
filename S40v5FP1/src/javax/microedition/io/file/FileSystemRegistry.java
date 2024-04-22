package javax.microedition.io.file;

import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.pri.PriAccess;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;

public class FileSystemRegistry {
   static boolean SPRINT_CONFIG = PriAccess.getInt(5) == 1;
   Vector listeners;
   private static FileSystemRegistry instance;

   private FileSystemRegistry() {
      if (registerForNotifications()) {
         InitJALM.s_getEventProducer().attachEventConsumer(8, new FileSystemRegistry.FileConnectionEventConsumer());
         this.listeners = new Vector(5);
      }

   }

   static FileSystemRegistry getInstance() {
      if (instance == null) {
         instance = new FileSystemRegistry();
      }

      return instance;
   }

   public static boolean addFileSystemListener(FileSystemListener listener) {
      if (getInstance().listeners == null) {
         return false;
      } else if (listener == null) {
         throw new NullPointerException("listener cannot be null");
      } else {
         if (!getInstance().listeners.contains(listener)) {
            getInstance().listeners.addElement(listener);
         }

         return true;
      }
   }

   public static boolean removeFileSystemListener(FileSystemListener listener) {
      if (getInstance().listeners == null) {
         return false;
      } else if (listener == null) {
         throw new NullPointerException("listener cannot be null");
      } else {
         return getInstance().listeners.removeElement(listener);
      }
   }

   public static Enumeration listRoots() {
      return new FileSystemRegistry.PrivateEnumeration(getRoots(), '*');
   }

   private static native String getRoots();

   static native boolean registerForNotifications();

   static native int retrieveEvent(Object[] var0, int var1);

   private static class PrivateEnumeration implements Enumeration {
      private String list;
      private int index;
      private char delimiter;

      PrivateEnumeration(String list, char delimiter) {
         this.list = list;
         this.delimiter = delimiter;
         this.index = 0;
      }

      public boolean hasMoreElements() {
         return this.list != null && this.list.length() != 0 && this.index != -1;
      }

      public Object nextElement() {
         if (this.index == -1) {
            throw new NoSuchElementException();
         } else {
            int nextIndex = this.list.indexOf(this.delimiter, this.index);
            String result;
            if (nextIndex != -1) {
               result = this.list.substring(this.index, nextIndex);
               this.index = nextIndex + 1;
            } else {
               result = this.list.substring(this.index);
               this.index = -1;
            }

            return FileSystemRegistry.SPRINT_CONFIG ? convertRoot(result) : result;
         }
      }

      private static String convertRoot(String root) {
         if (root.equals("C:/")) {
            return "/";
         } else {
            return root.equals("E:/") ? "MemoryCard/" : root;
         }
      }
   }

   private static class FileConnectionEventConsumer implements EventConsumer {
      Stack eventQueue = new Stack();
      Thread dispatcher = new Thread(new Runnable() {
         public void run() {
            while(true) {
               synchronized(FileConnectionEventConsumer.this.dispatcher) {
                  if (FileConnectionEventConsumer.this.eventQueue.size() != 0) {
                     Object[] event = (Object[])FileConnectionEventConsumer.this.eventQueue.pop();
                     Vector listeners = FileSystemRegistry.getInstance().listeners;

                     for(int i = 0; i < listeners.size(); ++i) {
                        ((FileSystemListener)listeners.elementAt(i)).rootChanged((Integer)event[0], (String)event[1] + ":/");
                     }
                  } else {
                     try {
                        FileConnectionEventConsumer.this.dispatcher.wait();
                     } catch (InterruptedException var6) {
                     }
                  }
               }
            }
         }
      });

      FileConnectionEventConsumer() {
         this.dispatcher.start();
      }

      public void consumeEvent(int category, int type, int param) {
         if (category == 8) {
            synchronized(this.dispatcher) {
               Object[] event = new Object[2];
               event[0] = new Integer(FileSystemRegistry.retrieveEvent(event, param));
               this.eventQueue.insertElementAt(event, this.eventQueue.size());
               this.dispatcher.notify();
            }
         }
      }
   }
}
