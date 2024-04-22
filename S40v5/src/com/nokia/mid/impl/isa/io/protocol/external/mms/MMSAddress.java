package com.nokia.mid.impl.isa.io.protocol.external.mms;

public class MMSAddress {
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
            if (var1 == null) {
               return var2;
            }

            try {
               if (Long.parseLong(var1) < 0L) {
                  var2 = false;
               }

               return var2;
            } catch (NumberFormatException var5) {
            }
         } else if (var0.toLowerCase().equals("x-mms-priority") && (var1 == null || var1.equals("low") || var1.equals("normal") || var1.equals("high"))) {
            return var2;
         }

         var2 = false;
         return var2;
      }
   }

   public static boolean validateAppId(String var0) {
      boolean var1 = true;
      if (var0 != null && var0.length() <= 31) {
         var0 = var0;
         boolean var2 = true;

         for(int var3 = 0; var3 < var0.length(); ++var3) {
            char var4;
            if (((var4 = var0.charAt(var3)) <= '/' || var4 >= ':') && (var4 <= '@' || var4 >= '[') && (var4 <= '`' || var4 >= '{') && var4 != '.' && var4 != '_') {
               var2 = false;
               break;
            }
         }

         if (!var2) {
            var1 = false;
         }

         return var1;
      } else {
         return false;
      }
   }

   private static boolean g(String var0) {
      boolean var1 = true;

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3;
         if ((var3 = var0.charAt(var2)) > 177 || var3 == '"' || var3 < 0 || var3 == '\r') {
            var1 = false;
            break;
         }

         if (var3 == '\\') {
            try {
               if ((var3 = var0.charAt(var2 + 1)) <= 'A' || var3 >= 'Z' || var3 > 'a' && var3 < 'z') {
                  var1 = false;
                  break;
               }
            } catch (IndexOutOfBoundsException var4) {
               var1 = false;
            }
         }
      }

      return var1;
   }

   private static boolean h(String var0) {
      boolean var1 = true;

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3;
         if ((var3 = var0.charAt(var2)) == '(' || var3 == ')' || var3 == '<' || var3 == '>' || var3 == '@' || var3 == ',' || var3 == ';' || var3 == ':' || var3 == '\\' || var3 == '"' || var3 == '.' || var3 == '[' || var3 == ']' || var3 == ' ') {
            var1 = false;
            break;
         }
      }

      return var1;
   }

   private static boolean i(String var0) {
      boolean var1 = false;
      if (var0.charAt(0) == '"') {
         int var2;
         if ((var2 = var0.indexOf(34, 1)) == -1 || var2 != var0.length() - 1) {
            return false;
         }

         var1 = g(var0.substring(1, var2));
      } else {
         var1 = h(var0);
      }

      return var1;
   }

   private static boolean j(String var0) {
      boolean var1 = true;
      if (var0.length() == 0) {
         var1 = false;
      } else if (var0.charAt(0) != '[') {
         var1 = h(var0);
      } else {
         if (var0.charAt(var0.length() - 1) != ']') {
            return false;
         }

         String var2 = var0.substring(1, var0.length() - 1);

         for(int var3 = 0; var3 < var2.length(); ++var3) {
            char var4;
            if ((var4 = var2.charAt(var3)) == '[' || var4 == ']' || var4 == '\\' || var4 == '"' || var4 == '\r') {
               return false;
            }
         }
      }

      return var1;
   }

   private static int a(String var0, char var1) {
      int var2 = -1;
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      int var7 = 0;
      int var8 = 0;
      int var9 = 0;
      if (var0.length() == 0) {
         return -1;
      } else {
         while(!var3) {
            char var10;
            if ((var10 = var0.charAt(var9)) == var1 && !var4 && !var5 && !var6) {
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

   private static boolean k(String var0) {
      boolean var1 = false;
      var0.indexOf(46);
      var1 = false;

      int var2;
      String var3;
      int var4;
      for(var2 = 0; (var4 = a(var0.substring(var2), '.')) != -1; var2 += var3.length() + 1) {
         if (!j(var3 = var0.substring(var2, var4 + var2))) {
            return false;
         }
      }

      return j(var0.substring(var2));
   }

   private static boolean l(String var0) {
      boolean var1 = false;
      int var2 = var0.indexOf(64);
      String var7 = var0.substring(0, var2);
      boolean var3 = false;
      int var4 = a(var7, '.');
      int var5 = 0;
      boolean var6 = false;

      while(!var6) {
         if (var4 == -1) {
            var3 = i(var7.substring(var5));
            var6 = true;
         } else {
            String var8;
            if (!i(var8 = var7.substring(var5, var4))) {
               var3 = false;
               break;
            }

            var5 += var8.length() + 1;
            var4 = a(var7.substring(var5), '.');
         }
      }

      var1 = var3;
      if (var3) {
         var1 = k(var0.substring(var2 + 1));
      }

      return var1;
   }

   private static boolean m(String var0) {
      boolean var1 = true;
      int var2 = 0;
      int var3;
      if ((var3 = a(var0, '<')) != -1) {
         if (var0.charAt(var0.length() - 1) != '>') {
            return false;
         }

         if (var3 > 0) {
            var1 = i(var0.substring(0, var3));
         }

         if (var1) {
            String var9;
            if ((var9 = var0.substring(var3 + 1, var0.length() - 1)).charAt(0) == '@') {
               int var6 = a(var9, ':');
               String var7;
               String var8 = var7 = var9.substring(1, var6 + 1);
               boolean var4 = false;
               int var10 = 0;

               boolean var10000;
               while(true) {
                  int var5;
                  if ((var5 = a(var8.substring(var10), ',')) == -1) {
                     var10000 = var8.charAt(var10) != '@' ? false : k(var8.substring(var10 + 1, var8.length() - 1));
                     break;
                  }

                  if (var8.charAt(var5 + 1) != '@') {
                     var10000 = false;
                     break;
                  }

                  String var11;
                  if (!k(var11 = var8.substring(var10, var10 + var5))) {
                     var10000 = false;
                     break;
                  }

                  var10 += var11.length() + 1;
               }

               var1 = var10000;
               var2 = 0 + var7.length() + 1;
            }

            if (var1) {
               var1 = l(var9.substring(var2));
            }
         }
      } else {
         var1 = l(var0);
      }

      return var1;
   }

   private static boolean n(String var0) {
      boolean var1 = true;
      int var2 = 0;
      boolean var3 = false;

      String var4;
      int var8;
      for(int var5 = 0; var5 < 3; ++var5) {
         if ((var8 = var0.indexOf(46, var2)) == -1) {
            var1 = false;
            break;
         }

         var4 = var0.substring(var2, var8);

         try {
            if ((var8 = Integer.parseInt(var4)) < 0 || var8 > 999) {
               var1 = false;
               break;
            }
         } catch (NumberFormatException var7) {
            var1 = false;
            break;
         }

         var2 += var4.length() + 1;
      }

      if (var1) {
         var4 = var0.substring(var2);

         try {
            if ((var8 = Integer.parseInt(var4)) < 0 || var8 > 999) {
               var1 = false;
            }
         } catch (NumberFormatException var6) {
            var1 = false;
         }
      }

      return var1;
   }

   private static boolean o(String var0) {
      boolean var1 = true;
      int var2;
      if ((var2 = var0.length()) >= 1 && var2 <= 4) {
         for(int var3 = 0; var3 < var2; ++var3) {
            char var4;
            if (((var4 = var0.charAt(var3)) < '0' || var4 > '9') && (var4 < 'A' || var4 > 'F')) {
               var1 = false;
               break;
            }
         }
      } else {
         var1 = false;
      }

      return var1;
   }

   private static boolean p(String var0) {
      boolean var1 = true;
      int var2 = 0;
      boolean var3 = false;

      for(int var4 = 0; var4 < 7; ++var4) {
         int var5;
         if ((var5 = var0.indexOf(58, var2)) == -1) {
            var1 = false;
            break;
         }

         String var6;
         if (!o(var6 = var0.substring(var2, var5))) {
            var1 = false;
            break;
         }

         var2 += var6.length() + 1;
      }

      if (var1 && !o(var0.substring(var2))) {
         var1 = false;
      }

      return var1;
   }

   private static int b(String var0, char var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < var0.length(); ++var3) {
         if (var0.charAt(var3) == var1) {
            ++var2;
         }
      }

      return var2;
   }

   private static boolean q(String var0) {
      boolean var1 = false;
      var1 = false;
      int var3;
      if ((var3 = var0.lastIndexOf(58)) != -1) {
         var1 = validateAppId(var0.substring(var3 + 1));
         if (b(var0, ':') == 7 && p(var0)) {
            return true;
         }
      }

      if (var3 == 0) {
         return var1;
      } else {
         String var2 = var1 ? var0.substring(0, var3) : var0;
         int var4 = a(var0, '@');
         if (var0.charAt(0) == '+') {
            try {
               if (Long.parseLong(var2.substring(1)) >= 0L) {
                  return true;
               }
            } catch (NumberFormatException var9) {
            }
         } else {
            try {
               if (Long.parseLong(var2) >= 0L) {
                  return true;
               }
            } catch (NumberFormatException var8) {
            }
         }

         if (var4 != -1) {
            if (var1 && var4 > var3) {
               var1 = false;
            } else {
               boolean var10000;
               label110: {
                  var0 = var2;
                  var1 = false;
                  if (var2.charAt(var2.length() - 1) == ';') {
                     if ((var3 = a(var2, ':')) != -1) {
                        if ((var1 = i(var2.substring(0, var3))) && var2.charAt(var3 + 1) != ';') {
                           ++var3;

                           String var10;
                           while((var4 = a(var0.substring(var3), ',')) != -1) {
                              if (!m(var10 = var0.substring(var3, var3 + var4))) {
                                 var10000 = false;
                                 break label110;
                              }

                              var3 += var10.length() + 1;
                           }

                           var1 = m(var10 = var0.substring(var3, var0.length() - 1));
                           var3 += var10.length();
                           if (var0.charAt(var3) != ';') {
                              var1 = false;
                           }
                        }
                     } else {
                        var1 = false;
                     }
                  } else {
                     var1 = m(var2);
                  }

                  var10000 = var1;
               }

               var1 = var10000;
            }
         } else {
            int var6 = b(var2, ':');
            if (b(var2, '.') == 3) {
               var1 = n(var2);
            } else if (var6 == 7) {
               var1 = p(var2);
            } else {
               var0 = var2;
               var1 = true;

               for(var3 = 0; var3 < var0.length(); ++var3) {
                  char var11;
                  if (((var11 = var0.charAt(var3)) < 'A' || var11 > 'Z') && (var11 < 'a' || var11 > 'z') && (var11 < '0' || var11 > '9')) {
                     var1 = false;
                     break;
                  }
               }

               var1 = var1;
            }
         }

         return var1;
      }
   }

   public static boolean validateUrl(String var0, boolean var1) {
      boolean var2 = false;
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
            var2 = q(var0.substring(var3.length()));
         } else {
            var2 = false;
         }

         return var2;
      }
   }

   public static String getAppIdFromAddress(String var0) {
      String var1 = null;
      int var2;
      if (var0 != null && !p(var0) && (var2 = var0.lastIndexOf(58)) != -1 && var2 != var0.length() - 1 && !validateAppId(var1 = var0.substring(var2 + 1))) {
         var1 = null;
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

         if (p(var2)) {
            return var2;
         }

         int var3;
         if ((var3 = var2.lastIndexOf(58)) != -1 && var3 != var2.length() - 1) {
            if (validateAppId(var2.substring(var3 + 1))) {
               var1 = var2.substring(0, var3);
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
      long var1 = 0L;

      try {
         if (var0.startsWith("+")) {
            var1 = Long.parseLong(var0.substring(1));
         } else {
            var1 = Long.parseLong(var0);
         }
      } catch (NumberFormatException var3) {
         return false;
      } catch (NullPointerException var4) {
         return false;
      }

      return var1 >= 0L;
   }
}
