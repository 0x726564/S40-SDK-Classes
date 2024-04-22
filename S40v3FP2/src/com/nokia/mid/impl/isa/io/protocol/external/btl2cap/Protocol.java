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
   private static final Object openLock = new Object();
   protected int mode;
   protected boolean isClosed = false;
   protected int transmitMTU;
   protected int receiveMTU;
   private static byte bluetoothState;

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      if (var2 != 1 && var2 != 2 && var2 != 3) {
         throw new IllegalArgumentException("illegal access mode: " + var2);
      } else {
         URLParser var4 = CommonBluetooth.parseConnectionString(var1, "btl2cap");
         if (var4.getHostValue().equals("localhost")) {
            CommonBluetooth.checkPermission(false);
            String var5 = new String("btl2cap:") + var1;
            byte[] var18 = var5.getBytes();
            ProtocolNotifier var17 = new ProtocolNotifier(var5);
            var17.setMode(var2);
            byte var19 = var4.getOptionsValue();
            var19 = (byte)(var19 | 2);
            var17.setOptions(var19);
            this.connectorOpenMT0(var17, var18);
            if (!var17.isNotifierForPushService()) {
               bluetoothState = CommonBluetooth.activateMedia();
               if (1 == bluetoothState) {
                  throw new IOException("BT System is not active");
               }

               if (var4.getTransmitMTUValue() == -1) {
                  var17.setTransmitMTU(48);
               } else {
                  var17.setTransmitMTU(var4.getTransmitMTUValue());
                  var17.setTransmitMTUSpecifiedInConnURL();
               }

               var17.setReceiveMTU(var4.getReceiveMTUValue() == -1 ? 672 : var4.getReceiveMTUValue());
               this.createL2CAPService(var17.getLocalServiceRecord(), var4.getUUIDValue(), var4.getNameValue());

               try {
                  var17.checkAndRegisterService();
               } catch (Exception var12) {
                  throw new BluetoothStateException("Service could not be registered");
               }
            } else {
               try {
                  var17.createLocalPushServiceRecord();
               } catch (Exception var11) {
                  throw new BluetoothStateException("Invalid PUSH service!");
               }
            }

            return var17;
         } else {
            bluetoothState = CommonBluetooth.activateMedia();
            if (1 == bluetoothState) {
               throw new IOException("BT System is not active");
            } else {
               CommonBluetooth.checkPermission(true);

               try {
                  Thread.sleep(1000L);
               } catch (InterruptedException var16) {
               }

               synchronized(openLock) {
                  ProtocolConnection var6 = new ProtocolConnection(true, var2);

                  try {
                     int var7 = var4.getTransmitMTUValue() == -1 ? 48 : var4.getTransmitMTUValue();
                     int var8 = var4.getReceiveMTUValue() == -1 ? 672 : var4.getReceiveMTUValue();
                     this.open0(var6, CommonBluetooth.getByteAddress(var4.getHostValue()), var4.getOptionsValue(), var4.getChannelORPsmValue(), var8, var7);
                     if (var8 < var6.getReceiveMTU()) {
                        throw new Error("BT stack failed by allowing too big receiveMTU");
                     }
                  } catch (ProtocolExceptionOfType var14) {
                     var6 = null;
                     var14.handleStatus();
                  }

                  if (var4.getTransmitMTUValue() != -1) {
                     if (var4.getTransmitMTUValue() > var6.getTransmitMTU()) {
                        try {
                           var6.close();
                           var6 = null;
                        } catch (Exception var13) {
                        }

                        throw new BluetoothConnectionException(6, "Connection could not be established(negotiated transmit MTU is too small)");
                     }

                     var6.setTransmitMTU(var4.getTransmitMTUValue());
                  }

                  return var6;
               }
            }
         }
      }
   }

   public boolean isClosed() {
      return this.isClosed;
   }

   private void createL2CAPService(LocalServiceRecord var1, UUID var2, String var3) {
      DataElement var4 = new DataElement(48);
      DataElement var5 = new DataElement(24, var2);
      var4.addElement(var5);
      var1.setAttributeValue(1, var4);
      DataElement var6 = new DataElement(48);
      DataElement var7 = new DataElement(48);
      DataElement var8 = new DataElement(24, new UUID(256L));
      var7.addElement(var8);
      DataElement var9 = new DataElement(9, 24577L);
      var7.addElement(var9);
      var6.addElement(var7);
      var1.setAttributeValue(4, var6);
      if (var3 != null) {
         DataElement var10 = new DataElement(32, var3);
         var1.setAttributeValue(256, var10);
      }

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
