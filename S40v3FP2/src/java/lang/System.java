package java.lang;

import com.nokia.mid.impl.isa.util.SharedObjects;
import com.sun.cldc.io.ConsoleOutputStream;
import java.io.PrintStream;
import java.util.TimeZone;

public final class System {
   public static final PrintStream out = getOutput();
   public static final PrintStream err;
   static long tz_offset;

   private System() {
   }

   private static PrintStream getOutput() {
      return new PrintStream(new ConsoleOutputStream());
   }

   public static long currentTimeMillis() {
      long var0 = currentTimeMillis0();
      return var0 - tz_offset;
   }

   private static native long currentTimeMillis0();

   public static native void arraycopy(Object var0, int var1, Object var2, int var3, int var4);

   public static native int identityHashCode(Object var0);

   public static String getProperty(String var0) {
      String var1 = null;
      if (var0 == null) {
         throw new NullPointerException("key can't be null");
      } else if (var0.equals("")) {
         throw new IllegalArgumentException("key can't be empty");
      } else {
         synchronized(SharedObjects.getLock("java.lang.System.getProperty()")) {
            var1 = getProperty0(var0);
            return var1;
         }
      }
   }

   private static native String getProperty0(String var0);

   public static void exit(int var0) {
      Runtime.getRuntime().exit(var0);
   }

   public static void gc() {
      Runtime.getRuntime().gc();
   }

   static {
      err = out;
      tz_offset = -1L;
      TimeZone var0 = TimeZone.getDefault();
      tz_offset = (long)var0.getRawOffset();
   }
}
