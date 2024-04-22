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
   static final byte[] M3G_FILE_IDENTIFIER = new byte[]{-85, 74, 83, 82, 49, 56, 52, -69, 13, 10, 26, 10};
   static final byte[] PNG_FILE_IDENTIFIER = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
   private static final int INVALID_HEADER_TYPE = 0;
   private static final int M3G_TYPE = 1;
   private static final int PNG_TYPE = 2;
   private static final int MAX_IDENTIFIER_LENGTH;
   private static final int AVG_HEADER_SEC_LENGTH = 64;
   private static final int AVG_XREF_SEC_LENGTH = 128;
   int handle;
   private Vector iLoadedObjects = new Vector();
   private Vector iFileHistory = new Vector();
   private String iResourceName;
   private String iParentResourceName;
   private int iTotalFileSize = 0;
   private int iBytesRead;
   private byte[] streamData;
   private int streamOffset;

   private Loader() {
      this.iBytesRead = M3G_FILE_IDENTIFIER.length;
   }

   private Loader(Vector var1, String var2) {
      this.iBytesRead = M3G_FILE_IDENTIFIER.length;
      this.iParentResourceName = var2;
      this.iFileHistory = var1;
   }

   public static Object3D[] load(String var0) throws IOException {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         try {
            return (new Loader()).loadFromString(var0);
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
            return (new Loader()).loadFromByteArray(var0, var1);
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

   private Object3D[] loadFromString(String var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.inFileHistory(var1)) {
         throw new IOException("Reference loop detected.");
      } else {
         this.iResourceName = var1;
         this.iFileHistory.addElement(var1);
         Loader.PeekInputStream var2 = new Loader.PeekInputStream(this.getInputStream(var1), MAX_IDENTIFIER_LENGTH);
         int var3 = this.getIdentifierType(var2);
         var2.rewind();
         this.streamData = null;
         this.streamOffset = 0;

         Object3D[] var4;
         try {
            var4 = this.loadStream(var2, var3);
         } catch (SecurityException var6) {
            var2.close();
            throw var6;
         } catch (IOException var7) {
            var2.close();
            throw var7;
         }

         var2.close();
         this.iFileHistory.removeElement(var1);
         return var4;
      }
   }

   private Object3D[] loadFromByteArray(byte[] var1, int var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("Resource byte array is null.");
      } else {
         int var3 = getIdentifierType(var1, var2);
         ByteArrayInputStream var4 = new ByteArrayInputStream(var1, var2, var1.length - var2);
         this.streamData = var1;
         this.streamOffset = var2;
         this.iResourceName = "ByteArray";

         Object3D[] var5;
         try {
            var5 = this.loadStream(var4, var3);
         } catch (SecurityException var7) {
            var4.close();
            throw var7;
         } catch (IOException var8) {
            var4.close();
            throw var8;
         }

         var4.close();
         return var5;
      }
   }

   private Object3D[] loadStream(InputStream var1, int var2) throws IOException {
      if (var2 == 1) {
         return this.loadM3G(var1);
      } else if (var2 == 2) {
         return this.loadPNG(var1);
      } else {
         throw new IOException("File not recognized.");
      }
   }

   private Object3D[] loadPNG(InputStream var1) throws IOException {
      byte var2 = 99;
      DataInputStream var3 = new DataInputStream(var1);
      var3.skip((long)PNG_FILE_IDENTIFIER.length);

      try {
         while(true) {
            int var4 = var3.readInt();
            int var5 = var3.readInt();
            if (var5 == 1229472850) {
               var3.skip(9L);
               int var6 = var3.readUnsignedByte();
               var4 -= 10;
               switch(var6) {
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

            if (var5 == 1951551059) {
               switch(var2) {
               case 97:
                  var2 = 98;
                  break;
               case 99:
                  var2 = 100;
               }
            }

            if (var5 == 1229209940) {
               break;
            }

            var3.skip((long)(var4 + 4));
         }
      } catch (Exception var10) {
      }

      Object var11;
      if (this.streamData == null) {
         var11 = this.getInputStream(this.iResourceName);
      } else {
         var11 = new ByteArrayInputStream(this.streamData, this.streamOffset, this.streamData.length - this.streamOffset);
      }

      Image2D var12;
      try {
         var12 = new Image2D(var2, Image.createImage((InputStream)var11));
      } catch (SecurityException var8) {
         ((InputStream)var11).close();
         throw var8;
      } catch (IOException var9) {
         ((InputStream)var11).close();
         throw var9;
      }

      try {
         ((InputStream)var11).close();
      } catch (Exception var7) {
      }

      return new Object3D[]{var12};
   }

   private Object3D[] loadM3G(InputStream var1) throws IOException {
      var1.skip((long)M3G_FILE_IDENTIFIER.length);
      if (var1 instanceof Loader.PeekInputStream) {
         ((Loader.PeekInputStream)var1).increasePeekBuffer(64);
      }

      int var2 = readByte(var1);
      int var3 = readUInt32(var1);
      if (var1 instanceof Loader.PeekInputStream && var3 > 64) {
         ((Loader.PeekInputStream)var1).increasePeekBuffer(var3 - 64);
      }

      int var4 = readUInt32(var1);
      int var5 = readByte(var1);
      int var6 = readUInt32(var1);
      byte var7 = (byte)readByte(var1);
      byte var8 = (byte)readByte(var1);
      boolean var9 = readBoolean(var1);
      this.iTotalFileSize = readUInt32(var1);
      int var10 = readUInt32(var1);
      String var11 = readString(var1);
      int var12 = readUInt32(var1);
      this.handle = _ctor(Interface.getHandle());
      Platform.registerFinalizer(this);
      int var14;
      if (var9) {
         if (var1 instanceof Loader.PeekInputStream) {
            ((Loader.PeekInputStream)var1).increasePeekBuffer(128);
         }

         this.loadExternalRefs(var1);
         if (this.iLoadedObjects.size() <= 0) {
            throw new IOException("No external sections [" + this.iResourceName + "].");
         }

         int[] var13 = new int[this.iLoadedObjects.size()];

         for(var14 = 0; var14 < var13.length; ++var14) {
            var13[var14] = ((Object3D)this.iLoadedObjects.elementAt(var14)).handle;
         }

         _setExternalReferences(this.handle, var13);
      }

      if (var1 instanceof Loader.PeekInputStream) {
         ((Loader.PeekInputStream)var1).rewind();
      } else if (var1.markSupported()) {
         var1.reset();
      }

      int var19 = 0;
      var14 = var1.available();
      if (var14 == 0) {
         var14 = 2048;
      }

      while(var19 < this.iTotalFileSize) {
         if (var19 + var14 > this.iTotalFileSize) {
            var14 = this.iTotalFileSize - var19;
         }

         byte[] var15 = new byte[var14];
         if (var1.read(var15) == -1) {
            break;
         }

         var19 += var14;
         var14 = _decodeData(this.handle, 0, var15);
         if (var14 > 0 && var1.available() > var14) {
            var14 = var1.available();
         }
      }

      if (var14 == 0 && var19 == this.iTotalFileSize) {
         Object3D[] var20 = null;
         int var16 = _getLoadedObjects(this.handle, (int[])null);
         if (var16 > 0) {
            int[] var17 = new int[var16];
            _getLoadedObjects(this.handle, var17);
            var20 = new Object3D[var16];

            for(int var18 = 0; var18 < var20.length; ++var18) {
               var20[var18] = Interface.getObjectInstance(var17[var18]);
            }

            this.setUserObjects();
         }

         return var20;
      } else {
         throw new IOException("Invalid file length [" + this.iResourceName + "].");
      }
   }

   private void setUserObjects() throws IOException {
      int var1 = _getObjectsWithUserParameters(this.handle, (int[])null);
      int[] var2 = null;
      if (var1 > 0) {
         var2 = new int[var1];
         _getObjectsWithUserParameters(this.handle, var2);
      }

      for(int var3 = 0; var3 < var1; ++var3) {
         int var4 = _getNumUserParameters(this.handle, var3);
         if (var4 > 0) {
            Hashtable var5 = new Hashtable();

            for(int var6 = 0; var6 < var4; ++var6) {
               int var7 = _getUserParameter(this.handle, var3, var6, (byte[])null);
               byte[] var8 = new byte[var7];
               int var9 = _getUserParameter(this.handle, var3, var6, var8);
               if (var5.put(new Integer(var9), var8) != null) {
                  throw new IOException("Duplicate id in user data [" + this.iResourceName + "].");
               }
            }

            Object3D var10 = Interface.getObjectInstance(var2[var3]);
            var10.setUserObject(var5);
         }
      }

   }

   private void loadExternalRefs(InputStream var1) throws IOException {
      int var2 = readByte(var1);
      if (var2 != -1 && (this.iTotalFileSize == 0 || this.iBytesRead < this.iTotalFileSize)) {
         int var4 = readUInt32(var1);
         this.iBytesRead += var4;
         if (var1 instanceof Loader.PeekInputStream && var4 > 128) {
            ((Loader.PeekInputStream)var1).increasePeekBuffer(var4 - 128);
         }

         int var5 = readUInt32(var1);
         Loader.CountedInputStream var7 = null;
         if (var2 == 0) {
            var7 = new Loader.CountedInputStream(var1);
            if (var5 != var4 - 13) {
               throw new IOException("Section length mismatch [" + this.iResourceName + "].");
            }
         } else {
            if (var2 != 1) {
               throw new IOException("Unrecognized compression scheme [" + this.iResourceName + "].");
            }

            if (var5 == 0 && var4 - 13 == 0) {
               var7 = new Loader.CountedInputStream((InputStream)null);
            } else {
               if (var5 <= 0 || var4 - 13 <= 0) {
                  throw new IOException("Section length mismatch [" + this.iResourceName + "].");
               }

               byte[] var8 = new byte[var4 - 13];
               var1.read(var8);
               byte[] var9 = new byte[var5];
               if (!_inflate(var8, var9)) {
                  throw new IOException("Decompression error.");
               }

               var7 = new Loader.CountedInputStream(new ByteArrayInputStream(var9));
            }
         }

         var7.resetCounter();

         while(var7.getCounter() < var5) {
            this.iLoadedObjects.addElement(this.loadObject(var7));
         }

         if (var7.getCounter() != var5) {
            throw new IOException("Section length mismatch [" + this.iResourceName + "].");
         } else {
            int var10 = readUInt32(var1);
         }
      }
   }

   private Object3D loadObject(Loader.CountedInputStream var1) throws IOException {
      int var2 = readByte(var1);
      int var3 = readUInt32(var1);
      int var4 = var1.getCounter() + var3;
      Object3D var5 = null;
      if (var2 == 255) {
         String var6 = readString(var1);
         var5 = (new Loader(this.iFileHistory, this.iResourceName)).loadFromString(var6)[0];
         if (var4 != var1.getCounter()) {
            throw new IOException("Object length mismatch [" + this.iResourceName + "].");
         } else {
            return var5;
         }
      } else {
         throw new IOException("Invalid external section [" + this.iResourceName + "].");
      }
   }

   private static final int readByte(InputStream var0) throws IOException {
      return var0.read();
   }

   private static boolean readBoolean(InputStream var0) throws IOException {
      int var1 = var0.read();
      if (var1 == 0) {
         return false;
      } else if (var1 != 1) {
         throw new IOException("Malformed boolean.");
      } else {
         return true;
      }
   }

   private static final int readUInt32(InputStream var0) throws IOException {
      return var0.read() + (var0.read() << 8) + (var0.read() << 16) + (var0.read() << 24);
   }

   private static String readString(InputStream var0) throws IOException {
      StringBuffer var1 = new StringBuffer();
      boolean var2 = false;

      for(int var3 = var0.read(); var3 != 0; var3 = var0.read()) {
         if ((var3 & 128) == 0) {
            var1.append((char)(var3 & 255));
         } else {
            int var4;
            if ((var3 & 224) == 192) {
               var4 = var0.read();
               if ((var4 & 192) != 128) {
                  throw new IOException("Invalid UTF-8 string.");
               }

               var1.append((char)((var3 & 31) << 6 | var4 & 63));
            } else {
               if ((var3 & 240) != 224) {
                  throw new IOException("Invalid UTF-8 string.");
               }

               var4 = var0.read();
               int var5 = var0.read();
               if ((var4 & 192) != 128 || (var5 & 192) != 128) {
                  throw new IOException("Invalid UTF-8 string.");
               }

               var1.append((char)((var3 & 15) << 12 | (var4 & 63) << 6 | var5 & 63));
            }
         }
      }

      return var1.toString();
   }

   private static int getIdentifierType(byte[] var0, int var1) {
      if (var0.length - var1 < PNG_FILE_IDENTIFIER.length) {
         return 0;
      } else {
         byte[] var4 = PNG_FILE_IDENTIFIER;
         int var2 = 0;

         int var3;
         for(var3 = 0; var2 < var4.length; ++var2) {
            if (var0[var2 + var1] != var4[var2]) {
               ++var3;
            }
         }

         if (var3 == 0) {
            return 2;
         } else if (var0.length - var1 < M3G_FILE_IDENTIFIER.length) {
            return 0;
         } else {
            var4 = M3G_FILE_IDENTIFIER;
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

   private int getIdentifierType(InputStream var1) throws IOException {
      byte[] var2 = new byte[MAX_IDENTIFIER_LENGTH];
      var1.read(var2);
      return getIdentifierType(var2, 0);
   }

   private boolean inFileHistory(String var1) {
      for(int var2 = 0; var2 < this.iFileHistory.size(); ++var2) {
         if (((String)this.iFileHistory.elementAt(var2)).equals(var1)) {
            return true;
         }
      }

      return false;
   }

   private InputStream getHttpInputStream(String var1) throws IOException {
      InputConnection var2 = (InputConnection)Connector.open(var1);
      if (var2 instanceof HttpConnection) {
         HttpConnection var3 = (HttpConnection)var2;
         String var4 = var3.getHeaderField("Content-Type");
         if (var4 != null && !var4.equals("application/m3g") && !var4.equals("image/png")) {
            throw new IOException("Wrong MIME type: " + var4 + ".");
         }
      }

      InputStream var7;
      try {
         var7 = var2.openInputStream();
      } catch (SecurityException var5) {
         var2.close();
         throw var5;
      } catch (IOException var6) {
         var2.close();
         throw var6;
      }

      var2.close();
      return var7;
   }

   private InputStream getInputStream(String var1) throws IOException {
      if (var1.indexOf(58) != -1) {
         return this.getHttpInputStream(var1);
      } else if (var1.charAt(0) == '/') {
         return (new Object()).getClass().getResourceAsStream(var1);
      } else if (this.iParentResourceName == null) {
         throw new IOException("Relative URI.");
      } else {
         String var2 = this.iParentResourceName.substring(0, this.iParentResourceName.lastIndexOf(47) + 1) + var1;
         return var2.charAt(0) == '/' ? (new Object()).getClass().getResourceAsStream(var2) : this.getHttpInputStream(var2);
      }
   }

   private final void registeredFinalize() {
      Platform.finalizeObject(this.handle);
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
      MAX_IDENTIFIER_LENGTH = M3G_FILE_IDENTIFIER.length;
   }

   class CountedInputStream extends InputStream {
      private InputStream iStream;
      private int iCounter;

      public CountedInputStream(InputStream var2) {
         this.iStream = var2;
         this.resetCounter();
      }

      public int read() throws IOException {
         ++this.iCounter;
         return this.iStream.read();
      }

      public void resetCounter() {
         this.iCounter = 0;
      }

      public int getCounter() {
         return this.iCounter;
      }

      public void close() {
         try {
            this.iStream.close();
         } catch (IOException var2) {
         }

      }

      public int available() throws IOException {
         return this.iStream.available();
      }
   }

   class PeekInputStream extends InputStream {
      private int[] iPeekBuffer;
      private InputStream iStream;
      private int iBuffered;
      private int iCounter;

      PeekInputStream(InputStream var2, int var3) {
         this.iStream = var2;
         this.iPeekBuffer = new int[var3];
      }

      public int read() throws IOException {
         if (this.iCounter < this.iBuffered) {
            return this.iPeekBuffer[this.iCounter++];
         } else {
            int var1 = this.iStream.read();
            if (this.iBuffered < this.iPeekBuffer.length) {
               this.iPeekBuffer[this.iBuffered] = var1;
               ++this.iBuffered;
            }

            ++this.iCounter;
            return var1;
         }
      }

      public void increasePeekBuffer(int var1) {
         int[] var2 = new int[this.iPeekBuffer.length + var1];

         for(int var3 = 0; var3 < this.iBuffered; ++var3) {
            var2[var3] = this.iPeekBuffer[var3];
         }

         this.iPeekBuffer = var2;
      }

      public int available() throws IOException {
         return this.iCounter < this.iBuffered ? this.iBuffered - this.iCounter + this.iStream.available() : this.iStream.available();
      }

      public void close() {
         try {
            this.iStream.close();
         } catch (IOException var2) {
         }

      }

      public void rewind() throws IOException {
         if (this.iCounter > this.iBuffered) {
            throw new IOException("Peek buffer overrun.");
         } else {
            this.iCounter = 0;
         }
      }
   }
}
