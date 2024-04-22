package com.sun.ukit.jaxp;

import java.io.Reader;

class Input {
   public String eV;
   public String eW;
   public Reader eX;
   public char[] chars;
   public char eY;
   public char eZ;
   public Input fa;
   public boolean fb = false;

   public Input(short var1) {
      this.chars = new char[512];
      this.eY = (char)this.chars.length;
   }

   public Input(char[] var1) {
      this.chars = var1;
      this.eY = (char)this.chars.length;
   }

   public Input() {
   }
}
