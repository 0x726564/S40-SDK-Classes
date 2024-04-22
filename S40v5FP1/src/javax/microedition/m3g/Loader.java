package javax.microedition.m3g;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import javax.microedition.lcdui.Image;

public class Loader {
   private static byte[] V = new byte[]{-85, 74, 83, 82, 49, 56, 52, -69, 13, 10, 26, 10};
   private static byte[] W = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
   private static final int X;
   private int handle;
   private Vector Y = new Vector();
   private Vector Z = new Vector();
   private String aa;
   private String ab;
   private int ac = 0;
   private int ad;
   private byte[] ae;
   private int af;

   private Loader() {
      this.ad = V.length;
   }

   private Loader(Vector var1, String var2) {
      this.ad = V.length;
      this.ab = var2;
      this.Z = var1;
   }

   public static Object3D[] load(String var0) throws IOException {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         try {
            return (new Loader()).a(var0);
         } catch (SecurityException var2) {
            throw var2;
         } catch (IOException var3) {
            throw var3;
         } catch (Exception var4) {
            throw new IOException("Load error " + var4);
         }
      }
   }

   public static Object3D[] load(byte[] var0, int var1) throws IOException {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (var1 >= 0 && var1 < var0.length) {
         try {
            return (new Loader()).a(var0, var1);
         } catch (SecurityException var3) {
            throw var3;
         } catch (IOException var4) {
            throw var4;
         } catch (Exception var5) {
            throw new IOException("Load error " + var5);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private Object3D[] a(String var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.b(var1)) {
         throw new IOException("Reference loop detected.");
      } else {
         this.aa = var1;
         this.Z.addElement(var1);
         Loader.PeekInputStream var2;
         Loader.PeekInputStream var3 = var2 = new Loader.PeekInputStream(this, this.d(var1), X);
         byte[] var4 = new byte[X];
         var3.read(var4);
         int var7 = b(var4, 0);
         var2.a();
         this.ae = null;
         this.af = 0;

         Object3D[] var8;
         try {
            var8 = this.a((InputStream)var2, var7);
         } catch (SecurityException var5) {
            var2.close();
            throw var5;
         } catch (IOException var6) {
            var2.close();
            throw var6;
         }

         var2.close();
         this.Z.removeElement(var1);
         return var8;
      }
   }

   private Object3D[] a(byte[] var1, int var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("Resource byte array is null.");
      } else {
         int var3 = b(var1, var2);
         ByteArrayInputStream var4 = new ByteArrayInputStream(var1, var2, var1.length - var2);
         this.ae = var1;
         this.af = var2;
         this.aa = "ByteArray";

         Object3D[] var7;
         try {
            var7 = this.a((InputStream)var4, var3);
         } catch (SecurityException var5) {
            var4.close();
            throw var5;
         } catch (IOException var6) {
            var4.close();
            throw var6;
         }

         var4.close();
         return var7;
      }
   }

   private Object3D[] a(InputStream var1, int var2) throws IOException {
      if (var2 == 1) {
         InputStream var4 = var1;
         Loader var3 = this;
         var1.skip((long)V.length);
         if (var1 instanceof Loader.PeekInputStream) {
            ((Loader.PeekInputStream)var1).a(64);
         }

         var1.read();
         int var5 = c(var1);
         if (var1 instanceof Loader.PeekInputStream && var5 > 64) {
            ((Loader.PeekInputStream)var1).a(var5 - 64);
         }

         c(var1);
         var1.read();
         c(var1);
         var1.read();
         var1.read();
         boolean var10000;
         if ((var5 = var1.read()) == 0) {
            var10000 = false;
         } else {
            if (var5 != 1) {
               throw new IOException("Malformed boolean.");
            }

            var10000 = true;
         }

         boolean var9 = var10000;
         this.ac = c(var1);
         c(var1);
         d(var1);
         c(var1);
         this.handle = _ctor(Interface.getHandle());
         Platform.a(this);
         int var6;
         int[] var10;
         if (var9) {
            if (var1 instanceof Loader.PeekInputStream) {
               ((Loader.PeekInputStream)var1).a(128);
            }

            this.b(var1);
            if (this.Y.size() <= 0) {
               throw new IOException("No external sections [" + this.aa + "].");
            }

            var10 = new int[this.Y.size()];

            for(var6 = 0; var6 < var10.length; ++var6) {
               var10[var6] = ((Object3D)var3.Y.elementAt(var6)).handle;
            }

            _setExternalReferences(var3.handle, var10);
         }

         if (var1 instanceof Loader.PeekInputStream) {
            ((Loader.PeekInputStream)var1).a();
         } else if (var1.markSupported()) {
            var1.reset();
         }

         var5 = 0;
         if ((var6 = var1.available()) == 0) {
            var6 = 2048;
         }

         while(var5 < var3.ac) {
            if (var5 + var6 > var3.ac) {
               var6 = var3.ac - var5;
            }

            byte[] var7 = new byte[var6];
            if (var4.read(var7) == -1) {
               break;
            }

            var5 += var6;
            if ((var6 = _decodeData(var3.handle, 0, var7)) > 0 && var4.available() > var6) {
               var6 = var4.available();
            }
         }

         if (var6 == 0 && var5 == var3.ac) {
            Object3D[] var11 = null;
            int var8;
            if ((var8 = _getLoadedObjects(var3.handle, (int[])null)) > 0) {
               var10 = new int[var8];
               _getLoadedObjects(var3.handle, var10);
               var11 = new Object3D[var8];

               for(var8 = 0; var8 < var11.length; ++var8) {
                  var11[var8] = Interface.getObjectInstance(var10[var8]);
               }

               var3.d();
            }

            return var11;
         } else {
            throw new IOException("Invalid file length [" + var3.aa + "].");
         }
      } else if (var2 == 2) {
         return this.a(var1);
      } else {
         throw new IOException("File not recognized.");
      }
   }

   private Object3D[] a(InputStream var1) throws IOException {
      byte var2 = 99;
      DataInputStream var10;
      (var10 = new DataInputStream(var1)).skip((long)W.length);

      try {
         while(true) {
            int var3 = var10.readInt();
            int var4;
            if ((var4 = var10.readInt()) == 1229472850) {
               var10.skip(9L);
               int var5 = var10.readUnsignedByte();
               var3 -= 10;
               switch(var5) {
               case 0:
                  var2 = 97;
               case 1:
               case 5:
               default:
                  break;
               case 2:
                  var2 = 99;
                  break;
               case 3:
                  var2 = 99;
                  break;
               case 4:
                  var2 = 98;
                  break;
               case 6:
                  var2 = 100;
               }
            }

            if (var4 == 1951551059) {
               switch(var2) {
               case 97:
                  var2 = 98;
                  break;
               case 99:
                  var2 = 100;
               }
            }

            if (var4 == 1229209940) {
               break;
            }

            var10.skip((long)(var3 + 4));
         }
      } catch (Exception var9) {
      }

      Object var11;
      if (this.ae == null) {
         var11 = this.d(this.aa);
      } else {
         var11 = new ByteArrayInputStream(this.ae, this.af, this.ae.length - this.af);
      }

      Image2D var12;
      try {
         var12 = new Image2D(var2, Image.createImage((InputStream)var11));
      } catch (SecurityException var7) {
         ((InputStream)var11).close();
         throw var7;
      } catch (IOException var8) {
         ((InputStream)var11).close();
         throw var8;
      }

      try {
         ((InputStream)var11).close();
      } catch (Exception var6) {
      }

      return new Object3D[]{var12};
   }

   private void d() throws IOException {
      int var1 = _getObjectsWithUserParameters(this.handle, (int[])null);
      int[] var2 = null;
      if (var1 > 0) {
         var2 = new int[var1];
         _getObjectsWithUserParameters(this.handle, var2);
      }

      for(int var3 = 0; var3 < var1; ++var3) {
         int var4;
         if ((var4 = _getNumUserParameters(this.handle, var3)) > 0) {
            Hashtable var5 = new Hashtable();

            for(int var6 = 0; var6 < var4; ++var6) {
               byte[] var7 = new byte[_getUserParameter(this.handle, var3, var6, (byte[])null)];
               int var8 = _getUserParameter(this.handle, var3, var6, var7);
               if (var5.put(new Integer(var8), var7) != null) {
                  throw new IOException("Duplicate id in user data [" + this.aa + "].");
               }
            }

            Interface.getObjectInstance(var2[var3]).setUserObject(var5);
         }
      }

   }

   private void b(InputStream var1) throws IOException {
      int var2;
      if ((var2 = var1.read()) != -1 && (this.ac == 0 || this.ad < this.ac)) {
         int var3 = c(var1);
         this.ad += var3;
         if (var1 instanceof Loader.PeekInputStream && var3 > 128) {
            ((Loader.PeekInputStream)var1).a(var3 - 128);
         }

         int var4 = c(var1);
         Loader.CountedInputStream var5 = null;
         if (var2 == 0) {
            var5 = new Loader.CountedInputStream(this, var1);
            if (var4 != var3 - 13) {
               throw new IOException("Section length mismatch [" + this.aa + "].");
            }
         } else {
            if (var2 != 1) {
               throw new IOException("Unrecognized compression scheme [" + this.aa + "].");
            }

            if (var4 == 0 && var3 - 13 == 0) {
               var5 = new Loader.CountedInputStream(this, (InputStream)null);
            } else {
               if (var4 <= 0 || var3 - 13 <= 0) {
                  throw new IOException("Section length mismatch [" + this.aa + "].");
               }

               byte[] var9 = new byte[var3 - 13];
               var1.read(var9);
               byte[] var10 = new byte[var4];
               if (!_inflate(var9, var10)) {
                  throw new IOException("Decompression error.");
               }

               var5 = new Loader.CountedInputStream(this, new ByteArrayInputStream(var10));
            }
         }

         var5.c();

         while(var5.getCounter() < var4) {
            Vector var10000 = this.Y;
            int var6 = var5.read();
            int var7 = c((InputStream)var5);
            var7 += var5.getCounter();
            Object3D var8 = null;
            if (var6 != 255) {
               throw new IOException("Invalid external section [" + this.aa + "].");
            }

            String var11 = d((InputStream)var5);
            var8 = (new Loader(this.Z, this.aa)).a(var11)[0];
            if (var7 != var5.getCounter()) {
               throw new IOException("Object length mismatch [" + this.aa + "].");
            }

            var10000.addElement(var8);
         }

         if (var5.getCounter() != var4) {
            throw new IOException("Section length mismatch [" + this.aa + "].");
         } else {
            c(var1);
         }
      }
   }

   private static final int c(InputStream var0) throws IOException {
      return var0.read() + (var0.read() << 8) + (var0.read() << 16) + (var0.read() << 24);
   }

   private static String d(InputStream var0) throws IOException {
      StringBuffer var1 = new StringBuffer();

      for(int var2 = var0.read(); var2 != 0; var2 = var0.read()) {
         if ((var2 & 128) == 0) {
            var1.append((char)(var2 & 255));
         } else {
            int var3;
            if ((var2 & 224) == 192) {
               if (((var3 = var0.read()) & 192) != 128) {
                  throw new IOException("Invalid UTF-8 string.");
               }

               var1.append((char)((var2 & 31) << 6 | var3 & 63));
            } else {
               if ((var2 & 240) != 224) {
                  throw new IOException("Invalid UTF-8 string.");
               }

               var3 = var0.read();
               int var4 = var0.read();
               if ((var3 & 192) != 128 || (var4 & 192) != 128) {
                  throw new IOException("Invalid UTF-8 string.");
               }

               var1.append((char)((var2 & 15) << 12 | (var3 & 63) << 6 | var4 & 63));
            }
         }
      }

      return var1.toString();
   }

   private static int b(byte[] var0, int var1) {
      if (var0.length - var1 < W.length) {
         return 0;
      } else {
         byte[] var4 = W;
         int var2 = 0;

         int var3;
         for(var3 = 0; var2 < var4.length; ++var2) {
            if (var0[var2 + var1] != var4[var2]) {
               ++var3;
            }
         }

         if (var3 == 0) {
            return 2;
         } else if (var0.length - var1 < V.length) {
            return 0;
         } else {
            var4 = V;
            var2 = 0;

            for(var3 = 0; var2 < var4.length; ++var2) {
               if (var0[var2 + var1] != var4[var2]) {
                  ++var3;
               }
            }

            if (var3 == 0) {
               return 1;
            } else {
               return 0;
            }
         }
      }
   }

   private boolean b(String var1) {
      for(int var2 = 0; var2 < this.Z.size(); ++var2) {
         if (((String)this.Z.elementAt(var2)).equals(var1)) {
            return true;
         }
      }

      return false;
   }

   private static InputStream c(String var0) throws IOException {
      InputConnection var1;
      String var2;
      HttpConnection var5;
      if ((var1 = (InputConnection)Connector.open(var0)) instanceof HttpConnection && (var2 = (var5 = (HttpConnection)var1).getHeaderField("Content-Type")) != null && !var2.equals("application/m3g") && !var2.equals("image/png")) {
         throw new IOException("Wrong MIME type: " + var2 + ".");
      } else {
         InputStream var6;
         try {
            var6 = var1.openInputStream();
         } catch (SecurityException var3) {
            var1.close();
            throw var3;
         } catch (IOException var4) {
            var1.close();
            throw var4;
         }

         var1.close();
         return var6;
      }
   }

   private InputStream d(String var1) throws IOException {
      if (var1.indexOf(58) != -1) {
         return c(var1);
      } else if (var1.charAt(0) == '/') {
         return (new Object()).getClass().getResourceAsStream(var1);
      } else if (this.ab == null) {
         throw new IOException("Relative URI.");
      } else {
         String var2;
         return (var2 = this.ab.substring(0, this.ab.lastIndexOf(47) + 1) + var1).charAt(0) == '/' ? (new Object()).getClass().getResourceAsStream(var2) : c(var2);
      }
   }

   private static native boolean _inflate(byte[] var0, byte[] var1);

   private static native int _ctor(int var0);

   private static native int _decodeData(int var0, int var1, byte[] var2);

   private static native void _setExternalReferences(int var0, int[] var1);

   private static native int _getLoadedObjects(int var0, int[] var1);

   private static native int _getObjectsWithUserParameters(int var0, int[] var1);

   private static native int _getNumUserParameters(int var0, int var1);

   private static native int _getUserParameter(int var0, int var1, int var2, byte[] var3);

   static {
      X = V.length;
   }

   class CountedInputStream extends InputStream {
      private InputStream f;
      private int h;

      public CountedInputStream(Loader var1, InputStream var2) {
         this.f = var2;
         this.h = 0;
      }

      public int read() throws IOException {
         ++this.h;
         return this.f.read();
      }

      public final void c() {
         this.h = 0;
      }

      public int getCounter() {
         return this.h;
      }

      public void close() {
         try {
            this.f.close();
         } catch (IOException var1) {
         }
      }

      public int available() throws IOException {
         return this.f.available();
      }
   }

   class PeekInputStream extends InputStream {
      private int[] e;
      private InputStream f;
      private int g;
      private int h;

      PeekInputStream(Loader var1, InputStream var2, int var3) {
         this.f = var2;
         this.e = new int[var3];
      }

      public int read() throws IOException {
         if (this.h < this.g) {
            return this.e[this.h++];
         } else {
            int var1 = this.f.read();
            if (this.g < this.e.length) {
               this.e[this.g] = var1;
               ++this.g;
            }

            ++this.h;
            return var1;
         }
      }

      public final void a(int var1) {
         int[] var3 = new int[this.e.length + var1];

         for(int var2 = 0; var2 < this.g; ++var2) {
            var3[var2] = this.e[var2];
         }

         this.e = var3;
      }

      public int available() throws IOException {
         return this.h < this.g ? this.g - this.h + this.f.available() : this.f.available();
      }

      public void close() {
         try {
            this.f.close();
         } catch (IOException var1) {
         }
      }

      public final void a() throws IOException {
         if (this.h > this.g) {
            throw new IOException("Peek buffer overrun.");
         } else {
            this.h = 0;
         }
      }
   }
}
