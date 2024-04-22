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

   public InputStream getResourceAsStream(String name) {
      try {
         if (name.length() > 0 && name.charAt(0) == '/') {
            name = name.substring(1);
         } else {
            String className = this.getName();
            int dotIndex = className.lastIndexOf(46);
            if (dotIndex >= 0) {
               name = className.substring(0, dotIndex + 1).replace('.', '/') + name;
            }
         }

         return new ResourceInputStream(name);
      } catch (IOException var4) {
         return null;
      }
   }

   private static void runCustomCode() {
   }
}
