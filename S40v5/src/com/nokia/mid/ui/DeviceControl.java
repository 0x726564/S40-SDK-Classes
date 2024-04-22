package com.nokia.mid.ui;

public class DeviceControl {
   private DeviceControl() {
   }

   public static void setLights(int var0, int var1) {
      com.nokia.mid.impl.isa.ui.DeviceControl.setLights(var0, var1);
   }

   public static void flashLights(long var0) {
      com.nokia.mid.impl.isa.ui.DeviceControl.flashLights(var0);
   }

   public static void startVibra(int var0, long var1) {
      com.nokia.mid.impl.isa.ui.DeviceControl.startVibra(var0, var1);
   }

   public static void stopVibra() {
      com.nokia.mid.impl.isa.ui.DeviceControl.stopVibra();
   }
}
