package com.sun.ukit.jaxp;

import java.io.Reader;

class Input {
   public String pubid;
   public String sysid;
   public Reader src;
   public char[] chars;
   public char chLen;
   public char chIdx;
   public Input next;

   public Input(short var1) {
      this.chars = new char[var1];
      this.chLen = (char)this.chars.length;
   }

   public Input(char[] var1) {
      this.chars = var1;
      this.chLen = (char)this.chars.length;
   }

   public Input() {
   }
}
