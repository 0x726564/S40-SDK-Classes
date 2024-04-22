package javax.microedition.media.protocol;

import java.io.IOException;
import javax.microedition.media.Controllable;

public abstract class DataSource implements Controllable {
   private String d;

   public DataSource(String var1) {
      this.d = var1;
   }

   public String getLocator() {
      return this.d;
   }

   public abstract String getContentType();

   public abstract void connect() throws IOException;

   public abstract void disconnect();

   public abstract void start() throws IOException;

   public abstract void stop() throws IOException;

   public abstract SourceStream[] getStreams();
}
