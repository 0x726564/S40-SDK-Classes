package java.io;

public class DataOutputStream extends OutputStream implements DataOutput {
   protected OutputStream out;

   public DataOutputStream(OutputStream var1) {
      this.out = var1;
   }

   public void write(int var1) throws IOException {
      this.out.write(var1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public void close() throws IOException {
      this.out.close();
   }

   public final void writeBoolean(boolean var1) throws IOException {
      this.write(var1 ? 1 : 0);
   }

   public final void writeByte(int var1) throws IOException {
      this.write(var1);
   }

   public final void writeShort(int var1) throws IOException {
      this.write(var1 >>> 8 & 255);
      this.write(var1 >>> 0 & 255);
   }

   public final void writeChar(int var1) throws IOException {
      this.write(var1 >>> 8 & 255);
      this.write(var1 >>> 0 & 255);
   }

   public final void writeInt(int var1) throws IOException {
      this.write(var1 >>> 24 & 255);
      this.write(var1 >>> 16 & 255);
      this.write(var1 >>> 8 & 255);
      this.write(var1 >>> 0 & 255);
   }

   public final void writeLong(long var1) throws IOException {
      this.write((int)(var1 >>> 56) & 255);
      this.write((int)(var1 >>> 48) & 255);
      this.write((int)(var1 >>> 40) & 255);
      this.write((int)(var1 >>> 32) & 255);
      this.write((int)(var1 >>> 24) & 255);
      this.write((int)(var1 >>> 16) & 255);
      this.write((int)(var1 >>> 8) & 255);
      this.write((int)(var1 >>> 0) & 255);
   }

   public final void writeFloat(float var1) throws IOException {
      this.writeInt(Float.floatToIntBits(var1));
   }

   public final void writeDouble(double var1) throws IOException {
      this.writeLong(Double.doubleToLongBits(var1));
   }

   public final void writeChars(String var1) throws IOException {
      int var2 = var1.length();

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var1.charAt(var3);
         this.write(var4 >>> 8 & 255);
         this.write(var4 >>> 0 & 255);
      }

   }

   public final void writeUTF(String var1) throws IOException {
      writeUTF(var1, this);
   }

   static final int writeUTF(String var0, DataOutput var1) throws IOException {
      int var2 = var0.length();
      int var3 = 0;
      char[] var4 = new char[var2];
      byte var6 = 0;
      var0.getChars(0, var2, var4, 0);

      char var5;
      for(int var7 = 0; var7 < var2; ++var7) {
         var5 = var4[var7];
         if (var5 >= 1 && var5 <= 127) {
            ++var3;
         } else if (var5 > 2047) {
            var3 += 3;
         } else {
            var3 += 2;
         }
      }

      if (var3 > 65535) {
         throw new UTFDataFormatException();
      } else {
         byte[] var10 = new byte[var3 + 2];
         int var9 = var6 + 1;
         var10[var6] = (byte)(var3 >>> 8 & 255);
         var10[var9++] = (byte)(var3 >>> 0 & 255);

         for(int var8 = 0; var8 < var2; ++var8) {
            var5 = var4[var8];
            if (var5 >= 1 && var5 <= 127) {
               var10[var9++] = (byte)var5;
            } else if (var5 > 2047) {
               var10[var9++] = (byte)(224 | var5 >> 12 & 15);
               var10[var9++] = (byte)(128 | var5 >> 6 & 63);
               var10[var9++] = (byte)(128 | var5 >> 0 & 63);
            } else {
               var10[var9++] = (byte)(192 | var5 >> 6 & 31);
               var10[var9++] = (byte)(128 | var5 >> 0 & 63);
            }
         }

         var1.write(var10);
         return var3 + 2;
      }
   }
}
