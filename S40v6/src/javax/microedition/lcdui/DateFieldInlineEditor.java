package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DateInputField;
import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import java.util.Vector;

class DateFieldInlineEditor implements DateEditor {
   private static final Command AM_COMMAND = new Command(12, 39);
   private static final Command PM_COMMAND = new Command(12, 40);
   private static final Command DATE_MENU_VIEW_COMMAND = new Command(12, 78);
   private static final Zone DATEFIELD_DATE_ADVICE_ZONE;
   private static final Zone DATEFIELD_DATE_EDIT_ZONE;
   private static final Zone DATEFIELD_TIME_ADVICE_ZONE;
   private static final Zone DATEFIELD_TIME_EDIT_ZONE;
   private static final TextLine DATE_ADVICE_TEXT;
   private static final TextLine TIME_ADVICE_TEXT;
   private static final Command[] extraCommands;
   private static final Command[] dateCommands;
   private static Object dateLock;
   private DateField owner = null;
   private boolean digitsIncompleted = false;
   private static int selectedYear;
   private static int selectedMonth;
   private static int selectedDay;

   public void init(DateField parent, String title) {
      this.owner = parent;
   }

   public void setTitle(String title) {
   }

   public void setDisplay(Display d) {
   }

   public Zone getZone(int inputMode) {
      return inputMode == 1 ? DATEFIELD_DATE_EDIT_ZONE : DATEFIELD_TIME_EDIT_ZONE;
   }

   public int getHeight() {
      int height = 0;
      int numOfRows = 0;
      if (this.owner != null && this.owner.dateText != null) {
         ++numOfRows;
      }

      if (this.owner != null && this.owner.timeText != null) {
         ++numOfRows;
      }

      if (numOfRows == 1) {
         height += DATEFIELD_DATE_EDIT_ZONE.height + DATEFIELD_DATE_EDIT_ZONE.y;
      } else if (numOfRows == 2) {
         height += DATEFIELD_TIME_EDIT_ZONE.height + DATEFIELD_TIME_EDIT_ZONE.y;
      }

      return height;
   }

   public int callPreferredWidth(int h) {
      return DATEFIELD_DATE_EDIT_ZONE.width;
   }

   public void paintDateField(Graphics g, int w, int h, boolean isFocused) {
      int posX = g.getTranslateX();
      int posY = g.getTranslateY();
      int posXDate = posX + DATEFIELD_DATE_EDIT_ZONE.getMarginLeft() + DATEFIELD_DATE_EDIT_ZONE.x;
      int posXTime = posXDate;
      int borderType = DATEFIELD_DATE_EDIT_ZONE.getBorderType();
      int selectedItem = 0;
      if (isFocused) {
         DateInputField highlightedDif = (DateInputField)this.owner.dateInputFields.elementAt(this.owner.highlight);
         if (highlightedDif != null) {
            if (highlightedDif.getType() == 1) {
               selectedItem = 1;
            } else if (highlightedDif.getType() == 2) {
               selectedItem = 2;
            }
         }
      }

      if (this.owner != null && posY <= g.getHeight() && posY + this.getHeight() >= 0) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
         int posYDate = posY + DATEFIELD_DATE_EDIT_ZONE.getMarginTop() + DATEFIELD_DATE_EDIT_ZONE.y;
         int posYTime = posY + (this.owner.inputMode == 3 ? DATEFIELD_TIME_EDIT_ZONE.getMarginTop() + DATEFIELD_TIME_EDIT_ZONE.y : DATEFIELD_DATE_EDIT_ZONE.getMarginTop() + DATEFIELD_DATE_EDIT_ZONE.y);
         ColorCtrl colorControl = ng.getColorCtrl();
         colorControl.setBgColor(UIStyle.COLOUR_BACKGROUND);
         colorControl.setFgColor(UIStyle.COLOUR_TEXT);
         if (this.owner.inputMode == 3) {
            if (UIStyle.isAlignedLeftToRight) {
               DATE_ADVICE_TEXT.setAlignment(1);
               TIME_ADVICE_TEXT.setAlignment(1);
               g.getImpl().drawText(DATE_ADVICE_TEXT, (short)(posX + DATEFIELD_DATE_ADVICE_ZONE.x + (DATEFIELD_DATE_ADVICE_ZONE.x == DATEFIELD_DATE_EDIT_ZONE.x ? DATEFIELD_DATE_EDIT_ZONE.getMarginLeft() : 0)), (short)(DATEFIELD_DATE_ADVICE_ZONE.y + posY), (short)DATE_ADVICE_TEXT.getTextLineWidth());
               g.getImpl().drawText(TIME_ADVICE_TEXT, (short)(posX + DATEFIELD_TIME_ADVICE_ZONE.x + (DATEFIELD_TIME_ADVICE_ZONE.x == DATEFIELD_TIME_EDIT_ZONE.x ? DATEFIELD_TIME_EDIT_ZONE.getMarginLeft() : 0)), (short)(DATEFIELD_TIME_ADVICE_ZONE.y + posY), (short)TIME_ADVICE_TEXT.getTextLineWidth());
            } else {
               DATE_ADVICE_TEXT.setAlignment(3);
               TIME_ADVICE_TEXT.setAlignment(3);
               g.getImpl().drawText(DATE_ADVICE_TEXT, (short)(posX + DATEFIELD_DATE_ADVICE_ZONE.x + DATEFIELD_DATE_ADVICE_ZONE.width - (DATEFIELD_DATE_ADVICE_ZONE.x == DATEFIELD_DATE_EDIT_ZONE.x ? DATEFIELD_DATE_EDIT_ZONE.getMarginRight() : 0)), (short)(DATEFIELD_DATE_ADVICE_ZONE.y + posY), (short)DATE_ADVICE_TEXT.getTextLineWidth());
               g.getImpl().drawText(TIME_ADVICE_TEXT, (short)(posX + DATEFIELD_TIME_ADVICE_ZONE.x + DATEFIELD_TIME_ADVICE_ZONE.width - (DATEFIELD_TIME_ADVICE_ZONE.x == DATEFIELD_TIME_EDIT_ZONE.x ? DATEFIELD_TIME_EDIT_ZONE.getMarginRight() : 0)), (short)(DATEFIELD_TIME_ADVICE_ZONE.y + posY), (short)TIME_ADVICE_TEXT.getTextLineWidth());
            }

            Displayable.uistyle.drawBorder(g.getImpl(), posX + DATEFIELD_DATE_EDIT_ZONE.x, posY + DATEFIELD_DATE_EDIT_ZONE.y, DATEFIELD_DATE_EDIT_ZONE.width, DATEFIELD_DATE_EDIT_ZONE.height, borderType, selectedItem == 1);
            Displayable.uistyle.drawBorder(g.getImpl(), posX + DATEFIELD_TIME_EDIT_ZONE.x, posY + DATEFIELD_TIME_EDIT_ZONE.y, DATEFIELD_TIME_EDIT_ZONE.width, DATEFIELD_TIME_EDIT_ZONE.height, borderType, selectedItem == 2);
         } else {
            if (UIStyle.isAlignedLeftToRight) {
               DATE_ADVICE_TEXT.setAlignment(1);
               TIME_ADVICE_TEXT.setAlignment(1);
               g.getImpl().drawText(this.owner.inputMode == 1 ? DATE_ADVICE_TEXT : TIME_ADVICE_TEXT, (short)(posX + DATEFIELD_DATE_ADVICE_ZONE.x + (DATEFIELD_DATE_ADVICE_ZONE.x == DATEFIELD_DATE_EDIT_ZONE.x ? DATEFIELD_DATE_EDIT_ZONE.getMarginLeft() : 0)), (short)(DATEFIELD_DATE_ADVICE_ZONE.y + posY), this.owner.inputMode == 1 ? (short)DATE_ADVICE_TEXT.getTextLineWidth() : (short)TIME_ADVICE_TEXT.getTextLineWidth());
            } else {
               DATE_ADVICE_TEXT.setAlignment(3);
               TIME_ADVICE_TEXT.setAlignment(3);
               g.getImpl().drawText(this.owner.inputMode == 1 ? DATE_ADVICE_TEXT : TIME_ADVICE_TEXT, (short)(posX + DATEFIELD_DATE_ADVICE_ZONE.x + DATEFIELD_DATE_ADVICE_ZONE.width - (DATEFIELD_DATE_ADVICE_ZONE.x == DATEFIELD_DATE_EDIT_ZONE.x ? DATEFIELD_DATE_EDIT_ZONE.getMarginRight() : 0)), (short)(DATEFIELD_DATE_ADVICE_ZONE.y + posY), this.owner.inputMode == 1 ? (short)DATE_ADVICE_TEXT.getTextLineWidth() : (short)TIME_ADVICE_TEXT.getTextLineWidth());
            }

            Displayable.uistyle.drawBorder(g.getImpl(), posX + DATEFIELD_DATE_EDIT_ZONE.x, posY + DATEFIELD_DATE_EDIT_ZONE.y, DATEFIELD_DATE_EDIT_ZONE.width, DATEFIELD_DATE_EDIT_ZONE.height, borderType, isFocused);
         }

         ng.setFont(DATEFIELD_DATE_EDIT_ZONE.getFont());
         int areaHeight = DATEFIELD_DATE_EDIT_ZONE.height - DATEFIELD_DATE_EDIT_ZONE.getMarginTop() - DATEFIELD_DATE_EDIT_ZONE.getMarginBottom();
         boolean renderedAMPM = false;
         int timeFieldWidth;
         if (!UIStyle.isAlignedLeftToRight) {
            timeFieldWidth = 0;
            int dateFieldWidth = 0;

            int i;
            DateInputField dif;
            for(i = 0; i < this.owner.dateInputFields.size(); ++i) {
               dif = (DateInputField)this.owner.dateInputFields.elementAt(i);
               if (dif.type == 2) {
                  if (dif.getSubType() == 6) {
                     TextLine textLine;
                     if (dif.fieldValue == 0) {
                        textLine = TextBreaker.breakOneLineTextInZone(UIStyle.getUIStyle().getZone(45), true, true, TextDatabase.getText(39), 0, false);
                     } else {
                        textLine = TextBreaker.breakOneLineTextInZone(UIStyle.getUIStyle().getZone(45), true, true, TextDatabase.getText(40), 0, false);
                     }

                     timeFieldWidth += textLine.getTextLineWidth() + 2;
                  } else {
                     timeFieldWidth += dif.getRenderWidth(ng) + 2;
                  }
               } else {
                  dateFieldWidth += dif.getRenderWidth(ng) + 2;
               }
            }

            posXTime = posX + DATEFIELD_TIME_EDIT_ZONE.x + DATEFIELD_TIME_EDIT_ZONE.width - DATEFIELD_TIME_EDIT_ZONE.getMarginRight() - timeFieldWidth;
            posXDate = posX + DATEFIELD_DATE_EDIT_ZONE.x + DATEFIELD_DATE_EDIT_ZONE.width - DATEFIELD_DATE_EDIT_ZONE.getMarginRight() - dateFieldWidth;

            for(i = 0; i < this.owner.dateInputFields.size(); ++i) {
               dif = (DateInputField)this.owner.dateInputFields.elementAt(i);
               if (dif.getSubType() == 6) {
                  if (selectedItem == 2) {
                     colorControl.setBgColor(UIStyle.COLOUR_HIGHLIGHT);
                     colorControl.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
                  } else {
                     colorControl.setBgColor(UIStyle.COLOUR_BACKGROUND);
                     colorControl.setFgColor(UIStyle.COLOUR_EDITOR_UNFOCUSED);
                  }

                  posXTime += dif.render(posXTime, posYTime, ng, areaHeight);
                  renderedAMPM = true;
               }
            }
         }

         for(timeFieldWidth = 0; timeFieldWidth < this.owner.dateInputFields.size(); ++timeFieldWidth) {
            DateInputField dif = (DateInputField)this.owner.dateInputFields.elementAt(timeFieldWidth);
            switch(dif.getSubType()) {
            case 1:
            case 2:
               if (selectedItem == 2) {
                  colorControl.setBgColor(UIStyle.COLOUR_HIGHLIGHT);
                  colorControl.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
               } else {
                  colorControl.setBgColor(UIStyle.COLOUR_BACKGROUND);
                  colorControl.setFgColor(UIStyle.COLOUR_EDITOR_UNFOCUSED);
               }

               posXTime += dif.render(posXTime, posYTime, ng, areaHeight);
               break;
            case 3:
            case 4:
            case 5:
               if (selectedItem == 1) {
                  colorControl.setBgColor(UIStyle.COLOUR_HIGHLIGHT);
                  colorControl.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
               } else {
                  colorControl.setBgColor(UIStyle.COLOUR_BACKGROUND);
                  colorControl.setFgColor(UIStyle.COLOUR_EDITOR_UNFOCUSED);
               }

               posXDate += dif.render(posXDate, posYDate, ng, areaHeight);
               break;
            case 6:
               if (!renderedAMPM) {
                  if (selectedItem == 2) {
                     colorControl.setBgColor(UIStyle.COLOUR_HIGHLIGHT);
                     colorControl.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
                  } else {
                     colorControl.setBgColor(UIStyle.COLOUR_BACKGROUND);
                     colorControl.setFgColor(UIStyle.COLOUR_EDITOR_UNFOCUSED);
                  }

                  posXTime += dif.render(posXTime, posYTime, ng, areaHeight);
                  renderedAMPM = true;
               }
            }
         }

         colorControl.setBgColor(UIStyle.COLOUR_BACKGROUND);
         colorControl.setFgColor(UIStyle.COLOUR_TEXT);
      }

   }

   public boolean callTraverse(int dir, int viewportWidth, int viewportHeight, int[] visRect) {
      boolean returnValue = true;
      int prevHiglight = false;
      DateInputField dif = null;
      int prevType = -1;
      int prevHiglight = this.owner.highlight;
      if (this.owner.highlight >= 0 && this.owner.highlight < this.owner.dateInputFields.size()) {
         dif = (DateInputField)this.owner.dateInputFields.elementAt(this.owner.highlight);
         prevType = dif.getType();
      }

      int numElems = this.owner.dateInputFields.size();
      int numOfDateElements = 0;
      int numOfTimeElements = 0;

      for(int i = 0; i < numElems; ++i) {
         dif = (DateInputField)this.owner.dateInputFields.elementAt(i);
         if (dir != 0) {
            dif.setFocus(false);
         }

         switch(dif.getSubType()) {
         case 1:
         case 2:
         case 6:
            ++numOfTimeElements;
            break;
         case 3:
         case 4:
         case 5:
            ++numOfDateElements;
         }
      }

      boolean isSingleLine = numOfDateElements <= 0 || numOfTimeElements <= 0;
      if (!this.owner.traversedIn) {
         this.owner.traversedIn = true;
         switch(dir) {
         case 0:
         case 5:
         case 6:
            this.owner.highlight = 0;
            break;
         case 1:
         case 2:
            this.owner.highlight = numElems - 1;
         case 3:
         case 4:
         }

         if (this.owner.highlight != -1) {
            dif = (DateInputField)this.owner.dateInputFields.elementAt(this.owner.highlight);
            dif.setFocus(true);
         }
      } else {
         DateField var10000;
         switch(dir) {
         case 1:
            if (!isSingleLine && this.owner.highlight >= numOfDateElements) {
               var10000 = this.owner;
               var10000.highlight -= numOfDateElements;
               break;
            }

            returnValue = false;
            break;
         case 2:
            if (this.owner.highlight > 0) {
               --this.owner.highlight;
            } else {
               returnValue = false;
            }
         case 3:
         case 4:
         default:
            break;
         case 5:
            if (this.owner.highlight < numElems - 1) {
               ++this.owner.highlight;
            } else {
               returnValue = false;
            }
            break;
         case 6:
            if (!isSingleLine && this.owner.highlight < numOfDateElements) {
               var10000 = this.owner;
               var10000.highlight += numOfDateElements;
               if (this.owner.highlight >= numElems) {
                  --this.owner.highlight;
               }
            } else {
               returnValue = false;
            }
         }

         if (dir != 0 && this.digitsIncompleted) {
            for(dif = this.validateDateEntries(); dif != null; dif = this.validateDateEntries()) {
               dif.incrementValue();
            }

            this.updateCalendarFromEditor(3);
            this.digitsIncompleted = false;
         }
      }

      if (dir != 0) {
         dif = (DateInputField)this.owner.dateInputFields.elementAt(this.owner.highlight);
         dif.setFocus(true);
         int currentType = dif.getType();
         if (prevHiglight != this.owner.highlight && currentType != prevType) {
            this.owner.owner.updateSoftkeys(true);
         }
      }

      if (returnValue) {
         visRect[1] = this.owner.getLabelHeight(visRect[1]);
         if (this.owner.inputMode == 3 && this.owner.highlight >= numOfDateElements) {
            visRect[1] += DATEFIELD_TIME_ADVICE_ZONE.y;
         }

         visRect[3] = DATEFIELD_DATE_EDIT_ZONE.y + DATEFIELD_DATE_EDIT_ZONE.height;
         this.owner.repaint();
      }

      return returnValue;
   }

   public boolean processKey(int keyCode) {
      int fieldType = false;
      DateInputField dif;
      int fieldType;
      switch(keyCode) {
      case -10:
         return false;
      case 32:
      case 35:
      case 42:
      default:
         dif = (DateInputField)this.owner.dateInputFields.elementAt(this.owner.highlight);
         fieldType = dif.getType();
         if (dif.getSubType() == 6) {
            if (dif.isInitialized()) {
               dif.setValue(dif.getValue() ^ 1);
            } else {
               this.initializeDateInputFields(2, true);
               dif.setFocus(true);
            }
         }
         break;
      case 48:
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
         dif = (DateInputField)this.owner.dateInputFields.elementAt(this.owner.highlight);
         if (!dif.isInitialized()) {
            this.initializeDateInputFields(dif.getType(), true);
         }

         fieldType = dif.getType();
         if (dif.getSubType() != 6) {
            int value = dif.addDigit((char)keyCode);
            this.digitsIncompleted = false;
            int currentValue;
            label57:
            switch(value) {
            case -1:
               dif.setFocus(true);
               this.owner.highlight = this.owner.dateInputFields.indexOf(dif);
               dif = this.validateDateEntries();

               while(true) {
                  if (dif == null) {
                     break label57;
                  }

                  currentValue = dif.getValue();
                  --currentValue;
                  dif.setValue(currentValue);
                  dif = this.validateDateEntries();
               }
            case 0:
               fieldType = 0;
               this.digitsIncompleted = true;
               break;
            case 1:
               dif.setFocus(false);
               int type = dif.getType();
               DateInputField difValidated = this.validateDateEntries();
               if (difValidated == null) {
                  for(currentValue = 0; currentValue < this.owner.dateInputFields.size(); ++currentValue) {
                     this.owner.highlight = ++this.owner.highlight % this.owner.dateInputFields.size();
                     dif = (DateInputField)this.owner.dateInputFields.elementAt(this.owner.highlight);
                     if (type == dif.getType()) {
                        break;
                     }
                  }

                  dif.setFocus(true);
               } else {
                  difValidated.setFocus(true);

                  for(this.owner.highlight = this.owner.dateInputFields.indexOf(difValidated); difValidated != null; difValidated = this.validateDateEntries()) {
                     currentValue = difValidated.getValue();
                     --currentValue;
                     difValidated.setValue(currentValue);
                  }
               }
            }
         } else {
            dif.setValue(dif.getValue() ^ 1);
            dif.setFocus(true);
         }
      }

      if (fieldType != 0) {
         this.updateCalendarFromEditor(fieldType);
      }

      this.owner.repaint();
      return true;
   }

   public Command[] getExtraCommands() {
      int highlight = this.owner.highlight;
      Vector inputFields = this.owner.dateInputFields;
      if (highlight >= 0 && highlight < inputFields.size()) {
         DateInputField dif = (DateInputField)inputFields.elementAt(highlight);
         int type = dif.getType();
         if (type == 2 && !this.owner.get24HourClockFlag()) {
            return extraCommands;
         }

         if (type == 1) {
            return dateCommands;
         }
      }

      return null;
   }

   public boolean launchExtraCommand(Command c) {
      int status;
      if (c != AM_COMMAND && c != PM_COMMAND) {
         if (c == DATE_MENU_VIEW_COMMAND) {
            synchronized(dateLock) {
               status = this.chooseDate(this.owner.calendar.get(1), this.owner.calendar.get(2), this.owner.calendar.get(5));
               if (status == 1) {
                  this.owner.calendar.set(1, selectedYear);
                  this.owner.calendar.set(2, selectedMonth);
                  this.owner.calendar.set(5, selectedDay);
                  this.initializeDateInputFields(1, true);
                  this.owner.updateFieldsFromCalendar();
                  this.owner.repaint();
               }
            }
         }
      } else {
         DateInputField dif = null;

         for(status = 0; status < this.owner.dateInputFields.size(); ++status) {
            dif = (DateInputField)this.owner.dateInputFields.elementAt(status);
            if (dif.getSubType() == 6) {
               dif.setValue(c == AM_COMMAND ? 0 : 1);
               if (!dif.isInitialized()) {
                  this.initializeDateInputFields(2, true);
               }

               this.owner.repaint();
            }
         }
      }

      return false;
   }

   public void initializeDateInputFields(int type, boolean value) {
      DateInputField dif = null;

      for(int i = 0; i < this.owner.dateInputFields.size(); ++i) {
         dif = (DateInputField)this.owner.dateInputFields.elementAt(i);
         if (type == 3 || dif.getType() == type) {
            dif.initialise(value);
         }
      }

      switch(type) {
      case 1:
         this.owner.dateInitialized = value;
         break;
      case 2:
         this.owner.timeInitialized = value;
         break;
      case 3:
         this.owner.dateInitialized = value;
         this.owner.timeInitialized = value;
      }

   }

   public String toString() {
      StringBuffer str = new StringBuffer();
      synchronized(Display.LCDUILock) {
         DateInputField dif = null;

         for(int i = 0; i < this.owner.dateInputFields.size(); ++i) {
            dif = (DateInputField)this.owner.dateInputFields.elementAt(i);
            str.append(dif.toString());
         }

         return str.toString();
      }
   }

   private DateInputField validateDateEntries() {
      DateInputField y = null;
      DateInputField m = null;
      DateInputField d = null;
      DateInputField dif = null;

      int year;
      for(year = 0; year < this.owner.dateInputFields.size(); ++year) {
         dif = (DateInputField)this.owner.dateInputFields.elementAt(year);
         switch(dif.getSubType()) {
         case 3:
            d = dif;
            break;
         case 4:
            m = dif;
            break;
         case 5:
            y = dif;
         }
      }

      if (y != null && m != null && d != null) {
         year = y.getValue();
         int month = m.getValue();
         int day = d.getValue();
         boolean isLeapYear = false;
         if (year <= 9999 && year >= 1) {
            if (month != 0 && month <= 12) {
               if (day != 0 && day <= 31) {
                  if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) {
                     return d;
                  } else {
                     if (year % 4 == 0) {
                        isLeapYear = true;
                        if (year % 100 == 0 && year % 400 != 0) {
                           isLeapYear = false;
                        }
                     }

                     if (month == 2) {
                        if (isLeapYear && day > 29) {
                           return d;
                        }

                        if (!isLeapYear && day > 28) {
                           return d;
                        }
                     }

                     return null;
                  }
               } else {
                  return d;
               }
            } else {
               return m;
            }
         } else {
            return y;
         }
      } else {
         return null;
      }
   }

   private void updateCalendarFromEditor(int fieldType) {
      for(int i = 0; i < this.owner.dateInputFields.size(); ++i) {
         DateInputField dif = (DateInputField)this.owner.dateInputFields.elementAt(i);
         if (fieldType == 3 || dif.getType() == fieldType) {
            int calendarFieldType = -1;
            int offset = 0;
            switch(dif.getSubType()) {
            case 1:
               calendarFieldType = 12;
               break;
            case 2:
               if (this.owner.get24HourClockFlag()) {
                  calendarFieldType = 11;
               } else {
                  calendarFieldType = 10;
                  if (dif.getValue() == 12) {
                     offset = -12;
                  }
               }
               break;
            case 3:
               calendarFieldType = 5;
               break;
            case 4:
               calendarFieldType = 2;
               offset = -1;
               break;
            case 5:
               calendarFieldType = 1;
               break;
            case 6:
               calendarFieldType = 9;
            }

            if (calendarFieldType != -1) {
               this.owner.calendar.set(calendarFieldType, dif.getValue() + offset);
            }
         }
      }

      if (fieldType == 2 || fieldType == 3) {
         this.owner.calendar.set(13, 0);
         this.owner.calendar.set(14, 0);
      }

      this.owner.callChangedItemState();
   }

   private static String extractDateTextLine() {
      String dateFormat = DeviceInfo.getDateFormatString().toLowerCase();
      StringBuffer dateBuffer = new StringBuffer();
      boolean month = false;
      boolean day = false;
      boolean year = false;

      for(int i = 0; i < dateFormat.length(); ++i) {
         switch(dateFormat.charAt(i)) {
         case 'D':
         case 'd':
            if (!day) {
               dateBuffer.append(TextDatabase.getText(46).toLowerCase());
               day = true;
            }
            break;
         case 'M':
         case 'm':
            if (!month) {
               dateBuffer.append(TextDatabase.getText(47).toLowerCase());
               month = true;
            }
            break;
         case 'Y':
         case 'y':
            if (!year) {
               dateBuffer.append(TextDatabase.getText(48).toLowerCase());
               year = true;
            }
            break;
         default:
            dateBuffer.append(dateFormat.charAt(i));
         }
      }

      return dateBuffer.toString();
   }

   private static String extractTimeTextLine() {
      String timeFormat = TextDatabase.getText(16);
      StringBuffer timeBuffer = new StringBuffer();

      for(int i = 0; i < timeFormat.length(); ++i) {
         switch(timeFormat.charAt(i)) {
         case 'H':
         case 'h':
            timeBuffer.append(TextDatabase.getText(45).toLowerCase());
            ++i;
            break;
         case 'M':
         case 'm':
            timeBuffer.append(TextDatabase.getText(44).toLowerCase());
            ++i;
            break;
         default:
            timeBuffer.append(timeFormat.charAt(i));
         }
      }

      return timeBuffer.toString();
   }

   private native int chooseDate(int var1, int var2, int var3);

   static {
      DATEFIELD_DATE_ADVICE_ZONE = Displayable.uistyle.getZone(42);
      DATEFIELD_DATE_EDIT_ZONE = Displayable.uistyle.getZone(43);
      DATEFIELD_TIME_ADVICE_ZONE = Displayable.uistyle.getZone(44);
      DATEFIELD_TIME_EDIT_ZONE = Displayable.uistyle.getZone(45);
      DATE_ADVICE_TEXT = TextBreaker.breakOneLineTextInZone(DATEFIELD_DATE_ADVICE_ZONE, true, true, extractDateTextLine(), 0, false);
      TIME_ADVICE_TEXT = TextBreaker.breakOneLineTextInZone(DATEFIELD_TIME_ADVICE_ZONE, true, true, extractTimeTextLine(), 0, false);
      extraCommands = new Command[]{AM_COMMAND, PM_COMMAND};
      dateCommands = new Command[]{DATE_MENU_VIEW_COMMAND};
      dateLock = new Object();
   }
}
