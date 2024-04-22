package com.nokia.mid.impl.isa.location;

import com.nokia.mid.impl.isa.util.SharedObjects;
import com.nokia.mid.pri.PriAccess;
import java.util.Vector;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

public class LocationProviderDefault extends LocationProvider {
   private static final int LOC_STATUS_OK = 0;
   private static final int LOC_STATUS_FAIL = 1;
   private static final int LOC_STATUS_SECURITY_FAIL = 2;
   private static final int UNINIT_STATE = 0;
   private static final int ANY_METHOD = 0;
   private static final int RESET_BOTH = 0;
   private static final int RESET_GET_LOCATION_ONLY = 1;
   private static final int RESET_LISTENER_ONLY = 2;
   private static int listenerIDGen = 0;
   private static final Object nativeLock = SharedObjects.getLock("com.nokia.mid.impl.isa.location.LocationProviderDefault");
   private static final Object getLocationLock = SharedObjects.getLock("com.nokia.mid.impl.isa.location.LocationProviderDefault.getLocation");
   private static final Object resetLock = SharedObjects.getLock("com.nokia.mid.impl.isa.location.LocationProviderDefault.reset");
   private Criteria criteria = new Criteria();
   private LocationListenerThread listenThread;
   private boolean haveAccess = false;
   private static LocationImpl lastLocation;
   private static boolean access;
   private volatile boolean isReset = false;
   private int nativeTransId;
   boolean isReadyForReturn;
   private boolean privacyUIDenied = false;
   LocationImpl singleLocation;
   LocationImpl listenLocation;
   int state = 0;
   private boolean sessionDenied = false;
   private int method = 0;
   LocationListener listener = null;
   private static LocationProviderDefault.StateChangeThread stateThread = null;
   private static int overallState = 0;

   LocationProviderDefault(Criteria src_criteria) {
      if (src_criteria != null) {
         this.criteria.setAltitudeRequired(src_criteria.isAltitudeRequired());
         this.criteria.setCostAllowed(src_criteria.isAllowedToCost());
         this.criteria.setHorizontalAccuracy(src_criteria.getHorizontalAccuracy());
         this.criteria.setPreferredPowerConsumption(src_criteria.getPreferredPowerConsumption());
         this.criteria.setPreferredResponseTime(src_criteria.getPreferredResponseTime());
         this.criteria.setSpeedAndCourseRequired(src_criteria.isSpeedAndCourseRequired());
         this.criteria.setVerticalAccuracy(src_criteria.getVerticalAccuracy());
         this.criteria.setAddressInfoRequired(src_criteria.isAddressInfoRequired());
      }

      this.singleLocation = null;
   }

   public static LocationProvider getProvider(Criteria criteria) throws LocationException {
      synchronized(nativeLock) {
         LocationProviderDefault lp = nativeGetProvider(criteria);
         if (lp != null) {
            lp.state = 1;
         }

         return lp;
      }
   }

   public static Location getLastLocation() {
      synchronized(nativeLock) {
         SecurityPermission.checkLocationPermission();
         return lastLocation != null ? lastLocation.clone() : null;
      }
   }

   public Location getLocation(int timeout) throws LocationException, InterruptedException {
      synchronized(getLocationLock) {
         if (timeout != 0 && timeout >= -1) {
            if (timeout == -1) {
               if (PriAccess.getInt(3) == 0) {
                  timeout = 60;
               } else {
                  timeout = 300;
               }
            }

            try {
               SecurityPermission.checkLocationPermission();
            } catch (SecurityException var5) {
               throw var5;
            }

            this.singleLocation = getLocationInstance();
            if (this.isReset) {
               this.isReset = false;
               this.singleLocation = null;
               throw new InterruptedException();
            } else if (this.nativeGetLocation(this.singleLocation, this.criteria, timeout) == 2) {
               this.singleLocation = null;
               throw new SecurityException("Positioning Log denied Location access.");
            } else {
               this.nativeTransId = 0;
               LocationImpl loc = this.singleLocation;
               this.singleLocation = null;
               updateTempUnavailState();
               if (loc != null) {
                  loc.setTimestamp(System.currentTimeMillis() - loc.getTimestamp());
                  if (loc.isValid()) {
                     if (this.isReset) {
                        this.isReset = false;
                     }

                     lastLocation = loc.clone();
                     return loc;
                  }
               }

               if (this.isReset) {
                  this.isReset = false;
                  throw new InterruptedException();
               } else {
                  throw new LocationException();
               }
            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public void setLocationListener(LocationListener listener, int interval, int timeout, int maxage) {
      synchronized(nativeLock) {
         this.synchronizedSetLocationListener(listener, interval, timeout, maxage);
      }
   }

   public int getState() {
      return this.state;
   }

   public void reset() {
      if (this.singleLocation != null) {
         synchronized(resetLock) {
            this.isReset = true;
            this.nativeEndLocation(this.singleLocation, 1);
            this.singleLocation = null;
         }
      }

   }

   private static synchronized boolean accessLocationSession(boolean start) {
      boolean boRes = true;
      if (start) {
         if (!access) {
            access = true;
         } else {
            boRes = false;
         }
      } else {
         access = false;
      }

      return boRes;
   }

   private void doSetReturn(boolean doReturn) {
      this.isReadyForReturn = doReturn;
   }

   private boolean isReturnable() {
      return this.isReadyForReturn;
   }

   private void waitForReturn() {
      while(!this.isReturnable()) {
         try {
            Thread.sleep(100L);
         } catch (InterruptedException var2) {
         }
      }

   }

   private void updateLastLocation(LocationImpl l) {
      if (l.isValid()) {
         lastLocation = l.clone();
      }

   }

   private static LocationImpl getLocationInstance() {
      QualifiedCoordinates qc = new QualifiedCoordinates(1.0D, 0.0D, 0.0F, 0.0F, 0.0F);
      return new LocationImpl(qc);
   }

   private void stopLocationListener() {
      if (stateThread != null) {
         stateThread.removeProviderFromList(this);
      }

      this.listener = null;
      if (this.listenThread != null) {
         this.listenThread.doStop();
         if (this.privacyUIDenied) {
            this.privacyUIDenied = false;
         } else {
            this.nativeEndLocation(this.listenLocation, 2);
         }

         this.listenThread = null;
         accessLocationSession(false);
         updateTempUnavailState();
      }

   }

   private synchronized void synchronizedSetLocationListener(LocationListener listener, int interval, int timeout, int maxage) {
      if (listener == null) {
         this.stopLocationListener();
      } else {
         SecurityPermission.checkLocationPermission();
         if (interval < -1) {
            throw new IllegalArgumentException();
         } else if (interval != -1 && interval != 0 && (timeout > interval || maxage > interval || timeout < 1 && timeout != -1 || maxage < 1 && maxage != -1)) {
            throw new IllegalArgumentException();
         } else {
            this.stopLocationListener();
            this.listener = listener;
            stateThread.addProviderToList(this);
            if (interval != 0) {
               if (accessLocationSession(true)) {
                  this.doSetReturn(false);
                  this.listenThread = new LocationListenerThread(this, listener, interval, timeout, maxage, this.hashCode());
                  this.listenThread.start();
                  this.waitForReturn();
                  if (this.privacyUIDenied) {
                     this.stopLocationListener();
                     throw new SecurityException("Positioning Log denied Location access.");
                  }
               } else {
                  this.state = 2;
                  this.sessionDenied = true;
                  listener.providerStateChanged(this, 2);
               }

            }
         }
      }
   }

   void getFirstLocation(int interval, int timeout, int maxage, int listenerID) throws SecurityException {
      this.listenLocation = getLocationInstance();
      int locResp = this.nativeGetFirstLocation(this.listenLocation, this.criteria, timeout, interval, maxage, listenerID);
      if (locResp == 1) {
         this.listenThread.doPeriodicUpdates();
      }

      if (locResp == 2) {
         this.privacyUIDenied = true;
         if (!this.isReturnable()) {
            this.doSetReturn(true);
         }

         throw new SecurityException();
      } else {
         if (!this.isReturnable()) {
            this.doSetReturn(true);
         }

      }
   }

   Location getNextLocation(int listenerID) {
      this.listenLocation = getLocationInstance();
      int locResp = this.nativeGetNextLocation(this.listenLocation, listenerID);
      if (locResp == 1) {
         this.listenThread.doPeriodicUpdates();
      }

      this.listenLocation.setTimestamp(System.currentTimeMillis() - this.listenLocation.getTimestamp());
      this.updateLastLocation(this.listenLocation);
      return this.listenLocation;
   }

   private static void updateTempUnavailState() {
      if (stateThread != null) {
         Vector providerList = stateThread.getProviderList();

         for(int i = 0; i < providerList.size(); ++i) {
            LocationProviderDefault lp = (LocationProviderDefault)providerList.elementAt(i);
            if (lp.state == 2 && lp.sessionDenied) {
               lp.state = 1;
               lp.sessionDenied = false;
               lp.listener.providerStateChanged(lp, lp.state);
            }
         }
      }

   }

   int validateState() {
      int state = 0;
      switch(this.method) {
      case 17:
         state = overallState >> 4;
         break;
      case 18:
         state = overallState >> 2;
         break;
      case 19:
         state = overallState;
      }

      return state & 3;
   }

   private native int nativeGetLocation(Location var1, Criteria var2, int var3);

   private native int nativeGetFirstLocation(Location var1, Criteria var2, int var3, int var4, int var5, int var6);

   private native int nativeGetNextLocation(Location var1, int var2);

   private synchronized native void nativeEndLocation(Location var1, int var2);

   private static native LocationProviderDefault nativeGetProvider(Criteria var0);

   private native int nativeValidateState(int var1, Criteria var2);

   private static native void nativeInit(Thread var0);

   static {
      if (stateThread == null) {
         stateThread = new LocationProviderDefault.StateChangeThread();
      }

      nativeInit(stateThread);
      stateThread.start();
   }

   private static class StateChangeThread extends Thread {
      private Vector providerList = new Vector(2);

      StateChangeThread() {
      }

      public void run() {
         while(true) {
            LocationProviderDefault.overallState = this.nativeWaitStateChange();

            for(int i = 0; i < this.providerList.size(); ++i) {
               LocationProviderDefault lp = (LocationProviderDefault)this.providerList.elementAt(i);
               int newState = lp.validateState();
               if (newState != lp.state) {
                  lp.state = newState;
                  lp.listener.providerStateChanged(lp, lp.state);
               }
            }
         }
      }

      void addProviderToList(LocationProviderDefault lp) {
         this.providerList.addElement(lp);
      }

      void removeProviderFromList(LocationProviderDefault lp) {
         this.providerList.removeElement(lp);
      }

      Vector getProviderList() {
         return this.providerList;
      }

      private native int nativeWaitStateChange();
   }
}
