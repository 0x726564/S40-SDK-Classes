package javax.microedition.io.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.microedition.io.StreamConnection;

public interface FileConnection extends StreamConnection {
   boolean isOpen();

   InputStream openInputStream() throws IOException;

   DataInputStream openDataInputStream() throws IOException;

   OutputStream openOutputStream() throws IOException;

   DataOutputStream openDataOutputStream() throws IOException;

   OutputStream openOutputStream(long var1) throws IOException;

   long totalSize();

   long availableSize();

   long usedSize();

   long directorySize(boolean var1) throws IOException;

   long fileSize() throws IOException;

   boolean canRead();

   boolean canWrite();

   boolean isHidden();

   void setReadable(boolean var1) throws IOException;

   void setWritable(boolean var1) throws IOException;

   void setHidden(boolean var1) throws IOException;

   Enumeration list() throws IOException;

   Enumeration list(String var1, boolean var2) throws IOException;

   void create() throws IOException;

   void mkdir() throws IOException;

   boolean exists();

   boolean isDirectory();

   void delete() throws IOException;

   void rename(String var1) throws IOException;

   void truncate(long var1) throws IOException;

   void setFileConnection(String var1) throws IOException;

   String getName();

   String getPath();

   String getURL();

   long lastModified();
}
