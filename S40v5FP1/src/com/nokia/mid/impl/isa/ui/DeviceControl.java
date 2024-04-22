package com.nokia.mid.impl.isa.ui;

import com.nokia.mid.impl.isa.util.SharedObjects;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceControl {
   private static Timer vibraTimer = null;
   private static DeviceControl.VibraTimerClient vibraTimerClient = null;
   private static Object sharedDCLock = SharedObjects.getLock("com.nokia.mid.impl.isa.ui.devicecontrol");

   private DeviceControl() {
   }

   public static void setLights(int num, int level) {
      if (num == 0 && (level < 0 || level > 100)) {
         throw new IllegalArgumentException();
      } else {
         synchronized(sharedDCLock) {
            nativeSetLights(num, level);
         }
      }
   }

   public static void flashLights(long duration) {
      if (duration < 0L) {
         throw new IllegalArgumentException();
      } else {
         synchronized(sharedDCLock) {
            nativeFlashLights(duration);
         }
      }
   }

   public static void startVibra(int freq, long duration) {
      activateVibra(freq, duration);
   }

   public static void stopVibra() {
      try {
         activateVibra(0, 0L);
      } catch (IllegalStateException var1) {
      }

   }

   public static void switchOnBacklightForDefaultPeriod() {
      synchronized(sharedDCLock) {
         nativeSwitchOnBacklightForDefaultPeriod();
      }
   }

   private static synchronized void activateVibra(int level, long duration) {
      if (level >= 0 && level <= 100 && duration >= 0L) {
         if (vibraTimer != null) {
            vibraTimer.cancel();
         }

         if (vibraTimerClient != null) {
            vibraTimerClient.cancel();
         }

         if (duration == 0L || level == 0) {
            duration = 0L;
            level = 0;
         }

         synchronized(sharedDCLock) {
            nativeOperateVibra(level);
         }

         if (level > 0) {
            vibraTimer = new Timer();
            vibraTimerClient = new DeviceControl.VibraTimerClient();
            long delay = duration + System.currentTimeMillis();
            if (delay < 0L) {
               duration -= System.currentTimeMillis();
            }

            try {
               vibraTimer.schedule(vibraTimerClient, duration);
            } catch (IllegalArgumentException var6) {
            }
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   private static native void nativeStaticInitializer();

   private static native void nativeSetLights(int var0, int var1);

   private static native void nativeFlashLights(long var0);

   private static native void nativeOperateVibra(int var0);

   private static native void nativeSwitchOnBacklightForDefaultPeriod();

   static {
      synchronized(sharedDCLock) {
         nativeStaticInitializer();
      }
   }

   static class VibraTimerClient extends TimerTask {
      public final void run() {
         synchronized(DeviceControl.sharedDCLock) {
            DeviceControl.nativeOperateVibra(0);
         }
      }
   }
}
