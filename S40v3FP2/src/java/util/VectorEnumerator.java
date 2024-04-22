package java.util;

final class VectorEnumerator implements Enumeration {
   Vector vector;
   int count;

   VectorEnumerator(Vector var1) {
      this.vector = var1;
      this.count = 0;
   }

   public boolean hasMoreElements() {
      return this.count < this.vector.elementCount;
   }

   public Object nextElement() {
      synchronized(this.vector) {
         if (this.count < this.vector.elementCount) {
            return this.vector.elementData[this.count++];
         }
      }

      throw new NoSuchElementException("VectorEnumerator");
   }
}
