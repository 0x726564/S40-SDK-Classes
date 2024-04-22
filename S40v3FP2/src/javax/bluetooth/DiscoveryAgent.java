package javax.bluetooth;

import com.nokia.mid.impl.isa.bluetooth.DiscoveryEvent;
import com.nokia.mid.impl.isa.bluetooth.RemoteServiceRecord;
import com.nokia.mid.impl.isa.bluetooth.Tracer;
import java.util.Vector;

public class DiscoveryAgent {
   private static final boolean DEBUG = false;
   public static final int NOT_DISCOVERABLE = 0;
   public static final int GIAC = 10390323;
   public static final int LIAC = 10390272;
   public static final int CACHED = 0;
   public static final int PREKNOWN = 1;
   private static final int INQUIRY_NOT_ACTIVE = 0;
   private static final int INQUIRY_ACTIVE = 1;
   private static final int INQUIRY_TERMINATED = 2;
   private static final int SERVICE_SEARCH_NOT_ACTIVE = 0;
   private static final int SERVICE_SEARCH_ACTIVE = 1;
   private static final int SERVICE_SEARCH_TERMINATED = 2;
   private static final int ATTRIBUTE_MAX_VALUE = 65535;
   private static boolean initialized;
   private static int curTransID;
   private int inquiryHandle;
   private DiscoveryAgent.InquiryListenerThread inquiryListenerThread;
   private int inquiryStatus;
   private Object inquiryStatusLock;
   private int serviceSearchHandle;
   private DiscoveryAgent.ServiceSearchListenerThread serviceSearchListenerThread;
   private int serviceSearchStatus;
   private Object serviceSearchStatusLock;
   private Vector cachedDevices;

   DiscoveryAgent() {
      synchronized(this) {
         if (initialized) {
            Tracer.println("DiscoveryAgent() throws RuntimeException");
            throw new RuntimeException("Discovery agent is already initialized(there can be only one discovery agent)");
         }

         initialized = true;
      }

      this.cachedDevices = new Vector();
      this.inquiryListenerThread = null;
      this.inquiryStatus = 0;
      this.inquiryStatusLock = new Object();
      this.serviceSearchListenerThread = null;
      this.serviceSearchStatus = 0;
      curTransID = 0;
      this.serviceSearchStatusLock = new Object();
   }

   private void addDeviceToCache(int var1, String var2) {
      synchronized(this.cachedDevices) {
         if (var1 == this.inquiryHandle) {
            if (!this.cachedDevices.contains(var2)) {
               this.cachedDevices.insertElementAt(var2, this.cachedDevices.size());
            }
         }

      }
   }

   public RemoteDevice[] retrieveDevices(int var1) {
      switch(var1) {
      case 0:
         synchronized(this.cachedDevices) {
            RemoteDevice[] var3 = null;
            int var4 = this.cachedDevices.size();
            if (var4 != 0) {
               var3 = new RemoteDevice[var4];

               for(int var5 = 0; var5 < var4; ++var5) {
                  var3[var5] = new RemoteDevice((String)this.cachedDevices.elementAt(var5));
               }
            }

            Tracer.println("retrieveDevices CACHED ret n=" + String.valueOf(var4));
            return var3;
         }
      case 1:
         Tracer.println("retrieveDevices PREKNOWN ret=NULL");
         return null;
      default:
         Tracer.println("IllegalArgumentException");
         throw new IllegalArgumentException("Unknown option(valid values: CACHED and PREKNOWN)");
      }
   }

   public boolean startInquiry(int var1, DiscoveryListener var2) throws IllegalArgumentException, BluetoothStateException, NullPointerException {
      Tracer.println("startInquiry(IAC=" + String.valueOf(var1) + ")");
      if (var1 == 10390272 || var1 == 10390323 || var1 >= 10390272 && var1 <= 10390335) {
         if (var2 == null) {
            Tracer.println("NullPointerException");
            throw new NullPointerException("Discovery listener can not be null");
         } else {
            synchronized(this.inquiryStatusLock) {
               if (this.inquiryStatus != 0) {
                  Tracer.println("BluetoothStateException");
                  throw new BluetoothStateException("One inquiry is still active and only one can be active");
               } else {
                  synchronized(this.cachedDevices) {
                     int var5 = 0;
                     boolean var6 = false;

                     do {
                        try {
                           Tracer.println("startInquiry0");
                           var5 = this.startInquiry0(var1);
                           var6 = true;
                        } catch (BluetoothStateException var12) {
                           Tracer.println("caught BluetoothStateException");
                           if (!var12.getMessage().equals("busy")) {
                              Tracer.println("BluetoothStateException");
                              throw var12;
                           }

                           Tracer.println("sleep(1000)");

                           try {
                              Thread.sleep(1000L);
                           } catch (InterruptedException var11) {
                           }
                        }
                     } while(!var6);

                     this.cachedDevices.removeAllElements();
                     this.inquiryHandle = var5;
                     this.inquiryStatus = 1;
                     this.inquiryListenerThread = new DiscoveryAgent.InquiryListenerThread(this.inquiryHandle, var2);
                     return true;
                  }
               }
            }
         }
      } else {
         Tracer.println("IllegalArgumentException");
         throw new IllegalArgumentException("Illegal parameter value(access code)");
      }
   }

   public boolean cancelInquiry(DiscoveryListener var1) throws NullPointerException {
      Tracer.println("cancelInquiry");
      if (var1 == null) {
         Tracer.println("NullPointerException");
         throw new NullPointerException("Discovery listener can not be null");
      } else {
         synchronized(this.inquiryStatusLock) {
            if (this.inquiryListenerThread != null && this.inquiryListenerThread.getListener() == var1 && this.inquiryStatus == 1) {
               Tracer.println("stopDiscovery0");
               this.stopDiscovery0(this.inquiryHandle);
               this.inquiryStatus = 2;
               return true;
            } else {
               return false;
            }
         }
      }
   }

   public int searchServices(int[] var1, UUID[] var2, RemoteDevice var3, DiscoveryListener var4) throws BluetoothStateException, IllegalArgumentException, NullPointerException {
      int[] var8 = new int[]{0, 1, 2, 3, 4};
      Tracer.println("searchServices");
      if (var2 != null && var3 != null && var4 != null) {
         if (var2.length == 0) {
            Tracer.println("IllegalArgumentException");
            throw new IllegalArgumentException("Illegal length of the UUID set");
         } else {
            int var5;
            int var6;
            for(var5 = 0; var5 < var2.length; ++var5) {
               if (var2[var5] == null) {
                  Tracer.println("NullPointerException");
                  throw new NullPointerException("UUID can not be null");
               }

               for(var6 = var5 + 1; var6 < var2.length; ++var6) {
                  if (var2[var5].equals(var2[var6])) {
                     Tracer.println("IllegalArgumentException");
                     throw new IllegalArgumentException("UUID set contains duplicates(not allowed)");
                  }
               }
            }

            int[] var7;
            if (var1 == null) {
               var7 = var8;
            } else {
               if (var1.length == 0) {
                  Tracer.println("IllegalArgumentException");
                  throw new IllegalArgumentException("Illegal length of the attribute set");
               }

               Vector var9 = new Vector(var8.length);

               for(var5 = 0; var5 < var8.length; ++var5) {
                  var9.addElement(new Integer(var8[var5]));
               }

               var5 = 0;

               label184:
               while(true) {
                  if (var5 >= var1.length) {
                     var7 = new int[var1.length + var9.size()];
                     System.arraycopy(var1, 0, var7, 0, var1.length);
                     var5 = 0;

                     while(true) {
                        if (var5 >= var9.size()) {
                           break label184;
                        }

                        var7[var1.length + var5] = (Integer)var9.elementAt(var5);
                        ++var5;
                     }
                  }

                  if (var1[var5] < 0 || var1[var5] > 65535) {
                     Tracer.println("IllegalArgumentException");
                     throw new IllegalArgumentException("Attribute has illegal value");
                  }

                  for(var6 = var5 + 1; var6 < var1.length; ++var6) {
                     if (var1[var5] == var1[var6]) {
                        Tracer.println("IllegalArgumentException");
                        throw new IllegalArgumentException("Attribut set contains duplicates(not allowed)");
                     }
                  }

                  for(var6 = 0; var6 < var9.size(); ++var6) {
                     if ((Integer)var9.elementAt(var6) == var1[var5]) {
                        var9.removeElementAt(var6);
                        break;
                     }
                  }

                  ++var5;
               }
            }

            if (var7.length > Integer.parseInt(LocalDevice.getProperty("bluetooth.sd.attr.retrievable.max"))) {
               Tracer.println("IllegalArgumentException");
               throw new IllegalArgumentException("Number of attributs to search is to large");
            } else {
               for(var5 = 0; var5 < var7.length; ++var5) {
                  for(var6 = var5 + 1; var6 < var7.length; ++var6) {
                     if (var7[var5] > var7[var6]) {
                        int var23 = var7[var5];
                        var7[var5] = var7[var6];
                        var7[var6] = var23;
                     }
                  }
               }

               String[] var10 = new String[var2.length];
               String var11 = new String("00000000000000000000000000000000");

               for(var5 = 0; var5 < var2.length; ++var5) {
                  String var12 = var2[var5].toString();
                  var10[var5] = new String(var11.substring(0, 32 - var12.length()).concat(var12));
               }

               byte[] var24 = new byte[4 + 16 * var10.length + 2 * var7.length];
               byte var13 = 0;
               int var25 = var13 + 1;
               var24[var13] = (byte)((var10.length & '\uff00') >> 8);
               var24[var25++] = (byte)(var10.length & 255);
               var24[var25++] = (byte)((var7.length & '\uff00') >> 8);
               var24[var25++] = (byte)(var7.length & 255);

               for(var5 = 0; var5 < var10.length; ++var5) {
                  for(int var14 = 0; var14 < 32; var14 += 2) {
                     var24[var25++] = (byte)Integer.parseInt(var10[var5].substring(var14, var14 + 2), 16);
                  }
               }

               for(var5 = 0; var5 < var7.length; ++var5) {
                  var24[var25++] = (byte)((var7[var5] & '\uff00') >> 8);
                  var24[var25++] = (byte)(var7[var5] & 255);
               }

               synchronized(this.serviceSearchStatusLock) {
                  if (this.serviceSearchStatus != 0) {
                     Tracer.println("BluetoothStateException");
                     throw new BluetoothStateException("The maximum number of concurrent service search is exceeded");
                  } else {
                     int var15 = 0;
                     boolean var16 = false;

                     do {
                        try {
                           Tracer.println("startServiceSearch0");
                           var15 = this.startServiceSearch0(var24, this.getBluetoothAddressAsByteArray(var3));
                           var16 = true;
                        } catch (BluetoothStateException var21) {
                           Tracer.println("caught BluetoothStateException");
                           if (!var21.getMessage().equals("busy")) {
                              Tracer.println("BluetoothStateException");
                              throw var21;
                           }

                           Tracer.println("sleep(1000)");

                           try {
                              Thread.sleep(1000L);
                           } catch (InterruptedException var20) {
                           }
                        }
                     } while(!var16);

                     this.serviceSearchHandle = var15;
                     this.serviceSearchStatus = 1;
                     ++curTransID;
                     if (curTransID < 0) {
                        curTransID = 1;
                     }

                     this.serviceSearchListenerThread = new DiscoveryAgent.ServiceSearchListenerThread(var3, curTransID, this.serviceSearchHandle, var4);
                     return curTransID;
                  }
               }
            }
         }
      } else {
         Tracer.println("NullPointerException");
         throw new NullPointerException("Discovery listener or UUID set can not be null");
      }
   }

   public boolean cancelServiceSearch(int var1) {
      synchronized(this.serviceSearchStatusLock) {
         Tracer.println("cancelServiceSearch");
         if (this.serviceSearchListenerThread != null && this.serviceSearchListenerThread.getTransID() == var1 && this.serviceSearchStatus == 1) {
            Tracer.println("stopDiscovery0");
            this.stopDiscovery0(this.serviceSearchHandle);
            this.serviceSearchStatus = 2;
            return true;
         } else {
            return false;
         }
      }
   }

   public String selectService(UUID var1, int var2, boolean var3) throws BluetoothStateException, NullPointerException, IllegalArgumentException {
      Tracer.println("selectService");
      if (var1 == null) {
         throw new NullPointerException("UUID can not be null");
      } else if (var2 != 0 && var2 != 1 && var2 != 2) {
         throw new IllegalArgumentException("Security is not one of the allowed values");
      } else {
         Object var4 = new Object();
         DiscoveryAgent.SelectServiceListener var5 = new DiscoveryAgent.SelectServiceListener(var2, var3, var4);
         synchronized(var4) {
            try {
               this.startInquiry(10390323, var5);
            } catch (BluetoothStateException var16) {
               throw new BluetoothStateException("Select service failed(problems to start inquiry)");
            }

            try {
               var4.wait();
            } catch (InterruptedException var15) {
            }
         }

         RemoteDevice[] var6 = var5.getDiscoveredDevices();
         if (var6 == null) {
            return null;
         } else {
            UUID[] var7 = new UUID[]{new UUID(var1.toString(), false)};

            for(int var8 = 0; var8 < var6.length; ++var8) {
               synchronized(var4) {
                  try {
                     this.searchServices((int[])null, var7, var6[var8], var5);
                  } catch (BluetoothStateException var13) {
                     throw new BluetoothStateException("Select service failed due to internal reasons");
                  }

                  try {
                     var4.wait();
                  } catch (InterruptedException var12) {
                  }
               }

               String var9 = var5.getURL();
               if (var9 != null) {
                  return new String(var9);
               }
            }

            return null;
         }
      }
   }

   private final byte[] getBluetoothAddressAsByteArray(RemoteDevice var1) {
      if (var1 == null) {
         throw new NullPointerException("btDev is null");
      } else {
         byte[] var2 = new byte[6];
         String var3 = var1.getBluetoothAddress();

         for(int var4 = 0; var4 < 12; var4 += 2) {
            var2[var4 / 2] = (byte)Integer.parseInt(var3.substring(var4, var4 + 2), 16);
         }

         return var2;
      }
   }

   private static native void initialize0();

   private native int startInquiry0(int var1) throws BluetoothStateException;

   private native void stopDiscovery0(int var1);

   private native int startServiceSearch0(byte[] var1, byte[] var2) throws BluetoothStateException;

   private native DiscoveryEvent getInquiryEventForHandle0(int var1);

   private native DiscoveryEvent getServiceSearchEventForHandle0(int var1);

   static {
      initialize0();
   }

   private class ServiceSearchListenerThread extends Thread {
      private RemoteDevice remoteDevice;
      private int transID;
      private int serviceSearchHandle;
      private DiscoveryListener discListener;

      public ServiceSearchListenerThread(RemoteDevice var2, int var3, int var4, DiscoveryListener var5) {
         this.remoteDevice = var2;
         this.transID = var3;
         this.serviceSearchHandle = var4;
         this.discListener = var5;
         this.start();
      }

      public int getTransID() {
         return this.transID;
      }

      public void run() {
         boolean var1 = false;
         DiscoveryEvent var2 = null;

         while(true) {
            while(true) {
               var2 = DiscoveryAgent.this.getServiceSearchEventForHandle0(this.serviceSearchHandle);
               if (var2 == null) {
                  try {
                     sleep(50L);
                  } catch (InterruptedException var11) {
                  }
               } else if (var2.messageType == 3) {
                  try {
                     RemoteServiceRecord[] var3 = var2.getServiceRecords(this.remoteDevice);
                     if (var3 != null) {
                        int var4 = 0;

                        for(int var5 = var3.length - 1; var4 < var5; --var5) {
                           RemoteServiceRecord var6 = var3[var4];
                           var3[var4] = var3[var5];
                           var3[var5] = var6;
                           ++var4;
                        }

                        synchronized(DiscoveryAgent.this.serviceSearchStatusLock) {
                           if (DiscoveryAgent.this.serviceSearchStatus == 1) {
                              this.discListener.servicesDiscovered(this.transID, var3);
                              var1 = true;
                           }
                        }
                     }
                  } catch (Exception var13) {
                  }
               } else if (var2.messageType == 4) {
                  Tracer.println("calling list.serviceSearchCompleted");
                  synchronized(DiscoveryAgent.this.serviceSearchStatusLock) {
                     DiscoveryAgent.this.serviceSearchListenerThread = null;
                     if (DiscoveryAgent.this.serviceSearchStatus != 2 && var2.respCode != 2) {
                        if (var2.respCode == 3) {
                           Tracer.println("SERVICE_SEARCH_ERROR");
                           DiscoveryAgent.this.serviceSearchStatus = 0;
                           this.discListener.serviceSearchCompleted(this.transID, var2.respCode);
                        } else if (var2.respCode == 6) {
                           Tracer.println("SERVICE_SEARCH_DEVICE_NOT_REACHABLE");
                           DiscoveryAgent.this.serviceSearchStatus = 0;
                           this.discListener.serviceSearchCompleted(this.transID, var2.respCode);
                        } else if (var1) {
                           Tracer.println("SERVICE_SEARCH_COMPLETED");
                           DiscoveryAgent.this.serviceSearchStatus = 0;
                           this.discListener.serviceSearchCompleted(this.transID, 1);
                        } else {
                           Tracer.println("SERVICE_SEARCH_NO_RECORDS");
                           DiscoveryAgent.this.serviceSearchStatus = 0;
                           this.discListener.serviceSearchCompleted(this.transID, 4);
                        }
                     } else {
                        Tracer.println("SERVICE_SEARCH_TERMINATED");
                        DiscoveryAgent.this.serviceSearchStatus = 0;
                        this.discListener.serviceSearchCompleted(this.transID, 2);
                     }

                     return;
                  }
               }
            }
         }
      }
   }

   private class InquiryListenerThread extends Thread {
      private int inquiryHandle;
      private DiscoveryListener discListener;

      public InquiryListenerThread(int var2, DiscoveryListener var3) {
         this.inquiryHandle = var2;
         this.discListener = var3;
         this.start();
      }

      public DiscoveryListener getListener() {
         return this.discListener;
      }

      public void run() {
         DiscoveryEvent var1 = null;

         while(true) {
            while(true) {
               var1 = DiscoveryAgent.this.getInquiryEventForHandle0(this.inquiryHandle);
               if (var1 == null) {
                  try {
                     sleep(500L);
                  } catch (InterruptedException var7) {
                  }
               } else if (var1.messageType == 1) {
                  String var2 = var1.getAddress();
                  if (var2 != null) {
                     synchronized(DiscoveryAgent.this.inquiryStatusLock) {
                        if (DiscoveryAgent.this.inquiryStatus == 1) {
                           DiscoveryAgent.this.addDeviceToCache(this.inquiryHandle, var2);
                           this.discListener.deviceDiscovered(new RemoteDevice(var2), new DeviceClass(var1.cod));
                        }
                     }
                  }
               } else if (var1.messageType == 2) {
                  synchronized(DiscoveryAgent.this.inquiryStatusLock) {
                     DiscoveryAgent.this.inquiryListenerThread = null;
                     if (DiscoveryAgent.this.inquiryStatus != 2 && var1.discType != 5) {
                        if (var1.discType == 7) {
                           DiscoveryAgent.this.inquiryStatus = 0;
                           Tracer.println("INQUIRY_ERROR");
                           this.discListener.inquiryCompleted(7);
                        } else {
                           DiscoveryAgent.this.inquiryStatus = 0;
                           Tracer.println("INQUIRY_COMPLETED");
                           this.discListener.inquiryCompleted(0);
                        }
                     } else {
                        DiscoveryAgent.this.inquiryStatus = 0;
                        Tracer.println("INQUIRY_TERMINATED");
                        this.discListener.inquiryCompleted(5);
                     }

                     return;
                  }
               }
            }
         }
      }
   }

   private class SelectServiceListener implements DiscoveryListener {
      private static final int MAJOR_DEV_CLASS_PHONE_BIT = 512;
      private int security;
      private boolean master;
      private String connectionURL;
      private Object callingThreadLock;
      private Vector foundPhones;
      private Vector foundOtherDevices;

      private SelectServiceListener(int var2, boolean var3, Object var4) {
         this.security = var2;
         this.master = var3;
         this.callingThreadLock = var4;
         this.connectionURL = null;
         this.foundPhones = new Vector();
         this.foundOtherDevices = new Vector();
      }

      private String getURL() {
         return this.connectionURL;
      }

      public void deviceDiscovered(RemoteDevice var1, DeviceClass var2) {
         if ((var2.getMajorDeviceClass() & 512) != 0) {
            this.foundPhones.addElement(var1);
         } else {
            this.foundOtherDevices.addElement(var1);
         }

      }

      public void inquiryCompleted(int var1) {
         synchronized(this.callingThreadLock) {
            Tracer.println("inquiryCompleted()=" + String.valueOf(var1));
            this.callingThreadLock.notify();
         }
      }

      private RemoteDevice[] getDiscoveredDevices() {
         RemoteDevice[] var1 = null;
         int var2 = this.foundPhones.size() + this.foundOtherDevices.size();
         if (var2 != 0) {
            var1 = new RemoteDevice[var2];

            int var3;
            for(var3 = 0; var3 < this.foundPhones.size(); ++var3) {
               var1[var3] = (RemoteDevice)this.foundPhones.elementAt(var3);
            }

            for(var3 = 0; var3 < this.foundOtherDevices.size(); ++var3) {
               var1[this.foundPhones.size() + var3] = (RemoteDevice)this.foundOtherDevices.elementAt(var3);
            }
         }

         return var1;
      }

      public void serviceSearchCompleted(int var1, int var2) {
         synchronized(this.callingThreadLock) {
            Tracer.println("serviceSearchCompleted()=" + String.valueOf(var2));
            this.callingThreadLock.notify();
         }
      }

      public void servicesDiscovered(int var1, ServiceRecord[] var2) {
         this.connectionURL = var2[0].getConnectionURL(this.security, this.master);
      }

      // $FF: synthetic method
      SelectServiceListener(int var2, boolean var3, Object var4, Object var5) {
         this(var2, var3, var4);
      }
   }
}
