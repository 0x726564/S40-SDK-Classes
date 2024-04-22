package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.InitJALM;

public class TextBox extends Screen {
   private static final Command searchCmd = new Command(11, 35);
   static final Command OK = new Command(4, 6);
   static final Command BACK = new Command(2, 3);
   static final Command[] extraCommands;
   private static TextBox.EditorAdapter adapter;
   private String midletNameWithSeparator = null;
   private TextHandler textHandler = null;
   TextField contentsObserver = null;
   private int posOfFirstDisplayedChar = 0;
   private Ticker tickerTB;
   private boolean editorAdapterUsed = false;
   private boolean fullScreenMode = false;
   private String titleWithSeparator;

   public TextBox(String var1, String var2, int var3, int var4) {
      synchronized(Display.LCDUILock) {
         this.textHandler = new TextHandler(var2, var3, var4);
         this.setTitle(var1);
         this.setNativeDelegate(true);
         this.addOrRemoveSearchCommand();
         this.fullScreenMode = this.isFullScreenMode();
      }
   }

   public void delete(int var1, int var2) {
      synchronized(Display.LCDUILock) {
         this.textHandler.delete(var1, var2);
         if (this.editorAdapterUsed) {
            adapter.setBufferContent();
         }

      }
   }

   public int getCaretPosition() {
      synchronized(Display.LCDUILock) {
         return this.textHandler.getCaretPosition();
      }
   }

   public int getChars(char[] var1) {
      synchronized(Display.LCDUILock) {
         return this.textHandler.getChars(var1);
      }
   }

   public int getConstraints() {
      synchronized(Display.LCDUILock) {
         return this.textHandler.getConstraints();
      }
   }

   public int getMaxSize() {
      synchronized(Display.LCDUILock) {
         return this.textHandler.getMaxSize();
      }
   }

   public String getString() {
      synchronized(Display.LCDUILock) {
         return this.textHandler.getString();
      }
   }

   public void insert(char[] var1, int var2, int var3, int var4) {
      synchronized(Display.LCDUILock) {
         this.textHandler.insert(var1, var2, var3, var4);
         if (this.editorAdapterUsed) {
            adapter.setBufferContent();
         }

      }
   }

   public void insert(String var1, int var2) {
      synchronized(Display.LCDUILock) {
         this.textHandler.insert(var1, var2);
         if (this.editorAdapterUsed) {
            adapter.setBufferContent();
         }

      }
   }

   public void setChars(char[] var1, int var2, int var3) {
      synchronized(Display.LCDUILock) {
         this.textHandler.setChars(var1, var2, var3);
         if (this.editorAdapterUsed) {
            adapter.setBufferContent();
         }

      }
   }

   public void setConstraints(int var1) {
      synchronized(Display.LCDUILock) {
         int var3 = var1 & '\uffff';
         int var4 = var1 & -65536;
         if ((this.textHandler.getConstraints() & '\uffff') != var3) {
            this.textHandler.setConstraints(var1);
            this.addOrRemoveSearchCommand();
            this.fullScreenMode = this.isFullScreenMode();
            this.restartAdaptor();
         } else if ((this.textHandler.getConstraints() & -65536) != var4) {
            this.textHandler.setConstraints(var1);
            if (this.editorAdapterUsed) {
               boolean var5 = this.isFullScreenMode();
               if (var5 != this.fullScreenMode) {
                  this.fullScreenMode = var5;
                  this.restartAdaptor();
               } else {
                  adapter.setConstraintsFlags();
               }
            } else {
               this.fullScreenMode = this.isFullScreenMode();
            }
         }

      }
   }

   public int setMaxSize(int var1) {
      synchronized(Display.LCDUILock) {
         int var3 = this.textHandler.setMaxSize(var1);
         this.restartAdaptor();
         return var3;
      }
   }

   public void setString(String var1) {
      synchronized(Display.LCDUILock) {
         this.textHandler.setString(var1);
         if (this.editorAdapterUsed) {
            adapter.setBufferContent();
         }

      }
   }

   public int size() {
      synchronized(Display.LCDUILock) {
         return this.textHandler.size();
      }
   }

   public void setInitialInputMode(String var1) {
      synchronized(Display.LCDUILock) {
         this.textHandler.setInitialInputMode(var1);
      }
   }

   public Ticker getTicker() {
      return this.tickerTB;
   }

   void setTitleImpl(String var1) {
      if (var1 != this.title) {
         this.title = var1;
         this.titleWithSeparator = Item.getStringWithSeparator(this.title);
         if (this.editorAdapterUsed) {
            boolean var2 = this.isFullScreenMode();
            if (var2 != this.fullScreenMode) {
               this.fullScreenMode = var2;
               this.restartAdaptor();
            } else {
               adapter.setPromptText();
            }
         } else {
            this.fullScreenMode = this.isFullScreenMode();
         }

      }
   }

   void setTickerImpl(Ticker var1) {
      this.tickerTB = var1;
   }

   boolean addCommandImpl(Command var1) {
      boolean var2 = super.addCommandImpl(var1);
      if (var2 && this.editorAdapterUsed) {
         adapter.setCommands();
      }

      return var2;
   }

   boolean removeCommandImpl(Command var1) {
      boolean var2 = super.removeCommandImpl(var1);
      if (var2 && this.editorAdapterUsed) {
         adapter.setCommands();
      }

      return var2;
   }

   void callShowNotify(Display var1) {
      super.callShowNotify(var1);
      synchronized(Display.LCDUILock) {
         if (this.contentsObserver == null) {
            this.midletNameWithSeparator = Item.getStringWithSeparator(this.myDisplay.getMIDletName());
         }

         if (adapter == null) {
            adapter = new TextBox.EditorAdapter();
         }

         if (this.contentsObserver != null) {
            this.updateSoftkeys(true);
         }

         adapter.start(this);
         adapter.processKeys();
      }
   }

   void callHideNotifyInProgress(Display var1) {
      super.callHideNotifyInProgress(var1);
      synchronized(Display.LCDUILock) {
         adapter.stop();
      }
   }

   void setContents(TextHandler var1) {
      if (var1 != null) {
         this.textHandler = var1;
         this.addOrRemoveSearchCommand();
         this.fullScreenMode = this.isFullScreenMode();
      }

   }

   TextHandler getContents() {
      return this.textHandler;
   }

   void setContentsObserver(TextField var1) {
      if (var1 != null) {
         this.contentsObserver = var1;
      }

   }

   void callDelegateEvent(int var1, int var2) {
      Command var3 = null;
      ItemCommandListener var4 = null;
      CommandListener var5 = null;
      boolean var6 = false;
      synchronized(Display.LCDUILock) {
         switch(var1) {
         case 1:
            var3 = this.optionMenuCommands.getCommand(var2);
            if (this.contentsObserver == null) {
               var5 = this.commandListener;
            } else if (var3 == OK) {
               this.contentsObserver.notifySystemScreenExitRequest(true);
               var6 = true;
            } else if (var3 == BACK) {
               this.contentsObserver.notifySystemScreenExitRequest(false);
               var6 = true;
            } else {
               var4 = this.contentsObserver.commandListener;
            }
            break;
         case 2:
            var6 = true;
            if (this.contentsObserver == null) {
               InitJALM.s_getExitManager().exit();
            } else {
               this.contentsObserver.notifySystemScreenExitRequest(false);
            }
         }
      }

      if (var5 != null) {
         synchronized(Display.calloutLock) {
            var5.commandAction(var3, this);
         }
      } else if (var4 != null) {
         synchronized(Display.calloutLock) {
            var4.commandAction(var3, this.contentsObserver);
         }
      }

      if (!var6 && this.editorAdapterUsed) {
         synchronized(Display.LCDUILock) {
            if (this.editorAdapterUsed && this.myDisplay.isScreenIdle()) {
               adapter.processKeys();
            }
         }
      }

   }

   void callProcessKeys() {
      adapter.processKeys();
   }

   Command[] getExtraCommands() {
      return this.contentsObserver != null ? extraCommands : null;
   }

   Item getCurrentItem() {
      return this.contentsObserver;
   }

   private void restartAdaptor() {
      if (this.editorAdapterUsed) {
         adapter.stop();
         adapter = null;
         System.gc();
         adapter = new TextBox.EditorAdapter();
         adapter.start(this);
         adapter.processKeys();
      }

   }

   private void addOrRemoveSearchCommand() {
      int var1 = this.textHandler.getConstraints() & '\uffff';
      int var2 = this.textHandler.getConstraints() & 131072;
      if ((var1 == 3 || var1 == 1) && var2 == 0) {
         this.addCommandImpl(searchCmd);
      } else {
         this.removeCommandImpl(searchCmd);
      }

   }

   private boolean isFullScreenMode() {
      return (this.textHandler.getConstraints() & '\uffff') == 0 && (this.textHandler.getConstraints() & -65536 & 65536) == 0 && this.title == null;
   }

   static {
      extraCommands = new Command[]{OK, BACK};
      adapter = null;
   }

   class EditorAdapter {
      TextBox client = null;

      EditorAdapter() {
         this.editorAdapterInit();
      }

      native void editorAdapterInit();

      void start(TextBox var1) {
         if (var1 != null && this.client == null) {
            this.client = var1;
            this.create();
            this.client.editorAdapterUsed = true;
         }

      }

      void stop() {
         if (this.client != null) {
            this.client.editorAdapterUsed = false;
            this.destroy();
            this.client = null;
         }

      }

      native void setConstraintsFlags();

      native void setBufferContent();

      native void setCommands();

      native void setPromptText();

      private native void create();

      private native void destroy();

      private native void processKeys();
   }
}
