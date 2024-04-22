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

   protected CustomItem(String label) {
      super(label);
   }

   boolean supportHorizontalScrolling() {
      return Form.UNICOM_FORM_SCROLLING;
   }

   public int getGameAction(int keyCode) {
      int gameAction = Displayable.keyMap.getGameAction(keyCode);
      if (-127 == gameAction) {
         throw new IllegalArgumentException("getGameAction: Invalid keyCode");
      } else {
         return gameAction;
      }
   }

   protected final int getInteractionModes() {
      return !Form.IS_FOUR_WAY_SCROLL ? 30 : 31;
   }

   protected abstract int getMinContentWidth();

   protected abstract int getMinContentHeight();

   protected abstract int getPrefContentWidth(int var1);

   protected abstract int getPrefContentHeight(int var1);

   protected void sizeChanged(int w, int h) {
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

   protected final void repaint(int x, int y, int w, int h) {
      if (this.owner != null && this.bounds != null) {
         if (this.exceptionImageItem != null) {
            this.exceptionImageItem.repaint(x, y, w, h);
         } else if (x <= this.bounds[2]) {
            x = x < 0 ? 0 : x;
            y = y < 0 ? 0 : y;
            y += this.labelHeight;
            if (y <= this.bounds[3]) {
               w = x + w > this.bounds[2] ? this.bounds[2] - x : w;
               h = y + h > this.bounds[3] ? this.bounds[3] - y : h;
               super.repaint(x, y, w, h);
            }
         }
      }
   }

   protected boolean traverse(int dir, int viewportWidth, int viewportHeight, int[] visRect_inout) {
      return false;
   }

   protected void traverseOut() {
   }

   protected void keyPressed(int keyCode) {
   }

   protected void keyReleased(int keyCode) {
   }

   protected void keyRepeated(int keyCode) {
   }

   protected void pointerPressed(int x, int y) {
   }

   protected void pointerReleased(int x, int y) {
   }

   protected void pointerDragged(int x, int y) {
   }

   protected void showNotify() {
   }

   protected void hideNotify() {
   }

   int callPreferredWidth(int h) {
      if (this.exceptionImageItem != null) {
         return this.exceptionImageItem.callPreferredWidth(h) + 2 * CUSTOM_BORDER_PAD;
      } else {
         try {
            boolean isEmpty;
            int prefWidth;
            synchronized(Display.calloutLock) {
               prefWidth = this.getPrefContentWidth(h);
               isEmpty = prefWidth == 0 || this.getPrefContentHeight(this.contentWidth) == 0;
            }

            synchronized(Display.LCDUILock) {
               return isEmpty && this.itemCommands.length() >= 1 ? this.getEmptyStringWidth((Font)null) + 2 * CUSTOM_BORDER_PAD : prefWidth + 2 * CUSTOM_BORDER_PAD;
            }
         } catch (Throwable var9) {
            this.handleThrowable(var9);
            return -1;
         }
      }
   }

   int callPreferredHeight(int w) {
      if (this.exceptionImageItem != null) {
         return this.exceptionImageItem.callPreferredHeight(w) + 2 * CUSTOM_BORDER_PAD;
      } else {
         try {
            boolean isEmpty;
            int conetentHeight;
            synchronized(Display.calloutLock) {
               conetentHeight = this.getPrefContentHeight(w);
               isEmpty = conetentHeight == 0 || this.getPrefContentWidth(conetentHeight) == 0;
            }

            synchronized(Display.LCDUILock) {
               return isEmpty && this.itemCommands.length() >= 1 ? this.getEmptyStringHeight(DEFAULT_WIDTH, (Font)null) + 2 * CUSTOM_BORDER_PAD : conetentHeight + 2 * CUSTOM_BORDER_PAD;
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
            boolean isEmpty;
            int minimumWidth;
            synchronized(Display.calloutLock) {
               minimumWidth = this.getMinContentWidth();
               isEmpty = minimumWidth == 0 || this.getPrefContentHeight(this.contentWidth) == 0;
            }

            synchronized(Display.LCDUILock) {
               minimumWidth = isEmpty && this.itemCommands.length() >= 1 ? this.getEmptyStringWidth((Font)null) + 2 * CUSTOM_BORDER_PAD : minimumWidth + 2 * CUSTOM_BORDER_PAD;
               return this.itemLabel != null && minimumWidth < Item.MIN_LABEL_WIDTH ? Item.MIN_LABEL_WIDTH : minimumWidth;
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
            boolean isEmpty;
            int contentHeight;
            synchronized(Display.calloutLock) {
               contentHeight = this.getMinContentHeight();
               isEmpty = contentHeight == 0 || this.getPrefContentWidth(contentHeight) == 0;
            }

            synchronized(Display.LCDUILock) {
               return isEmpty && this.itemCommands.length() >= 1 ? this.getEmptyStringHeight(DEFAULT_WIDTH, (Font)null) + 2 * CUSTOM_BORDER_PAD : contentHeight + 2 * CUSTOM_BORDER_PAD;
            }
         } catch (Throwable var8) {
            this.handleThrowable(var8);
            return -1;
         }
      }
   }

   void callSizeChanged(int w, int h) {
      if (this.exceptionImageItem != null) {
         this.exceptionImageItem.callSizeChanged(w, h);
      } else {
         super.callSizeChanged(w, h);
         this.labelHeight = this.getLabelHeight(w);

         try {
            synchronized(Display.calloutLock) {
               this.sizeChanged(w - 2 * CUSTOM_BORDER_PAD, h - this.labelHeight - 2 * CUSTOM_BORDER_PAD);
            }
         } catch (Throwable var6) {
            this.handleThrowable(var6);
         }

      }
   }

   void callPaint(Graphics g, int w, int h, boolean isFocused) {
      if (this.visible) {
         int widthCI;
         if (this.exceptionImageItem != null) {
            synchronized(Display.LCDUILock) {
               if (isFocused && !this.isTraversable) {
                  widthCI = g.getColor();
                  g.setColor(UIStyle.COLOUR_HIGHLIGHT);
                  Displayable.uistyle.drawBorder(g.getImpl(), g.getTranslateX(), g.getTranslateY() + this.exceptionImageItem.getLabelHeight(w), this.contentWidth, h - this.exceptionImageItem.getLabelHeight(w), UIStyle.BORDER_IMAGE_HIGHLIGHT, isFocused);
                  g.setColor(widthCI);
                  g.translate(CUSTOM_BORDER_PAD, CUSTOM_BORDER_PAD);
               }
            }

            this.exceptionImageItem.bounds = new int[4];
            this.exceptionImageItem.bounds[0] = this.bounds[0];
            this.exceptionImageItem.bounds[1] = this.bounds[1];
            this.exceptionImageItem.bounds[2] = this.bounds[2];
            this.exceptionImageItem.bounds[3] = this.bounds[3];
            if (this.hasFocus && !isFocused) {
               this.exceptionImageItem.stopAnimation();
            } else if ((!this.hasFocus || this.sizeChanged) && isFocused) {
               this.exceptionImageItem.startAnimation();
            }

            this.exceptionImageItem.callPaint(g, w - 2 * CUSTOM_BORDER_PAD, h - 2 * CUSTOM_BORDER_PAD, isFocused);
            this.hasFocus = isFocused;
         } else {
            super.callPaint(g, w, h, isFocused);
            this.hasFocus = isFocused;

            try {
               int heightCI = this.getPrefContentHeight(this.contentWidth);
               heightCI = h - this.labelHeight > heightCI + 2 * CUSTOM_BORDER_PAD ? heightCI + 2 * CUSTOM_BORDER_PAD : h - this.labelHeight;
               widthCI = this.getPrefContentWidth(heightCI) + 2 * CUSTOM_BORDER_PAD;
               widthCI = this.contentWidth > widthCI ? widthCI : this.contentWidth;
               widthCI = this.shouldHExpand() ? this.bounds[2] : widthCI;
               boolean isEmpty = this.getPrefContentWidth(h - this.labelHeight - 2 * CUSTOM_BORDER_PAD) == 0 || this.getPrefContentHeight(0) == 0;
               synchronized(Display.LCDUILock) {
                  if (isFocused && !this.isTraversable && (!isEmpty || this.itemCommands.length() >= 1)) {
                     int oldColor = g.getColor();
                     g.setColor(UIStyle.COLOUR_HIGHLIGHT);
                     if (JMS_BLENDED_HIGHLIGHT_SUPPORT) {
                        Displayable.uistyle.drawHighlightBar(g.getImpl(), 0, g.getTranslateY(), w, h, true);
                     } else {
                        Displayable.uistyle.drawBorder(g.getImpl(), g.getTranslateX(), g.getTranslateY(), isEmpty ? w : widthCI, isEmpty ? h - this.labelHeight : heightCI, UIStyle.BORDER_IMAGE_HIGHLIGHT, isFocused);
                     }

                     g.setColor(oldColor);
                  }

                  g.translate(CUSTOM_BORDER_PAD, CUSTOM_BORDER_PAD);
               }

               if (!isEmpty) {
                  g.setClip(0, 0, this.contentWidth - 2 * CUSTOM_BORDER_PAD, h - this.labelHeight - 2 * CUSTOM_BORDER_PAD);
                  this.paint(g, this.contentWidth - 2 * CUSTOM_BORDER_PAD, h - this.labelHeight - 2 * CUSTOM_BORDER_PAD);
                  return;
               }

               synchronized(Display.LCDUILock) {
                  if (this.itemCommands.length() >= 1) {
                     this.paintEmptyString(g, g.getTranslateX(), g.getTranslateY(), w - 2 * CUSTOM_BORDER_PAD, h - this.labelHeight - 2 * CUSTOM_BORDER_PAD, isFocused);
                     return;
                  }
               }
            } catch (Throwable var15) {
               this.handleThrowable(var15);
            }

         }
      }
   }

   boolean callTraverse(int dir, int viewportWidth, int viewportHeight, int[] visRect_inout) {
      if (this.exceptionImageItem != null) {
         return this.exceptionImageItem.callTraverse(dir, viewportWidth, viewportHeight, visRect_inout);
      } else {
         this.hasFocus = true;

         try {
            synchronized(Display.calloutLock) {
               boolean traversed = this.traverse(dir, viewportWidth, viewportHeight - this.labelHeight, visRect_inout);
               synchronized(Display.LCDUILock) {
                  if (!this.isTraversableInitialized) {
                     this.isTraversable = traversed;
                     if (this.isTraversable) {
                        this.isTraversableInitialized = true;
                     }
                  }
               }

               if (traversed) {
                  visRect_inout[1] = visRect_inout[1] + this.getLabelHeight(-1) + CUSTOM_BORDER_PAD;
                  if (visRect_inout[1] > this.bounds[3]) {
                     visRect_inout[1] = this.bounds[3];
                  } else if (visRect_inout[1] < 0) {
                     visRect_inout[1] = 0;
                  }

                  if (visRect_inout[0] > this.bounds[2]) {
                     visRect_inout[0] = this.bounds[2];
                  } else if (visRect_inout[0] < 0) {
                     visRect_inout[0] = 0;
                  }

                  if (visRect_inout[1] + visRect_inout[3] > this.bounds[3]) {
                     visRect_inout[3] = this.bounds[3] - visRect_inout[1];
                  } else if (visRect_inout[3] < 0) {
                     visRect_inout[3] = 0;
                  }

                  if (visRect_inout[0] + visRect_inout[2] > this.bounds[2]) {
                     visRect_inout[2] = this.bounds[2] - visRect_inout[0];
                  } else if (visRect_inout[2] < 0) {
                     visRect_inout[2] = 0;
                  }
               }

               this.repaint();
               return traversed;
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

   void callKeyPressed(int keyCode, int keyDataIdx) {
      if (keyCode == -10) {
         super.callKeyPressed(keyCode, keyDataIdx);
      } else {
         try {
            synchronized(Display.calloutLock) {
               this.keyPressed(keyCode);
            }
         } catch (Throwable var6) {
            this.handleThrowable(var6);
         }
      }

   }

   void callKeyReleased(int keyCode, int keyDataIdx) {
      if (this.exceptionImageItem != null) {
         this.exceptionImageItem.callKeyReleased(keyCode, keyDataIdx);
      } else {
         try {
            synchronized(Display.calloutLock) {
               this.keyReleased(keyCode);
            }
         } catch (Throwable var6) {
            this.handleThrowable(var6);
         }

      }
   }

   void callKeyRepeated(int keyCode, int keyDataIdx) {
      if (this.exceptionImageItem != null) {
         this.exceptionImageItem.callKeyRepeated(keyCode, keyDataIdx);
      } else {
         try {
            synchronized(Display.calloutLock) {
               this.keyRepeated(keyCode);
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

   void setOwner(Screen owner) {
      super.setOwner(owner);
      synchronized(Display.LCDUILock) {
         this.isTraversableInitialized = false;
         this.isTraversable = false;
      }
   }

   boolean isFocusable() {
      int h = this.getPrefContentHeight(this.contentWidth);
      return this.itemCommands.length() >= 1 || h != 0 && this.getPrefContentWidth(h) != 0;
   }

   boolean addCommandImpl(Command cmd) {
      return this.exceptionImageItem == null ? super.addCommandImpl(cmd) : false;
   }

   boolean removeCommandImpl(Command cmd) {
      return this.exceptionImageItem == null ? super.removeCommandImpl(cmd) : false;
   }

   boolean setDefaultCommandImpl(Command cmd) {
      return this.exceptionImageItem == null ? super.setDefaultCommandImpl(cmd) : false;
   }

   private void handleThrowable(Throwable thr) {
      this.textAlert = thr.toString();
      if (this.exceptionImageItem == null && this.owner != null) {
         synchronized(Display.LCDUILock) {
            this.defaultCommand = null;
            this.itemCommands.reset();
            this.exceptionImageItem = new ImageItem(this.getLabel(), new Image(Pixmap.createPixmap(13)), this.layout, (String)null, 1);
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

   boolean launchExtraCommand(Command c) {
      if (this.exceptionImageItem != null) {
         this.displayAlert();
         return true;
      } else {
         return false;
      }
   }

   void displayAlert() {
      Alert customItemAlert = new Alert(this.owner.getTitle(), TextDatabase.getText(1), (Image)null, (AlertType)null);
      customItemAlert.setLastPageText(this.textAlert);
      this.owner.myDisplay.setCurrent(customItemAlert);
   }

   static {
      CUSTOM_BORDER_PAD = UIStyle.CUSTOMITEM_BORDER_PAD;
      DETAILS_COMMAND = new Command(9, 73);
      extraCommands = new Command[]{DETAILS_COMMAND};
      JMS_BLENDED_HIGHLIGHT_SUPPORT = false;
   }
}
