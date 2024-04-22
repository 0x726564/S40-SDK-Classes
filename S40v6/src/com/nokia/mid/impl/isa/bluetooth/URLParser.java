package com.nokia.mid.impl.isa.bluetooth;

import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;

public class URLParser {
   public static final int NOT_SET = -1;
   public static final int FALSE = 0;
   public static final int TRUE = 1;
   private UUID uuidParam = null;
   private int channelIDParam = -1;
   private String hostParam = null;
   private String nameParam = null;
   private int transmitMTUParam = -1;
   private int receiveMTUParam = -1;
   private int masterParam = -1;
   private int encryptParam = -1;
   private int authorizeParam = -1;
   private int authenticateParam = -1;
   private byte optionsParam = 0;

   public URLParser(String connectionURL, String protocol) throws BluetoothConnectionException {
      if (connectionURL == null) {
         throw new NullPointerException("URL is null");
      } else if (!protocol.equals("btspp") && !protocol.equals("btl2cap") && !protocol.equals("btgoep")) {
         throw new IllegalArgumentException("Invalid protocol value (btspp, btl2cap or btgoep)");
      } else {
         String url = new String(connectionURL.toLowerCase());
         if (!url.startsWith("//")) {
            throw new IllegalArgumentException("Expected // in URL!!");
         } else {
            url = url.substring("//".length());
            if (url.equals("")) {
               throw new IllegalArgumentException("Invalid URL (expected localhost or BD address)");
            } else {
               if (url.startsWith("localhost:")) {
                  this.parseServerURL(url, protocol);
               } else {
                  this.parseClientURL(url, protocol);
               }

               if (!protocol.equals("btspp") && !protocol.equals("btgoep") || this.transmitMTUParam == -1 && this.receiveMTUParam == -1) {
                  if (this.encryptParam == 1) {
                     this.optionsParam = (byte)(this.optionsParam | 1);
                  }

                  if (this.nameParam != null) {
                     int index = connectionURL.toLowerCase().indexOf(";name=");
                     index += ";name=".length();
                     this.nameParam = connectionURL.substring(index, index + this.nameParam.length());
                  }

                  if (this.masterParam == 1) {
                     throw new BluetoothConnectionException(6, "master/slave role switch not supported");
                  }
               } else {
                  throw new IllegalArgumentException("Receive/Transmit MTU in btspp connection string");
               }
            }
         }
      }
   }

   private void parseServerURL(String url, String protocol) throws BluetoothConnectionException {
      url = url.substring("localhost:".length());
      if (url.equals("")) {
         throw new IllegalArgumentException("Invalid URL(expected UUID)");
      } else {
         this.hostParam = new String("localhost");
         int index = url.indexOf(";", 0);
         if (index == -1) {
            index = url.length();
         }

         if (index > 32) {
            throw new IllegalArgumentException("Invalid UUID");
         } else {
            String uuidString = url.substring(0, index);
            if (!CommonBluetooth.validHexNumber(uuidString)) {
               throw new IllegalArgumentException("Invalid UUID");
            } else {
               this.uuidParam = new UUID(uuidString, false);
               url = url.substring(index);
               this.parseParameters(url);
               if (this.authorizeParam == 1) {
                  this.optionsParam = (byte)(this.optionsParam | 1);
               }

               if (this.encryptParam == 1 || this.authenticateParam == 1) {
                  this.optionsParam = (byte)(this.optionsParam | 2);
               }

            }
         }
      }
   }

   private void parseClientURL(String url, String protocol) throws BluetoothConnectionException {
      int index = url.indexOf(":", 0);
      if (index == -1) {
         index = url.length();
      }

      if (index != 12) {
         throw new IllegalArgumentException("Invalid BD address");
      } else if (!CommonBluetooth.validHexNumber(url.substring(0, index))) {
         throw new IllegalArgumentException("Invalid BD address");
      } else {
         this.hostParam = url.substring(0, index);
         url = url.substring(index);
         if (!url.startsWith(":")) {
            throw new IllegalArgumentException("Invalid connection string. Expected ':'");
         } else {
            url = url.substring(1);
            if (url.equals("")) {
               throw new IllegalArgumentException("Invalid connection string. Expected channel ID");
            } else {
               index = url.indexOf(";", 0);
               if (index == -1) {
                  index = url.length();
               }

               if (index == 0) {
                  throw new IllegalArgumentException("Invalid connection string. Expected channel ID");
               } else {
                  String strChannelID = url.substring(0, index);
                  if (!protocol.equals("btspp") && !protocol.equals("btgoep")) {
                     if (index != 4 || !CommonBluetooth.validHexNumber(strChannelID)) {
                        throw new IllegalArgumentException("Invalid channel ID");
                     }

                     this.channelIDParam = Integer.parseInt(strChannelID, 16);
                     if (!CommonServiceRecord.validPSMValue((long)this.channelIDParam)) {
                        throw new IllegalArgumentException("Invalid channel ID (in conn. URL)");
                     }
                  } else {
                     if (index > 2 || !CommonBluetooth.validDecNumber(strChannelID)) {
                        throw new IllegalArgumentException("Invalid value for channel id");
                     }

                     this.channelIDParam = Integer.parseInt(strChannelID);
                     if (!CommonServiceRecord.validRFCOMMChannelValue((long)this.channelIDParam)) {
                        throw new IllegalArgumentException("Invalid channel ID (in conn. URL)");
                     }
                  }

                  url = url.substring(index);
                  this.parseParameters(url);
                  if (this.nameParam != null) {
                     throw new IllegalArgumentException("Invalid conn. string. name in client string");
                  } else if (this.authorizeParam != -1) {
                     throw new IllegalArgumentException("Invalid conn. string. authorize in client string");
                  }
               }
            }
         }
      }
   }

   void parseParameters(String url) throws BluetoothConnectionException {
      if (!url.equals("")) {
         int index;
         String paramValue;
         if (url.startsWith(";name=") && this.nameParam == null) {
            url = url.substring(";name=".length());
            index = url.indexOf(";", 0);
            if (index == -1) {
               index = url.length();
            }

            if (index == 0) {
               throw new IllegalArgumentException("Invalid name value in connection URL");
            }

            paramValue = url.substring(0, index);
            String validValues = new String(" _-0123456789qwertzuiopasdfghjklyxcvbnm");

            for(int i = 0; i < paramValue.length(); ++i) {
               if (validValues.indexOf(paramValue.charAt(i)) == -1) {
                  throw new IllegalArgumentException("Invalid character in name (in conn. URL)");
               }
            }

            this.nameParam = paramValue;
            this.parseParameters(url.substring(index));
         } else if (url.startsWith(";master=false") && this.masterParam == -1) {
            this.masterParam = 0;
            this.parseParameters(url.substring(";master=false".length()));
         } else if (url.startsWith(";master=true") && this.masterParam == -1) {
            this.masterParam = 1;
            this.parseParameters(url.substring(";master=true".length()));
         } else if (url.startsWith(";encrypt=false") && this.encryptParam == -1) {
            this.encryptParam = 0;
            this.parseParameters(url.substring(";encrypt=false".length()));
         } else if (url.startsWith(";encrypt=true") && this.encryptParam == -1) {
            this.encryptParam = 1;
            this.optionsParam = (byte)(this.optionsParam | 4);
            if (this.authenticateParam == 0) {
               throw new BluetoothConnectionException(2, "Invalid combination:encrypt=true,authenticate=false");
            }

            this.parseParameters(url.substring(";encrypt=true".length()));
         } else if (url.startsWith(";authorize=false") && this.authorizeParam == -1) {
            this.authorizeParam = 0;
            this.parseParameters(url.substring(";authorize=false".length()));
         } else if (url.startsWith(";authorize=true") && this.authorizeParam == -1) {
            this.authorizeParam = 1;
            this.optionsParam = (byte)(this.optionsParam | 2);
            if (this.authenticateParam == 0) {
               throw new BluetoothConnectionException(2, "Invalid combination:authorize=true,authenticate=false");
            }

            this.parseParameters(url.substring(";authorize=true".length()));
         } else if (url.startsWith(";authenticate=false") && this.authenticateParam == -1) {
            this.authenticateParam = 0;
            if (this.encryptParam == 1) {
               throw new BluetoothConnectionException(2, "Invalid combination:encrypt=true,authenticate=false");
            }

            if (this.authorizeParam == 1) {
               throw new BluetoothConnectionException(2, "Invalid combination:authorize=true,authenticate=false");
            }

            this.parseParameters(url.substring(";authenticate=false".length()));
         } else if (url.startsWith(";authenticate=true") && this.authenticateParam == -1) {
            this.authenticateParam = 1;
            this.optionsParam = (byte)(this.optionsParam | 1);
            this.parseParameters(url.substring(";authenticate=true".length()));
         } else if (url.startsWith(";transmitmtu=") && this.transmitMTUParam == -1) {
            url = url.substring(";transmitmtu=".length());
            index = url.indexOf(";", 0);
            if (index == -1) {
               index = url.length();
            }

            if (index == 0) {
               throw new IllegalArgumentException("Invalid transmitMTU value in connection URL");
            }

            paramValue = url.substring(0, index);
            if (!CommonBluetooth.validDecNumber(paramValue)) {
               throw new IllegalArgumentException("Invalid character in transmitMTU value");
            }

            try {
               this.transmitMTUParam = Integer.parseInt(paramValue);
            } catch (NumberFormatException var7) {
               throw new IllegalArgumentException("Invalid transmitMTU value in connection URL");
            }

            if (this.transmitMTUParam < 1) {
               throw new IllegalArgumentException("Invalid transmitMTU value in connection URL");
            }

            this.parseParameters(url.substring(index));
         } else {
            if (!url.startsWith(";receivemtu=") || this.receiveMTUParam != -1) {
               throw new IllegalArgumentException("Invalid URL -> parameters");
            }

            url = url.substring(";receivemtu=".length());
            index = url.indexOf(";", 0);
            if (index == -1) {
               index = url.length();
            }

            if (index == 0) {
               throw new IllegalArgumentException("Invalid receive MTU value in connection URL");
            }

            paramValue = url.substring(0, index);
            if (!CommonBluetooth.validDecNumber(paramValue)) {
               throw new IllegalArgumentException("Invalid character in receiveMTU value");
            }

            try {
               this.receiveMTUParam = Integer.parseInt(paramValue);
            } catch (NumberFormatException var6) {
               throw new IllegalArgumentException("Invalid receiveMTU value in connection URL");
            }

            if (this.receiveMTUParam < 48 || this.receiveMTUParam > Integer.parseInt(LocalDevice.getProperty("bluetooth.l2cap.receiveMTU.max"))) {
               throw new IllegalArgumentException("Invalid receiveMTU value");
            }

            this.parseParameters(url.substring(index));
         }
      }

   }

   public UUID getUUIDValue() {
      return this.uuidParam == null ? null : new UUID(this.uuidParam.toString(), false);
   }

   public int getChannelORPsmValue() {
      return this.channelIDParam;
   }

   public String getHostValue() {
      return new String(this.hostParam);
   }

   public String getNameValue() {
      return this.nameParam == null ? null : new String(this.nameParam);
   }

   public int getTransmitMTUValue() {
      return this.transmitMTUParam;
   }

   public int getReceiveMTUValue() {
      return this.receiveMTUParam;
   }

   public byte getOptionsValue() {
      return this.optionsParam;
   }
}
