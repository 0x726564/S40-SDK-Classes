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

   void showElement(Display var1, Screen var2, String var3, Image var4, boolean var5) {
      this.isModal = var5;
      this.textLines.removeAllElements();
      this.currentPage = 0;
      if (var4 == null) {
         this.textZone = uistyle.getZone(41);
         this.imgZone = null;
      } else {
         this.textZone = uistyle.getZone(42);
         this.imgZone = uistyle.getZone(43);
      }

      this.image = var4;
      TextBreaker.breakTextInZone(this.textZone, var5 ? TextBreaker.NBR_OF_AREAS_AS_NEEDED : 1, var3, TextBreaker.DEFAULT_TEXT_LEADING, false, false, this.textLines, false, true);
      this.isMultiPage = this.textLines.elementAt(0) instanceof Vector;
      this.setTitleImpl(var2.getTitle());
      this.updateSoftkeys(false);
      if (var1 != null) {
         var1.setCurrentInternal(var2, this);
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

   void changePage(int var1) {
      if (this.isModal && this.isMultiPage) {
         this.currentPage = (this.currentPage + var1) % this.textLines.size();
         if (this.currentPage < 0) {
            this.currentPage += this.textLines.size();
         }

         this.repaintFull();
      }
   }

   void callHideNotify(Display var1) {
      super.callHideNotify(var1);
      synchronized(Display.LCDUILock) {
         if (!this.isModal && !var1.isScreenChangeInProgress()) {
            this.myDisplay.setCurrentInternal((Displayable)null, this.getParentDisplayable());
         }

      }
   }

   void removedFromDisplayNotify(Display var1) {
      this.setTitleImpl((String)null);
      this.textLines.removeAllElements();
      this.textLines.trimToSize();
      this.image = null;
      this.textZone = null;
      this.imgZone = null;
   }

   void callPaint(Graphics var1) {
      super.callPaint(var1);
      synchronized(Display.LCDUILock) {
         com.nokia.mid.impl.isa.ui.gdi.Graphics var3 = var1.getImpl();
         Vector var4 = this.textLines;
         if (this.isMultiPage) {
            var4 = (Vector)var4.elementAt(this.currentPage);
         }

         if (this.image != null && this.currentPage == 0) {
            uistyle.drawPixmapInZone(var3, this.imgZone, 0, 0, this.image.pixmap);
         }

         var3.getColorCtrl().setFgColor(UIStyle.COLOUR_TEXT);
         var3.drawTextInZone(this.textZone, 0, 0, var4, UIStyle.isAlignedLeftToRight ? 1 : 3);
      }
   }

   void callKeyPressed(int var1, int var2) {
      super.callKeyPressed(var1, var2);
      if (this.isModal) {
         synchronized(Display.LCDUILock) {
            switch(var1) {
            case -2:
               this.changePage(1);
               break;
            case -1:
               this.changePage(-1);
            }

         }
      }
   }

   void callKeyReleased(int var1, int var2) {
      super.callKeyReleased(var1, var2);
      if (!this.isModal && var1 == 35) {
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

   boolean launchExtraCommand(Command var1) {
      if (var1 == backCommand) {
         return this.hideElement();
      } else {
         if (var1 == moreCommand) {
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
