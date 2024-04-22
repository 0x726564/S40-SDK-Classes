package com.nokia.mid.impl.isa.io.protocol.external.btl2cap;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import com.nokia.mid.impl.isa.bluetooth.LocalServiceRecord;
import com.nokia.mid.impl.isa.bluetooth.ProtocolExceptionOfType;
import com.nokia.mid.impl.isa.bluetooth.URLParser;
import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;

public class Protocol implements ConnectionBaseInterface {
   public static final int DEFAULT_TRANSMIT_MTU = 48;
   private static final Object kL = new Object();
   protected int mode;
   protected boolean isClosed = false;
   protected int transmitMTU;
   protected int receiveMTU;
   private static byte lE;

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      if (var2 != 1 && var2 != 2 && var2 != 3) {
         throw new IllegalArgumentException("illegal access mode: " + var2);
      } else {
         URLParser var16;
         if ((var16 = CommonBluetooth.parseConnectionString(var1, "btl2cap")).getHostValue().equals("localhost")) {
            CommonBluetooth.checkPermission(false);
            byte[] var17 = (var1 = new String("btl2cap:") + var1).getBytes();
            ProtocolNotifier var18;
            (var18 = new ProtocolNotifier(var1)).setMode(var2);
            byte var21 = (byte)(var16.getOptionsValue() | 2);
            var18.setOptions(var21);
            this.connectorOpenMT0(var18, var17);
            if (!var18.isNotifierForPushService()) {
               lE = CommonBluetooth.activateMedia();
               if (1 == lE) {
                  throw new IOException("BT System is not active");
               }

               if (var16.getTransmitMTUValue() == -1) {
                  var18.setTransmitMTU(48);
               } else {
                  var18.setTransmitMTU(var16.getTransmitMTUValue());
                  var18.setTransmitMTUSpecifiedInConnURL();
               }

               var18.setReceiveMTU(var16.getReceiveMTUValue() == -1 ? 672 : var16.getReceiveMTUValue());
               LocalServiceRecord var10000 = var18.getLocalServiceRecord();
               UUID var10001 = var16.getUUIDValue();
               String var22 = var16.getNameValue();
               UUID var19 = var10001;
               LocalServiceRecord var15 = var10000;
               DataElement var7 = new DataElement(48);
               DataElement var20 = new DataElement(24, var19);
               var7.addElement(var20);
               var15.setAttributeValue(1, var7);
               var20 = new DataElement(48);
               var7 = new DataElement(48);
               DataElement var8 = new DataElement(24, new UUID(256L));
               var7.addElement(var8);
               var8 = new DataElement(9, 24577L);
               var7.addElement(var8);
               var20.addElement(var7);
               var15.setAttributeValue(4, var20);
               if (var22 != null) {
                  var20 = new DataElement(32, var22);
                  var15.setAttributeValue(256, var20);
               }

               try {
                  var18.checkAndRegisterService();
               } catch (Exception var10) {
                  throw new BluetoothStateException("Service could not be registered");
               }
            } else {
               try {
                  var18.createLocalPushServiceRecord();
               } catch (Exception var9) {
                  throw new BluetoothStateException("Invalid PUSH service!");
               }
            }

            return var18;
         } else {
            lE = CommonBluetooth.activateMedia();
            if (1 == lE) {
               throw new IOException("BT System is not active");
            } else {
               CommonBluetooth.checkPermission(true);

               try {
                  Thread.sleep(1000L);
               } catch (InterruptedException var14) {
               }

               synchronized(kL) {
                  ProtocolConnection var4 = new ProtocolConnection(true, var2);

                  try {
                     int var5 = var16.getTransmitMTUValue() == -1 ? 48 : var16.getTransmitMTUValue();
                     int var6 = var16.getReceiveMTUValue() == -1 ? 672 : var16.getReceiveMTUValue();
                     this.open0(var4, CommonBluetooth.getByteAddress(var16.getHostValue()), var16.getOptionsValue(), var16.getChannelORPsmValue(), var6, var5);
                     if (var6 < var4.getReceiveMTU()) {
                        throw new Error("BT stack failed by allowing too big receiveMTU");
                     }
                  } catch (ProtocolExceptionOfType var12) {
                     var4 = null;
                     var12.handleStatus();
                  }

                  if (var16.getTransmitMTUValue() != -1) {
                     if (var16.getTransmitMTUValue() > var4.getTransmitMTU()) {
                        try {
                           var4.close();
                        } catch (Exception var11) {
                        }

                        throw new BluetoothConnectionException(6, "Connection could not be established(negotiated transmit MTU is too small)");
                     }

                     var4.setTransmitMTU(var16.getTransmitMTUValue());
                  }

                  return var4;
               }
            }
         }
      }
   }

   public boolean isClosed() {
      return this.isClosed;
   }

   protected void setMode(int var1) {
      this.mode = var1;
   }

   protected void setReceiveMTU(int var1) {
      this.receiveMTU = var1;
   }

   protected void setTransmitMTU(int var1) {
      this.transmitMTU = var1;
   }

   private static native void initialize0();

   private native void open0(ProtocolConnection var1, byte[] var2, byte var3, int var4, int var5, int var6) throws ProtocolExceptionOfType;

   private native void connectorOpenMT0(ProtocolNotifier var1, byte[] var2) throws IOException;

   static {
      initialize0();
   }
}
