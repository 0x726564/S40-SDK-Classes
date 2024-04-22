package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import java.util.Vector;

public class StringItem extends Item {
   private static final int FIRST_PAGE = 1;
   private static final int INTERNAL_PAGE = 2;
   private static final int LAST_PAGE = 3;
   private static final int FIRST_TIME = 4;
   private static final String ELIIPSIS_STRING = TextDatabase.getText(34);
   boolean textLinesEmpty;
   int currentOffsetWidth;
   int currentOffsetHeight;
   int lastLineWidth;
   int offsetXPosition;
   int offsetStringHeight;
   int lineHeight;
   int layoutToConsider;
   int verticalOffsetStringTranslation;
   boolean appendedToANonStringItem;
   private int currentWidth;
   private Font currentFont;
   private int currentHeight;
   private int stringHeight;
   private int location;
   private boolean isFocusInStringItem;
   private int labelHeight;
   private int pagePixel;
   private String str;
   private String strNLconsidered;
   private Font font;
   private Font paintFont;
   private Font buttonFont;
   private int appearanceMode;
   private int originalAppearanceMode;
   private int currentAppearanceMode;
   private int minimumLineHeight;
   private boolean skipTraverse;
   Vector textLines;
   Vector textLinesOffset;
   private int ellipsisWidth;
   private boolean newText;
   private boolean buttonTruncated;
   private int offsetNL;

   public StringItem(String var1, String var2) {
      this(var1, var2, 0);
   }

   public StringItem(String var1, String var2, int var3) {
      super(var1);
      this.textLinesEmpty = true;
      this.currentOffsetWidth = 0;
      this.currentOffsetHeight = 0;
      this.lastLineWidth = 0;
      this.offsetXPosition = 0;
      this.offsetStringHeight = 0;
      this.lineHeight = 0;
      this.layoutToConsider = -1;
      this.verticalOffsetStringTranslation = 0;
      this.appendedToANonStringItem = false;
      this.currentWidth = -1;
      this.currentHeight = -1;
      this.stringHeight = 0;
      this.location = 4;
      this.isFocusInStringItem = false;
      this.labelHeight = 0;
      this.pagePixel = 0;
      this.currentAppearanceMode = -1;
      this.textLines = new Vector();
      this.textLinesOffset = new Vector();
      this.newText = false;
      this.buttonTruncated = false;
      this.offsetNL = -1;
      synchronized(Display.LCDUILock) {
         switch(var3) {
         case 0:
         case 1:
         case 2:
            this.originalAppearanceMode = var3;
            this.appearanceMode = 0;
            this.setFontImpl((Font)null);
            this.currentFont = this.font;
            this.setTextImpl(var2);
            this.checkTraverse();
            return;
         default:
            throw new IllegalArgumentException();
         }
      }
   }

   public String getText() {
      return this.str;
   }

   public void setText(String var1) {
      synchronized(Display.LCDUILock) {
         this.setTextImpl(var1);
         this.checkTraverse();
         this.invalidate();
      }
   }

   public int getAppearanceMode() {
      return this.originalAppearanceMode;
   }

   public void setFont(Font var1) {
      synchronized(Display.LCDUILock) {
         this.setFontImpl(var1);
      }
   }

   public Font getFont() {
      return this.font;
   }

   int callMinimumWidth() {
      int var1 = this.isButton() ? 2 * BUTTON_BORDER_WIDTH : 0;
      Font var2 = this.isButton() ? this.getButtonFont() : this.font;
      var1 += this.str != null && !isStringEmpty(this.strNLconsidered) ? this.ellipsisWidth : (this.isFocusable() ? this.getEmptyStringWidth(var2) : this.ellipsisWidth);
      var1 = this.itemLabel != null && var1 < Item.MIN_LABEL_WIDTH ? Item.MIN_LABEL_WIDTH : var1;
      return var1;
   }

   int callPreferredWidth(int var1) {
      int var2;
      if (this.lockedWidth != -1) {
         var2 = this.getMinimumWidth();
         this.lockedWidth = this.lockedWidth > var2 ? this.lockedWidth : var2;
         return this.lockedWidth;
      } else {
         var2 = this.isButton() ? 2 * BUTTON_BORDER_WIDTH : 0;
         Font var3 = this.isButton() ? this.getButtonFont() : this.font;
         if (this.strNLconsidered != null && !isStringEmpty(this.strNLconsidered)) {
            var2 += var3.stringWidth(this.strNLconsidered);
            if (this.boundsIncludeOtherItems && var2 < DEFAULT_WIDTH) {
               var2 += this.offsetXPosition;
            }

            if (this.isButton() && UIStyle.MAX_BUTTON_WIDTH > 0) {
               if (this.buttonTruncated && this.offsetNL >= 0) {
                  var2 = 2 * BUTTON_BORDER_WIDTH;
                  if (this.boundsIncludeOtherItems) {
                     var2 += this.offsetXPosition;
                  }

                  var2 += var3.stringWidth(this.strNLconsidered.substring(0, this.offsetNL));
                  var2 += this.ellipsisWidth;
               }

               if (var2 > UIStyle.MAX_BUTTON_WIDTH) {
                  var2 = UIStyle.MAX_BUTTON_WIDTH;
                  this.buttonTruncated = true;
               }
            }

            if (var2 < DEFAULT_WIDTH && this.currentOffsetWidth <= 0) {
               return var2 > this.ellipsisWidth ? var2 : this.ellipsisWidth;
            } else {
               return DEFAULT_WIDTH;
            }
         } else {
            var2 = this.isFocusable() ? var2 + this.getEmptyStringWidth(var3) : var2;
            return var2 > DEFAULT_WIDTH ? DEFAULT_WIDTH : var2;
         }
      }
   }

   int callMinimumHeight() {
      int var1 = this.isButton() ? 2 * BUTTON_BORDER_HEIGHT : 0;
      if (this.appearanceMode == 1 || this.isButton()) {
         var1 += TextBreaker.DEFAULT_TEXT_LEADING;
      }

      if ((this.strNLconsidered == null || isStringEmpty(this.strNLconsidered)) && this.isFocusable()) {
         Font var2 = this.appearanceMode == 1 ? this.getPaintFont() : (this.isButton() ? this.getButtonFont() : this.font);
         var1 += this.getEmptyStringHeight(DEFAULT_WIDTH, var2);
      } else {
         var1 += this.minimumLineHeight;
      }

      if (this.lockedHeight != -1 && this.lockedHeight < var1 + this.getLabelHeight(this.lockedWidth)) {
         this.lockedHeight = var1 + this.getLabelHeight(this.lockedWidth);
      }

      return var1;
   }

   int callPreferredHeight(int var1) {
      var1 = var1 == -1 ? DEFAULT_WIDTH : var1;
      int var2;
      if (this.lockedHeight != -1) {
         var2 = this.getMinimumHeight();
         this.lockedHeight = this.lockedHeight > var2 ? this.lockedHeight : var2;
         return this.lockedHeight - this.getLabelHeight(var1);
      } else {
         var2 = this.isButton() ? 2 * BUTTON_BORDER_HEIGHT : 0;
         if (this.appearanceMode == 1 || this.isButton()) {
            var2 += TextBreaker.DEFAULT_TEXT_LEADING;
         }

         if (this.strNLconsidered != null && !isStringEmpty(this.strNLconsidered)) {
            this.defineStringItemTextLines(var1, -1, this.currentOffsetWidth, this.currentOffsetHeight);
            var2 += this.textLinesEmpty ? this.offsetStringHeight : this.stringHeight;
            if (this.isButton() && UIStyle.MAX_BUTTON_WIDTH > 0 && var2 > this.getMinimumHeight() - this.getLabelHeight(var1)) {
               var2 = this.getMinimumHeight() - this.getLabelHeight(var1);
               this.buttonTruncated = true;
            }

            return var2 > this.minimumLineHeight ? var2 : this.minimumLineHeight;
         } else if (this.isFocusable()) {
            Font var3 = this.appearanceMode == 1 ? this.getPaintFont() : (this.isButton() ? this.getButtonFont() : this.font);
            return var2 + this.getEmptyStringHeight(var1, var3);
         } else {
            return this.minimumLineHeight;
         }
      }
   }

   void setToDefaults() {
      if (!this.textLines.isEmpty()) {
         this.textLines.removeAllElements();
      }

      if (!this.textLinesOffset.isEmpty()) {
         this.textLinesOffset.removeAllElements();
      }

      this.currentOffsetWidth = 0;
      this.offsetXPosition = 0;
      this.currentWidth = -1;
      this.currentHeight = -1;
      this.stringHeight = 0;
      this.appendedToANonStringItem = false;
   }

   boolean equateNLB() {
      if (this.label != null && this.label.length() > 0) {
         if (this.label.charAt(0) == '\n') {
            return true;
         }
      } else {
         if (this.str == null || this.str.length() <= 0) {
            return false;
         }

         if (this.str.charAt(0) == '\n') {
            return true;
         }
      }

      if ((this.layout & 16384) == 16384) {
         return (this.layout & 256) == 256;
      } else {
         return this.label != null && this.label.length() > 0;
      }
   }

   boolean equateNLA() {
      if (this.str != null && this.str.length() > 0) {
         if (this.str.charAt(this.str.length() - 1) == '\n') {
            return true;
         }
      } else {
         if (this.label == null || this.label.length() <= 0) {
            return false;
         }

         if (this.label.charAt(this.label.length() - 1) == '\n') {
            return true;
         }
      }

      if ((this.layout & 16384) == 16384) {
         return (this.layout & 512) == 512;
      } else {
         return false;
      }
   }

   boolean shouldSkipTraverse() {
      return this.skipTraverse;
   }

   void callPaint(Graphics var1, int var2, int var3, boolean var4) {
      super.callPaint(var1, var2, var3, var4);
      if (this.strNLconsidered != null && !isStringEmpty(this.strNLconsidered) || this.isFocusable()) {
         int var5 = this.contentWidth;
         int var6 = var3 - this.getLabelHeight(this.lockedWidth);
         int var7 = 0;
         int var8 = 0;
         int var9 = var1.getTranslateX();
         int var10 = var1.getTranslateY();
         com.nokia.mid.impl.isa.ui.gdi.Graphics var11 = var1.getImpl();
         ColorCtrl var12 = var11.getColorCtrl();
         int var13 = var12.getFgColor();
         if (this.isButton()) {
            var7 = BUTTON_BORDER_HEIGHT;
            var8 = BUTTON_BORDER_WIDTH;
            Displayable.uistyle.drawBorder(var11, var9, var10, this.contentWidth, var6, UIStyle.BORDER_BUTTON, var4);
            var5 -= 2 * BUTTON_BORDER_WIDTH;
            var6 -= 2 * BUTTON_BORDER_HEIGHT;
         }

         if (this.itemCommands.length() > 0 && this.appearanceMode == 1) {
            var11.setFont(this.getPaintFont().getImpl());
            var12.setFgColor(UIStyle.COLOUR_HIGHLIGHT);
         }

         if (this.isButton()) {
            var11.setFont(this.getButtonFont().getImpl());
         }

         if ((this.strNLconsidered == null || isStringEmpty(this.strNLconsidered)) && this.isFocusable()) {
            this.paintEmptyString(var1, var9 + var8, var10 + var7, var5, var6, var4);
         } else {
            if (this.strNLconsidered != null && !isStringEmpty(this.strNLconsidered)) {
               this.defineStringItemTextLines(var5, var6, this.currentOffsetWidth, this.currentOffsetHeight);
               if (this.textLinesOffset != null && this.currentOffsetHeight != 0 && this.currentOffsetWidth != 0) {
                  var8 = this.offsetXPosition;
                  this.paintOffsetStringLines(var11, var9 + var8, var10 + var7, var4, var12);
                  var8 = 0;
                  var7 = this.offsetStringHeight < this.currentOffsetHeight ? var7 + this.currentOffsetHeight - this.verticalOffsetStringTranslation : var7 + this.offsetStringHeight - this.verticalOffsetStringTranslation;
               } else {
                  var7 += this.currentOffsetHeight;
               }

               var6 -= this.offsetStringHeight < this.currentOffsetHeight ? this.currentOffsetHeight - this.verticalOffsetStringTranslation : this.offsetStringHeight - this.verticalOffsetStringTranslation;
               if (this.textLines != null && !this.textLines.isEmpty()) {
                  this.paintNotOffsetStringLines(var11, var9 + var8, var10 + var7, var4, var12, var5, var6);
               }
            }

            var12.setFgColor(var13);
         }
      }
   }

   void paintNotOffsetStringLines(com.nokia.mid.impl.isa.ui.gdi.Graphics var1, int var2, int var3, boolean var4, ColorCtrl var5, int var6, int var7) {
      if (!this.textLines.isEmpty()) {
         int var8 = this.offsetStringHeight < this.currentOffsetHeight ? this.currentOffsetHeight - this.verticalOffsetStringTranslation : this.offsetStringHeight - this.verticalOffsetStringTranslation;
         if (this.appearanceMode == 1) {
            var5.setFgColor(UIStyle.COLOUR_HIGHLIGHT);
            if (var4) {
               if (this.textLines.size() > 1) {
                  var1.fillRect((short)(var2 - 1), (short)var3, (short)(var6 + 2), (short)(this.stringHeight - var8 - this.lineHeight + 1));
               }

               var1.fillRect((short)(var2 - 1), (short)(var3 + this.stringHeight - var8 - this.lineHeight), (short)(this.lastLineWidth + 2), (short)(this.lineHeight + TextBreaker.DEFAULT_TEXT_LEADING));
               var5.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
            }
         } else if (this.appearanceMode == 2 && var4) {
            var5.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
         } else {
            var5.setFgColor(UIStyle.COLOUR_TEXT);
         }

         var1.drawTextInArea(var2, var3, var6, var7, this.textLines, UIStyle.isAlignedLeftToRight ? 1 : 3);
         var1.setFont(this.font.getImpl());
      }
   }

   void paintOffsetStringLines(com.nokia.mid.impl.isa.ui.gdi.Graphics var1, int var2, int var3, boolean var4, ColorCtrl var5) {
      int var6 = this.offsetStringHeight - this.verticalOffsetStringTranslation;
      if (this.appearanceMode == 1 && this.textLinesOffset.size() > 0) {
         var5.setFgColor(UIStyle.COLOUR_HIGHLIGHT);
         if (var4) {
            if (this.textLines != null && !this.textLines.isEmpty()) {
               var1.fillRect((short)(var2 - 1), (short)var3, (short)(this.currentOffsetWidth + 2), (short)(var6 + TextBreaker.DEFAULT_TEXT_LEADING));
            } else {
               int var7 = this.lastLineWidth;
               if (this.textLinesOffset.size() > 1) {
                  var1.fillRect((short)(var2 - 1), (short)var3, (short)(this.currentOffsetWidth + 2), (short)(var6 - this.lineHeight + 1));
               } else {
                  var7 = this.lastLineWidth - this.offsetXPosition;
               }

               var1.fillRect((short)(var2 - 1), (short)(var3 + var6 - this.lineHeight), (short)(var7 + 2), (short)(this.lineHeight + TextBreaker.DEFAULT_TEXT_LEADING));
            }

            var5.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
         }
      } else {
         var5.setFgColor(UIStyle.COLOUR_TEXT);
      }

      var1.drawTextInArea(var2, var3, this.currentOffsetWidth, var6, this.textLinesOffset, UIStyle.isAlignedLeftToRight ? 1 : 3);
   }

   void invalidate() {
      super.invalidate();
      this.location = 4;
   }

   boolean callTraverse(int var1, int var2, int var3, int[] var4) {
      boolean var5 = false;
      synchronized(Display.LCDUILock) {
         if (this.str == null || this.textLines.isEmpty() && this.textLinesOffset.isEmpty() || this.bounds[3] <= this.owner.viewport[3]) {
            return false;
         }

         int var7 = this.currentOffsetHeight - this.verticalOffsetStringTranslation;
         switch(var1) {
         case 0:
            if (this.location == 4) {
               var4[1] = 0;
               var4[3] = this.owner.viewport[3];
               this.location = 1;
               this.isFocusInStringItem = true;
               var5 = true;
            }
            break;
         case 1:
            var5 = this.scrollUp(var7, var4);
            break;
         case 6:
            var5 = this.scrollDown(var7, var4);
            break;
         default:
            var5 = this.scrollLeftAndRight(var4);
         }
      }

      if (var5) {
         this.repaint();
      }

      return var5;
   }

   boolean isFocusable() {
      return this.itemCommands != null && this.itemCommands.length() >= 1;
   }

   int callGetLayout() {
      int var1 = this.layout & 255;
      int var2 = this.getVerticalTemporaryLayout();
      int var3 = UIStyle.isAlignedLeftToRight ? 1 : 2;
      switch(var1) {
      case 0:
         var1 = var2 | var3;
         break;
      case 1:
      case 2:
      case 3:
         var1 |= var2;
         break;
      case 16:
      case 32:
      case 48:
         var1 |= var3;
      }

      this.verticalLayout = var1 & 240;
      return var1;
   }

   Command[] getExtraCommands() {
      Command[] var1 = null;
      if (this.isButton() && this.buttonTruncated && UIStyle.MAX_BUTTON_WIDTH > 0) {
         var1 = new Command[]{ChoiceGroup.VIEW};
      }

      return var1;
   }

   boolean launchExtraCommand(Command var1) {
      if (var1.equals(ChoiceGroup.VIEW) && this.isButton() && this.buttonTruncated && UIStyle.MAX_BUTTON_WIDTH > 0) {
         Display var2 = this.owner.myDisplay;
         TruncatedItemScreen var3 = var2.getTruncatedItemScreen();
         var3.showElement(var2, this.owner, this.str, (Image)null, true);
         return true;
      } else {
         return false;
      }
   }

   void callKeyPressed(int var1, int var2) {
      synchronized(Display.LCDUILock) {
         if (this.isButton() && this.buttonTruncated && UIStyle.MAX_BUTTON_WIDTH > 0 && var1 == 35) {
            Display var4 = this.owner.myDisplay;
            TruncatedItemScreen var5 = var4.getTruncatedItemScreen();
            var5.showElement(var4, this.owner, this.str, (Image)null, false);
         }

      }
   }

   boolean addCommandImpl(Command var1) {
      boolean var2 = super.addCommandImpl(var1);
      if (var2 && this.itemCommands.length() == 1 && this.appearanceMode == 0) {
         this.appearanceMode = this.originalAppearanceMode == 2 ? 2 : 1;
         this.checkTraverse();
         this.invalidate();
      }

      return var2;
   }

   boolean removeCommandImpl(Command var1) {
      boolean var2 = super.removeCommandImpl(var1);
      if (var2 && this.itemCommands.length() < 1 && this.appearanceMode != 0) {
         this.originalAppearanceMode = this.appearanceMode;
         this.appearanceMode = 0;
         this.checkTraverse();
         this.invalidate();
      }

      return var2;
   }

   void setOffsetWidth(int var1) {
      this.currentOffsetWidth = var1;
   }

   void setOffsetPosition(int var1) {
      this.offsetXPosition = var1;
   }

   void setOffsetHeight(int var1) {
      this.currentOffsetHeight = var1;
   }

   boolean boundsIncludeOtherItems(boolean var1, int var2, int var3) {
      if ((this.owner == null || this.itemIndex > 0) && !this.equateNLB()) {
         Item var4 = ((Form)this.owner).items[this.itemIndex - 1];
         boolean var5 = true;
         if (var4 instanceof StringItem) {
            int var6 = this.font.charWidth(' ') - Form.CELL_SPACING;
            int[] var10000 = ((Form)this.owner).viewable;
            var10000[0] += var6;
            var10000 = ((Form)this.owner).viewable;
            var10000[2] -= var6;
            ((StringItem)var4).lastLineWidth += var6;
            int var7 = this.owner.viewport[2] - ((StringItem)var4).lastLineWidth;
            var5 = var7 >= this.callMinimumWidth() && (var7 < this.callPreferredWidth(-1) || var4.boundsIncludeOtherItems || var4.bounds[2] >= this.owner.viewport[2]);
         }

         this.boundsIncludeOtherItems = !var1 && var5 && this.getLabel() == null && !this.isButton() && this.lockedWidth == -1 && !this.shouldHShrink() && this.itemIndex > 0 && (this.callMinimumWidth() <= var3 - Form.CELL_SPACING && var2 > var3 - Form.CELL_SPACING || var4 instanceof StringItem && (this.owner.viewport[2] - Form.CELL_SPACING - ((StringItem)var4).lastLineWidth >= this.callMinimumWidth() || ((StringItem)var4).currentOffsetHeight - ((StringItem)var4).offsetStringHeight > 0) && !((StringItem)var4).appendedToANonStringItem && !((StringItem)var4).equateNLA() && !((StringItem)var4).isButton()) && var2 > var3 - Form.CELL_SPACING;
      } else {
         this.boundsIncludeOtherItems = false;
      }

      return this.boundsIncludeOtherItems;
   }

   int getMinimumScroll(int var1) {
      int var3 = ((Form)this.owner).oldViewY - this.bounds[1] - this.getLabelHeight(this.bounds[2]);
      var3 = var3 < 0 ? 0 : var3;
      if (this.lineHeight == 0) {
         this.lineHeight = this.minimumLineHeight = this.font.getHeight() + TextBreaker.DEFAULT_TEXT_LEADING;
      }

      int var2;
      if (var1 == 6) {
         var2 = (var3 + this.owner.viewport[3]) % this.lineHeight;
      } else if (var1 == 1) {
         var2 = var3 % this.lineHeight;
         if (var2 != 0) {
            var2 = this.lineHeight - var2;
         }
      } else {
         var2 = 0;
      }

      if (this.bounds[1] > ((Form)this.owner).oldViewY && this.bounds[1] + this.bounds[3] < ((Form)this.owner).oldViewY + this.owner.viewport[3]) {
         return this.pagePixel - var2 + this.lineHeight;
      } else {
         var2 = ((Form)this.owner).view[1] < this.bounds[1] + this.currentOffsetHeight - this.verticalOffsetStringTranslation ? (2 * this.lineHeight - var2 > Form.FORM_MAX_SCROLL ? this.lineHeight - var2 : 2 * this.lineHeight - var2) : this.pagePixel - var2 + this.lineHeight;
         var2 = var2 < 0 ? var2 + this.lineHeight : var2;
         return var2 <= 0 ? var2 + this.lineHeight : var2;
      }
   }

   int getVerticalTemporaryLayout() {
      char var1 = '／';
      int var2 = this.layout | var1;
      if (this.owner == null) {
         return var2;
      } else if (var2 == var1) {
         Item var3 = this.itemIndex > 0 ? ((Form)this.owner).items[this.itemIndex - 1] : null;
         Item var4 = this.itemIndex < ((Form)this.owner).numOfItems - 1 ? ((Form)this.owner).items[this.itemIndex + 1] : null;
         if (var3 != null && var3.lineY == this.lineY && (var3.verticalLayout | var1) != var1) {
            this.verticalLayout = var3.verticalLayout;
            return this.verticalLayout;
         } else if (var4 == null || var4.lineY != this.lineY || (var4.verticalLayout | var1) == var1 || this.currentOffsetHeight != 0 && this.currentOffsetWidth != 0) {
            this.verticalLayout = 16;
            return this.verticalLayout;
         } else {
            this.verticalLayout = var4.verticalLayout;
            return this.verticalLayout;
         }
      } else {
         this.verticalLayout = this.layout & 240;
         return this.verticalLayout;
      }
   }

   boolean isButton() {
      return this.itemCommands.length() > 0 && this.appearanceMode == 2;
   }

   boolean canItemAppearanceChange() {
      return true;
   }

   private void defineStringItemTextLines(int var1, int var2, int var3, int var4) {
      int var5 = this.getVerticalTemporaryLayout();
      boolean var6 = this.repeatTextLinesDefinition(var1, var2, var3, var4, var5);
      if (var6) {
         this.newText = false;
         this.currentFont = this.font;
         if (this.strNLconsidered != null && !isStringEmpty(this.strNLconsidered)) {
            this.lineHeight = 0;
            this.currentWidth = var1;
            this.currentHeight = var2;
            this.currentOffsetWidth = var3;
            this.currentOffsetHeight = var4;
            this.currentAppearanceMode = this.appearanceMode;
            this.layoutToConsider = var5;
            Object var7 = null;
            TextBreaker var8 = this.getInitializedTextBreakerAndInitializeTextLines();
            this.layoutToConsider = var5;
            if (!this.boundsIncludeOtherItems && (this.currentOffsetWidth == 0 || this.currentOffsetHeight == 0)) {
               this.offsetStringHeight = 0;
               this.currentOffsetHeight = 0;
               this.verticalOffsetStringTranslation = 0;
            } else {
               this.defineOffsetTextLines((TextLine)var7, var8, this.layoutToConsider, var4, var2);
            }

            this.defineNotOffsetTextLines((TextLine)var7, var8, var2);
            var8.destroyBreaker();
            var8 = null;
            this.fieldsDefinition(this.lineHeight);
         } else {
            this.textLines.removeAllElements();
            this.textLinesOffset.removeAllElements();
         }
      }
   }

   private void setTextImpl(String var1) {
      this.newText = true;
      this.str = var1;
      this.buttonTruncated = false;
      if (var1 == null) {
         this.strNLconsidered = null;
         this.str = null;
      } else {
         byte var2 = 0;
         int var3 = var1.length();
         if ((this.label == null || this.label.length() == 0) && (this.layout & 256) != 256 && var1 != null && var3 > 0 && var1.charAt(0) == '\n') {
            var2 = 1;
         }

         if (var1 != null && var3 > 1 && (this.layout & 512) != 512 && var1.charAt(var3 - 1) == '\n') {
            --var3;
         }

         if (var3 > var2) {
            this.strNLconsidered = var1.substring(var2, var3);
            if (UIStyle.MAX_BUTTON_WIDTH > 0 && this.isButton()) {
               this.offsetNL = this.strNLconsidered.indexOf(10);
               if (this.offsetNL >= 0) {
                  this.buttonTruncated = true;
               }
            }
         } else {
            this.strNLconsidered = "";
         }

      }
   }

   private void setFontImpl(Font var1) {
      boolean var2 = false;
      this.buttonTruncated = false;
      if (var1 != this.font || var1 == null) {
         var2 = true;
         if (var1 == null) {
            this.font = new Font(ITEM_VALUE_ZONE.getFont());
            this.buttonFont = null;
         } else {
            this.font = var1;
            this.buttonFont = this.font;
         }

         this.paintFont = null;
         this.ellipsisWidth = this.font.stringWidth(ELIIPSIS_STRING);
         this.emptyStringWidth = this.getEmptyStringWidth(this.font);
         this.lineHeight = this.minimumLineHeight = this.font.getHeight() + TextBreaker.DEFAULT_TEXT_LEADING;
         if (UIStyle.MAX_BUTTON_WIDTH > 0 && this.isButton() && this.strNLconsidered != null) {
            this.offsetNL = this.strNLconsidered.indexOf(10);
            if (this.offsetNL >= 0) {
               this.buttonTruncated = true;
            }
         }
      }

      if (var2) {
         this.invalidate();
      }

   }

   private void checkTraverse() {
      if (this.isFocusable() || (this.str != null || this.label != null) && (this.str != null || !isStringEmpty(this.label)) && (this.label != null || !isStringEmpty(this.strNLconsidered)) && (!isStringEmpty(this.label) || !isStringEmpty(this.strNLconsidered))) {
         this.skipTraverse = false;
      } else {
         this.skipTraverse = true;
      }

   }

   private Font getPaintFont() {
      if (this.paintFont == null) {
         this.paintFont = Font.getFont(this.font.getFace(), this.font.getStyle() | 4, this.font.getSize());
      }

      this.lineHeight = this.minimumLineHeight = this.paintFont.getHeight() + TextBreaker.DEFAULT_TEXT_LEADING;
      return this.paintFont;
   }

   private Font getButtonFont() {
      if (this.buttonFont == null) {
         this.buttonFont = new Font(UIStyle.getUIStyle().getButtonFont());
      }

      this.lineHeight = this.minimumLineHeight = this.buttonFont.getHeight() + TextBreaker.DEFAULT_TEXT_LEADING;
      return this.buttonFont;
   }

   private boolean scrollDown(int var1, int[] var2) {
      if (this.location == 3) {
         this.isFocusInStringItem = false;
         return false;
      } else {
         int var3 = (var2[1] + this.owner.viewport[3] - this.labelHeight) % this.lineHeight;
         if (var3 != 0) {
            var2[1] += this.lineHeight - var3;
         }

         if (!this.isFocusInStringItem) {
            this.location = 4;
         }

         int var5;
         if (!this.isFocusInStringItem && this.location == 4 && ((Form)this.owner).oldViewY < this.bounds[1]) {
            this.location = 1;
            this.isFocusInStringItem = true;
            var2[1] = 0;
         } else if (this.location != 1 && this.location != 4) {
            if (var2[1] + this.lineHeight < var1) {
               var2[1] += this.lineHeight;
            } else if (this.bounds[3] - (var2[1] + var2[3]) > this.pagePixel) {
               var2[1] += this.pagePixel;
               if (this.bounds[3] - var2[1] <= this.pagePixel) {
                  this.location = 3;
                  this.isFocusInStringItem = true;
               }
            } else {
               var2[1] = this.bounds[3] - this.owner.viewport[3];
               this.location = 3;
               this.isFocusInStringItem = true;
            }
         } else {
            this.location = 2;
            this.isFocusInStringItem = true;
            if (var2[1] < var1) {
               var2[1] += this.lineHeight;
            } else if (this.labelHeight <= 0) {
               var2[1] += this.pagePixel;
            } else {
               boolean var4 = true;

               for(var5 = 1; this.labelHeight + var5 * this.lineHeight < this.owner.viewport[3]; ++var5) {
               }

               var2[1] += this.labelHeight + (var5 - 2) * this.lineHeight;
            }

            if (this.bounds[3] - var2[1] <= this.pagePixel) {
               this.location = 3;
               this.isFocusInStringItem = true;
            }
         }

         if (var2[1] >= this.bounds[3] - this.owner.viewport[3]) {
            var2[1] = this.bounds[3] - this.owner.viewport[3];
            var2[3] = this.owner.viewport[3];
            this.location = 3;
         }

         var5 = this.owner.viewport[3];
         var2[3] = var5 < this.bounds[3] - var2[1] ? var5 : this.bounds[3] - var2[1];
         return true;
      }
   }

   private boolean scrollUp(int var1, int[] var2) {
      if (this.location == 1) {
         this.isFocusInStringItem = false;
         return false;
      } else {
         int var3 = (var2[1] - this.labelHeight) % this.lineHeight;
         if (var3 != 0) {
            var2[1] -= var3;
         }

         if (!this.isFocusInStringItem && ((Form)this.owner).oldViewY > this.bounds[1] + this.owner.viewport[3]) {
            this.location = 3;
         }

         if (this.location == 3) {
            if (!this.isFocusInStringItem && ((Form)this.owner).oldViewY > this.bounds[1] + this.bounds[3] - this.owner.viewport[3]) {
               var2[1] = this.bounds[3] - this.owner.viewport[3];
            } else {
               var2[1] -= this.pagePixel;
            }

            this.location = 2;
            this.isFocusInStringItem = true;
         } else if (var2[1] < var1) {
            if (var2[1] <= this.labelHeight + this.lineHeight) {
               var2[1] = 0;
               this.location = 1;
            } else {
               var2[1] -= this.lineHeight;
            }

            this.isFocusInStringItem = true;
         } else {
            this.isFocusInStringItem = true;
            if (var2[1] > this.owner.viewport[3]) {
               var2[1] -= this.pagePixel;
            } else {
               var2[1] = 0;
               this.location = 1;
            }
         }

         if (var2[1] <= 0) {
            var2[1] = 0;
            this.location = 1;
         }

         if (var2[1] >= this.bounds[3] - this.pagePixel) {
            var2[1] = this.bounds[3] - this.pagePixel;
            this.location = 3;
         }

         int var4 = this.owner.viewport[3];
         var2[3] = var4 < this.bounds[3] - var2[1] ? var4 : this.bounds[3] - var2[1];
         return true;
      }
   }

   private boolean scrollLeftAndRight(int[] var1) {
      if (!this.isFocusInStringItem && (!((Form)this.owner).isTallRow || ((Form)this.owner).oldViewY > this.bounds[1] && ((Form)this.owner).oldViewY < this.bounds[1] + this.bounds[3])) {
         var1[1] = ((Form)this.owner).oldViewY - this.bounds[1];
         this.isFocusInStringItem = true;
         this.location = 2;
         if (var1[1] <= this.owner.viewport[3] - this.pagePixel - this.lineHeight) {
            var1[1] = 0;
            this.location = 1;
         } else if (var1[1] + this.owner.viewport[3] >= this.bounds[3]) {
            var1[1] = this.bounds[3] - this.owner.viewport[3];
            this.location = 3;
         }

         int var2 = this.owner.viewport[3];
         var1[3] = var2 < this.bounds[3] - var1[1] ? var2 : this.bounds[3] - var1[1];
         return true;
      } else {
         this.isFocusInStringItem = false;
         return false;
      }
   }

   private void fieldsDefinition(int var1) {
      this.labelHeight = this.getLabelHeight(this.lockedWidth);
      if (this.owner != null) {
         boolean var2 = false;

         int var3;
         for(var3 = 0; var1 != 0 && var3 * var1 < this.owner.viewport[3]; ++var3) {
         }

         this.pagePixel = (var3 - 2) * var1;
      }

   }

   private boolean repeatTextLinesDefinition(int var1, int var2, int var3, int var4, int var5) {
      if (var1 == this.currentWidth && var2 == this.currentHeight && var3 == this.currentOffsetWidth && var4 == this.currentOffsetHeight && this.appearanceMode == this.currentAppearanceMode && this.layoutToConsider == var5 && this.font == this.currentFont && !this.newText) {
         if (this.owner != null) {
            int var6;
            for(var6 = 0; this.lineHeight != 0 && var6 * this.lineHeight < this.owner.viewport[3]; ++var6) {
            }

            this.pagePixel = (var6 - 2) * this.lineHeight;
         }

         return false;
      } else {
         return true;
      }
   }

   private TextBreaker getInitializedTextBreakerAndInitializeTextLines() {
      TextBreaker var1 = TextBreaker.getBreaker();
      if (this.itemCommands.length() > 0 && this.appearanceMode == 1) {
         var1.setFont(this.getPaintFont().getImpl());
      } else if (this.isButton()) {
         var1.setFont(this.getButtonFont().getImpl());
      } else {
         var1.setFont(this.font.getImpl());
      }

      var1.setLeading(TextBreaker.DEFAULT_TEXT_LEADING, true);
      var1.setText(this.strNLconsidered);
      if (this.isButton() && UIStyle.MAX_BUTTON_WIDTH > 0 && this.buttonTruncated) {
         var1.setTruncation(true);
      } else {
         var1.setTruncation(false);
      }

      var1.enableWordWrapping(false);
      this.stringHeight = 0;
      this.textLines.removeAllElements();
      this.textLinesOffset.removeAllElements();
      this.offsetStringHeight = 0;
      return var1;
   }

   private void defineOffsetTextLines(TextLine var1, TextBreaker var2, int var3, int var4, int var5) {
      this.offsetStringHeight = 0;
      switch(var3 & 255 & 48) {
      case 32:
         if ((var1 = var2.getTextLine(this.currentOffsetWidth)) != null) {
            this.lineHeight = var1.getTextLineHeight();
            this.lastLineWidth = var1.getTextLineWidth();
            this.textLinesOffset.addElement(var1);
            this.offsetStringHeight += this.lineHeight;
            this.verticalOffsetStringTranslation = this.currentOffsetHeight - this.lineHeight;
            this.stringHeight = var4 - this.verticalOffsetStringTranslation;
         } else {
            this.verticalOffsetStringTranslation = this.currentOffsetHeight;
         }
         break;
      case 48:
         if ((var1 = var2.getTextLine(this.currentOffsetWidth)) != null) {
            this.lineHeight = var1.getTextLineHeight();
            this.lastLineWidth = var1.getTextLineWidth();
            this.textLinesOffset.addElement(var1);
            this.offsetStringHeight += this.lineHeight;
            this.verticalOffsetStringTranslation = (this.currentOffsetHeight - this.lineHeight) / 2;
            this.stringHeight = var4 - this.verticalOffsetStringTranslation;
         } else {
            this.verticalOffsetStringTranslation = this.currentOffsetHeight;
         }
         break;
      default:
         this.verticalOffsetStringTranslation = 0;
         int var6;
         if (this.lineHeight <= 0) {
            var6 = this.appearanceMode == 1 ? this.paintFont.getHeight() : this.font.getHeight();
            var6 += TextBreaker.DEFAULT_TEXT_LEADING;
         } else {
            var6 = this.lineHeight;
         }

         if (this.lockedHeight != -1 && this.stringHeight + 2 * var6 > var5) {
            var2.setTruncation(true);
         }

         while(this.offsetStringHeight < var4 && (var1 = var2.getTextLine(this.currentOffsetWidth)) != null) {
            this.lineHeight = var1.getTextLineHeight();
            this.lastLineWidth = var1.getTextLineWidth();
            this.textLinesOffset.addElement(var1);
            this.offsetStringHeight += this.lineHeight;
            if (this.lockedHeight != -1 && this.stringHeight + 2 * (var6 + TextBreaker.DEFAULT_TEXT_LEADING) > var5) {
               var2.setTruncation(true);
            }
         }

         this.stringHeight = this.offsetStringHeight;
         var2.setTruncation(false);
      }

      this.lastLineWidth += this.offsetXPosition;
      if (this.stringHeight < this.currentOffsetHeight) {
         this.stringHeight = var4 - this.verticalOffsetStringTranslation;
      }

   }

   private void defineNotOffsetTextLines(TextLine var1, TextBreaker var2, int var3) {
      int var4;
      if (this.lineHeight <= 0) {
         var4 = this.appearanceMode == 1 ? this.paintFont.getHeight() : this.font.getHeight();
         var4 += TextBreaker.DEFAULT_TEXT_LEADING;
      } else {
         var4 = this.lineHeight;
      }

      if (this.lockedHeight != -1 && this.stringHeight + 2 * var4 > var3) {
         var2.setTruncation(true);
      }

      while((var1 = var2.getTextLine(this.currentWidth)) != null && (this.stringHeight + this.lineHeight <= var3 || var3 == -1)) {
         this.lineHeight = var1.getTextLineHeight();
         this.lastLineWidth = var1.getTextLineWidth();
         this.textLines.addElement(var1);
         this.stringHeight += this.lineHeight;
         this.textLinesEmpty = false;
         if (this.lockedHeight != -1 && this.stringHeight + 2 * this.lineHeight > var3) {
            var2.setTruncation(true);
         }
      }

      var2.setTruncation(false);
   }
}
