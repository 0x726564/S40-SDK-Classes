package java.util;

public class Vector {
   protected Object[] elementData;
   protected int elementCount;
   protected int capacityIncrement;

   public Vector(int var1, int var2) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Illegal Capacity: " + var1);
      } else {
         this.elementData = new Object[var1];
         this.capacityIncrement = var2;
      }
   }

   public Vector(int var1) {
      this(var1, 0);
   }

   public Vector() {
      this(10);
   }

   public synchronized void copyInto(Object[] var1) {
      for(int var2 = this.elementCount; var2-- > 0; var1[var2] = this.elementData[var2]) {
      }

   }

   public synchronized void trimToSize() {
      int var1 = this.elementData.length;
      if (this.elementCount < var1) {
         Object[] var2 = this.elementData;
         this.elementData = new Object[this.elementCount];
         System.arraycopy(var2, 0, this.elementData, 0, this.elementCount);
      }

   }

   public synchronized void ensureCapacity(int var1) {
      if (var1 > this.elementData.length) {
         this.j(var1);
      }

   }

   private void j(int var1) {
      int var2 = this.elementData.length;
      Object[] var3 = this.elementData;
      if ((var2 = this.capacityIncrement > 0 ? var2 + this.capacityIncrement : var2 << 1) < var1) {
         var2 = var1;
      }

      this.elementData = new Object[var2];
      System.arraycopy(var3, 0, this.elementData, 0, this.elementCount);
   }

   public synchronized void setSize(int var1) {
      if (var1 > this.elementCount && var1 > this.elementData.length) {
         this.j(var1);
      } else {
         for(int var2 = var1; var2 < this.elementCount; ++var2) {
            this.elementData[var2] = null;
         }
      }

      this.elementCount = var1;
   }

   public int capacity() {
      return this.elementData.length;
   }

   public int size() {
      return this.elementCount;
   }

   public boolean isEmpty() {
      return this.elementCount == 0;
   }

   public synchronized Enumeration elements() {
      return new VectorEnumerator(this);
   }

   public boolean contains(Object var1) {
      return this.indexOf(var1, 0) >= 0;
   }

   public int indexOf(Object var1) {
      return this.indexOf(var1, 0);
   }

   public synchronized int indexOf(Object var1, int var2) {
      int var3;
      if (var1 == null) {
         for(var3 = var2; var3 < this.elementCount; ++var3) {
            if (this.elementData[var3] == null) {
               return var3;
            }
         }
      } else {
         for(var3 = var2; var3 < this.elementCount; ++var3) {
            if (var1.equals(this.elementData[var3])) {
               return var3;
            }
         }
      }

      return -1;
   }

   public int lastIndexOf(Object var1) {
      return this.lastIndexOf(var1, this.elementCount - 1);
   }

   public synchronized int lastIndexOf(Object var1, int var2) {
      if (var2 >= this.elementCount) {
         throw new IndexOutOfBoundsException(var2 + " >= " + this.elementCount);
      } else {
         int var3;
         if (var1 == null) {
            for(var3 = var2; var3 >= 0; --var3) {
               if (this.elementData[var3] == null) {
                  return var3;
               }
            }
         } else {
            for(var3 = var2; var3 >= 0; --var3) {
               if (var1.equals(this.elementData[var3])) {
                  return var3;
               }
            }
         }

         return -1;
      }
   }

   public synchronized Object elementAt(int var1) {
      if (var1 >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(var1 + " >= " + this.elementCount);
      } else {
         try {
            return this.elementData[var1];
         } catch (ArrayIndexOutOfBoundsException var2) {
            throw new ArrayIndexOutOfBoundsException(var1 + " < 0");
         }
      }
   }

   public synchronized Object firstElement() {
      if (this.elementCount == 0) {
         throw new NoSuchElementException();
      } else {
         return this.elementData[0];
      }
   }

   public synchronized Object lastElement() {
      if (this.elementCount == 0) {
         throw new NoSuchElementException();
      } else {
         return this.elementData[this.elementCount - 1];
      }
   }

   public synchronized void setElementAt(Object var1, int var2) {
      if (var2 >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(var2 + " >= " + this.elementCount);
      } else {
         this.elementData[var2] = var1;
      }
   }

   public synchronized void removeElementAt(int var1) {
      if (var1 >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(var1 + " >= " + this.elementCount);
      } else if (var1 < 0) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else {
         int var2;
         if ((var2 = this.elementCount - var1 - 1) > 0) {
            System.arraycopy(this.elementData, var1 + 1, this.elementData, var1, var2);
         }

         --this.elementCount;
         this.elementData[this.elementCount] = null;
      }
   }

   public synchronized void insertElementAt(Object var1, int var2) {
      int var3 = this.elementCount + 1;
      if (var2 >= var3) {
         throw new ArrayIndexOutOfBoundsException(var2 + " > " + this.elementCount);
      } else {
         if (var3 > this.elementData.length) {
            this.j(var3);
         }

         System.arraycopy(this.elementData, var2, this.elementData, var2 + 1, this.elementCount - var2);
         this.elementData[var2] = var1;
         ++this.elementCount;
      }
   }

   public synchronized void addElement(Object var1) {
      int var2;
      if ((var2 = this.elementCount + 1) > this.elementData.length) {
         this.j(var2);
      }

      this.elementData[this.elementCount++] = var1;
   }

   public synchronized boolean removeElement(Object var1) {
      int var2;
      if ((var2 = this.indexOf(var1)) >= 0) {
         this.removeElementAt(var2);
         return true;
      } else {
         return false;
      }
   }

   public synchronized void removeAllElements() {
      for(int var1 = 0; var1 < this.elementCount; ++var1) {
         this.elementData[var1] = null;
      }

      this.elementCount = 0;
   }

   public final synchronized String toString() {
      int var1 = this.size() - 1;
      StringBuffer var2 = new StringBuffer();
      Enumeration var4 = this.elements();
      var2.append("[");

      for(int var3 = 0; var3 <= var1; ++var3) {
         var2.append(var4.nextElement());
         if (var3 < var1) {
            var2.append(", ");
         }
      }

      var2.append("]");
      return var2.toString();
   }
}
