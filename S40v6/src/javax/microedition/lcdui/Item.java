package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceControl;
import com.nokia.mid.impl.isa.ui.ReinitialiseListener;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import java.util.Vector;

public abstract class Item {
   public static final int LAYOUT_DEFAULT = 0;
   public static final int LAYOUT_LEFT = 1;
   public static final int LAYOUT_RIGHT = 2;
   public static final int LAYOUT_CENTER = 3;
   public static final int LAYOUT_TOP = 16;
   public static final int LAYOUT_BOTTOM = 32;
   public static final int LAYOUT_VCENTER = 48;
   public static final int LAYOUT_NEWLINE_BEFORE = 256;
   public static final int LAYOUT_NEWLINE_AFTER = 512;
   public static final int LAYOUT_SHRINK = 1024;
   public static final int LAYOUT_EXPAND = 2048;
   public static final int LAYOUT_VSHRINK = 4096;
   public static final int LAYOUT_VEXPAND = 8192;
   public static final int LAYOUT_2 = 16384;
   public static final int PLAIN = 0;
   public static final int HYPERLINK = 1;
   public static final int BUTTON = 2;
   static final int X = 0;
   static final int Y = 1;
   static final int WIDTH = 2;
   static final int HEIGHT = 3;
   static final int BUTTON_BORDER_WIDTH;
   static final int BUTTON_BORDER_HEIGHT;
   static final int DEFAULT_WIDTH;
   static final int EDITABLE_TEXT_BOTTOM_MARGIN;
   static final int VALID_LAYOUT;
   private static final char[] POSSIBLE_ITEM_STRING_SEPARATORS;
   static final int GET_VERTICAL_LEYOUT_MASK = 240;
   static final int GET_VERTICAL_AND_HORIZONTAL_LAYOUT_MASK = 255;
   static final Zone ITEM_VALUE_ZONE;
   static final Font DEFAULT_ITEM_FONT;
   static String ellipsisString;
   static int MIN_LABEL_WIDTH;
   int[] bounds;
   boolean hasFocus;
   boolean visible;
   boolean sizeChanged;
   ItemCommandListener commandListener;
   String label;
   Screen owner;
   int layout = 0;
   Command defaultCommand;
   int lockedWidth = -1;
   int lockedHeight = -1;
   int originalLockedHeight = -1;
   int lineY = -1;
   int rowHeight;
   int itemIndex = -1;
   int emptyStringWidth;
   int contentWidth;
   boolean hasOffsetTextLines = false;
   int verticalLayout;
   CommandVector itemCommands = new CommandVector();
   private Font emptyStringFont = Font.getDefaultFont();
   private Vector emptyStringTextLines;
   Item.Label itemLabel;
   boolean stateChanging;

   Item(String label) {
      Item.Label localLabel = !isStringEmpty(label) ? new Item.Label(label) : null;
      synchronized(Display.LCDUILock) {
         this.label = label;
         this.itemLabel = localLabel;
      }
   }

   public void setLabel(String label) {
      Item.Label localLabel = !isStringEmpty(label) ? new Item.Label(label) : null;
      synchronized(Display.LCDUILock) {
         if (this.owner != null && this.owner instanceof Alert) {
            throw new IllegalStateException();
         }

         this.label = label;
         this.itemLabel = localLabel;
      }

      this.invalidate();
   }

   public String getLabel() {
      return this.label;
   }

   public int getLayout() {
      return this.layout;
   }

   public void setLayout(int layout) {
      synchronized(Display.LCDUILock) {
         if (this.owner != null && this.owner instanceof Alert) {
            throw new IllegalStateException();
         } else {
            this.setLayoutImpl(layout);
            this.invalidate();
         }
      }
   }

   public void addCommand(Command cmd) {
      if (cmd == null) {
         throw new NullPointerException();
      } else {
         boolean appearanceChanged;
         synchronized(Display.LCDUILock) {
            if (this.owner != null && this.owner instanceof Alert) {
               throw new IllegalStateException();
            }

            appearanceChanged = this.addCommandImpl(cmd) && this.canItemAppearanceChange() && this.itemCommands.length() == 1;
         }

         if (appearanceChanged) {
            this.setPreferredSize(this.lockedWidth, this.lockedHeight);
         }

      }
   }

   public void removeCommand(Command cmd) {
      if (cmd != null) {
         synchronized(Display.LCDUILock) {
            if (cmd.equals(this.defaultCommand)) {
               this.defaultCommand = null;
            }

            this.removeCommandImpl(cmd);
         }
      }
   }

   public void setItemCommandListener(ItemCommandListener l) {
      synchronized(Display.LCDUILock) {
         if (this.owner != null && this.owner instanceof Alert) {
            throw new IllegalStateException();
         } else {
            this.commandListener = l;
         }
      }
   }

   public int getPreferredWidth() {
      int width = false;
      int height = false;
      int width;
      int height;
      synchronized(Display.LCDUILock) {
         width = this.lockedWidth;
         height = this.lockedHeight;
      }

      return width != -1 ? width : this.callItemLabelPreferredWidth(height);
   }

   public int getPreferredHeight() {
      int width = false;
      int height = false;
      int labelHeight = false;
      int width;
      int height;
      int labelHeight;
      synchronized(Display.LCDUILock) {
         width = this.lockedWidth;
         height = this.lockedHeight;
         labelHeight = this.getLabelHeight(-1);
      }

      return height != -1 ? height : this.callPreferredHeight(width) + labelHeight;
   }

   public void setPreferredSize(int width, int height) {
      if (width >= -1 && height >= -1) {
         int minWidth = this.callMinimumWidth();
         int minHeight = this.callMinimumHeight();
         synchronized(Display.LCDUILock) {
            if (this.owner != null && this.owner instanceof Alert) {
               throw new IllegalStateException();
            } else {
               this.lockedWidth = width != -1 && width < minWidth ? minWidth : width;
               this.lockedWidth = this.lockedWidth > DEFAULT_WIDTH ? DEFAULT_WIDTH : this.lockedWidth;
               if (this.itemLabel != null && (this.lockedWidth < this.getLabelWidth() || this.getLabelWidth() != DEFAULT_WIDTH)) {
                  this.itemLabel.defineLabelTextLines(this.lockedWidth);
               }

               int labelH = this.getLabelHeight(this.lockedWidth);
               this.lockedHeight = height != -1 && height < minHeight + labelH ? minHeight + labelH : height;
               this.originalLockedHeight = this.lockedHeight;
               this.invalidate();
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getMinimumWidth() {
      return this.callMinimumWidth();
   }

   public int getMinimumHeight() {
      int minimumHeight = this.callMinimumHeight();
      synchronized(Display.LCDUILock) {
         return this.lockedHeight != -1 ? this.lockedHeight : minimumHeight + this.getLabelHeight(-1);
      }
   }

   public void setDefaultCommand(Command cmd) {
      boolean appearanceChanged;
      synchronized(Display.LCDUILock) {
         appearanceChanged = this.setDefaultCommandImpl(cmd) && this.canItemAppearanceChange() && this.itemCommands.length() == 1;
      }

      if (appearanceChanged) {
         this.setPreferredSize(this.lockedWidth, this.lockedHeight);
      }

   }

   public void notifyStateChanged() {
      synchronized(Display.LCDUILock) {
         if (this.owner != null && this.owner instanceof Form) {
            this.owner.changedItemState(this);
         } else {
            throw new IllegalStateException();
         }
      }
   }

   boolean midletCommandsSupported() {
      return true;
   }

   boolean isFocusable() {
      return false;
   }

   int getMinimumScroll(int dir) {
      return Form.FORM_MAX_SCROLL;
   }

   void paintEmptyString(Graphics g, int x, int y, int width, int height, boolean hasFocus) {
      if (this.itemCommands != null && this.itemCommands.length() > 0) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
         ColorCtrl colorCtrl = ng.getColorCtrl();
         int oldFgColor = colorCtrl.getFgColor();
         int deltaY = height - this.emptyStringFont.getHeight();
         if (hasFocus) {
            UIStyle.getUIStyle().drawHighlightBar(ng, x, y, width + 1, height, false);
            colorCtrl.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
         } else {
            colorCtrl.setFgColor(UIStyle.COLOUR_TEXT);
         }

         ng.drawTextInArea(x, y + deltaY / 2 + 1, width, height - deltaY, this.emptyStringTextLines, 2);
         colorCtrl.setFgColor(oldFgColor);
      }
   }

   int getEmptyStringHeight(int width, Font font) {
      int h = 0;
      this.emptyStringTextLines = new Vector();
      this.emptyStringFont = font == null ? Font.getDefaultFont() : font;
      TextBreaker breaker = TextBreaker.getBreaker(this.emptyStringFont.getImpl(), TextDatabase.getText(33), false);
      breaker.setLeading(0);

      TextLine tLine;
      while((tLine = breaker.getTextLine(width)) != null) {
         h += tLine.getTextLineHeight();
         this.emptyStringTextLines.addElement(tLine);
      }

      breaker.destroyBreaker();
      return h + 1;
   }

   int getEmptyStringWidth(Font font) {
      this.emptyStringFont = font == null ? Font.getDefaultFont() : font;
      String emptyString = TextDatabase.getText(33);
      this.emptyStringWidth = this.emptyStringFont.charsWidth(emptyString.toCharArray(), 0, emptyString.length());
      this.emptyStringWidth = this.emptyStringWidth > DEFAULT_WIDTH ? DEFAULT_WIDTH : this.emptyStringWidth;
      return this.emptyStringWidth;
   }

   int getLabelHeight(int w) {
      return this.itemLabel == null ? 0 : this.itemLabel.labelHeight + UIStyle.LABEL_PAD_BEFORE;
   }

   int getLabelWidth() {
      return this.itemLabel == null ? 0 : this.itemLabel.labelWidth;
   }

   abstract int callPreferredWidth(int var1);

   abstract int callPreferredHeight(int var1);

   abstract int callMinimumWidth();

   abstract int callMinimumHeight();

   final int callItemLabelPreferredWidth(int height) {
      return this.itemLabel == null ? this.callPreferredWidth(height) : Math.max(this.callPreferredWidth(height), this.getLabelWidth());
   }

   final int callItemLabelMinimumWidth() {
      return this.itemLabel == null ? this.callMinimumWidth() : Math.max(this.callMinimumWidth(), this.getLabelWidth());
   }

   int callHighlightedWidth() {
      return this.bounds[2];
   }

   int callHighlightedX() {
      return this.bounds[0];
   }

   boolean shouldHShrink() {
      return (this.layout & 1024) == 1024;
   }

   boolean shouldHExpand() {
      return (this.layout & 2048) == 2048;
   }

   boolean shouldVShrink() {
      return (this.layout & 4096) == 4096;
   }

   boolean shouldVExpand() {
      return (this.layout & 8192) == 8192;
   }

   boolean equateNLA() {
      return (this.layout & 512) == 512;
   }

   boolean equateNLB() {
      if (this.label != null && this.label.length() > 0 && this.label.charAt(0) == '\n') {
         return true;
      } else {
         return (this.layout & 256) == 256;
      }
   }

   boolean shouldSkipTraverse() {
      return false;
   }

   void callPaint(Graphics g, int w, int h, boolean isFocused) {
      this.paintLabel(g, w);
   }

   void repaint() {
      if (this.bounds != null) {
         this.repaint(0, 0, this.bounds[2], this.bounds[3]);
      }

   }

   void repaint(int x, int y, int w, int h) {
      if (this.owner != null) {
         if (x < 0) {
            x = 0;
         } else if (x > this.bounds[2]) {
            return;
         }

         if (y < 0) {
            y = 0;
         } else if (y > this.bounds[3]) {
            return;
         }

         if (w < 0) {
            w = 0;
         } else if (w > this.bounds[2]) {
            w = this.bounds[2];
         }

         if (h < 0) {
            h = 0;
         } else if (h > this.bounds[3]) {
            h = this.bounds[3];
         }

         ((Form)this.owner).repaintItem(this, x, y, w, h);
      }
   }

   void paintLabel(Graphics g, int width) {
      synchronized(Display.LCDUILock) {
         if (this.itemLabel != null) {
            this.itemLabel.paintLabel(g);
         }

      }
   }

   void callSizeChanged(int w, int h) {
      synchronized(Display.LCDUILock) {
         if (this.itemLabel != null) {
            this.itemLabel.defineLabelTextLines(w);
         }

      }
   }

   void invalidate() {
      synchronized(Display.LCDUILock) {
         if (this.owner != null) {
            this.owner.invalidate();
         }

      }
   }

   boolean callTraverse(int dir, int viewportWidth, int viewportHeight, int[] visRect_inout) {
      if (!this.hasFocus) {
         this.owner.updateSoftkeys(true);
      }

      this.hasFocus = true;
      if (this.hasKeymatLight()) {
         DeviceControl.setKeymatLightsMode(1);
      }

      return false;
   }

   void callTraverseOut() {
      this.hasFocus = false;
      if (this.hasKeymatLight()) {
         DeviceControl.setKeymatLightsMode(0);
      }

   }

   void callKeyPressed(int keyCode, int keyDataIdx) {
      boolean executeDefaultCommand = false;
      synchronized(Display.LCDUILock) {
         if (keyCode == -10 && this.defaultCommand != null && this.commandListener != null) {
            executeDefaultCommand = true;
         }
      }

      if (executeDefaultCommand) {
         synchronized(Display.calloutLock) {
            this.commandListener.commandAction(this.defaultCommand, this);
         }
      }

   }

   void callKeyTyped(char c) {
   }

   void callKeyReleased(int keyCode, int keyDataIdx) {
   }

   void callKeyRepeated(int keyCode, int keyDataIdx) {
   }

   void callShowNotify() {
      this.visible = true;
      if (this.hasKeymatLight()) {
         DeviceControl.setKeymatLightsMode(1);
      }

   }

   void callHideNotify() {
      this.visible = false;
      if (this.hasKeymatLight()) {
         DeviceControl.setKeymatLightsMode(0);
      }

   }

   boolean addCommandImpl(Command cmd) {
      boolean wasAdded = this.itemCommands.addItemCommand(cmd);
      if (wasAdded && this.owner != null && this.hasFocus && this.isFocusable()) {
         this.owner.updateSoftkeys(true);
      }

      return wasAdded;
   }

   boolean setDefaultCommandImpl(Command cmd) {
      boolean wasAdded = false;
      if (this.owner != null && this.owner instanceof Alert) {
         throw new IllegalStateException();
      } else if (this.defaultCommand == cmd) {
         return wasAdded;
      } else {
         this.defaultCommand = cmd;
         if (cmd != null) {
            wasAdded = this.addCommandImpl(cmd);
         }

         if (!wasAdded && this.owner != null) {
            this.owner.updateSoftkeys(true);
         }

         return wasAdded;
      }
   }

   boolean removeCommandImpl(Command cmd) {
      boolean wasRemoved = this.itemCommands.removeCommand(cmd);
      if (wasRemoved && this.owner != null && this.hasFocus && this.isFocusable()) {
         this.owner.updateSoftkeys(true);
      }

      return wasRemoved;
   }

   Command[] getExtraCommands() {
      return null;
   }

   boolean launchExtraCommand(Command c) {
      return false;
   }

   ItemCommandListener getItemCommandListener() {
      return this.commandListener;
   }

   void setOwner(Screen owner) {
      synchronized(Display.LCDUILock) {
         if (this.owner != null && owner != null) {
            throw new IllegalStateException();
         } else {
            this.owner = owner;
         }
      }
   }

   void setLayoutImpl(int layout) {
      if ((layout & ~VALID_LAYOUT) != 0) {
         throw new IllegalArgumentException();
      } else {
         this.layout = layout;
         this.verticalLayout = layout & 240;
      }
   }

   int callGetLayout() {
      int l = this.layout & 255;
      int horizontalDefaultLayout = UIStyle.isAlignedLeftToRight ? 1 : 2;
      switch(l) {
      case 0:
         l = 16 | horizontalDefaultLayout;
         break;
      case 1:
      case 2:
      case 3:
         l |= 16;
         break;
      case 16:
      case 32:
      case 48:
         l |= horizontalDefaultLayout;
      }

      this.verticalLayout = l & 240;
      return l;
   }

   boolean hasOffsetTextLines(int rowHeight, int pW, int viewableWidth) {
      return this.hasOffsetTextLines;
   }

   static boolean isStringEmpty(String string) {
      if (string == null) {
         return true;
      } else {
         for(int i = 0; i < string.length(); ++i) {
            if (string.charAt(i) != ' ') {
               return false;
            }
         }

         return true;
      }
   }

   boolean supportHorizontalScrolling() {
      return false;
   }

   boolean supportsInternalTraversal() {
      return false;
   }

   void processTextEditorEvent(int eventType, int eventParam) {
   }

   static String getStringWithSeparator(String string) {
      String txt = null;
      if (string != null) {
         String trimmed_string = string.trim();
         int last = trimmed_string.length() - 1;
         if (trimmed_string.length() == 0) {
            return null;
         }

         int stringSeparatorInsertionIndex = -1;
         boolean replaceStringSeparator = false;

         for(int labSepIdx = 0; labSepIdx < POSSIBLE_ITEM_STRING_SEPARATORS.length; ++labSepIdx) {
            char separator = POSSIBLE_ITEM_STRING_SEPARATORS[labSepIdx];
            stringSeparatorInsertionIndex = trimmed_string.lastIndexOf(separator);
            if (last == stringSeparatorInsertionIndex) {
               stringSeparatorInsertionIndex = string.lastIndexOf(separator);
               replaceStringSeparator = true;
               break;
            }

            stringSeparatorInsertionIndex = -1;
         }

         if (-1 == stringSeparatorInsertionIndex) {
            stringSeparatorInsertionIndex = string.lastIndexOf(trimmed_string.charAt(last)) + 1;
         }

         char stringSeparator = TextDatabase.getText(36).charAt(0);
         StringBuffer stringStringBuffer = new StringBuffer(string);
         if (replaceStringSeparator) {
            stringStringBuffer.setCharAt(stringSeparatorInsertionIndex, stringSeparator);
         } else {
            stringStringBuffer.insert(stringSeparatorInsertionIndex, stringSeparator);
         }

         txt = stringStringBuffer.toString();
      }

      return txt;
   }

   boolean canItemAppearanceChange() {
      return false;
   }

   boolean isVisibleFrom(int viewY, int viewHeight) {
      if (this.bounds == null) {
         return false;
      } else {
         boolean tempIsVisibleFrom = false;
         if (viewY <= this.bounds[1] && viewY + viewHeight >= this.bounds[1]) {
            tempIsVisibleFrom = true;
         } else if (viewY <= this.bounds[1] + this.bounds[3] && viewY + viewHeight >= this.bounds[1] + this.bounds[3]) {
            tempIsVisibleFrom = true;
         } else if (this.bounds[3] > viewHeight && this.bounds[1] < viewY && this.bounds[1] + this.bounds[3] > viewY + viewHeight) {
            tempIsVisibleFrom = true;
         }

         return tempIsVisibleFrom;
      }
   }

   boolean hasKeymatLight() {
      return false;
   }

   static {
      BUTTON_BORDER_WIDTH = UIStyle.BUTTON_BORDER_WIDTH;
      BUTTON_BORDER_HEIGHT = UIStyle.BUTTON_BORDER_HEIGHT;
      DEFAULT_WIDTH = Displayable.screenNormMainZone.width;
      EDITABLE_TEXT_BOTTOM_MARGIN = UIStyle.EDITABLE_TEXT_BOTTOM_MARGIN;
      POSSIBLE_ITEM_STRING_SEPARATORS = new char[]{':', '：'};
      ITEM_VALUE_ZONE = Displayable.uistyle.getZone(20);
      DEFAULT_ITEM_FONT = new Font(ITEM_VALUE_ZONE.getFont());
      ellipsisString = TextDatabase.getText(34);
      MIN_LABEL_WIDTH = Font.getFont(0, 1, 0).charsWidth(ellipsisString.toCharArray(), 0, ellipsisString.length());
      VALID_LAYOUT = 32563;
      UIStyle.registerReinitialiseListener(new ReinitialiseListener() {
         public void reinitialiseForForeground() {
            Item.ellipsisString = TextDatabase.getText(34);
            Item.MIN_LABEL_WIDTH = Font.getFont(0, 1, 0).charsWidth(Item.ellipsisString.toCharArray(), 0, Item.ellipsisString.length());
         }
      });
   }

   class Label {
      private Vector textLines = new Vector();
      int labelWidth;
      private int labelHeight;
      private String labelStrNL;
      static final int LABEL_MAX_SIZE = 2;

      Label(String labelItem) {
         Item.this.label = labelItem;
         this.setLabelString(Item.this.label);
      }

      void setLabelString(String stringLabel) {
         int minimumHeight = Item.this.callMinimumHeight();
         synchronized(Display.LCDUILock) {
            this.labelHeight = 0;
            this.labelWidth = 0;
            this.labelStrNL = stringLabel.charAt(0) == '\n' ? stringLabel.substring(1) : stringLabel;
            Item.this.label = stringLabel;
            int width;
            if (Item.this.lockedWidth == -1) {
               width = Item.DEFAULT_WIDTH;
            } else {
               if (Item.this.lockedWidth < Item.MIN_LABEL_WIDTH) {
                  Item.this.lockedWidth = Item.MIN_LABEL_WIDTH;
               }

               width = Item.this.lockedWidth;
            }

            this.defineLabelTextLines(width);
            int newMinHeight = this.labelHeight + minimumHeight;
            Item.this.lockedHeight = Item.this.originalLockedHeight != -1 && Item.this.originalLockedHeight < newMinHeight ? newMinHeight : Item.this.originalLockedHeight;
         }
      }

      void defineLabelTextLines(int widthForLabel) {
         String text = Item.getStringWithSeparator(this.labelStrNL);
         this.textLines.removeAllElements();
         TextLine tline = null;
         TextBreaker breaker = TextBreaker.getBreaker();
         breaker.setFont(Displayable.uistyle.getZone(19).getFont());
         breaker.setLeading(TextBreaker.DEFAULT_TEXT_LEADING, false);
         breaker.setText(text);
         breaker.setTruncation(false);

         while((tline = breaker.getTextLine(widthForLabel)) != null && this.textLines.size() < 2) {
            this.textLines.addElement(tline);
            if (this.textLines.size() == 1) {
               breaker.setTruncation(true);
            }
         }

         breaker.destroyBreaker();
         this.computeLabelHeightAndWidth(widthForLabel);
      }

      void computeLabelHeightAndWidth(int width) {
         this.labelWidth = 0;
         this.labelHeight = 0;
         if (this.textLines != null && this.textLines.size() != 0) {
            TextLine tLine = (TextLine)this.textLines.firstElement();
            this.labelWidth = this.textLines.size() == 1 ? tLine.getTextLineWidth() : width;
            tLine = (TextLine)this.textLines.lastElement();
            int textLineHeight = tLine.getTextLineHeight();
            this.labelHeight = textLineHeight * this.textLines.size() + UIStyle.LABEL_PAD_AFTER;
         }
      }

      int paintLabel(Graphics g) {
         if (Item.isStringEmpty(this.labelStrNL)) {
            return -1;
         } else {
            int tX = g.getTranslateX();
            int tY = g.getTranslateY();
            com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
            ColorCtrl colorCtrl = ng.getColorCtrl();
            int oldFgColor = colorCtrl.getFgColor();
            colorCtrl.setFgColor(UIStyle.COLOUR_LABEL_TEXT);
            ng.drawTextInArea(UIStyle.isAlignedLeftToRight ? tX : tX + Item.this.bounds[2] - this.labelWidth, tY + UIStyle.LABEL_PAD_BEFORE, this.labelWidth, this.labelHeight, this.textLines, UIStyle.isAlignedLeftToRight ? 1 : 3);
            g.translate(0, Item.this.getLabelHeight(-1));
            colorCtrl.setFgColor(oldFgColor);
            return 0;
         }
      }
   }
}
