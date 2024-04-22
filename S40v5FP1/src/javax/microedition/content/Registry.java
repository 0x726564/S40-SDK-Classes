package javax.microedition.content;

import com.nokia.mid.impl.isa.content.InvocationImpl;
import com.nokia.mid.impl.isa.content.RegistryImpl;
import java.io.IOException;

public class Registry {
   private RegistryImpl registry;
   private static final Object chRegistrySemaphore = new Object();
   private static final Object chServerSemaphore = new Object();

   private Registry(RegistryImpl registryImpl) {
      this.registry = registryImpl;
   }

   public static Registry getRegistry(String classname) {
      if (classname == null) {
         throw new NullPointerException();
      } else {
         Registry instance = null;
         synchronized(chRegistrySemaphore) {
            instance = new Registry(RegistryImpl.getRegistryImpl(classname));
            return instance;
         }
      }
   }

   public static ContentHandlerServer getServer(String classname) throws ContentHandlerException {
      ContentHandlerServer server = null;
      if (classname == null) {
         throw new NullPointerException();
      } else {
         synchronized(chServerSemaphore) {
            server = RegistryImpl.getServerImpl(classname);
            return server;
         }
      }
   }

   public ContentHandlerServer register(String classname, String[] types, String[] suffixes, String[] actions, ActionNameMap[] actionnames, String ID, String[] accessAllowed) throws SecurityException, IllegalArgumentException, ClassNotFoundException, ContentHandlerException {
      return this.registry.register(classname, types, suffixes, actions, actionnames, ID, accessAllowed);
   }

   public boolean unregister(String classname) {
      return this.registry.unregister(classname);
   }

   public String[] getTypes() {
      return this.registry.getTypes();
   }

   public String[] getIDs() {
      return this.registry.getIDs();
   }

   public String[] getActions() {
      return this.registry.getActions();
   }

   public String[] getSuffixes() {
      return this.registry.getSuffixes();
   }

   public ContentHandler[] forType(String type) {
      return this.registry.forType(type);
   }

   public ContentHandler[] forAction(String action) {
      if (action == null) {
         throw new NullPointerException();
      } else {
         return this.registry.forAction(action);
      }
   }

   public ContentHandler[] forSuffix(String suffix) {
      if (suffix == null) {
         throw new NullPointerException();
      } else {
         return this.registry.forSuffix(suffix);
      }
   }

   public ContentHandler forID(String ID, boolean exact) {
      return this.registry.forID(ID, exact);
   }

   public ContentHandler[] findHandler(Invocation invocation) throws IOException, ContentHandlerException, SecurityException {
      return this.registry.findHandler(invocation.getImpl());
   }

   public boolean invoke(Invocation invocation, Invocation previous) throws IllegalArgumentException, IOException, ContentHandlerException, SecurityException {
      return previous == null ? this.registry.invoke(invocation.getImpl(), (InvocationImpl)null) : this.registry.invoke(invocation.getImpl(), previous.getImpl());
   }

   public boolean invoke(Invocation invocation) throws IllegalArgumentException, IOException, ContentHandlerException, SecurityException {
      return this.registry.invoke(invocation.getImpl());
   }

   public boolean reinvoke(Invocation invocation) throws IllegalArgumentException, IOException, ContentHandlerException, SecurityException {
      return this.registry.reinvoke(invocation.getImpl());
   }

   public Invocation getResponse(boolean wait) {
      return this.registry.getResponse(wait);
   }

   public void cancelGetResponse() {
      this.registry.cancelGetResponse();
   }

   public void setListener(ResponseListener listener) {
      this.registry.setListener(listener);
   }

   public String getID() {
      return this.registry.getID();
   }
}
