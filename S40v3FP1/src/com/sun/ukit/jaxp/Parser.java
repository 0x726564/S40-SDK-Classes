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
   private static final short BUFFSIZE_READER = 512;
   private static final short BUFFSIZE_PARSER = 128;
   private static final short BUFFSIZE_ENTITY = 32;
   public static final char EOS = '\uffff';
   private Pair mNoNS;
   private Pair mXml;
   private DefaultHandler mHand;
   private Hashtable mEnt;
   private Hashtable mPEnt;
   private boolean mIsNSAware;
   private short mSt;
   private char mESt;
   private char[] mBuff;
   private short mBuffIdx;
   private Pair mPref;
   private Pair mElm;
   private Pair mAttL;
   private Input mInp;
   private Input mDoc;
   private char[] mChars;
   private char mChLen;
   private char mChIdx;
   private Attrs mAttrs;
   private String[] mItems;
   private char mAttrIdx;
   private Pair mDltd;
   private static final char[] NONS = new char[1];
   private static final char[] XML;
   private static final char[] XMLNS;
   private static final byte[] asctyp;

   public Parser(boolean var1) {
      this.mIsNSAware = var1;
      this.mBuff = new char[128];
      this.mAttrs = new Attrs();
      this.mPref = this.pair(this.mPref);
      this.mPref.name = "";
      this.mPref.value = "";
      this.mPref.chars = NONS;
      this.mNoNS = this.mPref;
      this.mPref = this.pair(this.mPref);
      this.mPref.name = "xml";
      this.mPref.value = "http://www.w3.org/XML/1998/namespace";
      this.mPref.chars = XML;
      this.mXml = this.mPref;
   }

   public String getPublicId() {
      return this.mInp != null ? this.mInp.pubid : null;
   }

   public String getSystemId() {
      return this.mInp != null ? this.mInp.sysid : null;
   }

   public int getLineNumber() {
      return -1;
   }

   public int getColumnNumber() {
      return -1;
   }

   public boolean isNamespaceAware() {
      return this.mIsNSAware;
   }

   public boolean isValidating() {
      return false;
   }

   public void parse(InputStream var1, DefaultHandler var2) throws SAXException, IOException {
      if (var1 != null && var2 != null) {
         this.parse(new InputSource(var1), var2);
      } else {
         throw new IllegalArgumentException("");
      }
   }

   public void parse(InputSource var1, DefaultHandler var2) throws SAXException, IOException {
      if (var1 != null && var2 != null) {
         this.mHand = var2;
         this.mInp = new Input((short)512);
         this.setinp(var1);
         this.parse(var2);
      } else {
         throw new IllegalArgumentException("");
      }
   }

   private void parse(DefaultHandler var1) throws SAXException, IOException {
      try {
         this.mPEnt = new Hashtable();
         this.mEnt = new Hashtable();
         this.mDoc = this.mInp;
         this.mChars = this.mInp.chars;
         this.mHand.setDocumentLocator(this);
         this.mHand.startDocument();
         this.mSt = 1;

         char var2;
         while((var2 = this.next()) != '\uffff') {
            switch(this.chtyp(var2)) {
            case ' ':
               break;
            case '<':
               var2 = this.next();
               switch(var2) {
               case '!':
                  var2 = this.next();
                  this.back();
                  if (var2 == '-') {
                     this.comm();
                  } else {
                     this.dtd();
                  }
                  continue;
               case '?':
                  this.pi();
                  continue;
               default:
                  if (this.mSt == 5) {
                     this.panic("");
                  }

                  this.back();
                  this.mSt = 4;
                  this.elm();
                  this.mSt = 5;
                  continue;
               }
            default:
               this.panic("");
            }
         }

         if (this.mSt != 5) {
            this.panic("");
         }
      } finally {
         this.mHand.endDocument();

         while(this.mAttL != null) {
            for(; this.mAttL.list != null; this.mAttL.list = this.del(this.mAttL.list)) {
               if (this.mAttL.list.list != null) {
                  this.del(this.mAttL.list.list);
               }
            }

            this.mAttL = this.del(this.mAttL);
         }

         while(this.mElm != null) {
            this.mElm = this.del(this.mElm);
         }

         while(this.mPref != this.mXml) {
            this.mPref = this.del(this.mPref);
         }

         while(this.mInp != null) {
            this.pop();
         }

         if (this.mDoc != null && this.mDoc.src != null) {
            try {
               this.mDoc.src.close();
            } catch (IOException var9) {
            }
         }

         this.mPEnt = null;
         this.mEnt = null;
         this.mDoc = null;
         this.mHand = null;
      }

   }

   private void dtd() throws SAXException, IOException {
      Object var2 = null;
      String var3 = null;
      Pair var4 = null;
      if (!"DOCTYPE".equals(this.name(false))) {
         this.panic("");
      }

      this.mSt = 2;
      byte var5 = 0;

      while(var5 >= 0) {
         char var1 = this.next();
         switch(var5) {
         case 0:
            if (this.chtyp(var1) != ' ') {
               this.back();
               var3 = this.name(this.mIsNSAware);
               this.wsskip();
               var5 = 1;
            }
            break;
         case 1:
            switch(this.chtyp(var1)) {
            case '>':
               this.back();
               var5 = 3;
               break;
            case 'A':
               this.back();
               var4 = this.pubsys(' ');
               var5 = 2;
               break;
            case '[':
               this.back();
               var5 = 2;
               break;
            default:
               this.panic("");
            }

            if (var4 != null) {
               if (this.mHand.resolveEntity(var4.name, var4.value) != null) {
                  this.panic("");
               }

               this.del(var4);
               this.mHand.skippedEntity("[dtd]");
            }
            break;
         case 2:
            switch(this.chtyp(var1)) {
            case ' ':
               continue;
            case '>':
               this.back();
               var5 = 3;
               continue;
            case '[':
               this.dtdint();
               var5 = 3;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 3:
            switch(this.chtyp(var1)) {
            case ' ':
               continue;
            case '>':
               var5 = -1;
               continue;
            default:
               this.panic("");
               continue;
            }
         default:
            this.panic("");
         }
      }

      this.mSt = 3;
   }

   private void dtdint() throws SAXException, IOException {
      byte var2 = 0;

      while(var2 >= 0) {
         char var1 = this.next();
         switch(var2) {
         case 0:
            switch(this.chtyp(var1)) {
            case ' ':
               continue;
            case '%':
               this.pent(' ');
               continue;
            case '<':
               var1 = this.next();
               switch(var1) {
               case '!':
                  var1 = this.next();
                  this.back();
                  if (var1 == '-') {
                     this.comm();
                  } else {
                     this.bntok();
                     switch(this.bkeyword()) {
                     case 'a':
                        this.dtdattl();
                        break;
                     case 'e':
                        this.dtdelm();
                        break;
                     case 'n':
                        this.dtdent();
                        break;
                     case 'o':
                        this.dtdnot();
                        break;
                     default:
                        this.panic("");
                     }

                     var2 = 1;
                  }
                  continue;
               case '?':
                  this.pi();
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            case ']':
               var2 = -1;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 1:
            switch(var1) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
               continue;
            case '>':
               var2 = 0;
               continue;
            default:
               this.panic("");
               continue;
            }
         default:
            this.panic("");
         }
      }

   }

   private void dtdent() throws SAXException, IOException {
      String var1 = null;
      Object var2 = null;
      Input var3 = null;
      Pair var4 = null;
      byte var6 = 0;

      while(true) {
         while(true) {
            while(var6 >= 0) {
               char var5 = this.next();
               char[] var7;
               switch(var6) {
               case 0:
                  switch(this.chtyp(var5)) {
                  case ' ':
                     continue;
                  case '%':
                     var5 = this.next();
                     this.back();
                     if (this.chtyp(var5) == ' ') {
                        this.wsskip();
                        var1 = this.name(false);
                        switch(this.chtyp(this.wsskip())) {
                        case '"':
                        case '\'':
                           this.bqstr('-');
                           var7 = new char[this.mBuffIdx + 1];
                           System.arraycopy(this.mBuff, 1, var7, 1, var7.length - 1);
                           var7[0] = ' ';
                           var3 = new Input(var7);
                           var3.pubid = this.mInp.pubid;
                           var3.sysid = this.mInp.sysid;
                           this.mPEnt.put(var1, var3);
                           var6 = -1;
                           continue;
                        case 'A':
                           var4 = this.pubsys(' ');
                           if (this.wsskip() == '>') {
                              var3 = new Input();
                              var3.pubid = var4.name;
                              var3.sysid = var4.value;
                              this.mPEnt.put(var1, var3);
                           } else {
                              this.panic("");
                           }

                           this.del(var4);
                           var6 = -1;
                           continue;
                        default:
                           this.panic("");
                        }
                     } else {
                        this.pent(' ');
                     }
                     continue;
                  default:
                     this.back();
                     var1 = this.name(false);
                     var6 = 1;
                     continue;
                  }
               case 1:
                  switch(this.chtyp(var5)) {
                  case ' ':
                     continue;
                  case '"':
                  case '\'':
                     this.back();
                     this.bqstr('-');
                     if (this.mEnt.get(var1) == null) {
                        var7 = new char[this.mBuffIdx];
                        System.arraycopy(this.mBuff, 1, var7, 0, var7.length);
                        var3 = new Input(var7);
                        var3.pubid = this.mInp.pubid;
                        var3.sysid = this.mInp.sysid;
                        this.mEnt.put(var1, var3);
                     }

                     var6 = -1;
                     continue;
                  case 'A':
                     this.back();
                     var4 = this.pubsys(' ');
                     switch(this.wsskip()) {
                     case '>':
                        var3 = new Input();
                        var3.pubid = var4.name;
                        var3.sysid = var4.value;
                        this.mEnt.put(var1, var3);
                        break;
                     case 'N':
                        if ("NDATA".equals(this.name(false))) {
                           this.wsskip();
                           this.mHand.unparsedEntityDecl(var1, var4.name, var4.value, this.name(false));
                           break;
                        }
                     default:
                        this.panic("");
                     }

                     this.del(var4);
                     var6 = -1;
                     continue;
                  default:
                     this.panic("");
                     continue;
                  }
               default:
                  this.panic("");
               }
            }

            return;
         }
      }
   }

   private void dtdelm() throws SAXException, IOException {
      this.wsskip();
      this.name(this.mIsNSAware);

      while(true) {
         char var1 = this.next();
         switch(var1) {
         case '>':
            this.back();
            return;
         case '\uffff':
            this.panic("");
         }
      }
   }

   private void dtdattl() throws SAXException, IOException {
      Object var1 = null;
      Pair var2 = null;
      byte var4 = 0;

      while(var4 >= 0) {
         char var3 = this.next();
         switch(var4) {
         case 0:
            switch(this.chtyp(var3)) {
            case ' ':
               continue;
            case '%':
               this.pent(' ');
               continue;
            case ':':
            case 'A':
            case 'X':
            case '_':
            case 'a':
               this.back();
               char[] var5 = this.qname(this.mIsNSAware);
               var2 = this.find(this.mAttL, var5);
               if (var2 == null) {
                  var2 = this.pair(this.mAttL);
                  var2.chars = var5;
                  this.mAttL = var2;
               }

               var4 = 1;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 1:
            switch(this.chtyp(var3)) {
            case ' ':
               continue;
            case '%':
               this.pent(' ');
               continue;
            case ':':
            case 'A':
            case 'X':
            case '_':
            case 'a':
               this.back();
               this.dtdatt(var2);
               if (this.wsskip() == '>') {
                  return;
               }
               continue;
            default:
               this.panic("");
               continue;
            }
         default:
            this.panic("");
         }
      }

   }

   private void dtdatt(Pair var1) throws SAXException, IOException {
      Object var2 = null;
      Pair var3 = null;
      byte var5 = 0;

      while(var5 >= 0) {
         char var4 = this.next();
         switch(var5) {
         case 0:
            switch(this.chtyp(var4)) {
            case ' ':
               continue;
            case '%':
               this.pent(' ');
               continue;
            case ':':
            case 'A':
            case 'X':
            case '_':
            case 'a':
               this.back();
               char[] var6 = this.qname(this.mIsNSAware);
               var3 = this.find(var1.list, var6);
               if (var3 == null) {
                  var3 = this.pair(var1.list);
                  var3.chars = var6;
                  var1.list = var3;
               } else {
                  var3.id = 'c';
                  if (var3.list != null) {
                     this.del(var3.list);
                  }

                  var3.list = null;
               }

               this.wsskip();
               var5 = 1;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 1:
            switch(var4) {
            case ' ':
               continue;
            case '%':
               this.pent(' ');
               continue;
            case '(':
               this.back();
               var3.id = 'u';
               var5 = 2;
               continue;
            default:
               this.back();
               this.bntok();
               var3.id = this.bkeyword();
               switch(var3.id) {
               case 'N':
               case 'R':
               case 'T':
               case 'c':
               case 'i':
               case 'n':
               case 'r':
               case 't':
                  this.wsskip();
                  var5 = 4;
                  continue;
               case 'o':
                  if (this.wsskip() != '(') {
                     this.panic("");
                  }

                  var5 = 2;
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            }
         case 2:
            if (var4 != '(') {
               this.panic("");
            }

            var4 = this.wsskip();
            switch(this.chtyp(var4)) {
            case '%':
               this.next();
               this.pent(' ');
               continue;
            case '-':
            case '.':
            case ':':
            case 'A':
            case 'X':
            case '_':
            case 'a':
            case 'd':
               switch(var3.id) {
               case 'o':
                  this.mBuffIdx = -1;
                  this.bname(false);
                  break;
               case 'u':
                  this.bntok();
                  break;
               default:
                  this.panic("");
               }

               this.wsskip();
               var5 = 3;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 3:
            switch(var4) {
            case '%':
               this.pent(' ');
               continue;
            case ')':
               this.wsskip();
               var5 = 4;
               continue;
            case '|':
               this.wsskip();
               switch(var3.id) {
               case 'o':
                  this.mBuffIdx = -1;
                  this.bname(false);
                  break;
               case 'u':
                  this.bntok();
                  break;
               default:
                  this.panic("");
               }

               this.wsskip();
               continue;
            default:
               this.panic("");
               continue;
            }
         case 4:
            switch(var4) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
               continue;
            case '"':
            case '\'':
               this.back();
               var5 = 5;
               continue;
            case '#':
               this.bntok();
               switch(this.bkeyword()) {
               case 'F':
                  switch(this.wsskip()) {
                  case '"':
                  case '\'':
                     var5 = 5;
                     continue;
                  default:
                     var5 = -1;
                     continue;
                  }
               case 'I':
               case 'Q':
                  var5 = -1;
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            case '%':
               this.pent(' ');
               continue;
            default:
               this.back();
               var5 = -1;
               continue;
            }
         case 5:
            switch(var4) {
            case '"':
            case '\'':
               this.back();
               this.bqstr('-');
               var3.list = this.pair((Pair)null);
               var3.list.chars = new char[var3.chars.length + this.mBuffIdx + 3];
               System.arraycopy(var3.chars, 1, var3.list.chars, 0, var3.chars.length - 1);
               var3.list.chars[var3.chars.length - 1] = '=';
               var3.list.chars[var3.chars.length] = var4;
               System.arraycopy(this.mBuff, 1, var3.list.chars, var3.chars.length + 1, this.mBuffIdx);
               var3.list.chars[var3.chars.length + this.mBuffIdx + 1] = var4;
               var3.list.chars[var3.chars.length + this.mBuffIdx + 2] = ' ';
               var5 = -1;
               continue;
            default:
               this.panic("");
               continue;
            }
         default:
            this.panic("");
         }
      }

   }

   private void dtdnot() throws SAXException, IOException {
      this.wsskip();
      String var1 = this.name(false);
      this.wsskip();
      Pair var2 = this.pubsys('N');
      this.mHand.notationDecl(var1, var2.name, var2.value);
      this.del(var2);
   }

   private void elm() throws SAXException, IOException {
      Pair var1 = this.mPref;
      this.mElm = this.pair(this.mElm);
      this.mElm.chars = this.qname(this.mIsNSAware);
      this.mElm.name = this.mElm.local();
      Pair var2 = this.find(this.mAttL, this.mElm.chars);
      this.mAttrIdx = 0;
      Pair var3 = this.pair((Pair)null);
      var3.list = var2 != null ? var2.list : null;
      this.attr(var3);
      this.del(var3);
      this.mBuffIdx = -1;
      int var5 = 0;

      while(true) {
         label97:
         while(true) {
            while(var5 >= 0) {
               char var4 = this.next();
               switch(var5) {
               case 0:
               case 1:
                  switch(var4) {
                  case '/':
                     if (var5 != 0) {
                        this.panic("");
                     }

                     var5 = 1;
                     continue;
                  case '>':
                     if (this.mIsNSAware) {
                        this.mElm.value = this.rslv(this.mElm.chars);
                        this.mHand.startElement(this.mElm.value, this.mElm.name, "", this.mAttrs);
                     } else {
                        this.mHand.startElement("", "", this.mElm.name, this.mAttrs);
                     }

                     this.mItems = null;
                     var5 = var5 == 0 ? 2 : -1;
                     continue;
                  default:
                     this.panic("");
                     continue;
                  }
               case 2:
                  switch(this.chtyp(var4)) {
                  case ' ':
                     this.bappend(var4);
                     continue;
                  case '<':
                     this.bflash();
                  default:
                     this.back();
                     var5 = 3;
                     continue;
                  }
               case 3:
                  switch(var4) {
                  case '\r':
                     if (this.next() != '\n') {
                        this.back();
                     }

                     this.bappend('\n');
                     continue;
                  case '&':
                     this.ent('c');
                     continue;
                  case '<':
                     this.bflash();
                     switch(this.next()) {
                     case '!':
                        var4 = this.next();
                        this.back();
                        switch(var4) {
                        case '-':
                           this.comm();
                           break label97;
                        case '[':
                           this.cdat();
                           break label97;
                        default:
                           this.panic("");
                           break label97;
                        }
                     case '/':
                        this.mBuffIdx = -1;
                        this.bname(this.mIsNSAware);
                        char[] var6 = this.mElm.chars;
                        if (var6.length == this.mBuffIdx + 1) {
                           for(char var7 = 1; var7 <= this.mBuffIdx; ++var7) {
                              if (var6[var7] != this.mBuff[var7]) {
                                 this.panic("");
                              }
                           }
                        } else {
                           this.panic("");
                        }

                        if (this.wsskip() != '>') {
                           this.panic("");
                        }

                        var4 = this.next();
                        var5 = -1;
                        break label97;
                     case '?':
                        this.pi();
                        break label97;
                     default:
                        this.back();
                        this.elm();
                        break label97;
                     }
                  default:
                     this.bappend(var4);
                     continue;
                  }
               default:
                  this.panic("");
               }
            }

            if (this.mIsNSAware) {
               this.mHand.endElement(this.mElm.value, this.mElm.name, "");
            } else {
               this.mHand.endElement("", "", this.mElm.name);
            }

            for(this.mElm = this.del(this.mElm); this.mPref != var1; this.mPref = this.del(this.mPref)) {
               this.mHand.endPrefixMapping(this.mPref.name);
            }

            return;
         }

         this.mBuffIdx = -1;
         if (var5 != -1) {
            var5 = 2;
         }
      }
   }

   private void attr(Pair var1) throws SAXException, IOException {
      Pair var2 = null;
      char var3 = 'c';

      try {
         Pair var6;
         switch(this.wsskip()) {
         case '/':
         case '>':
            var6 = var1.list;

            for(; var6 != null; var6 = var6.next) {
               if (var6.list != null) {
                  Pair var7;
                  for(var7 = var1.next; var7 != null && !var7.eqname(var6.chars); var7 = var7.next) {
                  }

                  if (var7 == null) {
                     this.push(new Input(var6.list.chars));
                     this.attr(var1);
                     return;
                  }
               }
            }

            this.mAttrs.setLength(this.mAttrIdx);
            this.mItems = this.mAttrs.mItems;
            return;
         default:
            var1.chars = this.qname(this.mIsNSAware);
            var1.name = var1.local();
            String var5 = "CDATA";
            if (var1.list != null) {
               var6 = this.find(var1.list, var1.chars);
               if (var6 != null) {
                  switch(var6.id) {
                  case 'N':
                     var5 = "ENTITIES";
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
                     this.panic("");
                     break;
                  case 'R':
                     var5 = "IDREFS";
                     var3 = 'i';
                     break;
                  case 'T':
                     var5 = "NMTOKENS";
                     var3 = 'i';
                     break;
                  case 'c':
                     var3 = 'c';
                     break;
                  case 'i':
                     var5 = "ID";
                     var3 = 'i';
                     break;
                  case 'n':
                     var5 = "ENTITY";
                     var3 = 'i';
                     break;
                  case 'o':
                     var5 = "NOTATION";
                     var3 = 'i';
                     break;
                  case 'r':
                     var5 = "IDREF";
                     var3 = 'i';
                     break;
                  case 't':
                     var5 = "NMTOKEN";
                     var3 = 'i';
                     break;
                  case 'u':
                     var5 = "NMTOKEN";
                     var3 = 'i';
                  }
               }
            }

            this.wsskip();
            if (this.next() != '=') {
               this.panic("");
            }

            this.bqstr(var3);
            String var4 = new String(this.mBuff, 1, this.mBuffIdx);
            if (this.mIsNSAware && this.isdecl(var1, var4)) {
               this.mHand.startPrefixMapping(this.mPref.name, this.mPref.value);
               var2 = this.pair(var1);
               var2.list = var1.list;
               this.attr(var2);
            } else {
               ++this.mAttrIdx;
               var2 = this.pair(var1);
               var2.list = var1.list;
               this.attr(var2);
               --this.mAttrIdx;
               char var11 = (char)(this.mAttrIdx << 3);
               this.mItems[var11 + 1] = var1.qname();
               this.mItems[var11 + 2] = var1.name;
               this.mItems[var11 + 3] = var4;
               this.mItems[var11 + 4] = var5;
               this.mItems[var11 + 0] = var1.chars[0] != 0 ? this.rslv(var1.chars) : "";
            }

         }
      } finally {
         if (var2 != null) {
            this.del(var2);
         }

      }
   }

   private void comm() throws SAXException, IOException {
      if (this.mSt == 0) {
         this.mSt = 1;
      }

      int var2 = 0;

      while(var2 >= 0) {
         char var1 = this.next();
         switch(var2) {
         case 0:
            if (var1 == '-') {
               var2 = 1;
            } else {
               this.panic("");
            }
            break;
         case 1:
            if (var1 == '-') {
               var2 = 2;
            } else {
               this.panic("");
            }
            break;
         case 2:
            if (var1 == '-') {
               var2 = 3;
            }
            break;
         case 3:
            var2 = var1 == '-' ? 4 : 2;
            break;
         case 4:
            if (var1 == '>') {
               var2 = -1;
            } else {
               this.panic("");
            }
            break;
         default:
            this.panic("");
         }
      }

   }

   private void pi() throws SAXException, IOException {
      String var2 = null;
      this.mBuffIdx = -1;
      byte var3 = 0;

      while(var3 >= 0) {
         char var1 = this.next();
         switch(var3) {
         case 0:
            switch(this.chtyp(var1)) {
            case ' ':
               var2 = "";
               var3 = 2;
               continue;
            case 'A':
            case 'X':
            case '_':
            case 'a':
               this.back();
               var2 = this.name(false);
               var3 = 1;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 1:
            if (this.chtyp(var1) != ' ') {
               this.back();
               if (this.mXml.name.equals(var2.toLowerCase())) {
                  this.panic("");
               } else {
                  if (this.mSt == 0) {
                     this.mSt = 1;
                  }

                  var3 = 2;
               }

               this.mBuffIdx = -1;
            }
            break;
         case 2:
            if (var1 == '?') {
               var3 = 3;
            } else {
               this.bappend(var1);
            }
            break;
         case 3:
            if (var1 == '>') {
               this.mHand.processingInstruction(var2, new String(this.mBuff, 0, this.mBuffIdx + 1));
               var3 = -1;
            } else {
               this.bappend('?');
               this.bappend(var1);
               var3 = 2;
            }
            break;
         default:
            this.panic("");
         }
      }

   }

   private void cdat() throws SAXException, IOException {
      this.mBuffIdx = -1;
      byte var2 = 0;

      while(var2 >= 0) {
         char var1 = this.next();
         switch(var2) {
         case 0:
            if (var1 == '[') {
               var2 = 1;
            } else {
               this.panic("");
            }
            break;
         case 1:
            if (this.chtyp(var1) == 'A') {
               this.bappend(var1);
            } else {
               if (!"CDATA".equals(new String(this.mBuff, 0, this.mBuffIdx + 1))) {
                  this.panic("");
               }

               this.back();
               var2 = 2;
            }
            break;
         case 2:
            if (var1 != '[') {
               this.panic("");
            }

            this.mBuffIdx = -1;
            var2 = 3;
            break;
         case 3:
            if (var1 != ']') {
               this.bappend(var1);
            } else {
               var2 = 4;
            }
            break;
         case 4:
            if (var1 != ']') {
               this.bappend(']');
               this.bappend(var1);
               var2 = 3;
            } else {
               var2 = 5;
            }
            break;
         case 5:
            if (var1 != '>') {
               this.bappend(']');
               this.bappend(']');
               this.bappend(var1);
               var2 = 3;
            } else {
               this.bflash();
               var2 = -1;
            }
            break;
         default:
            this.panic("");
         }
      }

   }

   private String name(boolean var1) throws SAXException, IOException {
      this.mBuffIdx = -1;
      this.bname(var1);
      return new String(this.mBuff, 1, this.mBuffIdx);
   }

   private char[] qname(boolean var1) throws SAXException, IOException {
      this.mBuffIdx = -1;
      this.bname(var1);
      char[] var2 = new char[this.mBuffIdx + 1];
      System.arraycopy(this.mBuff, 0, var2, 0, this.mBuffIdx + 1);
      return var2;
   }

   private void pubsys(Input var1) throws SAXException, IOException {
      Pair var2 = this.pubsys(' ');
      var1.pubid = var2.name;
      var1.sysid = var2.value;
      this.del(var2);
   }

   private Pair pubsys(char var1) throws SAXException, IOException {
      Pair var2 = this.pair((Pair)null);
      String var3 = this.name(false);
      if ("PUBLIC".equals(var3)) {
         this.bqstr('i');
         var2.name = new String(this.mBuff, 1, this.mBuffIdx);
         switch(this.wsskip()) {
         case '"':
         case '\'':
            this.bqstr(' ');
            var2.value = new String(this.mBuff, 1, this.mBuffIdx);
            break;
         default:
            if (var1 != 'N') {
               this.panic("");
            }

            var2.value = null;
         }

         return var2;
      } else if ("SYSTEM".equals(var3)) {
         var2.name = null;
         this.bqstr(' ');
         var2.value = new String(this.mBuff, 1, this.mBuffIdx);
         return var2;
      } else {
         this.panic("");
         return null;
      }
   }

   private String eqstr(char var1) throws SAXException, IOException {
      if (var1 == '=') {
         this.wsskip();
         if (this.next() != '=') {
            this.panic("");
         }
      }

      this.bqstr(var1);
      return new String(this.mBuff, 1, this.mBuffIdx);
   }

   private void ent(char var1) throws SAXException, IOException {
      short var3 = (short)(this.mBuffIdx + 1);
      Input var4 = null;
      String var5 = null;
      this.mESt = 256;
      this.bappend('&');
      byte var6 = 0;

      while(true) {
         while(var6 >= 0) {
            char var2 = this.next();
            switch(var6) {
            case 0:
            case 1:
               switch(this.chtyp(var2)) {
               case '#':
                  if (var6 != 0) {
                     this.panic("");
                  }

                  var6 = 2;
                  continue;
               case '-':
               case '.':
               case 'd':
                  if (var6 != 1) {
                     this.panic("");
                  }
               case 'A':
               case 'X':
               case '_':
               case 'a':
                  this.bappend(var2);
                  this.eappend(var2);
                  var6 = 1;
                  continue;
               case ':':
                  if (this.mIsNSAware) {
                     this.panic("");
                  }

                  this.bappend(var2);
                  this.eappend(var2);
                  var6 = 1;
                  continue;
               case ';':
                  if (this.mESt < 256) {
                     this.mBuffIdx = (short)(var3 - 1);
                     this.bappend(this.mESt);
                     var6 = -1;
                  } else if (this.mSt == 2) {
                     this.bappend(';');
                     var6 = -1;
                  } else {
                     var5 = new String(this.mBuff, var3 + 1, this.mBuffIdx - var3);
                     var4 = (Input)this.mEnt.get(var5);
                     this.mBuffIdx = (short)(var3 - 1);
                     if (var4 != null) {
                        if (var4.chars == null) {
                           InputSource var7 = this.mHand.resolveEntity(var4.pubid, var4.sysid);
                           if (var7 != null) {
                              this.push(new Input((short)512));
                              this.setinp(var7);
                              this.mInp.pubid = var4.pubid;
                              this.mInp.sysid = var4.sysid;
                           } else {
                              this.bflash();
                              if (var1 != 'c') {
                                 this.panic("");
                              }

                              this.mHand.skippedEntity(var5);
                           }
                        } else {
                           this.push(var4);
                        }
                     } else {
                        this.bflash();
                        if (var1 != 'c') {
                           this.panic("");
                        }

                        this.mHand.skippedEntity(var5);
                     }

                     var6 = -1;
                  }
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            case 2:
               switch(this.chtyp(var2)) {
               case ';':
                  try {
                     var2 = (char)Short.parseShort(new String(this.mBuff, var3 + 1, this.mBuffIdx - var3), 10);
                  } catch (NumberFormatException var9) {
                     this.panic("");
                  }

                  this.mBuffIdx = (short)(var3 - 1);
                  this.bappend(var2);
                  var6 = -1;
                  continue;
               case 'a':
                  if (this.mBuffIdx == var3 && var2 == 'x') {
                     var6 = 3;
                     continue;
                  }
               default:
                  this.panic("");
                  continue;
               case 'd':
                  this.bappend(var2);
                  continue;
               }
            case 3:
               switch(this.chtyp(var2)) {
               case ';':
                  try {
                     var2 = (char)Short.parseShort(new String(this.mBuff, var3 + 1, this.mBuffIdx - var3), 16);
                  } catch (NumberFormatException var8) {
                     this.panic("");
                  }

                  this.mBuffIdx = (short)(var3 - 1);
                  this.bappend(var2);
                  var6 = -1;
                  continue;
               case 'A':
               case 'a':
               case 'd':
                  this.bappend(var2);
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            default:
               this.panic("");
            }
         }

         return;
      }
   }

   private void pent(char var1) throws SAXException, IOException {
      short var3 = (short)(this.mBuffIdx + 1);
      Input var4 = null;
      String var5 = null;
      this.bappend('%');
      if (this.mSt == 2) {
         this.bname(false);
         var5 = new String(this.mBuff, var3 + 2, this.mBuffIdx - var3 - 1);
         if (this.next() != ';') {
            this.panic("");
         }

         var4 = (Input)this.mPEnt.get(var5);
         this.mBuffIdx = (short)(var3 - 1);
         if (var4 != null) {
            if (var4.chars == null) {
               InputSource var6 = this.mHand.resolveEntity(var4.pubid, var4.sysid);
               if (var6 != null) {
                  if (var1 != '-') {
                     this.bappend(' ');
                  }

                  this.push(new Input((short)512));
                  this.setinp(var6);
                  this.mInp.pubid = var4.pubid;
                  this.mInp.sysid = var4.sysid;
               } else {
                  this.mHand.skippedEntity("%" + var5);
               }
            } else {
               if (var1 == '-') {
                  var4.chIdx = 1;
               } else {
                  this.bappend(' ');
                  var4.chIdx = 0;
               }

               this.push(var4);
            }
         } else {
            this.mHand.skippedEntity("%" + var5);
         }

      }
   }

   private boolean isdecl(Pair var1, String var2) {
      if (var1.chars[0] == 0) {
         if ("xmlns".equals(var1.name)) {
            this.mPref = this.pair(this.mPref);
            this.mPref.value = var2;
            this.mPref.name = "";
            this.mPref.chars = NONS;
            return true;
         }
      } else if (var1.eqpref(XMLNS)) {
         int var3 = var1.name.length();
         this.mPref = this.pair(this.mPref);
         this.mPref.value = var2;
         this.mPref.name = var1.name;
         this.mPref.chars = new char[var3 + 1];
         this.mPref.chars[0] = (char)(var3 + 1);
         var1.name.getChars(0, var3, this.mPref.chars, 1);
         return true;
      }

      return false;
   }

   private String rslv(char[] var1) throws SAXException {
      for(Pair var2 = this.mPref; var2 != null; var2 = var2.next) {
         if (var2.eqpref(var1)) {
            return var2.value;
         }
      }

      this.panic("");
      return null;
   }

   private char wsskip() throws SAXException, IOException {
      while(true) {
         char var1 = this.next();
         switch(var1) {
         case '\t':
         case '\n':
         case '\r':
         case ' ':
            break;
         case '\uffff':
            this.panic("");
         default:
            this.back();
            return var1;
         }
      }
   }

   private void panic(String var1) throws SAXException {
      this.mHand.fatalError(new SAXParseException(var1, this));
   }

   private void bname(boolean var1) throws SAXException, IOException {
      char var3 = (char)(this.mBuffIdx + 1);
      char var4 = var3;
      short var5 = (short)(var1 ? 0 : 2);
      this.bappend('\u0000');

      while(var5 >= 0) {
         char var2 = this.next();
         switch(var5) {
         case 0:
         case 2:
            switch(this.chtyp(var2)) {
            case ':':
               this.back();
               ++var5;
               continue;
            case 'A':
            case 'X':
            case '_':
            case 'a':
               this.bappend(var2);
               ++var5;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 1:
         case 3:
            switch(this.chtyp(var2)) {
            case '-':
            case '.':
            case 'A':
            case 'X':
            case '_':
            case 'a':
            case 'd':
               this.bappend(var2);
               continue;
            case ':':
               this.bappend(var2);
               if (var1) {
                  if (var4 != var3) {
                     this.panic("");
                  }

                  var4 = (char)this.mBuffIdx;
                  if (var5 == 1) {
                     var5 = 2;
                  }
               }
               continue;
            default:
               this.back();
               this.mBuff[var3] = var4;
               return;
            }
         default:
            this.panic("");
         }
      }

   }

   private void bntok() throws SAXException, IOException {
      this.mBuffIdx = -1;
      this.bappend('\u0000');

      while(true) {
         char var1 = this.next();
         switch(this.chtyp(var1)) {
         case '-':
         case '.':
         case ':':
         case 'A':
         case 'X':
         case '_':
         case 'a':
         case 'd':
            this.bappend(var1);
            break;
         default:
            this.back();
            return;
         }
      }
   }

   private char bkeyword() throws SAXException, IOException {
      String var1 = new String(this.mBuff, 1, this.mBuffIdx);
      switch(var1.length()) {
      case 2:
         return (char)("ID".equals(var1) ? 'i' : '?');
      case 3:
      case 4:
      default:
         break;
      case 5:
         switch(this.mBuff[1]) {
         case 'C':
            return (char)("CDATA".equals(var1) ? 'c' : '?');
         case 'F':
            return (char)("FIXED".equals(var1) ? 'F' : '?');
         case 'I':
            return (char)("IDREF".equals(var1) ? 'r' : '?');
         default:
            return '?';
         }
      case 6:
         switch(this.mBuff[1]) {
         case 'E':
            return (char)("ENTITY".equals(var1) ? 'n' : '?');
         case 'I':
            return (char)("IDREFS".equals(var1) ? 'R' : '?');
         default:
            return '?';
         }
      case 7:
         switch(this.mBuff[1]) {
         case 'A':
            return (char)("ATTLIST".equals(var1) ? 'a' : '?');
         case 'E':
            return (char)("ELEMENT".equals(var1) ? 'e' : '?');
         case 'I':
            return (char)("IMPLIED".equals(var1) ? 'I' : '?');
         case 'N':
            return (char)("NMTOKEN".equals(var1) ? 't' : '?');
         default:
            return '?';
         }
      case 8:
         switch(this.mBuff[2]) {
         case 'E':
            return (char)("REQUIRED".equals(var1) ? 'Q' : '?');
         case 'M':
            return (char)("NMTOKENS".equals(var1) ? 'T' : '?');
         case 'N':
            return (char)("ENTITIES".equals(var1) ? 'N' : '?');
         case 'O':
            return (char)("NOTATION".equals(var1) ? 'o' : '?');
         }
      }

      return '?';
   }

   private void bqstr(char var1) throws SAXException, IOException {
      Input var2 = this.mInp;
      this.mBuffIdx = -1;
      this.bappend('\u0000');
      byte var4 = 0;

      while(true) {
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
                        this.panic("");
                        continue;
                     }
                  case 1:
                  default:
                     this.panic("");
                     break;
                  case 2:
                  case 3:
                     switch(var3) {
                     case '\r':
                        if (var1 != ' ') {
                           if (this.next() != '\n') {
                              this.back();
                           }

                           var3 = '\n';
                        }
                     default:
                        switch(var1) {
                        case 'c':
                           switch(var3) {
                           case '\t':
                           case '\n':
                              this.bappend(' ');
                              continue;
                           default:
                              this.bappend(var3);
                              continue;
                           }
                        case 'i':
                           switch(var3) {
                           case '\t':
                           case '\n':
                           case ' ':
                              if (this.mBuffIdx > 0 && this.mBuff[this.mBuffIdx] != ' ') {
                                 this.bappend(' ');
                              }
                              continue;
                           default:
                              this.bappend(var3);
                              continue;
                           }
                        default:
                           this.bappend(var3);
                           continue;
                        }
                     case '"':
                        if (var4 == 3 && this.mInp == var2) {
                           var4 = -1;
                           break;
                        }

                        this.bappend(var3);
                        break;
                     case '%':
                        this.pent(var1);
                        break;
                     case '&':
                        this.ent(' ');
                        break;
                     case '\'':
                        if (var4 == 2 && this.mInp == var2) {
                           var4 = -1;
                        } else {
                           this.bappend(var3);
                        }
                     }
                  }
               }

               if (var1 == 'i' && this.mBuff[this.mBuffIdx] == ' ') {
                  --this.mBuffIdx;
               }

               return;
            }
         }
      }
   }

   private void bflash() throws SAXException {
      if (this.mBuffIdx >= 0) {
         this.mHand.characters(this.mBuff, 0, this.mBuffIdx + 1);
         this.mBuffIdx = -1;
      }

   }

   private void bappend(char var1) {
      try {
         ++this.mBuffIdx;
         this.mBuff[this.mBuffIdx] = var1;
      } catch (Exception var4) {
         char[] var3 = new char[this.mBuff.length << 1];
         System.arraycopy(this.mBuff, 0, var3, 0, this.mBuff.length);
         this.mBuff = var3;
         this.mBuff[this.mBuffIdx] = var1;
      }

   }

   private void eappend(char var1) {
      switch(this.mESt) {
      case '"':
      case '&':
      case '\'':
      case '<':
      case '>':
         this.mESt = 512;
         break;
      case 'Ā':
         switch(var1) {
         case 'a':
            this.mESt = 259;
            return;
         case 'g':
            this.mESt = 258;
            return;
         case 'l':
            this.mESt = 257;
            return;
         case 'q':
            this.mESt = 263;
            return;
         default:
            this.mESt = 512;
            return;
         }
      case 'ā':
         this.mESt = (char)(var1 == 't' ? 60 : 512);
         break;
      case 'Ă':
         this.mESt = (char)(var1 == 't' ? 62 : 512);
         break;
      case 'ă':
         switch(var1) {
         case 'm':
            this.mESt = 260;
            return;
         case 'p':
            this.mESt = 261;
            return;
         default:
            this.mESt = 512;
            return;
         }
      case 'Ą':
         this.mESt = (char)(var1 == 'p' ? 38 : 512);
         break;
      case 'ą':
         this.mESt = (char)(var1 == 'o' ? 262 : 512);
         break;
      case 'Ć':
         this.mESt = (char)(var1 == 's' ? 39 : 512);
         break;
      case 'ć':
         this.mESt = (char)(var1 == 'u' ? 264 : 512);
         break;
      case 'Ĉ':
         this.mESt = (char)(var1 == 'o' ? 265 : 512);
         break;
      case 'ĉ':
         this.mESt = (char)(var1 == 't' ? 34 : 512);
      }

   }

   private void setinp(InputSource var1) throws SAXException, IOException {
      Reader var2 = null;
      this.mChIdx = 0;
      this.mChLen = 0;
      this.mChars = this.mInp.chars;
      this.mInp.src = null;
      if (var1.getCharacterStream() != null) {
         var2 = var1.getCharacterStream();
         this.xml(var2);
      } else if (var1.getByteStream() != null) {
         if (var1.getEncoding() != null) {
            String var3 = var1.getEncoding().toUpperCase();
            if (var3.equals("UTF-16")) {
               var2 = this.bom(var1.getByteStream(), 'U');
            } else {
               var2 = this.enc(var3, var1.getByteStream());
            }

            this.xml(var2);
         } else {
            var2 = this.bom(var1.getByteStream(), ' ');
            if (var2 == null) {
               var2 = this.enc("UTF-8", var1.getByteStream());
               var2 = this.enc(this.xml(var2), var1.getByteStream());
            } else {
               this.xml(var2);
            }
         }
      } else {
         this.panic("");
      }

      this.mInp.src = var2;
      this.mInp.pubid = var1.getPublicId();
      this.mInp.sysid = var1.getSystemId();
   }

   private Reader bom(InputStream var1, char var2) throws SAXException, IOException {
      int var3 = var1.read();
      char[] var10000;
      char var10003;
      switch(var3) {
      case -1:
         var10000 = this.mChars;
         var10003 = this.mChIdx;
         this.mChIdx = (char)(var10003 + 1);
         var10000[var10003] = '\uffff';
         return new ReaderUTF8(var1);
      case 239:
         if (var2 == 'U') {
            this.panic("");
         }

         if (var1.read() != 187) {
            this.panic("");
         }

         if (var1.read() != 191) {
            this.panic("");
         }

         return new ReaderUTF8(var1);
      case 254:
         if (var1.read() != 255) {
            this.panic("");
         }

         return new ReaderUTF16(var1, 'b');
      case 255:
         if (var1.read() != 254) {
            this.panic("");
         }

         return new ReaderUTF16(var1, 'l');
      default:
         if (var2 == 'U') {
            this.panic("");
         }

         switch(var3 & 240) {
         case 192:
         case 208:
            var10000 = this.mChars;
            var10003 = this.mChIdx;
            this.mChIdx = (char)(var10003 + 1);
            var10000[var10003] = (char)((var3 & 31) << 6 | var1.read() & 63);
            break;
         case 224:
            var10000 = this.mChars;
            var10003 = this.mChIdx;
            this.mChIdx = (char)(var10003 + 1);
            var10000[var10003] = (char)((var3 & 15) << 12 | (var1.read() & 63) << 6 | var1.read() & 63);
            break;
         case 240:
            throw new UnsupportedEncodingException();
         default:
            var10000 = this.mChars;
            var10003 = this.mChIdx;
            this.mChIdx = (char)(var10003 + 1);
            var10000[var10003] = (char)var3;
         }

         return null;
      }
   }

   private String xml(Reader var1) throws SAXException, IOException {
      String var2 = null;
      String var3 = "UTF-8";
      short var6;
      if (this.mChIdx != 0) {
         var6 = (short)(this.mChars[0] == '<' ? 1 : -1);
      } else {
         var6 = 0;
      }

      while(true) {
         char var4;
         while(var6 >= 0) {
            int var5;
            var4 = (var5 = var1.read()) >= 0 ? (char)var5 : '\uffff';
            char[] var10000 = this.mChars;
            char var10003 = this.mChIdx;
            this.mChIdx = (char)(var10003 + 1);
            var10000[var10003] = var4;
            switch(var6) {
            case 0:
               switch(var4) {
               case '<':
                  var6 = 1;
                  continue;
               case '\ufeff':
                  var4 = (var5 = var1.read()) >= 0 ? (char)var5 : '\uffff';
                  this.mChars[this.mChIdx - 1] = var4;
                  var6 = (short)(var4 == '<' ? 1 : -1);
                  continue;
               default:
                  var6 = -1;
                  continue;
               }
            case 1:
               var6 = (short)(var4 == '?' ? 2 : -1);
               break;
            case 2:
               var6 = (short)(var4 != 'x' && var4 != 'X' ? -1 : 3);
               break;
            case 3:
               var6 = (short)(var4 != 'm' && var4 != 'M' ? -1 : 4);
               break;
            case 4:
               var6 = (short)(var4 != 'l' && var4 != 'L' ? -1 : 5);
               break;
            case 5:
               switch(var4) {
               case '\t':
               case '\n':
               case '\r':
               case ' ':
                  var6 = 6;
                  continue;
               default:
                  var6 = -1;
                  continue;
               }
            case 6:
               if (var4 == '?') {
                  var6 = 7;
               }
               break;
            case 7:
               var6 = (short)(var4 != '>' ? 6 : -2);
               break;
            default:
               this.panic("");
            }
         }

         this.mChLen = this.mChIdx;
         this.mChIdx = 0;
         if (var6 == -1) {
            return var3;
         }

         this.mChIdx = 5;
         byte var7 = 0;

         while(var7 >= 0) {
            var4 = this.next();
            switch(var7) {
            case 0:
               if (this.chtyp(var4) != ' ') {
                  this.back();
                  var7 = 1;
               }
               break;
            case 1:
            case 2:
            case 3:
               switch(this.chtyp(var4)) {
               case ' ':
                  continue;
               case '?':
                  if (var7 == 1) {
                     this.panic("");
                  }

                  this.back();
                  var7 = 4;
                  continue;
               case 'A':
               case '_':
               case 'a':
                  this.back();
                  var2 = this.name(false).toLowerCase();
                  if ("version".equals(var2)) {
                     if (var7 != 1) {
                        this.panic("");
                     }

                     if (!"1.0".equals(this.eqstr('='))) {
                        this.panic("");
                     }

                     var7 = 2;
                  } else if ("encoding".equals(var2)) {
                     if (var7 != 2) {
                        this.panic("");
                     }

                     var3 = this.eqstr('=').toUpperCase();
                     var7 = 3;
                  } else if ("standalone".equals(var2)) {
                     if (var7 == 1) {
                        this.panic("");
                     }

                     var2 = this.eqstr('=');
                     var7 = 4;
                  } else {
                     this.panic("");
                  }
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            case 4:
               switch(this.chtyp(var4)) {
               case ' ':
                  continue;
               case '?':
                  if (this.next() != '>') {
                     this.panic("");
                  }

                  if (this.mSt == 0) {
                     this.mSt = 1;
                  }

                  var7 = -1;
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            default:
               this.panic("");
            }
         }

         return var3;
      }
   }

   private Reader enc(String var1, InputStream var2) throws UnsupportedEncodingException {
      if (var1.equals("UTF-8")) {
         return new ReaderUTF8(var2);
      } else if (var1.equals("UTF-16LE")) {
         return new ReaderUTF16(var2, 'l');
      } else {
         return (Reader)(var1.equals("UTF-16BE") ? new ReaderUTF16(var2, 'b') : new InputStreamReader(var2, var1));
      }
   }

   private void push(Input var1) {
      this.mInp.chLen = this.mChLen;
      this.mInp.chIdx = this.mChIdx;
      var1.next = this.mInp;
      this.mInp = var1;
      this.mChars = var1.chars;
      this.mChLen = var1.chLen;
      this.mChIdx = var1.chIdx;
   }

   private void pop() {
      if (this.mInp.src != null) {
         try {
            this.mInp.src.close();
         } catch (IOException var2) {
         }

         this.mInp.src = null;
      }

      this.mInp = this.mInp.next;
      if (this.mInp != null) {
         this.mChars = this.mInp.chars;
         this.mChLen = this.mInp.chLen;
         this.mChIdx = this.mInp.chIdx;
      } else {
         this.mChars = null;
         this.mChLen = 0;
         this.mChIdx = 0;
      }

   }

   private char chtyp(char var1) throws SAXException {
      if (var1 < 128) {
         return (char)asctyp[var1];
      } else {
         if (var1 == '\uffff') {
            this.panic("");
         }

         return 'X';
      }
   }

   private char next() throws IOException {
      if (this.mChIdx >= this.mChLen) {
         if (this.mInp.src == null) {
            this.pop();
            return this.next();
         }

         int var1 = this.mInp.src.read(this.mChars, 0, this.mChars.length);
         if (var1 < 0) {
            if (this.mInp != this.mDoc) {
               this.pop();
               return this.next();
            }

            this.mChars[0] = '\uffff';
            this.mChLen = 1;
         } else {
            this.mChLen = (char)var1;
         }

         this.mChIdx = 0;
      }

      char[] var10000 = this.mChars;
      char var10003 = this.mChIdx;
      this.mChIdx = (char)(var10003 + 1);
      return var10000[var10003];
   }

   private void back() throws SAXException {
      if (this.mChIdx <= 0) {
         this.panic("");
      }

      --this.mChIdx;
   }

   private Pair find(Pair var1, char[] var2) {
      for(Pair var3 = var1; var3 != null; var3 = var3.next) {
         if (var3.eqname(var2)) {
            return var3;
         }
      }

      return null;
   }

   private Pair pair(Pair var1) {
      Pair var2;
      if (this.mDltd != null) {
         var2 = this.mDltd;
         this.mDltd = var2.next;
      } else {
         var2 = new Pair();
      }

      var2.next = var1;
      return var2;
   }

   private Pair del(Pair var1) {
      Pair var2 = var1.next;
      var1.name = null;
      var1.value = null;
      var1.chars = null;
      var1.list = null;
      var1.next = this.mDltd;
      this.mDltd = var1;
      return var2;
   }

   static {
      NONS[0] = 0;
      XML = new char[4];
      XML[0] = 4;
      XML[1] = 'x';
      XML[2] = 'm';
      XML[3] = 'l';
      XMLNS = new char[6];
      XMLNS[0] = 6;
      XMLNS[1] = 'x';
      XMLNS[2] = 'm';
      XMLNS[3] = 'l';
      XMLNS[4] = 'n';
      XMLNS[5] = 's';
      asctyp = new byte[128];

      short var0;
      for(var0 = 0; var0 <= 31; asctyp[var0++] = 122) {
      }

      asctyp[9] = 32;
      asctyp[13] = 32;

      for(asctyp[10] = 32; var0 <= 47; ++var0) {
         asctyp[var0] = (byte)var0;
      }

      while(var0 <= 57) {
         asctyp[var0++] = 100;
      }

      while(var0 <= 64) {
         asctyp[var0] = (byte)var0;
         ++var0;
      }

      while(var0 <= 90) {
         asctyp[var0++] = 65;
      }

      while(var0 <= 96) {
         asctyp[var0] = (byte)var0;
         ++var0;
      }

      while(var0 <= 122) {
         asctyp[var0++] = 97;
      }

      while(var0 <= 127) {
         asctyp[var0] = (byte)var0;
         ++var0;
      }

   }
}
