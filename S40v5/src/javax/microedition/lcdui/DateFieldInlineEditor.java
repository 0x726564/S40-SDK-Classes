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
   private static final Command iB = new Command(12, 39);
   private static final Command iC = new Command(12, 40);
   private static final Command iD = new Command(12, 78);
   private static final Zone iE;
   private static final Zone iF;
   private static final Zone iG;
   private static final Zone iH;
   private static final TextLine iI;
   private static final TextLine iJ;
   private static final Command[] bd;
   private DateField iK = null;
   private boolean iL = false;

   public final void a(DateField var1) {
      this.iK = var1;
   }

   public void setTitle(String var1) {
   }

   public void setDisplay(Display var1) {
   }

   public Zone getZone(int var1) {
      return var1 == 1 ? iF : iH;
   }

   public int getHeight() {
      int var1 = 0;
      int var2 = 0;
      if (this.iK != null && this.iK.m != null) {
         ++var2;
      }

      if (this.iK != null && this.iK.n != null) {
         ++var2;
      }

      if (var2 == 1) {
         var1 = 0 + iF.height + iF.y;
      } else if (var2 == 2) {
         var1 = 0 + iH.height + iH.y;
      }

      return var1;
   }

   public final int T() {
      return iF.width;
   }

   public final void a(Graphics var1, boolean var2) {
      int var3 = var1.getTranslateX();
      int var4 = var1.getTranslateY();
      int var5;
      int var6 = var5 = var3 + iF.getMarginLeft() + iF.x;
      int var9 = iF.getBorderType();
      byte var10 = 0;
      DateInputField var11;
      if (var2 && (var11 = (DateInputField)this.iK.i.elementAt(this.iK.o)) != null) {
         if (var11.getType() == 1) {
            var10 = 1;
         } else if (var11.getType() == 2) {
            var10 = 2;
         }
      }

      if (this.iK != null && var4 <= var1.getHeight() && var4 + this.getHeight() >= 0) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics var16 = var1.getImpl();
         int var7 = var4 + iF.getMarginTop() + iF.y;
         int var8 = var4 + (this.iK.j == 3 ? iH.getMarginTop() + iH.y : iF.getMarginTop() + iF.y);
         ColorCtrl var12;
         (var12 = var16.getColorCtrl()).setBgColor(UIStyle.COLOUR_BACKGROUND);
         var12.setFgColor(UIStyle.COLOUR_TEXT);
         if (this.iK.j == 3) {
            var1.getImpl().drawText(iI, (short)(var3 + iE.x + (iE.x == iF.x ? iF.getMarginLeft() : 0)), (short)(iE.y + var4), (short)iI.getTextLineWidth());
            var1.getImpl().drawText(iJ, (short)(var3 + iG.x + (iG.x == iH.x ? iH.getMarginLeft() : 0)), (short)(iG.y + var4), (short)iJ.getTextLineWidth());
            Displayable.eI.drawBorder(var1.getImpl(), var3 + iF.x, var4 + iF.y, iF.width, iF.height, var9, var10 == 1);
            Displayable.eI.drawBorder(var1.getImpl(), var3 + iH.x, var4 + iH.y, iH.width, iH.height, var9, var10 == 2);
         } else {
            var1.getImpl().drawText(this.iK.j == 1 ? iI : iJ, (short)(var3 + iE.x + (iE.x == iF.x ? iF.getMarginLeft() : 0)), (short)(iE.y + var4), this.iK.j == 1 ? (short)iI.getTextLineWidth() : (short)iJ.getTextLineWidth());
            Displayable.eI.drawBorder(var1.getImpl(), var3 + iF.x, var4 + iF.y, iF.width, iF.height, var9, var2);
         }

         var16.setFont(iF.getFont());
         int var13 = iF.height - iF.getMarginTop() - iF.getMarginBottom();

         for(int var14 = 0; var14 < this.iK.i.size(); ++var14) {
            DateInputField var15;
            switch((var15 = (DateInputField)this.iK.i.elementAt(var14)).getSubType()) {
            case 1:
            case 2:
            case 6:
               if (var10 == 2) {
                  var12.setBgColor(UIStyle.COLOUR_HIGHLIGHT);
                  var12.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
               } else {
                  var12.setBgColor(UIStyle.COLOUR_BACKGROUND);
                  var12.setFgColor(UIStyle.COLOUR_EDITOR_UNFOCUSED);
               }

               var6 += var15.render(var6, var8, var16, var13);
               break;
            case 3:
            case 4:
            case 5:
               if (var10 == 1) {
                  var12.setBgColor(UIStyle.COLOUR_HIGHLIGHT);
                  var12.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
               } else {
                  var12.setBgColor(UIStyle.COLOUR_BACKGROUND);
                  var12.setFgColor(UIStyle.COLOUR_EDITOR_UNFOCUSED);
               }

               var5 += var15.render(var5, var7, var16, var13);
            }
         }

         var12.setBgColor(UIStyle.COLOUR_BACKGROUND);
         var12.setFgColor(UIStyle.COLOUR_TEXT);
      }

   }

   public final boolean a(int var1, int[] var2) {
      boolean var3 = true;
      boolean var4 = false;
      DateInputField var5 = null;
      int var6 = -1;
      int var11 = this.iK.o;
      if (this.iK.o >= 0 && this.iK.o < this.iK.i.size()) {
         var6 = ((DateInputField)this.iK.i.elementAt(this.iK.o)).getType();
      }

      int var7 = this.iK.i.size();
      int var8 = 0;
      int var9 = 0;

      for(int var10 = 0; var10 < var7; ++var10) {
         var5 = (DateInputField)this.iK.i.elementAt(var10);
         if (var1 != 0) {
            var5.setFocus(false);
         }

         switch(var5.getSubType()) {
         case 1:
         case 2:
         case 6:
            ++var9;
            break;
         case 3:
         case 4:
         case 5:
            ++var8;
         }
      }

      boolean var12 = var8 <= 0 || var9 <= 0;
      if (!this.iK.p) {
         this.iK.p = true;
         switch(var1) {
         case 0:
         case 5:
         case 6:
            this.iK.o = 0;
            break;
         case 1:
         case 2:
            this.iK.o = var7 - 1;
         case 3:
         case 4:
         }

         if (this.iK.o != -1) {
            ((DateInputField)this.iK.i.elementAt(this.iK.o)).setFocus(true);
         }
      } else {
         DateField var10000;
         switch(var1) {
         case 1:
            if (!var12 && this.iK.o >= var8) {
               var10000 = this.iK;
               var10000.o -= var8;
               break;
            }

            var3 = false;
            break;
         case 2:
            if (this.iK.o > 0) {
               --this.iK.o;
            } else {
               var3 = false;
            }
         case 3:
         case 4:
         default:
            break;
         case 5:
            if (this.iK.o < var7 - 1) {
               ++this.iK.o;
            } else {
               var3 = false;
            }
            break;
         case 6:
            if (!var12 && this.iK.o < var8) {
               var10000 = this.iK;
               var10000.o += var8;
               if (this.iK.o >= var7) {
                  --this.iK.o;
               }
            } else {
               var3 = false;
            }
         }

         if (var1 != 0 && this.iL) {
            for(var5 = this.aM(); var5 != null; var5 = this.aM()) {
               var5.incrementValue();
            }

            this.K(3);
            this.iL = false;
         }
      }

      if (var1 != 0) {
         (var5 = (DateInputField)this.iK.i.elementAt(this.iK.o)).setFocus(true);
         var1 = var5.getType();
         if (var11 != this.iK.o && var1 != var6) {
            this.iK.au.c(true);
         }
      }

      if (var3) {
         var2[1] = this.iK.getLabelHeight(var2[1]);
         if (this.iK.j == 3 && this.iK.o >= var8) {
            var2[1] += iG.y;
         }

         var2[3] = iF.y + iF.height;
         this.iK.repaint();
      }

      return var3;
   }

   public final boolean o(int var1) {
      String var2 = this.toString();
      boolean var3 = false;
      DateInputField var4;
      int var7;
      switch(var1) {
      case -10:
         return false;
      case 32:
      case 35:
      case 42:
      default:
         var7 = (var4 = (DateInputField)this.iK.i.elementAt(this.iK.o)).getType();
         if (var4.getSubType() == 6) {
            if (var4.isInitialized()) {
               var4.setValue(var4.getValue() ^ 1);
            } else {
               this.a(2, true);
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
         if (!(var4 = (DateInputField)this.iK.i.elementAt(this.iK.o)).isInitialized()) {
            this.a(var4.getType(), true);
         }

         var7 = var4.getType();
         if (var4.getSubType() != 6) {
            var1 = var4.addDigit((char)var1);
            this.iL = false;
            int var6;
            label60:
            switch(var1) {
            case -1:
               var4.setFocus(true);
               this.iK.o = this.iK.i.indexOf(var4);
               var4 = this.aM();

               while(true) {
                  if (var4 == null) {
                     break label60;
                  }

                  var6 = var4.getValue();
                  --var6;
                  var4.setValue(var6);
                  var4 = this.aM();
               }
            case 0:
               var7 = 0;
               this.iL = true;
               break;
            case 1:
               var4.setFocus(false);
               var1 = var4.getType();
               DateInputField var5;
               if ((var5 = this.aM()) == null) {
                  for(var6 = 0; var6 < this.iK.i.size(); ++var6) {
                     this.iK.o = ++this.iK.o % this.iK.i.size();
                     var4 = (DateInputField)this.iK.i.elementAt(this.iK.o);
                     if (var1 == var4.getType()) {
                        break;
                     }
                  }

                  var4.setFocus(true);
               } else {
                  var5.setFocus(true);

                  for(this.iK.o = this.iK.i.indexOf(var5); var5 != null; var5 = this.aM()) {
                     var6 = var5.getValue();
                     --var6;
                     var5.setValue(var6);
                  }
               }
            }
         } else {
            var4.setValue(var4.getValue() ^ 1);
            var4.setFocus(true);
         }
      }

      if (!this.toString().equals(var2)) {
         this.iK.g();
      }

      if (var7 != 0) {
         this.K(var7);
      }

      this.iK.repaint();
      return true;
   }

   public Command[] getExtraCommands() {
      int var1 = this.iK.o;
      Vector var2 = this.iK.i;
      return var1 >= 0 && var1 < var2.size() && ((DateInputField)var2.elementAt(var1)).getType() == 2 && !this.iK.get24HourClockFlag() ? bd : null;
   }

   public final boolean a(Command var1) {
      if (var1 == iB || var1 == iC) {
         DateInputField var2 = null;

         for(int var3 = 0; var3 < this.iK.i.size(); ++var3) {
            if ((var2 = (DateInputField)this.iK.i.elementAt(var3)).getSubType() == 6) {
               var2.setValue(var1 == iB ? 0 : 1);
               if (!var2.isInitialized()) {
                  this.a(2, true);
               }

               this.iK.repaint();
            }
         }
      }

      return false;
   }

   public final void a(int var1, boolean var2) {
      DateInputField var3 = null;

      for(int var4 = 0; var4 < this.iK.i.size(); ++var4) {
         var3 = (DateInputField)this.iK.i.elementAt(var4);
         if (var1 == 3 || var3.getType() == var1) {
            var3.initialise(var2);
         }
      }

      switch(var1) {
      case 1:
         this.iK.k = var2;
         return;
      case 2:
         this.iK.l = var2;
         return;
      case 3:
         this.iK.k = var2;
         this.iK.l = var2;
      default:
      }
   }

   public final String toString() {
      StringBuffer var1 = new StringBuffer();
      synchronized(Display.hG) {
         DateInputField var3 = null;

         for(int var4 = 0; var4 < this.iK.i.size(); ++var4) {
            var3 = (DateInputField)this.iK.i.elementAt(var4);
            var1.append(var3.toString());
         }

         return var1.toString();
      }
   }

   private DateInputField aM() {
      DateInputField var1 = null;
      DateInputField var2 = null;
      DateInputField var3 = null;
      DateInputField var4 = null;

      int var5;
      for(var5 = 0; var5 < this.iK.i.size(); ++var5) {
         switch((var4 = (DateInputField)this.iK.i.elementAt(var5)).getSubType()) {
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
         int var7 = var2.getValue();
         int var8 = var3.getValue();
         boolean var6 = false;
         if (var5 <= 9999 && var5 >= 1) {
            if (var7 != 0 && var7 <= 12) {
               if (var8 != 0 && var8 <= 31) {
                  if ((var7 == 4 || var7 == 6 || var7 == 9 || var7 == 11) && var8 > 30) {
                     return var3;
                  } else {
                     if (var5 % 4 == 0) {
                        var6 = true;
                        if (var5 % 100 == 0 && var5 % 400 != 0) {
                           var6 = false;
                        }
                     }

                     if (var7 == 2) {
                        if (var6 && var8 > 29) {
                           return var3;
                        }

                        if (!var6 && var8 > 28) {
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

   private void K(int var1) {
      for(int var3 = 0; var3 < this.iK.i.size(); ++var3) {
         DateInputField var2 = (DateInputField)this.iK.i.elementAt(var3);
         if (var1 == 3 || var2.getType() == var1) {
            byte var4 = -1;
            byte var5 = 0;
            switch(var2.getSubType()) {
            case 1:
               var4 = 12;
               break;
            case 2:
               if (this.iK.get24HourClockFlag()) {
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
               this.iK.calendar.set(var4, var2.getValue() + var5);
            }
         }
      }

      if (var1 == 2 || var1 == 3) {
         this.iK.calendar.set(13, 0);
         this.iK.calendar.set(14, 0);
      }

   }

   private static String aN() {
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
      iE = Displayable.eI.getZone(47);
      iF = Displayable.eI.getZone(48);
      iG = Displayable.eI.getZone(49);
      iH = Displayable.eI.getZone(50);
      Zone var10000 = iE;
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

      iI = TextBreaker.breakOneLineTextInZone(var10000, true, true, var1.toString(), 0, false);
      iJ = TextBreaker.breakOneLineTextInZone(iG, true, true, aN(), 0, false);
      bd = new Command[]{iB, iC};
      Command[] var6 = new Command[]{iD};
   }
}
