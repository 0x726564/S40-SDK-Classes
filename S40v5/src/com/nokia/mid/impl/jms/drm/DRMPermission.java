package com.nokia.mid.impl.jms.drm;

import java.util.Date;

public class DRMPermission {
   public static final byte PERMISSION_UNKNOWN = 0;
   public static final byte PERMISSION_PLAY = 1;
   public static final byte PERMISSION_DISPLAY = 2;
   public static final byte PERMISSION_EXECUTE = 3;
   public static final byte PERMISSION_PRINT = 4;
   private int kk = -1;
   private int kl = -1;
   private byte km = 0;
   private long kn = 0L;
   private long ko = 0L;
   private long kp = 0L;
   private byte kq = 0;

   public byte getPermissionType() {
      return 0;
   }

   public Date getBeginDate() {
      return null;
   }

   public Date getEndDate() {
      return null;
   }

   public long getInterval() {
      return 0L;
   }

   public int getCount() {
      return this.kk;
   }

   public int getOriginalCount() {
      return this.kl;
   }

   public boolean hasFullRights() {
      return false;
   }
}
