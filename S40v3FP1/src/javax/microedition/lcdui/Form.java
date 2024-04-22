package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;

public class Form extends Screen {
   static final int LAYOUT_HMASK = 3;
   static final int LAYOUT_VMASK = 48;
   private static final int FORM_TRAVERSE = 0;
   private static final int ITEM_TRAVERSE = 2;
   private static final int GROW_SIZE = 4;
   static final int CELL_SPACING;
   static final int FORM_MAX_SCROLL;
   static final boolean SCROLLS_VERTICAL = true;
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
   private int scrollingIndex;
   private int topLimitIndex;
   private int topLimit;
   private int bottomLimit;
   private boolean rowHasFocusableItems;
   static final boolean UNICOM_FORM_SCROLLING;
   private boolean topItem;
   private boolean ignoreTraverse;

   public Form(String var1) {
      this(var1, (Item[])null);
   }

   public Form(String var1, Item[] var2) {
      super(var1);
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
      synchronized(Display.LCDUILock) {
         this.deleteAllImpl();
         this.numOfItems = 0;
         this.resetToTop = true;
         this.view = new int[4];
         this.visRect = new int[4];
         if (var2 == null) {
            this.items = new Item[4];
         } else {
            this.items = new Item[var2.length > 4 ? var2.length : 4];

            int var4;
            for(var4 = 0; var4 < var2.length; ++var4) {
               if (var2[var4].owner != null) {
                  throw new IllegalStateException();
               }
            }

            for(var4 = 0; var4 < var2.length; ++var4) {
               this.insertImpl(this.numOfItems, var2[var4]);
            }

         }
      }
   }

   public int append(Item var1) {
      synchronized(Display.LCDUILock) {
         if (var1.owner != null) {
            throw new IllegalStateException();
         } else {
            return this.insertImpl(this.numOfItems, var1);
         }
      }
   }

   public int append(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.append((Item)(new StringItem((String)null, var1)));
      }
   }

   public int append(Image var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.append((Item)(new ImageItem((String)null, var1, 0, (String)null)));
      }
   }

   public void insert(int var1, Item var2) {
      synchronized(Display.LCDUILock) {
         if (var2.owner != null) {
            throw new IllegalStateException();
         } else if (var1 >= 0 && var1 <= this.numOfItems) {
            this.insertImpl(var1, var2);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void delete(int var1) {
      synchronized(Display.LCDUILock) {
         if (var1 >= 0 && var1 < this.numOfItems) {
            this.deleteImpl(var1);
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

   public void set(int var1, Item var2) {
      synchronized(Display.LCDUILock) {
         if (var2.owner != null) {
            throw new IllegalStateException();
         } else if (var1 >= 0 && var1 < this.numOfItems) {
            this.setImpl(var1, var2);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public Item get(int var1) {
      synchronized(Display.LCDUILock) {
         if (var1 >= 0 && var1 < this.numOfItems) {
            return this.items[var1];
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void setItemStateListener(ItemStateListener var1) {
      synchronized(Display.LCDUILock) {
         this.itemStateListener = var1;
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

   boolean midletCommandsSupported() {
      return this.numOfItems != 0 && this.traverseIndex >= 0 ? this.items[this.traverseIndex].midletCommandsSupported() : true;
   }

   void setCurrentItem(Item var1) {
      if (!this.layoutValid) {
         this.layout();
      }

      if (this.traverseIndex == -1 || this.items[this.traverseIndex] != var1 || UNICOM_FORM_SCROLLING) {
         if (UNICOM_FORM_SCROLLING && var1 instanceof CustomItem) {
            this.setTopAndHighlightedItems(var1);
         } else {
            this.setTraverseIndex(0, this.traverseIndex, var1.itemIndex);
            this.resetToTop = false;
         }
      }
   }

   void callShowNotify(Display var1) {
      super.callShowNotify(var1);
      Item var2 = null;
      synchronized(Display.LCDUILock) {
         if (!this.layoutValid) {
            this.layout();
         }

         if (this.resetToTop) {
            if (this.traverseIndex >= 0 && this.traverseIndex < this.numOfItems && this.items[this.traverseIndex].hasFocus) {
               var2 = this.items[this.traverseIndex];
            }

            this.traverseIndex = -1;
            this.view[0] = 0;
            this.view[1] = this.topLimit;
            this.oldViewY = this.view[1];
            this.resetToTop = false;
         }
      }

      if (var2 != null) {
         var2.callTraverseOut();
      }

      this.traverse(0);
      Displayable.uistyle.hideIndex();
   }

   void callHideNotify(Display var1) {
      super.callHideNotify(var1);

      for(int var2 = 0; var2 < this.numOfItems; ++var2) {
         try {
            if (this.items[var2].visible) {
               this.items[var2].callHideNotify();
            }
         } catch (Exception var4) {
         }
      }

   }

   void removedFromDisplayNotify(Display var1) {
      super.removedFromDisplayNotify(var1);
      Item var2 = null;
      synchronized(Display.LCDUILock) {
         if (this.traverseIndex != -1 && this.items[this.traverseIndex].hasFocus) {
            var2 = this.items[this.traverseIndex];
         }

         this.formMode = 0;
      }

      if (var2 != null) {
         var2.callTraverseOut();
      }

   }

   void callKeyPressed(int var1, int var2) {
      super.callKeyPressed(var1, var2);
      Item var3 = null;
      byte var4 = -1;
      switch(var1) {
      case -4:
         var4 = 5;
         break;
      case -3:
         var4 = 2;
         break;
      case -2:
         var4 = 6;
         break;
      case -1:
         var4 = 1;
      }

      synchronized(Display.LCDUILock) {
         if (this.numOfItems == 0 || this.traverseIndex < 0) {
            return;
         }

         var3 = this.items[this.traverseIndex];
      }

      if (var3.supportsInternalTraversal()) {
         var3.callKeyPressed(var1, var2);
      } else if (var4 != 1 && var4 != 6) {
         if (var4 != 2 && var4 != 5) {
            if (var1 != -6 && var1 != -7 && var1 != -5) {
               var3.callKeyPressed(var1, var2);
            }
         } else if (var3.supportHorizontalScrolling()) {
            var3.callKeyPressed(var1, var2);
         } else {
            this.traverse(var4);
         }
      } else {
         this.traverse(var4);
      }

   }

   void callKeyReleased(int var1, int var2) {
      super.callKeyReleased(var1, var2);
      Item var3 = null;
      synchronized(Display.LCDUILock) {
         if (this.numOfItems == 0 || this.traverseIndex < 0) {
            return;
         }

         var3 = this.items[this.traverseIndex];
      }

      if (var3 != null) {
         var3.callKeyReleased(var1, var2);
      }

   }

   void callKeyRepeated(int var1, int var2) {
      super.callKeyRepeated(var1, var2);
      Item var3 = null;
      byte var4 = -1;
      switch(var1) {
      case -4:
         var4 = 5;
         break;
      case -3:
         var4 = 2;
         break;
      case -2:
         var4 = 6;
         break;
      case -1:
         var4 = 1;
      }

      synchronized(Display.LCDUILock) {
         if (this.numOfItems == 0 || this.traverseIndex < 0) {
            return;
         }

         var3 = this.items[this.traverseIndex];
      }

      if (var3.supportsInternalTraversal()) {
         var3.callKeyRepeated(var1, var2);
      } else if (var4 != 1 && var4 != 6) {
         if (var4 != 2 && var4 != 5) {
            if (var1 != -6 && var1 != -7 && var1 != -5) {
               var3.callKeyRepeated(var1, var2);
            }
         } else if (var3.supportHorizontalScrolling()) {
            var3.callKeyRepeated(var1, var2);
         } else {
            this.traverse(var4);
         }
      } else {
         this.traverse(var4);
      }

   }

   void traverse(int var1) {
      switch(this.formMode) {
      case 0:
         try {
            this.formTraverse(var1);
         } catch (Throwable var4) {
         }
         break;
      case 2:
         try {
            if (!this.itemTraverse(var1)) {
               this.formTraverse(var1);
            }
         } catch (Throwable var3) {
         }
      }

   }

   void formTraverse(int var1) {
      if (this.numOfItems == 0) {
         this.updateSoftkeys(true);
      } else if (var1 == 0) {
         this.setNoneDirectionTraverseIndex();
      } else if (this.traverseIndex >= 0 && !this.items[this.traverseIndex].shouldSkipTraverse() && this.scrollForBounds(var1, this.items[this.traverseIndex].bounds)) {
         this.validateVisibility = true;
         this.repaintFull();
      } else {
         int var2 = this.getScrollingDirection(var1);
         int var4 = this.traverseIndex;

         do {
            int var3 = var4;
            switch(var2) {
            case 1:
               if (UNICOM_FORM_SCROLLING && this.traverseIndex == 0) {
                  this.wrapAround(false);
                  this.setTraverseIndex(1, var4, this.traverseIndex);
                  return;
               }

               var4 = this.formScrollUp(var4);
               break;
            case 2:
               var4 = this.formScrollLeft(var4);
            case 3:
            case 4:
            default:
               break;
            case 5:
               var4 = this.formScrollRight(var4);
               break;
            case 6:
               if (UNICOM_FORM_SCROLLING && this.traverseIndex == this.numOfItems - 1) {
                  this.wrapAround(true);
                  this.setTraverseIndex(6, var4, this.traverseIndex);
                  return;
               }

               var4 = this.formScrollDown(var4);
            }

            if (var4 == -1 || var3 == var4) {
               return;
            }
         } while(this.items[var4].shouldSkipTraverse());

         this.setTraverseIndex(var1, this.traverseIndex, var4);
      }
   }

   boolean itemTraverse(int var1) {
      if (this.traverseIndex == -1) {
         return false;
      } else {
         Item var2 = this.items[this.traverseIndex];
         int[] var3 = new int[]{this.visRect[0] + var2.bounds[0], this.visRect[1] + var2.bounds[1], this.visRect[2], this.visRect[3]};
         if (this.formMode == 2 && this.scrollForBounds(var1, var3)) {
            this.validateVisibility = true;
            this.repaintFull();
            return true;
         } else if (UNICOM_FORM_SCROLLING && this.ignoreTraverse) {
            this.ignoreTraverse = false;
            return false;
         } else {
            int var4 = this.visRect[1];
            if (var2.callTraverse(var1, this.viewport[2], this.viewport[3], this.visRect)) {
               if (var1 != 0) {
                  if (this.visRect[1] > var4) {
                     var1 = 6;
                  } else if (this.visRect[1] < var4) {
                     var1 = 1;
                  }
               }

               var3[1] = this.visRect[1] + var2.bounds[1];
               var3[3] = this.visRect[3];
               this.formMode = 2;
               if (this.scrollForTraversal(var1, var3)) {
                  this.validateVisibility = true;
                  this.repaintFull();
               }

               if (!this.items[this.traverseIndex].isFocusable()) {
                  this.checkMinScroll(var1);
               }

               this.oldViewY = this.view[1];
               return true;
            } else {
               return false;
            }
         }
      }
   }

   boolean scrollForBounds(int var1, int[] var2) {
      if (this.formHeight <= this.viewport[3]) {
         this.view[1] = this.topLimit;
      } else if (this.isTallRow) {
         this.view[1] = this.view[1] > this.bottomLimit - this.viewport[3] ? this.bottomLimit - this.viewport[3] : this.view[1];
         return false;
      }

      int[] var10000;
      switch(var1) {
      case 1:
         if (var2[1] >= this.view[1]) {
            return false;
         }

         var10000 = this.view;
         var10000[1] -= this.getMinimumScroll(var1);
         this.view[1] = this.view[1] <= this.topLimit ? this.topLimit : this.view[1];
         return true;
      case 2:
      case 3:
      case 4:
      case 5:
      default:
         return false;
      case 6:
         if (var2[1] + var2[3] <= this.view[1] + this.viewport[3]) {
            return false;
         } else {
            var10000 = this.view;
            var10000[1] += FORM_MAX_SCROLL;
            this.view[1] = this.view[1] > this.bottomLimit - this.viewport[3] ? this.bottomLimit - this.viewport[3] : this.view[1];
            return true;
         }
      }
   }

   boolean scrollForTraversal(int var1, int[] var2) {
      if (this.formHeight < this.viewport[3]) {
         if (this.view[1] != this.topLimit) {
            this.view[1] = this.topLimit;
            return true;
         } else {
            return false;
         }
      } else {
         Item var3 = this.items[this.traverseIndex];
         if (var3.visible && (var1 == 2 || var1 == 5) && var3.bounds[3] > this.viewport[3]) {
            return false;
         } else {
            boolean var4 = var2[1] >= this.view[1] && var2[1] + var2[3] <= this.view[1] + this.viewport[3];
            if (UNICOM_FORM_SCROLLING) {
               var4 &= !this.topItem;
            }

            if (var4) {
               return false;
            } else {
               if (var2[3] > this.viewport[3] - FORM_MAX_SCROLL) {
                  if (var1 == 6 || var1 == 0 || var1 == 5 && this.alignedLeftToRight || var1 == 2 && !this.alignedLeftToRight) {
                     this.view[1] = var2[1];
                  } else if (var1 == 1 || var1 == 5 && !this.alignedLeftToRight || var1 == 2 && this.alignedLeftToRight) {
                     this.view[1] = var2[1] + var2[3] - this.viewport[3];
                  }
               } else if (var1 != 6 && var1 != 0 && (var1 != 5 || !this.alignedLeftToRight) && (var1 != 2 || this.alignedLeftToRight)) {
                  if (var1 == 1 || var1 == 5 && !this.alignedLeftToRight || var1 == 2 && this.alignedLeftToRight) {
                     this.view[1] = var2[1];
                  }
               } else {
                  this.view[1] = var2[1] + var2[3] - this.viewport[3];
               }

               if (UNICOM_FORM_SCROLLING && this.topItem) {
                  this.view[1] = var2[1];
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
         int var1 = 0;
         if (this.viewable == null) {
            this.viewable = new int[4];
         }

         this.viewable[0] = 0;
         this.viewable[1] = 0;
         this.viewable[2] = this.viewport[2];
         this.viewable[3] = this.viewport[3];
         this.view[2] = this.viewable[2];
         this.view[3] = 0;
         int var2 = 0;
         int var3 = 0;
         boolean var4 = false;

         for(int var6 = 0; var6 < this.numOfItems; ++var6) {
            Item var5 = this.items[var6];
            if (var5.bounds == null) {
               var5.bounds = new int[4];
            } else {
               if (var5 instanceof StringItem) {
                  ((StringItem)var5).setToDefaults();
               }

               var5.bounds[0] = 0;
               var5.bounds[1] = 0;
               var5.bounds[2] = 0;
               var5.bounds[3] = 0;
            }

            var3 = this.getItemWidth(var6, var3);
            this.boundsIncludeOtherItems = false;
            boolean var7 = this.isNewLine(var6, var3, var1);
            if (var7 && var2 > 0 || this.boundsIncludeOtherItems) {
               var1 = this.makeNewLine(var2, var1, var6, var3);
               var3 = this.getItemWidth(var6, var3);
               var2 = 0;
            }

            int var8 = this.getItemHeight(var6, var3);
            if (var5.bounds[2] != var3 || var5.bounds[3] != var8) {
               var5.sizeChanged = true;
            }

            if (!this.boundsIncludeOtherItems) {
               var5.bounds[0] = this.viewable[0];
               var5.bounds[3] = var8;
               var5.bounds[1] = this.viewable[1];
            }

            var5.bounds[2] = var3;
            if (var3 > 0) {
               if (this.boundsIncludeOtherItems) {
                  this.viewable[0] = var5.bounds[0] + var5.bounds[2] + CELL_SPACING;
                  this.viewable[2] = this.viewport[2] - this.viewable[0];
               } else {
                  int[] var10000 = this.viewable;
                  var10000[2] -= var3 + CELL_SPACING;
                  var10000 = this.viewable;
                  var10000[0] += var3 + CELL_SPACING;
               }
            }

            var2 = var8 > var2 ? var8 : var2;
         }

         this.layoutLastLine(var1, var2, this.viewable[2]);
         this.rightToLeftAlignementSwitch();
         this.formHeight = this.getFormHeight();
         this.checkViewY();
      }
   }

   void callPaint(Graphics var1) {
      super.callPaint(var1);
      var1.setClip(this.viewport[0], this.viewport[1], this.viewport[2], this.viewport[3]);

      try {
         if (this.numOfItems == 0) {
            TextBreaker var5 = TextBreaker.getBreaker(Font.getDefaultFont().getImpl(), TextDatabase.getText(33), false);
            TextLine var6 = null;
            var6 = var5.getTextLine(32767);
            var6.setAlignment(2);
            var1.getImpl().drawText(var6, (short)(screenNormMainZone.width / 2), (short)this.getMainZone().y);
            var5.destroyBreaker();
            return;
         }

         int[] var2 = new int[]{var1.getClipX(), var1.getClipY(), var1.getClipWidth(), var1.getClipHeight()};
         if (var2[1] + var2[3] <= this.viewport[1]) {
            return;
         }

         this.scroll = this.oldViewY = this.view[1];

         for(int var3 = 0; var3 < this.numOfItems; ++var3) {
            this.paintItem(this.items[var3], var1, var2);
         }

         this.paintScrollBar(var1);
         var1.reset(var2[0], var2[1], var2[2], var2[3]);
      } catch (Throwable var4) {
      }

   }

   void paintItem(Item var1, Graphics var2, int[] var3) {
      if (var1.bounds != null) {
         int var4 = var1.bounds[0] + this.viewport[0] - this.view[0];
         int var5 = var1.bounds[1] + this.viewport[1] - this.view[1];
         if (var5 + var1.bounds[3] >= var3[1] && var5 <= var3[1] + var3[3] || this.validateVisibility) {
            var2.clipRect(var4, var5, var1.bounds[2], var1.bounds[3]);
            if (var2.getClipWidth() > 0 && var2.getClipHeight() > 0) {
               if (this.validateVisibility && !var1.visible) {
                  var1.callShowNotify();
               }

               if (var1.sizeChanged) {
                  var1.callSizeChanged(var1.bounds[2], var1.bounds[3]);
                  var1.sizeChanged = false;
               }

               var2.translate(var4, var5);
               if (var1.itemIndex == this.traverseIndex) {
                  var1.callPaint(var2, var1.bounds[2], var1.bounds[3], true);
               } else {
                  var1.callPaint(var2, var1.bounds[2], var1.bounds[3], false);
               }
            } else if (this.validateVisibility && var1.visible) {
               var1.callHideNotify();
            }

            var2.reset(var3[0], var3[1], var3[2], var3[3]);
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
         synchronized(Display.LCDUILock) {
            if (!this.layoutValid) {
               this.layout();
            }
         }

         this.traverse(0);
         this.repaintFull();
      }
   }

   void callItemStateChanged(Item var1) {
      ItemStateListener var2 = this.itemStateListener;
      if (var2 != null && var1 != null) {
         synchronized(Display.calloutLock) {
            var2.itemStateChanged(var1);
         }
      }
   }

   void repaintItem(Item var1, int var2, int var3, int var4, int var5) {
      this.repaintFull();
   }

   int insertImpl(int var1, Item var2) {
      this.currentAlignment = var1 != 0 ? this.currentAlignment : ((var2.layout & 3) == 0 ? this.currentAlignment : var2.layout & 3);
      if (this.myDisplay != null && this.myDisplay.getCurrentTopOfStackDisplayable().isDisplayableInStack(this)) {
         if (this.traverseIndex >= var1 || this.traverseIndex == -1) {
            ++this.traverseIndex;
         }
      } else {
         this.resetToTop = true;
      }

      if (this.items.length == this.numOfItems) {
         Item[] var3 = new Item[this.numOfItems + 4];
         System.arraycopy(this.items, 0, var3, 0, var1);
         System.arraycopy(this.items, var1, var3, var1 + 1, this.numOfItems - var1);
         this.items = var3;
      } else if (var1 != this.numOfItems) {
         System.arraycopy(this.items, var1, this.items, var1 + 1, this.numOfItems - var1);
      }

      ++this.numOfItems;
      this.items[var1] = null;
      this.setImpl(var1, var2);
      return var1;
   }

   void setImpl(int var1, Item var2) {
      Item var3 = this.items[var1];
      if (var3 != null) {
         var3.setOwner((Screen)null);
      }

      var2.setOwner(this);
      var2.itemIndex = var1;
      this.items[var1] = var2;

      for(int var4 = var1 + 1; var4 < this.numOfItems; this.items[var4].itemIndex = var4++) {
      }

      this.invalidate();
   }

   void deleteImpl(int var1) {
      if (this.myDisplay == null || !this.myDisplay.getCurrentTopOfStackDisplayable().isDisplayableInStack(this)) {
         this.resetToTop = true;
      }

      Item var2 = this.items[var1];
      var2.setOwner((Screen)null);
      --this.numOfItems;
      if (this.traverseIndex == var1) {
         this.formMode = 0;
         this.isTallRow = false;
      }

      if (this.traverseIndex > var1 || this.traverseIndex == this.numOfItems) {
         --this.traverseIndex;
      }

      if (var1 < this.numOfItems) {
         System.arraycopy(this.items, var1 + 1, this.items, var1, this.numOfItems - var1);
      }

      this.items[this.numOfItems] = null;
      if (this.numOfItems == 0 && this.items.length > 4) {
         this.items = new Item[4];
      }

      for(int var3 = var1; var3 < this.numOfItems; this.items[var3].itemIndex = var3++) {
      }

      if (var1 == 0) {
         this.updateSoftkeys(true);
      }

      this.invalidate();
   }

   void deleteAllImpl() {
      this.isTallRow = false;
      this.customItemExceptionThrown = false;
      this.currentAlignment = this.alignedLeftToRight ? 1 : 2;
      if (this.numOfItems != 0) {
         for(int var1 = 0; var1 < this.numOfItems; ++var1) {
            this.items[var1].itemIndex = -1;
            this.items[var1].setOwner((Screen)null);
            this.items[var1] = null;
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

   private void setTraverseIndex(int var1, int var2, int var3) {
      this.isTallRow = false;
      this.formMode = 0;
      if (var1 == 0) {
         var3 = this.findTraversableItem(var3);
         if (var3 == -1) {
            this.updateSoftkeys(true);
            return;
         }
      }

      this.traverseIndex = var3;
      if (this.items[this.traverseIndex].bounds != null) {
         this.initializeVisRect(this.traverseIndex);
         if (var2 >= 0 && var2 < this.numOfItems && this.items[var2].hasFocus) {
            this.items[var2].callTraverseOut();
         }

         this.scrollForTraversal(var1, this.items[this.traverseIndex].bounds);
         if (!this.items[this.traverseIndex].isFocusable()) {
            this.checkMinScroll(var1);
         }

         this.itemTraverse(var1);
         this.updateSoftkeys(true);
         this.validateVisibility = true;
         this.oldViewY = this.view[1];
         this.repaintFull();
      }
   }

   private int getMinimumScroll(int var1) {
      int var2 = -1;
      int var3 = this.items[this.traverseIndex].lineY;

      int var4;
      for(var4 = this.traverseIndex - 1; var4 >= 0 && var3 == this.items[var4].lineY; --var4) {
      }

      ++var4;

      for(; var4 < this.numOfItems && var3 == this.items[var4].lineY; ++var4) {
         int var5 = this.items[var4].getMinimumScroll(var1);
         if ((this.items[var4].isVisibleFrom(this.view[1] - var5, this.viewport[3]) && var1 == 1 || this.items[var4].isVisibleFrom(this.view[1] + var5, this.viewport[3]) && var1 == 6 || this.items[var4].visible) && (var2 == -1 || var5 < var2)) {
            var2 = var5;
         }
      }

      return var2;
   }

   private int findNearestNeighbourUp() {
      if (this.traverseIndex == -1) {
         return 0;
      } else {
         Item var1 = this.items[this.traverseIndex];
         int var4 = var1.lineY;
         int var5 = var1.rowHeight;
         int var6 = var1.callHighlightedX() + var1.callHighlightedWidth() / 2;
         int var7 = var1.bounds[1];
         this.scrollingIndex = this.traverseIndex;
         boolean var8 = false;
         Item var9 = this.goToTheEndOfRow(var4);
         int var10 = -1;

         while(true) {
            if (this.scrollingIndex >= 0 && (var8 || var7 - (var4 + var5) <= this.viewport[3] - FORM_MAX_SCROLL || this.view[1] - var9.lineY <= this.viewport[3] - FORM_MAX_SCROLL)) {
               if (!var8) {
                  var8 = this.bigSpacerInRowUp(var4, -1);
                  if (this.scrollingIndex < 0) {
                     return this.returnNotFocusableItemUp(var10);
                  }

                  if (!var8) {
                     var8 = this.bigSpacerInRowUp(this.items[this.scrollingIndex].lineY, this.scrollingIndex);
                     if (var8 && var10 != -1 && var4 < this.view[1]) {
                        return this.returnNotFocusableItemUp(var10);
                     }
                  } else {
                     var10 = this.getBigSpacerCaseSelectedItemUp(var4, var7);
                     if (var10 != -1) {
                        return var10;
                     }
                  }
               }

               if (this.scrollingIndex >= 0) {
                  var9 = this.items[this.scrollingIndex];
                  int var11 = this.findItemInRowUp(var8, var6);
                  var10 = var11 != -1 ? var11 : var10;
                  int var2 = var9.lineY;
                  int var3 = var9.rowHeight;
                  if ((var10 == -1 || !this.items[var10].isFocusable()) && (var10 == -1 || !var8 || this.formHeight <= this.viewport[3] || this.view[1] <= 0)) {
                     var4 = var2;
                     var5 = var3;
                     continue;
                  }

                  return var10;
               }
            }

            return this.returnNotFocusableItemUp(var10);
         }
      }
   }

   private int returnNotFocusableItemUp(int var1) {
      Item var2 = null;
      if (var1 >= 0 && var1 < this.numOfItems) {
         var2 = this.items[var1];
      }

      return var2 != null && this.formHeight > this.viewport[3] && this.view[1] > 0 && var2.bounds[1] < this.view[1] ? var1 : -1;
   }

   private int findNearestNeighbourDown() {
      if (this.traverseIndex == -1) {
         return 0;
      } else {
         Item var1 = this.items[this.traverseIndex];
         int var3 = var1.lineY;
         int var4 = var1.callHighlightedX() + var1.callHighlightedWidth() / 2;
         int var5 = var1.bounds[1] + var1.bounds[3];
         this.scrollingIndex = this.traverseIndex;
         boolean var6 = false;
         Item var7 = this.goToTheStartOfRow(var3);
         int var8 = -1;

         while(true) {
            if (this.scrollingIndex < this.numOfItems && (var6 || var3 - var5 <= this.viewport[3] - FORM_MAX_SCROLL || var7.lineY + var7.rowHeight <= this.view[1] + 2 * this.viewport[3] - FORM_MAX_SCROLL)) {
               if (!var6) {
                  var6 = this.bigSpacerInRowDown(var3, -1);
                  if (this.scrollingIndex >= this.numOfItems) {
                     return this.returnNotFocusableItemDown(var8);
                  }

                  if (!var6) {
                     var6 = this.bigSpacerInRowDown(this.items[this.scrollingIndex].lineY, this.scrollingIndex);
                     if (var6 && var8 != -1 && var3 > this.view[1] + this.viewport[3]) {
                        return this.returnNotFocusableItemDown(var8);
                     }
                  } else {
                     var8 = this.getBigSpacerCaseSelectedItem(var3, var5);
                     if (var8 != -1) {
                        return var8;
                     }
                  }
               }

               if (this.scrollingIndex < this.numOfItems) {
                  var7 = this.items[this.scrollingIndex];
                  int var2 = var7.lineY;
                  int var9 = this.findItemInRowDown(var6, var4);
                  var8 = var9 != -1 ? var9 : var8;
                  if ((var8 == -1 || !this.items[var8].isFocusable()) && (var8 == -1 || !var6 || this.view[1] >= this.formHeight - this.viewport[3] || this.formHeight <= this.viewport[3])) {
                     var3 = var2;
                     continue;
                  }

                  return var8;
               }
            }

            return this.returnNotFocusableItemDown(var8);
         }
      }
   }

   private int returnNotFocusableItemDown(int var1) {
      Item var2 = null;
      if (var1 >= 0 && var1 < this.numOfItems) {
         var2 = this.items[var1];
      }

      if (var2 != null && this.view[1] < this.formHeight - this.viewport[3] && this.formHeight > this.viewport[3] && var2.bounds[1] + var2.bounds[3] > this.view[1] + this.viewport[3] - FORM_MAX_SCROLL) {
         if (var2.bounds[1] <= this.view[1] + this.viewport[3] - FORM_MAX_SCROLL) {
            this.view[1] = var2.bounds[1] + this.viewport[3] <= this.formHeight ? var2.bounds[1] : this.formHeight - this.viewport[3];
         }

         return var1;
      } else {
         return -1;
      }
   }

   private int findItemInRowUp(boolean var1, int var2) {
      Item var3 = this.items[this.scrollingIndex];
      Item var4 = this.items[this.traverseIndex];
      int var5 = var3.lineY;
      int var6 = -1;
      int var7 = -1;
      int var9 = -1;

      int var10;
      for(var10 = -1; this.scrollingIndex >= 0 && var3.lineY == var5; var3 = this.items[this.scrollingIndex]) {
         if (this.scrollingIndex != this.traverseIndex && !var3.shouldSkipTraverse() && var3.bounds[1] + var3.bounds[3] < var4.bounds[1] + var4.bounds[3] && (var3.bounds[1] + var3.bounds[3] > this.view[1] - FORM_MAX_SCROLL + CELL_SPACING || this.view[1] - var3.lineY <= this.viewport[3] - FORM_MAX_SCROLL || var1)) {
            int var11 = var3.callHighlightedX();
            int var12 = var3.callHighlightedWidth();
            if (this.isCrossing(var2, var11, var12)) {
               if (var3.isFocusable()) {
                  return this.scrollingIndex;
               }

               var9 = this.scrollingIndex;
               var6 = 0;
            }

            int var8 = this.borderDistance(var2, var11, var12);
            if (var3.isFocusable() && (var7 == -1 || var8 < var7)) {
               var7 = var8;
               var10 = this.scrollingIndex;
            }

            if (var6 == -1 || var8 < var6) {
               var6 = var8;
               var9 = this.scrollingIndex;
            }
         }

         --this.scrollingIndex;
         if (this.scrollingIndex < 0) {
            break;
         }
      }

      return var10 != -1 ? var10 : var9;
   }

   private int findItemInRowDown(boolean var1, int var2) {
      Item var3 = this.items[this.scrollingIndex];
      Item var4 = this.items[this.traverseIndex];
      int var5 = var3.lineY;
      int var6 = -1;
      int var7 = -1;
      boolean var8 = var3.lineY == var5;
      int var9 = -1;

      int var10;
      for(var10 = -1; this.scrollingIndex < this.numOfItems && var8; var8 = var3.lineY == var5) {
         if (this.scrollingIndex != this.traverseIndex && !var3.shouldSkipTraverse() && var3.bounds[1] + var3.bounds[3] > var4.bounds[1] + var4.bounds[3] && (var3.bounds[1] < this.view[1] + (this.viewport[3] - FORM_MAX_SCROLL) + CELL_SPACING || var3.lineY + var3.rowHeight <= this.view[1] + 2 * this.viewport[3] - FORM_MAX_SCROLL || var1)) {
            int var11 = var3.callHighlightedX();
            int var12 = var3.callHighlightedWidth();
            if (this.isCrossing(var2, var11, var12)) {
               if (var3.isFocusable()) {
                  return this.scrollingIndex;
               }

               var9 = this.scrollingIndex;
               var6 = 0;
            }

            int var13 = this.borderDistance(var2, var11, var12);
            if (var3.isFocusable() && (var7 == -1 || var13 < var7)) {
               var7 = var13;
               var10 = this.scrollingIndex;
            }

            if (var6 == -1 || var13 < var6) {
               var6 = var13;
               var9 = this.scrollingIndex;
            }
         }

         ++this.scrollingIndex;
         if (this.scrollingIndex >= this.numOfItems) {
            break;
         }

         var3 = this.items[this.scrollingIndex];
      }

      return var10 != -1 ? var10 : var9;
   }

   private Item goToTheStartOfRow(int var1) {
      while(this.scrollingIndex >= 0 && this.items[this.scrollingIndex].lineY == var1) {
         --this.scrollingIndex;
      }

      ++this.scrollingIndex;
      return this.items[this.scrollingIndex];
   }

   private Item goToTheEndOfRow(int var1) {
      while(this.scrollingIndex <= this.numOfItems - 1 && this.items[this.scrollingIndex].lineY == var1) {
         ++this.scrollingIndex;
      }

      --this.scrollingIndex;
      return this.items[this.scrollingIndex];
   }

   private int getBigSpacerCaseSelectedItemUp(int var1, int var2) {
      int var3 = -1;
      int var4 = -1;
      int var6 = -1;
      int var7 = -1;
      int var8 = this.scrollingIndex + 1;

      for(Item var9 = this.items[var8]; var9 != null && var9.lineY == var1 && !var9.shouldSkipTraverse(); var9 = var8 < this.numOfItems ? this.items[var8] : null) {
         if (var9.bounds[1] < this.view[1]) {
            int var5 = this.borderDistance(var2, var9.bounds[1], var9.bounds[3]);
            if (var8 != this.traverseIndex && (var3 == -1 || var3 > var5)) {
               var3 = var5;
               var6 = var8;
            }

            if (var8 != this.traverseIndex && var9.isFocusable() && (var4 == -1 || var4 > var5)) {
               var4 = var5;
               var7 = var8;
            }
         }

         ++var8;
      }

      if (var7 == -1) {
         var7 = var6;
      }

      return var7;
   }

   private int getBigSpacerCaseSelectedItem(int var1, int var2) {
      int var3 = -1;
      int var4 = -1;
      int var6 = -1;
      int var7 = -1;
      int var8 = this.scrollingIndex - 1;

      for(Item var9 = this.items[var8]; var9 != null && var9.lineY == var1 && !var9.shouldSkipTraverse(); var9 = var8 >= 0 ? this.items[var8] : null) {
         if (var9.bounds[1] >= this.view[1] + this.viewport[3] - FORM_MAX_SCROLL) {
            int var5 = this.borderDistance(var2, var9.bounds[1], var9.bounds[3]);
            if (var8 != this.traverseIndex && (var3 == -1 || var3 > var5)) {
               var3 = var5;
               var6 = var8;
            }

            if (var8 != this.traverseIndex && var9.isFocusable() && (var4 == -1 || var4 > var5)) {
               var4 = var5;
               var7 = var8;
            }
         }

         --var8;
      }

      if (var7 == -1) {
         var7 = var6;
      }

      return var7;
   }

   private boolean bigSpacerInRowDown(int var1, int var2) {
      int var3 = var2 == -1 ? this.scrollingIndex : var2;
      if (var3 > this.numOfItems - 1) {
         return false;
      } else {
         boolean var4 = false;

         for(Item var5 = this.items[var3]; var5.lineY == var1; var5 = this.items[var3]) {
            if (!var4 && var5.shouldSkipTraverse() && var5.bounds[3] > this.viewport[3] - FORM_MAX_SCROLL) {
               var4 = true;
            }

            ++var3;
            if (var3 > this.numOfItems - 1) {
               break;
            }
         }

         this.scrollingIndex = var2 == -1 ? var3 : this.scrollingIndex;
         return var4;
      }
   }

   private boolean bigSpacerInRowUp(int var1, int var2) {
      int var3 = var2 == -1 ? this.scrollingIndex : var2;
      if (var3 < 0) {
         return false;
      } else {
         Item var4 = this.items[var3];

         boolean var5;
         for(var5 = false; var4.lineY == var1; var4 = this.items[var3]) {
            if (!var5 && var4.shouldSkipTraverse() && var4.bounds[3] > this.viewport[3] - FORM_MAX_SCROLL) {
               var5 = true;
            }

            --var3;
            if (var3 < 0) {
               break;
            }
         }

         this.scrollingIndex = var2 == -1 ? var3 : this.scrollingIndex;
         return var5;
      }
   }

   private boolean isCrossing(int var1, int var2, int var3) {
      return var1 >= var2 && var1 <= var2 + var3;
   }

   private int borderDistance(int var1, int var2, int var3) {
      int var4 = var2 - var1;
      int var5 = var2 + var3 - var1;
      var4 = var4 >= 0 ? var4 : -var4;
      var5 = var5 >= 0 ? var5 : -var5;
      return var4 < var5 ? var4 : var5;
   }

   private void layoutLastLine(int var1, int var2, int var3) {
      if (!this.boundsIncludeOtherItems) {
         var2 = this.layoutRowHorizontal(var1, this.numOfItems - 1, var3, var2);
         this.layoutRowVertical(var1, this.numOfItems - 1, var2);
         this.view[3] = this.items[this.numOfItems - 1].lineY + var2;
      }

   }

   private void rightToLeftAlignementSwitch() {
      for(int var2 = 0; !this.alignedLeftToRight && var2 < this.numOfItems; ++var2) {
         Item var1 = this.items[var2];
         var1.bounds[0] = this.viewport[2] - var1.bounds[0] - var1.bounds[2];
         if (var1.boundsIncludeOtherItems) {
            ((StringItem)var1).setOffsetPosition(0);
         }
      }

   }

   private int makeNewLine(int var1, int var2, int var3, int var4) {
      int var5 = var3;
      if (var3 > 0 && this.boundsIncludeOtherItems) {
         var1 = this.makeNewLineWithStringItem(var1, var2, var3, var4);
         var5 = var3 < this.numOfItems - 1 ? var3 + 1 : var3;
      } else {
         var1 = this.layoutRowHorizontal(var2, var3 - 1, this.viewable[2], var1);
         this.layoutRowVertical(var2, var3 - 1, var1);
      }

      this.view[3] = this.items[var3 - 1].lineY + var1 + CELL_SPACING;
      if (!this.boundsIncludeOtherItems) {
         this.viewable[0] = 0;
         this.viewable[2] = this.viewport[2];
      }

      this.viewable[1] = this.view[3];
      int[] var10000 = this.viewable;
      var10000[3] -= var1 + CELL_SPACING;
      boolean var6 = false;
      return var5;
   }

   private int makeNewLineWithStringItem(int var1, int var2, int var3, int var4) {
      StringItem var5 = (StringItem)this.items[var3];
      int var6;
      int var7;
      if (this.items[var3 - 1] instanceof StringItem) {
         var1 = this.layoutRowHorizontal(var2, var3 - 1, 0, var1);

         for(var6 = var2; var6 < var3; ++var6) {
            this.items[var6].rowHeight = var1;
         }

         this.appendStringItemToStringItem(var1, var2, var3, var4);
         var4 = var5.callItemLabelPreferredWidth(var5.lockedHeight);
         var5.contentWidth = var5.callPreferredWidth(var5.lockedHeight);
         var6 = this.getItemHeight(var3, var4);
         var5.bounds[3] = var6;
         var7 = var3 - 1;

         for(var5.rowHeight = var5.bounds[3]; var7 > 0 && this.items[var7].bounds[1] == var5.bounds[1]; --var7) {
            this.items[var7].rowHeight = var5.rowHeight;
         }

         var1 = this.layoutRowHorizontal(var2, var7, 0, var1);

         for(int var8 = var2; var8 <= var7; ++var8) {
            this.items[var8].rowHeight = var1;
         }
      } else {
         var1 = this.layoutRowHorizontal(var2, var3 - 1, 0, var1);
         this.layoutRowVertical(var2, var3 - 1, var1);
         var5.lineY = this.items[var2].lineY;
         var5.bounds[0] = 0;
         var5.setOffsetPosition(this.viewport[2] - this.viewable[2]);
         var5.setOffsetWidth(this.viewport[2] - (this.items[var3 - 1].bounds[0] + this.items[var3 - 1].bounds[2]) - CELL_SPACING);
         var5.setOffsetHeight(var1);
         var4 = var5.callItemLabelPreferredWidth(var5.lockedHeight);
         var5.contentWidth = var5.callPreferredWidth(var5.lockedHeight);
         var6 = this.getItemHeight(var3, var4);
         var5.bounds[3] = var6;
         var5.bounds[1] = var5.lineY + var5.verticalOffsetStringTranslation;
         var1 = var5.bounds[1] + var5.bounds[3] - var5.lineY > var1 ? var5.bounds[1] + var5.bounds[3] - var5.lineY : var1;

         for(var7 = var2; var7 <= var3; ++var7) {
            this.items[var7].rowHeight = var1;
         }

         if (this.viewable[2] > 0 && this.viewable[2] < this.viewport[2] && (var5.textLines.isEmpty() || var5.textLinesOffset.isEmpty())) {
            var5.appendedToANonStringItem = true;
         }
      }

      return var1;
   }

   private void appendStringItemToStringItem(int var1, int var2, int var3, int var4) {
      StringItem var5 = (StringItem)this.items[var3];
      StringItem var7 = (StringItem)this.items[var3 - 1];
      var5.bounds[0] = 0;
      if (var7.currentOffsetHeight > var7.offsetStringHeight && var7.textLinesEmpty) {
         int var6 = var7.currentOffsetHeight - var7.offsetStringHeight;
         var5.setOffsetPosition(var7.offsetXPosition);
         var5.setOffsetWidth(var7.currentOffsetWidth);
         var5.setOffsetHeight(var6);
         var5.bounds[1] = var7.bounds[1] + var7.bounds[3] + CELL_SPACING;
      } else {
         if (!var7.boundsIncludeOtherItems) {
            this.layoutRowVertical(var2, var3 - 1, var1);
         }

         int var8 = var7.bounds[0];
         var8 += var7.lastLineWidth <= 0 ? 0 : var7.lastLineWidth + CELL_SPACING;
         int var9 = var7.rowHeight + var7.lineY - (var7.bounds[1] + var7.bounds[3]);
         if (var9 > 0) {
            var5.bounds[1] = var7.lineY + var7.bounds[3];
            var5.setOffsetPosition(var7.bounds[0]);
            var5.setOffsetWidth(this.viewport[2] - var7.bounds[0]);
            var5.setOffsetHeight(var9);
         } else {
            var5.setOffsetPosition(var8);
            var5.setOffsetWidth(this.viewport[2] - var8);
            var5.setOffsetHeight(var5.lineHeight);
            var5.bounds[1] = var7.bounds[1] + var7.bounds[3] - var7.lineHeight;
         }
      }

      var5.lineY = var5.bounds[1];
   }

   private int getItemWidth(int var1, int var2) {
      Item var3 = this.items[var1];
      if (var3.shouldHShrink()) {
         var2 = var3.callItemLabelMinimumWidth();
         var3.contentWidth = var3.callMinimumWidth();
      } else if (var3.lockedWidth != -1) {
         var2 = var3.lockedWidth;
         var3.contentWidth = var2;
      } else {
         var2 = var3.callItemLabelPreferredWidth(var3.lockedHeight);
         var3.contentWidth = var3.callPreferredWidth(var3.lockedHeight);
      }

      var2 = var2 > this.viewport[2] ? this.viewport[2] : var2;
      return var2;
   }

   private int getItemHeight(int var1, int var2) {
      Item var4 = this.items[var1];
      int var3;
      if (var4.shouldVShrink()) {
         var3 = var4.callMinimumHeight() + var4.getLabelHeight(-1);
      } else {
         var3 = var4.lockedHeight;
         if (var3 == -1) {
            var3 = var4.callPreferredHeight(var2) + var4.getLabelHeight(-1);
         }
      }

      return var3;
   }

   private int layoutRowHorizontal(int var1, int var2, int var3, int var4) {
      if (var3 < 0) {
         var3 = 0;
      }

      var3 = this.inflateHShrinkables(var1, var2, var3);
      var3 = this.inflateHExpandables(var1, var2, var3);
      var4 = 0;

      int var5;
      for(var5 = var1; var5 <= var2; ++var5) {
         if (var4 < this.items[var5].bounds[3]) {
            var4 = this.items[var5].bounds[3];
         }
      }

      if (var3 <= 0) {
         return var4;
      } else {
         int[] var10000;
         while(var1 <= var2) {
            var5 = this.items[var2].callGetLayout() & 3;
            if (!this.alignedLeftToRight) {
               if (var5 == 2) {
                  var5 = 1;
               } else if (var5 == 1) {
                  var5 = 2;
               }
            }

            if (var5 != 2) {
               break;
            }

            var10000 = this.items[var2].bounds;
            var10000[0] += var3;
            --var2;
         }

         for(var3 /= 2; var1 <= var2; ++var1) {
            var5 = this.items[var1].callGetLayout() & 3;
            if (!this.alignedLeftToRight) {
               if (var5 == 2) {
                  var5 = 1;
               } else if (var5 == 1) {
                  var5 = 2;
               }
            }

            if (var5 != 1) {
               if (var5 != 3) {
                  break;
               }

               var10000 = this.items[var1].bounds;
               var10000[0] += var3;
            }
         }

         return var4;
      }
   }

   private int inflateHShrinkables(int var1, int var2, int var3) {
      if (var3 == 0) {
         this.inflateHSkrinkablesNoSpace(var1, var2);
         return 0;
      } else {
         int var4 = var3;
         int var5 = 0;
         int[] var6 = new int[var2 - var1 + 1];

         Item var11;
         int var12;
         for(var12 = var1; var12 <= var2; ++var12) {
            var11 = this.items[var12];
            if (var11.shouldHShrink()) {
               var6[var12 - var1] = var11.callPreferredWidth(-1) - var11.callMinimumWidth();
               var5 += var6[var12 - var1];
               int var9 = var11.bounds[2] - var11.contentWidth;
               var4 = var9 > 0 ? var4 + var9 : var4;
            }
         }

         if (var5 == 0) {
            this.inflateHSkrinkablesNoSpace(var1, var2);
            var3 = this.viewport[2] - CELL_SPACING - (this.items[var2].bounds[0] + this.items[var2].bounds[2]);
            return var3;
         } else {
            int var13 = var5;
            int var14 = var4;

            int var7;
            int var15;
            int[] var10000;
            int var16;
            for(var15 = var1; var15 <= var2; ++var15) {
               var11 = this.items[var15];
               var12 = var11.bounds[2] - var11.contentWidth;
               if (var11.shouldHShrink() && var12 > 0) {
                  int var8 = var11.callMinimumWidth() + var6[var15 - var1] * var14 / var13;
                  var8 = var11.callPreferredWidth(-1) < var8 ? var11.callPreferredWidth(-1) : var8;
                  int var10 = var11.bounds[2] - var8;
                  if (var10 > 0) {
                     var4 -= var10;
                     var11.contentWidth = var11.bounds[2] > var11.callPreferredWidth(-1) ? var11.callPreferredWidth(-1) : var11.bounds[2];
                  } else {
                     var7 = var8 - var11.bounds[2];
                     var11.bounds[2] = var11.contentWidth = var8;

                     for(var16 = var15 + 1; var16 <= var2; ++var16) {
                        var10000 = this.items[var16].bounds;
                        var10000[0] += var7;
                     }
                  }

                  var5 -= var6[var15 - var1];
                  var6[var15 - var1] = 0;
                  var11.bounds[3] = this.getItemHeight(var15, var11.bounds[2]);
               }
            }

            for(var15 = var1; var15 <= var2; ++var15) {
               var11 = this.items[var15];
               if (var11.shouldHShrink() && var5 != 0) {
                  var7 = var6[var15 - var1] * var4 / var5;
                  var7 = var6[var15 - var1] < var7 ? var6[var15 - var1] : var7;
                  var10000 = var11.bounds;
                  var10000[2] += var7;

                  for(var16 = var15 + 1; var16 <= var2; ++var16) {
                     var10000 = this.items[var16].bounds;
                     var10000[0] += var7;
                  }

                  var11.contentWidth = var11.bounds[2];
                  var11.bounds[3] = this.getItemHeight(var15, var11.bounds[2]);
               }
            }

            var3 = this.viewport[2] - CELL_SPACING - (this.items[var2].bounds[0] + this.items[var2].bounds[2]);
            return var3;
         }
      }
   }

   private int inflateHExpandables(int var1, int var2, int var3) {
      if (var3 == 0) {
         this.inflateHExpandablesNoSpace(var1, var2);
         return 0;
      } else {
         int var4 = var3;
         int var7 = 0;
         int[] var8 = new int[var2 - var1 + 1];

         Item var11;
         int var12;
         for(var12 = var1; var12 <= var2; ++var12) {
            var11 = this.items[var12];
            if (var11.shouldHExpand()) {
               var8[var12 - var1] = var11.callPreferredWidth(-1);
               var7 += var8[var12 - var1];
               int var6 = var11.bounds[2] - var11.contentWidth;
               if (var6 > 0) {
                  var4 += var6;
               }
            }
         }

         if (var7 == 0) {
            this.inflateHExpandablesNoSpace(var1, var2);
            var3 = this.viewport[2] - CELL_SPACING - (this.items[var2].bounds[0] + this.items[var2].bounds[2]);
            return var3;
         } else {
            var12 = var7;
            int var13 = var4;

            int var5;
            int var15;
            int[] var10000;
            int var16;
            for(var15 = var1; var15 <= var2; ++var15) {
               var11 = this.items[var15];
               if (var11.shouldHExpand()) {
                  int var14 = var11.bounds[2] - var11.contentWidth;
                  if (var11.shouldHExpand() && var14 > 0) {
                     int var9 = var11.callPreferredWidth(-1) + var8[var15 - var1] * var13 / var12;
                     int var10 = var11.bounds[2] - var9;
                     if (var10 > 0) {
                        var4 -= var10;
                        var11.contentWidth = var11.bounds[2];
                     } else {
                        var5 = var9 - var11.bounds[2];
                        var11.contentWidth = var11.bounds[2] = var11.contentWidth = var9;

                        for(var16 = var15 + 1; var16 <= var2; ++var16) {
                           var10000 = this.items[var16].bounds;
                           var10000[0] += var5;
                        }
                     }

                     var8[var15 - var1] = 0;
                     var11.bounds[3] = this.getItemHeight(var15, var11.bounds[2]);
                  }
               }
            }

            for(var15 = var1; var15 <= var2; ++var15) {
               var11 = this.items[var15];
               if (var11.shouldHExpand() && var7 != 0) {
                  var5 = var8[var15 - var1] * var4 / var7;
                  var11.bounds[2] = var11.contentWidth + var5;

                  for(var16 = var15 + 1; var16 <= var2; ++var16) {
                     var10000 = this.items[var16].bounds;
                     var10000[0] += var5;
                  }

                  var11.contentWidth = var11.bounds[2];
                  var11.bounds[3] = this.getItemHeight(var15, var11.bounds[2]);
               }
            }

            var3 = this.viewport[2] - CELL_SPACING - (this.items[var2].bounds[0] + this.items[var2].bounds[2]);
            return var3;
         }
      }
   }

   private void inflateHExpandablesNoSpace(int var1, int var2) {
      for(int var4 = var1; var4 <= var2; ++var4) {
         Item var3 = this.items[var4];
         if (var3.shouldHExpand() && var3.bounds[2] - var3.contentWidth > 0) {
            var3.contentWidth = var3.bounds[2];
         }
      }

   }

   private void inflateHSkrinkablesNoSpace(int var1, int var2) {
      for(int var4 = var1; var4 <= var2; ++var4) {
         Item var3 = this.items[var4];
         if (var3.shouldHShrink() && var3.bounds[2] - var3.contentWidth > 0) {
            var3.contentWidth = var3.bounds[2] > var3.callPreferredWidth(-1) ? (var3.contentWidth = var3.callPreferredWidth(-1)) : var3.bounds[2];
         }
      }

   }

   private void layoutRowVertical(int var1, int var2, int var3) {
      boolean var4 = false;
      boolean var5 = false;

      for(int var8 = var1; var8 <= var2; ++var8) {
         Item var7 = this.items[var8];
         var7.rowHeight = var3;
         if (var7.shouldVExpand()) {
            var7.bounds[3] = var3;
         } else if (var7.shouldVShrink()) {
            int var10 = var7.lockedHeight;
            if (var10 == -1) {
               var10 = var7.callPreferredHeight(var7.bounds[2]) + var7.getLabelHeight(-1);
            }

            if (var10 > var3) {
               var10 = var3;
            }

            var7.bounds[3] = var10;
         }

         int var6 = var7.bounds[1];
         if (var1 != var2) {
            int[] var10000;
            int var9;
            switch(var7.callGetLayout() & 48) {
            case 16:
            default:
               break;
            case 32:
               var9 = var3 - var7.bounds[3];
               if (var9 > 0) {
                  var10000 = var7.bounds;
                  var10000[1] += var9;
               }
               break;
            case 48:
               var9 = var3 - var7.bounds[3];
               if (var9 > 0) {
                  var10000 = var7.bounds;
                  var10000[1] += var9 / 2;
               }
            }
         }

         var7.lineY = var6;
      }

   }

   private int getFormHeight() {
      this.setTopLimit();
      this.setBottomLimit();
      return this.bottomLimit - this.topLimit;
   }

   private void paintScrollBar(Graphics var1) {
      Zone var2 = this.getScrollbarZone();
      var1.setClip(var2.x, var2.y, var2.width, var2.height);
      Displayable.uistyle.drawScrollbar(var1.getImpl(), var2, 1, this.formHeight, this.viewport[3], this.scroll - this.topLimit + 1, true);
   }

   private boolean isNewLine(int var1, int var2, int var3) {
      boolean var4 = false;
      Item var5 = this.items[var1];
      if (var1 == var3) {
         this.boundsIncludeOtherItems = var5.boundsIncludeOtherItems(var4, var2, this.viewable[2]);
         if (!this.boundsIncludeOtherItems) {
            this.viewable[0] = 0;
            this.viewable[2] = this.viewport[2];
            this.viewable[1] = var1 > 0 ? this.items[var1 - 1].lineY + this.items[var1 - 1].rowHeight : this.topLimit;
            this.rowHasFocusableItems = var5.isFocusable();
            return false;
         }
      }

      var4 = !IS_FOUR_WAY_SCROLL && this.rowHasFocusableItems && var5.isFocusable();
      this.rowHasFocusableItems = var5.isFocusable() ? true : this.rowHasFocusableItems;
      int var6 = var5.callGetLayout() & 3;
      var4 = var4 || var1 > 0 && this.items[var1 - 1].equateNLA();
      var4 = var4 || var5.equateNLB();
      if (var1 > var3 && !var4) {
         var4 = var6 != this.currentAlignment;
         this.boundsIncludeOtherItems = var5.boundsIncludeOtherItems(var4, var2, this.viewable[2]);
         var4 = var4 || var2 > this.viewable[2] - CELL_SPACING || this.boundsIncludeOtherItems;
      }

      if (var4) {
         this.currentAlignment = var6;
      }

      return var4;
   }

   private void setNoneDirectionTraverseIndex() {
      if (this.traverseIndex != -1 && this.traverseIndex != 0) {
         this.setTraverseIndex(0, this.traverseIndex, this.traverseIndex);
      } else if (!this.items[0].isFocusable() && this.numOfItems != 1) {
         int var1 = 1;
         boolean var2 = false;

         for(Item var3 = this.items[var1]; var1 < this.numOfItems && var3.lineY < this.viewport[3] && !var2; ++var1) {
            var3 = this.items[var1];
            var2 = (var3.lineY == this.items[0].lineY || var3.bounds[1] + var3.bounds[3] <= this.viewport[3]) && var3.isFocusable();
         }

         if (var2) {
            this.setTraverseIndex(0, this.traverseIndex, var1 - 1);
         } else {
            this.setTraverseIndex(0, this.traverseIndex, 0);
         }
      } else {
         this.setTraverseIndex(0, this.traverseIndex, 0);
      }

   }

   private int getScrollingDirection(int var1) {
      int var2 = var1;
      if (var1 == 2 && !this.alignedLeftToRight) {
         var2 = 5;
      } else if (var1 == 5 && !this.alignedLeftToRight) {
         var2 = 2;
      }

      return var2;
   }

   private int formScrollUp(int var1) {
      int var2 = var1;
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

      int var3 = this.findNearestNeighbourUp();
      this.isTallRow = false;
      if (var3 != -1) {
         var2 = var3;
      } else if (this.scroll >= this.items[this.traverseIndex].lineY - FORM_MAX_SCROLL) {
         this.isTallRow = true;
         var10000 = this.view;
         var10000[1] -= this.getMinimumScroll(1);
         this.checkViewY();
         this.scroll = this.oldViewY = this.view[1];
         this.repaintFull();
         return -1;
      }

      return var2;
   }

   private int formScrollDown(int var1) {
      int var2 = var1;
      int var3 = this.items[this.traverseIndex].lineY + this.items[this.traverseIndex].rowHeight - this.viewport[3] > 0 ? this.items[this.traverseIndex].lineY + this.items[this.traverseIndex].rowHeight - this.viewport[3] : 0;
      int[] var10000;
      if (this.isTallRow) {
         if (this.scroll >= var3) {
            this.isTallRow = false;
         } else if (this.traverseIndex < this.numOfItems - 1) {
            var10000 = this.view;
            var10000[1] += this.getMinimumScroll(6);
            if (this.view[1] > var3 + FORM_MAX_SCROLL) {
               this.view[1] = var3 + FORM_MAX_SCROLL;
            }

            this.checkViewY();
            this.scroll = this.oldViewY = this.view[1];
            this.repaintFull();
            return -1;
         }
      }

      int var4 = this.findNearestNeighbourDown();
      this.isTallRow = false;
      if (var4 != -1) {
         var2 = var4;
      } else if (this.scroll <= var3 + FORM_MAX_SCROLL) {
         this.isTallRow = true;
         var10000 = this.view;
         var10000[1] += this.getMinimumScroll(6);
         this.checkViewY();
         this.scroll = this.oldViewY = this.view[1];
         this.repaintFull();
         return -1;
      }

      return var2;
   }

   private int formScrollLeft(int var1) {
      int var2 = var1;
      if (var1 <= 0) {
         return -1;
      } else {
         Item var3 = this.items[var1 - 1];
         Item var4 = this.items[var1];
         boolean var5 = this.isItemPartiallyShown(var3);
         if (!var4.boundsIncludeOtherItems && var4.lineY != var3.lineY && var3.isFocusable() && var5) {
            var2 = var1 - 1;
         } else {
            for(var5 = this.isItemPartiallyShown(var3); (!var3.isFocusable() || !var5) && var4.lineY == var3.lineY && var2 > 0; var5 = this.isItemPartiallyShown(var3)) {
               --var2;
               var3 = this.items[var2 - 1];
               var4 = this.items[var2];
            }

            var2 = var4.lineY == var3.lineY && var2 > 0 ? var3.itemIndex : var1;
         }

         if (!var3.isFocusable() || !var5) {
            var2 = var1;
         }

         return var2;
      }
   }

   private int formScrollRight(int var1) {
      int var2 = var1;
      if (var1 >= this.numOfItems - 1) {
         return -1;
      } else {
         Item var3 = this.items[var1 + 1];
         Item var4 = this.items[var1];
         boolean var5 = this.isItemPartiallyShown(var3);
         if (var4.lineY != var3.lineY && var3.isFocusable() && var5) {
            var2 = var1 + 1;
         } else {
            while((!var3.isFocusable() || !var5) && var4.lineY == var3.lineY && var2 < this.numOfItems - 1) {
               ++var2;
               var3 = this.items[var2 + 1];
               var4 = this.items[var2];
               var5 = this.isItemPartiallyShown(var3);
            }

            var2 = var4.lineY == var3.lineY && var2 < this.numOfItems - 1 ? var3.itemIndex : var1;
         }

         if (!var3.isFocusable() || !var5) {
            var2 = var1;
         }

         return var2;
      }
   }

   private void checkViewY() {
      int var1 = this.view[1];
      if (this.formHeight > this.viewport[3]) {
         this.view[1] = this.view[1] > this.bottomLimit - this.viewport[3] ? this.bottomLimit - this.viewport[3] : this.view[1];
         this.view[1] = this.view[1] < this.topLimit ? this.topLimit : this.view[1];
      } else {
         this.view[1] = this.topLimit;
      }

      if (UNICOM_FORM_SCROLLING) {
         this.view[1] = var1;
      }

   }

   private void checkMinScroll(int var1) {
      if (var1 == 1) {
         if (this.oldViewY - this.view[1] < FORM_MAX_SCROLL) {
            this.view[1] = this.oldViewY - FORM_MAX_SCROLL;
            this.scroll = this.view[1];
         }
      } else if (var1 == 6 && this.view[1] - this.oldViewY < FORM_MAX_SCROLL) {
         this.view[1] = this.oldViewY + FORM_MAX_SCROLL;
         this.scroll = this.view[1];
      }

      this.checkViewY();
   }

   private boolean isItemPartiallyShown(Item var1) {
      int var2 = this.view[1] + FORM_MAX_SCROLL;
      int var3 = this.view[1] + this.viewport[3] - FORM_MAX_SCROLL;
      boolean var4 = var1.bounds[1] + var1.bounds[3] >= var2 && var1.bounds[1] <= var3;
      return var4 ? true : var1.bounds[1] + var1.bounds[3] <= this.view[1] + this.viewport[3] && var1.bounds[1] >= this.view[1];
   }

   private int findTraversableItem(int var1) {
      int var2 = var1;
      boolean var4 = false;

      while(!var4 && this.items[var2].shouldSkipTraverse()) {
         ++var2;
         if (var2 == this.numOfItems) {
            var4 = true;
         }
      }

      if (var4) {
         if (var1 <= 0) {
            return -1;
         }

         var2 = var1 - 1;

         while(this.items[var2].shouldSkipTraverse()) {
            --var2;
            if (var2 == -1) {
               return -1;
            }
         }
      }

      this.formMode = 2;
      return var2;
   }

   private void initializeVisRect(int var1) {
      Item var2 = this.items[var1];
      this.visRect[0] = this.visRect[1] = 0;
      this.visRect[2] = this.viewport[2] < var2.bounds[2] ? this.viewport[2] : var2.bounds[2];
      this.visRect[3] = this.viewport[3] < var2.bounds[3] ? this.viewport[3] : var2.bounds[3];
   }

   Zone getScrollbarZone() {
      return this.ticker != null ? Displayable.screenTickScrollbarZone : Displayable.screenNormScrollbarZone;
   }

   Command[] getExtraCommands() {
      Command[] var1 = null;
      if (this.traverseIndex >= 0) {
         var1 = this.items[this.traverseIndex].getExtraCommands();
      }

      return var1;
   }

   boolean launchExtraCommand(Command var1) {
      return this.traverseIndex >= 0 ? this.items[this.traverseIndex].launchExtraCommand(var1) : false;
   }

   private void setTopLimit() {
      int var1;
      for(var1 = 0; var1 < this.numOfItems && this.items[var1].shouldSkipTraverse(); ++var1) {
      }

      if (var1 >= this.numOfItems) {
         this.topLimit = 0;
      } else {
         Item var2 = this.items[var1];
         int var3 = var2.lineY;
         this.scrollingIndex = var1;
         this.topLimitIndex = this.goToTheEndOfRow(var3).itemIndex;
         if (this.thisRowHasASpacer(var3, this.topLimitIndex)) {
            var3 = this.findMinTraversableBoundsY(var3);
         }

         this.topLimit = var3;
      }
   }

   private void setBottomLimit() {
      int var1;
      for(var1 = this.numOfItems - 1; var1 > 0 && this.items[var1].shouldSkipTraverse(); --var1) {
      }

      if (var1 < 0) {
         this.bottomLimit = this.viewport[3];
      } else {
         Item var2 = this.items[var1];
         int var3 = var2.lineY;
         int var4 = var2.rowHeight;
         int var5 = var3 + var4;
         this.scrollingIndex = var1;
         int var6 = this.goToTheEndOfRow(var3).itemIndex;
         if (this.thisRowHasASpacer(var3, var6)) {
            var5 = this.findMaxTraversableBoundsYPlusHeight(var3, var6);
         }

         this.bottomLimit = var5;
      }
   }

   private boolean thisRowHasASpacer(int var1, int var2) {
      int var3;
      for(var3 = var2; var3 >= 0 && !this.items[var3].shouldSkipTraverse() && this.items[var3].lineY == var1; --var3) {
      }

      return var3 > 0 && this.items[var3].lineY == var1;
   }

   private int findMinTraversableBoundsY(int var1) {
      int var2 = this.goToTheEndOfRow(var1).itemIndex;

      int var3;
      for(var3 = this.items[var2].rowHeight; var2 >= 0 && this.items[var2].lineY == var1; --var2) {
         if (!this.items[var2].shouldSkipTraverse()) {
            int var4 = this.items[var2].bounds[1];
            var3 = var4 < var3 ? var4 : var3;
         }
      }

      return var3;
   }

   private int findMaxTraversableBoundsYPlusHeight(int var1, int var2) {
      int var3 = var2;

      int var4;
      for(var4 = this.items[var2].lineY; var3 < this.numOfItems && this.items[var3].lineY == var1; ++var3) {
         if (!this.items[var3].shouldSkipTraverse()) {
            int var5 = this.items[var3].bounds[1] + this.items[var3].bounds[3];
            var4 = var5 > var4 ? var5 : var4;
         }
      }

      return var4;
   }

   private void setHighlightedItem(Item var1) {
      if (UNICOM_FORM_SCROLLING) {
         this.setTraverseIndex(0, this.traverseIndex, var1.itemIndex);
         this.resetToTop = false;
      }

   }

   private void setTopItem(Item var1) {
      if (UNICOM_FORM_SCROLLING) {
         this.topItem = true;
         this.ignoreTraverse = true;
         this.setTraverseIndex(0, this.traverseIndex, var1.itemIndex);
         this.resetToTop = false;
         this.topItem = false;
      }

   }

   private void wrapAround(boolean var1) {
      if (UNICOM_FORM_SCROLLING) {
         this.traverseIndex = var1 ? -1 : this.numOfItems - 1;
         this.view[0] = 0;
         this.view[1] = var1 ? this.topLimit : this.bottomLimit;
         this.oldViewY = this.view[1];
         this.resetToTop = false;
         this.traverse(0);
      }

   }

   private void setTopAndHighlightedItems(Item var1) {
      if (UNICOM_FORM_SCROLLING) {
         int var2 = this.getHeight();
         int var3 = var2 / (var1.getPreferredHeight() + 2);
         if (var3 < 3) {
            var3 = 3;
         } else if (var3 > 9) {
            var3 = 9;
         }

         int var4 = var1.itemIndex / var3 * var3;
         this.setTopItem(this.items[var4]);
         this.setHighlightedItem(var1);
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
