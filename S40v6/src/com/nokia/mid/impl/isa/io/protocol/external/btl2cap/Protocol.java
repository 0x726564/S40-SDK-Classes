package com.nokia.mid.impl.isa.io.protocol.external.btl2cap;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import com.nokia.mid.impl.isa.bluetooth.LocalServiceRecord;
import com.nokia.mid.impl.isa.bluetooth.ProtocolExceptionOfType;
import com.nokia.mid.impl.isa.bluetooth.URLParser;
import com.nokia.mid.impl.isa.util.SharedObjects;
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
   private static final Object sharedLock = SharedObjects.getLock("com.nokia.mid.impl.isa.io.protocol.external.btl2cap.Protocol.sharedLock");
   private static boolean doneInit = false;

   private static void performInit() {
      if (!doneInit) {
         synchronized(sharedLock) {
            initialize0();
            doneInit = true;
         }
      }
   }

   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      performInit();
      if (mode != 1 && mode != 2 && mode != 3) {
         throw new IllegalArgumentException("illegal access mode: " + mode);
      } else {
         URLParser urlParser = CommonBluetooth.parseConnectionString(name, "btl2cap");
         if (urlParser.getHostValue().equals("localhost")) {
            CommonBluetooth.checkPermission(false);
            String url = new String("btl2cap:") + name;
            byte[] url_as_byte_stream = url.getBytes();
            ProtocolNotifier protocolNotifier = new ProtocolNotifier(url);
            protocolNotifier.setMode(mode);
            byte serverOptions = urlParser.getOptionsValue();
            serverOptions = (byte)(serverOptions | 2);
            protocolNotifier.setOptions(serverOptions);
            this.connectorOpenMT0(protocolNotifier, url_as_byte_stream);
            if (!protocolNotifier.isNotifierForPushService()) {
               bluetoothState = CommonBluetooth.activateMedia();
               if (1 == bluetoothState) {
                  throw new IOException("BT System is not active");
               }

               if (urlParser.getTransmitMTUValue() == -1) {
                  protocolNotifier.setTransmitMTU(48);
               } else {
                  protocolNotifier.setTransmitMTU(urlParser.getTransmitMTUValue());
                  protocolNotifier.setTransmitMTUSpecifiedInConnURL();
               }

               protocolNotifier.setReceiveMTU(urlParser.getReceiveMTUValue() == -1 ? 672 : urlParser.getReceiveMTUValue());
               this.createL2CAPService(protocolNotifier.getLocalServiceRecord(), urlParser.getUUIDValue(), urlParser.getNameValue());

               try {
                  protocolNotifier.checkAndRegisterService();
               } catch (Exception var12) {
                  throw new BluetoothStateException("Service could not be registered");
               }
            } else {
               try {
                  protocolNotifier.createLocalPushServiceRecord();
               } catch (Exception var11) {
                  throw new BluetoothStateException("Invalid PUSH service!");
               }
            }

            return protocolNotifier;
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
                  ProtocolConnection protocolConnection = new ProtocolConnection(true, mode);

                  try {
                     int transmitMTUValue = urlParser.getTransmitMTUValue() == -1 ? 48 : urlParser.getTransmitMTUValue();
                     int receiveMTUValue = urlParser.getReceiveMTUValue() == -1 ? 672 : urlParser.getReceiveMTUValue();
                     this.open0(protocolConnection, CommonBluetooth.getByteAddress(urlParser.getHostValue()), urlParser.getOptionsValue(), urlParser.getChannelORPsmValue(), receiveMTUValue, transmitMTUValue);
                     if (receiveMTUValue < protocolConnection.getReceiveMTU()) {
                        throw new Error("BT stack failed by allowing too big receiveMTU");
                     }
                  } catch (ProtocolExceptionOfType var14) {
                     protocolConnection = null;
                     var14.handleStatus();
                  }

                  if (urlParser.getTransmitMTUValue() != -1) {
                     if (urlParser.getTransmitMTUValue() > protocolConnection.getTransmitMTU()) {
                        try {
                           protocolConnection.close();
                           protocolConnection = null;
                        } catch (Exception var13) {
                        }

                        throw new BluetoothConnectionException(6, "Connection could not be established(negotiated transmit MTU is too small)");
                     }

                     protocolConnection.setTransmitMTU(urlParser.getTransmitMTUValue());
                  }

                  return protocolConnection;
               }
            }
         }
      }
   }

   public boolean isClosed() {
      return this.isClosed;
   }

   private void createL2CAPService(LocalServiceRecord localService, UUID serviceClassID, String serviceName) {
      DataElement serviceClassIDListItem = new DataElement(48);
      DataElement serviceClass0 = new DataElement(24, serviceClassID);
      serviceClassIDListItem.addElement(serviceClass0);
      localService.setAttributeValue(1, serviceClassIDListItem);
      DataElement protocolDescriptorListItem = new DataElement(48);
      DataElement protocol0 = new DataElement(48);
      DataElement l2cap = new DataElement(24, new UUID(256L));
      protocol0.addElement(l2cap);
      DataElement protocolSpecificParameter0 = new DataElement(9, 24577L);
      protocol0.addElement(protocolSpecificParameter0);
      protocolDescriptorListItem.addElement(protocol0);
      localService.setAttributeValue(4, protocolDescriptorListItem);
      if (serviceName != null) {
         DataElement serviceNameItem = new DataElement(32, serviceName);
         localService.setAttributeValue(256, serviceNameItem);
      }

   }

   protected void setMode(int mode) {
      this.mode = mode;
   }

   protected void setReceiveMTU(int size) {
      this.receiveMTU = size;
   }

   protected void setTransmitMTU(int size) {
      this.transmitMTU = size;
   }

   private static native void initialize0();

   private native void open0(ProtocolConnection var1, byte[] var2, byte var3, int var4, int var5, int var6) throws ProtocolExceptionOfType;

   private native void connectorOpenMT0(ProtocolNotifier var1, byte[] var2) throws IOException;
}
