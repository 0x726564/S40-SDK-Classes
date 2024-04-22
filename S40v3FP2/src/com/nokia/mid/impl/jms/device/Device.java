package com.nokia.mid.impl.jms.device;

public class Device {
   private Device() {
   }

   public static synchronized native short getRemainingBatteryPowerBars();

   public static synchronized native short getMaxBatteryPowerBars();

   public static native boolean isInFlightMode();
}
