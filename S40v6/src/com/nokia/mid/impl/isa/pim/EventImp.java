package com.nokia.mid.impl.isa.pim;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import javax.microedition.pim.Event;
import javax.microedition.pim.FieldEmptyException;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.RepeatRule;

public class EventImp extends PIMItemImp implements Event {
   private static final int REPEAT_NONE = 0;
   private static final int REPEAT_DAILY = 1;
   private static final int REPEAT_WEEKLY = 2;
   private static final int REPEAT_BI_WEEKLY = 3;
   private static final int REPEAT_MONTHLY = 4;
   private static final int REPEAT_YEARLY = 5;
   private static final long MILLIS_IN_DAY = 86400000L;
   private int repeatFrequency = 0;
   private int dayInWeek = -1;
   private int monthInYear = -1;
   private int dayInMonth = -1;
   private int repeatEnd = -1;
   private boolean repeatModified = false;
   private boolean useRRstart = false;

   EventImp(PIMList pimList) {
      super(pimList);
   }

   public RepeatRule getRepeat() {
      RepeatRule rr = null;
      if (this.repeatFrequency != 0) {
         rr = new RepeatRule();
         switch(this.repeatFrequency) {
         case 2:
            rr.setInt(0, 17);
            break;
         case 3:
            rr.setInt(0, 17);
            rr.setInt(128, 2);
            break;
         case 4:
            rr.setInt(0, 18);
            break;
         case 5:
            rr.setInt(0, 19);
            break;
         default:
            rr.setInt(0, 16);
         }

         if (this.repeatEnd != -1) {
            rr.setDate(64, (long)this.repeatEnd * 1000L);
         }

         if (this.dayInWeek != -1) {
            rr.setInt(2, this.dayInWeek);
         }

         if (this.monthInYear != -1) {
            rr.setInt(8, this.monthInYear);
         }

         if (this.dayInMonth != -1) {
            rr.setInt(1, this.dayInMonth);
         }
      }

      return rr;
   }

   public void setRepeat(RepeatRule value) {
      if (value == null) {
         this.repeatFrequency = 0;
         this.useRRstart = false;
      } else {
         int[] fields = value.getFields();

         for(int i = 0; i < fields.length; ++i) {
            if (fields[i] == 64) {
               this.repeatEnd = (int)(value.getDate(64) / 1000L);
            }

            if (fields[i] == 2 && value.getInt(2) != -1) {
               this.dayInWeek = value.getInt(2);
            }

            if (fields[i] == 8 && value.getInt(8) != -1) {
               this.monthInYear = value.getInt(8);
            }

            if (fields[i] == 1 && value.getInt(1) != -1) {
               this.dayInMonth = value.getInt(1);
            }
         }

         switch(value.getInt(0)) {
         case 16:
            this.repeatFrequency = 1;
            break;
         case 17:
            try {
               if (value.getInt(128) == 2) {
                  this.repeatFrequency = 3;
               } else {
                  this.repeatFrequency = 2;
               }
            } catch (FieldEmptyException var4) {
               this.repeatFrequency = 2;
            }
            break;
         case 18:
            this.repeatFrequency = 4;
            break;
         case 19:
            this.repeatFrequency = 5;
            break;
         default:
            throw new RuntimeException("Wrong frequency in RepeatRule");
         }
      }

      this.repeatModified = true;
      this.useRRstart = this.dayInWeek != -1 || this.dayInMonth != -1 || this.monthInYear != -1;
   }

   public boolean isModified() {
      return super.isModified() || this.repeatModified;
   }

   public void commit() throws PIMException {
      if (this.useRRstart) {
         Date eventStart = null;
         Date eventEnd = null;
         long duration = 0L;
         if (this.list.isSupportedField(106) && this.countValues(106) != 0) {
            eventStart = new Date(this.getDate(106, 0));
         }

         if (this.list.isSupportedField(102) && this.countValues(102) != 0) {
            eventEnd = new Date(this.getDate(102, 0));
         }

         if (eventStart != null && eventEnd != null) {
            duration = eventEnd.getTime() - eventStart.getTime();
         }

         RepeatRule RR = this.createRRForUpdate();
         Calendar cal = Calendar.getInstance();
         Date now = cal.getTime();
         Date searchDate = null;
         if (eventStart == null) {
            searchDate = now;
         } else {
            Date previousDay = new Date(eventStart.getTime() - 86400000L);
            cal.setTime(previousDay);
            searchDate = cal.getTime();
         }

         long searchStart = searchDate.getTime();
         cal.set(1, cal.get(1) + 5);
         Date searchEnd = cal.getTime();
         Enumeration dateEnum = RR.dates(searchStart, searchStart + 1L, searchEnd.getTime());
         Date firstDate = (Date)dateEnum.nextElement();
         if (eventStart != null) {
            cal.setTime(eventStart);
            int hour = cal.get(11);
            int minute = cal.get(12);
            int second = cal.get(13);
            cal.setTime(firstDate);
            cal.set(11, hour);
            cal.set(12, minute);
            cal.set(13, second);
            firstDate = cal.getTime();
         }

         if (this.countValues(106) == 0) {
            this.addDate(106, 0, firstDate.getTime());
         } else {
            this.setDate(106, 0, 0, firstDate.getTime());
         }

         if (duration > 0L) {
            this.setDate(102, 0, 0, firstDate.getTime() + duration);
         }
      }

      super.commit();
      this.repeatModified = false;
   }

   static final boolean isValidField(int field) {
      return 100 <= field && field <= 108 || 200 <= field && field <= 202;
   }

   SearchManager getSearchManager() {
      return CalendarSearchManager.getInstance();
   }

   void copyData(PIMItem item) {
      super.copyData(item);
      this.setRepeat(((Event)item).getRepeat());
   }

   void copyPrivateData(PIMItem item) {
   }

   private RepeatRule createRRForUpdate() {
      RepeatRule retVal = new RepeatRule();
      switch(this.repeatFrequency) {
      case 1:
         retVal.setInt(0, 16);
         break;
      case 3:
         retVal.setInt(128, 2);
      case 2:
         retVal.setInt(0, 17);
         break;
      case 4:
         retVal.setInt(0, 18);
         break;
      case 5:
         retVal.setInt(0, 19);
      }

      if (this.dayInWeek != -1) {
         retVal.setInt(2, this.dayInWeek);
      }

      if (this.dayInMonth != -1) {
         retVal.setInt(1, this.dayInMonth);
      }

      if (this.monthInYear != -1) {
         retVal.setInt(8, this.monthInYear);
      }

      return retVal;
   }

   native byte[] toSerial(String var1, int var2);
}
