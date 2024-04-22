package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.EventProducer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class NativeTextHandler extends InlineTextHandler {
   private static final int KEY_PRESSED = 0;
   private static final int KEY_RELEASED = 1;
   private static final int KEY_REPEATED = 2;
   private static final int COMMAND_MODE_SELECTED = 0;
   private static final int COMMAND_MODE_KEY_PRESS = 1;
   private static final int COMMAND_MODE_KEY_RELEASE = 2;
   private static final int SOFTKEY_USAGE_SPECIFIED_BY_APPLICATION = 1;
   private static final int SOFTKEY_USAGE_SPECIFIED_BY_EDITOR = 2;
   private static final int SOFTKEY_USAGE_EMPTY_LABEL = 3;
   private static final int EDITOR_OPTIONS_CONTROL_SHOW_ALL = 1;
   private static final int EDITOR_OPTIONS_CONTROL_SHOW_EDITOR = 2;
   private static final int EDITOR_OPTIONS_CONTROL_SHOW_READONLY = 3;
   private static final int EDITOR_OPTIONS_CONTROL_DICTIONARY = 4;
   private Vector inlineEditorCommandVector;
   private Command[] extraCommands;
   private static final Command emptyLSKCommand = new Command(13, "");
   private static final Command emptyMSKCommand = new Command(9, "");
   private static final Command emptyRSKCommand = new Command(10, "");
   private int nativeHandle;
   private boolean isMidletCommandsSupported;
   private Command commandLSK;
   private Command commandRSK;
   private Command commandMSK;
   private static int appId = nativeStaticInitialize();

   private NativeTextHandler(TextEditor newOwner, int newMaxSize, int newConstraints, String newInitialInputMode, String newText, int newCursorPosition, boolean newFocus, boolean suppressCallbacks) {
      this.inlineEditorCommandVector = new Vector();
      this.extraCommands = new Command[0];
      this.isMidletCommandsSupported = true;
      this.owner = newOwner;
      this.maxSize = newMaxSize;
      this.constraints = newConstraints;
      this.initialInputMode = newInitialInputMode;
      this.nativeHandle = this.nativeCreateNativeEditor(this.maxSize, this.constraints, suppressCallbacks, appId, this.initialInputMode);
      NativeTextHandler.NativeEditorEventConsumer.register(this, this.nativeHandle);
      this.setString(newText, newCursorPosition);
      this.setFocus(newFocus);
      if (this.owner != null) {
         this.nativeSetCursorWrap(this.owner.getCursorWrap());
      }

   }

   NativeTextHandler(InlineTextHandler textHandler) {
      this(textHandler.getOwner(), textHandler.getMaxSize(), textHandler.getConstraints(), textHandler.getInitialInputMode(), textHandler.getString(), textHandler.getCursorPosition(), textHandler.isFocused(), textHandler.getOwner() == null);
   }

   public String getString() {
      return this.nativeGetString();
   }

   public void setString(String newText, int newCursorPosition) {
      this.nativeSetString(newText, newCursorPosition);
   }

   public void setConstraints(int newConstraints) {
      super.setConstraints(newConstraints);
      this.nativeSetConstraints(newConstraints);
   }

   public void setMaxSize(int newMaxSize) {
      super.setMaxSize(newMaxSize);
      this.nativeSetMaxSize(newMaxSize);
   }

   public void setInitialInputMode(String newInitialInputMode) {
      super.setInitialInputMode(newInitialInputMode);
      if (!this.isFocused()) {
         this.nativeSetInitialInputMode(newInitialInputMode, this.constraints);
      }

   }

   public int size() {
      return this.nativeSize();
   }

   public int getCursorPosition() {
      return this.nativeGetCursorPosition();
   }

   public void keyPressed(int keyCode, int keyDataIdx) {
      if (DeviceInfo.isSoftkey(keyCode)) {
         int commandIndex = this.getCommandIndex(keyCode);
         if (commandIndex >= 0) {
            this.nativeExecuteOption(commandIndex, 1);
         }
      } else {
         this.nativeProcessKey(keyDataIdx, 0);
      }

   }

   public void keyReleased(int keyCode, int keyDataIdx) {
      if (DeviceInfo.isSoftkey(keyCode)) {
         int commandIndex = this.getCommandIndex(keyCode);
         if (commandIndex >= 0 && this.size() > 0) {
            this.nativeExecuteOption(commandIndex, 2);
         }
      } else {
         this.nativeProcessKey(keyDataIdx, 1);
      }

   }

   public void keyRepeated(int keyCode, int keyDataIdx) {
      if (!DeviceInfo.isSoftkey(keyCode)) {
         this.nativeProcessKey(keyDataIdx, 2);
      }

   }

   public void longLeftSoftKeyPressed() {
      this.nativeToggleEditorDictionary();
   }

   public boolean isT9modeAllowed() {
      boolean result = false;
      int constraintsModifier = this.constraints & -65536;
      if ((this.constraints & '\uffff') == 0 && (constraintsModifier & 65536) == 0 && (constraintsModifier & 131072) == 0 && (constraintsModifier & 524288) == 0 && (constraintsModifier & 262144) == 0) {
         result = true;
      }

      return result;
   }

   public boolean launchExtraCommand(Command command) {
      int commandIndex = this.inlineEditorCommandVector.indexOf(command);
      boolean isListCommand = false;
      if (commandIndex >= 0) {
         this.nativeExecuteOption(commandIndex, 0);
         isListCommand = this.nativeIsListCommand(commandIndex);
         this.owner.setKeepRootOptionsMenu(isListCommand);
      }

      return isListCommand;
   }

   public void paint(com.nokia.mid.impl.isa.ui.gdi.Graphics ng, int x, int y, int width, int height) {
      this.nativePaint(ng, x, y, width, height);
   }

   public void destroy() {
      super.destroy();
      NativeTextHandler.NativeEditorEventConsumer.unregister(this.nativeHandle);
      this.nativeDestroyNativeEditor();
      this.nativeHandle = 0;
   }

   public Command[] getExtraCommands() {
      return this.extraCommands;
   }

   public void reconstructExtraCommands() {
      super.reconstructExtraCommands();
      this.repopulateOptionsMenu();
   }

   public int getHeight(int width, int maxAvailableHeight) {
      return this.nativeGetRowInfo(width, maxAvailableHeight);
   }

   public boolean midletCommandsSupported() {
      return this.isMidletCommandsSupported;
   }

   public void setFocus(boolean newFocus) {
      super.setFocus(newFocus);
      this.nativeSetFocus(newFocus);
   }

   public void setActive(boolean newActive) {
      super.setActive(newActive);
      this.nativeSetFocus(newActive);
   }

   public void setVisible(boolean newVisible) {
      this.nativeSetVisible(newVisible);
   }

   public void processTextEditorEvent(int pendingCallbackMask) {
      if (pendingCallbackMask == -1) {
         pendingCallbackMask = this.nativeGetPendingCallbackMask();
      }

      if (this.owner != null) {
         this.owner.processTextEditorEvent(pendingCallbackMask);
      }

   }

   public static int getTextHeight(int maxSize, String text, int width, int maxAvailableHeight, int constraints) {
      NativeTextHandler tmpNativeHandler = new NativeTextHandler((TextEditor)null, maxSize, constraints, "", text, 0, false, true);
      int textHeight = tmpNativeHandler.getHeight(width, maxAvailableHeight);
      tmpNativeHandler.destroy();
      return textHeight;
   }

   private int getCommandIndex(int keyCode) {
      int commandIndex = -1;
      Command command = null;
      if (keyCode == -6) {
         command = this.commandLSK;
      } else if (keyCode == -7) {
         command = this.commandRSK;
      } else if (keyCode == -5) {
         command = this.commandMSK;
      }

      if (command != null) {
         commandIndex = this.inlineEditorCommandVector.indexOf(command);
      }

      return commandIndex;
   }

   private void repopulateOptionsMenu() {
      int numInlineEditorCommands = this.nativeGetCommands();
      this.commandLSK = null;
      this.commandMSK = null;
      this.commandRSK = null;
      this.inlineEditorCommandVector = new Vector(numInlineEditorCommands);
      this.extraCommands = null;
      this.isMidletCommandsSupported = true;
      Vector softkeyCommandVector = new Vector(DeviceInfo.getNumSoftButtons());
      Vector optionListCommandVector = new Vector(numInlineEditorCommands);
      int suggestedSoftkeyUsageLSK = this.nativeGetSuggestedSoftkeyUsage(0);
      int suggestedSoftkeyUsageRSK = this.nativeGetSuggestedSoftkeyUsage(2);
      int suggestedSoftkeyUsageMSK = this.nativeGetSuggestedSoftkeyUsage(1);
      if (suggestedSoftkeyUsageLSK == 3) {
         this.commandLSK = emptyLSKCommand;
         softkeyCommandVector.addElement(this.commandLSK);
      }

      if (suggestedSoftkeyUsageMSK == 3) {
         this.commandMSK = emptyMSKCommand;
         softkeyCommandVector.addElement(this.commandMSK);
      }

      if (suggestedSoftkeyUsageRSK == 3) {
         this.commandRSK = emptyRSKCommand;
         softkeyCommandVector.addElement(this.commandRSK);
      }

      int i;
      if (numInlineEditorCommands > 0) {
         boolean isParent = false;

         for(int i = 0; i < numInlineEditorCommands; ++i) {
            String commandString = this.nativeGetCommandString(i);
            i = this.nativeGetCommandType(i);
            isParent = this.nativeIsParentCommand(i);
            Command command;
            if (i == 9 && this.commandMSK == null && suggestedSoftkeyUsageMSK == 2) {
               command = new Command(9, commandString);
               this.commandMSK = command;
               softkeyCommandVector.addElement(command);
            } else if (i == 10 && this.commandRSK == null && suggestedSoftkeyUsageRSK == 2) {
               command = new Command(10, commandString);
               this.commandRSK = command;
               softkeyCommandVector.addElement(command);
            } else if (i == 13 && this.commandLSK == null && suggestedSoftkeyUsageLSK == 2) {
               command = new Command(13, commandString);
               this.commandLSK = command;
               softkeyCommandVector.addElement(command);
            } else {
               command = new Command(12, commandString);
               if (!this.nativeIsDimmedCommand(i)) {
                  optionListCommandVector.addElement(command);
               }
            }

            command.setIsParent(isParent);
            this.inlineEditorCommandVector.addElement(command);
         }
      }

      if (this.nativeGetEditorOptionControl() == 2) {
         this.isMidletCommandsSupported = false;
      }

      int numExtraCommands = softkeyCommandVector.size() + optionListCommandVector.size();
      if (numExtraCommands > 0) {
         this.extraCommands = new Command[numExtraCommands];
         int extraCommandIndex = 0;

         for(i = 0; i < softkeyCommandVector.size(); ++i) {
            this.extraCommands[extraCommandIndex++] = (Command)softkeyCommandVector.elementAt(i);
         }

         for(i = 0; i < optionListCommandVector.size(); ++i) {
            this.extraCommands[extraCommandIndex++] = (Command)optionListCommandVector.elementAt(i);
         }
      }

   }

   void initiateCall() {
      synchronized(Display.LCDUILock) {
         if ((this.getConstraints() & '\uffff') == 3) {
            String phoneNumberString = this.getString();
            if (!phoneNumberString.equals("")) {
               this.nativeInitiateCall(phoneNumberString);
            }
         }

      }
   }

   private static native int nativeStaticInitialize();

   private native void nativeInitiateCall(String var1);

   private native int nativeGetPendingCallbackMask();

   private native void nativeDestroyNativeEditor();

   private native int nativeCreateNativeEditor(int var1, int var2, boolean var3, int var4, String var5);

   private native void nativeSetCursorWrap(int var1);

   private native void nativeProcessKey(int var1, int var2);

   private native void nativeSetFocus(boolean var1);

   private native void nativeSetVisible(boolean var1);

   private native void nativeExecuteOption(int var1, int var2);

   private native int nativeGetCommands();

   private native String nativeGetCommandString(int var1);

   private native boolean nativeIsDimmedCommand(int var1);

   private native boolean nativeIsListCommand(int var1);

   private native boolean nativeIsParentCommand(int var1);

   private native int nativeGetSuggestedSoftkeyUsage(int var1);

   private native int nativeGetEditorOptionControl();

   private native int nativeGetCommandType(int var1);

   private native int nativeGetRowInfo(int var1, int var2);

   private native void nativeSetString(String var1, int var2);

   private native String nativeGetString();

   private native int nativeGetCursorPosition();

   private native void nativeSetConstraints(int var1);

   private native void nativeSetInitialInputMode(String var1, int var2);

   private native int nativeSetMaxSize(int var1);

   private native int nativeSize();

   private native void nativePaint(com.nokia.mid.impl.isa.ui.gdi.Graphics var1, int var2, int var3, int var4, int var5);

   private native void nativeToggleEditorDictionary();

   private static class NativeEditorEventConsumer implements EventConsumer {
      private static Hashtable editorObjectsCollection = new Hashtable();

      public static void register(NativeTextHandler handler, int nativeHandle) {
         Integer key = new Integer(nativeHandle);
         editorObjectsCollection.put(key, handler);
      }

      public static void unregister(int nativeHandle) {
         Integer key = new Integer(nativeHandle);
         editorObjectsCollection.remove(key);
      }

      public void consumeEvent(int category, int type, int param) {
         if (category == 10) {
            int nativeHandle = param;
            synchronized(Display.LCDUILock) {
               NativeTextHandler nativeTextHandler = this.getNativeTextHandler(nativeHandle);
               if (nativeTextHandler != null) {
                  nativeTextHandler.processTextEditorEvent(-1);
               } else if ((type & 4096) != 0) {
                  Enumeration e = editorObjectsCollection.elements();

                  while(e.hasMoreElements()) {
                     NativeTextHandler handlerObject = (NativeTextHandler)e.nextElement();
                     if (handlerObject != null && handlerObject.owner.isFocused()) {
                        handlerObject.processTextEditorEvent(type);
                     }
                  }
               }

            }
         }
      }

      private NativeTextHandler getNativeTextHandler(int nativeHandle) {
         NativeTextHandler handlerObject = null;
         Integer key = new Integer(nativeHandle);
         Object obj = editorObjectsCollection.get(key);
         if (obj != null) {
            handlerObject = (NativeTextHandler)obj;
         }

         return handlerObject;
      }

      static {
         NativeTextHandler.NativeEditorEventConsumer singletonInstance = new NativeTextHandler.NativeEditorEventConsumer();
         EventProducer eventProducer = InitJALM.s_getEventProducer();
         eventProducer.attachEventConsumer(10, singletonInstance);
      }
   }
}
