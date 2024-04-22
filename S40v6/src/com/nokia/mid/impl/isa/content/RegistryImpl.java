package com.nokia.mid.impl.isa.content;

import com.nokia.mid.impl.isa.util.UrlParser;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.content.ActionNameMap;
import javax.microedition.content.ContentHandler;
import javax.microedition.content.ContentHandlerException;
import javax.microedition.content.ContentHandlerServer;
import javax.microedition.content.Invocation;
import javax.microedition.content.ResponseListener;

public final class RegistryImpl {
   private static final int CRITERIA_ID = 0;
   private static final int CRITERIA_SUFFIX = 2;
   private static final int CRITERIA_TYPE = 3;
   private static final int CRITERIA_ACTION = 4;
   private static final int CRITERIA_ID_SUFFIX = 1;
   private String chapiID;
   private String classname;
   private Object waitResponseLock = new Object();
   private static Object regLock = new Object();

   private RegistryImpl(String classname, String chapiID) {
      this.classname = classname;
      this.chapiID = chapiID;
   }

   public void cancelGetResponse() {
      synchronized(this.waitResponseLock) {
         this.waitResponseLock.notifyAll();
      }
   }

   protected static native String nGetContentHandlerId(String var0);

   protected static native String nGetContentHandlerServerId(String var0);

   protected static native ContentHandler[] nJCDSForMethods(String var0, String var1, int var2, boolean var3);

   private String[] nGetGenericMethod(int criteria) {
      int i = false;
      Enumeration e = null;
      String[] matchedResults = null;
      ContentHandler[] chArray = nJCDSForMethods(this.chapiID, (String)null, 1, false);
      Vector v = new Vector();

      for(int i = 0; i < chArray.length; ++i) {
         ContentHandlerImpl chi = (ContentHandlerImpl)chArray[i];
         switch(criteria) {
         case 1:
            this.injectElements(v, chi.getID(), false, false);
            break;
         case 2:
            this.injectElements(v, chi.getSuffixes(), true, true);
            break;
         case 3:
            this.injectElements(v, chi.getTypes(), true, true);
            break;
         case 4:
            this.injectElements(v, chi.getActions(), false, false);
         }
      }

      matchedResults = this.createStringArrayFromVector(v);
      return matchedResults;
   }

   private String[] createStringArrayFromVector(Vector v) {
      String[] matchedResults = new String[v.size()];
      int i = 0;

      for(Enumeration e = v.elements(); e.hasMoreElements(); matchedResults[i++] = (String)e.nextElement()) {
      }

      return matchedResults;
   }

   private void injectElements(Vector v, String value, boolean caseInsensitive, boolean trimElements) {
      if (trimElements) {
         value = value.trim();
      }

      boolean found = false;
      int i = 0;

      while(i < v.size()) {
         label28: {
            String element = (String)v.elementAt(i);
            if (caseInsensitive) {
               if (value.equalsIgnoreCase(element)) {
                  break label28;
               }
            } else if (value.equals(element)) {
               break label28;
            }

            ++i;
            continue;
         }

         found = true;
         break;
      }

      if (!found) {
         v.addElement(value);
      }

   }

   private void injectElements(Vector v, String[] array, boolean ignoreCase, boolean trimElements) {
      for(int i = 0; i < array.length; ++i) {
         this.injectElements(v, array[i], ignoreCase, trimElements);
      }

   }

   public static RegistryImpl getRegistryImpl(String classname) {
      if (classname.equals("")) {
         throw new IllegalArgumentException("Invalid class name");
      } else if (testChapiMIDPClass(classname)) {
         throw new IllegalArgumentException("Class name either does not extend MIDlet or is not placed on the current midlet suite");
      } else {
         String midletIdAttribute = nGetContentHandlerId(classname);
         if (midletIdAttribute.equals("")) {
            throw new IllegalArgumentException();
         } else {
            return new RegistryImpl(classname, midletIdAttribute);
         }
      }
   }

   private static boolean testChapiMIDPClass(String classname) {
      boolean appLifecycleProblems = false;

      try {
         if (!Util.isMIDPLifecycleCompliant(classname)) {
            appLifecycleProblems = true;
         }
      } catch (ClassNotFoundException var3) {
         appLifecycleProblems = true;
      }

      return appLifecycleProblems;
   }

   public static ContentHandlerServer getServerImpl(String classname) throws ContentHandlerException {
      if (testChapiMIDPClass(classname)) {
         throw new ContentHandlerException("Class name either does not extend MIDlet or is not placed on the current midlet suite", 1);
      } else {
         String chId = nGetContentHandlerServerId(classname);
         if (chId.equals("")) {
            throw new ContentHandlerException("Class name not registered as a content handler in the current application ", 1);
         } else {
            return getContentHandlerServerImpl(chId);
         }
      }
   }

   public ContentHandler[] findHandler(InvocationImpl invocation) throws IOException, ContentHandlerException, SecurityException {
      String invid = null;
      String invtype = null;
      String invurl = null;
      String invaction = null;
      ContentHandler[] charr = null;
      if (invocation == null) {
         throw new NullPointerException("invocation null");
      } else if (invocation.getID() == null && invocation.getType() == null && invocation.getURL() == null && invocation.getAction() == null) {
         throw new IllegalArgumentException();
      } else {
         invid = invocation.getID();
         if (invid != null) {
            ContentHandler ch = this.forID(invid, false);
            if (ch == null) {
               throw new ContentHandlerException("No Content Handler Found", 1);
            } else {
               return new ContentHandler[]{ch};
            }
         } else {
            invtype = invocation.getType();
            invurl = invocation.getURL();
            if (invtype == null && invurl != null) {
               try {
                  invtype = invocation.findType();
               } catch (ContentHandlerException var10) {
               }
            }

            if (invtype != null) {
               charr = this.forType(invtype);
               if (charr.length == 0) {
                  throw new ContentHandlerException("No ContentHandler Found", 1);
               } else {
                  return this.filterContentHandlers(charr, invocation.getAction());
               }
            } else if (invurl != null) {
               String filePath = UrlParser.getUriComponents(invurl)[2];
               int index = filePath.lastIndexOf(46);
               if (index != -1) {
                  String suffix = filePath.substring(index);
                  charr = this.forSuffix(suffix);
                  if (charr.length > 0) {
                     return this.filterContentHandlers(charr, invocation.getAction());
                  }
               }

               throw new ContentHandlerException("No ContentHandler Found", 1);
            } else {
               invaction = invocation.getAction();
               if (invaction != null) {
                  charr = this.forAction(invaction);
               } else {
                  charr = this.forGenericMethod("*.*", 1);
               }

               if (charr != null && charr.length > 0) {
                  return this.filterContentHandlers(charr, invocation.getAction());
               } else {
                  throw new ContentHandlerException("No ContentHandler Found", 1);
               }
            }
         }
      }
   }

   private ContentHandler[] filterContentHandlers(ContentHandler[] handlers, String action) throws ContentHandlerException {
      if (action == null) {
         return handlers;
      } else {
         int j = 0;

         for(int i = 0; i < handlers.length; ++i) {
            if (handlers[i].hasAction(action)) {
               ++j;
            }
         }

         if (j > 0) {
            ContentHandler[] result = new ContentHandler[j];
            j = 0;

            for(int i = 0; i < handlers.length; ++i) {
               if (handlers[i].hasAction(action)) {
                  result[j++] = handlers[i];
               }
            }

            return result;
         } else {
            throw new ContentHandlerException("No ContentHandler Found", 1);
         }
      }
   }

   private ContentHandler[] forGenericMethod(String filter, int criteria) {
      if (filter == null) {
         throw new NullPointerException();
      } else if (filter.trim().length() == 0) {
         return new ContentHandler[0];
      } else {
         ContentHandler[] array = nJCDSForMethods(this.chapiID, filter, criteria, false);
         return array;
      }
   }

   private static ContentHandlerServerImpl getContentHandlerServerImpl(String chapiId) {
      ContentHandler[] chs = null;
      chs = nJCDSForMethods(chapiId, chapiId, 0, true);
      if (chs.length > 1) {
         throw new RuntimeException("The given CH id:" + chapiId + " was registered for more than one Content Handler");
      } else if (chs.length == 0) {
         throw new RuntimeException("The given ID, does not exist at JCDS: " + chapiId);
      } else {
         ((ContentHandlerServerImpl)chs[0]).defaultConstructor();
         return (ContentHandlerServerImpl)chs[0];
      }
   }

   public ContentHandler[] forAction(String action) {
      return this.forGenericMethod(action, 4);
   }

   public ContentHandler forID(String ID, boolean exactMatch) {
      ContentHandler[] chArray = exactMatch ? this.forGenericMethod(ID, 0) : this.forGenericMethod(ID, 1);
      return chArray.length > 0 ? chArray[0] : null;
   }

   public ContentHandler[] forSuffix(String suffix) {
      return this.forGenericMethod(suffix, 2);
   }

   public ContentHandler[] forType(String type) {
      return this.forGenericMethod(type, 3);
   }

   public String[] getActions() {
      return this.nGetGenericMethod(4);
   }

   public String getID() {
      return this.chapiID;
   }

   String getClassName() {
      return this.classname;
   }

   public String[] getIDs() {
      return this.nGetGenericMethod(1);
   }

   public Invocation getResponse(boolean wait) {
      if (!wait) {
         return CHAPIQueueManager.getInstance().getInvocationResponse(this.getClassName());
      } else {
         while(true) {
            try {
               synchronized(this.waitResponseLock) {
                  Invocation inv = CHAPIQueueManager.getInstance().getInvocationResponse(this.getClassName());
                  if (inv == null) {
                     this.waitResponseLock.wait();
                     return CHAPIQueueManager.getInstance().getInvocationResponse(this.getClassName());
                  }

                  return inv;
               }
            } catch (InterruptedException var6) {
            }
         }
      }
   }

   public String[] getSuffixes() {
      return this.nGetGenericMethod(2);
   }

   public String[] getTypes() {
      return this.nGetGenericMethod(3);
   }

   public boolean invoke(InvocationImpl invocation, InvocationImpl previous) throws IllegalArgumentException, IOException, ContentHandlerException, SecurityException {
      checkInvocation(invocation, 1);
      if (previous != null) {
         if (previous.getStatus() != 2) {
            throw new IllegalStateException();
         }

         if (!invocation.getResponseRequired()) {
            throw new IllegalArgumentException();
         }
      }

      ContentHandlerImpl handler = null;
      invocation.checkHttpCacheMode();
      ContentHandler[] handlers = this.findHandler(invocation);
      handler = (ContentHandlerImpl)handlers[0];
      invocation.setID(handler.getID());
      invocation.setInvokingID(this.getID());
      invocation.setStatus(3);
      if (handler.isNativeHandler()) {
         invocation.cacheInvocationContents();
         invocation.setToNative(true);
      }

      invocation.shutdownHttpCacheMode();
      if (previous != null) {
         previous.setStatus(4);

         try {
            previous.open(true).close();
         } catch (Exception var8) {
         }

         invocation.setPrevious(previous);
         InvocationStore.getInstance().storeInvocation(previous.getUniqueID(), previous.getParent());
         previous.storeToNative();
      }

      Invocation activeCopy = new Invocation();
      InvocationImpl activeCopyImpl = Util.getInvocationAccessor().getInvocationImpl(activeCopy);
      activeCopyImpl.copyFrom(invocation);
      activeCopyImpl.setInvokingApplicationClassName(this.classname);
      activeCopyImpl.setServerDetails(handler.getClassName(), (int)handler.getMIDletId());
      activeCopyImpl.setAppName(Util.getAppName(this.classname));
      activeCopyImpl.setInvokingAuthority(Util.getAuthority());
      boolean retVal = activeCopyImpl.storeToNative();
      if (invocation.getResponseRequired()) {
         InvocationStore.getInstance().storeInvocation(activeCopyImpl.getUniqueID(), invocation.getParent());
      }

      return retVal;
   }

   public boolean invoke(InvocationImpl invocation) throws IllegalArgumentException, IOException, ContentHandlerException, SecurityException {
      return this.invoke(invocation, (InvocationImpl)null);
   }

   public ContentHandlerServer register(String cn, String[] types, String[] suffixes, String[] actions, ActionNameMap[] actionnames, String ID, String[] accessAllowed) throws SecurityException, IllegalArgumentException, ClassNotFoundException, ContentHandlerException {
      String chid = null;
      String[] locales = null;
      synchronized(regLock) {
         if (!Util.checkChapiSecurity()) {
            throw new SecurityException("javax.microedition.content.ContentHandler not allowed");
         }
      }

      if (cn == null) {
         throw new NullPointerException("Registration does not accept a null class name");
      } else if (this.hasNullElements(types, suffixes, actions, accessAllowed)) {
         throw new NullPointerException("The supplied arrays contain null elements");
      } else {
         if (actionnames != null && actionnames.length > 0) {
            locales = new String[actionnames.length];

            for(int l = 0; l < locales.length; ++l) {
               locales[l] = actionnames[l].getLocale();
            }
         }

         if (!this.hasValidElements(types, suffixes, actions, accessAllowed, locales)) {
            throw new IllegalArgumentException("The supplied arrays contain invalid elements");
         } else if (Util.isInvalidID(ID)) {
            throw new IllegalArgumentException("CHAPI-ID has invalid characters");
         } else if (!this.matchActionNameMap(actions, actionnames)) {
            throw new IllegalArgumentException("ActionNameMaps doesn't match with the given actions");
         } else if (!Util.isMIDPLifecycleCompliant(cn)) {
            throw new IllegalArgumentException("Class name is either not compliant with MIDP spec or couldn't be located on this MIDlet suite");
         } else {
            types = this.removeArrayRepetitions(types, true, true);
            suffixes = this.removeArrayRepetitions(suffixes, true, true);
            actions = this.removeArrayRepetitions(actions, false, false);
            synchronized(regLock) {
               try {
                  chid = nRegisterContentHandler(cn, types, suffixes, actions, actionnames, ID, accessAllowed);
               } catch (ContentHandlerException var13) {
                  throw new ContentHandlerException("Ambiguous or conflicting CH ID", 3);
               }

               ContentHandlerServerImpl chs = getContentHandlerServerImpl(chid);
               if (chs.getClassName().equals(this.classname)) {
                  this.chapiID = chs.getID();
               }

               return chs;
            }
         }
      }
   }

   private String[] removeArrayRepetitions(String[] array, boolean caseInsensitive, boolean trimStringElements) {
      if (array == null) {
         return null;
      } else {
         Vector vTempElements = new Vector();
         this.injectElements(vTempElements, array, caseInsensitive, trimStringElements);
         array = this.createStringArrayFromVector(vTempElements);
         return array;
      }
   }

   protected static native String nRegisterContentHandler(String var0, String[] var1, String[] var2, String[] var3, ActionNameMap[] var4, String var5, String[] var6) throws ContentHandlerException;

   private boolean matchActionNameMap(String[] actions, ActionNameMap[] actionnames) {
      if (actions == null) {
         return actionnames == null || actionnames.length == 0;
      } else if (actionnames == null) {
         return true;
      } else {
         for(int i = 0; i < actionnames.length; ++i) {
            if (actions.length != actionnames[i].size()) {
               return false;
            }

            for(int j = 0; j < actions.length; ++j) {
               if (!actions[j].equals(actionnames[i].getAction(j))) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private boolean hasNullElements(String[] types, String[] suffixes, String[] actions, String[] accessAllowed) {
      return Util.hasNullElements(types) || Util.hasNullElements(suffixes) || Util.hasNullElements(actions) || Util.hasNullElements(accessAllowed);
   }

   private boolean hasValidElements(String[] types, String[] suffixes, String[] actions, String[] accessAllowed, String[] actlocales) {
      return Util.hasValidArrayElements(types, false) && Util.hasValidArrayElements(suffixes, false) && Util.hasValidArrayElements(actions, false) && Util.hasValidArrayElements(accessAllowed, false) && Util.hasValidArrayElements(actlocales, true);
   }

   public void setListener(ResponseListener listener) {
      CHAPIConsumer.getInstance().setResponseListener(listener, this);
   }

   public boolean unregister(String cn) {
      boolean unregistrationPerformed = false;
      if (cn == null) {
         throw new NullPointerException("null");
      } else {
         synchronized(regLock) {
            unregistrationPerformed = nUnregisterContentHandler(cn);
            if (unregistrationPerformed && cn.equals(this.classname)) {
               this.chapiID = nGetContentHandlerId(this.classname);
            }

            return unregistrationPerformed;
         }
      }
   }

   private static native boolean nUnregisterContentHandler(String var0);

   public boolean reinvoke(InvocationImpl invocation) throws IOException, ContentHandlerException {
      checkInvocation(invocation, 2);
      ContentHandlerImpl handler = null;
      invocation.checkHttpCacheMode();
      ContentHandler[] handlers = this.findHandler(invocation);
      handler = (ContentHandlerImpl)handlers[0];
      invocation.setID(handler.getID());
      if (handler.isNativeHandler()) {
         invocation.cacheInvocationContents();
         invocation.setToNative(true);
      }

      invocation.shutdownHttpCacheMode();
      Invocation activeCopy = new Invocation();
      InvocationImpl activeCopyImpl = Util.getInvocationAccessor().getInvocationImpl(activeCopy);
      activeCopyImpl.copyFrom(invocation);
      activeCopyImpl.setStatus(3);
      activeCopyImpl.setServerDetails(handler.getClassName(), (int)handler.getMIDletId());
      invocation.setStatus(5);
      return activeCopyImpl.storeToNative();
   }

   void unblockGetResponse() {
      synchronized(this.waitResponseLock) {
         this.waitResponseLock.notify();
      }
   }

   static void checkInvocation(InvocationImpl invocation, int requiredStatus) {
      boolean argsOK = true;
      if (invocation == null) {
         throw new NullPointerException();
      } else {
         String[] args = invocation.getArgs();

         for(int i = 0; i < args.length; ++i) {
            if (args[i] == null) {
               argsOK = false;
               break;
            }
         }

         if (invocation.getID() == null && invocation.getType() == null && invocation.getURL() == null && invocation.getAction() == null || !argsOK || !invocation.getResponseRequired() && invocation.getPrevious() != null) {
            throw new IllegalArgumentException();
         } else if (invocation.getStatus() != requiredStatus) {
            throw new IllegalStateException();
         }
      }
   }
}
