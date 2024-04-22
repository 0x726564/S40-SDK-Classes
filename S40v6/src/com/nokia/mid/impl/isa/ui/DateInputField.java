package com.nokia.mid.impl.isa.ui;

import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Font;
import com.nokia.mid.impl.isa.ui.gdi.Graphics;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.util.DigitsConverter;

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
   public static final int HIGLIGHT_PADDING = 1;
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

   public DateInputField(int fieldType, int lowerLimit, int upperLimit, boolean formatShort, boolean cyclic) {
      this(fieldType, lowerLimit, upperLimit, formatShort);
      this.cyclic = cyclic;
   }

   public DateInputField(int fieldType, int lowerLimit, int upperLimit, boolean formatShort) {
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
      int numOfDigits = 2;
      if (fieldType != 1 && fieldType != 2 && fieldType != 3 && fieldType != 4 && fieldType != 5 && fieldType != 6) {
         throw new IllegalArgumentException("Field: invalid field type");
      } else {
         this.fieldType = fieldType;
         if (fieldType != 1 && fieldType != 2 && fieldType != 6) {
            if (fieldType == 3 || fieldType == 4 || fieldType == 5) {
               this.type = 1;
            }
         } else {
            this.type = 2;
         }

         if (lowerLimit >= 0 && upperLimit >= 0 && upperLimit >= lowerLimit) {
            this.upperLimit = upperLimit;
            this.lowerLimit = lowerLimit;
            if (fieldType == 5) {
               if (formatShort) {
                  this.upperLimit = 99;
               } else {
                  numOfDigits = 4;
               }
            }

            this.fieldDigits = this.createDigits(0, numOfDigits, (char[])null, '0');
            this.digitCnt = numOfDigits - 1;
         } else {
            throw new IllegalArgumentException("Field: invalid field limits");
         }
      }
   }

   public void setValue(int value) {
      if (value < 0) {
         throw new IllegalArgumentException("Field: invalid field value");
      } else {
         this.fieldValue = value;
         this.fieldDigits = this.createDigits(value, 0, this.fieldDigits, '0');
         this.checkDigits();
      }
   }

   public void setSeparator(char separator) {
      this.separator = separator;
   }

   public void setCyclic(boolean cyclic) {
      this.cyclic = cyclic;
   }

   public int addDigit(char digit) {
      int rValue = 0;
      int len = this.fieldDigits.length;
      int cnt = false;
      if (this.focused && digit >= '0' && digit <= '9' && this.fieldType != 6) {
         int cnt;
         if (this.digitCnt == len - 1) {
            for(cnt = 0; cnt < len; ++cnt) {
               this.fieldDigits[cnt] = '0';
            }
         } else {
            for(cnt = this.digitCnt; cnt < len - 1; ++cnt) {
               this.fieldDigits[cnt] = this.fieldDigits[cnt + 1];
            }
         }

         this.fieldDigits[len - 1] = digit;
         this.fieldValue = this.getValue(this.fieldDigits);
         if (this.digitCnt == 0) {
            if (!this.checkDigits()) {
               rValue = -1;
            } else {
               rValue = 1;
            }

            this.digitCnt = len - 1;
         } else {
            --this.digitCnt;
         }
      }

      return rValue;
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
      char[] digitsWithSeparator = this.fieldDigits;
      if (this.separator != 0) {
         digitsWithSeparator = new char[this.fieldDigits.length + 1];
         System.arraycopy(this.fieldDigits, 0, digitsWithSeparator, 0, this.fieldDigits.length);
         digitsWithSeparator[digitsWithSeparator.length - 1] = this.separator;
      }

      return digitsWithSeparator;
   }

   public int getValue() {
      return this.fieldValue;
   }

   public boolean isCyclic() {
      return this.cyclic;
   }

   public int render(int x, int y, Graphics ng, int heightOfArea) {
      Font nf = ng.getFont();
      ColorCtrl cc = ng.getColorCtrl();
      int originalFgColor = cc.getFgColor();
      int originalBgColor = cc.getBgColor();
      int left = x + 1;
      int baseline = (heightOfArea - nf.getDefaultCharHeight()) / 2 + y + nf.getBaselinePositionImpl();
      int cnt = 0;
      if (this.initialized && this.fieldType == 6) {
         return this.renderAM_PM(x, y, baseline, ng, cc, heightOfArea);
      } else {
         if (this.focused && this.digitCnt != this.fieldDigits.length - 1) {
            cnt = this.digitCnt + 1;
         }

         char[] renderDigitArray = this.fieldDigits;
         char[] nativefieldDigits = DigitsConverter.getDigitsInMenuLanguage(this.fieldDigits);
         if (nativefieldDigits != null) {
            renderDigitArray = nativefieldDigits;
         }

         int i;
         if (this.focused) {
            int highlight_width = 0;

            for(i = cnt; i < renderDigitArray.length; ++i) {
               int spaceWidth;
               if (this.initialized) {
                  highlight_width += nf.getCharWidth(renderDigitArray[i]);
                  spaceWidth = nf.getCharASpace(renderDigitArray[i]) + nf.getCharCSpace(renderDigitArray[i]);
                  if (spaceWidth > 0) {
                     highlight_width += spaceWidth;
                  }
               } else {
                  highlight_width += nf.getCharWidth('0');
                  spaceWidth = nf.getCharASpace('0') + nf.getCharCSpace('0');
                  if (spaceWidth > 0) {
                     highlight_width += spaceWidth;
                  }
               }
            }

            UIStyle.getUIStyle().drawHighlightBar(ng, x, y, highlight_width + 2, heightOfArea, false);
            cc.setBgColor(UIStyle.COLOUR_HIGHLIGHT);
            cc.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
         }

         boolean orginalTextTransparency = ng.getTextTransparency();
         ng.setTextTransparency(true);

         for(i = cnt; i < renderDigitArray.length; ++i) {
            if (this.initialized) {
               left = ng.drawUnicode(renderDigitArray[i], (short)left, (short)baseline);
            } else {
               left += nf.getCharWidth('0') + nf.getCharASpace('0') + nf.getCharCSpace('0');
            }
         }

         left += 2;
         if (this.focused) {
            cc.setBgColor(originalBgColor);
            cc.setFgColor(originalFgColor);
         }

         if (this.separator != 0) {
            left = ng.drawUnicode(this.separator, (short)left, (short)baseline);
            ++left;
         }

         ng.setTextTransparency(orginalTextTransparency);
         return left - x;
      }
   }

   public void initialise(boolean b) {
      this.initialized = b;
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public int getRenderWidth(Graphics gfx) {
      Font font = gfx.getFont();
      int width = 0;
      int cnt = 0;
      if (this.focused && this.digitCnt != this.fieldDigits.length - 1) {
         cnt = this.digitCnt + 1;
      }

      int spaceWidth;
      for(spaceWidth = cnt; spaceWidth < this.fieldDigits.length; ++spaceWidth) {
         width += font.getCharWidth(this.fieldDigits[spaceWidth]);
         int spaceWidth = font.getCharASpace(this.fieldDigits[spaceWidth]) + font.getCharCSpace(this.fieldDigits[spaceWidth]);
         if (spaceWidth > 0) {
            width += spaceWidth;
         }
      }

      if (this.separator != 0) {
         width += font.getCharWidth(this.separator);
         spaceWidth = font.getCharASpace(this.separator) + font.getCharCSpace(this.separator);
         if (spaceWidth > 0) {
            width += spaceWidth;
         }
      }

      return width + 1;
   }

   public DateInputField clone() {
      boolean formatShort = this.fieldType == 5 && this.fieldDigits.length == 2;
      DateInputField clone = new DateInputField(this.fieldType, this.lowerLimit, this.upperLimit, formatShort);
      clone.setValue(this.fieldValue);
      clone.setFocus(false);
      clone.setSeparator(this.separator);
      return clone;
   }

   public String toString() {
      return new String(this.getDigits());
   }

   public boolean checkDigits() {
      boolean rValue = true;
      if (this.fieldValue > this.upperLimit) {
         this.fieldValue = this.upperLimit;
         rValue = false;
      }

      if (this.fieldValue < this.lowerLimit) {
         this.fieldValue = this.lowerLimit;
         rValue = false;
      }

      if (!rValue) {
         this.fieldDigits = this.createDigits(this.fieldValue, 0, this.fieldDigits, '0');
      }

      return rValue;
   }

   public boolean hasFocus() {
      return this.focused;
   }

   public void setFocus(boolean focused) {
      this.focused = focused;
      this.digitCnt = this.fieldDigits.length - 1;
   }

   private int getValue(char[] digits) {
      int value = 0;
      int lValue = 1;
      if (digits != null) {
         if (this.fieldType == 6) {
            value = this.fieldValue;
         } else {
            for(int cnt = digits.length - 1; cnt >= 0; --cnt) {
               value += (digits[cnt] - 48) * lValue;
               lValue *= 10;
            }
         }
      }

      return value;
   }

   private char[] createDigits(int number, int numOfDigits, char[] dest, char initChar) {
      if (this.fieldType == 6) {
         return number == 0 ? TextDatabase.getText(39).toCharArray() : TextDatabase.getText(40).toCharArray();
      } else {
         String s = Integer.toString(number);
         char[] digits;
         int nDigits;
         if (dest == null) {
            digits = new char[numOfDigits];
            nDigits = numOfDigits;
         } else {
            digits = dest;
            nDigits = dest.length;
         }

         int dstBegin = nDigits - s.length();
         int srcBegin = s.length() - nDigits;
         int srcEnd = s.length();
         if (dstBegin < 0) {
            dstBegin = 0;
         }

         if (srcBegin < 0) {
            srcBegin = 0;
         }

         for(byte i = 0; i < nDigits; ++i) {
            digits[i] = initChar;
         }

         s.getChars(srcBegin, srcEnd, digits, dstBegin);
         return digits;
      }
   }

   private int renderAM_PM(int x, int y, int baseline, Graphics ng, ColorCtrl cc, int heightOfArea) {
      TextLine textLine;
      if (this.fieldValue == 0) {
         textLine = TextBreaker.breakOneLineTextInZone(uistyle.getZone(45), true, true, TextDatabase.getText(39), 0, false);
      } else {
         textLine = TextBreaker.breakOneLineTextInZone(uistyle.getZone(45), true, true, TextDatabase.getText(40), 0, false);
      }

      if (this.focused) {
         UIStyle.getUIStyle().drawHighlightBar(ng, x, y, textLine.getTextLineWidth() + 2, heightOfArea, false);
         cc.setBgColor(UIStyle.COLOUR_HIGHLIGHT);
         cc.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
      }

      ng.drawText(textLine, x + 1, baseline - textLine.getTextLineBase(), (short)textLine.getTextLineWidth());
      if (this.focused) {
         cc.setBgColor(UIStyle.COLOUR_BACKGROUND);
         cc.setFgColor(UIStyle.COLOUR_TEXT);
      }

      return textLine.getTextLineWidth() + 2;
   }
}
