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

   public static String getAbsoluteURI(String baseURI, String relURI) throws IllegalArgumentException {
      String[] base = getUriComponents(baseURI);
      String[] rel = getUriComponents(relURI);
      StringBuffer result = new StringBuffer("");
      if (rel[2].length() == 0 && rel[0] == null && rel[1] == null && rel[3] == null) {
         return relURI;
      } else if (rel[0] != null) {
         return relURI;
      } else {
         rel[0] = base[0];
         if (rel[1] == null) {
            rel[1] = base[1];
            if (rel[2].length() == 0 || rel[2].length() > 0 && rel[2].charAt(0) != '/') {
               rel[2] = resolvePath(base[2], rel[2]);
            }
         }

         if (rel[0] != null) {
            result.append(rel[0]);
            result.append(':');
         }

         if (rel[1] != null) {
            result.append("//");
            result.append(rel[1]);
         }

         result.append(rel[2]);
         if (rel[3] != null) {
            result.append(rel[3]);
         }

         return result.toString();
      }
   }

   public static String[] getUriComponents(String theUri) {
      String[] uri = new String[4];
      int ix = 0;
      int lastSlash = -1;
      int dots = 3;
      int state = 0;
      StringBuffer buff = new StringBuffer("");

      for(uri[2] = ""; ix <= theUri.length(); ++ix) {
         char ch;
         if (ix < theUri.length()) {
            ch = theUri.charAt(ix);
         } else {
            ch = 0;
         }

         switch(state) {
         case 0:
            switch(ch) {
            case '\u0000':
               uri[3] = buff.toString();
               continue;
            case '#':
            case '?':
               state = 3;
               buff.append(ch);
               continue;
            case '/':
               if (ix == 0) {
                  state = 1;
               } else {
                  state = 2;
               }

               buff.append(ch);
               lastSlash = buff.length();
               dots = 0;
               continue;
            case ':':
               if (ix <= 0) {
                  throw new IllegalArgumentException("invalid scheme");
               }

               uri[0] = buff.toString();
               buff.setLength(0);
               state = 1;
               continue;
            default:
               buff.append(ch);
               if (ix == 0 && ch == '.') {
                  dots = 1;
                  state = 2;
               }
               continue;
            }
         case 1:
            switch(ch) {
            case '\u0000':
               if (buff.length() > 1) {
                  uri[1] = buff.toString().substring(2);
               } else {
                  uri[2] = buff.toString();
               }
               continue;
            case '#':
               if (buff.length() < 2) {
                  uri[2] = buff.toString();
                  state = 3;
                  buff.setLength(0);
                  buff.append(ch);
                  continue;
               }
            default:
               if (buff.length() < 2) {
                  state = 2;
               }

               buff.append(ch);
               continue;
            case '/':
               if (buff.length() > 1) {
                  uri[1] = buff.toString().substring(2);
                  buff.setLength(0);
                  state = 2;
               }

               buff.append(ch);
               lastSlash = buff.length();
               dots = 0;
               continue;
            case '?':
               if (buff.length() > 1) {
                  uri[1] = buff.toString().substring(2);
               }

               buff.setLength(0);
               state = 3;
               buff.append(ch);
               lastSlash = -1;
               continue;
            }
         case 2:
            switch(ch) {
            case '\u0000':
               if (dots < 3 && dots > 0) {
                  if (lastSlash < 0) {
                     lastSlash = 0;
                  }

                  lastSlash += dots;
               }

               if (lastSlash >= 0) {
                  uri[2] = buff.toString();
               } else {
                  lastSlash = 0;
               }

               if (lastSlash < buff.length()) {
               }
               continue;
            case '#':
            case '?':
               if (lastSlash >= 0) {
               }

               uri[2] = buff.toString();
               state = 3;
               buff.setLength(0);
               buff.append(ch);
               continue;
            case '.':
               ++dots;
               buff.append(ch);
               continue;
            case '/':
               buff.append(ch);
               lastSlash = buff.length();
               dots = 0;
               continue;
            default:
               dots = 3;
               buff.append(ch);
               continue;
            }
         case 3:
            switch(ch) {
            case '\u0000':
               uri[3] = buff.toString();
               break;
            case '/':
            default:
               buff.append(ch);
            }
         }
      }

      return uri;
   }

   private static String resolvePath(String basePath, String relPath) {
      StringBuffer absPath = new StringBuffer("");
      int ix = false;
      int ix = basePath.lastIndexOf(47);
      if (ix < 0) {
         throw new IllegalArgumentException("Invalid base URI.");
      } else {
         absPath.append(basePath.substring(0, ix + 1));
         absPath.append(relPath);
         int state = 1;
         int lastSlashIx = absPath.toString().substring(0, ix).lastIndexOf(47);
         ++ix;

         for(; ix < absPath.length(); ++ix) {
            char ch = absPath.charAt(ix);
            int length = absPath.length();
            switch(state) {
            case 1:
               switch(ch) {
               case '.':
                  if (ix + 1 >= length) {
                     absPath.delete(ix, ix + 1);
                     state = 1;
                  } else {
                     state = 2;
                  }
                  continue;
               case '/':
                  throw new IllegalArgumentException("Invalid relative URI.");
               default:
                  state = 4;
                  continue;
               }
            case 2:
               switch(ch) {
               case '.':
                  if (ix + 1 >= length) {
                     if (lastSlashIx >= 0) {
                        absPath.delete(lastSlashIx + 1, ix + 1);
                        ix = lastSlashIx;
                        lastSlashIx = absPath.toString().substring(0, lastSlashIx).lastIndexOf(47);
                     } else {
                        absPath.delete(1, ix + 1);
                        ix = 0;
                     }

                     state = 1;
                  } else {
                     state = 3;
                  }
                  continue;
               case '/':
                  ix -= 2;
                  absPath.delete(ix + 1, ix + 3);
                  state = 1;
                  continue;
               default:
                  state = 4;
                  continue;
               }
            case 3:
               switch(ch) {
               case '.':
               default:
                  state = 4;
                  continue;
               case '/':
                  if (lastSlashIx >= 0) {
                     absPath.delete(lastSlashIx + 1, ix + 1);
                     ix = lastSlashIx;
                     lastSlashIx = absPath.toString().substring(0, lastSlashIx).lastIndexOf(47);
                  } else {
                     absPath.delete(1, ix + 1);
                     ix = 0;
                  }

                  state = 1;
                  continue;
               }
            case 4:
               switch(ch) {
               case '.':
               default:
                  state = 4;
                  continue;
               case '/':
                  state = 1;
                  lastSlashIx = absPath.toString().substring(0, ix).lastIndexOf(47);
                  continue;
               }
            default:
               throw new IllegalArgumentException("Invalid relative URI.");
            }
         }

         return absPath.toString();
      }
   }
}
