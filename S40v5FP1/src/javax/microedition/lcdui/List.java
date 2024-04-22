package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;

public class List extends Screen implements Choice {
   public static final Command SELECT_COMMAND = new Command("", 1, 0);
   private static List midletList = new List((String)null, 3);
   private static final com.nokia.mid.impl.isa.ui.gdi.Font EMPTY_GDI_FONT;
   private Command selectCommand;
   private static final int DEFAULT_FIT_POLICY;
   private int currentFitPolicy;
   private int type;
   private Zone listZone;
   private Zone scrollbarZone;
   private ChoiceHandler handler;

   public List(String title, int listType) {
      this.selectCommand = SELECT_COMMAND;
      if (listType != 1 && listType != 2 && listType != 3) {
         throw new IllegalArgumentException();
      } else {
         synchronized(Display.LCDUILock) {
            this.setTitleImpl(title);
            this.type = listType;
            this.currentFitPolicy = DEFAULT_FIT_POLICY;
            this.setZoneReferences();
         }
      }
   }

   public List(String title, int listType, String[] stringElements, Image[] imageElements) {
      this(title, listType);
      if (stringElements == null) {
         throw new NullPointerException();
      } else if (imageElements != null && imageElements.length != stringElements.length) {
         throw new IllegalArgumentException();
      } else {
         synchronized(Display.LCDUILock) {
            this.layoutValid = true;
            Image img = null;

            for(int i = 0; i < stringElements.length; ++i) {
               if (imageElements != null) {
                  img = imageElements[i];
               }

               this.append(stringElements[i], img);
            }

         }
      }
   }

   List(String title, ChoiceHandler choiceHandler) {
      this(title, 3, true);
      synchronized(Display.LCDUILock) {
         this.handler = choiceHandler;
      }
   }

   List(String title, int listType, boolean isSystemScreen) {
      this(title, listType);
      synchronized(Display.LCDUILock) {
         this.setSystemScreen(isSystemScreen);
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
         boolean firstElementAppend = false;
         int index;
         if (this.handler == null) {
            firstElementAppend = true;
            this.createHandler();
            index = this.handler.append(stringPart, imagePart);
            if (this.type != 2) {
               this.handler.choiceElements[0].selected = true;
            }
         } else {
            index = this.handler.append(stringPart, imagePart);
         }

         if (this.handler.needNewStarting()) {
            this.handler.setStarting(this.listZone.height);
         } else {
            this.handler.determineVisibleElements(this.listZone.height);
         }

         if (this.isShown()) {
            if (firstElementAppend) {
               this.handler.setWrapSoundOn();
            }

            this.repaintFull();
         }

         return index;
      }
   }

   public void insert(int elementNum, String stringPart, Image imagePart) {
      synchronized(Display.LCDUILock) {
         boolean firstElementAppend = false;
         if (this.handler == null) {
            firstElementAppend = true;
            this.createHandler();
            this.handler.insert(elementNum, stringPart, imagePart);
            if (this.type != 2) {
               this.handler.choiceElements[0].selected = true;
            }
         } else {
            this.handler.insert(elementNum, stringPart, imagePart);
         }

         if (this.handler.needNewStarting()) {
            this.handler.setStarting(this.listZone.height);
         } else {
            this.handler.determineVisibleElements(this.listZone.height);
         }

         if (this.isShown()) {
            if (firstElementAppend) {
               this.handler.setWrapSoundOn();
            }

            this.repaintFull();
         }

      }
   }

   public void delete(int elementNum) {
      synchronized(Display.LCDUILock) {
         if (this.handler == null) {
            throw new IndexOutOfBoundsException();
         } else {
            this.handler.delete(elementNum);
            if (this.handler.choiceElements != null && this.handler.choiceElements.length > 0) {
               if (this.type != 2 && this.getSelectedIndexImpl() == -1) {
                  int lastIndex = this.handler.choiceElements.length - 1;
                  this.handler.choiceElements[elementNum > lastIndex ? lastIndex : elementNum].selected = true;
               }
            } else {
               this.handler.setWrapSoundOff();
               this.handler = null;
            }

            if (this.handler != null && this.handler.listHeight <= this.listZone.height) {
               this.handler.startElement = 0;
            }

            if (this.isShown()) {
               this.repaintFull();
            }

         }
      }
   }

   public void deleteAll() {
      synchronized(Display.LCDUILock) {
         if (this.handler != null) {
            this.handler.setWrapSoundOff();
            this.handler = null;
            if (this.isShown()) {
               this.repaintFull();
            }
         }

      }
   }

   public void set(int elementNum, String stringPart, Image imagePart) {
      synchronized(Display.LCDUILock) {
         if (this.handler == null) {
            throw new IndexOutOfBoundsException();
         } else {
            this.handler.set(elementNum, stringPart, imagePart);
            if (this.handler.needNewStarting()) {
               this.handler.setStarting(this.listZone.height);
            } else {
               this.handler.determineVisibleElements(this.listZone.height);
            }

            if (this.isShown()) {
               this.repaintFull();
            }

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
         return this.getSelectedIndexImpl();
      }
   }

   public int getSelectedFlags(boolean[] selectedArray_return) {
      synchronized(Display.LCDUILock) {
         if (this.handler != null) {
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
            this.setSelectedIndexImpl(elementNum, selected);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void setSelectedFlags(boolean[] selectedArray) {
      synchronized(Display.LCDUILock) {
         if (selectedArray == null) {
            throw new NullPointerException();
         } else if (this.size() != 0) {
            if (this.type == 2) {
               this.handler.setSelectedFlags(selectedArray);
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

               this.setSelectedIndexImpl(index, true);
            }

            if (this.isShown()) {
               this.repaintFull();
            }

         }
      }
   }

   public void removeCommand(Command cmd) {
      synchronized(Display.LCDUILock) {
         if (cmd == this.selectCommand) {
            this.selectCommand = null;
         } else {
            super.removeCommandImpl(cmd);
         }

      }
   }

   public void setSelectCommand(Command command) {
      if (this.type == 3) {
         synchronized(Display.LCDUILock) {
            if (command == SELECT_COMMAND) {
               this.selectCommand = command;
               this.updateSoftkeys(true);
            } else if (command == null) {
               this.selectCommand = null;
               this.updateSoftkeys(true);
            } else {
               if (this.selectCommand != SELECT_COMMAND && this.selectCommand != command) {
                  this.addCommandImpl(this.selectCommand);
               }

               this.selectCommand = command;
               int index = this.displayableCommands.indexOfCommand(command);
               if (index != -1) {
                  this.displayableCommands.removeCommandAt(index);
               }

               this.updateSoftkeys(true);
            }
         }
      }
   }

   public void setFitPolicy(int fitPolicy) {
      if (fitPolicy >= 0 && fitPolicy <= 2) {
         synchronized(Display.LCDUILock) {
            this.setFitPolicyImpl(fitPolicy);
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
         if (this.handler.setFont(elementNum, font)) {
            if (this.handler.needNewStarting()) {
               this.handler.setStarting(this.listZone.height);
            } else {
               this.handler.determineVisibleElements(this.listZone.height);
            }

            if (this.isShown()) {
               this.repaintFull();
            }
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

   void callPaint(Graphics g) {
      super.callPaint(g);
      synchronized(Display.LCDUILock) {
         boolean bDone = false;
         if (this.getTicker() != null && this.listZone.y >= g.getClipY() + g.getClipHeight()) {
            bDone = true;
         } else {
            g.setClip(this.listZone.x, this.listZone.y, this.listZone.width, this.listZone.height);
            if (this.handler == null || this.size() == 0) {
               this.paintEmptyList(g);
               bDone = true;
            }
         }

         if (!bDone) {
            this.handler.paintElements(this.listZone.x, this.listZone.y, g, 0);
            this.updateSoftkeys(false);
            g.setClip(this.scrollbarZone.x, this.scrollbarZone.y, this.scrollbarZone.width, this.scrollbarZone.height);
            Displayable.uistyle.drawScrollbar(g.getImpl(), this.scrollbarZone, 1, this.size(), 1, this.handler.highlightedElement + 1, false);
            Displayable.uistyle.setIndex(this, this.handler.highlightedElement + 1);
         }

      }
   }

   void callKeyRepeated(int keyCode, int keyDataIdx) {
      this.callKeyPressed(keyCode, keyDataIdx);
   }

   void callKeyPressed(int keyCode, int keyDataIdx) {
      boolean doRepaint = false;
      boolean callListener;
      synchronized(Display.LCDUILock) {
         callListener = this.type == 3 && keyCode == -10 && this.size() != 0 && this.commandListener != null && this.selectCommand != null;
      }

      if (callListener) {
         synchronized(Display.calloutLock) {
            this.commandListener.commandAction(this.selectCommand, this);
         }
      } else {
         switch(keyCode) {
         case -10:
            boolean oldstate = false;
            synchronized(Display.LCDUILock) {
               if (this.size() != 0) {
                  oldstate = this.handler.choiceElements[this.handler.highlightedElement].selected;
                  this.setSelectedIndexImpl(this.handler.highlightedElement, !oldstate);
               }
               break;
            }
         case -2:
            synchronized(Display.LCDUILock) {
               if (this.size() != 0) {
                  this.handler.choiceElements[this.handler.highlightedElement].highlighted = false;
                  if (this.type == 3) {
                     this.handler.choiceElements[this.handler.highlightedElement].selected = false;
                  }

                  this.handler.scrollDown(this.listZone.height);
                  doRepaint = true;
                  if (this.type == 3) {
                     this.handler.choiceElements[this.handler.highlightedElement].selected = true;
                  }

                  this.handler.choiceElements[this.handler.highlightedElement].highlighted = true;
               }
               break;
            }
         case -1:
            synchronized(Display.LCDUILock) {
               if (this.size() != 0) {
                  this.handler.choiceElements[this.handler.highlightedElement].highlighted = false;
                  if (this.type == 3) {
                     this.handler.choiceElements[this.handler.highlightedElement].selected = false;
                  }

                  this.handler.scrollUp(this.listZone.height);
                  if (this.type == 3) {
                     this.handler.choiceElements[this.handler.highlightedElement].selected = true;
                  }

                  this.handler.choiceElements[this.handler.highlightedElement].highlighted = true;
                  doRepaint = true;
               }
               break;
            }
         case 35:
            synchronized(Display.LCDUILock) {
               if (this.size() != 0) {
                  Display display = this.myDisplay;
                  TruncatedItemScreen tScreen = display.getTruncatedItemScreen();
                  ChoiceHandler.ChoiceElement element = this.handler.choiceElements[this.handler.highlightedElement];
                  tScreen.showElement(display, this, element.stringPart, element.imagePart, false);
                  break;
               }
            }
         case -7:
         case -6:
         case -5:
         default:
            super.callKeyPressed(keyCode, keyDataIdx);
         }
      }

      if (doRepaint) {
         this.repaintFull();
      }

   }

   void callHideNotify(Display d) {
      super.callHideNotify(d);
      if (this.size() > 0) {
         this.handler.setWrapSoundOff();
         Displayable.uistyle.hideIndex(this);
      }

   }

   void callShowNotify(Display d) {
      super.callShowNotify(d);
      if (this.size() > 0) {
         this.handler.choiceElements[this.handler.highlightedElement].highlighted = true;
         this.handler.setWrapSoundOn();
      }

   }

   void callInvalidate() {
      super.callInvalidate();
      synchronized(Display.LCDUILock) {
         this.layoutValid = true;
         this.setZoneReferences();
         if (this.handler != null) {
            this.handler.setMainZone(this.listZone);
            if (this.handler.needNewStarting()) {
               this.handler.setStarting(this.listZone.height);
            }
         }

         if (!(this instanceof OptionsMenu)) {
            this.repaintFull();
         }

      }
   }

   Command[] getExtraCommands() {
      Command[] ret = null;
      if (this.size() != 0) {
         ChoiceHandler.ChoiceElement[] elements = this.handler.choiceElements;
         ChoiceHandler.ChoiceElement ce = elements[this.handler.highlightedElement];
         if (elements.length != 0) {
            switch(this.type) {
            case 1:
               ret = new Command[]{ChoiceGroup.EXCLUSIVE_SELECT};
               break;
            case 2:
               if (ce.selected) {
                  ret = new Command[]{ChoiceGroup.MULTI_UNMARK};
               } else {
                  ret = new Command[]{ChoiceGroup.MULTI_MARK};
               }
               break;
            case 3:
               if (this.selectCommand != null) {
                  ret = new Command[]{this.selectCommand};
               }
            }

            if ((!this.handler.wrapOn || this.handler.multiTrunc) && ce.isTruncated()) {
               if (ret != null) {
                  Command[] tmpRet = new Command[ret.length + 1];
                  System.arraycopy(ret, 0, tmpRet, 0, ret.length);
                  tmpRet[ret.length] = ChoiceGroup.VIEW;
                  ret = tmpRet;
               } else {
                  ret = new Command[]{ChoiceGroup.VIEW};
               }
            }
         }
      }

      return ret;
   }

   boolean launchExtraCommand(Command c) {
      boolean callListener = this.type == 3 && this.size() != 0 && this.commandListener != null && this.selectCommand != null && c == this.selectCommand;
      if (callListener) {
         this.myDisplay.requestCommandAction(c, this, this.commandListener);
      } else {
         if (this.size() == 0) {
            return false;
         }

         ChoiceHandler.ChoiceElement[] elements = this.handler.choiceElements;
         if (c == ChoiceGroup.VIEW) {
            Display display = this.myDisplay;
            TruncatedItemScreen tScreen = display.getTruncatedItemScreen();
            ChoiceHandler.ChoiceElement element = this.handler.choiceElements[this.handler.highlightedElement];
            tScreen.showElement(display, this, element.stringPart, element.imagePart, true);
            return true;
         }

         boolean oldstate = elements[this.handler.highlightedElement].selected;
         this.setSelectedIndexImpl(this.handler.highlightedElement, !oldstate);
      }

      return false;
   }

   Zone getScrollbarZone() {
      return this.ticker != null ? Displayable.screenTickScrollbarZone : Displayable.screenNormScrollbarZone;
   }

   boolean addCommandImpl(Command cmd) {
      return cmd != this.selectCommand ? super.addCommandImpl(cmd) : false;
   }

   int getSelectedIndexImpl() {
      return this.type != 2 && this.handler != null ? this.handler.getSelectedIndex() : -1;
   }

   void setSelectedIndexImpl(int elementNum, boolean selected) {
      ChoiceHandler.ChoiceElement[] elements = this.handler.choiceElements;
      if (this.type != 2) {
         if (!selected || elements[elementNum].selected) {
            return;
         }

         for(int i = 0; i < elements.length; ++i) {
            elements[i].selected = false;
         }

         this.handler.setSelectedIndex(elementNum, selected);
         if (this.type == 3) {
            elements[this.handler.highlightedElement].highlighted = false;
            this.handler.highlightedElement = elementNum;
            elements[elementNum].highlighted = true;
            if (elements[elementNum].displayedRatio != 1 && this.handler.needNewStarting()) {
               this.handler.setStarting(this.listZone.height);
            }
         }
      } else {
         this.handler.setSelectedIndex(elementNum, selected);
      }

      if (this.isShown()) {
         this.repaintFull();
      }

   }

   void setFitPolicyImpl(int fitPolicy) {
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
      }

   }

   private void paintEmptyList(Graphics g) {
      TextBreaker breaker = TextBreaker.getBreaker(EMPTY_GDI_FONT, TextDatabase.getText(33), false);
      TextLine tLine = breaker.getTextLine(this.listZone.width);
      tLine.setAlignment(2);
      ColorCtrl colorCtrl = g.getImpl().getColorCtrl();
      int oldFgColor = colorCtrl.getFgColor();
      colorCtrl.setFgColor(UIStyle.COLOUR_TEXT);
      g.getImpl().drawText(tLine, (short)((this.listZone.x + this.listZone.width) / 2), (short)this.listZone.y);
      colorCtrl.setFgColor(oldFgColor);
      breaker.destroyBreaker();
      this.updateSoftkeys(false);
      g.setClip(this.scrollbarZone.x, this.scrollbarZone.y, this.scrollbarZone.width, this.scrollbarZone.height);
      Displayable.uistyle.drawScrollbar(g.getImpl(), this.scrollbarZone, 0, 1, 1, 0, false);
      Displayable.uistyle.hideIndex(this);
   }

   private void setZoneReferences() {
      this.scrollbarZone = this.getScrollbarZone();
      this.listZone = this.ticker != null ? uistyle.getZone(54) : uistyle.getZone(53);
   }

   private void createHandler() {
      UIStyle ui = Displayable.uistyle;
      this.handler = new ChoiceHandler(true, false, this.listZone);
      this.handler.owner = this;
      this.handler.wrapOn = this.currentFitPolicy == 1 || this.currentFitPolicy == 0 && DEFAULT_FIT_POLICY == 1;
      if (this.type == 3) {
         this.handler.elementTextZone = ui.getZone(17);
         this.handler.elementIconGraphicZone = null;
         this.handler.elementChoiceBoxZone = null;
         this.handler.auxTextZone = ui.getZone(18);
         this.handler.auxIconZone = ui.getZone(19);
         this.handler.auxBoxZone = null;
         this.handler.checkIconNotSelected = null;
         this.handler.checkIconSelected = null;
      } else {
         this.handler.elementTextZone = ui.getZone(13);
         this.handler.elementIconGraphicZone = null;
         this.handler.elementChoiceBoxZone = ui.getZone(12);
         this.handler.auxTextZone = ui.getZone(15);
         this.handler.auxIconZone = ui.getZone(16);
         this.handler.auxBoxZone = ui.getZone(14);
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
         this.handler.choiceElements[this.handler.startElement].startLine = 0;
         this.handler.changeLayout();
         if (this.handler.needNewStarting()) {
            this.handler.setStarting(this.listZone.height);
         }

         if (this.handler.listHeight <= this.listZone.height) {
            this.handler.startElement = 0;
            this.handler.choiceElements[this.handler.startElement].startLine = 0;
         }

         if (this.isShown()) {
            this.repaintFull();
         }
      }

   }

   static void resetDisplayInExplorersList() {
      midletList.myDisplay = null;
   }

   static {
      synchronized(Display.LCDUILock) {
         midletList.setFitPolicyImpl(2);
      }

      InitJALM.s_setMIDletList(midletList);
      EMPTY_GDI_FONT = Displayable.uistyle.getZone(17).getFont();
      DEFAULT_FIT_POLICY = UIStyle.getNumberOfSoftKeys() > 2 ? 2 : 1;
   }
}
