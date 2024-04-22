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

   public StringItem(String label, String text) {
      this(label, text, 0);
   }

   public StringItem(String label, String text, int appearanceMode) {
      super(label);
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
         switch(appearanceMode) {
         case 0:
         case 1:
         case 2:
            this.originalAppearanceMode = appearanceMode;
            this.appearanceMode = 0;
            this.setFontImpl((Font)null);
            this.currentFont = this.font;
            this.setTextImpl(text);
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

   public void setText(String text) {
      synchronized(Display.LCDUILock) {
         this.setTextImpl(text);
         this.checkTraverse();
         this.invalidate();
      }
   }

   public int getAppearanceMode() {
      return this.originalAppearanceMode;
   }

   public void setFont(Font font) {
      synchronized(Display.LCDUILock) {
         this.setFontImpl(font);
      }
   }

   public Font getFont() {
      return this.font;
   }

   int callMinimumWidth() {
      int minW = this.isButton() ? 2 * BUTTON_BORDER_WIDTH : 0;
      Font tmpFont = this.isButton() ? this.getButtonFont() : this.font;
      minW += this.str != null && !isStringEmpty(this.strNLconsidered) ? this.ellipsisWidth : (this.isFocusable() ? this.getEmptyStringWidth(tmpFont) : this.ellipsisWidth);
      minW = this.itemLabel != null && minW < Item.MIN_LABEL_WIDTH ? Item.MIN_LABEL_WIDTH : minW;
      return minW;
   }

   int callPreferredWidth(int h) {
      int prefW;
      if (this.lockedWidth != -1) {
         prefW = this.getMinimumWidth();
         this.lockedWidth = this.lockedWidth > prefW ? this.lockedWidth : prefW;
         return this.lockedWidth;
      } else {
         prefW = this.isButton() ? 2 * BUTTON_BORDER_WIDTH : 0;
         Font tmpFont = this.isButton() ? this.getButtonFont() : this.font;
         if (this.strNLconsidered != null && !isStringEmpty(this.strNLconsidered)) {
            prefW += tmpFont.stringWidth(this.strNLconsidered);
            if (this.boundsIncludeOtherItems && prefW < DEFAULT_WIDTH) {
               prefW += this.offsetXPosition;
            }

            if (this.isButton() && UIStyle.MAX_BUTTON_WIDTH > 0) {
               if (this.buttonTruncated && this.offsetNL >= 0) {
                  prefW = 2 * BUTTON_BORDER_WIDTH;
                  if (this.boundsIncludeOtherItems) {
                     prefW += this.offsetXPosition;
                  }

                  prefW += tmpFont.stringWidth(this.strNLconsidered.substring(0, this.offsetNL));
                  prefW += this.ellipsisWidth;
               }

               if (prefW > UIStyle.MAX_BUTTON_WIDTH) {
                  prefW = UIStyle.MAX_BUTTON_WIDTH;
                  this.buttonTruncated = true;
               }
            }

            if (prefW < DEFAULT_WIDTH && this.currentOffsetWidth <= 0) {
               return prefW > this.ellipsisWidth ? prefW : this.ellipsisWidth;
            } else {
               return DEFAULT_WIDTH;
            }
         } else {
            prefW = this.isFocusable() ? prefW + this.getEmptyStringWidth(tmpFont) : prefW;
            return prefW > DEFAULT_WIDTH ? DEFAULT_WIDTH : prefW;
         }
      }
   }

   int callMinimumHeight() {
      int minH = this.isButton() ? 2 * BUTTON_BORDER_HEIGHT : 0;
      if (this.appearanceMode == 1 || this.isButton()) {
         minH += TextBreaker.DEFAULT_TEXT_LEADING;
      }

      if ((this.strNLconsidered == null || isStringEmpty(this.strNLconsidered)) && this.isFocusable()) {
         Font tmpFont = this.appearanceMode == 1 ? this.getPaintFont() : (this.isButton() ? this.getButtonFont() : this.font);
         minH += this.getEmptyStringHeight(DEFAULT_WIDTH, tmpFont);
      } else {
         minH += this.minimumLineHeight;
      }

      if (this.lockedHeight != -1 && this.lockedHeight < minH + this.getLabelHeight(this.lockedWidth)) {
         this.lockedHeight = minH + this.getLabelHeight(this.lockedWidth);
      }

      return minH;
   }

   int callPreferredHeight(int w) {
      w = w == -1 ? DEFAULT_WIDTH : w;
      int prefH;
      if (this.lockedHeight != -1) {
         prefH = this.getMinimumHeight();
         this.lockedHeight = this.lockedHeight > prefH ? this.lockedHeight : prefH;
         return this.lockedHeight - this.getLabelHeight(w);
      } else {
         prefH = this.isButton() ? 2 * BUTTON_BORDER_HEIGHT : 0;
         if (this.appearanceMode == 1 || this.isButton()) {
            prefH += TextBreaker.DEFAULT_TEXT_LEADING;
         }

         if (this.strNLconsidered != null && !isStringEmpty(this.strNLconsidered)) {
            this.defineStringItemTextLines(w, -1, this.currentOffsetWidth, this.currentOffsetHeight);
            prefH += this.textLinesEmpty ? this.offsetStringHeight : this.stringHeight;
            if (this.isButton() && UIStyle.MAX_BUTTON_WIDTH > 0 && prefH > this.getMinimumHeight() - this.getLabelHeight(w)) {
               prefH = this.getMinimumHeight() - this.getLabelHeight(w);
               this.buttonTruncated = true;
            }

            return prefH > this.minimumLineHeight ? prefH : this.minimumLineHeight;
         } else if (this.isFocusable()) {
            Font tmpFont = this.appearanceMode == 1 ? this.getPaintFont() : (this.isButton() ? this.getButtonFont() : this.font);
            return prefH + this.getEmptyStringHeight(w, tmpFont);
         } else {
            return isStringEmpty(this.getLabel()) ? 0 : this.minimumLineHeight;
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

      this.location = 4;
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

   void callPaint(Graphics g, int width, int height, boolean isFocused) {
      super.callPaint(g, width, height, isFocused);
      if (this.strNLconsidered != null && !isStringEmpty(this.strNLconsidered) || this.isFocusable()) {
         int w = this.contentWidth;
         int h = height - this.getLabelHeight(this.lockedWidth);
         int translateY = 0;
         int translateX = 0;
         int tX = g.getTranslateX();
         int tY = g.getTranslateY();
         com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
         ColorCtrl colorCtrl = ng.getColorCtrl();
         int oldFgColor = colorCtrl.getFgColor();
         if (this.isButton()) {
            translateY = BUTTON_BORDER_HEIGHT;
            translateX = BUTTON_BORDER_WIDTH;
            Displayable.uistyle.drawBorder(ng, tX, tY, this.contentWidth, h, UIStyle.BORDER_BUTTON, isFocused);
            w -= 2 * BUTTON_BORDER_WIDTH;
            h -= 2 * BUTTON_BORDER_HEIGHT;
         }

         if (this.itemCommands.length() > 0 && this.appearanceMode == 1) {
            ng.setFont(this.getPaintFont().getImpl());
            colorCtrl.setFgColor(UIStyle.COLOUR_HIGHLIGHT);
         }

         if (this.isButton()) {
            ng.setFont(this.getButtonFont().getImpl());
         }

         if ((this.strNLconsidered == null || isStringEmpty(this.strNLconsidered)) && this.isFocusable()) {
            this.paintEmptyString(g, tX + translateX, tY + translateY, w, h, isFocused);
         } else {
            if (this.strNLconsidered != null && !isStringEmpty(this.strNLconsidered)) {
               this.defineStringItemTextLines(w, h, this.currentOffsetWidth, this.currentOffsetHeight);
               if (this.textLinesOffset != null && this.currentOffsetHeight != 0 && this.currentOffsetWidth != 0) {
                  translateX = this.offsetXPosition;
                  this.paintOffsetStringLines(ng, tX + translateX, tY + translateY, isFocused, colorCtrl);
                  translateX = 0;
                  translateY = this.offsetStringHeight < this.currentOffsetHeight ? translateY + this.currentOffsetHeight - this.verticalOffsetStringTranslation : translateY + this.offsetStringHeight - this.verticalOffsetStringTranslation;
               } else {
                  translateY += this.currentOffsetHeight;
               }

               h -= this.offsetStringHeight < this.currentOffsetHeight ? this.currentOffsetHeight - this.verticalOffsetStringTranslation : this.offsetStringHeight - this.verticalOffsetStringTranslation;
               if (this.textLines != null && !this.textLines.isEmpty()) {
                  this.paintNotOffsetStringLines(ng, tX + translateX, tY + translateY, isFocused, colorCtrl, w, h);
               }
            }

            colorCtrl.setFgColor(oldFgColor);
         }
      }
   }

   void paintNotOffsetStringLines(com.nokia.mid.impl.isa.ui.gdi.Graphics ng, int tX, int tY, boolean isFocused, ColorCtrl colorCtrl, int w, int h) {
      if (!this.textLines.isEmpty()) {
         int height = this.offsetStringHeight < this.currentOffsetHeight ? this.currentOffsetHeight - this.verticalOffsetStringTranslation : this.offsetStringHeight - this.verticalOffsetStringTranslation;
         if (this.appearanceMode == 1) {
            colorCtrl.setFgColor(UIStyle.COLOUR_HIGHLIGHT);
            if (isFocused) {
               if (this.textLines.size() > 1) {
                  ng.fillRect((short)(tX - 1), (short)tY, (short)(w + 2), (short)(this.stringHeight - height - this.lineHeight + 1));
               }

               ng.fillRect((short)(tX - 1), (short)(tY + this.stringHeight - height - this.lineHeight), (short)(this.lastLineWidth + 2), (short)(this.lineHeight + TextBreaker.DEFAULT_TEXT_LEADING));
               colorCtrl.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
            }
         } else if (this.appearanceMode == 2 && isFocused) {
            colorCtrl.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
         } else {
            colorCtrl.setFgColor(UIStyle.COLOUR_TEXT);
         }

         ng.drawTextInArea(tX, tY, w, h, this.textLines, UIStyle.isAlignedLeftToRight ? 1 : 3);
         ng.setFont(this.font.getImpl());
      }
   }

   void paintOffsetStringLines(com.nokia.mid.impl.isa.ui.gdi.Graphics ng, int tX, int tY, boolean isFocused, ColorCtrl colorCtrl) {
      int height = this.offsetStringHeight - this.verticalOffsetStringTranslation;
      if (this.appearanceMode == 1 && this.textLinesOffset.size() > 0) {
         colorCtrl.setFgColor(UIStyle.COLOUR_HIGHLIGHT);
         if (isFocused) {
            if (this.textLines != null && !this.textLines.isEmpty()) {
               ng.fillRect((short)(tX - 1), (short)tY, (short)(this.currentOffsetWidth + 2), (short)(height + TextBreaker.DEFAULT_TEXT_LEADING));
            } else {
               int rectWidth = this.lastLineWidth;
               if (this.textLinesOffset.size() > 1) {
                  ng.fillRect((short)(tX - 1), (short)tY, (short)(this.currentOffsetWidth + 2), (short)(height - this.lineHeight + 1));
               } else {
                  rectWidth = this.lastLineWidth - this.offsetXPosition;
               }

               ng.fillRect((short)(tX - 1), (short)(tY + height - this.lineHeight), (short)(rectWidth + 2), (short)(this.lineHeight + TextBreaker.DEFAULT_TEXT_LEADING));
            }

            colorCtrl.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
         }
      } else {
         colorCtrl.setFgColor(UIStyle.COLOUR_TEXT);
      }

      ng.drawTextInArea(tX, tY, this.currentOffsetWidth, height, this.textLinesOffset, UIStyle.isAlignedLeftToRight ? 1 : 3);
   }

   void invalidate() {
      super.invalidate();
      this.location = 4;
   }

   boolean callTraverse(int dir, int viewportWidth, int viewportHeight, int[] visRect_inout) {
      boolean traversed = false;
      synchronized(Display.LCDUILock) {
         if (this.str == null || this.textLines.isEmpty() && this.textLinesOffset.isEmpty() || this.bounds[3] <= this.owner.viewport[3]) {
            return false;
         }

         int heightOff = this.currentOffsetHeight - this.verticalOffsetStringTranslation;
         switch(dir) {
         case 0:
            if (this.location == 4) {
               visRect_inout[1] = 0;
               visRect_inout[3] = this.owner.viewport[3];
               this.location = 1;
            }

            this.isFocusInStringItem = true;
            traversed = true;
            break;
         case 1:
            traversed = this.scrollUp(heightOff, visRect_inout);
            break;
         case 6:
            traversed = this.scrollDown(heightOff, visRect_inout);
            break;
         default:
            traversed = this.scrollLeftAndRight(visRect_inout);
         }
      }

      if (traversed) {
         this.repaint();
      }

      return traversed;
   }

   boolean isFocusable() {
      return this.itemCommands != null && this.itemCommands.length() >= 1;
   }

   int callGetLayout() {
      int l = this.layout & 255;
      int l_temp_vert = this.getVerticalTemporaryLayout();
      int horizontalDefaultLayout = UIStyle.isAlignedLeftToRight ? 1 : 2;
      switch(l) {
      case 0:
         l = l_temp_vert | horizontalDefaultLayout;
         break;
      case 1:
      case 2:
      case 3:
         l |= l_temp_vert;
         break;
      case 16:
      case 32:
      case 48:
         l |= horizontalDefaultLayout;
      }

      this.verticalLayout = l & 240;
      return l;
   }

   Command[] getExtraCommands() {
      Command[] ret = null;
      if (this.isButton() && this.buttonTruncated && UIStyle.MAX_BUTTON_WIDTH > 0) {
         ret = new Command[]{ChoiceGroup.VIEW};
      }

      return ret;
   }

   boolean launchExtraCommand(Command c) {
      if (c.equals(ChoiceGroup.VIEW) && this.isButton() && this.buttonTruncated && UIStyle.MAX_BUTTON_WIDTH > 0) {
         Display display = this.owner.myDisplay;
         TruncatedItemScreen tScreen = display.getTruncatedItemScreen();
         tScreen.showElement(display, this.owner, this.str, (Image)null, true);
         return true;
      } else {
         return false;
      }
   }

   void callKeyPressed(int keyCode, int keyDataIdx) {
      synchronized(Display.LCDUILock) {
         if (this.isButton() && this.buttonTruncated && UIStyle.MAX_BUTTON_WIDTH > 0 && keyCode == 35) {
            Display display = this.owner.myDisplay;
            TruncatedItemScreen tScreen = display.getTruncatedItemScreen();
            tScreen.showElement(display, this.owner, this.str, (Image)null, false);
         }

      }
   }

   boolean addCommandImpl(Command cmd) {
      boolean wasAdded = super.addCommandImpl(cmd);
      if (wasAdded && this.itemCommands.length() == 1 && this.appearanceMode == 0) {
         this.appearanceMode = this.originalAppearanceMode == 2 ? 2 : 1;
         this.checkTraverse();
         this.invalidate();
      }

      return wasAdded;
   }

   boolean removeCommandImpl(Command cmd) {
      boolean wasRemoved = super.removeCommandImpl(cmd);
      if (wasRemoved && this.itemCommands.length() < 1 && this.appearanceMode != 0) {
         this.appearanceMode = 0;
         this.checkTraverse();
         this.invalidate();
      }

      return wasRemoved;
   }

   void setOffsetWidth(int currentOffsetWidth) {
      this.currentOffsetWidth = currentOffsetWidth;
   }

   void setOffsetPosition(int offsetXPosition) {
      this.offsetXPosition = offsetXPosition;
   }

   void setOffsetHeight(int currentOffsetHeight) {
      this.currentOffsetHeight = currentOffsetHeight;
   }

   boolean boundsIncludeOtherItems(int rowH, int pW, int viewableWidth) {
      this.boundsIncludeOtherItems = false;
      if (this.itemIndex > 0 && !this.equateNLB() && !this.isButton() && this.getLabel() == null && this.lockedWidth == -1 && !this.shouldHShrink()) {
         Form myOwner = (Form)this.owner;
         int availableWidth = viewableWidth;
         int charWidth = this.font.charWidth(' ');
         StringItem prevStringItem = null;
         if (myOwner.items[this.itemIndex - 1] instanceof StringItem) {
            availableWidth = viewableWidth - (charWidth - Form.CELL_SPACING);
            prevStringItem = (StringItem)myOwner.items[this.itemIndex - 1];
         }

         int mW = this.callMinimumWidth();
         boolean mayIncludeItemInBounds = true;
         if (availableWidth >= mW && availableWidth < pW) {
            this.currentOffsetWidth = availableWidth;
            this.currentOffsetHeight = rowH;
         } else if (null != prevStringItem && !prevStringItem.equateNLA() && !prevStringItem.isButton() && prevStringItem.layout == this.layout && prevStringItem.bounds[2] - (charWidth + prevStringItem.lastLineWidth) < pW && !prevStringItem.appendedToANonStringItem) {
            if (prevStringItem.currentOffsetHeight > prevStringItem.offsetStringHeight && prevStringItem.textLinesEmpty) {
               this.currentOffsetWidth = prevStringItem.currentOffsetWidth;
               this.currentOffsetHeight = rowH - (prevStringItem.bounds[3] + Form.CELL_SPACING);
            } else if (mW <= prevStringItem.bounds[2] - (charWidth + prevStringItem.lastLineWidth)) {
               this.currentOffsetWidth = prevStringItem.bounds[2] - (charWidth + prevStringItem.lastLineWidth);
               this.currentOffsetHeight = prevStringItem.lineHeight;
            }
         } else {
            mayIncludeItemInBounds = false;
         }

         if (mayIncludeItemInBounds) {
            int pH = this.callPreferredHeight(-1);
            if (prevStringItem == null) {
               this.boundsIncludeOtherItems = pH > this.currentOffsetHeight;
            } else {
               this.boundsIncludeOtherItems = this.textLinesOffset.size() > 0 && !this.textLinesEmpty;
            }

            this.setToDefaults();
         }

         return this.boundsIncludeOtherItems;
      } else {
         return false;
      }
   }

   int getMinimumScroll(int dir) {
      int relativeY = ((Form)this.owner).oldViewY - this.bounds[1] - this.getLabelHeight(this.bounds[2]);
      relativeY = relativeY < 0 ? 0 : relativeY;
      if (this.lineHeight == 0) {
         this.lineHeight = this.minimumLineHeight = this.font.getHeight() + TextBreaker.DEFAULT_TEXT_LEADING;
      }

      int modulo;
      if (dir == 6) {
         modulo = (relativeY + this.owner.viewport[3]) % this.lineHeight;
      } else if (dir == 1) {
         modulo = relativeY % this.lineHeight;
         if (modulo != 0) {
            modulo = this.lineHeight - modulo;
         }
      } else {
         modulo = 0;
      }

      if (this.bounds[1] > ((Form)this.owner).oldViewY && this.bounds[1] + this.bounds[3] < ((Form)this.owner).oldViewY + this.owner.viewport[3]) {
         return this.pagePixel - modulo + this.lineHeight;
      } else {
         modulo = ((Form)this.owner).view[1] < this.bounds[1] + this.currentOffsetHeight - this.verticalOffsetStringTranslation ? (2 * this.lineHeight - modulo > Form.FORM_MAX_SCROLL ? this.lineHeight - modulo : 2 * this.lineHeight - modulo) : this.pagePixel - modulo + this.lineHeight;
         modulo = modulo < 0 ? modulo + this.lineHeight : modulo;
         return modulo <= 0 ? modulo + this.lineHeight : modulo;
      }
   }

   int getVerticalTemporaryLayout() {
      int hasNotVerticalLayoutMask = '／';
      int temL = this.layout | hasNotVerticalLayoutMask;
      if (this.owner == null) {
         return temL;
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

   private void defineStringItemTextLines(int widthForStringItem, int heightForStringItem, int offsetWidth, int heightOffset) {
      int tempLayout = this.getVerticalTemporaryLayout();
      boolean toDoAgain = this.repeatTextLinesDefinition(widthForStringItem, heightForStringItem, offsetWidth, heightOffset, tempLayout);
      if (toDoAgain) {
         this.newText = false;
         this.currentFont = this.font;
         if (this.strNLconsidered != null && !isStringEmpty(this.strNLconsidered)) {
            this.lineHeight = 0;
            this.currentWidth = widthForStringItem;
            this.currentHeight = heightForStringItem;
            this.currentOffsetWidth = offsetWidth;
            this.currentOffsetHeight = heightOffset;
            this.currentAppearanceMode = this.appearanceMode;
            this.layoutToConsider = tempLayout;
            TextLine tline = null;
            TextBreaker breaker = this.getInitializedTextBreakerAndInitializeTextLines();
            this.layoutToConsider = tempLayout;
            if (!this.boundsIncludeOtherItems && (this.currentOffsetWidth == 0 || this.currentOffsetHeight == 0)) {
               this.offsetStringHeight = 0;
               this.currentOffsetHeight = 0;
               this.verticalOffsetStringTranslation = 0;
            } else {
               this.defineOffsetTextLines((TextLine)tline, breaker, this.layoutToConsider, heightOffset, heightForStringItem);
            }

            this.defineNotOffsetTextLines((TextLine)tline, breaker, heightForStringItem);
            breaker.destroyBreaker();
            breaker = null;
            this.fieldsDefinition(this.lineHeight);
         } else {
            this.textLines.removeAllElements();
            this.textLinesOffset.removeAllElements();
         }
      }
   }

   private void setTextImpl(String itemString) {
      this.newText = true;
      this.str = itemString;
      this.buttonTruncated = false;
      if (itemString == null) {
         this.strNLconsidered = null;
         this.str = null;
      } else {
         int start = 0;
         int end = itemString.length();
         if ((this.label == null || this.label.length() == 0) && (this.layout & 256) != 256 && itemString != null && end > 0 && itemString.charAt(0) == '\n') {
            start = 1;
         }

         if (itemString != null && end > 1 && (this.layout & 512) != 512 && itemString.charAt(end - 1) == '\n') {
            --end;
         }

         if (end > start) {
            this.strNLconsidered = itemString.substring(start, end);
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

   private void setFontImpl(Font newFont) {
      boolean fontHasChanged = false;
      this.buttonTruncated = false;
      if (newFont != this.font || newFont == null) {
         fontHasChanged = true;
         if (newFont == null) {
            this.font = new Font(ITEM_VALUE_ZONE.getFont());
            this.buttonFont = null;
         } else {
            this.font = newFont;
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

      if (fontHasChanged) {
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

   private boolean scrollDown(int heightOff, int[] visRect_inout) {
      if (this.location == 3) {
         this.isFocusInStringItem = false;
         return false;
      } else {
         int modulo = (visRect_inout[1] + this.owner.viewport[3] - this.labelHeight) % this.lineHeight;
         if (modulo != 0) {
            visRect_inout[1] += this.lineHeight - modulo;
         }

         if (!this.isFocusInStringItem) {
            this.location = 4;
         }

         int i;
         if (!this.isFocusInStringItem && this.location == 4 && ((Form)this.owner).oldViewY < this.bounds[1]) {
            this.location = 1;
            this.isFocusInStringItem = true;
            visRect_inout[1] = 0;
         } else if (this.location != 1 && this.location != 4) {
            if (visRect_inout[1] + this.lineHeight < heightOff) {
               visRect_inout[1] += this.lineHeight;
            } else if (this.bounds[3] - (visRect_inout[1] + visRect_inout[3]) > this.pagePixel) {
               visRect_inout[1] += this.pagePixel;
               if (this.bounds[3] - visRect_inout[1] <= this.pagePixel) {
                  this.location = 3;
                  this.isFocusInStringItem = true;
               }
            } else {
               visRect_inout[1] = this.bounds[3] - this.owner.viewport[3];
               this.location = 3;
               this.isFocusInStringItem = true;
            }
         } else {
            this.location = 2;
            this.isFocusInStringItem = true;
            if (visRect_inout[1] < heightOff) {
               visRect_inout[1] += this.lineHeight;
            } else if (this.labelHeight <= 0) {
               visRect_inout[1] += this.pagePixel;
            } else {
               int i = true;

               for(i = 1; this.labelHeight + i * this.lineHeight < this.owner.viewport[3]; ++i) {
               }

               visRect_inout[1] += this.labelHeight + (i - 2) * this.lineHeight;
            }

            if (this.bounds[3] - visRect_inout[1] <= this.pagePixel) {
               this.location = 3;
               this.isFocusInStringItem = true;
            }
         }

         if (visRect_inout[1] >= this.bounds[3] - this.owner.viewport[3]) {
            visRect_inout[1] = this.bounds[3] - this.owner.viewport[3];
            visRect_inout[3] = this.owner.viewport[3];
            this.location = 3;
         }

         i = this.owner.viewport[3];
         visRect_inout[3] = i < this.bounds[3] - visRect_inout[1] ? i : this.bounds[3] - visRect_inout[1];
         return true;
      }
   }

   private boolean scrollUp(int heightOff, int[] visRect_inout) {
      if (this.location == 1) {
         this.isFocusInStringItem = false;
         return false;
      } else {
         int modulo = (visRect_inout[1] - this.labelHeight) % this.lineHeight;
         if (modulo != 0) {
            visRect_inout[1] -= modulo;
         }

         if (!this.isFocusInStringItem && ((Form)this.owner).oldViewY > this.bounds[1] + this.owner.viewport[3]) {
            this.location = 3;
         }

         if (this.location == 3) {
            if (!this.isFocusInStringItem && ((Form)this.owner).oldViewY > this.bounds[1] + this.bounds[3] - this.owner.viewport[3]) {
               visRect_inout[1] = this.bounds[3] - this.owner.viewport[3];
            } else {
               visRect_inout[1] -= this.pagePixel;
            }

            this.location = 2;
            this.isFocusInStringItem = true;
         } else if (visRect_inout[1] < heightOff) {
            if (visRect_inout[1] <= this.labelHeight + this.lineHeight) {
               visRect_inout[1] = 0;
               this.location = 1;
            } else {
               visRect_inout[1] -= this.lineHeight;
            }

            this.isFocusInStringItem = true;
         } else {
            this.isFocusInStringItem = true;
            if (visRect_inout[1] > this.owner.viewport[3]) {
               visRect_inout[1] -= this.pagePixel;
            } else {
               visRect_inout[1] = 0;
               this.location = 1;
            }
         }

         if (visRect_inout[1] <= 0) {
            visRect_inout[1] = 0;
            this.location = 1;
         }

         if (visRect_inout[1] >= this.bounds[3] - this.pagePixel) {
            visRect_inout[1] = this.bounds[3] - this.pagePixel;
            this.location = 3;
         }

         int toCompare = this.owner.viewport[3];
         visRect_inout[3] = toCompare < this.bounds[3] - visRect_inout[1] ? toCompare : this.bounds[3] - visRect_inout[1];
         return true;
      }
   }

   private boolean scrollLeftAndRight(int[] visRect_inout) {
      if (!this.isFocusInStringItem && (!((Form)this.owner).isTallRow || ((Form)this.owner).oldViewY > this.bounds[1] && ((Form)this.owner).oldViewY < this.bounds[1] + this.bounds[3])) {
         visRect_inout[1] = ((Form)this.owner).oldViewY - this.bounds[1];
         this.isFocusInStringItem = true;
         this.location = 2;
         if (visRect_inout[1] <= this.owner.viewport[3] - this.pagePixel - this.lineHeight) {
            visRect_inout[1] = 0;
            this.location = 1;
         } else if (visRect_inout[1] + this.owner.viewport[3] >= this.bounds[3]) {
            visRect_inout[1] = this.bounds[3] - this.owner.viewport[3];
            this.location = 3;
         }

         int toCompare = this.owner.viewport[3];
         visRect_inout[3] = toCompare < this.bounds[3] - visRect_inout[1] ? toCompare : this.bounds[3] - visRect_inout[1];
         return true;
      } else {
         this.isFocusInStringItem = false;
         return false;
      }
   }

   private void fieldsDefinition(int lineHeight) {
      this.labelHeight = this.getLabelHeight(this.lockedWidth);
      if (this.owner != null) {
         int i = false;

         int i;
         for(i = 0; lineHeight != 0 && i * lineHeight < this.owner.viewport[3]; ++i) {
         }

         this.pagePixel = (i - 2) * lineHeight;
      }

   }

   private boolean repeatTextLinesDefinition(int widthForStringItem, int heightForStringItem, int offsetWidth, int heightOffset, int tempLayout) {
      if (widthForStringItem == this.currentWidth && heightForStringItem == this.currentHeight && offsetWidth == this.currentOffsetWidth && heightOffset == this.currentOffsetHeight && this.appearanceMode == this.currentAppearanceMode && this.layoutToConsider == tempLayout && this.font == this.currentFont && !this.newText) {
         if (this.owner != null) {
            int i;
            for(i = 0; this.lineHeight != 0 && i * this.lineHeight < this.owner.viewport[3]; ++i) {
            }

            this.pagePixel = (i - 2) * this.lineHeight;
         }

         return false;
      } else {
         return true;
      }
   }

   private TextBreaker getInitializedTextBreakerAndInitializeTextLines() {
      TextBreaker breaker = TextBreaker.getBreaker();
      if (this.itemCommands.length() > 0 && this.appearanceMode == 1) {
         breaker.setFont(this.getPaintFont().getImpl());
      } else if (this.isButton()) {
         breaker.setFont(this.getButtonFont().getImpl());
      } else {
         breaker.setFont(this.font.getImpl());
      }

      breaker.setLeading(TextBreaker.DEFAULT_TEXT_LEADING, true);
      breaker.setText(this.strNLconsidered);
      if (this.isButton() && UIStyle.MAX_BUTTON_WIDTH > 0 && this.buttonTruncated) {
         breaker.setTruncation(true);
      } else {
         breaker.setTruncation(false);
      }

      breaker.enableWordWrapping(false);
      this.stringHeight = 0;
      this.textLines.removeAllElements();
      this.textLinesOffset.removeAllElements();
      this.offsetStringHeight = 0;
      return breaker;
   }

   private void defineOffsetTextLines(TextLine tline, TextBreaker breaker, int layoutToConsider, int heightOffset, int heightForStringItem) {
      this.offsetStringHeight = 0;
      int height = TextBreaker.DEFAULT_TEXT_LEADING + (this.appearanceMode == 1 ? this.paintFont.getHeight() : this.font.getHeight());

      do {
         if (this.lockedHeight != -1 && this.stringHeight + 2 * height > heightForStringItem) {
            breaker.setTruncation(true);
         }

         if ((tline = breaker.getTextLine(this.currentOffsetWidth)) == null) {
            break;
         }

         this.lineHeight = tline.getTextLineHeight();
         this.lastLineWidth = tline.getTextLineWidth();
         this.textLinesOffset.addElement(tline);
         this.offsetStringHeight += this.lineHeight;
      } while(this.offsetStringHeight < heightOffset);

      breaker.setTruncation(false);
      if (this.offsetStringHeight >= heightOffset) {
         this.verticalOffsetStringTranslation = 0;
         this.stringHeight = this.offsetStringHeight;
      } else {
         switch(layoutToConsider & 255 & 48) {
         case 16:
            this.verticalOffsetStringTranslation = 0;
            this.stringHeight = this.offsetStringHeight;
            break;
         case 32:
         default:
            this.verticalOffsetStringTranslation = this.currentOffsetHeight - this.offsetStringHeight;
            this.stringHeight = heightOffset - this.verticalOffsetStringTranslation;
            break;
         case 48:
            this.verticalOffsetStringTranslation = (heightOffset - this.offsetStringHeight) / 2;
            this.stringHeight = heightOffset - this.verticalOffsetStringTranslation;
         }
      }

      this.lastLineWidth += this.offsetXPosition;
      if (this.stringHeight < this.currentOffsetHeight) {
         this.stringHeight = heightOffset - this.verticalOffsetStringTranslation;
      }

   }

   private void defineNotOffsetTextLines(TextLine tline, TextBreaker breaker, int heightForStringItem) {
      int height;
      if (this.lineHeight <= 0) {
         height = this.appearanceMode == 1 ? this.paintFont.getHeight() : this.font.getHeight();
         height += TextBreaker.DEFAULT_TEXT_LEADING;
      } else {
         height = this.lineHeight;
      }

      if (this.lockedHeight != -1 && this.stringHeight + 2 * height > heightForStringItem) {
         breaker.setTruncation(true);
      }

      while((tline = breaker.getTextLine(this.currentWidth)) != null && (this.stringHeight + this.lineHeight <= heightForStringItem || heightForStringItem == -1)) {
         this.lineHeight = tline.getTextLineHeight();
         this.lastLineWidth = tline.getTextLineWidth();
         this.textLines.addElement(tline);
         this.stringHeight += this.lineHeight;
         this.textLinesEmpty = false;
         if (this.lockedHeight != -1 && this.stringHeight + 2 * this.lineHeight > heightForStringItem) {
            breaker.setTruncation(true);
         }
      }

      breaker.setTruncation(false);
   }
}
