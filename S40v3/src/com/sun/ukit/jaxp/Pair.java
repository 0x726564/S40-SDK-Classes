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

   public boolean eqpref(char[] var1) {
      if (this.chars[0] == var1[0]) {
         char var2 = this.chars[0];

         for(char var3 = 1; var3 < var2; ++var3) {
            if (this.chars[var3] != var1[var3]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean eqname(char[] var1) {
      char var2 = (char)this.chars.length;
      if (var2 == var1.length) {
         for(char var3 = 0; var3 < var2; ++var3) {
            if (this.chars[var3] != var1[var3]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
