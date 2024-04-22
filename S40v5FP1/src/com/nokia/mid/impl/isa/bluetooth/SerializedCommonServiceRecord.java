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

   private final int parserGetAttrID(byte[] serviceRecordStream, int pos) throws IllegalArgumentException, NullPointerException {
      if (serviceRecordStream == null) {
         throw new NullPointerException("Parsing error");
      } else if (pos >= 0 && pos + 2 > 0 && pos + 2 <= serviceRecordStream.length) {
         return serviceRecordStream[pos] << 8 & '\uff00' | serviceRecordStream[pos + 1] & 255;
      } else {
         throw new IllegalArgumentException("Parsing error");
      }
   }

   private final int parserGetAttrType(byte attrHeader) {
      return (attrHeader & 248) >> 3;
   }

   private final int parserGetHeaderLength(byte attrHeader) {
      byte[] lengthIndexToHeaderSizeMapTable = new byte[]{1, 1, 1, 1, 1, 2, 3, 4};
      byte attrLengthIndex = (byte)((attrHeader & 7) >> 0);
      return lengthIndexToHeaderSizeMapTable[attrLengthIndex] & 255;
   }

   private final int parserGetAttrLength(byte[] serviceRecordStream, int pos) throws IllegalArgumentException, NullPointerException {
      int res = 0;
      if (serviceRecordStream == null) {
         throw new NullPointerException("Parsing error");
      } else if (pos >= 0 && pos < serviceRecordStream.length) {
         byte attrLengthIndex = (byte)((serviceRecordStream[pos] & 7) >> 0);
         switch(attrLengthIndex) {
         case 0:
            res = this.parserGetAttrType(serviceRecordStream[pos]) == 0 ? 1 : 2;
            break;
         case 1:
            res = 3;
            break;
         case 2:
            res = 5;
            break;
         case 3:
            res = 9;
            break;
         case 4:
            res = 17;
            break;
         case 5:
            if (serviceRecordStream.length < 1 || pos >= serviceRecordStream.length - 1) {
               throw new IllegalArgumentException("Parse error");
            }

            res = serviceRecordStream[pos + 1] & 255;
            res += 2;
            break;
         case 6:
            if (serviceRecordStream.length < 2 || pos >= serviceRecordStream.length - 2) {
               throw new IllegalArgumentException("Parse error");
            }

            res = serviceRecordStream[pos + 1] << 8 & '\uff00' | serviceRecordStream[pos + 2] & 255;
            res += 3;
            break;
         case 7:
            if (serviceRecordStream.length < 3 || pos >= serviceRecordStream.length - 3) {
               throw new IllegalArgumentException("Parse error");
            }

            res = serviceRecordStream[pos + 1] << 16 & 16711680 | serviceRecordStream[pos + 2] << 8 & '\uff00' | serviceRecordStream[pos + 3] & 255;
            res += 4;
         }

         return res;
      } else {
         throw new IllegalArgumentException("Parsing error");
      }
   }

   protected final DataElement parserGetAttrValue(byte[] serviceRecordStream, int pos, int length) throws IllegalArgumentException, NullPointerException {
      DataElement res = null;
      if (serviceRecordStream == null) {
         throw new NullPointerException("Parsing error");
      } else if (pos >= 0 && pos + length > 0 && pos + length <= serviceRecordStream.length) {
         int type = this.parserGetAttrType(serviceRecordStream[pos]);
         int hdrLength = this.parserGetHeaderLength(serviceRecordStream[pos]);
         int attrValueLength = length - hdrLength;
         if (attrValueLength < 0) {
            throw new IllegalArgumentException("Parsing error");
         } else {
            long longValue;
            switch(type) {
            case 0:
               if (attrValueLength != 0) {
                  throw new IllegalArgumentException("Parse error");
               }

               res = new DataElement(0);
               break;
            case 1:
            case 2:
               byte[] streamValue;
               switch(attrValueLength) {
               case 1:
                  longValue = (long)serviceRecordStream[pos + hdrLength] & 255L;
                  if (type == 1) {
                     res = new DataElement(8, longValue);
                  } else {
                     res = new DataElement(16, longValue);
                  }

                  return res;
               case 2:
                  longValue = (long)serviceRecordStream[pos + hdrLength] << 8 & 65280L | (long)serviceRecordStream[pos + hdrLength + 1] & 255L;
                  if (type == 1) {
                     res = new DataElement(9, longValue);
                  } else {
                     res = new DataElement(17, longValue);
                  }

                  return res;
               case 4:
                  longValue = (long)serviceRecordStream[pos + hdrLength] << 24 & -16777216L | (long)serviceRecordStream[pos + hdrLength + 1] << 16 & 16711680L | (long)serviceRecordStream[pos + hdrLength + 2] << 8 & 65280L | (long)serviceRecordStream[pos + hdrLength + 3] & 255L;
                  if (type == 1) {
                     res = new DataElement(10, longValue);
                  } else {
                     res = new DataElement(18, longValue);
                  }

                  return res;
               case 8:
                  if (type == 1) {
                     streamValue = new byte[8];
                     System.arraycopy(serviceRecordStream, pos + hdrLength, streamValue, 0, 8);
                     res = new DataElement(11, streamValue);
                  } else {
                     longValue = (long)serviceRecordStream[pos + hdrLength] & 255L;

                     for(int i = 1; i < 8; ++i) {
                        longValue <<= 8;
                        longValue |= (long)serviceRecordStream[pos + hdrLength + i] & 255L;
                     }

                     res = new DataElement(19, longValue);
                  }

                  return res;
               case 16:
                  streamValue = new byte[16];
                  System.arraycopy(serviceRecordStream, pos + hdrLength, streamValue, 0, 16);
                  if (type == 1) {
                     res = new DataElement(12, streamValue);
                  } else {
                     res = new DataElement(20, streamValue);
                  }

                  return res;
               default:
                  throw new IllegalArgumentException("Parse error");
               }
            case 3:
               UUID var14;
               switch(attrValueLength) {
               case 2:
                  longValue = (long)serviceRecordStream[pos + hdrLength] << 8 & 65280L | (long)serviceRecordStream[pos + hdrLength + 1] & 255L;
                  var14 = new UUID(longValue);
                  break;
               case 4:
                  longValue = (long)serviceRecordStream[pos + hdrLength] << 24 & -16777216L | (long)serviceRecordStream[pos + hdrLength + 1] << 16 & 16711680L | (long)serviceRecordStream[pos + hdrLength + 2] << 8 & 65280L | (long)serviceRecordStream[pos + hdrLength + 3] & 255L;
                  var14 = new UUID(longValue);
                  break;
               case 16:
                  String stringUUID = new String("");

                  for(int i = 0; i < 16; ++i) {
                     byte byteValue = serviceRecordStream[pos + hdrLength + i];
                     stringUUID = stringUUID + Integer.toHexString((byteValue & 240) >> 4 & 255);
                     stringUUID = stringUUID + Integer.toHexString(byteValue & 15 & 255);
                  }

                  var14 = new UUID(stringUUID, false);
                  break;
               default:
                  throw new IllegalArgumentException("Parse error");
               }

               res = new DataElement(24, var14);
               break;
            case 4:
               String stringValue = null;

               try {
                  stringValue = new String(serviceRecordStream, pos + hdrLength, attrValueLength, "UTF-8");
               } catch (UnsupportedEncodingException var19) {
                  throw new IllegalArgumentException("Unsupported encoding UTF-8");
               }

               res = new DataElement(32, stringValue);
               break;
            case 5:
               if (attrValueLength != 1) {
                  throw new IllegalArgumentException("Parse error");
               }

               boolean booleanValue = false;
               if (serviceRecordStream[pos + hdrLength] != 0) {
                  booleanValue = true;
               }

               res = new DataElement(booleanValue);
               break;
            case 6:
            case 7:
               if (type == 6) {
                  res = this.parserDeserializeDataSeqOrAlt(48, serviceRecordStream, pos + hdrLength, attrValueLength);
               } else {
                  res = this.parserDeserializeDataSeqOrAlt(56, serviceRecordStream, pos + hdrLength, attrValueLength);
               }
               break;
            case 8:
               if (attrValueLength == 0) {
                  throw new IllegalArgumentException("Parse error");
               }

               String urlValue = null;

               try {
                  urlValue = new String(serviceRecordStream, pos + hdrLength, attrValueLength, "UTF-8");
               } catch (UnsupportedEncodingException var18) {
                  throw new IllegalArgumentException("Unsupported encoding UTF-8");
               }

               res = new DataElement(64, urlValue);
            }

            return res;
         }
      } else {
         throw new IllegalArgumentException("Parsing error");
      }
   }

   private final DataElement parserDeserializeDataSeqOrAlt(int type, byte[] serviceRecordStream, int pos, int length) throws IllegalArgumentException, NullPointerException {
      if (serviceRecordStream == null) {
         throw new NullPointerException("Parsing error");
      } else {
         int lastIndex = pos + length;
         if (pos >= 0 && lastIndex >= 0 && lastIndex <= serviceRecordStream.length) {
            DataElement res = new DataElement(type);
            int current = pos;

            do {
               if (current >= lastIndex) {
                  if (current > lastIndex) {
                     throw new IllegalArgumentException("Parse error");
                  }

                  return res;
               }

               int attrLengthValue = this.parserGetAttrLength(serviceRecordStream, current);
               DataElement attrValue = this.parserGetAttrValue(serviceRecordStream, current, attrLengthValue);
               res.insertElementAt(attrValue, res.getSize());
               current += attrLengthValue;
            } while(current > 0);

            throw new IllegalArgumentException("Parse error");
         } else {
            throw new IllegalArgumentException("Parsing error");
         }
      }
   }

   private final byte[] parserConcatenateByteStreams(byte[] firstStream, byte[] secondStream) {
      byte[] res = null;
      if (firstStream == null) {
         if (secondStream != null) {
            res = new byte[secondStream.length];
            System.arraycopy(secondStream, 0, res, 0, secondStream.length);
         }
      } else if (secondStream == null) {
         res = new byte[firstStream.length];
         System.arraycopy(firstStream, 0, res, 0, firstStream.length);
      } else {
         if (firstStream.length + secondStream.length <= 0) {
            throw new IllegalArgumentException("Parse error");
         }

         res = new byte[firstStream.length + secondStream.length];
         System.arraycopy(firstStream, 0, res, 0, firstStream.length);
         System.arraycopy(secondStream, 0, res, firstStream.length, secondStream.length);
      }

      return res;
   }

   protected final byte[] parserSerializeDataValue(DataElement dataValue) throws IllegalArgumentException, NullPointerException {
      byte[] header = null;
      byte[] dataStream = null;
      if (dataValue == null) {
         throw new NullPointerException("dataValue is null");
      } else {
         byte dataType = (byte)dataValue.getDataType();
         long longValue;
         String stringValue;
         byte sdpDataType;
         switch(dataType) {
         case 0:
            header = new byte[]{0};
            dataStream = null;
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
            sdpDataType = 2;
            if (dataType != 16) {
               sdpDataType = 1;
            }

            header = new byte[]{(byte)(sdpDataType << 3)};
            longValue = dataValue.getLong();
            dataStream = new byte[]{(byte)((int)(longValue & 255L))};
            break;
         case 9:
         case 17:
            sdpDataType = 2;
            if (dataType != 17) {
               sdpDataType = 1;
            }

            header = new byte[]{(byte)(sdpDataType << 3)};
            header[0] = (byte)(header[0] | 1);
            longValue = dataValue.getLong();
            dataStream = new byte[]{(byte)((int)((longValue & 65280L) >> 8)), (byte)((int)(longValue & 255L))};
            break;
         case 10:
         case 18:
            sdpDataType = 2;
            if (dataType != 18) {
               sdpDataType = 1;
            }

            header = new byte[]{(byte)(sdpDataType << 3)};
            header[0] = (byte)(header[0] | 2);
            longValue = dataValue.getLong();
            dataStream = new byte[]{(byte)((int)((longValue & -16777216L) >> 24)), (byte)((int)((longValue & 16711680L) >> 16)), (byte)((int)((longValue & 65280L) >> 8)), (byte)((int)(longValue & 255L))};
            break;
         case 11:
            header = new byte[]{8};
            header[0] = (byte)(header[0] | 3);
            dataStream = (byte[])dataValue.getValue();
            break;
         case 12:
         case 20:
            sdpDataType = 2;
            if (dataType != 20) {
               sdpDataType = 1;
            }

            header = new byte[]{(byte)(sdpDataType << 3)};
            header[0] = (byte)(header[0] | 4);
            dataStream = (byte[])dataValue.getValue();
            break;
         case 19:
            header = new byte[]{16};
            header[0] = (byte)(header[0] | 3);
            longValue = dataValue.getLong();
            dataStream = new byte[]{(byte)((int)((longValue & -72057594037927936L) >> 56)), (byte)((int)((longValue & 71776119061217280L) >> 48)), (byte)((int)((longValue & 280375465082880L) >> 40)), (byte)((int)((longValue & 1095216660480L) >> 32)), (byte)((int)((longValue & 4278190080L) >> 24)), (byte)((int)((longValue & 16711680L) >> 16)), (byte)((int)((longValue & 65280L) >> 8)), (byte)((int)(longValue & 255L))};
            break;
         case 24:
            String zeroesString = new String("00000000000000000000000000000000");
            String temp = ((UUID)dataValue.getValue()).toString();
            stringValue = new String(zeroesString.substring(0, 32 - temp.length()).concat(temp));
            header = new byte[]{24};
            header[0] = (byte)(header[0] | 4);
            dataStream = new byte[16];

            for(int i = 0; i < 32; i += 2) {
               dataStream[i / 2] = (byte)(dataStream[i / 2] | Integer.parseInt(stringValue.substring(i, i + 2), 16));
            }

            return this.parserConcatenateByteStreams(header, dataStream);
         case 32:
         case 64:
            sdpDataType = 4;
            if (dataType == 64) {
               sdpDataType = 8;
            }

            stringValue = (String)dataValue.getValue();

            try {
               dataStream = (byte[])stringValue.getBytes("UTF-8");
            } catch (UnsupportedEncodingException var14) {
               throw new IllegalArgumentException("Unsupported encoding UTF-8");
            }

            long dataLength = (long)dataStream.length;
            if (dataLength <= 255L) {
               header = new byte[]{(byte)(sdpDataType << 3), 0};
               header[0] = (byte)(header[0] | 5);
               header[1] = (byte)((int)dataLength);
            } else if (dataLength <= 65535L) {
               header = new byte[]{(byte)(sdpDataType << 3), 0, 0};
               header[0] = (byte)(header[0] | 6);
               header[1] = (byte)((int)((dataLength & 65280L) >> 8));
               header[2] = (byte)((int)(dataLength & 255L));
            } else {
               if (dataLength > 16777215L) {
                  throw new IllegalArgumentException("Invalid length");
               }

               header = new byte[4];
               header[0] = (byte)(sdpDataType << 3);
               header[0] = (byte)(header[0] | 7);
               header[1] = (byte)((int)((dataLength & 16711680L) >> 16));
               header[2] = (byte)((int)((dataLength & 65280L) >> 8));
               header[3] = (byte)((int)(dataLength & 255L));
            }
            break;
         case 40:
            header = new byte[]{40};
            dataStream = new byte[1];
            if (dataValue.getBoolean()) {
               dataStream[0] = 1;
            }
            break;
         case 48:
         case 56:
            if (this.parsedDSEQorDALT == null) {
               this.parsedDSEQorDALT = new Vector();
            } else if (this.parsedDSEQorDALT.contains(dataValue)) {
               throw new IllegalArgumentException("Recursive object->service record has incorrect format");
            }

            this.parsedDSEQorDALT.insertElementAt(dataValue, this.parsedDSEQorDALT.size());
            header = null;
            dataStream = this.parserSerializeDSEQorDALTTValue(dataValue);
         }

         return this.parserConcatenateByteStreams(header, dataStream);
      }
   }

   private final byte[] parserSerializeDSEQorDALTTValue(DataElement dataValue) {
      if (dataValue == null) {
         throw new NullPointerException("dataValue is null");
      } else {
         byte dataType = (byte)dataValue.getDataType();
         byte var2;
         switch(dataType) {
         case 48:
            var2 = 6;
            break;
         case 56:
            var2 = 7;
            break;
         default:
            throw new IllegalArgumentException("dataValue has invalid type");
         }

         byte[] res = null;
         Enumeration elements = (Enumeration)dataValue.getValue();
         if (!elements.hasMoreElements()) {
            res = new byte[]{0};
         } else {
            while(elements.hasMoreElements()) {
               DataElement dataElementValue = (DataElement)elements.nextElement();
               byte[] dataElementStream = this.parserSerializeDataValue(dataElementValue);
               res = this.parserConcatenateByteStreams(res, dataElementStream);
            }

            long dataLength = (long)res.length;
            byte[] header;
            if (dataLength <= 255L) {
               header = new byte[]{(byte)(var2 << 3), 0};
               header[0] = (byte)(header[0] | 5);
               header[1] = (byte)((int)dataLength);
            } else if (dataLength <= 65535L) {
               header = new byte[]{(byte)(var2 << 3), 0, 0};
               header[0] = (byte)(header[0] | 6);
               header[1] = (byte)((int)((dataLength & 65280L) >> 8));
               header[2] = (byte)((int)(dataLength & 255L));
            } else {
               if (dataLength > 16777215L) {
                  throw new IllegalArgumentException("Invalid length");
               }

               header = new byte[4];
               header[0] = (byte)(var2 << 3);
               header[0] = (byte)(header[0] | 7);
               header[1] = (byte)((int)((dataLength & 16711680L) >> 16));
               header[2] = (byte)((int)((dataLength & 65280L) >> 8));
               header[3] = (byte)((int)(dataLength & 255L));
            }

            res = this.parserConcatenateByteStreams(header, res);
         }

         return res;
      }
   }

   public final byte[] parserSerialize() throws IllegalArgumentException, NullPointerException {
      synchronized(this.serializeLock) {
         byte[] res = null;
         int[] attrIDs = this.getAttributeIDs();
         if (attrIDs.length == 0) {
            throw new IllegalStateException("Parsing error");
         } else {
            try {
               for(int i = 0; i < attrIDs.length; ++i) {
                  if (attrIDs[i] != 0) {
                     byte[] attrValueStream = new byte[]{(byte)((attrIDs[i] & '\uff00') >> 8), (byte)(attrIDs[i] & 255)};
                     res = this.parserConcatenateByteStreams(res, attrValueStream);
                     DataElement attrValue = this.getAttributeValue(attrIDs[i]);
                     this.parsedDSEQorDALT = null;
                     attrValueStream = this.parserSerializeDataValue(attrValue);
                     this.parsedDSEQorDALT = null;
                     res = this.parserConcatenateByteStreams(res, attrValueStream);
                  }
               }
            } catch (Exception var8) {
               throw new IllegalStateException("Parsing error");
            }

            return res;
         }
      }
   }

   protected final void parserSetAttributes(byte[] serviceRecordStream) throws IllegalArgumentException, NullPointerException {
      synchronized(this.serializeLock) {
         if (serviceRecordStream == null) {
            throw new NullPointerException("Parsing error");
         } else {
            int current = 0;
            int streamLength = serviceRecordStream.length;

            while(current < streamLength) {
               try {
                  int attrID = this.parserGetAttrID(serviceRecordStream, current);
                  int attrLengthValue = this.parserGetAttrLength(serviceRecordStream, current + 2);
                  DataElement attrValue = this.parserGetAttrValue(serviceRecordStream, current + 2, attrLengthValue);
                  this.updateAttributeValue(attrID, attrValue);
                  current = current + 2 + attrLengthValue;
                  if (current <= 0) {
                     throw new IllegalArgumentException("Parse error");
                  }
               } catch (Exception var10) {
                  throw new IllegalArgumentException("Incorrect format of the serviceRecordStream");
               }
            }

            if (current > streamLength) {
               throw new IllegalArgumentException("Incorrect format of the serviceRecordStream");
            }
         }
      }
   }
}
