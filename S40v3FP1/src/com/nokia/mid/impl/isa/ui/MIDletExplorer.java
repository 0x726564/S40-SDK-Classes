package com.nokia.mid.impl.isa.ui;

import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

final class MIDletExplorer extends MIDlet implements CommandListener {
   private static MIDletExplorer s_mExplorer;
   private static List s_mList;
   private Display display;
   private MIDletManager mManager;
   private Vector mInfoList;
   private List mList;
   private Command exitToJarCmd;
   private Object mStateMutex;

   static void s_setMIDletList(List var0) {
      if (s_mExplorer != null) {
         s_mExplorer.setMIDletList(var0);
      } else {
         s_mList = var0;
      }

   }

   MIDletExplorer() {
      if (s_mExplorer == null) {
         s_mExplorer = this;
         this.init();
      } else {
         throw new SecurityException();
      }
   }

   public final void startApp() {
      throw new RuntimeException("MIDletExplorer ERROR: startApp() called ");
   }

   public final void pauseApp() {
      throw new RuntimeException("MIDletExplorer ERROR: pauseApp() called ");
   }

   public final void destroyApp(boolean var1) {
      throw new RuntimeException("MIDletExplorer ERROR: destroyApp() called ");
   }

   private void init() {
      String var1 = System.getProperty("microedition.locale");
      String var2 = MIDletManager.s_getAppProperty("Nokia-MIDlet-Name-" + var1);
      if (var2 == null) {
         var2 = MIDletManager.s_getAppProperty("MIDlet-Name");
      }

      this.mManager = MIDletManager.s_getMIDletManager();
      this.mStateMutex = this.mManager.getStateMutex();
      this.display = Display.getDisplay(this);
      this.mInfoList = MIDletManager.s_getMIDletInfoList();
      this.initMIDletList(var2);
      this.display.setCurrent(this.mList);
   }

   private void initMIDletList(String var1) {
      String var2 = TextDatabase.getText(3);
      this.exitToJarCmd = new Command(var2, 2, 1);
      this.ensureMIDletListExists();
      this.mList.setTitle(var1);
      this.mList.addCommand(this.exitToJarCmd);
      this.mList.setCommandListener(this);
      MIDletAccess var3 = InitJALM.s_getMIDletAccessor();
      DisplayAccess var4 = var3.getDisplayAccessor();
      Pixmap var6 = Pixmap.createPixmap(0);

      for(int var9 = 0; var9 < this.mInfoList.size(); ++var9) {
         Image var8 = null;
         MIDletRTInfo var5 = (MIDletRTInfo)this.mInfoList.elementAt(var9);
         String var7 = var5.getIconName();
         if (var7 != null) {
            try {
               var8 = Image.createImage(var7);
            } catch (IOException var11) {
               var8 = var4.createImage(var6);
            }
         } else {
            var8 = var4.createImage(var6);
         }

         this.mList.append(var5.getName(), var8);
      }

   }

   public final void commandAction(Command var1, Displayable var2) {
      if (var2 == this.mList) {
         if (var1 == List.SELECT_COMMAND) {
            int var3 = this.mList.getSelectedIndex();
            MIDletRTInfo var4 = (MIDletRTInfo)this.mInfoList.elementAt(var3);
            this.mManager.launchMIDlet(var4);
            return;
         }

         if (var1 == this.exitToJarCmd) {
            this.mManager.selectEndMIDletSuite();
         }
      }

   }

   final void displayMIDletList() {
      synchronized(this.mStateMutex) {
         this.display = Display.getDisplay(this);
         this.display.setCurrent(this.mList);
      }
   }

   private void ensureMIDletListExists() {
      new List((String)null, 1);
      if (this.mList == null) {
         if (s_mList == null) {
            throw new RuntimeException();
         }

         this.mList = s_mList;
      }

   }

   private void setMIDletList(List var1) {
      if (this.mList == null) {
         this.mList = var1;
      } else {
         throw new SecurityException();
      }
   }
}
