package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import java.util.Vector;

final class TruncatedItemScreen extends Screen {
   private static final TruncatedItemScreen instance = new TruncatedItemScreen();
   private static final Command backCommand = new Command(10, 3);
   private static final Command moreCommand = new Command(9, 7);
   private static final Command[] singlePageCommands;
   private static final Command[] multiPageCommands;
   boolean isModal = false;
   boolean isMultiPage = false;
   private int currentPage = 0;
   private Vector textLines = new Vector();
   private Image image = null;
   private Zone textZone = null;
   private Zone imgZone = null;

   private TruncatedItemScreen() {
      this.setSystemScreen(true);
   }

   static TruncatedItemScreen getTruncatedItemScreen() {
      return instance;
   }

   void showElement(Display display, Screen parent, String text, Image image, boolean isModal) {
      this.isModal = isModal;
      this.textLines.removeAllElements();
      this.currentPage = 0;
      if (image == null) {
         this.textZone = uistyle.getZone(42);
         this.imgZone = null;
      } else {
         this.textZone = uistyle.getZone(43);
         this.imgZone = uistyle.getZone(44);
      }

      this.image = image;
      TextBreaker.breakTextInZone(this.textZone, isModal ? TextBreaker.NBR_OF_AREAS_AS_NEEDED : 1, text, TextBreaker.DEFAULT_TEXT_LEADING, false, false, this.textLines, false, true);
      this.isMultiPage = this.textLines.elementAt(0) instanceof Vector;
      this.setTitleImpl(parent.getTitle());
      this.updateSoftkeys(false);
      if (display != null) {
         display.setCurrentInternal(parent, this);
      }

   }

   boolean hideElement() {
      if (this.displayed) {
         this.myDisplay.setCurrentInternal((Displayable)null, this.getParentDisplayable());
         return true;
      } else {
         return false;
      }
   }

   void changePage(int delta) {
      if (this.isModal && this.isMultiPage) {
         this.currentPage = (this.currentPage + delta) % this.textLines.size();
         if (this.currentPage < 0) {
            this.currentPage += this.textLines.size();
         }

         this.repaintFull();
      }
   }

   void callHideNotify(Display d) {
      super.callHideNotify(d);
      synchronized(Display.LCDUILock) {
         if (!this.isModal && !d.isScreenChangeInProgress()) {
            this.myDisplay.setCurrentInternal((Displayable)null, this.getParentDisplayable());
         }

      }
   }

   void removedFromDisplayNotify(Display d) {
      this.setTitleImpl((String)null);
      this.textLines.removeAllElements();
      this.textLines.trimToSize();
      this.image = null;
      this.textZone = null;
      this.imgZone = null;
   }

   void callPaint(Graphics g) {
      super.callPaint(g);
      synchronized(Display.LCDUILock) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
         Vector lines = this.textLines;
         if (this.isMultiPage) {
            lines = (Vector)lines.elementAt(this.currentPage);
         }

         if (this.image != null && this.currentPage == 0) {
            uistyle.drawPixmapInZone(ng, this.imgZone, 0, 0, this.image.pixmap);
         }

         ng.getColorCtrl().setFgColor(UIStyle.COLOUR_TEXT);
         ng.drawTextInZone(this.textZone, 0, 0, lines, UIStyle.isAlignedLeftToRight ? 1 : 3);
      }
   }

   void callKeyPressed(int keycode, int keyDataIdx) {
      super.callKeyPressed(keycode, keyDataIdx);
      if (this.isModal) {
         synchronized(Display.LCDUILock) {
            switch(keycode) {
            case -2:
               this.changePage(1);
               break;
            case -1:
               this.changePage(-1);
            }

         }
      }
   }

   void callKeyReleased(int keycode, int keyDataIdx) {
      super.callKeyReleased(keycode, keyDataIdx);
      if (!this.isModal && keycode == 35) {
         synchronized(Display.LCDUILock) {
            this.hideElement();
         }
      }

   }

   Command[] getExtraCommands() {
      if (this.isModal) {
         return this.isMultiPage && this.textLines.size() > 1 ? multiPageCommands : singlePageCommands;
      } else {
         return null;
      }
   }

   boolean launchExtraCommand(Command cmd) {
      if (cmd == backCommand) {
         return this.hideElement();
      } else {
         if (cmd == moreCommand) {
            this.changePage(1);
         }

         return false;
      }
   }

   static {
      singlePageCommands = new Command[]{backCommand};
      multiPageCommands = new Command[]{moreCommand, backCommand};
   }
}
