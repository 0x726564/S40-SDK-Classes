package com.nokia.mid.impl.jms.drm;

public class DRMRights {
   private DRMPermission cB = null;

   public DRMPermission getPermission(int var1) throws IllegalArgumentException {
      if (var1 != 3) {
         throw new IllegalArgumentException("Illegal Permission Type " + var1);
      } else {
         return null;
      }
   }
}
