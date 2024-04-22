package java.lang;

public class Runtime {
   private static Runtime currentRuntime = new Runtime();

   public static Runtime getRuntime() {
      return currentRuntime;
   }

   private Runtime() {
   }

   public void exit(int status) {
      throw new SecurityException("MIDP lifecycle does not support system exit.");
   }

   public native long freeMemory();

   public native long totalMemory();

   public native void gc();
}
