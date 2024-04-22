package javax.bluetooth;

public class DeviceClass {
   private final int recordCoD;

   public DeviceClass(int record) {
      if (record >= 0 && record <= 16777215) {
         this.recordCoD = record;
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
