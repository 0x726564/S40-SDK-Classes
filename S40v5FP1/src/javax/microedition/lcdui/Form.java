package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import com.nokia.mid.ui.lcdui.VisibilityListener;

public class Form extends Screen {
   static final int LAYOUT_HMASK = 3;
   static final int LAYOUT_VMASK = 48;
   private static final int FORM_TRAVERSE = 0;
   private static final int ITEM_TRAVERSE = 2;
   private static final int GROW_SIZE = 4;
   static final int CELL_SPACING;
   static final int FORM_MAX_SCROLL;
   static boolean IS_FOUR_WAY_SCROLL;
   int[] view;
   Item[] items;
   int numOfItems;
   boolean isTallRow;
   int oldViewY;
   boolean customItemExceptionThrown;
   private int formMode;
   private int traverseIndex;
   private boolean validateVisibility;
   int[] viewable;
   private int[] visRect;
   private ItemStateListener itemStateListener;
   private int scroll;
   private boolean boundsIncludeOtherItems;
   private int formHeight;
   private int currentAlignment;
   private int pendingAlignment;
   private int preservedAlignment;
   private int scrollingIndex;
   private int topLimitIndex;
   private int topLimit;
   private int bottomLimit;
   private boolean rowHasFocusableItems;
   static final boolean UNICOM_FORM_SCROLLING;
   private boolean topItem;
   private boolean ignoreTraverse;
   private boolean inShowNotify;

   public Form(String title) {
      this(title, (Item[])null);
   }

   public Form(String title, Item[] items) {
      super(title);
      this.isTallRow = false;
      this.oldViewY = 0;
      this.customItemExceptionThrown = false;
      this.traverseIndex = -1;
      this.validateVisibility = true;
      this.scroll = 0;
      this.boundsIncludeOtherItems = false;
      this.scrollingIndex = -1;
      this.rowHasFocusableItems = false;
      this.topItem = false;
      this.ignoreTraverse = false;
      this.inShowNotify = false;
      synchronized(Display.LCDUILock) {
         this.deleteAllImpl();
         this.numOfItems = 0;
         this.resetToTop = true;
         this.view = new int[4];
         this.visRect = new int[4];
         if (items == null) {
            this.items = new Item[4];
         } else {
            this.items = new Item[items.length > 4 ? items.length : 4];

            int i;
            for(i = 0; i < items.length; ++i) {
               if (items[i].owner != null) {
                  throw new IllegalStateException();
               }
            }

            for(i = 0; i < items.length; ++i) {
               this.insertImpl(this.numOfItems, items[i]);
            }

         }
      }
   }

   public int append(Item item) {
      synchronized(Display.LCDUILock) {
         if (item.owner != null) {
            throw new IllegalStateException();
         } else {
            return this.insertImpl(this.numOfItems, item);
         }
      }
   }

   public int append(String str) {
      if (str == null) {
         throw new NullPointerException();
      } else {
         return this.append((Item)(new StringItem((String)null, str)));
      }
   }

   public int append(Image img) {
      if (img == null) {
         throw new NullPointerException();
      } else {
         return this.append((Item)(new ImageItem((String)null, img, 0, (String)null)));
      }
   }

   public void insert(int itemNum, Item item) {
      synchronized(Display.LCDUILock) {
         if (item.owner != null) {
            throw new IllegalStateException();
         } else if (itemNum >= 0 && itemNum <= this.numOfItems) {
            this.insertImpl(itemNum, item);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void delete(int itemNum) {
      synchronized(Display.LCDUILock) {
         if (itemNum >= 0 && itemNum < this.numOfItems) {
            this.deleteImpl(itemNum);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void deleteAll() {
      synchronized(Display.LCDUILock) {
         this.deleteAllImpl();
      }
   }

   public void set(int itemNum, Item item) {
      synchronized(Display.LCDUILock) {
         if (item.owner != null) {
            throw new IllegalStateException();
         } else if (itemNum >= 0 && itemNum < this.numOfItems) {
            this.setImpl(itemNum, item);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public Item get(int itemNum) {
      synchronized(Display.LCDUILock) {
         if (itemNum >= 0 && itemNum < this.numOfItems) {
            return this.items[itemNum];
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void setItemStateListener(ItemStateListener iListener) {
      synchronized(Display.LCDUILock) {
         this.itemStateListener = iListener;
      }
   }

   public int size() {
      return this.numOfItems;
   }

   public int getWidth() {
      return this.viewport[2] - 2 * UIStyle.CUSTOMITEM_BORDER_PAD;
   }

   public int getHeight() {
      return this.viewport[3];
   }

   boolean callsVisListOnHideNotify() {
      return true;
   }

   boolean midletCommandsSupported() {
      return this.numOfItems != 0 && this.traverseIndex >= 0 ? this.items[this.traverseIndex].midletCommandsSupported() : true;
   }

   void setCurrentItem(Item i) {
      if (!this.layoutValid) {
         this.layout();
      }

      if (this.traverseIndex == -1 || this.items[this.traverseIndex] != i || UNICOM_FORM_SCROLLING) {
         if (UNICOM_FORM_SCROLLING && i instanceof CustomItem) {
            this.setTopAndHighlightedItems(i);
         } else {
            this.setTraverseIndex(0, this.traverseIndex, i.itemIndex);
            this.resetToTop = false;
         }
      }
   }

   void callShowNotify(Display d) {
      this.inShowNotify = true;
      super.callShowNotify(d);
      Item itemToTraverseOut = null;
      synchronized(Display.LCDUILock) {
         if (!this.layoutValid) {
            this.layout();
         }

         if (this.resetToTop) {
            if (this.traverseIndex >= 0 && this.traverseIndex < this.numOfItems && this.items[this.traverseIndex].hasFocus) {
               itemToTraverseOut = this.items[this.traverseIndex];
            }

            this.traverseIndex = -1;
            this.view[0] = 0;
            this.view[1] = this.topLimit;
            this.oldViewY = this.view[1];
            this.resetToTop = false;
         }
      }

      if (itemToTraverseOut != null) {
         itemToTraverseOut.callTraverseOut();
      }

      this.traverse(0);
      Displayable.uistyle.hideIndex(this);
      this.inShowNotify = false;
   }

   void callHideNotify(Display d) {
      VisibilityListener localVisibilityListener = this.visibilityListener;
      super.callHideNotify(d);

      for(int x = 0; x < this.numOfItems; ++x) {
         try {
            if (this.items[x].visible) {
               this.items[x].callHideNotify();
            }
         } catch (Exception var7) {
         }
      }

      if (localVisibilityListener != null) {
         synchronized(Display.calloutLock) {
            localVisibilityListener.hideNotify(d, this);
         }
      }

   }

   void removedFromDisplayNotify(Display d) {
      super.removedFromDisplayNotify(d);
      Item itemToTraverseOut = null;
      synchronized(Display.LCDUILock) {
         if (this.traverseIndex != -1 && this.items[this.traverseIndex].hasFocus) {
            itemToTraverseOut = this.items[this.traverseIndex];
         }

         this.formMode = 0;
      }

      if (itemToTraverseOut != null) {
         itemToTraverseOut.callTraverseOut();
      }

   }

   void callKeyPressed(int keyCode, int keyDataIdx) {
      super.callKeyPressed(keyCode, keyDataIdx);
      Item i = null;
      int action = -1;
      switch(keyCode) {
      case -4:
         action = 5;
         break;
      case -3:
         action = 2;
         break;
      case -2:
         action = 6;
         break;
      case -1:
         action = 1;
      }

      synchronized(Display.LCDUILock) {
         if (this.numOfItems == 0 || this.traverseIndex < 0) {
            return;
         }

         i = this.items[this.traverseIndex];
      }

      if (i.supportsInternalTraversal()) {
         i.callKeyPressed(keyCode, keyDataIdx);
      } else if (action != 1 && action != 6) {
         if (action != 2 && action != 5) {
            if (keyCode != -6 && keyCode != -7 && keyCode != -5) {
               i.callKeyPressed(keyCode, keyDataIdx);
            }
         } else if (i.supportHorizontalScrolling()) {
            i.callKeyPressed(keyCode, keyDataIdx);
         } else {
            this.traverse(action);
         }
      } else {
         this.traverse(action);
      }

   }

   void callKeyReleased(int keyCode, int keyDataIdx) {
      super.callKeyReleased(keyCode, keyDataIdx);
      Item i = null;
      synchronized(Display.LCDUILock) {
         if (this.numOfItems == 0 || this.traverseIndex < 0) {
            return;
         }

         i = this.items[this.traverseIndex];
      }

      if (i != null) {
         i.callKeyReleased(keyCode, keyDataIdx);
      }

   }

   void callKeyRepeated(int keyCode, int keyDataIdx) {
      super.callKeyRepeated(keyCode, keyDataIdx);
      Item i = null;
      int action = -1;
      switch(keyCode) {
      case -4:
         action = 5;
         break;
      case -3:
         action = 2;
         break;
      case -2:
         action = 6;
         break;
      case -1:
         action = 1;
      }

      synchronized(Display.LCDUILock) {
         if (this.numOfItems == 0 || this.traverseIndex < 0) {
            return;
         }

         i = this.items[this.traverseIndex];
      }

      if (i.supportsInternalTraversal()) {
         i.callKeyRepeated(keyCode, keyDataIdx);
      } else if (action != 1 && action != 6) {
         if (action != 2 && action != 5) {
            if (keyCode != -6 && keyCode != -7 && keyCode != -5) {
               i.callKeyRepeated(keyCode, keyDataIdx);
            }
         } else if (i.supportHorizontalScrolling()) {
            i.callKeyRepeated(keyCode, keyDataIdx);
         } else {
            this.traverse(action);
         }
      } else {
         this.traverse(action);
      }

   }

   void traverse(int dir) {
      switch(this.formMode) {
      case 0:
         try {
            this.formTraverse(dir);
         } catch (Throwable var4) {
         }
         break;
      case 2:
         try {
            if (!this.itemTraverse(dir)) {
               this.formTraverse(dir);
            }
         } catch (Throwable var3) {
         }
      }

   }

   void formTraverse(int dir) {
      if (this.numOfItems == 0) {
         this.updateSoftkeys(true);
      } else if (dir == 0) {
         this.setNoneDirectionTraverseIndex();
      } else if (this.traverseIndex >= 0 && !this.items[this.traverseIndex].shouldSkipTraverse() && this.scrollForBounds(dir, this.items[this.traverseIndex].bounds)) {
         this.validateVisibility = true;
         this.repaintFull();
      } else {
         int bendDir = this.getScrollingDirection(dir);
         int newIndex = this.traverseIndex;

         do {
            int oldIndex = newIndex;
            switch(bendDir) {
            case 1:
               if (UNICOM_FORM_SCROLLING && this.traverseIndex == 0) {
                  this.wrapAround(false);
                  this.setTraverseIndex(1, newIndex, this.traverseIndex);
                  return;
               }

               newIndex = this.formScrollUp(newIndex);
               break;
            case 2:
               newIndex = this.formScrollLeft(newIndex);
            case 3:
            case 4:
            default:
               break;
            case 5:
               newIndex = this.formScrollRight(newIndex);
               break;
            case 6:
               if (UNICOM_FORM_SCROLLING && this.traverseIndex == this.numOfItems - 1) {
                  this.wrapAround(true);
                  this.setTraverseIndex(6, newIndex, this.traverseIndex);
                  return;
               }

               newIndex = this.formScrollDown(newIndex);
            }

            if (newIndex == -1 || oldIndex == newIndex) {
               return;
            }
         } while(this.items[newIndex].shouldSkipTraverse());

         this.setTraverseIndex(dir, this.traverseIndex, newIndex);
      }
   }

   boolean itemTraverse(int dir) {
      if (this.traverseIndex == -1) {
         return false;
      } else {
         Item traverseItem = this.items[this.traverseIndex];
         int[] visRectRefereedToForm = new int[]{this.visRect[0] + traverseItem.bounds[0], this.visRect[1] + traverseItem.bounds[1], this.visRect[2], this.visRect[3]};
         if (this.formMode == 2 && this.scrollForBounds(dir, visRectRefereedToForm)) {
            this.validateVisibility = true;
            this.repaintFull();
            return true;
         } else if (UNICOM_FORM_SCROLLING && this.ignoreTraverse) {
            this.ignoreTraverse = false;
            return false;
         } else if (traverseItem.callTraverse(dir, this.viewport[2], this.viewport[3], this.visRect)) {
            visRectRefereedToForm[1] = this.visRect[1] + traverseItem.bounds[1];
            visRectRefereedToForm[3] = this.visRect[3];
            this.formMode = 2;
            if (this.scrollForTraversal(dir, visRectRefereedToForm)) {
               this.validateVisibility = true;
               this.repaintFull();
            }

            if (!this.items[this.traverseIndex].isFocusable()) {
               this.checkMinScroll(dir);
            }

            this.oldViewY = this.view[1];
            return true;
         } else {
            return false;
         }
      }
   }

   boolean scrollForBounds(int dir, int[] bounds) {
      if (this.formHeight <= this.viewport[3]) {
         this.view[1] = this.topLimit;
      } else if (this.isTallRow) {
         this.view[1] = this.view[1] > this.bottomLimit - this.viewport[3] ? this.bottomLimit - this.viewport[3] : this.view[1];
         return false;
      }

      int[] var10000;
      switch(dir) {
      case 1:
         if (bounds[1] >= this.view[1]) {
            return false;
         }

         var10000 = this.view;
         var10000[1] -= this.getMinimumScroll(dir);
         this.view[1] = this.view[1] <= this.topLimit ? this.topLimit : this.view[1];
         return true;
      case 2:
      case 3:
      case 4:
      case 5:
      default:
         return false;
      case 6:
         if (bounds[1] + bounds[3] <= this.view[1] + this.viewport[3]) {
            return false;
         } else {
            var10000 = this.view;
            var10000[1] += FORM_MAX_SCROLL;
            this.view[1] = this.view[1] > this.bottomLimit - this.viewport[3] ? this.bottomLimit - this.viewport[3] : this.view[1];
            return true;
         }
      }
   }

   boolean scrollForTraversal(int dir, int[] bounds) {
      if (this.formHeight < this.viewport[3]) {
         if (this.view[1] != this.topLimit) {
            this.view[1] = this.topLimit;
            return true;
         } else {
            return false;
         }
      } else {
         Item focusedItem = this.items[this.traverseIndex];
         if (focusedItem.visible && (dir == 2 || dir == 5) && focusedItem.bounds[3] > this.viewport[3]) {
            return false;
         } else {
            boolean inBounds = bounds[1] >= this.view[1] && bounds[1] + bounds[3] <= this.view[1] + this.viewport[3];
            if (UNICOM_FORM_SCROLLING) {
               inBounds &= !this.topItem;
            }

            if (inBounds) {
               return false;
            } else {
               if (bounds[3] > this.viewport[3] - FORM_MAX_SCROLL) {
                  if (dir == 6 || dir == 0 || dir == 5 && UIStyle.isAlignedLeftToRight || dir == 2 && !UIStyle.isAlignedLeftToRight) {
                     this.view[1] = bounds[1];
                  } else if (dir == 1 || dir == 5 && !UIStyle.isAlignedLeftToRight || dir == 2 && UIStyle.isAlignedLeftToRight) {
                     this.view[1] = bounds[1] + bounds[3] - this.viewport[3];
                  }
               } else if (dir != 6 && dir != 0 && (dir != 5 || !UIStyle.isAlignedLeftToRight) && (dir != 2 || UIStyle.isAlignedLeftToRight)) {
                  if (dir == 1 || dir == 5 && !UIStyle.isAlignedLeftToRight || dir == 2 && UIStyle.isAlignedLeftToRight) {
                     this.view[1] = bounds[1];
                  }
               } else {
                  this.view[1] = bounds[1] + bounds[3] - this.viewport[3];
               }

               if (UNICOM_FORM_SCROLLING && this.topItem) {
                  this.view[1] = bounds[1];
               }

               this.checkViewY();
               return false;
            }
         }
      }
   }

   void layout() {
      super.layout();
      if (this.numOfItems != 0) {
         this.currentAlignment = this.items[0].callGetLayout() & 3;
         if (this.items[0] instanceof ImageItem && (this.items[0].layout & 16384) == 0) {
            this.preservedAlignment = (UIStyle.isAlignedLeftToRight ? 1 : 2) | 16384;
         } else {
            this.currentAlignment |= 16384;
         }

         this.pendingAlignment = this.currentAlignment;
         int rowStart = 0;
         if (this.viewable == null) {
            this.viewable = new int[4];
         }

         this.viewable[0] = 0;
         this.viewable[1] = 0;
         this.viewable[2] = this.viewport[2];
         this.viewable[3] = this.viewport[3];
         this.view[2] = this.viewable[2];
         this.view[3] = 0;
         int rowHeight = 0;
         int pW = 0;
         int pH = false;

         for(int index = 0; index < this.numOfItems; ++index) {
            Item auxItem = this.items[index];
            if (auxItem.visible) {
               auxItem.callHideNotify();
            }

            if (auxItem.bounds == null) {
               auxItem.bounds = new int[4];
            } else {
               if (auxItem instanceof StringItem) {
                  ((StringItem)auxItem).setToDefaults();
               }

               auxItem.bounds[0] = 0;
               auxItem.bounds[1] = 0;
               auxItem.bounds[2] = 0;
               auxItem.bounds[3] = 0;
            }

            pW = this.getItemWidth(index, pW);
            this.boundsIncludeOtherItems = false;
            boolean newLine = this.isNewLine(index, pW, rowStart, rowHeight);
            if (newLine && rowHeight > 0 || this.boundsIncludeOtherItems) {
               rowStart = this.makeNewLine(rowHeight, rowStart, index, pW);
               this.currentAlignment = this.pendingAlignment;
               pW = this.getItemWidth(index, pW);
               rowHeight = 0;
            }

            int pH = this.getItemHeight(index, pW);
            if (auxItem.bounds[2] != pW || auxItem.bounds[3] != pH) {
               auxItem.sizeChanged = true;
            }

            if (!this.boundsIncludeOtherItems) {
               auxItem.bounds[0] = this.viewable[0];
               auxItem.bounds[3] = pH;
               auxItem.bounds[1] = this.viewable[1];
            }

            auxItem.bounds[2] = pW;
            if (pW > 0) {
               if (this.boundsIncludeOtherItems) {
                  this.viewable[0] = auxItem.bounds[0] + auxItem.bounds[2] + CELL_SPACING;
                  this.viewable[2] = this.viewport[2] - this.viewable[0];
               } else {
                  int[] var10000 = this.viewable;
                  var10000[2] -= pW + CELL_SPACING;
                  var10000 = this.viewable;
                  var10000[0] += pW + CELL_SPACING;
               }
            }

            rowHeight = pH > rowHeight ? pH : rowHeight;
         }

         this.layoutLastLine(rowStart, rowHeight, this.viewable[2]);
         this.rightToLeftAlignementSwitch();
         this.formHeight = this.getFormHeight();
         this.checkViewY();
      }
   }

   void callPaint(Graphics g) {
      super.callPaint(g);
      g.setClip(this.viewport[0], this.viewport[1], this.viewport[2], this.viewport[3]);

      try {
         if (this.numOfItems == 0) {
            TextBreaker breaker = TextBreaker.getBreaker(Font.getDefaultFont().getImpl(), TextDatabase.getText(33), false);
            TextLine tLine = null;
            tLine = breaker.getTextLine(32767);
            tLine.setAlignment(2);
            ColorCtrl colorCtrl = g.getImpl().getColorCtrl();
            int oldFgColor = colorCtrl.getFgColor();
            colorCtrl.setFgColor(UIStyle.COLOUR_TEXT);
            g.getImpl().drawText(tLine, (short)(screenNormMainZone.width / 2), (short)this.getMainZone().y);
            colorCtrl.setFgColor(oldFgColor);
            breaker.destroyBreaker();
            return;
         }

         int[] clip = new int[]{g.getClipX(), g.getClipY(), g.getClipWidth(), g.getClipHeight()};
         if (clip[1] + clip[3] <= this.viewport[1]) {
            return;
         }

         this.scroll = this.oldViewY = this.view[1];

         for(int i = 0; i < this.numOfItems; ++i) {
            this.paintItem(this.items[i], g, clip);
         }

         if (this.formHeight > this.viewport[3]) {
            this.paintScrollBar(g);
         }

         g.reset(clip[0], clip[1], clip[2], clip[3]);
      } catch (Throwable var6) {
      }

   }

   void paintItem(Item item, Graphics g, int[] clip) {
      if (item.bounds != null) {
         int tX = item.bounds[0] + this.viewport[0] - this.view[0];
         int tY = item.bounds[1] + this.viewport[1] - this.view[1];
         if (tY + item.bounds[3] >= clip[1] && tY <= clip[1] + clip[3] || this.validateVisibility) {
            g.clipRect(tX, tY, item.bounds[2], item.bounds[3]);
            if (g.getClipWidth() > 0 && g.getClipHeight() > 0) {
               if (this.validateVisibility && !item.visible) {
                  item.callShowNotify();
               }

               if (item.sizeChanged) {
                  item.callSizeChanged(item.bounds[2], item.bounds[3]);
                  item.sizeChanged = false;
               }

               g.translate(tX, tY);
               if (item.itemIndex == this.traverseIndex) {
                  item.callPaint(g, item.bounds[2], item.bounds[3], true);
               } else {
                  item.callPaint(g, item.bounds[2], item.bounds[3], false);
               }
            } else if (this.validateVisibility && item.visible) {
               item.callHideNotify();
            }

            g.reset(clip[0], clip[1], clip[2], clip[3]);
         }
      }
   }

   ItemStateListener getItemStateListener() {
      return this.itemStateListener;
   }

   Item getCurrentItem() {
      return this.traverseIndex < 0 ? null : this.items[this.traverseIndex];
   }

   void callInvalidate() {
      if (!this.isShown()) {
         this.layoutValid = false;
      } else {
         super.callInvalidate();
         if (!this.inShowNotify) {
            synchronized(Display.LCDUILock) {
               if (!this.layoutValid) {
                  this.layout();
               }
            }

            this.traverse(0);
            this.repaintFull();
         }

      }
   }

   void callItemStateChanged(Item item) {
      ItemStateListener isl = this.itemStateListener;
      if (isl != null && item != null) {
         synchronized(Display.calloutLock) {
            isl.itemStateChanged(item);
         }
      }
   }

   void repaintItem(Item item, int x, int y, int w, int h) {
      this.repaintFull();
   }

   int insertImpl(int itemNum, Item item) {
      if (this.myDisplay != null && this.myDisplay.getCurrentTopOfStackDisplayable().isDisplayableInStack(this)) {
         if (this.traverseIndex >= itemNum || this.traverseIndex == -1) {
            ++this.traverseIndex;
         }
      } else {
         this.resetToTop = true;
      }

      if (this.items.length == this.numOfItems) {
         Item[] newItems = new Item[this.numOfItems + 4];
         System.arraycopy(this.items, 0, newItems, 0, itemNum);
         System.arraycopy(this.items, itemNum, newItems, itemNum + 1, this.numOfItems - itemNum);
         this.items = newItems;
      } else if (itemNum != this.numOfItems) {
         System.arraycopy(this.items, itemNum, this.items, itemNum + 1, this.numOfItems - itemNum);
      }

      ++this.numOfItems;
      this.items[itemNum] = null;
      this.setImpl(itemNum, item);
      return itemNum;
   }

   void setImpl(int itemNum, Item item) {
      Item oldItem = this.items[itemNum];
      if (oldItem != null) {
         oldItem.setOwner((Screen)null);
      }

      item.setOwner(this);
      item.itemIndex = itemNum;
      this.items[itemNum] = item;

      for(int i = itemNum + 1; i < this.numOfItems; this.items[i].itemIndex = i++) {
      }

      this.invalidate();
   }

   void deleteImpl(int itemNum) {
      if (this.myDisplay == null || !this.myDisplay.getCurrentTopOfStackDisplayable().isDisplayableInStack(this)) {
         this.resetToTop = true;
      }

      Item deletedItem = this.items[itemNum];
      deletedItem.setOwner((Screen)null);
      --this.numOfItems;
      if (this.traverseIndex == itemNum) {
         this.formMode = 0;
         this.isTallRow = false;
      }

      if (this.traverseIndex > itemNum || this.traverseIndex == this.numOfItems) {
         --this.traverseIndex;
      }

      if (itemNum < this.numOfItems) {
         System.arraycopy(this.items, itemNum + 1, this.items, itemNum, this.numOfItems - itemNum);
      }

      this.items[this.numOfItems] = null;
      if (this.numOfItems == 0 && this.items.length > 4) {
         this.items = new Item[4];
      }

      for(int i = itemNum; i < this.numOfItems; this.items[i].itemIndex = i++) {
      }

      if (itemNum == 0) {
         this.updateSoftkeys(true);
      }

      this.invalidate();
   }

   void deleteAllImpl() {
      this.isTallRow = false;
      this.customItemExceptionThrown = false;
      if (this.numOfItems != 0) {
         for(int x = 0; x < this.numOfItems; ++x) {
            this.items[x].itemIndex = -1;
            this.items[x].setOwner((Screen)null);
            this.items[x] = null;
         }

         if (this.items.length > 4) {
            this.items = new Item[4];
         }

         this.numOfItems = 0;
         this.formMode = 0;
         this.traverseIndex = -1;
         this.invalidate();
      }
   }

   private void setTraverseIndex(int dir, int oldIndex, int newIndex) {
      this.isTallRow = false;
      this.formMode = 0;
      if (dir == 0) {
         newIndex = this.findTraversableItem(newIndex);
         if (newIndex == -1) {
            this.updateSoftkeys(true);
            return;
         }
      }

      this.traverseIndex = newIndex;
      if (this.items[this.traverseIndex].bounds != null) {
         this.initializeVisRect(this.traverseIndex);
         if (oldIndex >= 0 && oldIndex < this.numOfItems && this.items[oldIndex].hasFocus) {
            this.items[oldIndex].callTraverseOut();
         }

         this.scrollForTraversal(dir, this.items[this.traverseIndex].bounds);
         if (!this.items[this.traverseIndex].isFocusable()) {
            this.checkMinScroll(dir);
         }

         this.itemTraverse(dir);
         this.updateSoftkeys(true);
         this.validateVisibility = true;
         this.oldViewY = this.view[1];
         this.repaintFull();
      }
   }

   private int getMinimumScroll(int dir) {
      int minScroll = -1;
      int refLineY = this.items[this.traverseIndex].lineY;

      int i;
      for(i = this.traverseIndex - 1; i >= 0 && refLineY == this.items[i].lineY; --i) {
      }

      ++i;

      for(; i < this.numOfItems && refLineY == this.items[i].lineY; ++i) {
         int scroll_m = this.items[i].getMinimumScroll(dir);
         if ((this.items[i].isVisibleFrom(this.view[1] - scroll_m, this.viewport[3]) && dir == 1 || this.items[i].isVisibleFrom(this.view[1] + scroll_m, this.viewport[3]) && dir == 6 || this.items[i].visible) && (minScroll == -1 || scroll_m < minScroll)) {
            minScroll = scroll_m;
         }
      }

      return minScroll;
   }

   private int findNearestNeighbourUp() {
      if (this.traverseIndex == -1) {
         return 0;
      } else {
         Item focusedItem = this.items[this.traverseIndex];
         int presentLineY = focusedItem.lineY;
         int presentLineHeight = focusedItem.rowHeight;
         int x1 = focusedItem.callHighlightedX() + focusedItem.callHighlightedWidth() / 2;
         int y1 = focusedItem.bounds[1];
         this.scrollingIndex = this.traverseIndex;
         boolean isSpacerCaseUp = false;
         Item scrollItem = this.goToTheEndOfRow(presentLineY);
         int foundItemIndex = -1;

         while(true) {
            if (this.scrollingIndex >= 0 && (isSpacerCaseUp || y1 - (presentLineY + presentLineHeight) <= this.viewport[3] - FORM_MAX_SCROLL || this.view[1] - scrollItem.lineY <= this.viewport[3] - FORM_MAX_SCROLL)) {
               if (!isSpacerCaseUp) {
                  isSpacerCaseUp = this.bigSpacerInRowUp(presentLineY, -1);
                  if (this.scrollingIndex < 0) {
                     return this.returnNotFocusableItemUp(foundItemIndex);
                  }

                  if (!isSpacerCaseUp) {
                     isSpacerCaseUp = this.bigSpacerInRowUp(this.items[this.scrollingIndex].lineY, this.scrollingIndex);
                     if (isSpacerCaseUp && foundItemIndex != -1 && presentLineY < this.view[1]) {
                        return this.returnNotFocusableItemUp(foundItemIndex);
                     }
                  } else {
                     foundItemIndex = this.getBigSpacerCaseSelectedItemUp(presentLineY, y1);
                     if (foundItemIndex != -1) {
                        return foundItemIndex;
                     }
                  }
               }

               if (this.scrollingIndex >= 0) {
                  scrollItem = this.items[this.scrollingIndex];
                  int tempFoundIndex = this.findItemInRowUp(isSpacerCaseUp, x1);
                  foundItemIndex = tempFoundIndex != -1 ? tempFoundIndex : foundItemIndex;
                  int prevLineY = scrollItem.lineY;
                  int prevLineHeight = scrollItem.rowHeight;
                  if ((foundItemIndex == -1 || !this.items[foundItemIndex].isFocusable()) && (foundItemIndex == -1 || !isSpacerCaseUp || this.formHeight <= this.viewport[3] || this.view[1] <= 0)) {
                     presentLineY = prevLineY;
                     presentLineHeight = prevLineHeight;
                     continue;
                  }

                  return foundItemIndex;
               }
            }

            return this.returnNotFocusableItemUp(foundItemIndex);
         }
      }
   }

   private int returnNotFocusableItemUp(int foundItemIndex) {
      Item notFocusableSelectedItem = null;
      if (foundItemIndex >= 0 && foundItemIndex < this.numOfItems) {
         notFocusableSelectedItem = this.items[foundItemIndex];
      }

      return notFocusableSelectedItem != null && this.formHeight > this.viewport[3] && this.view[1] > 0 && notFocusableSelectedItem.bounds[1] < this.view[1] ? foundItemIndex : -1;
   }

   private int findNearestNeighbourDown() {
      if (this.traverseIndex == -1) {
         return 0;
      } else {
         Item focusedItem = this.items[this.traverseIndex];
         int presentLineY = focusedItem.lineY;
         int x1 = focusedItem.callHighlightedX() + focusedItem.callHighlightedWidth() / 2;
         int y1 = focusedItem.bounds[1] + focusedItem.bounds[3];
         this.scrollingIndex = this.traverseIndex;
         boolean isSpacerCase = false;
         Item scrollItem = this.goToTheStartOfRow(presentLineY);
         int foundItemIndex = -1;

         while(true) {
            if (this.scrollingIndex < this.numOfItems && (isSpacerCase || presentLineY - y1 <= this.viewport[3] - FORM_MAX_SCROLL || scrollItem.lineY + scrollItem.rowHeight <= this.view[1] + 2 * this.viewport[3] - FORM_MAX_SCROLL)) {
               if (!isSpacerCase) {
                  isSpacerCase = this.bigSpacerInRowDown(presentLineY, -1);
                  if (this.scrollingIndex >= this.numOfItems) {
                     return this.returnNotFocusableItemDown(foundItemIndex);
                  }

                  if (!isSpacerCase) {
                     isSpacerCase = this.bigSpacerInRowDown(this.items[this.scrollingIndex].lineY, this.scrollingIndex);
                     if (isSpacerCase && foundItemIndex != -1 && presentLineY > this.view[1] + this.viewport[3]) {
                        return this.returnNotFocusableItemDown(foundItemIndex);
                     }
                  } else {
                     foundItemIndex = this.getBigSpacerCaseSelectedItem(presentLineY, y1);
                     if (foundItemIndex != -1) {
                        return foundItemIndex;
                     }
                  }
               }

               if (this.scrollingIndex < this.numOfItems) {
                  scrollItem = this.items[this.scrollingIndex];
                  int nextLine = scrollItem.lineY;
                  int tempFoundIndex = this.findItemInRowDown(isSpacerCase, x1);
                  foundItemIndex = tempFoundIndex != -1 ? tempFoundIndex : foundItemIndex;
                  if ((foundItemIndex == -1 || !this.items[foundItemIndex].isFocusable()) && (foundItemIndex == -1 || !isSpacerCase || this.view[1] >= this.formHeight - this.viewport[3] || this.formHeight <= this.viewport[3])) {
                     presentLineY = nextLine;
                     continue;
                  }

                  return foundItemIndex;
               }
            }

            return this.returnNotFocusableItemDown(foundItemIndex);
         }
      }
   }

   private int returnNotFocusableItemDown(int foundItemIndex) {
      Item notFocusableSelectedItem = null;
      if (foundItemIndex >= 0 && foundItemIndex < this.numOfItems) {
         notFocusableSelectedItem = this.items[foundItemIndex];
      }

      if (notFocusableSelectedItem != null && this.view[1] < this.formHeight - this.viewport[3] && this.formHeight > this.viewport[3] && notFocusableSelectedItem.bounds[1] + notFocusableSelectedItem.bounds[3] > this.view[1] + this.viewport[3] - FORM_MAX_SCROLL) {
         if (notFocusableSelectedItem.bounds[1] <= this.view[1] + this.viewport[3] - FORM_MAX_SCROLL) {
            this.view[1] = notFocusableSelectedItem.bounds[1] + this.viewport[3] <= this.formHeight ? notFocusableSelectedItem.bounds[1] : this.formHeight - this.viewport[3];
         }

         return foundItemIndex;
      } else {
         return -1;
      }
   }

   private int findItemInRowUp(boolean isSpacerCaseUp, int x1) {
      Item scrollItem = this.items[this.scrollingIndex];
      Item focusedItem = this.items[this.traverseIndex];
      int prevLineY = scrollItem.lineY;
      int minDistance = -1;
      int minDistanceFocusable = -1;
      int notFocusableSelectedItemIndex = -1;

      int focusableSelectedItemIndex;
      for(focusableSelectedItemIndex = -1; this.scrollingIndex >= 0 && scrollItem.lineY == prevLineY; scrollItem = this.items[this.scrollingIndex]) {
         boolean scrollItemfocussable = scrollItem.isFocusable();
         if ((scrollItemfocussable || scrollItem.bounds[1] < this.view[1]) && this.scrollingIndex != this.traverseIndex && !scrollItem.shouldSkipTraverse() && (scrollItem.bounds[1] + scrollItem.bounds[3] < focusedItem.bounds[1] + focusedItem.bounds[3] || scrollItem.bounds[1] < focusedItem.bounds[1]) && (scrollItem.bounds[1] + scrollItem.bounds[3] > this.view[1] - FORM_MAX_SCROLL + CELL_SPACING || this.view[1] - scrollItem.lineY <= this.viewport[3] - FORM_MAX_SCROLL || isSpacerCaseUp)) {
            int x2 = scrollItem.callHighlightedX();
            int width = scrollItem.callHighlightedWidth();
            if (this.isCrossing(x1, x2, width)) {
               if (scrollItemfocussable) {
                  return this.scrollingIndex;
               }

               notFocusableSelectedItemIndex = this.scrollingIndex;
               minDistance = 0;
            }

            int distance = this.borderDistance(x1, x2, width);
            if (scrollItemfocussable && (minDistanceFocusable == -1 || distance < minDistanceFocusable)) {
               minDistanceFocusable = distance;
               focusableSelectedItemIndex = this.scrollingIndex;
            }

            if (minDistance == -1 || distance < minDistance) {
               minDistance = distance;
               notFocusableSelectedItemIndex = this.scrollingIndex;
            }
         }

         --this.scrollingIndex;
         if (this.scrollingIndex < 0) {
            break;
         }
      }

      return focusableSelectedItemIndex != -1 ? focusableSelectedItemIndex : notFocusableSelectedItemIndex;
   }

   private int findItemInRowDown(boolean isSpacerCase, int x1) {
      Item scrollItem = this.items[this.scrollingIndex];
      Item focusedItem = this.items[this.traverseIndex];
      int nextLine = scrollItem.lineY;
      int minDistance = -1;
      int minDistanceFocusable = -1;
      boolean sameLine = scrollItem.lineY == nextLine;
      int notFocusableSelectedItemIndex = -1;

      int focusableSelectedItemIndex;
      for(focusableSelectedItemIndex = -1; this.scrollingIndex < this.numOfItems && sameLine; sameLine = scrollItem.lineY == nextLine) {
         if (this.scrollingIndex != this.traverseIndex && !scrollItem.shouldSkipTraverse() && scrollItem.bounds[1] + scrollItem.bounds[3] > focusedItem.bounds[1] + focusedItem.bounds[3] && (scrollItem.bounds[1] < this.view[1] + (this.viewport[3] - FORM_MAX_SCROLL) + CELL_SPACING || scrollItem.lineY + scrollItem.rowHeight <= this.view[1] + 2 * this.viewport[3] - FORM_MAX_SCROLL || isSpacerCase)) {
            int x2 = scrollItem.callHighlightedX();
            int width = scrollItem.callHighlightedWidth();
            if (this.isCrossing(x1, x2, width)) {
               if (scrollItem.isFocusable()) {
                  return this.scrollingIndex;
               }

               notFocusableSelectedItemIndex = this.scrollingIndex;
               minDistance = 0;
            }

            int distance = this.borderDistance(x1, x2, width);
            if (scrollItem.isFocusable() && (minDistanceFocusable == -1 || distance < minDistanceFocusable)) {
               minDistanceFocusable = distance;
               focusableSelectedItemIndex = this.scrollingIndex;
            }

            if (minDistance == -1 || distance < minDistance) {
               minDistance = distance;
               notFocusableSelectedItemIndex = this.scrollingIndex;
            }
         }

         ++this.scrollingIndex;
         if (this.scrollingIndex >= this.numOfItems) {
            break;
         }

         scrollItem = this.items[this.scrollingIndex];
      }

      return focusableSelectedItemIndex != -1 ? focusableSelectedItemIndex : notFocusableSelectedItemIndex;
   }

   private Item goToTheStartOfRow(int presentLineY) {
      while(this.scrollingIndex >= 0 && this.items[this.scrollingIndex].lineY == presentLineY) {
         --this.scrollingIndex;
      }

      ++this.scrollingIndex;
      return this.items[this.scrollingIndex];
   }

   private Item goToTheEndOfRow(int presentLineY) {
      while(this.scrollingIndex <= this.numOfItems - 1 && this.items[this.scrollingIndex].lineY == presentLineY) {
         ++this.scrollingIndex;
      }

      --this.scrollingIndex;
      return this.items[this.scrollingIndex];
   }

   private int getBigSpacerCaseSelectedItemUp(int presentLineY, int y1) {
      int minVerticalDistance = -1;
      int minFocusableItemVerticalDistance = -1;
      int selectedUnfocusableItem = -1;
      int selectedFocusableItem = -1;
      int h = this.scrollingIndex + 1;

      for(Item auxItem = this.items[h]; auxItem != null && auxItem.lineY == presentLineY && !auxItem.shouldSkipTraverse(); auxItem = h < this.numOfItems ? this.items[h] : null) {
         if (auxItem.bounds[1] < this.view[1]) {
            int verticalDistance = this.borderDistance(y1, auxItem.bounds[1], auxItem.bounds[3]);
            if (h != this.traverseIndex && (minVerticalDistance == -1 || minVerticalDistance > verticalDistance)) {
               minVerticalDistance = verticalDistance;
               selectedUnfocusableItem = h;
            }

            if (h != this.traverseIndex && auxItem.isFocusable() && (minFocusableItemVerticalDistance == -1 || minFocusableItemVerticalDistance > verticalDistance)) {
               minFocusableItemVerticalDistance = verticalDistance;
               selectedFocusableItem = h;
            }
         }

         ++h;
      }

      if (selectedFocusableItem == -1) {
         selectedFocusableItem = selectedUnfocusableItem;
      }

      return selectedFocusableItem;
   }

   private int getBigSpacerCaseSelectedItem(int presentLineY, int y1) {
      int minVerticalDistance = -1;
      int minFocusableItemVerticalDistance = -1;
      int selectedUnfocusableItem = -1;
      int selectedFocusableItem = -1;
      int h = this.scrollingIndex - 1;

      for(Item auxItem = this.items[h]; auxItem != null && auxItem.lineY == presentLineY && !auxItem.shouldSkipTraverse(); auxItem = h >= 0 ? this.items[h] : null) {
         if (auxItem.bounds[1] >= this.view[1] + this.viewport[3] - FORM_MAX_SCROLL) {
            int verticalDistance = this.borderDistance(y1, auxItem.bounds[1], auxItem.bounds[3]);
            if (h != this.traverseIndex && (minVerticalDistance == -1 || minVerticalDistance > verticalDistance)) {
               minVerticalDistance = verticalDistance;
               selectedUnfocusableItem = h;
            }

            if (h != this.traverseIndex && auxItem.isFocusable() && (minFocusableItemVerticalDistance == -1 || minFocusableItemVerticalDistance > verticalDistance)) {
               minFocusableItemVerticalDistance = verticalDistance;
               selectedFocusableItem = h;
            }
         }

         --h;
      }

      if (selectedFocusableItem == -1) {
         selectedFocusableItem = selectedUnfocusableItem;
      }

      return selectedFocusableItem;
   }

   private boolean bigSpacerInRowDown(int presentLineY, int i) {
      int index = i == -1 ? this.scrollingIndex : i;
      if (index > this.numOfItems - 1) {
         return false;
      } else {
         boolean isSpacerCase = false;

         for(Item auxItem = this.items[index]; auxItem.lineY == presentLineY; auxItem = this.items[index]) {
            if (!isSpacerCase && auxItem.shouldSkipTraverse() && auxItem.bounds[3] > this.viewport[3] - FORM_MAX_SCROLL) {
               isSpacerCase = true;
            }

            ++index;
            if (index > this.numOfItems - 1) {
               break;
            }
         }

         this.scrollingIndex = i == -1 ? index : this.scrollingIndex;
         return isSpacerCase;
      }
   }

   private boolean bigSpacerInRowUp(int presentLineY, int i) {
      int index = i == -1 ? this.scrollingIndex : i;
      if (index < 0) {
         return false;
      } else {
         Item auxItem = this.items[index];

         boolean isSpacerCaseUp;
         for(isSpacerCaseUp = false; auxItem.lineY == presentLineY; auxItem = this.items[index]) {
            if (!isSpacerCaseUp && auxItem.shouldSkipTraverse() && auxItem.bounds[3] > this.viewport[3] - FORM_MAX_SCROLL) {
               isSpacerCaseUp = true;
            }

            --index;
            if (index < 0) {
               break;
            }
         }

         this.scrollingIndex = i == -1 ? index : this.scrollingIndex;
         return isSpacerCaseUp;
      }
   }

   private boolean isCrossing(int x1, int x2, int width) {
      return x1 >= x2 && x1 <= x2 + width;
   }

   private int borderDistance(int x1, int x2, int width) {
      int dist1 = x2 - x1;
      int dist2 = x2 + width - x1;
      dist1 = dist1 >= 0 ? dist1 : -dist1;
      dist2 = dist2 >= 0 ? dist2 : -dist2;
      return dist1 < dist2 ? dist1 : dist2;
   }

   private void layoutLastLine(int rowStart, int rowHeight, int spaceLeft) {
      if (!this.boundsIncludeOtherItems) {
         rowHeight = this.layoutRowHorizontal(rowStart, this.numOfItems - 1, spaceLeft, rowHeight);
         this.layoutRowVertical(rowStart, this.numOfItems - 1, rowHeight, false);
         this.view[3] = this.items[this.numOfItems - 1].lineY + rowHeight;
      }

   }

   private void rightToLeftAlignementSwitch() {
      for(int index = 0; !UIStyle.isAlignedLeftToRight && index < this.numOfItems; ++index) {
         Item tempItem = this.items[index];
         tempItem.bounds[0] = this.viewport[2] - tempItem.bounds[0] - tempItem.bounds[2];
         if (tempItem.boundsIncludeOtherItems) {
            ((StringItem)tempItem).setOffsetPosition(0);
         }
      }

   }

   private int makeNewLine(int rowHeight, int rowStart, int index, int pW) {
      int retRowStart = index;
      if (index > 0 && this.boundsIncludeOtherItems) {
         rowHeight = this.makeNewLineWithStringItem(rowHeight, rowStart, index, pW);
         retRowStart = index < this.numOfItems - 1 ? index + 1 : index;
      } else {
         rowHeight = this.layoutRowHorizontal(rowStart, index - 1, this.viewable[2], rowHeight);
         this.layoutRowVertical(rowStart, index - 1, rowHeight, false);
      }

      this.view[3] = this.items[index - 1].lineY + rowHeight + CELL_SPACING;
      if (!this.boundsIncludeOtherItems) {
         this.viewable[0] = 0;
         this.viewable[2] = this.viewport[2];
      }

      this.viewable[1] = this.view[3];
      int[] var10000 = this.viewable;
      var10000[3] -= rowHeight + CELL_SPACING;
      int rowHeight = false;
      return retRowStart;
   }

   private int makeNewLineWithStringItem(int rowHeight, int rowStart, int index, int pW) {
      StringItem thisStringItem = (StringItem)this.items[index];
      int itemHeight;
      int k;
      if (this.items[index - 1] instanceof StringItem) {
         rowHeight = this.layoutRowHorizontal(rowStart, index - 1, 0, rowHeight);

         for(itemHeight = rowStart; itemHeight < index; ++itemHeight) {
            this.items[itemHeight].rowHeight = rowHeight;
         }

         this.appendStringItemToStringItem(rowHeight, rowStart, index, pW);
         pW = thisStringItem.callItemLabelPreferredWidth(thisStringItem.lockedHeight);
         thisStringItem.contentWidth = thisStringItem.callPreferredWidth(thisStringItem.lockedHeight);
         itemHeight = this.getItemHeight(index, pW);
         thisStringItem.bounds[3] = itemHeight;
         k = index - 1;

         for(thisStringItem.rowHeight = thisStringItem.bounds[3]; k > 0 && this.items[k].bounds[1] == thisStringItem.bounds[1]; --k) {
            this.items[k].rowHeight = thisStringItem.rowHeight;
         }

         rowHeight = this.layoutRowHorizontal(rowStart, k, 0, rowHeight);

         for(int j = rowStart; j <= k; ++j) {
            this.items[j].rowHeight = rowHeight;
         }
      } else {
         rowHeight = this.layoutRowHorizontal(rowStart, index - 1, 0, rowHeight);
         this.layoutRowVertical(rowStart, index - 1, rowHeight, false);
         thisStringItem.lineY = this.items[rowStart].lineY;
         thisStringItem.bounds[0] = 0;
         thisStringItem.setOffsetPosition(this.viewport[2] - this.viewable[2]);
         thisStringItem.setOffsetWidth(this.viewport[2] - (this.items[index - 1].bounds[0] + this.items[index - 1].bounds[2]) - CELL_SPACING);
         thisStringItem.setOffsetHeight(rowHeight);
         pW = thisStringItem.callItemLabelPreferredWidth(thisStringItem.lockedHeight);
         thisStringItem.contentWidth = thisStringItem.callPreferredWidth(thisStringItem.lockedHeight);
         itemHeight = this.getItemHeight(index, pW);
         thisStringItem.bounds[3] = itemHeight;
         thisStringItem.bounds[1] = thisStringItem.lineY + thisStringItem.verticalOffsetStringTranslation;
         rowHeight = thisStringItem.bounds[1] + thisStringItem.bounds[3] - thisStringItem.lineY > rowHeight ? thisStringItem.bounds[1] + thisStringItem.bounds[3] - thisStringItem.lineY : rowHeight;

         for(k = rowStart; k <= index; ++k) {
            this.items[k].rowHeight = rowHeight;
         }

         if (this.viewable[2] > 0 && this.viewable[2] < this.viewport[2] && (thisStringItem.textLines.isEmpty() || thisStringItem.textLinesOffset.isEmpty())) {
            thisStringItem.appendedToANonStringItem = true;
         }
      }

      return rowHeight;
   }

   private void appendStringItemToStringItem(int rowHeight, int rowStart, int index, int pW) {
      StringItem stringItem = (StringItem)this.items[index];
      StringItem prevStringItem = (StringItem)this.items[index - 1];
      stringItem.bounds[0] = 0;
      if (prevStringItem.currentOffsetHeight > prevStringItem.offsetStringHeight && prevStringItem.textLinesEmpty) {
         int offsetHeightThatRemains = prevStringItem.currentOffsetHeight - prevStringItem.offsetStringHeight;
         stringItem.setOffsetPosition(prevStringItem.offsetXPosition);
         stringItem.setOffsetWidth(prevStringItem.currentOffsetWidth);
         stringItem.setOffsetHeight(offsetHeightThatRemains);
         stringItem.bounds[1] = prevStringItem.bounds[1] + prevStringItem.bounds[3] + CELL_SPACING;
      } else {
         if (!prevStringItem.boundsIncludeOtherItems) {
            this.layoutRowVertical(rowStart, index - 1, rowHeight, true);
         }

         int horPrevLimit = prevStringItem.bounds[0];
         horPrevLimit += prevStringItem.lastLineWidth <= 0 ? 0 : prevStringItem.lastLineWidth + CELL_SPACING;
         int heightToUse = prevStringItem.rowHeight + prevStringItem.lineY - (prevStringItem.bounds[1] + prevStringItem.bounds[3]);
         if (heightToUse > 0) {
            stringItem.bounds[1] = prevStringItem.lineY + prevStringItem.bounds[3];
            stringItem.setOffsetPosition(prevStringItem.bounds[0]);
            stringItem.setOffsetWidth(this.viewport[2] - prevStringItem.bounds[0]);
            stringItem.setOffsetHeight(heightToUse);
         } else {
            stringItem.setOffsetPosition(horPrevLimit);
            stringItem.setOffsetWidth(this.viewport[2] - horPrevLimit);
            stringItem.setOffsetHeight(stringItem.lineHeight);
            stringItem.bounds[1] = prevStringItem.bounds[1] + prevStringItem.bounds[3] - prevStringItem.lineHeight;
         }
      }

      stringItem.lineY = stringItem.bounds[1];
   }

   private int getItemWidth(int index, int pW) {
      Item auxItem = this.items[index];
      if (auxItem.shouldHShrink()) {
         pW = auxItem.callItemLabelMinimumWidth();
         auxItem.contentWidth = auxItem.callMinimumWidth();
      } else if (auxItem.lockedWidth != -1) {
         pW = auxItem.lockedWidth;
         auxItem.contentWidth = pW;
      } else {
         pW = auxItem.callItemLabelPreferredWidth(auxItem.lockedHeight);
         auxItem.contentWidth = auxItem.callPreferredWidth(auxItem.lockedHeight);
      }

      pW = pW > this.viewport[2] ? this.viewport[2] : pW;
      return pW;
   }

   private int getItemHeight(int index, int pW) {
      Item thisItem = this.items[index];
      int pH;
      if (thisItem.shouldVShrink()) {
         pH = thisItem.callMinimumHeight() + thisItem.getLabelHeight(-1);
      } else {
         pH = thisItem.lockedHeight;
         if (pH == -1) {
            pH = thisItem.callPreferredHeight(pW) + thisItem.getLabelHeight(-1);
         }
      }

      return pH;
   }

   private int layoutRowHorizontal(int rowStart, int rowEnd, int hSpace, int rowHeight) {
      if (hSpace < 0) {
         hSpace = 0;
      }

      hSpace = this.inflateHShrinkables(rowStart, rowEnd, hSpace);
      hSpace = this.inflateHExpandables(rowStart, rowEnd, hSpace);
      rowHeight = 0;

      for(int i = rowStart; i <= rowEnd; ++i) {
         if (rowHeight < this.items[i].bounds[3]) {
            rowHeight = this.items[i].bounds[3];
         }
      }

      if (hSpace <= 0) {
         return rowHeight;
      } else {
         int[] var10000;
         if ((this.currentAlignment & 3) == 2) {
            while(rowStart <= rowEnd) {
               var10000 = this.items[rowEnd].bounds;
               var10000[0] += hSpace;
               --rowEnd;
            }
         } else if ((this.currentAlignment & 3) == 3) {
            for(hSpace /= 2; rowStart <= rowEnd; ++rowStart) {
               var10000 = this.items[rowStart].bounds;
               var10000[0] += hSpace;
            }
         }

         return rowHeight;
      }
   }

   private int inflateHShrinkables(int rowStart, int rowEnd, int space) {
      if (space == 0) {
         this.inflateHSkrinkablesNoSpace(rowStart, rowEnd);
         return 0;
      } else {
         int idealSpace = space;
         int somDiff = 0;
         int[] diffPrefMin = new int[rowEnd - rowStart + 1];

         Item auxItem;
         int diffL;
         for(diffL = rowStart; diffL <= rowEnd; ++diffL) {
            auxItem = this.items[diffL];
            if (auxItem.shouldHShrink()) {
               diffPrefMin[diffL - rowStart] = auxItem.callPreferredWidth(-1) - auxItem.callMinimumWidth();
               somDiff += diffPrefMin[diffL - rowStart];
               int diff = auxItem.bounds[2] - auxItem.contentWidth;
               idealSpace = diff > 0 ? idealSpace + diff : idealSpace;
            }
         }

         if (somDiff == 0) {
            this.inflateHSkrinkablesNoSpace(rowStart, rowEnd);
            space = this.viewport[2] - CELL_SPACING - (this.items[rowEnd].bounds[0] + this.items[rowEnd].bounds[2]);
            return space;
         } else {
            int auxSomDif = somDiff;
            int auxIdealSpace = idealSpace;

            int diffSpace;
            int i;
            int[] var10000;
            int j;
            for(i = rowStart; i <= rowEnd; ++i) {
               auxItem = this.items[i];
               diffL = auxItem.bounds[2] - auxItem.contentWidth;
               if (auxItem.shouldHShrink() && diffL > 0) {
                  int newW = auxItem.callMinimumWidth() + diffPrefMin[i - rowStart] * auxIdealSpace / auxSomDif;
                  newW = auxItem.callPreferredWidth(-1) < newW ? auxItem.callPreferredWidth(-1) : newW;
                  int dif = auxItem.bounds[2] - newW;
                  if (dif > 0) {
                     idealSpace -= dif;
                     auxItem.contentWidth = auxItem.bounds[2] > auxItem.callPreferredWidth(-1) ? auxItem.callPreferredWidth(-1) : auxItem.bounds[2];
                  } else {
                     diffSpace = newW - auxItem.bounds[2];
                     auxItem.bounds[2] = auxItem.contentWidth = newW;

                     for(j = i + 1; j <= rowEnd; ++j) {
                        var10000 = this.items[j].bounds;
                        var10000[0] += diffSpace;
                     }
                  }

                  somDiff -= diffPrefMin[i - rowStart];
                  diffPrefMin[i - rowStart] = 0;
                  auxItem.bounds[3] = this.getItemHeight(i, auxItem.bounds[2]);
               }
            }

            for(i = rowStart; i <= rowEnd; ++i) {
               auxItem = this.items[i];
               if (auxItem.shouldHShrink() && somDiff != 0) {
                  diffSpace = diffPrefMin[i - rowStart] * idealSpace / somDiff;
                  diffSpace = diffPrefMin[i - rowStart] < diffSpace ? diffPrefMin[i - rowStart] : diffSpace;
                  var10000 = auxItem.bounds;
                  var10000[2] += diffSpace;

                  for(j = i + 1; j <= rowEnd; ++j) {
                     var10000 = this.items[j].bounds;
                     var10000[0] += diffSpace;
                  }

                  auxItem.contentWidth = auxItem.bounds[2];
                  auxItem.bounds[3] = this.getItemHeight(i, auxItem.bounds[2]);
               }
            }

            space = this.viewport[2] - CELL_SPACING - (this.items[rowEnd].bounds[0] + this.items[rowEnd].bounds[2]);
            return space;
         }
      }
   }

   private int inflateHExpandables(int rowStart, int rowEnd, int space) {
      if (space == 0) {
         this.inflateHExpandablesNoSpace(rowStart, rowEnd);
         return 0;
      } else {
         int idealSpace = space;
         int somDiff = 0;
         int[] diffPrefMin = new int[rowEnd - rowStart + 1];

         Item auxItem;
         int auxSomDif;
         for(auxSomDif = rowStart; auxSomDif <= rowEnd; ++auxSomDif) {
            auxItem = this.items[auxSomDif];
            if (auxItem.shouldHExpand()) {
               diffPrefMin[auxSomDif - rowStart] = auxItem.callPreferredWidth(-1);
               somDiff += diffPrefMin[auxSomDif - rowStart];
               int diff = auxItem.bounds[2] - auxItem.contentWidth;
               if (diff > 0) {
                  idealSpace += diff;
               }
            }
         }

         if (somDiff == 0) {
            this.inflateHExpandablesNoSpace(rowStart, rowEnd);
            space = this.viewport[2] - CELL_SPACING - (this.items[rowEnd].bounds[0] + this.items[rowEnd].bounds[2]);
            return space;
         } else {
            auxSomDif = somDiff;
            int auxIdealSpace = idealSpace;

            int diffSpace;
            int i;
            int[] var10000;
            int j;
            for(i = rowStart; i <= rowEnd; ++i) {
               auxItem = this.items[i];
               if (auxItem.shouldHExpand()) {
                  int diffL = auxItem.bounds[2] - auxItem.contentWidth;
                  if (auxItem.shouldHExpand() && diffL > 0) {
                     int newW = auxItem.callPreferredWidth(-1) + diffPrefMin[i - rowStart] * auxIdealSpace / auxSomDif;
                     int dif = auxItem.bounds[2] - newW;
                     if (dif > 0) {
                        idealSpace -= dif;
                        auxItem.contentWidth = auxItem.bounds[2];
                     } else {
                        diffSpace = newW - auxItem.bounds[2];
                        auxItem.contentWidth = auxItem.bounds[2] = auxItem.contentWidth = newW;

                        for(j = i + 1; j <= rowEnd; ++j) {
                           var10000 = this.items[j].bounds;
                           var10000[0] += diffSpace;
                        }
                     }

                     diffPrefMin[i - rowStart] = 0;
                     auxItem.bounds[3] = this.getItemHeight(i, auxItem.bounds[2]);
                  }
               }
            }

            for(i = rowStart; i <= rowEnd; ++i) {
               auxItem = this.items[i];
               if (auxItem.shouldHExpand() && somDiff != 0) {
                  diffSpace = diffPrefMin[i - rowStart] * idealSpace / somDiff;
                  auxItem.bounds[2] = auxItem.contentWidth + diffSpace;

                  for(j = i + 1; j <= rowEnd; ++j) {
                     var10000 = this.items[j].bounds;
                     var10000[0] += diffSpace;
                  }

                  auxItem.contentWidth = auxItem.bounds[2];
                  auxItem.bounds[3] = this.getItemHeight(i, auxItem.bounds[2]);
               }
            }

            space = this.viewport[2] - CELL_SPACING - (this.items[rowEnd].bounds[0] + this.items[rowEnd].bounds[2]);
            return space;
         }
      }
   }

   private void inflateHExpandablesNoSpace(int rowStart, int rowEnd) {
      for(int i = rowStart; i <= rowEnd; ++i) {
         Item auxItem = this.items[i];
         if (auxItem.shouldHExpand() && auxItem.bounds[2] - auxItem.contentWidth > 0) {
            auxItem.contentWidth = auxItem.bounds[2];
         }
      }

   }

   private void inflateHSkrinkablesNoSpace(int rowStart, int rowEnd) {
      for(int i = rowStart; i <= rowEnd; ++i) {
         Item auxItem = this.items[i];
         if (auxItem.shouldHShrink() && auxItem.bounds[2] - auxItem.contentWidth > 0) {
            auxItem.contentWidth = auxItem.bounds[2] > auxItem.callPreferredWidth(-1) ? (auxItem.contentWidth = auxItem.callPreferredWidth(-1)) : auxItem.bounds[2];
         }
      }

   }

   private void layoutRowVertical(int rowStart, int rowEnd, int rowHeight, boolean forceLastToTop) {
      int space = false;
      int pH = false;

      for(int i = rowStart; i <= rowEnd; ++i) {
         Item auxItem = this.items[i];
         auxItem.rowHeight = rowHeight;
         if (auxItem.shouldVExpand()) {
            auxItem.bounds[3] = rowHeight;
         } else if (auxItem.shouldVShrink()) {
            int pH = auxItem.lockedHeight;
            if (pH == -1) {
               pH = auxItem.callPreferredHeight(auxItem.bounds[2]) + auxItem.getLabelHeight(-1);
            }

            if (pH > rowHeight) {
               pH = rowHeight;
            }

            auxItem.bounds[3] = pH;
         }

         int the_layout;
         if (i == rowEnd && forceLastToTop) {
            the_layout = 16;
         } else {
            the_layout = auxItem.callGetLayout() & 48;
         }

         int tempLineY = auxItem.bounds[1];
         if (rowStart != rowEnd) {
            int space;
            int[] var10000;
            switch(the_layout) {
            case 16:
               break;
            case 32:
            default:
               space = rowHeight - auxItem.bounds[3];
               if (space > 0) {
                  var10000 = auxItem.bounds;
                  var10000[1] += space;
               }
               break;
            case 48:
               space = rowHeight - auxItem.bounds[3];
               if (space > 0) {
                  var10000 = auxItem.bounds;
                  var10000[1] += space / 2;
               }
            }
         }

         auxItem.lineY = tempLineY;
      }

   }

   private int getFormHeight() {
      this.setTopLimit();
      this.setBottomLimit();
      return this.bottomLimit - this.topLimit;
   }

   private void paintScrollBar(Graphics g) {
      Zone scrollbarZone = this.getScrollbarZone();
      g.setClip(scrollbarZone.x, scrollbarZone.y, scrollbarZone.width, scrollbarZone.height);
      Displayable.uistyle.drawScrollbar(g.getImpl(), scrollbarZone, 1, this.formHeight, this.viewport[3], this.scroll - this.topLimit + 1, true);
   }

   private boolean isNewLine(int index, int pW, int rowStart, int rowHeight) {
      boolean newL = false;
      Item indexedItem = this.items[index];
      if (index == rowStart) {
         this.viewable[0] = 0;
         this.viewable[2] = this.viewport[2];
         this.viewable[1] = index > 0 ? this.items[index - 1].lineY + this.items[index - 1].rowHeight : this.topLimit;
         this.rowHasFocusableItems = indexedItem.isFocusable();
         return false;
      } else {
         newL = !IS_FOUR_WAY_SCROLL && this.rowHasFocusableItems && indexedItem.isFocusable();
         this.rowHasFocusableItems = indexedItem.isFocusable() ? true : this.rowHasFocusableItems;
         int presentItemLayout = indexedItem.callGetLayout() & 3;
         newL = newL || index > 0 && this.items[index - 1].equateNLA();
         newL = newL || indexedItem.equateNLB();
         boolean newAlignemnt = false;
         if ((indexedItem.layout & 3) != 0) {
            newAlignemnt = presentItemLayout != (this.currentAlignment & 3);
         } else if ((this.currentAlignment & 16384) == 0) {
            newAlignemnt = (this.preservedAlignment & 3) != (this.currentAlignment & 3);
         }

         if (index > rowStart && !newL) {
            if (!newAlignemnt) {
               this.boundsIncludeOtherItems = indexedItem.boundsIncludeOtherItems(rowHeight, pW, this.viewable[2]);
            }

            newL = newAlignemnt || pW > this.viewable[2] - CELL_SPACING || this.boundsIncludeOtherItems;
         }

         if (newAlignemnt) {
            if (indexedItem instanceof ImageItem && (indexedItem.layout & 16384) == 0) {
               if ((this.currentAlignment & 16384) != 0) {
                  this.preservedAlignment = this.currentAlignment;
               }

               this.pendingAlignment = presentItemLayout & 3;
            } else {
               if ((indexedItem.layout & 3) == 0) {
                  this.pendingAlignment = this.preservedAlignment;
               } else {
                  this.pendingAlignment = presentItemLayout & 3 | 16384;
               }

               this.preservedAlignment = 0;
            }
         }

         return newL;
      }
   }

   private void setNoneDirectionTraverseIndex() {
      if (this.traverseIndex != -1 && this.traverseIndex != 0) {
         this.setTraverseIndex(0, this.traverseIndex, this.traverseIndex);
      } else if (!this.items[0].isFocusable() && this.numOfItems != 1) {
         int newTraverseIndex = 1;
         boolean found = false;

         for(Item traversedItem = this.items[newTraverseIndex]; newTraverseIndex < this.numOfItems && traversedItem.lineY < this.viewport[3] && !found; ++newTraverseIndex) {
            traversedItem = this.items[newTraverseIndex];
            found = (traversedItem.lineY == this.items[0].lineY || traversedItem.bounds[1] + traversedItem.bounds[3] <= this.viewport[3]) && traversedItem.isFocusable();
         }

         if (found) {
            this.setTraverseIndex(0, this.traverseIndex, newTraverseIndex - 1);
         } else {
            this.setTraverseIndex(0, this.traverseIndex, 0);
         }
      } else {
         this.setTraverseIndex(0, this.traverseIndex, 0);
      }

   }

   private int getScrollingDirection(int dir) {
      int bendDir = dir;
      if (dir == 2 && !UIStyle.isAlignedLeftToRight) {
         bendDir = 5;
      } else if (dir == 5 && !UIStyle.isAlignedLeftToRight) {
         bendDir = 2;
      }

      return bendDir;
   }

   private int formScrollUp(int index) {
      int newIndex = index;
      int[] var10000;
      if (this.isTallRow) {
         if (this.scroll <= this.items[this.traverseIndex].lineY) {
            this.isTallRow = false;
         } else if (!this.items[this.traverseIndex].boundsIncludeOtherItems) {
            this.isTallRow = true;
            var10000 = this.view;
            var10000[1] -= this.getMinimumScroll(1);
            if (this.view[1] < this.items[this.traverseIndex].lineY) {
               this.view[1] = this.items[this.traverseIndex].lineY;
            }

            this.checkViewY();
            this.scroll = this.oldViewY = this.view[1];
            this.repaintFull();
            return -1;
         }
      }

      int ni1 = this.findNearestNeighbourUp();
      this.isTallRow = false;
      if (ni1 != -1) {
         newIndex = ni1;
      } else if (this.scroll >= this.items[this.traverseIndex].lineY - FORM_MAX_SCROLL) {
         this.isTallRow = true;
         var10000 = this.view;
         var10000[1] -= this.getMinimumScroll(1);
         this.checkViewY();
         this.scroll = this.oldViewY = this.view[1];
         this.repaintFull();
         return -1;
      }

      return newIndex;
   }

   private int formScrollDown(int index) {
      int newIndex = index;
      int beginningOfTheLineLastPage = this.items[this.traverseIndex].lineY + this.items[this.traverseIndex].rowHeight - this.viewport[3] > 0 ? this.items[this.traverseIndex].lineY + this.items[this.traverseIndex].rowHeight - this.viewport[3] : 0;
      int[] var10000;
      if (this.isTallRow) {
         if (this.scroll >= beginningOfTheLineLastPage) {
            this.isTallRow = false;
         } else if (this.traverseIndex < this.numOfItems - 1) {
            var10000 = this.view;
            var10000[1] += this.getMinimumScroll(6);
            if (this.view[1] > beginningOfTheLineLastPage + FORM_MAX_SCROLL) {
               this.view[1] = beginningOfTheLineLastPage + FORM_MAX_SCROLL;
            }

            this.checkViewY();
            this.scroll = this.oldViewY = this.view[1];
            this.repaintFull();
            return -1;
         }
      }

      int ni2 = this.findNearestNeighbourDown();
      this.isTallRow = false;
      if (ni2 != -1) {
         newIndex = ni2;
      } else if (this.scroll <= beginningOfTheLineLastPage + FORM_MAX_SCROLL) {
         this.isTallRow = true;
         var10000 = this.view;
         var10000[1] += this.getMinimumScroll(6);
         this.checkViewY();
         this.scroll = this.oldViewY = this.view[1];
         this.repaintFull();
         return -1;
      }

      return newIndex;
   }

   private int formScrollLeft(int oldIndex) {
      int newIndex = oldIndex;
      if (oldIndex <= 0) {
         return -1;
      } else {
         Item previousItem = this.items[oldIndex - 1];

         Item thisItem;
         for(thisItem = this.items[oldIndex]; !previousItem.isFocusable() && thisItem.lineY == previousItem.lineY && newIndex > 0; thisItem = this.items[newIndex]) {
            --newIndex;
            previousItem = this.items[newIndex - 1];
         }

         newIndex = thisItem.lineY == previousItem.lineY && newIndex > 0 ? previousItem.itemIndex : oldIndex;
         if (!previousItem.isFocusable()) {
            newIndex = oldIndex;
         }

         return newIndex;
      }
   }

   private int formScrollRight(int oldIndex) {
      int newIndex = oldIndex;
      if (oldIndex >= this.numOfItems - 1) {
         return -1;
      } else {
         Item nextItem = this.items[oldIndex + 1];

         Item thisItem;
         for(thisItem = this.items[oldIndex]; !nextItem.isFocusable() && thisItem.lineY == nextItem.lineY && newIndex < this.numOfItems - 1; thisItem = this.items[newIndex]) {
            ++newIndex;
            nextItem = this.items[newIndex + 1];
         }

         newIndex = thisItem.lineY == nextItem.lineY && newIndex < this.numOfItems - 1 ? nextItem.itemIndex : oldIndex;
         if (!nextItem.isFocusable()) {
            newIndex = oldIndex;
         }

         return newIndex;
      }
   }

   private void checkViewY() {
      int viewSaved = this.view[1];
      if (this.formHeight > this.viewport[3]) {
         this.view[1] = this.view[1] > this.bottomLimit - this.viewport[3] ? this.bottomLimit - this.viewport[3] : this.view[1];
         this.view[1] = this.view[1] < this.topLimit ? this.topLimit : this.view[1];
      } else {
         this.view[1] = this.topLimit;
      }

      if (UNICOM_FORM_SCROLLING) {
         this.view[1] = viewSaved;
      }

   }

   private void checkMinScroll(int dir) {
      if (dir == 1) {
         if (this.oldViewY - this.view[1] < FORM_MAX_SCROLL) {
            this.view[1] = this.oldViewY - FORM_MAX_SCROLL;
            this.scroll = this.view[1];
         }
      } else if (dir == 6 && this.view[1] - this.oldViewY < FORM_MAX_SCROLL) {
         this.view[1] = this.oldViewY + FORM_MAX_SCROLL;
         this.scroll = this.view[1];
      }

      this.checkViewY();
   }

   private boolean isItemPartiallyShown(Item item) {
      int upLimit = this.view[1] + FORM_MAX_SCROLL;
      int downLimit = this.view[1] + this.viewport[3] - FORM_MAX_SCROLL;
      boolean partiallyShown = item.bounds[1] + item.bounds[3] >= upLimit && item.bounds[1] <= downLimit;
      return partiallyShown ? true : item.bounds[1] + item.bounds[3] <= this.view[1] + this.viewport[3] && item.bounds[1] >= this.view[1];
   }

   private int findTraversableItem(int index) {
      int newIndex = index;
      boolean allSkipped = false;

      while(!allSkipped && this.items[newIndex].shouldSkipTraverse()) {
         ++newIndex;
         if (newIndex == this.numOfItems) {
            allSkipped = true;
         }
      }

      if (allSkipped) {
         if (index <= 0) {
            return -1;
         }

         newIndex = index - 1;

         while(this.items[newIndex].shouldSkipTraverse()) {
            --newIndex;
            if (newIndex == -1) {
               return -1;
            }
         }
      }

      this.formMode = 2;
      return newIndex;
   }

   private void initializeVisRect(int index) {
      Item tempItem = this.items[index];
      this.visRect[0] = this.visRect[1] = 0;
      this.visRect[2] = this.viewport[2] < tempItem.bounds[2] ? this.viewport[2] : tempItem.bounds[2];
      this.visRect[3] = this.viewport[3] < tempItem.bounds[3] ? this.viewport[3] : tempItem.bounds[3];
   }

   Zone getScrollbarZone() {
      return this.ticker != null ? Displayable.screenTickScrollbarZone : Displayable.screenNormScrollbarZone;
   }

   Command[] getExtraCommands() {
      Command[] cmdRetVal = null;
      if (this.traverseIndex >= 0) {
         cmdRetVal = this.items[this.traverseIndex].getExtraCommands();
      }

      return cmdRetVal;
   }

   boolean launchExtraCommand(Command c) {
      return this.traverseIndex >= 0 ? this.items[this.traverseIndex].launchExtraCommand(c) : false;
   }

   private void setTopLimit() {
      int i;
      for(i = 0; i < this.numOfItems && this.items[i].shouldSkipTraverse(); ++i) {
      }

      if (i >= this.numOfItems) {
         this.topLimit = 0;
      } else {
         Item firstTraversableItem = this.items[i];
         int referenceY = firstTraversableItem.lineY;
         this.scrollingIndex = i;
         this.topLimitIndex = this.goToTheEndOfRow(referenceY).itemIndex;
         if (this.thisRowHasASpacer(referenceY, this.topLimitIndex)) {
            referenceY = this.findMinTraversableBoundsY(referenceY);
         }

         this.topLimit = referenceY;
      }
   }

   private void setBottomLimit() {
      int i;
      for(i = this.numOfItems - 1; i > 0 && this.items[i].shouldSkipTraverse(); --i) {
      }

      if (i < 0) {
         this.bottomLimit = this.viewport[3];
      } else {
         Item lastTraversableItem = this.items[i];
         int refernceY = lastTraversableItem.lineY;
         int referenceHeight = lastTraversableItem.rowHeight;
         int referenceYPlusHeight = refernceY + referenceHeight;
         this.scrollingIndex = i;
         int index = this.goToTheEndOfRow(refernceY).itemIndex;
         if (this.thisRowHasASpacer(refernceY, index)) {
            referenceYPlusHeight = this.findMaxTraversableBoundsYPlusHeight(refernceY, index);
         }

         this.bottomLimit = referenceYPlusHeight;
      }
   }

   private boolean thisRowHasASpacer(int lineY, int index) {
      int i;
      for(i = index; i >= 0 && !this.items[i].shouldSkipTraverse() && this.items[i].lineY == lineY; --i) {
      }

      return i > 0 && this.items[i].lineY == lineY;
   }

   private int findMinTraversableBoundsY(int lineY) {
      int i = this.goToTheEndOfRow(lineY).itemIndex;

      int min;
      for(min = this.items[i].rowHeight; i >= 0 && this.items[i].lineY == lineY; --i) {
         if (!this.items[i].shouldSkipTraverse()) {
            int boundsY = this.items[i].bounds[1];
            min = boundsY < min ? boundsY : min;
         }
      }

      return min;
   }

   private int findMaxTraversableBoundsYPlusHeight(int lineY, int index) {
      int i = index;

      int max;
      for(max = this.items[index].lineY; i < this.numOfItems && this.items[i].lineY == lineY; ++i) {
         if (!this.items[i].shouldSkipTraverse()) {
            int boundsYPlusHeight = this.items[i].bounds[1] + this.items[i].bounds[3];
            max = boundsYPlusHeight > max ? boundsYPlusHeight : max;
         }
      }

      return max;
   }

   private void setHighlightedItem(Item i) {
      if (UNICOM_FORM_SCROLLING) {
         this.setTraverseIndex(0, this.traverseIndex, i.itemIndex);
         this.resetToTop = false;
      }

   }

   private void setTopItem(Item i) {
      if (UNICOM_FORM_SCROLLING) {
         this.topItem = true;
         this.ignoreTraverse = true;
         this.setTraverseIndex(0, this.traverseIndex, i.itemIndex);
         this.resetToTop = false;
         this.topItem = false;
      }

   }

   private void wrapAround(boolean wrapToTop) {
      if (UNICOM_FORM_SCROLLING) {
         this.traverseIndex = wrapToTop ? -1 : this.numOfItems - 1;
         this.view[0] = 0;
         this.view[1] = wrapToTop ? this.topLimit : this.bottomLimit;
         this.oldViewY = this.view[1];
         this.resetToTop = false;
         this.traverse(0);
      }

   }

   private void setTopAndHighlightedItems(Item i) {
      if (UNICOM_FORM_SCROLLING) {
         int height_pixels = this.getHeight();
         int itemsPerScreen = height_pixels / (i.getPreferredHeight() + 2);
         if (itemsPerScreen < 3) {
            itemsPerScreen = 3;
         } else if (itemsPerScreen > 9) {
            itemsPerScreen = 9;
         }

         int tempTop = i.itemIndex / itemsPerScreen * itemsPerScreen;
         this.setTopItem(this.items[tempTop]);
         this.setHighlightedItem(i);
      }

   }

   int getViewPortHeight() {
      return this.viewport[3];
   }

   static {
      CELL_SPACING = UIStyle.CELL_SPACING;
      FORM_MAX_SCROLL = UIStyle.FORM_MAX_SCROLL;
      IS_FOUR_WAY_SCROLL = UIStyle.isFourWayScroll();
      UNICOM_FORM_SCROLLING = false;
   }
}
