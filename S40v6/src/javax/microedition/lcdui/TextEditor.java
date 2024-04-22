package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.TextDatabase;

class TextEditor {
   static final int MAX_TEXT_SIZE = 5000;
   private static final Command searchCmd = new Command(11, 35);
   private InlineTextHandler textHandler;
   private TextEditorOwner owner;
   private boolean isVisible;
   private boolean isFocussed;
   private boolean canSearch;
   private boolean doSearchCommandCheck;
   private boolean optionsChangedProcessed;
   private int currentRemaining;
   private int oldRemaining = -1;

   TextEditor(String text, int maxSize, int constraints, TextEditorOwner owner) {
      this.textHandler = new JavaTextHandler(maxSize, constraints, this);
      this.owner = owner;
      this.setConstraints(constraints);
      this.setString(text);
      this.setMaxSize(maxSize);
      this.doSearchCommandCheck = true;
   }

   int getChars(char[] data) {
      if (data == null) {
         throw new NullPointerException();
      } else {
         String str = this.getString();
         int strLength = str.length();
         if (data.length < strLength) {
            throw new ArrayIndexOutOfBoundsException();
         } else {
            str.getChars(0, strLength, data, 0);
            return strLength;
         }
      }
   }

   void setChars(char[] data, int offset, int length) {
      if (data == null) {
         this.clearBuffer();
      } else if (offset <= data.length && offset >= 0 && length >= 0 && offset + length <= data.length && offset + length >= 0) {
         if (length > this.textHandler.getMaxSize()) {
            throw new IllegalArgumentException();
         } else if (length == 0) {
            this.clearBuffer();
         } else {
            String newText = new String(data, offset, length);
            if (!this.isValidString(newText)) {
               throw new IllegalArgumentException();
            } else {
               this.setString(newText);
            }
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   void setConstraints(int constraints) {
      int unmask = constraints & '\uffff';
      if (unmask >= 0 && unmask <= 5) {
         this.textHandler.setConstraints(constraints);
         if (!this.isValidString(this.getString())) {
            this.clearBuffer();
         }

         if (this.doSearchCommandCheck) {
            this.addOrRemoveSearchCommand();
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   void insert(String src, int position) {
      if (src == null) {
         throw new NullPointerException();
      } else {
         this.insert(src.toCharArray(), 0, src.length(), position);
      }
   }

   void insert(char[] data, int offset, int length, int position) {
      if (data == null) {
         throw new NullPointerException();
      } else if (offset >= 0 && offset <= data.length && length >= 0 && offset + length <= data.length) {
         if (length != 0) {
            String text = this.getString();
            int textLen = text.length();
            if (textLen + length > this.textHandler.getMaxSize()) {
               throw new IllegalArgumentException();
            } else {
               if (position < 0) {
                  position = 0;
               } else if (position > textLen) {
                  position = textLen;
               }

               char[] newTextCharArray = new char[textLen + length];
               text.getChars(0, position, newTextCharArray, 0);
               System.arraycopy(data, offset, newTextCharArray, position, length);
               text.getChars(position, textLen, newTextCharArray, position + length);
               String newText = new String(newTextCharArray);
               if (!this.isValidString(newText)) {
                  throw new IllegalArgumentException();
               } else {
                  this.setString(newText, position + length);
               }
            }
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   void delete(int offset, int length) {
      if (offset >= 0 && length >= 0) {
         String text = this.getString();
         int textLength = text.length();
         if (offset < textLength && offset + length <= textLength) {
            if (length != 0) {
               char[] newTextCharArray = new char[textLength - length];
               text.getChars(0, offset, newTextCharArray, 0);
               text.getChars(offset + length, textLength, newTextCharArray, offset);
               String newText = new String(newTextCharArray);
               if (!this.isValidString(newText)) {
                  throw new IllegalArgumentException();
               } else {
                  this.setString(newText, offset);
               }
            }
         } else {
            throw new StringIndexOutOfBoundsException();
         }
      } else {
         throw new StringIndexOutOfBoundsException();
      }
   }

   int getMaxSize() {
      return this.textHandler.getMaxSize();
   }

   int setMaxSize(int newMaxSize) {
      if (newMaxSize <= 0) {
         throw new IllegalArgumentException();
      } else {
         if (newMaxSize > 5000) {
            newMaxSize = 5000;
         }

         String text = this.getString();
         if (newMaxSize < text.length()) {
            text = text.substring(0, newMaxSize);
            if (!this.isValidString(text)) {
               throw new IllegalArgumentException();
            }

            this.setString(text, this.getCursorPosition());
         }

         this.textHandler.setMaxSize(newMaxSize);
         return newMaxSize;
      }
   }

   int size() {
      return this.textHandler.size();
   }

   int getCursorPosition() {
      return this.textHandler.getCursorPosition();
   }

   int getConstraints() {
      return this.textHandler.getConstraints();
   }

   void setInitialInputMode(String characterSubset) {
      this.textHandler.setInitialInputMode(characterSubset);
   }

   String getString() {
      String str = this.textHandler.getString();
      char decimalSeparator = TextDatabase.getText(49).charAt(0);
      if ((this.textHandler.getConstraints() & '\uffff') == 5 && decimalSeparator != '.') {
         str = str.replace(decimalSeparator, '.');
      }

      return str;
   }

   void setString(String newText) {
      this.setString(newText, this.textHandler.getCursorPosition());
   }

   int getCursorWrap() {
      return this.owner.getCursorWrap();
   }

   void keyPressed(int keyCode, int keyDataIdx) {
      this.textHandler.keyPressed(keyCode, keyDataIdx);
   }

   void keyReleased(int keyCode, int keyDataIdx) {
      this.textHandler.keyReleased(keyCode, keyDataIdx);
   }

   void keyRepeated(int keyCode, int keyDataIdx) {
      this.textHandler.keyRepeated(keyCode, keyDataIdx);
   }

   void longLeftSoftKeyPressed() {
      this.textHandler.longLeftSoftKeyPressed();
   }

   boolean isT9modeAllowed() {
      return this.textHandler.isT9modeAllowed();
   }

   void paint(com.nokia.mid.impl.isa.ui.gdi.Graphics ng, int x, int y, int width, int height) {
      this.currentRemaining = this.getMaxSize() - this.size();
      if (this.isFocused() && this.currentRemaining != this.oldRemaining) {
         this.oldRemaining = this.currentRemaining;
         Displayable.uistyle.setIndex(this.owner.getDisplayable(), this.currentRemaining);
      }

      this.textHandler.paint(ng, x, y, width, height);
   }

   int getHeight(int width, int maxAvailableHeight) {
      return this.textHandler.getHeight(width, maxAvailableHeight);
   }

   void reconstructExtraCommands() {
      this.textHandler.reconstructExtraCommands();
   }

   boolean isFocused() {
      return this.isFocussed;
   }

   void setFocus(boolean focus) {
      if (this.isFocussed != focus) {
         this.isFocussed = focus;
         this.textHandler.setFocus(focus);
         this.reInitialize(false);
         if (this.owner != null && focus) {
            this.owner.processTextEditorEvent(16, 0);
         }

         if (!focus) {
            this.oldRemaining = -1;
            Displayable.uistyle.hideIndex(this.owner.getDisplayable());
         }
      }

   }

   void setSearchResultString(String newText) {
      if (newText != null && newText.length() > this.getMaxSize()) {
         newText = newText.substring(0, this.getMaxSize());
      }

      this.setString(newText);
      this.owner.changedItemState();
   }

   void showNotify() {
      this.isVisible = true;
      this.reInitialize(false);
      if (this.isFocussed) {
         this.textHandler.setActive(true);
      }

      this.textHandler.setVisible(true);
      if (this.owner != null && this.isFocussed) {
         this.oldRemaining = -1;
         if (this.optionsChangedProcessed) {
            this.optionsChangedProcessed = false;
         } else {
            this.owner.processTextEditorEvent(16, 0);
         }
      }

      this.addOrRemoveSearchCommand();
   }

   void hideNotify() {
      this.isVisible = false;
      this.reInitialize(false);
      this.textHandler.setVisible(false);
   }

   void processTextEditorEvent(int callbackType) {
      if (this.owner != null) {
         this.owner.processTextEditorEvent(callbackType, 0);
      }

   }

   void reInitialize(boolean goingtoBG) {
      InlineTextHandler newTextHandler = null;
      if (this.textHandler instanceof JavaTextHandler) {
         if (this.isVisible) {
            newTextHandler = new NativeTextHandler(this.textHandler);
         }
      } else if (this.textHandler instanceof NativeTextHandler && (goingtoBG || !this.isVisible && !this.isFocussed)) {
         newTextHandler = new JavaTextHandler(this.textHandler);
      }

      if (newTextHandler != null) {
         this.textHandler.destroy();
         this.textHandler = (InlineTextHandler)newTextHandler;
      }

   }

   void initiateCall() {
      this.textHandler.initiateCall();
   }

   boolean midletCommandsSupported() {
      return this.textHandler.midletCommandsSupported();
   }

   boolean launchExtraCommand(Command c) {
      if (c == searchCmd) {
         SearchScreen ss = SearchScreen.getSearchScreen();
         ss.setOwner(this.owner.getDisplayable());
         ss.setSearchResultConsumer(this);
         this.owner.getDisplayable().myDisplay.setCurrentInternal(this.owner.getDisplayable(), ss);
         return true;
      } else {
         return this.textHandler.launchExtraCommand(c);
      }
   }

   void setKeepRootOptionsMenu(boolean keepRootOptionsMenu) {
      this.owner.getDisplayable().myDisplay.getOptionsMenu().setKeepRootOptionsMenu(keepRootOptionsMenu);
   }

   Command[] getExtraCommands() {
      Command[] tecom = null;
      Command[] allcom = null;
      if (!this.canSearch) {
         return this.textHandler.getExtraCommands();
      } else {
         tecom = this.textHandler.getExtraCommands();
         if (tecom == null) {
            return new Command[]{searchCmd};
         } else {
            allcom = new Command[tecom.length + 1];
            allcom[0] = searchCmd;
            System.arraycopy(tecom, 0, allcom, 1, tecom.length);
            return allcom;
         }
      }
   }

   void addOrRemoveSearchCommand() {
      int constraintMode = this.getConstraints() & '\uffff';
      int editMask = this.getConstraints() & 131072;
      if ((constraintMode == 3 || constraintMode == 1) && editMask == 0 && this.size() == 0) {
         if (!this.canSearch) {
            this.canSearch = true;
            if (this.owner.hasFocus() && this.owner.getDisplayable() != null) {
               this.owner.getDisplayable().updateSoftkeys(true);
            }
         }
      } else if (this.canSearch) {
         this.canSearch = false;
         if (this.owner.hasFocus() && this.owner.getDisplayable() != null) {
            this.owner.getDisplayable().updateSoftkeys(true);
         }
      }

   }

   void setOptionsChangedProcessed(boolean optionsChangedProcessed) {
      this.optionsChangedProcessed = optionsChangedProcessed;
   }

   private void setString(String newText, int newCursorPosition) {
      if (newText == null) {
         newText = "";
      } else {
         if (newText.length() > this.textHandler.getMaxSize()) {
            throw new IllegalArgumentException();
         }

         if (!this.isValidString(newText)) {
            throw new IllegalArgumentException();
         }

         char decimalSeparator = TextDatabase.getText(49).charAt(0);
         if ((this.textHandler.getConstraints() & '\uffff') == 5 && decimalSeparator != '.') {
            newText = newText.replace('.', decimalSeparator);
         }
      }

      if (newCursorPosition > newText.length()) {
         newCursorPosition = newText.length();
      }

      this.textHandler.setString(newText, newCursorPosition);
      if (this.doSearchCommandCheck) {
         this.addOrRemoveSearchCommand();
      }

   }

   private void clearBuffer() {
      this.textHandler.setString("", 0);
   }

   private boolean isValidString(String string) {
      if (string != null && string.length() != 0) {
         try {
            int constraints = this.textHandler.getConstraints();
            switch(constraints & 65535) {
            case 2:
               Integer.parseInt(string);
               break;
            case 3:
               char[] text = string.toCharArray();

               for(int idx = 0; idx < text.length; ++idx) {
                  if ((text[idx] < '0' || text[idx] > '9') && text[idx] != '*' && text[idx] != '+' && text[idx] != 'p' && text[idx] != 'w' && text[idx] != '#') {
                     return false;
                  }
               }
            case 4:
            default:
               break;
            case 5:
               Double.parseDouble(string);
            }

            return true;
         } catch (NumberFormatException var5) {
            return false;
         }
      } else {
         return true;
      }
   }
}
