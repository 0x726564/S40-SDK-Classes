package com.nokia.mid.s40.location;

import com.nokia.mid.impl.policy.PolicyAccess;
import javax.microedition.location.Landmark;
import javax.microedition.location.LandmarkException;
import javax.microedition.location.LandmarkStore;

public final class LocationUtil {
   static LocationUtil.LandmarkAccessorIF laIF = null;
   static LocationUtil.LandmarkStoreAccessorIF lsaIF = null;
   static boolean granted = false;

   private LocationUtil() {
   }

   public static int getLandmarkID(Landmark object) throws SecurityException, LandmarkException {
      if (!granted) {
         throw new SecurityException("Permission Denied");
      } else if (object == null) {
         throw new LandmarkException("Invalid landmark");
      } else if (laIF != null) {
         return laIF.getLandmarkID(object);
      } else {
         throw new LandmarkException("No landmark ID available");
      }
   }

   public static int getLandmarkStoreID(LandmarkStore object) throws SecurityException, LandmarkException {
      if (!granted) {
         throw new SecurityException("Permission Denied");
      } else if (object == null) {
         throw new LandmarkException("Invalid landmark store");
      } else if (lsaIF != null) {
         return lsaIF.getLandmarkStoreID(object);
      } else {
         throw new LandmarkException("No landmark store ID available");
      }
   }

   public static void setLandmarkAccessorIF(LocationUtil.LandmarkAccessorIF object) throws SecurityException {
      if (granted) {
         laIF = object;
      } else {
         throw new SecurityException("Permission Denied");
      }
   }

   public static void setLandmarkStoreAccessorIF(LocationUtil.LandmarkStoreAccessorIF object) throws SecurityException {
      if (granted) {
         lsaIF = object;
      } else {
         throw new SecurityException("Permission Denied");
      }
   }

   static {
      granted = PolicyAccess.isManufacturerSigned0();
   }

   public interface LandmarkStoreAccessorIF {
      int getLandmarkStoreID(LandmarkStore var1);
   }

   public interface LandmarkAccessorIF {
      int getLandmarkID(Landmark var1) throws LandmarkException;
   }
}
