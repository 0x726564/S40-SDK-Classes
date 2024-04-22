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
   private static final int DEFAULT_FIT_POLICY = UIStyle.getNumberOfSoftKeys() > 2 ? 2 : 1;
   private static final Zone POPUP_NOICON_TEXT_ZONE;
   private static final Zone POPUP_ICON_TEXT_ZONE;
   private static final Zone POPUP_ICON_IMAGE_ZONE;
   private static final Zone POPUP_LEFT_ARROW_ZONE;
   private static final Zone POPUP_RIGHT_ARROW_ZONE;
   private static final Zone POPUP_BORDER_ZONE;
   private static final Pixmap ARROW_LEFT_POPUP_ICON;
   private static final Pixmap ARROW_RIGHT_POPUP_ICON;
   private static final Zone POPUP_LIST_NOICON_TEXT_ZONE;
   private static final Zone POPUP_LIST_ICON_TEXT_ZONE;
   private static final Zone POPUP_LIST_ICON_IMAGE_ZONE;
   private static final int POPUP_BORDER_HEIGHT;
   private int currentFitPolicy;
   private int type;
   ChoiceHandler handler;
   private boolean traversedIn;
   private boolean invariantStart;
   List popupList;
   private ChoiceHandler.ChoiceElement popupElement;
   private int popupMainZoneH;
   int oldHighlighted;
   private ChoiceGroup.PopupListListener popupListListener;

   public ChoiceGroup(String aLabel, int choiceType) {
      super(aLabel);
      this.currentFitPolicy = DEFAULT_FIT_POLICY;
      if (choiceType != 1 && choiceType != 2 && choiceType != 4) {
         throw new IllegalArgumentException("Illegal type for creating a ChoiceGroup or a List.");
      } else {
         synchronized(Display.LCDUILock) {
            this.type = choiceType;
            this.currentFitPolicy = DEFAULT_FIT_POLICY;
            if (this.type == 4) {
               this.popupListListener = new ChoiceGroup.PopupListListener();
            }

         }
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

         }
      }
   }

   public int size() {
      synchronized(Display.LCDUILock) {
         return this.handler != null && this.handler.choiceElements != null ? this.handler.choiceElements.length : 0;
      }
   }

   public String getString(int elementNum) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && elementNum >= 0 && elementNum < this.handler.choiceElements.length) {
            return this.handler.choiceElements[elementNum].stringPart;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public Image getImage(int elementNum) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && elementNum >= 0 && elementNum < this.handler.choiceElements.length) {
            ChoiceHandler.ChoiceElement ce = this.handler.choiceElements[elementNum];
            return ce.mutableImagePart != null ? ce.mutableImagePart : ce.imagePart;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public int append(String stringPart, Image imagePart) {
      synchronized(Display.LCDUILock) {
         int index = false;
         int index;
         if (this.popupList != null) {
            index = this.popupList.append(stringPart, imagePart);
         } else if (this.size() == 0) {
            this.createHandler();
            index = this.handler.append(stringPart, imagePart);
            if (this.type != 2) {
               this.handler.choiceElements[0].selected = true;
            }
         } else {
            index = this.handler.append(stringPart, imagePart);
         }

         this.invariantStart = true;
         if (this.type != 4 || this.size() == 1) {
            this.invalidate();
         }

         return index;
      }
   }

   public void insert(int elementNum, String stringPart, Image imagePart) {
      synchronized(Display.LCDUILock) {
         if (this.popupList != null) {
            this.popupList.insert(elementNum, stringPart, imagePart);
         } else if (this.size() == 0) {
            this.createHandler();
            this.handler.insert(elementNum, stringPart, imagePart);
            if (this.type != 2) {
               this.handler.choiceElements[0].selected = true;
            }
         } else {
            this.handler.insert(elementNum, stringPart, imagePart);
         }

         this.invariantStart = true;
         if (this.type != 4 || this.size() == 1) {
            this.invalidate();
         }

      }
   }

   public void delete(int elementNum) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && elementNum >= 0 && elementNum < this.handler.choiceElements.length) {
            int oldFocused = this.handler.highlightedElement;
            if (this.popupList != null) {
               this.popupList.delete(elementNum);
               this.handler.choiceElements[this.handler.highlightedElement].highlighted = this.hasFocus;
            } else {
               this.handler.delete(elementNum);
               if (this.handler.choiceElements != null && this.handler.choiceElements.length > 0) {
                  if (this.type != 2 && this.getSelectedIndex() == -1) {
                     int lastIndex = this.handler.choiceElements.length - 1;
                     this.handler.choiceElements[elementNum > lastIndex ? lastIndex : elementNum].selected = true;
                  }

                  this.handler.choiceElements[this.handler.highlightedElement].highlighted = this.hasFocus;
               } else {
                  this.handler = null;
               }
            }

            this.invariantStart = true;
            if (this.type != 4 || elementNum == oldFocused || this.handler == null) {
               this.popupElement = null;
               this.invalidate();
            }

         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void deleteAll() {
      synchronized(Display.LCDUILock) {
         if (this.popupList != null) {
            this.popupList.deleteAll();
            this.popupList = null;
         }

         this.handler = null;
         this.popupElement = null;
         this.invalidate();
      }
   }

   public void set(int elementNum, String stringPart, Image imagePart) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && elementNum >= 0 && elementNum < this.handler.choiceElements.length) {
            if (this.popupList != null) {
               this.popupList.set(elementNum, stringPart, imagePart);
            } else {
               this.handler.set(elementNum, stringPart, imagePart);
            }

            if (elementNum != this.handler.highlightedElement) {
               this.invariantStart = true;
            }

            if (this.type != 4 || elementNum == this.handler.highlightedElement) {
               if (this.type == 4) {
                  this.updatePopupElement();
               }

               this.invalidate();
            }

         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public boolean isSelected(int elementNum) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && elementNum >= 0 && elementNum < this.handler.choiceElements.length) {
            return this.handler.choiceElements[elementNum].selected;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public int getSelectedIndex() {
      synchronized(Display.LCDUILock) {
         return this.type != 2 && this.size() != 0 ? this.handler.getSelectedIndex() : -1;
      }
   }

   public int getSelectedFlags(boolean[] selectedArray_return) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0) {
            return this.handler.getSelectedFlags(selectedArray_return);
         } else {
            for(int i = 0; i < selectedArray_return.length; ++i) {
               selectedArray_return[i] = false;
            }

            return 0;
         }
      }
   }

   public void setSelectedIndex(int elementNum, boolean selected) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && elementNum >= 0 && elementNum <= this.handler.choiceElements.length - 1) {
            if (this.popupList != null) {
               this.popupList.setSelectedIndex(elementNum, selected);
            } else {
               if (this.type != 2) {
                  if (!selected || this.handler.choiceElements[elementNum].selected) {
                     return;
                  }

                  for(int i = 0; i < this.handler.choiceElements.length; ++i) {
                     this.handler.choiceElements[i].selected = false;
                  }
               }

               this.handler.setSelectedIndex(elementNum, selected);
               this.repaint();
            }

            if (this.type == 4) {
               this.handler.choiceElements[this.handler.highlightedElement].highlighted = false;
               this.handler.highlightedElement = elementNum;
               this.handler.choiceElements[elementNum].highlighted = true;
               this.updatePopupElement();
               this.invalidate();
            }

         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void setSelectedFlags(boolean[] selectedArray) {
      synchronized(Display.LCDUILock) {
         if (selectedArray == null) {
            throw new NullPointerException();
         } else if (this.handler != null && this.handler.choiceElements != null) {
            if (this.type == 2) {
               this.handler.setSelectedFlags(selectedArray);
               this.repaint();
            } else {
               if (selectedArray.length < this.handler.choiceElements.length) {
                  throw new IllegalArgumentException();
               }

               int index = 0;
               boolean found = false;

               for(int i = 0; i < this.handler.choiceElements.length; ++i) {
                  this.handler.choiceElements[i].selected = false;
                  if (selectedArray[i] && !found) {
                     found = true;
                     index = i;
                  }
               }

               this.setSelectedIndex(index, true);
            }

         }
      }
   }

   public void setFitPolicy(int fitPolicy) {
      if (fitPolicy >= 0 && fitPolicy <= 2) {
         synchronized(Display.LCDUILock) {
            if (this.popupList != null) {
               this.popupList.setFitPolicy(fitPolicy);
            } else {
               if (this.currentFitPolicy != fitPolicy) {
                  if (fitPolicy == 0) {
                     if (this.currentFitPolicy != DEFAULT_FIT_POLICY && this.handler != null) {
                        this.setWrapping(DEFAULT_FIT_POLICY == 1);
                     }

                     this.currentFitPolicy = fitPolicy;
                  } else {
                     this.currentFitPolicy = fitPolicy;
                     if (this.handler != null) {
                        this.setWrapping(this.currentFitPolicy == 1);
                     }
                  }

                  if (this.type == 4) {
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
      return this.currentFitPolicy;
   }

   public void setFont(int elementNum, Font font) {
      synchronized(Display.LCDUILock) {
         if (this.popupList != null) {
            this.popupList.setFont(elementNum, font);
         } else {
            this.handler.setFont(elementNum, font);
         }

         this.invariantStart = elementNum != this.handler.highlightedElement ? true : this.invariantStart;
         if (this.type != 4 || elementNum == this.handler.highlightedElement) {
            if (this.type == 4) {
               this.updatePopupElement();
            }

            this.invalidate();
         }

      }
   }

   public Font getFont(int elementNum) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && elementNum >= 0 && elementNum < this.handler.choiceElements.length) {
            Font jFont = this.handler.choiceElements[elementNum].jFont;
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
         ChoiceHandler.ChoiceElement[] elements = this.handler.choiceElements;
         ChoiceHandler.ChoiceElement ce = elements[this.handler.highlightedElement];
         switch(this.type) {
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

         if ((!this.handler.wrapOn || this.handler.multiTrunc) && (this.type == 4 && this.popupElement != null && this.popupElement.isTruncated() || this.type != 4 && ce.isTruncated())) {
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
      } else {
         ChoiceHandler.ChoiceElement[] elements = this.handler.choiceElements;
         if (c.equals(VIEW)) {
            Display display = this.owner.myDisplay;
            TruncatedItemScreen tScreen = display.getTruncatedItemScreen();
            ChoiceHandler.ChoiceElement element = this.handler.choiceElements[this.handler.highlightedElement];
            tScreen.showElement(display, this.owner, element.stringPart, element.imagePart, true);
            return true;
         } else if (c.equals(POPUP_OPEN)) {
            if (this.popupList == null) {
               this.popupList = new List(this.owner.getTitle(), this.handler);
               this.popupList.addCommand(new Command(2, 3));
               this.popupList.setCommandListener(this.popupListListener);
            }

            int lh = this.getLabelHeight(this.owner.getMainZone().width);
            this.oldHighlighted = this.handler.highlightedElement;
            this.owner.myDisplay.setCurrentInternal(this.owner, this.popupList);
            return true;
         } else {
            boolean oldstate = elements[this.handler.highlightedElement].selected;
            this.setSelectedIndex(this.handler.highlightedElement, !oldstate);
            this.owner.changedItemState(this);
            this.owner.updateSoftkeys(true);
            if (this.owner.isShown()) {
               this.owner.repaintRequest();
            }

            return false;
         }
      }
   }

   boolean supportHorizontalScrolling() {
      return this.type == 4;
   }

   void callKeyPressed(int keyCode, int keyDataIdx) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0) {
            int hilighted = this.handler.highlightedElement;
            int i;
            switch(keyCode) {
            case -10:
               if (this.type != 4) {
                  boolean oldstate = false;
                  oldstate = this.handler.choiceElements[hilighted].selected;
                  this.setSelectedIndex(hilighted, !oldstate);
                  this.owner.changedItemState(this);
                  this.owner.updateSoftkeys(true);
               } else {
                  ChoiceHandler.ChoiceElement[] elements = this.handler.choiceElements;
                  if (this.popupList == null) {
                     this.popupList = new List(this.owner.getTitle(), this.handler);
                     this.popupList.addCommand(new Command(2, 3));
                     this.popupList.setCommandListener(this.popupListListener);
                  }

                  int lh = this.getLabelHeight(this.owner.getMainZone().width);
                  this.oldHighlighted = this.handler.highlightedElement;
                  this.owner.myDisplay.setCurrentInternal(this.owner, this.popupList);
               }
               break;
            case -4:
               if (this.type == 4) {
                  i = this.getSelectedIndex() + 1;
                  if (i >= this.size()) {
                     i = 0;
                  }

                  this.setSelectedIndex(i, true);
                  this.owner.changedItemState(this);
               }
               break;
            case -3:
               if (this.type == 4) {
                  i = this.getSelectedIndex() - 1;
                  if (i < 0) {
                     i = this.size() - 1;
                  }

                  this.setSelectedIndex(i, true);
                  this.owner.changedItemState(this);
               }
               break;
            case 35:
               Display display = this.owner.myDisplay;
               TruncatedItemScreen tScreen = display.getTruncatedItemScreen();
               ChoiceHandler.ChoiceElement element = this.handler.choiceElements[hilighted];
               tScreen.showElement(display, this.owner, element.stringPart, element.imagePart, false);
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
            if (this.type == 4) {
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
         if (this.type == 4) {
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
      if (this.type == 4) {
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
            if (consumed) {
               this.owner.updateSoftkeys(true);
            }

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
                     this.owner.updateSoftkeys(true);
                     return true;
                  }

                  return false;
               }

               if (this.type == 4) {
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

               if (this.type != 4) {
                  consumed = this.cgScrollUp(lh, viewportHeight, visRect);
               }
               break;
            case 6:
               if (this.size() == 0) {
                  return false;
               }

               if (this.type != 4) {
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
         this.handler.choiceElements[this.handler.highlightedElement].highlighted = false;
      }

      this.traversedIn = false;
   }

   void callShowNotify() {
      super.callShowNotify();
      if (this.size() > 0 && !this.hasFocus) {
         ChoiceHandler.ChoiceElement[] elements = this.handler.choiceElements;
         elements[this.handler.highlightedElement].highlighted = false;
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

   private void createHandler() {
      UIStyle ui = Displayable.uistyle;
      this.handler = new ChoiceHandler(false, true, Displayable.screenNormMainZone);
      this.handler.wrapOn = this.currentFitPolicy == 1 || this.currentFitPolicy == 0 && DEFAULT_FIT_POLICY == 1;
      if (this.type == 4) {
         this.handler.elementTextZone = ui.getZone(17);
         this.handler.elementIconGraphicZone = null;
         this.handler.elementChoiceBoxZone = null;
         this.handler.auxTextZone = ui.getZone(18);
         this.handler.auxIconZone = ui.getZone(19);
         this.handler.auxBoxZone = null;
         this.handler.checkIconNotSelected = null;
         this.handler.checkIconSelected = null;
         this.popupMainZoneH = Displayable.screenNormMainZone.height;
      } else {
         this.handler.elementTextZone = ui.getZone(8);
         this.handler.elementIconGraphicZone = null;
         this.handler.elementChoiceBoxZone = ui.getZone(7);
         this.handler.auxTextZone = ui.getZone(10);
         this.handler.auxIconZone = ui.getZone(11);
         this.handler.auxBoxZone = ui.getZone(9);
         if (this.type == 1) {
            this.handler.checkIconNotSelected = Pixmap.createPixmap(9);
            this.handler.checkIconSelected = Pixmap.createPixmap(8);
         } else {
            this.handler.checkIconNotSelected = Pixmap.createPixmap(7);
            this.handler.checkIconSelected = Pixmap.createPixmap(6);
         }
      }

   }

   private void setWrapping(boolean wrapOn) {
      this.handler.wrapOn = wrapOn;
      if (this.handler.choiceElements != null) {
         this.handler.changeLayout();
      }

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
         this.handler.choiceElements[this.handler.highlightedElement].highlighted = false;
         if (this.type == 4) {
            ChoiceHandler.ChoiceElement ce = this.popupElement;
            this.popupElement.highlighted = true;
            itemH = ce.height + 2 * POPUP_BORDER_HEIGHT;
         } else {
            ChoiceHandler.ChoiceElement[] elements = this.handler.choiceElements;
            this.handler.highlightedElement = dir == 6 ? 0 : this.handler.highlightedElement;
            this.handler.highlightedElement = dir == 1 ? elements.length - 1 : this.handler.highlightedElement;
            ChoiceHandler.ChoiceElement var10000 = elements[this.handler.highlightedElement];
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

         this.handler.choiceElements[this.handler.highlightedElement].highlighted = true;
         this.repaint();
         return true;
      }
   }

   private void swapPopupHandlerZones(boolean setPopup) {
      if (setPopup) {
         this.handler.mainZone = this.owner == null ? Displayable.screenNormMainZone : this.owner.getMainZone();
         if (this.handler.choiceElements[this.handler.highlightedElement].imagePart == null) {
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
         ChoiceHandler.ChoiceElement ce = this.handler.choiceElements[this.handler.highlightedElement];
         if (this.popupElement == null) {
            this.swapPopupHandlerZones(true);
            this.popupElement = this.handler.new ChoiceElement(ce.stringPart, ce.imagePart);
            this.popupElement.setFont(ce.jFont);
            this.popupElement.highlighted = true;
            this.popupElement.displayedRatio = 0;
            this.popupElement.startLine = 0;
            this.popupElement.startPage = 0;
            this.popupElement.borderH = (short)POPUP_BORDER_HEIGHT;
            this.popupElement.selected = ce.selected;
            this.swapPopupHandlerZones(false);
         }

         if (this.owner != null && this.popupMainZoneH != this.owner.getMainZone().height) {
            this.swapPopupHandlerZones(true);
            this.popupMainZoneH = this.owner.getMainZone().height;
            this.swapPopupHandlerZones(false);
         }

      }
   }

   private void updatePopupElement() {
      if (this.size() != 0) {
         ChoiceHandler.ChoiceElement ce = this.handler.choiceElements[this.handler.highlightedElement];
         this.swapPopupHandlerZones(true);
         this.popupMainZoneH = this.owner == null ? Displayable.screenNormMainZone.height : this.owner.getMainZone().height;
         if (this.popupElement == null) {
            this.popupElement = this.handler.new ChoiceElement(ce.stringPart, ce.imagePart);
            this.popupElement.borderH = (short)POPUP_BORDER_HEIGHT;
         } else {
            this.popupElement.set(ce.stringPart, ce.imagePart);
         }

         this.popupElement.setFont(ce.jFont);
         this.popupElement.highlighted = true;
         this.popupElement.displayedRatio = 0;
         this.popupElement.startLine = 0;
         this.popupElement.startPage = 0;
         this.popupElement.selected = ce.selected;
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
         this.handler.choiceElements[this.handler.highlightedElement].highlighted = this.hasFocus;
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
         this.handler.choiceElements[0].startLine = 0;
         this.handler.choiceElements[this.handler.highlightedElement].highlighted = this.hasFocus;
         this.handler.paintElements(x, y, g, 0);
      }

   }

   private void popupUpdated(int lh, int viewportHeight, int[] visRect) {
      int lineH = false;
      this.createPopupElement();
      this.popupElement.highlighted = true;
      ChoiceHandler.ChoiceElement ce = this.popupElement;
      int lineH = ce.lines[0].getTextLineHeight();
      this.invariantStart = false;
      visRect[1] = 0;
      visRect[3] = lh + ce.height + 2 * POPUP_BORDER_HEIGHT < viewportHeight ? lh + ce.height + 2 * POPUP_BORDER_HEIGHT : viewportHeight;
      this.repaint();
   }

   private void cgUpdated(int lh, int viewportHeight, int[] visRect) {
      int h2Focused = lh;
      int lineH = false;
      ChoiceHandler.ChoiceElement[] elements = this.handler.choiceElements;
      ChoiceHandler.ChoiceElement ce = elements[this.handler.highlightedElement];
      int lineH = ce.lines[0].getTextLineHeight();
      this.handler.setMainZone(this.owner.getMainZone());

      int h2BottomFocused;
      for(h2BottomFocused = 0; h2BottomFocused < this.handler.highlightedElement; ++h2BottomFocused) {
         h2Focused += elements[h2BottomFocused].height;
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
      int lineH = false;
      ChoiceHandler.ChoiceElement[] elements = this.handler.choiceElements;
      ChoiceHandler.ChoiceElement ce = elements[this.handler.highlightedElement];
      int lineH = ce.lines[0].getTextLineHeight();
      this.handler.setMainZone(this.owner.getMainZone());

      int tmpStart;
      for(tmpStart = elements.length - 1; tmpStart > this.handler.highlightedElement; --tmpStart) {
         hFocused2Last += elements[tmpStart].height;
      }

      if (this.handler.highlightedElement > 0) {
         hFocused2Last += ce.height;
         ce.highlighted = false;
         ce = elements[--this.handler.highlightedElement];
         ce.highlighted = true;
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
      int hPagesDisplayed = false;
      int h2NewFocused = lh;
      int lineH = false;
      ChoiceHandler.ChoiceElement[] elements = this.handler.choiceElements;
      ChoiceHandler.ChoiceElement ce = elements[this.handler.highlightedElement];
      int lineH = ce.lines[0].getTextLineHeight();
      this.handler.setMainZone(this.owner.getMainZone());
      if (this.handler.highlightedElement >= elements.length - 1) {
         return false;
      } else {
         ce.highlighted = false;
         ce = elements[++this.handler.highlightedElement];
         ce.highlighted = true;

         for(int i = 0; i < this.handler.highlightedElement; ++i) {
            h2NewFocused += elements[i].height;
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
      POPUP_NOICON_TEXT_ZONE = Displayable.uistyle.getZone(37);
      POPUP_ICON_TEXT_ZONE = Displayable.uistyle.getZone(38);
      POPUP_ICON_IMAGE_ZONE = Displayable.uistyle.getZone(39);
      POPUP_LEFT_ARROW_ZONE = Displayable.uistyle.getZone(40);
      POPUP_RIGHT_ARROW_ZONE = Displayable.uistyle.getZone(41);
      POPUP_BORDER_ZONE = Displayable.uistyle.getZone(36);
      ARROW_LEFT_POPUP_ICON = Pixmap.createPixmap(11);
      ARROW_RIGHT_POPUP_ICON = Pixmap.createPixmap(12);
      POPUP_LIST_NOICON_TEXT_ZONE = Displayable.uistyle.getZone(17);
      POPUP_LIST_ICON_TEXT_ZONE = Displayable.uistyle.getZone(18);
      POPUP_LIST_ICON_IMAGE_ZONE = Displayable.uistyle.getZone(19);
      POPUP_BORDER_HEIGHT = POPUP_BORDER_ZONE.getMarginTop();
   }

   private class PopupListListener implements CommandListener {
      private PopupListListener() {
      }

      public void commandAction(Command c, Displayable d) {
         if (d.equals(ChoiceGroup.this.popupList)) {
            int i;
            if (c != null && c.getCommandType() == 2) {
               if (ChoiceGroup.this.handler != null) {
                  i = ChoiceGroup.this.handler.highlightedElement;
                  ChoiceGroup.this.handler.choiceElements[i].highlighted = false;
                  ChoiceGroup.this.handler.choiceElements[i].selected = false;
                  ChoiceGroup.this.handler.highlightedElement = ChoiceGroup.this.oldHighlighted;
                  ChoiceGroup.this.handler.choiceElements[ChoiceGroup.this.oldHighlighted].highlighted = true;
                  ChoiceGroup.this.handler.choiceElements[ChoiceGroup.this.oldHighlighted].selected = true;
               }

               if (ChoiceGroup.this.owner != null) {
                  ChoiceGroup.this.owner.myDisplay.setCurrentInternal((Displayable)null, ChoiceGroup.this.owner);
               } else {
                  d.myDisplay.setCurrent(d.myDisplay.getCurrent());
               }
            }

            if (c.equals(List.SELECT_COMMAND)) {
               i = ChoiceGroup.this.popupList.getSelectedIndex();
               ChoiceGroup.this.setSelectedIndex(i, true);
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
