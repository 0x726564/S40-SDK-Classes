package javax.microedition.io.file;

public interface FileSystemListener {
   int ROOT_ADDED = 0;
   int ROOT_REMOVED = 1;

   void rootChanged(int var1, String var2);
}
