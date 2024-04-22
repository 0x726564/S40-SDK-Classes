package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.TextDatabase;

class TextEditor {
   private static char aR = TextDatabase.getText(49).charAt(0);
   private static final Command aS = new Command(11, 35);
   private InlineTextHandler aT;
   private TextEditorOwner aU;
   private boolean aV;
   private boolean aW;
   private boolean aX;
   private boolean aY;
   private boolean aZ;
   private int ba;
   private int bb = -1;

   TextEditor(String var1, int var2, int var3, TextEditorOwner var4) {
      this.aT = new JavaTextHandler(var2, var3, this);
      this.aU = var4;
      this.setConstraints(var3);
      this.setString(var1);
      this.setMaxSize(var2);
      this.aY = true;
   }

   final int getChars(char[] var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         String var3;
         int var2 = (var3 = this.getString()).length();
         if (var1.length < var2) {
            throw new ArrayIndexOutOfBoundsException();
         } else {
            var3.getChars(0, var2, var1, 0);
            return var2;
         }
      }
   }

   final void setChars(char[] var1, int var2, int var3) {
      if (var1 == null) {
         this.G();
      } else if (var2 <= var1.length && var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 > this.aT.getMaxSize()) {
            throw new IllegalArgumentException();
         } else if (var3 == 0) {
            this.G();
         } else {
            String var4 = new String(var1, var2, var3);
            if (!this.c(var4)) {
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
      int var2;
      if ((var2 = var1 & '\uffff') >= 0 && var2 <= 5) {
         this.aT.setConstraints(var1);
         if (!this.c(this.getString())) {
            this.G();
         }

         if (this.aY) {
            this.F();
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   final void insert(String var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.insert(var1.toCharArray(), 0, var1.length(), var2);
      }
   }

   final void insert(char[] var1, int var2, int var3, int var4) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length) {
         if (var3 != 0) {
            String var5;
            int var6;
            if ((var6 = (var5 = this.getString()).length()) + var3 > this.aT.getMaxSize()) {
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
               if (!this.c(var8)) {
                  throw new IllegalArgumentException();
               } else {
                  this.a(var8, var4 + var3);
               }
            }
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   final void delete(int var1, int var2) {
      if (var1 >= 0 && var2 >= 0) {
         String var3;
         int var4 = (var3 = this.getString()).length();
         if (var1 < var4 && var1 + var2 <= var4) {
            if (var2 != 0) {
               char[] var5 = new char[var4 - var2];
               var3.getChars(0, var1, var5, 0);
               var3.getChars(var1 + var2, var4, var5, var1);
               String var6 = new String(var5);
               if (!this.c(var6)) {
                  throw new IllegalArgumentException();
               } else {
                  this.a(var6, var1);
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
      return this.aT.getMaxSize();
   }

   final int setMaxSize(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException();
      } else {
         if (var1 > 5000) {
            var1 = 5000;
         }

         String var2 = this.getString();
         if (var1 < var2.length()) {
            var2 = var2.substring(0, var1);
            if (!this.c(var2)) {
               throw new IllegalArgumentException();
            }

            this.a(var2, this.getCursorPosition());
         }

         this.aT.setMaxSize(var1);
         return var1;
      }
   }

   final int size() {
      return this.aT.size();
   }

   int getCursorPosition() {
      return this.aT.getCursorPosition();
   }

   int getConstraints() {
      return this.aT.getConstraints();
   }

   void setInitialInputMode(String var1) {
      this.aT.setInitialInputMode(var1);
   }

   String getString() {
      String var1 = this.aT.getString();
      if ((this.aT.getConstraints() & '\uffff') == 5 && aR != '.') {
         var1 = var1.replace(aR, '.');
      }

      return var1;
   }

   void setString(String var1) {
      this.a(var1, this.aT.getCursorPosition());
   }

   int getCursorWrap() {
      return this.aU.getCursorWrap();
   }

   final void d(int var1, int var2) {
      this.aT.d(var1, var2);
   }

   final void e(int var1, int var2) {
      this.aT.e(var1, var2);
   }

   final void f(int var1, int var2) {
      this.aT.f(var1, var2);
   }

   final void a(com.nokia.mid.impl.isa.ui.gdi.Graphics var1, int var2, int var3, int var4, int var5) {
      this.ba = this.getMaxSize() - this.aT.size();
      if (this.aW && this.ba != this.bb) {
         this.bb = this.ba;
         Displayable.eI.setIndex(this.ba);
      }

      this.aT.a(var1, var2, var3, var4, var5);
   }

   final int a(int var1, int var2) {
      return this.aT.a(var1, var2);
   }

   final void l() {
      this.aT.l();
   }

   final boolean isFocused() {
      return this.aW;
   }

   void setFocus(boolean var1) {
      if (this.aW != var1) {
         this.aW = var1;
         this.aT.setFocus(var1);
         this.E();
         if (this.aU != null && var1) {
            this.aU.l(16);
         }

         if (!var1) {
            this.bb = -1;
            Displayable.eI.hideIndex();
         }
      }

   }

   void setSearchResultString(String var1) {
      if (var1 != null && var1.length() > this.getMaxSize()) {
         var1 = var1.substring(0, this.getMaxSize());
      }

      this.setString(var1);
      this.aU.I();
   }

   final void showNotify() {
      this.aV = true;
      this.E();
      if (this.aU != null && this.aW) {
         this.bb = -1;
         if (this.aZ) {
            this.aZ = false;
         } else {
            this.aU.l(16);
         }
      }

      this.F();
   }

   final void hideNotify() {
      this.aV = false;
      this.E();
   }

   final void k(int var1) {
      if (this.aU != null) {
         this.aU.l(var1);
      }

   }

   private void E() {
      Object var1 = null;
      if (this.aT instanceof JavaTextHandler) {
         if (this.aV || this.aW) {
            var1 = new NativeTextHandler(this.aT);
         }
      } else if (!this.aV && !this.aW) {
         var1 = new JavaTextHandler(this.aT);
      }

      if (var1 != null) {
         this.aT.destroy();
         this.aT = (InlineTextHandler)var1;
      }

   }

   final boolean m() {
      return this.aT.m();
   }

   final boolean a(Command var1) {
      if (var1 == aS) {
         SearchScreen var2;
         (var2 = SearchScreen.getSearchScreen()).setOwner(this.aU.getDisplayable());
         var2.setSearchResultConsumer(this);
         this.aU.getDisplayable().eV.c(this.aU.getDisplayable(), var2);
         return true;
      } else {
         return this.aT.a(var1);
      }
   }

   void setKeepRootOptionsMenu(boolean var1) {
      this.aU.getDisplayable().eV.getOptionsMenu().setKeepRootOptionsMenu(var1);
   }

   Command[] getExtraCommands() {
      Command[] var1 = null;
      Command[] var2 = null;
      if (!this.aX) {
         return this.aT.getExtraCommands();
      } else if ((var1 = this.aT.getExtraCommands()) == null) {
         return new Command[]{aS};
      } else {
         (var2 = new Command[var1.length + 1])[0] = aS;
         System.arraycopy(var1, 0, var2, 1, var1.length);
         return var2;
      }
   }

   final void F() {
      int var1 = this.getConstraints() & '\uffff';
      int var2 = this.getConstraints() & 131072;
      if ((var1 == 3 || var1 == 1) && var2 == 0 && this.aT.size() == 0) {
         if (!this.aX) {
            this.aX = true;
            if (this.aU.hasFocus() && this.aU.getDisplayable() != null) {
               this.aU.getDisplayable().c(true);
               return;
            }
         }
      } else if (this.aX) {
         this.aX = false;
         if (this.aU.hasFocus() && this.aU.getDisplayable() != null) {
            this.aU.getDisplayable().c(true);
         }
      }

   }

   void setOptionsChangedProcessed(boolean var1) {
      this.aZ = var1;
   }

   private void a(String var1, int var2) {
      if (var1 == null) {
         var1 = "";
      } else {
         if (var1.length() > this.aT.getMaxSize()) {
            throw new IllegalArgumentException();
         }

         if (!this.c(var1)) {
            throw new IllegalArgumentException();
         }

         if ((this.aT.getConstraints() & '\uffff') == 5 && aR != '.') {
            var1 = var1.replace('.', aR);
         }
      }

      if (var2 > var1.length()) {
         var2 = var1.length();
      }

      this.aT.a(var1, var2);
      if (this.aY) {
         this.F();
      }

   }

   private void G() {
      this.aT.a("", 0);
   }

   private boolean c(String var1) {
      if (var1 != null && var1.length() != 0) {
         try {
            switch(this.aT.getConstraints() & 65535) {
            case 2:
               Integer.parseInt(var1);
               break;
            case 3:
               char[] var4 = var1.toCharArray();

               for(int var2 = 0; var2 < var4.length; ++var2) {
                  if ((var4[var2] < '0' || var4[var2] > '9') && var4[var2] != '*' && var4[var2] != '+' && var4[var2] != 'p' && var4[var2] != 'w' && var4[var2] != '#') {
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
         } catch (NumberFormatException var3) {
            return false;
         }
      } else {
         return true;
      }
   }
}
