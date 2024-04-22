package com.sun.ukit.jaxp;

class Pair {
   public String name;
   public String value;
   public char[] chars;
   public char id;
   public Pair list;
   public Pair next;

   public String qname() {
      return new String(this.chars, 1, this.chars.length - 1);
   }

   public String local() {
      return this.chars[0] != 0 ? new String(this.chars, this.chars[0] + 1, this.chars.length - this.chars[0] - 1) : new String(this.chars, 1, this.chars.length - 1);
   }

   public String pref() {
      return this.chars[0] != 0 ? new String(this.chars, 1, this.chars[0] - 1) : "";
   }

   public boolean eqpref(char[] qname) {
      if (this.chars[0] == qname[0]) {
         char len = this.chars[0];

         for(char i = 1; i < len; ++i) {
            if (this.chars[i] != qname[i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean eqname(char[] qname) {
      char len = (char)this.chars.length;
      if (len == qname.length) {
         for(char i = 0; i < len; ++i) {
            if (this.chars[i] != qname[i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
