package javax.microedition.amms;

import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.media.Controllable;
import javax.microedition.media.MediaException;

public interface MediaProcessor extends Controllable {
   int REALIZED = 200;
   int STARTED = 400;
   int STOPPED = 300;
   int UNKNOWN = -1;
   int UNREALIZED = 100;

   void abort();

   void addMediaProcessorListener(MediaProcessorListener var1);

   void complete() throws MediaException;

   int getProgress();

   int getState();

   void removeMediaProcessorListener(MediaProcessorListener var1);

   void setInput(InputStream var1, int var2) throws MediaException;

   void setInput(Object var1) throws MediaException;

   void setOutput(OutputStream var1);

   void start() throws MediaException;

   void stop() throws MediaException;
}
