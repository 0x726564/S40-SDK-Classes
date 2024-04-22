package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;

public class TextBox extends Screen {
   private static final Zone textNormalZone;
   private static final Zone textTickerZone;
   private static final int EDITOR_TEXT_WRAP_FULL = 1;
   private TextEditor textEditor;

   public TextBox(String var1, String var2, int var3, int var4) {
      super(var1);
      synchronized(Display.LCDUILock) {
         this.textEditor = new TextEditor(var2, var3, var4, new TextBox.TextEditorOwnerImpl(this));
      }
   }

   public String getString() {
      synchronized(Display.LCDUILock) {
         return this.textEditor.getString();
      }
   }

   public void setString(String var1) {
      synchronized(Display.LCDUILock) {
         this.textEditor.setString(var1);
      }
   }

   public int getChars(char[] var1) {
      synchronized(Display.LCDUILock) {
         return this.textEditor.getChars(var1);
      }
   }

   public void setChars(char[] var1, int var2, int var3) {
      synchronized(Display.LCDUILock) {
         this.textEditor.setChars(var1, var2, var3);
      }
   }

   public void insert(String var1, int var2) {
      synchronized(Display.LCDUILock) {
         this.textEditor.insert(var1, var2);
      }
   }

   public void insert(char[] var1, int var2, int var3, int var4) {
      synchronized(Display.LCDUILock) {
         this.textEditor.insert(var1, var2, var3, var4);
      }
   }

   public void delete(int var1, int var2) {
      synchronized(Display.LCDUILock) {
         this.textEditor.delete(var1, var2);
      }
   }

   public int getMaxSize() {
      synchronized(Display.LCDUILock) {
         return this.textEditor.getMaxSize();
      }
   }

   public int setMaxSize(int var1) {
      synchronized(Display.LCDUILock) {
         return this.textEditor.setMaxSize(var1);
      }
   }

   public int size() {
      synchronized(Display.LCDUILock) {
         return this.textEditor.size();
      }
   }

   public int getCaretPosition() {
      synchronized(Display.LCDUILock) {
         return this.textEditor.getCursorPosition();
      }
   }

   public void setConstraints(int var1) {
      synchronized(Display.LCDUILock) {
         this.textEditor.setConstraints(var1);
      }
   }

   public int getConstraints() {
      synchronized(Display.LCDUILock) {
         return this.textEditor.getConstraints();
      }
   }

   public void setInitialInputMode(String var1) {
      synchronized(Display.LCDUILock) {
         this.textEditor.setInitialInputMode(var1);
      }
   }

   void callShowNotify(Display var1) {
      super.callShowNotify(var1);
      synchronized(Display.LCDUILock) {
         this.textEditor.showNotify();
         this.textEditor.setFocus(true);
      }
   }

   void callHideNotify(Display var1) {
      super.callHideNotify(var1);
      synchronized(Display.LCDUILock) {
         this.textEditor.hideNotify();
      }
   }

   void removedFromDisplayNotify(Display var1) {
      super.removedFromDisplayNotify(var1);
      synchronized(Display.LCDUILock) {
         this.textEditor.setFocus(false);
      }
   }

   void callKeyPressed(int var1, int var2) {
      super.callKeyPressed(var1, var2);
      synchronized(Display.LCDUILock) {
         this.textEditor.keyPressed(var1, var2);
      }
   }

   void callKeyReleased(int var1, int var2) {
      super.callKeyReleased(var1, var2);
      synchronized(Display.LCDUILock) {
         this.textEditor.keyReleased(var1, var2);
      }
   }

   void callKeyRepeated(int var1, int var2) {
      super.callKeyRepeated(var1, var2);
      synchronized(Display.LCDUILock) {
         this.textEditor.keyRepeated(var1, var2);
      }
   }

   void callInvalidate() {
      super.callInvalidate();
      synchronized(Display.LCDUILock) {
         if (!this.layoutValid) {
            this.layoutValid = true;
         }
      }

      this.repaintFull();
   }

   void callPaint(Graphics var1) {
      super.callPaint(var1);
      synchronized(Display.LCDUILock) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics var3 = var1.getImpl();
         Zone var4 = this.getMainZone();
         ColorCtrl var5 = var3.getColorCtrl();
         int var6 = var5.getFgColor();
         int var7 = var5.getBgColor();
         var5.setFgColor(UIStyle.COLOUR_WHITE);
         var5.setBgColor(UIStyle.COLOUR_WHITE);
         var3.fillRect(var4.x, var4.y, var4.width, var4.height);
         var5.setFgColor(var6);
         var5.setBgColor(var7);
         this.textEditor.paint(var3, var4.x + var4.getMarginLeft(), var4.y + var4.getMarginTop(), var4.width - var4.getMarginLeft() - var4.getMarginRight(), var4.height - var4.getMarginTop() - var4.getMarginBottom());
      }
   }

   Zone getMainZone() {
      return this.ticker != null ? textTickerZone : textNormalZone;
   }

   boolean midletCommandsSupported() {
      return this.textEditor.midletCommandsSupported();
   }

   boolean launchExtraCommand(Command var1) {
      return this.textEditor.launchExtraCommand(var1);
   }

   Command[] getExtraCommands() {
      return this.textEditor.getExtraCommands();
   }

   static {
      textNormalZone = uistyle.getZone(50);
      textTickerZone = uistyle.getZone(51);
   }

   class TextEditorOwnerImpl implements TextEditorOwner {
      private TextBox textEditorOwner;

      TextEditorOwnerImpl(TextBox var2) {
         this.textEditorOwner = var2;
      }

      public Displayable getDisplayable() {
         return this.textEditorOwner;
      }

      public boolean hasFocus() {
         return TextBox.this.textEditor.isFocused();
      }

      public void changedItemState() {
      }

      public int getCursorWrap() {
         return 1;
      }

      public void processTextEditorEvent(int var1, int var2) {
         if ((var1 & 64) != 0) {
            TextBox.this.repaintFull();
         }

         if ((var1 & 32) != 0) {
            TextBox.this.invalidate();
         }

         if ((var1 & 16) != 0 && TextBox.this.textEditor.isFocused()) {
            TextBox.this.textEditor.reconstructExtraCommands();
            TextBox.this.updateSoftkeys(true);
         }

      }
   }
}
