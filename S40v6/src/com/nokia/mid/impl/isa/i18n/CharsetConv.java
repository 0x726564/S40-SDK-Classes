package com.nokia.mid.impl.isa.i18n;

public class CharsetConv {
   public static final String defaultEncoding = System.getProperty("microedition.encoding");

   public static native short[] initConv(byte[] var0, char var1);

   public static native byte[] isSupportedEncoding(String var0);

   public static native int isFixedSizeEncoding(byte[] var0);

   public static native int getMaxByteLength(byte[] var0);

   public static native int byteToChar(short[] var0, byte[] var1, int var2, int var3, char[] var4, int var5);

   public static native int charToByte(short[] var0, char[] var1, int var2, byte[] var3, int var4, int var5);

   public static native int byteToCharArray(byte[] var0, byte[] var1, int var2, int var3, char[] var4, int var5, int var6);

   public static native int charArrayToByte(byte[] var0, char[] var1, int var2, int var3, byte[] var4, int var5, int var6);
}
