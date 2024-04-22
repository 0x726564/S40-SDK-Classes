package com.nokia.mid.impl.isa.util;

import java.lang.ref.Reference;
import java.util.Hashtable;

public final class SharedObjects {
   public static final String GLOBAL_LOCK_NAME = "com.nokia.mid.impl.isa.vm.globalLock";
   private static Hashtable table = createTable();

   private SharedObjects() {
      throw new IllegalStateException();
   }

   public static void put(String key, Object value) {
      put(key, value, false);
   }

   public static void put(String key, Object value, boolean useStrongReference) {
      if (!useStrongReference) {
         value = new SharedWeakReference(value);
      }

      table.put(key, value);
   }

   public static Object get(String key) {
      Object obj = table.get(key);
      if (obj != null && obj instanceof SharedWeakReference) {
         obj = ((Reference)obj).get();
      }

      return obj;
   }

   public static Object get(String key, Object defaultValue) {
      synchronized(table) {
         Object obj = table.get(key);
         if (obj != null && obj instanceof SharedWeakReference) {
            obj = ((Reference)obj).get();
         }

         if (obj == null) {
            obj = defaultValue;
            table.put(key, new SharedWeakReference(defaultValue));
         }

         return obj;
      }
   }

   public static Object get(String key, String defaultClassName) {
      synchronized(table) {
         Object obj = table.get(key);
         if (obj != null && obj instanceof SharedWeakReference) {
            obj = ((Reference)obj).get();
         }

         if (obj == null) {
            try {
               Class clazz = Class.forName(defaultClassName);
               obj = clazz.newInstance();
               table.put(key, new SharedWeakReference(obj));
            } catch (ClassNotFoundException var6) {
               throw new IllegalArgumentException();
            } catch (IllegalAccessException var7) {
               throw new IllegalArgumentException();
            } catch (InstantiationException var8) {
               throw new IllegalArgumentException();
            }
         }

         return obj;
      }
   }

   public static Object getLock(String key) {
      synchronized(table) {
         Object lock = table.get(key);
         if (lock != null && lock instanceof SharedWeakReference) {
            lock = ((Reference)lock).get();
         }

         if (lock == null) {
            lock = new Object();
            table.put(key, new SharedWeakReference(lock));
         }

         return lock;
      }
   }

   public static Object remove(String key) {
      Object obj = table.remove(key);
      if (obj instanceof SharedWeakReference) {
         obj = ((Reference)obj).get();
      }

      return obj;
   }

   private static Hashtable createTable() {
      Hashtable t = new Hashtable();
      t.put("com.nokia.mid.impl.isa.vm.globalLock", new Object());
      t = nativeSetTable(t);
      return t;
   }

   private static native Hashtable nativeSetTable(Hashtable var0);
}
