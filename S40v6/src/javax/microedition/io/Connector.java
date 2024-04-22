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

   public static Connection open(String name) throws IOException {
      return open(name, 3);
   }

   public static Connection open(String name, int mode) throws IOException {
      return open(name, mode, false);
   }

   public static Connection open(String name, int mode, boolean timeouts) throws IOException {
      return InternalConnector.open(name, mode, timeouts);
   }

   public static DataInputStream openDataInputStream(String name) throws IOException {
      Connection con = open(name, 1);
      DataInputStream is = null;

      try {
         is = ((InputConnection)con).openDataInputStream();
      } catch (ClassCastException var7) {
         throw new IllegalArgumentException("Invalid class");
      } finally {
         con.close();
      }

      return is;
   }

   public static DataOutputStream openDataOutputStream(String name) throws IOException {
      Connection con = open(name, 2);
      DataOutputStream os = null;

      try {
         os = ((OutputConnection)con).openDataOutputStream();
      } catch (ClassCastException var7) {
         throw new IllegalArgumentException("Invalid class");
      } finally {
         con.close();
      }

      return os;
   }

   public static InputStream openInputStream(String name) throws IOException {
      return openDataInputStream(name);
   }

   public static OutputStream openOutputStream(String name) throws IOException {
      return openDataOutputStream(name);
   }
}
