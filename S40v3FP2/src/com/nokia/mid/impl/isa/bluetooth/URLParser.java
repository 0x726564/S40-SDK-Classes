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

   public URLParser(String var1, String var2) throws BluetoothConnectionException {
      if (var1 == null) {
         throw new NullPointerException("URL is null");
      } else if (!var2.equals("btspp") && !var2.equals("btl2cap")) {
         throw new IllegalArgumentException("Invalid protocol value (btspp or btl2cap)");
      } else {
         String var3 = new String(var1.toLowerCase());
         if (!var3.startsWith("//")) {
            throw new IllegalArgumentException("Expected // in URL!!");
         } else {
            var3 = var3.substring("//".length());
            if (var3.equals("")) {
               throw new IllegalArgumentException("Invalid URL (expected localhost or BD address)");
            } else {
               if (var3.startsWith("localhost:")) {
                  this.parseServerURL(var3, var2);
               } else {
                  this.parseClientURL(var3, var2);
               }

               if (var2.equals("btspp") && (this.transmitMTUParam != -1 || this.receiveMTUParam != -1)) {
                  throw new IllegalArgumentException("Receive/Transmit MTU in btspp connection string");
               } else {
                  if (this.encryptParam == 1) {
                     this.optionsParam = (byte)(this.optionsParam | 1);
                  }

                  if (this.nameParam != null) {
                     int var4 = var1.toLowerCase().indexOf(";name=");
                     var4 += ";name=".length();
                     this.nameParam = var1.substring(var4, var4 + this.nameParam.length());
                  }

                  if (this.masterParam == 1) {
                     throw new BluetoothConnectionException(6, "master/slave role switch not supported");
                  }
               }
            }
         }
      }
   }

   private void parseServerURL(String var1, String var2) throws BluetoothConnectionException {
      var1 = var1.substring("localhost:".length());
      if (var1.equals("")) {
         throw new IllegalArgumentException("Invalid URL(expected UUID)");
      } else {
         this.hostParam = new String("localhost");
         int var3 = var1.indexOf(";", 0);
         if (var3 == -1) {
            var3 = var1.length();
         }

         if (var3 > 32) {
            throw new IllegalArgumentException("Invalid UUID");
         } else {
            String var4 = var1.substring(0, var3);
            if (!CommonBluetooth.validHexNumber(var4)) {
               throw new IllegalArgumentException("Invalid UUID");
            } else {
               this.uuidParam = new UUID(var4, false);
               var1 = var1.substring(var3);
               this.parseParameters(var1);
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

   private void parseClientURL(String var1, String var2) throws BluetoothConnectionException {
      int var3 = var1.indexOf(":", 0);
      if (var3 == -1) {
         var3 = var1.length();
      }

      if (var3 != 12) {
         throw new IllegalArgumentException("Invalid BD address");
      } else if (!CommonBluetooth.validHexNumber(var1.substring(0, var3))) {
         throw new IllegalArgumentException("Invalid BD address");
      } else {
         this.hostParam = var1.substring(0, var3);
         var1 = var1.substring(var3);
         if (!var1.startsWith(":")) {
            throw new IllegalArgumentException("Invalid connection string. Expected ':'");
         } else {
            var1 = var1.substring(1);
            if (var1.equals("")) {
               throw new IllegalArgumentException("Invalid connection string. Expected channel ID");
            } else {
               var3 = var1.indexOf(";", 0);
               if (var3 == -1) {
                  var3 = var1.length();
               }

               if (var3 == 0) {
                  throw new IllegalArgumentException("Invalid connection string. Expected channel ID");
               } else {
                  String var4 = var1.substring(0, var3);
                  if (var2.equals("btspp")) {
                     if (var3 > 2 || !CommonBluetooth.validDecNumber(var4)) {
                        throw new IllegalArgumentException("Invalid value for channel id");
                     }

                     this.channelIDParam = Integer.parseInt(var4);
                     if (!CommonServiceRecord.validRFCOMMChannelValue((long)this.channelIDParam)) {
                        throw new IllegalArgumentException("Invalid channel ID (in conn. URL)");
                     }
                  } else {
                     if (var3 != 4 || !CommonBluetooth.validHexNumber(var4)) {
                        throw new IllegalArgumentException("Invalid channel ID");
                     }

                     this.channelIDParam = Integer.parseInt(var4, 16);
                     if (!CommonServiceRecord.validPSMValue((long)this.channelIDParam)) {
                        throw new IllegalArgumentException("Invalid channel ID (in conn. URL)");
                     }
                  }

                  var1 = var1.substring(var3);
                  this.parseParameters(var1);
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

   void parseParameters(String var1) throws BluetoothConnectionException {
      if (!var1.equals("")) {
         int var2;
         String var3;
         if (var1.startsWith(";name=") && this.nameParam == null) {
            var1 = var1.substring(";name=".length());
            var2 = var1.indexOf(";", 0);
            if (var2 == -1) {
               var2 = var1.length();
            }

            if (var2 == 0) {
               throw new IllegalArgumentException("Invalid name value in connection URL");
            }

            var3 = var1.substring(0, var2);
            String var4 = new String(" _-0123456789qwertzuiopasdfghjklyxcvbnm");

            for(int var5 = 0; var5 < var3.length(); ++var5) {
               if (var4.indexOf(var3.charAt(var5)) == -1) {
                  throw new IllegalArgumentException("Invalid character in name (in conn. URL)");
               }
            }

            this.nameParam = var3;
            this.parseParameters(var1.substring(var2));
         } else if (var1.startsWith(";master=false") && this.masterParam == -1) {
            this.masterParam = 0;
            this.parseParameters(var1.substring(";master=false".length()));
         } else if (var1.startsWith(";master=true") && this.masterParam == -1) {
            this.masterParam = 1;
            this.parseParameters(var1.substring(";master=true".length()));
         } else if (var1.startsWith(";encrypt=false") && this.encryptParam == -1) {
            this.encryptParam = 0;
            this.parseParameters(var1.substring(";encrypt=false".length()));
         } else if (var1.startsWith(";encrypt=true") && this.encryptParam == -1) {
            this.encryptParam = 1;
            this.optionsParam = (byte)(this.optionsParam | 4);
            if (this.authenticateParam == 0) {
               throw new BluetoothConnectionException(2, "Invalid combination:encrypt=true,authenticate=false");
            }

            this.parseParameters(var1.substring(";encrypt=true".length()));
         } else if (var1.startsWith(";authorize=false") && this.authorizeParam == -1) {
            this.authorizeParam = 0;
            this.parseParameters(var1.substring(";authorize=false".length()));
         } else if (var1.startsWith(";authorize=true") && this.authorizeParam == -1) {
            this.authorizeParam = 1;
            this.optionsParam = (byte)(this.optionsParam | 2);
            if (this.authenticateParam == 0) {
               throw new BluetoothConnectionException(2, "Invalid combination:authorize=true,authenticate=false");
            }

            this.parseParameters(var1.substring(";authorize=true".length()));
         } else if (var1.startsWith(";authenticate=false") && this.authenticateParam == -1) {
            this.authenticateParam = 0;
            if (this.encryptParam == 1) {
               throw new BluetoothConnectionException(2, "Invalid combination:encrypt=true,authenticate=false");
            }

            if (this.authorizeParam == 1) {
               throw new BluetoothConnectionException(2, "Invalid combination:authorize=true,authenticate=false");
            }

            this.parseParameters(var1.substring(";authenticate=false".length()));
         } else if (var1.startsWith(";authenticate=true") && this.authenticateParam == -1) {
            this.authenticateParam = 1;
            this.optionsParam = (byte)(this.optionsParam | 1);
            this.parseParameters(var1.substring(";authenticate=true".length()));
         } else if (var1.startsWith(";transmitmtu=") && this.transmitMTUParam == -1) {
            var1 = var1.substring(";transmitmtu=".length());
            var2 = var1.indexOf(";", 0);
            if (var2 == -1) {
               var2 = var1.length();
            }

            if (var2 == 0) {
               throw new IllegalArgumentException("Invalid transmitMTU value in connection URL");
            }

            var3 = var1.substring(0, var2);
            if (!CommonBluetooth.validDecNumber(var3)) {
               throw new IllegalArgumentException("Invalid character in transmitMTU value");
            }

            try {
               this.transmitMTUParam = Integer.parseInt(var3);
            } catch (NumberFormatException var7) {
               throw new IllegalArgumentException("Invalid transmitMTU value in connection URL");
            }

            if (this.transmitMTUParam < 1) {
               throw new IllegalArgumentException("Invalid transmitMTU value in connection URL");
            }

            this.parseParameters(var1.substring(var2));
         } else {
            if (!var1.startsWith(";receivemtu=") || this.receiveMTUParam != -1) {
               throw new IllegalArgumentException("Invalid URL -> parameters");
            }

            var1 = var1.substring(";receivemtu=".length());
            var2 = var1.indexOf(";", 0);
            if (var2 == -1) {
               var2 = var1.length();
            }

            if (var2 == 0) {
               throw new IllegalArgumentException("Invalid receive MTU value in connection URL");
            }

            var3 = var1.substring(0, var2);
            if (!CommonBluetooth.validDecNumber(var3)) {
               throw new IllegalArgumentException("Invalid character in receiveMTU value");
            }

            try {
               this.receiveMTUParam = Integer.parseInt(var3);
            } catch (NumberFormatException var6) {
               throw new IllegalArgumentException("Invalid receiveMTU value in connection URL");
            }

            if (this.receiveMTUParam < 48 || this.receiveMTUParam > Integer.parseInt(LocalDevice.getProperty("bluetooth.l2cap.receiveMTU.max"))) {
               throw new IllegalArgumentException("Invalid receiveMTU value");
            }

            this.parseParameters(var1.substring(var2));
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
