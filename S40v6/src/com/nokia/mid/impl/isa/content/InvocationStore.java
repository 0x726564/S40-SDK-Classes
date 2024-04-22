package com.nokia.mid.impl.isa.content;

import java.util.Hashtable;
import javax.microedition.content.Invocation;

public class InvocationStore {
   private static InvocationStore instance;
   private Hashtable store = new Hashtable();

   public static synchronized InvocationStore getInstance() {
      if (instance != null) {
         return instance;
      } else {
         instance = new InvocationStore();
         return instance;
      }
   }

   private InvocationStore() {
   }

   public void storeInvocation(int uniqueID, Invocation inv) {
      this.store.put(new Integer(uniqueID), inv);
   }

   public Invocation removeInvocation(int uniqueID) {
      Invocation inv = (Invocation)this.store.remove(new Integer(uniqueID));
      return inv;
   }
}
