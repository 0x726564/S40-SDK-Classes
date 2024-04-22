package com.nokia.mid.impl.isa.content;

import javax.microedition.content.ContentHandlerServer;
import javax.microedition.content.Invocation;
import javax.microedition.content.RequestListener;

public class ContentHandlerServerImpl extends ContentHandlerImpl implements ContentHandlerServer {
   private String[] visibilityList;
   private Object waitRequestLock;

   public int accessAllowedCount() {
      return this.visibilityList.length;
   }

   public void cancelGetRequest() {
      synchronized(this.waitRequestLock) {
         this.waitRequestLock.notifyAll();
      }
   }

   public synchronized boolean finish(Invocation invocation, int status) {
      if (invocation == null) {
         throw new NullPointerException();
      } else if (status != 5 && status != 6 && status != 8) {
         throw new IllegalArgumentException();
      } else if (invocation.getStatus() != 2 && invocation.getStatus() != 4) {
         throw new IllegalStateException();
      } else {
         InvocationImpl impl = Util.getInvocationAccessor().getInvocationImpl(invocation);
         impl.setStatus(status);
         return impl.storeToNative();
      }
   }

   public String getAccessAllowed(int index) {
      return this.visibilityList[index];
   }

   public Invocation getRequest(boolean wait) {
      if (!wait) {
         return CHAPIQueueManager.getInstance().getInvocationRequest(this.getClassName());
      } else {
         while(true) {
            try {
               synchronized(this.waitRequestLock) {
                  Invocation inv = CHAPIQueueManager.getInstance().getInvocationRequest(this.getClassName());
                  if (inv == null) {
                     this.waitRequestLock.wait();
                     return CHAPIQueueManager.getInstance().getInvocationRequest(this.getClassName());
                  }

                  return inv;
               }
            } catch (InterruptedException var6) {
            }
         }
      }
   }

   public boolean isAccessAllowed(String ID) {
      if (ID == null) {
         throw new NullPointerException();
      } else if (this.visibilityList.length == 0) {
         return true;
      } else {
         for(int i = 0; i < this.visibilityList.length; ++i) {
            if (ID.startsWith(this.visibilityList[i])) {
               return true;
            }
         }

         return false;
      }
   }

   public void setListener(RequestListener listener) {
      CHAPIConsumer.getInstance().setRequestListener(listener, this);
   }

   void unblockGetRequest() {
      synchronized(this.waitRequestLock) {
         this.waitRequestLock.notify();
      }
   }

   void defaultConstructor() {
      this.waitRequestLock = new Object();
   }
}
