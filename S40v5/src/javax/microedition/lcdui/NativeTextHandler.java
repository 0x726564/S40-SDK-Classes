package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import java.util.Hashtable;
import java.util.Vector;

class NativeTextHandler extends InlineTextHandler {
   private Vector bc;
   private Command[] bd;
   private static final Command be = new Command(13, "");
   private static final Command bf = new Command(9, "");
   private static final Command bg = new Command(10, "");
   private int bh;
   private boolean bi;
   private boolean bj;
   private Command bk;
   private Command bl;
   private Command bm;
   private static int bn = nativeStaticInitialize();

   private NativeTextHandler(TextEditor var1, int var2, int var3, String var4, String var5, int var6, boolean var7, boolean var8) {
      this.bc = new Vector();
      this.bd = new Command[0];
      this.bi = true;
      this.J = var1;
      this.I = var2;
      this.H = var3;
      this.G = var4;
      this.bh = this.nativeCreateNativeEditor(this.I, this.H, var8, bn, this.G);
      NativeTextHandler.NativeEditorEventConsumer.a(this, this.bh);
      this.nativeSetString(var5, var6);
      this.setFocus(var7);
      if (this.J != null) {
         this.nativeSetCursorWrap(this.J.getCursorWrap());
      }

   }

   NativeTextHandler(InlineTextHandler var1) {
      this(var1.getOwner(), var1.getMaxSize(), var1.getConstraints(), var1.getInitialInputMode(), var1.getString(), var1.getCursorPosition(), var1.isFocused(), var1.getOwner() == null);
   }

   public String getString() {
      return this.nativeGetString();
   }

   public final void a(String var1, int var2) {
      this.nativeSetString(var1, var2);
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
      if (!this.isFocused()) {
         this.nativeSetInitialInputMode(var1, this.H);
      }

   }

   public final int size() {
      return this.nativeSize();
   }

   public int getCursorPosition() {
      return this.nativeGetCursorPosition();
   }

   public final void d(int var1, int var2) {
      if (DeviceInfo.isSoftkey(var1)) {
         if ((var1 = this.getCommandIndex(var1)) >= 0) {
            this.nativeExecuteOption(var1, 1);
         }

      } else {
         this.nativeProcessKey(var2, 0);
      }
   }

   public final void e(int var1, int var2) {
      if (DeviceInfo.isSoftkey(var1)) {
         if ((var1 = this.getCommandIndex(var1)) >= 0) {
            this.nativeExecuteOption(var1, 2);
         }

      } else {
         this.nativeProcessKey(var2, 1);
      }
   }

   public final void f(int var1, int var2) {
      if (!DeviceInfo.isSoftkey(var1)) {
         this.nativeProcessKey(var2, 2);
      }

   }

   public final boolean a(Command var1) {
      int var2 = this.bc.indexOf(var1);
      this.bj = false;
      if (var2 >= 0) {
         this.nativeExecuteOption(var2, 0);
         if (this.nativeIsListCommand(var2)) {
            this.bj = true;
            this.J.setKeepRootOptionsMenu(true);
         } else {
            this.J.setKeepRootOptionsMenu(false);
         }
      }

      return this.bj;
   }

   public final void a(com.nokia.mid.impl.isa.ui.gdi.Graphics var1, int var2, int var3, int var4, int var5) {
      this.nativePaint(var1, var2, var3, var4, var5);
   }

   public final void destroy() {
      super.destroy();
      NativeTextHandler.NativeEditorEventConsumer.I(this.bh);
      this.nativeDestroyNativeEditor();
      this.bh = 0;
   }

   public Command[] getExtraCommands() {
      return this.bd;
   }

   public final void l() {
      super.l();
      NativeTextHandler var11;
      int var1 = (var11 = this).nativeGetCommands();
      var11.bk = null;
      var11.bm = null;
      var11.bl = null;
      var11.bc = new Vector(var1);
      var11.bd = null;
      var11.bi = true;
      Vector var2 = new Vector(DeviceInfo.getNumSoftButtons());
      Vector var3 = new Vector(var1);
      int var4 = var11.nativeGetSuggestedSoftkeyUsage(0);
      int var5 = var11.nativeGetSuggestedSoftkeyUsage(2);
      int var6 = var11.nativeGetSuggestedSoftkeyUsage(1);
      if (var4 == 3) {
         var11.bk = be;
         var2.addElement(var11.bk);
      }

      if (var6 == 3) {
         var11.bm = bf;
         var2.addElement(var11.bm);
      }

      if (var5 == 3) {
         var11.bl = bg;
         var2.addElement(var11.bl);
      }

      int var9;
      if (var1 > 0) {
         for(int var10 = 0; var10 < var1; ++var10) {
            String var8 = var11.nativeGetCommandString(var10);
            Command var7;
            if ((var9 = var11.nativeGetCommandType(var10)) == 9 && var11.bm == null && var6 == 2) {
               var7 = new Command(9, var8);
               var11.bm = var7;
               var2.addElement(var7);
            } else if (var9 == 10 && var11.bl == null && var5 == 2) {
               var7 = new Command(10, var8);
               var11.bl = var7;
               var2.addElement(var7);
            } else if (var9 == 13 && var11.bk == null && var4 == 2) {
               var7 = new Command(13, var8);
               var11.bk = var7;
               var2.addElement(var7);
            } else {
               var7 = new Command(12, var8);
               if (!var11.nativeIsDimmedCommand(var10)) {
                  var3.addElement(var7);
               }
            }

            var11.bc.addElement(var7);
         }
      }

      if (var11.nativeGetEditorOptionControl() == 2) {
         var11.bi = false;
      }

      int var13;
      if ((var13 = var2.size() + var3.size()) > 0) {
         var11.bd = new Command[var13];
         int var12 = 0;

         for(var9 = 0; var9 < var2.size(); ++var9) {
            var11.bd[var12++] = (Command)var2.elementAt(var9);
         }

         for(var9 = 0; var9 < var3.size(); ++var9) {
            var11.bd[var12++] = (Command)var3.elementAt(var9);
         }
      }

   }

   public final int a(int var1, int var2) {
      return this.nativeGetRowInfo(var1, var2);
   }

   public final boolean m() {
      return this.bi;
   }

   public void setFocus(boolean var1) {
      super.setFocus(var1);
      this.nativeSetFocus(var1);
   }

   public final void H() {
      int var1 = this.nativeGetPendingCallbackMask();
      if (this.J != null) {
         this.J.k(var1);
      }

   }

   public static int a(int var0, String var1, int var2, int var3) {
      NativeTextHandler var4;
      var0 = (var4 = new NativeTextHandler((TextEditor)null, var0, 0, "", var1, 0, false, true)).nativeGetRowInfo(var2, var3);
      var4.destroy();
      return var0;
   }

   private int getCommandIndex(int var1) {
      int var2 = -1;
      Command var3 = null;
      if (var1 == -6) {
         var3 = this.bk;
      } else if (var1 == -7) {
         var3 = this.bl;
      } else if (var1 == -5) {
         var3 = this.bm;
      }

      if (var3 != null) {
         var2 = this.bc.indexOf(var3);
      }

      return var2;
   }

   private static native int nativeStaticInitialize();

   private native int nativeGetPendingCallbackMask();

   private native void nativeDestroyNativeEditor();

   private native int nativeCreateNativeEditor(int var1, int var2, boolean var3, int var4, String var5);

   private native void nativeSetCursorWrap(int var1);

   private native void nativeProcessKey(int var1, int var2);

   private native void nativeSetFocus(boolean var1);

   private native void nativeExecuteOption(int var1, int var2);

   private native int nativeGetCommands();

   private native String nativeGetCommandString(int var1);

   private native boolean nativeIsDimmedCommand(int var1);

   private native boolean nativeIsListCommand(int var1);

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

   private static class NativeEditorEventConsumer implements EventConsumer {
      private static Hashtable hp = new Hashtable();

      public static void a(NativeTextHandler var0, int var1) {
         Integer var2 = new Integer(var1);
         hp.put(var2, var0);
      }

      public static void I(int var0) {
         Integer var1 = new Integer(var0);
         hp.remove(var1);
      }

      public void consumeEvent(int var1, int var2, int var3) {
         if (var1 == 10) {
            var1 = var3;
            synchronized(Display.hG) {
               NativeTextHandler var5;
               if ((var5 = this.getNativeTextHandler(var1)) != null) {
                  var5.H();
               }

            }
         }
      }

      private NativeTextHandler getNativeTextHandler(int var1) {
         NativeTextHandler var2 = null;
         Integer var3 = new Integer(var1);
         Object var4;
         if ((var4 = hp.get(var3)) != null) {
            var2 = (NativeTextHandler)var4;
         }

         return var2;
      }

      static {
         NativeTextHandler.NativeEditorEventConsumer var0 = new NativeTextHandler.NativeEditorEventConsumer();
         InitJALM.s_getEventProducer().attachEventConsumer(10, var0);
      }
   }
}
