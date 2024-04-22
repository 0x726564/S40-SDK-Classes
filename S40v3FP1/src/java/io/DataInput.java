package java.io;

public interface DataInput {
   void readFully(byte[] var1) throws IOException;

   void readFully(byte[] var1, int var2, int var3) throws IOException;

   int skipBytes(int var1) throws IOException;

   boolean readBoolean() throws IOException;

   byte readByte() throws IOException;

   int readUnsignedByte() throws IOException;

   short readShort() throws IOException;

   int readUnsignedShort() throws IOException;

   char readChar() throws IOException;

   int readInt() throws IOException;

   long readLong() throws IOException;

   float readFloat() throws IOException;

   double readDouble() throws IOException;

   String readUTF() throws IOException;
}
