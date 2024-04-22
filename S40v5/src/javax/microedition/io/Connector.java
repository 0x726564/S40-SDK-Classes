package javax.microedition.io;

import com.sun.midp.io.InternalConnector;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Connector {
   public static final int READ = 1;
   public static final int WRITE = 2;
   public static final int READ_WRITE = 3;

   private Connector() {
   }

   public static Connection open(String var0) throws IOException {
      return open(var0, 3);
   }

   public static Connection open(String var0, int var1) throws IOException {
      return open(var0, var1, false);
   }

   public static Connection open(String var0, int var1, boolean var2) throws IOException {
      return InternalConnector.open(var0, var1, var2);
   }

   public static DataInputStream openDataInputStream(String var0) throws IOException {
      Connection var1 = open(var0, 1);
      var0 = null;

      DataInputStream var7;
      try {
         var7 = ((InputConnection)var1).openDataInputStream();
      } catch (ClassCastException var5) {
         throw new IllegalArgumentException("Invalid class");
      } finally {
         var1.close();
      }

      return var7;
   }

   public static DataOutputStream openDataOutputStream(String var0) throws IOException {
      Connection var1 = open(var0, 2);
      var0 = null;

      DataOutputStream var7;
      try {
         var7 = ((OutputConnection)var1).openDataOutputStream();
      } catch (ClassCastException var5) {
         throw new IllegalArgumentException("Invalid class");
      } finally {
         var1.close();
      }

      return var7;
   }

   public static InputStream openInputStream(String var0) throws IOException {
      return openDataInputStream(var0);
   }

   public static OutputStream openOutputStream(String var0) throws IOException {
      return openDataOutputStream(var0);
   }
}
