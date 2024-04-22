package javax.microedition.content;

public interface ContentHandlerServer extends ContentHandler {
   int accessAllowedCount();

   void cancelGetRequest();

   boolean finish(Invocation var1, int var2);

   String getAccessAllowed(int var1);

   Invocation getRequest(boolean var1);

   boolean isAccessAllowed(String var1);

   void setListener(RequestListener var1);
}
