package java.lang;

import com.sun.cldc.io.ResourceInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Class {
   private Class() {
   }

   public final String toString() {
      return (this.isInterface() ? "interface " : "class ") + this.getName();
   }

   public static native Class forName(String var0) throws ClassNotFoundException;

   public final native Object newInstance() throws InstantiationException, IllegalAccessException;

   public final native boolean isInstance(Object var1);

   public final native boolean isAssignableFrom(Class var1);

   public final native boolean isInterface();

   public final native boolean isArray();

   public final native String getName();

   public final InputStream getResourceAsStream(String var1) {
      try {
         if (var1.length() > 0 && var1.charAt(0) == '/') {
            var1 = var1.substring(1);
         } else {
            int var2;
            String var4;
            if ((var2 = (var4 = this.getName()).lastIndexOf(46)) >= 0) {
               var1 = var4.substring(0, var2 + 1).replace('.', '/') + var1;
            }
         }

         return new ResourceInputStream(var1);
      } catch (IOException var3) {
         return null;
      }
   }
}
