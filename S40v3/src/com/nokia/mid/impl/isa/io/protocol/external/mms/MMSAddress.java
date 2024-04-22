package com.nokia.mid.impl.isa.io.protocol.external.mms;

public class MMSAddress {
   private static final char COLON = ':';
   public static final boolean SERVER = true;
   public static final boolean CLIENT = false;
   public static final String MMS_PREFIX = "mms://";
   public static final int MAX_APP_ID_LENGTH = 32;

   public static boolean validateHeader(String var0, String var1) {
      boolean var2 = true;
      if (var0 == null) {
         return false;
      } else {
         var0 = var0.toLowerCase();
         if (var1 != null) {
            var1 = var1.toLowerCase();
         }

         if (var0.equals("x-mms-delivery-time")) {
            if (var1 != null) {
               try {
                  long var3 = Long.parseLong(var1);
                  if (var3 < 0L) {
                     var2 = false;
                  }
               } catch (NumberFormatException var5) {
                  var2 = false;
               }
            }
         } else if (var0.toLowerCase().equals("x-mms-priority")) {
            if (var1 != null && !var1.equals("low") && !var1.equals("normal") && !var1.equals("high")) {
               var2 = false;
            }
         } else {
            var2 = false;
         }

         return var2;
      }
   }

   private static boolean validateAppIdSegment(String var0) {
      boolean var1 = true;
      boolean var2 = false;

      for(int var3 = 0; var3 < var0.length(); ++var3) {
         char var4 = var0.charAt(var3);
         if ((var4 <= '/' || var4 >= ':') && (var4 <= '@' || var4 >= '[') && (var4 <= '`' || var4 >= '{') && var4 != '.' && var4 != '_') {
            var1 = false;
            break;
         }
      }

      return var1;
   }

   public static boolean validateAppId(String var0) {
      boolean var1 = true;
      boolean var2 = false;
      boolean var4 = false;
      boolean var6 = false;
      if (var0 != null && var0.length() <= 31) {
         if (!validateAppIdSegment(var0)) {
            var1 = false;
         }

         return var1;
      } else {
         return false;
      }
   }

   private static boolean validateQuotedStr(String var0) {
      boolean var1 = true;

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         if (var3 > 177 || var3 == '"' || var3 < 0 || var3 == '\r') {
            var1 = false;
            break;
         }

         if (var3 == '\\') {
            try {
               char var4 = var0.charAt(var2 + 1);
               if (var4 <= 'A' || var4 >= 'Z' || var4 > 'a' && var4 < 'z') {
                  var1 = false;
                  break;
               }
            } catch (IndexOutOfBoundsException var5) {
               var1 = false;
            }
         }
      }

      return var1;
   }

   private static boolean validateAtom(String var0) {
      boolean var1 = true;

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         if (var3 == '(' || var3 == ')' || var3 == '<' || var3 == '>' || var3 == '@' || var3 == ',' || var3 == ';' || var3 == ':' || var3 == '\\' || var3 == '"' || var3 == '.' || var3 == '[' || var3 == ']' || var3 == ' ') {
            var1 = false;
            break;
         }
      }

      return var1;
   }

   private static boolean validateWord(String var0) {
      boolean var1 = true;
      if (var0.charAt(0) == '"') {
         int var2 = var0.indexOf(34, 1);
         if (var2 == -1 || var2 != var0.length() - 1) {
            return false;
         }

         var1 = validateQuotedStr(var0.substring(1, var2));
      } else {
         var1 = validateAtom(var0);
      }

      return var1;
   }

   private static boolean validateLocalPart(String var0) {
      boolean var1 = true;
      int var2 = indexOfClear(var0, '.');
      int var3 = 0;
      boolean var4 = false;

      while(!var4) {
         if (var2 == -1) {
            var1 = validateWord(var0.substring(var3));
            var4 = true;
         } else {
            String var5 = var0.substring(var3, var2);
            if (!validateWord(var5)) {
               var1 = false;
               break;
            }

            var3 += var5.length() + 1;
            var2 = indexOfClear(var0.substring(var3), '.');
         }
      }

      return var1;
   }

   private static boolean validateSubdomain(String var0) {
      boolean var1 = true;
      if (var0.length() == 0) {
         var1 = false;
      } else if (var0.charAt(0) != '[') {
         var1 = validateAtom(var0);
      } else {
         if (var0.charAt(var0.length() - 1) != ']') {
            return false;
         }

         String var2 = var0.substring(1, var0.length() - 1);

         for(int var3 = 0; var3 < var2.length(); ++var3) {
            char var4 = var2.charAt(var3);
            if (var4 == '[' || var4 == ']' || var4 == '\\' || var4 == '"' || var4 == '\r') {
               return false;
            }
         }
      }

      return var1;
   }

   private static int indexOfClear(String var0, char var1) {
      int var2 = -1;
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      int var7 = 0;
      int var8 = 0;
      int var9 = 0;
      if (var0.length() == 0) {
         return var2;
      } else {
         while(!var3) {
            char var10 = var0.charAt(var9);
            if (var10 == var1 && !var4 && !var5 && !var6) {
               var2 = var9;
               var3 = true;
            } else if (var10 == '[') {
               if (!var4 && !var6) {
                  var5 = true;
                  ++var7;
               }
            } else if (var10 == ']') {
               if (!var4 && !var6 && var5) {
                  --var7;
                  if (var7 == 0) {
                     var5 = false;
                  }
               }
            } else if (var10 == '<') {
               if (!var4 && !var5) {
                  var6 = true;
                  ++var8;
               }
            } else if (var10 == '>') {
               if (!var4 && !var5 && var6) {
                  --var8;
                  if (var8 == 0) {
                     var6 = false;
                  }
               }
            } else if (var10 == '"' && !var5 && !var6) {
               var4 = !var4;
            }

            ++var9;
            if (var9 == var0.length()) {
               var3 = true;
            }
         }

         return var2;
      }
   }

   private static boolean validateDomain(String var0) {
      boolean var1 = true;
      int var2 = var0.indexOf(46);

      int var3;
      String var4;
      for(var3 = 0; (var2 = indexOfClear(var0.substring(var3), '.')) != -1; var3 += var4.length() + 1) {
         var4 = var0.substring(var3, var2 + var3);
         var1 = validateSubdomain(var4);
         if (!var1) {
            return false;
         }
      }

      var4 = var0.substring(var3);
      var1 = validateSubdomain(var4);
      return var1;
   }

   private static boolean validateAddrSpec(String var0) {
      boolean var1 = true;
      int var2 = var0.indexOf(64);
      var1 = validateLocalPart(var0.substring(0, var2));
      if (var1) {
         var1 = validateDomain(var0.substring(var2 + 1));
      }

      return var1;
   }

   private static boolean validateRoute(String var0) {
      boolean var1 = true;

      int var2;
      int var3;
      String var4;
      for(var2 = 0; (var3 = indexOfClear(var0.substring(var2), ',')) != -1; var2 += var4.length() + 1) {
         if (var0.charAt(var3 + 1) != '@') {
            return false;
         }

         var4 = var0.substring(var2, var2 + var3);
         if (!validateDomain(var4)) {
            return false;
         }
      }

      if (var0.charAt(var2) != '@') {
         return false;
      } else {
         var4 = var0.substring(var2 + 1, var0.length() - 1);
         var1 = validateDomain(var4);
         return var1;
      }
   }

   private static boolean validateMailbox(String var0) {
      boolean var1 = true;
      int var2 = 0;
      int var3 = indexOfClear(var0, '<');
      if (var3 != -1) {
         if (var0.charAt(var0.length() - 1) != '>') {
            return false;
         }

         if (var3 > 0) {
            var1 = validateWord(var0.substring(0, var3));
         }

         if (var1) {
            String var4 = var0.substring(var3 + 1, var0.length() - 1);
            if (var4.charAt(0) == '@') {
               int var5 = indexOfClear(var4, ':');
               String var6 = var4.substring(1, var5 + 1);
               var1 = validateRoute(var6);
               var2 += var6.length() + 1;
            }

            if (var1) {
               var1 = validateAddrSpec(var4.substring(var2));
            }
         }
      } else {
         var1 = validateAddrSpec(var0);
      }

      return var1;
   }

   private static boolean validateEmail(String var0) {
      boolean var1 = true;
      if (var0.charAt(var0.length() - 1) == ';') {
         int var2 = indexOfClear(var0, ':');
         if (var2 != -1) {
            var1 = validateWord(var0.substring(0, var2));
            if (var1 && var0.charAt(var2 + 1) != ';') {
               int var3;
               int var4;
               String var5;
               for(var4 = var2 + 1; (var3 = indexOfClear(var0.substring(var4), ',')) != -1; var4 += var5.length() + 1) {
                  var5 = var0.substring(var4, var4 + var3);
                  if (!validateMailbox(var5)) {
                     return false;
                  }
               }

               var5 = var0.substring(var4, var0.length() - 1);
               var1 = validateMailbox(var5);
               var4 += var5.length();
               if (var0.charAt(var4) != ';') {
                  var1 = false;
               }
            }
         } else {
            var1 = false;
         }
      } else {
         var1 = validateMailbox(var0);
      }

      return var1;
   }

   private static boolean validateIPv4(String var0) {
      boolean var1 = true;
      int var2 = 0;
      boolean var3 = false;

      String var5;
      int var10;
      for(int var6 = 0; var6 < 3; ++var6) {
         int var4 = var0.indexOf(46, var2);
         if (var4 == -1) {
            var1 = false;
            break;
         }

         var5 = var0.substring(var2, var4);

         try {
            var10 = Integer.parseInt(var5);
            if (var10 < 0 || var10 > 999) {
               var1 = false;
               break;
            }
         } catch (NumberFormatException var9) {
            var1 = false;
            break;
         }

         var2 += var5.length() + 1;
      }

      if (var1) {
         var5 = var0.substring(var2);

         try {
            var10 = Integer.parseInt(var5);
            if (var10 < 0 || var10 > 999) {
               var1 = false;
            }
         } catch (NumberFormatException var8) {
            var1 = false;
         }
      }

      return var1;
   }

   private static boolean validateIPv6Atom(String var0) {
      boolean var1 = true;
      int var2 = var0.length();
      if (var2 >= 1 && var2 <= 4) {
         for(int var3 = 0; var3 < var2; ++var3) {
            char var4 = var0.charAt(var3);
            if ((var4 < '0' || var4 > '9') && (var4 < 'A' || var4 > 'F')) {
               var1 = false;
               break;
            }
         }
      } else {
         var1 = false;
      }

      return var1;
   }

   private static boolean validateIPv6(String var0) {
      boolean var1 = true;
      int var2 = 0;
      boolean var3 = false;

      String var4;
      for(int var5 = 0; var5 < 7; ++var5) {
         int var6 = var0.indexOf(58, var2);
         if (var6 == -1) {
            var1 = false;
            break;
         }

         var4 = var0.substring(var2, var6);
         if (!validateIPv6Atom(var4)) {
            var1 = false;
            break;
         }

         var2 += var4.length() + 1;
      }

      if (var1) {
         var4 = var0.substring(var2);
         if (!validateIPv6Atom(var4)) {
            var1 = false;
         }
      }

      return var1;
   }

   private static boolean validateShortcode(String var0) {
      boolean var1 = true;

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         if ((var3 < 'A' || var3 > 'Z') && (var3 < 'a' || var3 > 'z') && (var3 < '0' || var3 > '9')) {
            var1 = false;
            break;
         }
      }

      return var1;
   }

   private static int countChar(String var0, char var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < var0.length(); ++var3) {
         if (var0.charAt(var3) == var1) {
            ++var2;
         }
      }

      return var2;
   }

   private static boolean validateAddress(String var0) {
      boolean var1 = true;
      boolean var3 = false;
      int var4 = var0.lastIndexOf(58);
      if (var4 != -1) {
         var3 = validateAppId(var0.substring(var4 + 1));
         if (countChar(var0, ':') == 7 && validateIPv6(var0)) {
            return true;
         }
      }

      if (var4 == 0) {
         return var3;
      } else {
         String var2 = var3 ? var0.substring(0, var4) : var0;
         int var5 = indexOfClear(var0, '@');
         long var6;
         if (var0.charAt(0) == '+') {
            try {
               var6 = Long.parseLong(var2.substring(1));
               if (var6 >= 0L) {
                  return true;
               }
            } catch (NumberFormatException var9) {
            }
         } else {
            try {
               var6 = Long.parseLong(var2);
               if (var6 >= 0L) {
                  return true;
               }
            } catch (NumberFormatException var8) {
            }
         }

         if (var5 != -1) {
            if (var3 && var5 > var4) {
               var1 = false;
            } else {
               var1 = validateEmail(var2);
            }
         } else {
            int var10 = countChar(var2, ':');
            int var7 = countChar(var2, '.');
            if (var7 == 3) {
               var1 = validateIPv4(var2);
            } else if (var10 == 7) {
               var1 = validateIPv6(var2);
            } else {
               var1 = validateShortcode(var2);
            }
         }

         return var1;
      }
   }

   public static boolean validateUrl(String var0, boolean var1) {
      boolean var2 = true;
      if (var0 == null) {
         return false;
      } else {
         String var3;
         if (var1) {
            var3 = "mms://";
         } else {
            var3 = "//";
         }

         if (var0.startsWith(var3)) {
            var2 = validateAddress(var0.substring(var3.length()));
         } else {
            var2 = false;
         }

         return var2;
      }
   }

   public static String getAppIdFromAddress(String var0) {
      String var1 = null;
      if (var0 != null && !validateIPv6(var0)) {
         int var2 = var0.lastIndexOf(58);
         if (var2 != -1 && var2 != var0.length() - 1) {
            var1 = var0.substring(var2 + 1);
            if (!validateAppId(var1)) {
               var1 = null;
            }
         }
      }

      return var1;
   }

   public static String getAppIdFromUrl(String var0) {
      return getAppIdFromAddress(var0);
   }

   public static String getAddressFromUrl(String var0) {
      String var1 = null;
      if (var0 != null && var0.startsWith("//")) {
         var1 = var0.substring(2);
      }

      return var1;
   }

   public static boolean getConnectionMode(String var0) {
      boolean var1 = false;
      if (var0 != null && var0.startsWith("//:")) {
         var1 = true;
      }

      return var1;
   }

   public static String getDeviceAddress(String var0) {
      String var1 = null;
      if (var0 != null) {
         if (!var0.startsWith("mms://")) {
            return null;
         }

         String var2 = var0.substring("mms://".length());
         if (getConnectionMode(var0)) {
            return null;
         }

         if (validateIPv6(var2)) {
            return var2;
         }

         int var4 = var2.lastIndexOf(58);
         if (var4 != -1 && var4 != var2.length() - 1) {
            String var3 = var2.substring(var4 + 1);
            if (validateAppId(var3)) {
               var1 = var2.substring(0, var4);
            } else {
               var1 = var2;
            }
         } else {
            var1 = var2;
         }
      }

      return var1;
   }

   public static boolean isPhoneNumber(String var0) {
      long var1 = -1L;

      try {
         if (var0.startsWith("+")) {
            var1 = Long.parseLong(var0.substring(1));
         } else {
            var1 = Long.parseLong(var0);
         }
      } catch (NumberFormatException var4) {
         return false;
      } catch (NullPointerException var5) {
         return false;
      }

      return var1 >= 0L;
   }
}
