package com.nokia.mid.impl.isa.ui;

import com.nokia.mid.pri.PriAccess;
import com.sun.midp.io.j2me.http.Protocol;
import java.util.Timer;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

final class MIDletManager implements EventConsumer, ExitManager, MIDletAccess, Runnable {
   private static DisplayAccess iI;
   private static boolean iJ = false;
   private static boolean iK = false;
   private MIDletRTInfo iL;
   private Object b = new Object();
   private MIDletState iM;
   private Vector iN = new Vector(10);
   private short iO = 0;
   private boolean iP = false;
   private Thread iQ;
   private MIDletState iR;
   private static MIDlet iS = null;

   private static boolean Y() {
      return null.size() == 1 || isAutomatedTCK();
   }

   static native String s_getAppProperty(String var0);

   static void s_setDisplayAccessor(DisplayAccess var0) {
      if (iI == null) {
         iI = var0;
      } else {
         throw new SecurityException();
      }
   }

   public final DisplayAccess getDisplayAccessor() {
      return iI;
   }

   private static native void exitInternal();

   private static native boolean isAutomatedTCK();

   private static native void nativeNotifyExitBeginError(Throwable var0);

   private static native void nativeNotifyExitBeginNormal();

   static native void notifyStartOrExitMIDlet(boolean var0, int var1);

   private static void a(Throwable var0) {
      if (!iJ) {
         iJ = true;
         nativeNotifyExitBeginError(var0);
      }

      EventDispatcher.a();
      exitInternal();
   }

   private MIDletManager() {
      Protocol.s_setTimerDatabase(this);

      try {
         EventDispatcher var1;
         (var1 = EventDispatcher.a(this.b, this)).attachEventConsumer(3, this);
         var1.start();
      } catch (Throwable var2) {
         a(var2);
      }

      if (PriAccess.getInt(5) == 2) {
         try {
            Class.forName("com.reliance.media.impl.RAPSystemPlayer");
            return;
         } catch (ClassNotFoundException var3) {
         }
      }

   }

   public final void consumeEvent(int var1, int var2, int var3) {
      if (var1 == 3) {
         switch(var2) {
         case 1:
            var1 = 1500;
            if (var3 == 0) {
               EventDispatcher.a();
               exitInternal();
            }

            if (var3 > 0) {
               var1 = var3;
            }

            this.q(var1);
            return;
         case 2:
         default:
            break;
         case 3:
            synchronized(this.b) {
               if (this.iM != null) {
                  this.iM.pauseApp();
                  this.iM.notifyPaused();
               }

               return;
            }
         case 4:
            synchronized(this.b) {
               if (this.iM != null) {
                  this.iM.resumeRequest();
               }

               return;
            }
         }
      }

   }

   final void a(MIDletRTInfo var1) {
      if (PriAccess.getInt(5) == 1) {
         String var2 = var1.getCls().toString();
         byte var3 = 0;
         if (var2.startsWith("class ")) {
            var3 = 6;
         }

         this.setReferringMIDletURI0("midlet:" + var2.substring(var3));
      }

      if (!this.iP) {
         this.iP = true;
         var1.getName();
         MIDletRTInfo var11 = var1;
         MIDletManager var10 = this;

         try {
            synchronized(var10.b){}

            try {
               var10.iL = var11;
               notifyStartOrExitMIDlet(true, null.indexOf(var11));
               throw new InstantiationException("Failed to construct instance of " + null);
            } finally {
               ;
            }
         } catch (Exception var9) {
            this.iP = false;
            this.a(var1, var9);
            this.iR = var1.getInstance();
            this.iQ = new Thread(this);
            this.iQ.start();
         }
      }

   }

   public final void run() {
      if (this.iP) {
         MIDletState var2 = this.iR;
         var2.setStateLifecycle(0);
         this.a(var2);
         iS = var2.getMIDlet();
         this.iM = this.iR;
         MIDletState var1 = this.iR;
         iI.setForeground(var1.getMIDlet());
         this.iP = false;
         this.iR = null;
         this.iQ = null;
      }

   }

   private void a(MIDletState var1) {
      try {
         var1.startApp();
      } catch (MIDletStateChangeException var2) {
         var1.setStateLifecycle(1);
      } catch (RuntimeException var3) {
         this.c((Throwable)var3);
      }
   }

   public final void abortOnError(Throwable var1) {
      a(var1);
   }

   public final void exitOnError(Throwable var1) {
      this.c(var1);
   }

   final void Z() {
      if (!iJ) {
         iJ = true;
         nativeNotifyExitBeginNormal();
      }

      this.q(1500);
   }

   private static void b(Throwable var0) {
      if (!iJ) {
         iJ = true;
         nativeNotifyExitBeginError(var0);
      }

   }

   private final void c(Throwable var1) {
      b(var1);
      this.q(1500);
   }

   private final void q(int var1) {
      if (this.iM != null) {
         try {
            this.b(this.iM);
         } catch (MIDletDestroyFailedException var2) {
         }

         this.setMIDletToDestroyedAndRemoveInstance(this.iM.getMIDletInfo());
         this.iM = null;
      }

      EventDispatcher.a();
      exitInternal();
   }

   public final void destroyMIDlet(MIDlet var1) {
      MIDletState var9 = a(var1);

      try {
         this.b(var9);
         MIDletRTInfo var3 = var9.getMIDletInfo();
         int var4 = 0;

         try {
            var4 = Integer.parseInt(System.getProperty("direct.midlet.launch"));
         } catch (Exception var7) {
         }

         if (!Y() && var4 <= 0) {
            if (this.iM != null) {
               MIDlet var6 = null.getMIDlet();
               iI = iI.replaceDisplay(var6);
               ((MIDletExplorer)var6).x();
               this.iM = null;
            }
         } else {
            d(var3.getInstance());
         }

         this.setMIDletToDestroyedAndRemoveInstance(var3);
         if (PriAccess.getInt(5) == 1 && this.isExitURISet0() && !Y() && var4 == 0) {
            this.Z();
         }

         if (Y() || var4 > 0) {
            EventDispatcher.a();
            exitInternal();
         }

      } catch (MIDletDestroyFailedException var8) {
         this.a(var9.getMIDletInfo(), var8);
      }
   }

   private void b(MIDletState var1) throws MIDletDestroyFailedException {
      try {
         if (PriAccess.getInt(5) == 1) {
            this.destroyExitURI0((String)null);
         }

         var1.destroyApp(true);

         try {
            ((Runnable)Class.forName("net.sourceforge.cobertura.runtime.midp.MIDletManagerHook").newInstance()).run();
         } catch (Exception var2) {
         }
      } catch (MIDletStateChangeException var3) {
         throw new MIDletDestroyFailedException(var3);
      } catch (RuntimeException var4) {
         throw new MIDletDestroyFailedException(var4);
      }
   }

   private void a(MIDletRTInfo var1, Exception var2) {
      b((Throwable)var2);
      this.setMIDletToDestroyedAndRemoveInstance(var1);
      EventDispatcher.a();
      exitInternal();
   }

   private void setMIDletToDestroyedAndRemoveInstance(MIDletRTInfo var1) {
      MIDletState var2;
      if ((var2 = var1.getInstance()) != null) {
         if (var2.getStateLifecycle() != 3) {
            var2.setStateLifecycle(3);
         }

         MIDletManager var3;
         for(int var4 = (var3 = this).iN.size() - 1; var4 >= 0; --var4) {
            ((Timer)var3.iN.elementAt(var4)).cancel();
         }

         var3.iN.removeAllElements();
         var1.k();
         if (!iJ) {
            notifyStartOrExitMIDlet(false, null.indexOf(var1));
         }
      }

   }

   final Object getStateMutex() {
      return this.b;
   }

   final MIDletRTInfo c(MIDletState var1) {
      synchronized(this.b) {
         if (this.iL != null && var1.getMIDlet().getClass().equals(this.iL.getCls()) && this.iL.getInstance() == null) {
            this.iL.setInstance(var1);
            return this.iL;
         } else {
            throw new SecurityException("MIDletManager ERROR: Illegal attempt to construct " + (var1 == null ? "NULL" : var1.getMIDlet().toString()));
         }
      }
   }

   static void d(MIDletState var0) {
      MIDlet var1 = var0.getMIDlet();
      if (iI == null) {
         Display.getDisplay(var1);
      }

      iI.resetDisplay(var1);
   }

   private static MIDletState a(MIDlet var0) {
      if (var0 != null) {
         MIDletState var1;
         if ((var1 = null.getInstance()) != null && var0 == var1.getMIDlet()) {
            return var1;
         } else {
            for(int var2 = null.size() - 1; var2 >= 0; --var2) {
               if ((var1 = ((MIDletRTInfo)null.elementAt(var2)).getInstance()) != null && var0 == var1.getMIDlet()) {
                  return var1;
               }
            }

            throw new IllegalArgumentException("MIDletManager ERROR in getMIDletState(): " + var0 + " not found!");
         }
      } else {
         throw new IllegalArgumentException("MIDletManager ERROR: getMIDletState(null) called!");
      }
   }

   public final String getMIDletName(MIDlet var1) {
      String var2;
      if ((var2 = a(var1).getMIDletName()) != null && !var2.equals("")) {
         return var2;
      } else {
         if ((var2 = s_getAppProperty("Nokia-MIDlet-Name-" + System.getProperty("microedition.locale"))) == null) {
            var2 = s_getAppProperty("MIDlet-Name");
         }

         return var2 != null ? var2 : "";
      }
   }

   public final void registerTimer(MIDlet var1, Timer var2) {
      if (!this.iN.contains(var2)) {
         this.iN.addElement(var2);
      }

   }

   public final void deregisterTimer(MIDlet var1, Timer var2) {
      if (this.iN.contains(var2)) {
         this.iN.removeElement(var2);
         if (++this.iO > 10) {
            this.iO = 0;
            if (this.iN.capacity() > 10) {
               this.iN.trimToSize();
               this.iN.ensureCapacity(10);
            }
         }
      }

   }

   static MIDlet getCurrentMIDlet() {
      return iS;
   }

   private native void destroyExitURI0(String var1);

   private native void setReferringMIDletURI0(String var1);

   private native boolean isExitURISet0();

   public final void exit() {
      this.Z();
   }
}
