package com.nokia.mid.impl.isa.bluetooth;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;

public class LocalServiceRecord extends SerializedCommonServiceRecord {
   private int deviceServiceClasses = 0;
   private final CommonNotifier connectionNotifier;
   private Object lockObject = new Object();

   private LocalServiceRecord() {
      throw new RuntimeException("wrong constructor called");
   }

   public LocalServiceRecord(CommonNotifier var1) {
      if (var1 == null) {
         throw new NullPointerException("got no notifier");
      } else {
         this.connectionNotifier = var1;
      }
   }

   public Object getLockObject() {
      return this.lockObject;
   }

   public void setLockObject(Object var1) {
      if (var1 == null) {
         throw new NullPointerException("Lock object is null");
      } else {
         this.lockObject = var1;
      }
   }

   public CommonNotifier getConnectionNotifier() {
      return this.connectionNotifier;
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
         LocalDevice var7 = LocalDevice.getLocalDevice();
         var3 = var7.getBluetoothAddress();
      } catch (BluetoothStateException var8) {
         return null;
      }

      long var4 = this.connectionNotifier.getChannelID();
      String var6 = this.connectionNotifier.getProtocol();
      return this.createConnectionURL(var3, var1, var2, var6, var4);
   }

   public void setDeviceServiceClasses(int var1) {
      if (var1 != 0 && (var1 & -16760833) == 0) {
         this.deviceServiceClasses = var1;
      } else {
         throw new IllegalArgumentException("Invalid (major) service class(es)");
      }
   }

   public int getDeviceServiceClasses() {
      return this.deviceServiceClasses;
   }

   public void initRecord(byte[] var1) throws NullPointerException, IllegalArgumentException {
      if (null == var1) {
         throw new NullPointerException("serviceRecordStream is null");
      } else {
         this.parserSetAttributes(var1);
      }
   }
}
