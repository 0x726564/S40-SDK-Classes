package javax.microedition.location;

import com.nokia.mid.impl.isa.location.EmulatedLocationProvider;
import com.nokia.mid.impl.isa.location.LocationProviderDefault;

public abstract class LocationProvider {
   public static final int AVAILABLE = 1;
   public static final int TEMPORARILY_UNAVAILABLE = 2;
   public static final int OUT_OF_SERVICE = 3;

   protected LocationProvider() {
   }

   public static LocationProvider getInstance(Criteria criteria) throws LocationException {
      return EmulatedLocationProvider.getInstance();
   }

   public abstract Location getLocation(int var1) throws LocationException, InterruptedException;

   public abstract void setLocationListener(LocationListener var1, int var2, int var3, int var4);

   public static Location getLastKnownLocation() {
      return LocationProviderDefault.getLastLocation();
   }

   public abstract int getState();

   public abstract void reset();

   public static void addProximityListener(ProximityListener listener, Coordinates coordinates, float proximityRadius) throws LocationException {
      throw new LocationException();
   }

   public static void removeProximityListener(ProximityListener listener) {
   }
}
