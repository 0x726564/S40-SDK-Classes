package javax.bluetooth;

import com.nokia.mid.impl.isa.bluetooth.DiscoveryEvent;
import com.nokia.mid.impl.isa.bluetooth.RemoteServiceRecord;
import com.nokia.mid.impl.isa.bluetooth.Tracer;
import java.util.Vector;

public class DiscoveryAgent {
   public static final int NOT_DISCOVERABLE = 0;
   public static final int GIAC = 10390323;
   public static final int LIAC = 10390272;
   public static final int CACHED = 0;
   public static final int PREKNOWN = 1;
   private static boolean initialized;
   private static int d;
   private int a;
   private DiscoveryAgent.InquiryListenerThread e;
   private int f;
   private Object g;
   private int h;
   private DiscoveryAgent.ServiceSearchListenerThread i;
   private int j;
   private Object k;
   private Vector l;

   DiscoveryAgent() {
      synchronized(this) {
         if (initialized) {
            Tracer.println("DiscoveryAgent() throws RuntimeException");
            throw new RuntimeException("Discovery agent is already initialized(there can be only one discovery agent)");
         }

         initialized = true;
      }

      this.l = new Vector();
      this.e = null;
      this.f = 0;
      this.g = new Object();
      this.i = null;
      this.j = 0;
      d = 0;
      this.k = new Object();
   }

   public RemoteDevice[] retrieveDevices(int var1) {
      switch(var1) {
      case 0:
         synchronized(this.l) {
            RemoteDevice[] var2 = null;
            int var3;
            if ((var3 = this.l.size()) != 0) {
               var2 = new RemoteDevice[var3];

               for(int var4 = 0; var4 < var3; ++var4) {
                  var2[var4] = new RemoteDevice((String)this.l.elementAt(var4));
               }
            }

            Tracer.println("retrieveDevices CACHED ret n=" + String.valueOf(var3));
            return var2;
         }
      case 1:
         Tracer.println("retrieveDevices PREKNOWN ret=NULL");
         return null;
      default:
         Tracer.println("IllegalArgumentException");
         throw new IllegalArgumentException("Unknown option(valid values: CACHED and PREKNOWN)");
      }
   }

   public boolean startInquiry(int var1, DiscoveryListener var2) throws BluetoothStateException {
      Tracer.println("startInquiry(IAC=" + String.valueOf(var1) + ")");
      if (var1 == 10390272 || var1 == 10390323 || var1 >= 10390272 && var1 <= 10390335) {
         if (var2 == null) {
            Tracer.println("NullPointerException");
            throw new NullPointerException("Discovery listener can not be null");
         } else {
            synchronized(this.g) {
               if (this.f != 0) {
                  Tracer.println("BluetoothStateException");
                  throw new BluetoothStateException("One inquiry is still active and only one can be active");
               } else {
                  synchronized(this.l) {
                     int var5 = 0;
                     boolean var6 = false;

                     do {
                        try {
                           Tracer.println("startInquiry0");
                           var5 = this.startInquiry0(var1);
                           var6 = true;
                        } catch (BluetoothStateException var9) {
                           Tracer.println("caught BluetoothStateException");
                           if (!var9.getMessage().equals("busy")) {
                              Tracer.println("BluetoothStateException");
                              throw var9;
                           }

                           Tracer.println("sleep(1000)");

                           try {
                              Thread.sleep(1000L);
                           } catch (InterruptedException var8) {
                           }
                        }
                     } while(!var6);

                     this.l.removeAllElements();
                     this.a = var5;
                     this.f = 1;
                     this.e = new DiscoveryAgent.InquiryListenerThread(this, this.a, var2);
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

   public boolean cancelInquiry(DiscoveryListener var1) {
      Tracer.println("cancelInquiry");
      if (var1 == null) {
         Tracer.println("NullPointerException");
         throw new NullPointerException("Discovery listener can not be null");
      } else {
         synchronized(this.g) {
            if (this.e != null && this.e.getListener() == var1 && this.f == 1) {
               Tracer.println("stopDiscovery0");
               this.stopDiscovery0(this.a);
               this.f = 2;
               return true;
            } else {
               return false;
            }
         }
      }
   }

   public int searchServices(int[] var1, UUID[] var2, RemoteDevice var3, DiscoveryListener var4) throws BluetoothStateException {
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
                        int var21 = var7[var5];
                        var7[var5] = var7[var6];
                        var7[var6] = var21;
                     }
                  }
               }

               String[] var13 = new String[var2.length];
               String var19 = new String("00000000000000000000000000000000");

               for(var5 = 0; var5 < var2.length; ++var5) {
                  String var18 = var2[var5].toString();
                  var13[var5] = new String(var19.substring(0, 32 - var18.length()).concat(var18));
               }

               byte[] var20 = new byte[4 + 16 * var13.length + 2 * var7.length];
               byte var15 = 0;
               int var16 = var15 + 1;
               var20[0] = (byte)(var13.length >> 8);
               ++var16;
               var20[1] = (byte)var13.length;
               ++var16;
               var20[2] = (byte)(var7.length >> 8);
               ++var16;
               var20[3] = (byte)var7.length;

               for(var5 = 0; var5 < var13.length; ++var5) {
                  for(var6 = 0; var6 < 32; var6 += 2) {
                     var20[var16++] = (byte)Integer.parseInt(var13[var5].substring(var6, var6 + 2), 16);
                  }
               }

               for(var5 = 0; var5 < var7.length; ++var5) {
                  var20[var16++] = (byte)(var7[var5] >> 8);
                  var20[var16++] = (byte)var7[var5];
               }

               synchronized(this.k) {
                  if (this.j != 0) {
                     Tracer.println("BluetoothStateException");
                     throw new BluetoothStateException("The maximum number of concurrent service search is exceeded");
                  } else {
                     int var14 = 0;
                     boolean var17 = false;

                     do {
                        try {
                           Tracer.println("startServiceSearch0");
                           var14 = this.startServiceSearch0(var20, a(var3));
                           var17 = true;
                        } catch (BluetoothStateException var11) {
                           Tracer.println("caught BluetoothStateException");
                           if (!var11.getMessage().equals("busy")) {
                              Tracer.println("BluetoothStateException");
                              throw var11;
                           }

                           Tracer.println("sleep(1000)");

                           try {
                              Thread.sleep(1000L);
                           } catch (InterruptedException var10) {
                           }
                        }
                     } while(!var17);

                     this.h = var14;
                     this.j = 1;
                     if (++d < 0) {
                        d = 1;
                     }

                     this.i = new DiscoveryAgent.ServiceSearchListenerThread(this, var3, d, this.h, var4);
                     return d;
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
      synchronized(this.k) {
         Tracer.println("cancelServiceSearch");
         if (this.i != null && this.i.getTransID() == var1 && this.j == 1) {
            Tracer.println("stopDiscovery0");
            this.stopDiscovery0(this.h);
            this.j = 2;
            return true;
         } else {
            return false;
         }
      }
   }

   public String selectService(UUID var1, int var2, boolean var3) throws BluetoothStateException {
      Tracer.println("selectService");
      if (var1 == null) {
         throw new NullPointerException("UUID can not be null");
      } else if (var2 != 0 && var2 != 1 && var2 != 2) {
         throw new IllegalArgumentException("Security is not one of the allowed values");
      } else {
         Object var4 = new Object();
         DiscoveryAgent.SelectServiceListener var13 = new DiscoveryAgent.SelectServiceListener(this, var2, var3, var4);
         synchronized(var4) {
            try {
               this.startInquiry(10390323, var13);
            } catch (BluetoothStateException var10) {
               throw new BluetoothStateException("Select service failed(problems to start inquiry)");
            }
         }

         RemoteDevice[] var14;
         if ((var14 = DiscoveryAgent.SelectServiceListener.a(var13)) == null) {
            return null;
         } else {
            UUID[] var5;
            (var5 = new UUID[1])[0] = new UUID(var1.toString(), false);

            for(int var6 = 0; var6 < var14.length; ++var6) {
               synchronized(var4) {
                  try {
                     this.searchServices((int[])null, var5, var14[var6], var13);
                  } catch (BluetoothStateException var8) {
                     throw new BluetoothStateException("Select service failed due to internal reasons");
                  }
               }

               String var12;
               if ((var12 = DiscoveryAgent.SelectServiceListener.b(var13)) != null) {
                  return new String(var12);
               }
            }

            return null;
         }
      }
   }

   private static byte[] a(RemoteDevice var0) {
      if (var0 == null) {
         throw new NullPointerException("btDev is null");
      } else {
         byte[] var1 = new byte[6];
         String var3 = var0.getBluetoothAddress();

         for(int var2 = 0; var2 < 12; var2 += 2) {
            var1[var2 / 2] = (byte)Integer.parseInt(var3.substring(var2, var2 + 2), 16);
         }

         return var1;
      }
   }

   private static native void initialize0();

   private native int startInquiry0(int var1) throws BluetoothStateException;

   private native void stopDiscovery0(int var1);

   private native int startServiceSearch0(byte[] var1, byte[] var2) throws BluetoothStateException;

   private native DiscoveryEvent getInquiryEventForHandle0(int var1);

   private native DiscoveryEvent getServiceSearchEventForHandle0(int var1);

   static DiscoveryEvent a(DiscoveryAgent var0, int var1) {
      return var0.getInquiryEventForHandle0(var1);
   }

   static Object a(DiscoveryAgent var0) {
      return var0.g;
   }

   static int b(DiscoveryAgent var0) {
      return var0.f;
   }

   static void a(DiscoveryAgent var0, int var1, String var2) {
      DiscoveryAgent var10000 = var0;
      int var10001 = var1;
      String var6 = var2;
      int var5 = var10001;
      DiscoveryAgent var3 = var10000;
      synchronized(var10000.l) {
         if (var5 == var3.a && !var3.l.contains(var6)) {
            var3.l.insertElementAt(var6, var3.l.size());
         }

      }
   }

   static DiscoveryAgent.InquiryListenerThread a(DiscoveryAgent var0, DiscoveryAgent.InquiryListenerThread var1) {
      return var0.e = null;
   }

   static int b(DiscoveryAgent var0, int var1) {
      return var0.f = 0;
   }

   static DiscoveryEvent c(DiscoveryAgent var0, int var1) {
      return var0.getServiceSearchEventForHandle0(var1);
   }

   static Object c(DiscoveryAgent var0) {
      return var0.k;
   }

   static int d(DiscoveryAgent var0) {
      return var0.j;
   }

   static DiscoveryAgent.ServiceSearchListenerThread a(DiscoveryAgent var0, DiscoveryAgent.ServiceSearchListenerThread var1) {
      return var0.i = null;
   }

   static int d(DiscoveryAgent var0, int var1) {
      return var0.j = 0;
   }

   static {
      initialize0();
   }

   private class ServiceSearchListenerThread extends Thread {
      private RemoteDevice q;
      private int transID;
      private int h;
      private DiscoveryListener b;
      private final DiscoveryAgent c;

      public ServiceSearchListenerThread(DiscoveryAgent var1, RemoteDevice var2, int var3, int var4, DiscoveryListener var5) {
         this.c = var1;
         this.q = var2;
         this.transID = var3;
         this.h = var4;
         this.b = var5;
         this.start();
      }

      public int getTransID() {
         return this.transID;
      }

      public void run() {
         boolean var1 = false;
         DiscoveryEvent var2 = null;

         while(true) {
            while((var2 = DiscoveryAgent.c(this.c, this.h)) != null) {
               if (var2.messageType == 3) {
                  try {
                     RemoteServiceRecord[] var3;
                     if ((var3 = var2.getServiceRecords(this.q)) != null) {
                        int var4 = 0;

                        for(int var5 = var3.length - 1; var4 < var5; --var5) {
                           RemoteServiceRecord var6 = var3[var4];
                           var3[var4] = var3[var5];
                           var3[var5] = var6;
                           ++var4;
                        }

                        synchronized(DiscoveryAgent.c(this.c)) {
                           if (DiscoveryAgent.d(this.c) == 1) {
                              this.b.servicesDiscovered(this.transID, var3);
                              var1 = true;
                           }
                        }
                     }
                  } catch (Exception var10) {
                  }
               } else if (var2.messageType == 4) {
                  Tracer.println("calling list.serviceSearchCompleted");
                  synchronized(DiscoveryAgent.c(this.c)) {
                     DiscoveryAgent.a(this.c, (DiscoveryAgent.ServiceSearchListenerThread)null);
                     if (DiscoveryAgent.d(this.c) != 2 && var2.respCode != 2) {
                        if (var2.respCode == 3) {
                           Tracer.println("SERVICE_SEARCH_ERROR");
                           DiscoveryAgent.d(this.c, 0);
                           this.b.serviceSearchCompleted(this.transID, var2.respCode);
                        } else if (var2.respCode == 6) {
                           Tracer.println("SERVICE_SEARCH_DEVICE_NOT_REACHABLE");
                           DiscoveryAgent.d(this.c, 0);
                           this.b.serviceSearchCompleted(this.transID, var2.respCode);
                        } else if (var1) {
                           Tracer.println("SERVICE_SEARCH_COMPLETED");
                           DiscoveryAgent.d(this.c, 0);
                           this.b.serviceSearchCompleted(this.transID, 1);
                        } else {
                           Tracer.println("SERVICE_SEARCH_NO_RECORDS");
                           DiscoveryAgent.d(this.c, 0);
                           this.b.serviceSearchCompleted(this.transID, 4);
                        }
                     } else {
                        Tracer.println("SERVICE_SEARCH_TERMINATED");
                        DiscoveryAgent.d(this.c, 0);
                        this.b.serviceSearchCompleted(this.transID, 2);
                     }

                     return;
                  }
               }
            }

            try {
               sleep(50L);
            } catch (InterruptedException var8) {
            }
         }
      }
   }

   private class InquiryListenerThread extends Thread {
      private int a;
      private DiscoveryListener b;
      private final DiscoveryAgent c;

      public InquiryListenerThread(DiscoveryAgent var1, int var2, DiscoveryListener var3) {
         this.c = var1;
         this.a = var2;
         this.b = var3;
         this.start();
      }

      public DiscoveryListener getListener() {
         return this.b;
      }

      public void run() {
         DiscoveryEvent var1 = null;

         while(true) {
            while((var1 = DiscoveryAgent.a(this.c, this.a)) != null) {
               if (var1.messageType == 1) {
                  String var2;
                  if ((var2 = var1.getAddress()) != null) {
                     synchronized(DiscoveryAgent.a(this.c)) {
                        if (DiscoveryAgent.b(this.c) == 1) {
                           DiscoveryAgent.a(this.c, this.a, var2);
                           this.b.deviceDiscovered(new RemoteDevice(var2), new DeviceClass(var1.cod));
                        }
                     }
                  }
               } else if (var1.messageType == 2) {
                  synchronized(DiscoveryAgent.a(this.c)) {
                     DiscoveryAgent.a(this.c, (DiscoveryAgent.InquiryListenerThread)null);
                     if (DiscoveryAgent.b(this.c) != 2 && var1.discType != 5) {
                        if (var1.discType == 7) {
                           DiscoveryAgent.b(this.c, 0);
                           Tracer.println("INQUIRY_ERROR");
                           this.b.inquiryCompleted(7);
                        } else {
                           DiscoveryAgent.b(this.c, 0);
                           Tracer.println("INQUIRY_COMPLETED");
                           this.b.inquiryCompleted(0);
                        }
                     } else {
                        DiscoveryAgent.b(this.c, 0);
                        Tracer.println("INQUIRY_TERMINATED");
                        this.b.inquiryCompleted(5);
                     }

                     return;
                  }
               }
            }

            try {
               sleep(500L);
            } catch (InterruptedException var5) {
            }
         }
      }
   }

   private class SelectServiceListener implements DiscoveryListener {
      private int u;
      private boolean v;
      private String w;
      private Object x;
      private Vector y;
      private Vector z;

      private String getURL() {
         return this.w;
      }

      public void deviceDiscovered(RemoteDevice var1, DeviceClass var2) {
         if ((var2.getMajorDeviceClass() & 512) != 0) {
            this.y.addElement(var1);
         } else {
            this.z.addElement(var1);
         }
      }

      public void inquiryCompleted(int var1) {
         synchronized(this.x) {
            Tracer.println("inquiryCompleted()=" + String.valueOf(var1));
         }
      }

      private RemoteDevice[] getDiscoveredDevices() {
         RemoteDevice[] var1 = null;
         int var2;
         if ((var2 = this.y.size() + this.z.size()) != 0) {
            var1 = new RemoteDevice[var2];

            for(var2 = 0; var2 < this.y.size(); ++var2) {
               var1[var2] = (RemoteDevice)this.y.elementAt(var2);
            }

            for(var2 = 0; var2 < this.z.size(); ++var2) {
               var1[this.y.size() + var2] = (RemoteDevice)this.z.elementAt(var2);
            }
         }

         return var1;
      }

      public void serviceSearchCompleted(int var1, int var2) {
         synchronized(this.x) {
            Tracer.println("serviceSearchCompleted()=" + String.valueOf(var2));
         }
      }

      public void servicesDiscovered(int var1, ServiceRecord[] var2) {
         this.w = var2[0].getConnectionURL(this.u, this.v);
      }

      SelectServiceListener(DiscoveryAgent var1, int var2, boolean var3, Object var4, Object var5) {
         this.u = var2;
         this.v = var3;
         this.x = var4;
         this.w = null;
         this.y = new Vector();
         this.z = new Vector();
      }

      static RemoteDevice[] a(DiscoveryAgent.SelectServiceListener var0) {
         return var0.getDiscoveredDevices();
      }

      static String b(DiscoveryAgent.SelectServiceListener var0) {
         return var0.getURL();
      }
   }
}
