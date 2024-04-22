package javax.microedition.location;

import com.nokia.mid.impl.policy.PolicyAccess;
import com.nokia.mid.s40.location.LocationUtil;
import java.io.IOException;

public class Landmark {
   private String name;
   private QualifiedCoordinates coordinates;
   private AddressInfo address;
   private String description;
   private byte dataValidity;
   private PersistedLandmark pLandmark;
   LandmarkStore parentStore;

   public Landmark(String aName, String aDescription, QualifiedCoordinates someCoordinates, AddressInfo anAddressInfo) {
      if (aName == null) {
         throw new NullPointerException("The name is null");
      } else {
         this.name = aName;
         this.address = anAddressInfo == null ? null : anAddressInfo.clone();
         this.coordinates = someCoordinates == null ? null : someCoordinates.clone();
         this.description = aDescription;
         this.dataValidity = (byte)(this.dataValidity | 2);
         this.dataValidity = (byte)(this.dataValidity | 1);
      }
   }

   Landmark(PersistedLandmark plm) {
      this(plm.name, plm.description, plm.coordinates, plm.address);
      this.dataValidity = plm.dataValidity;
      this.parentStore = plm.parentStore;
      this.pLandmark = plm;
      if (this.address != null) {
         boolean isempty = true;

         for(int a = 0; a < 17; ++a) {
            if (this.address.addressFields[a] != null) {
               isempty = false;
               break;
            }
         }

         if (isempty) {
            this.address = null;
         }
      }

   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      if (this.pLandmark == null) {
         return this.description;
      } else {
         synchronized(this.pLandmark) {
            if ((byte)(this.dataValidity & 1) == 0) {
               try {
                  this.description = this.pLandmark.retrieveDescription();
                  this.dataValidity = (byte)(this.dataValidity | 1);
               } catch (IOException var4) {
                  this.description = null;
               }
            }
         }

         return this.description;
      }
   }

   public QualifiedCoordinates getQualifiedCoordinates() {
      return this.coordinates;
   }

   public AddressInfo getAddressInfo() {
      if (this.pLandmark == null) {
         return this.address;
      } else {
         synchronized(this.pLandmark) {
            if ((byte)(this.dataValidity & 2) == 0) {
               try {
                  this.address = this.pLandmark.retrieveAdrressInfo();
                  this.dataValidity = (byte)(this.dataValidity | 2);
                  boolean isempty = true;

                  for(int a = 0; a < 17; ++a) {
                     if (this.address.addressFields[a] != null) {
                        isempty = false;
                        break;
                     }
                  }

                  if (isempty) {
                     this.address = null;
                  }
               } catch (IOException var5) {
                  this.address = null;
               }
            }
         }

         return this.address;
      }
   }

   public void setName(String name) {
      if (name == null) {
         throw new NullPointerException();
      } else {
         this.name = name;
      }
   }

   public void setDescription(String description) {
      if (this.pLandmark != null) {
         synchronized(this.pLandmark) {
            this.description = description;
            this.dataValidity = (byte)(this.dataValidity | 1);
         }
      } else {
         this.description = description;
         this.dataValidity = (byte)(this.dataValidity | 1);
      }

   }

   public void setQualifiedCoordinates(QualifiedCoordinates coordinates) {
      this.coordinates = coordinates;
   }

   public void setAddressInfo(AddressInfo addressInfo) {
      if (this.pLandmark != null) {
         synchronized(this.pLandmark) {
            this.address = addressInfo;
            this.dataValidity = (byte)(this.dataValidity | 2);
         }
      } else {
         this.address = addressInfo;
         this.dataValidity = (byte)(this.dataValidity | 2);
      }

   }

   PersistedLandmark getPersistedLandmark() {
      return this.pLandmark;
   }

   void setPersistedLandmark(PersistedLandmark plm) {
      this.pLandmark = plm;
   }

   byte getDataValidity() {
      return this.dataValidity;
   }

   static {
      if (PolicyAccess.isManufacturerSigned0()) {
         LocationUtil.setLandmarkAccessorIF(new Landmark.LandmarkAccessor());
      }

   }

   private static class LandmarkAccessor implements LocationUtil.LandmarkAccessorIF {
      private LandmarkAccessor() {
      }

      public int getLandmarkID(Landmark lm) throws LandmarkException {
         PersistedLandmark plm = lm.getPersistedLandmark();
         if (plm != null) {
            return lm.getPersistedLandmark().getLandmarkID();
         } else {
            throw new LandmarkException("no ID for non persisted landmark");
         }
      }

      // $FF: synthetic method
      LandmarkAccessor(Object x0) {
         this();
      }
   }
}
