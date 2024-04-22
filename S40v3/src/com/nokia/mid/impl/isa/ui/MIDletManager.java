package com.nokia.mid.impl.isa.ui;

import com.nokia.mid.pri.PriAccess;
import com.sun.midp.io.j2me.http.Protocol;
import java.util.Timer;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

final class MIDletManager implements EventConsumer, ExitManager, MIDletAccess, Runnable {
   static final int DEFAULT_DESTROY_TIMEOUT = 1500;
   private static final short MAX_TIMER_DEREGISTER_BEFORE_COMPACT = 10;
   private static final int MIN_TIMER_LIST_CAPACITY = 10;
   private static boolean mainCalled;
   private static MIDletManager s_mManager;
   private static Vector s_mInfoList;
   private static MIDletRTInfo s_mExplorerInfo;
   private static DisplayAccess displayAccessor;
   private static boolean s_isExitInProgress = false;
   private static boolean midletPushed = false;
   private static String activeMidletName;
   private MIDletRTInfo mInfoBeingConstructed;
   private Object mStateMutex = new Object();
   private MIDletState mRunning;
   private MIDletState mExplorer;
   private Vector mTimerList = new Vector(10);
   private short timersDeregisteredSinceLastCompact = 0;
   private boolean startingMidlet = false;
   private Thread startThread;
   private MIDletState startState;
   private static MIDlet currentMIDlet = null;

   public static void main(String[] var0) {
      if (!mainCalled) {
         mainCalled = true;
         s_mManager = new MIDletManager();
         int var1 = -1;
         if (var0 == null) {
            exitJALM();
         } else if (var0.length != 0) {
            if (var0[0] == null) {
               throw new RuntimeException("Application to lauch is null");
            }

            midletPushed = true;
            var1 = loadMIDletSuite(var0[0]);
         } else {
            loadMIDletSuite((String)null);
         }

         s_mManager.init(var1);
         s_mManager.schedule();
      } else {
         throw new SecurityException();
      }
   }

   private static int loadMIDletSuite(String var0) {
      int var1 = -1;
      s_loadMIDletExplorer();
      boolean var2 = false;
      s_mInfoList = new Vector();
      int var3 = 1;
      String var4 = System.getProperty("microedition.locale");

      while(true) {
         try {
            String var5 = "Nokia-MIDlet-" + var3 + "-" + var4;
            String var6 = s_getAppProperty(var5);
            var5 = "MIDlet-" + var3;
            String var7 = s_getAppProperty(var5);
            if (var6 != null) {
               var6 = var6 + var7.substring(var7.indexOf(","));
            }

            if (var7 == null) {
               if (s_mInfoList.size() == 0) {
                  throw new RuntimeException("No MIDlets in suite!");
               }

               if (var0 != null && !var2) {
                  throw new RuntimeException("Application to start not found");
               }

               return var1;
            }

            if (var0 != null && var0.equals(var7)) {
               var2 = true;
               var1 = var3 - 1;
            }

            if (var6 != null) {
               var7 = var6;
            }

            MIDletRTInfo var8 = new MIDletRTInfo(var7);
            s_mInfoList.addElement(var8);
         } catch (Exception var9) {
            s_abortOnError(var9);
         }

         ++var3;
      }
   }

   private static void s_loadMIDletExplorer() {
      try {
         Class var0 = Class.forName("com.nokia.mid.impl.isa.ui.MIDletExplorer");
         s_mExplorerInfo = new MIDletRTInfo(var0);
      } catch (Exception var1) {
         s_abortOnError(var1);
      }

   }

   private static boolean s_isSingleMIDlet() {
      return midletPushed || s_mInfoList.size() == 1 || isAutomatedTCK();
   }

   static MIDletManager s_getMIDletManager() {
      return s_mManager;
   }

   static Vector s_getMIDletInfoList() {
      return s_mInfoList;
   }

   static native String s_getAppProperty(String var0);

   static void s_setDisplayAccessor(DisplayAccess var0) {
      if (displayAccessor == null) {
         displayAccessor = var0;
      } else {
         throw new SecurityException();
      }
   }

   public DisplayAccess getDisplayAccessor() {
      return displayAccessor;
   }

   private static native void exitInternal();

   private static native boolean isAutomatedTCK();

   private static native void nativeNotifyExitBeginError(Throwable var0);

   private static native void nativeNotifyExitBeginNormal();

   static native void notifyStartOrExitMIDlet(boolean var0, int var1);

   private static void s_abortOnError(Throwable var0) {
      if (!s_isExitInProgress) {
         s_isExitInProgress = true;
         nativeNotifyExitBeginError(var0);
      }

      exitJALM();
   }

   private static void exitJALM() {
      EventDispatcher.s_destroyEventDispatcher();
      exitInternal();
   }

   private MIDletManager() {
      Protocol.s_setTimerDatabase(this);

      try {
         EventDispatcher var1 = EventDispatcher.s_createEventDispatcher(this.mStateMutex, this);
         var1.attachEventConsumer(3, this);
         var1.start();
      } catch (Throwable var2) {
         s_abortOnError(var2);
      }

      this.initializeMedia();
   }

   private void init(int var1) {
      if (s_isSingleMIDlet()) {
         MIDletRTInfo var2;
         if (var1 == -1) {
            var2 = (MIDletRTInfo)s_mInfoList.elementAt(0);
         } else {
            var2 = (MIDletRTInfo)s_mInfoList.elementAt(var1);
         }

         this.launchMIDlet(var2);
      } else {
         int var5 = 0;

         try {
            var5 = Integer.parseInt(System.getProperty("direct.midlet.launch"));
         } catch (Exception var4) {
         }

         if (var5 > 0 && var5 <= s_mInfoList.size()) {
            MIDletRTInfo var3 = (MIDletRTInfo)s_mInfoList.elementAt(var5 - 1);
            this.launchMIDlet(var3);
         } else {
            this.launchMIDletExplorer();
         }
      }

   }

   private void schedule() {
      while(true) {
         synchronized(this.mStateMutex) {
            try {
               if (this.mRunning != null) {
                  if (this.mRunning.getStateLifecycle() == 2) {
                     this.activateMIDlet(this.mRunning);
                  }

                  if (this.mRunning.getStateLifecycle() == 3) {
                     this.notifyDestroyed(this.mRunning);
                  }
               }

               this.mStateMutex.wait();
            } catch (Exception var4) {
               this.destroyMIDletSuite(var4);
            }
         }
      }
   }

   public final void consumeEvent(int var1, int var2, int var3) {
      if (var1 == 3) {
         switch(var2) {
         case 1:
            int var4 = 1500;
            if (var3 == 0) {
               exitJALM();
            }

            if (var3 > 0) {
               var4 = var3;
            }

            this.destroyMIDletSuite(var4);
         case 2:
         default:
            break;
         case 3:
            synchronized(this.mStateMutex) {
               this.pauseMIDlet();
               break;
            }
         case 4:
            synchronized(this.mStateMutex) {
               this.resumeMIDlet();
            }
         }
      }

   }

   private void shutdownHook() {
      try {
         Class var1 = Class.forName("net.sourceforge.cobertura.runtime.midp.MIDletManagerHook");
         ((Runnable)var1.newInstance()).run();
      } catch (Exception var2) {
      }

   }

   final void launchMIDlet(MIDletRTInfo var1) {
      if (PriAccess.getInt(5) == 1) {
         String var2 = var1.getCls().toString();
         byte var3 = 0;
         if (var2.startsWith("class ")) {
            var3 = 6;
         }

         this.setReferringMIDletURI0("midlet:" + var2.substring(var3));
      }

      if (!this.startingMidlet) {
         this.startingMidlet = true;
         activeMidletName = var1.getName();
         this.constructMIDlet(var1);
         this.startState = var1.getInstance();
         this.startThread = new Thread(this);
         this.startThread.start();
      }

   }

   public final void run() {
      if (this.startingMidlet) {
         this.activateMIDlet(this.startState);
         this.mRunning = this.startState;
         this.moveMIDletToForeground(this.startState);
         this.startingMidlet = false;
         this.startState = null;
         this.startThread = null;
      }

   }

   final void launchMIDletExplorer() {
      this.constructMIDletExplorer();
   }

   private void constructMIDlet(MIDletRTInfo var1) {
      try {
         synchronized(this.mStateMutex) {
            this.mInfoBeingConstructed = var1;
            notifyStartOrExitMIDlet(true, s_mInfoList.indexOf(var1));
            var1.constructMIDlet();
            this.mInfoBeingConstructed = null;
         }
      } catch (Exception var5) {
         this.startingMidlet = false;
         this.moveMIDletToDestroyed(var1, var5);
      }

   }

   private void constructMIDletExplorer() {
      try {
         synchronized(this.mStateMutex) {
            this.mInfoBeingConstructed = s_mExplorerInfo;
            s_mExplorerInfo.constructMIDlet();
            this.mExplorer = s_mExplorerInfo.getInstance();
            this.mInfoBeingConstructed = null;
         }
      } catch (Exception var4) {
         s_abortOnError(var4);
      }

   }

   private void activateMIDlet(MIDletState var1) {
      var1.setStateLifecycle(0);
      this.callStartApp(var1);
      currentMIDlet = var1.getMIDlet();
   }

   private void callStartApp(MIDletState var1) {
      try {
         var1.startApp();
      } catch (MIDletStateChangeException var3) {
         var1.setStateLifecycle(1);
      } catch (RuntimeException var4) {
         this.destroyMIDletSuite(var4);
      }

   }

   private void moveMIDletToForeground(MIDletState var1) {
      displayAccessor.setForeground(var1.getMIDlet());
   }

   private void moveMIDletExplorerToForeground() {
      if (this.mRunning != null) {
         MIDlet var1 = this.mExplorer.getMIDlet();
         displayAccessor = displayAccessor.replaceDisplay(var1);
         ((MIDletExplorer)var1).displayMIDletList();
         this.mRunning = null;
      }

   }

   public final void abortOnError(Throwable var1) {
      s_abortOnError(var1);
   }

   public final void exitOnError(Throwable var1) {
      this.destroyMIDletSuite(var1);
   }

   final void selectEndMIDletSuite() {
      this.notifyExitBeginNormal();
      this.destroyMIDletSuite(1500);
   }

   private void notifyExitBeginNormal() {
      if (!s_isExitInProgress) {
         s_isExitInProgress = true;
         nativeNotifyExitBeginNormal();
      }

   }

   private void notifyExitBeginError(Throwable var1) {
      if (!s_isExitInProgress) {
         s_isExitInProgress = true;
         nativeNotifyExitBeginError(var1);
      }

   }

   private final void destroyMIDletSuite(Throwable var1) {
      this.notifyExitBeginError(var1);
      this.destroyMIDletSuite(1500);
   }

   private final void destroyMIDletSuite(int var1) {
      if (this.mRunning != null) {
         try {
            this.callDestroyApp(this.mRunning, var1);
         } catch (MIDletDestroyFailedException var3) {
         }

         this.setMIDletToDestroyedAndRemoveInstance(this.mRunning.getMIDletInfo());
         this.mRunning = null;
      }

      exitJALM();
   }

   public final void destroyMIDlet(MIDlet var1) {
      MIDletState var2 = this.getMIDletState(var1);

      try {
         currentMIDlet = null;
         this.callDestroyApp(var2, 1500);
         this.moveMIDletToDestroyed(var2.getMIDletInfo());
      } catch (MIDletDestroyFailedException var4) {
         this.moveMIDletToDestroyed(var2.getMIDletInfo(), var4);
      }

   }

   private void callDestroyApp(MIDletState var1, int var2) throws MIDletDestroyFailedException {
      try {
         currentMIDlet = null;
         if (PriAccess.getInt(5) == 1) {
            this.destroyExitURI0((String)null);
         }

         var1.destroyApp(true);
         this.shutdownHook();
      } catch (MIDletStateChangeException var4) {
         throw new MIDletDestroyFailedException(var4);
      } catch (RuntimeException var5) {
         throw new MIDletDestroyFailedException(var5);
      }
   }

   private void moveMIDletToDestroyed(MIDletRTInfo var1) {
      int var2 = 0;

      try {
         var2 = Integer.parseInt(System.getProperty("direct.midlet.launch"));
      } catch (Exception var4) {
      }

      if (!s_isSingleMIDlet() && var2 <= 0) {
         this.moveMIDletExplorerToForeground();
      } else {
         this.initDisplay(var1.getInstance());
      }

      this.setMIDletToDestroyedAndRemoveInstance(var1);
      if (PriAccess.getInt(5) == 1 && this.isExitURISet0() && !s_isSingleMIDlet() && var2 == 0) {
         this.selectEndMIDletSuite();
      }

      if (s_isSingleMIDlet() || var2 > 0) {
         exitJALM();
      }

   }

   private void moveMIDletToDestroyed(MIDletRTInfo var1, Exception var2) {
      this.notifyExitBeginError(var2);
      this.setMIDletToDestroyedAndRemoveInstance(var1);
      exitJALM();
   }

   private void setMIDletToDestroyedAndRemoveInstance(MIDletRTInfo var1) {
      MIDletState var2 = var1.getInstance();
      if (var2 != null) {
         if (var2.getStateLifecycle() != 3) {
            var2.setStateLifecycle(3);
         }

         this.cancelTimers();
         var1.removeInstance();
         if (!s_isExitInProgress) {
            notifyStartOrExitMIDlet(false, s_mInfoList.indexOf(var1));
         }
      }

   }

   private final void cancelTimers() {
      for(int var1 = this.mTimerList.size() - 1; var1 >= 0; --var1) {
         ((Timer)this.mTimerList.elementAt(var1)).cancel();
      }

      this.mTimerList.removeAllElements();
   }

   private void notifyDestroyed(MIDletState var1) {
      int var2 = 0;

      try {
         var2 = Integer.parseInt(System.getProperty("direct.midlet.launch"));
      } catch (Exception var4) {
      }

      if (s_isSingleMIDlet() || var2 > 0) {
         this.notifyExitBeginNormal();
      }

      this.shutdownHook();
      this.moveMIDletToDestroyed(var1.getMIDletInfo());
   }

   final Object getStateMutex() {
      return this.mStateMutex;
   }

   final MIDletRTInfo registerMIDlet(MIDletState var1) {
      synchronized(this.mStateMutex) {
         if (this.mInfoBeingConstructed != null) {
            Class var3 = var1.getMIDlet().getClass();
            if (var3.equals(this.mInfoBeingConstructed.getCls()) && this.mInfoBeingConstructed.getInstance() == null) {
               this.mInfoBeingConstructed.setInstance(var1);
               return this.mInfoBeingConstructed;
            }
         }

         throw new SecurityException("MIDletManager ERROR: Illegal attempt to construct " + (var1 == null ? "NULL" : var1.getMIDlet().toString()));
      }
   }

   final void initDisplay(MIDletState var1) {
      MIDlet var2 = var1.getMIDlet();
      if (displayAccessor == null) {
         Display.getDisplay(var2);
      }

      displayAccessor.resetDisplay(var2);
   }

   private MIDletState getMIDletState(MIDlet var1) {
      if (var1 != null) {
         MIDletState var2 = s_mExplorerInfo.getInstance();
         if (var2 != null && var1 == var2.getMIDlet()) {
            return var2;
         } else {
            for(int var3 = s_mInfoList.size() - 1; var3 >= 0; --var3) {
               MIDletRTInfo var4 = (MIDletRTInfo)s_mInfoList.elementAt(var3);
               var2 = var4.getInstance();
               if (var2 != null && var1 == var2.getMIDlet()) {
                  return var2;
               }
            }

            throw new IllegalArgumentException("MIDletManager ERROR in getMIDletState(): " + var1 + " not found!");
         }
      } else {
         throw new IllegalArgumentException("MIDletManager ERROR: getMIDletState(null) called!");
      }
   }

   public final String getMIDletName(MIDlet var1) {
      String var2 = this.getMIDletState(var1).getMIDletName();
      if (var2 != null && !var2.equals("")) {
         return var2;
      } else {
         var2 = s_getAppProperty("Nokia-MIDlet-Name-" + System.getProperty("microedition.locale"));
         if (var2 == null) {
            var2 = s_getAppProperty("MIDlet-Name");
         }

         return var2 != null ? var2 : "";
      }
   }

   public final void registerTimer(MIDlet var1, Timer var2) {
      if (!this.mTimerList.contains(var2)) {
         this.mTimerList.addElement(var2);
      }

   }

   public final void deregisterTimer(MIDlet var1, Timer var2) {
      if (this.mTimerList.contains(var2)) {
         this.mTimerList.removeElement(var2);
         if (++this.timersDeregisteredSinceLastCompact > 10) {
            this.timersDeregisteredSinceLastCompact = 0;
            if (this.mTimerList.capacity() > 10) {
               this.mTimerList.trimToSize();
               this.mTimerList.ensureCapacity(10);
            }
         }
      }

   }

   private void initializeMedia() {
      if (PriAccess.getInt(5) == 2) {
         try {
            Class.forName("com.reliance.media.impl.RAPSystemPlayer");
         } catch (ClassNotFoundException var2) {
         }
      }

   }

   private void pauseMIDlet() {
      if (this.mRunning != null) {
         this.mRunning.pauseApp();
         this.mRunning.notifyPaused();
      }

   }

   private void resumeMIDlet() {
      if (this.mRunning != null) {
         this.mRunning.resumeRequest();
      }

   }

   static MIDlet getCurrentMIDlet() {
      return currentMIDlet;
   }

   private native void destroyExitURI0(String var1);

   private native void setReferringMIDletURI0(String var1);

   private native boolean isExitURISet0();

   public void exit() {
      this.selectEndMIDletSuite();
   }
}
