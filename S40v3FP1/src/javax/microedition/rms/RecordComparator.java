package javax.microedition.rms;

public interface RecordComparator {
   int EQUIVALENT = 0;
   int FOLLOWS = 1;
   int PRECEDES = -1;

   int compare(byte[] var1, byte[] var2);
}
