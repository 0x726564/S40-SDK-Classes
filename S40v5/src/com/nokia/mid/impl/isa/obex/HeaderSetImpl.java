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
   private Hashtable gP;
   private Hashtable gQ;
   private int responseCode;
   private boolean gR;
   public static final int BODY = 72;
   public static final int END_OF_BODY = 73;
   public static final int CONNECTION_ID = 203;
   public static final int AUTH_USERNAME = 163;
   public static final int AUTH_PASSWORD = 164;

   public HeaderSetImpl(byte[] var1) throws IOException {
      this(var1, 0, var1.length);
   }

   public HeaderSetImpl(byte[] var1, int var2, int var3) throws IOException {
      this.responseCode = -1;

      try {
         this.deserializeHeaders(var1, var2, var3);
         this.gR = true;
      } catch (Exception var4) {
         throw new IOException(var4.getMessage());
      }
   }

   public HeaderSetImpl() {
      this.responseCode = -1;
      this.gP = new Hashtable();
      this.gQ = new Hashtable();
      this.gR = false;
   }

   public void includeHeaders(HeaderSetImpl var1) {
      Hashtable var2 = var1.gP;
      Hashtable var5 = var1.gQ;
      Enumeration var4 = var2.keys();

      Object var3;
      while(var4.hasMoreElements()) {
         var3 = var4.nextElement();
         this.gP.put(var3, var2.get(var3));
      }

      var4 = var5.keys();

      while(var4.hasMoreElements()) {
         var3 = var4.nextElement();
         this.gQ.put(var3, var5.get(var3));
      }

   }

   public void setHeaderPrivate(int var1, Object var2) {
      int var3 = getHeaderType(var1, false);
      if (m(var1)) {
         throw new IllegalArgumentException("Invalid header value");
      } else {
         if (var2 == null) {
            this.gQ.remove(new Integer(var1));
         }

         if (!a(var1, var3, var2)) {
            throw new IllegalArgumentException("Invalid header value");
         } else {
            this.gQ.put(new Integer(var1), var2);
         }
      }
   }

   public void setHeader(int var1, Object var2) {
      int var3;
      if ((var3 = getHeaderType(var1, false)) == -1) {
         throw new IllegalArgumentException("Invalid header ID");
      } else if (!m(var1)) {
         throw new IllegalArgumentException("Invalid header ID");
      } else if (var2 == null) {
         this.gP.remove(new Integer(var1));
      } else if (!a(var1, var3, var2)) {
         throw new IllegalArgumentException("Invalid header value");
      } else if (headerSize(var1, var3, var2) > 65524) {
         throw new IllegalArgumentException("Header too large");
      } else if (var1 == 66) {
         try {
            this.gP.put(new Integer(var1), ((String)var2).getBytes("ASCII"));
         } catch (UnsupportedEncodingException var4) {
         }
      } else {
         this.gP.put(new Integer(var1), var2);
      }
   }

   public Object getHeader(int var1) throws IOException {
      if (getHeaderType(var1, false) == -1) {
         throw new IllegalArgumentException("Invalid header ID");
      } else if (!m(var1)) {
         throw new IllegalArgumentException("Invalid header ID");
      } else {
         Object var3;
         if ((var3 = this.gP.get(new Integer(var1))) == null) {
            return null;
         } else if (var1 == 66) {
            try {
               return new String((byte[])var3);
            } catch (ClassCastException var2) {
               throw new IOException("Error retrieving TYPE header");
            }
         } else {
            return var3;
         }
      }
   }

   public Object getHeaderPrivate(int var1) {
      if (m(var1)) {
         throw new IllegalArgumentException("Invalid header ID");
      } else {
         return this.gQ.get(new Integer(var1));
      }
   }

   public int[] getHeaderList() throws IOException {
      int var1;
      if ((var1 = this.gP.size()) == 0) {
         return null;
      } else {
         int[] var4 = new int[var1];
         Enumeration var3 = this.gP.keys();

         for(int var2 = 0; var3.hasMoreElements(); var4[var2++] = (Integer)var3.nextElement()) {
         }

         return var4;
      }
   }

   private int[] getPrivateHeaderList() {
      int var1;
      if ((var1 = this.gQ.size()) == 0) {
         return null;
      } else {
         int[] var4 = new int[var1];
         Enumeration var3 = this.gQ.keys();

         for(int var2 = 0; var3.hasMoreElements(); var4[var2++] = (Integer)var3.nextElement()) {
         }

         return var4;
      }
   }

   public boolean containsAuthenticationChallenge() {
      return this.gQ.get(new Integer(161)) != null;
   }

   public String getRealm() {
      return (String)this.gQ.get(new Integer(162));
   }

   public boolean isUserId() {
      return (Boolean)this.gQ.get(new Integer(160));
   }

   public boolean isFullAccess() {
      return (Boolean)this.gQ.get(new Integer(161));
   }

   public void createAuthenticationChallenge(String var1, boolean var2, boolean var3) {
      if (var1 != null) {
         this.gQ.put(new Integer(162), var1);
      }

      this.gQ.put(new Integer(160), var2 ? Boolean.TRUE : Boolean.FALSE);
      this.gQ.put(new Integer(161), var3 ? Boolean.TRUE : Boolean.FALSE);
   }

   public int getResponseCode() throws IOException {
      if (this.responseCode == -1) {
         throw new IOException("Not allowed");
      } else {
         return this.responseCode;
      }
   }

   public void deserializeHeaders(byte[] var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException("serialisedHeader cannot be null");
      } else if (var2 + var3 > var1.length) {
         throw new IllegalArgumentException("Invalid offset or length");
      } else {
         this.responseCode = var1[var2++] & 255;
         byte var9 = var1[var2++];
         this.gP = new Hashtable();
         this.gQ = new Hashtable();

         for(int var4 = 0; var4 < var9; ++var4) {
            int var5 = var1[var2++] & 255;
            int var6 = (int)bytesToLong(var1, var2);
            var2 += 4;
            Object var7 = null;
            switch(getHeaderType(var5, true)) {
            case 0:
               var7 = new Long(bytesToLong(var1, var2));
               var2 += 4;
               break;
            case 1:
               var7 = new Byte(var1[var2++]);
               break;
            case 2:
               try {
                  var7 = new String(var1, var2, var6, "UCS-2LE");
               } catch (UnsupportedEncodingException var8) {
                  throw new IllegalArgumentException("UnsupportedEncoding");
               }

               var2 += var6;
               break;
            case 3:
               if (var5 == 196) {
                  ((Calendar)(var7 = Calendar.getInstance())).setTime(new Date(bytesToLong(var1, var2) * 1000L));
                  var2 += 4;
               } else {
                  var7 = this.a(var1, var2);
                  var2 += var6;
               }
               break;
            case 4:
               if (var5 == 66) {
                  var7 = new byte[var6 - 1];
                  System.arraycopy(var1, var2, var7, 0, var6 - 1);
               } else {
                  var7 = new byte[var6];
                  System.arraycopy(var1, var2, var7, 0, var6);
               }

               var2 += var6;
               break;
            case 5:
               var7 = var1[var2++] == 1 ? Boolean.TRUE : Boolean.FALSE;
               break;
            default:
               throw new IllegalArgumentException("Headers corrupted");
            }

            if (m(var5)) {
               this.gP.put(new Integer(var5), var7);
            } else {
               this.gQ.put(new Integer(var5), var7);
            }
         }

      }
   }

   public byte[] serializeHeaders() throws IOException {
      try {
         int[] var1 = this.getHeaderList();
         int[] var2 = this.getPrivateHeaderList();
         if (var1 == null && var2 == null) {
            return null;
         } else {
            int var3;
            int[] var4 = new int[var3 = (var1 == null ? 0 : var1.length) + (var2 == null ? 0 : var2.length)];
            if (var1 != null) {
               System.arraycopy(var1, 0, var4, 0, var1.length);
            }

            if (var2 != null) {
               System.arraycopy(var2, 0, var4, var1 == null ? 0 : var1.length, var2.length);
            }

            Vector var15 = new Vector(var3);
            int var16 = 0;

            int var6;
            byte[] var19;
            for(int var5 = 0; var5 < var3; ++var5) {
               Object var7;
               if (m(var6 = var4[var5])) {
                  var7 = this.getHeader(var6);
               } else {
                  var7 = this.getHeaderPrivate(var6);
               }

               Object var8 = null;
               byte[] var11;
               switch(getHeaderType(var6, true)) {
               case 0:
                  var19 = new byte[4];
                  a((Long)var7, var19, 0);
                  break;
               case 1:
                  var19 = new byte[]{(Byte)var7};
                  break;
               case 2:
                  var19 = ((String)var7).getBytes("UCS-2LE");
                  break;
               case 3:
                  if (var6 == 196) {
                     long var21 = ((Calendar)var7).getTime().getTime() / 1000L;
                     var19 = new byte[4];
                     a(var21, var19, 0);
                  } else {
                     var19 = this.a((Calendar)var7, false);
                  }
                  break;
               case 4:
                  if (var6 == 66) {
                     var19 = new byte[(var11 = ((String)var7).getBytes("ASCII")).length + 1];
                     System.arraycopy(var11, 0, var19, 0, var11.length);
                     var19[var19.length - 1] = 0;
                  } else {
                     var19 = (byte[])var7;
                  }
                  break;
               case 5:
                  var19 = new byte[]{(byte)((Boolean)var7 ? 1 : 0)};
                  break;
               default:
                  throw new IOException("Unknown header ID: " + var6);
               }

               (var11 = new byte[var19.length + 5])[0] = (byte)var6;
               a((long)var19.length, var11, 1);
               System.arraycopy(var19, 0, var11, 5, var19.length);
               var16 += var11.length;
               if (var6 == 203) {
                  var15.insertElementAt(var11, 0);
               } else {
                  var15.addElement(var11);
               }
            }

            byte[] var17 = new byte[var16 + 2];
            byte var18 = 0;
            var6 = var18 + 1;
            var17[0] = (byte)this.responseCode;
            ++var6;
            var17[1] = (byte)var3;

            for(int var20 = 0; var20 < var15.size(); ++var20) {
               System.arraycopy(var19 = (byte[])var15.elementAt(var20), 0, var17, var6, var19.length);
               var6 += var19.length;
            }

            return var17;
         }
      } catch (IOException var13) {
         throw new IOException("Unable to serialize the HeaderSet");
      } catch (ArrayIndexOutOfBoundsException var14) {
         throw new IOException("Unable to serialize the HeaderSet");
      }
   }

   public static int getHeaderType(int var0, boolean var1) {
      switch(var0) {
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
         if (!var1 || var0 != 161 && var0 != 160) {
            if (var1 && var0 == 162) {
               return 2;
            } else if (var0 >= 48 && var0 <= 63) {
               return 2;
            } else if (var0 >= 112 && var0 <= 127) {
               return 4;
            } else if (var0 >= 176 && var0 <= 191) {
               return 1;
            } else {
               return var0 >= 240 && var0 <= 255 ? 0 : -1;
            }
         } else {
            return 5;
         }
      }
   }

   private static boolean m(int var0) {
      switch(var0) {
      case 1:
      case 5:
      case 66:
      case 68:
      case 70:
      case 71:
      case 74:
      case 76:
      case 79:
      case 192:
      case 195:
      case 196:
         return true;
      default:
         return var0 >= 48 && var0 <= 63 || var0 >= 112 && var0 <= 127 || var0 >= 176 && var0 <= 191 || var0 >= 240 && var0 <= 255;
      }
   }

   private static boolean a(int var0, int var1, Object var2) {
      switch(var1) {
      case 0:
         if (!(var2 instanceof Long)) {
            return false;
         } else {
            long var3;
            if ((var3 = (Long)var2) >= 0L && var3 <= 4294967295L) {
               return true;
            }

            return false;
         }
      case 1:
         return var2 instanceof Byte;
      case 2:
         return var2 instanceof String;
      case 3:
         if (!(var2 instanceof Calendar)) {
            return false;
         } else {
            if (var0 == 196 && ((Calendar)var2).getTime().getTime() / 1000L > 2147483647L) {
               return false;
            }

            return true;
         }
      case 4:
         if (var0 == 66) {
            return var2 instanceof String;
         }

         return var2 instanceof byte[];
      case 5:
         return var2 instanceof Boolean;
      default:
         return false;
      }
   }

   public static int headerSize(int var0, int var1, Object var2) {
      int var3 = 0;
      switch(var1) {
      case 0:
         var3 = 4;
         break;
      case 1:
         var3 = 1;
         break;
      case 2:
         var3 = (((String)var2).length() << 1) + 2;
         break;
      case 3:
         var3 = 32;
         break;
      case 4:
         if (var0 == 66) {
            var3 = ((String)var2).length() + 1;
         } else {
            var3 = ((byte[])var2).length;
         }
         break;
      case 5:
         var3 = 1;
      }

      return var3;
   }

   public boolean isReceivedHeaderSet() {
      return this.gR;
   }

   public void setResponseCode(int var1) {
      HeaderSetImpl var10000 = this;
      int var2 = var1;
      if (var1 != 160 && var1 != 161 && var1 != 162 && var1 != 163 && var1 != 164 && var1 != 165 && var1 != 166 && var1 != 176 && var1 != 177 && var1 != 178 && var1 != 179 && var1 != 180 && var1 != 181 && var1 != 192 && var1 != 193 && var1 != 194 && var1 != 195 && var1 != 196 && var1 != 197 && var1 != 198 && var1 != 199 && var1 != 200 && var1 != 201 && var1 != 202 && var1 != 203 && var1 != 204 && var1 != 205 && var1 != 206 && var1 != 207 && var1 != 208 && var1 != 209 && var1 != 210 && var1 != 211 && var1 != 212 && var1 != 213 && var1 != 224 && var1 != 225) {
         var2 = 208;
      }

      var10000.responseCode = var2;
   }

   public void setResponseCodePrivate(int var1) {
      if (var1 == 144) {
         this.responseCode = var1;
      } else {
         this.setResponseCode(var1);
      }
   }

   public static long bytesToLong(byte[] var0, int var1) {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (var0.length < 4) {
         throw new IllegalArgumentException("Invalid byte array");
      } else if (var1 >= 0 && var1 + 3 < var0.length) {
         long var2 = 0L;

         for(long var4 = 0L; var4 < 4L; ++var4) {
            var2 += ((long)var0[var1 + (3 - (int)var4)] & 255L) << (int)(8L * var4);
         }

         return var2;
      } else {
         throw new IllegalArgumentException("Invalid offset");
      }
   }

   private byte[] a(Calendar var1, boolean var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         try {
            StringBuffer var4;
            (var4 = new StringBuffer(20)).append(Integer.toString(var1.get(1)));
            a(var4, var1.get(2) + 1);
            a(var4, var1.get(5));
            var4.append('T');
            int var6 = var1.get(10);
            if (var1.get(9) == 1) {
               var6 += 12;
            }

            a(var4, var6);
            a(var4, var1.get(12));
            a(var4, var1.get(13));
            int var5;
            if ((var5 = a(var1)) == 0) {
               var4.append('Z');
            } else {
               if (var5 > 0) {
                  var4.append('+');
               } else {
                  var4.append('-');
                  var5 = -var5;
               }

               float var7;
               var6 = (int)(var7 = (float)(var5 /= 1000) / 60.0F / 60.0F);
               a(var4, var6);
               if (var7 - (float)var6 != 0.0F) {
                  a(var4, (int)((var7 - (float)var6) * 60.0F));
               }
            }

            return var4.toString().getBytes("ASCII");
         } catch (UnsupportedEncodingException var3) {
            throw new IllegalArgumentException("ASCII encoding not supported");
         }
      }
   }

   private static void a(StringBuffer var0, int var1) {
      if (var1 < 10) {
         var0.append('0');
      }

      var0.append(Integer.toString(var1));
   }

   private Calendar a(byte[] var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         boolean var21 = false;

         try {
            String var22 = new String(var1, "ASCII");
            int var3 = var22.length();
            int var4 = Integer.parseInt(var22.substring(var2, var2 + 4));
            var2 += 4;
            if (var22.charAt(var2) == '-') {
               var21 = true;
               ++var2;
            }

            int var5 = Integer.parseInt(var22.substring(var2, var2 + 2)) - 1;
            var2 += 2;
            if (var21) {
               ++var2;
            }

            int var6 = Integer.parseInt(var22.substring(var2, var2 + 2));
            var2 += 2;
            int var7 = 0;
            int var8 = 0;
            int var9 = 0;
            long var13 = 0L;
            if (var2 != var3) {
               if (var22.charAt(var2) != 'T') {
                  throw new IllegalArgumentException("Invalid ISO8601 time stamp");
               }

               ++var2;
               var7 = Integer.parseInt(var22.substring(var2, var2 + 2));
               var2 += 2;
               if (var21) {
                  ++var2;
               }

               var8 = Integer.parseInt(var22.substring(var2, var2 + 2));
               var2 += 2;
               if (var21) {
                  ++var2;
               }

               var9 = Integer.parseInt(var22.substring(var2, var2 + 2));
               var2 += 2;
               if (var2 != var3 && var22.charAt(var2) != 'Z') {
                  char var10 = var22.charAt(var2);
                  ++var2;
                  var13 = (long)(Integer.parseInt(var22.substring(var2, var2 + 2)) * 60 * 60);
                  var2 += 2;
                  if (var2 != var3) {
                     if (var21) {
                        ++var2;
                     }

                     var13 += (long)(Integer.parseInt(var22.substring(var2, var2 + 2)) * 60);
                  }

                  var13 *= 1000L;
                  if (var10 == '-') {
                     var13 = -var13;
                  }
               }
            }

            Calendar var23;
            (var23 = Calendar.getInstance()).set(14, 0);
            var23.set(13, var9);
            var23.set(12, var8);
            if (var7 >= 12) {
               var23.set(9, 1);
               var7 -= 12;
            } else {
               var23.set(9, 0);
            }

            var23.set(10, var7);
            var23.set(5, 1);
            var23.set(2, var5);
            var23.set(5, var6);
            var23.set(1, var4);
            long var16 = var23.getTime().getTime() + ((long)a(var23) - var13);
            var23.setTime(new Date(var16));
            return var23;
         } catch (NumberFormatException var18) {
            throw new IllegalArgumentException("Invalid ISO8601 time stamp");
         } catch (UnsupportedEncodingException var19) {
            throw new IllegalArgumentException("ASCII incoding not supported");
         } catch (ArrayIndexOutOfBoundsException var20) {
            throw new IllegalArgumentException("Invalid ISO8601 time stamp");
         }
      }
   }

   private static int a(Calendar var0) {
      int var1 = var0.get(14) + var0.get(13) * 1000 + var0.get(12) * 60 * 1000 + var0.get(10) * 60 * 60 * 1000;
      return var0.getTimeZone().getOffset(1, var0.get(1), var0.get(2), var0.get(5), var0.get(7), var1);
   }

   public HeaderSetImpl clone() {
      HeaderSetImpl var1;
      Hashtable var2 = (var1 = new HeaderSetImpl()).gP;
      Hashtable var3 = var1.gQ;
      Enumeration var4 = this.gP.keys();

      Object var5;
      while(var4.hasMoreElements()) {
         var5 = var4.nextElement();
         var2.put(var5, this.gP.get(var5));
      }

      var4 = this.gQ.keys();

      while(var4.hasMoreElements()) {
         var5 = var4.nextElement();
         var3.put(var5, this.gQ.get(var5));
      }

      var1.responseCode = this.responseCode;
      var1.gR = this.gR;
      return var1;
   }

   private static byte[] a(long var0, byte[] var2, int var3) {
      if (var0 > Long.MAX_VALUE) {
         throw new IllegalArgumentException("value exceeds the maximum value");
      } else if (var2.length < 4) {
         throw new IllegalArgumentException("Invalid byte array");
      } else if (var3 >= 0 && var3 + 4 <= var2.length) {
         for(int var4 = 0; var4 < 4; ++var4) {
            var2[var3 + var4] = (byte)((int)(var0 >> 8 * (3 - var4) & 255L));
         }

         return var2;
      } else {
         throw new IllegalArgumentException("Invalid offset");
      }
   }
}
