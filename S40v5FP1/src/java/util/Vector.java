package java.util;

public class Vector {
   protected Object[] elementData;
   protected int elementCount;
   protected int capacityIncrement;

   public Vector(int initialCapacity, int capacityIncrement) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
      } else {
         this.elementData = new Object[initialCapacity];
         this.capacityIncrement = capacityIncrement;
      }
   }

   public Vector(int initialCapacity) {
      this(initialCapacity, 0);
   }

   public Vector() {
      this(10);
   }

   public synchronized void copyInto(Object[] anArray) {
      for(int i = this.elementCount; i-- > 0; anArray[i] = this.elementData[i]) {
      }

   }

   public synchronized void trimToSize() {
      int oldCapacity = this.elementData.length;
      if (this.elementCount < oldCapacity) {
         Object[] oldData = this.elementData;
         this.elementData = new Object[this.elementCount];
         System.arraycopy(oldData, 0, this.elementData, 0, this.elementCount);
      }

   }

   public synchronized void ensureCapacity(int minCapacity) {
      if (minCapacity > this.elementData.length) {
         this.ensureCapacityHelper(minCapacity);
      }

   }

   private void ensureCapacityHelper(int minCapacity) {
      int oldCapacity = this.elementData.length;
      Object[] oldData = this.elementData;
      int newCapacity = this.capacityIncrement > 0 ? oldCapacity + this.capacityIncrement : oldCapacity * 2;
      if (newCapacity < minCapacity) {
         newCapacity = minCapacity;
      }

      this.elementData = new Object[newCapacity];
      System.arraycopy(oldData, 0, this.elementData, 0, this.elementCount);
   }

   public synchronized void setSize(int newSize) {
      if (newSize > this.elementCount && newSize > this.elementData.length) {
         this.ensureCapacityHelper(newSize);
      } else {
         for(int i = newSize; i < this.elementCount; ++i) {
            this.elementData[i] = null;
         }
      }

      this.elementCount = newSize;
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

   public boolean contains(Object elem) {
      return this.indexOf(elem, 0) >= 0;
   }

   public int indexOf(Object elem) {
      return this.indexOf(elem, 0);
   }

   public synchronized int indexOf(Object elem, int index) {
      int i;
      if (elem == null) {
         for(i = index; i < this.elementCount; ++i) {
            if (this.elementData[i] == null) {
               return i;
            }
         }
      } else {
         for(i = index; i < this.elementCount; ++i) {
            if (elem.equals(this.elementData[i])) {
               return i;
            }
         }
      }

      return -1;
   }

   public int lastIndexOf(Object elem) {
      return this.lastIndexOf(elem, this.elementCount - 1);
   }

   public synchronized int lastIndexOf(Object elem, int index) {
      if (index >= this.elementCount) {
         throw new IndexOutOfBoundsException(index + " >= " + this.elementCount);
      } else {
         int i;
         if (elem == null) {
            for(i = index; i >= 0; --i) {
               if (this.elementData[i] == null) {
                  return i;
               }
            }
         } else {
            for(i = index; i >= 0; --i) {
               if (elem.equals(this.elementData[i])) {
                  return i;
               }
            }
         }

         return -1;
      }
   }

   public synchronized Object elementAt(int index) {
      if (index >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + this.elementCount);
      } else {
         try {
            return this.elementData[index];
         } catch (ArrayIndexOutOfBoundsException var3) {
            throw new ArrayIndexOutOfBoundsException(index + " < 0");
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

   public synchronized void setElementAt(Object obj, int index) {
      if (index >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + this.elementCount);
      } else {
         this.elementData[index] = obj;
      }
   }

   public synchronized void removeElementAt(int index) {
      if (index >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + this.elementCount);
      } else if (index < 0) {
         throw new ArrayIndexOutOfBoundsException(index);
      } else {
         int j = this.elementCount - index - 1;
         if (j > 0) {
            System.arraycopy(this.elementData, index + 1, this.elementData, index, j);
         }

         --this.elementCount;
         this.elementData[this.elementCount] = null;
      }
   }

   public synchronized void insertElementAt(Object obj, int index) {
      int newcount = this.elementCount + 1;
      if (index >= newcount) {
         throw new ArrayIndexOutOfBoundsException(index + " > " + this.elementCount);
      } else {
         if (newcount > this.elementData.length) {
            this.ensureCapacityHelper(newcount);
         }

         System.arraycopy(this.elementData, index, this.elementData, index + 1, this.elementCount - index);
         this.elementData[index] = obj;
         ++this.elementCount;
      }
   }

   public synchronized void addElement(Object obj) {
      int newcount = this.elementCount + 1;
      if (newcount > this.elementData.length) {
         this.ensureCapacityHelper(newcount);
      }

      this.elementData[this.elementCount++] = obj;
   }

   public synchronized boolean removeElement(Object obj) {
      int i = this.indexOf(obj);
      if (i >= 0) {
         this.removeElementAt(i);
         return true;
      } else {
         return false;
      }
   }

   public synchronized void removeAllElements() {
      for(int i = 0; i < this.elementCount; ++i) {
         this.elementData[i] = null;
      }

      this.elementCount = 0;
   }

   public synchronized String toString() {
      int max = this.size() - 1;
      StringBuffer buf = new StringBuffer();
      Enumeration e = this.elements();
      buf.append("[");

      for(int i = 0; i <= max; ++i) {
         buf.append(e.nextElement());
         if (i < max) {
            buf.append(", ");
         }
      }

      buf.append("]");
      return buf.toString();
   }
}
