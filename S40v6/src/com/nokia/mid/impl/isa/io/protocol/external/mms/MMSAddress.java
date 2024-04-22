package com.nokia.mid.impl.isa.io.protocol.external.mms;

public class MMSAddress {
   private static final char COLON = ':';
   public static final boolean SERVER = true;
   public static final boolean CLIENT = false;
   public static final String MMS_PREFIX = "mms://";
   public static final int MAX_APP_ID_LENGTH = 32;

   public static boolean validateHeader(String header, String headerValue) {
      boolean res = true;
      if (header == null) {
         return false;
      } else {
         header = header.toLowerCase();
         if (headerValue != null) {
            headerValue = headerValue.toLowerCase();
         }

         if (header.equals("x-mms-delivery-time")) {
            if (headerValue != null) {
               try {
                  long l = Long.parseLong(headerValue);
                  if (l < 0L) {
                     res = false;
                  }
               } catch (NumberFormatException var5) {
                  res = false;
               }
            }
         } else if (header.toLowerCase().equals("x-mms-priority")) {
            if (headerValue != null && !headerValue.equals("low") && !headerValue.equals("normal") && !headerValue.equals("high")) {
               res = false;
            }
         } else {
            res = false;
         }

         return res;
      }
   }

   private static boolean validateAppIdSegment(String appIdSeg) {
      boolean res = true;
      if (appIdSeg.trim().length() <= 0) {
         return false;
      } else {
         for(int a = 0; a < appIdSeg.length(); ++a) {
            char c = appIdSeg.charAt(a);
            if ((c <= '/' || c >= ':') && (c <= '@' || c >= '[') && (c <= '`' || c >= '{') && c != '.' && c != '_') {
               res = false;
               break;
            }
         }

         return res;
      }
   }

   public static boolean validateAppId(String appId) {
      boolean res = true;
      if (appId != null && appId.length() <= 31) {
         if (!validateAppIdSegment(appId)) {
            res = false;
         }

         return res;
      } else {
         return false;
      }
   }

   private static boolean validateQuotedStr(String quotedStr) {
      boolean res = true;

      for(int a = 0; a < quotedStr.length(); ++a) {
         char ch = quotedStr.charAt(a);
         if (ch > 177 || ch == '"' || ch < 0 || ch == '\r') {
            res = false;
            break;
         }

         if (ch == '\\') {
            try {
               char next = quotedStr.charAt(a + 1);
               if (next <= 'A' || next >= 'Z' || next > 'a' && next < 'z') {
                  res = false;
                  break;
               }
            } catch (IndexOutOfBoundsException var5) {
               res = false;
            }
         }
      }

      return res;
   }

   private static boolean validateAtom(String atom) {
      boolean res = true;

      for(int a = 0; a < atom.length(); ++a) {
         char ch = atom.charAt(a);
         if (ch == '(' || ch == ')' || ch == '<' || ch == '>' || ch == '@' || ch == ',' || ch == ';' || ch == ':' || ch == '\\' || ch == '"' || ch == '.' || ch == '[' || ch == ']' || ch == ' ') {
            res = false;
            break;
         }
      }

      return res;
   }

   private static boolean validateWord(String word) {
      boolean res = true;
      if (word.charAt(0) == '"') {
         int nextQuote = word.indexOf(34, 1);
         if (nextQuote == -1 || nextQuote != word.length() - 1) {
            return false;
         }

         res = validateQuotedStr(word.substring(1, nextQuote));
      } else {
         res = validateAtom(word);
      }

      return res;
   }

   private static boolean validateLocalPart(String localPart) {
      boolean res = true;
      int dot = indexOfClear(localPart, '.');
      int pos = 0;
      boolean finished = false;

      while(!finished) {
         if (dot == -1) {
            res = validateWord(localPart.substring(pos));
            finished = true;
         } else {
            String word = localPart.substring(pos, dot);
            if (!validateWord(word)) {
               res = false;
               break;
            }

            pos += word.length() + 1;
            dot = indexOfClear(localPart.substring(pos), '.');
         }
      }

      return res;
   }

   private static boolean validateSubdomain(String subdomain) {
      boolean res = true;
      if (subdomain.length() == 0) {
         res = false;
      } else if (subdomain.charAt(0) != '[') {
         res = validateAtom(subdomain);
      } else {
         if (subdomain.charAt(subdomain.length() - 1) != ']') {
            return false;
         }

         String inner = subdomain.substring(1, subdomain.length() - 1);

         for(int a = 0; a < inner.length(); ++a) {
            char ch = inner.charAt(a);
            if (ch == '[' || ch == ']' || ch == '\\' || ch == '"' || ch == '\r') {
               return false;
            }
         }
      }

      return res;
   }

   private static int indexOfClear(String str, char ch) {
      int res = -1;
      boolean finished = false;
      boolean inQuotes = false;
      boolean inSquareBrackets = false;
      boolean inAngleBrackets = false;
      int squareBracketsLevel = 0;
      int angleBracketsLevel = 0;
      int pos = 0;
      if (str.length() == 0) {
         return res;
      } else {
         while(!finished) {
            char current = str.charAt(pos);
            if (current == ch && !inQuotes && !inSquareBrackets && !inAngleBrackets) {
               res = pos;
               finished = true;
            } else if (current == '[') {
               if (!inQuotes && !inAngleBrackets) {
                  inSquareBrackets = true;
                  ++squareBracketsLevel;
               }
            } else if (current == ']') {
               if (!inQuotes && !inAngleBrackets && inSquareBrackets) {
                  --squareBracketsLevel;
                  if (squareBracketsLevel == 0) {
                     inSquareBrackets = false;
                  }
               }
            } else if (current == '<') {
               if (!inQuotes && !inSquareBrackets) {
                  inAngleBrackets = true;
                  ++angleBracketsLevel;
               }
            } else if (current == '>') {
               if (!inQuotes && !inSquareBrackets && inAngleBrackets) {
                  --angleBracketsLevel;
                  if (angleBracketsLevel == 0) {
                     inAngleBrackets = false;
                  }
               }
            } else if (current == '"' && !inSquareBrackets && !inAngleBrackets) {
               inQuotes = !inQuotes;
            }

            ++pos;
            if (pos == str.length()) {
               finished = true;
            }
         }

         return res;
      }
   }

   private static boolean validateDomain(String domain) {
      boolean res = true;
      int dot = domain.indexOf(46);

      int pos;
      String subdomain;
      for(pos = 0; (dot = indexOfClear(domain.substring(pos), '.')) != -1; pos += subdomain.length() + 1) {
         subdomain = domain.substring(pos, dot + pos);
         res = validateSubdomain(subdomain);
         if (!res) {
            return false;
         }
      }

      subdomain = domain.substring(pos);
      res = validateSubdomain(subdomain);
      return res;
   }

   private static boolean validateAddrSpec(String addrSpec) {
      boolean res = true;
      int at = addrSpec.indexOf(64);
      res = validateLocalPart(addrSpec.substring(0, at));
      if (res) {
         res = validateDomain(addrSpec.substring(at + 1));
      }

      return res;
   }

   private static boolean validateRoute(String route) {
      boolean res = true;

      int pos;
      int comma;
      String domain;
      for(pos = 0; (comma = indexOfClear(route.substring(pos), ',')) != -1; pos += domain.length() + 1) {
         if (route.charAt(comma + 1) != '@') {
            return false;
         }

         domain = route.substring(pos, pos + comma);
         if (!validateDomain(domain)) {
            return false;
         }
      }

      if (route.charAt(pos) != '@') {
         return false;
      } else {
         domain = route.substring(pos + 1, route.length() - 1);
         res = validateDomain(domain);
         return res;
      }
   }

   private static boolean validateMailbox(String mailbox) {
      boolean res = true;
      int pos = 0;
      int lt = indexOfClear(mailbox, '<');
      if (lt != -1) {
         if (mailbox.charAt(mailbox.length() - 1) != '>') {
            return false;
         }

         if (lt > 0) {
            res = validateWord(mailbox.substring(0, lt));
         }

         if (res) {
            String routeAddr = mailbox.substring(lt + 1, mailbox.length() - 1);
            if (routeAddr.charAt(0) == '@') {
               int colonIx = indexOfClear(routeAddr, ':');
               String route = routeAddr.substring(1, colonIx + 1);
               res = validateRoute(route);
               pos += route.length() + 1;
            }

            if (res) {
               res = validateAddrSpec(routeAddr.substring(pos));
            }
         }
      } else {
         res = validateAddrSpec(mailbox);
      }

      return res;
   }

   private static boolean validateEmail(String email) {
      boolean res = true;
      if (email.charAt(email.length() - 1) == ';') {
         int colonIx = indexOfClear(email, ':');
         if (colonIx != -1) {
            res = validateWord(email.substring(0, colonIx));
            if (res && email.charAt(colonIx + 1) != ';') {
               int comma;
               int pos;
               String mailbox;
               for(pos = colonIx + 1; (comma = indexOfClear(email.substring(pos), ',')) != -1; pos += mailbox.length() + 1) {
                  mailbox = email.substring(pos, pos + comma);
                  if (!validateMailbox(mailbox)) {
                     return false;
                  }
               }

               mailbox = email.substring(pos, email.length() - 1);
               res = validateMailbox(mailbox);
               pos += mailbox.length();
               if (email.charAt(pos) != ';') {
                  res = false;
               }
            }
         } else {
            res = false;
         }
      } else {
         res = validateMailbox(email);
      }

      return res;
   }

   private static boolean validateIPv4(String ipv4) {
      boolean res = true;
      int pos = 0;
      int val = false;

      String num;
      int val;
      for(int a = 0; a < 3; ++a) {
         int dot = ipv4.indexOf(46, pos);
         if (dot == -1) {
            res = false;
            break;
         }

         num = ipv4.substring(pos, dot);

         try {
            val = Integer.parseInt(num);
            if (val < 0 || val > 999) {
               res = false;
               break;
            }
         } catch (NumberFormatException var9) {
            res = false;
            break;
         }

         pos += num.length() + 1;
      }

      if (res) {
         num = ipv4.substring(pos);

         try {
            val = Integer.parseInt(num);
            if (val < 0 || val > 999) {
               res = false;
            }
         } catch (NumberFormatException var8) {
            res = false;
         }
      }

      return res;
   }

   private static boolean validateIPv6Atom(String ipv6Atom) {
      boolean res = true;
      int len = ipv6Atom.length();
      if (len >= 1 && len <= 4) {
         for(int a = 0; a < len; ++a) {
            char ch = ipv6Atom.charAt(a);
            if ((ch < '0' || ch > '9') && (ch < 'A' || ch > 'F')) {
               res = false;
               break;
            }
         }
      } else {
         res = false;
      }

      return res;
   }

   private static boolean validateIPv6(String ipv6) {
      boolean res = true;
      int pos = 0;
      int colonIx = false;

      String ipv6Atom;
      for(int a = 0; a < 7; ++a) {
         int colonIx = ipv6.indexOf(58, pos);
         if (colonIx == -1) {
            res = false;
            break;
         }

         ipv6Atom = ipv6.substring(pos, colonIx);
         if (!validateIPv6Atom(ipv6Atom)) {
            res = false;
            break;
         }

         pos += ipv6Atom.length() + 1;
      }

      if (res) {
         ipv6Atom = ipv6.substring(pos);
         if (!validateIPv6Atom(ipv6Atom)) {
            res = false;
         }
      }

      return res;
   }

   private static boolean validateShortcode(String shortcode) {
      boolean res = true;

      for(int a = 0; a < shortcode.length(); ++a) {
         char ch = shortcode.charAt(a);
         if ((ch < 'A' || ch > 'Z') && (ch < 'a' || ch > 'z') && (ch < '0' || ch > '9')) {
            res = false;
            break;
         }
      }

      return res;
   }

   private static int countChar(String str, char ch) {
      int count = 0;

      for(int a = 0; a < str.length(); ++a) {
         if (str.charAt(a) == ch) {
            ++count;
         }
      }

      return count;
   }

   private static boolean validateAddress(String addr) {
      boolean res = true;
      boolean appIdPresent = false;
      int colonIx = addr.lastIndexOf(58);
      if (colonIx != -1) {
         appIdPresent = validateAppId(addr.substring(colonIx + 1));
         if (countChar(addr, ':') == 7 && validateIPv6(addr)) {
            return true;
         }
      }

      if (colonIx == 0) {
         return appIdPresent;
      } else {
         String theAddr = appIdPresent ? addr.substring(0, colonIx) : addr;
         int at = indexOfClear(addr, '@');
         long num;
         if (addr.charAt(0) == '+') {
            try {
               num = Long.parseLong(theAddr.substring(1));
               if (num >= 0L) {
                  return true;
               }
            } catch (NumberFormatException var9) {
            }
         } else {
            try {
               num = Long.parseLong(theAddr);
               if (num >= 0L) {
                  return true;
               }
            } catch (NumberFormatException var8) {
            }
         }

         if (at != -1) {
            if (appIdPresent && at > colonIx) {
               res = false;
            } else {
               res = validateEmail(theAddr);
            }
         } else {
            int colons = countChar(theAddr, ':');
            int dots = countChar(theAddr, '.');
            if (dots == 3) {
               res = validateIPv4(theAddr);
            } else if (colons == 7) {
               res = validateIPv6(theAddr);
            } else {
               res = validateShortcode(theAddr);
            }
         }

         return res;
      }
   }

   public static boolean validateUrl(String url, boolean checkProtocol) {
      boolean res = true;
      if (url == null) {
         return false;
      } else {
         String str;
         if (checkProtocol) {
            str = "mms://";
         } else {
            str = "//";
         }

         if (url.startsWith(str)) {
            res = validateAddress(url.substring(str.length()));
         } else {
            res = false;
         }

         return res;
      }
   }

   public static String getAppIdFromAddress(String address) {
      String tmpAppID = null;
      if (address != null && !validateIPv6(address)) {
         int colonIx = address.lastIndexOf(58);
         if (colonIx != -1 && colonIx != address.length() - 1) {
            tmpAppID = address.substring(colonIx + 1);
            if (!validateAppId(tmpAppID)) {
               tmpAppID = null;
            }
         }
      }

      return tmpAppID;
   }

   public static String getAppIdFromUrl(String url) {
      return getAppIdFromAddress(url);
   }

   public static String getAddressFromUrl(String url) {
      String res = null;
      if (url != null && url.startsWith("//")) {
         res = url.substring(2);
      }

      return res;
   }

   public static boolean getConnectionMode(String url) {
      boolean res = false;
      if (url != null && url.startsWith("//:")) {
         res = true;
      }

      return res;
   }

   public static String getDeviceAddress(String address) {
      String devAddr = null;
      if (address != null) {
         if (!address.startsWith("mms://")) {
            return null;
         }

         String tmpAddress = address.substring("mms://".length());
         if (getConnectionMode(address)) {
            return null;
         }

         if (validateIPv6(tmpAddress)) {
            return tmpAddress;
         }

         int colonIx = tmpAddress.lastIndexOf(58);
         if (colonIx != -1 && colonIx != tmpAddress.length() - 1) {
            String tmpAppId = tmpAddress.substring(colonIx + 1);
            if (validateAppId(tmpAppId)) {
               devAddr = tmpAddress.substring(0, colonIx);
            } else {
               devAddr = tmpAddress;
            }
         } else {
            devAddr = tmpAddress;
         }
      }

      return devAddr;
   }

   public static boolean isPhoneNumber(String addr) {
      long phone = -1L;

      try {
         if (addr.startsWith("+")) {
            phone = Long.parseLong(addr.substring(1));
         } else {
            phone = Long.parseLong(addr);
         }
      } catch (NumberFormatException var4) {
         return false;
      } catch (NullPointerException var5) {
         return false;
      }

      return phone >= 0L;
   }
}
