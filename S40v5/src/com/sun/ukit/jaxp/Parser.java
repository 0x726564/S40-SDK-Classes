package com.sun.ukit.jaxp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public final class Parser extends SAXParser implements Locator {
   public static final String FAULT = "";
   public static final char EOS = '\uffff';
   private Pair cN;
   private DefaultHandler cO;
   private Hashtable cP;
   private Hashtable cQ;
   private boolean cR;
   private short cS;
   private char cT;
   private char[] cU;
   private short cV;
   private Pair cW;
   private Pair cX;
   private Pair cY;
   private Input cZ;
   private Input da;
   private char[] db;
   private char dc;
   private char dd;
   private Attrs de;
   private String[] df;
   private char dg;
   private Pair dh;
   private static final char[] di;
   private static final char[] dj;
   private static final char[] dk;
   private static final byte[] dl;

   public Parser(boolean var1) {
      this.cR = var1;
      this.cU = new char[128];
      this.de = new Attrs();
      this.cW = this.b(this.cW);
      this.cW.name = "";
      this.cW.value = "";
      this.cW.chars = di;
      this.cW = this.b(this.cW);
      this.cW.name = "xml";
      this.cW.value = "http://www.w3.org/XML/1998/namespace";
      this.cW.chars = dj;
      this.cN = this.cW;
   }

   public final String getPublicId() {
      return this.cZ != null ? this.cZ.eV : null;
   }

   public final String getSystemId() {
      return this.cZ != null ? this.cZ.eW : null;
   }

   public final int getLineNumber() {
      return -1;
   }

   public final int getColumnNumber() {
      return -1;
   }

   public final boolean isNamespaceAware() {
      return this.cR;
   }

   public final boolean isValidating() {
      return false;
   }

   public final void parse(InputStream var1, DefaultHandler var2) throws SAXException, IOException {
      if (var1 != null && var2 != null) {
         this.parse(new InputSource(var1), var2);
      } else {
         throw new IllegalArgumentException("");
      }
   }

   public final void parse(InputSource var1, DefaultHandler var2) throws SAXException, IOException {
      if (var1 != null && var2 != null) {
         this.cO = var2;
         this.cZ = new Input((short)512);
         this.setinp(var1);
         Parser var19 = this;

         try {
            var19.cQ = new Hashtable();
            var19.cP = new Hashtable();
            var19.da = var19.cZ;
            var19.db = var19.cZ.chars;
            var19.cO.setDocumentLocator(var19);
            var19.cO.startDocument();
            var19.cS = 1;

            label856:
            while(true) {
               char var20;
               if ((var20 = var19.next()) == '\uffff') {
                  if (var19.cS != 5) {
                     var19.t("");
                  }
                  break;
               }

               switch(var19.h(var20)) {
               case ' ':
                  break;
               case '<':
                  switch(var19.next()) {
                  case '!':
                     var20 = var19.next();
                     var19.F();
                     if (var20 == '-') {
                        var19.z();
                        continue;
                     } else {
                        Parser var21 = var19;
                        Pair var3 = null;
                        if (!"DOCTYPE".equals(var19.d(false))) {
                           var19.t("");
                        }

                        var19.cS = 2;
                        byte var4 = 0;

                        while(true) {
                           label852:
                           while(var4 >= 0) {
                              char var22 = var21.next();
                              switch(var4) {
                              case 0:
                                 if (var21.h(var22) != ' ') {
                                    var21.F();
                                    var21.d(var21.cR);
                                    var21.B();
                                    var4 = 1;
                                 }
                                 break;
                              case 1:
                                 switch(var21.h(var22)) {
                                 case '>':
                                    var21.F();
                                    var4 = 3;
                                    break;
                                 case 'A':
                                    var21.F();
                                    var3 = var21.a(' ');
                                    var4 = 2;
                                    break;
                                 case '[':
                                    var21.F();
                                    var4 = 2;
                                    break;
                                 default:
                                    var21.t("");
                                 }

                                 if (var3 != null) {
                                    if (var21.cO.resolveEntity(var3.name, var3.value) != null) {
                                       var21.t("");
                                    }

                                    var21.c(var3);
                                    var21.cO.skippedEntity("[dtd]");
                                 }
                                 break;
                              case 2:
                                 switch(var21.h(var22)) {
                                 case ' ':
                                    continue;
                                 case '>':
                                    var21.F();
                                    var4 = 3;
                                    continue;
                                 case '[':
                                    Parser var23 = var21;
                                    byte var6 = 0;

                                    while(true) {
                                       while(var6 >= 0) {
                                          char var5 = var23.next();
                                          switch(var6) {
                                          case 0:
                                             switch(var23.h(var5)) {
                                             case ' ':
                                                continue;
                                             case '%':
                                                var23.d(' ');
                                                continue;
                                             case '<':
                                                switch(var23.next()) {
                                                case '!':
                                                   var5 = var23.next();
                                                   var23.F();
                                                   if (var5 == '-') {
                                                      var23.z();
                                                      continue;
                                                   }

                                                   var23.C();
                                                   String var7;
                                                   Pair var8;
                                                   Parser var24;
                                                   label839:
                                                   switch(var23.D()) {
                                                   case 'a':
                                                      var24 = var23;
                                                      var7 = null;
                                                      var8 = null;
                                                      byte var28 = 0;

                                                      while(true) {
                                                         while(true) {
                                                            while(true) {
                                                               if (var28 < 0) {
                                                                  break label839;
                                                               }

                                                               char var30 = var24.next();
                                                               char[] var31;
                                                               switch(var28) {
                                                               case 0:
                                                                  switch(var24.h(var30)) {
                                                                  case ' ':
                                                                     continue;
                                                                  case '%':
                                                                     var24.d(' ');
                                                                     continue;
                                                                  case ':':
                                                                  case 'A':
                                                                  case 'X':
                                                                  case '_':
                                                                  case 'a':
                                                                     var24.F();
                                                                     var31 = var24.e(var24.cR);
                                                                     if ((var8 = a(var24.cY, var31)) == null) {
                                                                        (var8 = var24.b(var24.cY)).chars = var31;
                                                                        var24.cY = var8;
                                                                     }

                                                                     var28 = 1;
                                                                     continue;
                                                                  default:
                                                                     var24.t("");
                                                                     continue;
                                                                  }
                                                               case 1:
                                                                  switch(var24.h(var30)) {
                                                                  case ' ':
                                                                     continue;
                                                                  case '%':
                                                                     var24.d(' ');
                                                                     continue;
                                                                  case ':':
                                                                  case 'A':
                                                                  case 'X':
                                                                  case '_':
                                                                  case 'a':
                                                                     var24.F();
                                                                     Pair var32 = var8;
                                                                     Parser var29 = var24;
                                                                     var7 = null;
                                                                     Pair var12 = null;
                                                                     byte var26 = 0;

                                                                     while(var26 >= 0) {
                                                                        char var13 = var29.next();
                                                                        switch(var26) {
                                                                        case 0:
                                                                           switch(var29.h(var13)) {
                                                                           case ' ':
                                                                              continue;
                                                                           case '%':
                                                                              var29.d(' ');
                                                                              continue;
                                                                           case ':':
                                                                           case 'A':
                                                                           case 'X':
                                                                           case '_':
                                                                           case 'a':
                                                                              var29.F();
                                                                              var31 = var29.e(var29.cR);
                                                                              if ((var12 = a(var32.ho, var31)) == null) {
                                                                                 (var12 = var29.b(var32.ho)).chars = var31;
                                                                                 var32.ho = var12;
                                                                              } else {
                                                                                 var12.hn = 'c';
                                                                                 if (var12.ho != null) {
                                                                                    var29.c(var12.ho);
                                                                                 }

                                                                                 var12.ho = null;
                                                                              }

                                                                              var29.B();
                                                                              var26 = 1;
                                                                              continue;
                                                                           default:
                                                                              var29.t("");
                                                                              continue;
                                                                           }
                                                                        case 1:
                                                                           switch(var13) {
                                                                           case ' ':
                                                                              continue;
                                                                           case '%':
                                                                              var29.d(' ');
                                                                              continue;
                                                                           case '(':
                                                                              var29.F();
                                                                              var12.hn = 'u';
                                                                              var26 = 2;
                                                                              continue;
                                                                           default:
                                                                              var29.F();
                                                                              var29.C();
                                                                              var12.hn = var29.D();
                                                                              switch(var12.hn) {
                                                                              case 'N':
                                                                              case 'R':
                                                                              case 'T':
                                                                              case 'c':
                                                                              case 'i':
                                                                              case 'n':
                                                                              case 'r':
                                                                              case 't':
                                                                                 var29.B();
                                                                                 var26 = 4;
                                                                                 continue;
                                                                              case 'o':
                                                                                 if (var29.B() != '(') {
                                                                                    var29.t("");
                                                                                 }

                                                                                 var26 = 2;
                                                                                 continue;
                                                                              default:
                                                                                 var29.t("");
                                                                                 continue;
                                                                              }
                                                                           }
                                                                        case 2:
                                                                           if (var13 != '(') {
                                                                              var29.t("");
                                                                           }

                                                                           var13 = var29.B();
                                                                           switch(var29.h(var13)) {
                                                                           case '%':
                                                                              var29.next();
                                                                              var29.d(' ');
                                                                              continue;
                                                                           case '-':
                                                                           case '.':
                                                                           case ':':
                                                                           case 'A':
                                                                           case 'X':
                                                                           case '_':
                                                                           case 'a':
                                                                           case 'd':
                                                                              switch(var12.hn) {
                                                                              case 'o':
                                                                                 var29.cV = -1;
                                                                                 var29.f(false);
                                                                                 break;
                                                                              case 'u':
                                                                                 var29.C();
                                                                                 break;
                                                                              default:
                                                                                 var29.t("");
                                                                              }

                                                                              var29.B();
                                                                              var26 = 3;
                                                                              continue;
                                                                           default:
                                                                              var29.t("");
                                                                              continue;
                                                                           }
                                                                        case 3:
                                                                           switch(var13) {
                                                                           case '%':
                                                                              var29.d(' ');
                                                                              continue;
                                                                           case ')':
                                                                              var29.B();
                                                                              var26 = 4;
                                                                              continue;
                                                                           case '|':
                                                                              var29.B();
                                                                              switch(var12.hn) {
                                                                              case 'o':
                                                                                 var29.cV = -1;
                                                                                 var29.f(false);
                                                                                 break;
                                                                              case 'u':
                                                                                 var29.C();
                                                                                 break;
                                                                              default:
                                                                                 var29.t("");
                                                                              }

                                                                              var29.B();
                                                                              continue;
                                                                           default:
                                                                              var29.t("");
                                                                              continue;
                                                                           }
                                                                        case 4:
                                                                           switch(var13) {
                                                                           case '\t':
                                                                           case '\n':
                                                                           case '\r':
                                                                           case ' ':
                                                                              continue;
                                                                           case '"':
                                                                           case '\'':
                                                                              var29.F();
                                                                              var26 = 5;
                                                                              continue;
                                                                           case '#':
                                                                              var29.C();
                                                                              switch(var29.D()) {
                                                                              case 'F':
                                                                                 switch(var29.B()) {
                                                                                 case '"':
                                                                                 case '\'':
                                                                                    var26 = 5;
                                                                                    continue;
                                                                                 default:
                                                                                    var29.t("");
                                                                                    continue;
                                                                                 }
                                                                              case 'I':
                                                                              case 'Q':
                                                                                 var26 = -1;
                                                                                 continue;
                                                                              default:
                                                                                 var29.t("");
                                                                                 continue;
                                                                              }
                                                                           case '%':
                                                                              var29.d(' ');
                                                                              continue;
                                                                           default:
                                                                              var29.F();
                                                                              var26 = -1;
                                                                              continue;
                                                                           }
                                                                        case 5:
                                                                           switch(var13) {
                                                                           case '"':
                                                                           case '\'':
                                                                              var29.F();
                                                                              var29.e('-');
                                                                              var12.ho = var29.b((Pair)null);
                                                                              var12.ho.chars = new char[var12.chars.length + var29.cV + 3];
                                                                              System.arraycopy(var12.chars, 1, var12.ho.chars, 0, var12.chars.length - 1);
                                                                              var12.ho.chars[var12.chars.length - 1] = '=';
                                                                              var12.ho.chars[var12.chars.length] = var13;
                                                                              System.arraycopy(var29.cU, 1, var12.ho.chars, var12.chars.length + 1, var29.cV);
                                                                              var12.ho.chars[var12.chars.length + var29.cV + 1] = var13;
                                                                              var12.ho.chars[var12.chars.length + var29.cV + 2] = ' ';
                                                                              var26 = -1;
                                                                              continue;
                                                                           default:
                                                                              var29.t("");
                                                                              continue;
                                                                           }
                                                                        default:
                                                                           var29.t("");
                                                                        }
                                                                     }

                                                                     if (var24.B() == '>') {
                                                                        break label839;
                                                                     }
                                                                     continue;
                                                                  default:
                                                                     var24.t("");
                                                                     continue;
                                                                  }
                                                               default:
                                                                  var24.t("");
                                                               }
                                                            }
                                                         }
                                                      }
                                                   case 'e':
                                                      var24 = var23;
                                                      var23.B();
                                                      var23.d(var23.cR);

                                                      while(true) {
                                                         while(true) {
                                                            switch(var24.next()) {
                                                            case '#':
                                                               if (!"PCDATA".equals(var24.d(false))) {
                                                                  var24.t("");
                                                               }
                                                               break;
                                                            case '>':
                                                               var24.F();
                                                               break label839;
                                                            case '\uffff':
                                                               var24.t("");
                                                            }
                                                         }
                                                      }
                                                   case 'n':
                                                      var24 = var23;
                                                      var7 = null;
                                                      var8 = null;
                                                      Input var9 = null;
                                                      Pair var10 = null;
                                                      byte var27 = 0;

                                                      while(true) {
                                                         while(true) {
                                                            while(true) {
                                                               if (var27 < 0) {
                                                                  break label839;
                                                               }

                                                               char var11 = var24.next();
                                                               char[] var25;
                                                               switch(var27) {
                                                               case 0:
                                                                  switch(var24.h(var11)) {
                                                                  case ' ':
                                                                     continue;
                                                                  case '%':
                                                                     var11 = var24.next();
                                                                     var24.F();
                                                                     if (var24.h(var11) == ' ') {
                                                                        var24.B();
                                                                        var7 = var24.d(false);
                                                                        switch(var24.h(var24.B())) {
                                                                        case '"':
                                                                        case '\'':
                                                                           var24.e('-');
                                                                           var25 = new char[var24.cV + 1];
                                                                           System.arraycopy(var24.cU, 1, var25, 1, var25.length - 1);
                                                                           var25[0] = ' ';
                                                                           (var9 = new Input(var25)).eV = var24.cZ.eV;
                                                                           var9.eW = var24.cZ.eW;
                                                                           var24.cQ.put(var7, var9);
                                                                           var27 = -1;
                                                                           continue;
                                                                        case 'A':
                                                                           var10 = var24.a(' ');
                                                                           if (var24.B() == '>') {
                                                                              (var9 = new Input()).eV = var10.name;
                                                                              var9.eW = var10.value;
                                                                              var24.cQ.put(var7, var9);
                                                                           } else {
                                                                              var24.t("");
                                                                           }

                                                                           var24.c(var10);
                                                                           var27 = -1;
                                                                           continue;
                                                                        default:
                                                                           var24.t("");
                                                                        }
                                                                     } else {
                                                                        var24.d(' ');
                                                                     }
                                                                     continue;
                                                                  default:
                                                                     var24.F();
                                                                     var7 = var24.d(false);
                                                                     var27 = 1;
                                                                     continue;
                                                                  }
                                                               case 1:
                                                                  switch(var24.h(var11)) {
                                                                  case ' ':
                                                                     continue;
                                                                  case '"':
                                                                  case '\'':
                                                                     var24.F();
                                                                     var24.e('-');
                                                                     if (var24.cP.get(var7) == null) {
                                                                        var25 = new char[var24.cV];
                                                                        System.arraycopy(var24.cU, 1, var25, 0, var25.length);
                                                                        (var9 = new Input(var25)).eV = var24.cZ.eV;
                                                                        var9.eW = var24.cZ.eW;
                                                                        var24.cP.put(var7, var9);
                                                                     }

                                                                     var27 = -1;
                                                                     continue;
                                                                  case 'A':
                                                                     var24.F();
                                                                     var10 = var24.a(' ');
                                                                     switch(var24.B()) {
                                                                     case '>':
                                                                        (var9 = new Input()).eV = var10.name;
                                                                        var9.eW = var10.value;
                                                                        var24.cP.put(var7, var9);
                                                                        break;
                                                                     case 'N':
                                                                        if ("NDATA".equals(var24.d(false))) {
                                                                           var24.B();
                                                                           var24.cO.unparsedEntityDecl(var7, var10.name, var10.value, var24.d(false));
                                                                           break;
                                                                        }
                                                                     default:
                                                                        var24.t("");
                                                                     }

                                                                     var24.c(var10);
                                                                     var27 = -1;
                                                                     continue;
                                                                  default:
                                                                     var24.t("");
                                                                     continue;
                                                                  }
                                                               default:
                                                                  var24.t("");
                                                               }
                                                            }
                                                         }
                                                      }
                                                   case 'o':
                                                      var23.B();
                                                      var7 = var23.d(false);
                                                      var23.B();
                                                      var8 = var23.a('N');
                                                      var23.cO.notationDecl(var7, var8.name, var8.value);
                                                      var23.c(var8);
                                                      break;
                                                   default:
                                                      var23.t("");
                                                   }

                                                   var6 = 1;
                                                   continue;
                                                case '?':
                                                   var23.A();
                                                   continue;
                                                default:
                                                   var23.t("");
                                                   continue;
                                                }
                                             case ']':
                                                var6 = -1;
                                                continue;
                                             default:
                                                var23.t("");
                                                continue;
                                             }
                                          case 1:
                                             switch(var5) {
                                             case '\t':
                                             case '\n':
                                             case '\r':
                                             case ' ':
                                                continue;
                                             case '>':
                                                var6 = 0;
                                                continue;
                                             default:
                                                var23.t("");
                                                continue;
                                             }
                                          default:
                                             var23.t("");
                                          }
                                       }

                                       var4 = 3;
                                       continue label852;
                                    }
                                 default:
                                    var21.t("");
                                    continue;
                                 }
                              case 3:
                                 switch(var21.h(var22)) {
                                 case ' ':
                                    continue;
                                 case '>':
                                    var4 = -1;
                                    continue;
                                 default:
                                    var21.t("");
                                    continue;
                                 }
                              default:
                                 var21.t("");
                              }
                           }

                           var21.cS = 3;
                           continue label856;
                        }
                     }
                  case '?':
                     var19.A();
                     continue;
                  default:
                     if (var19.cS == 5) {
                        var19.t("");
                     }

                     var19.F();
                     var19.cS = 4;
                     var19.y();
                     var19.cS = 5;
                     continue;
                  }
               default:
                  var19.t("");
               }
            }
         } finally {
            var19.cO.endDocument();

            while(var19.cY != null) {
               for(; var19.cY.ho != null; var19.cY.ho = var19.c(var19.cY.ho)) {
                  if (var19.cY.ho.ho != null) {
                     var19.c(var19.cY.ho.ho);
                  }
               }

               var19.cY = var19.c(var19.cY);
            }

            while(var19.cX != null) {
               var19.cX = var19.c(var19.cX);
            }

            while(var19.cW != var19.cN) {
               var19.cW = var19.c(var19.cW);
            }

            while(var19.cZ != null) {
               var19.pop();
            }

            if (var19.da != null && var19.da.eX != null) {
               try {
                  var19.da.eX.close();
               } catch (IOException var17) {
               }
            }

            var19.cQ = null;
            var19.cP = null;
            var19.da = null;
            var19.cO = null;
         }

      } else {
         throw new IllegalArgumentException("");
      }
   }

   private void y() throws SAXException, IOException {
      Pair var1 = this.cW;
      this.cX = this.b(this.cX);
      this.cX.chars = this.e(this.cR);
      this.cX.name = this.cX.U();
      Pair var2 = a(this.cY, this.cX.chars);
      this.dg = 0;
      Pair var3;
      (var3 = this.b((Pair)null)).ho = var2 != null ? var2.ho : null;
      this.a(var3);
      this.c(var3);
      this.cV = -1;
      int var8 = 0;

      while(true) {
         while(true) {
            while(var8 >= 0) {
               char var7 = this.next();
               switch(var8) {
               case 0:
               case 1:
                  switch(var7) {
                  case '/':
                     if (var8 != 0) {
                        this.t("");
                     }

                     var8 = 1;
                     continue;
                  case '>':
                     if (this.cR) {
                        this.cX.value = this.a(this.cX.chars);
                        this.cO.startElement(this.cX.value, this.cX.name, "", this.de);
                     } else {
                        this.cO.startElement("", "", this.cX.name, this.de);
                     }

                     this.df = null;
                     var8 = var8 == 0 ? 2 : -1;
                     continue;
                  default:
                     this.t("");
                     continue;
                  }
               case 2:
                  switch(this.h(var7)) {
                  case ' ':
                     this.f(var7);
                     continue;
                  case '<':
                     this.E();
                  default:
                     this.F();
                     var8 = 3;
                     continue;
                  }
               case 3:
                  switch(var7) {
                  case '\r':
                     if (this.next() != '\n') {
                        this.F();
                     }

                     this.f('\n');
                     continue;
                  case '&':
                     this.c('c');
                     continue;
                  case '<':
                     this.E();
                     label167:
                     switch(this.next()) {
                     case '!':
                        var7 = this.next();
                        this.F();
                        switch(var7) {
                        case '-':
                           this.z();
                           break label167;
                        case '[':
                           Parser var10 = this;
                           this.cV = -1;
                           byte var6 = 0;

                           while(true) {
                              if (var6 < 0) {
                                 break label167;
                              }

                              char var5 = var10.next();
                              switch(var6) {
                              case 0:
                                 if (var5 == '[') {
                                    var6 = 1;
                                 } else {
                                    var10.t("");
                                 }
                                 break;
                              case 1:
                                 if (var10.h(var5) == 'A') {
                                    var10.f(var5);
                                 } else {
                                    if (!"CDATA".equals(new String(var10.cU, 0, var10.cV + 1))) {
                                       var10.t("");
                                    }

                                    var10.F();
                                    var6 = 2;
                                 }
                                 break;
                              case 2:
                                 if (var5 != '[') {
                                    var10.t("");
                                 }

                                 var10.cV = -1;
                                 var6 = 3;
                                 break;
                              case 3:
                                 if (var5 == '\uffff') {
                                    var10.t("");
                                 } else if (var5 != ']') {
                                    var10.f(var5);
                                 } else {
                                    var6 = 4;
                                 }
                                 break;
                              case 4:
                                 if (var5 == '\uffff') {
                                    var10.t("");
                                 } else if (var5 != ']') {
                                    var10.f(']');
                                    var10.f(var5);
                                    var6 = 3;
                                 } else {
                                    var6 = 5;
                                 }
                                 break;
                              case 5:
                                 if (var5 == '\uffff') {
                                    var10.t("");
                                 } else if (var5 != '>') {
                                    var10.f(']');
                                    var10.f(']');
                                    var10.f(var5);
                                    var6 = 3;
                                 } else {
                                    var10.E();
                                    var6 = -1;
                                 }
                                 break;
                              default:
                                 var10.t("");
                              }
                           }
                        default:
                           this.t("");
                           break label167;
                        }
                     case '/':
                        this.cV = -1;
                        this.f(this.cR);
                        char[] var9;
                        if ((var9 = this.cX.chars).length == this.cV + 1) {
                           for(char var4 = 1; var4 <= this.cV; ++var4) {
                              if (var9[var4] != this.cU[var4]) {
                                 this.t("");
                              }
                           }
                        } else {
                           this.t("");
                        }

                        if (this.B() != '>') {
                           this.t("");
                        }

                        this.next();
                        var8 = -1;
                        break;
                     case '?':
                        this.A();
                        break;
                     default:
                        this.F();
                        this.y();
                     }

                     this.cV = -1;
                     if (var8 != -1) {
                        var8 = 2;
                     }
                     continue;
                  case '?':
                     if (this.next() == '>') {
                        this.t("");
                     } else {
                        this.F();
                     }
                     continue;
                  case ']':
                     if (this.next() == ']') {
                        if (this.next() == '>') {
                           this.t("");
                        } else {
                           this.F();
                        }
                     } else {
                        this.F();
                     }
                     continue;
                  default:
                     this.f(var7);
                     continue;
                  }
               default:
                  this.t("");
               }
            }

            if (this.cR) {
               this.cO.endElement(this.cX.value, this.cX.name, "");
            } else {
               this.cO.endElement("", "", this.cX.name);
            }

            for(this.cX = this.c(this.cX); this.cW != var1; this.cW = this.c(this.cW)) {
               this.cO.endPrefixMapping(this.cW.name);
            }

            return;
         }
      }
   }

   private void a(Pair var1) throws SAXException, IOException {
      Pair var2 = null;
      char var3 = 'c';

      int var8;
      boolean var10000;
      try {
         label274: {
            Pair var5;
            switch(this.B()) {
            case '/':
            case '>':
               for(var5 = var1.ho; var5 != null; var5 = var5.hp) {
                  if (var5.ho != null) {
                     Pair var4;
                     for(var4 = var1.hp; var4 != null && !var4.c(var5.chars); var4 = var4.hp) {
                     }

                     if (var4 == null) {
                        this.a(new Input(var5.ho.chars));
                        this.a(var1);
                        return;
                     }
                  }
               }

               this.de.setLength(this.dg);
               this.df = this.de.df;
               return;
            }

            var1.chars = this.e(this.cR);
            var1.name = var1.U();
            String var12 = "CDATA";
            if (var1.ho != null && (var5 = a(var1.ho, var1.chars)) != null) {
               switch(var5.hn) {
               case 'N':
                  var12 = "ENTITIES";
                  var3 = 'i';
                  break;
               case 'O':
               case 'P':
               case 'Q':
               case 'S':
               case 'U':
               case 'V':
               case 'W':
               case 'X':
               case 'Y':
               case 'Z':
               case '[':
               case '\\':
               case ']':
               case '^':
               case '_':
               case '`':
               case 'a':
               case 'b':
               case 'd':
               case 'e':
               case 'f':
               case 'g':
               case 'h':
               case 'j':
               case 'k':
               case 'l':
               case 'm':
               case 'p':
               case 'q':
               case 's':
               default:
                  this.t("");
                  break;
               case 'R':
                  var12 = "IDREFS";
                  var3 = 'i';
                  break;
               case 'T':
                  var12 = "NMTOKENS";
                  var3 = 'i';
                  break;
               case 'c':
                  var3 = 'c';
                  break;
               case 'i':
                  var12 = "ID";
                  var3 = 'i';
                  break;
               case 'n':
                  var12 = "ENTITY";
                  var3 = 'i';
                  break;
               case 'o':
                  var12 = "NOTATION";
                  var3 = 'i';
                  break;
               case 'r':
                  var12 = "IDREF";
                  var3 = 'i';
                  break;
               case 't':
                  var12 = "NMTOKEN";
                  var3 = 'i';
                  break;
               case 'u':
                  var12 = "NMTOKEN";
                  var3 = 'i';
               }
            }

            this.B();
            if (this.next() != '=') {
               this.t("");
            }

            this.e(var3);
            String var15 = new String(this.cU, 1, this.cV);
            if (this.cR) {
               label255: {
                  if (var1.chars[0] == 0) {
                     if ("xmlns".equals(var1.name)) {
                        this.cW = this.b(this.cW);
                        this.cW.value = var15;
                        this.cW.name = "";
                        this.cW.chars = di;
                        var10000 = true;
                        break label255;
                     }
                  } else if (var1.b(dk)) {
                     var8 = var1.name.length();
                     this.cW = this.b(this.cW);
                     this.cW.value = var15;
                     this.cW.name = var1.name;
                     this.cW.chars = new char[var8 + 1];
                     this.cW.chars[0] = (char)(var8 + 1);
                     var1.name.getChars(0, var8, this.cW.chars, 1);
                     var10000 = true;
                     break label255;
                  }

                  var10000 = false;
               }

               if (var10000) {
                  this.cO.startPrefixMapping(this.cW.name, this.cW.value);
                  (var2 = this.b(var1)).ho = var1.ho;
                  this.a(var2);
                  break label274;
               }
            }

            ++this.dg;
            (var2 = this.b(var1)).ho = var1.ho;
            this.a(var2);
            --this.dg;
            char var13 = (char)(this.dg << 3);
            this.df[var13 + 1] = new String(var1.chars, 1, var1.chars.length - 1);
            this.df[var13 + 2] = var1.name;
            this.df[var13 + 3] = var15;
            this.df[var13 + 4] = var12;
            this.df[var13] = var1.chars[0] != 0 ? this.a(var1.chars) : "";
         }
      } finally {
         if (var2 != null) {
            this.c(var2);
         }

      }

      Attrs var6 = this.de;
      int var14 = 0;

      label246:
      while(true) {
         if (var14 >= var6.getLength()) {
            var10000 = false;
            break;
         }

         String var7 = var6.getQName(var14);

         for(var8 = var14 + 1; var8 < var6.getLength(); ++var8) {
            String var11 = var6.getQName(var8);
            if (var7 != null && var11 != null && var7.equals(var11)) {
               var10000 = true;
               break label246;
            }
         }

         ++var14;
      }

      if (var10000) {
         this.t("");
      }

   }

   private void z() throws SAXException, IOException {
      if (this.cS == 0) {
         this.cS = 1;
      }

      int var2 = 0;

      while(var2 >= 0) {
         char var1 = this.next();
         switch(var2) {
         case 0:
            if (var1 == '-') {
               var2 = 1;
            } else {
               this.t("");
            }
            break;
         case 1:
            if (var1 == '-') {
               var2 = 2;
            } else {
               this.t("");
            }
            break;
         case 2:
            if (var1 == '-') {
               var2 = 3;
            } else if (var1 == '\uffff') {
               this.t("");
            }
            break;
         case 3:
            var2 = var1 == '-' ? 4 : 2;
            break;
         case 4:
            if (var1 == '>') {
               var2 = -1;
            } else {
               this.t("");
            }
            break;
         default:
            this.t("");
         }
      }

   }

   private void A() throws SAXException, IOException {
      String var2 = null;
      this.cV = -1;
      byte var3 = 0;

      while(var3 >= 0) {
         char var1 = this.next();
         switch(var3) {
         case 0:
            switch(this.h(var1)) {
            case ' ':
               var2 = "";
               var3 = 2;
               continue;
            case 'A':
            case 'X':
            case '_':
            case 'a':
               this.F();
               var2 = this.d(false);
               var3 = 1;
               continue;
            default:
               this.t("");
               continue;
            }
         case 1:
            if (this.h(var1) != ' ') {
               this.F();
               if (this.cN.name.equals(var2.toLowerCase())) {
                  this.t("");
               } else if (var1 == '=') {
                  this.t("");
               } else {
                  if (this.cS == 0) {
                     this.cS = 1;
                  }

                  var3 = 2;
               }

               this.cV = -1;
            }
            break;
         case 2:
            if (var1 == '?') {
               var3 = 3;
            } else if (var1 == '\uffff') {
               this.t("");
            } else {
               this.f(var1);
            }
            break;
         case 3:
            if (var1 == '>') {
               this.cO.processingInstruction(var2, new String(this.cU, 0, this.cV + 1));
               var3 = -1;
            } else {
               this.f('?');
               this.f(var1);
               var3 = 2;
            }
            break;
         default:
            this.t("");
         }
      }

   }

   private String d(boolean var1) throws SAXException, IOException {
      this.cV = -1;
      this.f(var1);
      return new String(this.cU, 1, this.cV);
   }

   private char[] e(boolean var1) throws SAXException, IOException {
      this.cV = -1;
      this.f(var1);
      char[] var2 = new char[this.cV + 1];
      System.arraycopy(this.cU, 0, var2, 0, this.cV + 1);
      return var2;
   }

   private Pair a(char var1) throws SAXException, IOException {
      Pair var2 = this.b((Pair)null);
      String var3 = this.d(false);
      if ("PUBLIC".equals(var3)) {
         this.e('i');
         var2.name = new String(this.cU, 1, this.cV);
         switch(this.B()) {
         case '"':
         case '\'':
            this.e(' ');
            var2.value = new String(this.cU, 1, this.cV);
            break;
         default:
            if (var1 != 'N') {
               this.t("");
            }

            var2.value = null;
         }

         return var2;
      } else if ("SYSTEM".equals(var3)) {
         var2.name = null;
         this.e(' ');
         var2.value = new String(this.cU, 1, this.cV);
         return var2;
      } else {
         this.t("");
         return null;
      }
   }

   private String b(char var1) throws SAXException, IOException {
      this.B();
      if (this.next() != '=') {
         this.t("");
      }

      this.e('=');
      return new String(this.cU, 1, this.cV);
   }

   private void c(char var1) throws SAXException, IOException {
      short var3 = (short)(this.cV + 1);
      Input var4 = null;
      String var5 = null;
      this.cT = 256;
      this.f('&');
      byte var9 = 0;

      while(true) {
         while(var9 >= 0) {
            char var2 = this.next();
            switch(var9) {
            case 0:
            case 1:
               switch(this.h(var2)) {
               case '#':
                  if (var9 != 0) {
                     this.t("");
                  }

                  var9 = 2;
                  continue;
               case '-':
               case '.':
               case 'd':
                  if (var9 != 1) {
                     this.t("");
                  }
               case 'A':
               case 'X':
               case '_':
               case 'a':
                  this.f(var2);
                  this.g(var2);
                  var9 = 1;
                  continue;
               case ':':
                  if (this.cR) {
                     this.t("");
                  }

                  this.f(var2);
                  this.g(var2);
                  var9 = 1;
                  continue;
               case ';':
                  if (this.cT < 256) {
                     this.cV = (short)(var3 - 1);
                     this.f(this.cT);
                     var9 = -1;
                  } else if (this.cS == 2) {
                     this.f(';');
                     var9 = -1;
                  } else {
                     var5 = new String(this.cU, var3 + 1, this.cV - var3);
                     var4 = (Input)this.cP.get(var5);
                     this.cV = (short)(var3 - 1);
                     if (var4 != null) {
                        if (var4.chars == null) {
                           InputSource var6;
                           if ((var6 = this.cO.resolveEntity(var4.eV, var4.eW)) != null) {
                              this.a(new Input((short)512));
                              this.setinp(var6);
                              this.cZ.eV = var4.eV;
                              this.cZ.eW = var4.eW;
                           } else {
                              this.E();
                              if (var1 != 'c') {
                                 this.t("");
                              }

                              this.cO.skippedEntity(var5);
                           }
                        } else {
                           this.a(var4);
                        }
                     } else {
                        this.E();
                        if (var1 != 'c') {
                           this.t("");
                        }

                        this.cO.skippedEntity(var5);
                     }

                     var9 = -1;
                  }
                  continue;
               default:
                  this.t("");
                  continue;
               }
            case 2:
               switch(this.h(var2)) {
               case ';':
                  try {
                     var2 = (char)Short.parseShort(new String(this.cU, var3 + 1, this.cV - var3), 10);
                  } catch (NumberFormatException var8) {
                     this.t("");
                  }

                  this.cV = (short)(var3 - 1);
                  this.f(var2);
                  var9 = -1;
                  continue;
               case 'a':
                  if (this.cV == var3 && var2 == 'x') {
                     var9 = 3;
                     continue;
                  }
               default:
                  this.t("");
                  continue;
               case 'd':
                  this.f(var2);
                  continue;
               }
            case 3:
               switch(this.h(var2)) {
               case ';':
                  try {
                     var2 = (char)Short.parseShort(new String(this.cU, var3 + 1, this.cV - var3), 16);
                  } catch (NumberFormatException var7) {
                     this.t("");
                  }

                  this.cV = (short)(var3 - 1);
                  this.f(var2);
                  var9 = -1;
                  continue;
               case 'A':
               case 'a':
               case 'd':
                  this.f(var2);
                  continue;
               default:
                  this.t("");
                  continue;
               }
            default:
               this.t("");
            }
         }

         return;
      }
   }

   private void d(char var1) throws SAXException, IOException {
      short var2 = (short)(this.cV + 1);
      Input var3 = null;
      String var4 = null;
      this.f('%');
      if (this.cS == 2) {
         this.f(false);
         var4 = new String(this.cU, var2 + 2, this.cV - var2 - 1);
         if (this.next() != ';') {
            this.t("");
         }

         var3 = (Input)this.cQ.get(var4);
         this.cV = (short)(var2 - 1);
         if (var3 != null) {
            if (var3.chars != null) {
               if (var1 == '-') {
                  var3.eZ = 1;
               } else {
                  this.f(' ');
                  var3.eZ = 0;
               }

               this.a(var3);
               return;
            }

            InputSource var5;
            if ((var5 = this.cO.resolveEntity(var3.eV, var3.eW)) == null) {
               this.cO.skippedEntity("%" + var4);
               return;
            }

            if (var1 != '-') {
               this.f(' ');
            }

            this.a(new Input((short)512));
            this.setinp(var5);
            this.cZ.eV = var3.eV;
            this.cZ.eW = var3.eW;
         } else {
            this.cO.skippedEntity("%" + var4);
         }

      }
   }

   private String a(char[] var1) throws SAXException {
      for(Pair var2 = this.cW; var2 != null; var2 = var2.hp) {
         if (var2.b(var1)) {
            return var2.value;
         }
      }

      this.t("");
      return null;
   }

   private char B() throws SAXException, IOException {
      while(true) {
         char var1;
         switch(var1 = this.next()) {
         case '\t':
         case '\n':
         case '\r':
         case ' ':
            break;
         case '\uffff':
            this.t("");
         default:
            this.F();
            return var1;
         }
      }
   }

   private void t(String var1) throws SAXException {
      SAXParseException var2 = new SAXParseException(var1, this);
      this.cO.fatalError(var2);
   }

   private void f(boolean var1) throws SAXException, IOException {
      char var3;
      char var4 = var3 = (char)(this.cV + 1);
      short var5 = (short)(var1 ? 0 : 2);
      this.f('\u0000');

      while(var5 >= 0) {
         char var2 = this.next();
         switch(var5) {
         case 0:
         case 2:
            switch(this.h(var2)) {
            case ':':
               this.F();
               ++var5;
               continue;
            case 'A':
            case 'X':
            case '_':
            case 'a':
               this.f(var2);
               ++var5;
               continue;
            default:
               this.t("");
               continue;
            }
         case 1:
         case 3:
            switch(this.h(var2)) {
            case '-':
            case '.':
            case 'A':
            case 'X':
            case '_':
            case 'a':
            case 'd':
               this.f(var2);
               continue;
            case ':':
               this.f(var2);
               if (var1) {
                  if (var4 != var3) {
                     this.t("");
                  }

                  var4 = (char)this.cV;
                  if (var5 == 1) {
                     var5 = 2;
                  }
               }
               continue;
            default:
               this.F();
               this.cU[var3] = var4;
               return;
            }
         default:
            this.t("");
         }
      }

   }

   private void C() throws SAXException, IOException {
      this.cV = -1;
      this.f('\u0000');

      while(true) {
         char var1 = this.next();
         switch(this.h(var1)) {
         case '-':
         case '.':
         case ':':
         case 'A':
         case 'X':
         case '_':
         case 'a':
         case 'd':
            this.f(var1);
            break;
         default:
            this.F();
            return;
         }
      }
   }

   private char D() throws SAXException, IOException {
      String var1;
      switch((var1 = new String(this.cU, 1, this.cV)).length()) {
      case 2:
         if ("ID".equals(var1)) {
            return 'i';
         }

         return '?';
      case 3:
      case 4:
      default:
         break;
      case 5:
         switch(this.cU[1]) {
         case 'C':
            if ("CDATA".equals(var1)) {
               return 'c';
            }

            return '?';
         case 'F':
            if ("FIXED".equals(var1)) {
               return 'F';
            }

            return '?';
         case 'I':
            if ("IDREF".equals(var1)) {
               return 'r';
            }

            return '?';
         default:
            return '?';
         }
      case 6:
         switch(this.cU[1]) {
         case 'E':
            if ("ENTITY".equals(var1)) {
               return 'n';
            }

            return '?';
         case 'I':
            if ("IDREFS".equals(var1)) {
               return 'R';
            }

            return '?';
         default:
            return '?';
         }
      case 7:
         switch(this.cU[1]) {
         case 'A':
            if ("ATTLIST".equals(var1)) {
               return 'a';
            }

            return '?';
         case 'E':
            if ("ELEMENT".equals(var1)) {
               return 'e';
            }

            return '?';
         case 'I':
            if ("IMPLIED".equals(var1)) {
               return 'I';
            }

            return '?';
         case 'N':
            if ("NMTOKEN".equals(var1)) {
               return 't';
            }

            return '?';
         default:
            return '?';
         }
      case 8:
         switch(this.cU[2]) {
         case 'E':
            if ("REQUIRED".equals(var1)) {
               return 'Q';
            }

            return '?';
         case 'M':
            if ("NMTOKENS".equals(var1)) {
               return 'T';
            }

            return '?';
         case 'N':
            if ("ENTITIES".equals(var1)) {
               return 'N';
            }

            return '?';
         case 'O':
            if ("NOTATION".equals(var1)) {
               return 'o';
            }

            return '?';
         }
      }

      return '?';
   }

   private void e(char var1) throws SAXException, IOException {
      Input var2 = this.cZ;
      this.cV = -1;
      this.f('\u0000');
      byte var4 = 0;

      while(true) {
         while(true) {
            while(var4 >= 0) {
               char var3 = this.next();
               switch(var4) {
               case 0:
                  switch(var3) {
                  case '\t':
                  case '\n':
                  case '\r':
                  case ' ':
                     continue;
                  case '"':
                     var4 = 3;
                     continue;
                  case '\'':
                     var4 = 2;
                     continue;
                  default:
                     this.t("");
                     continue;
                  }
               case 1:
               default:
                  this.t("");
                  break;
               case 2:
               case 3:
                  switch(var3) {
                  case '\r':
                     if (var1 != ' ') {
                        if (this.next() != '\n') {
                           this.F();
                        }

                        var3 = '\n';
                     }
                  default:
                     switch(var1) {
                     case 'c':
                        switch(var3) {
                        case '\t':
                        case '\n':
                           this.f(' ');
                           continue;
                        default:
                           this.f(var3);
                           continue;
                        }
                     case 'i':
                        switch(var3) {
                        case '\t':
                        case '\n':
                        case ' ':
                           if (this.cV > 0 && this.cU[this.cV] != ' ') {
                              this.f(' ');
                           }
                           continue;
                        default:
                           this.f(var3);
                           continue;
                        }
                     default:
                        this.f(var3);
                        continue;
                     }
                  case '"':
                     if (var4 == 3 && this.cZ == var2) {
                        var4 = -1;
                        break;
                     }

                     this.f(var3);
                     break;
                  case '%':
                     this.d(var1);
                     break;
                  case '&':
                     this.c(' ');
                     break;
                  case '\'':
                     if (var4 == 2 && this.cZ == var2) {
                        var4 = -1;
                        break;
                     }

                     this.f(var3);
                     break;
                  case '\uffff':
                     this.t("");
                  }
               }
            }

            if (var1 == 'i' && this.cU[this.cV] == ' ') {
               --this.cV;
            }

            return;
         }
      }
   }

   private void E() throws SAXException {
      if (this.cV >= 0) {
         this.cO.characters(this.cU, 0, this.cV + 1);
         this.cV = -1;
      }

   }

   private void f(char var1) {
      try {
         ++this.cV;
         this.cU[this.cV] = var1;
      } catch (Exception var3) {
         char[] var2 = new char[this.cU.length << 1];
         System.arraycopy(this.cU, 0, var2, 0, this.cU.length);
         this.cU = var2;
         this.cU[this.cV] = var1;
      }
   }

   private void g(char var1) {
      switch(this.cT) {
      case '"':
      case '&':
      case '\'':
      case '<':
      case '>':
         this.cT = 512;
      default:
         return;
      case 'Ā':
         switch(var1) {
         case 'a':
            this.cT = 259;
            return;
         case 'g':
            this.cT = 258;
            return;
         case 'l':
            this.cT = 257;
            return;
         case 'q':
            this.cT = 263;
            return;
         default:
            this.cT = 512;
            return;
         }
      case 'ā':
         this.cT = (char)(var1 == 't' ? 60 : 512);
         return;
      case 'Ă':
         this.cT = (char)(var1 == 't' ? 62 : 512);
         return;
      case 'ă':
         switch(var1) {
         case 'm':
            this.cT = 260;
            return;
         case 'p':
            this.cT = 261;
            return;
         default:
            this.cT = 512;
            return;
         }
      case 'Ą':
         this.cT = (char)(var1 == 'p' ? 38 : 512);
         return;
      case 'ą':
         this.cT = (char)(var1 == 'o' ? 262 : 512);
         return;
      case 'Ć':
         this.cT = (char)(var1 == 's' ? 39 : 512);
         return;
      case 'ć':
         this.cT = (char)(var1 == 'u' ? 264 : 512);
         return;
      case 'Ĉ':
         this.cT = (char)(var1 == 'o' ? 265 : 512);
         return;
      case 'ĉ':
         this.cT = (char)(var1 == 't' ? 34 : 512);
      }
   }

   private void setinp(InputSource var1) throws SAXException, IOException {
      Reader var2 = null;
      this.dd = 0;
      this.dc = 0;
      this.db = this.cZ.chars;
      this.cZ.eX = null;
      if (var1.getCharacterStream() != null) {
         var2 = var1.getCharacterStream();
         this.a(var2);
      } else if (var1.getByteStream() != null) {
         if (var1.getEncoding() != null) {
            String var3;
            if ((var3 = var1.getEncoding().toUpperCase()).equals("UTF-16")) {
               var2 = this.a(var1.getByteStream(), 'U');
            } else {
               var2 = a(var3, var1.getByteStream());
            }

            this.a(var2);
         } else if ((var2 = this.a(var1.getByteStream(), ' ')) == null) {
            var2 = a("UTF-8", var1.getByteStream());
            var2 = a(this.a(var2), var1.getByteStream());
         } else {
            this.a(var2);
         }
      } else {
         this.t("");
      }

      this.cZ.eX = var2;
      this.cZ.eV = var1.getPublicId();
      this.cZ.eW = var1.getSystemId();
   }

   private Reader a(InputStream var1, char var2) throws SAXException, IOException {
      char[] var10000;
      char var10003;
      int var3;
      switch(var3 = var1.read()) {
      case -1:
         var10000 = this.db;
         var10003 = this.dd;
         this.dd = (char)(var10003 + 1);
         var10000[var10003] = '\uffff';
         return new ReaderUTF8(var1);
      case 239:
         if (var2 == 'U') {
            this.t("");
         }

         if (var1.read() != 187) {
            this.t("");
         }

         if (var1.read() != 191) {
            this.t("");
         }

         return new ReaderUTF8(var1);
      case 254:
         if (var1.read() != 255) {
            this.t("");
         }

         return new ReaderUTF16(var1, 'b');
      case 255:
         if (var1.read() != 254) {
            this.t("");
         }

         return new ReaderUTF16(var1, 'l');
      default:
         if (var2 == 'U') {
            this.t("");
         }

         switch(var3 & 240) {
         case 192:
         case 208:
            var10000 = this.db;
            var10003 = this.dd;
            this.dd = (char)(var10003 + 1);
            var10000[var10003] = (char)((var3 & 31) << 6 | var1.read() & 63);
            break;
         case 224:
            var10000 = this.db;
            var10003 = this.dd;
            this.dd = (char)(var10003 + 1);
            var10000[var10003] = (char)((var3 & 15) << 12 | (var1.read() & 63) << 6 | var1.read() & 63);
            break;
         case 240:
            throw new UnsupportedEncodingException();
         default:
            var10000 = this.db;
            var10003 = this.dd;
            this.dd = (char)(var10003 + 1);
            var10000[var10003] = (char)var3;
         }

         return null;
      }
   }

   private String a(Reader var1) throws SAXException, IOException {
      String var2 = null;
      String var3 = "UTF-8";
      short var5;
      if (this.dd != 0) {
         var5 = (short)(this.db[0] == '<' ? 1 : -1);
      } else {
         var5 = 0;
      }

      while(true) {
         char var4;
         while(var5 >= 0) {
            int var6;
            var4 = (var6 = var1.read()) >= 0 ? (char)var6 : '\uffff';
            char[] var10000 = this.db;
            char var10003 = this.dd;
            this.dd = (char)(var10003 + 1);
            var10000[var10003] = var4;
            switch(var5) {
            case 0:
               switch(var4) {
               case '<':
                  var5 = 1;
                  continue;
               case '\ufeff':
                  var4 = (var6 = var1.read()) >= 0 ? (char)var6 : '\uffff';
                  this.db[this.dd - 1] = var4;
                  var5 = (short)(var4 == '<' ? 1 : -1);
                  continue;
               default:
                  var5 = -1;
                  continue;
               }
            case 1:
               var5 = (short)(var4 == '?' ? 2 : -1);
               break;
            case 2:
               var5 = (short)(var4 != 'x' && var4 != 'X' ? -1 : 3);
               break;
            case 3:
               var5 = (short)(var4 != 'm' && var4 != 'M' ? -1 : 4);
               break;
            case 4:
               var5 = (short)(var4 != 'l' && var4 != 'L' ? -1 : 5);
               break;
            case 5:
               switch(var4) {
               case '\t':
               case '\n':
               case '\r':
               case ' ':
                  var5 = 6;
                  continue;
               default:
                  var5 = -1;
                  continue;
               }
            case 6:
               if (var4 == '?') {
                  var5 = 7;
               } else if (var4 == '\uffff') {
                  var5 = -1;
               }
               break;
            case 7:
               var5 = (short)(var4 != '>' ? 6 : -2);
               break;
            default:
               this.t("");
            }
         }

         this.dc = this.dd;
         this.dd = 0;
         if (var5 == -1) {
            return var3;
         }

         this.dd = 5;
         byte var7 = 0;

         while(var7 >= 0) {
            var4 = this.next();
            switch(var7) {
            case 0:
               if (this.h(var4) != ' ') {
                  this.F();
                  var7 = 1;
               }
               break;
            case 1:
            case 2:
            case 3:
               switch(this.h(var4)) {
               case ' ':
                  continue;
               case '?':
                  if (var7 == 1) {
                     this.t("");
                  }

                  this.F();
                  var7 = 4;
                  continue;
               case 'A':
               case '_':
               case 'a':
                  this.F();
                  var2 = this.d(false).toLowerCase();
                  if ("version".equals(var2)) {
                     if (var7 != 1) {
                        this.t("");
                     }

                     if (!"1.0".equals(this.b('='))) {
                        this.t("");
                     }

                     var7 = 2;
                  } else if ("encoding".equals(var2)) {
                     if (var7 != 2) {
                        this.t("");
                     }

                     var3 = this.b('=').toUpperCase();
                     var7 = 3;
                  } else if ("standalone".equals(var2)) {
                     if (var7 == 1) {
                        this.t("");
                     }

                     this.b('=');
                     var7 = 4;
                  } else {
                     this.t("");
                  }
                  continue;
               default:
                  this.t("");
                  continue;
               }
            case 4:
               switch(this.h(var4)) {
               case ' ':
                  continue;
               case '?':
                  if (this.next() != '>') {
                     this.t("");
                  }

                  if (this.cS == 0) {
                     this.cS = 1;
                  }

                  var7 = -1;
                  continue;
               default:
                  this.t("");
                  continue;
               }
            default:
               this.t("");
            }
         }

         return var3;
      }
   }

   private static Reader a(String var0, InputStream var1) throws UnsupportedEncodingException {
      if (var0.equals("UTF-8")) {
         return new ReaderUTF8(var1);
      } else if (var0.equals("UTF-16LE")) {
         return new ReaderUTF16(var1, 'l');
      } else {
         return (Reader)(var0.equals("UTF-16BE") ? new ReaderUTF16(var1, 'b') : new InputStreamReader(var1, var0));
      }
   }

   private void a(Input var1) {
      this.cZ.eY = this.dc;
      this.cZ.eZ = this.dd;
      var1.fa = this.cZ;
      this.cZ = var1;
      this.db = var1.chars;
      this.dc = var1.eY;
      this.dd = var1.eZ;
   }

   private void pop() throws SAXException {
      if (this.cZ.eX != null) {
         try {
            this.cZ.eX.close();
         } catch (IOException var1) {
         }

         this.cZ.eX = null;
      }

      this.cZ = this.cZ.fa;
      if (this.cZ != null) {
         if (this.cZ.fb && this.cZ.fa != null) {
            this.t("");
         }

         this.cZ.fb = true;
         this.db = this.cZ.chars;
         this.dc = this.cZ.eY;
         this.dd = this.cZ.eZ;
      } else {
         this.db = null;
         this.dc = 0;
         this.dd = 0;
      }
   }

   private char h(char var1) throws SAXException {
      if (var1 < 128) {
         return (char)dl[var1];
      } else {
         if (var1 == '\uffff') {
            this.t("");
         }

         return 'X';
      }
   }

   private char next() throws SAXException, IOException {
      if (this.dd >= this.dc) {
         if (this.cZ == null) {
            this.t("");
         }

         if (this.cZ.eX == null) {
            this.pop();
            return this.next();
         }

         int var1;
         if ((var1 = this.cZ.eX.read(this.db, 0, this.db.length)) < 0) {
            if (this.cZ != this.da) {
               this.pop();
               return this.next();
            }

            this.db[0] = '\uffff';
            this.dc = 1;
         } else {
            this.dc = (char)var1;
         }

         this.dd = 0;
      }

      char[] var10000 = this.db;
      char var10003 = this.dd;
      this.dd = (char)(var10003 + 1);
      return var10000[var10003];
   }

   private void F() throws SAXException {
      if (this.dd <= 0) {
         this.t("");
      }

      --this.dd;
   }

   private static Pair a(Pair var0, char[] var1) {
      for(Pair var2 = var0; var2 != null; var2 = var2.hp) {
         if (var2.c(var1)) {
            return var2;
         }
      }

      return null;
   }

   private Pair b(Pair var1) {
      Pair var2;
      if (this.dh != null) {
         var2 = this.dh;
         this.dh = var2.hp;
      } else {
         var2 = new Pair();
      }

      var2.hp = var1;
      return var2;
   }

   private Pair c(Pair var1) {
      Pair var2 = var1.hp;
      var1.name = null;
      var1.value = null;
      var1.chars = null;
      var1.ho = null;
      var1.hp = this.dh;
      this.dh = var1;
      return var2;
   }

   static {
      (di = new char[1])[0] = 0;
      (dj = new char[4])[0] = 4;
      dj[1] = 'x';
      dj[2] = 'm';
      dj[3] = 'l';
      (dk = new char[6])[0] = 6;
      dk[1] = 'x';
      dk[2] = 'm';
      dk[3] = 'l';
      dk[4] = 'n';
      dk[5] = 's';
      dl = new byte[128];

      short var0;
      for(var0 = 0; var0 <= 31; dl[var0++] = 122) {
      }

      dl[9] = 32;
      dl[13] = 32;

      for(dl[10] = 32; var0 <= 47; dl[var0] = (byte)(var0++)) {
      }

      while(var0 <= 57) {
         dl[var0++] = 100;
      }

      while(var0 <= 64) {
         dl[var0] = (byte)(var0++);
      }

      while(var0 <= 90) {
         dl[var0++] = 65;
      }

      while(var0 <= 96) {
         dl[var0] = (byte)(var0++);
      }

      while(var0 <= 122) {
         dl[var0++] = 97;
      }

      while(var0 <= 127) {
         dl[var0] = (byte)(var0++);
      }

   }
}
