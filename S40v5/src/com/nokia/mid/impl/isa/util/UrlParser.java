package com.nokia.mid.impl.isa.util;

public class UrlParser {
   public static final int SCHEME = 0;
   public static final int AUTHORITY = 1;
   public static final int PATH = 2;
   public static final int QUERY = 3;

   public static String getAbsoluteURI(String var0, String var1) throws IllegalArgumentException {
      String[] var2 = getUriComponents(var0);
      String[] var8 = getUriComponents(var1);
      StringBuffer var3 = new StringBuffer("");
      if (var8[2].length() == 0 && var8[0] == null && var8[1] == null && var8[3] == null) {
         return var1;
      } else if (var8[0] != null) {
         return var1;
      } else {
         var8[0] = var2[0];
         if (var8[1] == null) {
            var8[1] = var2[1];
            if (var8[2].length() == 0 || var8[2].length() > 0 && var8[2].charAt(0) != '/') {
               String var10002 = var2[2];
               String var10 = var8[2];
               var1 = var10002;
               StringBuffer var4 = new StringBuffer("");
               boolean var5 = false;
               int var12;
               if ((var12 = var1.lastIndexOf(47)) < 0) {
                  throw new IllegalArgumentException("Invalid base URI.");
               }

               var4.append(var1.substring(0, var12 + 1));
               var4.append(var10);
               byte var11 = 1;
               int var6 = var4.toString().substring(0, var12).lastIndexOf(47);
               ++var12;

               for(; var12 < var4.length(); ++var12) {
                  char var9 = var4.charAt(var12);
                  int var7 = var4.length();
                  switch(var11) {
                  case 1:
                     switch(var9) {
                     case '.':
                        if (var12 + 1 >= var7) {
                           var4.delete(var12, var12 + 1);
                           var11 = 1;
                        } else {
                           var11 = 2;
                        }
                        continue;
                     case '/':
                        throw new IllegalArgumentException("Invalid relative URI.");
                     default:
                        var11 = 4;
                        continue;
                     }
                  case 2:
                     switch(var9) {
                     case '.':
                        if (var12 + 1 >= var7) {
                           if (var6 >= 0) {
                              var4.delete(var6 + 1, var12 + 1);
                              var12 = var6;
                              var6 = var4.toString().substring(0, var6).lastIndexOf(47);
                           } else {
                              var4.delete(1, var12 + 1);
                              var12 = 0;
                           }

                           var11 = 1;
                        } else {
                           var11 = 3;
                        }
                        continue;
                     case '/':
                        var12 -= 2;
                        var4.delete(var12 + 1, var12 + 3);
                        var11 = 1;
                        continue;
                     default:
                        var11 = 4;
                        continue;
                     }
                  case 3:
                     switch(var9) {
                     case '.':
                     default:
                        var11 = 4;
                        continue;
                     case '/':
                        if (var6 >= 0) {
                           var4.delete(var6 + 1, var12 + 1);
                           var12 = var6;
                           var6 = var4.toString().substring(0, var6).lastIndexOf(47);
                        } else {
                           var4.delete(1, var12 + 1);
                           var12 = 0;
                        }

                        var11 = 1;
                        continue;
                     }
                  case 4:
                     switch(var9) {
                     case '.':
                     default:
                        var11 = 4;
                        continue;
                     case '/':
                        var11 = 1;
                        var6 = var4.toString().substring(0, var12).lastIndexOf(47);
                        continue;
                     }
                  default:
                     throw new IllegalArgumentException("Invalid relative URI.");
                  }
               }

               var8[2] = var4.toString();
            }
         }

         if (var8[0] != null) {
            var3.append(var8[0]);
            var3.append(':');
         }

         if (var8[1] != null) {
            var3.append("//");
            var3.append(var8[1]);
         }

         var3.append(var8[2]);
         if (var8[3] != null) {
            var3.append(var8[3]);
         }

         return var3.toString();
      }
   }

   public static String[] getUriComponents(String var0) {
      String[] var1 = new String[4];
      int var2 = 0;
      int var3 = -1;
      int var4 = 3;
      byte var5 = 0;
      StringBuffer var7 = new StringBuffer("");

      for(var1[2] = ""; var2 <= var0.length(); ++var2) {
         char var6;
         if (var2 < var0.length()) {
            var6 = var0.charAt(var2);
         } else {
            var6 = 0;
         }

         switch(var5) {
         case 0:
            switch(var6) {
            case '\u0000':
               var1[3] = var7.toString();
               continue;
            case '#':
            case '?':
               var5 = 3;
               var7.append(var6);
               continue;
            case '/':
               if (var2 == 0) {
                  var5 = 1;
               } else {
                  var5 = 2;
               }

               var7.append(var6);
               var3 = var7.length();
               var4 = 0;
               continue;
            case ':':
               if (var2 <= 0) {
                  throw new IllegalArgumentException("invalid scheme");
               }

               var1[0] = var7.toString();
               var7.setLength(0);
               var5 = 1;
               continue;
            default:
               var7.append(var6);
               if (var2 == 0 && var6 == '.') {
                  var4 = 1;
                  var5 = 2;
               }
               continue;
            }
         case 1:
            switch(var6) {
            case '\u0000':
               if (var7.length() > 1) {
                  var1[1] = var7.toString().substring(2);
               } else {
                  var1[2] = var7.toString();
               }
               continue;
            case '#':
               if (var7.length() < 2) {
                  var1[2] = var7.toString();
                  var5 = 3;
                  var7.setLength(0);
                  var7.append(var6);
                  continue;
               }
            default:
               if (var7.length() < 2) {
                  var5 = 2;
               }

               var7.append(var6);
               continue;
            case '/':
               if (var7.length() > 1) {
                  var1[1] = var7.toString().substring(2);
                  var7.setLength(0);
                  var5 = 2;
               }

               var7.append(var6);
               var3 = var7.length();
               var4 = 0;
               continue;
            case '?':
               if (var7.length() > 1) {
                  var1[1] = var7.toString().substring(2);
               }

               var7.setLength(0);
               var5 = 3;
               var7.append(var6);
               var3 = -1;
               continue;
            }
         case 2:
            switch(var6) {
            case '\u0000':
               if (var4 < 3 && var4 > 0) {
                  if (var3 < 0) {
                     var3 = 0;
                  }

                  var3 += var4;
               }

               if (var3 >= 0) {
                  var1[2] = var7.toString();
               } else {
                  var3 = 0;
               }

               if (var3 < var7.length()) {
               }
               continue;
            case '#':
            case '?':
               var1[2] = var7.toString();
               var5 = 3;
               var7.setLength(0);
               var7.append(var6);
               continue;
            case '.':
               ++var4;
               var7.append(var6);
               continue;
            case '/':
               var7.append(var6);
               var3 = var7.length();
               var4 = 0;
               continue;
            default:
               var4 = 3;
               var7.append(var6);
               continue;
            }
         case 3:
            switch(var6) {
            case '\u0000':
               var1[3] = var7.toString();
               break;
            case '/':
            default:
               var7.append(var6);
            }
         }
      }

      return var1;
   }
}
