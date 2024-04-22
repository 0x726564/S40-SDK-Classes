package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DateInputField;
import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

public class DateField extends Item {
   public static final int DATE = 1;
   public static final int TIME = 2;
   public static final int DATE_TIME = 3;
   private static final long MILLIS_IN_DAY = 86400000L;
   static final boolean isSystem24HourClock = DeviceInfo.is24HoursClock();
   Vector dateInputFields;
   int inputMode;
   Calendar calendar;
   boolean dateInitialized;
   boolean timeInitialized;
   TextLine dateText;
   TextLine timeText;
   int highlight;
   boolean traversedIn;
   private boolean is24HourClock;
   private static boolean isInline = UIStyle.isInline();
   private DateEditor dateEditor;

   public DateField(String var1, int var2) {
      this(var1, var2, (TimeZone)null);
   }

   public DateField(String var1, int var2, TimeZone var3) {
      super(var1);
      this.dateInputFields = new Vector(6);
      this.inputMode = 1;
      this.calendar = null;
      this.dateInitialized = false;
      this.timeInitialized = false;
      this.dateText = null;
      this.timeText = null;
      this.highlight = -1;
      this.is24HourClock = isSystem24HourClock;
      this.dateEditor = null;
      synchronized(Display.LCDUILock) {
         if (var3 == null) {
            var3 = TimeZone.getDefault();
         }

         this.is24HourClock = isSystem24HourClock;
         this.calendar = Calendar.getInstance(var3);
         Class var5 = null;

         try {
            if (isInline) {
               var5 = Class.forName("javax.microedition.lcdui.DateFieldInlineEditor");
            } else {
               var5 = Class.forName("javax.microedition.lcdui.DateFieldScreenEditor");
            }

            this.dateEditor = (DateEditor)var5.newInstance();
            this.dateEditor.init(this, (String)null);
         } catch (Exception var8) {
         }

         this.setInputModeImpl(var2);
      }
   }

   public Date getDate() {
      synchronized(Display.LCDUILock) {
         return this.isInitialized() ? this.calendar.getTime() : null;
      }
   }

   public void setDate(Date var1) {
      synchronized(Display.LCDUILock) {
         this.setDateImpl(var1);
         this.updateFieldsFromCalendar();
         this.invalidate();
      }
   }

   public int getInputMode() {
      return this.inputMode;
   }

   public void setInputMode(int var1) {
      synchronized(Display.LCDUILock) {
         if (var1 != this.inputMode) {
            this.setInputModeImpl(var1);
         }

      }
   }

   void setOwner(Screen var1) {
      super.setOwner(var1);
      synchronized(Display.LCDUILock) {
         if (this.owner != null) {
            this.dateEditor.setTitle(this.owner.getTitle());
         }

      }
   }

   boolean equateNLA() {
      if (super.equateNLA()) {
         return true;
      } else {
         return (this.layout & 16384) != 16384;
      }
   }

   boolean equateNLB() {
      if (super.equateNLB()) {
         return true;
      } else {
         return (this.layout & 16384) != 16384;
      }
   }

   void setInputModeImpl(int var1) {
      boolean var2 = true;
      switch(var1) {
      case 1:
         this.dateInputFields.removeAllElements();
         this.insertDateInputFields();
         this.timeInitialized = false;
         if (this.dateInitialized) {
            var2 = false;
            this.dateEditor.initializeDateInputFields(1, true);
         }
         break;
      case 2:
         this.dateInputFields.removeAllElements();
         this.insertTimeInputFields();
         this.dateInitialized = false;
         this.calendar.set(5, 1);
         this.calendar.set(2, 0);
         this.calendar.set(1, 1970);
         if (this.timeInitialized) {
            var2 = false;
            this.dateEditor.initializeDateInputFields(2, true);
         }
         break;
      case 3:
         this.dateInputFields.removeAllElements();
         this.insertDateInputFields();
         this.insertTimeInputFields();
         byte var3 = 0;
         if (this.dateInitialized && this.timeInitialized) {
            var3 = 3;
         } else if (this.dateInitialized) {
            var3 = 1;
         } else if (this.timeInitialized) {
            var3 = 2;
         }

         if (var3 != 0) {
            var2 = false;
            this.dateEditor.initializeDateInputFields(var3, true);
         }
         break;
      default:
         throw new IllegalArgumentException();
      }

      if (!this.timeInitialized) {
         this.calendar.set(11, 24);
         this.calendar.set(12, 0);
         this.calendar.set(13, 0);
         this.calendar.set(14, 0);
      }

      this.inputMode = var1;
      if (var2) {
         this.setDateImpl((Date)null);
      } else {
         this.setDateImpl(this.calendar.getTime());
      }

      if (this.traversedIn) {
         this.highlight = 0;
      }

      this.invalidate();
      this.updateFieldsFromCalendar();
      if (this.owner != null) {
         this.owner.repaintFull();
      }

   }

   boolean get24HourClockFlag() {
      return this.is24HourClock;
   }

   void set24HourClockFlag(boolean var1) {
      this.is24HourClock = var1;
      this.updateFieldsFromCalendar();
      if (this.owner != null) {
         this.owner.repaintFull();
      }

   }

   int getHighlight() {
      return this.highlight;
   }

   boolean isFocusable() {
      return true;
   }

   int getHeight() {
      synchronized(Display.LCDUILock) {
         return this.dateEditor != null ? this.dateEditor.getHeight() : 0;
      }
   }

   int callPreferredWidth(int var1) {
      synchronized(Display.LCDUILock) {
         return this.dateEditor.callPreferredWidth(var1);
      }
   }

   int callPreferredHeight(int var1) {
      return this.getHeight();
   }

   int callMinimumWidth() {
      return this.callPreferredWidth(-1);
   }

   int callMinimumHeight() {
      return this.getHeight();
   }

   void callPaint(Graphics var1, int var2, int var3, boolean var4) {
      super.callPaint(var1, var2, var3, var4);
      synchronized(Display.LCDUILock) {
         this.dateEditor.setDisplay(this.owner.myDisplay);
         this.dateEditor.paintDateField(var1, var2, var3, var4);
      }
   }

   boolean callTraverse(int var1, int var2, int var3, int[] var4) {
      super.callTraverse(var1, var2, var3, var4);
      synchronized(Display.LCDUILock) {
         return this.dateEditor.callTraverse(var1, var2, var3, var4);
      }
   }

   void callTraverseOut() {
      super.callTraverseOut();
      synchronized(Display.LCDUILock) {
         DateInputField var2 = (DateInputField)this.dateInputFields.elementAt(this.highlight);
         var2.setFocus(false);
         this.traversedIn = false;
      }
   }

   void callKeyPressed(int var1, int var2) {
      boolean var3 = false;
      synchronized(Display.LCDUILock) {
         var3 = this.dateEditor.processKey(var1);
      }

      if (!var3) {
         super.callKeyPressed(var1, var2);
      }

   }

   Command[] getExtraCommands() {
      synchronized(Display.LCDUILock) {
         return this.dateEditor.getExtraCommands();
      }
   }

   boolean launchExtraCommand(Command var1) {
      synchronized(Display.LCDUILock) {
         return this.dateEditor.launchExtraCommand(var1);
      }
   }

   void callChangedItemState() {
      if (this.owner != null) {
         if (this.isInitialized()) {
            this.owner.changedItemState(this);
         }

      }
   }

   void updateFieldsFromCalendar() {
      Screen var1 = this.owner;
      int var2 = this.calendar.get(5);
      int var3 = this.calendar.get(2) + 1;
      int var4 = this.calendar.get(1);
      int var5 = this.calendar.get(12);
      int var7 = 0;
      int var6;
      if (this.is24HourClock) {
         var6 = this.calendar.get(11);
      } else {
         var6 = this.calendar.get(10);
         var6 = var6 == 0 ? 12 : var6;
         var7 = this.calendar.get(9);
      }

      DateInputField var8 = null;

      for(int var9 = 0; var9 < this.dateInputFields.size(); ++var9) {
         var8 = (DateInputField)this.dateInputFields.elementAt(var9);
         switch(var8.getSubType()) {
         case 1:
            var8.setValue(var5);
            break;
         case 2:
            var8.setValue(var6);
            break;
         case 3:
            var8.setValue(var2);
            break;
         case 4:
            var8.setValue(var3);
            break;
         case 5:
            var8.setValue(var4);
            break;
         case 6:
            var8.setValue(var7);
         }
      }

      this.updateTextsFromFields();
      if (var1 != null) {
         var1.repaintFull();
      }

   }

   private void setDateImpl(Date var1) {
      if (var1 == null) {
         this.dateEditor.initializeDateInputFields(this.inputMode, false);
      } else {
         switch(this.inputMode) {
         case 1:
            this.calendar.setTime(var1);
            this.calendar.set(11, 0);
            this.calendar.set(12, 0);
            this.dateEditor.initializeDateInputFields(1, true);
            break;
         case 2:
            int var2 = this.calendar.getTimeZone().getRawOffset();
            long var3 = var1.getTime() + (long)var2;
            if (var3 < 86400000L && var3 >= 0L) {
               this.calendar.setTime(var1);
               this.dateEditor.initializeDateInputFields(2, true);
            } else {
               this.dateEditor.initializeDateInputFields(2, false);
            }
            break;
         case 3:
            this.calendar.setTime(var1);
            this.dateEditor.initializeDateInputFields(3, true);
         }

         this.calendar.set(13, 0);
         this.calendar.set(14, 0);
      }
   }

   private boolean isInitialized() {
      return this.inputMode == 3 && this.dateInitialized && this.timeInitialized || this.inputMode == 1 && this.dateInitialized || this.inputMode == 2 && this.timeInitialized;
   }

   private void updateTextsFromFields() {
      StringBuffer var1 = new StringBuffer(0);
      StringBuffer var2 = new StringBuffer(0);
      boolean var3 = false;
      boolean var4 = false;
      if (!isInline) {
         if ((this.inputMode == 1 || this.inputMode == 3) && !this.dateInitialized) {
            var1.append(TextDatabase.getText(28));
            var3 = true;
         }

         if ((this.inputMode == 2 || this.inputMode == 3) && !this.timeInitialized) {
            var2.append(TextDatabase.getText(27));
            var4 = true;
         }
      }

      for(int var5 = 0; var5 < this.dateInputFields.size(); ++var5) {
         DateInputField var6 = (DateInputField)this.dateInputFields.elementAt(var5);
         switch(var6.getSubType()) {
         case 1:
         case 2:
         case 6:
            if (!var4) {
               var2.append(var6.getDigits());
            }
            break;
         case 3:
         case 4:
         case 5:
            if (!var3) {
               var1.append(var6.getDigits());
            }
         }
      }

      if (var1.length() > 0) {
         this.dateText = TextBreaker.breakOneLineTextInZone(this.dateEditor.getZone(1), true, true, var1.toString(), 0, false);
      } else if (!isInline) {
         var1.append(TextDatabase.getText(28));
      }

      if (var2.length() > 0) {
         this.timeText = TextBreaker.breakOneLineTextInZone(this.dateEditor.getZone(2), true, true, var2.toString(), 0, false);
      }

      if (this.inputMode == 1) {
         this.timeText = null;
      } else if (this.inputMode == 2) {
         this.dateText = null;
      }

   }

   private void insertDateInputFields() {
      String var1 = DeviceInfo.getDateFormatString();
      DateInputField var2 = null;
      byte var3 = 0;
      int var4 = 0;

      for(int var5 = 0; var5 < var1.length(); ++var5) {
         switch(var1.charAt(var5)) {
         case 'D':
         case 'd':
            if (var3 != 3) {
               var2 = new DateInputField(3, 1, 31, false);
               var2.initialise(!isInline);
               var3 = 3;
               this.dateInputFields.addElement(var2);
               ++var4;
            }
            break;
         case 'M':
         case 'm':
            if (var3 != 4) {
               var2 = new DateInputField(4, 1, 12, false);
               var2.initialise(!isInline);
               var3 = 4;
               this.dateInputFields.addElement(var2);
               ++var4;
            }
            break;
         case 'Y':
         case 'y':
            if (var3 != 5) {
               var2 = new DateInputField(5, 1, 9999, false);
               var2.initialise(!isInline);
               var3 = 5;
               this.dateInputFields.addElement(var2);
               ++var4;
            }
            break;
         default:
            if (var4 != 0) {
               var2.setSeparator(var1.charAt(var5));
            }
         }
      }

   }

   private void insertTimeInputFields() {
      String var1 = TextDatabase.getText(16);
      DateInputField var2 = null;
      byte var3 = 23;
      byte var4 = 0;
      int var5 = 0;
      if (!this.is24HourClock) {
         var3 = 12;
         var4 = 1;
      }

      byte var6 = 0;

      for(int var7 = 0; var7 < var1.length(); ++var7) {
         switch(var1.charAt(var7)) {
         case 'H':
         case 'h':
            if (var6 != 2) {
               var2 = new DateInputField(2, var4, var3, false);
               var2.initialise(!isInline);
               var6 = 2;
               this.dateInputFields.addElement(var2);
               ++var5;
            }
            break;
         case 'M':
         case 'm':
            if (var6 != 1) {
               var2 = new DateInputField(1, 0, 59, false);
               var2.initialise(!isInline);
               var6 = 1;
               this.dateInputFields.addElement(var2);
               ++var5;
            }
            break;
         default:
            if (var5 != 0) {
               var2.setSeparator(var1.charAt(var7));
               var2.initialise(!isInline);
            }
         }
      }

      if (!this.is24HourClock) {
         var2 = new DateInputField(6, 0, 1, false);
         var2.initialise(!isInline);
         this.dateInputFields.addElement(var2);
      }

   }
}
