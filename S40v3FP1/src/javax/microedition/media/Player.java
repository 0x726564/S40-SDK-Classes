package javax.microedition.media;

public interface Player extends Controllable {
   int UNREALIZED = 100;
   int REALIZED = 200;
   int PREFETCHED = 300;
   int STARTED = 400;
   int CLOSED = 0;
   long TIME_UNKNOWN = -1L;

   void realize() throws MediaException;

   void prefetch() throws MediaException;

   void start() throws MediaException;

   void stop() throws MediaException;

   void deallocate();

   void close();

   void setTimeBase(TimeBase var1) throws MediaException;

   TimeBase getTimeBase();

   long setMediaTime(long var1) throws MediaException;

   long getMediaTime();

   int getState();

   long getDuration();

   String getContentType();

   void setLoopCount(int var1);

   void addPlayerListener(PlayerListener var1);

   void removePlayerListener(PlayerListener var1);
}
