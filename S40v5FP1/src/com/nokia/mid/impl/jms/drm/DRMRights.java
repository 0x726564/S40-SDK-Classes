package com.nokia.mid.impl.jms.drm;

public class DRMRights {
   private DRMPermission m_playPermission = null;
   private DRMPermission m_printPermission = null;
   private DRMPermission m_executePermission = null;
   private DRMPermission m_displayPermission = null;

   public DRMPermission getPermission(int type) throws IllegalArgumentException {
      if (type != 3) {
         throw new IllegalArgumentException("Illegal Permission Type " + type);
      } else {
         return this.m_executePermission;
      }
   }
}
