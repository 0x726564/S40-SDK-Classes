package com.nokia.mid.impl.isa.bluetooth;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.DataElement;
import javax.bluetooth.UUID;

public abstract class SerializedCommonServiceRecord extends CommonServiceRecord {
   private Vector hf = null;

   SerializedCommonServiceRecord() {
   }

   private final int b(byte[] var1, int var2) throws IllegalArgumentException, NullPointerException {
      int var4 = 0;
      if (var1 == null) {
         throw new NullPointerException("Parsing error");
      } else if (var2 >= 0 && var2 < var1.length) {
         switch((byte)(var1[var2] & 7)) {
         case 0:
            var4 = (var1[var2] & 248) >> 3 == 0 ? 1 : 2;
            break;
         case 1:
            var4 = 3;
            break;
         case 2:
            var4 = 5;
            break;
         case 3:
            var4 = 9;
            break;
         case 4:
            var4 = 17;
            break;
         case 5:
            if (var1.length < 1 || var2 >= var1.length - 1) {
               throw new IllegalArgumentException("Parse error");
            }

            var4 = var1[var2 + 1] & 255;
            var4 += 2;
            break;
         case 6:
            if (var1.length < 2 || var2 >= var1.length - 2) {
               throw new IllegalArgumentException("Parse error");
            }

            var4 = var1[var2 + 1] << 8 & '\uff00' | var1[var2 + 2] & 255;
            var4 += 3;
            break;
         case 7:
            if (var1.length < 3 || var2 >= var1.length - 3) {
               throw new IllegalArgumentException("Parse error");
            }

            var4 = var1[var2 + 1] << 16 & 16711680 | var1[var2 + 2] << 8 & '\uff00' | var1[var2 + 3] & 255;
            var4 += 4;
         }

         return var4;
      } else {
         throw new IllegalArgumentException("Parsing error");
      }
   }

   protected final DataElement parserGetAttrValue(byte[] var1, int var2, int var3) throws IllegalArgumentException, NullPointerException {
      DataElement var4 = null;
      if (var1 == null) {
         throw new NullPointerException("Parsing error");
      } else if (var2 >= 0 && var2 + var3 > 0 && var2 + var3 <= var1.length) {
         int var5 = (var1[var2] & 248) >> 3;
         byte var6 = var1[var2];
         byte[] var7 = new byte[]{1, 1, 1, 1, 1, 2, 3, 4};
         var6 = (byte)(var6 & 7);
         int var15 = var7[var6] & 255;
         if ((var3 -= var15) < 0) {
            throw new IllegalArgumentException("Parsing error");
         } else {
            String var13;
            long var17;
            switch(var5) {
            case 0:
               if (var3 != 0) {
                  throw new IllegalArgumentException("Parse error");
               }

               var4 = new DataElement(0);
               break;
            case 1:
            case 2:
               switch(var3) {
               case 1:
                  var17 = (long)var1[var2 + var15] & 255L;
                  if (var5 == 1) {
                     var4 = new DataElement(8, var17);
                  } else {
                     var4 = new DataElement(16, var17);
                  }

                  return var4;
               case 2:
                  var17 = (long)var1[var2 + var15] << 8 & 65280L | (long)var1[var2 + var15 + 1] & 255L;
                  if (var5 == 1) {
                     var4 = new DataElement(9, var17);
                  } else {
                     var4 = new DataElement(17, var17);
                  }

                  return var4;
               case 4:
                  var17 = (long)var1[var2 + var15] << 24 & -16777216L | (long)var1[var2 + var15 + 1] << 16 & 16711680L | (long)var1[var2 + var15 + 2] << 8 & 65280L | (long)var1[var2 + var15 + 3] & 255L;
                  if (var5 == 1) {
                     var4 = new DataElement(10, var17);
                  } else {
                     var4 = new DataElement(18, var17);
                  }

                  return var4;
               case 8:
                  if (var5 == 1) {
                     var7 = new byte[8];
                     System.arraycopy(var1, var2 + var15, var7, 0, 8);
                     var4 = new DataElement(11, var7);
                  } else {
                     var17 = (long)var1[var2 + var15] & 255L;

                     for(int var18 = 1; var18 < 8; ++var18) {
                        var17 = (var17 <<= 8) | (long)var1[var2 + var15 + var18] & 255L;
                     }

                     var4 = new DataElement(19, var17);
                  }

                  return var4;
               case 16:
                  var7 = new byte[16];
                  System.arraycopy(var1, var2 + var15, var7, 0, 16);
                  if (var5 == 1) {
                     var4 = new DataElement(12, var7);
                  } else {
                     var4 = new DataElement(20, var7);
                  }

                  return var4;
               default:
                  throw new IllegalArgumentException("Parse error");
               }
            case 3:
               UUID var10;
               switch(var3) {
               case 2:
                  var17 = (long)var1[var2 + var15] << 8 & 65280L | (long)var1[var2 + var15 + 1] & 255L;
                  var10 = new UUID(var17);
                  break;
               case 4:
                  var17 = (long)var1[var2 + var15] << 24 & -16777216L | (long)var1[var2 + var15 + 1] << 16 & 16711680L | (long)var1[var2 + var15 + 2] << 8 & 65280L | (long)var1[var2 + var15 + 3] & 255L;
                  var10 = new UUID(var17);
                  break;
               case 16:
                  var13 = new String("");

                  for(int var16 = 0; var16 < 16; ++var16) {
                     byte var8 = var1[var2 + var15 + var16];
                     var13 = var13 + Integer.toHexString((var8 & 240) >> 4 & 255);
                     var13 = var13 + Integer.toHexString(var8 & 15 & 255);
                  }

                  var10 = new UUID(var13, false);
                  break;
               default:
                  throw new IllegalArgumentException("Parse error");
               }

               var4 = new DataElement(24, var10);
               break;
            case 4:
               var4 = null;

               try {
                  var13 = new String(var1, var2 + var15, var3, "UTF-8");
               } catch (UnsupportedEncodingException var12) {
                  throw new IllegalArgumentException("Unsupported encoding UTF-8");
               }

               var4 = new DataElement(32, var13);
               break;
            case 5:
               if (var3 != 1) {
                  throw new IllegalArgumentException("Parse error");
               }

               boolean var14 = false;
               if (var1[var2 + var15] != 0) {
                  var14 = true;
               }

               var4 = new DataElement(var14);
               break;
            case 6:
            case 7:
               if (var5 == 6) {
                  var4 = this.a(48, var1, var2 + var15, var3);
               } else {
                  var4 = this.a(56, var1, var2 + var15, var3);
               }
               break;
            case 8:
               if (var3 == 0) {
                  throw new IllegalArgumentException("Parse error");
               }

               var4 = null;

               try {
                  var13 = new String(var1, var2 + var15, var3, "UTF-8");
               } catch (UnsupportedEncodingException var11) {
                  throw new IllegalArgumentException("Unsupported encoding UTF-8");
               }

               var4 = new DataElement(64, var13);
            }

            return var4;
         }
      } else {
         throw new IllegalArgumentException("Parsing error");
      }
   }

   private final DataElement a(int var1, byte[] var2, int var3, int var4) throws IllegalArgumentException, NullPointerException {
      if (var2 == null) {
         throw new NullPointerException("Parsing error");
      } else {
         var4 += var3;
         if (var3 >= 0 && var4 >= 0 && var4 <= var2.length) {
            DataElement var7 = new DataElement(var1);
            var3 = var3;

            int var5;
            do {
               if (var3 >= var4) {
                  if (var3 > var4) {
                     throw new IllegalArgumentException("Parse error");
                  }

                  return var7;
               }

               var5 = this.b(var2, var3);
               DataElement var6 = this.parserGetAttrValue(var2, var3, var5);
               var7.insertElementAt(var6, var7.getSize());
            } while((var3 += var5) > 0);

            throw new IllegalArgumentException("Parse error");
         } else {
            throw new IllegalArgumentException("Parsing error");
         }
      }
   }

   private static byte[] a(byte[] var0, byte[] var1) {
      byte[] var2 = null;
      if (var0 == null) {
         if (var1 != null) {
            var2 = new byte[var1.length];
            System.arraycopy(var1, 0, var2, 0, var1.length);
         }
      } else if (var1 == null) {
         var2 = new byte[var0.length];
         System.arraycopy(var0, 0, var2, 0, var0.length);
      } else {
         if (var0.length + var1.length <= 0) {
            throw new IllegalArgumentException("Parse error");
         }

         var2 = new byte[var0.length + var1.length];
         System.arraycopy(var0, 0, var2, 0, var0.length);
         System.arraycopy(var1, 0, var2, var0.length, var1.length);
      }

      return var2;
   }

   protected final byte[] parserSerializeDataValue(DataElement var1) throws IllegalArgumentException, NullPointerException {
      byte[] var5 = null;
      byte[] var6 = null;
      if (var1 == null) {
         throw new NullPointerException("dataValue is null");
      } else {
         byte var2;
         long var34;
         byte var30;
         String var31;
         switch(var2 = (byte)var1.getDataType()) {
         case 0:
            (var5 = new byte[1])[0] = 0;
            var6 = null;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 13:
         case 14:
         case 15:
         case 21:
         case 22:
         case 23:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 57:
         case 58:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         default:
            break;
         case 8:
         case 16:
            var30 = 2;
            if (var2 != 16) {
               var30 = 1;
            }

            (var5 = new byte[1])[0] = (byte)(var30 << 3);
            var34 = var1.getLong();
            (var6 = new byte[1])[0] = (byte)((int)(var34 & 255L));
            break;
         case 9:
         case 17:
            var30 = 2;
            if (var2 != 17) {
               var30 = 1;
            }

            (var5 = new byte[1])[0] = (byte)(var30 << 3);
            var5[0] |= 1;
            var34 = var1.getLong();
            (var6 = new byte[2])[0] = (byte)((int)((var34 & 65280L) >> 8));
            var6[1] = (byte)((int)(var34 & 255L));
            break;
         case 10:
         case 18:
            var30 = 2;
            if (var2 != 18) {
               var30 = 1;
            }

            (var5 = new byte[1])[0] = (byte)(var30 << 3);
            var5[0] |= 2;
            var34 = var1.getLong();
            (var6 = new byte[4])[0] = (byte)((int)((var34 & -16777216L) >> 24));
            var6[1] = (byte)((int)((var34 & 16711680L) >> 16));
            var6[2] = (byte)((int)((var34 & 65280L) >> 8));
            var6[3] = (byte)((int)(var34 & 255L));
            break;
         case 11:
            (var5 = new byte[1])[0] = 8;
            var5[0] |= 3;
            var6 = (byte[])var1.getValue();
            break;
         case 12:
         case 20:
            var30 = 2;
            if (var2 != 20) {
               var30 = 1;
            }

            (var5 = new byte[1])[0] = (byte)(var30 << 3);
            var5[0] |= 4;
            var6 = (byte[])var1.getValue();
            break;
         case 19:
            (var5 = new byte[1])[0] = 16;
            var5[0] |= 3;
            var34 = var1.getLong();
            (var6 = new byte[8])[0] = (byte)((int)((var34 & -72057594037927936L) >> 56));
            var6[1] = (byte)((int)((var34 & 71776119061217280L) >> 48));
            var6[2] = (byte)((int)((var34 & 280375465082880L) >> 40));
            var6[3] = (byte)((int)((var34 & 1095216660480L) >> 32));
            var6[4] = (byte)((int)((var34 & 4278190080L) >> 24));
            var6[5] = (byte)((int)((var34 & 16711680L) >> 16));
            var6[6] = (byte)((int)((var34 & 65280L) >> 8));
            var6[7] = (byte)((int)(var34 & 255L));
            break;
         case 24:
            var31 = new String("00000000000000000000000000000000");
            String var32 = ((UUID)var1.getValue()).toString();
            var31 = new String(var31.substring(0, 32 - var32.length()).concat(var32));
            (var5 = new byte[1])[0] = 24;
            var5[0] |= 4;
            var6 = new byte[16];

            for(int var33 = 0; var33 < 32; var33 += 2) {
               var6[var33 / 2] |= Integer.parseInt(var31.substring(var33, var33 + 2), 16);
            }

            return a(var5, var6);
         case 32:
         case 64:
            var30 = 4;
            if (var2 == 64) {
               var30 = 8;
            }

            var31 = (String)var1.getValue();

            try {
               var6 = (byte[])var31.getBytes("UTF-8");
            } catch (UnsupportedEncodingException var25) {
               throw new IllegalArgumentException("Unsupported encoding UTF-8");
            }

            long var9;
            if ((var9 = (long)var6.length) <= 255L) {
               (var5 = new byte[2])[0] = (byte)(var30 << 3);
               var5[0] |= 5;
               var5[1] = (byte)((int)var9);
            } else if (var9 <= 65535L) {
               (var5 = new byte[3])[0] = (byte)(var30 << 3);
               var5[0] |= 6;
               var5[1] = (byte)((int)((var9 & 65280L) >> 8));
               var5[2] = (byte)((int)(var9 & 255L));
            } else {
               if (var9 > 16777215L) {
                  throw new IllegalArgumentException("Invalid length");
               }

               (var5 = new byte[4])[0] = (byte)(var30 << 3);
               var5[0] |= 7;
               var5[1] = (byte)((int)((var9 & 16711680L) >> 16));
               var5[2] = (byte)((int)((var9 & 65280L) >> 8));
               var5[3] = (byte)((int)(var9 & 255L));
            }
            break;
         case 40:
            (var5 = new byte[1])[0] = 40;
            var6 = new byte[1];
            if (var1.getBoolean()) {
               var6[0] = 1;
            }
            break;
         case 48:
         case 56:
            if (this.hf == null) {
               this.hf = new Vector();
            } else if (this.hf.contains(var1)) {
               throw new IllegalArgumentException("Recursive object->service record has incorrect format");
            }

            this.hf.insertElementAt(var1, this.hf.size());
            var5 = null;
            SerializedCommonServiceRecord var26 = this;
            if (var1 == null) {
               throw new NullPointerException("dataValue is null");
            }

            byte var7;
            switch((byte)var1.getDataType()) {
            case 48:
               var7 = 6;
               break;
            case 56:
               var7 = 7;
               break;
            default:
               throw new IllegalArgumentException("dataValue has invalid type");
            }

            byte[] var28 = null;
            Enumeration var27;
            if (!(var27 = (Enumeration)var1.getValue()).hasMoreElements()) {
               (var28 = new byte[1])[0] = 0;
            } else {
               while(var27.hasMoreElements()) {
                  DataElement var3 = (DataElement)var27.nextElement();
                  byte[] var29 = var26.parserSerializeDataValue(var3);
                  var28 = a(var28, var29);
               }

               byte[] var8;
               long var23;
               if ((var23 = (long)var28.length) <= 255L) {
                  (var8 = new byte[2])[0] = (byte)(var7 << 3);
                  var8[0] |= 5;
                  var8[1] = (byte)((int)var23);
               } else if (var23 <= 65535L) {
                  (var8 = new byte[3])[0] = (byte)(var7 << 3);
                  var8[0] |= 6;
                  var8[1] = (byte)((int)((var23 & 65280L) >> 8));
                  var8[2] = (byte)((int)(var23 & 255L));
               } else {
                  if (var23 > 16777215L) {
                     throw new IllegalArgumentException("Invalid length");
                  }

                  (var8 = new byte[4])[0] = (byte)(var7 << 3);
                  var8[0] |= 7;
                  var8[1] = (byte)((int)((var23 & 16711680L) >> 16));
                  var8[2] = (byte)((int)((var23 & 65280L) >> 8));
                  var8[3] = (byte)((int)(var23 & 255L));
               }

               var28 = a(var8, var28);
            }

            var6 = var28;
         }

         return a(var5, var6);
      }
   }

   public final byte[] parserSerialize() throws IllegalArgumentException, NullPointerException {
      synchronized(this.serializeLock) {
         byte[] var2 = null;
         int[] var3;
         if ((var3 = this.getAttributeIDs()).length == 0) {
            throw new IllegalStateException("Parsing error");
         } else {
            try {
               for(int var5 = 0; var5 < var3.length; ++var5) {
                  byte[] var4;
                  (var4 = new byte[2])[0] = (byte)(var3[var5] >> 8);
                  var4[1] = (byte)var3[var5];
                  var2 = a(var2, var4);
                  DataElement var8 = this.getAttributeValue(var3[var5]);
                  this.hf = null;
                  var4 = this.parserSerializeDataValue(var8);
                  this.hf = null;
                  var2 = a(var2, var4);
               }
            } catch (Exception var6) {
               throw new IllegalStateException("Parsing error");
            }

            return var2;
         }
      }
   }

   protected final void parserSetAttributes(byte[] var1) throws IllegalArgumentException, NullPointerException {
      synchronized(this.serializeLock) {
         if (var1 == null) {
            throw new NullPointerException("Parsing error");
         } else {
            int var3 = 0;
            int var4 = var1.length;

            while(var3 < var4) {
               try {
                  if (var1 == null) {
                     throw new NullPointerException("Parsing error");
                  }

                  if (var3 < 0 || var3 + 2 <= 0 || var3 + 2 > var1.length) {
                     throw new IllegalArgumentException("Parsing error");
                  }

                  int var5 = var1[var3] << 8 & '\uff00' | var1[var3 + 1] & 255;
                  int var7 = this.b(var1, var3 + 2);
                  DataElement var6 = this.parserGetAttrValue(var1, var3 + 2, var7);
                  this.updateAttributeValue(var5, var6);
                  if ((var3 = var3 + 2 + var7) <= 0) {
                     throw new IllegalArgumentException("Parse error");
                  }
               } catch (Exception var8) {
                  throw new IllegalArgumentException("Incorrect format of the serviceRecordStream");
               }
            }

            if (var3 > var4) {
               throw new IllegalArgumentException("Incorrect format of the serviceRecordStream");
            }
         }
      }
   }
}
