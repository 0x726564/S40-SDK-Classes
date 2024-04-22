package com.nokia.mid.impl.isa.bluetooth;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;

public class LocalServiceRecord extends SerializedCommonServiceRecord {
   private int cs = 0;
   private final CommonNotifier ct;
   private Object cu = new Object();

   private LocalServiceRecord() {
      throw new RuntimeException("wrong constructor called");
   }

   public LocalServiceRecord(CommonNotifier var1) {
      if (var1 == null) {
         throw new NullPointerException("got no notifier");
      } else {
         this.ct = var1;
      }
   }

   public Object getLockObject() {
      return this.cu;
   }

   public void setLockObject(Object var1) {
      if (var1 == null) {
         throw new NullPointerException("Lock object is null");
      } else {
         this.cu = var1;
      }
   }

   public CommonNotifier getConnectionNotifier() {
      return this.ct;
   }

   public RemoteDevice getHostDevice() {
      return null;
   }

   public boolean populateRecord(int[] var1) {
      throw new RuntimeException("This ServiceRecord describes a local service");
   }

   public String getConnectionURL(int var1, boolean var2) {
      String var3;
      try {
         var3 = LocalDevice.getLocalDevice().getBluetoothAddress();
      } catch (BluetoothStateException var7) {
         return null;
      }

      long var4 = this.ct.getChannelID();
      String var6 = this.ct.getProtocol();
      return this.createConnectionURL(var3, var1, var2, var6, var4);
   }

   public void setDeviceServiceClasses(int var1) {
      if (var1 != 0 && (var1 & -16760833) == 0) {
         this.cs = var1;
      } else {
         throw new IllegalArgumentException("Invalid (major) service class(es)");
      }
   }

   public int getDeviceServiceClasses() {
      return this.cs;
   }

   public void initRecord(byte[] var1) throws NullPointerException, IllegalArgumentException {
      if (null == var1) {
         throw new NullPointerException("serviceRecordStream is null");
      } else {
         this.parserSetAttributes(var1);
      }
   }
}
