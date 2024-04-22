package javax.microedition.io.file;

import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.pri.PriAccess;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;

public class FileSystemRegistry {
   static boolean h = PriAccess.getInt(5) == 1;
   Vector listeners;
   private static FileSystemRegistry i;

   private FileSystemRegistry() {
      if (registerForNotifications()) {
         InitJALM.s_getEventProducer().attachEventConsumer(8, new FileSystemRegistry.FileConnectionEventConsumer());
         this.listeners = new Vector(5);
      }

   }

   static FileSystemRegistry getInstance() {
      if (i == null) {
         i = new FileSystemRegistry();
      }

      return i;
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
      private String r;
      private int index;
      private char s;

      PrivateEnumeration(String var1, char var2) {
         this.r = var1;
         this.s = '*';
         this.index = 0;
      }

      public boolean hasMoreElements() {
         return this.r != null && this.r.length() != 0 && this.index != -1;
      }

      public Object nextElement() {
         if (this.index == -1) {
            throw new NoSuchElementException();
         } else {
            String var1;
            int var2;
            if ((var2 = this.r.indexOf(this.s, this.index)) != -1) {
               var1 = this.r.substring(this.index, var2);
               this.index = var2 + 1;
            } else {
               var1 = this.r.substring(this.index);
               this.index = -1;
            }

            if (FileSystemRegistry.h) {
               if (var1.equals("C:/")) {
                  return "/";
               } else {
                  return var1.equals("E:/") ? "MemoryCard/" : var1;
               }
            } else {
               return var1;
            }
         }
      }
   }

   private static class FileConnectionEventConsumer implements EventConsumer {
      Stack t = new Stack();
      Thread u = new Thread(new Runnable(this) {
         private final FileSystemRegistry.FileConnectionEventConsumer g;

         {
            this.g = var1;
         }

         public void run() {
            while(true) {
               synchronized(this.g.u) {
                  if (this.g.t.size() != 0) {
                     Object[] var2 = (Object[])this.g.t.pop();
                     Vector var3 = FileSystemRegistry.getInstance().listeners;

                     for(int var4 = 0; var4 < var3.size(); ++var4) {
                        ((FileSystemListener)var3.elementAt(var4)).rootChanged((Integer)var2[0], (String)var2[1] + ":/");
                     }
                  }
               }
            }
         }
      });

      FileConnectionEventConsumer() {
         this.u.start();
      }

      public void consumeEvent(int var1, int var2, int var3) {
         if (var1 == 8) {
            synchronized(this.u) {
               Object[] var5;
               (var5 = new Object[2])[0] = new Integer(FileSystemRegistry.retrieveEvent(var5, var3));
               this.t.insertElementAt(var5, this.t.size());
            }
         }
      }
   }
}
