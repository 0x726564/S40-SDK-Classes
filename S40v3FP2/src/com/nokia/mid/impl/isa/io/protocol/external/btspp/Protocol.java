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
   private static final Object openLock = new Object();
   protected int mode;
   protected boolean isClosed = false;
   private static byte bluetoothState;

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      Tracer.println("Connector.open called for btspp");
      if (var2 != 1 && var2 != 2 && var2 != 3) {
         Tracer.println("IllegalArgumentException");
         throw new IllegalArgumentException("illegal access mode: " + var2);
      } else {
         URLParser var4 = CommonBluetooth.parseConnectionString(var1, "btspp");
         if (var4.getHostValue().equals("localhost")) {
            Tracer.println("SERVER call checkPermission(false)");
            CommonBluetooth.checkPermission(false);
            String var5 = new String("btspp:") + var1;
            byte[] var16 = var5.getBytes();
            ProtocolNotifier var7 = new ProtocolNotifier(var5);
            var7.setMode(var2);
            byte var8 = var4.getOptionsValue();
            var8 = (byte)(var8 | 2);
            var7.setOptions(var8);
            Tracer.println("connectorOpenMT0");
            this.connectorOpenMT0(var7, var16);
            if (!var7.isNotifierForPushService()) {
               Tracer.println("NON-PUSH activateMedia");
               bluetoothState = CommonBluetooth.activateMedia();
               if (1 == bluetoothState) {
                  Tracer.println("IOException");
                  throw new IOException("BT System is not active");
               }

               this.createSPPService(var7.getLocalServiceRecord(), var4.getUUIDValue(), var4.getNameValue());

               try {
                  var7.checkAndRegisterService();
               } catch (Exception var12) {
                  Tracer.println("BluetoothStateException");
                  throw new BluetoothStateException("Service could not be registered");
               }
            } else {
               try {
                  Tracer.println("PUSH");
                  var7.createLocalPushServiceRecord();
               } catch (Exception var11) {
                  Tracer.println("unexpected BluetoothStateException");
                  throw new BluetoothStateException("Invalid PUSH service!");
               }
            }

            Tracer.println("returning protocolNotifier");
            return var7;
         } else {
            Tracer.println("CLIENT activateMedia");
            bluetoothState = CommonBluetooth.activateMedia();
            if (1 == bluetoothState) {
               Tracer.println("IOException");
               throw new IOException("BT System is not active");
            } else {
               Tracer.println("checkPermission(true)");
               CommonBluetooth.checkPermission(true);

               try {
                  Tracer.println("sleep(1000)");
                  Thread.sleep(1000L);
               } catch (InterruptedException var15) {
               }

               synchronized(openLock) {
                  ProtocolConnection var6 = new ProtocolConnection(true, var2);

                  try {
                     Tracer.println("open0");
                     this.open0(var6, CommonBluetooth.getByteAddress(var4.getHostValue()), var4.getOptionsValue(), var4.getChannelORPsmValue());
                  } catch (ProtocolExceptionOfType var13) {
                     Tracer.println("catched ProtocolExceptionOfType");
                     var6 = null;
                     var13.handleStatus();
                  }

                  Tracer.println("returning protocolConnection");
                  return var6;
               }
            }
         }
      }
   }

   public boolean isClosed() {
      Tracer.println("isClosed=" + String.valueOf(this.isClosed));
      return this.isClosed;
   }

   private void createSPPService(LocalServiceRecord var1, UUID var2, String var3) {
      Tracer.println("createSPPService");
      DataElement var4 = new DataElement(48);
      DataElement var5 = new DataElement(24, var2);
      var4.addElement(var5);
      DataElement var6 = new DataElement(24, new UUID(4353L));
      var4.addElement(var6);
      var1.setAttributeValue(1, var4);
      DataElement var7 = new DataElement(48);
      DataElement var8 = new DataElement(48);
      DataElement var9 = new DataElement(24, new UUID(256L));
      var8.addElement(var9);
      var7.addElement(var8);
      DataElement var10 = new DataElement(48);
      DataElement var11 = new DataElement(24, new UUID(3L));
      var10.addElement(var11);
      DataElement var12 = new DataElement(8, 2L);
      var10.addElement(var12);
      var7.addElement(var10);
      var1.setAttributeValue(4, var7);
      if (var3 != null) {
         DataElement var13 = new DataElement(32, var3);
         var1.setAttributeValue(256, var13);
      }

   }

   protected void setMode(int var1) {
      Tracer.println("setMode(" + String.valueOf(var1) + ")");
      this.mode = var1;
   }

   private static native void initialize0();

   private native void open0(ProtocolConnection var1, byte[] var2, byte var3, int var4) throws ProtocolExceptionOfType;

   private native void connectorOpenMT0(ProtocolNotifier var1, byte[] var2) throws IOException;

   static {
      initialize0();
   }
}
