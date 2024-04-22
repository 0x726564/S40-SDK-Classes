package java.util;

import com.sun.cldc.util.j2me.CalendarImpl;

public class Date {
   private Calendar calendar;
   private long eg;

   public Date() {
      this(System.currentTimeMillis());
   }

   public Date(long var1) {
      this.calendar = Calendar.getInstance();
      if (this.calendar != null) {
         this.calendar.setTimeInMillis(var1);
      }

      this.eg = var1;
   }

   public long getTime() {
      return this.calendar != null ? this.calendar.getTimeInMillis() : this.eg;
   }

   public void setTime(long var1) {
      if (this.calendar != null) {
         this.calendar.setTimeInMillis(var1);
      }

      this.eg = var1;
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof Date && this.getTime() == ((Date)var1).getTime();
   }

   public final int hashCode() {
      long var1;
      return (int)(var1 = this.getTime()) ^ (int)(var1 >> 32);
   }

   public String toString() {
      return CalendarImpl.toString(this.calendar);
   }
}
