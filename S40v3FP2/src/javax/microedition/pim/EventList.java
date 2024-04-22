package javax.microedition.pim;

import java.util.Enumeration;

public interface EventList extends PIMList {
   int ENDING = 1;
   int OCCURRING = 2;
   int STARTING = 0;

   Event createEvent();

   Event importEvent(Event var1);

   void removeEvent(Event var1) throws PIMException;

   Enumeration items(int var1, long var2, long var4, boolean var6) throws PIMException;

   int[] getSupportedRepeatRuleFields(int var1);
}
