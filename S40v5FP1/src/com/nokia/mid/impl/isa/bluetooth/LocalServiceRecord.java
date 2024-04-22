package com.nokia.mid.impl.isa.bluetooth;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;

public class LocalServiceRecord extends SerializedCommonServiceRecord {
   private int deviceServiceClasses = 0;
   private final CommonNotifier connectionNotifier;
   private Object lockObject = new Object();

   private LocalServiceRecord() {
      throw new RuntimeException("wrong constructor called");
   }

   public LocalServiceRecord(CommonNotifier notifier) {
      if (notifier == null) {
         throw new NullPointerException("got no notifier");
      } else {
         this.connectionNotifier = notifier;
      }
   }

   public Object getLockObject() {
      return this.lockObject;
   }

   public void setLockObject(Object obj) {
      if (obj == null) {
         throw new NullPointerException("Lock object is null");
      } else {
         this.lockObject = obj;
      }
   }

   public CommonNotifier getConnectionNotifier() {
      return this.connectionNotifier;
   }

   public RemoteDevice getHostDevice() {
      return null;
   }

   public boolean populateRecord(int[] attrIDs) {
      throw new RuntimeException("This ServiceRecord describes a local service");
   }

   public String getConnectionURL(int requiredSecurity, boolean mustBeMaster) {
      String address;
      try {
         LocalDevice localDevice = LocalDevice.getLocalDevice();
         address = localDevice.getBluetoothAddress();
      } catch (BluetoothStateException var8) {
         return null;
      }

      long channelID = this.connectionNotifier.getChannelID();
      String protocol = this.connectionNotifier.getProtocol();
      return this.createConnectionURL(address, requiredSecurity, mustBeMaster, protocol, channelID);
   }

   public void setDeviceServiceClasses(int classes) {
      if (classes != 0 && (classes & -16760833) == 0) {
         this.deviceServiceClasses = classes;
      } else {
         throw new IllegalArgumentException("Invalid (major) service class(es)");
      }
   }

   public int getDeviceServiceClasses() {
      return this.deviceServiceClasses;
   }

   public void initRecord(byte[] serviceRecordStream) throws NullPointerException, IllegalArgumentException {
      if (null == serviceRecordStream) {
         throw new NullPointerException("serviceRecordStream is null");
      } else {
         this.parserSetAttributes(serviceRecordStream);
      }
   }

   public void initServiceRecordHandle(long sdpHandle) throws IllegalArgumentException {
      try {
         DataElement sdpHandleItem = new DataElement(10, sdpHandle);
         if (!this.updateAttributeValue(0, sdpHandleItem)) {
            throw new IllegalArgumentException("setAttributeValue Problem");
         }
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException("initServiceRecordHandle Problem Value = " + sdpHandle);
      }
   }
}
