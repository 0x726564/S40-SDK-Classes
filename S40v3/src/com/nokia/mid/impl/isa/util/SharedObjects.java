package com.nokia.mid.impl.isa.util;

import java.lang.ref.Reference;
import java.util.Hashtable;

public final class SharedObjects {
   public static final String GLOBAL_LOCK_NAME = "com.nokia.mid.impl.isa.vm.globalLock";
   private static Hashtable table = createTable();

   private SharedObjects() {
      throw new IllegalStateException();
   }

   public static void put(String var0, Object var1) {
      put(var0, var1, false);
   }

   public static void put(String var0, Object var1, boolean var2) {
      if (!var2) {
         var1 = new SharedWeakReference(var1);
      }

      table.put(var0, var1);
   }

   public static Object get(String var0) {
      Object var1 = table.get(var0);
      if (var1 != null && var1 instanceof SharedWeakReference) {
         var1 = ((Reference)var1).get();
      }

      return var1;
   }

   public static Object get(String var0, Object var1) {
      synchronized(table) {
         Object var2 = table.get(var0);
         if (var2 != null && var2 instanceof SharedWeakReference) {
            var2 = ((Reference)var2).get();
         }

         if (var2 == null) {
            var2 = var1;
            table.put(var0, new SharedWeakReference(var1));
         }

         return var2;
      }
   }

   public static Object get(String var0, String var1) {
      synchronized(table) {
         Object var2 = table.get(var0);
         if (var2 != null && var2 instanceof SharedWeakReference) {
            var2 = ((Reference)var2).get();
         }

         if (var2 == null) {
            try {
               Class var4 = Class.forName(var1);
               var2 = var4.newInstance();
               table.put(var0, new SharedWeakReference(var2));
            } catch (ClassNotFoundException var6) {
               throw new IllegalArgumentException();
            } catch (IllegalAccessException var7) {
               throw new IllegalArgumentException();
            } catch (InstantiationException var8) {
               throw new IllegalArgumentException();
            }
         }

         return var2;
      }
   }

   public static Object getLock(String var0) {
      synchronized(table) {
         Object var1 = table.get(var0);
         if (var1 != null && var1 instanceof SharedWeakReference) {
            var1 = ((Reference)var1).get();
         }

         if (var1 == null) {
            var1 = new Object();
            table.put(var0, new SharedWeakReference(var1));
         }

         return var1;
      }
   }

   public static Object remove(String var0) {
      Object var1 = table.remove(var0);
      if (var1 instanceof SharedWeakReference) {
         var1 = ((Reference)var1).get();
      }

      return var1;
   }

   private static Hashtable createTable() {
      Hashtable var0 = new Hashtable();
      var0.put("com.nokia.mid.impl.isa.vm.globalLock", new Object());
      var0 = nativeSetTable(var0);
      return var0;
   }

   private static native Hashtable nativeSetTable(Hashtable var0);
}
