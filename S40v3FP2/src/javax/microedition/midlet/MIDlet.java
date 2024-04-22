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

   public final String getAppProperty(String var1) {
      if (var1 == null) {
         throw new NullPointerException("MIDlet.getAppProperty(null) called!");
      } else {
         return this.state.getAppProperty(var1);
      }
   }

   public final void resumeRequest() {
      this.state.resumeRequest();
   }

   public final boolean platformRequest(String var1) throws ConnectionNotFoundException {
      synchronized(platformRequestLock) {
         boolean var10000;
         try {
            var10000 = platformRequest0(var1);
         } catch (ConnectionNotFoundException var5) {
            System.out.println("URL is : " + var1);
            var5.printStackTrace();
            throw var5;
         }

         return var10000;
      }
   }

   public final int checkPermission(String var1) {
      return checkPermission0(var1);
   }

   private static native int checkPermission0(String var0);

   private static native boolean platformRequest0(String var0) throws ConnectionNotFoundException;
}
