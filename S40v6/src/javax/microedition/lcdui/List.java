package javax.microedition.lcdui;

public class List extends Screen implements Choice {
   public static final Command SELECT_COMMAND = new Command("", 1, 0);
   private Command selectCommand;
   private ChoiceVector cv;
   private NativeList presentation;
   private boolean isVisible;
   private boolean isUsedAsBackground;

   List(String title, ChoiceVector popupCv) {
      this.selectCommand = SELECT_COMMAND;
      this.isVisible = true;
      this.isUsedAsBackground = false;
      this.setTitleImpl(title);
      this.setNativeDelegate(true);
      this.nativeWrapper = true;
      this.cv = popupCv;
   }

   public List(String title, int listType) {
      this.selectCommand = SELECT_COMMAND;
      this.isVisible = true;
      this.isUsedAsBackground = false;
      if (listType != 1 && listType != 2 && listType != 3) {
         throw new IllegalArgumentException();
      } else {
         this.setTitleImpl(title);
         this.setNativeDelegate(true);
         this.cv = new List.ListCV((String[])null, (Image[])null, listType);
         this.nativeWrapper = true;
      }
   }

   public List(String title, int listType, String[] stringElements, Image[] imageElements) {
      this.selectCommand = SELECT_COMMAND;
      this.isVisible = true;
      this.isUsedAsBackground = false;
      if (stringElements == null) {
         throw new NullPointerException();
      } else if (imageElements != null && imageElements.length != stringElements.length) {
         throw new IllegalArgumentException();
      } else if (listType != 1 && listType != 2 && listType != 3) {
         throw new IllegalArgumentException();
      } else {
         this.setTitleImpl(title);
         this.setNativeDelegate(true);
         this.cv = new List.ListCV(stringElements, imageElements, listType);
         this.nativeWrapper = true;
      }
   }

   public int size() {
      synchronized(Display.LCDUILock) {
         return this.cv.size();
      }
   }

   public String getString(int elementNum) {
      synchronized(Display.LCDUILock) {
         return this.cv.getString(elementNum);
      }
   }

   public Image getImage(int elementNum) {
      synchronized(Display.LCDUILock) {
         return this.cv.getImage(elementNum);
      }
   }

   public int append(String stringPart, Image imagePart) {
      synchronized(Display.LCDUILock) {
         int index = this.cv.nOfItems;
         this.insertImpl(this.cv.nOfItems, stringPart, imagePart);
         return index;
      }
   }

   public void insert(int elementNum, String stringPart, Image imagePart) {
      synchronized(Display.LCDUILock) {
         this.insertImpl(elementNum, stringPart, imagePart);
      }
   }

   void insertImpl(int elementNum, String stringPart, Image imagePart) {
      boolean updateSoftkeys = false;
      if (elementNum != this.cv.nOfItems) {
         this.cv.checkForBounds(elementNum);
      }

      if (this.presentation != null && stringPart != null) {
         updateSoftkeys = this.presentation.insert(elementNum, imagePart != null);
      }

      this.cv.insert(elementNum, stringPart, imagePart);
      if (updateSoftkeys) {
         this.updateSoftkeys(false, false);
      }

   }

   void insert(int elementNum, ChoiceItem ci) {
      boolean updateSoftkeys = false;
      if (elementNum != this.cv.nOfItems) {
         this.cv.checkForBounds(elementNum);
      }

      if (this.presentation != null) {
         updateSoftkeys = this.presentation.insert(elementNum, ci.image != null);
      }

      this.cv.insert(elementNum, ci);
      if (updateSoftkeys) {
         this.updateSoftkeys(false, false);
      }

   }

   public void delete(int elementNum) {
      boolean updateSoftkeys = false;
      synchronized(Display.LCDUILock) {
         this.cv.checkForBounds(elementNum);
         if (this.presentation != null) {
            updateSoftkeys = this.presentation.delete(elementNum);
         }

         this.cv.delete(elementNum);
         if (updateSoftkeys) {
            this.updateSoftkeys(false, false);
         }

      }
   }

   public void deleteAll() {
      synchronized(Display.LCDUILock) {
         if (this.presentation != null) {
            this.presentation.deleteAll();
         }

         this.cv.deleteAll();
         this.updateSoftkeys(false, false);
      }
   }

   public void set(int elementNum, String stringPart, Image imagePart) {
      synchronized(Display.LCDUILock) {
         boolean layoutChanges = this.cv.set(elementNum, stringPart, imagePart);
         if (this.presentation != null) {
            this.presentation.resetData(elementNum, imagePart != null, layoutChanges);
         }

      }
   }

   public boolean isSelected(int elementNum) {
      synchronized(Display.LCDUILock) {
         return this.cv.isSelected(elementNum);
      }
   }

   public boolean isShown() {
      synchronized(Display.LCDUILock) {
         return this.isVisible && this.isShownImpl();
      }
   }

   public int getSelectedIndex() {
      synchronized(Display.LCDUILock) {
         return this.cv.getSelectedIndex();
      }
   }

   public int getSelectedFlags(boolean[] selectedArray_return) {
      synchronized(Display.LCDUILock) {
         return this.cv.getSelectedFlags(selectedArray_return);
      }
   }

   public void setSelectedIndex(int elementNum, boolean selected) {
      synchronized(Display.LCDUILock) {
         if (this.cv.setSelectedIndex(elementNum, selected) && this.presentation != null) {
            this.presentation.nativeSetSelectedIndex(elementNum, selected);
         }

      }
   }

   public void setSelectedFlags(boolean[] selectedArray) {
      synchronized(Display.LCDUILock) {
         this.cv.setSelectedFlags(selectedArray);
         if (this.presentation != null) {
            this.presentation.setSelectedFlags();
         }

      }
   }

   public void removeCommand(Command cmd) {
      synchronized(Display.LCDUILock) {
         if (cmd == this.selectCommand) {
            this.selectCommand = null;
         } else if (super.removeCommandImpl(cmd) && this.presentation != null) {
            this.presentation.updateMenuCommands();
         }

      }
   }

   public void setSelectCommand(Command command) {
      if (this.cv.type == 3) {
         synchronized(Display.LCDUILock) {
            if (command != null && command != SELECT_COMMAND) {
               if (this.selectCommand != SELECT_COMMAND && this.selectCommand != command) {
                  this.addCommandImpl(this.selectCommand);
               }

               this.selectCommand = command;
               int index = this.displayableCommands.indexOfCommand(command);
               if (index != -1) {
                  this.displayableCommands.removeCommandAt(index);
               }

               this.updateSoftkeys(true);
            } else {
               this.selectCommand = command;
               if (this.displayed) {
                  this.updateSoftkeys(true);
                  this.presentation.updateMenuCommands();
               }

            }
         }
      }
   }

   public void setFitPolicy(int fitPolicy) {
      synchronized(Display.LCDUILock) {
         if (this.cv.setFitPolicy(fitPolicy) && this.presentation != null) {
            this.presentation.nativeSetFitPolicy(fitPolicy == 1);
         }

      }
   }

   public int getFitPolicy() {
      return this.cv.getFitPolicy();
   }

   public void setFont(int elementNum, Font font) {
      synchronized(Display.LCDUILock) {
         if (this.cv.setFont(elementNum, font) && this.presentation != null) {
            this.presentation.nativeReloadItemElement(this.cv, elementNum);
         }

      }
   }

   public Font getFont(int elementNum) {
      synchronized(Display.LCDUILock) {
         this.cv.checkForBounds(elementNum);
         return this.cv.getFont(elementNum);
      }
   }

   void stayAlive(boolean live) {
      if (this.presentation != null) {
         if (this.isUsedAsBackground && !live) {
            this.presentation.dismiss();
            this.presentation = null;
         }

         this.isUsedAsBackground = live;
      }

   }

   void undimTickerZone(boolean tickerZoneVisible) {
      if (this.presentation != null) {
         this.presentation.nativeSetTicker(tickerZoneVisible || this.ticker != null);
      }

   }

   void callPaint(Graphics g) {
   }

   void callKeyRepeated(int keyCode, int keyDataIdx) {
   }

   void callKeyReleased(int keyCode, int keyDataIdx) {
   }

   void callKeyPressed(int keyCode, int keyDataIdx) {
   }

   void callHideNotify(Display d) {
      super.callHideNotify(d);
      synchronized(Display.LCDUILock) {
         if (!this.isUsedAsBackground) {
            this.presentation.dismiss();
            this.presentation = null;
         }

      }
   }

   void callShowNotify(Display d) {
      synchronized(Display.LCDUILock) {
         this.displayed = true;
         this.myDisplay = d;
         if (this.presentation == null) {
            this.presentation = NativeList.createNativeList(this.cv);
            this.presentation.launch();
         }

         if (this.isUsedAsBackground) {
            this.isUsedAsBackground = false;
            this.invalidate();
         }

         this.updateSoftkeys(false, false);
      }

      super.callShowNotify(d);
   }

   void updateSoftKeyLabel(String labelText, int index) {
      this.presentation.updateSoftKey(labelText, index, this.softkeyCommands.getCommand(index));
   }

   void callInvalidate() {
   }

   void invalidate() {
      if (this.presentation != null) {
         this.presentation.nativeSetTicker(this.getTicker() != null);
      }

   }

   Command[] getExtraCommands() {
      Command[] extra_cmds = null;
      if (this.presentation != null && this.cv.size() > 0) {
         if (this.cv.type != 3 && this.cv.type != 4) {
            extra_cmds = this.presentation.getExtraCommands();
         } else if (this.selectCommand != null) {
            extra_cmds = new Command[]{this.selectCommand};
         }
      }

      return extra_cmds;
   }

   boolean launchExtraCommand(Command c) {
      boolean callListener = this.cv.type == 3 && this.cv.size() != 0 && this.commandListener != null && this.selectCommand != null && c == this.selectCommand;
      if (callListener) {
         this.myDisplay.requestCommandAction(c, this, this.commandListener);
      }

      return false;
   }

   boolean addCommandImpl(Command cmd) {
      boolean wasAdded = cmd != this.selectCommand ? super.addCommandImpl(cmd) : false;
      if (wasAdded && this.presentation != null) {
         this.presentation.updateMenuCommands();
      }

      return wasAdded;
   }

   void setTitleImpl(String s) {
      this.title = s;
      if (this.presentation != null) {
         this.presentation.nativeSetTitle(this.getInternalTitle());
      }

   }

   boolean isPowerSavingActive() {
      return false;
   }

   private void callHandler(Command cmd, CommandListener listener) {
      synchronized(Display.calloutLock) {
         listener.commandAction(cmd, this);
      }
   }

   private class ListCV extends ChoiceVector {
      ListCV(String[] texts, Image[] images, int type) {
         super(texts, images, type);
      }

      String getTitle() {
         return List.this.getInternalTitle();
      }

      void getCmd(int param, boolean isSoftkey) {
         Command cmd = null;
         if (isSoftkey) {
            if (param == 1) {
               if (this.type == 3) {
                  cmd = List.this.selectCommand;
               }
            } else {
               cmd = List.this.softkeyCommands.getCommand(param);
            }
         } else {
            cmd = List.this.optionMenuCommands.getCommand(param);
         }

         if (cmd == OptionsMenu.optionsCommand) {
         }

      }

      Command[] getOptionCommands() {
         return List.this.optionMenuCommands.getAsArray();
      }

      void handleCmd(Command cmd) {
         CommandListener listener;
         synchronized(Display.LCDUILock) {
            listener = List.this.commandListener;
         }

         if (listener != null) {
            List.this.callHandler(cmd, listener);
         }

      }

      boolean hasTicker() {
         return List.this.ticker != null;
      }
   }
}
