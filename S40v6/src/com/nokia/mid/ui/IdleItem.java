package com.nokia.mid.ui;

import com.nokia.mid.impl.isa.ui.DisplayAccess;
import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.EventProducer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.JavaEventGenerator;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public abstract class IdleItem {
   private static final int STATE_UNASSIGNED = 0;
   private static final int STATE_ASSIGNED = 1;
   private static final int STATE_PASSIVE = 2;
   private static final int STATE_ACTIVE = 3;
   private static final int STATE_FOCUSED = 4;
   private static final int IDLE_SCREEN_DIRECTION_MASK = 15;
   private static final int IDLE_SCREEN_VISIBLE = 16;
   private static final int IDLE_SCREEN_ACTIVE = 32;
   private static final int IDLE_SCREEN_SIZE_CHANGED = 64;
   private static final int IDLE_SCREEN_ACTIVATED = 128;
   private static final int IDLE_SCREEN_UPDATE = 256;
   public static final int NONE = 0;
   public static final int TRAVERSE_HORIZONTAL = 1;
   public static final int TRAVERSE_VERTICAL = 2;
   static final Object idleLock = new Object();
   static final Object calloutLock = new Object();
   private static int state = 0;
   private static IdleItem currentIdleItem = null;
   private static IdleEventListener idleItemEventListener = null;
   private int idleItemZoneWidth;
   private int idleItemZoneHeight;
   private Image idleItemImage;
   private Pixmap idleItemPixmap;
   private Command selectCommand = null;
   private IdleItem.CommandVector idleItemCommands = new IdleItem.CommandVector();
   private String[] optionLabels;
   private IdleItemCommandListener idleItemCommandListener = null;
   private boolean enableScrollIcons = false;
   private boolean hidden = true;

   private static native int nativeStaticInitialiser();

   public IdleItem() {
      IdleItem.IdleItemEventConsumer.register();
      if (this.nativeConnectToScreen()) {
      }

   }

   public final int getMinimumHeight() {
      return this.idleItemZoneHeight;
   }

   public final int getMinimumWidth() {
      return this.idleItemZoneWidth;
   }

   public final int getPreferredHeight() {
      return this.idleItemZoneHeight;
   }

   public final int getPreferredWidth() {
      return this.idleItemZoneWidth;
   }

   public final void addCommand(Command cmd) {
      Command shiftCommand = null;
      boolean wasAdded = false;
      if (cmd == null) {
         throw new NullPointerException();
      } else {
         synchronized(idleLock) {
            if (this.selectCommand == null) {
               this.selectCommand = cmd;
               this.nativeGenerateUpdate(this.enableScrollIcons, this.getCommandLabelImpl(this.selectCommand));
            } else {
               if (cmd.getCommandType() == 4 && this.selectCommand.getCommandType() != 4) {
                  shiftCommand = this.selectCommand;
                  this.selectCommand = cmd;
                  this.nativeGenerateUpdate(this.enableScrollIcons, this.getCommandLabelImpl(this.selectCommand));
               }

               if (shiftCommand != null) {
                  wasAdded = this.idleItemCommands.insertCommand(0, shiftCommand);
               } else {
                  wasAdded = this.idleItemCommands.insertCommand(-1, cmd);
               }

               if (wasAdded) {
                  this.setupOptionsMenu();
                  this.nativeGenerateSetOptions();
               }
            }

         }
      }
   }

   public final void removeCommand(Command cmd) {
      boolean wasRemoved = false;
      synchronized(idleLock) {
         if (cmd == this.selectCommand) {
            if (this.idleItemCommands.length == 0) {
               this.selectCommand = null;
               this.nativeGenerateUpdate(this.enableScrollIcons, (String)null);
            } else {
               this.selectCommand = this.idleItemCommands.getCommand(0);
               this.nativeGenerateUpdate(this.enableScrollIcons, this.getCommandLabelImpl(this.selectCommand));
               wasRemoved = this.idleItemCommands.removeCommand(this.selectCommand);
            }
         }

         wasRemoved |= this.idleItemCommands.removeCommand(cmd);
         if (wasRemoved) {
            this.setupOptionsMenu();
            this.nativeGenerateSetOptions();
         }

      }
   }

   public final void setIdleItemCommandListener(IdleItemCommandListener commandListener) {
      synchronized(idleLock) {
         this.idleItemCommandListener = commandListener;
      }
   }

   public final IdleItemCommandListener getIdleItemCommandListener() {
      synchronized(idleLock) {
         return this.idleItemCommandListener;
      }
   }

   public static final void setIdleItemEventListener(IdleEventListener eventListener) {
      IdleItem.IdleItemEventConsumer.register();
      synchronized(idleLock) {
         idleItemEventListener = eventListener;
      }
   }

   public static final IdleEventListener getIdleItemEventListener() {
      synchronized(idleLock) {
         return idleItemEventListener;
      }
   }

   public static final boolean setCurrentIdleItem(IdleItem newIdleItem) {
      boolean result = false;
      synchronized(calloutLock) {
         if (newIdleItem != null) {
            switch(state) {
            case 1:
            case 2:
               if (currentIdleItem != null) {
                  currentIdleItem.callRemovedFromDisplay();
               }

               result = newIdleItem.callAddedToDisplay();
               if (result) {
                  currentIdleItem = newIdleItem;
                  state = 2;
               }
            case 3:
            case 4:
            }
         } else if (state > 0) {
            state = 1;
            if (currentIdleItem != null) {
               currentIdleItem.callRemovedFromDisplay();
            }

            currentIdleItem = null;
            result = true;
         }

         return result;
      }
   }

   public static final IdleItem getCurrentIdleItem() {
      synchronized(calloutLock) {
         return currentIdleItem;
      }
   }

   protected void addedToDisplay() {
   }

   protected void removedFromDisplay() {
   }

   protected void sizeChanged(int w, int h) {
   }

   protected void showNotify() {
   }

   protected void hideNotify() {
   }

   protected boolean traverse(int direction, int width, int height) {
      return false;
   }

   protected void traverseOut() {
   }

   protected abstract void paint(Graphics var1, int var2, int var3);

   protected final int getInteractionModes() {
      return 3;
   }

   protected final void repaint() {
      JavaEventGenerator.s_generateEvent(0, 15, 8, 0);
   }

   private boolean callAddedToDisplay() {
      boolean result = false;
      if (this.idleItemImage == null) {
         this.idleItemZoneWidth = 0;
         this.idleItemZoneHeight = 0;
         result = this.nativeCreateImageBuffer();
         if (result && this.idleItemZoneWidth > 0 && this.idleItemZoneHeight > 0) {
            this.idleItemImage = DirectUtils.createImage(this.idleItemZoneWidth, this.idleItemZoneHeight, 0);
            DisplayAccess displayAccessor = InitJALM.s_getMIDletAccessor().getDisplayAccessor();
            this.idleItemPixmap = displayAccessor.getImagePixmap(this.idleItemImage);
         }
      }

      this.updateOptions();
      this.addedToDisplay();
      return result;
   }

   private void updateOptions() {
      if (this.selectCommand != null) {
         this.nativeGenerateUpdate(this.enableScrollIcons, this.getCommandLabelImpl(this.selectCommand));
         if (this.idleItemCommands.isNotEmpty()) {
            this.setupOptionsMenu();
            this.nativeGenerateSetOptions();
         }
      } else {
         this.nativeGenerateUpdate(this.enableScrollIcons, (String)null);
      }

   }

   private void callRemovedFromDisplay() {
      this.removedFromDisplay();
      this.idleItemImage = null;
      this.idleItemPixmap = null;
   }

   private void callSizeChanged() {
      this.sizeChanged(this.idleItemZoneWidth, this.idleItemZoneHeight);
   }

   private void callLocalShowNotify() {
      if (state >= 2 && this.hidden) {
         this.showNotify();
         this.repaint();
         this.hidden = false;
      }

   }

   private void callLocalHideNotify() {
      if (state >= 2 && !this.hidden) {
         this.hidden = true;
         this.hideNotify();
      }

   }

   private boolean callLocalTraverse(int type) {
      int direction = 0;
      switch(type) {
      case 1:
         direction = 1;
         break;
      case 2:
         direction = 6;
         break;
      case 3:
         direction = 2;
         break;
      case 4:
         direction = 5;
      }

      switch(state) {
      case 0:
      case 1:
         return false;
      case 2:
         if (direction == 0) {
            state = 3;
         } else {
            state = 4;
         }
         break;
      case 3:
         if (direction != 0) {
            state = 4;
         }
      case 4:
      }

      boolean internal_traverse = this.traverse(direction, this.idleItemZoneWidth, this.idleItemZoneHeight);
      if (direction != 0) {
         this.repaint();
      }

      return internal_traverse;
   }

   private void callLocalTraverseOut() {
      switch(state) {
      case 0:
      case 1:
      case 2:
      default:
         break;
      case 3:
         state = 2;
         this.repaint();
         break;
      case 4:
         state = 3;
         this.traverseOut();
         this.repaint();
      }

   }

   private void setupOptionsMenu() {
      int numberOfOptions = this.idleItemCommands.length();
      if (numberOfOptions == 0) {
         this.optionLabels = null;
      } else {
         this.optionLabels = new String[numberOfOptions];

         for(int i = 0; i < numberOfOptions; ++i) {
            this.optionLabels[i] = this.getCommandLabelImpl(this.idleItemCommands.getCommand(i));
         }
      }

   }

   void callPaint() {
      if (state >= 2 && !this.hidden && this.idleItemImage != null) {
         this.paint(this.idleItemImage.getGraphics(), this.idleItemZoneWidth, this.idleItemZoneHeight);
         if (this.idleItemPixmap != null) {
            this.nativeUpdateCanvas(this.idleItemPixmap);
         }
      }

   }

   String getCommandLabelImpl(Command cmd) {
      int localTextId = -1;
      String label;
      if ((label = cmd.getLabel()).length() == 0) {
         switch(cmd.getCommandType()) {
         case 1:
            localTextId = 9;
            break;
         case 2:
            localTextId = 3;
            break;
         case 3:
            localTextId = 8;
            break;
         case 4:
            localTextId = 6;
            break;
         case 5:
            localTextId = 31;
            break;
         case 6:
            localTextId = 32;
            break;
         case 7:
            localTextId = 30;
            break;
         case 8:
            localTextId = 9;
         }

         return TextDatabase.getText(localTextId);
      } else {
         return label;
      }
   }

   private static void consumeIdleItemScreenEvent(int type, int param) {
      synchronized(calloutLock) {
         IdleItemCommandListener icl = null;
         if (currentIdleItem != null) {
            icl = currentIdleItem.getIdleItemCommandListener();
         }

         IdleEventListener iel = getIdleItemEventListener();
         switch(type) {
         case 1:
         case 2:
         case 3:
         case 4:
            idleItemTraverse(type);
            break;
         case 5:
            if (currentIdleItem != null && currentIdleItem.selectCommand != null && icl != null) {
               icl.commandAction(currentIdleItem.selectCommand, currentIdleItem);
            }
            break;
         case 6:
            if (currentIdleItem != null) {
               Command cmd = currentIdleItem.idleItemCommands.getCommand(param - 1);
               if (icl != null) {
                  icl.commandAction(cmd, currentIdleItem);
               }
            }
            break;
         case 7:
            if ((param & 128) != 0) {
               state = 1;
               if (iel != null) {
                  iel.eventAction(0);
               }
            }

            if (currentIdleItem != null) {
               if ((param & 16) != 0) {
                  currentIdleItem.callLocalShowNotify();
               } else {
                  currentIdleItem.callLocalHideNotify();
               }

               if ((param & 32) != 0) {
                  currentIdleItem.callLocalTraverse(param & 15);
               } else {
                  currentIdleItem.callLocalTraverseOut();
               }

               if ((param & 64) != 0) {
                  currentIdleItem.callSizeChanged();
               }

               if ((param & 256) != 0) {
                  currentIdleItem.callPaint();
               }
            }
            break;
         case 8:
            if (currentIdleItem != null) {
               currentIdleItem.callPaint();
            }
            break;
         case 9:
            if (iel != null) {
               iel.eventAction(3);
            }
            break;
         case 10:
            if (iel != null) {
               TextDatabase.reinitialise();
               iel.eventAction(4);
               if (currentIdleItem != null) {
                  currentIdleItem.updateOptions();
               }
            }
            break;
         case 11:
            if (iel != null) {
               iel.eventAction(2);
            }
         case 12:
         default:
            break;
         case 13:
            state = 0;
            if (currentIdleItem != null) {
               currentIdleItem.callRemovedFromDisplay();
               currentIdleItem.nativeDestroyImageBuffer();
            }

            currentIdleItem = null;
            if (iel != null) {
               iel.eventAction(1);
            }
         }

      }
   }

   private static void idleItemTraverse(int type) {
      if (currentIdleItem != null) {
         boolean result = currentIdleItem.callLocalTraverse(type);
         if (!result) {
            currentIdleItem.callLocalTraverseOut();
         }
      }

   }

   private native boolean nativeConnectToScreen();

   private native boolean nativeCreateImageBuffer();

   private native boolean nativeGenerateUpdate(boolean var1, String var2);

   private native void nativeGenerateSetOptions();

   private native boolean nativeUpdateCanvas(Pixmap var1);

   private native void nativeDestroyImageBuffer();

   static {
      nativeStaticInitialiser();
   }

   private final class CommandVector {
      static final int DEFAULT_VECTOR_SIZE = 0;
      static final int REALLOCATE_GROWTH_SIZE = 1;
      private Command[] commands = null;
      private int length = 0;

      public CommandVector() {
         this.commands = new Command[0];
         this.length = 0;
      }

      public boolean insertCommand(int atIndex, Command newCommand) {
         boolean result = false;
         int new_length = this.length + 1;
         if (this.indexOfCommand(newCommand) == -1) {
            if (atIndex > this.length) {
               new_length = atIndex + 1;
               this.resize(new_length);
            } else if (atIndex < 0) {
               atIndex = this.length;
            }

            Command[] src = this.commands;
            if (this.commands.length < new_length) {
               this.commands = new Command[src.length + 1];
               System.arraycopy(src, 0, this.commands, 0, atIndex);
            }

            for(int i = this.length; i > atIndex; --i) {
               this.commands[i] = src[i - 1];
            }

            this.length = new_length;
            this.commands[atIndex] = newCommand;
            result = true;
         }

         return result;
      }

      public boolean removeCommand(Command oldCommand) {
         if (oldCommand == null) {
            return false;
         } else {
            int index = this.indexOfCommand(oldCommand);
            if (index != -1) {
               this.removeCommandAt(index);
               return true;
            } else {
               return false;
            }
         }
      }

      public Command removeCommandAt(int index) {
         if (index >= 0 && index < this.length) {
            Command returnCommand = this.commands[index];
            --this.length;

            while(index < this.length) {
               this.commands[index] = this.commands[index + 1];
               ++index;
            }

            this.commands[this.length] = null;
            return returnCommand;
         } else {
            return null;
         }
      }

      public int indexOfCommand(Command command) {
         for(int i = 0; i < this.length; ++i) {
            if (command == this.commands[i]) {
               return i;
            }
         }

         return -1;
      }

      public int length() {
         return this.length;
      }

      public boolean isNotEmpty() {
         return this.length > 0;
      }

      public Command getCommand(int index) {
         return index >= 0 && index < this.length ? this.commands[index] : null;
      }

      private void resize(int newSize) {
         if (newSize > this.commands.length) {
            Command[] nca = new Command[newSize];
            System.arraycopy(this.commands, 0, nca, 0, this.length);
            this.commands = nca;
         }

      }
   }

   private static class IdleItemEventConsumer implements EventConsumer {
      public static void register() {
      }

      public void consumeEvent(int category, int type, int param) {
         if (category == 15) {
            IdleItem.consumeIdleItemScreenEvent(type, param);
         }

      }

      static {
         IdleItem.IdleItemEventConsumer singletonInstance = new IdleItem.IdleItemEventConsumer();
         EventProducer eventProducer = InitJALM.s_getEventProducer();
         eventProducer.attachEventConsumer(15, singletonInstance);
      }
   }
}
