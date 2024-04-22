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
   public static int selectedSerialFormat = -1;
   private static PIM bd = null;
   private static final int[] be = getSupportedVcardFormats();
   private static String[] bf = new String[]{"UTF-8", "US-ASCII", "ISO-8859-1"};
   private static final Object bg;
   private Object[] bh = null;

   public static synchronized PIM getInstance() {
      if (bd == null) {
         bd = new PIMImp();
      }

      return bd;
   }

   public PIMList openPIMList(int var1, int var2) throws PIMException {
      switch(var1) {
      case 1:
         var1 = ContactListImp.cw[0];
         return this.openPIMList(1, var2, PIMTextDatabase.getText(var1));
      case 2:
         var1 = EventListImp.cw[0];
         return this.openPIMList(2, var2, PIMTextDatabase.getText(var1));
      case 3:
         var1 = ToDoListImp.cw[0];
         return this.openPIMList(3, var2, PIMTextDatabase.getText(var1));
      default:
         throw new IllegalArgumentException("Invalid PIM list.");
      }
   }

   public PIMList openPIMList(int var1, int var2, String var3) throws PIMException {
      if (var3 == null) {
         throw new NullPointerException("name is null.");
      } else if (var2 != 2 && var2 != 3 && var2 != 1) {
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
         int var3;
         switch(var1) {
         case 1:
            var3 = ContactListImp.cw.length;
            break;
         case 2:
            var3 = EventListImp.cw.length;
            break;
         case 3:
            var3 = ToDoListImp.cw.length;
            break;
         default:
            throw new IllegalArgumentException("Invalid PIM list.");
         }

         String[] var2 = new String[var3];
         switch(var1) {
         case 1:
            for(var1 = 0; var1 < var3; ++var1) {
               var2[var1] = PIMTextDatabase.getText(ContactListImp.cw[var1]);
            }

            return var2;
         case 2:
            for(var1 = 0; var1 < var3; ++var1) {
               var2[var1] = PIMTextDatabase.getText(EventListImp.cw[var1]);
            }

            return var2;
         case 3:
            for(var1 = 0; var1 < var3; ++var1) {
               var2[var1] = PIMTextDatabase.getText(ToDoListImp.cw[var1]);
            }
         }

         return var2;
      }
   }

   public PIMItem[] fromSerialFormat(InputStream var1, String var2) throws PIMException, UnsupportedEncodingException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (var2 == null) {
            var2 = bf[0];
         }

         InputStreamReader var7 = new InputStreamReader(var1, var2);
         var2 = null;

         try {
            var2 = this.a(var7);
         } catch (IOException var5) {
            throw new PIMException("Error in stream accessing.", 1);
         }

         Integer var3 = new Integer(-1);
         String var10;
         int var4 = e(var10 = a(var2, var3));
         if (var10 != "BEGIN:VCALENDAR" && var10 != "END:VCALENDAR") {
            var3 = new Integer(1);
         } else {
            var3 = new Integer(2);
         }

         selectedSerialFormat = var3;
         byte[] var8 = this.a(var7, var2, var3, var4);
         if (var3 != selectedSerialFormat) {
            var3 = new Integer(selectedSerialFormat);
         }

         synchronized(bg) {
            if (this.fromSerial(var8, var3) != 0) {
               throw new PIMException("Error converting the input stream to a PIMItem array.", 1);
            } else {
               PIMItem[] var9 = new PIMItem[((Object[])null).length];
               int var11;
               if (var3 == 2) {
                  for(var11 = 0; var11 < ((Object[])null).length; ++var11) {
                     var9[var11] = null;
                  }
               } else {
                  for(var11 = 0; var11 < ((Object[])null).length; ++var11) {
                     var9[var11] = new ContactImp((PBNativeRecord)null);
                  }
               }

               this.bh = null;
               return var9;
            }
         }
      }
   }

   public void toSerialFormat(PIMItem var1, OutputStream var2, String var3, String var4) throws PIMException, UnsupportedEncodingException {
      if (var1 != null && var2 != null && var4 != null) {
         String var10000;
         if (var3 == null) {
            var10000 = "UTF-8";
         } else {
            label117: {
               byte[] var6;
               if ((var6 = CharsetConv.isSupportedEncoding(var3)) != null) {
                  for(int var7 = 0; var7 < bf.length; ++var7) {
                     if (bf[var7].equals(new String(var6, 0, var6.length - 1, "US-ASCII"))) {
                        var10000 = bf[var7];
                        break label117;
                     }
                  }
               }

               throw new UnsupportedEncodingException();
            }
         }

         var3 = var10000;
         PIMList var10001 = var1.getPIMList();
         String var15 = var4;
         PIMList var13 = var10001;
         byte var10 = 3;
         if (var13 instanceof ContactList) {
            var10 = 1;
         } else if (var13 instanceof EventList) {
            var10 = 2;
         }

         String[] var12 = this.supportedSerialFormats(var10);
         int var14 = 0;

         boolean var16;
         while(true) {
            if (var14 >= var12.length) {
               var16 = false;
               break;
            }

            if (var12[var14].equals(var15)) {
               if (var10 == 1) {
                  selectedSerialFormat = 1;
                  if ("VCARD/3.0".equals(var15)) {
                     selectedSerialFormat = 5;
                  }
               } else {
                  selectedSerialFormat = 2;
               }

               var16 = true;
               break;
            }

            ++var14;
         }

         if (!var16) {
            throw new IllegalArgumentException("Unsupported dataFormat.");
         } else if ((!"VCARD/2.1".equals(var4) && !"VCARD/3.0".equals(var4) || var1 instanceof Contact) && (!"VCALENDAR/1.0".equals(var4) || var1 instanceof Event || var1 instanceof ToDo)) {
            synchronized(bg) {
               byte[] var11;
               if ((var11 = ((PIMItemImp)var1).toSerial(var3, selectedSerialFormat)) == null) {
                  throw new PIMException("Error in forming serial data for output stream.", 1);
               } else {
                  try {
                     var2.write(var11);
                  } catch (IOException var8) {
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
         String[] var2 = new String[be.length];

         for(var1 = 0; var1 < var2.length; ++var1) {
            switch(be[var1]) {
            case 1:
               var2[var1] = "VCARD/2.1";
               break;
            case 5:
               var2[var1] = "VCARD/3.0";
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

   private byte[] a(InputStreamReader var1, String var2, Integer var3, int var4) throws PIMException {
      Object var5 = null;

      try {
         ByteArrayOutputStream var6 = new ByteArrayOutputStream();
         OutputStreamWriter var7 = new OutputStreamWriter(var6, bf[0]);
         if (var4 != 1) {
            throw new PIMException("Error in starting statememt of the input stream.");
         } else {
            var7.write((String)var2, 0, var2.length());
            var2 = null;

            while(var4 != 0) {
               var2 = this.a(var1);
               if (selectedSerialFormat == 1 && var2.regionMatches(true, 0, "VERSION:3.0", 0, "VERSION:3.0".length()) && d(5)) {
                  selectedSerialFormat = 5;
               }

               var4 += e(a(var2, var3));
               var7.write((String)var2, 0, var2.length());
            }

            byte[] var10 = var6.toByteArray();
            var7.close();
            return var10;
         }
      } catch (UnsupportedEncodingException var8) {
         throw new PIMException("Unsupported encoding string.", 1);
      } catch (IOException var9) {
         throw new PIMException("Error in stream accessing.", 1);
      }
   }

   private static String a(String var0, Integer var1) throws PIMException {
      int var2 = -1;
      String var3 = null;
      if (var0.regionMatches(true, 0, "BEGIN:VCARD", 0, "BEGIN:VCARD".length())) {
         var2 = var1 == 1 ? 1 : 5;
         var3 = "BEGIN:VCARD";
      } else if (var0.regionMatches(true, 0, "BEGIN:VCALENDAR", 0, "BEGIN:VCALENDAR".length())) {
         var2 = 2;
         var3 = "BEGIN:VCALENDAR";
      } else if (var0.regionMatches(true, 0, "END:VCARD", 0, "END:VCARD".length())) {
         var2 = var1 == 1 ? 1 : 5;
         var3 = "END:VCARD";
      } else if (var0.regionMatches(true, 0, "END:VCALENDAR", 0, "END:VCALENDAR".length())) {
         var2 = 2;
         var3 = "END:VCALENDAR";
      }

      if (var1 != -1 && var2 != -1 && var1 != var2) {
         if (var2 == 2) {
            throw new PIMException("VCALENDAR found within VCARD in input stream.");
         } else {
            throw new PIMException("VCARD found within VCALENDAR in input stream.");
         }
      } else {
         return var3;
      }
   }

   private static int e(String var0) {
      if (var0 != "BEGIN:VCARD" && var0 != "BEGIN:VCALENDAR") {
         return var0 != "END:VCARD" && var0 != "END:VCALENDAR" ? 0 : -1;
      } else {
         return 1;
      }
   }

   private String a(InputStreamReader var1) throws PIMException, IOException {
      String var7 = new String();
      boolean var2 = false;
      boolean var3 = false;
      boolean var4 = false;
      String var5 = null;
      Integer var6 = new Integer(-1);

      int var8;
      while(!var3) {
         if ((var8 = var1.read()) == -1) {
            throw new PIMException("Not enough data at start if line in the input stream.");
         }

         if (var8 != 13 && var8 != 10) {
            var3 = true;
            var7 = var7 + (char)var8;
         }
      }

      while(!var2) {
         do {
            var8 = var1.read();
            if ((var5 = a(var7 = var7 + (char)var8, var6)) == "END:VCARD" || var5 == "END:VCALENDAR") {
               return var7;
            }
         } while(var8 != 13 && var8 != -1);

         if (var8 == -1) {
            throw new PIMException("Not enough data in input stream, .looking for CR.");
         }

         var8 = var1.read();
         var7 = var7 + (char)var8;
         if (var8 == -1) {
            throw new PIMException("Not enough data in input stream, .looking for LINEFEED.");
         }

         if (var8 == 10) {
            var2 = true;
         }
      }

      return var7;
   }

   private static boolean d(int var0) {
      for(int var1 = 0; var1 < be.length; ++var1) {
         if (be[var1] == 5) {
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
      } catch (ClassNotFoundException var0) {
      }

      registerPIMCleanup();
      bg = SharedObjects.getLock("javax.microedition.pim.serialLock");
   }
}
