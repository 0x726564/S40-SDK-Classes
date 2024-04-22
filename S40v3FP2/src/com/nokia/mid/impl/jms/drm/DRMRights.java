package com.nokia.mid.impl.jms.drm;

public class DRMRights {
   private DRMPermission m_playPermission = null;
   private DRMPermission m_printPermission = null;
   private DRMPermission m_executePermission = null;
   private DRMPermission m_displayPermission = null;

   public DRMPermission getPermission(int var1) throws IllegalArgumentException {
      if (var1 != 3) {
         throw new IllegalArgumentException("Illegal Permission Type " + var1);
      } else {
         return this.m_executePermission;
      }
   }
}
