package javax.microedition.media.protocol;

import java.io.IOException;
import javax.microedition.media.Controllable;

public interface SourceStream extends Controllable {
   int NOT_SEEKABLE = 0;
   int SEEKABLE_TO_START = 1;
   int RANDOM_ACCESSIBLE = 2;

   ContentDescriptor getContentDescriptor();

   long getContentLength();

   int read(byte[] var1, int var2, int var3) throws IOException;

   int getTransferSize();

   long seek(long var1) throws IOException;

   long tell();

   int getSeekType();
}
