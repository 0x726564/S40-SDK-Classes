package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.TextDatabase;

class JavaOptionsMenu extends List implements OptionsMenu {
   private boolean selectionMade;
   private boolean repaintEnable = true;
   private boolean listUpdatePending = true;
   private int pendingSelectedIdx = -1;

   JavaOptionsMenu() {
      super("", 3, true);
      synchronized(Display.LCDUILock) {
         this.setFitPolicyImpl(2);
         this.sl.set(TextDatabase.getText(9), SoftLabel.SOFTLABEL_SELECT);
         this.sl.set(TextDatabase.getText(3), 2);
         this.setPopup(true);
      }
   }

   void callKeyPressed(int var1, int var2) {
      Displayable var3 = this.getParentDisplayable();
      boolean var4 = false;
      if (var1 != DeviceInfo.KEY_SOFT_SELECT && var1 != -7) {
         super.callKeyPressed(var1, var2);
      } else if (!this.selectionMade) {
         synchronized(Display.LCDUILock) {
            this.selectionMade = true;
            if (var1 == DeviceInfo.KEY_SOFT_SELECT) {
               Command var6 = var3.optionMenuCommands.getCommand(this.getHighlightedOptionIndex());
               if (var6 != null) {
                  var4 = var3.commandListAction(var6);
               }
            }

            if (!var4) {
               this.myDisplay.setCurrentInternal((Displayable)null, var3);
            }
         }
      }

   }

   void callShowNotify(Display var1) {
      super.callShowNotify(var1);
      synchronized(Display.LCDUILock) {
         Displayable var3 = this.getParentDisplayable();
         if (this.title != var3.title) {
            this.setTitleImpl(var3.title);
         }

         if (this.listUpdatePending) {
            this.update(this.pendingSelectedIdx);
         }

      }
   }

   void removedFromDisplayNotify(Display var1) {
      this.listUpdatePending = true;
      this.pendingSelectedIdx = -1;
      this.selectionMade = false;
      this.repaintEnable = true;
      this.title = null;
      this.deleteAll();
   }

   void callPaint(Graphics var1) {
      if (this.repaintEnable) {
         var1.clearScreen(true, false);
         super.callPaint(var1);
      }

   }

   boolean usesSoftLabel() {
      return true;
   }

   public int getHighlightedOptionIndex() {
      return this.listUpdatePending ? this.pendingSelectedIdx : this.getSelectedIndexImpl();
   }

   public final void update(int var1) {
      Displayable var2 = this.getParentDisplayable();
      if (var2 != null) {
         this.repaintEnable = false;
         int var3 = var2.optionMenuCommands.length();
         if (var3 == 0) {
            if (!this.selectionMade) {
               this.listUpdatePending = false;
               this.pendingSelectedIdx = -1;
               this.deleteAll();
               this.myDisplay.setCurrentInternal((Displayable)null, var2);
               this.selectionMade = true;
            }
         } else if (this.isShown()) {
            this.listUpdatePending = false;
            this.pendingSelectedIdx = -1;
            this.deleteAll();

            for(int var4 = 0; var4 < var3; ++var4) {
               this.append(var2.optionMenuCommands.getCommand(var4).menuLabel, (Image)null);
            }

            if (var1 >= 0) {
               if (var1 < this.size()) {
                  this.setSelectedIndexImpl(var1, true);
               } else {
                  this.setSelectedIndexImpl(this.size() - 1, true);
               }
            }

            this.repaintEnable = true;
            this.repaintFull();
         } else {
            this.listUpdatePending = true;
            if (var1 >= 0) {
               if (var1 < var3) {
                  this.pendingSelectedIdx = var1;
               } else {
                  this.pendingSelectedIdx = var3 - 1;
               }
            }
         }
      }

   }
}
