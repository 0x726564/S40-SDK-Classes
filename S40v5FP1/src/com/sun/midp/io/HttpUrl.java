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

   public HttpUrl(String url) {
      int afterScheme = 0;
      if (url != null) {
         int length = url.length();
         if (length != 0) {
            int endOfScheme = url.indexOf(58);
            if (endOfScheme != -1) {
               if (endOfScheme == length - 1) {
                  this.scheme = url.substring(0, endOfScheme);
                  return;
               }

               if (endOfScheme < length - 2 && url.charAt(endOfScheme + 1) == '/' && url.charAt(endOfScheme + 2) == '/') {
                  this.scheme = url.substring(0, endOfScheme);
                  afterScheme = endOfScheme + 1;
               }
            }

            this.parseAfterScheme(url, afterScheme, length);
         }
      }
   }

   public HttpUrl(String theScheme, String partialUrl) {
      this.scheme = theScheme;
      if (partialUrl != null) {
         int length = partialUrl.length();
         if (length != 0) {
            this.parseAfterScheme(partialUrl, 0, length);
         }
      }
   }

   private void parseAfterScheme(String url, int afterScheme, int length) {
      if (url.indexOf(32) != -1) {
         throw new IllegalArgumentException("Space character in URL");
      } else {
         int endOfAuthority = length;
         int endOfPath = length;
         int endOfQuery = length;
         int startOfAuthority;
         if (url.startsWith("//", afterScheme)) {
            startOfAuthority = afterScheme + 2;
         } else {
            startOfAuthority = afterScheme;
         }

         int start = url.indexOf(35, startOfAuthority);
         if (start != -1) {
            endOfAuthority = start;
            endOfPath = start;
            endOfQuery = start++;
            if (start < length) {
               this.fragment = url.substring(start, length);
            }
         }

         start = url.indexOf(63, startOfAuthority);
         if (start != -1 && start < endOfQuery) {
            endOfAuthority = start;
            endOfPath = start++;
            if (start < endOfQuery) {
               this.query = url.substring(start, endOfQuery);
            }
         }

         if (startOfAuthority == afterScheme) {
            start = afterScheme;
         } else {
            start = url.indexOf(47, startOfAuthority);
         }

         if (start != -1 && start < endOfPath) {
            endOfAuthority = start;
            this.path = url.substring(start, endOfPath);
         }

         if (startOfAuthority < endOfAuthority) {
            this.authority = url.substring(startOfAuthority, endOfAuthority);
            int endOfPort = this.authority.length();
            start = this.authority.indexOf(93);
            int startOfPort;
            if (start == -1) {
               startOfPort = this.authority.indexOf(58);
            } else {
               startOfPort = this.authority.indexOf(58, start);
            }

            int endOfHost;
            if (startOfPort != -1) {
               endOfHost = startOfPort++;
               if (startOfPort < endOfPort) {
                  try {
                     this.port = Integer.parseInt(this.authority.substring(startOfPort, endOfPort));
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
               endOfHost = endOfPort;
            }

            if (endOfHost >= 1) {
               this.host = this.authority.substring(0, endOfHost);
               if (this.host.startsWith("[") && this.host.endsWith("]")) {
                  int len = this.host.length();
                  if (len < 4) {
                     throw new IllegalArgumentException("Invalid IPv6 address");
                  }

                  this.validateIPv6Address(this.host.substring(1, len - 1));
               }

               if (!Character.isDigit(this.host.charAt(0)) && this.host.charAt(0) != '[') {
                  int startOfDomain = this.host.indexOf(46);
                  if (startOfDomain != -1) {
                     this.domain = this.host.substring(startOfDomain + 1, this.host.length());
                     this.machine = this.host.substring(0, startOfDomain);
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

   public void addBaseUrl(String baseUrl) throws IOException {
      this.addBaseUrl(new HttpUrl(baseUrl));
   }

   public void addBaseUrl(HttpUrl baseUrl) {
      if (this.authority == null) {
         this.scheme = baseUrl.scheme;
         this.authority = baseUrl.authority;
         if (this.path == null) {
            this.path = baseUrl.path;
         } else if (this.path.charAt(0) != '/' && baseUrl.path != null && baseUrl.path.charAt(0) == '/') {
            String basePath = baseUrl.path.substring(0, baseUrl.path.lastIndexOf(47));
            this.path = basePath + '/' + this.path;
         }
      }
   }

   public String toString() {
      StringBuffer url = new StringBuffer();
      if (this.scheme != null) {
         url.append(this.scheme);
         url.append(':');
      }

      if (this.authority != null) {
         url.append('/');
         url.append('/');
         url.append(this.authority);
      }

      if (this.path != null) {
         url.append(this.path);
      }

      if (this.query != null) {
         url.append('?');
         url.append(this.query);
      }

      if (this.fragment != null) {
         url.append('#');
         url.append(this.fragment);
      }

      return url.toString();
   }

   private void validateIPv6Address(String address) throws IllegalArgumentException {
      int segmentCount = 0;
      boolean containsDoubleColon = false;
      int start = 0;
      int end = address.indexOf(58);
      int length = address.length();
      if (length >= 2 && length <= 50) {
         if (address.indexOf(":::") != -1) {
            throw new IllegalArgumentException("Invalid IPv6 address");
         } else if (address.endsWith(":")) {
            if (!address.equals("::")) {
               throw new IllegalArgumentException("Invalid IPv6 address - cannot end with ':'");
            }
         } else {
            if (address.startsWith(":")) {
               if (!address.startsWith("::")) {
                  throw new IllegalArgumentException("Invalid IPv6 address - cannot begin with ':'");
               }

               containsDoubleColon = true;
               start = 2;
               end = address.indexOf(58, start);
            }

            String segment;
            while(end != -1) {
               segment = address.substring(start, end);
               if (!this.isValidHexSegment(segment)) {
                  throw new IllegalArgumentException("Invalid IPv6 address");
               }

               ++segmentCount;
               ++end;
               if (address.charAt(end) == ':') {
                  if (containsDoubleColon) {
                     throw new IllegalArgumentException("Invalid IPv6 address - cannot contain '::' more than once");
                  }

                  containsDoubleColon = true;
                  start = end + 1;
                  end = address.indexOf(58, start);
               } else {
                  start = end;
                  end = address.indexOf(58, end);
               }
            }

            segment = address.substring(start, length);
            if (!this.isValidHexSegment(segment) && !this.isValidIPv4Segment(segment)) {
               throw new IllegalArgumentException("Invalid IPv6 address");
            } else {
               ++segmentCount;
               if (containsDoubleColon && segmentCount > 6) {
                  throw new IllegalArgumentException("Invalid IPv6 address");
               } else if (!containsDoubleColon && segmentCount != 8) {
                  throw new IllegalArgumentException("Invalid IPv6 address");
               }
            }
         }
      } else {
         throw new IllegalArgumentException("Invalid IPv6 address");
      }
   }

   private boolean isValidHexSegment(String segment) {
      if (segment != null && !segment.equals("")) {
         int length = segment.length();
         if (length > 4) {
            return false;
         } else {
            byte[] bytes = segment.getBytes();

            for(int i = 0; i < length; ++i) {
               int b = bytes[i];
               if (b < 48 || b > 57 && b < 65 || b > 70 && b < 97 || b > 102) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean isValidIPv4Segment(String segment) {
      if (segment != null && !segment.equals("")) {
         int length = segment.length();
         if (length <= 15 && length >= 7) {
            int dotCount = 0;
            boolean isLastAddressPart = false;
            int start = 0;
            boolean var7 = false;

            do {
               int end = segment.indexOf(46, start);
               if (end == -1) {
                  if (dotCount != 3) {
                     return false;
                  }

                  end = length;
                  isLastAddressPart = true;
               }

               try {
                  int addressPart = Integer.parseInt(segment.substring(start, end));
                  if (addressPart < 0 || addressPart > 255) {
                     return false;
                  }

                  if (isLastAddressPart) {
                     return true;
                  }
               } catch (NumberFormatException var9) {
                  return false;
               }

               start = end + 1;
               ++dotCount;
            } while(dotCount <= 3);

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
