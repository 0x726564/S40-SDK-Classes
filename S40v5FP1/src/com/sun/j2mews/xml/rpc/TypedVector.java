package com.sun.j2mews.xml.rpc;

import java.util.Vector;

class TypedVector extends Vector {
   public int type;
   public boolean nillable;

   TypedVector(int type, boolean nillable) {
      this.type = type;
      this.nillable = nillable;
   }
}
