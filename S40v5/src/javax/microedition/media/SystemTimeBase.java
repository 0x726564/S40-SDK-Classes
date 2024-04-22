package javax.microedition.media;

class SystemTimeBase implements TimeBase {
   private static long b = System.currentTimeMillis() * 1000L;

   public long getTime() {
      return System.currentTimeMillis() * 1000L - b;
   }
}
