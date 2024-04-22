package com.nokia.mid.impl.isa.bluetooth;

import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.DataElement;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

public abstract class CommonServiceRecord implements ServiceRecord {
   private static final long MIN_RFCOMM_CHANNEL_ID = 1L;
   private static final long MAX_RFCOMM_CHANNEL_ID = 30L;
   private static final long MIN_PSM_VALUE = 4097L;
   private static final long MAX_PSM_VALUE = 65535L;
   protected Vector attrCollection = new Vector();
   protected final Object serializeLock = new Object();

   public static boolean validRFCOMMChannelValue(long var0) {
      return var0 >= 1L && var0 <= 30L;
   }

   public static boolean validPSMValue(long var0) {
      return var0 >= 4097L && var0 <= 65535L && (var0 & 72340172838076673L) == 1L;
   }

   public int[] getAttributeIDs() {
      synchronized(this.attrCollection) {
         int var3 = 0;
         int[] var1 = new int[this.attrCollection.size()];

         for(Enumeration var4 = this.attrCollection.elements(); var4.hasMoreElements(); var1[var3++] = ((CommonServiceRecord.AttributePair)var4.nextElement()).getAttrID()) {
         }

         return var1;
      }
   }

   public DataElement getAttributeValue(int var1) {
      if (var1 >= 0 && var1 <= 65535) {
         synchronized(this.attrCollection) {
            Enumeration var4 = this.attrCollection.elements();

            CommonServiceRecord.AttributePair var3;
            do {
               if (!var4.hasMoreElements()) {
                  return null;
               }

               var3 = (CommonServiceRecord.AttributePair)var4.nextElement();
            } while(var3.getAttrID() != var1);

            return var3.getAttrValue();
         }
      } else {
         throw new IllegalArgumentException("attribute ID out of range: " + var1);
      }
   }

   protected boolean updateAttributeValue(int var1, DataElement var2) {
      if (var1 >= 0 && var1 <= 65535) {
         synchronized(this.attrCollection) {
            int var4 = this.attrCollection.size();

            for(int var6 = 0; var6 < var4; ++var6) {
               CommonServiceRecord.AttributePair var5 = (CommonServiceRecord.AttributePair)this.attrCollection.elementAt(var6);
               if (var5.getAttrID() == var1) {
                  if (var2 == null) {
                     this.attrCollection.removeElementAt(var6);
                     return true;
                  }

                  var5.replaceAttrValue(var2);
                  return true;
               }
            }

            if (var2 != null) {
               this.attrCollection.addElement(new CommonServiceRecord.AttributePair(var1, var2));
               return true;
            } else {
               return false;
            }
         }
      } else {
         throw new IllegalArgumentException("invalid attribute ID");
      }
   }

   public boolean setAttributeValue(int var1, DataElement var2) {
      if (this instanceof RemoteServiceRecord) {
         throw new RuntimeException("object does not represents a service from remote");
      } else if (var1 == 0) {
         throw new IllegalArgumentException("invalid attribute ID");
      } else {
         return this.updateAttributeValue(var1, var2);
      }
   }

   protected String createConnectionURL(String var1, int var2, boolean var3, String var4, long var5) throws IllegalArgumentException {
      if (var2 != 0 && var2 != 1 && var2 != 2) {
         throw new IllegalArgumentException("Security is not one of the allowed values(NOAUTHENTICATE_NOENCRYPT,AUTHENTICATE_NOENCRYPT,AUTHENTICATE_ENCRYPT)");
      } else if (null == var4 || !var4.equals("btspp") && !var4.equals("btl2cap")) {
         throw new Error("createConnectionURL, protocol param must be btspp or btl2cap");
      } else if (var1 == null) {
         return null;
      } else {
         String var7 = new String(var4);
         var7 = var7.concat("://");
         var7 = var7.concat(var1);
         var7 = var7.concat(":");
         if (var4.equals("btl2cap")) {
            var7 = var7.concat(Integer.toHexString((int)(var5 & 61440L) >> 12));
            var7 = var7.concat(Integer.toHexString((int)(var5 & 3840L) >> 8));
            var7 = var7.concat(Integer.toHexString((int)(var5 & 240L) >> 4));
            var7 = var7.concat(Integer.toHexString((int)(var5 & 15L)));
         } else {
            var7 = var7.concat(Long.toString(var5));
         }

         switch(var2) {
         case 0:
            var7 = var7.concat(";authenticate=false;encrypt=false");
            break;
         case 1:
            var7 = var7.concat(";authenticate=true;encrypt=false");
            break;
         case 2:
            var7 = var7.concat(";authenticate=true;encrypt=true");
         }

         if (var3) {
            var7 = var7.concat(";master=true");
         } else {
            var7 = var7.concat(";master=false");
         }

         return var7;
      }
   }

   public DataElement checkProtocolDescriptorListForProtocolUuid(UUID var1) throws NullPointerException {
      DataElement var2 = this.getAttributeValue(4);
      if (var2 != null && var2.getDataType() == 48) {
         Enumeration var3 = (Enumeration)var2.getValue();
         Enumeration var4 = var3;

         while(true) {
            DataElement var5;
            do {
               do {
                  if (!var4.hasMoreElements()) {
                     throw new NullPointerException();
                  }

                  var5 = (DataElement)var4.nextElement();
               } while(var5 == null);
            } while(var5.getDataType() != 48);

            Enumeration var6 = (Enumeration)var5.getValue();
            Enumeration var7 = var6;

            while(var7.hasMoreElements()) {
               DataElement var8 = (DataElement)var7.nextElement();
               if (var8 != null && var8.getDataType() == 24 && var1.equals((UUID)var8.getValue())) {
                  if (var7.hasMoreElements()) {
                     return ((DataElement)var7.nextElement()).clone();
                  }

                  return null;
               }
            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   private class AttributePair {
      int attrID;
      DataElement attrValue;

      AttributePair(int var2, DataElement var3) {
         this.attrID = var2;
         this.attrValue = var3.clone();
      }

      private int getAttrID() {
         return this.attrID;
      }

      private DataElement getAttrValue() {
         return this.attrValue.clone();
      }

      private void replaceAttrValue(DataElement var1) {
         this.attrValue = var1.clone();
      }
   }
}
