package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;

public class TextField extends Item {
   public static final int ANY = 0;
   public static final int EMAILADDR = 1;
   public static final int NUMERIC = 2;
   public static final int PHONENUMBER = 3;
   public static final int URL = 4;
   public static final int DECIMAL = 5;
   public static final int PASSWORD = 65536;
   public static final int UNEDITABLE = 131072;
   public static final int SENSITIVE = 262144;
   public static final int NON_PREDICTIVE = 524288;
   public static final int INITIAL_CAPS_WORD = 1048576;
   public static final int INITIAL_CAPS_SENTENCE = 2097152;
   public static final int CONSTRAINT_MASK = 65535;
   private static Zone TEXTFIELD_VALUE_ZONE;
   private static final int EDITOR_TEXT_WRAP_LEFTRIGHT = 2;
   TextEditor textEditor;
   int preferredHeight;

   public TextField(String title, String text, int maxSize, int constraints) {
      super(title);
      synchronized(Display.LCDUILock) {
         this.textEditor = new TextEditor(text, maxSize, constraints, new TextField.TextEditorOwnerImpl(this));
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

   void setOwner(Screen owner) {
      super.setOwner(owner);
      if (owner == null) {
         this.textEditor.setFocus(false);
         this.textEditor.hideNotify();
      }

   }

   void callHideNotify() {
      super.callHideNotify();
      synchronized(Display.LCDUILock) {
         this.textEditor.hideNotify();
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

   int callMinimumHeight() {
      synchronized(Display.LCDUILock) {
         this.computePreferredHeight();
         return this.preferredHeight;
      }
   }

   int callMinimumWidth() {
      return TEXTFIELD_VALUE_ZONE.width;
   }

   void callPaint(Graphics g, int w, int h, boolean isFocused) {
      synchronized(Display.LCDUILock) {
         super.callPaint(g, w, h, isFocused);
         int posX = g.getTranslateX();
         int posY = g.getTranslateY();
         if (posY <= g.getHeight() && posY + this.getHeight() >= 0) {
            com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
            ColorCtrl color_ctrl = ng.getColorCtrl();
            int orig_fg_colour = color_ctrl.getFgColor();
            int orig_bg_colour = color_ctrl.getBgColor();
            int offsetX = posX + TEXTFIELD_VALUE_ZONE.x;
            int offsetY = posY + TEXTFIELD_VALUE_ZONE.y;
            Displayable.uistyle.drawBorder(ng, offsetX, offsetY, TEXTFIELD_VALUE_ZONE.width, this.getHeight(), TEXTFIELD_VALUE_ZONE.getBorderType(), isFocused);
            if (isFocused) {
               color_ctrl.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
               color_ctrl.setBgColor(UIStyle.COLOUR_SCHEME_HIGHLIGHT);
            } else {
               color_ctrl.setFgColor(UIStyle.COLOUR_EDITOR_UNFOCUSED);
               color_ctrl.setBgColor(UIStyle.COLOUR_BACKGROUND);
            }

            int textRenderStartX = offsetX + TEXTFIELD_VALUE_ZONE.getMarginLeft();
            int textRenderStartY = offsetY + TEXTFIELD_VALUE_ZONE.getMarginTop();
            int textRenderWidth = TEXTFIELD_VALUE_ZONE.width - TEXTFIELD_VALUE_ZONE.getMarginLeft() - TEXTFIELD_VALUE_ZONE.getMarginRight();
            int textRenderHeight = this.getHeight() - TEXTFIELD_VALUE_ZONE.getMarginTop() - TEXTFIELD_VALUE_ZONE.getMarginBottom();
            this.textEditor.paint(ng, textRenderStartX, textRenderStartY, textRenderWidth, textRenderHeight);
            color_ctrl.setFgColor(orig_fg_colour);
            color_ctrl.setBgColor(orig_bg_colour);
         }
      }
   }

   int callPreferredHeight(int w) {
      synchronized(Display.LCDUILock) {
         this.computePreferredHeight();
         return this.preferredHeight;
      }
   }

   int callPreferredWidth(int h) {
      return TEXTFIELD_VALUE_ZONE.width;
   }

   void callShowNotify() {
      super.callShowNotify();
      synchronized(Display.LCDUILock) {
         this.textEditor.showNotify();
      }
   }

   boolean callTraverse(int dir, int viewportWidth, int viewportHeight, int[] visRect_inout) {
      super.callTraverse(dir, viewportWidth, viewportHeight, visRect_inout);
      synchronized(Display.LCDUILock) {
         this.textEditor.setFocus(true);
         visRect_inout[0] = 0;
         visRect_inout[1] = 0;
         visRect_inout[2] = TEXTFIELD_VALUE_ZONE.width;
         visRect_inout[3] = this.preferredHeight + this.getLabelHeight(-1);
         return true;
      }
   }

   void callTraverseOut() {
      super.callTraverseOut();
      synchronized(Display.LCDUILock) {
         this.textEditor.setFocus(false);
      }
   }

   boolean equateNLA() {
      return true;
   }

   boolean equateNLB() {
      return true;
   }

   boolean isFocusable() {
      return true;
   }

   boolean supportsInternalTraversal() {
      return true;
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

   private int getHeight() {
      return this.preferredHeight;
   }

   void computePreferredHeight() {
      this.preferredHeight = TEXTFIELD_VALUE_ZONE.getMarginTop() + TEXTFIELD_VALUE_ZONE.getMarginBottom();
      if (this.owner != null && this.owner instanceof Form) {
         Form form = (Form)this.owner;
         int availableHeight = form.getViewPortHeight() - this.getLabelHeight(-1) - TEXTFIELD_VALUE_ZONE.getMarginTop() - TEXTFIELD_VALUE_ZONE.getMarginBottom();
         int availableWidth = TEXTFIELD_VALUE_ZONE.width - TEXTFIELD_VALUE_ZONE.getMarginLeft() - TEXTFIELD_VALUE_ZONE.getMarginRight();
         this.preferredHeight += this.textEditor.getHeight(availableWidth, availableHeight);
      }

      if (this.preferredHeight < TEXTFIELD_VALUE_ZONE.height) {
         this.preferredHeight = TEXTFIELD_VALUE_ZONE.height;
      }

   }

   static {
      TEXTFIELD_VALUE_ZONE = Item.ITEM_VALUE_ZONE;
   }

   class TextEditorOwnerImpl implements TextEditorOwner {
      private TextField textEditorOwner;

      TextEditorOwnerImpl(TextField textEditorOwner) {
         this.textEditorOwner = textEditorOwner;
      }

      public Displayable getDisplayable() {
         return TextField.this.owner;
      }

      public boolean hasFocus() {
         return TextField.this.hasFocus;
      }

      public void changedItemState() {
         if (TextField.this.owner != null) {
            TextField.this.owner.changedItemState(this.textEditorOwner);
         }

      }

      public int getCursorWrap() {
         return 2;
      }

      public void processTextEditorEvent(int eventType, int eventParam) {
         if (TextField.this.owner != null) {
            if ((eventType & 1024) != 0) {
               TextField.this.owner.closeOptionsMenu();
            }

            if ((eventType & 1) != 0) {
               TextField.this.textEditor.addOrRemoveSearchCommand();
               TextField.this.owner.changedItemState(this.textEditorOwner);
            }

            if ((eventType & 64) != 0) {
               TextField.this.repaint();
            }

            int directionOfTraversal;
            if ((eventType & 32) != 0) {
               directionOfTraversal = TextField.this.preferredHeight;
               TextField.this.computePreferredHeight();
               if (directionOfTraversal != TextField.this.preferredHeight) {
                  TextField.this.invalidate();
               }
            }

            if ((eventType & 16) != 0 && TextField.this.hasFocus) {
               TextField.this.textEditor.reconstructExtraCommands();
               TextField.this.owner.updateSoftkeys(true);
            }

            if (((eventType & 4) != 0 || (eventType & 8) != 0) && TextField.this.owner instanceof Form) {
               directionOfTraversal = (eventType & 4) != 0 ? 1 : 6;
               Form form = (Form)TextField.this.owner;
               form.formTraverse(directionOfTraversal);
            }

         }
      }
   }
}
