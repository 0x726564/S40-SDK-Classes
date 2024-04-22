package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.media.Control;

public final class ControlManager {
   private Hashtable ctrlTable = new Hashtable(2);

   public final void addControlsFromEncodedStrings(String[] controls) {
      for(int i = 0; i < controls.length; ++i) {
         String encStr = controls[i];
         int sepIdx = encStr.indexOf(59);
         String ctrlImplName = encStr.substring(sepIdx + 1);
         String ctrlFullName = encStr.substring(0, sepIdx);
         int cnSepIdx = ctrlFullName.lastIndexOf(46);
         String ctrlPkgName = ctrlFullName.substring(0, cnSepIdx);
         String ctrlClsName = ctrlFullName.substring(cnSepIdx + 1);

         try {
            Control ctrlImpl = (Control)Class.forName(ctrlImplName).newInstance();
            this.addControl(ctrlPkgName, ctrlClsName, ctrlImpl);
         } catch (ClassNotFoundException var11) {
         } catch (IllegalAccessException var12) {
         } catch (InstantiationException var13) {
         }
      }

   }

   public boolean isEmpty() {
      return this.ctrlTable.isEmpty();
   }

   public final void addControl(String packageName, String ctrlName, Control ctrl) {
      this.ctrlTable.put(new ControlManager.ControlManagerInfo(packageName, ctrlName), ctrl);
   }

   public final Control getControl(String packageName, String ctrlName) {
      return (Control)this.ctrlTable.get(new ControlManager.ControlManagerInfo(packageName, ctrlName));
   }

   public final Control[] getControls() {
      Enumeration en = this.ctrlTable.elements();
      Vector v = new Vector();

      while(en.hasMoreElements()) {
         Object c = en.nextElement();
         if (!v.contains(c)) {
            v.addElement(c);
         }
      }

      Control[] retCtrls = new Control[0];
      if (v.size() > 0) {
         retCtrls = new Control[v.size()];
         v.copyInto(retCtrls);
      }

      return retCtrls;
   }

   public final void setActiveState(boolean isActive) {
      Enumeration e = this.ctrlTable.elements();
      Vector uniqueCtrlTable = new Vector();

      while(e.hasMoreElements()) {
         Switchable ctrl = (Switchable)e.nextElement();
         if (!uniqueCtrlTable.contains(ctrl)) {
            uniqueCtrlTable.addElement(ctrl);
            if (isActive) {
               ctrl.activate();
            } else {
               ctrl.deactivate();
            }
         }
      }

   }

   class ControlManagerInfo {
      private String packageName;
      private String className;

      ControlManagerInfo(String packageName, String className) {
         this.packageName = packageName;
         this.className = className;
      }

      public boolean equals(Object obj) {
         try {
            ControlManager.ControlManagerInfo to = (ControlManager.ControlManagerInfo)obj;
            return this.packageName.equals(to.packageName) && this.className.equals(to.className);
         } catch (Exception var3) {
            return false;
         }
      }

      public int hashCode() {
         return this.packageName.hashCode() + this.className.hashCode();
      }
   }
}
