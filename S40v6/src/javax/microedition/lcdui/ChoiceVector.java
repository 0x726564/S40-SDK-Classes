package javax.microedition.lcdui;

abstract class ChoiceVector {
   ChoiceItem[] listOfItems;
   int nOfItems = 0;
   int selectedIndex = -1;
   int highlightedIndex = -1;
   int type;
   int fitPolicy = 0;
   int nOfImages = 0;
   boolean busy = false;

   ChoiceVector() {
   }

   ChoiceVector(String[] texts, Image[] images, int type) {
      this.type = type;
      this.nOfItems = texts != null ? texts.length : 0;
      this.listOfItems = new ChoiceItem[this.nOfItems + 5];

      for(int i = 0; i < this.nOfItems; ++i) {
         Image image = null == images ? null : images[i];
         this.listOfItems[i] = new ChoiceItem(texts[i], image);
         if (image != null) {
            ++this.nOfImages;
         }
      }

      if (type != 2 && this.nOfItems > 0) {
         this.listOfItems[0].selected = true;
         this.selectedIndex = 0;
      }

   }

   int getType() {
      return this.type;
   }

   int size() {
      return this.nOfItems;
   }

   String getString(int elementNum) throws IndexOutOfBoundsException {
      synchronized(Display.LCDUILock) {
         this.checkForBounds(elementNum);
         return this.listOfItems[elementNum].text;
      }
   }

   Image getImage(int elementNum) {
      synchronized(Display.LCDUILock) {
         this.checkForBounds(elementNum);
         return this.listOfItems[elementNum].image;
      }
   }

   void insert(int elementNum, String stringPart, Image imagePart) {
      this.insert(elementNum, new ChoiceItem(stringPart, imagePart));
   }

   void insert(int elementNum, ChoiceItem ci) {
      if (elementNum >= 0 && elementNum <= this.nOfItems) {
         this.ensureCapacity(elementNum);
         this.listOfItems[elementNum] = ci;
         if (ci.image != null) {
            ++this.nOfImages;
         }

         ++this.nOfItems;
         if (this.type != 2) {
            if (this.nOfItems == 1) {
               this.setSelectedIndex(0, true);
            } else if (elementNum <= this.selectedIndex) {
               ++this.selectedIndex;
            }
         }

      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   void delete(int elementNum) {
      if (elementNum >= 0 && elementNum < this.nOfItems) {
         if (this.listOfItems[elementNum].image != null) {
            --this.nOfImages;
         }

         this.listOfItems[elementNum] = null;
         if (elementNum < this.nOfItems - 1) {
            System.arraycopy(this.listOfItems, elementNum + 1, this.listOfItems, elementNum, this.nOfItems - (elementNum + 1));
         }

         --this.nOfItems;
         if (this.type != 2) {
            if (elementNum < this.selectedIndex || elementNum == this.selectedIndex && elementNum == this.nOfItems) {
               --this.selectedIndex;
            }

            if (this.selectedIndex >= 0) {
               this.listOfItems[this.selectedIndex].selected = true;
            }
         }

      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   void deleteAll() {
      this.listOfItems = new ChoiceItem[5];
      this.nOfImages = 0;
      this.selectedIndex = -1;
      this.nOfItems = 0;
   }

   boolean set(int elementNum, String stringPart, Image imagePart) {
      this.checkForBounds(elementNum);
      if (stringPart == null) {
         throw new NullPointerException();
      } else {
         boolean layoutChanges = imagePart != null && this.nOfImages == 0 || imagePart == null && this.getImage(elementNum) != null && this.nOfImages == 1;
         ChoiceItem ci = this.listOfItems[elementNum];
         if (ci.image != imagePart) {
            if (imagePart == null) {
               --this.nOfImages;
            } else if (ci.image == null) {
               ++this.nOfImages;
            }
         }

         this.listOfItems[elementNum].set(stringPart, imagePart);
         return layoutChanges;
      }
   }

   boolean isSelected(int elementNum) {
      this.checkForBounds(elementNum);
      return this.listOfItems[elementNum].selected;
   }

   int getSelectedIndex() {
      return this.type != 2 ? this.selectedIndex : -1;
   }

   boolean hasImage() {
      return this.nOfImages > 0;
   }

   int getNumOfImages() {
      return this.nOfImages;
   }

   int getSelectedFlags(boolean[] selectedArray_return) throws NullPointerException, IllegalArgumentException {
      int selectedNum = 0;
      if (selectedArray_return == null) {
         throw new NullPointerException();
      } else if (this.nOfItems == 0) {
         return 0;
      } else if (selectedArray_return.length < this.nOfItems) {
         throw new IllegalArgumentException();
      } else {
         int i;
         for(i = this.nOfItems; i < selectedArray_return.length; ++i) {
            selectedArray_return[i] = false;
         }

         for(i = 0; i < this.nOfItems; ++i) {
            selectedArray_return[i] = this.listOfItems[i].selected;
            selectedNum += selectedArray_return[i] ? 1 : 0;
         }

         return selectedNum;
      }
   }

   void setHighlightedItem(int index) {
      if (this.highlightedIndex >= 0) {
         this.listOfItems[this.highlightedIndex].highlighted = false;
      }

      this.highlightedIndex = index;
      this.listOfItems[this.highlightedIndex].highlighted = true;
   }

   ChoiceItem getHighlightedItem() {
      return this.listOfItems[this.highlightedIndex];
   }

   boolean setSelectedIndex(int elementNum, boolean selected) {
      this.checkForBounds(elementNum);
      if ((selected || this.type == 2) && selected != this.isSelected(elementNum)) {
         if (this.type != 2) {
            if (this.selectedIndex >= 0) {
               this.listOfItems[this.selectedIndex].selected = false;
            }

            this.selectedIndex = elementNum;
         } else {
            selected = !this.listOfItems[elementNum].selected;
         }

         this.listOfItems[elementNum].selected = selected;
         return true;
      } else {
         return false;
      }
   }

   void setSelectedFlags(boolean[] selectedArray) {
      if (selectedArray.length < this.nOfItems) {
         throw new IllegalArgumentException();
      } else if (selectedArray == null) {
         throw new NullPointerException();
      } else if (this.nOfItems != 0) {
         int i;
         for(i = 0; i < this.nOfItems; ++i) {
            if (this.type == 2) {
               this.listOfItems[i].selected = selectedArray[i];
            } else if (selectedArray[i]) {
               this.listOfItems[this.selectedIndex].selected = false;
               this.listOfItems[i].selected = true;
               this.selectedIndex = i;
               break;
            }
         }

         if (i == this.nOfItems && this.type != 2) {
            if (this.selectedIndex >= 0) {
               this.listOfItems[this.selectedIndex].selected = false;
            }

            this.listOfItems[0].selected = true;
            this.selectedIndex = 0;
         }

      }
   }

   boolean setFitPolicy(int newFitPolicy) {
      boolean policyHasChanged = false;
      if (newFitPolicy >= 0 && newFitPolicy <= 2) {
         if (this.fitPolicy != newFitPolicy && (newFitPolicy == 1 || this.fitPolicy == 1)) {
            policyHasChanged = true;
         }

         this.fitPolicy = newFitPolicy;
         return policyHasChanged;
      } else {
         throw new IllegalArgumentException();
      }
   }

   int getFitPolicy() {
      return this.fitPolicy;
   }

   boolean setFont(int elementNum, Font font) {
      this.checkForBounds(elementNum);
      Font newFont = font != null ? font : Font.getDefaultFont();
      if (newFont != this.listOfItems[elementNum].jFont) {
         this.listOfItems[elementNum].jFont = newFont;
         return true;
      } else {
         return false;
      }
   }

   Font getFont(int elementNum) {
      return this.listOfItems[elementNum].jFont;
   }

   void markAll(boolean marked) {
      for(int i = 0; i < this.nOfItems; ++i) {
         this.listOfItems[i].selected = marked;
      }

   }

   boolean checkForBounds(int elementNum) {
      if (elementNum >= 0 && elementNum < this.nOfItems) {
         return true;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   ChoiceItem getItem(int ix) {
      return this.listOfItems[ix];
   }

   boolean hasTicker() {
      return false;
   }

   abstract String getTitle();

   abstract void handleCmd(Command var1);

   abstract Command[] getOptionCommands();

   private void ensureCapacity(int index) {
      ChoiceItem[] oldElementList = this.listOfItems;
      if (this.listOfItems.length >= this.nOfItems) {
         this.listOfItems = new ChoiceItem[this.nOfItems + 5];
         System.arraycopy(oldElementList, 0, this.listOfItems, 0, index);
      }

      if (index < this.nOfItems) {
         System.arraycopy(oldElementList, index, this.listOfItems, index + 1, this.nOfItems - index);
      }

   }
}
