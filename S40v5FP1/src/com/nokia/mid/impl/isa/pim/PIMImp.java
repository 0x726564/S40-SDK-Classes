package com.nokia.mid.impl.isa.pim;

import com.nokia.mid.impl.isa.i18n.CharsetConv;
import com.nokia.mid.impl.isa.util.SharedObjects;
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
   private static final Object serialLock;
   Object[] pimData = null;

   public static synchronized PIM getInstance() {
      if (_instance == null) {
         _instance = new PIMImp();
      }

      return _instance;
   }

   public PIMList openPIMList(int pimListType, int mode) throws PIMException {
      int textID;
      switch(pimListType) {
      case 1:
         textID = ContactListImp.LIST_NAMES[0];
         return this.openPIMList(1, mode, PIMTextDatabase.getText(textID));
      case 2:
         textID = EventListImp.LIST_NAMES[0];
         return this.openPIMList(2, mode, PIMTextDatabase.getText(textID));
      case 3:
         textID = ToDoListImp.LIST_NAMES[0];
         return this.openPIMList(3, mode, PIMTextDatabase.getText(textID));
      default:
         throw new IllegalArgumentException("Invalid PIM list.");
      }
   }

   public PIMList openPIMList(int pimListType, int mode, String name) throws PIMException {
      if (name == null) {
         throw new NullPointerException("name is null.");
      } else if (!this.isValidMode(mode)) {
         throw new IllegalArgumentException("Invalid Mode.");
      } else {
         PIMList list = null;
         if (pimListType != 1 && pimListType != 3 && pimListType != 2) {
            throw new IllegalArgumentException("Invalid PIM list.");
         } else if (!PIMListImp.hasAccessRights(mode, pimListType, false)) {
            throw new SecurityException();
         } else {
            switch(pimListType) {
            case 1:
               list = new ContactListImp(mode, name);
               break;
            case 2:
               list = new EventListImp(mode, name);
               break;
            case 3:
               list = new ToDoListImp(mode, name);
               break;
            default:
               throw new IllegalArgumentException("Invalid PIM list.");
            }

            return (PIMList)list;
         }
      }
   }

   public String[] listPIMLists(int pimListType) {
      if (pimListType != 1 && pimListType != 2 && pimListType != 3) {
         throw new IllegalArgumentException("Invalid PIM list.");
      } else if (!PIMListImp.hasAccessRights(1, pimListType, false)) {
         throw new SecurityException();
      } else {
         int var2;
         switch(pimListType) {
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

         String[] lists = new String[var2];
         int i;
         switch(pimListType) {
         case 1:
            for(i = 0; i < var2; ++i) {
               lists[i] = PIMTextDatabase.getText(ContactListImp.LIST_NAMES[i]);
            }

            return lists;
         case 2:
            for(i = 0; i < var2; ++i) {
               lists[i] = PIMTextDatabase.getText(EventListImp.LIST_NAMES[i]);
            }

            return lists;
         case 3:
            for(i = 0; i < var2; ++i) {
               lists[i] = PIMTextDatabase.getText(ToDoListImp.LIST_NAMES[i]);
            }
         }

         return lists;
      }
   }

   public PIMItem[] fromSerialFormat(InputStream is, String enc) throws PIMException, UnsupportedEncodingException {
      if (is == null) {
         throw new NullPointerException();
      } else {
         if (enc == null) {
            enc = SUPPORTED_ENCODING_FORMATS[0];
         }

         InputStreamReader isr = new InputStreamReader(is, enc);
         String startLine = null;

         try {
            startLine = this.getLine(isr);
         } catch (IOException var14) {
            throw new PIMException("Error in stream accessing.", 1);
         }

         Integer bCalendar = new Integer(-1);
         String lineType = this.identifyVLineType(startLine, bCalendar);
         int endCount = this.convertVLineToValue(lineType);
         if (lineType != "BEGIN:VCALENDAR" && lineType != "END:VCALENDAR") {
            bCalendar = new Integer(1);
         } else {
            bCalendar = new Integer(2);
         }

         selectedSerialFormat = bCalendar;
         byte[] inData = this.parseInputStreamForEntry(isr, startLine, bCalendar, endCount);
         if (bCalendar != selectedSerialFormat) {
            bCalendar = new Integer(selectedSerialFormat);
         }

         synchronized(serialLock) {
            int errCode = this.fromSerial(inData, bCalendar);
            if (errCode != 0) {
               throw new PIMException("Error converting the input stream to a PIMItem array.", 1);
            } else {
               PIMItem[] localPimData = new PIMItem[this.pimData.length];
               int i;
               if (bCalendar == 2) {
                  for(i = 0; i < this.pimData.length; ++i) {
                     localPimData[i] = (PIMItem)this.pimData[i];
                  }
               } else {
                  for(i = 0; i < this.pimData.length; ++i) {
                     localPimData[i] = new ContactImp((PBNativeRecord)this.pimData[i]);
                  }
               }

               this.pimData = null;
               return localPimData;
            }
         }
      }
   }

   public void toSerialFormat(PIMItem item, OutputStream os, String enc, String dataFormat) throws PIMException, UnsupportedEncodingException {
      if (item != null && os != null && dataFormat != null) {
         String charset;
         if (enc == null) {
            charset = "UTF-8";
         } else {
            charset = this.getSupportedEncoding(enc);
         }

         if (!this.isSupportedDataFormat(item.getPIMList(), dataFormat)) {
            throw new IllegalArgumentException("Unsupported dataFormat.");
         } else if ((!"VCARD/2.1".equals(dataFormat) && !"VCARD/3.0".equals(dataFormat) || item instanceof Contact) && (!"VCALENDAR/1.0".equals(dataFormat) || item instanceof Event || item instanceof ToDo)) {
            synchronized(serialLock) {
               byte[] encodedData = ((PIMItemImp)item).toSerial(charset, selectedSerialFormat);
               if (encodedData == null) {
                  throw new PIMException("Error in forming serial data for output stream.", 1);
               } else {
                  try {
                     os.write(encodedData);
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

   public String[] supportedSerialFormats(int pimListType) {
      switch(pimListType) {
      case 1:
         String[] format = new String[SUPPORTED_VCARD_FORMATS.length];

         for(int i = 0; i < format.length; ++i) {
            switch(SUPPORTED_VCARD_FORMATS[i]) {
            case 1:
               format[i] = "VCARD/2.1";
               break;
            case 5:
               format[i] = "VCARD/3.0";
            }
         }

         return format;
      case 2:
      case 3:
         return new String[]{"VCALENDAR/1.0"};
      default:
         throw new IllegalArgumentException("Unrecognised list type.");
      }
   }

   private byte[] parseInputStreamForEntry(InputStreamReader isr, String startLine, Integer bCalendar, int endCount) throws PIMException {
      Object var5 = null;

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         OutputStreamWriter osw = new OutputStreamWriter(baos, SUPPORTED_ENCODING_FORMATS[0]);
         if (endCount != 1) {
            throw new PIMException("Error in starting statememt of the input stream.");
         } else {
            osw.write((String)startLine, 0, startLine.length());

            for(String line = null; endCount != 0; line = null) {
               line = this.getLine(isr);
               if (selectedSerialFormat == 1 && line.regionMatches(true, 0, "VERSION:3.0", 0, "VERSION:3.0".length()) && this.isSupportedVcardFormat(5)) {
                  selectedSerialFormat = 5;
               }

               endCount += this.convertVLineToValue(this.identifyVLineType(line, bCalendar));
               osw.write((String)line, 0, line.length());
            }

            byte[] baData = baos.toByteArray();
            osw.close();
            return baData;
         }
      } catch (UnsupportedEncodingException var9) {
         throw new PIMException("Unsupported encoding string.", 1);
      } catch (IOException var10) {
         throw new PIMException("Error in stream accessing.", 1);
      }
   }

   private String identifyVLineType(String line, Integer bCalendar) throws PIMException {
      int type = -1;
      String matchType = null;
      if (line.regionMatches(true, 0, "BEGIN:VCARD", 0, "BEGIN:VCARD".length())) {
         type = bCalendar == 1 ? 1 : 5;
         matchType = "BEGIN:VCARD";
      } else if (line.regionMatches(true, 0, "BEGIN:VCALENDAR", 0, "BEGIN:VCALENDAR".length())) {
         type = 2;
         matchType = "BEGIN:VCALENDAR";
      } else if (line.regionMatches(true, 0, "END:VCARD", 0, "END:VCARD".length())) {
         type = bCalendar == 1 ? 1 : 5;
         matchType = "END:VCARD";
      } else if (line.regionMatches(true, 0, "END:VCALENDAR", 0, "END:VCALENDAR".length())) {
         type = 2;
         matchType = "END:VCALENDAR";
      }

      if (bCalendar != -1 && type != -1 && bCalendar != type) {
         if (type == 2) {
            throw new PIMException("VCALENDAR found within VCARD in input stream.");
         } else {
            throw new PIMException("VCARD found within VCALENDAR in input stream.");
         }
      } else {
         return matchType;
      }
   }

   private int convertVLineToValue(String lineType) {
      if (lineType != "BEGIN:VCARD" && lineType != "BEGIN:VCALENDAR") {
         return lineType != "END:VCARD" && lineType != "END:VCALENDAR" ? 0 : -1;
      } else {
         return 1;
      }
   }

   private String getLine(InputStreamReader isr) throws PIMException, IOException {
      String line = new String();
      boolean endOfLine = false;
      boolean startOfLine = false;
      int c = true;
      String lineType = null;
      Integer bCalendar = new Integer(-1);

      int c;
      while(!startOfLine) {
         c = isr.read();
         if (c == -1) {
            throw new PIMException("Not enough data at start if line in the input stream.");
         }

         if (c != 13 && c != 10) {
            startOfLine = true;
            line = line + (char)c;
         }
      }

      while(!endOfLine) {
         do {
            c = isr.read();
            line = line + (char)c;
            lineType = this.identifyVLineType(line, bCalendar);
            if (lineType == "END:VCARD" || lineType == "END:VCALENDAR") {
               return line;
            }
         } while(c != 13 && c != -1);

         if (c == -1) {
            throw new PIMException("Not enough data in input stream, .looking for CR.");
         }

         c = isr.read();
         line = line + (char)c;
         if (c == -1) {
            throw new PIMException("Not enough data in input stream, .looking for LINEFEED.");
         }

         if (c == 10) {
            endOfLine = true;
         }
      }

      return line;
   }

   private boolean isValidMode(int mode) {
      return mode == 2 || mode == 3 || mode == 1;
   }

   private String getSupportedEncoding(String enc) throws UnsupportedEncodingException {
      byte[] enc_ascii = CharsetConv.isSupportedEncoding(enc);
      if (enc_ascii != null) {
         for(int i = 0; i < SUPPORTED_ENCODING_FORMATS.length; ++i) {
            if (SUPPORTED_ENCODING_FORMATS[i].equals(new String(enc_ascii, 0, enc_ascii.length - 1, "US-ASCII"))) {
               return SUPPORTED_ENCODING_FORMATS[i];
            }
         }
      }

      throw new UnsupportedEncodingException();
   }

   private boolean isSupportedDataFormat(PIMList list, String dataFormat) {
      int listType = 3;
      if (list instanceof ContactList) {
         listType = 1;
      } else if (list instanceof EventList) {
         listType = 2;
      }

      String[] supportedSerialFormats = this.supportedSerialFormats(listType);

      for(int i = 0; i < supportedSerialFormats.length; ++i) {
         if (supportedSerialFormats[i].equals(dataFormat)) {
            if (listType == 1) {
               selectedSerialFormat = 1;
               if ("VCARD/3.0".equals(dataFormat)) {
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

   private boolean isSupportedVcardFormat(int vcardFormat) {
      for(int i = 0; i < SUPPORTED_VCARD_FORMATS.length; ++i) {
         if (SUPPORTED_VCARD_FORMATS[i] == vcardFormat) {
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
      serialLock = SharedObjects.getLock("javax.microedition.pim.serialLock");
   }
}
