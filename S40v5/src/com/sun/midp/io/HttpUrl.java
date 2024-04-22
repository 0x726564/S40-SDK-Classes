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
         int var3;
         if ((var3 = var1.length()) != 0) {
            int var4;
            if ((var4 = var1.indexOf(58)) != -1) {
               if (var4 == var3 - 1) {
                  this.scheme = var1.substring(0, var4);
                  return;
               }

               if (var4 < var3 - 2 && var1.charAt(var4 + 1) == '/' && var1.charAt(var4 + 2) == '/') {
                  this.scheme = var1.substring(0, var4);
                  var2 = var4 + 1;
               }
            }

            this.a(var1, var2, var3);
         }
      }
   }

   public HttpUrl(String var1, String var2) {
      this.scheme = var1;
      if (var2 != null) {
         int var3;
         if ((var3 = var2.length()) != 0) {
            this.a(var2, 0, var3);
         }
      }
   }

   private void a(String var1, int var2, int var3) {
      if (var1.indexOf(32) != -1) {
         throw new IllegalArgumentException("Space character in URL");
      } else {
         int var5 = var3;
         int var6 = var3;
         int var7 = var3;
         int var8 = var3;
         int var4;
         if (var1.startsWith("//", var2)) {
            var4 = var2 + 2;
         } else {
            var4 = var2;
         }

         if ((var3 = var1.indexOf(35, var4)) != -1) {
            var6 = var3;
            var7 = var3;
            var8 = var3++;
            if (var3 < var5) {
               this.fragment = var1.substring(var3, var5);
            }
         }

         if ((var3 = var1.indexOf(63, var4)) != -1 && var3 < var8) {
            var6 = var3;
            var7 = var3++;
            if (var3 < var8) {
               this.query = var1.substring(var3, var8);
            }
         }

         if (var4 == var2) {
            var3 = var2;
         } else {
            var3 = var1.indexOf(47, var4);
         }

         if (var3 != -1 && var3 < var7) {
            var6 = var3;
            this.path = var1.substring(var3, var7);
         }

         if (var4 < var6) {
            this.authority = var1.substring(var4, var6);
            var4 = this.authority.length();
            if ((var3 = this.authority.indexOf(93)) == -1) {
               var2 = this.authority.indexOf(58);
            } else {
               var2 = this.authority.indexOf(58, var3);
            }

            int var10;
            if (var2 != -1) {
               var10 = var2++;
               if (var2 < var4) {
                  try {
                     this.port = Integer.parseInt(this.authority.substring(var2, var4));
                     if (this.port <= 0) {
                        throw new IllegalArgumentException("invalid port format");
                     }

                     if (this.port == 0 || this.port > 65535) {
                        throw new IllegalArgumentException("port out of legal range");
                     }
                  } catch (NumberFormatException var9) {
                     throw new IllegalArgumentException("invalid port format");
                  }
               }
            } else {
               var10 = var4;
            }

            if (var10 >= 1) {
               this.host = this.authority.substring(0, var10);
               if (this.host.startsWith("[") && this.host.endsWith("]")) {
                  if ((var10 = this.host.length()) < 4) {
                     throw new IllegalArgumentException("Invalid IPv6 address");
                  }

                  String var11 = this.host.substring(1, var10 - 1);
                  var10 = 0;
                  boolean var13 = false;
                  var5 = 0;
                  var6 = var11.indexOf(58);
                  if ((var7 = var11.length()) < 2 || var7 > 50) {
                     throw new IllegalArgumentException("Invalid IPv6 address");
                  }

                  if (var11.indexOf(":::") != -1) {
                     throw new IllegalArgumentException("Invalid IPv6 address");
                  }

                  if (var11.endsWith(":")) {
                     if (!var11.equals("::")) {
                        throw new IllegalArgumentException("Invalid IPv6 address - cannot end with ':'");
                     }
                  } else {
                     if (var11.startsWith(":")) {
                        if (!var11.startsWith("::")) {
                           throw new IllegalArgumentException("Invalid IPv6 address - cannot begin with ':'");
                        }

                        var13 = true;
                        var5 = 2;
                        var6 = var11.indexOf(58, 2);
                     }

                     while(var6 != -1) {
                        if (!w(var11.substring(var5, var6))) {
                           throw new IllegalArgumentException("Invalid IPv6 address");
                        }

                        ++var10;
                        ++var6;
                        if (var11.charAt(var6) == ':') {
                           if (var13) {
                              throw new IllegalArgumentException("Invalid IPv6 address - cannot contain '::' more than once");
                           }

                           var13 = true;
                           var5 = var6 + 1;
                           var6 = var11.indexOf(58, var5);
                        } else {
                           var5 = var6;
                           var6 = var11.indexOf(58, var6);
                        }
                     }

                     String var12;
                     if (!w(var12 = var11.substring(var5, var7)) && !x(var12)) {
                        throw new IllegalArgumentException("Invalid IPv6 address");
                     }

                     ++var10;
                     if (var13 && var10 > 6) {
                        throw new IllegalArgumentException("Invalid IPv6 address");
                     }

                     if (!var13 && var10 != 8) {
                        throw new IllegalArgumentException("Invalid IPv6 address");
                     }
                  }
               }

               if (!Character.isDigit(this.host.charAt(0)) && this.host.charAt(0) != '[') {
                  if ((var10 = this.host.indexOf(46)) != -1) {
                     this.domain = this.host.substring(var10 + 1, this.host.length());
                     this.machine = this.host.substring(0, var10);
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

   public final String toString() {
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

   private static boolean w(String var0) {
      if (var0 != null && !var0.equals("")) {
         int var1;
         if ((var1 = var0.length()) > 4) {
            return false;
         } else {
            byte[] var4 = var0.getBytes();

            for(int var3 = 0; var3 < var1; ++var3) {
               byte var2;
               if ((var2 = var4[var3]) < 48 || var2 > 57 && var2 < 65 || var2 > 70 && var2 < 97 || var2 > 102) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private static boolean x(String var0) {
      if (var0 != null && !var0.equals("")) {
         int var1;
         if ((var1 = var0.length()) <= 15 && var1 >= 7) {
            int var2 = 0;
            boolean var4 = false;
            int var3 = 0;
            boolean var5 = false;

            do {
               int var7;
               if ((var7 = var0.indexOf(46, var3)) == -1) {
                  if (var2 != 3) {
                     return false;
                  }

                  var7 = var1;
                  var4 = true;
               }

               try {
                  if ((var3 = Integer.parseInt(var0.substring(var3, var7))) < 0 || var3 > 255) {
                     return false;
                  }
               } catch (NumberFormatException var6) {
                  return false;
               }

               if (var4) {
                  return true;
               }

               var3 = var7 + 1;
               ++var2;
            } while(var2 <= 3);

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
