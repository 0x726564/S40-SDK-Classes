package com.sun.j2mews.xml.rpc;

import java.util.Vector;

class TypedVector extends Vector {
   public int type;
   public boolean kr;

   TypedVector(int var1, boolean var2) {
      this.type = var1;
      this.kr = var2;
   }
}
