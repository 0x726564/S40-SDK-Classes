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
   private static MIDletExplorer cH;
   private static List cI;
   private Display cJ;
   private MIDletManager v;
   private Vector cK;
   private List cL;
   private Command cM;
   private Object b;

   static void s_setMIDletList(List var0) {
      if (cH != null) {
         cH.setMIDletList(var0);
      } else {
         cI = var0;
      }
   }

   MIDletExplorer() {
      if (cH == null) {
         cH = this;
         String var1 = System.getProperty("microedition.locale");
         if ((var1 = MIDletManager.s_getAppProperty("Nokia-MIDlet-Name-" + var1)) == null) {
            var1 = MIDletManager.s_getAppProperty("MIDlet-Name");
         }

         this.v = null;
         this.b = this.v.getStateMutex();
         this.cJ = Display.getDisplay(this);
         this.cK = null;
         this.s(var1);
         this.cJ.setCurrent(this.cL);
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

   private void s(String var1) {
      String var2 = TextDatabase.getText(3);
      this.cM = new Command(var2, 2, 1);
      new List((String)null, 1);
      if (this.cL == null) {
         if (cI == null) {
            throw new RuntimeException();
         }

         this.cL = cI;
      }

      this.cL.setTitle(var1);
      this.cL.addCommand(this.cM);
      this.cL.setCommandListener(this);
      DisplayAccess var7 = InitJALM.s_getMIDletAccessor().getDisplayAccessor();
      Pixmap var3 = Pixmap.createPixmap(0);

      for(int var5 = 0; var5 < this.cK.size(); ++var5) {
         MIDletRTInfo var8;
         Image var9;
         label29: {
            String var4 = null;
            if ((var4 = (var8 = (MIDletRTInfo)this.cK.elementAt(var5)).getIconName()) != null) {
               try {
                  var9 = Image.createImage(var4);
                  break label29;
               } catch (IOException var6) {
               }
            }

            var9 = var7.createImage(var3);
         }

         this.cL.append(var8.getName(), var9);
      }

   }

   public final void commandAction(Command var1, Displayable var2) {
      if (var2 == this.cL) {
         if (var1 == List.SELECT_COMMAND) {
            int var3 = this.cL.getSelectedIndex();
            MIDletRTInfo var4 = (MIDletRTInfo)this.cK.elementAt(var3);
            this.v.a(var4);
            return;
         }

         if (var1 == this.cM) {
            this.v.Z();
         }
      }

   }

   final void x() {
      synchronized(this.b) {
         this.cJ = Display.getDisplay(this);
         this.cJ.setCurrent(this.cL);
      }
   }

   private void setMIDletList(List var1) {
      if (this.cL == null) {
         this.cL = var1;
      } else {
         throw new SecurityException();
      }
   }
}
