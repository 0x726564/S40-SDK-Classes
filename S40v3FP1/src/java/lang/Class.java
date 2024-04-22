package java.lang;

import com.sun.cldc.io.ResourceInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Class {
   private Class() {
   }

   public String toString() {
      return (this.isInterface() ? "interface " : "class ") + this.getName();
   }

   public static native Class forName(String var0) throws ClassNotFoundException;

   public native Object newInstance() throws InstantiationException, IllegalAccessException;

   public native boolean isInstance(Object var1);

   public native boolean isAssignableFrom(Class var1);

   public native boolean isInterface();

   public native boolean isArray();

   public native String getName();

   public InputStream getResourceAsStream(String var1) {
      try {
         if (var1.length() > 0 && var1.charAt(0) == '/') {
            var1 = var1.substring(1);
         } else {
            String var2 = this.getName();
            int var3 = var2.lastIndexOf(46);
            if (var3 >= 0) {
               var1 = var2.substring(0, var3 + 1).replace('.', '/') + var1;
            }
         }

         return new ResourceInputStream(var1);
      } catch (IOException var4) {
         return null;
      }
   }

   private static void runCustomCode() {
   }
}
