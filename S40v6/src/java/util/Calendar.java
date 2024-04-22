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

   public final void setTime(Date date) {
      this.setTimeInMillis(date.getTime());
   }

   public static synchronized Calendar getInstance() {
      try {
         Class clazz = Class.forName("com.sun.cldc.util.j2me.CalendarImpl");
         return (Calendar)clazz.newInstance();
      } catch (Exception var1) {
         return null;
      }
   }

   public static synchronized Calendar getInstance(TimeZone zone) {
      Calendar cal = getInstance();
      cal.setTimeZone(zone);
      return cal;
   }

   protected long getTimeInMillis() {
      if (!this.isTimeSet) {
         this.computeTime();
         this.isTimeSet = true;
      }

      return this.time;
   }

   protected void setTimeInMillis(long millis) {
      this.isTimeSet = true;
      this.fields[7] = 0;
      this.time = millis;
      this.computeFields();
   }

   public final int get(int field) {
      if (field == 7 || field == 11 || field == 9 || field == 10) {
         this.getTimeInMillis();
         this.computeFields();
      }

      return this.fields[field];
   }

   public final void set(int field, int value) {
      this.isTimeSet = false;
      this.isSet[field] = true;
      this.fields[field] = value;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof Calendar)) {
         return false;
      } else {
         Calendar that = (Calendar)obj;
         return this.getTimeInMillis() == that.getTimeInMillis() && this.zone.equals(that.zone);
      }
   }

   public boolean before(Object when) {
      return when instanceof Calendar && this.getTimeInMillis() < ((Calendar)when).getTimeInMillis();
   }

   public boolean after(Object when) {
      return when instanceof Calendar && this.getTimeInMillis() > ((Calendar)when).getTimeInMillis();
   }

   public void setTimeZone(TimeZone value) {
      this.zone = value;
      this.getTimeInMillis();
      this.computeFields();
   }

   public TimeZone getTimeZone() {
      return this.zone;
   }

   protected abstract void computeFields();

   protected abstract void computeTime();
}
