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
   private TextEditor textEditor;
   private int preferredHeight;

   public TextField(String var1, String var2, int var3, int var4) {
      super(var1);
      synchronized(Display.LCDUILock) {
         this.textEditor = new TextEditor(var2, var3, var4, new TextField.TextEditorOwnerImpl(this));
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

   void setOwner(Screen var1) {
      super.setOwner(var1);
      if (var1 == null) {
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

   int callMinimumHeight() {
      synchronized(Display.LCDUILock) {
         this.computePreferredHeight();
         return this.preferredHeight;
      }
   }

   int callMinimumWidth() {
      return TEXTFIELD_VALUE_ZONE.width;
   }

   void callPaint(Graphics var1, int var2, int var3, boolean var4) {
      synchronized(Display.LCDUILock) {
         super.callPaint(var1, var2, var3, var4);
         int var6 = var1.getTranslateX();
         int var7 = var1.getTranslateY();
         if (var7 <= var1.getHeight() && var7 + this.getHeight() >= 0) {
            com.nokia.mid.impl.isa.ui.gdi.Graphics var8 = var1.getImpl();
            ColorCtrl var9 = var8.getColorCtrl();
            int var10 = var9.getFgColor();
            int var11 = var9.getBgColor();
            int var12 = var6 + TEXTFIELD_VALUE_ZONE.x;
            int var13 = var7 + TEXTFIELD_VALUE_ZONE.y;
            Displayable.uistyle.drawBorder(var8, var12, var13, TEXTFIELD_VALUE_ZONE.width, this.getHeight(), TEXTFIELD_VALUE_ZONE.getBorderType(), var4);
            if (var4) {
               var9.setFgColor(UIStyle.COLOUR_EDITOR_FOCUSED);
               var9.setBgColor(UIStyle.COLOUR_SCHEME_HIGHLIGHT);
            } else {
               var9.setFgColor(UIStyle.COLOUR_EDITOR_UNFOCUSED);
               var9.setBgColor(UIStyle.COLOUR_BACKGROUND);
            }

            int var14 = var12 + TEXTFIELD_VALUE_ZONE.getMarginLeft();
            int var15 = var13 + TEXTFIELD_VALUE_ZONE.getMarginTop();
            int var16 = TEXTFIELD_VALUE_ZONE.width - TEXTFIELD_VALUE_ZONE.getMarginLeft() - TEXTFIELD_VALUE_ZONE.getMarginRight();
            int var17 = this.getHeight() - TEXTFIELD_VALUE_ZONE.getMarginTop() - TEXTFIELD_VALUE_ZONE.getMarginBottom();
            this.textEditor.paint(var8, var14, var15, var16, var17);
            var9.setFgColor(var10);
            var9.setBgColor(var11);
         }
      }
   }

   int callPreferredHeight(int var1) {
      synchronized(Display.LCDUILock) {
         this.computePreferredHeight();
         return this.preferredHeight;
      }
   }

   int callPreferredWidth(int var1) {
      return TEXTFIELD_VALUE_ZONE.width;
   }

   void callShowNotify() {
      super.callShowNotify();
      synchronized(Display.LCDUILock) {
         this.textEditor.showNotify();
      }
   }

   boolean callTraverse(int var1, int var2, int var3, int[] var4) {
      super.callTraverse(var1, var2, var3, var4);
      synchronized(Display.LCDUILock) {
         this.textEditor.setFocus(true);
         var4[0] = 0;
         var4[1] = 0;
         var4[2] = TEXTFIELD_VALUE_ZONE.width;
         var4[3] = this.preferredHeight + this.getLabelHeight(-1);
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

   boolean launchExtraCommand(Command var1) {
      return this.textEditor.launchExtraCommand(var1);
   }

   Command[] getExtraCommands() {
      return this.textEditor.getExtraCommands();
   }

   private int getHeight() {
      return this.preferredHeight;
   }

   private void computePreferredHeight() {
      this.preferredHeight = TEXTFIELD_VALUE_ZONE.getMarginTop() + TEXTFIELD_VALUE_ZONE.getMarginBottom();
      if (this.owner != null && this.owner instanceof Form) {
         Form var1 = (Form)this.owner;
         int var2 = var1.getViewPortHeight() - this.getLabelHeight(-1) - TEXTFIELD_VALUE_ZONE.getMarginTop() - TEXTFIELD_VALUE_ZONE.getMarginBottom();
         int var3 = TEXTFIELD_VALUE_ZONE.width - TEXTFIELD_VALUE_ZONE.getMarginLeft() - TEXTFIELD_VALUE_ZONE.getMarginRight();
         this.preferredHeight += this.textEditor.getHeight(var3, var2);
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

      TextEditorOwnerImpl(TextField var2) {
         this.textEditorOwner = var2;
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

      public void processTextEditorEvent(int var1, int var2) {
         if (TextField.this.owner != null) {
            if ((var1 & 1) != 0) {
               TextField.this.owner.changedItemState(this.textEditorOwner);
            }

            if ((var1 & 64) != 0) {
               TextField.this.repaint();
            }

            int var3;
            if ((var1 & 32) != 0) {
               var3 = TextField.this.preferredHeight;
               TextField.this.computePreferredHeight();
               if (var3 != TextField.this.preferredHeight) {
                  TextField.this.invalidate();
               }
            }

            if ((var1 & 16) != 0 && TextField.this.hasFocus) {
               TextField.this.textEditor.reconstructExtraCommands();
               TextField.this.owner.updateSoftkeys(true);
            }

            if (((var1 & 4) != 0 || (var1 & 8) != 0) && TextField.this.owner instanceof Form) {
               var3 = (var1 & 4) != 0 ? 1 : 6;
               Form var4 = (Form)TextField.this.owner;
               var4.formTraverse(var3);
            }

         }
      }
   }
}
