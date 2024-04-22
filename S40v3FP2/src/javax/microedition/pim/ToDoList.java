package javax.microedition.pim;

import java.util.Enumeration;

public interface ToDoList extends PIMList {
   ToDo createToDo();

   ToDo importToDo(ToDo var1);

   void removeToDo(ToDo var1) throws PIMException;

   Enumeration items(int var1, long var2, long var4) throws PIMException;
}
