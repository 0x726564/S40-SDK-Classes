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

   public static boolean addFileSystemListener(FileSystemListener var0) {
      if (getInstance().listeners == null) {
         return false;
      } else if (var0 == null) {
         throw new NullPointerException("listener cannot be null");
      } else {
         if (!getInstance().listeners.contains(var0)) {
            getInstance().listeners.addElement(var0);
         }

         return true;
      }
   }

   public static boolean removeFileSystemListener(FileSystemListener var0) {
      if (getInstance().listeners == null) {
         return false;
      } else if (var0 == null) {
         throw new NullPointerException("listener cannot be null");
      } else {
         return getInstance().listeners.removeElement(var0);
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

      PrivateEnumeration(String var1, char var2) {
         this.list = var1;
         this.delimiter = var2;
         this.index = 0;
      }

      public boolean hasMoreElements() {
         return this.list != null && this.list.length() != 0 && this.index != -1;
      }

      public Object nextElement() {
         if (this.index == -1) {
            throw new NoSuchElementException();
         } else {
            int var2 = this.list.indexOf(this.delimiter, this.index);
            String var1;
            if (var2 != -1) {
               var1 = this.list.substring(this.index, var2);
               this.index = var2 + 1;
            } else {
               var1 = this.list.substring(this.index);
               this.index = -1;
            }

            return FileSystemRegistry.SPRINT_CONFIG ? convertRoot(var1) : var1;
         }
      }

      private static String convertRoot(String var0) {
         if (var0.equals("C:/")) {
            return "/";
         } else {
            return var0.equals("E:/") ? "MemoryCard/" : var0;
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
                     Object[] var2 = (Object[])FileConnectionEventConsumer.this.eventQueue.pop();
                     Vector var3 = FileSystemRegistry.getInstance().listeners;

                     for(int var4 = 0; var4 < var3.size(); ++var4) {
                        ((FileSystemListener)var3.elementAt(var4)).rootChanged((Integer)var2[0], (String)var2[1] + ":/");
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

      public void consumeEvent(int var1, int var2, int var3) {
         if (var1 == 8) {
            synchronized(this.dispatcher) {
               Object[] var5 = new Object[2];
               var5[0] = new Integer(FileSystemRegistry.retrieveEvent(var5, var3));
               this.eventQueue.insertElementAt(var5, this.eventQueue.size());
               this.dispatcher.notify();
            }
         }
      }
   }
}
