package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;

public class TextBox extends Screen {
   private static final Zone textNormalZone;
   private static final Zone textTickerZone;
   private static final int EDITOR_TEXT_WRAP_FULL = 1;
   private TextEditor textEditor;

   public TextBox(String title, String text, int maxSize, int constraints) {
      super(title);
      synchronized(Display.LCDUILock) {
         this.textEditor = new TextEditor(text, maxSize, constraints, new TextBox.TextEditorOwnerImpl(this));
      }
   }

   public String getString() {
      synchronized(Display.LCDUILock) {
         return this.textEditor.getString();
      }
   }

   public void setString(String text) {
      synchronized(Display.LCDUILock) {
         this.textEditor.setString(text);
      }
   }

   public int getChars(char[] data) {
      synchronized(Display.LCDUILock) {
         return this.textEditor.getChars(data);
      }
   }

   public void setChars(char[] data, int offset, int length) {
      synchronized(Display.LCDUILock) {
         this.textEditor.setChars(data, offset, length);
      }
   }

   public void insert(String src, int position) {
      synchronized(Display.LCDUILock) {
         this.textEditor.insert(src, position);
      }
   }

   public void insert(char[] data, int offset, int length, int position) {
      synchronized(Display.LCDUILock) {
         this.textEditor.insert(data, offset, length, position);
      }
   }

   public void delete(int offset, int length) {
      synchronized(Display.LCDUILock) {
         this.textEditor.delete(offset, length);
      }
   }

   public int getMaxSize() {
      synchronized(Display.LCDUILock) {
         return this.textEditor.getMaxSize();
      }
   }

   public int setMaxSize(int maxSize) {
      synchronized(Display.LCDUILock) {
         return this.textEditor.setMaxSize(maxSize);
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

   public void setConstraints(int constraints) {
      synchronized(Display.LCDUILock) {
         this.textEditor.setConstraints(constraints);
      }
   }

   public int getConstraints() {
      synchronized(Display.LCDUILock) {
         return this.textEditor.getConstraints();
      }
   }

   public void setInitialInputMode(String characterSubset) {
      synchronized(Display.LCDUILock) {
         this.textEditor.setInitialInputMode(characterSubset);
      }
   }

   void callShowNotify(Display d) {
      super.callShowNotify(d);
      synchronized(Display.LCDUILock) {
         this.textEditor.setOptionsChangedProcessed(true);
         this.textEditor.showNotify();
         this.textEditor.setFocus(true);
      }
   }

   void callHideNotify(Display d) {
      super.callHideNotify(d);
      synchronized(Display.LCDUILock) {
         this.textEditor.hideNotify();
      }
   }

   void removedFromDisplayNotify(Display d) {
      super.removedFromDisplayNotify(d);
      synchronized(Display.LCDUILock) {
         this.textEditor.setFocus(false);
      }
   }

   void callKeyPressed(int keyCode, int keyDataIdx) {
      super.callKeyPressed(keyCode, keyDataIdx);
      synchronized(Display.LCDUILock) {
         this.textEditor.keyPressed(keyCode, keyDataIdx);
      }
   }

   void callKeyReleased(int keyCode, int keyDataIdx) {
      super.callKeyReleased(keyCode, keyDataIdx);
      synchronized(Display.LCDUILock) {
         this.textEditor.keyReleased(keyCode, keyDataIdx);
      }
   }

   void callKeyRepeated(int keyCode, int keyDataIdx) {
      super.callKeyRepeated(keyCode, keyDataIdx);
      synchronized(Display.LCDUILock) {
         this.textEditor.keyRepeated(keyCode, keyDataIdx);
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

   void callPaint(Graphics g) {
      super.callPaint(g);
      synchronized(Display.LCDUILock) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
         Zone zone = this.getMainZone();
         ColorCtrl color_ctrl = ng.getColorCtrl();
         int orig_fg_colour = color_ctrl.getFgColor();
         int orig_bg_colour = color_ctrl.getBgColor();
         color_ctrl.setFgColor(UIStyle.COLOUR_WHITE);
         color_ctrl.setBgColor(UIStyle.COLOUR_WHITE);
         ng.fillRect(zone.x, zone.y, zone.width, zone.height);
         color_ctrl.setFgColor(orig_fg_colour);
         color_ctrl.setBgColor(orig_bg_colour);
         this.textEditor.paint(ng, zone.x + zone.getMarginLeft(), zone.y + zone.getMarginTop(), zone.width - zone.getMarginLeft() - zone.getMarginRight(), zone.height - zone.getMarginTop() - zone.getMarginBottom());
      }
   }

   Zone getMainZone() {
      return this.ticker != null ? textTickerZone : textNormalZone;
   }

   boolean midletCommandsSupported() {
      return this.textEditor.midletCommandsSupported();
   }

   boolean launchExtraCommand(Command c) {
      return this.textEditor.launchExtraCommand(c);
   }

   Command[] getExtraCommands() {
      return this.textEditor.getExtraCommands();
   }

   static {
      textNormalZone = uistyle.getZone(51);
      textTickerZone = uistyle.getZone(52);
   }

   class TextEditorOwnerImpl implements TextEditorOwner {
      private TextBox textEditorOwner;

      TextEditorOwnerImpl(TextBox textEditorOwner) {
         this.textEditorOwner = textEditorOwner;
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

      public void processTextEditorEvent(int eventType, int eventParam) {
         if ((eventType & 1024) != 0) {
            TextBox.this.closeOptionsMenu();
         }

         if ((eventType & 1) != 0) {
            TextBox.this.textEditor.addOrRemoveSearchCommand();
         }

         if ((eventType & 64) != 0) {
            TextBox.this.repaintFull();
         }

         if ((eventType & 32) != 0) {
            TextBox.this.invalidate();
         }

         if ((eventType & 16) != 0 && TextBox.this.textEditor.isFocused()) {
            TextBox.this.textEditor.reconstructExtraCommands();
            TextBox.this.updateSoftkeys(true);
         }

      }
   }
}
