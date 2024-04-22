package java.util;

public class Stack extends Vector {
   public Object push(Object item) {
      this.addElement(item);
      return item;
   }

   public synchronized Object pop() {
      int len = this.size();
      Object obj = this.peek();
      this.removeElementAt(len - 1);
      return obj;
   }

   public synchronized Object peek() {
      int len = this.size();
      if (len == 0) {
         throw new EmptyStackException();
      } else {
         return this.elementAt(len - 1);
      }
   }

   public boolean empty() {
      return this.size() == 0;
   }

   public synchronized int search(Object o) {
      int i = this.lastIndexOf(o);
      return i >= 0 ? this.size() - i : -1;
   }
}
