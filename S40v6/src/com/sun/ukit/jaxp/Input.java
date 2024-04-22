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
   public boolean popped = false;

   public Input(short buffsize) {
      this.chars = new char[buffsize];
      this.chLen = (char)this.chars.length;
   }

   public Input(char[] buff) {
      this.chars = buff;
      this.chLen = (char)this.chars.length;
   }

   public Input() {
   }
}
