package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.TextDatabase;

class TextHandler {
   static int EDITING_CAPACITY = 900;
   static final char decimalSeperatorChar = TextDatabase.getText(49).charAt(0);
   char[] text = null;
   int textLen = 0;
   int constraints = 0;
   int cursorPosition = 0;
   String characterSubset = null;

   private static native void nativeStaticInitialiser();

   TextHandler(String var1, int var2, int var3) {
      int var4 = var2;
      if (var2 <= 0) {
         throw new IllegalArgumentException();
      } else {
         if (var2 > EDITING_CAPACITY) {
            var4 = EDITING_CAPACITY;
         }

         if (!this.validateConstraints(var3)) {
            throw new IllegalArgumentException();
         } else {
            this.text = new char[var4];
            if (var1 != null) {
               if (var1.length() > var4) {
                  throw new IllegalArgumentException();
               }

               this.textLen = var1.length();
               this.cursorPosition = this.textLen;
               System.arraycopy(var1.toCharArray(), 0, this.text, 0, this.textLen);
               if (!this.validateText(this.text, 0, this.textLen)) {
                  throw new IllegalArgumentException();
               }
            }

         }
      }
   }

   void delete(int var1, int var2) {
      if (var1 <= this.textLen - 1 && var1 + var2 <= this.textLen && var1 >= 0 && var2 >= 0) {
         System.arraycopy(this.text, var1 + var2, this.text, var1, this.textLen - (var1 + var2));
         this.textLen -= var2;
         this.cursorPosition = var1;
         this.arrayclear(this.textLen);
         int var3 = this.constraints & '\uffff';
         if (var3 == 5 && decimalSeperatorChar != '.') {
            for(int var4 = 0; var4 < this.textLen; ++var4) {
               if (this.text[var4] == decimalSeperatorChar) {
                  this.text[var4] = '.';
               }
            }
         }

         if (!this.validateText(this.text, 0, this.textLen)) {
            this.arrayclear(0);
            this.cursorPosition = 0;
            this.textLen = 0;
            throw new IllegalArgumentException();
         }
      } else {
         throw new StringIndexOutOfBoundsException();
      }
   }

   int getCaretPosition() {
      return this.cursorPosition;
   }

   int getChars(char[] var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1.length < this.textLen) {
         throw new ArrayIndexOutOfBoundsException();
      } else {
         System.arraycopy(this.text, 0, var1, 0, this.textLen);
         int var2 = this.constraints & '\uffff';
         if (var2 == 5 && decimalSeperatorChar != '.') {
            for(int var3 = 0; var3 < this.textLen; ++var3) {
               if (var1[var3] == decimalSeperatorChar) {
                  var1[var3] = '.';
               }
            }
         }

         return this.textLen;
      }
   }

   int getConstraints() {
      return this.constraints;
   }

   int getMaxSize() {
      return this.text.length;
   }

   String getString() {
      String var1 = "";
      if (this.textLen != 0) {
         var1 = new String(this.text, 0, this.textLen);
      }

      int var2 = this.constraints & '\uffff';
      if (var2 == 5 && decimalSeperatorChar != '.') {
         var1 = var1.replace(decimalSeperatorChar, '.');
      }

      return var1;
   }

   void insert(char[] var1, int var2, int var3, int var4) {
      Object var5 = null;
      int var6 = 0;
      int var7 = var2;
      int var8 = 0;
      int var9 = var4;
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 <= var1.length && var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
         if (this.textLen + var3 > this.text.length) {
            throw new IllegalArgumentException();
         } else {
            int var10 = this.constraints & '\uffff';
            if (var4 < 0) {
               var9 = 0;
            } else if (var4 > this.textLen) {
               var9 = this.textLen;
            }

            char[] var11;
            for(var11 = new char[this.textLen + var3]; var8 < var9; ++var8) {
               if (var10 == 5 && decimalSeperatorChar != '.' && this.text[var8] == decimalSeperatorChar) {
                  var11[var6] = '.';
               } else {
                  var11[var6] = this.text[var8];
               }

               ++var6;
            }

            while(var7 < var2 + var3) {
               var11[var6] = var1[var7];
               ++var7;
               ++var6;
            }

            while(var8 < this.textLen) {
               if (var10 == 5 && decimalSeperatorChar != '.' && this.text[var8] == decimalSeperatorChar) {
                  var11[var6] = '.';
               } else {
                  var11[var6] = this.text[var8];
               }

               ++var8;
               ++var6;
            }

            if (!this.validateText(var11, 0, var11.length)) {
               throw new IllegalArgumentException();
            } else {
               System.arraycopy(var11, 0, this.text, 0, var11.length);
               this.textLen = var11.length;
               this.cursorPosition = var9 + var3;
            }
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   void insert(String var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.insert(var1.toCharArray(), 0, var1.length(), var2);
      }
   }

   void setChars(char[] var1, int var2, int var3) {
      Object var4 = null;
      if (var1 == null) {
         this.arrayclear(0);
         this.cursorPosition = 0;
         this.textLen = 0;
      } else {
         if (var2 > var1.length || var2 < 0 || var3 < 0 || var2 + var3 > var1.length || var2 + var3 < 0) {
            throw new ArrayIndexOutOfBoundsException();
         }

         if (var3 > this.text.length) {
            throw new IllegalArgumentException();
         }

         char[] var7 = new char[var3];
         System.arraycopy(var1, var2, var7, 0, var3);
         if (!this.validateText(var7, 0, var7.length)) {
            throw new IllegalArgumentException();
         }

         this.arrayclear(0);
         System.arraycopy(var7, 0, this.text, 0, var7.length);
         this.textLen = var7.length;
         this.cursorPosition = this.textLen;
      }

   }

   void setConstraints(int var1) {
      if (!this.validateConstraints(var1)) {
         throw new IllegalArgumentException();
      } else {
         if (!this.validateText(this.text, 0, this.textLen)) {
            this.arrayclear(0);
            this.cursorPosition = 0;
            this.textLen = 0;
         }

      }
   }

   void setInitialInputMode(String var1) {
      this.characterSubset = var1;
   }

   int setMaxSize(int var1) {
      Object var2 = null;
      int var3 = var1;
      int var4 = this.textLen;
      if (var1 <= 0) {
         throw new IllegalArgumentException();
      } else {
         if (var1 != this.text.length) {
            if (var1 > EDITING_CAPACITY) {
               var3 = EDITING_CAPACITY;
            }

            char[] var5 = new char[var3];
            if (var3 < this.textLen) {
               var4 = var3;
            }

            System.arraycopy(this.text, 0, var5, 0, var4);
            this.text = var5;
            this.textLen = var4;
            if (!this.validateText(this.text, 0, this.textLen)) {
               throw new IllegalArgumentException();
            }

            if (var3 <= this.cursorPosition) {
               this.cursorPosition = var3;
            }
         }

         return var3;
      }
   }

   void setString(String var1) {
      if (var1 == null) {
         this.arrayclear(0);
         this.cursorPosition = 0;
         this.textLen = 0;
      } else {
         this.setChars(var1.toCharArray(), 0, var1.length());
      }

   }

   int size() {
      return this.textLen;
   }

   TextHandler clone() {
      TextHandler var1 = new TextHandler((String)null, this.text.length, this.constraints);
      System.arraycopy(this.text, 0, var1.text, 0, this.textLen);
      var1.textLen = this.textLen;
      var1.cursorPosition = this.cursorPosition;
      var1.characterSubset = this.characterSubset;
      return var1;
   }

   private boolean validateConstraints(int var1) {
      int var2 = var1 & '\uffff';
      if (var2 >= 0 && var2 <= 5) {
         this.constraints = var1;
         return true;
      } else {
         return false;
      }
   }

   private boolean validateText(char[] var1, int var2, int var3) {
      boolean var4 = true;
      if (var1 != null && var3 != 0) {
         int var5 = this.constraints & '\uffff';
         String var6;
         if (var5 == 2) {
            var6 = new String(var1, var2, var3);

            try {
               Integer.parseInt(var6);
            } catch (NumberFormatException var9) {
               var4 = false;
            }
         } else if (var5 == 5) {
            var6 = new String(var1, var2, var3);

            try {
               Double.parseDouble(var6);
            } catch (NumberFormatException var8) {
               var4 = false;
            }
         } else if (var5 == 3) {
            for(int var10 = var2; var10 < var2 + var3; ++var10) {
               if ((var1[var10] < '0' || var1[var10] > '9') && var1[var10] != '*' && var1[var10] != '+' && var1[var10] != 'p' && var1[var10] != 'w' && var1[var10] != '#') {
                  var4 = false;
                  break;
               }
            }
         }
      }

      return var4;
   }

   private void arrayclear(int var1) {
      if (var1 >= 0 && var1 < this.text.length) {
         while(var1 < this.text.length) {
            this.text[var1] = 0;
            ++var1;
         }
      }

   }

   static {
      nativeStaticInitialiser();
   }
}
