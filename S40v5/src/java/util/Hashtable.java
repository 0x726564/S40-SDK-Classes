package java.util;

public class Hashtable {
   private transient HashtableEntry[] bO;
   private transient int count;
   private int threshold;

   public Hashtable(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         if (var1 == 0) {
            var1 = 1;
         }

         this.bO = new HashtableEntry[var1];
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
      return new Hashtable.HashtableEnumerator(this, this.bO, true);
   }

   public synchronized Enumeration elements() {
      return new Hashtable.HashtableEnumerator(this, this.bO, false);
   }

   public synchronized boolean contains(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         HashtableEntry[] var4;
         int var2 = (var4 = this.bO).length;

         while(var2-- > 0) {
            for(HashtableEntry var3 = var4[var2]; var3 != null; var3 = var3.fn) {
               if (var3.value.equals(var1)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public synchronized boolean containsKey(Object var1) {
      HashtableEntry[] var4 = this.bO;
      int var2;
      int var3 = ((var2 = var1.hashCode()) & Integer.MAX_VALUE) % var4.length;

      for(HashtableEntry var5 = var4[var3]; var5 != null; var5 = var5.fn) {
         if (var5.hash == var2 && var5.key.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public synchronized Object get(Object var1) {
      HashtableEntry[] var4 = this.bO;
      int var2;
      int var3 = ((var2 = var1.hashCode()) & Integer.MAX_VALUE) % var4.length;

      for(HashtableEntry var5 = var4[var3]; var5 != null; var5 = var5.fn) {
         if (var5.hash == var2 && var5.key.equals(var1)) {
            return var5.value;
         }
      }

      return null;
   }

   protected void rehash() {
      int var1 = this.bO.length;
      HashtableEntry[] var2 = this.bO;
      int var3;
      HashtableEntry[] var4 = new HashtableEntry[var3 = (var1 << 1) + 1];
      this.threshold = var3 * 75 / 100;
      this.bO = var4;
      int var7 = var1;

      HashtableEntry var5;
      int var6;
      while(var7-- > 0) {
         for(HashtableEntry var8 = var2[var7]; var8 != null; var4[var6] = var5) {
            var5 = var8;
            var8 = var8.fn;
            var6 = (var5.hash & Integer.MAX_VALUE) % var3;
            var5.fn = var4[var6];
         }
      }

   }

   public synchronized Object put(Object var1, Object var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         HashtableEntry[] var3 = this.bO;
         int var4;
         int var5 = ((var4 = var1.hashCode()) & Integer.MAX_VALUE) % var3.length;

         HashtableEntry var6;
         for(var6 = var3[var5]; var6 != null; var6 = var6.fn) {
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
            (var6 = new HashtableEntry()).hash = var4;
            var6.key = var1;
            var6.value = var2;
            var6.fn = var3[var5];
            var3[var5] = var6;
            ++this.count;
            return null;
         }
      }
   }

   public synchronized Object remove(Object var1) {
      HashtableEntry[] var2 = this.bO;
      int var3;
      int var4 = ((var3 = var1.hashCode()) & Integer.MAX_VALUE) % var2.length;
      HashtableEntry var5 = var2[var4];

      for(HashtableEntry var6 = null; var5 != null; var5 = var5.fn) {
         if (var5.hash == var3 && var5.key.equals(var1)) {
            if (var6 != null) {
               var6.fn = var5.fn;
            } else {
               var2[var4] = var5.fn;
            }

            --this.count;
            return var5.value;
         }

         var6 = var5;
      }

      return null;
   }

   public synchronized void clear() {
      HashtableEntry[] var1;
      int var2 = (var1 = this.bO).length;

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
      Enumeration var7 = this.elements();
      var2.append("{");

      for(int var4 = 0; var4 <= var1; ++var4) {
         String var5 = var3.nextElement().toString();
         String var6 = var7.nextElement().toString();
         var2.append(var5 + "=" + var6);
         if (var4 < var1) {
            var2.append(", ");
         }
      }

      var2.append("}");
      return var2.toString();
   }

   class HashtableEnumerator implements Enumeration {
      private boolean keys;
      private int index;
      private HashtableEntry[] bO;
      private HashtableEntry bP;

      HashtableEnumerator(Hashtable var1, HashtableEntry[] var2, boolean var3) {
         this.bO = var2;
         this.keys = var3;
         this.index = var2.length;
      }

      public boolean hasMoreElements() {
         if (this.bP != null) {
            return true;
         } else {
            do {
               if (this.index-- <= 0) {
                  return false;
               }
            } while((this.bP = this.bO[this.index]) == null);

            return true;
         }
      }

      public Object nextElement() {
         if (this.bP == null) {
            while(this.index-- > 0 && (this.bP = this.bO[this.index]) == null) {
            }
         }

         if (this.bP != null) {
            HashtableEntry var1 = this.bP;
            this.bP = var1.fn;
            return this.keys ? var1.key : var1.value;
         } else {
            throw new NoSuchElementException("HashtableEnumerator");
         }
      }
   }
}
