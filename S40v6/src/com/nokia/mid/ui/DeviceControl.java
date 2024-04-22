package com.nokia.mid.ui;

public class DeviceControl {
   private DeviceControl() {
   }

   public static void setLights(int num, int level) {
      com.nokia.mid.impl.isa.ui.DeviceControl.setLights(num, level);
   }

   public static void flashLights(long duration) {
      com.nokia.mid.impl.isa.ui.DeviceControl.flashLights(duration);
   }

   public static void startVibra(int freq, long duration) {
      com.nokia.mid.impl.isa.ui.DeviceControl.startVibra(freq, duration);
   }

   public static void stopVibra() {
      com.nokia.mid.impl.isa.ui.DeviceControl.stopVibra();
   }
}
