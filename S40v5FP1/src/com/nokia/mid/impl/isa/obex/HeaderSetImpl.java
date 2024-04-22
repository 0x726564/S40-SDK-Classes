package com.nokia.mid.impl.isa.obex;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.obex.HeaderSet;

public class HeaderSetImpl implements HeaderSet {
   private Hashtable publicHeaders;
   private Hashtable privateHeaders;
   private int responseCode;
   private boolean isReceived;
   private static final int TYPE_LONG = 0;
   private static final int TYPE_BYTE = 1;
   private static final int TYPE_STRING = 2;
   private static final int TYPE_CALENDAR = 3;
   private static final int TYPE_BYTE_ARRAY = 4;
   private static final int TYPE_BOOLEAN = 5;
   public static final int BODY = 72;
   public static final int END_OF_BODY = 73;
   public static final int CONNECTION_ID = 203;
   private static final int AUTH_USER_ID = 160;
   private static final int AUTH_ACCESS = 161;
   private static final int AUTH_REALM = 162;
   public static final int AUTH_USERNAME = 163;
   public static final int AUTH_PASSWORD = 164;
   private static final boolean USE_ISO8601_EXTENDED_FORMAT = false;

   public HeaderSetImpl(byte[] serializedHeaders) throws IOException {
      this(serializedHeaders, 0, serializedHeaders.length);
   }

   public HeaderSetImpl(byte[] serializedHeaders, int offset, int length) throws IOException {
      this.responseCode = -1;

      try {
         this.deserializeHeaders(serializedHeaders, offset, length);
         this.isReceived = true;
      } catch (Exception var5) {
         throw new IOException(var5.getMessage());
      }
   }

   public HeaderSetImpl() {
      this.responseCode = -1;
      this.publicHeaders = new Hashtable();
      this.privateHeaders = new Hashtable();
      this.isReceived = false;
   }

   public void includeHeaders(HeaderSetImpl incl) {
      Hashtable otherPub = incl.publicHeaders;
      Hashtable otherPriv = incl.privateHeaders;
      Enumeration e = otherPub.keys();

      Object key;
      while(e.hasMoreElements()) {
         key = e.nextElement();
         this.publicHeaders.put(key, otherPub.get(key));
      }

      e = otherPriv.keys();

      while(e.hasMoreElements()) {
         key = e.nextElement();
         this.privateHeaders.put(key, otherPriv.get(key));
      }

   }

   public void setHeaderPrivate(int headerID, Object headerValue) {
      int headerType = getHeaderType(headerID, false);
      if (headerType == -1) {
      }

      if (this.isPublicHeader(headerID)) {
         throw new IllegalArgumentException("Invalid header value");
      } else {
         if (headerValue == null) {
            this.privateHeaders.remove(new Integer(headerID));
         }

         if (!this.isValidHeaderValue(headerID, headerType, headerValue)) {
            throw new IllegalArgumentException("Invalid header value");
         } else {
            this.privateHeaders.put(new Integer(headerID), headerValue);
         }
      }
   }

   public void setHeader(int headerID, Object headerValue) {
      int headerType = getHeaderType(headerID, false);
      if (headerType == -1) {
         throw new IllegalArgumentException("Invalid header ID");
      } else if (!this.isPublicHeader(headerID)) {
         throw new IllegalArgumentException("Invalid header ID");
      } else if (headerValue == null) {
         this.publicHeaders.remove(new Integer(headerID));
      } else if (!this.isValidHeaderValue(headerID, headerType, headerValue)) {
         throw new IllegalArgumentException("Invalid header value");
      } else if (headerSize(headerID, headerType, headerValue) > 65524) {
         throw new IllegalArgumentException("Header too large");
      } else {
         if (headerID == 66) {
            try {
               this.publicHeaders.put(new Integer(headerID), ((String)headerValue).getBytes("ASCII"));
            } catch (UnsupportedEncodingException var5) {
            }
         } else {
            this.publicHeaders.put(new Integer(headerID), headerValue);
         }

      }
   }

   public Object getHeader(int headerID) throws IOException {
      if (getHeaderType(headerID, false) == -1) {
         throw new IllegalArgumentException("Invalid header ID");
      } else if (!this.isPublicHeader(headerID)) {
         throw new IllegalArgumentException("Invalid header ID");
      } else {
         Object obj = this.publicHeaders.get(new Integer(headerID));
         if (obj == null) {
            if (headerID == 79) {
               obj = this.publicHeaders.get(new Integer(81));
            }

            if (obj == null) {
               return null;
            }
         }

         if (headerID == 66) {
            try {
               return new String((byte[])obj);
            } catch (ClassCastException var4) {
               throw new IOException("Error retrieving TYPE header");
            }
         } else {
            return obj;
         }
      }
   }

   public Object getHeaderPrivate(int headerID) {
      if (this.isPublicHeader(headerID)) {
         throw new IllegalArgumentException("Invalid header ID");
      } else {
         return this.privateHeaders.get(new Integer(headerID));
      }
   }

   public int[] getHeaderList() throws IOException {
      int size = this.publicHeaders.size();
      if (size == 0) {
         return null;
      } else {
         int[] headerList = new int[size];
         Enumeration keys = this.publicHeaders.keys();

         for(int var4 = 0; keys.hasMoreElements(); headerList[var4++] = (Integer)keys.nextElement()) {
         }

         return headerList;
      }
   }

   private int[] getPrivateHeaderList() {
      int size = this.privateHeaders.size();
      if (size == 0) {
         return null;
      } else {
         int[] headerList = new int[size];
         Enumeration keys = this.privateHeaders.keys();

         for(int var4 = 0; keys.hasMoreElements(); headerList[var4++] = (Integer)keys.nextElement()) {
         }

         return headerList;
      }
   }

   public boolean containsAuthenticationChallenge() {
      return this.privateHeaders.get(new Integer(161)) != null;
   }

   public String getRealm() {
      return (String)this.privateHeaders.get(new Integer(162));
   }

   public boolean isUserId() {
      return (Boolean)this.privateHeaders.get(new Integer(160));
   }

   public boolean isFullAccess() {
      return (Boolean)this.privateHeaders.get(new Integer(161));
   }

   public void createAuthenticationChallenge(String realm, boolean userID, boolean access) {
      if (realm != null) {
         this.privateHeaders.put(new Integer(162), realm);
      }

      this.privateHeaders.put(new Integer(160), userID ? Boolean.TRUE : Boolean.FALSE);
      this.privateHeaders.put(new Integer(161), access ? Boolean.TRUE : Boolean.FALSE);
   }

   public int getResponseCode() throws IOException {
      if (this.responseCode == -1) {
         throw new IOException("Not allowed");
      } else {
         return this.responseCode;
      }
   }

   public void deserializeHeaders(byte[] serializedHeaders, int offset, int length) {
      if (serializedHeaders == null) {
         throw new NullPointerException("serialisedHeader cannot be null");
      } else if (offset + length > serializedHeaders.length) {
         throw new IllegalArgumentException("Invalid offset or length");
      } else {
         int index = offset + 1;
         this.responseCode = serializedHeaders[offset] & 255;
         int numOfHeaders = serializedHeaders[index++];
         this.publicHeaders = new Hashtable();
         this.privateHeaders = new Hashtable();

         for(int i = 0; i < numOfHeaders; ++i) {
            int headerID = serializedHeaders[index++] & 255;
            int headerLength = (int)bytesToLong(serializedHeaders, index);
            index += 4;
            Object headerValue = null;
            switch(getHeaderType(headerID, true)) {
            case 0:
               headerValue = new Long(bytesToLong(serializedHeaders, index));
               index += 4;
               break;
            case 1:
               headerValue = new Byte(serializedHeaders[index++]);
               break;
            case 2:
               try {
                  headerValue = new String(serializedHeaders, index, headerLength, "UCS-2LE");
               } catch (UnsupportedEncodingException var11) {
                  throw new IllegalArgumentException("UnsupportedEncoding");
               }

               index += headerLength;
               break;
            case 3:
               if (headerID == 196) {
                  headerValue = Calendar.getInstance();
                  ((Calendar)headerValue).setTime(new Date(bytesToLong(serializedHeaders, index) * 1000L));
                  index += 4;
               } else {
                  headerValue = this.ISO8601ToCalendar(serializedHeaders, index);
                  index += headerLength;
               }
               break;
            case 4:
               if (headerID == 66) {
                  headerValue = new byte[headerLength - 1];
                  System.arraycopy(serializedHeaders, index, headerValue, 0, headerLength - 1);
               } else {
                  headerValue = new byte[headerLength];
                  System.arraycopy(serializedHeaders, index, headerValue, 0, headerLength);
               }

               index += headerLength;
               break;
            case 5:
               headerValue = serializedHeaders[index++] == 1 ? Boolean.TRUE : Boolean.FALSE;
               break;
            default:
               throw new IllegalArgumentException("Headers corrupted");
            }

            if (headerID == 81) {
               headerID = 79;
            }

            if (this.isPublicHeader(headerID)) {
               this.publicHeaders.put(new Integer(headerID), headerValue);
            } else {
               this.privateHeaders.put(new Integer(headerID), headerValue);
            }
         }

      }
   }

   public byte[] serializeHeaders() throws IOException {
      try {
         int[] publicList = this.getHeaderList();
         int[] privateList = this.getPrivateHeaderList();
         if (publicList == null && privateList == null) {
            return null;
         } else {
            int numOfHeaders = (publicList == null ? 0 : publicList.length) + (privateList == null ? 0 : privateList.length);
            int[] allList = new int[numOfHeaders];
            if (publicList != null) {
               System.arraycopy(publicList, 0, allList, 0, publicList.length);
            }

            if (privateList != null) {
               System.arraycopy(privateList, 0, allList, publicList == null ? 0 : publicList.length, privateList.length);
            }

            Vector serializedHeaders = new Vector(numOfHeaders);
            int size = 0;

            int headerID;
            byte[] value;
            for(int i = 0; i < numOfHeaders; ++i) {
               headerID = allList[i];
               Object header;
               if (this.isPublicHeader(headerID)) {
                  header = this.getHeader(headerID);
               } else {
                  header = this.getHeaderPrivate(headerID);
               }

               byte[] value = null;
               byte[] array;
               switch(getHeaderType(headerID, true)) {
               case 0:
                  value = new byte[4];
                  longToBytes((Long)header, value, 0);
                  break;
               case 1:
                  value = new byte[]{(Byte)header};
                  break;
               case 2:
                  value = ((String)header).getBytes("UCS-2LE");
                  break;
               case 3:
                  if (headerID == 196) {
                     long longValue = ((Calendar)header).getTime().getTime() / 1000L;
                     value = new byte[4];
                     longToBytes(longValue, value, 0);
                  } else {
                     value = this.calendarToISO8601((Calendar)header, false);
                  }
                  break;
               case 4:
                  if (headerID == 66) {
                     array = ((String)header).getBytes("ASCII");
                     value = new byte[array.length + 1];
                     System.arraycopy(array, 0, value, 0, array.length);
                     value[value.length - 1] = 0;
                  } else {
                     value = (byte[])header;
                  }
                  break;
               case 5:
                  value = new byte[]{(byte)((Boolean)header ? 1 : 0)};
                  break;
               default:
                  throw new IOException("Unknown header ID: " + headerID);
               }

               array = new byte[value.length + 5];
               array[0] = (byte)headerID;
               longToBytes((long)value.length, array, 1);
               System.arraycopy(value, 0, array, 5, value.length);
               size += array.length;
               if (headerID == 203) {
                  serializedHeaders.insertElementAt(array, 0);
               } else {
                  serializedHeaders.addElement(array);
               }
            }

            byte[] array = new byte[size + 2];
            int index = 0;
            headerID = index + 1;
            array[index] = (byte)this.responseCode;
            array[headerID++] = (byte)numOfHeaders;

            for(int i = 0; i < serializedHeaders.size(); ++i) {
               value = (byte[])serializedHeaders.elementAt(i);
               System.arraycopy(value, 0, array, headerID, value.length);
               headerID += value.length;
            }

            return array;
         }
      } catch (IOException var13) {
         throw new IOException("Unable to serialize the HeaderSet");
      } catch (ArrayIndexOutOfBoundsException var14) {
         throw new IOException("Unable to serialize the HeaderSet");
      }
   }

   public static int getHeaderType(int headerID, boolean all) {
      switch(headerID) {
      case 1:
      case 5:
         return 2;
      case 66:
      case 70:
      case 71:
      case 72:
      case 73:
      case 74:
      case 76:
      case 79:
      case 81:
      case 163:
      case 164:
         return 4;
      case 68:
      case 196:
         return 3;
      case 192:
      case 195:
      case 203:
         return 0;
      default:
         if (!all || headerID != 161 && headerID != 160) {
            if (all && headerID == 162) {
               return 2;
            } else if (headerID >= 48 && headerID <= 63) {
               return 2;
            } else if (headerID >= 112 && headerID <= 127) {
               return 4;
            } else if (headerID >= 176 && headerID <= 191) {
               return 1;
            } else {
               return headerID >= 240 && headerID <= 255 ? 0 : -1;
            }
         } else {
            return 5;
         }
      }
   }

   private boolean isPublicHeader(int headerID) {
      switch(headerID) {
      case 1:
      case 5:
      case 66:
      case 68:
      case 70:
      case 71:
      case 74:
      case 76:
      case 79:
      case 81:
      case 192:
      case 195:
      case 196:
         return true;
      default:
         return headerID >= 48 && headerID <= 63 || headerID >= 112 && headerID <= 127 || headerID >= 176 && headerID <= 191 || headerID >= 240 && headerID <= 255;
      }
   }

   private boolean isValidHeaderValue(int headerID, int headerType, Object headerValue) {
      long seconds;
      switch(headerType) {
      case 0:
         if (!(headerValue instanceof Long)) {
            return false;
         }

         seconds = (Long)headerValue;
         return seconds >= 0L && seconds <= 4294967295L;
      case 1:
         return headerValue instanceof Byte;
      case 2:
         return headerValue instanceof String;
      case 3:
         if (!(headerValue instanceof Calendar)) {
            return false;
         } else {
            if (headerID == 196) {
               seconds = ((Calendar)headerValue).getTime().getTime() / 1000L;
               if (seconds > 2147483647L) {
                  return false;
               }
            }

            return true;
         }
      case 4:
         if (headerID == 66) {
            return headerValue instanceof String;
         }

         return headerValue instanceof byte[];
      case 5:
         return headerValue instanceof Boolean;
      default:
         return false;
      }
   }

   public static int headerSize(int headerID, int headerType, Object headerValue) {
      int length = 0;
      switch(headerType) {
      case 0:
         length = 4;
         break;
      case 1:
         length = 1;
         break;
      case 2:
         length = ((String)headerValue).length() * 2 + 2;
         break;
      case 3:
         length = 32;
         break;
      case 4:
         if (headerID == 66) {
            length = ((String)headerValue).length() + 1;
         } else {
            length = ((byte[])headerValue).length;
         }
         break;
      case 5:
         length = 1;
      }

      return length;
   }

   public boolean isReceivedHeaderSet() {
      return this.isReceived;
   }

   public void setResponseCode(int responseCode) {
      this.responseCode = checkResponseCode(responseCode);
   }

   public void setResponseCodePrivate(int responseCode) {
      if (responseCode == 144) {
         this.responseCode = responseCode;
      } else {
         this.setResponseCode(responseCode);
      }

   }

   private static int checkResponseCode(int code) {
      if (code != 160 && code != 161 && code != 162 && code != 163 && code != 164 && code != 165 && code != 166 && code != 176 && code != 177 && code != 178 && code != 179 && code != 180 && code != 181 && code != 192 && code != 193 && code != 194 && code != 195 && code != 196 && code != 197 && code != 198 && code != 199 && code != 200 && code != 201 && code != 202 && code != 203 && code != 204 && code != 205 && code != 206 && code != 207 && code != 208 && code != 209 && code != 210 && code != 211 && code != 212 && code != 213 && code != 224 && code != 225) {
         code = 208;
      }

      return code;
   }

   public static long bytesToLong(byte[] array, int offset) {
      if (array == null) {
         throw new NullPointerException();
      } else if (array.length < 4) {
         throw new IllegalArgumentException("Invalid byte array");
      } else if (offset >= 0 && offset + 3 < array.length) {
         long value = 0L;

         for(long i = 0L; i < 4L; ++i) {
            value += ((long)array[offset + (3 - (int)i)] & 255L) << (int)(8L * i);
         }

         return value;
      } else {
         throw new IllegalArgumentException("Invalid offset");
      }
   }

   private byte[] calendarToISO8601(Calendar calendar, boolean extended) {
      if (calendar == null) {
         throw new NullPointerException();
      } else {
         try {
            StringBuffer buffer = new StringBuffer(extended ? 25 : 20);
            buffer.append(Integer.toString(calendar.get(1)));
            if (extended) {
               buffer.append("-");
            }

            this.append2DigitNumber(buffer, calendar.get(2) + 1);
            if (extended) {
               buffer.append("-");
            }

            this.append2DigitNumber(buffer, calendar.get(5));
            buffer.append('T');
            int value = calendar.get(10);
            if (calendar.get(9) == 1) {
               value += 12;
            }

            this.append2DigitNumber(buffer, value);
            if (extended) {
               buffer.append(":");
            }

            this.append2DigitNumber(buffer, calendar.get(12));
            if (extended) {
               buffer.append(":");
            }

            this.append2DigitNumber(buffer, calendar.get(13));
            int offset = this.getGMTOffset(calendar);
            if (offset == 0) {
               buffer.append('Z');
            } else {
               if (offset > 0) {
                  buffer.append('+');
               } else {
                  buffer.append('-');
                  offset = -offset;
               }

               offset /= 1000;
               float floatOffset = (float)offset / 60.0F;
               floatOffset /= 60.0F;
               value = (int)floatOffset;
               this.append2DigitNumber(buffer, value);
               if (floatOffset - (float)value != 0.0F) {
                  if (extended) {
                     buffer.append(":");
                  }

                  this.append2DigitNumber(buffer, (int)((floatOffset - (float)value) * 60.0F));
               }
            }

            return buffer.toString().getBytes("ASCII");
         } catch (UnsupportedEncodingException var7) {
            throw new IllegalArgumentException("ASCII encoding not supported");
         }
      }
   }

   private void append2DigitNumber(StringBuffer buffer, int value) {
      if (value < 10) {
         buffer.append('0');
      }

      buffer.append(Integer.toString(value));
   }

   private Calendar ISO8601ToCalendar(byte[] timeISO8601, int offset) {
      if (timeISO8601 == null) {
         throw new NullPointerException();
      } else {
         boolean extended = false;

         try {
            String time = new String(timeISO8601, "ASCII");
            int length = time.length();
            int year = Integer.parseInt(time.substring(offset, offset + 4));
            int index = offset + 4;
            if (time.charAt(index) == '-') {
               extended = true;
               ++index;
            }

            int month = Integer.parseInt(time.substring(index, index + 2)) - 1;
            index += 2;
            if (extended) {
               ++index;
            }

            int day = Integer.parseInt(time.substring(index, index + 2));
            index += 2;
            int hour = 0;
            int minute = 0;
            int second = 0;
            long gmtOffset = 0L;
            if (index != length) {
               if (time.charAt(index) != 'T') {
                  throw new IllegalArgumentException("Invalid ISO8601 time stamp");
               }

               ++index;
               hour = Integer.parseInt(time.substring(index, index + 2));
               index += 2;
               if (extended) {
                  ++index;
               }

               minute = Integer.parseInt(time.substring(index, index + 2));
               index += 2;
               if (extended) {
                  ++index;
               }

               second = Integer.parseInt(time.substring(index, index + 2));
               index += 2;
               if (index != length && time.charAt(index) != 'Z') {
                  char sign = time.charAt(index);
                  ++index;
                  gmtOffset = (long)(Integer.parseInt(time.substring(index, index + 2)) * 60 * 60);
                  index += 2;
                  if (index != length) {
                     if (extended) {
                        ++index;
                     }

                     gmtOffset += (long)(Integer.parseInt(time.substring(index, index + 2)) * 60);
                  }

                  gmtOffset *= 1000L;
                  if (sign == '-') {
                     gmtOffset *= -1L;
                  }
               }
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(14, 0);
            calendar.set(13, second);
            calendar.set(12, minute);
            if (hour >= 12) {
               calendar.set(9, 1);
               hour -= 12;
            } else {
               calendar.set(9, 0);
            }

            calendar.set(10, hour);
            calendar.set(5, 1);
            calendar.set(2, month);
            calendar.set(5, day);
            calendar.set(1, year);
            long millis = calendar.getTime().getTime();
            millis += (long)this.getGMTOffset(calendar) - gmtOffset;
            calendar.setTime(new Date(millis));
            return calendar;
         } catch (NumberFormatException var18) {
            throw new IllegalArgumentException("Invalid ISO8601 time stamp");
         } catch (UnsupportedEncodingException var19) {
            throw new IllegalArgumentException("ASCII incoding not supported");
         } catch (ArrayIndexOutOfBoundsException var20) {
            throw new IllegalArgumentException("Invalid ISO8601 time stamp");
         }
      }
   }

   private int getGMTOffset(Calendar calendar) {
      int millis = calendar.get(14) + calendar.get(13) * 1000 + calendar.get(12) * 60 * 1000 + calendar.get(10) * 60 * 60 * 1000;
      return calendar.getTimeZone().getOffset(1, calendar.get(1), calendar.get(2), calendar.get(5), calendar.get(7), millis);
   }

   public HeaderSetImpl clone() {
      HeaderSetImpl newHeaderSet = new HeaderSetImpl();
      Hashtable newPub = newHeaderSet.publicHeaders;
      Hashtable newPriv = newHeaderSet.privateHeaders;
      Enumeration e = this.publicHeaders.keys();

      Object key;
      while(e.hasMoreElements()) {
         key = e.nextElement();
         newPub.put(key, this.publicHeaders.get(key));
      }

      e = this.privateHeaders.keys();

      while(e.hasMoreElements()) {
         key = e.nextElement();
         newPriv.put(key, this.privateHeaders.get(key));
      }

      newHeaderSet.responseCode = this.responseCode;
      newHeaderSet.isReceived = this.isReceived;
      return newHeaderSet;
   }

   private static byte[] longToBytes(long value, byte[] array, int offset) {
      if (array == null) {
         throw new NullPointerException();
      } else if (value > Long.MAX_VALUE) {
         throw new IllegalArgumentException("value exceeds the maximum value");
      } else if (array.length < 4) {
         throw new IllegalArgumentException("Invalid byte array");
      } else if (offset >= 0 && offset + 4 <= array.length) {
         for(int i = 0; i < 4; ++i) {
            array[offset + i] = (byte)((int)(value >> 8 * (3 - i) & 255L));
         }

         return array;
      } else {
         throw new IllegalArgumentException("Invalid offset");
      }
   }
}
