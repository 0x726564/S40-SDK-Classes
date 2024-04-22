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

   public List(String var1, int var2) {
      this.selectCommand = SELECT_COMMAND;
      if (var2 != 1 && var2 != 2 && var2 != 3) {
         throw new IllegalArgumentException();
      } else {
         synchronized(Display.LCDUILock) {
            this.setTitleImpl(var1);
            this.type = var2;
            this.currentFitPolicy = DEFAULT_FIT_POLICY;
            this.setZoneReferences();
         }
      }
   }

   public List(String var1, int var2, String[] var3, Image[] var4) {
      this(var1, var2);
      if (var3 == null) {
         throw new NullPointerException();
      } else if (var4 != null && var4.length != var3.length) {
         throw new IllegalArgumentException();
      } else {
         synchronized(Display.LCDUILock) {
            this.layoutValid = true;
            Image var6 = null;

            for(int var7 = 0; var7 < var3.length; ++var7) {
               if (var4 != null) {
                  var6 = var4[var7];
               }

               this.append(var3[var7], var6);
            }

         }
      }
   }

   List(String var1, ChoiceHandler var2) {
      this(var1, 3, true);
      synchronized(Display.LCDUILock) {
         this.handler = var2;
      }
   }

   List(String var1, int var2, boolean var3) {
      this(var1, var2);
      synchronized(Display.LCDUILock) {
         this.setSystemScreen(var3);
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
         boolean var5 = false;
         int var8;
         if (this.handler == null) {
            var5 = true;
            this.createHandler();
            var8 = this.handler.append(var1, var2);
            if (this.type != 2) {
               this.handler.choiceElements[0].selected = true;
            }
         } else {
            var8 = this.handler.append(var1, var2);
         }

         if (this.handler.needNewStarting()) {
            this.handler.setStarting(this.listZone.height);
         }

         if (this.isShown()) {
            if (var5) {
               this.handler.setWrapSoundOn();
            }

            this.repaintFull();
         }

         return var8;
      }
   }

   public void insert(int var1, String var2, Image var3) {
      synchronized(Display.LCDUILock) {
         boolean var5 = false;
         if (this.handler == null) {
            var5 = true;
            this.createHandler();
            this.handler.insert(var1, var2, var3);
            if (this.type != 2) {
               this.handler.choiceElements[0].selected = true;
            }
         } else {
            this.handler.insert(var1, var2, var3);
         }

         if (this.handler.needNewStarting()) {
            this.handler.setStarting(this.listZone.height);
         }

         if (this.isShown()) {
            if (var5) {
               this.handler.setWrapSoundOn();
            }

            this.repaintFull();
         }

      }
   }

   public void delete(int var1) {
      synchronized(Display.LCDUILock) {
         if (this.handler == null) {
            throw new IndexOutOfBoundsException();
         } else {
            this.handler.delete(var1);
            if (this.handler.choiceElements != null && this.handler.choiceElements.length > 0) {
               if (this.type != 2 && this.getSelectedIndexImpl() == -1) {
                  int var3 = this.handler.choiceElements.length - 1;
                  this.handler.choiceElements[var1 > var3 ? var3 : var1].selected = true;
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

   public void set(int var1, String var2, Image var3) {
      synchronized(Display.LCDUILock) {
         if (this.handler == null) {
            throw new IndexOutOfBoundsException();
         } else {
            this.handler.set(var1, var2, var3);
            if (this.handler.needNewStarting()) {
               this.handler.setStarting(this.listZone.height);
            }

            if (this.isShown()) {
               this.repaintFull();
            }

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
         return this.getSelectedIndexImpl();
      }
   }

   public int getSelectedFlags(boolean[] var1) {
      synchronized(Display.LCDUILock) {
         if (this.handler != null) {
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
            this.setSelectedIndexImpl(var1, var2);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void setSelectedFlags(boolean[] var1) {
      synchronized(Display.LCDUILock) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (this.size() != 0) {
            if (this.type == 2) {
               this.handler.setSelectedFlags(var1);
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

               this.setSelectedIndexImpl(var3, true);
            }

            if (this.isShown()) {
               this.repaintFull();
            }

         }
      }
   }

   public void removeCommand(Command var1) {
      synchronized(Display.LCDUILock) {
         if (var1 == this.selectCommand) {
            this.selectCommand = null;
         } else {
            super.removeCommandImpl(var1);
         }

      }
   }

   public void setSelectCommand(Command var1) {
      if (this.type == 3) {
         synchronized(Display.LCDUILock) {
            if (var1 == SELECT_COMMAND) {
               this.selectCommand = var1;
               this.updateSoftkeys(true);
            } else if (var1 == null) {
               this.selectCommand = null;
               this.updateSoftkeys(true);
            } else {
               if (this.selectCommand != SELECT_COMMAND && this.selectCommand != var1) {
                  this.addCommandImpl(this.selectCommand);
               }

               this.selectCommand = var1;
               int var3 = this.displayableCommands.indexOfCommand(var1);
               if (var3 != -1) {
                  this.displayableCommands.removeCommandAt(var3);
               }

               this.updateSoftkeys(true);
            }
         }
      }
   }

   public void setFitPolicy(int var1) {
      if (var1 >= 0 && var1 <= 2) {
         synchronized(Display.LCDUILock) {
            this.setFitPolicyImpl(var1);
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
         if (this.handler.setFont(var1, var2)) {
            if (this.handler.needNewStarting()) {
               this.handler.setStarting(this.listZone.height);
            }

            if (this.isShown()) {
               this.repaintFull();
            }
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

   void callPaint(Graphics var1) {
      super.callPaint(var1);
      synchronized(Display.LCDUILock) {
         boolean var3 = false;
         if (this.getTicker() != null && this.listZone.y >= var1.getClipY() + var1.getClipHeight()) {
            var3 = true;
         } else {
            var1.setClip(this.listZone.x, this.listZone.y, this.listZone.width, this.listZone.height);
            if (this.handler == null || this.size() == 0) {
               this.paintEmptyList(var1);
               var3 = true;
            }
         }

         if (!var3) {
            this.handler.paintElements(this.listZone.x, this.listZone.y, var1, 0);
            this.updateSoftkeys(false);
            var1.setClip(this.scrollbarZone.x, this.scrollbarZone.y, this.scrollbarZone.width, this.scrollbarZone.height);
            Displayable.uistyle.drawScrollbar(var1.getImpl(), this.scrollbarZone, 1, this.size(), 1, this.handler.highlightedElement + 1, false);
            Displayable.uistyle.setIndex(this.handler.highlightedElement + 1);
         }

      }
   }

   void callKeyRepeated(int var1, int var2) {
      this.callKeyPressed(var1, var2);
   }

   void callKeyPressed(int var1, int var2) {
      boolean var4 = false;
      Object var5 = null;
      boolean var3;
      synchronized(Display.LCDUILock) {
         var3 = this.type == 3 && var1 == -10 && this.size() != 0 && this.commandListener != null && this.selectCommand != null;
      }

      if (var3) {
         synchronized(Display.calloutLock) {
            this.commandListener.commandAction(this.selectCommand, this);
         }
      } else {
         switch(var1) {
         case -10:
            boolean var6 = false;
            synchronized(Display.LCDUILock) {
               if (this.size() != 0) {
                  var6 = this.handler.choiceElements[this.handler.highlightedElement].selected;
                  this.setSelectedIndexImpl(this.handler.highlightedElement, !var6);
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
                  var4 = true;
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
                  var4 = true;
               }
               break;
            }
         case 35:
            synchronized(Display.LCDUILock) {
               if (this.size() != 0) {
                  Display var8 = this.myDisplay;
                  TruncatedItemScreen var9 = var8.getTruncatedItemScreen();
                  ChoiceHandler.ChoiceElement var10 = this.handler.choiceElements[this.handler.highlightedElement];
                  var9.showElement(var8, this, var10.stringPart, var10.imagePart, false);
                  break;
               }
            }
         case -7:
         case -6:
         case -5:
         default:
            super.callKeyPressed(var1, var2);
         }
      }

      if (var4) {
         this.repaintFull();
      }

   }

   void callHideNotify(Display var1) {
      super.callHideNotify(var1);
      if (this.size() > 0) {
         this.handler.setWrapSoundOff();
      }

   }

   void callShowNotify(Display var1) {
      super.callShowNotify(var1);
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
      Command[] var1 = null;
      if (this.size() != 0) {
         ChoiceHandler.ChoiceElement[] var2 = this.handler.choiceElements;
         ChoiceHandler.ChoiceElement var3 = var2[this.handler.highlightedElement];
         if (var2.length != 0) {
            switch(this.type) {
            case 1:
               var1 = new Command[]{ChoiceGroup.EXCLUSIVE_SELECT};
               break;
            case 2:
               if (var3.selected) {
                  var1 = new Command[]{ChoiceGroup.MULTI_UNMARK};
               } else {
                  var1 = new Command[]{ChoiceGroup.MULTI_MARK};
               }
               break;
            case 3:
               if (this.selectCommand != null) {
                  var1 = new Command[]{this.selectCommand};
               }
            }

            if (!this.handler.wrapOn && var3.isTruncated()) {
               if (var1 != null) {
                  Command[] var4 = new Command[var1.length + 1];
                  System.arraycopy(var1, 0, var4, 0, var1.length);
                  var4[var1.length] = ChoiceGroup.VIEW;
                  var1 = var4;
               } else {
                  var1 = new Command[]{ChoiceGroup.VIEW};
               }
            }
         }
      }

      return var1;
   }

   boolean launchExtraCommand(Command var1) {
      boolean var2 = this.type == 3 && this.size() != 0 && this.commandListener != null && this.selectCommand != null && var1 == this.selectCommand;
      if (var2) {
         this.myDisplay.requestCommandAction(var1, this, this.commandListener);
      } else {
         if (this.size() == 0) {
            return false;
         }

         ChoiceHandler.ChoiceElement[] var3 = this.handler.choiceElements;
         if (var1 == ChoiceGroup.VIEW) {
            Display var7 = this.myDisplay;
            TruncatedItemScreen var5 = var7.getTruncatedItemScreen();
            ChoiceHandler.ChoiceElement var6 = this.handler.choiceElements[this.handler.highlightedElement];
            var5.showElement(var7, this, var6.stringPart, var6.imagePart, true);
            return true;
         }

         boolean var4 = var3[this.handler.highlightedElement].selected;
         this.setSelectedIndexImpl(this.handler.highlightedElement, !var4);
      }

      return false;
   }

   Zone getScrollbarZone() {
      return this.ticker != null ? Displayable.screenTickScrollbarZone : Displayable.screenNormScrollbarZone;
   }

   boolean addCommandImpl(Command var1) {
      return var1 != this.selectCommand ? super.addCommandImpl(var1) : false;
   }

   int getSelectedIndexImpl() {
      return this.type != 2 && this.handler != null ? this.handler.getSelectedIndex() : -1;
   }

   void setSelectedIndexImpl(int var1, boolean var2) {
      ChoiceHandler.ChoiceElement[] var3 = this.handler.choiceElements;
      if (this.type != 2) {
         if (!var2 || var3[var1].selected) {
            return;
         }

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4].selected = false;
         }

         this.handler.setSelectedIndex(var1, var2);
         if (this.type == 3) {
            var3[this.handler.highlightedElement].highlighted = false;
            this.handler.highlightedElement = var1;
            var3[var1].highlighted = true;
            if (var3[var1].displayedRatio != 1 && this.handler.needNewStarting()) {
               this.handler.setStarting(this.listZone.height);
            }
         }
      } else {
         this.handler.setSelectedIndex(var1, var2);
      }

      if (this.isShown()) {
         this.repaintFull();
      }

   }

   void setFitPolicyImpl(int var1) {
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
      }

   }

   private void paintEmptyList(Graphics var1) {
      TextBreaker var3 = TextBreaker.getBreaker(EMPTY_GDI_FONT, TextDatabase.getText(33), false);
      TextLine var2 = var3.getTextLine(this.listZone.width);
      var2.setAlignment(2);
      ColorCtrl var4 = var1.getImpl().getColorCtrl();
      int var5 = var4.getFgColor();
      var4.setFgColor(UIStyle.COLOUR_TEXT);
      var1.getImpl().drawText(var2, (short)((this.listZone.x + this.listZone.width) / 2), (short)this.listZone.y);
      var4.setFgColor(var5);
      var3.destroyBreaker();
      this.updateSoftkeys(false);
      var1.setClip(this.scrollbarZone.x, this.scrollbarZone.y, this.scrollbarZone.width, this.scrollbarZone.height);
      Displayable.uistyle.drawScrollbar(var1.getImpl(), this.scrollbarZone, 0, 1, 1, 0, false);
      Displayable.uistyle.hideIndex();
   }

   private void setZoneReferences() {
      this.scrollbarZone = this.getScrollbarZone();
      this.listZone = this.ticker != null ? Displayable.screenTickMainZone : Displayable.screenNormMainZone;
   }

   private void createHandler() {
      UIStyle var1 = Displayable.uistyle;
      this.handler = new ChoiceHandler(true, false, this.listZone);
      this.handler.wrapOn = this.currentFitPolicy == 1 || this.currentFitPolicy == 0 && DEFAULT_FIT_POLICY == 1;
      if (this.type == 3) {
         this.handler.elementTextZone = var1.getZone(17);
         this.handler.elementIconGraphicZone = null;
         this.handler.elementChoiceBoxZone = null;
         this.handler.auxTextZone = var1.getZone(18);
         this.handler.auxIconZone = var1.getZone(19);
         this.handler.auxBoxZone = null;
         this.handler.checkIconNotSelected = null;
         this.handler.checkIconSelected = null;
      } else {
         this.handler.elementTextZone = var1.getZone(13);
         this.handler.elementIconGraphicZone = null;
         this.handler.elementChoiceBoxZone = var1.getZone(12);
         this.handler.auxTextZone = var1.getZone(15);
         this.handler.auxIconZone = var1.getZone(16);
         this.handler.auxBoxZone = var1.getZone(14);
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

   static {
      synchronized(Display.LCDUILock) {
         midletList.setFitPolicyImpl(2);
      }

      InitJALM.s_setMIDletList(midletList);
      EMPTY_GDI_FONT = Displayable.uistyle.getZone(17).getFont();
      DEFAULT_FIT_POLICY = UIStyle.getNumberOfSoftKeys() > 2 ? 2 : 1;
   }
}
