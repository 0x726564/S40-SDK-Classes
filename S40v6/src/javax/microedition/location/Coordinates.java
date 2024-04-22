package javax.microedition.location;

public class Coordinates {
   public static final int DD_MM_SS = 1;
   public static final int DD_MM = 2;
   double latitude = Double.NaN;
   double longitude = Double.NaN;
   float altitude = Float.NaN;

   public Coordinates(double latitude, double longitude, float altitude) {
      this.setLatitude(latitude);
      this.setLongitude(longitude);
      this.setAltitude(altitude);
   }

   public double getLatitude() {
      return this.latitude;
   }

   public double getLongitude() {
      return this.longitude;
   }

   public float getAltitude() {
      return this.altitude;
   }

   public void setAltitude(float altitude) {
      this.altitude = altitude;
   }

   public void setLatitude(double latitude) {
      if (Double.isNaN(latitude)) {
         throw new IllegalArgumentException();
      } else if (latitude >= -90.0D && latitude <= 90.0D) {
         this.latitude = latitude;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setLongitude(double longitude) {
      if (Double.isNaN(longitude)) {
         throw new IllegalArgumentException();
      } else if (longitude >= -180.0D && longitude < 180.0D) {
         this.longitude = longitude;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static double ConvertCoordinate(String coordinate) {
      char firstChar = coordinate.charAt(0);
      int ddix = coordinate.indexOf(58);
      int dd = parseDegrees(coordinate, ddix, firstChar);
      byte sign;
      if (firstChar == '-') {
         dd = -dd;
         sign = -1;
      } else {
         sign = 1;
      }

      if (coordinate.length() < ddix + 2) {
         throw new IllegalArgumentException();
      } else {
         String mmstr = coordinate.substring(ddix + 1);
         int mm = parseMinutes(mmstr);
         int mmlen = mmstr.length();
         double var1;
         if (mmlen > 2) {
            if (mmlen < 4) {
               throw new IllegalArgumentException();
            }

            char delimeterChar = mmstr.charAt(2);
            double ss;
            if (delimeterChar == ':') {
               if (mmlen < 5) {
                  throw new IllegalArgumentException();
               }

               ss = parseSeconds(mmstr.substring(3));
               var1 = getValueSS(sign, dd, mm, ss);
            } else {
               if (delimeterChar != '.') {
                  throw new IllegalArgumentException();
               }

               ss = parseFraction(mmstr.substring(3), 5);
               var1 = getValueMM(sign, dd, mm, ss);
            }
         } else {
            var1 = getValueMM(sign, dd, mm, 0.0D);
         }

         return var1;
      }
   }

   private static double getValueMM(int sign, int dd, int mm, double ff) {
      return (double)sign * ((double)dd + ((double)mm + ff) / 60.0D);
   }

   private static double getValueSS(int sign, int dd, int mm, double ss) {
      return (double)sign * ((double)dd + (double)mm / 60.0D + ss);
   }

   private static int parseDegrees(String coordinate, int ddix, char firstChar) {
      if (ddix < 1) {
         throw new IllegalArgumentException();
      } else if (ddix > 1 && firstChar == '0' || ddix > 2 && firstChar == '-' && coordinate.charAt(1) == '0') {
         throw new IllegalArgumentException();
      } else {
         int dd = Integer.parseInt(coordinate.substring(0, ddix));
         if (dd < 180 && dd >= -180) {
            return dd;
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   private static int parse2Digits(String digits) {
      char dChar1 = digits.charAt(0);
      if (dChar1 >= '0' && dChar1 <= '5') {
         char dChar2 = digits.charAt(1);
         if (dChar2 >= '0' && dChar2 <= '9') {
            return Character.digit(dChar1, 10) * 10 + Character.digit(dChar2, 10);
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static int parseMinutes(String mmstr) {
      try {
         int mm = parse2Digits(mmstr);
         return mm;
      } catch (IllegalArgumentException var3) {
         throw new IllegalArgumentException();
      }
   }

   private static double parseSeconds(String ssstr) {
      double ff = 0.0D;

      int ss;
      try {
         ss = parse2Digits(ssstr);
      } catch (IllegalArgumentException var5) {
         throw new IllegalArgumentException();
      }

      if (ssstr.length() > 2) {
         if (ssstr.charAt(2) != '.') {
            throw new IllegalArgumentException();
         }

         ff = parseFraction(ssstr.substring(3), 3);
      }

      return ((double)ss + ff) / 3600.0D;
   }

   private static double parseFraction(String ffstr, int maxlen) {
      if (ffstr.length() <= maxlen && ffstr.length() >= 1) {
         String s = "0." + ffstr;
         return Double.parseDouble(s);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static double convert(String coordinate) {
      try {
         if (coordinate == null) {
            throw new NullPointerException();
         } else {
            coordinate = coordinate.trim();
            if (coordinate.length() < 4) {
               throw new IllegalArgumentException();
            } else {
               double res = ConvertCoordinate(coordinate);
               if (!(res >= 180.0D) && !(res < -180.0D)) {
                  return res;
               } else {
                  throw new IllegalArgumentException();
               }
            }
         }
      } catch (NumberFormatException var3) {
         throw new IllegalArgumentException();
      }
   }

   public boolean equals(Object other) {
      if (other == this) {
         return true;
      } else if (!super.equals(other)) {
         return false;
      } else if (!(other instanceof Coordinates)) {
         return false;
      } else {
         Coordinates o = (Coordinates)other;
         if (Double.doubleToLongBits(this.getLatitude()) != Double.doubleToLongBits(o.getLatitude())) {
            return false;
         } else if (Double.doubleToLongBits(this.getLongitude()) != Double.doubleToLongBits(o.getLongitude())) {
            return false;
         } else {
            return Float.floatToIntBits(this.getAltitude()) == Float.floatToIntBits(o.getAltitude());
         }
      }
   }

   public int hashCode() {
      int result = 17;
      long tmp = Double.doubleToLongBits(this.getLatitude());
      int result = 37 * result + (int)(tmp ^ tmp >> 32);
      tmp = Double.doubleToLongBits(this.getLongitude());
      result = 37 * result + (int)(tmp ^ tmp >> 32);
      result = 37 * result + Float.floatToIntBits(this.getAltitude());
      return result;
   }

   private static int decimalToValue(int noOfDecimals) {
      switch(noOfDecimals) {
      case 3:
         return 1000;
      case 5:
         return 100000;
      default:
         return 1000;
      }
   }

   private static String double2IntDotIntString(double number, int noOfDecimals) {
      StringBuffer res = new StringBuffer(noOfDecimals + 4);
      int scale = decimalToValue(noOfDecimals);
      int intscale = (int)Math.floor((double)scale * number + 0.5D);
      int integerNumber = intscale / scale;
      int decimalNumber = intscale % scale;
      if (integerNumber < 10) {
         res.append('0');
      }

      res.append(integerNumber);
      if (decimalNumber > 0) {
         char[] zeroes = new char[]{'0', '0', '0', '0', '0', '0'};
         String frac = Integer.toString(decimalNumber);
         res.append('.');
         res.append(zeroes, 0, noOfDecimals - frac.length());
         res.append(frac);
      }

      return res.toString();
   }

   public static String convert(double coordinate, int outputType) {
      if (!(coordinate >= 180.0D) && !(coordinate < -180.0D)) {
         if (Double.isNaN(coordinate)) {
            throw new IllegalArgumentException();
         } else {
            int sign;
            int dd;
            if (outputType == 2) {
               sign = coordinate < 0.0D ? -1 : 1;
               coordinate = Math.abs(coordinate);
               dd = (int)coordinate;
               double mm = (coordinate - (double)dd) * 60.0D;
               return (sign < 0 ? "-" : "") + dd + ":" + double2IntDotIntString(mm, 5);
            } else if (outputType == 1) {
               sign = coordinate < 0.0D ? -1 : 1;
               coordinate = Math.abs(coordinate);
               dd = (int)coordinate;
               int mm = (int)((coordinate - (double)dd) * 60.0D);
               double ss = ((coordinate - (double)dd) * 60.0D - (double)mm) * 60.0D;
               double rss = (double)((int)Math.floor(100.0D * ss + 0.5D)) / 100.0D;
               if (rss >= 60.0D) {
                  ++mm;
                  rss -= 60.0D;
               }

               if (mm >= 60) {
                  ++dd;
                  mm -= 60;
               }

               return (sign < 0 ? "-" : "") + dd + ":" + (mm < 10 ? "0" : "") + mm + ":" + double2IntDotIntString(rss, 3);
            } else {
               throw new IllegalArgumentException();
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public float azimuthTo(Coordinates to) {
      if (to == null) {
         throw new NullPointerException();
      } else {
         double otherLatitude = to.getLatitude();
         double otherLongitude = to.getLongitude();
         if (!Double.isNaN(this.latitude) && !Double.isNaN(this.longitude) && !Double.isNaN(otherLatitude) && !Double.isNaN(otherLongitude)) {
            float azimuth;
            if (otherLatitude == this.latitude && otherLongitude == this.longitude) {
               azimuth = 0.0F;
            } else {
               azimuth = this.nativeBearing(this.latitude, this.longitude, otherLatitude, otherLongitude);
            }

            return azimuth;
         } else {
            return Float.NaN;
         }
      }
   }

   public float distance(Coordinates to) {
      if (to == null) {
         throw new NullPointerException();
      } else {
         double otherLatitude = to.getLatitude();
         double otherLongitude = to.getLongitude();
         if (!Double.isNaN(this.latitude) && !Double.isNaN(this.longitude) && !Double.isNaN(otherLatitude) && !Double.isNaN(otherLongitude)) {
            float distance;
            if (otherLatitude == this.latitude && otherLongitude == this.longitude) {
               distance = 0.0F;
            } else {
               distance = this.nativeDistance(this.latitude, this.longitude, otherLatitude, otherLongitude);
            }

            return distance;
         } else {
            return Float.NaN;
         }
      }
   }

   private native float nativeDistance(double var1, double var3, double var5, double var7);

   private native float nativeBearing(double var1, double var3, double var5, double var7);
}
