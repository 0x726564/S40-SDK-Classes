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
   private static final Object kL = new Object();
   protected int mode;
   protected boolean isClosed = false;
   private static byte lE;

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      Tracer.println("Connector.open called for btspp");
      if (var2 != 1 && var2 != 2 && var2 != 3) {
         Tracer.println("IllegalArgumentException");
         throw new IllegalArgumentException("illegal access mode: " + var2);
      } else {
         URLParser var16;
         if ((var16 = CommonBluetooth.parseConnectionString(var1, "btspp")).getHostValue().equals("localhost")) {
            Tracer.println("SERVER call checkPermission(false)");
            CommonBluetooth.checkPermission(false);
            byte[] var17 = (var1 = new String("btspp:") + var1).getBytes();
            ProtocolNotifier var5;
            (var5 = new ProtocolNotifier(var1)).setMode(var2);
            byte var14 = (byte)(var16.getOptionsValue() | 2);
            var5.setOptions(var14);
            Tracer.println("connectorOpenMT0");
            this.connectorOpenMT0(var5, var17);
            if (!var5.isNotifierForPushService()) {
               Tracer.println("NON-PUSH activateMedia");
               lE = CommonBluetooth.activateMedia();
               if (1 == lE) {
                  Tracer.println("IOException");
                  throw new IOException("BT System is not active");
               }

               LocalServiceRecord var10000 = var5.getLocalServiceRecord();
               UUID var10001 = var16.getUUIDValue();
               String var6 = var16.getNameValue();
               UUID var18 = var10001;
               LocalServiceRecord var15 = var10000;
               Tracer.println("createSPPService");
               DataElement var7 = new DataElement(48);
               DataElement var19 = new DataElement(24, var18);
               var7.addElement(var19);
               var19 = new DataElement(24, new UUID(4353L));
               var7.addElement(var19);
               var15.setAttributeValue(1, var7);
               var19 = new DataElement(48);
               var7 = new DataElement(48);
               DataElement var8 = new DataElement(24, new UUID(256L));
               var7.addElement(var8);
               var19.addElement(var7);
               var7 = new DataElement(48);
               var8 = new DataElement(24, new UUID(3L));
               var7.addElement(var8);
               var8 = new DataElement(8, 2L);
               var7.addElement(var8);
               var19.addElement(var7);
               var15.setAttributeValue(4, var19);
               if (var6 != null) {
                  var19 = new DataElement(32, var6);
                  var15.setAttributeValue(256, var19);
               }

               try {
                  var5.checkAndRegisterService();
               } catch (Exception var10) {
                  Tracer.println("BluetoothStateException");
                  throw new BluetoothStateException("Service could not be registered");
               }
            } else {
               try {
                  Tracer.println("PUSH");
                  var5.createLocalPushServiceRecord();
               } catch (Exception var9) {
                  Tracer.println("unexpected BluetoothStateException");
                  throw new BluetoothStateException("Invalid PUSH service!");
               }
            }

            Tracer.println("returning protocolNotifier");
            return var5;
         } else {
            Tracer.println("CLIENT activateMedia");
            lE = CommonBluetooth.activateMedia();
            if (1 == lE) {
               Tracer.println("IOException");
               throw new IOException("BT System is not active");
            } else {
               Tracer.println("checkPermission(true)");
               CommonBluetooth.checkPermission(true);

               try {
                  Tracer.println("sleep(1000)");
                  Thread.sleep(1000L);
               } catch (InterruptedException var13) {
               }

               synchronized(kL) {
                  ProtocolConnection var4 = new ProtocolConnection(true, var2);

                  try {
                     Tracer.println("open0");
                     this.open0(var4, CommonBluetooth.getByteAddress(var16.getHostValue()), var16.getOptionsValue(), var16.getChannelORPsmValue());
                  } catch (ProtocolExceptionOfType var11) {
                     Tracer.println("catched ProtocolExceptionOfType");
                     var4 = null;
                     var11.handleStatus();
                  }

                  Tracer.println("returning protocolConnection");
                  return var4;
               }
            }
         }
      }
   }

   public boolean isClosed() {
      Tracer.println("isClosed=" + String.valueOf(this.isClosed));
      return this.isClosed;
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
