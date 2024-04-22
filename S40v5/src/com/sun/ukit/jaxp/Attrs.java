package com.sun.ukit.jaxp;

import org.xml.sax.Attributes;

class Attrs implements Attributes {
   String[] df = new String[64];
   private char en;

   void setLength(char var1) {
      if (var1 > (char)(this.df.length >> 3)) {
         this.df = new String[var1 << 3];
      }

      this.en = var1;
   }

   public int getLength() {
      return this.en;
   }

   public String getURI(int var1) {
      return var1 >= 0 && var1 < this.en ? this.df[var1 << 3] : null;
   }

   public String getLocalName(int var1) {
      return var1 >= 0 && var1 < this.en ? this.df[(var1 << 3) + 2] : null;
   }

   public String getQName(int var1) {
      return var1 >= 0 && var1 < this.en ? this.df[(var1 << 3) + 1] : null;
   }

   public String getType(int var1) {
      if (var1 < 0) {
         return null;
      } else {
         return var1 < this.df.length >> 3 ? this.df[(var1 << 3) + 4] : null;
      }
   }

   public String getValue(int var1) {
      return var1 >= 0 && var1 < this.en ? this.df[(var1 << 3) + 3] : null;
   }

   public int getIndex(String var1, String var2) {
      char var3 = this.en;

      for(char var4 = 0; var4 < var3; ++var4) {
         if (this.df[var4 << 3].equals(var1) && this.df[(var4 << 3) + 2].equals(var2)) {
            return var4;
         }
      }

      return -1;
   }

   public int getIndex(String var1) {
      char var2 = this.en;

      for(char var3 = 0; var3 < var2; ++var3) {
         if (this.getQName(var3).equals(var1)) {
            return var3;
         }
      }

      return -1;
   }

   public String getType(String var1, String var2) {
      int var3;
      return (var3 = this.getIndex(var1, var2)) >= 0 ? this.df[(var3 << 3) + 4] : null;
   }

   public String getType(String var1) {
      int var2;
      return (var2 = this.getIndex(var1)) >= 0 ? this.df[(var2 << 3) + 4] : null;
   }

   public String getValue(String var1, String var2) {
      int var3;
      return (var3 = this.getIndex(var1, var2)) >= 0 ? this.df[(var3 << 3) + 3] : null;
   }

   public String getValue(String var1) {
      int var2;
      return (var2 = this.getIndex(var1)) >= 0 ? this.df[(var2 << 3) + 3] : null;
   }
}
