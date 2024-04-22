package com.nokia.mid.impl.isa.util;

public class UrlParser {
   public static final int SCHEME = 0;
   public static final int AUTHORITY = 1;
   public static final int PATH = 2;
   public static final int QUERY = 3;
   private static final int complete_state = 1;
   private static final int one_dot_state = 2;
   private static final int two_dots_state = 3;
   private static final int alpha_state = 4;
   private static final char EOF = '\u0000';

   public static String getAbsoluteURI(String var0, String var1) throws IllegalArgumentException {
      String[] var2 = getUriComponents(var0);
      String[] var3 = getUriComponents(var1);
      StringBuffer var4 = new StringBuffer("");
      if (var3[2].length() == 0 && var3[0] == null && var3[1] == null && var3[3] == null) {
         return var1;
      } else if (var3[0] != null) {
         return var1;
      } else {
         var3[0] = var2[0];
         if (var3[1] == null) {
            var3[1] = var2[1];
            if (var3[2].length() == 0 || var3[2].length() > 0 && var3[2].charAt(0) != '/') {
               var3[2] = resolvePath(var2[2], var3[2]);
            }
         }

         if (var3[0] != null) {
            var4.append(var3[0]);
            var4.append(':');
         }

         if (var3[1] != null) {
            var4.append("//");
            var4.append(var3[1]);
         }

         var4.append(var3[2]);
         if (var3[3] != null) {
            var4.append(var3[3]);
         }

         return var4.toString();
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
               if (var3 >= 0) {
               }

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

   private static String resolvePath(String var0, String var1) {
      StringBuffer var2 = new StringBuffer("");
      boolean var3 = false;
      int var8 = var0.lastIndexOf(47);
      if (var8 < 0) {
         throw new IllegalArgumentException("Invalid base URI.");
      } else {
         var2.append(var0.substring(0, var8 + 1));
         var2.append(var1);
         byte var5 = 1;
         int var6 = var2.toString().substring(0, var8).lastIndexOf(47);
         ++var8;

         for(; var8 < var2.length(); ++var8) {
            char var4 = var2.charAt(var8);
            int var7 = var2.length();
            switch(var5) {
            case 1:
               switch(var4) {
               case '.':
                  if (var8 + 1 >= var7) {
                     var2.delete(var8, var8 + 1);
                     var5 = 1;
                  } else {
                     var5 = 2;
                  }
                  continue;
               case '/':
                  throw new IllegalArgumentException("Invalid relative URI.");
               default:
                  var5 = 4;
                  continue;
               }
            case 2:
               switch(var4) {
               case '.':
                  if (var8 + 1 >= var7) {
                     if (var6 >= 0) {
                        var2.delete(var6 + 1, var8 + 1);
                        var8 = var6;
                        var6 = var2.toString().substring(0, var6).lastIndexOf(47);
                     } else {
                        var2.delete(1, var8 + 1);
                        var8 = 0;
                     }

                     var5 = 1;
                  } else {
                     var5 = 3;
                  }
                  continue;
               case '/':
                  var8 -= 2;
                  var2.delete(var8 + 1, var8 + 3);
                  var5 = 1;
                  continue;
               default:
                  var5 = 4;
                  continue;
               }
            case 3:
               switch(var4) {
               case '.':
               default:
                  var5 = 4;
                  continue;
               case '/':
                  if (var6 >= 0) {
                     var2.delete(var6 + 1, var8 + 1);
                     var8 = var6;
                     var6 = var2.toString().substring(0, var6).lastIndexOf(47);
                  } else {
                     var2.delete(1, var8 + 1);
                     var8 = 0;
                  }

                  var5 = 1;
                  continue;
               }
            case 4:
               switch(var4) {
               case '.':
               default:
                  var5 = 4;
                  continue;
               case '/':
                  var5 = 1;
                  var6 = var2.toString().substring(0, var8).lastIndexOf(47);
                  continue;
               }
            default:
               throw new IllegalArgumentException("Invalid relative URI.");
            }
         }

         return var2.toString();
      }
   }
}
