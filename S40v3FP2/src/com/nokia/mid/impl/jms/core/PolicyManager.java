package com.nokia.mid.impl.jms.core;

import java.io.IOException;

public class PolicyManager {
   public static final int JAVA_PERMISSION_UNKNOWN = -1;
   public static final int JAVA_PERMISSION_ASK_EVERYTIME = 0;
   public static final int JAVA_PERMISSION_ASK_ONCE = 1;
   public static final int JAVA_PERMISSION_ALWYAS_ALLOWED = 2;
   public static final int JAVA_PERMISSION_NOT_ALLOWED = 3;
   public static final int JAVA_PERMISSION_NOT_SET = 4;
   public static final int JAVA_FUNCTION_GROUP_NETWORK_ACCESS = 1;
   public static final int JAVA_FUNCTION_GROUP_MESSAGING = 2;
   public static final int JAVA_FUNCTION_GROUP_CONNECTIVITY = 3;
   public static final int JAVA_FUNCTION_GROUP_MMEDIA_REC = 4;
   public static final int JAVA_FUNCTION_GROUP_READ_MYDATA = 5;
   public static final int JAVA_FUNCTION_GROUP_ADD_NEW_DATA = 6;
   public static final int JAVA_FUNCTION_GROUP_AUTOSTART = 7;
   public static final int JAVA_FUNCTION_GROUP_LOCATION = 8;
   public static final int JAVA_FUNCTION_GROUP_LANDMARKS = 9;
   public static final int JAVA_FUNCTION_GROUP_PLATFORM_REQUEST = 10;
   public static final int JAVA_FUNCTION_GROUP_LIMIT = 11;
   public static final int JAVA_POLICY_DOMAIN_UNKNOWN = 0;
   public static final int JAVA_POLICY_DOMAIN_UNTRUSTED = 1;
   public static final int JAVA_POLICY_DOMAIN_THIRD_PARTY = 2;
   public static final int JAVA_POLICY_DOMAIN_OPERATOR = 16;
   public static final int JAVA_POLICY_DOMAIN_MANUFACTURER = 32;
   private static PolicyManager m_policyManager = null;

   protected PolicyManager() {
   }

   public static PolicyManager getPolicyManager() {
      if (m_policyManager == null) {
         m_policyManager = new PolicyManager();
      }

      return m_policyManager;
   }

   public int[] getAvailableFunctionGroups() {
      int[] var1 = null;
      if (var1 == null) {
         var1 = this.getAvailableFunctionGroups0();
      }

      return var1;
   }

   public boolean[] getAllowedPermissionLevels(int var1, int var2) {
      int var3 = this.getProtectionDomain(var1);
      return this.getAllowedPermissionLevelsByDomain(var3, var2);
   }

   public boolean[] getAllowedPermissionLevelsByDomain(int var1, int var2) {
      if (var1 == 0) {
         return null;
      } else {
         return var2 > 0 && var2 < 11 ? this.getAllowedPermissionLevelsByDomain0(var1, var2) : null;
      }
   }

   public native int getPermission(int var1, int var2);

   public native void setPermission(int var1, int var2, int var3);

   public native int getFunctionGroupFromAPI(String var1);

   public int getProtectionDomain(int var1) {
      byte[] var2 = null;

      try {
         var2 = MIDletRegistry.getMIDletRegistry().getMIDletAttribute(var1, 2);
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      return var2 != null && var2.length != 0 ? var2[0] : 0;
   }

   private native int[] getAvailableFunctionGroups0();

   private native boolean[] getAllowedPermissionLevelsByDomain0(int var1, int var2);
}
