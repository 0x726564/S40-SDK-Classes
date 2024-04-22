package com.nokia.mid.impl.isa.location;

import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;

class LocationListenerThread extends Thread {
   private LocationListener client;
   private LocationProviderDefault provider;
   private int interval;
   private int timeout;
   private int maxage;
   private boolean stopped;
   private int listenerThreadID;
   private boolean periodicUpdates;

   LocationListenerThread(LocationProviderDefault lp, LocationListener l, int interval, int timeout, int maxage, int listenerID) {
      this.client = l;
      this.provider = lp;
      this.interval = interval;
      this.timeout = timeout;
      this.maxage = maxage;
      this.stopped = false;
      this.periodicUpdates = false;
      this.listenerThreadID = listenerID;
   }

   void doPeriodicUpdates() {
      this.periodicUpdates = true;
   }

   public void run() {
      try {
         this.provider.getFirstLocation(this.interval, this.timeout, this.maxage, this.listenerThreadID);

         while(!this.stopped) {
            Location loc = this.provider.getNextLocation(this.listenerThreadID);
            if (this.client != null) {
               this.client.locationUpdated(this.provider, loc);
            }

            if (this.periodicUpdates) {
               try {
                  Thread.sleep((long)((this.interval + this.timeout) * 1000));
               } catch (Exception var3) {
               }
            }
         }
      } catch (SecurityException var4) {
      }

   }

   void doStop() {
      this.stopped = true;
   }
}
