package java.util;

import com.sun.cldc.util.j2me.CalendarImpl;

public class Date {
   private Calendar calendar;
   private long fastTime;

   public Date() {
      this(System.currentTimeMillis());
   }

   public Date(long date) {
      this.calendar = Calendar.getInstance();
      if (this.calendar != null) {
         this.calendar.setTimeInMillis(date);
      }

      this.fastTime = date;
   }

   public long getTime() {
      return this.calendar != null ? this.calendar.getTimeInMillis() : this.fastTime;
   }

   public void setTime(long time) {
      if (this.calendar != null) {
         this.calendar.setTimeInMillis(time);
      }

      this.fastTime = time;
   }

   public boolean equals(Object obj) {
      return obj != null && obj instanceof Date && this.getTime() == ((Date)obj).getTime();
   }

   public int hashCode() {
      long ht = this.getTime();
      return (int)ht ^ (int)(ht >> 32);
   }

   public String toString() {
      return CalendarImpl.toString(this.calendar);
   }
}
