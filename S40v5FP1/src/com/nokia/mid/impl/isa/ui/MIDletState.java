package com.nokia.mid.impl.isa.ui;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public abstract class MIDletState {
   static final int LIFE_ACTIVE = 0;
   static final int LIFE_PAUSEDOK = 1;
   static final int LIFE_WAITING = 2;
   static final int LIFE_DESTROYED = 3;
   private int mStateLifecycle = 2;
   private Object mStateMutex;
   private MIDletRTInfo mInfo;
   private MIDletManager mManager;
   protected MIDlet midlet;

   protected MIDletState(MIDlet m) {
      this.midlet = m;
      this.mManager = MIDletManager.s_getMIDletManager();
      this.mInfo = this.mManager.registerMIDlet(this);
      this.mStateMutex = this.mManager.getStateMutex();
      this.mManager.initDisplay(this);
   }

   protected abstract void startApp() throws MIDletStateChangeException;

   protected abstract void pauseApp();

   protected abstract void destroyApp(boolean var1) throws MIDletStateChangeException;

   public final void notifyDestroyed() {
      synchronized(this.mStateMutex) {
         this.mStateLifecycle = 3;
         this.mStateMutex.notify();
      }
   }

   public final void notifyPaused() {
      synchronized(this.mStateMutex) {
         if (this.mStateLifecycle == 0) {
            this.mStateLifecycle = 1;
         }

      }
   }

   public final String getAppProperty(String key) {
      return MIDletManager.s_getAppProperty(key);
   }

   public final void resumeRequest() {
      synchronized(this.mStateMutex) {
         if (this.mStateLifecycle == 1) {
            this.mStateLifecycle = 2;
            this.mStateMutex.notify();
         }

      }
   }

   int getStateLifecycle() {
      synchronized(this.mStateMutex) {
         return this.mStateLifecycle;
      }
   }

   void setStateLifecycle(int state) {
      synchronized(this.mStateMutex) {
         this.mStateLifecycle = state;
      }
   }

   MIDlet getMIDlet() {
      return this.midlet;
   }

   MIDletRTInfo getMIDletInfo() {
      return this.mInfo;
   }

   public final String getMIDletName() {
      return this.mInfo.getName();
   }
}
