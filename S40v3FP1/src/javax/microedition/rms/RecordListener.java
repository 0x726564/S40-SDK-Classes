package javax.microedition.rms;

public interface RecordListener {
   void recordAdded(RecordStore var1, int var2);

   void recordChanged(RecordStore var1, int var2);

   void recordDeleted(RecordStore var1, int var2);
}
