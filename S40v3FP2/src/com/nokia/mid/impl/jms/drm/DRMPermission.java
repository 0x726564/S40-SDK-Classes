package com.nokia.mid.impl.jms.drm;

import java.util.Date;
import java.util.TimeZone;

public class DRMPermission {
   public static final byte PERMISSION_UNKNOWN = 0;
   public static final byte PERMISSION_PLAY = 1;
   public static final byte PERMISSION_DISPLAY = 2;
   public static final byte PERMISSION_EXECUTE = 3;
   public static final byte PERMISSION_PRINT = 4;
   private int m_count = -1;
   private int m_originalCount = -1;
   private byte m_hasFullRights = 0;
   private long m_beginDate = 0L;
   private long m_endDate = 0L;
   private long m_interval = 0L;
   private byte m_permissionType = 0;

   public byte getPermissionType() {
      return this.m_permissionType;
   }

   public Date getBeginDate() {
      if (this.m_beginDate == 0L) {
         return null;
      } else {
         TimeZone var1 = TimeZone.getDefault();
         return new Date(this.m_beginDate * 1000L - (long)var1.getRawOffset());
      }
   }

   public Date getEndDate() {
      if (this.m_endDate == 0L) {
         return null;
      } else {
         TimeZone var1 = TimeZone.getDefault();
         return new Date(this.m_endDate * 1000L - (long)var1.getRawOffset());
      }
   }

   public long getInterval() {
      return this.m_interval;
   }

   public int getCount() {
      return this.m_count;
   }

   public int getOriginalCount() {
      return this.m_originalCount;
   }

   public boolean hasFullRights() {
      return this.m_hasFullRights != 0;
   }
}
