package com.nokia.mid.impl.isa.bluetooth;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.DataElement;
import javax.bluetooth.UUID;

public abstract class SerializedCommonServiceRecord extends CommonServiceRecord {
   private static final int ATTRIBUTE_ID_SIZE = 2;
   private static final int DATA_ELEMENT_SIZE_MASK = 7;
   private static final int DATA_ELEMENT_SIZE_POS = 0;
   private static final int DATA_ELEMENT_TYPE_MASK = 248;
   private static final int DATA_ELEMENT_TYPE_POS = 3;
   private static final byte DATA_ELEMENT_TYPE_NULL = 0;
   private static final byte DATA_ELEMENT_TYPE_UINT = 1;
   private static final byte DATA_ELEMENT_TYPE_INT = 2;
   private static final byte DATA_ELEMENT_TYPE_UUID = 3;
   private static final byte DATA_ELEMENT_TYPE_STRING = 4;
   private static final byte DATA_ELEMENT_TYPE_BOOLEAN = 5;
   private static final byte DATA_ELEMENT_TYPE_DSEQ = 6;
   private static final byte DATA_ELEMENT_TYPE_DALT = 7;
   private static final byte DATA_ELEMENT_TYPE_URL = 8;
   private static final String DEFAULT_ENCODING_STRING = "UTF-8";
   private Vector parsedDSEQorDALT = null;

   SerializedCommonServiceRecord() {
   }

   private final int parserGetAttrID(byte[] var1, int var2) throws IllegalArgumentException, NullPointerException {
      if (var1 == null) {
         throw new NullPointerException("Parsing error");
      } else if (var2 >= 0 && var2 + 2 > 0 && var2 + 2 <= var1.length) {
         return var1[var2] << 8 & '\uff00' | var1[var2 + 1] & 255;
      } else {
         throw new IllegalArgumentException("Parsing error");
      }
   }

   private final int parserGetAttrType(byte var1) {
      return (var1 & 248) >> 3;
   }

   private final int parserGetHeaderLength(byte var1) {
      byte[] var2 = new byte[]{1, 1, 1, 1, 1, 2, 3, 4};
      byte var3 = (byte)((var1 & 7) >> 0);
      return var2[var3] & 255;
   }

   private final int parserGetAttrLength(byte[] var1, int var2) throws IllegalArgumentException, NullPointerException {
      int var3 = 0;
      if (var1 == null) {
         throw new NullPointerException("Parsing error");
      } else if (var2 >= 0 && var2 < var1.length) {
         byte var4 = (byte)((var1[var2] & 7) >> 0);
         switch(var4) {
         case 0:
            var3 = this.parserGetAttrType(var1[var2]) == 0 ? 1 : 2;
            break;
         case 1:
            var3 = 3;
            break;
         case 2:
            var3 = 5;
            break;
         case 3:
            var3 = 9;
            break;
         case 4:
            var3 = 17;
            break;
         case 5:
            if (var1.length < 1 || var2 >= var1.length - 1) {
               throw new IllegalArgumentException("Parse error");
            }

            var3 = var1[var2 + 1] & 255;
            var3 += 2;
            break;
         case 6:
            if (var1.length < 2 || var2 >= var1.length - 2) {
               throw new IllegalArgumentException("Parse error");
            }

            var3 = var1[var2 + 1] << 8 & '\uff00' | var1[var2 + 2] & 255;
            var3 += 3;
            break;
         case 7:
            if (var1.length < 3 || var2 >= var1.length - 3) {
               throw new IllegalArgumentException("Parse error");
            }

            var3 = var1[var2 + 1] << 16 & 16711680 | var1[var2 + 2] << 8 & '\uff00' | var1[var2 + 3] & 255;
            var3 += 4;
         }

         return var3;
      } else {
         throw new IllegalArgumentException("Parsing error");
      }
   }

   protected final DataElement parserGetAttrValue(byte[] var1, int var2, int var3) throws IllegalArgumentException, NullPointerException {
      DataElement var4 = null;
      if (var1 == null) {
         throw new NullPointerException("Parsing error");
      } else if (var2 >= 0 && var2 + var3 > 0 && var2 + var3 <= var1.length) {
         int var5 = this.parserGetAttrType(var1[var2]);
         int var6 = this.parserGetHeaderLength(var1[var2]);
         int var7 = var3 - var6;
         if (var7 < 0) {
            throw new IllegalArgumentException("Parsing error");
         } else {
            long var8;
            switch(var5) {
            case 0:
               if (var7 != 0) {
                  throw new IllegalArgumentException("Parse error");
               }

               var4 = new DataElement(0);
               break;
            case 1:
            case 2:
               byte[] var10;
               switch(var7) {
               case 1:
                  var8 = (long)var1[var2 + var6] & 255L;
                  if (var5 == 1) {
                     var4 = new DataElement(8, var8);
                  } else {
                     var4 = new DataElement(16, var8);
                  }

                  return var4;
               case 2:
                  var8 = (long)var1[var2 + var6] << 8 & 65280L | (long)var1[var2 + var6 + 1] & 255L;
                  if (var5 == 1) {
                     var4 = new DataElement(9, var8);
                  } else {
                     var4 = new DataElement(17, var8);
                  }

                  return var4;
               case 4:
                  var8 = (long)var1[var2 + var6] << 24 & -16777216L | (long)var1[var2 + var6 + 1] << 16 & 16711680L | (long)var1[var2 + var6 + 2] << 8 & 65280L | (long)var1[var2 + var6 + 3] & 255L;
                  if (var5 == 1) {
                     var4 = new DataElement(10, var8);
                  } else {
                     var4 = new DataElement(18, var8);
                  }

                  return var4;
               case 8:
                  if (var5 == 1) {
                     var10 = new byte[8];
                     System.arraycopy(var1, var2 + var6, var10, 0, 8);
                     var4 = new DataElement(11, var10);
                  } else {
                     var8 = (long)var1[var2 + var6] & 255L;

                     for(int var20 = 1; var20 < 8; ++var20) {
                        var8 <<= 8;
                        var8 |= (long)var1[var2 + var6 + var20] & 255L;
                     }

                     var4 = new DataElement(19, var8);
                  }

                  return var4;
               case 16:
                  var10 = new byte[16];
                  System.arraycopy(var1, var2 + var6, var10, 0, 16);
                  if (var5 == 1) {
                     var4 = new DataElement(12, var10);
                  } else {
                     var4 = new DataElement(20, var10);
                  }

                  return var4;
               default:
                  throw new IllegalArgumentException("Parse error");
               }
            case 3:
               UUID var14;
               switch(var7) {
               case 2:
                  var8 = (long)var1[var2 + var6] << 8 & 65280L | (long)var1[var2 + var6 + 1] & 255L;
                  var14 = new UUID(var8);
                  break;
               case 4:
                  var8 = (long)var1[var2 + var6] << 24 & -16777216L | (long)var1[var2 + var6 + 1] << 16 & 16711680L | (long)var1[var2 + var6 + 2] << 8 & 65280L | (long)var1[var2 + var6 + 3] & 255L;
                  var14 = new UUID(var8);
                  break;
               case 16:
                  String var15 = new String("");

                  for(int var16 = 0; var16 < 16; ++var16) {
                     byte var17 = var1[var2 + var6 + var16];
                     var15 = var15 + Integer.toHexString((var17 & 240) >> 4 & 255);
                     var15 = var15 + Integer.toHexString(var17 & 15 & 255);
                  }

                  var14 = new UUID(var15, false);
                  break;
               default:
                  throw new IllegalArgumentException("Parse error");
               }

               var4 = new DataElement(24, var14);
               break;
            case 4:
               String var12 = null;

               try {
                  var12 = new String(var1, var2 + var6, var7, "UTF-8");
               } catch (UnsupportedEncodingException var19) {
                  throw new IllegalArgumentException("Unsupported encoding UTF-8");
               }

               var4 = new DataElement(32, var12);
               break;
            case 5:
               if (var7 != 1) {
                  throw new IllegalArgumentException("Parse error");
               }

               boolean var11 = false;
               if (var1[var2 + var6] != 0) {
                  var11 = true;
               }

               var4 = new DataElement(var11);
               break;
            case 6:
            case 7:
               if (var5 == 6) {
                  var4 = this.parserDeserializeDataSeqOrAlt(48, var1, var2 + var6, var7);
               } else {
                  var4 = this.parserDeserializeDataSeqOrAlt(56, var1, var2 + var6, var7);
               }
               break;
            case 8:
               if (var7 == 0) {
                  throw new IllegalArgumentException("Parse error");
               }

               String var13 = null;

               try {
                  var13 = new String(var1, var2 + var6, var7, "UTF-8");
               } catch (UnsupportedEncodingException var18) {
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

   private final DataElement parserDeserializeDataSeqOrAlt(int var1, byte[] var2, int var3, int var4) throws IllegalArgumentException, NullPointerException {
      if (var2 == null) {
         throw new NullPointerException("Parsing error");
      } else {
         int var5 = var3 + var4;
         if (var3 >= 0 && var5 >= 0 && var5 <= var2.length) {
            DataElement var6 = new DataElement(var1);
            int var7 = var3;

            do {
               if (var7 >= var5) {
                  if (var7 > var5) {
                     throw new IllegalArgumentException("Parse error");
                  }

                  return var6;
               }

               int var8 = this.parserGetAttrLength(var2, var7);
               DataElement var9 = this.parserGetAttrValue(var2, var7, var8);
               var6.insertElementAt(var9, var6.getSize());
               var7 += var8;
            } while(var7 > 0);

            throw new IllegalArgumentException("Parse error");
         } else {
            throw new IllegalArgumentException("Parsing error");
         }
      }
   }

   private final byte[] parserConcatenateByteStreams(byte[] var1, byte[] var2) {
      byte[] var3 = null;
      if (var1 == null) {
         if (var2 != null) {
            var3 = new byte[var2.length];
            System.arraycopy(var2, 0, var3, 0, var2.length);
         }
      } else if (var2 == null) {
         var3 = new byte[var1.length];
         System.arraycopy(var1, 0, var3, 0, var1.length);
      } else {
         if (var1.length + var2.length <= 0) {
            throw new IllegalArgumentException("Parse error");
         }

         var3 = new byte[var1.length + var2.length];
         System.arraycopy(var1, 0, var3, 0, var1.length);
         System.arraycopy(var2, 0, var3, var1.length, var2.length);
      }

      return var3;
   }

   protected final byte[] parserSerializeDataValue(DataElement var1) throws IllegalArgumentException, NullPointerException {
      byte[] var7 = null;
      byte[] var8 = null;
      if (var1 == null) {
         throw new NullPointerException("dataValue is null");
      } else {
         byte var2 = (byte)var1.getDataType();
         long var3;
         String var5;
         byte var6;
         switch(var2) {
         case 0:
            var7 = new byte[]{0};
            var8 = null;
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
            var6 = 2;
            if (var2 != 16) {
               var6 = 1;
            }

            var7 = new byte[]{(byte)(var6 << 3)};
            var3 = var1.getLong();
            var8 = new byte[]{(byte)((int)(var3 & 255L))};
            break;
         case 9:
         case 17:
            var6 = 2;
            if (var2 != 17) {
               var6 = 1;
            }

            var7 = new byte[]{(byte)(var6 << 3)};
            var7[0] = (byte)(var7[0] | 1);
            var3 = var1.getLong();
            var8 = new byte[]{(byte)((int)((var3 & 65280L) >> 8)), (byte)((int)(var3 & 255L))};
            break;
         case 10:
         case 18:
            var6 = 2;
            if (var2 != 18) {
               var6 = 1;
            }

            var7 = new byte[]{(byte)(var6 << 3)};
            var7[0] = (byte)(var7[0] | 2);
            var3 = var1.getLong();
            var8 = new byte[]{(byte)((int)((var3 & -16777216L) >> 24)), (byte)((int)((var3 & 16711680L) >> 16)), (byte)((int)((var3 & 65280L) >> 8)), (byte)((int)(var3 & 255L))};
            break;
         case 11:
            var7 = new byte[]{8};
            var7[0] = (byte)(var7[0] | 3);
            var8 = (byte[])var1.getValue();
            break;
         case 12:
         case 20:
            var6 = 2;
            if (var2 != 20) {
               var6 = 1;
            }

            var7 = new byte[]{(byte)(var6 << 3)};
            var7[0] = (byte)(var7[0] | 4);
            var8 = (byte[])var1.getValue();
            break;
         case 19:
            var7 = new byte[]{16};
            var7[0] = (byte)(var7[0] | 3);
            var3 = var1.getLong();
            var8 = new byte[]{(byte)((int)((var3 & -72057594037927936L) >> 56)), (byte)((int)((var3 & 71776119061217280L) >> 48)), (byte)((int)((var3 & 280375465082880L) >> 40)), (byte)((int)((var3 & 1095216660480L) >> 32)), (byte)((int)((var3 & 4278190080L) >> 24)), (byte)((int)((var3 & 16711680L) >> 16)), (byte)((int)((var3 & 65280L) >> 8)), (byte)((int)(var3 & 255L))};
            break;
         case 24:
            String var11 = new String("00000000000000000000000000000000");
            String var12 = ((UUID)var1.getValue()).toString();
            var5 = new String(var11.substring(0, 32 - var12.length()).concat(var12));
            var7 = new byte[]{24};
            var7[0] = (byte)(var7[0] | 4);
            var8 = new byte[16];

            for(int var13 = 0; var13 < 32; var13 += 2) {
               var8[var13 / 2] = (byte)(var8[var13 / 2] | Integer.parseInt(var5.substring(var13, var13 + 2), 16));
            }

            return this.parserConcatenateByteStreams(var7, var8);
         case 32:
         case 64:
            var6 = 4;
            if (var2 == 64) {
               var6 = 8;
            }

            var5 = (String)var1.getValue();

            try {
               var8 = (byte[])var5.getBytes("UTF-8");
            } catch (UnsupportedEncodingException var14) {
               throw new IllegalArgumentException("Unsupported encoding UTF-8");
            }

            long var9 = (long)var8.length;
            if (var9 <= 255L) {
               var7 = new byte[]{(byte)(var6 << 3), 0};
               var7[0] = (byte)(var7[0] | 5);
               var7[1] = (byte)((int)var9);
            } else if (var9 <= 65535L) {
               var7 = new byte[]{(byte)(var6 << 3), 0, 0};
               var7[0] = (byte)(var7[0] | 6);
               var7[1] = (byte)((int)((var9 & 65280L) >> 8));
               var7[2] = (byte)((int)(var9 & 255L));
            } else {
               if (var9 > 16777215L) {
                  throw new IllegalArgumentException("Invalid length");
               }

               var7 = new byte[4];
               var7[0] = (byte)(var6 << 3);
               var7[0] = (byte)(var7[0] | 7);
               var7[1] = (byte)((int)((var9 & 16711680L) >> 16));
               var7[2] = (byte)((int)((var9 & 65280L) >> 8));
               var7[3] = (byte)((int)(var9 & 255L));
            }
            break;
         case 40:
            var7 = new byte[]{40};
            var8 = new byte[1];
            if (var1.getBoolean()) {
               var8[0] = 1;
            }
            break;
         case 48:
         case 56:
            if (this.parsedDSEQorDALT == null) {
               this.parsedDSEQorDALT = new Vector();
            } else if (this.parsedDSEQorDALT.contains(var1)) {
               throw new IllegalArgumentException("Recursive object->service record has incorrect format");
            }

            this.parsedDSEQorDALT.insertElementAt(var1, this.parsedDSEQorDALT.size());
            var7 = null;
            var8 = this.parserSerializeDSEQorDALTTValue(var1);
         }

         return this.parserConcatenateByteStreams(var7, var8);
      }
   }

   private final byte[] parserSerializeDSEQorDALTTValue(DataElement var1) {
      if (var1 == null) {
         throw new NullPointerException("dataValue is null");
      } else {
         byte var3 = (byte)var1.getDataType();
         byte var2;
         switch(var3) {
         case 48:
            var2 = 6;
            break;
         case 56:
            var2 = 7;
            break;
         default:
            throw new IllegalArgumentException("dataValue has invalid type");
         }

         byte[] var4 = null;
         Enumeration var5 = (Enumeration)var1.getValue();
         if (!var5.hasMoreElements()) {
            var4 = new byte[]{0};
         } else {
            while(var5.hasMoreElements()) {
               DataElement var6 = (DataElement)var5.nextElement();
               byte[] var7 = this.parserSerializeDataValue(var6);
               var4 = this.parserConcatenateByteStreams(var4, var7);
            }

            long var9 = (long)var4.length;
            byte[] var8;
            if (var9 <= 255L) {
               var8 = new byte[]{(byte)(var2 << 3), 0};
               var8[0] = (byte)(var8[0] | 5);
               var8[1] = (byte)((int)var9);
            } else if (var9 <= 65535L) {
               var8 = new byte[]{(byte)(var2 << 3), 0, 0};
               var8[0] = (byte)(var8[0] | 6);
               var8[1] = (byte)((int)((var9 & 65280L) >> 8));
               var8[2] = (byte)((int)(var9 & 255L));
            } else {
               if (var9 > 16777215L) {
                  throw new IllegalArgumentException("Invalid length");
               }

               var8 = new byte[4];
               var8[0] = (byte)(var2 << 3);
               var8[0] = (byte)(var8[0] | 7);
               var8[1] = (byte)((int)((var9 & 16711680L) >> 16));
               var8[2] = (byte)((int)((var9 & 65280L) >> 8));
               var8[3] = (byte)((int)(var9 & 255L));
            }

            var4 = this.parserConcatenateByteStreams(var8, var4);
         }

         return var4;
      }
   }

   public final byte[] parserSerialize() throws IllegalArgumentException, NullPointerException {
      synchronized(this.serializeLock) {
         byte[] var2 = null;
         int[] var3 = this.getAttributeIDs();
         if (var3.length == 0) {
            throw new IllegalStateException("Parsing error");
         } else {
            try {
               for(int var7 = 0; var7 < var3.length; ++var7) {
                  byte[] var6 = new byte[]{(byte)((var3[var7] & '\uff00') >> 8), (byte)(var3[var7] & 255)};
                  var2 = this.parserConcatenateByteStreams(var2, var6);
                  DataElement var4 = this.getAttributeValue(var3[var7]);
                  this.parsedDSEQorDALT = null;
                  var6 = this.parserSerializeDataValue(var4);
                  this.parsedDSEQorDALT = null;
                  var2 = this.parserConcatenateByteStreams(var2, var6);
               }
            } catch (Exception var9) {
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
                  int var5 = this.parserGetAttrID(var1, var3);
                  int var7 = this.parserGetAttrLength(var1, var3 + 2);
                  DataElement var6 = this.parserGetAttrValue(var1, var3 + 2, var7);
                  this.updateAttributeValue(var5, var6);
                  var3 = var3 + 2 + var7;
                  if (var3 <= 0) {
                     throw new IllegalArgumentException("Parse error");
                  }
               } catch (Exception var10) {
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
