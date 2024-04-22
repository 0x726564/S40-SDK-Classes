package java.lang;

import com.nokia.mid.impl.isa.util.SharedObjects;
import com.sun.cldc.io.ConsoleOutputStream;
import java.io.PrintStream;

public final class System {
   public static final PrintStream out = getOutput();
   public static final PrintStream err;

   private System() {
   }

   private static PrintStream getOutput() {
      return new PrintStream(new ConsoleOutputStream());
   }

   public static native long currentTimeMillis();

   public static native void arraycopy(Object var0, int var1, Object var2, int var3, int var4);

   public static native int identityHashCode(Object var0);

   public static String getProperty(String key) {
      String result = null;
      if (key == null) {
         throw new NullPointerException("key can't be null");
      } else if (key.equals("")) {
         throw new IllegalArgumentException("key can't be empty");
      } else {
         synchronized(SharedObjects.getLock("java.lang.System.getProperty()")) {
            result = getProperty0(key);
            return result;
         }
      }
   }

   private static native String getProperty0(String var0);

   public static void exit(int status) {
      Runtime.getRuntime().exit(status);
   }

   public static void gc() {
      Runtime.getRuntime().gc();
   }

   static {
      err = out;
   }
}
