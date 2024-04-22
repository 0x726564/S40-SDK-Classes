package com.nokia.mid.impl.isa.pim;

import com.nokia.mid.impl.isa.i18n.CharsetConv;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.Event;
import javax.microedition.pim.EventList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.ToDo;

public class PIMImp extends PIM {
   private static final String VCARD_FORMAT_2_1 = "VCARD/2.1";
   private static final String VCARD_FORMAT_3_0 = "VCARD/3.0";
   private static final String VCALENDAR_FORMAT = "VCALENDAR/1.0";
   private static final String VCARD_VER_2_1 = "VERSION:2.1";
   private static final String VCARD_VER_3_0 = "VERSION:3.0";
   private final int[] serialLock = new int[1];
   private static final int VTYPE_VCARD_2_1 = 1;
   private static final int VTYPE_VCALENDAR = 2;
   private static final int VTYPE_VCARD_3_0 = 5;
   private static final int VTYPE_UNDEFINED = -1;
   public static int selectedSerialFormat = -1;
   private static final String BEGIN_VCARD = "BEGIN:VCARD";
   private static final String END_VCARD = "END:VCARD";
   private static final String BEGIN_VCALENDAR = "BEGIN:VCALENDAR";
   private static final String END_VCALENDAR = "END:VCALENDAR";
   private static final int CR = 13;
   private static final int LINEFEED = 10;
   private static PIM _instance = null;
   private static final int[] SUPPORTED_VCARD_FORMATS = getSupportedVcardFormats();
   private static String[] SUPPORTED_ENCODING_FORMATS = new String[]{"UTF-8", "US-ASCII", "ISO-8859-1"};
   Object[] pimData = null;

   public static synchronized PIM getInstance() {
      if (_instance == null) {
         _instance = new PIMImp();
      }

      return _instance;
   }

   public PIMList openPIMList(int var1, int var2) throws PIMException {
      int var3;
      switch(var1) {
      case 1:
         var3 = ContactListImp.LIST_NAMES[0];
         return this.openPIMList(1, var2, PIMTextDatabase.getText(var3));
      case 2:
         var3 = EventListImp.LIST_NAMES[0];
         return this.openPIMList(2, var2, PIMTextDatabase.getText(var3));
      case 3:
         var3 = ToDoListImp.LIST_NAMES[0];
         return this.openPIMList(3, var2, PIMTextDatabase.getText(var3));
      default:
         throw new IllegalArgumentException("Invalid PIM list.");
      }
   }

   public PIMList openPIMList(int var1, int var2, String var3) throws PIMException {
      if (var3 == null) {
         throw new NullPointerException("name is null.");
      } else if (!this.isValidMode(var2)) {
         throw new IllegalArgumentException("Invalid Mode.");
      } else {
         Object var4 = null;
         switch(var1) {
         case 1:
            var4 = new ContactListImp(var2, var3);
            break;
         case 2:
            var4 = new EventListImp(var2, var3);
            break;
         case 3:
            var4 = new ToDoListImp(var2, var3);
            break;
         default:
            throw new IllegalArgumentException("Invalid PIM list.");
         }

         if (!PIMListImp.hasOpenListRights(var2, var1)) {
            throw new SecurityException();
         } else {
            return (PIMList)var4;
         }
      }
   }

   public String[] listPIMLists(int var1) {
      if (var1 != 1 && var1 != 2 && var1 != 3) {
         throw new IllegalArgumentException("Invalid PIM list.");
      } else if (!PIMListImp.hasOpenListRights(1, var1)) {
         throw new SecurityException();
      } else {
         int var2;
         switch(var1) {
         case 1:
            var2 = ContactListImp.LIST_NAMES.length;
            break;
         case 2:
            var2 = EventListImp.LIST_NAMES.length;
            break;
         case 3:
            var2 = ToDoListImp.LIST_NAMES.length;
            break;
         default:
            throw new IllegalArgumentException("Invalid PIM list.");
         }

         String[] var3 = new String[var2];
         int var4;
         switch(var1) {
         case 1:
            for(var4 = 0; var4 < var2; ++var4) {
               var3[var4] = PIMTextDatabase.getText(ContactListImp.LIST_NAMES[var4]);
            }

            return var3;
         case 2:
            for(var4 = 0; var4 < var2; ++var4) {
               var3[var4] = PIMTextDatabase.getText(EventListImp.LIST_NAMES[var4]);
            }

            return var3;
         case 3:
            for(var4 = 0; var4 < var2; ++var4) {
               var3[var4] = PIMTextDatabase.getText(ToDoListImp.LIST_NAMES[var4]);
            }
         }

         return var3;
      }
   }

   public PIMItem[] fromSerialFormat(InputStream var1, String var2) throws PIMException, UnsupportedEncodingException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (var2 == null) {
            var2 = SUPPORTED_ENCODING_FORMATS[0];
         }

         InputStreamReader var3 = new InputStreamReader(var1, var2);
         String var4 = null;

         try {
            var4 = this.getLine(var3);
         } catch (IOException var14) {
            throw new PIMException("Error in stream accessing.", 1);
         }

         Integer var5 = new Integer(-1);
         String var6 = this.identifyVLineType(var4, var5);
         int var7 = this.convertVLineToValue(var6);
         if (var6 != "BEGIN:VCALENDAR" && var6 != "END:VCALENDAR") {
            var5 = new Integer(1);
         } else {
            var5 = new Integer(2);
         }

         selectedSerialFormat = var5;
         byte[] var8 = this.parseInputStreamForEntry(var3, var4, var5, var7);
         if (var5 != selectedSerialFormat) {
            var5 = new Integer(selectedSerialFormat);
         }

         synchronized(this.serialLock) {
            int var10 = this.fromSerial(var8, var5);
            if (var10 != 0) {
               throw new PIMException("Error converting the input stream to a PIMItem array.", 1);
            } else {
               PIMItem[] var11 = new PIMItem[this.pimData.length];
               int var12;
               if (var5 == 2) {
                  for(var12 = 0; var12 < this.pimData.length; ++var12) {
                     var11[var12] = (PIMItem)this.pimData[var12];
                  }
               } else {
                  for(var12 = 0; var12 < this.pimData.length; ++var12) {
                     var11[var12] = new ContactImp((PBNativeRecord)this.pimData[var12]);
                  }
               }

               this.pimData = null;
               return var11;
            }
         }
      }
   }

   public void toSerialFormat(PIMItem var1, OutputStream var2, String var3, String var4) throws PIMException, UnsupportedEncodingException {
      if (var1 != null && var2 != null && var4 != null) {
         String var5;
         if (var3 == null) {
            var5 = "UTF-8";
         } else {
            var5 = this.getSupportedEncoding(var3);
         }

         if (!this.isSupportedDataFormat(var1.getPIMList(), var4)) {
            throw new IllegalArgumentException("Unsupported dataFormat.");
         } else if ((!"VCARD/2.1".equals(var4) && !"VCARD/3.0".equals(var4) || var1 instanceof Contact) && (!"VCALENDAR/1.0".equals(var4) || var1 instanceof Event || var1 instanceof ToDo)) {
            synchronized(this.serialLock) {
               byte[] var7 = ((PIMItemImp)var1).toSerial(var5, selectedSerialFormat);
               if (var7 == null) {
                  throw new PIMException("Error in forming serial data for output stream.", 1);
               } else {
                  try {
                     var2.write(var7);
                  } catch (IOException var10) {
                     throw new PIMException("Unable to complete write to output stream.", 1);
                  }

               }
            }
         } else {
            throw new PIMException("Wrong data format for this PIMItem.", 1);
         }
      } else {
         throw new NullPointerException();
      }
   }

   public String[] supportedSerialFormats(int var1) {
      switch(var1) {
      case 1:
         String[] var2 = new String[SUPPORTED_VCARD_FORMATS.length];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            switch(SUPPORTED_VCARD_FORMATS[var3]) {
            case 1:
               var2[var3] = "VCARD/2.1";
               break;
            case 5:
               var2[var3] = "VCARD/3.0";
            }
         }

         return var2;
      case 2:
      case 3:
         return new String[]{"VCALENDAR/1.0"};
      default:
         throw new IllegalArgumentException("Unrecognised list type.");
      }
   }

   private byte[] parseInputStreamForEntry(InputStreamReader var1, String var2, Integer var3, int var4) throws PIMException {
      Object var5 = null;

      try {
         ByteArrayOutputStream var6 = new ByteArrayOutputStream();
         OutputStreamWriter var7 = new OutputStreamWriter(var6, SUPPORTED_ENCODING_FORMATS[0]);
         if (var4 != 1) {
            throw new PIMException("Error in starting statememt of the input stream.");
         } else {
            var7.write((String)var2, 0, var2.length());

            for(String var8 = null; var4 != 0; var8 = null) {
               var8 = this.getLine(var1);
               if (selectedSerialFormat == 1 && var8.regionMatches(true, 0, "VERSION:3.0", 0, "VERSION:3.0".length()) && this.isSupportedVcardFormat(5)) {
                  selectedSerialFormat = 5;
               }

               var4 += this.convertVLineToValue(this.identifyVLineType(var8, var3));
               var7.write((String)var8, 0, var8.length());
            }

            byte[] var11 = var6.toByteArray();
            var7.close();
            return var11;
         }
      } catch (UnsupportedEncodingException var9) {
         throw new PIMException("Unsupported encoding string.", 1);
      } catch (IOException var10) {
         throw new PIMException("Error in stream accessing.", 1);
      }
   }

   private String identifyVLineType(String var1, Integer var2) throws PIMException {
      int var3 = -1;
      String var4 = null;
      if (var1.regionMatches(true, 0, "BEGIN:VCARD", 0, "BEGIN:VCARD".length())) {
         var3 = var2 == 1 ? 1 : 5;
         var4 = "BEGIN:VCARD";
      } else if (var1.regionMatches(true, 0, "BEGIN:VCALENDAR", 0, "BEGIN:VCALENDAR".length())) {
         var3 = 2;
         var4 = "BEGIN:VCALENDAR";
      } else if (var1.regionMatches(true, 0, "END:VCARD", 0, "END:VCARD".length())) {
         var3 = var2 == 1 ? 1 : 5;
         var4 = "END:VCARD";
      } else if (var1.regionMatches(true, 0, "END:VCALENDAR", 0, "END:VCALENDAR".length())) {
         var3 = 2;
         var4 = "END:VCALENDAR";
      }

      if (var2 != -1 && var3 != -1 && var2 != var3) {
         if (var3 == 2) {
            throw new PIMException("VCALENDAR found within VCARD in input stream.");
         } else {
            throw new PIMException("VCARD found within VCALENDAR in input stream.");
         }
      } else {
         return var4;
      }
   }

   private int convertVLineToValue(String var1) {
      if (var1 != "BEGIN:VCARD" && var1 != "BEGIN:VCALENDAR") {
         return var1 != "END:VCARD" && var1 != "END:VCALENDAR" ? 0 : -1;
      } else {
         return 1;
      }
   }

   private String getLine(InputStreamReader var1) throws PIMException, IOException {
      String var2 = new String();
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = true;
      String var6 = null;
      Integer var7 = new Integer(-1);

      int var8;
      while(!var4) {
         var8 = var1.read();
         if (var8 == -1) {
            throw new PIMException("Not enough data at start if line in the input stream.");
         }

         if (var8 != 13 && var8 != 10) {
            var4 = true;
            var2 = var2 + (char)var8;
         }
      }

      while(!var3) {
         do {
            var8 = var1.read();
            var2 = var2 + (char)var8;
            var6 = this.identifyVLineType(var2, var7);
            if (var6 == "END:VCARD" || var6 == "END:VCALENDAR") {
               return var2;
            }
         } while(var8 != 13 && var8 != -1);

         if (var8 == -1) {
            throw new PIMException("Not enough data in input stream, .looking for CR.");
         }

         var8 = var1.read();
         var2 = var2 + (char)var8;
         if (var8 == -1) {
            throw new PIMException("Not enough data in input stream, .looking for LINEFEED.");
         }

         if (var8 == 10) {
            var3 = true;
         }
      }

      return var2;
   }

   private boolean isValidMode(int var1) {
      return var1 == 2 || var1 == 3 || var1 == 1;
   }

   private String getSupportedEncoding(String var1) throws UnsupportedEncodingException {
      byte[] var2 = CharsetConv.isSupportedEncoding(var1);
      if (var2 != null) {
         for(int var3 = 0; var3 < SUPPORTED_ENCODING_FORMATS.length; ++var3) {
            if (SUPPORTED_ENCODING_FORMATS[var3].equals(new String(var2, 0, var2.length - 1, "US-ASCII"))) {
               return SUPPORTED_ENCODING_FORMATS[var3];
            }
         }
      }

      throw new UnsupportedEncodingException();
   }

   private boolean isSupportedDataFormat(PIMList var1, String var2) {
      byte var3 = 3;
      if (var1 instanceof ContactList) {
         var3 = 1;
      } else if (var1 instanceof EventList) {
         var3 = 2;
      }

      String[] var4 = this.supportedSerialFormats(var3);

      for(int var5 = 0; var5 < var4.length; ++var5) {
         if (var4[var5].equals(var2)) {
            if (var3 == 1) {
               selectedSerialFormat = 1;
               if ("VCARD/3.0".equals(var2)) {
                  selectedSerialFormat = 5;
               }
            } else {
               selectedSerialFormat = 2;
            }

            return true;
         }
      }

      return false;
   }

   private boolean isSupportedVcardFormat(int var1) {
      for(int var2 = 0; var2 < SUPPORTED_VCARD_FORMATS.length; ++var2) {
         if (SUPPORTED_VCARD_FORMATS[var2] == var1) {
            return true;
         }
      }

      return false;
   }

   private native int fromSerial(byte[] var1, int var2);

   private static native void registerPIMCleanup();

   private static native int[] getSupportedVcardFormats();

   static {
      try {
         Class.forName("com.nokia.mid.impl.isa.pim.ToDoImp");
         Class.forName("com.nokia.mid.impl.isa.pim.EventImp");
         Class.forName("com.nokia.mid.impl.isa.pim.ToDoListImp");
         Class.forName("com.nokia.mid.impl.isa.pim.EventListImp");
         Class.forName("javax.microedition.pim.PIMException");
      } catch (ClassNotFoundException var1) {
      }

      registerPIMCleanup();
   }
}
