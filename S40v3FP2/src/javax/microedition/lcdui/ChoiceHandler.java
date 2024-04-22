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
   int highlightedElement;
   ChoiceHandler.ChoiceElement[] choiceElements;
   int listHeight;
   int startElement;
   int sumH;
   boolean hasCyclicBehaviour;
   boolean hasPartialBehaviuor;
   private int imageNumber;
   private int truncatedElements;
   private Sound wrapSound;

   ChoiceHandler(boolean var1, boolean var2, Zone var3) {
      this.mainZone = Displayable.screenNormMainZone;
      this.elementTextZone = Displayable.uistyle.getZone(17);
      this.auxTextZone = Displayable.uistyle.getZone(18);
      this.auxIconZone = Displayable.uistyle.getZone(19);
      this.wrapSound = null;
      this.hasCyclicBehaviour = var1;
      this.hasPartialBehaviuor = var2;
      this.mainZone = var3;
   }

   boolean isInTruncationMode() {
      return this.truncatedElements > 0;
   }

   void set(int var1, String var2, Image var3) throws NullPointerException, IndexOutOfBoundsException {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         this.checkIndex(var1);
         ChoiceHandler.ChoiceElement var4 = this.choiceElements[var1];
         int var5 = var4.height;
         if (var3 != null && var4.imagePart == null && ++this.imageNumber == 1 || var3 == null && var4.imagePart != null && --this.imageNumber == 0) {
            this.swapZones();
            this.changeLayout();
         }

         this.truncatedElements -= var4.isTruncated() ? 1 : 0;
         var4.set(var2, var3);
         this.truncatedElements += var4.isTruncated() ? 1 : 0;
         this.listHeight += var4.height - var5;
      }
   }

   boolean setFont(int var1, Font var2) throws IndexOutOfBoundsException {
      if (this.choiceElements != null && var1 >= 0 && var1 < this.choiceElements.length) {
         ChoiceHandler.ChoiceElement var3 = this.choiceElements[var1];
         boolean var4 = false;
         this.listHeight -= var3.height;
         this.truncatedElements -= var3.isTruncated() ? 1 : 0;
         var4 = var3.setFont(var2);
         this.truncatedElements += var3.isTruncated() ? 1 : 0;
         this.listHeight += var3.height;
         return var4;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   int append(String var1, Image var2) throws NullPointerException {
      int var3 = this.choiceElements == null ? 0 : this.choiceElements.length;
      this.insert(var3, var1, var2);
      return this.choiceElements.length - 1;
   }

   void insert(int var1, String var2, Image var3) throws NullPointerException, IndexOutOfBoundsException {
      if (this.choiceElements != null && (var1 < 0 || var1 > this.choiceElements.length) || this.choiceElements == null && var1 != 0) {
         throw new IndexOutOfBoundsException();
      } else if (var2 == null) {
         throw new NullPointerException();
      } else {
         if (var3 != null && ++this.imageNumber == 1) {
            this.swapZones();
            this.changeLayout();
         }

         ChoiceHandler.ChoiceElement var4 = new ChoiceHandler.ChoiceElement(var2, var3);
         if (this.choiceElements == null) {
            this.choiceElements = new ChoiceHandler.ChoiceElement[]{var4};
            this.listHeight = var4.height;
            this.highlightedElement = 0;
            this.choiceElements[0].highlighted = true;
            this.startElement = 0;
         } else {
            ChoiceHandler.ChoiceElement[] var5 = new ChoiceHandler.ChoiceElement[this.choiceElements.length + 1];
            System.arraycopy(this.choiceElements, 0, var5, 0, var1);
            System.arraycopy(this.choiceElements, var1, var5, var1 + 1, this.choiceElements.length - var1);
            var5[var1] = var4;
            this.listHeight += var4.height;
            this.choiceElements = var5;
            this.highlightedElement += var1 <= this.highlightedElement ? 1 : 0;
         }

         this.truncatedElements += var4.isTruncated() ? 1 : 0;
      }
   }

   void delete(int var1) throws IndexOutOfBoundsException {
      this.checkIndex(var1);
      boolean var2 = false;
      ChoiceHandler.ChoiceElement var3 = this.choiceElements[var1];
      if (var3.imagePart != null && --this.imageNumber == 0) {
         this.swapZones();
         var2 = true;
      }

      if (this.choiceElements.length <= 1) {
         this.choiceElements = null;
         this.listHeight = 0;
      } else {
         if (var2) {
            this.changeLayout();
         }

         this.truncatedElements -= var3.isTruncated() ? 1 : 0;
         this.listHeight -= var3.height;
         this.startElement -= this.startElement == this.choiceElements.length - 1 ? 1 : 0;
         ChoiceHandler.ChoiceElement[] var4 = new ChoiceHandler.ChoiceElement[this.choiceElements.length - 1];
         System.arraycopy(this.choiceElements, 0, var4, 0, var1);
         System.arraycopy(this.choiceElements, var1 + 1, var4, var1, var4.length - var1);
         this.choiceElements = var4;
         if (var1 == this.highlightedElement) {
            this.highlightedElement = this.highlightedElement > this.choiceElements.length - 1 ? this.choiceElements.length - 1 : this.highlightedElement;
            this.choiceElements[this.highlightedElement].highlighted = true;
         } else if (var1 < this.highlightedElement) {
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
         for(int var1 = 0; var1 < this.choiceElements.length; ++var1) {
            if (this.choiceElements[var1].selected) {
               return var1;
            }
         }

         return -1;
      }
   }

   int getSelectedFlags(boolean[] var1) throws NullPointerException, IllegalArgumentException {
      int var2 = 0;
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.choiceElements == null) {
         return 0;
      } else if (var1.length < this.choiceElements.length) {
         throw new IllegalArgumentException();
      } else {
         int var3;
         for(var3 = 0; var3 < var1.length; ++var3) {
            var1[var3] = false;
         }

         for(var3 = 0; var3 < this.choiceElements.length; ++var3) {
            var1[var3] = this.choiceElements[var3].selected;
            var2 += var1[var3] ? 1 : 0;
         }

         return var2;
      }
   }

   void setSelectedIndex(int var1, boolean var2) throws IndexOutOfBoundsException {
      this.checkIndex(var1);
      this.choiceElements[var1].selected = var2;
   }

   void setSelectedFlags(boolean[] var1) throws NullPointerException, IllegalArgumentException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.choiceElements != null) {
         if (var1.length < this.choiceElements.length) {
            throw new IllegalArgumentException();
         } else {
            for(int var2 = 0; var2 < this.choiceElements.length; ++var2) {
               this.choiceElements[var2].selected = var1[var2];
            }

         }
      }
   }

   void paintElements(int var1, int var2, Graphics var3, int var4) {
      int var5 = this.mainZone.height - (var2 - this.mainZone.y);
      if (this.choiceElements != null) {
         if (var4 == 1) {
            this.scrollDown(var5);
         }

         if (var4 == -1) {
            this.scrollUp(var5);
         }

         this.sumH = 0;

         int var6;
         for(var6 = 0; var6 < this.choiceElements.length; ++var6) {
            this.choiceElements[var6].displayedRatio = -1;
         }

         for(var6 = this.startElement; var6 < this.choiceElements.length && this.sumH <= var5; var6 = this.incrementIndex(var6) != this.startElement && this.incrementIndex(var6) != 0 ? this.incrementIndex(var6) : this.choiceElements.length) {
            this.choiceElements[var6].paintElement(var1, var2 + this.sumH, var5, var3, false);
         }

      }
   }

   void setMainZone(Zone var1) {
      if (this.choiceElements != null && this.choiceElements.length > 0 && (var1.height != this.mainZone.height || var1.width != this.mainZone.width)) {
         if (var1.width == this.mainZone.width) {
            this.updateStartingPages(this.mainZone.height, var1.height);
         } else {
            this.changeLayout();
         }

         this.choiceElements[this.startElement].startLine = 0;
      }

      this.mainZone = var1;
   }

   void setStarting(int var1) {
      if (this.choiceElements != null) {
         ChoiceHandler.ChoiceElement var2;
         if (this.listHeight <= var1) {
            this.startElement = 0;
            var2 = this.choiceElements[this.startElement];
            var2.startLine = 0;
            var2.startPage = 0;
         } else {
            var2 = this.choiceElements[this.highlightedElement];
            if (var2.isMultipage) {
               this.startElement = this.highlightedElement;
               var2.startLine = 0;
               var2.startPage = 0;
            } else {
               int var3 = 0;
               int var4 = 0;
               this.choiceElements[this.startElement].startLine = 0;

               for(int var5 = this.highlightedElement; var3 < var1; var5 = this.decrementIndex(var5)) {
                  var2 = this.choiceElements[var5];

                  for(int var6 = var2.lines.length - 1; var6 >= 0; --var6) {
                     var3 += var2.lines[var6].getTextLineHeight();
                     var3 += var6 == 0 ? this.elementTextZone.getMarginTop() : 0;
                     var3 += var6 == var2.lines.length - 1 ? this.elementTextZone.getMarginBottom() : 0;
                     if (var5 != this.highlightedElement && var6 == var2.lines.length - 1) {
                        var3 += var2.heightOfFillerSpace;
                     }

                     if (var3 <= var1) {
                        this.startElement = var5;
                        var4 = var6;
                     }
                  }
               }

               this.choiceElements[this.startElement].startLine = var4;
            }
         }
      }
   }

   void changeLayout() {
      this.listHeight = 0;
      if (this.choiceElements != null) {
         for(int var2 = 0; var2 < this.choiceElements.length; ++var2) {
            ChoiceHandler.ChoiceElement var1 = this.choiceElements[var2];
            var1.changeElementLayout();
            this.listHeight += var1.height;
            this.truncatedElements += var1.isTruncated() ? 1 : 0;
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
      int var1 = 0;
      boolean var2 = false;
      if (this.choiceElements == null) {
         return false;
      } else {
         int var5 = this.choiceElements[this.startElement].lines[0].getTextLineHeight();

         for(int var4 = this.startElement; var4 < this.choiceElements.length; var4 = this.incrementIndex(var4)) {
            ChoiceHandler.ChoiceElement var3 = this.choiceElements[var4];
            var1 += this.elementTextZone.height;
            var1 += (var3.lines.length - var3.startLine - 1) * var5;
            if (var4 == this.highlightedElement) {
               break;
            }
         }

         return var1 > this.mainZone.height;
      }
   }

   private void updateStartingPages(int var1, int var2) {
      if (var1 != var2) {
         for(int var3 = 0; var3 < this.choiceElements.length; ++var3) {
            this.choiceElements[var3].setStartingPages(var2);
         }

      }
   }

   private void swapZones() {
      Zone var1 = this.elementTextZone;
      this.elementTextZone = this.auxTextZone;
      this.auxTextZone = var1;
      var1 = this.elementChoiceBoxZone;
      this.elementChoiceBoxZone = this.auxBoxZone;
      this.auxBoxZone = var1;
      var1 = this.elementIconGraphicZone;
      this.elementIconGraphicZone = this.auxIconZone;
      this.auxIconZone = var1;
   }

   void scrollUp(int var1) {
      ChoiceHandler.ChoiceElement var2 = this.choiceElements[this.highlightedElement];
      if (var2.isMultipage && var2.startLine != 0) {
         var2.scrollUp();
      } else {
         var2.highlighted = false;
         if (this.hasCyclicBehaviour || this.highlightedElement != 0) {
            this.highlightedElement = this.decrementIndex(this.highlightedElement);
            var2 = this.choiceElements[this.highlightedElement];
            var2.highlighted = true;
            if (var2.displayedRatio != 1) {
               if (var2.isMultipage) {
                  this.startElement = this.highlightedElement;
                  int var3 = var2.startingPages.length - 1;
                  var2.startPage = var3;
                  var2.startLine = var2.startingPages[var3];
               } else if (this.highlightedElement == this.choiceElements.length - 1) {
                  this.setStarting(var1);
               } else {
                  this.startElement = this.highlightedElement;
                  var2.startLine = 0;
               }
            }

            if (this.highlightedElement == this.choiceElements.length - 1 && this.wrapSound != null) {
               this.wrapSound.stop();
               this.wrapSound.play(1);
            }

         }
      }
   }

   void scrollDown(int var1) {
      ChoiceHandler.ChoiceElement var2 = this.choiceElements[this.highlightedElement];
      int var3 = var2.startingPages.length - 1;
      if (var2.isMultipage && var2.startPage != var3) {
         var2.scrollDown();
      } else {
         var2.highlighted = false;
         if (this.hasCyclicBehaviour || this.highlightedElement != this.choiceElements.length - 1) {
            this.highlightedElement = this.incrementIndex(this.highlightedElement);
            var2 = this.choiceElements[this.highlightedElement];
            var2.highlighted = true;
            if (var2.displayedRatio != 1) {
               this.choiceElements[this.startElement].startLine = 0;
               if (this.highlightedElement == 0) {
                  this.startElement = this.highlightedElement;
                  if (var2.isMultipage) {
                     int var4 = var2.startingPages.length - 1;
                     var2.startPage = var4;
                     var2.startLine = var2.startingPages[var4];
                  } else {
                     var2.startLine = 0;
                  }
               } else {
                  this.setStarting(var1);
               }
            }

            if (this.highlightedElement == 0 && this.wrapSound != null) {
               this.wrapSound.stop();
               this.wrapSound.play(1);
            }

         }
      }
   }

   private void checkIndex(int var1) throws IndexOutOfBoundsException {
      if (this.choiceElements == null || var1 < 0 || var1 >= this.choiceElements.length) {
         throw new IndexOutOfBoundsException();
      }
   }

   private int incrementIndex(int var1) {
      return this.hasCyclicBehaviour ? (var1 + 1) % this.choiceElements.length : var1 + 1;
   }

   private int decrementIndex(int var1) {
      return this.hasCyclicBehaviour ? (var1 - 1 + this.choiceElements.length) % this.choiceElements.length : var1 - 1;
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
      boolean isMultipage;
      TextLine[] lines;
      int height;
      int heightOfFillerSpace;
      int[] startingPages;
      short borderH;

      ChoiceElement(String var2, Image var3) throws NullPointerException {
         this.displayedRatio = -1;
         this.font = ChoiceHandler.this.elementTextZone.getFont();
         this.set(var2, var3);
      }

      void set(String var1, Image var2) throws NullPointerException {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.stringPart = var1;
            if (var2 != null && var2.isMutable()) {
               this.mutableImagePart = var2;
               this.imagePart = Image.createImage(var2);
            } else {
               this.imagePart = var2;
               this.mutableImagePart = null;
            }

            this.changeElementLayout();
         }
      }

      boolean setFont(Font var1) {
         boolean var2 = false;
         this.jFont = var1;
         com.nokia.mid.impl.isa.ui.gdi.Font var3 = ChoiceHandler.this.elementTextZone.getFont();
         if (var1 == null) {
            if (this.font.getMIDPSize() != var3.getMIDPSize() || this.font.getMIDPStyle() != var3.getMIDPStyle()) {
               this.font = var3;
               var2 = true;
            }
         } else {
            int var4 = this.jFont.getStyle();
            var4 &= -5;
            if (var4 != this.font.getMIDPStyle() || this.jFont.getSize() != this.font.getMIDPSize()) {
               this.font = new com.nokia.mid.impl.isa.ui.gdi.Font(var3.getMIDPSize(), var4, true);
               var2 = true;
            }
         }

         if (var2) {
            this.changeElementLayout();
         }

         return var2;
      }

      void paintElement(int var1, int var2, int var3, Graphics var4, boolean var5) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics var6 = var4.getImpl();
         int var7 = 0;
         int var8 = this.startLine;
         this.displayedRatio = -1;
         if (ChoiceHandler.this.mainZone != null && ChoiceHandler.this.elementTextZone != null) {
            int var9;
            if (this.highlighted && !var5) {
               var9 = ChoiceHandler.this.elementTextZone.x + ChoiceHandler.this.mainZone.x;
               int var10 = ChoiceHandler.this.elementTextZone.width;
               if (ChoiceHandler.this.elementChoiceBoxZone != null) {
                  if (var9 > ChoiceHandler.this.elementChoiceBoxZone.x) {
                     var9 = ChoiceHandler.this.elementChoiceBoxZone.x;
                  }

                  var10 += ChoiceHandler.this.elementChoiceBoxZone.width;
               }

               if (ChoiceHandler.this.elementIconGraphicZone != null) {
                  if (var9 > ChoiceHandler.this.elementIconGraphicZone.x) {
                     var9 = ChoiceHandler.this.elementIconGraphicZone.x;
                  }

                  var10 += ChoiceHandler.this.elementIconGraphicZone.width;
               }

               int var11 = ChoiceHandler.this.elementTextZone.height;

               while(true) {
                  ++var8;
                  if (var8 >= this.lines.length || var11 >= var3) {
                     UIStyle.getUIStyle().drawHighlightBar(var6, var9, var2 + ChoiceHandler.this.elementTextZone.y, var10, var11, true);
                     var8 = this.startLine;
                     break;
                  }

                  var11 += this.lines[var8].getTextLineHeight();
               }
            }

            ChoiceHandler var10000;
            while(var8 < this.lines.length && ChoiceHandler.this.sumH < var3) {
               var9 = this.lines[var8].getTextLineHeight();
               var9 += var8 == this.startLine ? ChoiceHandler.this.elementTextZone.getMarginTop() : 0;
               var9 += var8 == this.lines.length - 1 ? ChoiceHandler.this.elementTextZone.getMarginBottom() : 0;
               if (var9 + ChoiceHandler.this.sumH <= var3) {
                  this.paintLine(var8, var1, var2 + var7, var6, var5);
                  var7 += var9;
                  this.displayedRatio = 0;
                  ++var8;
               } else if (ChoiceHandler.this.hasPartialBehaviuor) {
                  this.paintLine(var8, var1, var2 + var7, var6, var5);
                  this.displayedRatio = 0;
               }

               var10000 = ChoiceHandler.this;
               var10000.sumH += var9;
            }

            this.displayedRatio = this.startLine == 0 && this.lines.length == var8 ? 1 : this.displayedRatio;
            var10000 = ChoiceHandler.this;
            var10000.sumH += this.heightOfFillerSpace;
         }
      }

      void changeElementLayout() {
         if (ChoiceHandler.this.mainZone != null && ChoiceHandler.this.elementTextZone != null) {
            int var1 = ChoiceHandler.this.elementTextZone.width - (ChoiceHandler.this.elementTextZone.getMarginLeft() + ChoiceHandler.this.elementTextZone.getMarginRight());
            TextLine var2 = null;
            TextBreaker var3 = TextBreaker.getBreaker();
            var3.setFont(this.font != null ? this.font : ChoiceHandler.this.elementTextZone.getFont());
            var3.setLeading(0, false);
            var3.setText(this.stringPart.length() > 0 ? this.stringPart : " ");
            this.displayedRatio = -1;
            this.isMultipage = false;
            this.lines = null;
            if (!ChoiceHandler.this.wrapOn) {
               var3.setTruncation(true);
               this.lines = new TextLine[]{var3.getTextLine(var1)};
               this.height = this.lines[0].getTextLineHeight() + ChoiceHandler.this.elementTextZone.getMarginTop() + ChoiceHandler.this.elementTextZone.getMarginBottom();
            } else {
               boolean var4 = true;
               var3.setTruncation(false);

               while((var2 = var3.getTextLine(var1)) != null) {
                  if (var4) {
                     var4 = false;
                     this.lines = new TextLine[]{var2};
                     this.startingPages = new int[]{0};
                     this.height = var2.getTextLineHeight() + ChoiceHandler.this.elementTextZone.getMarginTop();
                  } else {
                     TextLine[] var5 = new TextLine[this.lines.length + 1];
                     System.arraycopy(this.lines, 0, var5, 0, this.lines.length);
                     var5[this.lines.length] = var2;
                     this.lines = var5;
                     this.height += var2.getTextLineHeight();
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

            this.setStartingPages(ChoiceHandler.this.mainZone.height);
            var3.destroyBreaker();
            this.startLine = 0;
         }
      }

      boolean isTruncated() {
         return !ChoiceHandler.this.wrapOn && this.lines[0].isTruncated();
      }

      void setStartingPages(int var1) {
         boolean var2 = true;
         int var3 = 0;
         int var4 = this.lines[0].getTextLineHeight();
         int var5 = ChoiceHandler.this.elementTextZone.getMarginTop();
         int var6 = ChoiceHandler.this.elementTextZone.getMarginBottom();
         int var7 = var1 / var4;
         int var8 = (var1 - var6 - this.borderH) / var4;
         this.startingPages = new int[]{0};

         while(true) {
            while(var3 < this.lines.length - 1) {
               int[] var9;
               if (var2) {
                  var2 = false;
                  if (var5 + var6 + this.borderH + this.lines.length * var4 < var1) {
                     this.isMultipage = false;
                     return;
                  }

                  this.isMultipage = true;
                  var3 = (var1 - var5 - this.borderH) / var4 - 1;
                  var9 = new int[this.startingPages.length + 1];
                  System.arraycopy(this.startingPages, 0, var9, 0, this.startingPages.length);
                  var9[this.startingPages.length] = var3;
                  this.startingPages = var9;
                  --var3;
               } else {
                  var3 += var7;
                  var3 -= var3 == this.lines.length - 1 && var8 != var7 ? 1 : 0;
                  if (var3 == this.lines.length - 1 && var3 * var4 + var6 + this.borderH > var1 || var3 < this.lines.length - 1) {
                     var9 = new int[this.startingPages.length + 1];
                     System.arraycopy(this.startingPages, 0, var9, 0, this.startingPages.length);
                     var9[this.startingPages.length] = var3;
                     this.startingPages = var9;
                     --var3;
                  }
               }
            }

            return;
         }
      }

      void scrollUp() {
         if (this.isMultipage && this.startingPages != null && this.lines != null) {
            this.startLine = this.startLine != 0 && this.startPage != 0 ? this.startingPages[--this.startPage] : this.startLine;
         }

      }

      void scrollDown() {
         if (this.isMultipage && this.startingPages != null && this.lines != null) {
            this.startLine = this.startLine != this.lines.length - 1 && this.startPage != this.startingPages.length - 1 ? this.startingPages[++this.startPage] : this.startLine;
         }

      }

      private void paintLine(int var1, int var2, int var3, com.nokia.mid.impl.isa.ui.gdi.Graphics var4, boolean var5) {
         if (var2 != 0) {
         }

         if (this.lines[var1] != null && this.lines[var1].getTextLineHeight() > 0) {
            ColorCtrl var6 = var4.getColorCtrl();
            int var7 = var6.getFgColor();
            if (this.highlighted) {
               if (var5) {
                  var6.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
               } else {
                  var6.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
               }
            } else {
               var6.setFgColor(UIStyle.COLOUR_TEXT);
            }

            this.drawElementText(var1, var3, var4);
            var6.setFgColor(var7);
         }

         if (var1 == 0) {
            this.drawCheckMark(var3, var4);
            this.drawElementImage(var3, this.imagePart, var4);
         }

      }

      private void drawElementText(int var1, int var2, com.nokia.mid.impl.isa.ui.gdi.Graphics var3) {
         int var4 = ChoiceHandler.this.elementTextZone.x + ChoiceHandler.this.mainZone.x;
         int var5 = ChoiceHandler.this.elementTextZone.y + var2;
         var5 += var1 == 0 ? ChoiceHandler.this.elementTextZone.getMarginTop() : 0;
         if (UIStyle.isAlignedLeftToRight) {
            var4 += ChoiceHandler.this.elementTextZone.getMarginLeft();
            this.lines[var1].setAlignment(1);
         } else {
            var4 += ChoiceHandler.this.elementTextZone.width - ChoiceHandler.this.elementTextZone.getMarginRight();
            this.lines[var1].setAlignment(3);
         }

         var3.drawText(this.lines[var1], (short)var4, (short)var5, (short)(ChoiceHandler.this.elementTextZone.width - (ChoiceHandler.this.elementTextZone.getMarginLeft() + ChoiceHandler.this.elementTextZone.getMarginRight())));
      }

      private void drawCheckMark(int var1, com.nokia.mid.impl.isa.ui.gdi.Graphics var2) {
         Pixmap var3 = this.selected ? ChoiceHandler.this.checkIconSelected : ChoiceHandler.this.checkIconNotSelected;
         if (ChoiceHandler.this.elementChoiceBoxZone != null && var3 != null) {
            Displayable.uistyle.drawPixmapInZone(var2, ChoiceHandler.this.elementChoiceBoxZone, ChoiceHandler.this.mainZone.x, var1, var3);
         }
      }

      private void drawElementImage(int var1, Image var2, com.nokia.mid.impl.isa.ui.gdi.Graphics var3) {
         if (ChoiceHandler.this.elementIconGraphicZone != null && var2 != null) {
            Displayable.uistyle.drawPixmapInZone(var3, ChoiceHandler.this.elementIconGraphicZone, ChoiceHandler.this.mainZone.x, var1, var2.getPixmap());
         }
      }
   }
}
