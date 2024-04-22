package com.nokia.mid.impl.isa.content;

import javax.microedition.content.Invocation;

public class CHAPIQueueManager {
   private static CHAPIQueueManager instance;

   public static synchronized CHAPIQueueManager getInstance() {
      if (instance != null) {
         return instance;
      } else {
         nInitialize();
         instance = new CHAPIQueueManager();
         return instance;
      }
   }

   private CHAPIQueueManager() {
   }

   public synchronized Invocation getInvocationRequest(String className) {
      if (this.isInvocationRequestAvailable(className)) {
         Invocation inv = new Invocation();
         InvocationImpl invImpl = Util.getInvocationAccessor().getInvocationImpl(inv);
         int uniqueID = nGetInvocationRequestID(className);
         invImpl.restoreFromNative(uniqueID);
         this.handlePrevious(invImpl, false);
         invImpl.setReturnPrevious(false);
         invImpl.setStatus(2);
         invImpl.storeToNative();
         return inv;
      } else {
         return null;
      }
   }

   public synchronized Invocation getInvocationResponse(String className) {
      if (this.isInvocationResponseAvailable(className)) {
         int uniqueID = nGetInvocationResponseID(className);
         Invocation inv = null;
         InvocationImpl invImpl = null;
         inv = InvocationStore.getInstance().removeInvocation(uniqueID);
         if (inv == null) {
            inv = new Invocation();
         }

         invImpl = Util.getInvocationAccessor().getInvocationImpl(inv);
         invImpl.restoreFromNative(uniqueID);
         this.handlePrevious(invImpl, true);
         invImpl.setReturnPrevious(true);
         InvocationImpl prev = invImpl.getPrevious();
         if (prev != null) {
            prev.setStatus(2);
         }

         nDiscardInvocation(uniqueID);
         return inv;
      } else {
         return null;
      }
   }

   public synchronized boolean isInvocationResponseAvailable(String className) {
      return nIsInvocationResponseAvailable(className);
   }

   public synchronized boolean isInvocationRequestAvailable(String className) {
      return nIsInvocationRequestAvailable(className);
   }

   private void handlePrevious(InvocationImpl invImpl, boolean checkStore) {
      if (nHasPrevious(invImpl.getUniqueID())) {
         int uniqueIDPrev = nGetPreviousID(invImpl.getUniqueID());
         Invocation prevInv = null;
         InvocationImpl prevInvImpl = null;
         if (checkStore) {
            prevInv = InvocationStore.getInstance().removeInvocation(uniqueIDPrev);
         }

         if (prevInv == null) {
            prevInv = new Invocation();
         }

         prevInvImpl = Util.getInvocationAccessor().getInvocationImpl(prevInv);
         prevInvImpl.restoreFromNative(uniqueIDPrev);
         prevInvImpl.setReturnPrevious(false);
         invImpl.setPrevious(prevInvImpl);
      }

   }

   private static native void nInitialize();

   private static native int nGetInvocationRequestID(String var0);

   private static native int nGetInvocationResponseID(String var0);

   private static native boolean nIsInvocationRequestAvailable(String var0);

   private static native boolean nIsInvocationResponseAvailable(String var0);

   private static native boolean nHasPrevious(int var0);

   private static native int nGetPreviousID(int var0);

   private static native void nDiscardInvocation(int var0);
}
