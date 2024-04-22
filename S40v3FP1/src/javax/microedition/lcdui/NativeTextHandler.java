package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.EventProducer;
import com.nokia.mid.impl.isa.ui.InitJALM;
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

   private NativeTextHandler(TextEditor var1, int var2, int var3, String var4, String var5, int var6, boolean var7, boolean var8) {
      this.inlineEditorCommandVector = new Vector();
      this.extraCommands = new Command[0];
      this.nativeHandle = 0;
      this.isMidletCommandsSupported = true;
      this.owner = var1;
      this.maxSize = var2;
      this.constraints = var3;
      this.nativeHandle = this.nativeCreateNativeEditor(this.maxSize, this.constraints, var8);
      NativeTextHandler.NativeEditorEventConsumer.register(this, this.nativeHandle);
      this.setInitialInputMode(var4);
      this.setString(var5, var6);
      this.setFocus(var7);
   }

   NativeTextHandler(InlineTextHandler var1) {
      this(var1.getOwner(), var1.getMaxSize(), var1.getConstraints(), var1.getInitialInputMode(), var1.getString(), var1.getCursorPosition(), var1.isFocused(), var1.getOwner() == null);
   }

   public String getString() {
      return this.nativeGetString();
   }

   public void setString(String var1, int var2) {
      this.nativeSetString(var1, var2);
   }

   public void setCursorPosition(int var1) {
      this.nativeSetCursorPosition(var1);
   }

   public void setConstraints(int var1) {
      super.setConstraints(var1);
      this.nativeSetConstraints(var1);
   }

   public void setMaxSize(int var1) {
      super.setMaxSize(var1);
      this.nativeSetMaxSize(var1);
   }

   public void setInitialInputMode(String var1) {
      super.setInitialInputMode(var1);
      this.nativeSetInitialInputMode(var1);
   }

   public int size() {
      return this.nativeSize();
   }

   public int getCursorPosition() {
      return this.nativeGetCursorPosition();
   }

   public void keyPressed(int var1, int var2) {
      if (DeviceInfo.isSoftkey(var1)) {
         int var3 = this.getCommandIndex(var1);
         if (var3 >= 0) {
            this.nativeExecuteOption(var3, 1);
         }
      } else {
         this.nativeProcessKey(var2, 0);
      }

   }

   public void keyReleased(int var1, int var2) {
      if (DeviceInfo.isSoftkey(var1)) {
         int var3 = this.getCommandIndex(var1);
         if (var3 >= 0) {
            this.nativeExecuteOption(var3, 2);
         }
      } else {
         this.nativeProcessKey(var2, 1);
      }

   }

   public void keyRepeated(int var1, int var2) {
      if (!DeviceInfo.isSoftkey(var1)) {
         this.nativeProcessKey(var2, 2);
      }

   }

   public boolean launchExtraCommand(Command var1) {
      int var2 = this.inlineEditorCommandVector.indexOf(var1);
      if (var2 >= 0) {
         this.nativeExecuteOption(var2, 0);
      }

      return false;
   }

   public void paint(com.nokia.mid.impl.isa.ui.gdi.Graphics var1, int var2, int var3, int var4, int var5) {
      this.nativePaint(var1, var2, var3, var4, var5);
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

   public int getHeight(int var1, int var2) {
      return this.nativeGetRowInfo(var1, var2);
   }

   public boolean midletCommandsSupported() {
      return this.isMidletCommandsSupported;
   }

   public void setFocus(boolean var1) {
      super.setFocus(var1);
      this.nativeSetFocus(var1);
   }

   public void processTextEditorEvent(int var1) {
      int var2 = this.nativeGetPendingCallbackMask();
      if (this.owner != null) {
         this.owner.processTextEditorEvent(var2);
      }

   }

   public static int getTextHeight(int var0, String var1, int var2, int var3) {
      NativeTextHandler var4 = new NativeTextHandler((TextEditor)null, var0, 0, "", var1, 0, false, true);
      int var5 = var4.getHeight(var2, var3);
      var4.destroy();
      return var5;
   }

   private int getCommandIndex(int var1) {
      int var2 = -1;
      Command var3 = null;
      if (var1 == -6) {
         var3 = this.commandLSK;
      } else if (var1 == -7) {
         var3 = this.commandRSK;
      } else if (var1 == -5) {
         var3 = this.commandMSK;
      }

      if (var3 != null) {
         var2 = this.inlineEditorCommandVector.indexOf(var3);
      }

      return var2;
   }

   private void repopulateOptionsMenu() {
      int var1 = this.nativeGetCommands();
      this.commandLSK = null;
      this.commandMSK = null;
      this.commandRSK = null;
      this.inlineEditorCommandVector = new Vector(var1);
      this.extraCommands = null;
      this.isMidletCommandsSupported = true;
      Vector var2 = new Vector(DeviceInfo.getNumSoftButtons());
      Vector var3 = new Vector(var1);
      int var4 = this.nativeGetSuggestedSoftkeyUsage(0);
      int var5 = this.nativeGetSuggestedSoftkeyUsage(2);
      int var6 = this.nativeGetSuggestedSoftkeyUsage(1);
      if (var4 == 3) {
         this.commandLSK = emptyLSKCommand;
         var2.addElement(this.commandLSK);
      }

      if (var6 == 3) {
         this.commandMSK = emptyMSKCommand;
         var2.addElement(this.commandMSK);
      }

      if (var5 == 3) {
         this.commandRSK = emptyRSKCommand;
         var2.addElement(this.commandRSK);
      }

      int var9;
      if (var1 > 0) {
         for(int var10 = 0; var10 < var1; ++var10) {
            String var8 = this.nativeGetCommandString(var10);
            var9 = this.nativeGetCommandType(var10);
            Command var7;
            if (var9 == 9 && this.commandMSK == null && var6 == 2) {
               var7 = new Command(9, var8);
               this.commandMSK = var7;
               var2.addElement(var7);
            } else if (var9 == 10 && this.commandRSK == null && var5 == 2) {
               var7 = new Command(10, var8);
               this.commandRSK = var7;
               var2.addElement(var7);
            } else if (var9 == 13 && this.commandLSK == null && var4 == 2) {
               var7 = new Command(13, var8);
               this.commandLSK = var7;
               var2.addElement(var7);
            } else {
               var7 = new Command(12, var8);
               boolean var11 = this.nativeIsDimmedCommand(var10);
               if (!var11) {
                  var3.addElement(var7);
               }
            }

            this.inlineEditorCommandVector.addElement(var7);
         }
      }

      if (this.nativeGetEditorOptionControl() == 2) {
         this.isMidletCommandsSupported = false;
      }

      int var13 = var2.size() + var3.size();
      if (var13 > 0) {
         this.extraCommands = new Command[var13];
         int var12 = 0;

         for(var9 = 0; var9 < var2.size(); ++var9) {
            this.extraCommands[var12++] = (Command)var2.elementAt(var9);
         }

         for(var9 = 0; var9 < var3.size(); ++var9) {
            this.extraCommands[var12++] = (Command)var3.elementAt(var9);
         }
      }

   }

   private static native void nativeStaticInitialize();

   private native int nativeGetPendingCallbackMask();

   private native void nativeDestroyNativeEditor();

   private native int nativeCreateNativeEditor(int var1, int var2, boolean var3);

   private native void nativeProcessKey(int var1, int var2);

   private native void nativeSetFocus(boolean var1);

   private native void nativeExecuteOption(int var1, int var2);

   private native int nativeGetCommands();

   private native String nativeGetCommandString(int var1);

   private native boolean nativeIsDimmedCommand(int var1);

   private native int nativeGetSuggestedSoftkeyUsage(int var1);

   private native int nativeGetEditorOptionControl();

   private native int nativeGetCommandType(int var1);

   private native int nativeGetRowInfo(int var1, int var2);

   private native void nativeSetString(String var1, int var2);

   private native String nativeGetString();

   private native int nativeGetCursorPosition();

   private native void nativeSetCursorPosition(int var1);

   private native void nativeSetConstraints(int var1);

   private native void nativeSetInitialInputMode(String var1);

   private native int nativeSetMaxSize(int var1);

   private native int nativeSize();

   private native void nativePaint(com.nokia.mid.impl.isa.ui.gdi.Graphics var1, int var2, int var3, int var4, int var5);

   static {
      nativeStaticInitialize();
   }

   private static class NativeEditorEventConsumer implements EventConsumer {
      private static Hashtable editorObjectsCollection = new Hashtable();

      public static void register(NativeTextHandler var0, int var1) {
         Integer var2 = new Integer(var1);
         editorObjectsCollection.put(var2, var0);
      }

      public static void unregister(int var0) {
         Integer var1 = new Integer(var0);
         editorObjectsCollection.remove(var1);
      }

      public void consumeEvent(int var1, int var2, int var3) {
         if (var1 == 10) {
            int var4 = var2;
            synchronized(Display.LCDUILock) {
               NativeTextHandler var6 = this.getNativeTextHandler(var4);
               if (var6 != null) {
                  var6.processTextEditorEvent(var3);
               }

            }
         }
      }

      public int size() {
         return editorObjectsCollection.size();
      }

      private NativeTextHandler getNativeTextHandler(int var1) {
         NativeTextHandler var2 = null;
         Integer var3 = new Integer(var1);
         Object var4 = editorObjectsCollection.get(var3);
         if (var4 != null) {
            var2 = (NativeTextHandler)var4;
         }

         return var2;
      }

      static {
         NativeTextHandler.NativeEditorEventConsumer var0 = new NativeTextHandler.NativeEditorEventConsumer();
         EventProducer var1 = InitJALM.s_getEventProducer();
         var1.attachEventConsumer(10, var0);
      }
   }
}
