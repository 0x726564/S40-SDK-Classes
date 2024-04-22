package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.TextDatabase;

class TextEditor {
   static final char DECIMAL_SEPERATOR_CHAR = TextDatabase.getText(49).charAt(0);
   static final int MAX_TEXT_SIZE = 5000;
   private static final Command searchCmd = new Command(11, 35);
   private InlineTextHandler textHandler;
   private TextEditorOwner owner;
   private boolean isVisible;
   private boolean isFocussed;
   private boolean canSearch;
   private boolean doSearchCommandCheck;

   TextEditor(String var1, int var2, int var3, TextEditorOwner var4) {
      this.textHandler = new JavaTextHandler(var2, var3, this);
      this.owner = var4;
      this.setConstraints(var3);
      this.setString(var1);
      this.setMaxSize(var2);
      this.doSearchCommandCheck = true;
   }

   int getChars(char[] var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         String var2 = this.getString();
         int var3 = var2.length();
         if (var1.length < var3) {
            throw new ArrayIndexOutOfBoundsException();
         } else {
            var2.getChars(0, var3, var1, 0);
            return var3;
         }
      }
   }

   void setChars(char[] var1, int var2, int var3) {
      if (var1 == null) {
         this.clearBuffer();
      } else if (var2 <= var1.length && var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 > this.textHandler.getMaxSize()) {
            throw new IllegalArgumentException();
         } else if (var3 == 0) {
            this.clearBuffer();
         } else {
            String var4 = new String(var1, var2, var3);
            if (!this.isValidString(var4)) {
               throw new IllegalArgumentException();
            } else {
               this.setString(var4);
            }
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   void setConstraints(int var1) {
      int var2 = var1 & '\uffff';
      if (var2 >= 0 && var2 <= 5) {
         this.textHandler.setConstraints(var1);
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

   void insert(String var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.insert(var1.toCharArray(), 0, var1.length(), var2);
      }
   }

   void insert(char[] var1, int var2, int var3, int var4) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length) {
         if (var3 != 0) {
            String var5 = this.getString();
            int var6 = var5.length();
            if (var6 + var3 > this.textHandler.getMaxSize()) {
               throw new IllegalArgumentException();
            } else {
               if (var4 < 0) {
                  var4 = 0;
               } else if (var4 > var6) {
                  var4 = var6;
               }

               char[] var7 = new char[var6 + var3];
               var5.getChars(0, var4, var7, 0);
               System.arraycopy(var1, var2, var7, var4, var3);
               var5.getChars(var4, var6, var7, var4 + var3);
               String var8 = new String(var7);
               if (!this.isValidString(var8)) {
                  throw new IllegalArgumentException();
               } else {
                  this.setString(var8, var4 + var3);
               }
            }
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   void delete(int var1, int var2) {
      if (var1 >= 0 && var2 >= 0) {
         String var3 = this.getString();
         int var4 = var3.length();
         if (var1 < var4 && var1 + var2 <= var4) {
            if (var2 != 0) {
               char[] var5 = new char[var4 - var2];
               var3.getChars(0, var1, var5, 0);
               var3.getChars(var1 + var2, var4, var5, var1);
               String var6 = new String(var5);
               if (!this.isValidString(var6)) {
                  throw new IllegalArgumentException();
               } else {
                  this.setString(var6, var1);
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

   int setMaxSize(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException();
      } else {
         if (var1 > 5000) {
            var1 = 5000;
         }

         String var2 = this.getString();
         if (var1 < var2.length()) {
            var2 = var2.substring(0, var1);
            if (!this.isValidString(var2)) {
               throw new IllegalArgumentException();
            }

            int var3 = this.getCursorPosition();
            if (var1 > var3) {
               var3 = 0;
            }

            this.setString(var2, var3);
         }

         this.textHandler.setMaxSize(var1);
         return var1;
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

   int getCursorWrap() {
      return this.owner.getCursorWrap();
   }

   void setInitialInputMode(String var1) {
      this.textHandler.setInitialInputMode(var1);
   }

   void keyPressed(int var1, int var2) {
      this.textHandler.keyPressed(var1, var2);
   }

   void keyReleased(int var1, int var2) {
      this.textHandler.keyReleased(var1, var2);
   }

   void keyRepeated(int var1, int var2) {
      this.textHandler.keyRepeated(var1, var2);
   }

   void paint(com.nokia.mid.impl.isa.ui.gdi.Graphics var1, int var2, int var3, int var4, int var5) {
      this.textHandler.paint(var1, var2, var3, var4, var5);
   }

   int getHeight(int var1, int var2) {
      return this.textHandler.getHeight(var1, var2);
   }

   void reconstructExtraCommands() {
      this.textHandler.reconstructExtraCommands();
   }

   boolean isFocused() {
      return this.isFocussed;
   }

   void setFocus(boolean var1) {
      if (this.isFocussed != var1) {
         this.isFocussed = var1;
         this.textHandler.setFocus(var1);
         this.reInitialize();
         if (this.owner != null && var1) {
            this.owner.processTextEditorEvent(16, 0);
         }
      }

   }

   String getString() {
      String var1 = this.textHandler.getString();
      if ((this.textHandler.getConstraints() & '\uffff') == 5 && DECIMAL_SEPERATOR_CHAR != '.') {
         var1 = var1.replace(DECIMAL_SEPERATOR_CHAR, '.');
      }

      return var1;
   }

   void setString(String var1) {
      this.setString(var1, this.textHandler.getCursorPosition());
   }

   void setSearchResultString(String var1) {
      this.setString(var1);
      this.owner.changedItemState();
   }

   void showNotify() {
      this.isVisible = true;
      this.reInitialize();
      if (this.owner != null && this.isFocussed) {
         this.owner.processTextEditorEvent(16, 0);
      }

      this.addOrRemoveSearchCommand();
   }

   void hideNotify() {
      this.isVisible = false;
      this.reInitialize();
   }

   void processTextEditorEvent(int var1) {
      if (this.owner != null) {
         if ((var1 & 1) != 0) {
            this.addOrRemoveSearchCommand();
         }

         this.owner.processTextEditorEvent(var1, 0);
      }

   }

   void reInitialize() {
      Object var1 = null;
      if (this.textHandler instanceof JavaTextHandler) {
         if (this.isVisible || this.isFocussed) {
            var1 = new NativeTextHandler(this.textHandler);
         }
      } else if (!this.isVisible && !this.isFocussed) {
         var1 = new JavaTextHandler(this.textHandler);
      }

      if (var1 != null) {
         this.textHandler.destroy();
         this.textHandler = (InlineTextHandler)var1;
      }

   }

   void initiateCall() {
      this.textHandler.initiateCall();
   }

   boolean midletCommandsSupported() {
      return this.textHandler.midletCommandsSupported();
   }

   boolean launchExtraCommand(Command var1) {
      if (var1 == searchCmd) {
         SearchScreen var2 = SearchScreen.getSearchScreen();
         var2.setOwner(this.owner.getDisplayable());
         var2.setSearchResultConsumer(this);
         this.owner.getDisplayable().myDisplay.setCurrentInternal(this.owner.getDisplayable(), var2);
         return true;
      } else {
         return this.textHandler.launchExtraCommand(var1);
      }
   }

   Command[] getExtraCommands() {
      Command[] var1 = null;
      Command[] var2 = null;
      if (!this.canSearch) {
         return this.textHandler.getExtraCommands();
      } else {
         var1 = this.textHandler.getExtraCommands();
         if (var1 == null) {
            return new Command[]{searchCmd};
         } else {
            var2 = new Command[var1.length + 1];
            var2[0] = searchCmd;
            System.arraycopy(var1, 0, var2, 1, var1.length);
            return var2;
         }
      }
   }

   void addOrRemoveSearchCommand() {
      int var1 = this.getConstraints() & '\uffff';
      int var2 = this.getConstraints() & 131072;
      if ((var1 == 3 || var1 == 1) && var2 == 0 && this.size() == 0) {
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

   private void setString(String var1, int var2) {
      if (var1 == null) {
         var1 = "";
      } else {
         if (var1.length() > this.textHandler.getMaxSize()) {
            throw new IllegalArgumentException();
         }

         if (!this.isValidString(var1)) {
            throw new IllegalArgumentException();
         }

         if ((this.textHandler.getConstraints() & '\uffff') == 5 && DECIMAL_SEPERATOR_CHAR != '.') {
            var1 = var1.replace('.', DECIMAL_SEPERATOR_CHAR);
         }
      }

      if (var2 > var1.length()) {
         var2 = var1.length();
      }

      this.textHandler.setString(var1, var2);
      if (this.doSearchCommandCheck) {
         this.addOrRemoveSearchCommand();
      }

   }

   private void clearBuffer() {
      this.textHandler.setString("", 0);
   }

   private boolean isValidString(String var1) {
      if (var1 != null && var1.length() != 0) {
         try {
            int var2 = this.textHandler.getConstraints();
            switch(var2 & 65535) {
            case 2:
               Integer.parseInt(var1);
               break;
            case 3:
               char[] var3 = var1.toCharArray();

               for(int var4 = 0; var4 < var3.length; ++var4) {
                  if ((var3[var4] < '0' || var3[var4] > '9') && var3[var4] != '*' && var3[var4] != '+' && var3[var4] != 'p' && var3[var4] != 'w' && var3[var4] != '#') {
                     return false;
                  }
               }
            case 4:
            default:
               break;
            case 5:
               Double.parseDouble(var1);
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
