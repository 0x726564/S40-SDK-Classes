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
   private static DisplayAccess displayAccessor;
   private static boolean s_isExitInProgress = false;
   private static boolean midletPushed = false;
   private static int notChapi = 1;
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

   public static void main(String[] args) {
      if (!mainCalled) {
         mainCalled = true;
         s_mManager = new MIDletManager();
         int midletToLaunchIdx = -1;
         if (args == null) {
            exitJALM();
         } else if (args.length != 0) {
            if (args[0] == null) {
               throw new RuntimeException("Application to launch is null");
            }

            midletPushed = true;
            midletToLaunchIdx = loadMIDlet(args[0]);
         } else {
            loadMIDlet((String)null);
         }

         s_mManager.init(midletToLaunchIdx);
         s_mManager.schedule();
      } else {
         throw new SecurityException();
      }
   }

   private static int loadMIDlet(String midletToStartPropStr) {
      int midletToStartIdx = -1;
      boolean midletToStartMatched = false;
      boolean exit = false;
      s_mInfoList = new Vector();
      int _midletCount = 1;
      String locale = System.getProperty("microedition.locale");
      String key;
      String appProperties;
      if (midletToStartPropStr != null) {
         for(; !exit; ++_midletCount) {
            try {
               key = "MIDlet-" + _midletCount;
               appProperties = s_getAppProperty(key, 0);
               if (appProperties == null) {
                  int midletToStartIdx = 0;
                  MIDletRTInfo mInfo = new MIDletRTInfo(midletToStartPropStr);
                  s_mInfoList.addElement(mInfo);
                  notChapi = 0;
                  return midletToStartIdx;
               }

               if (midletToStartPropStr.equals(appProperties)) {
                  exit = true;
               }
            } catch (Exception var11) {
               s_abortOnError(var11);
            }
         }
      }

      _midletCount = 1;

      while(true) {
         try {
            key = "Nokia-MIDlet-" + _midletCount + "-" + locale;
            String localisedAppProperties = s_getAppProperty(key, 0);
            key = "MIDlet-" + _midletCount;
            appProperties = s_getAppProperty(key, 0);
            if (localisedAppProperties != null) {
               localisedAppProperties = localisedAppProperties + appProperties.substring(appProperties.indexOf(","));
            }

            if (appProperties == null) {
               if (s_mInfoList.size() == 0) {
                  throw new RuntimeException("No MIDlets in suite!");
               }

               if (midletToStartPropStr != null && !midletToStartMatched) {
                  throw new RuntimeException("Application to start not found");
               }

               return midletToStartIdx;
            }

            if (midletToStartPropStr != null && midletToStartPropStr.equals(appProperties)) {
               midletToStartMatched = true;
               midletToStartIdx = _midletCount - 1;
            }

            if (localisedAppProperties != null) {
               appProperties = localisedAppProperties;
            }

            MIDletRTInfo mInfo = new MIDletRTInfo(appProperties);
            s_mInfoList.addElement(mInfo);
         } catch (Exception var10) {
            s_abortOnError(var10);
         }

         ++_midletCount;
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

   static native String s_getAppProperty(String var0, int var1);

   static void s_setDisplayAccessor(DisplayAccess da) {
      if (displayAccessor == null) {
         displayAccessor = da;
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

   private static void s_abortOnError(Throwable t) {
      if (!s_isExitInProgress) {
         s_isExitInProgress = true;
         nativeNotifyExitBeginError(t);
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
         EventDispatcher eventDispatcher = EventDispatcher.s_createEventDispatcher(this.mStateMutex, this);
         eventDispatcher.attachEventConsumer(3, this);
         eventDispatcher.start();
      } catch (Throwable var2) {
         s_abortOnError(var2);
      }

      this.initializeMedia();
   }

   private void init(int midletToStartIdx) {
      if (s_isSingleMIDlet()) {
         MIDletRTInfo _mInfo;
         if (midletToStartIdx == -1) {
            _mInfo = (MIDletRTInfo)s_mInfoList.elementAt(0);
         } else {
            _mInfo = (MIDletRTInfo)s_mInfoList.elementAt(midletToStartIdx);
         }

         this.launchMIDlet(_mInfo);
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
               this.destroyMIDlet((Throwable)var4);
            }
         }
      }
   }

   public final void consumeEvent(int category, int type, int param) {
      if (category == 3) {
         switch(type) {
         case 1:
            int _time_out = 1500;
            if (param == 0) {
               exitJALM();
            }

            if (param > 0) {
               _time_out = param;
            }

            this.destroyMIDlet(_time_out);
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
         Class clazz = Class.forName("net.sourceforge.cobertura.runtime.midp.MIDletManagerHook");
         ((Runnable)clazz.newInstance()).run();
      } catch (Exception var2) {
      }

   }

   final void launchMIDlet(MIDletRTInfo mInfo) {
      if (PriAccess.getInt(5) == 1) {
         String midletClass = mInfo.getCls().toString();
         int classIndex = 0;
         if (midletClass.startsWith("class ")) {
            classIndex = 6;
         }

         this.setReferringMIDletURI0("midlet:" + midletClass.substring(classIndex));
      }

      if (!this.startingMidlet) {
         this.startingMidlet = true;
         activeMidletName = mInfo.getName();
         this.constructMIDlet(mInfo);
         this.startState = mInfo.getInstance();
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

   private void constructMIDlet(MIDletRTInfo mInfo) {
      try {
         synchronized(this.mStateMutex) {
            this.mInfoBeingConstructed = mInfo;
            mInfo.constructMIDlet();
            this.mInfoBeingConstructed = null;
         }
      } catch (Exception var5) {
         this.startingMidlet = false;
         this.moveMIDletToDestroyed(mInfo, var5);
      }

   }

   private void activateMIDlet(MIDletState ms) {
      ms.setStateLifecycle(0);
      this.callStartApp(ms);
      currentMIDlet = ms.getMIDlet();
   }

   private void callStartApp(MIDletState ms) {
      try {
         ms.startApp();
      } catch (MIDletStateChangeException var3) {
         ms.setStateLifecycle(1);
      } catch (RuntimeException var4) {
         this.destroyMIDlet((Throwable)var4);
      }

   }

   private void moveMIDletToForeground(MIDletState ms) {
      displayAccessor.setForeground(ms.getMIDlet());
   }

   public final void abortOnError(Throwable t) {
      s_abortOnError(t);
   }

   public final void exitOnError(Throwable t) {
      this.destroyMIDlet(t);
   }

   final void selectEndMIDlet() {
      this.notifyExitBeginNormal();
      this.destroyMIDlet(1500);
   }

   private void notifyExitBeginNormal() {
      if (!s_isExitInProgress) {
         s_isExitInProgress = true;
         nativeNotifyExitBeginNormal();
      }

   }

   private void notifyExitBeginError(Throwable t) {
      if (!s_isExitInProgress) {
         s_isExitInProgress = true;
         nativeNotifyExitBeginError(t);
      }

   }

   private final void destroyMIDlet(Throwable t) {
      this.notifyExitBeginError(t);
      this.destroyMIDlet(1500);
   }

   private final void destroyMIDlet(int time_out) {
      if (this.mRunning != null) {
         try {
            this.callDestroyApp(this.mRunning, time_out);
         } catch (MIDletDestroyFailedException var3) {
         }

         this.setMIDletToDestroyedAndRemoveInstance(this.mRunning.getMIDletInfo());
         this.mRunning = null;
      }

      exitJALM();
   }

   public final void destroyMIDlet(MIDlet m) {
      MIDletState ms = this.getMIDletState(m);

      try {
         this.callDestroyApp(ms, 1500);
         this.moveMIDletToDestroyed(ms.getMIDletInfo());
      } catch (MIDletDestroyFailedException var4) {
         this.moveMIDletToDestroyed(ms.getMIDletInfo(), var4);
      }

   }

   private void callDestroyApp(MIDletState ms, int time_out) throws MIDletDestroyFailedException {
      try {
         if (PriAccess.getInt(5) == 1) {
            this.destroyExitURI0((String)null);
         }

         ms.destroyApp(true);
         this.shutdownHook();
      } catch (MIDletStateChangeException var4) {
         throw new MIDletDestroyFailedException(var4);
      } catch (RuntimeException var5) {
         throw new MIDletDestroyFailedException(var5);
      }
   }

   private void moveMIDletToDestroyed(MIDletRTInfo mInfo) {
      int midletNumber = 0;

      try {
         midletNumber = Integer.parseInt(System.getProperty("direct.midlet.launch"));
      } catch (Exception var4) {
      }

      this.initDisplay(mInfo.getInstance());
      this.setMIDletToDestroyedAndRemoveInstance(mInfo);
      if (s_isSingleMIDlet() || midletNumber > 0) {
         exitJALM();
      }

   }

   private void moveMIDletToDestroyed(MIDletRTInfo mInfo, Exception x) {
      this.notifyExitBeginError(x);
      this.setMIDletToDestroyedAndRemoveInstance(mInfo);
      exitJALM();
   }

   private void setMIDletToDestroyedAndRemoveInstance(MIDletRTInfo mInfo) {
      MIDletState _ms = mInfo.getInstance();
      if (_ms != null) {
         if (_ms.getStateLifecycle() != 3) {
            _ms.setStateLifecycle(3);
         }

         this.cancelTimers();
         mInfo.removeInstance();
      }

   }

   private final void cancelTimers() {
      for(int idx = this.mTimerList.size() - 1; idx >= 0; --idx) {
         ((Timer)this.mTimerList.elementAt(idx)).cancel();
      }

      this.mTimerList.removeAllElements();
   }

   private void notifyDestroyed(MIDletState ms) {
      int midletNumber = 0;

      try {
         midletNumber = Integer.parseInt(System.getProperty("direct.midlet.launch"));
      } catch (Exception var4) {
      }

      if (s_isSingleMIDlet() || midletNumber > 0) {
         this.notifyExitBeginNormal();
      }

      this.shutdownHook();
      this.moveMIDletToDestroyed(ms.getMIDletInfo());
      currentMIDlet = null;
   }

   final Object getStateMutex() {
      return this.mStateMutex;
   }

   final MIDletRTInfo registerMIDlet(MIDletState ms) {
      synchronized(this.mStateMutex) {
         if (this.mInfoBeingConstructed != null) {
            Class _mCls = ms.getMIDlet().getClass();
            if (_mCls.equals(this.mInfoBeingConstructed.getCls()) && this.mInfoBeingConstructed.getInstance() == null) {
               this.mInfoBeingConstructed.setInstance(ms);
               return this.mInfoBeingConstructed;
            }
         }

         throw new SecurityException("MIDletManager ERROR: Illegal attempt to construct " + (ms == null ? "NULL" : ms.getMIDlet().toString()));
      }
   }

   final void initDisplay(MIDletState ms) {
      MIDlet _m = ms.getMIDlet();
      if (displayAccessor == null) {
         Display.getDisplay(_m);
      }

      displayAccessor.resetDisplay(_m);
   }

   private MIDletState getMIDletState(MIDlet m) {
      if (m != null) {
         for(int _mIdx = s_mInfoList.size() - 1; _mIdx >= 0; --_mIdx) {
            MIDletRTInfo _mInfo = (MIDletRTInfo)s_mInfoList.elementAt(_mIdx);
            MIDletState _msPretender = _mInfo.getInstance();
            if (_msPretender != null && m == _msPretender.getMIDlet()) {
               return _msPretender;
            }
         }

         throw new IllegalArgumentException("MIDletManager ERROR in getMIDletState(): " + m + " not found!");
      } else {
         throw new IllegalArgumentException("MIDletManager ERROR: getMIDletState(null) called!");
      }
   }

   public final String getMIDletName(MIDlet m) {
      String name = this.getMIDletState(m).getMIDletName();
      if (name != null && !name.equals("")) {
         return name;
      } else {
         name = s_getAppProperty("Nokia-MIDlet-Name-" + System.getProperty("microedition.locale"), 0);
         if (name == null) {
            name = s_getAppProperty("MIDlet-Name", 0);
         }

         return name != null ? name : "";
      }
   }

   public final void registerTimer(MIDlet m, Timer t) {
      if (!this.mTimerList.contains(t)) {
         this.mTimerList.addElement(t);
      }

   }

   public final void deregisterTimer(MIDlet m, Timer t) {
      if (this.mTimerList.contains(t)) {
         this.mTimerList.removeElement(t);
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
      this.selectEndMIDlet();
   }
}
