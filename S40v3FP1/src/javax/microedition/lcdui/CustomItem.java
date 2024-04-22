package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;

public abstract class CustomItem extends Item {
   protected static final int TRAVERSE_HORIZONTAL = 1;
   protected static final int TRAVERSE_VERTICAL = 2;
   protected static final int KEY_PRESS = 4;
   protected static final int KEY_RELEASE = 8;
   protected static final int KEY_REPEAT = 16;
   protected static final int POINTER_PRESS = 32;
   protected static final int POINTER_RELEASE = 64;
   protected static final int POINTER_DRAG = 128;
   protected static final int NONE = 0;
   private static final int CUSTOM_BORDER_PAD;
   private static final Command DETAILS_COMMAND;
   static final Command[] extraCommands;
   private int labelHeight = 0;
   private ImageItem exceptionImageItem = null;
   private String textAlert = null;
   private boolean isTraversableInitialized = false;
   private boolean isTraversable = false;
   private static final boolean JMS_BLENDED_HIGHLIGHT_SUPPORT;

   protected CustomItem(String var1) {
      super(var1);
   }

   boolean supportHorizontalScrolling() {
      return Form.UNICOM_FORM_SCROLLING;
   }

   public int getGameAction(int var1) {
      int var2 = Displayable.keyMap.getGameAction(var1);
      if (-127 == var2) {
         throw new IllegalArgumentException("getGameAction: Invalid keyCode");
      } else {
         return var2;
      }
   }

   protected final int getInteractionModes() {
      if (!Form.IS_FOUR_WAY_SCROLL) {
         return UIStyle.isRotator() ? 2 : 30;
      } else {
         return 31;
      }
   }

   protected abstract int getMinContentWidth();

   protected abstract int getMinContentHeight();

   protected abstract int getPrefContentWidth(int var1);

   protected abstract int getPrefContentHeight(int var1);

   protected void sizeChanged(int var1, int var2) {
   }

   protected final void invalidate() {
      super.invalidate();
   }

   protected abstract void paint(Graphics var1, int var2, int var3);

   protected final void repaint() {
      if (this.exceptionImageItem != null) {
         this.exceptionImageItem.repaint();
      } else {
         super.repaint();
      }
   }

   protected final void repaint(int var1, int var2, int var3, int var4) {
      if (this.owner != null && this.bounds != null) {
         if (this.exceptionImageItem != null) {
            this.exceptionImageItem.repaint(var1, var2, var3, var4);
         } else if (var1 <= this.bounds[2]) {
            var1 = var1 < 0 ? 0 : var1;
            var2 = var2 < 0 ? 0 : var2;
            var2 += this.labelHeight;
            if (var2 <= this.bounds[3]) {
               var3 = var1 + var3 > this.bounds[2] ? this.bounds[2] - var1 : var3;
               var4 = var2 + var4 > this.bounds[3] ? this.bounds[3] - var2 : var4;
               super.repaint(var1, var2, var3, var4);
            }
         }
      }
   }

   protected boolean traverse(int var1, int var2, int var3, int[] var4) {
      return false;
   }

   protected void traverseOut() {
   }

   protected void keyPressed(int var1) {
   }

   protected void keyReleased(int var1) {
   }

   protected void keyRepeated(int var1) {
   }

   protected void pointerPressed(int var1, int var2) {
   }

   protected void pointerReleased(int var1, int var2) {
   }

   protected void pointerDragged(int var1, int var2) {
   }

   protected void showNotify() {
   }

   protected void hideNotify() {
   }

   int callPreferredWidth(int var1) {
      if (this.exceptionImageItem != null) {
         return this.exceptionImageItem.callPreferredWidth(var1) + 2 * CUSTOM_BORDER_PAD;
      } else {
         try {
            boolean var2;
            int var3;
            synchronized(Display.calloutLock) {
               var3 = this.getPrefContentWidth(var1);
               var2 = var3 == 0 || this.getPrefContentHeight(this.contentWidth) == 0;
            }

            synchronized(Display.LCDUILock) {
               return var2 && this.itemCommands.length() >= 1 ? this.getEmptyStringWidth((Font)null) + 2 * CUSTOM_BORDER_PAD : var3 + 2 * CUSTOM_BORDER_PAD;
            }
         } catch (Throwable var9) {
            this.handleThrowable(var9);
            return -1;
         }
      }
   }

   int callPreferredHeight(int var1) {
      if (this.exceptionImageItem != null) {
         return this.exceptionImageItem.callPreferredHeight(var1) + 2 * CUSTOM_BORDER_PAD;
      } else {
         try {
            boolean var2;
            int var3;
            synchronized(Display.calloutLock) {
               var3 = this.getPrefContentHeight(var1);
               var2 = var3 == 0 || this.getPrefContentWidth(var3) == 0;
            }

            synchronized(Display.LCDUILock) {
               return var2 && this.itemCommands.length() >= 1 ? this.getEmptyStringHeight(DEFAULT_WIDTH, (Font)null) + 2 * CUSTOM_BORDER_PAD : var3 + 2 * CUSTOM_BORDER_PAD;
            }
         } catch (Throwable var9) {
            this.handleThrowable(var9);
            return -1;
         }
      }
   }

   int callMinimumWidth() {
      if (this.exceptionImageItem != null) {
         return this.exceptionImageItem.callMinimumWidth() + 2 * CUSTOM_BORDER_PAD;
      } else {
         try {
            boolean var1;
            int var2;
            synchronized(Display.calloutLock) {
               var2 = this.getMinContentWidth();
               var1 = var2 == 0 || this.getPrefContentHeight(this.contentWidth) == 0;
            }

            synchronized(Display.LCDUILock) {
               var2 = var1 && this.itemCommands.length() >= 1 ? this.getEmptyStringWidth((Font)null) + 2 * CUSTOM_BORDER_PAD : var2 + 2 * CUSTOM_BORDER_PAD;
               return this.itemLabel != null && var2 < Item.MIN_LABEL_WIDTH ? Item.MIN_LABEL_WIDTH : var2;
            }
         } catch (Throwable var8) {
            this.handleThrowable(var8);
            return -1;
         }
      }
   }

   int callMinimumHeight() {
      if (this.exceptionImageItem != null) {
         return this.exceptionImageItem.callMinimumHeight() + 2 * CUSTOM_BORDER_PAD;
      } else {
         try {
            boolean var1;
            int var2;
            synchronized(Display.calloutLock) {
               var2 = this.getMinContentHeight();
               var1 = var2 == 0 || this.getPrefContentWidth(var2) == 0;
            }

            synchronized(Display.LCDUILock) {
               return var1 && this.itemCommands.length() >= 1 ? this.getEmptyStringHeight(DEFAULT_WIDTH, (Font)null) + 2 * CUSTOM_BORDER_PAD : var2 + 2 * CUSTOM_BORDER_PAD;
            }
         } catch (Throwable var8) {
            this.handleThrowable(var8);
            return -1;
         }
      }
   }

   void callSizeChanged(int var1, int var2) {
      if (this.exceptionImageItem != null) {
         this.exceptionImageItem.callSizeChanged(var1, var2);
      } else {
         super.callSizeChanged(var1, var2);
         this.labelHeight = this.getLabelHeight(var1);

         try {
            synchronized(Display.calloutLock) {
               this.sizeChanged(var1 - 2 * CUSTOM_BORDER_PAD, var2 - this.labelHeight - 2 * CUSTOM_BORDER_PAD);
            }
         } catch (Throwable var6) {
            this.handleThrowable(var6);
         }

      }
   }

   void callPaint(Graphics var1, int var2, int var3, boolean var4) {
      if (this.visible) {
         int var6;
         if (this.exceptionImageItem != null) {
            synchronized(Display.LCDUILock) {
               if (var4 && !this.isTraversable) {
                  var6 = var1.getColor();
                  var1.setColor(UIStyle.COLOUR_HIGHLIGHT);
                  Displayable.uistyle.drawBorder(var1.getImpl(), var1.getTranslateX(), var1.getTranslateY() + this.exceptionImageItem.getLabelHeight(var2), this.contentWidth, var3 - this.exceptionImageItem.getLabelHeight(var2), UIStyle.BORDER_TYPE_5, var4);
                  var1.setColor(var6);
                  var1.translate(CUSTOM_BORDER_PAD, CUSTOM_BORDER_PAD);
               }
            }

            this.exceptionImageItem.bounds = new int[4];
            this.exceptionImageItem.bounds[0] = this.bounds[0];
            this.exceptionImageItem.bounds[1] = this.bounds[1];
            this.exceptionImageItem.bounds[2] = this.bounds[2];
            this.exceptionImageItem.bounds[3] = this.bounds[3];
            if (this.hasFocus && !var4) {
               this.exceptionImageItem.stopAnimation();
            } else if ((!this.hasFocus || this.sizeChanged) && var4) {
               this.exceptionImageItem.startAnimation();
            }

            this.exceptionImageItem.callPaint(var1, var2 - 2 * CUSTOM_BORDER_PAD, var3 - 2 * CUSTOM_BORDER_PAD, var4);
            this.hasFocus = var4;
         } else {
            super.callPaint(var1, var2, var3, var4);
            this.hasFocus = var4;

            try {
               int var5 = this.getPrefContentHeight(this.contentWidth);
               var5 = var3 - this.labelHeight > var5 + 2 * CUSTOM_BORDER_PAD ? var5 + 2 * CUSTOM_BORDER_PAD : var3 - this.labelHeight;
               var6 = this.getPrefContentWidth(var5) + 2 * CUSTOM_BORDER_PAD;
               var6 = this.contentWidth > var6 ? var6 : this.contentWidth;
               var6 = this.shouldHExpand() ? this.bounds[2] : var6;
               boolean var7 = this.getPrefContentWidth(var3 - this.labelHeight - 2 * CUSTOM_BORDER_PAD) == 0 || this.getPrefContentHeight(0) == 0;
               synchronized(Display.LCDUILock) {
                  if (var4 && !this.isTraversable && (!var7 || this.itemCommands.length() >= 1)) {
                     int var9 = var1.getColor();
                     var1.setColor(UIStyle.COLOUR_HIGHLIGHT);
                     if (JMS_BLENDED_HIGHLIGHT_SUPPORT) {
                        Displayable.uistyle.drawHighlightBar(var1.getImpl(), 0, var1.getTranslateY(), var2, var3, true);
                     } else {
                        Displayable.uistyle.drawBorder(var1.getImpl(), var1.getTranslateX(), var1.getTranslateY(), var7 ? var2 : var6, var7 ? var3 - this.labelHeight : var5, UIStyle.BORDER_TYPE_5, var4);
                     }

                     var1.setColor(var9);
                  }

                  var1.translate(CUSTOM_BORDER_PAD, CUSTOM_BORDER_PAD);
               }

               if (!var7) {
                  var1.setClip(0, 0, this.contentWidth - 2 * CUSTOM_BORDER_PAD, var3 - this.labelHeight - 2 * CUSTOM_BORDER_PAD);
                  this.paint(var1, this.contentWidth - 2 * CUSTOM_BORDER_PAD, var3 - this.labelHeight - 2 * CUSTOM_BORDER_PAD);
                  return;
               }

               synchronized(Display.LCDUILock) {
                  if (this.itemCommands.length() >= 1) {
                     this.paintEmptyString(var1, var1.getTranslateX(), var1.getTranslateY(), var2 - 2 * CUSTOM_BORDER_PAD, var3 - this.labelHeight - 2 * CUSTOM_BORDER_PAD, var4);
                     return;
                  }
               }
            } catch (Throwable var15) {
               this.handleThrowable(var15);
            }

         }
      }
   }

   boolean callTraverse(int var1, int var2, int var3, int[] var4) {
      if (this.exceptionImageItem != null) {
         return this.exceptionImageItem.callTraverse(var1, var2, var3, var4);
      } else {
         this.hasFocus = true;

         try {
            synchronized(Display.calloutLock) {
               boolean var6 = this.traverse(var1, var2, var3 - this.labelHeight, var4);
               synchronized(Display.LCDUILock) {
                  if (!this.isTraversableInitialized) {
                     this.isTraversable = var6;
                     if (this.isTraversable) {
                        this.isTraversableInitialized = true;
                     }
                  }
               }

               if (var6) {
                  var4[1] = var4[1] + this.getLabelHeight(-1) + CUSTOM_BORDER_PAD;
                  if (var4[1] > this.bounds[3]) {
                     var4[1] = this.bounds[3];
                  } else if (var4[1] < 0) {
                     var4[1] = 0;
                  }

                  if (var4[0] > this.bounds[2]) {
                     var4[0] = this.bounds[2];
                  } else if (var4[0] < 0) {
                     var4[0] = 0;
                  }

                  if (var4[1] + var4[3] > this.bounds[3]) {
                     var4[3] = this.bounds[3] - var4[1];
                  } else if (var4[3] < 0) {
                     var4[3] = 0;
                  }

                  if (var4[0] + var4[2] > this.bounds[2]) {
                     var4[2] = this.bounds[2] - var4[0];
                  } else if (var4[2] < 0) {
                     var4[2] = 0;
                  }
               }

               this.repaint();
               return var6;
            }
         } catch (Throwable var12) {
            this.handleThrowable(var12);
            return false;
         }
      }
   }

   void callTraverseOut() {
      super.callTraverseOut();
      synchronized(Display.LCDUILock) {
         this.isTraversableInitialized = false;
         this.isTraversable = false;
      }

      if (this.exceptionImageItem != null) {
         this.exceptionImageItem.callTraverseOut();
      } else {
         try {
            synchronized(Display.calloutLock) {
               this.traverseOut();
               this.repaint(0, 0, this.bounds[2], this.bounds[3]);
            }
         } catch (Throwable var5) {
            this.handleThrowable(var5);
         }

      }
   }

   void callKeyPressed(int var1, int var2) {
      if (var1 == -10) {
         super.callKeyPressed(var1, var2);
      } else {
         try {
            synchronized(Display.calloutLock) {
               this.keyPressed(var1);
            }
         } catch (Throwable var6) {
            this.handleThrowable(var6);
         }
      }

   }

   void callKeyReleased(int var1, int var2) {
      if (this.exceptionImageItem != null) {
         this.exceptionImageItem.callKeyReleased(var1, var2);
      } else {
         try {
            synchronized(Display.calloutLock) {
               this.keyReleased(var1);
            }
         } catch (Throwable var6) {
            this.handleThrowable(var6);
         }

      }
   }

   void callKeyRepeated(int var1, int var2) {
      if (this.exceptionImageItem != null) {
         this.exceptionImageItem.callKeyRepeated(var1, var2);
      } else {
         try {
            synchronized(Display.calloutLock) {
               this.keyRepeated(var1);
            }
         } catch (Throwable var6) {
            this.handleThrowable(var6);
         }

      }
   }

   void callShowNotify() {
      if (this.exceptionImageItem != null) {
         this.visible = true;
         this.exceptionImageItem.callShowNotify();
      } else {
         super.callShowNotify();

         try {
            synchronized(Display.calloutLock) {
               this.showNotify();
            }
         } catch (Throwable var4) {
            this.handleThrowable(var4);
         }

      }
   }

   void callHideNotify() {
      if (this.exceptionImageItem != null) {
         this.exceptionImageItem.callHideNotify();
      } else {
         super.callHideNotify();

         try {
            synchronized(Display.calloutLock) {
               this.hideNotify();
            }
         } catch (Throwable var4) {
            this.handleThrowable(var4);
         }

      }
   }

   void setOwner(Screen var1) {
      super.setOwner(var1);
      synchronized(Display.LCDUILock) {
         this.isTraversableInitialized = false;
         this.isTraversable = false;
      }
   }

   boolean isFocusable() {
      int var1 = this.getPrefContentHeight(this.contentWidth);
      return this.itemCommands.length() >= 1 || var1 != 0 && this.getPrefContentWidth(var1) != 0;
   }

   boolean addCommandImpl(Command var1) {
      return this.exceptionImageItem == null ? super.addCommandImpl(var1) : false;
   }

   boolean removeCommandImpl(Command var1) {
      return this.exceptionImageItem == null ? super.removeCommandImpl(var1) : false;
   }

   boolean setDefaultCommandImpl(Command var1) {
      return this.exceptionImageItem == null ? super.setDefaultCommandImpl(var1) : false;
   }

   private void handleThrowable(Throwable var1) {
      this.textAlert = var1.toString();
      if (this.exceptionImageItem == null && this.owner != null) {
         synchronized(Display.LCDUILock) {
            this.defaultCommand = null;
            this.itemCommands.reset();
            this.exceptionImageItem = new ImageItem(this.getLabel(), new Image(Pixmap.createPixmap(12)), this.layout, (String)null, 1);
            this.exceptionImageItem.setOwner(this.owner);
            if (this.visible) {
               this.exceptionImageItem.callShowNotify();
            }

            if (!((Form)this.owner).customItemExceptionThrown) {
               ((Form)this.owner).customItemExceptionThrown = true;
               this.displayAlert();
            }
         }

         this.invalidate();
      }

   }

   Command[] getExtraCommands() {
      return this.exceptionImageItem != null ? extraCommands : null;
   }

   boolean launchExtraCommand(Command var1) {
      if (this.exceptionImageItem != null) {
         this.displayAlert();
         return true;
      } else {
         return false;
      }
   }

   void displayAlert() {
      Alert var1 = new Alert(this.owner.getTitle(), TextDatabase.getText(1), (Image)null, (AlertType)null);
      var1.setLastPageText(this.textAlert);
      this.owner.myDisplay.setCurrent(var1);
   }

   static {
      CUSTOM_BORDER_PAD = UIStyle.CUSTOMITEM_BORDER_PAD;
      DETAILS_COMMAND = new Command(9, 73);
      extraCommands = new Command[]{DETAILS_COMMAND};
      JMS_BLENDED_HIGHLIGHT_SUPPORT = false;
   }
}
