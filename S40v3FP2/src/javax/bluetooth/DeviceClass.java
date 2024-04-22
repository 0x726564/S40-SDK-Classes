package javax.bluetooth;

public class DeviceClass {
   private final int recordCoD;

   public DeviceClass(int var1) {
      if (var1 >= 0 && var1 <= 16777215) {
         this.recordCoD = var1;
      } else {
         throw new IllegalArgumentException("invalid CoD record parameter");
      }
   }

   public int getServiceClasses() {
      return this.recordCoD & 16769024;
   }

   public int getMajorDeviceClass() {
      return this.recordCoD & 7936;
   }

   public int getMinorDeviceClass() {
      return this.recordCoD & 252;
   }
}
