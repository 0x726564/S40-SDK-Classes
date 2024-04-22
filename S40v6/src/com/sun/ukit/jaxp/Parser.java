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
   private int mBuffIdx;
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

   public Parser(boolean nsaware) {
      this.mIsNSAware = nsaware;
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

   public void parse(InputStream src, DefaultHandler handler) throws SAXException, IOException {
      if (src != null && handler != null) {
         this.parse(new InputSource(src), handler);
      } else {
         throw new IllegalArgumentException("");
      }
   }

   public void parse(InputSource is, DefaultHandler handler) throws SAXException, IOException {
      if (is != null && handler != null) {
         this.mHand = handler;
         this.mInp = new Input((short)512);
         this.setinp(is);
         this.parse(handler);
      } else {
         throw new IllegalArgumentException("");
      }
   }

   private void parse(DefaultHandler handler) throws SAXException, IOException {
      try {
         this.mPEnt = new Hashtable();
         this.mEnt = new Hashtable();
         this.mDoc = this.mInp;
         this.mChars = this.mInp.chars;
         this.mHand.setDocumentLocator(this);
         this.mHand.startDocument();
         this.mSt = 1;

         char ch;
         while((ch = this.next()) != '\uffff') {
            switch(this.chtyp(ch)) {
            case ' ':
               break;
            case '<':
               ch = this.next();
               switch(ch) {
               case '!':
                  ch = this.next();
                  this.back();
                  if (ch == '-') {
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
      Pair psid = null;
      if (!"DOCTYPE".equals(this.name(false))) {
         this.panic("");
      }

      this.mSt = 2;
      byte st = 0;

      while(st >= 0) {
         char ch = this.next();
         switch(st) {
         case 0:
            if (this.chtyp(ch) != ' ') {
               this.back();
               this.name(this.mIsNSAware);
               this.wsskip();
               st = 1;
            }
            break;
         case 1:
            switch(this.chtyp(ch)) {
            case '>':
               this.back();
               st = 3;
               break;
            case 'A':
               this.back();
               psid = this.pubsys(' ');
               st = 2;
               break;
            case '[':
               this.back();
               st = 2;
               break;
            default:
               this.panic("");
            }

            if (psid != null) {
               if (this.mHand.resolveEntity(psid.name, psid.value) != null) {
                  this.panic("");
               }

               this.del(psid);
               this.mHand.skippedEntity("[dtd]");
            }
            break;
         case 2:
            switch(this.chtyp(ch)) {
            case ' ':
               continue;
            case '>':
               this.back();
               st = 3;
               continue;
            case '[':
               this.dtdint();
               st = 3;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 3:
            switch(this.chtyp(ch)) {
            case ' ':
               continue;
            case '>':
               st = -1;
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
      byte st = 0;

      while(st >= 0) {
         char ch = this.next();
         switch(st) {
         case 0:
            switch(this.chtyp(ch)) {
            case ' ':
               continue;
            case '%':
               this.pent(' ');
               continue;
            case '<':
               ch = this.next();
               switch(ch) {
               case '!':
                  ch = this.next();
                  this.back();
                  if (ch == '-') {
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

                     st = 1;
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
               st = -1;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 1:
            switch(ch) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
               continue;
            case '>':
               st = 0;
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
      String str = null;
      char[] val = null;
      Input inp = null;
      Pair ids = null;
      byte st = 0;

      while(true) {
         while(true) {
            while(st >= 0) {
               char ch = this.next();
               char[] val;
               switch(st) {
               case 0:
                  switch(this.chtyp(ch)) {
                  case ' ':
                     continue;
                  case '%':
                     ch = this.next();
                     this.back();
                     if (this.chtyp(ch) == ' ') {
                        this.wsskip();
                        str = this.name(false);
                        switch(this.chtyp(this.wsskip())) {
                        case '"':
                        case '\'':
                           this.bqstr('-');
                           val = new char[this.mBuffIdx + 1];
                           System.arraycopy(this.mBuff, 1, val, 1, val.length - 1);
                           val[0] = ' ';
                           inp = new Input(val);
                           inp.pubid = this.mInp.pubid;
                           inp.sysid = this.mInp.sysid;
                           this.mPEnt.put(str, inp);
                           st = -1;
                           continue;
                        case 'A':
                           ids = this.pubsys(' ');
                           if (this.wsskip() == '>') {
                              inp = new Input();
                              inp.pubid = ids.name;
                              inp.sysid = ids.value;
                              this.mPEnt.put(str, inp);
                           } else {
                              this.panic("");
                           }

                           this.del(ids);
                           st = -1;
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
                     str = this.name(false);
                     st = 1;
                     continue;
                  }
               case 1:
                  switch(this.chtyp(ch)) {
                  case ' ':
                     continue;
                  case '"':
                  case '\'':
                     this.back();
                     this.bqstr('-');
                     if (this.mEnt.get(str) == null) {
                        val = new char[this.mBuffIdx];
                        System.arraycopy(this.mBuff, 1, val, 0, val.length);
                        inp = new Input(val);
                        inp.pubid = this.mInp.pubid;
                        inp.sysid = this.mInp.sysid;
                        this.mEnt.put(str, inp);
                     }

                     st = -1;
                     continue;
                  case 'A':
                     this.back();
                     ids = this.pubsys(' ');
                     switch(this.wsskip()) {
                     case '>':
                        inp = new Input();
                        inp.pubid = ids.name;
                        inp.sysid = ids.value;
                        this.mEnt.put(str, inp);
                        break;
                     case 'N':
                        if ("NDATA".equals(this.name(false))) {
                           this.wsskip();
                           this.mHand.unparsedEntityDecl(str, ids.name, ids.value, this.name(false));
                           break;
                        }
                     default:
                        this.panic("");
                     }

                     this.del(ids);
                     st = -1;
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
         char ch = this.next();
         switch(ch) {
         case '#':
            if (!"PCDATA".equals(this.name(false))) {
               this.panic("");
            }
            break;
         case '>':
            this.back();
            return;
         case '\uffff':
            this.panic("");
         }
      }
   }

   private void dtdattl() throws SAXException, IOException {
      char[] elmqn = null;
      Pair elm = null;
      byte st = 0;

      while(st >= 0) {
         char ch = this.next();
         switch(st) {
         case 0:
            switch(this.chtyp(ch)) {
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
               char[] elmqn = this.qname(this.mIsNSAware);
               elm = this.find(this.mAttL, elmqn);
               if (elm == null) {
                  elm = this.pair(this.mAttL);
                  elm.chars = elmqn;
                  this.mAttL = elm;
               }

               st = 1;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 1:
            switch(this.chtyp(ch)) {
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
               this.dtdatt(elm);
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

   private void dtdatt(Pair elm) throws SAXException, IOException {
      char[] attqn = null;
      Pair att = null;
      byte st = 0;

      while(st >= 0) {
         char ch = this.next();
         switch(st) {
         case 0:
            switch(this.chtyp(ch)) {
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
               char[] attqn = this.qname(this.mIsNSAware);
               att = this.find(elm.list, attqn);
               if (att == null) {
                  att = this.pair(elm.list);
                  att.chars = attqn;
                  elm.list = att;
               } else {
                  att.id = 'c';
                  if (att.list != null) {
                     this.del(att.list);
                  }

                  att.list = null;
               }

               this.wsskip();
               st = 1;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 1:
            switch(ch) {
            case ' ':
               continue;
            case '%':
               this.pent(' ');
               continue;
            case '(':
               this.back();
               att.id = 'u';
               st = 2;
               continue;
            default:
               this.back();
               this.bntok();
               att.id = this.bkeyword();
               switch(att.id) {
               case 'N':
               case 'R':
               case 'T':
               case 'c':
               case 'i':
               case 'n':
               case 'r':
               case 't':
                  this.wsskip();
                  st = 4;
                  continue;
               case 'o':
                  if (this.wsskip() != '(') {
                     this.panic("");
                  }

                  st = 2;
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            }
         case 2:
            if (ch != '(') {
               this.panic("");
            }

            ch = this.wsskip();
            switch(this.chtyp(ch)) {
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
               switch(att.id) {
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
               st = 3;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 3:
            switch(ch) {
            case '%':
               this.pent(' ');
               continue;
            case ')':
               this.wsskip();
               st = 4;
               continue;
            case '|':
               this.wsskip();
               switch(att.id) {
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
            switch(ch) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
               continue;
            case '"':
            case '\'':
               this.back();
               st = 5;
               continue;
            case '#':
               this.bntok();
               switch(this.bkeyword()) {
               case 'F':
                  switch(this.wsskip()) {
                  case '"':
                  case '\'':
                     st = 5;
                     continue;
                  default:
                     this.panic("");
                     continue;
                  }
               case 'I':
               case 'Q':
                  st = -1;
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
               st = -1;
               continue;
            }
         case 5:
            switch(ch) {
            case '"':
            case '\'':
               this.back();
               this.bqstr('-');
               att.list = this.pair((Pair)null);
               att.list.chars = new char[att.chars.length + this.mBuffIdx + 3];
               System.arraycopy(att.chars, 1, att.list.chars, 0, att.chars.length - 1);
               att.list.chars[att.chars.length - 1] = '=';
               att.list.chars[att.chars.length] = ch;
               System.arraycopy(this.mBuff, 1, att.list.chars, att.chars.length + 1, this.mBuffIdx);
               att.list.chars[att.chars.length + this.mBuffIdx + 1] = ch;
               att.list.chars[att.chars.length + this.mBuffIdx + 2] = ' ';
               st = -1;
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
      String name = this.name(false);
      this.wsskip();
      Pair ids = this.pubsys('N');
      this.mHand.notationDecl(name, ids.name, ids.value);
      this.del(ids);
   }

   private void elm() throws SAXException, IOException {
      Pair pref = this.mPref;
      this.mElm = this.pair(this.mElm);
      this.mElm.chars = this.qname(this.mIsNSAware);
      this.mElm.name = this.mElm.local();
      Pair elm = this.find(this.mAttL, this.mElm.chars);
      this.mAttrIdx = 0;
      Pair att = this.pair((Pair)null);
      att.list = elm != null ? elm.list : null;
      this.attr(att);
      this.del(att);
      this.mBuffIdx = -1;
      if (this.mAttrs.checkDuplicates()) {
         this.panic("");
      }

      int st = 0;

      while(true) {
         while(true) {
            while(st >= 0) {
               char ch = this.next();
               switch(st) {
               case 0:
               case 1:
                  switch(ch) {
                  case '/':
                     if (st != 0) {
                        this.panic("");
                     }

                     st = 1;
                     continue;
                  case '>':
                     if (this.mIsNSAware) {
                        this.mElm.value = this.rslv(this.mElm.chars);
                        this.mHand.startElement(this.mElm.value, this.mElm.name, "", this.mAttrs);
                     } else {
                        this.mHand.startElement("", "", this.mElm.name, this.mAttrs);
                     }

                     this.mItems = null;
                     st = st == 0 ? 2 : -1;
                     continue;
                  default:
                     this.panic("");
                     continue;
                  }
               case 2:
                  switch(this.chtyp(ch)) {
                  case ' ':
                     this.bappend(ch);
                     continue;
                  case '<':
                     this.bflash();
                  default:
                     this.back();
                     st = 3;
                     continue;
                  }
               case 3:
                  switch(ch) {
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
                     label123:
                     switch(this.next()) {
                     case '!':
                        ch = this.next();
                        this.back();
                        switch(ch) {
                        case '-':
                           this.comm();
                           break label123;
                        case '[':
                           this.cdat();
                           break label123;
                        default:
                           this.panic("");
                           break label123;
                        }
                     case '/':
                        this.mBuffIdx = -1;
                        this.bname(this.mIsNSAware);
                        char[] chars = this.mElm.chars;
                        if (chars.length == this.mBuffIdx + 1) {
                           for(char i = 1; i <= this.mBuffIdx; ++i) {
                              if (chars[i] != this.mBuff[i]) {
                                 this.panic("");
                              }
                           }
                        } else {
                           this.panic("");
                        }

                        if (this.wsskip() != '>') {
                           this.panic("");
                        }

                        ch = this.next();
                        st = -1;
                        break;
                     case '?':
                        this.pi();
                        break;
                     default:
                        this.back();
                        this.elm();
                     }

                     this.mBuffIdx = -1;
                     if (st != -1) {
                        st = 2;
                     }
                     continue;
                  case '?':
                     this.bappend('?');
                     if (this.next() == '>') {
                        this.panic("");
                     } else {
                        this.back();
                     }
                     continue;
                  case ']':
                     this.bappend(']');
                     if (this.next() == ']') {
                        if (this.next() == '>') {
                           this.panic("");
                        } else {
                           this.back();
                        }
                     } else {
                        this.back();
                     }
                     continue;
                  default:
                     this.bappend(ch);
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

            for(this.mElm = this.del(this.mElm); this.mPref != pref; this.mPref = this.del(this.mPref)) {
               this.mHand.endPrefixMapping(this.mPref.name);
            }

            return;
         }
      }
   }

   private void attr(Pair att) throws SAXException, IOException {
      Pair next = null;
      char norm = 'c';

      try {
         Pair def;
         switch(this.wsskip()) {
         case '/':
         case '>':
            def = att.list;

            for(; def != null; def = def.next) {
               if (def.list != null) {
                  Pair act;
                  for(act = att.next; act != null && !act.eqname(def.chars); act = act.next) {
                  }

                  if (act == null) {
                     this.push(new Input(def.list.chars));
                     this.attr(att);
                     return;
                  }
               }
            }

            this.mAttrs.setLength(this.mAttrIdx);
            this.mItems = this.mAttrs.mItems;
            return;
         default:
            att.chars = this.qname(this.mIsNSAware);
            att.name = att.local();
            String type = "CDATA";
            if (att.list != null) {
               def = this.find(att.list, att.chars);
               if (def != null) {
                  switch(def.id) {
                  case 'N':
                     type = "ENTITIES";
                     norm = 'i';
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
                     type = "IDREFS";
                     norm = 'i';
                     break;
                  case 'T':
                     type = "NMTOKENS";
                     norm = 'i';
                     break;
                  case 'c':
                     norm = 'c';
                     break;
                  case 'i':
                     type = "ID";
                     norm = 'i';
                     break;
                  case 'n':
                     type = "ENTITY";
                     norm = 'i';
                     break;
                  case 'o':
                     type = "NOTATION";
                     norm = 'i';
                     break;
                  case 'r':
                     type = "IDREF";
                     norm = 'i';
                     break;
                  case 't':
                     type = "NMTOKEN";
                     norm = 'i';
                     break;
                  case 'u':
                     type = "NMTOKEN";
                     norm = 'i';
                  }
               }
            }

            this.wsskip();
            if (this.next() != '=') {
               this.panic("");
            }

            this.bqstr(norm);
            String val = new String(this.mBuff, 1, this.mBuffIdx);
            if (this.mIsNSAware && this.isdecl(att, val)) {
               this.mHand.startPrefixMapping(this.mPref.name, this.mPref.value);
               next = this.pair(att);
               next.list = att.list;
               this.attr(next);
            } else {
               ++this.mAttrIdx;
               next = this.pair(att);
               next.list = att.list;
               this.attr(next);
               --this.mAttrIdx;
               char idx = (char)(this.mAttrIdx << 3);
               this.mItems[idx + 1] = att.qname();
               this.mItems[idx + 2] = att.name;
               this.mItems[idx + 3] = val;
               this.mItems[idx + 4] = type;
               this.mItems[idx + 0] = att.chars[0] != 0 ? this.rslv(att.chars) : "";
            }

         }
      } finally {
         if (next != null) {
            this.del(next);
         }

      }
   }

   private void comm() throws SAXException, IOException {
      if (this.mSt == 0) {
         this.mSt = 1;
      }

      int st = 0;

      while(st >= 0) {
         char ch = this.next();
         switch(st) {
         case 0:
            if (ch == '-') {
               st = 1;
            } else {
               this.panic("");
            }
            break;
         case 1:
            if (ch == '-') {
               st = 2;
            } else {
               this.panic("");
            }
            break;
         case 2:
            if (ch == '-') {
               st = 3;
            } else if (ch == '\uffff') {
               this.panic("");
            }
            break;
         case 3:
            st = ch == '-' ? 4 : 2;
            break;
         case 4:
            if (ch == '>') {
               st = -1;
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
      String str = null;
      this.mBuffIdx = -1;
      byte st = 0;

      while(st >= 0) {
         char ch = this.next();
         switch(st) {
         case 0:
            switch(this.chtyp(ch)) {
            case ' ':
               str = "";
               st = 2;
               continue;
            case 'A':
            case 'X':
            case '_':
            case 'a':
               this.back();
               str = this.name(false);
               st = 1;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 1:
            if (this.chtyp(ch) != ' ') {
               this.back();
               if (this.mXml.name.equals(str.toLowerCase())) {
                  this.panic("");
               } else if (ch == '=') {
                  this.panic("");
               } else {
                  if (this.mSt == 0) {
                     this.mSt = 1;
                  }

                  st = 2;
               }

               this.mBuffIdx = -1;
            }
            break;
         case 2:
            if (ch == '?') {
               st = 3;
            } else if (ch == '\uffff') {
               this.panic("");
            } else {
               this.bappend(ch);
            }
            break;
         case 3:
            if (ch == '>') {
               this.mHand.processingInstruction(str, new String(this.mBuff, 0, this.mBuffIdx + 1));
               st = -1;
            } else {
               this.bappend('?');
               this.bappend(ch);
               st = 2;
            }
            break;
         default:
            this.panic("");
         }
      }

   }

   private void cdat() throws SAXException, IOException {
      this.mBuffIdx = -1;
      byte st = 0;

      while(st >= 0) {
         char ch = this.next();
         switch(st) {
         case 0:
            if (ch == '[') {
               st = 1;
            } else {
               this.panic("");
            }
            break;
         case 1:
            if (this.chtyp(ch) == 'A') {
               this.bappend(ch);
            } else {
               if (!"CDATA".equals(new String(this.mBuff, 0, this.mBuffIdx + 1))) {
                  this.panic("");
               }

               this.back();
               st = 2;
            }
            break;
         case 2:
            if (ch != '[') {
               this.panic("");
            }

            this.mBuffIdx = -1;
            st = 3;
            break;
         case 3:
            if (ch == '\uffff') {
               this.panic("");
            } else if (ch != ']') {
               this.bappend(ch);
            } else {
               st = 4;
            }
            break;
         case 4:
            if (ch == '\uffff') {
               this.panic("");
            } else if (ch != ']') {
               this.bappend(']');
               this.bappend(ch);
               st = 3;
            } else {
               st = 5;
            }
            break;
         case 5:
            if (ch == '\uffff') {
               this.panic("");
            } else if (ch != '>') {
               this.bappend(']');
               this.bappend(']');
               this.bappend(ch);
               st = 3;
            } else {
               this.bflash();
               st = -1;
            }
            break;
         default:
            this.panic("");
         }
      }

   }

   private String name(boolean ns) throws SAXException, IOException {
      this.mBuffIdx = -1;
      this.bname(ns);
      return new String(this.mBuff, 1, this.mBuffIdx);
   }

   private char[] qname(boolean ns) throws SAXException, IOException {
      this.mBuffIdx = -1;
      this.bname(ns);
      char[] chars = new char[this.mBuffIdx + 1];
      System.arraycopy(this.mBuff, 0, chars, 0, this.mBuffIdx + 1);
      return chars;
   }

   private void pubsys(Input inp) throws SAXException, IOException {
      Pair pair = this.pubsys(' ');
      inp.pubid = pair.name;
      inp.sysid = pair.value;
      this.del(pair);
   }

   private Pair pubsys(char flag) throws SAXException, IOException {
      Pair ids = this.pair((Pair)null);
      String str = this.name(false);
      if ("PUBLIC".equals(str)) {
         this.bqstr('i');
         ids.name = new String(this.mBuff, 1, this.mBuffIdx);
         switch(this.wsskip()) {
         case '"':
         case '\'':
            this.bqstr(' ');
            ids.value = new String(this.mBuff, 1, this.mBuffIdx);
            break;
         default:
            if (flag != 'N') {
               this.panic("");
            }

            ids.value = null;
         }

         return ids;
      } else if ("SYSTEM".equals(str)) {
         ids.name = null;
         this.bqstr(' ');
         ids.value = new String(this.mBuff, 1, this.mBuffIdx);
         return ids;
      } else {
         this.panic("");
         return null;
      }
   }

   private String eqstr(char flag) throws SAXException, IOException {
      if (flag == '=') {
         this.wsskip();
         if (this.next() != '=') {
            this.panic("");
         }
      }

      this.bqstr(flag);
      return new String(this.mBuff, 1, this.mBuffIdx);
   }

   private void ent(char flag) throws SAXException, IOException {
      short idx = (short)(this.mBuffIdx + 1);
      Input inp = null;
      String str = null;
      this.mESt = 256;
      this.bappend('&');
      byte st = 0;

      while(true) {
         while(st >= 0) {
            char ch = this.next();
            switch(st) {
            case 0:
            case 1:
               switch(this.chtyp(ch)) {
               case '#':
                  if (st != 0) {
                     this.panic("");
                  }

                  st = 2;
                  continue;
               case '-':
               case '.':
               case 'd':
                  if (st != 1) {
                     this.panic("");
                  }
               case 'A':
               case 'X':
               case '_':
               case 'a':
                  this.bappend(ch);
                  this.eappend(ch);
                  st = 1;
                  continue;
               case ':':
                  if (this.mIsNSAware) {
                     this.panic("");
                  }

                  this.bappend(ch);
                  this.eappend(ch);
                  st = 1;
                  continue;
               case ';':
                  if (this.mESt < 256) {
                     this.mBuffIdx = (short)(idx - 1);
                     this.bappend(this.mESt);
                     st = -1;
                  } else if (this.mSt == 2) {
                     this.bappend(';');
                     st = -1;
                  } else {
                     str = new String(this.mBuff, idx + 1, this.mBuffIdx - idx);
                     inp = (Input)this.mEnt.get(str);
                     this.mBuffIdx = (short)(idx - 1);
                     if (inp != null) {
                        if (inp.chars == null) {
                           InputSource is = this.mHand.resolveEntity(inp.pubid, inp.sysid);
                           if (is != null) {
                              this.push(new Input((short)512));
                              this.setinp(is);
                              this.mInp.pubid = inp.pubid;
                              this.mInp.sysid = inp.sysid;
                           } else {
                              this.bflash();
                              if (flag != 'c') {
                                 this.panic("");
                              }

                              this.mHand.skippedEntity(str);
                           }
                        } else {
                           this.push(inp);
                        }
                     } else {
                        this.bflash();
                        if (flag != 'c') {
                           this.panic("");
                        }

                        this.mHand.skippedEntity(str);
                     }

                     st = -1;
                  }
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            case 2:
               switch(this.chtyp(ch)) {
               case ';':
                  try {
                     ch = (char)Short.parseShort(new String(this.mBuff, idx + 1, this.mBuffIdx - idx), 10);
                  } catch (NumberFormatException var9) {
                     this.panic("");
                  }

                  this.mBuffIdx = (short)(idx - 1);
                  this.bappend(ch);
                  st = -1;
                  continue;
               case 'a':
                  if (this.mBuffIdx == idx && ch == 'x') {
                     st = 3;
                     continue;
                  }
               default:
                  this.panic("");
                  continue;
               case 'd':
                  this.bappend(ch);
                  continue;
               }
            case 3:
               switch(this.chtyp(ch)) {
               case ';':
                  try {
                     ch = (char)Short.parseShort(new String(this.mBuff, idx + 1, this.mBuffIdx - idx), 16);
                  } catch (NumberFormatException var8) {
                     this.panic("");
                  }

                  this.mBuffIdx = (short)(idx - 1);
                  this.bappend(ch);
                  st = -1;
                  continue;
               case 'A':
               case 'a':
               case 'd':
                  this.bappend(ch);
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

   private void pent(char flag) throws SAXException, IOException {
      short idx = (short)(this.mBuffIdx + 1);
      Input inp = null;
      String str = null;
      this.bappend('%');
      if (this.mSt == 2) {
         this.bname(false);
         str = new String(this.mBuff, idx + 2, this.mBuffIdx - idx - 1);
         if (this.next() != ';') {
            this.panic("");
         }

         inp = (Input)this.mPEnt.get(str);
         this.mBuffIdx = (short)(idx - 1);
         if (inp != null) {
            if (inp.chars == null) {
               InputSource is = this.mHand.resolveEntity(inp.pubid, inp.sysid);
               if (is != null) {
                  if (flag != '-') {
                     this.bappend(' ');
                  }

                  this.push(new Input((short)512));
                  this.setinp(is);
                  this.mInp.pubid = inp.pubid;
                  this.mInp.sysid = inp.sysid;
               } else {
                  this.mHand.skippedEntity("%" + str);
               }
            } else {
               if (flag == '-') {
                  inp.chIdx = 1;
               } else {
                  this.bappend(' ');
                  inp.chIdx = 0;
               }

               this.push(inp);
            }
         } else {
            this.mHand.skippedEntity("%" + str);
         }

      }
   }

   private boolean isdecl(Pair name, String value) {
      if (name.chars[0] == 0) {
         if ("xmlns".equals(name.name)) {
            this.mPref = this.pair(this.mPref);
            this.mPref.value = value;
            this.mPref.name = "";
            this.mPref.chars = NONS;
            return true;
         }
      } else if (name.eqpref(XMLNS)) {
         int len = name.name.length();
         this.mPref = this.pair(this.mPref);
         this.mPref.value = value;
         this.mPref.name = name.name;
         this.mPref.chars = new char[len + 1];
         this.mPref.chars[0] = (char)(len + 1);
         name.name.getChars(0, len, this.mPref.chars, 1);
         return true;
      }

      return false;
   }

   private String rslv(char[] qname) throws SAXException {
      for(Pair pref = this.mPref; pref != null; pref = pref.next) {
         if (pref.eqpref(qname)) {
            return pref.value;
         }
      }

      this.panic("");
      return null;
   }

   private char wsskip() throws SAXException, IOException {
      while(true) {
         char ch = this.next();
         switch(ch) {
         case '\t':
         case '\n':
         case '\r':
         case ' ':
            break;
         case '\uffff':
            this.panic("");
         default:
            this.back();
            return ch;
         }
      }
   }

   private void panic(String msg) throws SAXException {
      SAXParseException saxpe = new SAXParseException(msg, this);
      this.mHand.fatalError(saxpe);
   }

   private void bname(boolean ns) throws SAXException, IOException {
      char pos = (char)(this.mBuffIdx + 1);
      char idx = pos;
      short st = (short)(ns ? 0 : 2);
      this.bappend('\u0000');

      while(st >= 0) {
         char ch = this.next();
         switch(st) {
         case 0:
         case 2:
            switch(this.chtyp(ch)) {
            case ':':
               this.back();
               ++st;
               continue;
            case 'A':
            case 'X':
            case '_':
            case 'a':
               this.bappend(ch);
               ++st;
               continue;
            default:
               this.panic("");
               continue;
            }
         case 1:
         case 3:
            switch(this.chtyp(ch)) {
            case '-':
            case '.':
            case 'A':
            case 'X':
            case '_':
            case 'a':
            case 'd':
               this.bappend(ch);
               continue;
            case ':':
               this.bappend(ch);
               if (ns) {
                  if (idx != pos) {
                     this.panic("");
                  }

                  idx = (char)this.mBuffIdx;
                  if (st == 1) {
                     st = 2;
                  }
               }
               continue;
            default:
               this.back();
               this.mBuff[pos] = idx;
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
         char ch = this.next();
         switch(this.chtyp(ch)) {
         case '-':
         case '.':
         case ':':
         case 'A':
         case 'X':
         case '_':
         case 'a':
         case 'd':
            this.bappend(ch);
            break;
         default:
            this.back();
            return;
         }
      }
   }

   private char bkeyword() throws SAXException, IOException {
      String str = new String(this.mBuff, 1, this.mBuffIdx);
      switch(str.length()) {
      case 2:
         return (char)("ID".equals(str) ? 'i' : '?');
      case 3:
      case 4:
      default:
         break;
      case 5:
         switch(this.mBuff[1]) {
         case 'C':
            return (char)("CDATA".equals(str) ? 'c' : '?');
         case 'F':
            return (char)("FIXED".equals(str) ? 'F' : '?');
         case 'I':
            return (char)("IDREF".equals(str) ? 'r' : '?');
         default:
            return '?';
         }
      case 6:
         switch(this.mBuff[1]) {
         case 'E':
            return (char)("ENTITY".equals(str) ? 'n' : '?');
         case 'I':
            return (char)("IDREFS".equals(str) ? 'R' : '?');
         default:
            return '?';
         }
      case 7:
         switch(this.mBuff[1]) {
         case 'A':
            return (char)("ATTLIST".equals(str) ? 'a' : '?');
         case 'E':
            return (char)("ELEMENT".equals(str) ? 'e' : '?');
         case 'I':
            return (char)("IMPLIED".equals(str) ? 'I' : '?');
         case 'N':
            return (char)("NMTOKEN".equals(str) ? 't' : '?');
         default:
            return '?';
         }
      case 8:
         switch(this.mBuff[2]) {
         case 'E':
            return (char)("REQUIRED".equals(str) ? 'Q' : '?');
         case 'M':
            return (char)("NMTOKENS".equals(str) ? 'T' : '?');
         case 'N':
            return (char)("ENTITIES".equals(str) ? 'N' : '?');
         case 'O':
            return (char)("NOTATION".equals(str) ? 'o' : '?');
         }
      }

      return '?';
   }

   private void bqstr(char flag) throws SAXException, IOException {
      Input inp = this.mInp;
      this.mBuffIdx = -1;
      this.bappend('\u0000');
      byte st = 0;

      while(true) {
         while(true) {
            while(st >= 0) {
               char ch = this.next();
               switch(st) {
               case 0:
                  switch(ch) {
                  case '\t':
                  case '\n':
                  case '\r':
                  case ' ':
                     continue;
                  case '"':
                     st = 3;
                     continue;
                  case '\'':
                     st = 2;
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
                  switch(ch) {
                  case '\r':
                     if (flag != ' ') {
                        if (this.next() != '\n') {
                           this.back();
                        }

                        ch = '\n';
                     }
                  default:
                     switch(flag) {
                     case 'c':
                        switch(ch) {
                        case '\t':
                        case '\n':
                           this.bappend(' ');
                           continue;
                        default:
                           this.bappend(ch);
                           continue;
                        }
                     case 'i':
                        switch(ch) {
                        case '\t':
                        case '\n':
                        case ' ':
                           if (this.mBuffIdx > 0 && this.mBuff[this.mBuffIdx] != ' ') {
                              this.bappend(' ');
                           }
                           continue;
                        default:
                           this.bappend(ch);
                           continue;
                        }
                     default:
                        this.bappend(ch);
                        continue;
                     }
                  case '"':
                     if (st == 3 && this.mInp == inp) {
                        st = -1;
                        break;
                     }

                     this.bappend(ch);
                     break;
                  case '%':
                     this.pent(flag);
                     break;
                  case '&':
                     this.ent(' ');
                     break;
                  case '\'':
                     if (st == 2 && this.mInp == inp) {
                        st = -1;
                        break;
                     }

                     this.bappend(ch);
                     break;
                  case '\uffff':
                     this.panic("");
                  }
               }
            }

            if (flag == 'i' && this.mBuff[this.mBuffIdx] == ' ') {
               --this.mBuffIdx;
            }

            return;
         }
      }
   }

   private void bflash() throws SAXException {
      if (this.mBuffIdx >= 0) {
         this.mHand.characters(this.mBuff, 0, this.mBuffIdx + 1);
         this.mBuffIdx = -1;
      }

   }

   private void bappend(char ch) {
      try {
         ++this.mBuffIdx;
         this.mBuff[this.mBuffIdx] = ch;
      } catch (Exception var4) {
         char[] buff = new char[this.mBuff.length << 1];
         System.arraycopy(this.mBuff, 0, buff, 0, this.mBuff.length);
         this.mBuff = buff;
         this.mBuff[this.mBuffIdx] = ch;
      }

   }

   private void eappend(char ch) {
      switch(this.mESt) {
      case '"':
      case '&':
      case '\'':
      case '<':
      case '>':
         this.mESt = 512;
         break;
      case 'Ā':
         switch(ch) {
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
         this.mESt = (char)(ch == 't' ? 60 : 512);
         break;
      case 'Ă':
         this.mESt = (char)(ch == 't' ? 62 : 512);
         break;
      case 'ă':
         switch(ch) {
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
         this.mESt = (char)(ch == 'p' ? 38 : 512);
         break;
      case 'ą':
         this.mESt = (char)(ch == 'o' ? 262 : 512);
         break;
      case 'Ć':
         this.mESt = (char)(ch == 's' ? 39 : 512);
         break;
      case 'ć':
         this.mESt = (char)(ch == 'u' ? 264 : 512);
         break;
      case 'Ĉ':
         this.mESt = (char)(ch == 'o' ? 265 : 512);
         break;
      case 'ĉ':
         this.mESt = (char)(ch == 't' ? 34 : 512);
      }

   }

   private void setinp(InputSource is) throws SAXException, IOException {
      Reader reader = null;
      this.mChIdx = 0;
      this.mChLen = 0;
      this.mChars = this.mInp.chars;
      this.mInp.src = null;
      if (is.getCharacterStream() != null) {
         reader = is.getCharacterStream();
         this.xml(reader);
      } else if (is.getByteStream() != null) {
         if (is.getEncoding() != null) {
            String encoding = is.getEncoding().toUpperCase();
            if (encoding.equals("UTF-16")) {
               reader = this.bom(is.getByteStream(), 'U');
            } else {
               reader = this.enc(encoding, is.getByteStream());
            }

            this.xml(reader);
         } else {
            reader = this.bom(is.getByteStream(), ' ');
            if (reader == null) {
               reader = this.enc("UTF-8", is.getByteStream());
               reader = this.enc(this.xml(reader), is.getByteStream());
            } else {
               this.xml(reader);
            }
         }
      } else {
         this.panic("");
      }

      this.mInp.src = reader;
      this.mInp.pubid = is.getPublicId();
      this.mInp.sysid = is.getSystemId();
   }

   private Reader bom(InputStream is, char hint) throws SAXException, IOException {
      int val = is.read();
      char[] var10000;
      char var10003;
      switch(val) {
      case -1:
         var10000 = this.mChars;
         var10003 = this.mChIdx;
         this.mChIdx = (char)(var10003 + 1);
         var10000[var10003] = '\uffff';
         return new ReaderUTF8(is);
      case 239:
         if (hint == 'U') {
            this.panic("");
         }

         if (is.read() != 187) {
            this.panic("");
         }

         if (is.read() != 191) {
            this.panic("");
         }

         return new ReaderUTF8(is);
      case 254:
         if (is.read() != 255) {
            this.panic("");
         }

         return new ReaderUTF16(is, 'b');
      case 255:
         if (is.read() != 254) {
            this.panic("");
         }

         return new ReaderUTF16(is, 'l');
      default:
         if (hint == 'U') {
            this.panic("");
         }

         switch(val & 240) {
         case 192:
         case 208:
            var10000 = this.mChars;
            var10003 = this.mChIdx;
            this.mChIdx = (char)(var10003 + 1);
            var10000[var10003] = (char)((val & 31) << 6 | is.read() & 63);
            break;
         case 224:
            var10000 = this.mChars;
            var10003 = this.mChIdx;
            this.mChIdx = (char)(var10003 + 1);
            var10000[var10003] = (char)((val & 15) << 12 | (is.read() & 63) << 6 | is.read() & 63);
            break;
         case 240:
            throw new UnsupportedEncodingException();
         default:
            var10000 = this.mChars;
            var10003 = this.mChIdx;
            this.mChIdx = (char)(var10003 + 1);
            var10000[var10003] = (char)val;
         }

         return null;
      }
   }

   private String xml(Reader reader) throws SAXException, IOException {
      String str = null;
      String enc = "UTF-8";
      short st;
      if (this.mChIdx != 0) {
         st = (short)(this.mChars[0] == '<' ? 1 : -1);
      } else {
         st = 0;
      }

      while(true) {
         char ch;
         while(st >= 0) {
            int val;
            ch = (val = reader.read()) >= 0 ? (char)val : '\uffff';
            char[] var10000 = this.mChars;
            char var10003 = this.mChIdx;
            this.mChIdx = (char)(var10003 + 1);
            var10000[var10003] = ch;
            switch(st) {
            case 0:
               switch(ch) {
               case '<':
                  st = 1;
                  continue;
               case '\ufeff':
                  ch = (val = reader.read()) >= 0 ? (char)val : '\uffff';
                  this.mChars[this.mChIdx - 1] = ch;
                  st = (short)(ch == '<' ? 1 : -1);
                  continue;
               default:
                  st = -1;
                  continue;
               }
            case 1:
               st = (short)(ch == '?' ? 2 : -1);
               break;
            case 2:
               st = (short)(ch != 'x' && ch != 'X' ? -1 : 3);
               break;
            case 3:
               st = (short)(ch != 'm' && ch != 'M' ? -1 : 4);
               break;
            case 4:
               st = (short)(ch != 'l' && ch != 'L' ? -1 : 5);
               break;
            case 5:
               switch(ch) {
               case '\t':
               case '\n':
               case '\r':
               case ' ':
                  st = 6;
                  continue;
               default:
                  st = -1;
                  continue;
               }
            case 6:
               if (ch == '?') {
                  st = 7;
               } else if (ch == '\uffff') {
                  st = -1;
               }
               break;
            case 7:
               st = (short)(ch != '>' ? 6 : -2);
               break;
            default:
               this.panic("");
            }
         }

         this.mChLen = this.mChIdx;
         this.mChIdx = 0;
         if (st == -1) {
            return enc;
         }

         this.mChIdx = 5;
         byte st = 0;

         while(st >= 0) {
            ch = this.next();
            switch(st) {
            case 0:
               if (this.chtyp(ch) != ' ') {
                  this.back();
                  st = 1;
               }
               break;
            case 1:
            case 2:
            case 3:
               switch(this.chtyp(ch)) {
               case ' ':
                  continue;
               case '?':
                  if (st == 1) {
                     this.panic("");
                  }

                  this.back();
                  st = 4;
                  continue;
               case 'A':
               case '_':
               case 'a':
                  this.back();
                  str = this.name(false).toLowerCase();
                  if ("version".equals(str)) {
                     if (st != 1) {
                        this.panic("");
                     }

                     if (!"1.0".equals(this.eqstr('='))) {
                        this.panic("");
                     }

                     st = 2;
                  } else if ("encoding".equals(str)) {
                     if (st != 2) {
                        this.panic("");
                     }

                     enc = this.eqstr('=').toUpperCase();
                     st = 3;
                  } else if ("standalone".equals(str)) {
                     if (st == 1) {
                        this.panic("");
                     }

                     str = this.eqstr('=');
                     st = 4;
                  } else {
                     this.panic("");
                  }
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            case 4:
               switch(this.chtyp(ch)) {
               case ' ':
                  continue;
               case '?':
                  if (this.next() != '>') {
                     this.panic("");
                  }

                  if (this.mSt == 0) {
                     this.mSt = 1;
                  }

                  st = -1;
                  continue;
               default:
                  this.panic("");
                  continue;
               }
            default:
               this.panic("");
            }
         }

         return enc;
      }
   }

   private Reader enc(String name, InputStream is) throws UnsupportedEncodingException {
      if (name.equals("UTF-8")) {
         return new ReaderUTF8(is);
      } else if (name.equals("UTF-16LE")) {
         return new ReaderUTF16(is, 'l');
      } else {
         return (Reader)(name.equals("UTF-16BE") ? new ReaderUTF16(is, 'b') : new InputStreamReader(is, name));
      }
   }

   private void push(Input inp) {
      this.mInp.chLen = this.mChLen;
      this.mInp.chIdx = this.mChIdx;
      inp.next = this.mInp;
      this.mInp = inp;
      this.mChars = inp.chars;
      this.mChLen = inp.chLen;
      this.mChIdx = inp.chIdx;
   }

   private void pop() throws SAXException {
      if (this.mInp.src != null) {
         try {
            this.mInp.src.close();
         } catch (IOException var2) {
         }

         this.mInp.src = null;
      }

      this.mInp = this.mInp.next;
      if (this.mInp != null) {
         if (this.mInp.popped && this.mInp.next != null) {
            this.panic("");
         }

         this.mInp.popped = true;
         this.mChars = this.mInp.chars;
         this.mChLen = this.mInp.chLen;
         this.mChIdx = this.mInp.chIdx;
      } else {
         this.mChars = null;
         this.mChLen = 0;
         this.mChIdx = 0;
      }

   }

   private char chtyp(char ch) throws SAXException {
      if (ch < 128) {
         return (char)asctyp[ch];
      } else {
         if (ch == '\uffff') {
            this.panic("");
         }

         return 'X';
      }
   }

   private char next() throws SAXException, IOException {
      if (this.mChIdx >= this.mChLen) {
         if (this.mInp == null) {
            this.panic("");
         }

         if (this.mInp.src == null) {
            this.pop();
            return this.next();
         }

         int Num = this.mInp.src.read(this.mChars, 0, this.mChars.length);
         if (Num < 0) {
            if (this.mInp != this.mDoc) {
               this.pop();
               return this.next();
            }

            this.mChars[0] = '\uffff';
            this.mChLen = 1;
         } else {
            this.mChLen = (char)Num;
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

   private Pair find(Pair chain, char[] qname) {
      for(Pair pair = chain; pair != null; pair = pair.next) {
         if (pair.eqname(qname)) {
            return pair;
         }
      }

      return null;
   }

   private Pair pair(Pair next) {
      Pair pair;
      if (this.mDltd != null) {
         pair = this.mDltd;
         this.mDltd = pair.next;
      } else {
         pair = new Pair();
      }

      pair.next = next;
      return pair;
   }

   private Pair del(Pair pair) {
      Pair next = pair.next;
      pair.name = null;
      pair.value = null;
      pair.chars = null;
      pair.list = null;
      pair.next = this.mDltd;
      this.mDltd = pair;
      return next;
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

      short i;
      for(i = 0; i <= 31; asctyp[i++] = 122) {
      }

      asctyp[9] = 32;
      asctyp[13] = 32;

      for(asctyp[10] = 32; i <= 47; ++i) {
         asctyp[i] = (byte)i;
      }

      while(i <= 57) {
         asctyp[i++] = 100;
      }

      while(i <= 64) {
         asctyp[i] = (byte)i;
         ++i;
      }

      while(i <= 90) {
         asctyp[i++] = 65;
      }

      while(i <= 96) {
         asctyp[i] = (byte)i;
         ++i;
      }

      while(i <= 122) {
         asctyp[i++] = 97;
      }

      while(i <= 127) {
         asctyp[i] = (byte)i;
         ++i;
      }

   }
}
