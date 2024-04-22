package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.TextDatabase;

class NativeList implements EventConsumer {
   static final Command PLATFORM_SELECT = new Command(9, 9);
   static final Command PLATFORM_MARK = new Command(9, 10);
   static final Command PLATFORM_UNMARK = new Command(9, 11);
   private static NativeList currentList;
   Command[] list_softkeys_cmds;
   Command[] options_menu_cmds;
   ChoiceVector cv;
   boolean ignoreNextSetItem;
   boolean launched;
   boolean isVisible;

   static NativeList createNativeList(ChoiceVector cv) {
      if (currentList != null) {
         currentList.dismiss();
      }

      currentList = new NativeList(cv);
      return currentList;
   }

   private NativeList(ChoiceVector cv) {
      this.list_softkeys_cmds = new Command[SoftLabel.NUM_SOFTKEYS];
      this.ignoreNextSetItem = false;
      this.launched = false;
      this.isVisible = false;
      this.cv = cv;
      InitJALM.s_getEventProducer().attachEventConsumer(14, this);
   }

   void launch() {
      this.nativeLaunch(this.cv.getTitle(), this.cv.getType(), this.cv.size(), this.cv.getSelectedIndex(), this.cv.hasImage(), this.cv.hasTicker(), this.cv.getFitPolicy() == 1);
      this.launched = true;
   }

   void dismiss() {
      this.nativeDismis();
      InitJALM.s_getEventProducer().detachEventConsumer(14, this);
      currentList = null;
   }

   boolean insert(int index, boolean hasImage) {
      this.ignoreNextSetItem = this.nativeGetContentLock(this.cv);
      if (this.cv.size() == 0) {
         this.nativeReleaseContentLock();
         this.nativeRestoreList(hasImage);
         return true;
      } else if (hasImage && !this.cv.hasImage()) {
         this.nativeReleaseContentLock();
         this.nativeChangeImageLayout(this.cv, this.cv.size() + 1, true);
         return false;
      } else {
         this.nativeInsert(this.cv, index);
         this.nativeReleaseContentLock();
         return false;
      }
   }

   boolean delete(int index) {
      if (this.cv.size() == 1) {
         this.nativeSetEmptyList();
         return true;
      } else {
         this.ignoreNextSetItem = this.nativeGetContentLock(this.cv);
         if (this.cv.getImage(index) != null && this.cv.getNumOfImages() == 1) {
            this.nativeChangeImageLayout(this.cv, this.cv.size() - 1, false);
         } else {
            this.nativeRemove(this.cv, index);
         }

         this.nativeReleaseContentLock();
         return false;
      }
   }

   void deleteAll() {
      this.ignoreNextSetItem = this.nativeGetContentLock(this.cv);
      this.nativeDeleteAll(this.cv);
      this.nativeReleaseContentLock();
   }

   void resetData(int elementNum, boolean hasImage, boolean layoutHasChanged) {
      this.ignoreNextSetItem = this.nativeGetContentLock(this.cv);
      if (layoutHasChanged) {
         this.nativeChangeImageLayout(this.cv, this.cv.size(), hasImage);
      } else {
         this.nativeReloadItemElement(this.cv, elementNum);
      }

      this.nativeReleaseContentLock();
   }

   public void consumeEvent(int category, int type, int param) {
      Command cmd = null;
      boolean callOut = false;
      boolean releaseLock = true;
      switch(type) {
      case 1:
         synchronized(Display.LCDUILock) {
            if (this.ignoreNextSetItem) {
               this.ignoreNextSetItem = false;
            } else {
               this.nativeReleaseContentLock();
               this.nativeSet(this.cv);
            }

            releaseLock = false;
            break;
         }
      case 2:
         synchronized(Display.LCDUILock) {
            cmd = this.list_softkeys_cmds[0];
            if (cmd != OptionsMenu.optionsCommand) {
               callOut = true;
            } else {
               this.options_menu_cmds = this.cv.getOptionCommands();
               String[] optionsLabels = new String[this.options_menu_cmds.length];

               for(int i = 0; i < optionsLabels.length; ++i) {
                  optionsLabels[i] = this.options_menu_cmds[i].getMenuLabel();
               }

               this.nativeSetMenuCmds(optionsLabels);
            }
            break;
         }
      case 3:
         synchronized(Display.LCDUILock) {
            cmd = this.list_softkeys_cmds[1];
            if (this.cv.nOfItems != 0 && this.cv.type != 3 && this.cv.type != 4) {
               if (param < this.cv.nOfItems) {
                  boolean selected = this.cv.type == 2 ? !this.cv.getItem(param).selected : true;
                  this.cv.setSelectedIndex(param, selected);
               }
            } else {
               callOut = true;
            }
            break;
         }
      case 4:
         synchronized(Display.LCDUILock) {
            cmd = this.list_softkeys_cmds[2];
            callOut = true;
            break;
         }
      case 5:
         this.cv.markAll(param != 0);
         break;
      case 6:
         cmd = this.options_menu_cmds[param];
         callOut = true;
         break;
      case 7:
         this.isVisible = param == 1;
         releaseLock = false;
         break;
      case 8:
         releaseLock = false;
         break;
      case 9:
         releaseLock = false;
         synchronized(Display.LCDUILock) {
            if (this.cv.nOfItems > 0) {
               if (this.cv.type != 3 && this.cv.type != 4) {
                  if (this.cv.type == 2) {
                     this.cv.setHighlightedItem(param);
                     if (this.cv.getItem(param).selected) {
                        this.nativeUpdateSoftKey(TextDatabase.getText(11), SoftLabel.SOFTLABEL_SELECT);
                     } else {
                        this.nativeUpdateSoftKey(TextDatabase.getText(10), SoftLabel.SOFTLABEL_SELECT);
                     }
                  }
               } else {
                  this.cv.setSelectedIndex(param, true);
               }
            }
         }
      }

      if (releaseLock) {
         this.nativeReleaseContentLock();
      }

      if (callOut && cmd != null) {
         this.cv.handleCmd(cmd);
      }

   }

   void updateSoftKey(String labelText, int index, Command cmd) {
      this.list_softkeys_cmds[index] = cmd;
      if (this.launched) {
         this.nativeGetContentLock(this.cv);
         this.nativeUpdateSoftKey(labelText, index);
         this.nativeReleaseContentLock();
      }

   }

   void updateMenuCommands() {
      this.nativeGetContentLock(this.cv);
      if (this.nativeIsMenuShown()) {
         this.options_menu_cmds = this.cv.getOptionCommands();
         String[] optionsLabels = new String[this.options_menu_cmds.length];

         for(int i = 0; i < optionsLabels.length; ++i) {
            optionsLabels[i] = this.options_menu_cmds[i].getMenuLabel();
         }

         this.nativeSetMenuCmds(optionsLabels);
      }

      this.nativeReleaseContentLock();
   }

   void setSelectedFlags() {
      if (this.cv.type == 2) {
         boolean[] flags = new boolean[this.cv.nOfItems];
         this.cv.getSelectedFlags(flags);
         this.nativeSetSelectedFlags(flags);
      } else {
         int selected_item = this.cv.getSelectedIndex();
         this.nativeSetSelectedIndex(selected_item, this.cv.isSelected(selected_item));
      }

   }

   Command[] getExtraCommands() {
      Command[] ret = null;
      if (this.cv.size() != 0) {
         switch(this.cv.type) {
         case 1:
            ret = new Command[]{PLATFORM_SELECT};
            break;
         case 2:
            ret = this.cv.highlightedIndex >= 0 && this.cv.getHighlightedItem().selected ? new Command[]{PLATFORM_UNMARK} : new Command[]{PLATFORM_MARK};
         }
      }

      return ret;
   }

   private static native void nativeStaticInitialiser();

   private native void nativeLaunch(String var1, int var2, int var3, int var4, boolean var5, boolean var6, boolean var7);

   private native void nativeDismis();

   private native void nativeSetEmptyList();

   private native void nativeRestoreList(boolean var1);

   public native void nativeSetTitle(String var1);

   public native void nativeSetTicker(boolean var1);

   private native boolean nativeInsert(ChoiceVector var1, int var2);

   private native boolean nativeGetContentLock(ChoiceVector var1);

   private native void nativeReleaseContentLock();

   private native boolean nativeRemove(ChoiceVector var1, int var2);

   private native boolean nativeDeleteAll(ChoiceVector var1);

   private native boolean nativeChangeImageLayout(ChoiceVector var1, int var2, boolean var3);

   native int nativeGetSelectedIndex();

   native void nativeSetSelectedIndex(int var1, boolean var2);

   native void nativeReloadItemElement(ChoiceVector var1, int var2);

   private native void nativeSet(ChoiceVector var1);

   native void nativeSetFitPolicy(boolean var1);

   private native void nativeSetSelectedFlags(boolean[] var1);

   private native void nativeSetMenuCmds(String[] var1);

   private native void nativeUpdateSoftKey(String var1, int var2);

   private native boolean nativeIsMenuShown();

   static {
      nativeStaticInitialiser();
   }
}
