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
   private static final Zone DATEFIELD_DATE_ADVICE_ZONE;
   private static final Zone DATEFIELD_DATE_EDIT_ZONE;
   private static final Zone DATEFIELD_TIME_ADVICE_ZONE;
   private static final Zone DATEFIELD_TIME_EDIT_ZONE;
   private static final TextLine DATE_ADVICE_TEXT;
   private static final TextLine TIME_ADVICE_TEXT;
   private static final Command[] extraCommands;
   private DateField owner = null;
   private boolean digitsIncompleted = false;

   public void init(DateField var1, String var2) {
      this.owner = var1;
   }

   public void setTitle(String var1) {
   }

   public void setDisplay(Display var1) {
   }

   public Zone getZone(int var1) {
      return var1 == 1 ? DATEFIELD_DATE_EDIT_ZONE : DATEFIELD_TIME_EDIT_ZONE;
   }

   public int getHeight() {
      int var1 = 0;
      int var2 = 0;
      if (this.owner != null && this.owner.dateText != null) {
         ++var2;
      }

      if (this.owner != null && this.owner.timeText != null) {
         ++var2;
      }

      if (var2 == 1) {
         var1 += DATEFIELD_DATE_EDIT_ZONE.height + DATEFIELD_DATE_EDIT_ZONE.y;
      } else if (var2 == 2) {
         var1 += DATEFIELD_TIME_EDIT_ZONE.height + DATEFIELD_TIME_EDIT_ZONE.y;
      }

      return var1;
   }

   public int callPreferredWidth(int var1) {
      return DATEFIELD_DATE_EDIT_ZONE.width;
   }

   public void paintDateField(Graphics var1, int var2, int var3, boolean var4) {
      int var5 = var1.getTranslateX();
      int var6 = var1.getTranslateY();
      int var7 = var5 + DATEFIELD_DATE_EDIT_ZONE.getMarginLeft() + DATEFIELD_DATE_EDIT_ZONE.x;
      int var8 = var7;
      int var11 = UIStyle.usesBackgroundsInsteadOfBorders() ? 99 : DATEFIELD_DATE_EDIT_ZONE.getBorderType();
      int var12 = UIStyle.usesBackgroundsInsteadOfBorders() ? 99 : UIStyle.BORDER_NONE;
      byte var13 = 0;
      if (var4) {
         DateInputField var14 = (DateInputField)this.owner.dateInputFields.elementAt(this.owner.highlight);
         if (var14 != null) {
            if (var14.getType() == 1) {
               var13 = 1;
            } else if (var14.getType() == 2) {
               var13 = 2;
            }
         }
      }

      if (this.owner != null && var6 <= var1.getHeight() && var6 + this.getHeight() >= 0) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics var19 = var1.getImpl();
         int var9 = var6 + DATEFIELD_DATE_EDIT_ZONE.getMarginTop() + DATEFIELD_DATE_EDIT_ZONE.y;
         int var10 = var6 + (this.owner.inputMode == 3 ? DATEFIELD_TIME_EDIT_ZONE.getMarginTop() + DATEFIELD_TIME_EDIT_ZONE.y : DATEFIELD_DATE_EDIT_ZONE.getMarginTop() + DATEFIELD_DATE_EDIT_ZONE.y);
         ColorCtrl var15 = var19.getColorCtrl();
         var15.setBgColor(UIStyle.COLOUR_BACKGROUND);
         var15.setFgColor(UIStyle.COLOUR_TEXT);
         if (this.owner.inputMode == 3) {
            var1.getImpl().drawText(DATE_ADVICE_TEXT, (short)(var5 + DATEFIELD_DATE_ADVICE_ZONE.x + (DATEFIELD_DATE_ADVICE_ZONE.x == DATEFIELD_DATE_EDIT_ZONE.x ? DATEFIELD_DATE_EDIT_ZONE.getMarginLeft() : 0)), (short)(DATEFIELD_DATE_ADVICE_ZONE.y + var6), (short)DATE_ADVICE_TEXT.getTextLineWidth());
            var1.getImpl().drawText(TIME_ADVICE_TEXT, (short)(var5 + DATEFIELD_TIME_ADVICE_ZONE.x + (DATEFIELD_TIME_ADVICE_ZONE.x == DATEFIELD_TIME_EDIT_ZONE.x ? DATEFIELD_TIME_EDIT_ZONE.getMarginLeft() : 0)), (short)(DATEFIELD_TIME_ADVICE_ZONE.y + var6), (short)TIME_ADVICE_TEXT.getTextLineWidth());
            Displayable.uistyle.drawBorder(var1.getImpl(), var5 + DATEFIELD_DATE_EDIT_ZONE.x, var6 + DATEFIELD_DATE_EDIT_ZONE.y, DATEFIELD_DATE_EDIT_ZONE.width, DATEFIELD_DATE_EDIT_ZONE.height, var13 == 1 ? var11 : var12, var13 == 1);
            Displayable.uistyle.drawBorder(var1.getImpl(), var5 + DATEFIELD_TIME_EDIT_ZONE.x, var6 + DATEFIELD_TIME_EDIT_ZONE.y, DATEFIELD_TIME_EDIT_ZONE.width, DATEFIELD_TIME_EDIT_ZONE.height, var13 == 2 ? var11 : var12, var13 == 2);
         } else {
            var1.getImpl().drawText(this.owner.inputMode == 1 ? DATE_ADVICE_TEXT : TIME_ADVICE_TEXT, (short)(var5 + DATEFIELD_DATE_ADVICE_ZONE.x + (DATEFIELD_DATE_ADVICE_ZONE.x == DATEFIELD_DATE_EDIT_ZONE.x ? DATEFIELD_DATE_EDIT_ZONE.getMarginLeft() : 0)), (short)(DATEFIELD_DATE_ADVICE_ZONE.y + var6), this.owner.inputMode == 1 ? (short)DATE_ADVICE_TEXT.getTextLineWidth() : (short)TIME_ADVICE_TEXT.getTextLineWidth());
            Displayable.uistyle.drawBorder(var1.getImpl(), var5 + DATEFIELD_DATE_EDIT_ZONE.x, var6 + DATEFIELD_DATE_EDIT_ZONE.y, DATEFIELD_DATE_EDIT_ZONE.width, DATEFIELD_DATE_EDIT_ZONE.height, var4 ? var11 : var12, var4);
         }

         var19.setFont(DATEFIELD_DATE_EDIT_ZONE.getFont());
         int var16 = DATEFIELD_DATE_EDIT_ZONE.height - DATEFIELD_DATE_EDIT_ZONE.getMarginTop() - DATEFIELD_DATE_EDIT_ZONE.getMarginBottom();

         for(int var17 = 0; var17 < this.owner.dateInputFields.size(); ++var17) {
            DateInputField var18 = (DateInputField)this.owner.dateInputFields.elementAt(var17);
            switch(var18.getSubType()) {
            case 1:
            case 2:
            case 6:
               if (var13 == 2) {
                  var15.setBgColor(UIStyle.COLOUR_HIGHLIGHT);
                  var15.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
               } else {
                  var15.setBgColor(UIStyle.COLOUR_BACKGROUND);
                  var15.setFgColor(UIStyle.COLOUR_EDITOR_UNFOCUSED);
               }

               var8 += var18.render(var8, var10, var19, var16);
               break;
            case 3:
            case 4:
            case 5:
               if (var13 == 1) {
                  var15.setBgColor(UIStyle.COLOUR_HIGHLIGHT);
                  var15.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
               } else {
                  var15.setBgColor(UIStyle.COLOUR_BACKGROUND);
                  var15.setFgColor(UIStyle.COLOUR_EDITOR_UNFOCUSED);
               }

               var7 += var18.render(var7, var9, var19, var16);
            }
         }

         var15.setBgColor(UIStyle.COLOUR_BACKGROUND);
         var15.setFgColor(UIStyle.COLOUR_TEXT);
      }

   }

   public boolean callTraverse(int var1, int var2, int var3, int[] var4) {
      boolean var5 = true;
      boolean var6 = false;
      DateInputField var7 = null;
      int var8 = -1;
      int var14 = this.owner.highlight;
      if (this.owner.highlight >= 0 && this.owner.highlight < this.owner.dateInputFields.size()) {
         var7 = (DateInputField)this.owner.dateInputFields.elementAt(this.owner.highlight);
         var8 = var7.getType();
      }

      int var9 = this.owner.dateInputFields.size();
      int var10 = 0;
      int var11 = 0;

      for(int var12 = 0; var12 < var9; ++var12) {
         var7 = (DateInputField)this.owner.dateInputFields.elementAt(var12);
         if (var1 != 0) {
            var7.setFocus(false);
         }

         switch(var7.getSubType()) {
         case 1:
         case 2:
         case 6:
            ++var11;
            break;
         case 3:
         case 4:
         case 5:
            ++var10;
         }
      }

      boolean var15 = var10 <= 0 || var11 <= 0;
      if (!this.owner.traversedIn) {
         this.owner.traversedIn = true;
         switch(var1) {
         case 0:
         case 5:
         case 6:
            this.owner.highlight = 0;
            break;
         case 1:
         case 2:
            this.owner.highlight = var9 - 1;
         case 3:
         case 4:
         }

         if (this.owner.highlight != -1) {
            var7 = (DateInputField)this.owner.dateInputFields.elementAt(this.owner.highlight);
            var7.setFocus(true);
         }
      } else {
         DateField var10000;
         switch(var1) {
         case 1:
            if (!var15 && this.owner.highlight >= var10) {
               var10000 = this.owner;
               var10000.highlight -= var10;
               break;
            }

            var5 = false;
            break;
         case 2:
            if (this.owner.highlight > 0) {
               --this.owner.highlight;
            } else {
               var5 = false;
            }
         case 3:
         case 4:
         default:
            break;
         case 5:
            if (this.owner.highlight < var9 - 1) {
               ++this.owner.highlight;
            } else {
               var5 = false;
            }
            break;
         case 6:
            if (!var15 && this.owner.highlight < var10) {
               var10000 = this.owner;
               var10000.highlight += var10;
               if (this.owner.highlight >= var9) {
                  --this.owner.highlight;
               }
            } else {
               var5 = false;
            }
         }

         if (var1 != 0 && this.digitsIncompleted) {
            for(var7 = this.validateDateEntries(); var7 != null; var7 = this.validateDateEntries()) {
               var7.incrementValue();
            }

            this.updateCalendarFromEditor(3);
            this.digitsIncompleted = false;
         }
      }

      if (var1 != 0) {
         var7 = (DateInputField)this.owner.dateInputFields.elementAt(this.owner.highlight);
         var7.setFocus(true);
         int var13 = var7.getType();
         if (var14 != this.owner.highlight && var13 != var8) {
            this.owner.owner.updateSoftkeys(true);
         }
      }

      if (var5) {
         var4[1] = this.owner.getLabelHeight(var4[1]) + UIStyle.LABEL_PAD;
         if (this.owner.inputMode == 3 && this.owner.highlight >= var10) {
            var4[1] += DATEFIELD_TIME_ADVICE_ZONE.y;
         }

         var4[3] = DATEFIELD_DATE_EDIT_ZONE.y + DATEFIELD_DATE_EDIT_ZONE.height;
         this.owner.repaint();
      }

      return var5;
   }

   public boolean processKey(int var1) {
      String var2 = this.toString();
      boolean var3 = false;
      DateInputField var4;
      int var9;
      switch(var1) {
      case -10:
         return false;
      case 32:
      case 35:
      case 42:
      default:
         var4 = (DateInputField)this.owner.dateInputFields.elementAt(this.owner.highlight);
         var9 = var4.getType();
         if (var4.getSubType() == 6) {
            if (var4.isInitialized()) {
               var4.setValue(var4.getValue() ^ 1);
            } else {
               this.initializeDateInputFields(2, true);
               var4.setFocus(true);
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
         var4 = (DateInputField)this.owner.dateInputFields.elementAt(this.owner.highlight);
         if (!var4.isInitialized()) {
            this.initializeDateInputFields(var4.getType(), true);
         }

         var9 = var4.getType();
         if (var4.getSubType() != 6) {
            int var5 = var4.addDigit((char)var1);
            this.digitsIncompleted = false;
            int var8;
            label61:
            switch(var5) {
            case -1:
               var4.setFocus(true);
               this.owner.highlight = this.owner.dateInputFields.indexOf(var4);
               var4 = this.validateDateEntries();

               while(true) {
                  if (var4 == null) {
                     break label61;
                  }

                  var8 = var4.getValue();
                  --var8;
                  var4.setValue(var8);
                  var4 = this.validateDateEntries();
               }
            case 0:
               var9 = 0;
               this.digitsIncompleted = true;
               break;
            case 1:
               var4.setFocus(false);
               int var6 = var4.getType();
               DateInputField var7 = this.validateDateEntries();
               if (var7 == null) {
                  for(var8 = 0; var8 < this.owner.dateInputFields.size(); ++var8) {
                     this.owner.highlight = ++this.owner.highlight % this.owner.dateInputFields.size();
                     var4 = (DateInputField)this.owner.dateInputFields.elementAt(this.owner.highlight);
                     if (var6 == var4.getType()) {
                        break;
                     }
                  }

                  var4.setFocus(true);
               } else {
                  var7.setFocus(true);

                  for(this.owner.highlight = this.owner.dateInputFields.indexOf(var7); var7 != null; var7 = this.validateDateEntries()) {
                     var8 = var7.getValue();
                     --var8;
                     var7.setValue(var8);
                  }
               }
            }
         } else {
            var4.setValue(var4.getValue() ^ 1);
            var4.setFocus(true);
         }
      }

      String var10 = this.toString();
      if (!var10.equals(var2)) {
         this.owner.callChangedItemState();
      }

      if (var9 != 0) {
         this.updateCalendarFromEditor(var9);
      }

      this.owner.repaint();
      return true;
   }

   public Command[] getExtraCommands() {
      int var1 = this.owner.highlight;
      Vector var2 = this.owner.dateInputFields;
      if (var1 >= 0 && var1 < var2.size()) {
         DateInputField var3 = (DateInputField)var2.elementAt(var1);
         int var4 = var3.getType();
         if (var4 == 2 && !this.owner.get24HourClockFlag()) {
            return extraCommands;
         }
      }

      return null;
   }

   public boolean launchExtraCommand(Command var1) {
      if (var1 == AM_COMMAND || var1 == PM_COMMAND) {
         DateInputField var2 = null;

         for(int var3 = 0; var3 < this.owner.dateInputFields.size(); ++var3) {
            var2 = (DateInputField)this.owner.dateInputFields.elementAt(var3);
            if (var2.getSubType() == 6) {
               var2.setValue(var1 == AM_COMMAND ? 0 : 1);
               if (!var2.isInitialized()) {
                  this.initializeDateInputFields(2, true);
               }

               this.owner.repaint();
            }
         }
      }

      return false;
   }

   public void initializeDateInputFields(int var1, boolean var2) {
      DateInputField var3 = null;

      for(int var4 = 0; var4 < this.owner.dateInputFields.size(); ++var4) {
         var3 = (DateInputField)this.owner.dateInputFields.elementAt(var4);
         if (var1 == 3 || var3.getType() == var1) {
            var3.initialise(var2);
         }
      }

      switch(var1) {
      case 1:
         this.owner.dateInitialized = var2;
         break;
      case 2:
         this.owner.timeInitialized = var2;
         break;
      case 3:
         this.owner.dateInitialized = var2;
         this.owner.timeInitialized = var2;
      }

   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      synchronized(Display.LCDUILock) {
         DateInputField var3 = null;

         for(int var4 = 0; var4 < this.owner.dateInputFields.size(); ++var4) {
            var3 = (DateInputField)this.owner.dateInputFields.elementAt(var4);
            var1.append(var3.toString());
         }

         return var1.toString();
      }
   }

   private DateInputField validateDateEntries() {
      DateInputField var1 = null;
      DateInputField var2 = null;
      DateInputField var3 = null;
      DateInputField var4 = null;

      int var5;
      for(var5 = 0; var5 < this.owner.dateInputFields.size(); ++var5) {
         var4 = (DateInputField)this.owner.dateInputFields.elementAt(var5);
         switch(var4.getSubType()) {
         case 3:
            var3 = var4;
            break;
         case 4:
            var2 = var4;
            break;
         case 5:
            var1 = var4;
         }
      }

      if (var1 != null && var2 != null && var3 != null) {
         var5 = var1.getValue();
         int var6 = var2.getValue();
         int var7 = var3.getValue();
         boolean var8 = false;
         if (var5 <= 9999 && var5 >= 1) {
            if (var6 != 0 && var6 <= 12) {
               if (var7 != 0 && var7 <= 31) {
                  if ((var6 == 4 || var6 == 6 || var6 == 9 || var6 == 11) && var7 > 30) {
                     return var3;
                  } else {
                     if (var5 % 4 == 0) {
                        var8 = true;
                        if (var5 % 100 == 0 && var5 % 400 != 0) {
                           var8 = false;
                        }
                     }

                     if (var6 == 2) {
                        if (var8 && var7 > 29) {
                           return var3;
                        }

                        if (!var8 && var7 > 28) {
                           return var3;
                        }
                     }

                     return null;
                  }
               } else {
                  return var3;
               }
            } else {
               return var2;
            }
         } else {
            return var1;
         }
      } else {
         return null;
      }
   }

   private void updateCalendarFromEditor(int var1) {
      for(int var3 = 0; var3 < this.owner.dateInputFields.size(); ++var3) {
         DateInputField var2 = (DateInputField)this.owner.dateInputFields.elementAt(var3);
         if (var1 == 3 || var2.getType() == var1) {
            byte var4 = -1;
            byte var5 = 0;
            switch(var2.getSubType()) {
            case 1:
               var4 = 12;
               break;
            case 2:
               if (this.owner.get24HourClockFlag()) {
                  var4 = 11;
               } else {
                  var4 = 10;
                  if (var2.getValue() == 12) {
                     var5 = -12;
                  }
               }
               break;
            case 3:
               var4 = 5;
               break;
            case 4:
               var4 = 2;
               var5 = -1;
               break;
            case 5:
               var4 = 1;
               break;
            case 6:
               var4 = 9;
            }

            if (var4 != -1) {
               this.owner.calendar.set(var4, var2.getValue() + var5);
            }
         }
      }

      if (var1 == 2 || var1 == 3) {
         this.owner.calendar.set(13, 0);
         this.owner.calendar.set(14, 0);
      }

   }

   private static String extractDateTextLine() {
      String var0 = DeviceInfo.getDateFormatString().toLowerCase();
      StringBuffer var1 = new StringBuffer();
      boolean var2 = false;
      boolean var3 = false;
      boolean var4 = false;

      for(int var5 = 0; var5 < var0.length(); ++var5) {
         switch(var0.charAt(var5)) {
         case 'D':
         case 'd':
            if (!var3) {
               var1.append(TextDatabase.getText(46).toLowerCase());
               var3 = true;
            }
            break;
         case 'M':
         case 'm':
            if (!var2) {
               var1.append(TextDatabase.getText(47).toLowerCase());
               var2 = true;
            }
            break;
         case 'Y':
         case 'y':
            if (!var4) {
               var1.append(TextDatabase.getText(48).toLowerCase());
               var4 = true;
            }
            break;
         default:
            var1.append(var0.charAt(var5));
         }
      }

      return var1.toString();
   }

   private static String extractTimeTextLine() {
      String var0 = TextDatabase.getText(16);
      StringBuffer var1 = new StringBuffer();

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         switch(var0.charAt(var2)) {
         case 'H':
         case 'h':
            var1.append(TextDatabase.getText(45).toLowerCase());
            ++var2;
            break;
         case 'M':
         case 'm':
            var1.append(TextDatabase.getText(44).toLowerCase());
            ++var2;
            break;
         default:
            var1.append(var0.charAt(var2));
         }
      }

      return var1.toString();
   }

   static {
      DATEFIELD_DATE_ADVICE_ZONE = Displayable.uistyle.getZone(46);
      DATEFIELD_DATE_EDIT_ZONE = Displayable.uistyle.getZone(47);
      DATEFIELD_TIME_ADVICE_ZONE = Displayable.uistyle.getZone(48);
      DATEFIELD_TIME_EDIT_ZONE = Displayable.uistyle.getZone(49);
      DATE_ADVICE_TEXT = TextBreaker.breakOneLineTextInZone(DATEFIELD_DATE_ADVICE_ZONE, true, true, extractDateTextLine(), 0, false);
      TIME_ADVICE_TEXT = TextBreaker.breakOneLineTextInZone(DATEFIELD_TIME_ADVICE_ZONE, true, true, extractTimeTextLine(), 0, false);
      extraCommands = new Command[]{AM_COMMAND, PM_COMMAND};
   }
}
