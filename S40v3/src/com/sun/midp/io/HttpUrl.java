package com.sun.midp.io;

import com.nokia.mid.pri.PriAccess;
import java.io.IOException;

public class HttpUrl {
   public String scheme;
   public String authority;
   public String path;
   public String query;
   public String fragment;
   public String host;
   public int port = -1;
   public String machine;
   public String domain;
   public static final int UNSPECIFIED_PORT = -1;

   public HttpUrl(String var1) {
      int var2 = 0;
      if (var1 != null) {
         int var3 = var1.length();
         if (var3 != 0) {
            int var4 = var1.indexOf(58);
            if (var4 != -1) {
               if (var4 == var3 - 1) {
                  this.scheme = var1.substring(0, var4);
                  return;
               }

               if (var4 < var3 - 2 && var1.charAt(var4 + 1) == '/' && var1.charAt(var4 + 2) == '/') {
                  this.scheme = var1.substring(0, var4);
                  var2 = var4 + 1;
               }
            }

            this.parseAfterScheme(var1, var2, var3);
         }
      }
   }

   public HttpUrl(String var1, String var2) {
      this.scheme = var1;
      if (var2 != null) {
         int var3 = var2.length();
         if (var3 != 0) {
            this.parseAfterScheme(var2, 0, var3);
         }
      }
   }

   private void parseAfterScheme(String var1, int var2, int var3) {
      if (var1.indexOf(32) != -1) {
         throw new IllegalArgumentException("Space character in URL");
      } else {
         int var7 = var3;
         int var8 = var3;
         int var9 = var3;
         int var5;
         if (var1.startsWith("//", var2)) {
            var5 = var2 + 2;
         } else {
            var5 = var2;
         }

         int var4 = var1.indexOf(35, var5);
         if (var4 != -1) {
            var7 = var4;
            var8 = var4;
            var9 = var4++;
            if (var4 < var3) {
               this.fragment = var1.substring(var4, var3);
            }
         }

         var4 = var1.indexOf(63, var5);
         if (var4 != -1 && var4 < var9) {
            var7 = var4;
            var8 = var4++;
            if (var4 < var9) {
               this.query = var1.substring(var4, var9);
            }
         }

         if (var5 == var2) {
            var4 = var2;
         } else {
            var4 = var1.indexOf(47, var5);
         }

         if (var4 != -1 && var4 < var8) {
            var7 = var4;
            this.path = var1.substring(var4, var8);
         }

         if (var5 < var7) {
            this.authority = var1.substring(var5, var7);
            int var12 = this.authority.length();
            var4 = this.authority.indexOf(93);
            int var11;
            if (var4 == -1) {
               var11 = this.authority.indexOf(58);
            } else {
               var11 = this.authority.indexOf(58, var4);
            }

            int var10;
            if (var11 != -1) {
               var10 = var11++;
               if (var11 < var12) {
                  try {
                     this.port = Integer.parseInt(this.authority.substring(var11, var12));
                     if (this.port <= 0) {
                        throw new IllegalArgumentException("invalid port format");
                     }

                     if (this.port == 0 || this.port > 65535) {
                        throw new IllegalArgumentException("port out of legal range");
                     }
                  } catch (NumberFormatException var15) {
                     throw new IllegalArgumentException("invalid port format");
                  }
               }
            } else {
               var10 = var12;
            }

            if (var10 >= 1) {
               this.host = this.authority.substring(0, var10);
               if (this.host.startsWith("[") && this.host.endsWith("]")) {
                  int var14 = this.host.length();
                  if (var14 < 4) {
                     throw new IllegalArgumentException("Invalid IPv6 address");
                  }

                  this.validateIPv6Address(this.host.substring(1, var14 - 1));
               }

               if (!Character.isDigit(this.host.charAt(0)) && this.host.charAt(0) != '[') {
                  int var13 = this.host.indexOf(46);
                  if (var13 != -1) {
                     this.domain = this.host.substring(var13 + 1, this.host.length());
                     this.machine = this.host.substring(0, var13);
                  } else {
                     this.machine = this.host;
                  }

               } else if (PriAccess.getInt(5) == 3 && !PriAccess.getFlag(3) && System.getProperty("jms.system.midlet") == null) {
                  throw new IllegalArgumentException("IP address not allowed");
               }
            }
         }
      }
   }

   public void addBaseUrl(String var1) throws IOException {
      this.addBaseUrl(new HttpUrl(var1));
   }

   public void addBaseUrl(HttpUrl var1) {
      if (this.authority == null) {
         this.scheme = var1.scheme;
         this.authority = var1.authority;
         if (this.path == null) {
            this.path = var1.path;
         } else if (this.path.charAt(0) != '/' && var1.path != null && var1.path.charAt(0) == '/') {
            String var2 = var1.path.substring(0, var1.path.lastIndexOf(47));
            this.path = var2 + '/' + this.path;
         }
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      if (this.scheme != null) {
         var1.append(this.scheme);
         var1.append(':');
      }

      if (this.authority != null) {
         var1.append('/');
         var1.append('/');
         var1.append(this.authority);
      }

      if (this.path != null) {
         var1.append(this.path);
      }

      if (this.query != null) {
         var1.append('?');
         var1.append(this.query);
      }

      if (this.fragment != null) {
         var1.append('#');
         var1.append(this.fragment);
      }

      return var1.toString();
   }

   private void validateIPv6Address(String var1) throws IllegalArgumentException {
      int var2 = 0;
      boolean var3 = false;
      int var5 = 0;
      int var6 = var1.indexOf(58);
      int var7 = var1.length();
      if (var7 >= 2 && var7 <= 50) {
         if (var1.indexOf(":::") != -1) {
            throw new IllegalArgumentException("Invalid IPv6 address");
         } else if (var1.endsWith(":")) {
            if (!var1.equals("::")) {
               throw new IllegalArgumentException("Invalid IPv6 address - cannot end with ':'");
            }
         } else {
            if (var1.startsWith(":")) {
               if (!var1.startsWith("::")) {
                  throw new IllegalArgumentException("Invalid IPv6 address - cannot begin with ':'");
               }

               var3 = true;
               var5 = 2;
               var6 = var1.indexOf(58, var5);
            }

            String var4;
            while(var6 != -1) {
               var4 = var1.substring(var5, var6);
               if (!this.isValidHexSegment(var4)) {
                  throw new IllegalArgumentException("Invalid IPv6 address");
               }

               ++var2;
               ++var6;
               if (var1.charAt(var6) == ':') {
                  if (var3) {
                     throw new IllegalArgumentException("Invalid IPv6 address - cannot contain '::' more than once");
                  }

                  var3 = true;
                  var5 = var6 + 1;
                  var6 = var1.indexOf(58, var5);
               } else {
                  var5 = var6;
                  var6 = var1.indexOf(58, var6);
               }
            }

            var4 = var1.substring(var5, var7);
            if (!this.isValidHexSegment(var4) && !this.isValidIPv4Segment(var4)) {
               throw new IllegalArgumentException("Invalid IPv6 address");
            } else {
               ++var2;
               if (var3 && var2 > 6) {
                  throw new IllegalArgumentException("Invalid IPv6 address");
               } else if (!var3 && var2 != 8) {
                  throw new IllegalArgumentException("Invalid IPv6 address");
               }
            }
         }
      } else {
         throw new IllegalArgumentException("Invalid IPv6 address");
      }
   }

   private boolean isValidHexSegment(String var1) {
      if (var1 != null && !var1.equals("")) {
         int var2 = var1.length();
         if (var2 > 4) {
            return false;
         } else {
            byte[] var3 = var1.getBytes();

            for(int var5 = 0; var5 < var2; ++var5) {
               byte var4 = var3[var5];
               if (var4 < 48 || var4 > 57 && var4 < 65 || var4 > 70 && var4 < 97 || var4 > 102) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean isValidIPv4Segment(String var1) {
      if (var1 != null && !var1.equals("")) {
         int var2 = var1.length();
         if (var2 <= 15 && var2 >= 7) {
            int var3 = 0;
            boolean var5 = false;
            int var6 = 0;
            boolean var7 = false;

            do {
               int var10 = var1.indexOf(46, var6);
               if (var10 == -1) {
                  if (var3 != 3) {
                     return false;
                  }

                  var10 = var2;
                  var5 = true;
               }

               try {
                  int var4 = Integer.parseInt(var1.substring(var6, var10));
                  if (var4 < 0 || var4 > 255) {
                     return false;
                  }

                  if (var5) {
                     return true;
                  }
               } catch (NumberFormatException var9) {
                  return false;
               }

               var6 = var10 + 1;
               ++var3;
            } while(var3 <= 3);

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
