package java.io;

public interface DataOutput {
   void write(int var1) throws IOException;

   void write(byte[] var1) throws IOException;

   void write(byte[] var1, int var2, int var3) throws IOException;

   void writeBoolean(boolean var1) throws IOException;

   void writeByte(int var1) throws IOException;

   void writeShort(int var1) throws IOException;

   void writeChar(int var1) throws IOException;

   void writeInt(int var1) throws IOException;

   void writeLong(long var1) throws IOException;

   void writeFloat(float var1) throws IOException;

   void writeDouble(double var1) throws IOException;

   void writeChars(String var1) throws IOException;

   void writeUTF(String var1) throws IOException;
}
