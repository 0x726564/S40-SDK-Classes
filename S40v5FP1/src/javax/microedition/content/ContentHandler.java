package javax.microedition.content;

public interface ContentHandler {
   String ACTION_EDIT = "edit";
   String ACTION_EXECUTE = "execute";
   String ACTION_INSTALL = "install";
   String ACTION_NEW = "new";
   String ACTION_OPEN = "open";
   String ACTION_PRINT = "print";
   String ACTION_SAVE = "save";
   String ACTION_SELECT = "select";
   String ACTION_SEND = "send";
   String ACTION_STOP = "stop";
   String UNIVERSAL_TYPE = "*";

   String getAction(int var1);

   int getActionCount();

   ActionNameMap getActionNameMap();

   ActionNameMap getActionNameMap(int var1);

   ActionNameMap getActionNameMap(String var1);

   int getActionNameMapCount();

   String getAppName();

   String getAuthority();

   String getID();

   String getSuffix(int var1);

   int getSuffixCount();

   String getType(int var1);

   int getTypeCount();

   String getVersion();

   boolean hasAction(String var1);

   boolean hasSuffix(String var1);

   boolean hasType(String var1);
}
