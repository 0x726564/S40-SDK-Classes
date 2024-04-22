package com.nokia.mid.impl.isa.bluetooth;

import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.DataElement;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

public abstract class CommonServiceRecord implements ServiceRecord {
   public static final int RFCOMM_UUID = 3;
   public static final int OBEX_UUID = 8;
   public static final int L2CAP_UUID = 256;
   public static final int SERIAL_PORT_UUID = 4353;
   public static final int SERVICE_RECORD_HANDLE_ID = 0;
   public static final int SERVICE_CLASS_IDLIST_ID = 1;
   public static final int SERVICE_RECORD_STATE_ID = 2;
   public static final int SERVICE_ID = 3;
   public static final int PROTOCOL_DESCRIPTOR_LIST_ID = 4;
   public static final int SERVICE_NAME_ID = 256;
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

         for(Enumeration var5 = this.attrCollection.elements(); var5.hasMoreElements(); var1[var3++] = CommonServiceRecord.AttributePair.a((CommonServiceRecord.AttributePair)var5.nextElement())) {
         }

         return var1;
      }
   }

   public DataElement getAttributeValue(int var1) {
      if (var1 >= 0 && var1 <= 65535) {
         synchronized(this.attrCollection) {
            Enumeration var3 = this.attrCollection.elements();

            CommonServiceRecord.AttributePair var5;
            do {
               if (!var3.hasMoreElements()) {
                  return null;
               }
            } while(CommonServiceRecord.AttributePair.a(var5 = (CommonServiceRecord.AttributePair)var3.nextElement()) != var1);

            return CommonServiceRecord.AttributePair.b(var5);
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
               CommonServiceRecord.AttributePair var5;
               if (CommonServiceRecord.AttributePair.a(var5 = (CommonServiceRecord.AttributePair)this.attrCollection.elementAt(var6)) == var1) {
                  if (var2 == null) {
                     this.attrCollection.removeElementAt(var6);
                     return true;
                  }

                  CommonServiceRecord.AttributePair.a(var5, var2);
                  return true;
               }
            }

            if (var2 != null) {
               this.attrCollection.addElement(new CommonServiceRecord.AttributePair(this, var1, var2));
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
      } else if (null == var4 || !var4.equals("btspp") && !var4.equals("btl2cap") && !var4.equals("btgoep")) {
         throw new Error("createConnectionURL, protocol param must be btspp, btl2cap or btgoep");
      } else if (var1 == null) {
         return null;
      } else {
         String var7 = (new String(var4)).concat("://").concat(var1).concat(":");
         if (var4.equals("btl2cap")) {
            var7 = var7.concat(Integer.toHexString((int)(var5 & 61440L) >> 12)).concat(Integer.toHexString((int)(var5 & 3840L) >> 8)).concat(Integer.toHexString((int)(var5 & 240L) >> 4)).concat(Integer.toHexString((int)(var5 & 15L)));
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
      DataElement var4;
      if ((var4 = this.getAttributeValue(4)) != null && var4.getDataType() == 48) {
         Enumeration var5 = (Enumeration)var4.getValue();

         while(true) {
            DataElement var2;
            do {
               do {
                  if (!var5.hasMoreElements()) {
                     throw new NullPointerException();
                  }
               } while((var2 = (DataElement)var5.nextElement()) == null);
            } while(var2.getDataType() != 48);

            Enumeration var6 = (Enumeration)var2.getValue();

            while(var6.hasMoreElements()) {
               DataElement var3;
               if ((var3 = (DataElement)var6.nextElement()) != null && var3.getDataType() == 24 && var1.equals((UUID)var3.getValue())) {
                  if (var6.hasMoreElements()) {
                     return (DataElement)var6.nextElement();
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
      private int cC;
      private DataElement cD;

      AttributePair(CommonServiceRecord var1, int var2, DataElement var3) {
         this.cC = var2;
         this.cD = var3;
      }

      private int getAttrID() {
         return this.cC;
      }

      private DataElement getAttrValue() {
         return this.cD;
      }

      static int a(CommonServiceRecord.AttributePair var0) {
         return var0.getAttrID();
      }

      static DataElement b(CommonServiceRecord.AttributePair var0) {
         return var0.getAttrValue();
      }

      static void a(CommonServiceRecord.AttributePair var0, DataElement var1) {
         var0.cD = var1;
      }
   }
}
