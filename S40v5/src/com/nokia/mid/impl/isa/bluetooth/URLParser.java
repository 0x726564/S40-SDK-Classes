package com.nokia.mid.impl.isa.bluetooth;

import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;

public class URLParser {
   public static final int NOT_SET = -1;
   public static final int FALSE = 0;
   public static final int TRUE = 1;
   private UUID lk = null;
   private int ll = -1;
   private String lm = null;
   private String ln = null;
   private int lo = -1;
   private int lp = -1;
   private int lq = -1;
   private int lr = -1;
   private int ls = -1;
   private int lt = -1;
   private byte lu = 0;

   public URLParser(String var1, String var2) throws BluetoothConnectionException {
      if (var1 == null) {
         throw new NullPointerException("URL is null");
      } else if (!var2.equals("btspp") && !var2.equals("btl2cap") && !var2.equals("btgoep")) {
         throw new IllegalArgumentException("Invalid protocol value (btspp, btl2cap or btgoep)");
      } else {
         String var3;
         if (!(var3 = new String(var1.toLowerCase())).startsWith("//")) {
            throw new IllegalArgumentException("Expected // in URL!!");
         } else if ((var3 = var3.substring("//".length())).equals("")) {
            throw new IllegalArgumentException("Invalid URL (expected localhost or BD address)");
         } else {
            String var5;
            if (var3.startsWith("localhost:")) {
               if ((var5 = var3.substring("localhost:".length())).equals("")) {
                  throw new IllegalArgumentException("Invalid URL(expected UUID)");
               }

               this.lm = new String("localhost");
               int var6;
               if ((var6 = var5.indexOf(";", 0)) == -1) {
                  var6 = var5.length();
               }

               if (var6 > 32) {
                  throw new IllegalArgumentException("Invalid UUID");
               }

               String var9;
               if (!CommonBluetooth.validHexNumber(var9 = var5.substring(0, var6))) {
                  throw new IllegalArgumentException("Invalid UUID");
               }

               this.lk = new UUID(var9, false);
               var5 = var5.substring(var6);
               this.Q(var5);
               if (this.ls == 1) {
                  this.lu = (byte)(this.lu | 1);
               }

               if (this.lr == 1 || this.lt == 1) {
                  this.lu = (byte)(this.lu | 2);
               }
            } else {
               int var7;
               if ((var7 = var3.indexOf(":", 0)) == -1) {
                  var7 = var3.length();
               }

               if (var7 != 12) {
                  throw new IllegalArgumentException("Invalid BD address");
               }

               if (!CommonBluetooth.validHexNumber(var3.substring(0, var7))) {
                  throw new IllegalArgumentException("Invalid BD address");
               }

               this.lm = var3.substring(0, var7);
               if (!(var5 = var3.substring(var7)).startsWith(":")) {
                  throw new IllegalArgumentException("Invalid connection string. Expected ':'");
               }

               if ((var5 = var5.substring(1)).equals("")) {
                  throw new IllegalArgumentException("Invalid connection string. Expected channel ID");
               }

               if ((var7 = var5.indexOf(";", 0)) == -1) {
                  var7 = var5.length();
               }

               if (var7 == 0) {
                  throw new IllegalArgumentException("Invalid connection string. Expected channel ID");
               }

               var3 = var5.substring(0, var7);
               if (!var2.equals("btspp") && !var2.equals("btgoep")) {
                  if (var7 != 4 || !CommonBluetooth.validHexNumber(var3)) {
                     throw new IllegalArgumentException("Invalid channel ID");
                  }

                  this.ll = Integer.parseInt(var3, 16);
                  if (!CommonServiceRecord.validPSMValue((long)this.ll)) {
                     throw new IllegalArgumentException("Invalid channel ID (in conn. URL)");
                  }
               } else {
                  if (var7 > 2 || !CommonBluetooth.validDecNumber(var3)) {
                     throw new IllegalArgumentException("Invalid value for channel id");
                  }

                  this.ll = Integer.parseInt(var3);
                  if (!CommonServiceRecord.validRFCOMMChannelValue((long)this.ll)) {
                     throw new IllegalArgumentException("Invalid channel ID (in conn. URL)");
                  }
               }

               var5 = var5.substring(var7);
               this.Q(var5);
               if (this.ln != null) {
                  throw new IllegalArgumentException("Invalid conn. string. name in client string");
               }

               if (this.ls != -1) {
                  throw new IllegalArgumentException("Invalid conn. string. authorize in client string");
               }
            }

            if (!var2.equals("btspp") && !var2.equals("btgoep") || this.lo == -1 && this.lp == -1) {
               if (this.lr == 1) {
                  this.lu = (byte)(this.lu | 1);
               }

               if (this.ln != null) {
                  int var8 = var1.toLowerCase().indexOf(";name=") + ";name=".length();
                  this.ln = var1.substring(var8, var8 + this.ln.length());
               }

               if (this.lq == 1) {
                  throw new BluetoothConnectionException(6, "master/slave role switch not supported");
               }
            } else {
               throw new IllegalArgumentException("Receive/Transmit MTU in btspp connection string");
            }
         }
      }
   }

   private void Q(String var1) throws BluetoothConnectionException {
      if (!var1.equals("")) {
         int var2;
         String var3;
         if (var1.startsWith(";name=") && this.ln == null) {
            if ((var2 = (var1 = var1.substring(";name=".length())).indexOf(";", 0)) == -1) {
               var2 = var1.length();
            }

            if (var2 == 0) {
               throw new IllegalArgumentException("Invalid name value in connection URL");
            } else {
               var3 = var1.substring(0, var2);
               String var4 = new String(" _-0123456789qwertzuiopasdfghjklyxcvbnm");

               for(int var5 = 0; var5 < var3.length(); ++var5) {
                  if (var4.indexOf(var3.charAt(var5)) == -1) {
                     throw new IllegalArgumentException("Invalid character in name (in conn. URL)");
                  }
               }

               this.ln = var3;
               this.Q(var1.substring(var2));
            }
         } else if (var1.startsWith(";master=false") && this.lq == -1) {
            this.lq = 0;
            this.Q(var1.substring(";master=false".length()));
         } else if (var1.startsWith(";master=true") && this.lq == -1) {
            this.lq = 1;
            this.Q(var1.substring(";master=true".length()));
         } else if (var1.startsWith(";encrypt=false") && this.lr == -1) {
            this.lr = 0;
            this.Q(var1.substring(";encrypt=false".length()));
         } else if (var1.startsWith(";encrypt=true") && this.lr == -1) {
            this.lr = 1;
            this.lu = (byte)(this.lu | 4);
            if (this.lt == 0) {
               throw new BluetoothConnectionException(2, "Invalid combination:encrypt=true,authenticate=false");
            } else {
               this.Q(var1.substring(";encrypt=true".length()));
            }
         } else if (var1.startsWith(";authorize=false") && this.ls == -1) {
            this.ls = 0;
            this.Q(var1.substring(";authorize=false".length()));
         } else if (var1.startsWith(";authorize=true") && this.ls == -1) {
            this.ls = 1;
            this.lu = (byte)(this.lu | 2);
            if (this.lt == 0) {
               throw new BluetoothConnectionException(2, "Invalid combination:authorize=true,authenticate=false");
            } else {
               this.Q(var1.substring(";authorize=true".length()));
            }
         } else if (var1.startsWith(";authenticate=false") && this.lt == -1) {
            this.lt = 0;
            if (this.lr == 1) {
               throw new BluetoothConnectionException(2, "Invalid combination:encrypt=true,authenticate=false");
            } else if (this.ls == 1) {
               throw new BluetoothConnectionException(2, "Invalid combination:authorize=true,authenticate=false");
            } else {
               this.Q(var1.substring(";authenticate=false".length()));
            }
         } else if (var1.startsWith(";authenticate=true") && this.lt == -1) {
            this.lt = 1;
            this.lu = (byte)(this.lu | 1);
            this.Q(var1.substring(";authenticate=true".length()));
         } else if (var1.startsWith(";transmitmtu=") && this.lo == -1) {
            if ((var2 = (var1 = var1.substring(";transmitmtu=".length())).indexOf(";", 0)) == -1) {
               var2 = var1.length();
            }

            if (var2 == 0) {
               throw new IllegalArgumentException("Invalid transmitMTU value in connection URL");
            } else if (!CommonBluetooth.validDecNumber(var3 = var1.substring(0, var2))) {
               throw new IllegalArgumentException("Invalid character in transmitMTU value");
            } else {
               try {
                  this.lo = Integer.parseInt(var3);
               } catch (NumberFormatException var6) {
                  throw new IllegalArgumentException("Invalid transmitMTU value in connection URL");
               }

               if (this.lo < 1) {
                  throw new IllegalArgumentException("Invalid transmitMTU value in connection URL");
               } else {
                  this.Q(var1.substring(var2));
               }
            }
         } else if (var1.startsWith(";receivemtu=") && this.lp == -1) {
            if ((var2 = (var1 = var1.substring(";receivemtu=".length())).indexOf(";", 0)) == -1) {
               var2 = var1.length();
            }

            if (var2 == 0) {
               throw new IllegalArgumentException("Invalid receive MTU value in connection URL");
            } else if (!CommonBluetooth.validDecNumber(var3 = var1.substring(0, var2))) {
               throw new IllegalArgumentException("Invalid character in receiveMTU value");
            } else {
               try {
                  this.lp = Integer.parseInt(var3);
               } catch (NumberFormatException var7) {
                  throw new IllegalArgumentException("Invalid receiveMTU value in connection URL");
               }

               if (this.lp >= 48 && this.lp <= Integer.parseInt(LocalDevice.getProperty("bluetooth.l2cap.receiveMTU.max"))) {
                  this.Q(var1.substring(var2));
               } else {
                  throw new IllegalArgumentException("Invalid receiveMTU value");
               }
            }
         } else {
            throw new IllegalArgumentException("Invalid URL -> parameters");
         }
      }
   }

   public UUID getUUIDValue() {
      return this.lk == null ? null : new UUID(this.lk.toString(), false);
   }

   public int getChannelORPsmValue() {
      return this.ll;
   }

   public String getHostValue() {
      return new String(this.lm);
   }

   public String getNameValue() {
      return this.ln == null ? null : new String(this.ln);
   }

   public int getTransmitMTUValue() {
      return this.lo;
   }

   public int getReceiveMTUValue() {
      return this.lp;
   }

   public byte getOptionsValue() {
      return this.lu;
   }
}
