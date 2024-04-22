package com.nokia.mid.impl.isa.pim;

import java.util.Enumeration;
import javax.microedition.pim.Event;
import javax.microedition.pim.EventList;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.UnsupportedFieldException;

class EventListImp extends PIMListImp implements EventList {
   private static final int[][] dD = getSupportedFieldsArray();
   static final int[] cw = getListNamesArray();
   private static final int[][] dE = getSupportedFieldLabelsArray();
   private int type;

   EventListImp(int var1, String var2) throws PIMException {
      super(var1);

      for(var1 = 0; var1 <= 4; ++var1) {
         if (PIMTextDatabase.getText(cw[var1]).equals(var2)) {
            this.type = var1;
            return;
         }
      }

      throw new PIMException("Invalid name", 1);
   }

   public String getName() {
      return PIMTextDatabase.getText(cw[this.type]);
   }

   public boolean isSupportedField(int var1) {
      for(int var2 = 0; var2 < dD[this.type].length; ++var2) {
         if (dD[this.type][var2] == var1) {
            return true;
         }
      }

      return false;
   }

   public int[] getSupportedFields() {
      int[] var1 = new int[dD[this.type].length];
      System.arraycopy(dD[this.type], 0, var1, 0, dD[this.type].length);
      return var1;
   }

   public int getFieldDataType(int var1) {
      this.a(var1);
      switch(var1) {
      case 100:
         return 3;
      case 101:
      case 105:
      default:
         throw new IllegalArgumentException();
      case 102:
      case 106:
         return 2;
      case 103:
      case 104:
      case 107:
      case 108:
         return 4;
      }
   }

   public String getFieldLabel(int var1) {
      if (EventImp.e(var1)) {
         if (var1 == 108) {
            return "";
         } else {
            for(int var2 = 1; var2 < dD[this.type].length; ++var2) {
               if (dD[this.type][var2] == var1) {
                  return PIMTextDatabase.getText(dE[this.type][var2 - 1]);
               }
            }

            throw new UnsupportedFieldException(String.valueOf(var1));
         }
      } else {
         throw new IllegalArgumentException("Invalid field:" + String.valueOf(var1));
      }
   }

   public Event createEvent() {
      return new EventImp(this);
   }

   public Event importEvent(Event var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         EventImp var2;
         (var2 = new EventImp(this)).c(var1);
         return var2;
      }
   }

   public void removeEvent(Event var1) throws PIMException {
      this.removeItem(var1);
      CalendarSearchManager.getInstance().removeItem(var1);
      ((EventImp)var1).removeFromList();
   }

   public Enumeration items(int var1, long var2, long var4, boolean var6) throws PIMException {
      if (!this.open) {
         throw new PIMException("List closed!", 2);
      } else if ((var1 == 0 || var1 == 1 || var1 == 2) && var4 > var2) {
         if (this.getMode() != 2 && PIMListImp.a(1, 2, false)) {
            return new SearchResult(this.getSearchManager(), var1, var2, var4, var6, this);
         } else {
            throw new SecurityException("Read permission denied");
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int[] getSupportedRepeatRuleFields(int var1) {
      if (this.type == 2) {
         switch(var1) {
         case 16:
         case 17:
         case 18:
            return new int[0];
         case 19:
            return new int[]{0};
         default:
            throw new IllegalArgumentException("Invalid frequency");
         }
      } else {
         switch(var1) {
         case 16:
            return new int[]{64};
         case 17:
            return new int[]{128, 2, 64};
         case 18:
            return new int[]{1, 64};
         case 19:
            return new int[]{1, 8, 64};
         default:
            throw new IllegalArgumentException("Invalid frequency");
         }
      }
   }

   SearchManager getSearchManager() {
      return CalendarSearchManager.getInstance();
   }

   final void a(int var1) {
      if (!EventImp.e(var1)) {
         throw new IllegalArgumentException("Invalid field: " + String.valueOf(var1));
      } else if (!this.isSupportedField(var1)) {
         throw new UnsupportedFieldException(String.valueOf(var1));
      }
   }

   protected final boolean e() {
      return PIMListImp.a(1, 2, false);
   }

   protected final boolean a(boolean var1) {
      return PIMListImp.a(2, 2, var1);
   }

   private static native int[][] getSupportedFieldsArray();

   private static native int[] getListNamesArray();

   private static native int[][] getSupportedFieldLabelsArray();
}
