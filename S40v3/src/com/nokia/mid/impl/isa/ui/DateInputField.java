package com.nokia.mid.impl.isa.ui;

import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Font;
import com.nokia.mid.impl.isa.ui.gdi.Graphics;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;

public class DateInputField {
   public static final int NO_FIELD = 0;
   public static final int MINUTE_FIELD = 1;
   public static final int HOUR_FIELD = 2;
   public static final int DAY_FIELD = 3;
   public static final int MONTH_FIELD = 4;
   public static final int YEAR_FIELD = 5;
   public static final int AM_PM = 6;
   public static final int DATE_FIELD = 1;
   public static final int TIME_FIELD = 2;
   private static UIStyle uistyle = UIStyle.getUIStyle();
   private static final int HIGLIGHT_PADDING = 1;
   private static TextLine PM_TEXT;
   private static TextLine AM_TEXT;
   public int digitCnt;
   public int fieldType;
   public int type;
   public int fieldValue;
   public char[] fieldDigits;
   public int upperLimit;
   public int lowerLimit;
   public char separator;
   private boolean focused;
   private boolean cyclic;
   private boolean initialized;

   public DateInputField(int var1, int var2, int var3, boolean var4, boolean var5) {
      this(var1, var2, var3, var4);
      this.cyclic = var5;
   }

   public DateInputField(int var1, int var2, int var3, boolean var4) {
      this.digitCnt = 0;
      this.fieldType = 0;
      this.type = 0;
      this.fieldValue = 0;
      this.fieldDigits = null;
      this.upperLimit = 0;
      this.lowerLimit = 0;
      this.separator = 0;
      this.focused = false;
      this.cyclic = true;
      this.initialized = false;
      byte var5 = 2;
      if (var1 != 1 && var1 != 2 && var1 != 3 && var1 != 4 && var1 != 5 && var1 != 6) {
         throw new IllegalArgumentException("Field: invalid field type");
      } else {
         this.fieldType = var1;
         if (var1 != 1 && var1 != 2 && var1 != 6) {
            if (var1 == 3 || var1 == 4 || var1 == 5) {
               this.type = 1;
            }
         } else {
            this.type = 2;
         }

         if (var2 >= 0 && var3 >= 0 && var3 >= var2) {
            this.upperLimit = var3;
            this.lowerLimit = var2;
            if (var1 == 5) {
               if (var4) {
                  this.upperLimit = 99;
               } else {
                  var5 = 4;
               }
            }

            this.fieldDigits = this.createDigits(0, var5, (char[])null, '0');
            this.digitCnt = var5 - 1;
         } else {
            throw new IllegalArgumentException("Field: invalid field limits");
         }
      }
   }

   public void setValue(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Field: invalid field value");
      } else {
         this.fieldValue = var1;
         this.fieldDigits = this.createDigits(var1, 0, this.fieldDigits, '0');
         this.checkDigits();
      }
   }

   public void setSeparator(char var1) {
      this.separator = var1;
   }

   public void setCyclic(boolean var1) {
      this.cyclic = var1;
   }

   public int addDigit(char var1) {
      byte var2 = 0;
      int var3 = this.fieldDigits.length;
      boolean var4 = false;
      if (this.focused && var1 >= '0' && var1 <= '9' && this.fieldType != 6) {
         int var5;
         if (this.digitCnt == var3 - 1) {
            for(var5 = 0; var5 < var3; ++var5) {
               this.fieldDigits[var5] = '0';
            }
         } else {
            for(var5 = this.digitCnt; var5 < var3 - 1; ++var5) {
               this.fieldDigits[var5] = this.fieldDigits[var5 + 1];
            }
         }

         this.fieldDigits[var3 - 1] = var1;
         this.fieldValue = this.getValue(this.fieldDigits);
         if (this.digitCnt == 0) {
            if (!this.checkDigits()) {
               var2 = -1;
            } else {
               var2 = 1;
            }

            this.digitCnt = var3 - 1;
         } else {
            --this.digitCnt;
         }
      }

      return var2;
   }

   public void decrementValue() {
      if (this.fieldValue > this.lowerLimit) {
         --this.fieldValue;
      } else if (this.cyclic) {
         this.fieldValue = this.upperLimit;
      }

      this.fieldDigits = this.createDigits(this.fieldValue, 0, this.fieldDigits, '0');
   }

   public void incrementValue() {
      if (this.fieldValue < this.upperLimit) {
         ++this.fieldValue;
      } else if (this.cyclic) {
         this.fieldValue = this.lowerLimit;
      }

      this.fieldDigits = this.createDigits(this.fieldValue, 0, this.fieldDigits, '0');
   }

   public int getType() {
      return this.type;
   }

   public int getSubType() {
      return this.fieldType;
   }

   public char[] getDigits() {
      char[] var1 = this.fieldDigits;
      if (this.separator != 0) {
         var1 = new char[this.fieldDigits.length + 1];
         System.arraycopy(this.fieldDigits, 0, var1, 0, this.fieldDigits.length);
         var1[var1.length - 1] = this.separator;
      }

      return var1;
   }

   public int getValue() {
      return this.fieldValue;
   }

   public boolean isCyclic() {
      return this.cyclic;
   }

   public int render(int var1, int var2, Graphics var3, int var4) {
      Font var5 = var3.getFont();
      ColorCtrl var6 = var3.getColorCtrl();
      int var7 = var6.getFgColor();
      int var8 = var6.getBgColor();
      int var9 = var1 + 1;
      int var10 = (var4 - var5.getDefaultCharHeight()) / 2 + var2 + var5.getBaselinePositionImpl();
      int var11 = 0;
      if (this.initialized && this.fieldType == 6) {
         this.renderAM_PM(var1, var2, var10, var3, var6, var4);
         return 0;
      } else {
         if (this.focused && this.digitCnt != this.fieldDigits.length - 1) {
            var11 = this.digitCnt + 1;
         }

         int var13;
         if (this.focused) {
            int var12 = 0;

            for(var13 = var11; var13 < this.fieldDigits.length; ++var13) {
               int var14;
               if (this.initialized) {
                  var12 += var5.getCharWidth(this.fieldDigits[var13]);
                  var14 = var5.getCharASpace(this.fieldDigits[var13]) + var5.getCharCSpace(this.fieldDigits[var13]);
                  if (var14 > 0) {
                     var12 += var14;
                  }
               } else {
                  var12 += var5.getCharWidth('0');
                  var14 = var5.getCharASpace('0') + var5.getCharCSpace('0');
                  if (var14 > 0) {
                     var12 += var14;
                  }
               }
            }

            UIStyle.getUIStyle().drawHighlightBar(var3, var1, var2, var12 + 2, var4, false);
            var6.setBgColor(UIStyle.COLOUR_HIGHLIGHT);
            var6.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
         }

         boolean var15 = var3.getTextTransparency();
         var3.setTextTransparency(true);

         for(var13 = var11; var13 < this.fieldDigits.length; ++var13) {
            if (this.initialized) {
               var9 = var3.drawUnicode(this.fieldDigits[var13], (short)var9, (short)var10);
            } else {
               var9 += var5.getCharWidth('0') + var5.getCharASpace('0') + var5.getCharCSpace('0');
            }
         }

         var9 += 2;
         if (this.focused) {
            var6.setBgColor(var8);
            var6.setFgColor(var7);
         }

         if (this.separator != 0) {
            var9 = var3.drawUnicode(this.separator, (short)var9, (short)var10);
            ++var9;
         }

         var3.setTextTransparency(var15);
         return var9 - var1;
      }
   }

   public void initialise(boolean var1) {
      this.initialized = var1;
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public int getRenderWidth(Graphics var1) {
      Font var2 = var1.getFont();
      int var3 = 0;
      int var4 = 0;
      if (this.focused && this.digitCnt != this.fieldDigits.length - 1) {
         var4 = this.digitCnt + 1;
      }

      int var5;
      for(var5 = var4; var5 < this.fieldDigits.length; ++var5) {
         var3 += var2.getCharWidth(this.fieldDigits[var5]);
         int var6 = var2.getCharASpace(this.fieldDigits[var5]) + var2.getCharCSpace(this.fieldDigits[var5]);
         if (var6 > 0) {
            var3 += var6;
         }
      }

      if (this.separator != 0) {
         var3 += var2.getCharWidth(this.separator);
         var5 = var2.getCharASpace(this.separator) + var2.getCharCSpace(this.separator);
         if (var5 > 0) {
            var3 += var5;
         }
      }

      return var3 + 1;
   }

   public DateInputField clone() {
      boolean var1 = this.fieldType == 5 && this.fieldDigits.length == 2;
      DateInputField var2 = new DateInputField(this.fieldType, this.lowerLimit, this.upperLimit, var1);
      var2.setValue(this.fieldValue);
      var2.setFocus(false);
      var2.setSeparator(this.separator);
      return var2;
   }

   public String toString() {
      return new String(this.getDigits());
   }

   public boolean checkDigits() {
      boolean var1 = true;
      if (this.fieldValue > this.upperLimit) {
         this.fieldValue = this.upperLimit;
         var1 = false;
      }

      if (this.fieldValue < this.lowerLimit) {
         this.fieldValue = this.lowerLimit;
         var1 = false;
      }

      if (!var1) {
         this.fieldDigits = this.createDigits(this.fieldValue, 0, this.fieldDigits, '0');
      }

      return var1;
   }

   public boolean hasFocus() {
      return this.focused;
   }

   public void setFocus(boolean var1) {
      this.focused = var1;
      this.digitCnt = this.fieldDigits.length - 1;
   }

   private int getValue(char[] var1) {
      int var2 = 0;
      int var3 = 1;
      if (var1 != null) {
         if (this.fieldType == 6) {
            var2 = this.fieldValue;
         } else {
            for(int var4 = var1.length - 1; var4 >= 0; --var4) {
               var2 += (var1[var4] - 48) * var3;
               var3 *= 10;
            }
         }
      }

      return var2;
   }

   private char[] createDigits(int var1, int var2, char[] var3, char var4) {
      if (this.fieldType == 6) {
         return var1 == 0 ? TextDatabase.getText(39).toCharArray() : TextDatabase.getText(40).toCharArray();
      } else {
         String var5 = Integer.toString(var1);
         char[] var6;
         int var7;
         if (var3 == null) {
            var6 = new char[var2];
            var7 = var2;
         } else {
            var6 = var3;
            var7 = var3.length;
         }

         int var9 = var7 - var5.length();
         int var10 = var5.length() - var7;
         int var11 = var5.length();
         if (var9 < 0) {
            var9 = 0;
         }

         if (var10 < 0) {
            var10 = 0;
         }

         for(byte var12 = 0; var12 < var7; ++var12) {
            var6[var12] = var4;
         }

         var5.getChars(var10, var11, var6, var9);
         return var6;
      }
   }

   private void renderAM_PM(int var1, int var2, int var3, Graphics var4, ColorCtrl var5, int var6) {
      TextLine var7 = this.fieldValue == 0 ? PM_TEXT : AM_TEXT;
      if (this.focused) {
         Font var8 = var4.getFont();
         UIStyle.getUIStyle().drawHighlightBar(var4, var1, var2, var7.getTextLineWidth() + 2, var6, false);
         var5.setBgColor(UIStyle.COLOUR_HIGHLIGHT);
         var5.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
      }

      var4.drawText(var7, var1 + 1, var3 - var7.getTextLineBase(), (short)var7.getTextLineWidth());
      if (this.focused) {
         var5.setBgColor(UIStyle.COLOUR_BACKGROUND);
         var5.setFgColor(UIStyle.COLOUR_TEXT);
      }

   }

   static {
      PM_TEXT = TextBreaker.breakOneLineTextInZone(uistyle.getZone(49), true, true, TextDatabase.getText(39), 0, false);
      AM_TEXT = TextBreaker.breakOneLineTextInZone(uistyle.getZone(49), true, true, TextDatabase.getText(40), 0, false);
   }
}
