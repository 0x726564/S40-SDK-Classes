package com.sun.ukit.jaxp;

class Pair {
   public String name;
   public String value;
   public char[] chars;
   public char hn;
   public Pair ho;
   public Pair hp;

   public final String U() {
      return this.chars[0] != 0 ? new String(this.chars, this.chars[0] + 1, this.chars.length - this.chars[0] - 1) : new String(this.chars, 1, this.chars.length - 1);
   }

   public final boolean b(char[] var1) {
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

   public final boolean c(char[] var1) {
      char var2;
      if ((var2 = (char)this.chars.length) == var1.length) {
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
