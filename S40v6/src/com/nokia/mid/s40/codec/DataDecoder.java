package com.nokia.mid.s40.codec;

import java.io.IOException;

public class DataDecoder {
   private final int SA_OK = 0;
   private static final String EXC_ENCODING_NOT_SUPPORTED = "DataDecoder: Encoding format not supported.";
   private static final String EXC_UNSUPPORTED_TYPE = "DataDecode: The data type is unsupported!";
   private int nativeDecoder = 0;

   public DataDecoder(String encoding, byte[] data, int offset, int length) throws IOException {
      if (data == null) {
         throw new NullPointerException();
      } else if (length >= 1 && length + offset <= data.length && length + offset >= 0 && offset >= 0) {
         this.createDataDecoder0(encoding, data, offset, length);
         if (this.nativeDecoder == 0) {
            throw new IOException("DataDecoder: Encoding format not supported.");
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public String getName() throws IOException {
      return this.getName0();
   }

   public int getType() throws IOException {
      return this.getType0();
   }

   public boolean listHasMoreItems() throws IOException {
      return this.listHasMoreItems0();
   }

   public void getStart(int type) throws IOException {
      if (DataType.getTypeGroup(type) != 4) {
         throw new IllegalArgumentException("DataDecode: The data type is unsupported!");
      } else {
         this.getStartEnd0(type, true);
      }
   }

   public void getEnd(int type) throws IOException {
      int status = false;
      if (DataType.getTypeGroup(type) != 4) {
         throw new IllegalArgumentException("DataDecode: The data type is unsupported!");
      } else {
         this.getStartEnd0(type, false);
      }
   }

   public String getString(int type) throws IOException {
      if (DataType.getTypeGroup(type) != 3) {
         throw new IllegalArgumentException("DataDecode: The data type is unsupported!");
      } else {
         return this.getString0(type);
      }
   }

   public long getInteger(int type) throws IOException {
      if (DataType.getTypeGroup(type) != 1) {
         throw new IllegalArgumentException("DataDecode: The data type is unsupported!");
      } else {
         return this.getInteger0(type);
      }
   }

   public double getFloat(int type) throws IOException {
      if (DataType.getTypeGroup(type) != 2) {
         throw new IllegalArgumentException("DataDecode: The data type is unsupported!");
      } else {
         return this.getFloat0(type);
      }
   }

   public boolean getBoolean() throws IOException {
      return this.getInteger0(0) == 1L;
   }

   public byte[] getByteArray() throws IOException {
      byte[] byteBuffer = null;
      byte[] byteBuffer = this.getByteArray0();
      if (byteBuffer == null) {
         byteBuffer = new byte[0];
      }

      return byteBuffer;
   }

   private native void createDataDecoder0(String var1, byte[] var2, int var3, int var4) throws IOException;

   private native void getStartEnd0(int var1, boolean var2) throws IOException;

   private native String getString0(int var1) throws IOException;

   private native long getInteger0(int var1) throws IOException;

   private native double getFloat0(int var1) throws IOException;

   private native byte[] getByteArray0() throws IOException;

   private native int getType0() throws IOException;

   private native String getName0() throws IOException;

   private native boolean listHasMoreItems0() throws IOException;
}
