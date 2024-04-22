package com.nokia.mid.s40.codec;

import java.io.IOException;

public class DataEncoder {
   private int nativeEncoder = 0;
   private boolean bufferReleased = false;
   private static final String EXC_ENCODING_NOT_SUPPORTED = "DataEncoder: Encoding format not supported.";
   private static final String EXC_BUFFER_RELEASED = "DataEncoder: The buffer was previously released!";
   private static final String EXC_UNSUPPORTED_TYPE = "DataEncoder: The data type is unsupported!";
   private static final String EXC_NOT_IN_RANGE = "DataEncoder: The value is not within range";

   public DataEncoder(String encoding) throws IOException {
      if (encoding == null) {
         throw new NullPointerException();
      } else {
         this.createDataEncoder0(encoding);
         if (this.nativeEncoder == 0) {
            throw new IOException("DataEncoder: Encoding format not supported.");
         }
      }
   }

   public byte[] getData() throws IOException {
      byte[] data = null;
      if (this.bufferReleased) {
         throw new IOException("DataEncoder: The buffer was previously released!");
      } else {
         byte[] data = this.getData0();
         this.bufferReleased = true;
         return data;
      }
   }

   public void putStart(int type, String name) throws IOException {
      if (name == null) {
         throw new NullPointerException();
      } else if (DataType.getTypeGroup(type) != 4) {
         throw new IllegalArgumentException("DataEncoder: The data type is unsupported!");
      } else if (this.bufferReleased) {
         throw new IOException("DataEncoder: The buffer was previously released!");
      } else {
         this.putStartEnd0(type, name, true);
      }
   }

   public void putEnd(int type, String name) throws IOException {
      if (name == null) {
         throw new NullPointerException();
      } else if (DataType.getTypeGroup(type) != 4) {
         throw new IllegalArgumentException("DataEncoder: The data type is unsupported!");
      } else if (this.bufferReleased) {
         throw new IOException("DataEncoder: The buffer was previously released!");
      } else {
         this.putStartEnd0(type, name, false);
      }
   }

   public void put(int type, String name, String value) throws IOException {
      if (name != null && value != null) {
         if (DataType.getTypeGroup(type) != 3) {
            throw new IllegalArgumentException("DataEncoder: The data type is unsupported!");
         } else if (this.bufferReleased) {
            throw new IOException("DataEncoder: The buffer was previously released!");
         } else {
            this.putString0(type, name, value);
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void put(int type, String name, boolean value) throws IOException {
      if (name == null) {
         throw new NullPointerException();
      } else if (type != 0) {
         throw new IllegalArgumentException("DataEncoder: The data type is unsupported!");
      } else if (this.bufferReleased) {
         throw new IOException("DataEncoder: The buffer was previously released!");
      } else {
         this.putInteger0(type, name, value ? 1L : 0L);
      }
   }

   public void put(int type, String name, long value) throws IOException {
      if (name == null) {
         throw new NullPointerException();
      } else if (DataType.getTypeGroup(type) != 1) {
         throw new IllegalArgumentException("DataEncoder: The data type is unsupported!");
      } else if (this.bufferReleased) {
         throw new IOException("DataEncoder: The buffer was previously released!");
      } else {
         switch(type) {
         case 1:
            if (value < -128L || value > 127L) {
               throw new IllegalArgumentException("DataEncoder: The value is not within range");
            }
            break;
         case 2:
            if (value >= 0L && value <= 255L) {
               break;
            }

            throw new IllegalArgumentException("DataEncoder: The value is not within range");
         case 3:
            if (value < 0L || value > 65535L) {
               throw new IllegalArgumentException("DataEncoder: The value is not within range");
            }
            break;
         case 4:
            if (value < -32768L || value > 32767L) {
               throw new IllegalArgumentException("DataEncoder: The value is not within range");
            }
            break;
         case 5:
            if (value < 0L || value > 65535L) {
               throw new IllegalArgumentException("DataEncoder: The value is not within range");
            }
            break;
         case 6:
            if (value < -2147483648L || value > 2147483647L) {
               throw new IllegalArgumentException("DataEncoder: The value is not within range");
            }
         }

         this.putInteger0(type, name, value);
      }
   }

   public void put(int type, String name, double value) throws IOException {
      if (name == null) {
         throw new NullPointerException();
      } else if (DataType.getTypeGroup(type) != 2) {
         throw new IllegalArgumentException();
      } else if (this.bufferReleased) {
         throw new IOException("DataEncoder: The buffer was previously released!");
      } else if (type != 8 || !(value < 1.401298464324817E-45D) && !(value > 3.4028234663852886E38D)) {
         this.putFloat0(type, name, value);
      } else {
         throw new IllegalArgumentException("DataEncoder: The value is not within range");
      }
   }

   public void put(String name, byte[] array, int length) throws IOException {
      if (name == null) {
         throw new NullPointerException();
      } else if (length < 0) {
         throw new IllegalArgumentException();
      } else {
         if (array == null) {
            if (length > 0) {
               throw new IllegalArgumentException();
            }
         } else if (length > array.length) {
            throw new IllegalArgumentException();
         }

         this.putByteArray0(name, array, length);
      }
   }

   private native void createDataEncoder0(String var1) throws IOException;

   private native void putStartEnd0(int var1, String var2, boolean var3) throws IOException;

   private native void putString0(int var1, String var2, String var3) throws IOException;

   private native void putInteger0(int var1, String var2, long var3) throws IOException;

   private native void putFloat0(int var1, String var2, double var3) throws IOException;

   private native byte[] getData0() throws IOException;

   private native void putByteArray0(String var1, byte[] var2, int var3) throws IOException;
}
