package java.util;

public class Hashtable {
   private transient HashtableEntry[] table;
   private transient int count;
   private int threshold;
   private static final int loadFactorPercent = 75;

   public Hashtable(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         if (var1 == 0) {
            var1 = 1;
         }

         this.table = new HashtableEntry[var1];
         this.threshold = var1 * 75 / 100;
      }
   }

   public Hashtable() {
      this(11);
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public synchronized Enumeration keys() {
      return new Hashtable.HashtableEnumerator(this.table, true);
   }

   public synchronized Enumeration elements() {
      return new Hashtable.HashtableEnumerator(this.table, false);
   }

   public synchronized boolean contains(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         HashtableEntry[] var2 = this.table;
         int var3 = var2.length;

         while(var3-- > 0) {
            for(HashtableEntry var4 = var2[var3]; var4 != null; var4 = var4.next) {
               if (var4.value.equals(var1)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public synchronized boolean containsKey(Object var1) {
      HashtableEntry[] var2 = this.table;
      int var3 = var1.hashCode();
      int var4 = (var3 & Integer.MAX_VALUE) % var2.length;

      for(HashtableEntry var5 = var2[var4]; var5 != null; var5 = var5.next) {
         if (var5.hash == var3 && var5.key.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public synchronized Object get(Object var1) {
      HashtableEntry[] var2 = this.table;
      int var3 = var1.hashCode();
      int var4 = (var3 & Integer.MAX_VALUE) % var2.length;

      for(HashtableEntry var5 = var2[var4]; var5 != null; var5 = var5.next) {
         if (var5.hash == var3 && var5.key.equals(var1)) {
            return var5.value;
         }
      }

      return null;
   }

   protected void rehash() {
      int var1 = this.table.length;
      HashtableEntry[] var2 = this.table;
      int var3 = var1 * 2 + 1;
      HashtableEntry[] var4 = new HashtableEntry[var3];
      this.threshold = var3 * 75 / 100;
      this.table = var4;
      int var5 = var1;

      HashtableEntry var7;
      int var8;
      while(var5-- > 0) {
         for(HashtableEntry var6 = var2[var5]; var6 != null; var4[var8] = var7) {
            var7 = var6;
            var6 = var6.next;
            var8 = (var7.hash & Integer.MAX_VALUE) % var3;
            var7.next = var4[var8];
         }
      }

   }

   public synchronized Object put(Object var1, Object var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         HashtableEntry[] var3 = this.table;
         int var4 = var1.hashCode();
         int var5 = (var4 & Integer.MAX_VALUE) % var3.length;

         HashtableEntry var6;
         for(var6 = var3[var5]; var6 != null; var6 = var6.next) {
            if (var6.hash == var4 && var6.key.equals(var1)) {
               Object var7 = var6.value;
               var6.value = var2;
               return var7;
            }
         }

         if (this.count >= this.threshold) {
            this.rehash();
            return this.put(var1, var2);
         } else {
            var6 = new HashtableEntry();
            var6.hash = var4;
            var6.key = var1;
            var6.value = var2;
            var6.next = var3[var5];
            var3[var5] = var6;
            ++this.count;
            return null;
         }
      }
   }

   public synchronized Object remove(Object var1) {
      HashtableEntry[] var2 = this.table;
      int var3 = var1.hashCode();
      int var4 = (var3 & Integer.MAX_VALUE) % var2.length;
      HashtableEntry var5 = var2[var4];

      for(HashtableEntry var6 = null; var5 != null; var5 = var5.next) {
         if (var5.hash == var3 && var5.key.equals(var1)) {
            if (var6 != null) {
               var6.next = var5.next;
            } else {
               var2[var4] = var5.next;
            }

            --this.count;
            return var5.value;
         }

         var6 = var5;
      }

      return null;
   }

   public synchronized void clear() {
      HashtableEntry[] var1 = this.table;
      int var2 = var1.length;

      while(true) {
         --var2;
         if (var2 < 0) {
            this.count = 0;
            return;
         }

         var1[var2] = null;
      }
   }

   public synchronized String toString() {
      int var1 = this.size() - 1;
      StringBuffer var2 = new StringBuffer();
      Enumeration var3 = this.keys();
      Enumeration var4 = this.elements();
      var2.append("{");

      for(int var5 = 0; var5 <= var1; ++var5) {
         String var6 = var3.nextElement().toString();
         String var7 = var4.nextElement().toString();
         var2.append(var6 + "=" + var7);
         if (var5 < var1) {
            var2.append(", ");
         }
      }

      var2.append("}");
      return var2.toString();
   }

   class HashtableEnumerator implements Enumeration {
      boolean keys;
      int index;
      HashtableEntry[] table;
      HashtableEntry entry;

      HashtableEnumerator(HashtableEntry[] var2, boolean var3) {
         this.table = var2;
         this.keys = var3;
         this.index = var2.length;
      }

      public boolean hasMoreElements() {
         if (this.entry != null) {
            return true;
         } else {
            do {
               if (this.index-- <= 0) {
                  return false;
               }
            } while((this.entry = this.table[this.index]) == null);

            return true;
         }
      }

      public Object nextElement() {
         if (this.entry == null) {
            while(this.index-- > 0 && (this.entry = this.table[this.index]) == null) {
            }
         }

         if (this.entry != null) {
            HashtableEntry var1 = this.entry;
            this.entry = var1.next;
            return this.keys ? var1.key : var1.value;
         } else {
            throw new NoSuchElementException("HashtableEnumerator");
         }
      }
   }
}
