package com.sun.cldc.util.j2me;

import com.nokia.mid.impl.isa.util.SharedObjects;
import java.util.TimeZone;

public class TimeZoneImpl extends TimeZone {
   private String ID;
   private static String[] az = null;
   private int aA;
   private static final byte[] aB = new byte[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
   private static TimeZone[] aC = new TimeZone[]{new TimeZoneImpl(0, "GMT"), new TimeZoneImpl(0, "UTC")};

   public TimeZoneImpl() {
   }

   private TimeZoneImpl(int var1, String var2) {
      this.aA = var1;
      this.ID = var2;
   }

   public int getOffset(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 >= 0 && var3 <= 11) {
         byte var7 = aB[var3];
         if ((var1 == 0 || var1 == 1) && var4 >= 1 && var4 <= var7 && var5 >= 1 && var5 <= 7 && var6 >= 0 && var6 < 86400000) {
            return this.aA;
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IllegalArgumentException("Illegal month " + var3);
      }
   }

   public int getRawOffset() {
      return this.aA;
   }

   public boolean useDaylightTime() {
      return false;
   }

   public String getID() {
      return this.ID;
   }

   public synchronized TimeZone getInstance(String var1) {
      if (var1 == null) {
         int var6;
         synchronized(SharedObjects.getLock("com.sun.cldc.util.j2me.TimeZoneImpl.getInstance")) {
            var6 = getLocalTimeZoneInfo();
         }

         StringBuffer var2;
         (var2 = new StringBuffer("GMT".length() + 6)).append("GMT");
         int var3 = Math.abs(var6);
         if (var6 != 0) {
            if (var6 > 0) {
               var2.append('+');
            } else {
               var2.append('-');
            }

            int var4 = var3 / 60;
            var3 %= 60;
            if (var4 < 10) {
               var2.append('0');
            }

            var2.append(var4);
            var2.append(':');
            if (var3 < 10) {
               var2.append('0');
            }

            var2.append(var3);
         }

         return new TimeZoneImpl(var6 * '\uea60', var2.toString());
      } else {
         return getTimeZone(var1);
      }
   }

   public static synchronized TimeZone getTimeZone(String var0) {
      for(int var1 = 0; var1 < aC.length; ++var1) {
         if (aC[var1].getID().equals(var0)) {
            return aC[var1];
         }
      }

      TimeZone var2;
      if ((var2 = c(var0)) == null) {
         var2 = aC[0];
      }

      return var2;
   }

   public synchronized String[] getIDs() {
      if (az == null) {
         az = new String[aC.length];

         for(int var1 = 0; var1 < aC.length; ++var1) {
            az[var1] = aC[var1].getID();
         }
      }

      return az;
   }

   public final boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         TimeZoneImpl var2 = (TimeZoneImpl)var1;
         if (this.ID == null) {
            if (var2.ID != null) {
               return false;
            }
         } else if (!this.ID.equals(var2.ID)) {
            return false;
         }

         return var2.aA == this.aA;
      }
   }

   public final int hashCode() {
      boolean var1 = false;
      int var2;
      if (this.ID != null) {
         var2 = this.ID.hashCode();
      } else {
         var2 = 0;
      }

      return var2 + this.aA;
   }

   private static final TimeZone c(String var0) {
      try {
         if (var0.length() > 3 && var0.regionMatches(true, 0, "GMT", 0, 3)) {
            boolean var1 = false;
            if (var0.charAt(3) == '-') {
               var1 = true;
            } else if (var0.charAt(3) != '+') {
               return null;
            }

            if (!Character.isDigit(var0.charAt(4))) {
               return null;
            }

            boolean var2 = false;
            boolean var3 = false;
            int var7;
            int var8;
            int var9;
            if ((var8 = var0.indexOf(58)) != -1) {
               var7 = Integer.parseInt(var0.substring(4, var8));
               String var4;
               if ((var4 = var0.substring(var8 + 1)).length() != 2) {
                  return null;
               }

               if ((var8 = Integer.parseInt(var4)) < 0) {
                  return null;
               }
            } else if ((var9 = var0.length()) > 7) {
               var7 = Integer.parseInt(var0.substring(4, 6));
               var8 = Integer.parseInt(var0.substring(6));
            } else if (var9 > 6) {
               var7 = Integer.parseInt(var0.substring(4, 5));
               var8 = Integer.parseInt(var0.substring(5));
            } else {
               var7 = Integer.parseInt(var0.substring(4));
               var8 = 0;
            }

            if (var7 < 24 && var8 < 60) {
               if (var7 == 0 & var8 == 0) {
                  var1 = false;
               }

               var9 = var7 * 60 + var8;
               if (var1) {
                  var9 = -var9;
               }

               StringBuffer var6;
               (var6 = new StringBuffer(10)).append("GMT");
               if (var1) {
                  var6.append("-");
               } else {
                  var6.append("+");
               }

               if (var7 < 10) {
                  var6.append("0");
               }

               var6.append(var7 + ":");
               if (var8 < 10) {
                  var6.append("0");
               }

               var6.append("" + var8);
               return new TimeZoneImpl(var9 * '\uea60', var6.toString());
            }

            return null;
         }
      } catch (NumberFormatException var5) {
      }

      return null;
   }

   private static native int getLocalTimeZoneInfo();
}
