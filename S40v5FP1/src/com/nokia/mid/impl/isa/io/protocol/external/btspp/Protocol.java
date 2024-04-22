package com.nokia.mid.impl.isa.io.protocol.external.btspp;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import com.nokia.mid.impl.isa.bluetooth.LocalServiceRecord;
import com.nokia.mid.impl.isa.bluetooth.ProtocolExceptionOfType;
import com.nokia.mid.impl.isa.bluetooth.Tracer;
import com.nokia.mid.impl.isa.bluetooth.URLParser;
import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;

public class Protocol implements ConnectionBaseInterface {
   public static final String goepProtocol = "?nokia_goep_protocol=true";
   private static final Object openLock = new Object();
   protected int mode;
   protected boolean isClosed = false;
   private static byte bluetoothState;

   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      boolean goep = false;
      if (name.endsWith("?nokia_goep_protocol=true")) {
         goep = true;
         name = name.substring(0, name.length() - "?nokia_goep_protocol=true".length());
      }

      Tracer.println("Connector.open called for btspp");
      Tracer.println("name is " + name);
      if (mode != 1 && mode != 2 && mode != 3) {
         Tracer.println("IllegalArgumentException");
         throw new IllegalArgumentException("illegal access mode: " + mode);
      } else {
         URLParser urlParser = CommonBluetooth.parseConnectionString(name, "btspp");
         if (urlParser.getHostValue().equals("localhost")) {
            Tracer.println("SERVER call checkPermission(false)");
            CommonBluetooth.checkPermission(false, goep);
            String url = new String("btspp:") + name;
            byte[] url_as_byte_stream = url.getBytes();
            ProtocolNotifier protocolNotifier = new ProtocolNotifier(url);
            protocolNotifier.setMode(mode);
            byte serverOptions = urlParser.getOptionsValue();
            serverOptions = (byte)(serverOptions | 2);
            protocolNotifier.setOptions(serverOptions);
            Tracer.println("connectorOpenMT0");
            this.connectorOpenMT0(protocolNotifier, url_as_byte_stream);
            if (!protocolNotifier.isNotifierForPushService()) {
               Tracer.println("NON-PUSH activateMedia");
               bluetoothState = CommonBluetooth.activateMedia();
               if (1 == bluetoothState) {
                  Tracer.println("IOException");
                  throw new IOException("BT System is not active");
               }

               this.createSPPService(protocolNotifier.getLocalServiceRecord(), urlParser.getUUIDValue(), urlParser.getNameValue());

               try {
                  protocolNotifier.checkAndRegisterService();
               } catch (Exception var13) {
                  Tracer.println("BluetoothStateException");
                  throw new BluetoothStateException("Service could not be registered");
               }
            } else {
               try {
                  Tracer.println("PUSH");
                  protocolNotifier.createLocalPushServiceRecord();
               } catch (Exception var12) {
                  Tracer.println("unexpected BluetoothStateException");
                  throw new BluetoothStateException("Invalid PUSH service!");
               }
            }

            Tracer.println("returning protocolNotifier");
            return protocolNotifier;
         } else {
            Tracer.println("CLIENT activateMedia");
            bluetoothState = CommonBluetooth.activateMedia();
            if (1 == bluetoothState) {
               Tracer.println("IOException");
               throw new IOException("BT System is not active");
            } else {
               Tracer.println("checkPermission(true)");
               CommonBluetooth.checkPermission(true, goep);

               try {
                  Tracer.println("sleep(1000)");
                  Thread.sleep(1000L);
               } catch (InterruptedException var16) {
               }

               synchronized(openLock) {
                  ProtocolConnection protocolConnection = new ProtocolConnection(true, mode);

                  try {
                     Tracer.println("open0");
                     this.open0(protocolConnection, CommonBluetooth.getByteAddress(urlParser.getHostValue()), urlParser.getOptionsValue(), urlParser.getChannelORPsmValue());
                  } catch (ProtocolExceptionOfType var14) {
                     Tracer.println("catched ProtocolExceptionOfType");
                     protocolConnection = null;
                     var14.handleStatus();
                  }

                  Tracer.println("returning protocolConnection");
                  return protocolConnection;
               }
            }
         }
      }
   }

   public boolean isClosed() {
      Tracer.println("isClosed=" + String.valueOf(this.isClosed));
      return this.isClosed;
   }

   private void createSPPService(LocalServiceRecord localService, UUID serviceClassID, String serviceName) {
      Tracer.println("createSPPService");
      DataElement serviceClassIDListItem = new DataElement(48);
      DataElement serviceClass0 = new DataElement(24, serviceClassID);
      serviceClassIDListItem.addElement(serviceClass0);
      DataElement serviceClass1 = new DataElement(24, new UUID(4353L));
      serviceClassIDListItem.addElement(serviceClass1);
      localService.setAttributeValue(1, serviceClassIDListItem);
      DataElement protocolDescriptorListItem = new DataElement(48);
      DataElement protocol0 = new DataElement(48);
      DataElement l2cap = new DataElement(24, new UUID(256L));
      protocol0.addElement(l2cap);
      protocolDescriptorListItem.addElement(protocol0);
      DataElement protocol1 = new DataElement(48);
      DataElement rfcomm = new DataElement(24, new UUID(3L));
      protocol1.addElement(rfcomm);
      DataElement protocolSpecificParameter0 = new DataElement(8, 2L);
      protocol1.addElement(protocolSpecificParameter0);
      protocolDescriptorListItem.addElement(protocol1);
      localService.setAttributeValue(4, protocolDescriptorListItem);
      if (serviceName != null) {
         DataElement serviceNameItem = new DataElement(32, serviceName);
         localService.setAttributeValue(256, serviceNameItem);
      }

   }

   protected void setMode(int mode) {
      Tracer.println("setMode(" + String.valueOf(mode) + ")");
      this.mode = mode;
   }

   private static native void initialize0();

   private native void open0(ProtocolConnection var1, byte[] var2, byte var3, int var4) throws ProtocolExceptionOfType;

   private native void connectorOpenMT0(ProtocolNotifier var1, byte[] var2) throws IOException;

   static {
      initialize0();
   }
}
