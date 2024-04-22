package javax.microedition.rms;

class DataConverter {
   private DataConverter() {
   }

   public static int getInt(byte[] data, int offset) {
      return 0;
   }

   public static int putInt(int i, byte[] data, int offset) {
      return 0;
   }

   public static long getLong(byte[] data, int offset) {
      return 0L;
   }

   public static int putLong(long l, byte[] data, int offset) {
      return 0;
   }

   public static char getChar(byte[] data, int offset) {
      return '0';
   }

   public static int putChar(char c, byte[] data, int offset) {
      return 0;
   }

   public static String getString(byte[] data, int offset, int numBytes) {
      return null;
   }

   public static int putString(String s, byte[] data, int offset) {
      return 0;
   }
}
