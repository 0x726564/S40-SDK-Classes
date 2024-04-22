package javax.microedition.rms;

public interface RecordEnumeration {
   int numRecords();

   byte[] nextRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException;

   int nextRecordId() throws InvalidRecordIDException;

   byte[] previousRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException;

   int previousRecordId() throws InvalidRecordIDException;

   boolean hasNextElement();

   boolean hasPreviousElement();

   void reset();

   void rebuild();

   void keepUpdated(boolean var1);

   boolean isKeptUpdated();

   void destroy();
}
