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
   static final byte[] JPG_FILE_IDENTIFIER = new byte[]{-1, -40};
   private static final int INVALID_HEADER_TYPE = 0;
   private static final int M3G_TYPE = 1;
   private static final int PNG_TYPE = 2;
   private static final int JPG_TYPE = 3;
   private static final int MAX_IDENTIFIER_LENGTH;
   private static final int AVG_HEADER_SEC_LENGTH = 64;
   private static final int AVG_XREF_SEC_LENGTH = 128;
   private static boolean allowJpgXref;
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
      String jsr248 = System.getProperty("microedition.msa.version");
      if (jsr248 != null) {
         if (jsr248.compareTo("1.0") == 0) {
            allowJpgXref = true;
         }

         if (jsr248.compareTo("1.0-SUBSET") == 0) {
            allowJpgXref = true;
         }
      }

   }

   private Loader(Vector aFileHistory, String aParentResourceName) {
      this.iBytesRead = M3G_FILE_IDENTIFIER.length;
      String jsr248 = System.getProperty("microedition.msa.version");
      if (jsr248 != null) {
         if (jsr248.compareTo("1.0") == 0) {
            allowJpgXref = true;
         }

         if (jsr248.compareTo("1.0-SUBSET") == 0) {
            allowJpgXref = true;
         }
      }

      this.iParentResourceName = aParentResourceName;
      this.iFileHistory = aFileHistory;
   }

   public static Object3D[] load(String name) throws IOException {
      if (name == null) {
         throw new NullPointerException();
      } else {
         try {
            return (new Loader()).loadFromString(name, true);
         } catch (SecurityException var2) {
            throw var2;
         } catch (IOException var3) {
            throw var3;
         } catch (Exception var4) {
            throw new IOException("Load error " + var4);
         }
      }
   }

   public static Object3D[] load(byte[] data, int offset) throws IOException {
      if (data == null) {
         throw new NullPointerException();
      } else if (offset >= 0 && offset < data.length) {
         try {
            return (new Loader()).loadFromByteArray(data, offset, true);
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

   private Object3D[] loadFromString(String aName, boolean allowJpg) throws IOException {
      if (aName == null) {
         throw new NullPointerException();
      } else if (this.inFileHistory(aName)) {
         throw new IOException("Reference loop detected.");
      } else {
         this.iResourceName = aName;
         this.iFileHistory.addElement(aName);
         Loader.PeekInputStream stream = new Loader.PeekInputStream(this.getInputStream(aName), MAX_IDENTIFIER_LENGTH);
         int type = this.getIdentifierType(stream);
         stream.rewind();
         this.streamData = null;
         this.streamOffset = 0;

         Object3D[] o;
         try {
            o = this.loadStream(stream, type, allowJpg);
         } catch (SecurityException var7) {
            stream.close();
            throw var7;
         } catch (IOException var8) {
            stream.close();
            throw var8;
         }

         stream.close();
         this.iFileHistory.removeElement(aName);
         return o;
      }
   }

   private Object3D[] loadFromByteArray(byte[] aData, int aOffset, boolean allowJpg) throws IOException {
      if (aData == null) {
         throw new NullPointerException("Resource byte array is null.");
      } else {
         int type = getIdentifierType(aData, aOffset);
         ByteArrayInputStream stream = new ByteArrayInputStream(aData, aOffset, aData.length - aOffset);
         this.streamData = aData;
         this.streamOffset = aOffset;
         this.iResourceName = "ByteArray";

         Object3D[] o;
         try {
            o = this.loadStream(stream, type, allowJpg);
         } catch (SecurityException var8) {
            stream.close();
            throw var8;
         } catch (IOException var9) {
            stream.close();
            throw var9;
         }

         stream.close();
         return o;
      }
   }

   private Object3D[] loadStream(InputStream aStream, int aType, boolean allowJpg) throws IOException {
      if (aType == 1) {
         return this.loadM3G(aStream);
      } else if (aType == 2) {
         return this.loadPNG(aStream);
      } else if (allowJpg && aType == 3) {
         return this.loadJPG(aStream);
      } else {
         throw new IOException("File not recognized.");
      }
   }

   private Object3D[] loadPNG(InputStream aStream) throws IOException {
      int format = 99;
      DataInputStream png = new DataInputStream(aStream);
      png.skip((long)PNG_FILE_IDENTIFIER.length);

      try {
         while(true) {
            int length = png.readInt();
            int type = png.readInt();
            if (type == 1229472850) {
               png.skip(9L);
               int colourType = png.readUnsignedByte();
               length -= 10;
               switch(colourType) {
               case 0:
                  format = 97;
               case 1:
               case 5:
               default:
                  break;
               case 2:
                  format = 99;
                  break;
               case 3:
                  format = 99;
                  break;
               case 4:
                  format = 98;
                  break;
               case 6:
                  format = 100;
               }
            }

            if (type == 1951551059) {
               switch(format) {
               case 97:
                  format = 98;
                  break;
               case 99:
                  format = 100;
               }
            }

            if (type == 1229209940) {
               break;
            }

            png.skip((long)(length + 4));
         }
      } catch (Exception var10) {
      }

      Object stream;
      if (this.streamData == null) {
         stream = this.getInputStream(this.iResourceName);
      } else {
         stream = new ByteArrayInputStream(this.streamData, this.streamOffset, this.streamData.length - this.streamOffset);
      }

      Image2D i2d;
      try {
         i2d = new Image2D(format, Image.createImage((InputStream)stream));
      } catch (SecurityException var8) {
         ((InputStream)stream).close();
         throw var8;
      } catch (IOException var9) {
         ((InputStream)stream).close();
         throw var9;
      }

      try {
         ((InputStream)stream).close();
      } catch (Exception var7) {
      }

      return new Object3D[]{i2d};
   }

   private Object3D[] loadJPG(InputStream aStream) throws IOException {
      int format = -1;
      DataInputStream jpg = new DataInputStream(aStream);
      jpg.skip(2L);

      try {
         do {
            int marker;
            do {
               marker = jpg.readUnsignedByte();
            } while(marker != 255);

            do {
               marker = jpg.readUnsignedByte();
            } while(marker == 255);

            switch(marker) {
            case 192:
            case 193:
            case 194:
            case 195:
            case 197:
            case 198:
            case 199:
            case 201:
            case 202:
            case 203:
            case 205:
            case 206:
            case 207:
               jpg.skip(7L);
               int numComponents = jpg.readUnsignedByte();
               switch(numComponents) {
               case 1:
                  format = 97;
                  continue;
               case 3:
                  format = 99;
                  continue;
               default:
                  throw new IOException("Unknown JPG format.");
               }
            case 196:
            case 200:
            case 204:
            case 208:
            case 209:
            case 210:
            case 211:
            case 212:
            case 213:
            case 214:
            case 215:
            case 216:
            case 217:
            case 218:
            case 219:
            case 220:
            case 221:
            case 222:
            case 223:
            default:
               jpg.skip((long)(jpg.readUnsignedShort() - 2));
               break;
            case 224:
               int length = jpg.readUnsignedShort();
               int jfif = jpg.readInt();
               if (jfif != 1246120262) {
                  throw new IOException("Not a valid JPG file.");
               }

               jpg.skip((long)(length - 4 - 2));
            }
         } while(format == -1);
      } catch (Exception var11) {
      }

      Object stream;
      if (this.streamData == null) {
         stream = this.getInputStream(this.iResourceName);
      } else {
         stream = new ByteArrayInputStream(this.streamData, this.streamOffset, this.streamData.length - this.streamOffset);
      }

      Image2D i2d;
      try {
         i2d = new Image2D(format, Image.createImage((InputStream)stream));
      } catch (SecurityException var9) {
         ((InputStream)stream).close();
         throw var9;
      } catch (IOException var10) {
         ((InputStream)stream).close();
         throw var10;
      }

      try {
         ((InputStream)stream).close();
      } catch (Exception var8) {
      }

      return new Object3D[]{i2d};
   }

   private Object3D[] loadM3G(InputStream aStream) throws IOException {
      aStream.skip((long)M3G_FILE_IDENTIFIER.length);
      if (aStream instanceof Loader.PeekInputStream) {
         ((Loader.PeekInputStream)aStream).increasePeekBuffer(64);
      }

      int compressionScheme = readByte(aStream);
      int totalSectionLength = readUInt32(aStream);
      if (aStream instanceof Loader.PeekInputStream && totalSectionLength > 64) {
         ((Loader.PeekInputStream)aStream).increasePeekBuffer(totalSectionLength - 64);
      }

      int uncompressedLength = readUInt32(aStream);
      int objectType = readByte(aStream);
      int length = readUInt32(aStream);
      byte vMajor = (byte)readByte(aStream);
      byte vMinor = (byte)readByte(aStream);
      boolean externalLinks = readBoolean(aStream);
      this.iTotalFileSize = readUInt32(aStream);
      int approximateContentSize = readUInt32(aStream);
      String authoringField = readString(aStream);
      int checksum = readUInt32(aStream);
      this.handle = _ctor(Interface.getHandle());
      Platform.registerFinalizer(this);
      int size;
      if (externalLinks) {
         if (aStream instanceof Loader.PeekInputStream) {
            ((Loader.PeekInputStream)aStream).increasePeekBuffer(128);
         }

         this.loadExternalRefs(aStream);
         if (this.iLoadedObjects.size() <= 0) {
            throw new IOException("No external sections [" + this.iResourceName + "].");
         }

         int[] xRef = new int[this.iLoadedObjects.size()];

         for(size = 0; size < xRef.length; ++size) {
            xRef[size] = ((Object3D)this.iLoadedObjects.elementAt(size)).handle;
         }

         _setExternalReferences(this.handle, xRef);
      }

      if (aStream instanceof Loader.PeekInputStream) {
         ((Loader.PeekInputStream)aStream).rewind();
      } else if (aStream.markSupported()) {
         aStream.reset();
      }

      int read = 0;
      size = aStream.available();
      if (size == 0) {
         size = 2048;
      }

      while(read < this.iTotalFileSize) {
         if (read + size > this.iTotalFileSize) {
            size = this.iTotalFileSize - read;
         }

         byte[] data = new byte[size];
         if (aStream.read(data) == -1) {
            break;
         }

         read += size;
         size = _decodeData(this.handle, 0, data);
         if (size > 0 && aStream.available() > size) {
            size = aStream.available();
         }
      }

      if (size == 0 && read == this.iTotalFileSize) {
         Object3D[] objects = null;
         int num = _getLoadedObjects(this.handle, (int[])null);
         if (num > 0) {
            int[] obj = new int[num];
            _getLoadedObjects(this.handle, obj);
            objects = new Object3D[num];

            for(int i = 0; i < objects.length; ++i) {
               objects[i] = Interface.getObjectInstance(obj[i]);
            }

            this.setUserObjects();
         }

         return objects;
      } else {
         throw new IOException("Invalid file length [" + this.iResourceName + "].");
      }
   }

   private void setUserObjects() throws IOException {
      int numObjects = _getObjectsWithUserParameters(this.handle, (int[])null);
      int[] obj = null;
      if (numObjects > 0) {
         obj = new int[numObjects];
         _getObjectsWithUserParameters(this.handle, obj);
      }

      for(int i = 0; i < numObjects; ++i) {
         int num = _getNumUserParameters(this.handle, i);
         if (num > 0) {
            Hashtable hash = new Hashtable();

            for(int j = 0; j < num; ++j) {
               int len = _getUserParameter(this.handle, i, j, (byte[])null);
               byte[] data = new byte[len];
               int id = _getUserParameter(this.handle, i, j, data);
               if (hash.put(new Integer(id), data) != null) {
                  throw new IOException("Duplicate id in user data [" + this.iResourceName + "].");
               }
            }

            Object3D object = Interface.getObjectInstance(obj[i]);
            object.setUserObject(hash);
         }
      }

   }

   private void loadExternalRefs(InputStream aStream) throws IOException {
      int firstByte = readByte(aStream);
      if (firstByte != -1 && (this.iTotalFileSize == 0 || this.iBytesRead < this.iTotalFileSize)) {
         int totalSectionLength = readUInt32(aStream);
         this.iBytesRead += totalSectionLength;
         if (aStream instanceof Loader.PeekInputStream && totalSectionLength > 128) {
            ((Loader.PeekInputStream)aStream).increasePeekBuffer(totalSectionLength - 128);
         }

         int uncompressedLength = readUInt32(aStream);
         Loader.CountedInputStream uncompressedStream = null;
         if (firstByte == 0) {
            uncompressedStream = new Loader.CountedInputStream(aStream);
            if (uncompressedLength != totalSectionLength - 13) {
               throw new IOException("Section length mismatch [" + this.iResourceName + "].");
            }
         } else {
            if (firstByte != 1) {
               throw new IOException("Unrecognized compression scheme [" + this.iResourceName + "].");
            }

            if (uncompressedLength == 0 && totalSectionLength - 13 == 0) {
               uncompressedStream = new Loader.CountedInputStream((InputStream)null);
            } else {
               if (uncompressedLength <= 0 || totalSectionLength - 13 <= 0) {
                  throw new IOException("Section length mismatch [" + this.iResourceName + "].");
               }

               byte[] compressed = new byte[totalSectionLength - 13];
               aStream.read(compressed);
               byte[] uncompressed = new byte[uncompressedLength];
               if (!_inflate(compressed, uncompressed)) {
                  throw new IOException("Decompression error.");
               }

               uncompressedStream = new Loader.CountedInputStream(new ByteArrayInputStream(uncompressed));
            }
         }

         uncompressedStream.resetCounter();

         while(uncompressedStream.getCounter() < uncompressedLength) {
            this.iLoadedObjects.addElement(this.loadObject(uncompressedStream));
         }

         if (uncompressedStream.getCounter() != uncompressedLength) {
            throw new IOException("Section length mismatch [" + this.iResourceName + "].");
         } else {
            int checksum = readUInt32(aStream);
         }
      }
   }

   private Object3D loadObject(Loader.CountedInputStream aStream) throws IOException {
      int objectType = readByte(aStream);
      int length = readUInt32(aStream);
      int expectedCount = aStream.getCounter() + length;
      Object3D newObject = null;
      if (objectType == 255) {
         String xref = readString(aStream);
         newObject = (new Loader(this.iFileHistory, this.iResourceName)).loadFromString(xref, allowJpgXref)[0];
         if (expectedCount != aStream.getCounter()) {
            throw new IOException("Object length mismatch [" + this.iResourceName + "].");
         } else {
            return newObject;
         }
      } else {
         throw new IOException("Invalid external section [" + this.iResourceName + "].");
      }
   }

   private static final int readByte(InputStream aStream) throws IOException {
      return aStream.read();
   }

   private static boolean readBoolean(InputStream aStream) throws IOException {
      int b = aStream.read();
      if (b == 0) {
         return false;
      } else if (b != 1) {
         throw new IOException("Malformed boolean.");
      } else {
         return true;
      }
   }

   private static final int readUInt32(InputStream aStream) throws IOException {
      return aStream.read() + (aStream.read() << 8) + (aStream.read() << 16) + (aStream.read() << 24);
   }

   private static String readString(InputStream aStream) throws IOException {
      StringBuffer result = new StringBuffer();
      int i = false;

      for(int c = aStream.read(); c != 0; c = aStream.read()) {
         if ((c & 128) == 0) {
            result.append((char)(c & 255));
         } else {
            int c2;
            if ((c & 224) == 192) {
               c2 = aStream.read();
               if ((c2 & 192) != 128) {
                  throw new IOException("Invalid UTF-8 string.");
               }

               result.append((char)((c & 31) << 6 | c2 & 63));
            } else {
               if ((c & 240) != 224) {
                  throw new IOException("Invalid UTF-8 string.");
               }

               c2 = aStream.read();
               int c3 = aStream.read();
               if ((c2 & 192) != 128 || (c3 & 192) != 128) {
                  throw new IOException("Invalid UTF-8 string.");
               }

               result.append((char)((c & 15) << 12 | (c2 & 63) << 6 | c3 & 63));
            }
         }
      }

      return result.toString();
   }

   private static int getIdentifierType(byte[] aData, int aOffset) {
      if (aData.length - aOffset < JPG_FILE_IDENTIFIER.length) {
         return 0;
      } else {
         byte[] identifier = JPG_FILE_IDENTIFIER;
         int i = 0;

         int diff;
         for(diff = 0; i < identifier.length; ++i) {
            if (aData[i + aOffset] != identifier[i]) {
               ++diff;
            }
         }

         if (diff == 0) {
            return 3;
         } else if (aData.length - aOffset < PNG_FILE_IDENTIFIER.length) {
            return 0;
         } else {
            identifier = PNG_FILE_IDENTIFIER;
            i = 0;

            for(diff = 0; i < identifier.length; ++i) {
               if (aData[i + aOffset] != identifier[i]) {
                  ++diff;
               }
            }

            if (diff == 0) {
               return 2;
            } else if (aData.length - aOffset < M3G_FILE_IDENTIFIER.length) {
               return 0;
            } else {
               identifier = M3G_FILE_IDENTIFIER;
               i = 0;

               for(diff = 0; i < identifier.length; ++i) {
                  if (aData[i + aOffset] != identifier[i]) {
                     ++diff;
                  }
               }

               if (diff == 0) {
                  return 1;
               } else {
                  return 0;
               }
            }
         }
      }
   }

   private int getIdentifierType(InputStream aStream) throws IOException {
      byte[] identifier = new byte[MAX_IDENTIFIER_LENGTH];
      aStream.read(identifier);
      return getIdentifierType(identifier, 0);
   }

   private boolean inFileHistory(String name) {
      for(int i = 0; i < this.iFileHistory.size(); ++i) {
         if (((String)this.iFileHistory.elementAt(i)).equals(name)) {
            return true;
         }
      }

      return false;
   }

   private InputStream getHttpInputStream(String name) throws IOException {
      InputConnection ic = (InputConnection)Connector.open(name);
      if (ic instanceof HttpConnection) {
         HttpConnection hc = (HttpConnection)ic;
         String contentType = hc.getHeaderField("Content-Type");
         if (contentType != null && !contentType.equals("application/m3g") && !contentType.equals("image/png") && !contentType.equals("image/jpeg")) {
            throw new IOException("Wrong MIME type: " + contentType + ".");
         }
      }

      InputStream is;
      try {
         is = ic.openInputStream();
      } catch (SecurityException var5) {
         ic.close();
         throw var5;
      } catch (IOException var6) {
         ic.close();
         throw var6;
      }

      ic.close();
      return is;
   }

   private InputStream getInputStream(String name) throws IOException {
      if (name.indexOf(58) != -1) {
         return this.getHttpInputStream(name);
      } else if (name.charAt(0) == '/') {
         return (new Object()).getClass().getResourceAsStream(name);
      } else if (this.iParentResourceName == null) {
         throw new IOException("Relative URI.");
      } else {
         String uri = this.iParentResourceName.substring(0, this.iParentResourceName.lastIndexOf(47) + 1) + name;
         return uri.charAt(0) == '/' ? (new Object()).getClass().getResourceAsStream(uri) : this.getHttpInputStream(uri);
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
      allowJpgXref = false;
   }

   class CountedInputStream extends InputStream {
      private InputStream iStream;
      private int iCounter;

      public CountedInputStream(InputStream aStream) {
         this.iStream = aStream;
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

      PeekInputStream(InputStream aStream, int aLength) {
         this.iStream = aStream;
         this.iPeekBuffer = new int[aLength];
      }

      public int read() throws IOException {
         if (this.iCounter < this.iBuffered) {
            return this.iPeekBuffer[this.iCounter++];
         } else {
            int nv = this.iStream.read();
            if (this.iBuffered < this.iPeekBuffer.length) {
               this.iPeekBuffer[this.iBuffered] = nv;
               ++this.iBuffered;
            }

            ++this.iCounter;
            return nv;
         }
      }

      public void increasePeekBuffer(int aLength) {
         int[] temp = new int[this.iPeekBuffer.length + aLength];

         for(int i = 0; i < this.iBuffered; ++i) {
            temp[i] = this.iPeekBuffer[i];
         }

         this.iPeekBuffer = temp;
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
