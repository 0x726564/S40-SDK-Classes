package java.lang;

public class Runtime {
   private static Runtime eG = new Runtime();

   public static Runtime getRuntime() {
      return eG;
   }

   private Runtime() {
   }

   public void exit(int var1) {
      throw new SecurityException("MIDP lifecycle does not support system exit.");
   }

   public native long freeMemory();

   public native long totalMemory();

   public native void gc();
}
