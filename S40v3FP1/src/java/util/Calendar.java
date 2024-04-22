package java.util;

public abstract class Calendar {
   public static final int YEAR = 1;
   public static final int MONTH = 2;
   public static final int DATE = 5;
   public static final int DAY_OF_MONTH = 5;
   public static final int DAY_OF_WEEK = 7;
   public static final int AM_PM = 9;
   public static final int HOUR = 10;
   public static final int HOUR_OF_DAY = 11;
   public static final int MINUTE = 12;
   public static final int SECOND = 13;
   public static final int MILLISECOND = 14;
   public static final int SUNDAY = 1;
   public static final int MONDAY = 2;
   public static final int TUESDAY = 3;
   public static final int WEDNESDAY = 4;
   public static final int THURSDAY = 5;
   public static final int FRIDAY = 6;
   public static final int SATURDAY = 7;
   public static final int JANUARY = 0;
   public static final int FEBRUARY = 1;
   public static final int MARCH = 2;
   public static final int APRIL = 3;
   public static final int MAY = 4;
   public static final int JUNE = 5;
   public static final int JULY = 6;
   public static final int AUGUST = 7;
   public static final int SEPTEMBER = 8;
   public static final int OCTOBER = 9;
   public static final int NOVEMBER = 10;
   public static final int DECEMBER = 11;
   public static final int AM = 0;
   public static final int PM = 1;
   private static final int FIELDS = 15;
   protected int[] fields = new int[15];
   protected boolean[] isSet = new boolean[15];
   protected long time;
   private boolean isTimeSet;
   private TimeZone zone = TimeZone.getDefault();
   private Date dateObj = null;

   protected Calendar() {
      if (this.zone == null) {
         throw new RuntimeException("Could not find default timezone");
      } else {
         this.setTimeInMillis(System.currentTimeMillis());
      }
   }

   public final Date getTime() {
      if (this.dateObj == null) {
         return this.dateObj = new Date(this.getTimeInMillis());
      } else {
         synchronized(this.dateObj) {
            this.dateObj.setTime(this.getTimeInMillis());
            return this.dateObj;
         }
      }
   }

   public final void setTime(Date var1) {
      this.setTimeInMillis(var1.getTime());
   }

   public static synchronized Calendar getInstance() {
      try {
         Class var0 = Class.forName("com.sun.cldc.util.j2me.CalendarImpl");
         return (Calendar)var0.newInstance();
      } catch (Exception var1) {
         return null;
      }
   }

   public static synchronized Calendar getInstance(TimeZone var0) {
      Calendar var1 = getInstance();
      var1.setTimeZone(var0);
      return var1;
   }

   protected long getTimeInMillis() {
      if (!this.isTimeSet) {
         this.computeTime();
         this.isTimeSet = true;
      }

      return this.time;
   }

   protected void setTimeInMillis(long var1) {
      this.isTimeSet = true;
      this.fields[7] = 0;
      this.time = var1;
      this.computeFields();
   }

   public final int get(int var1) {
      if (var1 == 7 || var1 == 11 || var1 == 9 || var1 == 10) {
         this.getTimeInMillis();
         this.computeFields();
      }

      return this.fields[var1];
   }

   public final void set(int var1, int var2) {
      this.isTimeSet = false;
      this.isSet[var1] = true;
      this.fields[var1] = var2;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Calendar)) {
         return false;
      } else {
         Calendar var2 = (Calendar)var1;
         return this.getTimeInMillis() == var2.getTimeInMillis() && this.zone.equals(var2.zone);
      }
   }

   public boolean before(Object var1) {
      return var1 instanceof Calendar && this.getTimeInMillis() < ((Calendar)var1).getTimeInMillis();
   }

   public boolean after(Object var1) {
      return var1 instanceof Calendar && this.getTimeInMillis() > ((Calendar)var1).getTimeInMillis();
   }

   public void setTimeZone(TimeZone var1) {
      this.zone = var1;
      this.getTimeInMillis();
      this.computeFields();
   }

   public TimeZone getTimeZone() {
      return this.zone;
   }

   protected abstract void computeFields();

   protected abstract void computeTime();
}
