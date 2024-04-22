package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.sound.SoundDatabase;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import com.nokia.mid.sound.Sound;

class ChoiceHandler {
   Zone mainZone;
   Zone elementTextZone;
   Zone elementIconGraphicZone;
   Zone elementChoiceBoxZone;
   Zone auxTextZone;
   Zone auxIconZone;
   Zone auxBoxZone;
   Pixmap checkIconSelected;
   Pixmap checkIconNotSelected;
   boolean wrapOn;
   boolean multiTrunc;
   int highlightedElement;
   ChoiceHandler.ChoiceElement[] choiceElements;
   int listHeight;
   int startElement;
   int sumH;
   boolean hasCyclicBehaviour;
   boolean hasPartialBehaviour;
   Object owner;
   private int imageNumber;
   private int truncatedElements;
   private Sound wrapSound;

   ChoiceHandler(boolean hasCyclic, boolean hasPartial, Zone aZone) {
      this.mainZone = Displayable.screenNormMainZone;
      this.elementTextZone = Displayable.uistyle.getZone(17);
      this.auxTextZone = Displayable.uistyle.getZone(18);
      this.auxIconZone = Displayable.uistyle.getZone(19);
      this.wrapSound = null;
      this.hasCyclicBehaviour = hasCyclic;
      this.hasPartialBehaviour = hasPartial;
      this.mainZone = aZone;
   }

   boolean isInTruncationMode() {
      return this.truncatedElements > 0;
   }

   void set(int elementNum, String stringPart, Image imagePart) throws NullPointerException, IndexOutOfBoundsException {
      if (stringPart == null) {
         throw new NullPointerException();
      } else {
         this.checkIndex(elementNum);
         ChoiceHandler.ChoiceElement ce = this.choiceElements[elementNum];
         int oldH = ce.height;
         if (imagePart != null && ce.imagePart == null && ++this.imageNumber == 1 || imagePart == null && ce.imagePart != null && --this.imageNumber == 0) {
            this.swapZones();
            this.changeLayout();
         }

         this.truncatedElements -= ce.isTruncated() ? 1 : 0;
         ce.set(stringPart, imagePart);
         this.truncatedElements += ce.isTruncated() ? 1 : 0;
         this.listHeight += ce.height - oldH;
      }
   }

   boolean setFont(int elementNum, Font font) throws IndexOutOfBoundsException {
      if (this.choiceElements != null && elementNum >= 0 && elementNum < this.choiceElements.length) {
         ChoiceHandler.ChoiceElement ce = this.choiceElements[elementNum];
         boolean changed = false;
         this.listHeight -= ce.height;
         this.truncatedElements -= ce.isTruncated() ? 1 : 0;
         changed = ce.setFont(font);
         this.truncatedElements += ce.isTruncated() ? 1 : 0;
         this.listHeight += ce.height;
         return changed;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   int append(String stringPart, Image imagePart) throws NullPointerException {
      int index = this.choiceElements == null ? 0 : this.choiceElements.length;
      this.insert(index, stringPart, imagePart);
      return this.choiceElements.length - 1;
   }

   void insert(int elementNum, String stringPart, Image imagePart) throws NullPointerException, IndexOutOfBoundsException {
      if (this.choiceElements != null && (elementNum < 0 || elementNum > this.choiceElements.length) || this.choiceElements == null && elementNum != 0) {
         throw new IndexOutOfBoundsException();
      } else if (stringPart == null) {
         throw new NullPointerException();
      } else {
         if (imagePart != null && ++this.imageNumber == 1) {
            this.swapZones();
            this.changeLayout();
         }

         ChoiceHandler.ChoiceElement ce = new ChoiceHandler.ChoiceElement(stringPart, imagePart);
         if (this.choiceElements == null) {
            this.choiceElements = new ChoiceHandler.ChoiceElement[]{ce};
            this.listHeight = ce.height;
            this.highlightedElement = 0;
            this.choiceElements[0].highlighted = true;
            this.startElement = 0;
         } else {
            ChoiceHandler.ChoiceElement[] tmp = new ChoiceHandler.ChoiceElement[this.choiceElements.length + 1];
            System.arraycopy(this.choiceElements, 0, tmp, 0, elementNum);
            System.arraycopy(this.choiceElements, elementNum, tmp, elementNum + 1, this.choiceElements.length - elementNum);
            tmp[elementNum] = ce;
            this.listHeight += ce.height;
            this.choiceElements = tmp;
            this.highlightedElement += elementNum <= this.highlightedElement ? 1 : 0;
         }

         this.truncatedElements += ce.isTruncated() ? 1 : 0;
      }
   }

   void delete(int elementNum) throws IndexOutOfBoundsException {
      this.checkIndex(elementNum);
      boolean changeLayout = false;
      ChoiceHandler.ChoiceElement ce = this.choiceElements[elementNum];
      if (ce.imagePart != null && --this.imageNumber == 0) {
         this.swapZones();
         changeLayout = true;
      }

      if (this.choiceElements.length <= 1) {
         this.choiceElements = null;
         this.listHeight = 0;
      } else {
         if (changeLayout) {
            this.changeLayout();
         }

         this.truncatedElements -= ce.isTruncated() ? 1 : 0;
         this.listHeight -= ce.height;
         this.startElement -= this.startElement == this.choiceElements.length - 1 ? 1 : 0;
         ChoiceHandler.ChoiceElement[] tmp = new ChoiceHandler.ChoiceElement[this.choiceElements.length - 1];
         System.arraycopy(this.choiceElements, 0, tmp, 0, elementNum);
         System.arraycopy(this.choiceElements, elementNum + 1, tmp, elementNum, tmp.length - elementNum);
         this.choiceElements = tmp;
         if (elementNum == this.highlightedElement) {
            this.highlightedElement = this.highlightedElement > this.choiceElements.length - 1 ? this.choiceElements.length - 1 : this.highlightedElement;
            this.choiceElements[this.highlightedElement].highlighted = true;
         } else if (elementNum < this.highlightedElement) {
            --this.highlightedElement;
         }

      }
   }

   void deleteAll() {
      this.choiceElements = null;
      this.listHeight = 0;
      this.imageNumber = 0;
      this.truncatedElements = 0;
   }

   int getSelectedIndex() {
      if (this.choiceElements == null) {
         return -1;
      } else {
         for(int i = 0; i < this.choiceElements.length; ++i) {
            if (this.choiceElements[i].selected) {
               return i;
            }
         }

         return -1;
      }
   }

   int getSelectedFlags(boolean[] selectedArray_return) throws NullPointerException, IllegalArgumentException {
      int selectedNum = 0;
      if (selectedArray_return == null) {
         throw new NullPointerException();
      } else if (this.choiceElements == null) {
         return 0;
      } else if (selectedArray_return.length < this.choiceElements.length) {
         throw new IllegalArgumentException();
      } else {
         int i;
         for(i = 0; i < selectedArray_return.length; ++i) {
            selectedArray_return[i] = false;
         }

         for(i = 0; i < this.choiceElements.length; ++i) {
            selectedArray_return[i] = this.choiceElements[i].selected;
            selectedNum += selectedArray_return[i] ? 1 : 0;
         }

         return selectedNum;
      }
   }

   void setSelectedIndex(int elementNum, boolean selected) throws IndexOutOfBoundsException {
      this.checkIndex(elementNum);
      this.choiceElements[elementNum].selected = selected;
   }

   void setSelectedFlags(boolean[] selectedArray) throws NullPointerException, IllegalArgumentException {
      if (selectedArray == null) {
         throw new NullPointerException();
      } else if (this.choiceElements != null) {
         if (selectedArray.length < this.choiceElements.length) {
            throw new IllegalArgumentException();
         } else {
            for(int i = 0; i < this.choiceElements.length; ++i) {
               this.choiceElements[i].selected = selectedArray[i];
            }

         }
      }
   }

   void paintElements(int posX, int posY, Graphics g, int dir) {
      int choiceZoneHeight = this.mainZone.height - (posY - this.mainZone.y);
      if (this.choiceElements != null) {
         if (dir == 1) {
            this.scrollDown(choiceZoneHeight);
         }

         if (dir == -1) {
            this.scrollUp(choiceZoneHeight);
         }

         this.sumH = 0;

         int i;
         for(i = 0; i < this.choiceElements.length; ++i) {
            this.choiceElements[i].displayedRatio = -1;
         }

         for(i = this.startElement; i < this.choiceElements.length && this.sumH <= choiceZoneHeight; i = this.incrementIndex(i) != this.startElement && this.incrementIndex(i) != 0 ? this.incrementIndex(i) : this.choiceElements.length) {
            this.choiceElements[i].paintElement(posX, posY + this.sumH, choiceZoneHeight, g, false);
         }

      }
   }

   void determineVisibleElements(int choiceZoneHeight) {
      int heightUsed = 0;

      int i;
      for(i = 0; i < this.choiceElements.length; ++i) {
         this.choiceElements[i].displayedRatio = -1;
      }

      for(i = this.startElement; i < this.choiceElements.length && heightUsed <= choiceZoneHeight; i = this.incrementIndex(i) != this.startElement && this.incrementIndex(i) != 0 ? this.incrementIndex(i) : this.choiceElements.length) {
         heightUsed = this.choiceElements[i].determineElementVisibility(choiceZoneHeight, heightUsed);
      }

   }

   void setMainZone(Zone aMainZone) {
      if (this.choiceElements != null && this.choiceElements.length > 0 && (aMainZone.height != this.mainZone.height || aMainZone.width != this.mainZone.width)) {
         this.changeLayout();
         this.choiceElements[this.startElement].startLine = 0;
      }

      this.mainZone = aMainZone;
   }

   void setStarting(int choiceZoneHeight) {
      if (this.choiceElements != null) {
         ChoiceHandler.ChoiceElement ce;
         int h;
         if (this.listHeight <= choiceZoneHeight) {
            this.startElement = 0;
            ce = this.choiceElements[this.startElement];
            ce.startLine = 0;
            ce.startPage = 0;

            for(h = 0; h < this.choiceElements.length; ++h) {
               this.choiceElements[h].displayedRatio = 1;
            }

         } else {
            for(h = 0; h < this.choiceElements.length; ++h) {
               this.choiceElements[h].displayedRatio = -1;
            }

            ChoiceHandler.ChoiceElement var10000 = this.choiceElements[this.highlightedElement];
            h = 0;
            int tmpStartLine = 0;
            int nextElement;
            if (this.incrementIndex(this.highlightedElement) != 0) {
               nextElement = this.incrementIndex(this.highlightedElement);
            } else {
               nextElement = this.highlightedElement;
            }

            this.choiceElements[this.startElement].startLine = 0;

            for(int i = nextElement; h < choiceZoneHeight; i = this.decrementIndex(i)) {
               ce = this.choiceElements[i];

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
            }

            this.choiceElements[this.startElement].startLine = tmpStartLine;
         }
      }
   }

   void changeLayout() {
      this.listHeight = 0;
      if (this.choiceElements != null) {
         for(int i = 0; i < this.choiceElements.length; ++i) {
            ChoiceHandler.ChoiceElement ce = this.choiceElements[i];
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
      if (this.choiceElements == null) {
         return false;
      } else {
         int lineH = this.choiceElements[this.startElement].lines[0].getTextLineHeight();

         for(int i = this.startElement; i < this.choiceElements.length; i = this.incrementIndex(i)) {
            ChoiceHandler.ChoiceElement ce = this.choiceElements[i];
            h += ce.startLine == 0 ? this.elementTextZone.getMarginTop() : 0;
            h += (ce.lines.length - ce.startLine) * lineH + this.elementTextZone.getMarginBottom() + ce.heightOfFillerSpace;
            if (i == this.highlightedElement) {
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
      ChoiceHandler.ChoiceElement ce = this.choiceElements[this.highlightedElement];
      ce.highlighted = false;
      if (this.hasCyclicBehaviour || this.highlightedElement != 0) {
         this.highlightedElement = this.decrementIndex(this.highlightedElement);
         ce = this.choiceElements[this.highlightedElement];
         ce.highlighted = true;
         int previousElement = this.decrementIndex(this.highlightedElement);
         ChoiceHandler.ChoiceElement ce2 = this.choiceElements[previousElement];
         if (ce.displayedRatio != 1 || ce2.displayedRatio == -1) {
            if (this.highlightedElement == this.choiceElements.length - 1) {
               this.setStarting(areaHeight);
            } else {
               if (previousElement != this.choiceElements.length - 1) {
                  this.startElement = previousElement;
               } else {
                  this.startElement = this.highlightedElement;
               }

               ce.startLine = 0;
               this.determineVisibleElements(areaHeight);
            }
         }

         if (this.highlightedElement == this.choiceElements.length - 1 && this.wrapSound != null) {
            this.wrapSound.stop();
            this.wrapSound.play(1);
         }

      }
   }

   void scrollDown(int areaHeight) {
      ChoiceHandler.ChoiceElement ce = this.choiceElements[this.highlightedElement];
      ce.highlighted = false;
      if (this.hasCyclicBehaviour || this.highlightedElement != this.choiceElements.length - 1) {
         this.highlightedElement = this.incrementIndex(this.highlightedElement);
         ce = this.choiceElements[this.highlightedElement];
         ce.highlighted = true;
         int nextElement = this.incrementIndex(this.highlightedElement);
         ChoiceHandler.ChoiceElement ce2 = this.choiceElements[nextElement];
         if (ce.displayedRatio != 1 || ce2.displayedRatio == -1) {
            this.choiceElements[this.startElement].startLine = 0;
            if (this.highlightedElement == 0) {
               this.startElement = this.highlightedElement;
               ce.startLine = 0;
               this.determineVisibleElements(areaHeight);
            } else {
               this.setStarting(areaHeight);
            }
         }

         if (this.highlightedElement == 0 && this.wrapSound != null) {
            this.wrapSound.stop();
            this.wrapSound.play(1);
         }

      }
   }

   private void checkIndex(int index) throws IndexOutOfBoundsException {
      if (this.choiceElements == null || index < 0 || index >= this.choiceElements.length) {
         throw new IndexOutOfBoundsException();
      }
   }

   private int incrementIndex(int i) {
      return this.hasCyclicBehaviour ? (i + 1) % this.choiceElements.length : i + 1;
   }

   private int decrementIndex(int i) {
      return this.hasCyclicBehaviour ? (i - 1 + this.choiceElements.length) % this.choiceElements.length : i - 1;
   }

   class ChoiceElement {
      String stringPart;
      Image imagePart;
      Image mutableImagePart;
      com.nokia.mid.impl.isa.ui.gdi.Font font;
      Font jFont;
      boolean selected;
      boolean highlighted;
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
         this.displayedRatio = -1;
         this.font = ChoiceHandler.this.elementTextZone.getFont();
         this.set(stringPart, imagePart);
      }

      void set(String stringPart, Image imagePart) throws NullPointerException {
         if (stringPart == null) {
            throw new NullPointerException();
         } else {
            this.stringPart = stringPart;
            if (imagePart != null && imagePart.isMutable()) {
               this.mutableImagePart = imagePart;
               this.imagePart = Image.createImage(imagePart);
            } else {
               this.imagePart = imagePart;
               this.mutableImagePart = null;
            }

            this.changeElementLayout();
         }
      }

      boolean setFont(Font aFont) {
         boolean changed = false;
         this.jFont = aFont;
         com.nokia.mid.impl.isa.ui.gdi.Font defFont = ChoiceHandler.this.elementTextZone.getFont();
         if (aFont == null) {
            if (this.font.getMIDPSize() != defFont.getMIDPSize() || this.font.getMIDPStyle() != defFont.getMIDPStyle()) {
               this.font = defFont;
               changed = true;
            }
         } else {
            int jFontStyle = this.jFont.getStyle();
            jFontStyle &= -5;
            if (jFontStyle != this.font.getMIDPStyle() || this.jFont.getSize() != this.font.getMIDPSize()) {
               this.font = new com.nokia.mid.impl.isa.ui.gdi.Font(defFont.getMIDPSize(), jFontStyle, true);
               changed = true;
            }
         }

         if (changed) {
            this.changeElementLayout();
         }

         return changed;
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
                     int overlapAdjustment = ChoiceHandler.this.elementTextZone.getMarginTop() + ChoiceHandler.this.elementTextZone.getMarginBottom();
                     highlightHeight -= overlapAdjustment * (i + 1);
                     UIStyle.getUIStyle().drawHighlightBar(ng, highlightXPos, posY + ChoiceHandler.this.elementTextZone.y + overlapAdjustment, highlightWidth, highlightHeight, true);
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
            int wtext = ChoiceHandler.this.elementTextZone.width - (ChoiceHandler.this.elementTextZone.getMarginLeft() + ChoiceHandler.this.elementTextZone.getMarginRight());
            TextLine tline = null;
            TextBreaker breaker = TextBreaker.getBreaker();
            breaker.setFont(this.font != null ? this.font : ChoiceHandler.this.elementTextZone.getFont());
            breaker.setLeading(0, false);
            breaker.setText(this.stringPart.length() > 0 ? this.stringPart : " ");
            this.displayedRatio = -1;
            this.lines = null;
            if (!ChoiceHandler.this.wrapOn) {
               breaker.setTruncation(true);
               this.lines = new TextLine[]{breaker.getTextLine(wtext)};
               this.height = this.lines[0].getTextLineHeight() + ChoiceHandler.this.elementTextZone.getMarginTop() + ChoiceHandler.this.elementTextZone.getMarginBottom();
            } else {
               boolean firstline = true;
               breaker.setTruncation(false);

               while((tline = breaker.getTextLine(wtext)) != null) {
                  if (firstline) {
                     firstline = false;
                     this.lines = new TextLine[]{tline};
                     this.startingPages = new int[]{0};
                     this.height = tline.getTextLineHeight() + ChoiceHandler.this.elementTextZone.getMarginTop();
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
                  this.height += ChoiceHandler.this.elementTextZone.getMarginBottom();
               }
            }

            if (this.height < ChoiceHandler.this.elementTextZone.height) {
               this.heightOfFillerSpace = ChoiceHandler.this.elementTextZone.height - this.height;
               this.height = ChoiceHandler.this.elementTextZone.height;
            }

            breaker.destroyBreaker();
            this.startLine = 0;
            if (ChoiceHandler.this.owner instanceof List) {
               this.height += ChoiceHandler.this.elementTextZone.getMarginBottom() + ChoiceHandler.this.elementTextZone.getMarginTop();
            }

         }
      }

      boolean isTruncated() {
         return !ChoiceHandler.this.wrapOn && this.lines[0].isTruncated() || ChoiceHandler.this.multiTrunc && this.lines[this.lines.length - 1].isTruncated();
      }

      private void paintLine(int index, int posX, int posY, com.nokia.mid.impl.isa.ui.gdi.Graphics ng, boolean isPopup) {
         if (posX != 0) {
         }

         if (this.lines[index] != null && this.lines[index].getTextLineHeight() > 0) {
            ColorCtrl colorCtrl = ng.getColorCtrl();
            int oldFgColor = colorCtrl.getFgColor();
            if (this.highlighted) {
               if (isPopup) {
                  colorCtrl.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
               } else {
                  colorCtrl.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
               }
            } else {
               colorCtrl.setFgColor(UIStyle.COLOUR_TEXT);
            }

            this.drawElementText(index, posY, ng);
            colorCtrl.setFgColor(oldFgColor);
         }

         if (index == 0) {
            this.drawCheckMark(posY, ng);
            this.drawElementImage(posY, this.imagePart, ng);
         }

      }

      private void drawElementText(int index, int yPosition, com.nokia.mid.impl.isa.ui.gdi.Graphics ng) {
         int xp = ChoiceHandler.this.elementTextZone.x + ChoiceHandler.this.mainZone.x;
         int yp = ChoiceHandler.this.elementTextZone.y + yPosition;
         yp += index == 0 ? ChoiceHandler.this.elementTextZone.getMarginTop() : 0;
         if (UIStyle.isAlignedLeftToRight) {
            xp += ChoiceHandler.this.elementTextZone.getMarginLeft();
            this.lines[index].setAlignment(1);
         } else {
            xp += ChoiceHandler.this.elementTextZone.width - ChoiceHandler.this.elementTextZone.getMarginRight();
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

      private void drawElementImage(int _position, Image img, com.nokia.mid.impl.isa.ui.gdi.Graphics ng) {
         if (ChoiceHandler.this.elementIconGraphicZone != null && img != null) {
            Displayable.uistyle.drawPixmapInZone(ng, ChoiceHandler.this.elementIconGraphicZone, ChoiceHandler.this.mainZone.x, _position, img.getPixmap());
         }
      }
   }
}
