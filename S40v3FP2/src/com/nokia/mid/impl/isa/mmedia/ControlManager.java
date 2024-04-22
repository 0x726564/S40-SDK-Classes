package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.media.Control;

public final class ControlManager {
   private Hashtable ctrlTable = new Hashtable(2);

   public final void addControlsFromEncodedStrings(String[] var1) {
      MediaPrefs.nTrace("CM::addControlsFromEncodedStrings:");

      for(int var2 = 0; var2 < var1.length; ++var2) {
         String var3 = var1[var2];
         int var4 = var3.indexOf(59);
         String var5 = var3.substring(var4 + 1);
         String var6 = var3.substring(0, var4);
         int var7 = var6.lastIndexOf(46);
         String var8 = var6.substring(0, var7);
         String var9 = var6.substring(var7 + 1);

         try {
            MediaPrefs.nTrace("Control name: [" + var6 + "]");
            MediaPrefs.nTrace("Control impl: [" + var5 + "]");
            Control var10 = (Control)Class.forName(var5).newInstance();
            MediaPrefs.nTrace("Added [" + var8 + "].[" + var9 + "]");
            this.addControl(var8, var9, var10);
         } catch (ClassNotFoundException var11) {
         } catch (IllegalAccessException var12) {
         } catch (InstantiationException var13) {
         }
      }

      MediaPrefs.nTrace("CM::addControlsFromEncodedString:END");
   }

   public final void addControl(String var1, String var2, Control var3) {
      this.ctrlTable.put(new ControlManager.ControlManagerInfo(var1, var2), var3);
   }

   public final Control getControl(String var1, String var2) {
      MediaPrefs.nTrace("CM::getControl::[" + var1 + "].[" + var2 + "]");
      return (Control)this.ctrlTable.get(new ControlManager.ControlManagerInfo(var1, var2));
   }

   public final Control[] getControls() {
      Enumeration var1 = this.ctrlTable.elements();
      Vector var2 = new Vector();

      while(var1.hasMoreElements()) {
         Object var3 = var1.nextElement();
         if (!var2.contains(var3)) {
            var2.addElement(var3);
         }
      }

      Control[] var4 = new Control[0];
      if (var2.size() > 0) {
         var4 = new Control[var2.size()];
         var2.copyInto(var4);
      }

      return var4;
   }

   public final void setActiveState(boolean var1) {
      Enumeration var2 = this.ctrlTable.elements();
      Vector var3 = new Vector();

      while(var2.hasMoreElements()) {
         Switchable var4 = (Switchable)var2.nextElement();
         if (!var3.contains(var4)) {
            var3.addElement(var4);
            if (var1) {
               var4.activate();
            } else {
               var4.deactivate();
            }
         }
      }

   }

   class ControlManagerInfo {
      private String packageName;
      private String className;

      ControlManagerInfo(String var2, String var3) {
         this.packageName = var2;
         this.className = var3;
      }

      public boolean equals(Object var1) {
         try {
            ControlManager.ControlManagerInfo var2 = (ControlManager.ControlManagerInfo)var1;
            return this.packageName.equals(var2.packageName) && this.className.equals(var2.className);
         } catch (Exception var3) {
            return false;
         }
      }

      public int hashCode() {
         return this.packageName.hashCode() + this.className.hashCode();
      }
   }
}
