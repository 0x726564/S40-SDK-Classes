package javax.microedition.media.control;

import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public interface RecordControl extends Control {
   void setRecordStream(OutputStream var1);

   void setRecordLocation(String var1) throws IOException, MediaException;

   String getContentType();

   void startRecord();

   void stopRecord();

   void commit() throws IOException;

   int setRecordSizeLimit(int var1) throws MediaException;

   void reset() throws IOException;
}
