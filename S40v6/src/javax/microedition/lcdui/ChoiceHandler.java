package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.sound.SoundDatabase;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import com.nokia.mid.sound.Sound;

class ChoiceHandler extends ChoiceVector {
   Zone mainZone;
   Zone elementTextZone;
   Zone elementIconGraphicZone;
   Zone elementChoiceBoxZone;
   Zone auxTextZone;
   Zone auxIconZone;
   Zone auxBoxZone;
   Pixmap checkIconSelected;
   Pixmap checkIconNotSelected;
   boolean multiTrunc;
   int listHeight;
   int startElement;
   int sumH;
   boolean hasCyclicBehaviour;
   boolean hasPartialBehaviour;
   ChoiceGroup parent;
   private int truncatedElements;
   private Sound wrapSound;

   ChoiceHandler(boolean hasCyclic, boolean hasPartial, Zone aZone, int type) {
      super((String[])null, (Image[])null, type);
      this.mainZone = Displayable.screenNormMainZone;
      this.elementTextZone = Displayable.uistyle.getZone(16);
      this.auxTextZone = Displayable.uistyle.getZone(17);
      this.auxIconZone = Displayable.uistyle.getZone(18);
      this.wrapSound = null;
      this.hasCyclicBehaviour = hasCyclic;
      this.hasPartialBehaviour = hasPartial;
      this.mainZone = aZone;
   }

   boolean isInTruncationMode() {
      return this.truncatedElements > 0;
   }

   boolean set(int elementNum, String stringPart, Image imagePart) throws NullPointerException, IndexOutOfBoundsException {
      if (elementNum >= 0 && elementNum < this.nOfItems) {
         ChoiceHandler.ChoiceElement ce = (ChoiceHandler.ChoiceElement)this.listOfItems[elementNum];
         int oldH = ce.height;
         this.truncatedElements -= ce.isTruncated() ? 1 : 0;
         boolean layoutChange = super.set(elementNum, stringPart, imagePart);
         ce.changeElementLayout();
         this.truncatedElements += ce.isTruncated() ? 1 : 0;
         if (layoutChange) {
            this.swapZones();
            this.changeLayout();
         }

         this.listHeight += ce.height - oldH;
         return false;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   boolean setFont(int elementNum, Font font) throws IndexOutOfBoundsException {
      if (super.setFont(elementNum, font)) {
         ChoiceHandler.ChoiceElement ce = (ChoiceHandler.ChoiceElement)this.listOfItems[elementNum];
         this.listHeight -= ce.height;
         this.truncatedElements -= ce.isTruncated() ? 1 : 0;
         ce.changeElementLayout();
         this.truncatedElements += ce.isTruncated() ? 1 : 0;
         this.listHeight += ce.height;
         return true;
      } else {
         return false;
      }
   }

   void insert(int elementNum, String stringPart, Image imagePart) throws NullPointerException, IndexOutOfBoundsException {
      ChoiceHandler.ChoiceElement ce = new ChoiceHandler.ChoiceElement(stringPart, imagePart);
      this.insert(elementNum, ce);
      if (imagePart != null && this.nOfImages == 1) {
         this.swapZones();
         this.changeLayout();
      }

      if (this.nOfItems == 1) {
         this.listHeight = ce.height;
         this.highlightedIndex = 0;
         this.listOfItems[0].highlighted = true;
         this.startElement = 0;
      } else {
         this.listHeight += ce.height;
         this.highlightedIndex += elementNum <= this.highlightedIndex ? 1 : 0;
      }

      this.truncatedElements += ce.isTruncated() ? 1 : 0;
   }

   void delete(int elementNum) throws IndexOutOfBoundsException {
      ChoiceHandler.ChoiceElement ce = (ChoiceHandler.ChoiceElement)this.listOfItems[elementNum];
      super.delete(elementNum);
      if (this.nOfItems == 0) {
         this.listHeight = 0;
      } else {
         if (ce.image != null && this.nOfImages == 0) {
            this.swapZones();
            this.changeLayout();
         }

         this.truncatedElements -= ce.isTruncated() ? 1 : 0;
         this.listHeight -= ce.height;
         this.startElement -= this.startElement == this.nOfItems - 1 ? 1 : 0;
         if (elementNum == this.highlightedIndex) {
            this.highlightedIndex = this.highlightedIndex > this.nOfItems - 1 ? this.nOfItems - 1 : this.highlightedIndex;
            this.listOfItems[this.highlightedIndex].highlighted = true;
         } else if (elementNum < this.highlightedIndex) {
            --this.highlightedIndex;
         }

      }
   }

   void deleteAll() {
      super.deleteAll();
      this.listHeight = 0;
      this.truncatedElements = 0;
   }

   void paintElements(int posX, int posY, Graphics g, int dir) {
      int choiceZoneHeight = this.mainZone.height - (posY - this.mainZone.y);
      if (this.nOfItems != 0) {
         if (dir == 1) {
            this.scrollDown(choiceZoneHeight);
         }

         if (dir == -1) {
            this.scrollUp(choiceZoneHeight);
         }

         this.sumH = 0;

         int i;
         for(i = 0; i < this.nOfItems; ++i) {
            ((ChoiceHandler.ChoiceElement)this.listOfItems[i]).displayedRatio = -1;
         }

         for(i = this.startElement; i < this.nOfItems && this.sumH <= choiceZoneHeight; i = this.incrementIndex(i) != this.startElement && this.incrementIndex(i) != 0 ? this.incrementIndex(i) : this.nOfItems) {
            ((ChoiceHandler.ChoiceElement)this.listOfItems[i]).paintElement(posX, posY + this.sumH, choiceZoneHeight, g, false);
         }

      }
   }

   void determineVisibleElements(int choiceZoneHeight) {
      int heightUsed = 0;

      int i;
      for(i = 0; i < this.nOfItems; ++i) {
         ((ChoiceHandler.ChoiceElement)this.listOfItems[i]).displayedRatio = -1;
      }

      for(i = this.startElement; i < this.nOfItems && heightUsed <= choiceZoneHeight; i = this.incrementIndex(i) != this.startElement && this.incrementIndex(i) != 0 ? this.incrementIndex(i) : this.nOfItems) {
         heightUsed = ((ChoiceHandler.ChoiceElement)this.listOfItems[i]).determineElementVisibility(choiceZoneHeight, heightUsed);
      }

   }

   void setMainZone(Zone aMainZone) {
      if (this.listOfItems != null && this.nOfItems > 0 && (aMainZone.height != this.mainZone.height || aMainZone.width != this.mainZone.width)) {
         this.changeLayout();
         ((ChoiceHandler.ChoiceElement)this.listOfItems[this.startElement]).startLine = 0;
      }

      this.mainZone = aMainZone;
   }

   void setStarting(int choiceZoneHeight) {
      if (this.listOfItems != null) {
         ChoiceHandler.ChoiceElement ce;
         int h;
         if (this.listHeight <= choiceZoneHeight) {
            this.startElement = 0;
            ce = (ChoiceHandler.ChoiceElement)this.listOfItems[this.startElement];
            ce.startLine = 0;
            ce.startPage = 0;

            for(h = 0; h < this.nOfItems; ++h) {
               ((ChoiceHandler.ChoiceElement)this.listOfItems[h]).displayedRatio = 1;
            }

         } else {
            for(h = 0; h < this.nOfItems; ++h) {
               ((ChoiceHandler.ChoiceElement)this.listOfItems[h]).displayedRatio = -1;
            }

            ce = (ChoiceHandler.ChoiceElement)this.listOfItems[this.highlightedIndex];
            h = 0;
            int tmpStartLine = 0;
            int nextElement;
            if (this.incrementIndex(this.highlightedIndex) != 0) {
               nextElement = this.incrementIndex(this.highlightedIndex);
            } else {
               nextElement = this.highlightedIndex;
            }

            ((ChoiceHandler.ChoiceElement)this.listOfItems[this.startElement]).startLine = 0;

            for(int i = nextElement; h < choiceZoneHeight; i = this.decrementIndex(i)) {
               ce = (ChoiceHandler.ChoiceElement)this.listOfItems[i];

               for(int j = ce.lines.length - 1; j >= 0; --j) {
                  h += ce.lines[j].getTextLineHeight();
                  h += j == 0 ? this.elementTextZone.getMarginTop() : 0;
                  h += j == ce.lines.length - 1 ? this.elementTextZone.getMarginBottom() : 0;
                  if (i != nextElement && j == ce.lines.length - 1) {
                     h += ce.heightOfFillerSpace;
                  }

                  if (h <= choiceZoneHeight) {
                     this.startElement = i;
                     tmpStartLine = j;
                     ce.displayedRatio = 0;
                  }
               }

               if (h <= choiceZoneHeight && tmpStartLine == 0) {
                  ce.displayedRatio = 1;
               }

               if (i == 0) {
                  break;
               }
            }

            ((ChoiceHandler.ChoiceElement)this.listOfItems[this.startElement]).startLine = tmpStartLine;
         }
      }
   }

   void changeLayout() {
      this.listHeight = 0;
      if (this.listOfItems != null) {
         for(int i = 0; i < this.nOfItems; ++i) {
            ChoiceHandler.ChoiceElement ce = (ChoiceHandler.ChoiceElement)this.listOfItems[i];
            ce.changeElementLayout();
            this.listHeight += ce.height;
            this.truncatedElements += ce.isTruncated() ? 1 : 0;
         }

      }
   }

   void setWrapSoundOn() {
      if (UIStyle.isListEndToneOn()) {
         this.wrapSound = SoundDatabase.getSound(5);
      }

   }

   void setWrapSoundOff() {
      if (this.wrapSound != null) {
         this.wrapSound.stop();
         this.wrapSound = null;
      }

   }

   boolean needNewStarting() {
      int h = 0;
      int lineH = false;
      if (this.listOfItems == null) {
         return false;
      } else {
         int lineH = ((ChoiceHandler.ChoiceElement)this.listOfItems[this.startElement]).lines[0].getTextLineHeight();

         for(int i = this.startElement; i < this.nOfItems; i = this.incrementIndex(i)) {
            ChoiceHandler.ChoiceElement ce = (ChoiceHandler.ChoiceElement)this.listOfItems[i];
            h += ce.startLine == 0 ? this.elementTextZone.getMarginTop() : 0;
            h += (ce.lines.length - ce.startLine) * lineH + this.elementTextZone.getMarginBottom() + ce.heightOfFillerSpace;
            if (i == this.highlightedIndex) {
               break;
            }
         }

         return h > this.mainZone.height;
      }
   }

   private void swapZones() {
      Zone tmp = this.elementTextZone;
      this.elementTextZone = this.auxTextZone;
      this.auxTextZone = tmp;
      tmp = this.elementChoiceBoxZone;
      this.elementChoiceBoxZone = this.auxBoxZone;
      this.auxBoxZone = tmp;
      tmp = this.elementIconGraphicZone;
      this.elementIconGraphicZone = this.auxIconZone;
      this.auxIconZone = tmp;
   }

   void scrollUp(int areaHeight) {
      ChoiceHandler.ChoiceElement ce = (ChoiceHandler.ChoiceElement)this.listOfItems[this.highlightedIndex];
      ce.highlighted = false;
      if (this.hasCyclicBehaviour || this.highlightedIndex != 0) {
         this.highlightedIndex = this.decrementIndex(this.highlightedIndex);
         ce = (ChoiceHandler.ChoiceElement)this.listOfItems[this.highlightedIndex];
         ce.highlighted = true;
         int previousElement = this.decrementIndex(this.highlightedIndex);
         ChoiceHandler.ChoiceElement ce2 = (ChoiceHandler.ChoiceElement)this.listOfItems[previousElement];
         if (ce.displayedRatio != 1 || ce2.displayedRatio == -1) {
            if (this.highlightedIndex == this.nOfItems - 1) {
               this.setStarting(areaHeight);
            } else {
               if (previousElement != this.nOfItems - 1) {
                  this.startElement = previousElement;
               } else {
                  this.startElement = this.highlightedIndex;
               }

               ce.startLine = 0;
               this.determineVisibleElements(areaHeight);
            }
         }

         if (this.highlightedIndex == this.nOfItems - 1 && this.wrapSound != null) {
            this.wrapSound.stop();
            this.wrapSound.play(1);
         }

      }
   }

   void scrollDown(int areaHeight) {
      ChoiceHandler.ChoiceElement ce = (ChoiceHandler.ChoiceElement)this.listOfItems[this.highlightedIndex];
      ce.highlighted = false;
      if (this.hasCyclicBehaviour || this.highlightedIndex != this.nOfItems - 1) {
         this.highlightedIndex = this.incrementIndex(this.highlightedIndex);
         ce = (ChoiceHandler.ChoiceElement)this.listOfItems[this.highlightedIndex];
         ce.highlighted = true;
         int nextElement = this.incrementIndex(this.highlightedIndex);
         ChoiceHandler.ChoiceElement ce2 = (ChoiceHandler.ChoiceElement)this.listOfItems[nextElement];
         if (ce.displayedRatio != 1 || ce2.displayedRatio == -1) {
            ((ChoiceHandler.ChoiceElement)this.listOfItems[this.startElement]).startLine = 0;
            if (this.highlightedIndex == 0) {
               this.startElement = this.highlightedIndex;
               ce.startLine = 0;
               this.determineVisibleElements(areaHeight);
            } else {
               this.setStarting(areaHeight);
            }
         }

         if (this.highlightedIndex == 0 && this.wrapSound != null) {
            this.wrapSound.stop();
            this.wrapSound.play(1);
         }

      }
   }

   private int incrementIndex(int i) {
      return this.hasCyclicBehaviour ? (i + 1) % this.nOfItems : i + 1;
   }

   private int decrementIndex(int i) {
      return this.hasCyclicBehaviour ? (i - 1 + this.nOfItems) % this.nOfItems : i - 1;
   }

   String getTitle() {
      return this.parent.owner.getInternalTitle();
   }

   void handleCmd(Command cmd) {
      ChoiceGroup.PopupListListener listener;
      synchronized(Display.LCDUILock) {
         listener = this.parent.popupListListener;
      }

      if (listener != null) {
         listener.commandAction(cmd, this.parent.popupList);
      }

   }

   Command[] getOptionCommands() {
      return null;
   }

   class ChoiceElement extends ChoiceItem {
      Image mutableImagePart;
      byte displayedRatio = -1;
      int startLine;
      int startPage;
      TextLine[] lines;
      int height;
      int heightOfFillerSpace;
      int[] startingPages;
      short borderH;
      static final int CHOICE_MAX_SIZE = 3;

      ChoiceElement(String stringPart, Image imagePart) throws NullPointerException {
         super(stringPart, imagePart);
         this.changeElementLayout();
         this.displayedRatio = -1;
      }

      void set(String stringPart, Image imagePart) throws NullPointerException {
         if (stringPart == null) {
            throw new NullPointerException();
         } else {
            this.text = stringPart;
            if (imagePart != null && imagePart.isMutable()) {
               this.mutableImagePart = imagePart;
               this.image = Image.createImage(imagePart);
            } else {
               this.image = imagePart;
               this.mutableImagePart = null;
            }

         }
      }

      void paintElement(int posX, int posY, int choiceZoneHeight, Graphics g, boolean isPopup) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
         int offset = 0;
         int i = this.startLine;
         this.displayedRatio = -1;
         if (ChoiceHandler.this.mainZone != null && ChoiceHandler.this.elementTextZone != null) {
            int highlightXPos;
            if (this.highlighted && !isPopup) {
               highlightXPos = ChoiceHandler.this.elementTextZone.x + ChoiceHandler.this.mainZone.x;
               int highlightWidth = ChoiceHandler.this.elementTextZone.width;
               if (ChoiceHandler.this.elementChoiceBoxZone != null) {
                  if (highlightXPos > ChoiceHandler.this.elementChoiceBoxZone.x) {
                     highlightXPos = ChoiceHandler.this.elementChoiceBoxZone.x;
                  }

                  highlightWidth += ChoiceHandler.this.elementChoiceBoxZone.width;
               }

               if (ChoiceHandler.this.elementIconGraphicZone != null) {
                  if (highlightXPos > ChoiceHandler.this.elementIconGraphicZone.x) {
                     highlightXPos = ChoiceHandler.this.elementIconGraphicZone.x;
                  }

                  highlightWidth += ChoiceHandler.this.elementIconGraphicZone.width;
               }

               int highlightHeight = ChoiceHandler.this.elementTextZone.height;

               while(true) {
                  ++i;
                  if (i >= this.lines.length || highlightHeight >= choiceZoneHeight) {
                     UIStyle.getUIStyle().drawHighlightBar(ng, highlightXPos, posY + ChoiceHandler.this.elementTextZone.y, highlightWidth, highlightHeight, true);
                     i = this.startLine;
                     break;
                  }

                  highlightHeight += this.lines[i].getTextLineHeight();
               }
            }

            ChoiceHandler var10000;
            while(i < this.lines.length && ChoiceHandler.this.sumH < choiceZoneHeight) {
               highlightXPos = this.lines[i].getTextLineHeight();
               highlightXPos += i == this.startLine ? ChoiceHandler.this.elementTextZone.getMarginTop() : 0;
               highlightXPos += i == this.lines.length - 1 ? ChoiceHandler.this.elementTextZone.getMarginBottom() : 0;
               if (highlightXPos + ChoiceHandler.this.sumH <= choiceZoneHeight) {
                  this.paintLine(i, posX, posY + offset, ng, isPopup);
                  offset += highlightXPos;
                  this.displayedRatio = 0;
                  ++i;
               } else if (ChoiceHandler.this.hasPartialBehaviour) {
                  this.paintLine(i, posX, posY + offset, ng, isPopup);
                  this.displayedRatio = 0;
               }

               var10000 = ChoiceHandler.this;
               var10000.sumH += highlightXPos;
            }

            this.displayedRatio = this.startLine == 0 && this.lines.length == i ? 1 : this.displayedRatio;
            var10000 = ChoiceHandler.this;
            var10000.sumH += this.heightOfFillerSpace;
         }
      }

      private int determineElementVisibility(int choiceZoneHeight, int heightUsed) {
         int i = this.startLine;

         int iLineH;
         for(this.displayedRatio = -1; i < this.lines.length && heightUsed < choiceZoneHeight; heightUsed += iLineH) {
            iLineH = this.lines[i].getTextLineHeight();
            iLineH += i == this.startLine ? ChoiceHandler.this.elementTextZone.getMarginTop() : 0;
            iLineH += i == this.lines.length - 1 ? ChoiceHandler.this.elementTextZone.getMarginBottom() : 0;
            if (iLineH + heightUsed <= choiceZoneHeight) {
               this.displayedRatio = 0;
               ++i;
            } else if (ChoiceHandler.this.hasPartialBehaviour) {
               this.displayedRatio = 0;
            }
         }

         this.displayedRatio = this.startLine == 0 && this.lines.length == i ? 1 : this.displayedRatio;
         heightUsed += this.heightOfFillerSpace;
         return heightUsed;
      }

      void changeElementLayout() {
         if (ChoiceHandler.this.mainZone != null && ChoiceHandler.this.elementTextZone != null) {
            Zone currentTextZone;
            if (ChoiceHandler.this.type == 4) {
               currentTextZone = this.image != null ? ChoiceGroup.POPUP_ICON_TEXT_ZONE : ChoiceGroup.POPUP_NOICON_TEXT_ZONE;
            } else {
               currentTextZone = ChoiceHandler.this.elementTextZone;
            }

            this.heightOfFillerSpace = 0;
            int wtext = currentTextZone.width - (currentTextZone.getMarginLeft() + currentTextZone.getMarginRight());
            TextLine tline = null;
            TextBreaker breaker = TextBreaker.getBreaker();
            breaker.setFont(this.jFont != null ? this.jFont.getImpl() : currentTextZone.getFont());
            breaker.setLeading(0, false);
            breaker.setText(this.text.length() > 0 ? this.text : " ");
            this.displayedRatio = -1;
            this.lines = null;
            if (ChoiceHandler.this.fitPolicy != 1) {
               breaker.setTruncation(true);
               this.lines = new TextLine[]{breaker.getTextLine(wtext)};
               this.height = this.lines[0].getTextLineHeight() + currentTextZone.getMarginTop() + currentTextZone.getMarginBottom();
            } else {
               boolean firstline = true;
               breaker.setTruncation(false);

               while((tline = breaker.getTextLine(wtext)) != null) {
                  if (firstline) {
                     firstline = false;
                     this.lines = new TextLine[]{tline};
                     this.startingPages = new int[]{0};
                     this.height = tline.getTextLineHeight() + currentTextZone.getMarginTop();
                  } else {
                     if (this.lines.length < 3) {
                        if (this.lines.length == 2) {
                           ChoiceHandler.this.multiTrunc = true;
                        }

                        TextLine[] newLines = new TextLine[this.lines.length + 1];
                        System.arraycopy(this.lines, 0, newLines, 0, this.lines.length);
                        newLines[this.lines.length] = tline;
                        this.lines = newLines;
                        this.height += tline.getTextLineHeight();
                     }

                     if (this.lines.length == 2) {
                        breaker.setTruncation(true);
                     }
                  }
               }

               if (this.lines != null && this.lines.length > 0) {
                  this.height += currentTextZone.getMarginBottom();
               }
            }

            if (this.height < currentTextZone.height) {
               this.heightOfFillerSpace = currentTextZone.height - this.height;
               this.height = currentTextZone.height;
            }

            breaker.destroyBreaker();
            this.startLine = 0;
         }
      }

      boolean isTruncated() {
         return ChoiceHandler.this.fitPolicy != 1 && this.lines[0].isTruncated() || ChoiceHandler.this.multiTrunc && this.lines[this.lines.length - 1].isTruncated();
      }

      private void paintLine(int index, int posX, int posY, com.nokia.mid.impl.isa.ui.gdi.Graphics ng, boolean isPopup) {
         if (posX != 0) {
         }

         int space = 0;
         if (this.lines[index] != null && this.lines[index].getTextLineHeight() > 0) {
            ColorCtrl colorCtrl = ng.getColorCtrl();
            int oldFgColor = colorCtrl.getFgColor();
            if (this.highlighted) {
               if (isPopup) {
                  colorCtrl.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
               } else {
                  colorCtrl.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
               }
            } else if (isPopup) {
               colorCtrl.setFgColor(UIStyle.COLOUR_EDITOR_UNFOCUSED);
            } else {
               colorCtrl.setFgColor(UIStyle.COLOUR_TEXT);
            }

            space = isPopup ? (ChoiceHandler.this.elementTextZone.width - ChoiceHandler.this.elementTextZone.getMarginRight() - ChoiceHandler.this.elementTextZone.getMarginLeft() - this.lines[index].getTextLineWidth()) / 2 : 0;
            if (space < 0) {
               space = 0;
            }

            this.drawElementText(index, space, posY, ng, isPopup);
            colorCtrl.setFgColor(oldFgColor);
         }

         if (index == 0) {
            this.drawCheckMark(posY, ng);
            this.drawElementImage(space, posY, this.image, ng);
         }

      }

      private void drawElementText(int index, int xOffset, int yOffset, com.nokia.mid.impl.isa.ui.gdi.Graphics ng, boolean isPopup) {
         int xp = ChoiceHandler.this.elementTextZone.x + ChoiceHandler.this.mainZone.x;
         int yp = ChoiceHandler.this.elementTextZone.y + yOffset;
         yp += index == 0 ? ChoiceHandler.this.elementTextZone.getMarginTop() : 0;
         if (UIStyle.isAlignedLeftToRight) {
            xp += ChoiceHandler.this.elementTextZone.getMarginLeft() + xOffset;
            this.lines[index].setAlignment(1);
         } else {
            xp += ChoiceHandler.this.elementTextZone.width - ChoiceHandler.this.elementTextZone.getMarginRight() - xOffset;
            this.lines[index].setAlignment(3);
         }

         ng.drawText(this.lines[index], (short)xp, (short)yp, (short)(ChoiceHandler.this.elementTextZone.width - (ChoiceHandler.this.elementTextZone.getMarginLeft() + ChoiceHandler.this.elementTextZone.getMarginRight())));
      }

      private void drawCheckMark(int _position, com.nokia.mid.impl.isa.ui.gdi.Graphics ng) {
         Pixmap icon = this.selected ? ChoiceHandler.this.checkIconSelected : ChoiceHandler.this.checkIconNotSelected;
         if (ChoiceHandler.this.elementChoiceBoxZone != null && icon != null) {
            Displayable.uistyle.drawPixmapInZone(ng, ChoiceHandler.this.elementChoiceBoxZone, ChoiceHandler.this.mainZone.x, _position, icon);
         }
      }

      private void drawElementImage(int xOffset, int yOffset, Image img, com.nokia.mid.impl.isa.ui.gdi.Graphics ng) {
         if (ChoiceHandler.this.elementIconGraphicZone != null && img != null) {
            Displayable.uistyle.drawPixmapInZone(ng, ChoiceHandler.this.elementIconGraphicZone, ChoiceHandler.this.mainZone.x + (UIStyle.isAlignedLeftToRight ? xOffset : -xOffset), yOffset, img.getPixmap());
         }
      }
   }
}
