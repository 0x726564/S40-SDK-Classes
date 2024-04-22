package javax.bluetooth;

public class DeviceClass {
   private final int t;

   public DeviceClass(int var1) {
      if (var1 >= 0 && var1 <= 16777215) {
         this.t = var1;
      } else {
         throw new IllegalArgumentException("invalid CoD record parameter");
      }
   }

   public int getServiceClasses() {
      return this.t & 16769024;
   }

   public int getMajorDeviceClass() {
      return this.t & 7936;
   }

   public int getMinorDeviceClass() {
      return this.t & 252;
   }
}
