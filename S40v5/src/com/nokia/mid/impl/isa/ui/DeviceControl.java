package com.nokia.mid.impl.isa.ui;

import com.nokia.mid.impl.isa.util.SharedObjects;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceControl {
   private static Timer jO = null;
   private static DeviceControl.VibraTimerClient jP = null;
   private static Object jQ;

   private DeviceControl() {
   }

   public static void setLights(int var0, int var1) {
      if (var0 == 0 && (var1 < 0 || var1 > 100)) {
         throw new IllegalArgumentException();
      } else {
         synchronized(jQ) {
            nativeSetLights(var0, var1);
         }
      }
   }

   public static void flashLights(long var0) {
      if (var0 < 0L) {
         throw new IllegalArgumentException();
      } else {
         synchronized(jQ) {
            nativeFlashLights(var0);
         }
      }
   }

   public static void startVibra(int var0, long var1) {
      a(var0, var1);
   }

   public static void stopVibra() {
      try {
         a(0, 0L);
      } catch (IllegalStateException var0) {
      }
   }

   public static void switchOnBacklightForDefaultPeriod() {
      synchronized(jQ) {
         nativeSwitchOnBacklightForDefaultPeriod();
      }
   }

   private static synchronized void a(int var0, long var1) {
      if (var0 >= 0 && var0 <= 100 && var1 >= 0L) {
         if (jO != null) {
            jO.cancel();
         }

         if (jP != null) {
            jP.cancel();
         }

         if (var1 == 0L || var0 == 0) {
            var0 = 0;
         }

         synchronized(jQ) {
            nativeOperateVibra(var0);
         }

         if (var0 > 0) {
            jO = new Timer();
            jP = new DeviceControl.VibraTimerClient();
            if (var1 + System.currentTimeMillis() < 0L) {
               var1 -= System.currentTimeMillis();
            }

            try {
               jO.schedule(jP, var1);
               return;
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

   static Object access$000() {
      return jQ;
   }

   static void r(int var0) {
      nativeOperateVibra(0);
   }

   static {
      synchronized(jQ = SharedObjects.getLock("com.nokia.mid.impl.isa.ui.devicecontrol")) {
         nativeStaticInitializer();
      }
   }

   static class VibraTimerClient extends TimerTask {
      public final void run() {
         synchronized(DeviceControl.access$000()) {
            DeviceControl.r(0);
         }
      }
   }
}
