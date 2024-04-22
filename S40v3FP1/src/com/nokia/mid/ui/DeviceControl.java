package com.nokia.mid.ui;

import java.util.Timer;
import java.util.TimerTask;

public class DeviceControl {
   private static Timer vibraTimer = null;
   private static DeviceControl.VibraTimerClient vibraTimerClient = null;

   private DeviceControl() {
   }

   public static void setLights(int var0, int var1) {
      if (var0 != 0 || var1 >= 0 && var1 <= 100) {
         nativeSetLights(var0, var1);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static void flashLights(long var0) {
      if (var0 < 0L) {
         throw new IllegalArgumentException();
      } else {
         nativeFlashLights(var0);
      }
   }

   public static void startVibra(int var0, long var1) {
      activateVibra(var0, var1);
   }

   public static void stopVibra() {
      try {
         activateVibra(0, 0L);
      } catch (IllegalStateException var1) {
      }

   }

   private static synchronized void activateVibra(int var0, long var1) {
      if (var0 >= 0 && var0 <= 100 && var1 >= 0L) {
         if (vibraTimer != null) {
            vibraTimer.cancel();
         }

         if (vibraTimerClient != null) {
            vibraTimerClient.cancel();
         }

         if (var1 == 0L || var0 == 0) {
            var1 = 0L;
            var0 = 0;
         }

         nativeStartVibra(var0);
         if (var0 > 0) {
            vibraTimer = new Timer();
            vibraTimerClient = new DeviceControl.VibraTimerClient();
            long var3 = var1 + System.currentTimeMillis();
            if (var3 < 0L) {
               var1 -= System.currentTimeMillis();
            }

            try {
               vibraTimer.schedule(vibraTimerClient, var1);
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

   private static native void nativeStartVibra(int var0);

   static {
      nativeStaticInitializer();
   }

   private static class VibraTimerClient extends TimerTask {
      private VibraTimerClient() {
      }

      public final void run() {
         DeviceControl.nativeStartVibra(0);
      }

      // $FF: synthetic method
      VibraTimerClient(Object var1) {
         this();
      }
   }
}
