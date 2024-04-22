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
   private static final Zone POPUP_ARROW_ZONE;
   private static final Zone POPUP_BORDER_ZONE;
   private static final Pixmap ARROW_POPUP_ICON;
   private static final Zone POPUP_LIST_NOICON_TEXT_ZONE;
   private static final Zone POPUP_LIST_ICON_TEXT_ZONE;
   private static final Zone POPUP_LIST_ICON_IMAGE_ZONE;
   private static final int POPUP_BORDER_HEIGHT;
   private int currentFitPolicy;
   private int type;
   private ChoiceHandler handler;
   private boolean traversedIn;
   private int pagesDisplayed;
   private boolean invariantStart;
   private List popupList;
   private ChoiceHandler.ChoiceElement popupElement;
   private int popupMainZoneH;
   private int oldHighlighted;
   private ChoiceGroup.PopupListListener popupListListener;

   public ChoiceGroup(String var1, int var2) {
      super(var1);
      this.currentFitPolicy = DEFAULT_FIT_POLICY;
      if (var2 != 1 && var2 != 2 && var2 != 4) {
         throw new IllegalArgumentException("Illegal type for creating a ChoiceGroup or a List.");
      } else {
         synchronized(Display.LCDUILock) {
            this.type = var2;
            this.currentFitPolicy = DEFAULT_FIT_POLICY;
            if (this.type == 4) {
               this.popupListListener = new ChoiceGroup.PopupListListener();
            }

         }
      }
   }

   public ChoiceGroup(String var1, int var2, String[] var3, Image[] var4) {
      this(var1, var2);
      if (var3 == null) {
         throw new NullPointerException("ChoiceGroup: stringElements==null.");
      } else if (var4 != null && var4.length != var3.length) {
         throw new IllegalArgumentException("ChoiceGroup: length of stringElements and imageElements are not the same.");
      } else {
         synchronized(Display.LCDUILock) {
            for(int var6 = 0; var6 < var3.length; ++var6) {
               this.append(var3[var6], var4 == null ? null : var4[var6]);
            }

         }
      }
   }

   public int size() {
      synchronized(Display.LCDUILock) {
         return this.handler != null && this.handler.choiceElements != null ? this.handler.choiceElements.length : 0;
      }
   }

   public String getString(int var1) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && var1 >= 0 && var1 < this.handler.choiceElements.length) {
            return this.handler.choiceElements[var1].stringPart;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public Image getImage(int var1) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && var1 >= 0 && var1 < this.handler.choiceElements.length) {
            ChoiceHandler.ChoiceElement var3 = this.handler.choiceElements[var1];
            return var3.mutableImagePart != null ? var3.mutableImagePart : var3.imagePart;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public int append(String var1, Image var2) {
      synchronized(Display.LCDUILock) {
         boolean var4 = false;
         int var7;
         if (this.popupList != null) {
            var7 = this.popupList.append(var1, var2);
         } else if (this.size() == 0) {
            this.createHandler();
            var7 = this.handler.append(var1, var2);
            if (this.type != 2) {
               this.handler.choiceElements[0].selected = true;
            }
         } else {
            var7 = this.handler.append(var1, var2);
         }

         this.invariantStart = true;
         if (this.type != 4 || this.size() == 1) {
            this.invalidate();
         }

         return var7;
      }
   }

   public void insert(int var1, String var2, Image var3) {
      synchronized(Display.LCDUILock) {
         if (this.popupList != null) {
            this.popupList.insert(var1, var2, var3);
         } else if (this.size() == 0) {
            this.createHandler();
            this.handler.insert(var1, var2, var3);
            if (this.type != 2) {
               this.handler.choiceElements[0].selected = true;
            }
         } else {
            this.handler.insert(var1, var2, var3);
         }

         this.invariantStart = true;
         if (this.type != 4 || this.size() == 1) {
            this.invalidate();
         }

      }
   }

   public void delete(int var1) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && var1 >= 0 && var1 < this.handler.choiceElements.length) {
            int var3 = this.handler.highlightedElement;
            if (this.popupList != null) {
               this.popupList.delete(var1);
               this.handler.choiceElements[this.handler.highlightedElement].highlighted = this.hasFocus;
            } else {
               this.handler.delete(var1);
               if (this.handler.choiceElements != null && this.handler.choiceElements.length > 0) {
                  if (this.type != 2 && this.getSelectedIndex() == -1) {
                     int var4 = this.handler.choiceElements.length - 1;
                     this.handler.choiceElements[var1 > var4 ? var4 : var1].selected = true;
                  }

                  this.handler.choiceElements[this.handler.highlightedElement].highlighted = this.hasFocus;
               } else {
                  this.handler = null;
               }
            }

            this.invariantStart = true;
            if (this.type != 4 || var1 == var3 || this.handler == null) {
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

   public void set(int var1, String var2, Image var3) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && var1 >= 0 && var1 < this.handler.choiceElements.length) {
            if (this.popupList != null) {
               this.popupList.set(var1, var2, var3);
            } else {
               this.handler.set(var1, var2, var3);
            }

            if (var1 != this.handler.highlightedElement) {
               this.invariantStart = true;
            }

            if (this.type != 4 || var1 == this.handler.highlightedElement) {
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

   public boolean isSelected(int var1) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && var1 >= 0 && var1 < this.handler.choiceElements.length) {
            return this.handler.choiceElements[var1].selected;
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

   public int getSelectedFlags(boolean[] var1) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0) {
            return this.handler.getSelectedFlags(var1);
         } else {
            for(int var3 = 0; var3 < var1.length; ++var3) {
               var1[var3] = false;
            }

            return 0;
         }
      }
   }

   public void setSelectedIndex(int var1, boolean var2) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && var1 >= 0 && var1 <= this.handler.choiceElements.length - 1) {
            if (this.popupList != null) {
               this.popupList.setSelectedIndex(var1, var2);
            } else {
               if (this.type != 2) {
                  if (!var2 || this.handler.choiceElements[var1].selected) {
                     return;
                  }

                  for(int var4 = 0; var4 < this.handler.choiceElements.length; ++var4) {
                     this.handler.choiceElements[var4].selected = false;
                  }
               }

               this.handler.setSelectedIndex(var1, var2);
               this.repaint();
            }

            if (this.type == 4) {
               this.handler.choiceElements[this.handler.highlightedElement].highlighted = false;
               this.handler.highlightedElement = var1;
               this.handler.choiceElements[var1].highlighted = true;
               this.updatePopupElement();
               this.invalidate();
            }

         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void setSelectedFlags(boolean[] var1) {
      synchronized(Display.LCDUILock) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (this.handler != null && this.handler.choiceElements != null) {
            if (this.type == 2) {
               this.handler.setSelectedFlags(var1);
               this.repaint();
            } else {
               if (var1.length < this.handler.choiceElements.length) {
                  throw new IllegalArgumentException();
               }

               int var3 = 0;
               boolean var4 = false;

               for(int var5 = 0; var5 < this.handler.choiceElements.length; ++var5) {
                  this.handler.choiceElements[var5].selected = false;
                  if (var1[var5] && !var4) {
                     var4 = true;
                     var3 = var5;
                  }
               }

               this.setSelectedIndex(var3, true);
            }

         }
      }
   }

   public void setFitPolicy(int var1) {
      if (var1 >= 0 && var1 <= 2) {
         synchronized(Display.LCDUILock) {
            if (this.popupList != null) {
               this.popupList.setFitPolicy(var1);
            } else {
               if (this.currentFitPolicy != var1) {
                  if (var1 == 0) {
                     if (this.currentFitPolicy != DEFAULT_FIT_POLICY && this.handler != null) {
                        this.setWrapping(DEFAULT_FIT_POLICY == 1);
                     }

                     this.currentFitPolicy = var1;
                  } else {
                     this.currentFitPolicy = var1;
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

   public void setFont(int var1, Font var2) {
      synchronized(Display.LCDUILock) {
         if (this.popupList != null) {
            this.popupList.setFont(var1, var2);
         } else {
            this.handler.setFont(var1, var2);
         }

         this.invariantStart = var1 != this.handler.highlightedElement ? true : this.invariantStart;
         if (this.type != 4 || var1 == this.handler.highlightedElement) {
            if (this.type == 4) {
               this.updatePopupElement();
            }

            this.invalidate();
         }

      }
   }

   public Font getFont(int var1) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0 && var1 >= 0 && var1 < this.handler.choiceElements.length) {
            Font var3 = this.handler.choiceElements[var1].jFont;
            return var3 == null ? Font.getDefaultFont() : var3;
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
      Command[] var1 = null;
      if (this.size() == 0) {
         return var1;
      } else {
         ChoiceHandler.ChoiceElement[] var2 = this.handler.choiceElements;
         ChoiceHandler.ChoiceElement var3 = var2[this.handler.highlightedElement];
         switch(this.type) {
         case 1:
            var1 = new Command[]{EXCLUSIVE_SELECT};
            break;
         case 2:
            var1 = var3.selected ? new Command[]{MULTI_UNMARK} : new Command[]{MULTI_MARK};
         case 3:
         default:
            break;
         case 4:
            var1 = new Command[]{POPUP_OPEN};
         }

         if (!this.handler.wrapOn && (this.type == 4 && this.popupElement != null && this.popupElement.isTruncated() || this.type != 4 && var3.isTruncated())) {
            if (var1 != null) {
               Command[] var4 = new Command[var1.length + 1];
               System.arraycopy(var1, 0, var4, 0, var1.length);
               var4[var1.length] = VIEW;
               var1 = var4;
            } else {
               var1 = new Command[]{VIEW};
            }
         }

         return var1;
      }
   }

   boolean launchExtraCommand(Command var1) {
      if (this.size() == 0) {
         return false;
      } else {
         ChoiceHandler.ChoiceElement[] var2 = this.handler.choiceElements;
         if (var1.equals(VIEW)) {
            Display var7 = this.owner.myDisplay;
            TruncatedItemScreen var4 = var7.getTruncatedItemScreen();
            ChoiceHandler.ChoiceElement var5 = this.handler.choiceElements[this.handler.highlightedElement];
            var4.showElement(var7, this.owner, var5.stringPart, var5.imagePart, true);
            return true;
         } else if (var1.equals(POPUP_OPEN)) {
            if (this.popupList == null) {
               this.popupList = new List(this.owner.getTitle(), this.handler);
               this.popupList.addCommand(new Command(2, 3));
               this.popupList.setCommandListener(this.popupListListener);
            }

            int var6 = this.getLabelHeight(this.owner.getMainZone().width);
            this.oldHighlighted = this.handler.highlightedElement;
            this.pagesDisplayed = var2[this.oldHighlighted].isMultipage && var6 == 0 ? 1 : 0;
            this.owner.myDisplay.setCurrentInternal(this.owner, this.popupList);
            return true;
         } else {
            boolean var3 = var2[this.handler.highlightedElement].selected;
            this.setSelectedIndex(this.handler.highlightedElement, !var3);
            this.owner.changedItemState(this);
            this.owner.updateSoftkeys(true);
            if (this.owner.isShown()) {
               this.owner.repaintRequest();
            }

            return false;
         }
      }
   }

   void callKeyPressed(int var1, int var2) {
      synchronized(Display.LCDUILock) {
         if (this.size() != 0) {
            int var4 = this.handler.highlightedElement;
            switch(var1) {
            case -10:
               if (this.type != 4) {
                  boolean var5 = false;
                  var5 = this.handler.choiceElements[var4].selected;
                  this.setSelectedIndex(var4, !var5);
                  this.owner.changedItemState(this);
                  this.owner.updateSoftkeys(true);
               } else {
                  ChoiceHandler.ChoiceElement[] var12 = this.handler.choiceElements;
                  if (this.popupList == null) {
                     this.popupList = new List(this.owner.getTitle(), this.handler);
                     this.popupList.addCommand(new Command(2, 3));
                     this.popupList.setCommandListener(this.popupListListener);
                  }

                  int var6 = this.getLabelHeight(this.owner.getMainZone().width);
                  this.oldHighlighted = this.handler.highlightedElement;
                  this.pagesDisplayed = var12[this.oldHighlighted].isMultipage && var6 == 0 ? 1 : 0;
                  this.owner.myDisplay.setCurrentInternal(this.owner, this.popupList);
               }
               break;
            case 35:
               Display var7 = this.owner.myDisplay;
               TruncatedItemScreen var8 = var7.getTruncatedItemScreen();
               ChoiceHandler.ChoiceElement var9 = this.handler.choiceElements[var4];
               var8.showElement(var7, this.owner, var9.stringPart, var9.imagePart, false);
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

   void callPaint(Graphics var1, int var2, int var3, boolean var4) {
      super.callPaint(var1, var2, var3, var4);
      synchronized(Display.LCDUILock) {
         int var6 = var1.getTranslateX();
         int var7 = var1.getTranslateY();
         this.hasFocus = var4;
         var3 -= this.getLabelHeight(-1);
         if (this.isFocusable() && var3 != 0) {
            if (this.type == 4) {
               this.implPopupPaint(var1, var6, var7, var2, var3);
            } else {
               this.implPaint(var1, var6, var7, var2, var3);
            }

         }
      }
   }

   int callPreferredHeight(int var1) {
      boolean var2 = false;
      if (!this.isFocusable()) {
         return 0;
      } else {
         int var3;
         int var4;
         if (this.type == 4) {
            this.createPopupElement();
            var4 = this.popupElement == null ? 0 : this.popupElement.height;
            var4 += 2 * POPUP_BORDER_HEIGHT;
            var3 = POPUP_NOICON_TEXT_ZONE.width;
         } else {
            var4 = this.handler == null ? 0 : this.handler.listHeight;
            var4 += this.size() == 0 ? 2 : 0;
            var3 = (this.owner == null ? Displayable.screenNormMainZone : this.owner.getMainZone()).width;
         }

         if (this.size() == 0) {
            var4 += this.getEmptyStringHeight(var3, (Font)null) + 1;
         }

         return var4;
      }
   }

   int callPreferredWidth(int var1) {
      int var2 = this.owner == null ? Displayable.screenNormMainZone.width : this.owner.getWidth();
      if (this.type == 4) {
         var2 = POPUP_BORDER_ZONE.width;
      }

      return !this.isFocusable() && this.label == null ? 0 : var2;
   }

   boolean callTraverse(int var1, int var2, int var3, int[] var4) {
      super.callTraverse(var1, var2, var3, var4);
      synchronized(Display.LCDUILock) {
         int var6 = this.getLabelHeight(this.owner.getMainZone().width);
         boolean var7 = false;
         if (!this.traversedIn) {
            var7 = this.acquireFocus(var6, var1, var3, var4);
            this.traversedIn = true;
            if (var7) {
               this.owner.updateSoftkeys(true);
            }

            return var7;
         } else {
            switch(var1) {
            case 0:
               if (this.size() == 0) {
                  if (this.isFocusable()) {
                     int var8 = this.callPreferredHeight(-1) + var6;
                     var4[1] = 0;
                     var4[3] = var3 < var8 ? var3 : var8;
                     this.repaint();
                     this.owner.updateSoftkeys(true);
                     return true;
                  }

                  return false;
               }

               if (this.type == 4) {
                  this.popupUpdated(var6, var3, var4);
               } else {
                  this.cgUpdated(var6, var3, var4);
               }

               var7 = true;
               break;
            case 1:
               if (this.size() == 0) {
                  return false;
               }

               if (this.type == 4) {
                  var7 = this.popupScrollUp(var6, var3, var4);
               } else {
                  var7 = this.cgScrollUp(var6, var3, var4);
               }
               break;
            case 6:
               if (this.size() == 0) {
                  return false;
               }

               if (this.type == 4) {
                  var7 = this.popupScrollDown(var6, var3, var4);
               } else {
                  var7 = this.cgScrollDown(var6, var3, var4);
               }
               break;
            default:
               var7 = false;
            }

            var4[3] = var4[3] < var3 ? var4[3] : var3;
            if (var7) {
               this.owner.updateSoftkeys(true);
            }

            return var7;
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
         ChoiceHandler.ChoiceElement[] var1 = this.handler.choiceElements;
         var1[this.handler.highlightedElement].highlighted = false;
      }

   }

   boolean shouldSkipTraverse() {
      return (this.label == null || "".equals(this.label)) && this.size() == 0 && (this.itemCommands == null || this.itemCommands.length() <= 0);
   }

   void setOwner(Screen var1) {
      super.setOwner(var1);
      synchronized(Display.LCDUILock) {
         this.pagesDisplayed = 0;
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

   public void setPreferredSize(int var1, int var2) {
      if (var1 < -1 || var2 < -1) {
         throw new IllegalArgumentException();
      }
   }

   private void createHandler() {
      UIStyle var1 = Displayable.uistyle;
      this.handler = new ChoiceHandler(false, true, Displayable.screenNormMainZone);
      this.handler.wrapOn = this.currentFitPolicy == 1 || this.currentFitPolicy == 0 && DEFAULT_FIT_POLICY == 1;
      if (this.type == 4) {
         this.handler.elementTextZone = var1.getZone(17);
         this.handler.elementIconGraphicZone = null;
         this.handler.elementChoiceBoxZone = null;
         this.handler.auxTextZone = var1.getZone(18);
         this.handler.auxIconZone = var1.getZone(19);
         this.handler.auxBoxZone = null;
         this.handler.checkIconNotSelected = null;
         this.handler.checkIconSelected = null;
         this.popupMainZoneH = Displayable.screenNormMainZone.height;
      } else {
         this.handler.elementTextZone = var1.getZone(8);
         this.handler.elementIconGraphicZone = null;
         this.handler.elementChoiceBoxZone = var1.getZone(7);
         this.handler.auxTextZone = var1.getZone(10);
         this.handler.auxIconZone = var1.getZone(11);
         this.handler.auxBoxZone = var1.getZone(9);
         if (this.type == 1) {
            this.handler.checkIconNotSelected = Pixmap.createPixmap(9);
            this.handler.checkIconSelected = Pixmap.createPixmap(8);
         } else {
            this.handler.checkIconNotSelected = Pixmap.createPixmap(7);
            this.handler.checkIconSelected = Pixmap.createPixmap(6);
         }
      }

   }

   private void setWrapping(boolean var1) {
      this.handler.wrapOn = var1;
      if (this.handler.choiceElements != null) {
         this.handler.changeLayout();
      }

   }

   private boolean acquireFocus(int var1, int var2, int var3, int[] var4) {
      this.invariantStart = false;
      int var8;
      if (this.size() == 0) {
         if (this.isFocusable()) {
            var8 = this.callPreferredHeight(-1) + var1;
            var4[1] = 0;
            var4[3] = var3 < var8 ? var3 : var8;
            this.traversedIn = true;
            this.repaint();
            return true;
         } else {
            return false;
         }
      } else {
         boolean var7 = false;
         this.handler.setMainZone(this.owner.getMainZone());
         this.handler.choiceElements[this.handler.highlightedElement].highlighted = false;
         ChoiceHandler.ChoiceElement var6;
         if (this.type == 4) {
            var6 = this.popupElement;
            this.popupElement.highlighted = true;
            var8 = var6.height + 2 * POPUP_BORDER_HEIGHT;
         } else {
            ChoiceHandler.ChoiceElement[] var5 = this.handler.choiceElements;
            this.handler.highlightedElement = var2 == 6 ? 0 : this.handler.highlightedElement;
            this.handler.highlightedElement = var2 == 1 ? var5.length - 1 : this.handler.highlightedElement;
            var6 = var5[this.handler.highlightedElement];
            var8 = this.handler.listHeight;
         }

         if (var2 == 6) {
            var4[1] = 0;
            var4[3] = var3 < var8 + var1 ? var3 : var8 + var1;
            if (var6.isMultipage) {
               this.pagesDisplayed = var1 == 0 ? 1 : 0;
            }
         }

         if (var2 == 1) {
            if (var3 >= var8 + var1) {
               var4[1] = 0;
               var4[3] = var8 + var1;
            } else {
               var4[1] = var8 + var1 - var3;
               var4[3] = var3;
            }

            if (var6.isMultipage) {
               this.pagesDisplayed = var6.startingPages.length;
            }
         }

         this.handler.choiceElements[this.handler.highlightedElement].highlighted = true;
         this.repaint();
         return true;
      }
   }

   private void swapPopupHandlerZones(boolean var1) {
      if (var1) {
         this.handler.mainZone = this.owner == null ? Displayable.screenNormMainZone : this.owner.getMainZone();
         if (this.handler.choiceElements[this.handler.highlightedElement].imagePart == null) {
            this.handler.elementTextZone = POPUP_NOICON_TEXT_ZONE;
            this.handler.elementIconGraphicZone = null;
         } else {
            this.handler.elementTextZone = POPUP_ICON_TEXT_ZONE;
            this.handler.elementIconGraphicZone = POPUP_ICON_IMAGE_ZONE;
         }

         this.handler.elementChoiceBoxZone = POPUP_ARROW_ZONE;
         this.handler.checkIconSelected = ARROW_POPUP_ICON;
         this.handler.hasPartialBehaviuor = true;
         this.handler.hasCyclicBehaviour = false;
      } else {
         this.handler.mainZone = Displayable.screenNormMainZone;
         this.handler.elementChoiceBoxZone = null;
         if (this.handler.auxIconZone == null) {
            this.handler.elementTextZone = POPUP_LIST_ICON_TEXT_ZONE;
            this.handler.elementIconGraphicZone = POPUP_LIST_ICON_IMAGE_ZONE;
         } else {
            this.handler.elementTextZone = POPUP_LIST_NOICON_TEXT_ZONE;
            this.handler.elementIconGraphicZone = null;
         }

         this.handler.checkIconNotSelected = null;
         this.handler.checkIconSelected = null;
         this.handler.hasPartialBehaviuor = false;
         this.handler.hasCyclicBehaviour = true;
      }

   }

   private void createPopupElement() {
      if (this.size() != 0) {
         ChoiceHandler.ChoiceElement var1 = this.handler.choiceElements[this.handler.highlightedElement];
         if (this.popupElement == null) {
            this.swapPopupHandlerZones(true);
            this.popupElement = this.handler.new ChoiceElement(var1.stringPart, var1.imagePart);
            this.popupElement.setFont(var1.jFont);
            this.popupElement.highlighted = true;
            this.popupElement.displayedRatio = 0;
            this.popupElement.startLine = 0;
            this.popupElement.startPage = 0;
            this.popupElement.borderH = (short)POPUP_BORDER_HEIGHT;
            this.popupElement.selected = var1.selected;
            this.swapPopupHandlerZones(false);
         }

         if (this.owner != null && this.popupMainZoneH != this.owner.getMainZone().height) {
            this.swapPopupHandlerZones(true);
            this.popupElement.setStartingPages(this.owner.getMainZone().height);
            this.popupMainZoneH = this.owner.getMainZone().height;
            this.swapPopupHandlerZones(false);
         }

      }
   }

   private void updatePopupElement() {
      if (this.size() != 0) {
         ChoiceHandler.ChoiceElement var1 = this.handler.choiceElements[this.handler.highlightedElement];
         this.swapPopupHandlerZones(true);
         this.popupMainZoneH = this.owner == null ? Displayable.screenNormMainZone.height : this.owner.getMainZone().height;
         if (this.popupElement == null) {
            this.popupElement = this.handler.new ChoiceElement(var1.stringPart, var1.imagePart);
            this.popupElement.borderH = (short)POPUP_BORDER_HEIGHT;
         } else {
            this.popupElement.set(var1.stringPart, var1.imagePart);
         }

         this.popupElement.setStartingPages(this.popupMainZoneH);
         this.pagesDisplayed = this.popupElement.isMultipage ? 1 : this.pagesDisplayed;
         this.popupElement.setFont(var1.jFont);
         this.popupElement.highlighted = true;
         this.popupElement.displayedRatio = 0;
         this.popupElement.startLine = 0;
         this.popupElement.startPage = 0;
         this.popupElement.selected = var1.selected;
         this.swapPopupHandlerZones(false);
      }
   }

   private void implPopupPaint(Graphics var1, int var2, int var3, int var4, int var5) {
      int var6 = var5 - 2 * POPUP_BORDER_HEIGHT;
      Zone var7 = this.owner.getMainZone();
      Displayable.uistyle.drawBorder(var1.getImpl(), var2, var3, var4, var5, POPUP_BORDER_ZONE.getBorderType(), this.hasFocus);
      if (this.size() == 0) {
         this.paintEmptyString(var1, POPUP_NOICON_TEXT_ZONE.x + var2, var3 + POPUP_NOICON_TEXT_ZONE.y, POPUP_NOICON_TEXT_ZONE.width - 1, var6, this.hasFocus);
         Displayable.uistyle.drawPixmapInZone(var1.getImpl(), POPUP_ARROW_ZONE, var7.x, var3, ARROW_POPUP_ICON);
      } else {
         this.swapPopupHandlerZones(true);
         this.createPopupElement();
         this.handler.sumH = 0;
         this.handler.choiceElements[this.handler.highlightedElement].highlighted = this.hasFocus;
         this.popupElement.highlighted = this.hasFocus;
         this.popupElement.paintElement(var2, var3, var6, var1, true);
         this.swapPopupHandlerZones(false);
      }

   }

   private void implPaint(Graphics var1, int var2, int var3, int var4, int var5) {
      Zone var6 = this.owner.getMainZone();
      if (this.size() == 0) {
         this.paintEmptyString(var1, var6.x, var3, var4, var5, this.hasFocus);
      } else {
         this.handler.startElement = 0;
         this.handler.choiceElements[0].startLine = 0;
         this.handler.choiceElements[this.handler.highlightedElement].highlighted = this.hasFocus;
         this.handler.paintElements(var2, var3, var1, 0);
      }

   }

   private void popupUpdated(int var1, int var2, int[] var3) {
      boolean var4 = false;
      this.createPopupElement();
      this.popupElement.highlighted = true;
      ChoiceHandler.ChoiceElement var5 = this.popupElement;
      int var6 = var5.lines[0].getTextLineHeight();
      if (var5.isMultipage) {
         if (this.invariantStart && this.pagesDisplayed > 1 && this.pagesDisplayed <= var5.startingPages.length) {
            var3[1] = var1 + POPUP_BORDER_HEIGHT + var5.startingPages[this.pagesDisplayed - 1] * var6 + this.handler.elementTextZone.getMarginTop();
            var3[3] = var2;
            this.invariantStart = false;
         } else if (this.pagesDisplayed <= 1 || !this.invariantStart) {
            var3[1] = 0;
            this.pagesDisplayed = var1 != 0 ? 0 : 1;
            var3[3] = var2;
         }
      } else {
         this.pagesDisplayed = 0;
         this.invariantStart = false;
         var3[1] = 0;
         var3[3] = var1 + var5.height + 2 * POPUP_BORDER_HEIGHT < var2 ? var1 + var5.height + 2 * POPUP_BORDER_HEIGHT : var2;
      }

      this.repaint();
   }

   private void cgUpdated(int var1, int var2, int[] var3) {
      int var6 = var1;
      boolean var7 = false;
      ChoiceHandler.ChoiceElement[] var4 = this.handler.choiceElements;
      ChoiceHandler.ChoiceElement var5 = var4[this.handler.highlightedElement];
      int var9 = var5.lines[0].getTextLineHeight();
      this.handler.setMainZone(this.owner.getMainZone());

      int var8;
      for(var8 = 0; var8 < this.handler.highlightedElement; ++var8) {
         var6 += var4[var8].height;
      }

      if (var5.isMultipage) {
         if (this.invariantStart && this.pagesDisplayed > 1 && this.pagesDisplayed <= var5.startingPages.length) {
            var8 = var6 + var5.startingPages[this.pagesDisplayed - 1] * var9 + this.handler.elementTextZone.getMarginTop();
            var3[1] = var8;
            var3[3] = var2;
            this.invariantStart = false;
         } else if (this.pagesDisplayed <= 1 || !this.invariantStart) {
            if (this.handler.highlightedElement == 0) {
               var3[1] = var6 - var1;
               this.pagesDisplayed = var1 != 0 ? 0 : 1;
            } else {
               this.pagesDisplayed = 1;
               var3[1] = var6;
            }

            var3[3] = var2;
         }
      } else {
         var8 = var6 + var5.height - var3[1];
         this.pagesDisplayed = 0;
         var8 = var8 < 0 ? -var8 : var8;
         if (var8 > var2 || var1 + this.handler.listHeight - var6 < var2) {
            var3[1] = var6 + var5.height - var2;
         }

         if (var1 + this.handler.listHeight < var2 || var3[1] < 0) {
            var3[1] = 0;
         }

         this.invariantStart = false;
      }

      this.repaint();
   }

   private boolean popupScrollUp(int var1, int var2, int[] var3) {
      boolean var6 = false;
      boolean var7 = false;
      boolean var8 = false;
      this.createPopupElement();
      this.popupElement.highlighted = true;
      ChoiceHandler.ChoiceElement var5 = this.popupElement;
      int var10 = var5.lines[0].getTextLineHeight();
      if (var5.isMultipage && this.pagesDisplayed > 1 && this.pagesDisplayed <= var5.startingPages.length) {
         --this.pagesDisplayed;
         int var9 = POPUP_BORDER_HEIGHT + (var5.lines.length - var5.startingPages[this.pagesDisplayed - 1]) * var10 + POPUP_NOICON_TEXT_ZONE.getMarginBottom() + (this.pagesDisplayed == 1 ? POPUP_NOICON_TEXT_ZONE.getMarginTop() : 0);
         var3[1] = var1 + 2 * POPUP_BORDER_HEIGHT + var5.height - var9;
         var3[3] = var2;
         this.repaint();
         return true;
      } else if (var1 != 0 && var3[1] != 0) {
         var3[1] = 0;
         var3[3] = var1 + 2 * POPUP_BORDER_HEIGHT + var5.height <= var2 ? var1 + 2 * POPUP_BORDER_HEIGHT + var5.height : var2;
         this.pagesDisplayed = 0;
         this.repaint();
         return true;
      } else {
         return false;
      }
   }

   private boolean cgScrollUp(int var1, int var2, int[] var3) {
      boolean var6 = false;
      int var7 = 0;
      boolean var8 = false;
      ChoiceHandler.ChoiceElement[] var4 = this.handler.choiceElements;
      ChoiceHandler.ChoiceElement var5 = var4[this.handler.highlightedElement];
      int var11 = var5.lines[0].getTextLineHeight();
      this.handler.setMainZone(this.owner.getMainZone());

      int var9;
      for(var9 = var4.length - 1; var9 > this.handler.highlightedElement; --var9) {
         var7 += var4[var9].height;
      }

      int var10;
      if (var5.isMultipage && this.pagesDisplayed > 1 && this.pagesDisplayed <= var5.startingPages.length) {
         --this.pagesDisplayed;
         var10 = var7 + (var5.lines.length - var5.startingPages[this.pagesDisplayed - 1]) * var11 + this.handler.elementTextZone.getMarginBottom() + (this.pagesDisplayed == 1 ? this.handler.elementTextZone.getMarginTop() : 0);
         var3[1] = var1 + this.handler.listHeight - var10;
         var3[3] = var2;
         this.repaint();
         return true;
      } else if (this.handler.highlightedElement > 0) {
         var7 += var5.height;
         var5.highlighted = false;
         var5 = var4[--this.handler.highlightedElement];
         var5.highlighted = true;
         if (var5.isMultipage) {
            this.pagesDisplayed = var5.startingPages.length;
            var10 = var7 + (var5.lines.length - var5.startingPages[this.pagesDisplayed - 1]) * var11 + this.handler.elementTextZone.getMarginBottom();
            var3[1] = var1 + this.handler.listHeight - var10;
            var3[3] = var2;
         } else {
            this.pagesDisplayed = 0;
            var10 = var7 + var5.height;
            if (var1 + this.handler.listHeight <= var2) {
               var3[1] = 0;
               var3[3] = var1 + this.handler.listHeight;
            } else {
               var9 = var1 + this.handler.listHeight - var10;
               if (var9 < var3[1]) {
                  var3[1] = var9;
               }

               var3[3] = var2;
            }
         }

         this.repaint();
         return true;
      } else if (var1 != 0 && var3[1] != 0) {
         this.pagesDisplayed = 0;
         var3[1] = 0;
         var3[3] = var1 + this.handler.listHeight <= var2 ? var1 + this.handler.listHeight : var2;
         this.repaint();
         return true;
      } else {
         return false;
      }
   }

   private boolean popupScrollDown(int var1, int var2, int[] var3) {
      boolean var5 = false;
      boolean var6 = false;
      this.createPopupElement();
      this.popupElement.highlighted = true;
      ChoiceHandler.ChoiceElement var4 = this.popupElement;
      int var9 = var4.lines[0].getTextLineHeight();
      if (var4.isMultipage && this.pagesDisplayed < var4.startingPages.length && this.pagesDisplayed >= 0) {
         int var8 = var4.startingPages[this.pagesDisplayed] * var9 + (this.pagesDisplayed == 0 ? 0 : POPUP_NOICON_TEXT_ZONE.getMarginTop());
         ++this.pagesDisplayed;
         var3[1] = var1 + POPUP_BORDER_HEIGHT + var8;
         if (this.pagesDisplayed != var4.startingPages.length && this.pagesDisplayed != 1) {
            int var7 = (var4.startingPages[this.pagesDisplayed] + 1) * var9 + POPUP_NOICON_TEXT_ZONE.getMarginTop() - var8;
            var3[1] = var3[1] + var7 - var2;
            var3[3] = var2;
            var3[1] = var3[1] < 0 ? 0 : var3[1];
         } else {
            var3[3] = var1 + 2 * POPUP_BORDER_HEIGHT + var4.height - var3[1] < var2 ? var1 + 2 * POPUP_BORDER_HEIGHT + var4.height - var3[1] : var2;
         }

         this.repaint();
         return true;
      } else {
         return false;
      }
   }

   private boolean cgScrollDown(int var1, int var2, int[] var3) {
      boolean var6 = false;
      int var7 = var1;
      int var8 = var1;
      boolean var9 = false;
      ChoiceHandler.ChoiceElement[] var4 = this.handler.choiceElements;
      ChoiceHandler.ChoiceElement var5 = var4[this.handler.highlightedElement];
      int var12 = var5.lines[0].getTextLineHeight();
      this.handler.setMainZone(this.owner.getMainZone());
      int var10;
      if (var5.isMultipage && this.pagesDisplayed < var5.startingPages.length && this.pagesDisplayed >= 0) {
         for(var10 = 0; var10 < this.handler.highlightedElement; ++var10) {
            var8 += var4[var10].height;
         }

         int var11 = var5.startingPages[this.pagesDisplayed] * var12 + (this.pagesDisplayed == 0 ? 0 : this.handler.elementTextZone.getMarginTop());
         ++this.pagesDisplayed;
         var3[1] = var8 + var11;
         if (this.pagesDisplayed != var5.startingPages.length && this.pagesDisplayed != 1) {
            var10 = (var5.startingPages[this.pagesDisplayed] + 1) * var12 + this.handler.elementTextZone.getMarginTop() - var11;
            var3[1] = var3[1] + var10 - var2;
            var3[3] = var2;
            var3[1] = var3[1] < 0 ? 0 : var3[1];
         } else {
            var3[3] = var1 + this.handler.listHeight - var3[1] < var2 ? var1 + this.handler.listHeight - var3[1] : var2;
         }

         this.repaint();
         return true;
      } else if (this.handler.highlightedElement >= var4.length - 1) {
         return false;
      } else {
         var5.highlighted = false;
         var5 = var4[++this.handler.highlightedElement];
         var5.highlighted = true;

         for(var10 = 0; var10 < this.handler.highlightedElement; ++var10) {
            var7 += var4[var10].height;
         }

         if (var5.isMultipage) {
            var3[1] = var7;
            var3[3] = var1 + this.handler.listHeight - var3[1] < var2 ? var1 + this.handler.listHeight - var3[1] : var2;
            this.pagesDisplayed = 1;
         } else {
            this.pagesDisplayed = 0;
            if (var7 + var5.height <= var2) {
               var3[3] = var1 + this.handler.listHeight < var2 ? var1 + this.handler.listHeight : var2;
            } else {
               if (var7 + var5.height > var3[1] + var2) {
                  var3[1] = var7 + var5.height - var2;
               }

               var3[3] = var2;
            }
         }

         this.repaint();
         return true;
      }
   }

   static {
      POPUP_NOICON_TEXT_ZONE = Displayable.uistyle.getZone(38);
      POPUP_ICON_TEXT_ZONE = Displayable.uistyle.getZone(39);
      POPUP_ICON_IMAGE_ZONE = Displayable.uistyle.getZone(40);
      POPUP_ARROW_ZONE = Displayable.uistyle.getZone(37);
      POPUP_BORDER_ZONE = Displayable.uistyle.getZone(36);
      ARROW_POPUP_ICON = Pixmap.createPixmap(11);
      POPUP_LIST_NOICON_TEXT_ZONE = Displayable.uistyle.getZone(17);
      POPUP_LIST_ICON_TEXT_ZONE = Displayable.uistyle.getZone(18);
      POPUP_LIST_ICON_IMAGE_ZONE = Displayable.uistyle.getZone(19);
      POPUP_BORDER_HEIGHT = POPUP_BORDER_ZONE.getMarginTop();
   }

   private class PopupListListener implements CommandListener {
      private PopupListListener() {
      }

      public void commandAction(Command var1, Displayable var2) {
         if (var2.equals(ChoiceGroup.this.popupList)) {
            int var3;
            if (var1 != null && var1.getCommandType() == 2) {
               if (ChoiceGroup.this.handler != null) {
                  var3 = ChoiceGroup.this.handler.highlightedElement;
                  ChoiceGroup.this.handler.choiceElements[var3].highlighted = false;
                  ChoiceGroup.this.handler.choiceElements[var3].selected = false;
                  ChoiceGroup.this.handler.highlightedElement = ChoiceGroup.this.oldHighlighted;
                  ChoiceGroup.this.handler.choiceElements[ChoiceGroup.this.oldHighlighted].highlighted = true;
                  ChoiceGroup.this.handler.choiceElements[ChoiceGroup.this.oldHighlighted].selected = true;
               }

               if (ChoiceGroup.this.owner != null) {
                  ChoiceGroup.this.owner.myDisplay.setCurrentInternal((Displayable)null, ChoiceGroup.this.owner);
               } else {
                  var2.myDisplay.setCurrent(var2.myDisplay.getCurrent());
               }
            }

            if (var1.equals(List.SELECT_COMMAND)) {
               var3 = ChoiceGroup.this.popupList.getSelectedIndex();
               ChoiceGroup.this.setSelectedIndex(var3, true);
               if (ChoiceGroup.this.owner != null) {
                  ChoiceGroup.this.owner.changedItemState(ChoiceGroup.this);
                  ChoiceGroup.this.owner.myDisplay.setCurrentInternal((Displayable)null, ChoiceGroup.this.owner);
               } else {
                  var2.myDisplay.setCurrent(var2.myDisplay.getCurrent());
               }
            }

            ChoiceGroup.this.popupList = null;
         }
      }

      // $FF: synthetic method
      PopupListListener(Object var2) {
         this();
      }
   }
}
