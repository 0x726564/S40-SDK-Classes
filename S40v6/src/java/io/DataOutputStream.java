package java.io;

public class DataOutputStream extends OutputStream implements DataOutput {
   protected OutputStream out;

   public DataOutputStream(OutputStream out) {
      this.out = out;
   }

   public void write(int b) throws IOException {
      this.out.write(b);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      this.out.write(b, off, len);
   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public void close() throws IOException {
      this.out.close();
   }

   public final void writeBoolean(boolean v) throws IOException {
      this.write(v ? 1 : 0);
   }

   public final void writeByte(int v) throws IOException {
      this.write(v);
   }

   public final void writeShort(int v) throws IOException {
      this.write(v >>> 8 & 255);
      this.write(v >>> 0 & 255);
   }

   public final void writeChar(int v) throws IOException {
      this.write(v >>> 8 & 255);
      this.write(v >>> 0 & 255);
   }

   public final void writeInt(int v) throws IOException {
      this.write(v >>> 24 & 255);
      this.write(v >>> 16 & 255);
      this.write(v >>> 8 & 255);
      this.write(v >>> 0 & 255);
   }

   public final void writeLong(long v) throws IOException {
      this.write((int)(v >>> 56) & 255);
      this.write((int)(v >>> 48) & 255);
      this.write((int)(v >>> 40) & 255);
      this.write((int)(v >>> 32) & 255);
      this.write((int)(v >>> 24) & 255);
      this.write((int)(v >>> 16) & 255);
      this.write((int)(v >>> 8) & 255);
      this.write((int)(v >>> 0) & 255);
   }

   public final void writeFloat(float v) throws IOException {
      this.writeInt(Float.floatToIntBits(v));
   }

   public final void writeDouble(double v) throws IOException {
      this.writeLong(Double.doubleToLongBits(v));
   }

   public final void writeChars(String s) throws IOException {
      int len = s.length();

      for(int i = 0; i < len; ++i) {
         int v = s.charAt(i);
         this.write(v >>> 8 & 255);
         this.write(v >>> 0 & 255);
      }

   }

   public final void writeUTF(String str) throws IOException {
      writeUTF(str, this);
   }

   static final int writeUTF(String str, DataOutput out) throws IOException {
      int strlen = str.length();
      int utflen = 0;
      char[] charr = new char[strlen];
      int count = 0;
      str.getChars(0, strlen, charr, 0);

      char c;
      for(int i = 0; i < strlen; ++i) {
         c = charr[i];
         if (c >= 1 && c <= 127) {
            ++utflen;
         } else if (c > 2047) {
            utflen += 3;
         } else {
            utflen += 2;
         }
      }

      if (utflen > 65535) {
         throw new UTFDataFormatException();
      } else {
         byte[] bytearr = new byte[utflen + 2];
         int var9 = count + 1;
         bytearr[count] = (byte)(utflen >>> 8 & 255);
         bytearr[var9++] = (byte)(utflen >>> 0 & 255);

         for(int i = 0; i < strlen; ++i) {
            c = charr[i];
            if (c >= 1 && c <= 127) {
               bytearr[var9++] = (byte)c;
            } else if (c > 2047) {
               bytearr[var9++] = (byte)(224 | c >> 12 & 15);
               bytearr[var9++] = (byte)(128 | c >> 6 & 63);
               bytearr[var9++] = (byte)(128 | c >> 0 & 63);
            } else {
               bytearr[var9++] = (byte)(192 | c >> 6 & 31);
               bytearr[var9++] = (byte)(128 | c >> 0 & 63);
            }
         }

         out.write(bytearr);
         return utflen + 2;
      }
   }
}
