package javax.microedition.lcdui;

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
   static final String EMPTY_STRING;
   static final int DEFAULT_WIDTH;
   static final int EDITABLE_TEXT_BOTTOM_MARGIN;
   static final int VALID_LAYOUT;
   private static final char ITEM_STRING_SEPARATOR;
   private static final char[] POSSIBLE_ITEM_STRING_SEPARATORS;
   static final int GET_VERTICAL_LEYOUT_MASK = 240;
   static final int GET_VERTICAL_AND_HORIZONTAL_LAYOUT_MASK = 255;
   static final Zone ITEM_VALUE_ZONE;
   static final Font DEFAULT_ITEM_FONT;
   static final int MAX_LABEL_HEIGHT;
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
   boolean boundsIncludeOtherItems = false;
   int verticalLayout;
   CommandVector itemCommands = new CommandVector();
   private Font emptyStringFont = Font.getDefaultFont();
   private Vector emptyStringTextLines;
   Item.Label itemLabel;
   boolean stateChanging;

   Item(String var1) {
      Item.Label var2 = !isStringEmpty(var1) ? new Item.Label(var1) : null;
      synchronized(Display.LCDUILock) {
         this.label = var1;
         this.itemLabel = var2;
      }
   }

   public void setLabel(String var1) {
      Item.Label var2 = !isStringEmpty(var1) ? new Item.Label(var1) : null;
      synchronized(Display.LCDUILock) {
         if (this.owner != null && this.owner instanceof Alert) {
            throw new IllegalStateException();
         }

         this.label = var1;
         this.itemLabel = var2;
      }

      this.invalidate();
   }

   public String getLabel() {
      return this.label;
   }

   public int getLayout() {
      return this.layout;
   }

   public void setLayout(int var1) {
      synchronized(Display.LCDUILock) {
         if (this.owner != null && this.owner instanceof Alert) {
            throw new IllegalStateException();
         } else {
            this.setLayoutImpl(var1);
            this.invalidate();
         }
      }
   }

   public void addCommand(Command var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         boolean var2;
         synchronized(Display.LCDUILock) {
            if (this.owner != null && this.owner instanceof Alert) {
               throw new IllegalStateException();
            }

            var2 = this.addCommandImpl(var1) && this.canItemAppearanceChange() && this.itemCommands.length() == 1;
         }

         if (var2) {
            this.setPreferredSize(this.lockedWidth, this.lockedHeight);
         }

      }
   }

   public void removeCommand(Command var1) {
      if (var1 != null) {
         synchronized(Display.LCDUILock) {
            if (var1.equals(this.defaultCommand)) {
               this.defaultCommand = null;
            }

            this.removeCommandImpl(var1);
         }
      }
   }

   public void setItemCommandListener(ItemCommandListener var1) {
      synchronized(Display.LCDUILock) {
         if (this.owner != null && this.owner instanceof Alert) {
            throw new IllegalStateException();
         } else {
            this.commandListener = var1;
         }
      }
   }

   public int getPreferredWidth() {
      boolean var1 = false;
      boolean var2 = false;
      int var6;
      int var7;
      synchronized(Display.LCDUILock) {
         var6 = this.lockedWidth;
         var7 = this.lockedHeight;
      }

      return var6 != -1 ? var6 : this.callItemLabelPreferredWidth(var7);
   }

   public int getPreferredHeight() {
      boolean var1 = false;
      boolean var2 = false;
      boolean var3 = false;
      int var7;
      int var8;
      int var9;
      synchronized(Display.LCDUILock) {
         var7 = this.lockedWidth;
         var8 = this.lockedHeight;
         var9 = this.getLabelHeight(-1);
      }

      return var8 != -1 ? var8 : this.callPreferredHeight(var7) + var9;
   }

   public void setPreferredSize(int var1, int var2) {
      if (var1 >= -1 && var2 >= -1) {
         int var3 = this.callMinimumWidth();
         int var4 = this.callMinimumHeight();
         synchronized(Display.LCDUILock) {
            if (this.owner != null && this.owner instanceof Alert) {
               throw new IllegalStateException();
            } else {
               this.lockedWidth = var1 != -1 && var1 < var3 ? var3 : var1;
               this.lockedWidth = this.lockedWidth > DEFAULT_WIDTH ? DEFAULT_WIDTH : this.lockedWidth;
               if (this.itemLabel != null && (this.lockedWidth < this.getLabelWidth() || this.getLabelWidth() != DEFAULT_WIDTH)) {
                  this.itemLabel.defineLabelTextLines(this.lockedWidth);
               }

               int var6 = this.getLabelHeight(this.lockedWidth);
               this.lockedHeight = var2 != -1 && var2 < var4 + var6 ? var4 + var6 : var2;
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
      int var1 = this.callMinimumHeight();
      synchronized(Display.LCDUILock) {
         return this.lockedHeight != -1 ? this.lockedHeight : var1 + this.getLabelHeight(-1);
      }
   }

   public void setDefaultCommand(Command var1) {
      boolean var2;
      synchronized(Display.LCDUILock) {
         var2 = this.setDefaultCommandImpl(var1) && this.canItemAppearanceChange() && this.itemCommands.length() == 1;
      }

      if (var2) {
         this.setPreferredSize(this.lockedWidth, this.lockedHeight);
      }

   }

   public void notifyStateChanged() {
      synchronized(Display.LCDUILock) {
         Screen var2 = this.owner;
         if (var2 != null && var2 instanceof Form) {
            var2.changedItemState(this);
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

   int getMinimumScroll(int var1) {
      return Form.FORM_MAX_SCROLL;
   }

   void paintEmptyString(Graphics var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (this.itemCommands != null && this.itemCommands.length() > 0) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics var7 = var1.getImpl();
         ColorCtrl var8 = var7.getColorCtrl();
         int var9 = var8.getFgColor();
         int var10 = var5 - this.emptyStringFont.getHeight();
         if (var6) {
            UIStyle.getUIStyle().drawHighlightBar(var7, var2, var3, var4 + 1, var5, false);
            var8.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
         } else {
            var8.setFgColor(UIStyle.COLOUR_TEXT);
         }

         var7.drawTextInArea(var2, var3 + var10 / 2 + 1, var4, var5 - var10, this.emptyStringTextLines, 2);
         var8.setFgColor(var9);
      }
   }

   int getEmptyStringHeight(int var1, Font var2) {
      int var4 = 0;
      this.emptyStringTextLines = new Vector();
      this.emptyStringFont = var2 == null ? Font.getDefaultFont() : var2;
      TextBreaker var5 = TextBreaker.getBreaker(this.emptyStringFont.getImpl(), EMPTY_STRING, false);
      var5.setLeading(0);

      TextLine var3;
      while((var3 = var5.getTextLine(var1)) != null) {
         var4 += var3.getTextLineHeight();
         this.emptyStringTextLines.addElement(var3);
      }

      var5.destroyBreaker();
      return var4 + 1;
   }

   int getEmptyStringWidth(Font var1) {
      this.emptyStringFont = var1 == null ? Font.getDefaultFont() : var1;
      this.emptyStringWidth = this.emptyStringFont.charsWidth(EMPTY_STRING.toCharArray(), 0, EMPTY_STRING.length());
      this.emptyStringWidth = this.emptyStringWidth > DEFAULT_WIDTH ? DEFAULT_WIDTH : this.emptyStringWidth;
      return this.emptyStringWidth;
   }

   int getLabelHeight(int var1) {
      return this.itemLabel == null ? 0 : this.itemLabel.getLabelHeight();
   }

   int getLabelWidth() {
      return this.itemLabel == null ? 0 : this.itemLabel.labelWidth;
   }

   abstract int callPreferredWidth(int var1);

   abstract int callPreferredHeight(int var1);

   abstract int callMinimumWidth();

   abstract int callMinimumHeight();

   final int callItemLabelPreferredWidth(int var1) {
      return this.itemLabel == null ? this.callPreferredWidth(var1) : Math.max(this.callPreferredWidth(var1), this.getLabelWidth());
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

   void callPaint(Graphics var1, int var2, int var3, boolean var4) {
      this.paintLabel(var1, var2);
   }

   void repaint() {
      if (this.bounds != null) {
         this.repaint(0, 0, this.bounds[2], this.bounds[3]);
      }

   }

   void repaint(int var1, int var2, int var3, int var4) {
      if (this.owner != null) {
         if (var1 < 0) {
            var1 = 0;
         } else if (var1 > this.bounds[2]) {
            return;
         }

         if (var2 < 0) {
            var2 = 0;
         } else if (var2 > this.bounds[3]) {
            return;
         }

         if (var3 < 0) {
            var3 = 0;
         } else if (var3 > this.bounds[2]) {
            var3 = this.bounds[2];
         }

         if (var4 < 0) {
            var4 = 0;
         } else if (var4 > this.bounds[3]) {
            var4 = this.bounds[3];
         }

         ((Form)this.owner).repaintItem(this, var1, var2, var3, var4);
      }
   }

   void paintLabel(Graphics var1, int var2) {
      synchronized(Display.LCDUILock) {
         if (this.itemLabel != null) {
            this.itemLabel.paintLabel(var1);
         }

      }
   }

   void callSizeChanged(int var1, int var2) {
      synchronized(Display.LCDUILock) {
         if (this.itemLabel != null) {
            this.itemLabel.defineLabelTextLines(var1);
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

   boolean callTraverse(int var1, int var2, int var3, int[] var4) {
      this.hasFocus = true;
      return false;
   }

   void callTraverseOut() {
      this.hasFocus = false;
   }

   void callKeyPressed(int var1, int var2) {
      boolean var3 = false;
      synchronized(Display.LCDUILock) {
         if (var1 == -10 && this.defaultCommand != null && this.commandListener != null) {
            var3 = true;
         }
      }

      if (var3) {
         synchronized(Display.calloutLock) {
            this.commandListener.commandAction(this.defaultCommand, this);
         }
      }

   }

   void callKeyTyped(char var1) {
   }

   void callKeyReleased(int var1, int var2) {
   }

   void callKeyRepeated(int var1, int var2) {
   }

   void callShowNotify() {
      this.visible = true;
   }

   void callHideNotify() {
      this.visible = false;
   }

   boolean addCommandImpl(Command var1) {
      boolean var2 = this.itemCommands.addItemCommand(var1);
      if (var2 && this.owner != null && this.hasFocus && this.isFocusable()) {
         this.owner.updateSoftkeys(true);
      }

      return var2;
   }

   boolean setDefaultCommandImpl(Command var1) {
      boolean var2 = false;
      if (this.owner != null && this.owner instanceof Alert) {
         throw new IllegalStateException();
      } else if (this.defaultCommand == var1) {
         return var2;
      } else {
         this.defaultCommand = var1;
         if (var1 != null) {
            var2 = this.addCommandImpl(var1);
         }

         if (!var2 && this.owner != null) {
            this.owner.updateSoftkeys(true);
         }

         return var2;
      }
   }

   boolean removeCommandImpl(Command var1) {
      boolean var2 = this.itemCommands.removeCommand(var1);
      if (var2 && this.owner != null && this.hasFocus && this.isFocusable()) {
         this.owner.updateSoftkeys(true);
      }

      return var2;
   }

   Command[] getExtraCommands() {
      return null;
   }

   boolean launchExtraCommand(Command var1) {
      return false;
   }

   ItemCommandListener getItemCommandListener() {
      return this.commandListener;
   }

   void setOwner(Screen var1) {
      synchronized(Display.LCDUILock) {
         if (this.owner != null && var1 != null) {
            throw new IllegalStateException();
         } else {
            this.owner = var1;
         }
      }
   }

   void setLayoutImpl(int var1) {
      if ((var1 & ~VALID_LAYOUT) != 0) {
         throw new IllegalArgumentException();
      } else {
         this.layout = var1;
         this.verticalLayout = var1 & 240;
      }
   }

   int callGetLayout() {
      int var1 = this.layout & 255;
      int var2 = UIStyle.isAlignedLeftToRight ? 1 : 2;
      switch(var1) {
      case 0:
         var1 = 16 | var2;
         break;
      case 1:
      case 2:
      case 3:
         var1 |= 16;
         break;
      case 16:
      case 32:
      case 48:
         var1 |= var2;
      }

      this.verticalLayout = var1 & 240;
      return var1;
   }

   boolean boundsIncludeOtherItems(boolean var1, int var2, int var3) {
      this.boundsIncludeOtherItems = false;
      return this.boundsIncludeOtherItems;
   }

   static boolean isStringEmpty(String var0) {
      if (var0 == null) {
         return true;
      } else {
         for(int var1 = 0; var1 < var0.length(); ++var1) {
            if (var0.charAt(var1) != ' ') {
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

   void processTextEditorEvent(int var1, int var2) {
   }

   static String getStringWithSeparator(String var0) {
      String var1 = null;
      if (var0 != null) {
         String var2 = var0.trim();
         int var3 = var2.length() - 1;
         if (var2.length() == 0) {
            return null;
         }

         int var4 = -1;
         boolean var5 = false;

         for(int var6 = 0; var6 < POSSIBLE_ITEM_STRING_SEPARATORS.length; ++var6) {
            char var7 = POSSIBLE_ITEM_STRING_SEPARATORS[var6];
            var4 = var2.lastIndexOf(var7);
            if (var3 == var4) {
               var4 = var0.lastIndexOf(var7);
               var5 = true;
               break;
            }

            var4 = -1;
         }

         if (-1 == var4) {
            var4 = var0.lastIndexOf(var2.charAt(var3)) + 1;
         }

         StringBuffer var8 = new StringBuffer(var0);
         if (var5) {
            var8.setCharAt(var4, ITEM_STRING_SEPARATOR);
         } else {
            var8.insert(var4, ITEM_STRING_SEPARATOR);
         }

         var1 = var8.toString();
      }

      return var1;
   }

   boolean canItemAppearanceChange() {
      return false;
   }

   boolean isVisibleFrom(int var1, int var2) {
      if (this.bounds == null) {
         return false;
      } else {
         boolean var3 = false;
         if (var1 <= this.bounds[1] && var1 + var2 >= this.bounds[1]) {
            var3 = true;
         } else if (var1 <= this.bounds[1] + this.bounds[3] && var1 + var2 >= this.bounds[1] + this.bounds[3]) {
            var3 = true;
         } else if (this.bounds[3] > var2 && this.bounds[1] < var1 && this.bounds[1] + this.bounds[3] > var1 + var2) {
            var3 = true;
         }

         return var3;
      }
   }

   static {
      BUTTON_BORDER_WIDTH = UIStyle.BUTTON_BORDER_WIDTH;
      BUTTON_BORDER_HEIGHT = UIStyle.BUTTON_BORDER_HEIGHT;
      EMPTY_STRING = TextDatabase.getText(33);
      DEFAULT_WIDTH = Screen.screenNormMainZone.width;
      EDITABLE_TEXT_BOTTOM_MARGIN = UIStyle.EDITABLE_TEXT_BOTTOM_MARGIN;
      ITEM_STRING_SEPARATOR = TextDatabase.getText(36).charAt(0);
      POSSIBLE_ITEM_STRING_SEPARATORS = new char[]{':', '：'};
      ITEM_VALUE_ZONE = Displayable.uistyle.getZone(21);
      DEFAULT_ITEM_FONT = new Font(ITEM_VALUE_ZONE.getFont());
      MAX_LABEL_HEIGHT = Displayable.screenTickMainZone.height - Form.FORM_MAX_SCROLL;
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
      private int labelWidth;
      private int labelHeight;
      private String labelStrNL;

      Label(String var2) {
         Item.this.label = var2;
         this.setLabelString(Item.this.label);
      }

      void setLabelString(String var1) {
         int var2 = Item.this.callMinimumHeight();
         synchronized(Display.LCDUILock) {
            this.labelHeight = 0;
            this.labelWidth = 0;
            this.labelStrNL = var1.charAt(0) == '\n' ? var1.substring(1) : var1;
            Item.this.label = var1;
            int var4;
            if (Item.this.lockedWidth == -1) {
               var4 = Item.DEFAULT_WIDTH;
            } else {
               if (Item.this.lockedWidth < Item.MIN_LABEL_WIDTH) {
                  Item.this.lockedWidth = Item.MIN_LABEL_WIDTH;
               }

               var4 = Item.this.lockedWidth;
            }

            this.defineLabelTextLines(var4);
            int var5 = this.labelHeight + var2;
            Item.this.lockedHeight = Item.this.originalLockedHeight != -1 && Item.this.originalLockedHeight < var5 ? var5 : Item.this.originalLockedHeight;
         }
      }

      void defineLabelTextLines(int var1) {
         String var2 = Item.getStringWithSeparator(this.labelStrNL);
         this.textLines.removeAllElements();
         TextBreaker.breakTextInArea(var1, Item.MAX_LABEL_HEIGHT, 1, Displayable.uistyle.getZone(20).getFont(), var2, TextBreaker.DEFAULT_TEXT_LEADING, true, true, this.textLines, true);
         this.computeLabelHeightAndWidth(var1);
      }

      void computeLabelHeightAndWidth(int var1) {
         this.labelWidth = 0;
         this.labelHeight = 0;
         if (this.textLines != null && this.textLines.size() != 0) {
            TextLine var2 = (TextLine)this.textLines.firstElement();
            this.labelWidth = this.textLines.size() == 1 ? var2.getTextLineWidth() : var1;
            var2 = (TextLine)this.textLines.lastElement();
            int var3 = var2.getTextLineHeight();
            this.labelHeight = var3 * this.textLines.size() + UIStyle.LABEL_PAD_AFTER;
         }
      }

      int getLabelHeight() {
         return Item.this.itemIndex > 0 ? this.labelHeight + UIStyle.LABEL_PAD_BEFORE : this.labelHeight;
      }

      int paintLabel(Graphics var1) {
         if (Item.isStringEmpty(this.labelStrNL)) {
            return -1;
         } else {
            int var2 = var1.getTranslateX();
            int var3 = var1.getTranslateY();
            com.nokia.mid.impl.isa.ui.gdi.Graphics var4 = var1.getImpl();
            ColorCtrl var5 = var4.getColorCtrl();
            int var6 = var5.getFgColor();
            var5.setFgColor(UIStyle.COLOUR_TEXT);
            var4.drawTextInArea(UIStyle.isAlignedLeftToRight ? var2 : var2 + Item.this.bounds[2] - this.labelWidth, Item.this.itemIndex > 0 ? var3 + UIStyle.LABEL_PAD_BEFORE : var3, this.labelWidth, this.labelHeight, this.textLines, UIStyle.isAlignedLeftToRight ? 1 : 3);
            var1.translate(0, this.getLabelHeight());
            var5.setFgColor(var6);
            return 0;
         }
      }
   }
}
