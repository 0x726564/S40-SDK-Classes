package com.nokia.mid.impl.isa.location;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

public class EmulatedLocationProvider extends LocationProvider {
   EmulatedLocationProvider.LocationListenerUpdater listenerThread;
   private static Vector locations;
   private static int currentLocation;
   private static EmulatedLocationProvider instance = null;

   public EmulatedLocationProvider() {
      currentLocation = 0;
      locations = new Vector();
      Criteria criteria = new Criteria();
      criteria.getHorizontalAccuracy();
      criteria.getPreferredPowerConsumption();
      criteria.getPreferredResponseTime();
      criteria.getVerticalAccuracy();
   }

   public static LocationProvider getInstance() {
      instance = new EmulatedLocationProvider();
      locations = new Vector();
      locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(1.0D, 1.0D, 3.0F, 10.0F, 20.0F)));
      locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(1.0D, 1.0D, 3.0F, 10.0F, 20.0F)));
      locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(2.0D, 2.0D, 3.0F, 10.0F, 20.0F)));
      locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(3.0D, 4.0D, 3.5F, 10.0F, 20.0F)));
      locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(4.0D, 5.0D, 3.6F, 10.0F, 20.0F)));
      locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(5.0D, 6.0D, 2.0F, 10.0F, 20.0F)));
      return instance;
   }

   public Location getLocation(int timeout) throws LocationException, InterruptedException {
      int newtime = true;
      SecurityPermission.checkLocationPermission();
      if (timeout != 0 && timeout >= -1) {
         if (timeout != -1) {
            ;
         }

         try {
            locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(1.0D, 1.0D, 3.0F, 10.0F, 20.0F)));
            locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(1.0D, 1.0D, 3.0F, 10.0F, 20.0F)));
            locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(2.0D, 2.0D, 3.0F, 10.0F, 20.0F)));
            locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(3.0D, 4.0D, 3.0F, 10.0F, 20.0F)));
            locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(4.0D, 5.0D, 2.0F, 10.0F, 20.0F)));
            locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(5.0D, 6.0D, 2.0F, 10.0F, 20.0F)));
         } finally {
            return (Location)locations.elementAt(currentLocation++ % (locations.size() - 1));
         }

         return (Location)locations.elementAt(currentLocation++ % (locations.size() - 1));
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setLocationListener(LocationListener listener, int interval, int timeout, int maxage) {
      SecurityPermission.checkLocationPermission();

      try {
         if (this.listenerThread != null) {
            this.listenerThread.abort();
         }

         this.listenerThread = new EmulatedLocationProvider.LocationListenerUpdater(this, listener, interval, timeout, maxage);
         this.listenerThread.start();
      } catch (IllegalArgumentException var6) {
         throw var6;
      } catch (SecurityException var7) {
         throw var7;
      } catch (IllegalMonitorStateException var8) {
      } catch (NullPointerException var9) {
      } catch (Exception var10) {
      }

   }

   public int getState() {
      return 1;
   }

   public void reset() {
      this.setLocationListener((LocationListener)null, -1, -1, -1);
   }

   static boolean parseCoordFile() {
      int counter = 0;
      DataInputStream in = null;
      FileConnection filecon = null;

      try {
         filecon = (FileConnection)Connector.open("file:///E:/GPS/coord.txt");
         if (filecon.exists()) {
            in = filecon.openDataInputStream();

            for(String line = readLine(in); line != null; line = readLine(in)) {
               EmulatedLocationProvider.MyTokenizer tokenizer = new EmulatedLocationProvider.MyTokenizer(line, ";");

               try {
                  double lat = Double.parseDouble(tokenizer.nextToken());
                  double lon = Double.parseDouble(tokenizer.nextToken());
                  float alt = Float.parseFloat(tokenizer.nextToken());
                  float hor = Float.parseFloat(tokenizer.nextToken());
                  float ver = Float.parseFloat(tokenizer.nextToken());
                  locations.addElement(new EmulatedLocationImpl(new QualifiedCoordinates(lat, lon, alt, hor, ver)));
                  ++counter;
               } catch (Throwable var22) {
               }
            }
         }
      } catch (IOException var23) {
      } finally {
         try {
            if (in != null) {
               in.close();
            }

            if (filecon != null) {
               filecon.close();
            }
         } catch (IOException var21) {
         }

      }

      return counter != 0;
   }

   private static String readLine(DataInputStream in) {
      StringBuffer buf = new StringBuffer();

      int ch;
      try {
         while((ch = in.read()) != 10 && ch != -1) {
            if (ch != 13) {
               buf.append((char)ch);
            }
         }
      } catch (IOException var4) {
      }

      return buf.length() == 0 ? null : buf.toString();
   }

   class LocationListenerUpdater extends Thread {
      private LocationProvider provider;
      private final LocationListener listener;
      public Location loc;
      private Vector locations;
      private int interval = 1;
      private int timeout = 1;
      private int maxage = 1;
      private boolean abort;

      LocationListenerUpdater(LocationProvider provider, LocationListener listener, int interval, int timeout, int maxage) {
         int intervalcheck = -1;
         int intervalnext = true;
         int timeoutcheck = true;
         int maxagecheck = true;
         this.listener = listener;
         if (listener == null) {
            this.abort = false;
         }

         if (interval == intervalcheck) {
            this.interval = 5;
         } else {
            this.interval = interval;
         }

         if (interval == -5) {
            throw new IllegalArgumentException();
         } else if (interval < intervalcheck) {
            throw new IllegalArgumentException();
         } else if (interval >= -1 && (interval <= 0 || timeout <= interval && maxage <= interval && (timeout >= 1 || timeout == -1) && (maxage >= 1 || maxage == -1))) {
            this.abort = false;
         } else {
            throw new IllegalArgumentException();
         }
      }

      public void run() {
         while(true) {
            try {
               if (!this.abort) {
                  try {
                     synchronized(this.listener) {
                        this.listener.wait((long)(this.interval * 1000));
                     }
                  } catch (InterruptedException var7) {
                  }

                  try {
                     this.listener.locationUpdated(this.provider, EmulatedLocationProvider.this.getLocation(this.timeout));
                  } catch (LocationException var3) {
                  } catch (InterruptedException var4) {
                  } catch (IllegalArgumentException var5) {
                  }
                  continue;
               }
            } catch (NullPointerException var8) {
            }

            return;
         }
      }

      public void abort() {
         try {
            this.abort = true;
         } catch (IllegalMonitorStateException var2) {
         } catch (Exception var3) {
         }

      }
   }

   private static class MyTokenizer {
      private String str;
      private String del;
      private int pos = 0;

      public MyTokenizer(String line, String del) {
         this.str = line;
         this.del = del;
      }

      public String nextToken() {
         int ind = this.str.indexOf(this.del, this.pos);
         String token;
         if (ind == -1) {
            if (ind < this.str.length()) {
               token = this.str.substring(this.pos);
               this.pos = this.str.length();
               return token;
            } else {
               return null;
            }
         } else {
            token = this.str.substring(this.pos, ind);
            this.pos = ind + 1;
            return token;
         }
      }
   }
}
