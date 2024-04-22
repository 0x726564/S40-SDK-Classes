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
   private static UIStyle kE;
   private static TextLine kF;
   private static TextLine kG;
   public int digitCnt;
   public int fieldType;
   public int type;
   public int fieldValue;
   public char[] fieldDigits;
   public int upperLimit;
   public int lowerLimit;
   public char separator;
   private boolean kH;
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
      this.kH = false;
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

            this.fieldDigits = this.a(0, var5, (char[])null, '0');
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
         this.fieldDigits = this.a(var1, 0, this.fieldDigits, '0');
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
      if (this.kH && var1 >= '0' && var1 <= '9' && this.fieldType != 6) {
         int var8;
         if (this.digitCnt == var3 - 1) {
            for(var8 = 0; var8 < var3; ++var8) {
               this.fieldDigits[var8] = '0';
            }
         } else {
            for(var8 = this.digitCnt; var8 < var3 - 1; ++var8) {
               this.fieldDigits[var8] = this.fieldDigits[var8 + 1];
            }
         }

         this.fieldDigits[var3 - 1] = var1;
         char[] var9 = this.fieldDigits;
         int var5 = 0;
         int var6 = 1;
         if (var9 != null) {
            if (this.fieldType == 6) {
               var5 = this.fieldValue;
            } else {
               for(int var7 = var9.length - 1; var7 >= 0; --var7) {
                  var5 += (var9[var7] - 48) * var6;
                  var6 *= 10;
               }
            }
         }

         this.fieldValue = var5;
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

      this.fieldDigits = this.a(this.fieldValue, 0, this.fieldDigits, '0');
   }

   public void incrementValue() {
      if (this.fieldValue < this.upperLimit) {
         ++this.fieldValue;
      } else if (this.cyclic) {
         this.fieldValue = this.lowerLimit;
      }

      this.fieldDigits = this.a(this.fieldValue, 0, this.fieldDigits, '0');
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
      ColorCtrl var6;
      int var7 = (var6 = var3.getColorCtrl()).getFgColor();
      int var8 = var6.getBgColor();
      int var9 = var1 + 1;
      int var10 = (var4 - var5.getDefaultCharHeight()) / 2 + var2 + var5.getBaselinePositionImpl();
      int var11 = 0;
      if (this.initialized && this.fieldType == 6) {
         TextLine var19 = this.fieldValue == 0 ? kF : kG;
         if (this.kH) {
            UIStyle.getUIStyle().drawHighlightBar(var3, var1, var2, var19.getTextLineWidth() + 2, var4, false);
            var6.setBgColor(UIStyle.COLOUR_HIGHLIGHT);
            var6.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
         }

         var3.drawText(var19, var1 + 1, var10 - var19.getTextLineBase(), (short)var19.getTextLineWidth());
         if (this.kH) {
            var6.setBgColor(UIStyle.COLOUR_BACKGROUND);
            var6.setFgColor(UIStyle.COLOUR_TEXT);
         }

         return 0;
      } else {
         if (this.kH && this.digitCnt != this.fieldDigits.length - 1) {
            var11 = this.digitCnt + 1;
         }

         int var13;
         if (this.kH) {
            int var12 = 0;

            for(var13 = var11; var13 < this.fieldDigits.length; ++var13) {
               int var14;
               if (this.initialized) {
                  var12 += var5.getCharWidth(this.fieldDigits[var13]);
                  if ((var14 = var5.getCharASpace(this.fieldDigits[var13]) + var5.getCharCSpace(this.fieldDigits[var13])) > 0) {
                     var12 += var14;
                  }
               } else {
                  var12 += var5.getCharWidth('0');
                  if ((var14 = var5.getCharASpace('0') + var5.getCharCSpace('0')) > 0) {
                     var12 += var14;
                  }
               }
            }

            UIStyle.getUIStyle().drawHighlightBar(var3, var1, var2, var12 + 2, var4, false);
            var6.setBgColor(UIStyle.COLOUR_HIGHLIGHT);
            var6.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
         }

         boolean var20 = var3.getTextTransparency();
         var3.setTextTransparency(true);

         for(var13 = var11; var13 < this.fieldDigits.length; ++var13) {
            if (this.initialized) {
               var9 = var3.drawUnicode(this.fieldDigits[var13], (short)var9, (short)var10);
            } else {
               var9 += var5.getCharWidth('0') + var5.getCharASpace('0') + var5.getCharCSpace('0');
            }
         }

         var9 += 2;
         if (this.kH) {
            var6.setBgColor(var8);
            var6.setFgColor(var7);
         }

         if (this.separator != 0) {
            var9 = var3.drawUnicode(this.separator, (short)var9, (short)var10);
            ++var9;
         }

         var3.setTextTransparency(var20);
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
      Font var5 = var1.getFont();
      int var2 = 0;
      int var3 = 0;
      if (this.kH && this.digitCnt != this.fieldDigits.length - 1) {
         var3 = this.digitCnt + 1;
      }

      for(var3 = var3; var3 < this.fieldDigits.length; ++var3) {
         var2 += var5.getCharWidth(this.fieldDigits[var3]);
         int var4;
         if ((var4 = var5.getCharASpace(this.fieldDigits[var3]) + var5.getCharCSpace(this.fieldDigits[var3])) > 0) {
            var2 += var4;
         }
      }

      if (this.separator != 0) {
         var2 += var5.getCharWidth(this.separator);
         if ((var3 = var5.getCharASpace(this.separator) + var5.getCharCSpace(this.separator)) > 0) {
            var2 += var3;
         }
      }

      return var2 + 1;
   }

   public DateInputField clone() {
      boolean var1 = this.fieldType == 5 && this.fieldDigits.length == 2;
      DateInputField var2;
      (var2 = new DateInputField(this.fieldType, this.lowerLimit, this.upperLimit, var1)).setValue(this.fieldValue);
      var2.setFocus(false);
      var2.setSeparator(this.separator);
      return var2;
   }

   public final String toString() {
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
         this.fieldDigits = this.a(this.fieldValue, 0, this.fieldDigits, '0');
      }

      return var1;
   }

   public boolean hasFocus() {
      return this.kH;
   }

   public void setFocus(boolean var1) {
      this.kH = var1;
      this.digitCnt = this.fieldDigits.length - 1;
   }

   private char[] a(int var1, int var2, char[] var3, char var4) {
      if (this.fieldType == 6) {
         return var1 == 0 ? TextDatabase.getText(39).toCharArray() : TextDatabase.getText(40).toCharArray();
      } else {
         String var7 = Integer.toString(var1);
         char[] var8;
         if (var3 == null) {
            var8 = new char[var2];
            var2 = var2;
         } else {
            var8 = var3;
            var2 = var3.length;
         }

         int var9 = var2 - var7.length();
         int var10 = var7.length() - var2;
         int var5 = var7.length();
         if (var9 < 0) {
            var9 = 0;
         }

         if (var10 < 0) {
            var10 = 0;
         }

         for(byte var6 = 0; var6 < var2; ++var6) {
            var8[var6] = '0';
         }

         var7.getChars(var10, var5, var8, var9);
         return var8;
      }
   }

   static {
      kF = TextBreaker.breakOneLineTextInZone((kE = UIStyle.getUIStyle()).getZone(50), true, true, TextDatabase.getText(39), 0, false);
      kG = TextBreaker.breakOneLineTextInZone(kE.getZone(50), true, true, TextDatabase.getText(40), 0, false);
   }
}
