package javax.microedition.midlet;

import javax.microedition.io.ConnectionNotFoundException;

public abstract class MIDlet {
   private MIDletProxy state = new MIDletProxy(this);
   private static final Object platformRequestLock = new Object();

   protected MIDlet() {
   }

   protected abstract void startApp() throws MIDletStateChangeException;

   protected abstract void pauseApp();

   protected abstract void destroyApp(boolean var1) throws MIDletStateChangeException;

   public final void notifyDestroyed() {
      this.state.notifyDestroyed();
   }

   public final void notifyPaused() {
      this.state.notifyPaused();
   }

   public final String getAppProperty(String key) {
      if (key == null) {
         throw new NullPointerException("MIDlet.getAppProperty(null) called!");
      } else {
         return this.state.getAppProperty(key);
      }
   }

   public final void resumeRequest() {
      this.state.resumeRequest();
   }

   public final boolean platformRequest(String URL) throws ConnectionNotFoundException {
      synchronized(platformRequestLock) {
         boolean var10000;
         try {
            var10000 = platformRequest0(URL);
         } catch (ConnectionNotFoundException var5) {
            System.out.println("URL is : " + URL);
            var5.printStackTrace();
            throw var5;
         }

         return var10000;
      }
   }

   public final int checkPermission(String permission) {
      return checkPermission0(permission);
   }

   private static native int checkPermission0(String var0);

   private static native boolean platformRequest0(String var0) throws ConnectionNotFoundException;
}
