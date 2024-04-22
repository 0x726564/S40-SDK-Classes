package com.sun.cldc.util.j2me;

import com.nokia.mid.impl.isa.util.SharedObjects;
import java.util.TimeZone;
import javax.microedition.io.PushRegistry;

public class TimeZoneImpl extends TimeZone {
   private String ID;
   private static String[] ids = null;
   private int rawOffset;
   private static final int MILLIS_PER_HOUR = 3600000;
   private static final int MILLIS_PER_DAY = 86400000;
   private final byte[] monthLength;
   private static final byte[] staticMonthLength = new byte[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
   private static final int ONE_MINUTE = 60000;
   private static final String GMT_ID = "GMT";
   private static final int GMT_ID_LENGTH = 3;
   static TimeZone[] zones = new TimeZone[]{new TimeZoneImpl(0, "GMT"), new TimeZoneImpl(0, "UTC")};

   public TimeZoneImpl() {
      this.monthLength = staticMonthLength;
   }

   private TimeZoneImpl(int rawOffset, String ID) {
      this.monthLength = staticMonthLength;
      this.rawOffset = rawOffset;
      this.ID = ID;
   }

   public int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis) {
      long dstOffset = 0L;
      if (month >= 0 && month <= 11) {
         int monthLength = staticMonthLength[month];
         if ((era == 0 || era == 1) && day >= 1 && day <= monthLength && dayOfWeek >= 1 && dayOfWeek <= 7 && millis >= 0 && millis < 86400000) {
            dstOffset = PushRegistry.getDaylightSavingOffsetInMillis();
            return this.rawOffset + (int)dstOffset;
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IllegalArgumentException("Illegal month " + month);
      }
   }

   public int getRawOffset() {
      return this.rawOffset;
   }

   public boolean useDaylightTime() {
      return false;
   }

   public String getID() {
      return this.ID;
   }

   public synchronized TimeZone getInstance(String ID) {
      if (ID == null) {
         int offset;
         synchronized(SharedObjects.getLock("com.sun.cldc.util.j2me.TimeZoneImpl.getInstance")) {
            offset = getLocalTimeZoneInfo();
         }

         StringBuffer sb = new StringBuffer("GMT".length() + 6);
         sb.append("GMT");
         int absOffset = Math.abs(offset);
         if (offset != 0) {
            if (offset > 0) {
               sb.append('+');
            } else {
               sb.append('-');
            }

            int hours = absOffset / 60;
            int minutes = absOffset % 60;
            if (hours < 10) {
               sb.append('0');
            }

            sb.append(hours);
            sb.append(':');
            if (minutes < 10) {
               sb.append('0');
            }

            sb.append(minutes);
         }

         return new TimeZoneImpl(offset * '\uea60', sb.toString());
      } else {
         return getTimeZone(ID);
      }
   }

   public static synchronized TimeZone getTimeZone(String ID) {
      for(int i = 0; i < zones.length; ++i) {
         if (zones[i].getID().equals(ID)) {
            return zones[i];
         }
      }

      TimeZone tz = parseCustomTimeZone(ID);
      if (tz == null) {
         tz = zones[0];
      }

      return tz;
   }

   public synchronized String[] getIDs() {
      if (ids == null) {
         ids = new String[zones.length];

         for(int i = 0; i < zones.length; ++i) {
            ids[i] = zones[i].getID();
         }
      }

      return ids;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         TimeZoneImpl other = (TimeZoneImpl)obj;
         if (this.ID == null) {
            if (other.ID != null) {
               return false;
            }
         } else if (!this.ID.equals(other.ID)) {
            return false;
         }

         return other.rawOffset == this.rawOffset;
      }
   }

   public int hashCode() {
      int idHash = false;
      int idHash;
      if (this.ID != null) {
         idHash = this.ID.hashCode();
      } else {
         idHash = 0;
      }

      return idHash + this.rawOffset;
   }

   private static boolean isLatinDigit(char x) {
      return x >= '0' && x <= '9';
   }

   private static final TimeZone parseCustomTimeZone(String id) {
      try {
         if (id.length() > 3 && id.regionMatches(true, 0, "GMT", 0, 3)) {
            boolean negative = false;
            if (id.charAt(3) == '-') {
               negative = true;
            } else if (id.charAt(3) != '+') {
               return null;
            }

            if (!isLatinDigit(id.charAt(4))) {
               return null;
            }

            int hh = false;
            int mm = false;
            int colon_idx = id.indexOf(58);
            int hh;
            int mm;
            int offset;
            if (colon_idx != -1) {
               hh = Integer.parseInt(id.substring(4, colon_idx));
               String min_string = id.substring(colon_idx + 1);
               if (min_string.length() != 2) {
                  return null;
               }

               mm = Integer.parseInt(min_string);
               if (mm < 0) {
                  return null;
               }
            } else {
               offset = id.length();
               if (offset > 7) {
                  hh = Integer.parseInt(id.substring(4, 6));
                  mm = Integer.parseInt(id.substring(6));
               } else if (offset > 6) {
                  hh = Integer.parseInt(id.substring(4, 5));
                  mm = Integer.parseInt(id.substring(5));
               } else {
                  hh = Integer.parseInt(id.substring(4));
                  mm = 0;
               }
            }

            if (hh < 24 && mm < 60) {
               if (hh == 0 & mm == 0) {
                  negative = false;
               }

               offset = hh * 60 + mm;
               if (negative) {
                  offset = -offset;
               }

               StringBuffer sb = new StringBuffer(10);
               sb.append("GMT");
               if (negative) {
                  sb.append("-");
               } else {
                  sb.append("+");
               }

               if (hh < 10) {
                  sb.append("0");
               }

               sb.append(hh + ":");
               if (mm < 10) {
                  sb.append("0");
               }

               sb.append("" + mm);
               return new TimeZoneImpl(offset * '\uea60', sb.toString());
            }

            return null;
         }
      } catch (NumberFormatException var7) {
      }

      return null;
   }

   private static native int getLocalTimeZoneInfo();
}
