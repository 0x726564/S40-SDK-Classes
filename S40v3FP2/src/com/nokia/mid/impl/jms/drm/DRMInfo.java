package com.nokia.mid.impl.jms.drm;

public class DRMInfo {
   private String m_mimeType = null;
   private String m_issuerUrl = null;
   private String m_logicalName = null;
   private byte m_drmType = 0;
   private Object[] m_rights = null;
   private byte[] m_uid = null;
   private DRMPermission m_permissionToBeDisplayed = null;

   public String getMimeType() {
      return this.m_mimeType;
   }

   public String getIssuerUrl() {
      return this.m_issuerUrl;
   }

   public DRMRights[] getRights() {
      try {
         if (this.m_rights == null) {
            return null;
         } else {
            DRMRights[] var1 = new DRMRights[this.m_rights.length];

            for(int var2 = 0; var2 < this.m_rights.length; ++var2) {
               var1[var2] = (DRMRights)this.m_rights[var2];
            }

            return var1;
         }
      } catch (Exception var3) {
         return null;
      }
   }

   public String getLogicalName() {
      return this.m_logicalName;
   }

   public byte getDRMType() {
      return this.m_drmType;
   }

   public DRMPermission getPermissionToBeDisplayed() {
      return this.m_permissionToBeDisplayed;
   }

   public byte[] getUID() {
      return this.m_uid;
   }
}
