package java.util;

import com.sun.cldc.util.j2me.CalendarImpl;

public class Date {
   private Calendar calendar;
   private long fastTime;

   public Date() {
      this(System.currentTimeMillis());
   }

   public Date(long var1) {
      this.calendar = Calendar.getInstance();
      if (this.calendar != null) {
         this.calendar.setTimeInMillis(var1);
      }

      this.fastTime = var1;
   }

   public long getTime() {
      return this.calendar != null ? this.calendar.getTimeInMillis() : this.fastTime;
   }

   public void setTime(long var1) {
      if (this.calendar != null) {
         this.calendar.setTimeInMillis(var1);
      }

      this.fastTime = var1;
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof Date && this.getTime() == ((Date)var1).getTime();
   }

   public int hashCode() {
      long var1 = this.getTime();
      return (int)var1 ^ (int)(var1 >> 32);
   }

   public String toString() {
      return CalendarImpl.toString(this.calendar);
   }
}
