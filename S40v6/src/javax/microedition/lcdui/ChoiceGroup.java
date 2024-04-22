package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;

public class ChoiceGroup extends Item implements Choice {
   static final Command VIEW = new Command(12, 43);
   static final Command EXCLUSIVE_SELECT = new Command(9, 9);
   static final Command MULTI_MARK = new Command(9, 10);
   static final Command MULTI_UNMARK = new Command(9, 11);
   static final Command POPUP_OPEN = new Command(9, 42);
   static final Zone POPUP_NOICON_TEXT_ZONE;
   static final Zone POPUP_ICON_TEXT_ZONE;
   private static final Zone POPUP_ICON_IMAGE_ZONE;
   private static final Zone POPUP_LEFT_ARROW_ZONE;
   private static final Zone POPUP_RIGHT_ARROW_ZONE;
   static final Zone POPUP_BORDER_ZONE;
   private static final Pixmap ARROW_LEFT_POPUP_ICON;
   private static final Pixmap ARROW_RIGHT_POPUP_ICON;
   private static final Zone POPUP_LIST_NOICON_TEXT_ZONE;
   private static final Zone POPUP_LIST_ICON_TEXT_ZONE;
   private static final Zone POPUP_LIST_ICON_IMAGE_ZONE;
   private static final int POPUP_BORDER_HEIGHT;
   ChoiceHandler handler;
   private boolean traversedIn;
   private boolean invariantStart;
   List popupList;
   private ChoiceHandler.ChoiceElement popupElement;
   private int popupMainZoneH;
   int oldHighlighted;
   ChoiceGroup.PopupListListener popupListListener;

   public ChoiceGroup(String aLabel, int choiceType) {
      super(aLabel);
      if (choiceType != 1 && choiceType != 2 && choiceType != 4) {
         throw new IllegalArgumentException("Illegal type for creating a ChoiceGroup or a List.");
      } else {
         synchronized(Display.LCDUILock) {
            if (choiceType == 4) {
               this.popupListListener = new ChoiceGroup.PopupListListener();
            }
         }

         this.createHandler(choiceType);
      }
   }

   public ChoiceGroup(String aLabel, int choiceType, String[] stringElements, Image[] imageElements) {
      this(aLabel, choiceType);
      if (stringElements == null) {
         throw new NullPointerException("ChoiceGroup: stringElements==null.");
      } else if (imageElements != null && imageElements.length != stringElements.length) {
         throw new IllegalArgumentException("ChoiceGroup: length of stringElements and imageElements are not the same.");
      } else {
         synchronized(Display.LCDUILock) {
            for(int i = 0; i < stringElements.length; ++i) {
               this.append(stringElements[i], imageElements == null ? null : imageElements[i]);
            }

            this.handler.changeLayout();
         }
      }
   }

   public int size() {
      synchronized(Display.LCDUILock) {
         return this.handler == null ? 0 : this.handler.nOfItems;
      }
   }

   public String getString(int elementNum) {
      synchronized(Display.LCDUILock) {
         return this.handler.getString(elementNum);
      }
   }

   public Image getImage(int elementNum) {
      synchronized(Display.LCDUILock) {
         this.handler.checkForBounds(elementNum);
         ChoiceHandler.ChoiceElement ce = (ChoiceHandler.ChoiceElement)this.handler.getItem(elementNum);
         return ce.mutableImagePart != null ? ce.mutableImagePart : ce.image;
      }
   }

   public int append(String stringPart, Image imagePart) {
      int index = this.size();
      this.insert(index, stringPart, imagePart);
      return index;
   }

   public void insert(int elementNum, String stringPart, Image imagePart) {
      synchronized(Display.LCDUILock) {
         if (this.popupList != null) {
            ChoiceItem ci = this.handler.new ChoiceElement(stringPart, imagePart);
            this.popupList.insert(elementNum, ci);
         } else {
            this.handler.insert(elementNum, stringPart, imagePart);
         }

         this.invariantStart = true;
         if (this.handler.type != 4 || this.size() == 1) {
            this.invalidate();
         }

      }
   }

   public void delete(int elementNum) {
      synchronized(Display.LCDUILock) {
         int oldFocused = this.handler.highlightedIndex;
         if (this.popupList != null) {
            this.popupList.delete(elementNum);
         } else {
            this.handler.delete(elementNum);
            if (this.handler.nOfItems > 0) {
               this.handler.listOfItems[this.handler.highlightedIndex].highlighted = this.hasFocus;
            }
         }

         this.invariantStart = true;
         if (this.handler.type != 4 || elementNum == oldFocused || this.handler == null) {
            this.popupElement = null;
            this.invalidate();
         }

      }
   }

   public void deleteAll() {
      synchronized(Display.LCDUILock) {
         if (this.popupList != null) {
            this.popupList.deleteAll();
            this.popupList = null;
         } else {
            this.handler.deleteAll();
         }

         this.popupElement = null;
         this.invalidate();
      }
   }

   public void set(int elementNum, String stringPart, Image imagePart) {
      synchronized(Display.LCDUILock) {
         if (this.popupList != null) {
            this.popupList.set(elementNum, stringPart, imagePart);
         } else {
            this.handler.set(elementNum, stringPart, imagePart);
         }

         if (elementNum != this.handler.highlightedIndex) {
            this.invariantStart = true;
         }

         if (this.handler.type != 4 || elementNum == this.handler.highlightedIndex) {
            if (this.handler.type == 4) {
               this.updatePopupElement();
            }

            this.invalidate();
         }

      }
   }

   public boolean isSelected(int elementNum) {
      synchronized(Display.LCDUILock) {
         return this.handler.isSelected(elementNum);
      }
   }

   public int getSelectedIndex() {
      synchronized(Display.LCDUILock) {
         return this.handler.getSelectedIndex();
      }
   }

   public int getSelectedFlags(boolean[] selectedArray_return) {
      synchronized(Display.LCDUILock) {
         return this.handler.getSelectedFlags(selectedArray_return);
      }
   }

   public void setSelectedIndex(int elementNum, boolean selected) {
      synchronized(Display.LCDUILock) {
         if (this.popupList != null) {
            this.popupList.setSelectedIndex(elementNum, selected);
         } else {
            this.handler.setSelectedIndex(elementNum, selected);
            this.repaint();
         }

         if (this.handler.type == 4) {
            this.handler.setHighlightedItem(elementNum);
            this.updatePopupElement();
            this.invalidate();
         }

      }
   }

   public void setSelectedFlags(boolean[] selectedArray) {
      synchronized(Display.LCDUILock) {
         if (this.popupList != null) {
            this.popupList.setSelectedFlags(selectedArray);
         } else {
            this.handler.setSelectedFlags(selectedArray);
            this.repaint();
         }

         if (this.handler.type == 4) {
            this.handler.listOfItems[this.handler.highlightedIndex].highlighted = false;
            int elementNum = this.handler.getSelectedIndex();
            this.handler.highlightedIndex = elementNum;
            this.handler.listOfItems[elementNum].highlighted = true;
            this.updatePopupElement();
            this.invalidate();
         }

      }
   }

   public void setFitPolicy(int fitPolicy) {
      if (fitPolicy >= 0 && fitPolicy <= 2) {
         synchronized(Display.LCDUILock) {
            if (this.popupList != null) {
               this.popupList.setFitPolicy(fitPolicy);
            } else {
               if (this.handler.setFitPolicy(fitPolicy)) {
                  this.handler.changeLayout();
                  if (this.handler.type == 4) {
                     this.updatePopupElement();
                  }

                  this.invalidate();
               }

            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getFitPolicy() {
      synchronized(Display.LCDUILock) {
         return this.handler.getFitPolicy();
      }
   }

   public void setFont(int elementNum, Font font) {
      synchronized(Display.LCDUILock) {
         if (this.popupList != null) {
            this.popupList.setFont(elementNum, font);
         } else {
            this.handler.setFont(elementNum, font);
         }

         this.invariantStart = elementNum != this.handler.highlightedIndex ? true : this.invariantStart;
         if (this.handler.type != 4 || elementNum == this.handler.highlightedIndex) {
            if (this.handler.type == 4) {
               this.updatePopupElement();
            }

            this.invalidate();
         }

      }
   }

   public Font getFont(int elementNum) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && elementNum >= 0 && elementNum < this.handler.listOfItems.length) {
            Font jFont = this.handler.listOfItems[elementNum].jFont;
            return jFont == null ? Font.getDefaultFont() : jFont;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   boolean equateNLA() {
      if (super.equateNLA()) {
         return true;
      } else {
         return (this.layout & 16384) != 16384;
      }
   }

   boolean equateNLB() {
      if (super.equateNLB()) {
         return true;
      } else {
         return (this.layout & 16384) != 16384;
      }
   }

   boolean isFocusable() {
      return this.itemCommands != null && this.itemCommands.length() > 0 || this.size() > 0;
   }

   Command[] getExtraCommands() {
      Command[] ret = null;
      if (this.size() == 0) {
         return ret;
      } else {
         ChoiceHandler.ChoiceElement ce = (ChoiceHandler.ChoiceElement)this.handler.getItem(this.handler.highlightedIndex);
         switch(this.handler.type) {
         case 1:
            ret = new Command[]{EXCLUSIVE_SELECT};
            break;
         case 2:
            ret = ce.selected ? new Command[]{MULTI_UNMARK} : new Command[]{MULTI_MARK};
         case 3:
         default:
            break;
         case 4:
            ret = new Command[]{POPUP_OPEN};
         }

         if ((this.handler.fitPolicy != 1 || this.handler.multiTrunc) && (this.handler.type == 4 && this.popupElement != null && this.popupElement.isTruncated() || this.handler.type != 4 && ce.isTruncated())) {
            if (ret != null) {
               Command[] tmpRet = new Command[ret.length + 1];
               System.arraycopy(ret, 0, tmpRet, 0, ret.length);
               tmpRet[ret.length] = VIEW;
               ret = tmpRet;
            } else {
               ret = new Command[]{VIEW};
            }
         }

         return ret;
      }
   }

   boolean launchExtraCommand(Command c) {
      if (this.size() == 0) {
         return false;
      } else if (c.equals(VIEW)) {
         Display display = this.owner.myDisplay;
         TruncatedItemScreen tScreen = display.getTruncatedItemScreen();
         ChoiceHandler.ChoiceElement element = (ChoiceHandler.ChoiceElement)this.handler.listOfItems[this.handler.highlightedIndex];
         tScreen.showElement(display, this.owner, element.text, element.image, true);
         return true;
      } else if (c.equals(POPUP_OPEN)) {
         if (this.popupList == null) {
            this.popupList = new List(this.owner.getTitle(), this.handler);
            this.popupList.addCommand(new Command(2, 3));
            this.popupList.setCommandListener(this.popupListListener);
            this.popupList.setSystemScreen(true);
         }

         this.oldHighlighted = this.handler.highlightedIndex;
         this.owner.myDisplay.setCurrentInternal(this.owner, this.popupList);
         return true;
      } else {
         boolean oldstate = this.handler.getItem(this.handler.highlightedIndex).selected;
         this.setSelectedIndex(this.handler.highlightedIndex, !oldstate);
         this.owner.changedItemState(this);
         this.owner.updateSoftkeys(true);
         if (this.owner.isShown()) {
            this.owner.repaintRequest();
         }

         return false;
      }
   }

   boolean supportHorizontalScrolling() {
      return this.handler.type == 4;
   }

   void callKeyPressed(int keyCode, int keyDataIdx) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0) {
            int hilighted = this.handler.highlightedIndex;
            int i;
            switch(keyCode) {
            case -10:
               if (this.handler.type != 4) {
                  boolean oldstate = false;
                  oldstate = this.handler.listOfItems[hilighted].selected;
                  this.setSelectedIndex(hilighted, !oldstate);
                  this.owner.changedItemState(this);
                  this.owner.updateSoftkeys(true);
               } else {
                  if (this.popupList == null) {
                     this.popupList = new List(this.owner.getTitle(), this.handler);
                     this.popupList.addCommand(new Command(2, 3));
                     this.popupList.setCommandListener(this.popupListListener);
                     this.popupList.setSystemScreen(true);
                  }

                  this.oldHighlighted = this.handler.highlightedIndex;
                  this.owner.myDisplay.setCurrentInternal(this.owner, this.popupList);
               }
               break;
            case -4:
               if (this.handler.type == 4) {
                  if (UIStyle.isAlignedLeftToRight) {
                     i = this.getSelectedIndex() + 1;
                     if (i >= this.size()) {
                        i = 0;
                     }
                  } else {
                     i = this.getSelectedIndex() - 1;
                     if (i < 0) {
                        i = this.size() - 1;
                     }
                  }

                  this.setSelectedIndex(i, true);
                  this.owner.changedItemState(this);
               }
               break;
            case -3:
               if (this.handler.type == 4) {
                  if (UIStyle.isAlignedLeftToRight) {
                     i = this.getSelectedIndex() - 1;
                     if (i < 0) {
                        i = this.size() - 1;
                     }
                  } else {
                     i = this.getSelectedIndex() + 1;
                     if (i >= this.size()) {
                        i = 0;
                     }
                  }

                  this.setSelectedIndex(i, true);
                  this.owner.changedItemState(this);
               }
               break;
            case 35:
               Display display = this.owner.myDisplay;
               TruncatedItemScreen tScreen = display.getTruncatedItemScreen();
               ChoiceHandler.ChoiceElement element = (ChoiceHandler.ChoiceElement)this.handler.listOfItems[hilighted];
               tScreen.showElement(display, this.owner, element.text, element.image, false);
            }

         }
      }
   }

   int callMinimumHeight() {
      return this.callPreferredHeight(0);
   }

   int callMinimumWidth() {
      return this.callPreferredWidth(-1);
   }

   void callPaint(Graphics g, int w, int h, boolean isFocused) {
      super.callPaint(g, w, h, isFocused);
      synchronized(Display.LCDUILock) {
         int x = g.getTranslateX();
         int y = g.getTranslateY();
         this.hasFocus = isFocused;
         h -= this.getLabelHeight(-1);
         if (this.isFocusable() && h != 0) {
            if (this.handler.type == 4) {
               this.implPopupPaint(g, x, y, w, h);
            } else {
               this.implPaint(g, x, y, w, h);
            }

         }
      }
   }

   int callPreferredHeight(int width) {
      int h = false;
      if (!this.isFocusable()) {
         return 0;
      } else {
         int w;
         int h;
         if (this.handler.type == 4) {
            this.createPopupElement();
            h = this.popupElement == null ? 0 : this.popupElement.height;
            h += 2 * POPUP_BORDER_HEIGHT;
            w = POPUP_NOICON_TEXT_ZONE.width;
         } else {
            h = this.handler == null ? 0 : this.handler.listHeight;
            h += this.size() == 0 ? 2 : 0;
            w = (this.owner == null ? Displayable.screenNormMainZone : this.owner.getMainZone()).width;
         }

         if (this.size() == 0) {
            h += this.getEmptyStringHeight(w, (Font)null) + 1;
         }

         return h;
      }
   }

   int callPreferredWidth(int height) {
      int width = this.owner == null ? Displayable.screenNormMainZone.width : this.owner.getWidth();
      if (this.handler.type == 4) {
         width = POPUP_BORDER_ZONE.width;
      }

      return !this.isFocusable() && this.label == null ? 0 : width;
   }

   boolean callTraverse(int dir, int viewportWidth, int viewportHeight, int[] visRect) {
      super.callTraverse(dir, viewportWidth, viewportHeight, visRect);
      synchronized(Display.LCDUILock) {
         int lh = this.getLabelHeight(this.owner.getMainZone().width);
         boolean consumed = false;
         if (!this.traversedIn) {
            consumed = this.acquireFocus(lh, dir, viewportHeight, visRect);
            this.traversedIn = true;
            return consumed;
         } else {
            switch(dir) {
            case 0:
               if (this.size() == 0) {
                  if (this.isFocusable()) {
                     int h = this.callPreferredHeight(-1) + lh;
                     visRect[1] = 0;
                     visRect[3] = viewportHeight < h ? viewportHeight : h;
                     this.repaint();
                     return true;
                  }

                  return false;
               }

               if (this.handler.type == 4) {
                  this.popupUpdated(lh, viewportHeight, visRect);
               } else {
                  this.cgUpdated(lh, viewportHeight, visRect);
               }

               consumed = true;
               break;
            case 1:
               if (this.size() == 0) {
                  return false;
               }

               if (this.handler.type != 4) {
                  consumed = this.cgScrollUp(lh, viewportHeight, visRect);
               }
               break;
            case 6:
               if (this.size() == 0) {
                  return false;
               }

               if (this.handler.type != 4) {
                  consumed = this.cgScrollDown(lh, viewportHeight, visRect);
               }
               break;
            default:
               consumed = false;
            }

            visRect[3] = visRect[3] < viewportHeight ? visRect[3] : viewportHeight;
            if (consumed) {
               this.owner.updateSoftkeys(true);
            }

            return consumed;
         }
      }
   }

   void callTraverseOut() {
      super.callTraverseOut();
      if (this.size() != 0) {
         this.handler.listOfItems[this.handler.highlightedIndex].highlighted = false;
      }

      this.traversedIn = false;
   }

   void callShowNotify() {
      super.callShowNotify();
      if (this.size() > 0 && !this.hasFocus) {
         this.handler.getItem(this.handler.highlightedIndex).highlighted = false;
      }

   }

   boolean shouldSkipTraverse() {
      return (this.label == null || "".equals(this.label)) && this.size() == 0 && (this.itemCommands == null || this.itemCommands.length() <= 0);
   }

   void setOwner(Screen anOwner) {
      super.setOwner(anOwner);
      synchronized(Display.LCDUILock) {
         if (this.owner != null) {
            this.lockedWidth = this.owner.getMainZone().width;
            if (this.handler != null) {
               this.handler.setMainZone(this.owner.getMainZone());
            }
         } else {
            this.lockedWidth = -1;
         }

      }
   }

   public void setPreferredSize(int width, int height) {
      if (width < -1 || height < -1) {
         throw new IllegalArgumentException();
      }
   }

   private void createHandler(int type) {
      UIStyle ui = Displayable.uistyle;
      this.handler = new ChoiceHandler(false, true, Displayable.screenNormMainZone, type);
      if (type == 4) {
         this.handler.elementTextZone = ui.getZone(16);
         this.handler.elementIconGraphicZone = null;
         this.handler.elementChoiceBoxZone = null;
         this.handler.auxTextZone = ui.getZone(17);
         this.handler.auxIconZone = ui.getZone(18);
         this.handler.auxBoxZone = null;
         this.handler.checkIconNotSelected = null;
         this.handler.checkIconSelected = null;
         this.popupMainZoneH = Displayable.screenNormMainZone.height;
      } else {
         this.handler.elementTextZone = ui.getZone(7);
         this.handler.elementIconGraphicZone = null;
         this.handler.elementChoiceBoxZone = ui.getZone(6);
         this.handler.auxTextZone = ui.getZone(9);
         this.handler.auxIconZone = ui.getZone(10);
         this.handler.auxBoxZone = ui.getZone(8);
         if (type == 1) {
            this.handler.checkIconNotSelected = Pixmap.createPixmap(9);
            this.handler.checkIconSelected = Pixmap.createPixmap(8);
         } else {
            this.handler.checkIconNotSelected = Pixmap.createPixmap(7);
            this.handler.checkIconSelected = Pixmap.createPixmap(6);
         }
      }

      this.handler.parent = this;
   }

   private boolean acquireFocus(int lh, int dir, int viewportHeight, int[] visRect) {
      this.invariantStart = false;
      int itemH;
      if (this.size() == 0) {
         if (this.isFocusable()) {
            itemH = this.callPreferredHeight(-1) + lh;
            visRect[1] = 0;
            visRect[3] = viewportHeight < itemH ? viewportHeight : itemH;
            this.traversedIn = true;
            this.repaint();
            return true;
         } else {
            return false;
         }
      } else {
         int itemH = false;
         this.handler.setMainZone(this.owner.getMainZone());
         this.handler.listOfItems[this.handler.highlightedIndex].highlighted = false;
         ChoiceHandler.ChoiceElement ce;
         if (this.handler.type == 4) {
            ce = this.popupElement;
            this.popupElement.highlighted = true;
            itemH = ce.height + 2 * POPUP_BORDER_HEIGHT;
         } else {
            this.handler.highlightedIndex = dir == 6 ? 0 : this.handler.highlightedIndex;
            this.handler.highlightedIndex = dir == 1 ? this.handler.nOfItems - 1 : this.handler.highlightedIndex;
            ce = (ChoiceHandler.ChoiceElement)this.handler.getItem(this.handler.highlightedIndex);
            itemH = this.handler.listHeight;
         }

         if (dir == 6) {
            visRect[1] = 0;
            visRect[3] = viewportHeight < itemH + lh ? viewportHeight : itemH + lh;
         }

         if (dir == 1) {
            if (viewportHeight >= itemH + lh) {
               visRect[1] = 0;
               visRect[3] = itemH + lh;
            } else {
               visRect[1] = itemH + lh - viewportHeight;
               visRect[3] = viewportHeight;
            }
         }

         this.handler.listOfItems[this.handler.highlightedIndex].highlighted = true;
         this.repaint();
         return true;
      }
   }

   private void swapPopupHandlerZones(boolean setPopup) {
      if (setPopup) {
         this.handler.mainZone = this.owner == null ? Displayable.screenNormMainZone : this.owner.getMainZone();
         if (this.handler.listOfItems[this.handler.highlightedIndex].image == null) {
            this.handler.elementTextZone = POPUP_NOICON_TEXT_ZONE;
            this.handler.elementIconGraphicZone = null;
         } else {
            this.handler.elementTextZone = POPUP_ICON_TEXT_ZONE;
            this.handler.elementIconGraphicZone = POPUP_ICON_IMAGE_ZONE;
         }

         this.handler.hasPartialBehaviour = true;
         this.handler.hasCyclicBehaviour = false;
      } else {
         this.handler.mainZone = Displayable.screenNormMainZone;
         if (this.handler.auxIconZone == null) {
            this.handler.elementTextZone = POPUP_LIST_ICON_TEXT_ZONE;
            this.handler.elementIconGraphicZone = POPUP_LIST_ICON_IMAGE_ZONE;
         } else {
            this.handler.elementTextZone = POPUP_LIST_NOICON_TEXT_ZONE;
            this.handler.elementIconGraphicZone = null;
         }

         this.handler.checkIconNotSelected = null;
         this.handler.hasPartialBehaviour = false;
         this.handler.hasCyclicBehaviour = true;
      }

   }

   private void createPopupElement() {
      if (this.size() != 0) {
         this.popupElement = (ChoiceHandler.ChoiceElement)this.handler.getHighlightedItem();
         this.popupElement.displayedRatio = 0;
         this.popupElement.startLine = 0;
         this.popupElement.startPage = 0;
         this.popupElement.borderH = (short)POPUP_BORDER_HEIGHT;
      }
   }

   private void updatePopupElement() {
      if (this.size() != 0) {
         this.popupElement = (ChoiceHandler.ChoiceElement)this.handler.getHighlightedItem();
         this.swapPopupHandlerZones(true);
         this.popupMainZoneH = this.owner == null ? Displayable.screenNormMainZone.height : this.owner.getMainZone().height;
         this.popupElement.displayedRatio = 0;
         this.popupElement.startLine = 0;
         this.popupElement.startPage = 0;
         this.swapPopupHandlerZones(false);
      }
   }

   private void implPopupPaint(Graphics g, int x, int y, int w, int h) {
      int intern_h = h - 2 * POPUP_BORDER_HEIGHT;
      Zone mainZone = this.owner.getMainZone();
      Displayable.uistyle.drawBorder(g.getImpl(), x, y, w, h, POPUP_BORDER_ZONE.getBorderType(), this.hasFocus);
      Displayable.uistyle.drawPixmapInZone(g.getImpl(), POPUP_LEFT_ARROW_ZONE, mainZone.x, y, ARROW_LEFT_POPUP_ICON);
      Displayable.uistyle.drawPixmapInZone(g.getImpl(), POPUP_RIGHT_ARROW_ZONE, mainZone.x, y, ARROW_RIGHT_POPUP_ICON);
      if (this.size() == 0) {
         this.paintEmptyString(g, POPUP_NOICON_TEXT_ZONE.x + x, y + POPUP_NOICON_TEXT_ZONE.y, POPUP_NOICON_TEXT_ZONE.width - 1, intern_h, this.hasFocus);
      } else {
         this.swapPopupHandlerZones(true);
         this.createPopupElement();
         this.handler.sumH = 0;
         this.popupElement.highlighted = this.hasFocus;
         this.popupElement.paintElement(x, y, intern_h, g, true);
         this.swapPopupHandlerZones(false);
      }

   }

   private void implPaint(Graphics g, int x, int y, int w, int h) {
      Zone mainZone = this.owner.getMainZone();
      if (this.size() == 0) {
         this.paintEmptyString(g, mainZone.x, y, w, h, this.hasFocus);
      } else {
         this.handler.startElement = 0;
         ((ChoiceHandler.ChoiceElement)this.handler.listOfItems[0]).startLine = 0;
         ((ChoiceHandler.ChoiceElement)this.handler.listOfItems[this.handler.highlightedIndex]).highlighted = this.hasFocus;
         this.handler.paintElements(x, y, g, 0);
      }

   }

   private void popupUpdated(int lh, int viewportHeight, int[] visRect) {
      this.createPopupElement();
      this.popupElement.highlighted = true;
      ChoiceHandler.ChoiceElement ce = this.popupElement;
      this.invariantStart = false;
      visRect[1] = 0;
      visRect[3] = lh + ce.height + 2 * POPUP_BORDER_HEIGHT < viewportHeight ? lh + ce.height + 2 * POPUP_BORDER_HEIGHT : viewportHeight;
      this.repaint();
   }

   private void cgUpdated(int lh, int viewportHeight, int[] visRect) {
      int h2Focused = lh;
      ChoiceHandler.ChoiceElement ce = (ChoiceHandler.ChoiceElement)this.handler.getHighlightedItem();
      this.handler.setMainZone(this.owner.getMainZone());

      int h2BottomFocused;
      for(h2BottomFocused = 0; h2BottomFocused < this.handler.highlightedIndex; ++h2BottomFocused) {
         h2Focused += ((ChoiceHandler.ChoiceElement)this.handler.listOfItems[h2BottomFocused]).height;
      }

      h2BottomFocused = h2Focused + ce.height + ce.heightOfFillerSpace;
      if (visRect[1] > h2Focused) {
         visRect[1] = h2Focused;
         visRect[3] = lh + this.handler.listHeight - visRect[1];
         if (visRect[3] > viewportHeight) {
            visRect[3] = viewportHeight;
         }
      } else if (visRect[1] + visRect[3] < h2BottomFocused) {
         if (viewportHeight < h2BottomFocused) {
            visRect[1] = h2BottomFocused - viewportHeight;
            visRect[3] = viewportHeight;
         } else {
            visRect[1] = 0;
            visRect[3] = h2BottomFocused;
         }
      }

      this.invariantStart = false;
      this.repaint();
   }

   private boolean cgScrollUp(int lh, int viewportHeight, int[] visRect) {
      int hNewFocused2Last = false;
      int hFocused2Last = 0;
      ChoiceHandler.ChoiceElement ce = (ChoiceHandler.ChoiceElement)this.handler.getHighlightedItem();
      this.handler.setMainZone(this.owner.getMainZone());

      int tmpStart;
      for(tmpStart = this.handler.nOfItems - 1; tmpStart > this.handler.highlightedIndex; --tmpStart) {
         hFocused2Last += ((ChoiceHandler.ChoiceElement)this.handler.getItem(tmpStart)).height;
      }

      if (this.handler.highlightedIndex > 0) {
         hFocused2Last += ce.height;
         this.handler.setHighlightedItem(this.handler.highlightedIndex - 1);
         int hNewFocused2Last = hFocused2Last + ce.height;
         if (lh + this.handler.listHeight <= viewportHeight) {
            visRect[1] = 0;
            visRect[3] = lh + this.handler.listHeight;
         } else {
            tmpStart = lh + this.handler.listHeight - hNewFocused2Last;
            if (tmpStart < visRect[1]) {
               visRect[1] = tmpStart;
            }

            visRect[3] = viewportHeight;
         }

         this.repaint();
         return true;
      } else if (lh != 0 && visRect[1] != 0) {
         visRect[1] = 0;
         visRect[3] = lh + this.handler.listHeight <= viewportHeight ? lh + this.handler.listHeight : viewportHeight;
         this.repaint();
         return true;
      } else {
         return false;
      }
   }

   private boolean cgScrollDown(int lh, int viewportHeight, int[] visRect) {
      ChoiceHandler.ChoiceElement ce = (ChoiceHandler.ChoiceElement)this.handler.getHighlightedItem();
      int h2NewFocused = lh;
      this.handler.setMainZone(this.owner.getMainZone());
      if (this.handler.highlightedIndex >= this.handler.nOfItems - 1) {
         return false;
      } else {
         this.handler.setHighlightedItem(this.handler.highlightedIndex + 1);

         for(int i = 0; i < this.handler.highlightedIndex; ++i) {
            h2NewFocused += ((ChoiceHandler.ChoiceElement)this.handler.getItem(i)).height;
         }

         if (h2NewFocused + ce.height <= viewportHeight) {
            visRect[3] = lh + this.handler.listHeight < viewportHeight ? lh + this.handler.listHeight : viewportHeight;
         } else {
            if (h2NewFocused + ce.height > visRect[1] + viewportHeight) {
               visRect[1] = h2NewFocused + ce.height - viewportHeight;
            }

            visRect[3] = viewportHeight;
         }

         this.repaint();
         return true;
      }
   }

   static {
      POPUP_NOICON_TEXT_ZONE = Displayable.uistyle.getZone(33);
      POPUP_ICON_TEXT_ZONE = Displayable.uistyle.getZone(34);
      POPUP_ICON_IMAGE_ZONE = Displayable.uistyle.getZone(35);
      POPUP_LEFT_ARROW_ZONE = Displayable.uistyle.getZone(36);
      POPUP_RIGHT_ARROW_ZONE = Displayable.uistyle.getZone(37);
      POPUP_BORDER_ZONE = Displayable.uistyle.getZone(32);
      ARROW_LEFT_POPUP_ICON = Pixmap.createPixmap(11);
      ARROW_RIGHT_POPUP_ICON = Pixmap.createPixmap(12);
      POPUP_LIST_NOICON_TEXT_ZONE = Displayable.uistyle.getZone(16);
      POPUP_LIST_ICON_TEXT_ZONE = Displayable.uistyle.getZone(17);
      POPUP_LIST_ICON_IMAGE_ZONE = Displayable.uistyle.getZone(18);
      POPUP_BORDER_HEIGHT = POPUP_BORDER_ZONE.getMarginTop();
   }

   private class PopupListListener implements CommandListener {
      private PopupListListener() {
      }

      public void commandAction(Command c, Displayable d) {
         if (d.equals(ChoiceGroup.this.popupList)) {
            if (c != null && c.getCommandType() == 2) {
               if (ChoiceGroup.this.handler != null) {
                  ChoiceGroup.this.handler.setHighlightedItem(ChoiceGroup.this.oldHighlighted);
                  ChoiceGroup.this.handler.setSelectedIndex(ChoiceGroup.this.oldHighlighted, true);
               }

               if (ChoiceGroup.this.owner != null) {
                  ChoiceGroup.this.owner.myDisplay.setCurrentInternal((Displayable)null, ChoiceGroup.this.owner);
               } else {
                  d.myDisplay.setCurrent(d.myDisplay.getCurrent());
               }
            }

            if (c.equals(List.SELECT_COMMAND)) {
               int i = ChoiceGroup.this.handler.getSelectedIndex();
               ChoiceGroup.this.handler.setHighlightedItem(i);
               ChoiceGroup.this.handler.setSelectedIndex(i, true);
               ChoiceGroup.this.updatePopupElement();
               ChoiceGroup.this.invalidate();
               if (ChoiceGroup.this.owner != null) {
                  ChoiceGroup.this.owner.changedItemState(ChoiceGroup.this);
                  ChoiceGroup.this.owner.myDisplay.setCurrentInternal((Displayable)null, ChoiceGroup.this.owner);
               } else {
                  d.myDisplay.setCurrent(d.myDisplay.getCurrent());
               }
            }

            ChoiceGroup.this.popupList = null;
         }
      }

      // $FF: synthetic method
      PopupListListener(Object x1) {
         this();
      }
   }
}
