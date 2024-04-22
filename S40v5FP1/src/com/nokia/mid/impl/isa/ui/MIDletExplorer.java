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

   static void s_setMIDletList(List ml) {
      if (s_mExplorer != null) {
         s_mExplorer.setMIDletList(ml);
      } else {
         s_mList = ml;
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

   public final void destroyApp(boolean unconditional) {
      throw new RuntimeException("MIDletExplorer ERROR: destroyApp() called ");
   }

   private void init() {
      String _locale = System.getProperty("microedition.locale");
      String _title = MIDletManager.s_getAppProperty("Nokia-MIDlet-Name-" + _locale);
      if (_title == null) {
         _title = MIDletManager.s_getAppProperty("MIDlet-Name");
      }

      this.mManager = MIDletManager.s_getMIDletManager();
      this.mStateMutex = this.mManager.getStateMutex();
      this.display = Display.getDisplay(this);
      this.mInfoList = MIDletManager.s_getMIDletInfoList();
      this.initMIDletList(_title);
      this.display.setCurrent(this.mList);
   }

   private void initMIDletList(String title) {
      String _label = TextDatabase.getText(3);
      this.exitToJarCmd = new Command(_label, 2, 1);
      this.ensureMIDletListExists();
      this.mList.setTitle(title);
      this.mList.addCommand(this.exitToJarCmd);
      this.mList.setCommandListener(this);
      MIDletAccess ma = InitJALM.s_getMIDletAccessor();
      DisplayAccess da = ma.getDisplayAccessor();
      Pixmap defaultIconPixmap = Pixmap.createPixmap(0);

      for(int _mIdx = 0; _mIdx < this.mInfoList.size(); ++_mIdx) {
         Image mIcon = null;
         MIDletRTInfo _mInfo = (MIDletRTInfo)this.mInfoList.elementAt(_mIdx);
         String iconName = _mInfo.getIconName();
         if (iconName != null) {
            try {
               mIcon = Image.createImage(iconName);
            } catch (IOException var11) {
               mIcon = da.createImage(defaultIconPixmap);
            }
         } else {
            mIcon = da.createImage(defaultIconPixmap);
         }

         this.mList.append(_mInfo.getName(), mIcon);
      }

   }

   public final void commandAction(Command cmd, Displayable d) {
      if (d == this.mList) {
         if (cmd == List.SELECT_COMMAND) {
            int _selIdx = this.mList.getSelectedIndex();
            MIDletRTInfo _mInfo = (MIDletRTInfo)this.mInfoList.elementAt(_selIdx);
            this.mManager.launchMIDlet(_mInfo);
            return;
         }

         if (cmd == this.exitToJarCmd) {
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

   private void setMIDletList(List ml) {
      if (this.mList == null) {
         this.mList = ml;
      } else {
         throw new SecurityException();
      }
   }
}
