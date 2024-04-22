package com.nokia.mid.impl.isa.ui;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public abstract class MIDletState {
   private int t = 2;
   private Object b;
   private MIDletRTInfo u;
   private MIDletManager v;
   protected MIDlet midlet;

   protected MIDletState(MIDlet var1) {
      this.midlet = var1;
      this.v = null;
      this.u = this.v.c(this);
      this.b = this.v.getStateMutex();
      MIDletManager.d(this);
   }

   protected abstract void startApp() throws MIDletStateChangeException;

   protected abstract void pauseApp();

   protected abstract void destroyApp(boolean var1) throws MIDletStateChangeException;

   public final void notifyDestroyed() {
      synchronized(this.b) {
         this.t = 3;
         this.b.notify();
      }
   }

   public final void notifyPaused() {
      synchronized(this.b) {
         if (this.t == 0) {
            this.t = 1;
         }

      }
   }

   public final String getAppProperty(String var1) {
      return MIDletManager.s_getAppProperty(var1);
   }

   public final void resumeRequest() {
      synchronized(this.b) {
         if (this.t == 1) {
            this.t = 2;
            this.b.notify();
         }

      }
   }

   int getStateLifecycle() {
      synchronized(this.b) {
         return this.t;
      }
   }

   void setStateLifecycle(int var1) {
      synchronized(this.b) {
         this.t = var1;
      }
   }

   MIDlet getMIDlet() {
      return this.midlet;
   }

   MIDletRTInfo getMIDletInfo() {
      return this.u;
   }

   public final String getMIDletName() {
      return this.u.getName();
   }
}
