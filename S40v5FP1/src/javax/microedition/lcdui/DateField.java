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

   public DateField(String label, int mode) {
      this(label, mode, (TimeZone)null);
   }

   public DateField(String label, int mode, TimeZone timeZone) {
      super(label);
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
         if (timeZone == null) {
            timeZone = TimeZone.getDefault();
         }

         this.is24HourClock = isSystem24HourClock;
         this.calendar = Calendar.getInstance(timeZone);
         Class dfClass = null;

         try {
            if (isInline) {
               dfClass = Class.forName("javax.microedition.lcdui.DateFieldInlineEditor");
            } else {
               dfClass = Class.forName("javax.microedition.lcdui.DateFieldScreenEditor");
            }

            this.dateEditor = (DateEditor)dfClass.newInstance();
            this.dateEditor.init(this, (String)null);
         } catch (Exception var8) {
         }

         this.setInputModeImpl(mode);
      }
   }

   public Date getDate() {
      synchronized(Display.LCDUILock) {
         return this.isInitialized() ? this.calendar.getTime() : null;
      }
   }

   public void setDate(Date date) {
      synchronized(Display.LCDUILock) {
         this.setDateImpl(date);
         this.updateFieldsFromCalendar();
         this.invalidate();
      }
   }

   public int getInputMode() {
      return this.inputMode;
   }

   public void setInputMode(int mode) {
      synchronized(Display.LCDUILock) {
         if (mode != this.inputMode) {
            this.setInputModeImpl(mode);
         }

      }
   }

   void setOwner(Screen owner) {
      super.setOwner(owner);
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

   void setInputModeImpl(int mode) {
      boolean resetDateValue = true;
      switch(mode) {
      case 1:
         this.dateInputFields.removeAllElements();
         this.insertDateInputFields();
         this.timeInitialized = false;
         if (this.dateInitialized) {
            resetDateValue = false;
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
            resetDateValue = false;
            this.dateEditor.initializeDateInputFields(2, true);
         }
         break;
      case 3:
         this.dateInputFields.removeAllElements();
         this.insertDateInputFields();
         this.insertTimeInputFields();
         int typeToInitialize = 0;
         if (this.dateInitialized && this.timeInitialized) {
            typeToInitialize = 3;
         } else if (this.dateInitialized) {
            typeToInitialize = 1;
         } else if (this.timeInitialized) {
            typeToInitialize = 2;
         }

         if (typeToInitialize != 0) {
            resetDateValue = false;
            this.dateEditor.initializeDateInputFields(typeToInitialize, true);
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

      this.inputMode = mode;
      if (resetDateValue) {
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

   void set24HourClockFlag(boolean is24HourClock) {
      this.is24HourClock = is24HourClock;
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

   int callPreferredWidth(int h) {
      synchronized(Display.LCDUILock) {
         return this.dateEditor.callPreferredWidth(h);
      }
   }

   int callPreferredHeight(int w) {
      return this.getHeight();
   }

   int callMinimumWidth() {
      return this.callPreferredWidth(-1);
   }

   int callMinimumHeight() {
      return this.getHeight();
   }

   void callPaint(Graphics g, int w, int h, boolean isFocused) {
      super.callPaint(g, w, h, isFocused);
      synchronized(Display.LCDUILock) {
         this.dateEditor.setDisplay(this.owner.myDisplay);
         this.dateEditor.paintDateField(g, w, h, isFocused);
      }
   }

   boolean callTraverse(int dir, int viewportWidth, int viewportHeight, int[] visRect) {
      super.callTraverse(dir, viewportWidth, viewportHeight, visRect);
      synchronized(Display.LCDUILock) {
         return this.dateEditor.callTraverse(dir, viewportWidth, viewportHeight, visRect);
      }
   }

   void callTraverseOut() {
      super.callTraverseOut();
      synchronized(Display.LCDUILock) {
         DateInputField dif = (DateInputField)this.dateInputFields.elementAt(this.highlight);
         dif.setFocus(false);
         this.traversedIn = false;
      }
   }

   void callKeyPressed(int keyCode, int keyDataIdx) {
      boolean returnValue = false;
      synchronized(Display.LCDUILock) {
         returnValue = this.dateEditor.processKey(keyCode);
      }

      if (!returnValue) {
         super.callKeyPressed(keyCode, keyDataIdx);
      }

   }

   Command[] getExtraCommands() {
      synchronized(Display.LCDUILock) {
         return this.dateEditor.getExtraCommands();
      }
   }

   boolean launchExtraCommand(Command c) {
      synchronized(Display.LCDUILock) {
         return this.dateEditor.launchExtraCommand(c);
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
      Displayable parent = this.owner;
      int day = this.calendar.get(5);
      int month = this.calendar.get(2) + 1;
      int year = this.calendar.get(1);
      int minute = this.calendar.get(12);
      int am_pm = 0;
      int hour;
      if (this.is24HourClock) {
         hour = this.calendar.get(11);
      } else {
         hour = this.calendar.get(10);
         hour = hour == 0 ? 12 : hour;
         am_pm = this.calendar.get(9);
      }

      DateInputField dif = null;

      for(int i = 0; i < this.dateInputFields.size(); ++i) {
         dif = (DateInputField)this.dateInputFields.elementAt(i);
         switch(dif.getSubType()) {
         case 1:
            dif.setValue(minute);
            break;
         case 2:
            dif.setValue(hour);
            break;
         case 3:
            dif.setValue(day);
            break;
         case 4:
            dif.setValue(month);
            break;
         case 5:
            dif.setValue(year);
            break;
         case 6:
            dif.setValue(am_pm);
         }
      }

      this.updateTextsFromFields();
      if (parent != null) {
         parent.repaintFull();
      }

   }

   private void setDateImpl(Date date) {
      if (date == null) {
         this.dateEditor.initializeDateInputFields(this.inputMode, false);
      } else {
         switch(this.inputMode) {
         case 1:
            this.calendar.setTime(date);
            this.calendar.set(11, 0);
            this.calendar.set(12, 0);
            this.dateEditor.initializeDateInputFields(1, true);
            break;
         case 2:
            int timeZoneOffset = this.calendar.getTimeZone().getRawOffset();
            long localStandardTime = date.getTime() + (long)timeZoneOffset;
            if (localStandardTime < 86400000L && localStandardTime >= 0L) {
               this.calendar.setTime(date);
               this.dateEditor.initializeDateInputFields(2, true);
            } else {
               this.dateEditor.initializeDateInputFields(2, false);
            }
            break;
         case 3:
            this.calendar.setTime(date);
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
      StringBuffer dateBuff = new StringBuffer(0);
      StringBuffer timeBuff = new StringBuffer(0);
      boolean skipUpdatingDate = false;
      boolean skipUpdatingTime = false;
      if (!isInline) {
         if ((this.inputMode == 1 || this.inputMode == 3) && !this.dateInitialized) {
            dateBuff.append(TextDatabase.getText(28));
            skipUpdatingDate = true;
         }

         if ((this.inputMode == 2 || this.inputMode == 3) && !this.timeInitialized) {
            timeBuff.append(TextDatabase.getText(27));
            skipUpdatingTime = true;
         }
      }

      for(int i = 0; i < this.dateInputFields.size(); ++i) {
         DateInputField dif = (DateInputField)this.dateInputFields.elementAt(i);
         switch(dif.getSubType()) {
         case 1:
         case 2:
         case 6:
            if (!skipUpdatingTime) {
               timeBuff.append(dif.getDigits());
            }
            break;
         case 3:
         case 4:
         case 5:
            if (!skipUpdatingDate) {
               dateBuff.append(dif.getDigits());
            }
         }
      }

      if (dateBuff.length() > 0) {
         this.dateText = TextBreaker.breakOneLineTextInZone(this.dateEditor.getZone(1), true, true, dateBuff.toString(), 0, false);
      } else if (!isInline) {
         dateBuff.append(TextDatabase.getText(28));
      }

      if (timeBuff.length() > 0) {
         this.timeText = TextBreaker.breakOneLineTextInZone(this.dateEditor.getZone(2), true, true, timeBuff.toString(), 0, false);
      }

      if (this.inputMode == 1) {
         this.timeText = null;
      } else if (this.inputMode == 2) {
         this.dateText = null;
      }

   }

   private void insertDateInputFields() {
      String dateFormat = DeviceInfo.getDateFormatString();
      DateInputField dif = null;
      int previousField = 0;
      int numberOfDateElements = 0;

      for(int i = 0; i < dateFormat.length(); ++i) {
         switch(dateFormat.charAt(i)) {
         case 'D':
         case 'd':
            if (previousField != 3) {
               dif = new DateInputField(3, 1, 31, false);
               dif.initialise(!isInline);
               previousField = 3;
               this.dateInputFields.addElement(dif);
               ++numberOfDateElements;
            }
            break;
         case 'M':
         case 'm':
            if (previousField != 4) {
               dif = new DateInputField(4, 1, 12, false);
               dif.initialise(!isInline);
               previousField = 4;
               this.dateInputFields.addElement(dif);
               ++numberOfDateElements;
            }
            break;
         case 'Y':
         case 'y':
            if (previousField != 5) {
               dif = new DateInputField(5, 1, 9999, false);
               dif.initialise(!isInline);
               previousField = 5;
               this.dateInputFields.addElement(dif);
               ++numberOfDateElements;
            }
            break;
         default:
            if (numberOfDateElements != 0) {
               dif.setSeparator(dateFormat.charAt(i));
            }
         }
      }

   }

   private void insertTimeInputFields() {
      String timeFormat = TextDatabase.getText(16);
      DateInputField dif = null;
      int maxHours = 23;
      int minHours = 0;
      int numberOfTimeElements = 0;
      if (!this.is24HourClock) {
         maxHours = 12;
         minHours = 1;
      }

      int previousField = 0;

      for(int i = 0; i < timeFormat.length(); ++i) {
         switch(timeFormat.charAt(i)) {
         case 'H':
         case 'h':
            if (previousField != 2) {
               dif = new DateInputField(2, minHours, maxHours, false);
               dif.initialise(!isInline);
               previousField = 2;
               this.dateInputFields.addElement(dif);
               ++numberOfTimeElements;
            }
            break;
         case 'M':
         case 'm':
            if (previousField != 1) {
               dif = new DateInputField(1, 0, 59, false);
               dif.initialise(!isInline);
               previousField = 1;
               this.dateInputFields.addElement(dif);
               ++numberOfTimeElements;
            }
            break;
         default:
            if (numberOfTimeElements != 0) {
               dif.setSeparator(timeFormat.charAt(i));
               dif.initialise(!isInline);
            }
         }
      }

      if (!this.is24HourClock) {
         dif = new DateInputField(6, 0, 1, false);
         dif.initialise(!isInline);
         this.dateInputFields.addElement(dif);
      }

   }
}
