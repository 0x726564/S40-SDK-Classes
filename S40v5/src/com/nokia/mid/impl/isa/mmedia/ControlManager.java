package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.media.Control;

public final class ControlManager {
   private Hashtable eK = new Hashtable(2);

   public final void addControlsFromEncodedStrings(String[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         String var3;
         int var4 = (var3 = var1[var2]).indexOf(59);
         String var5 = var3.substring(var4 + 1);
         var4 = (var3 = var3.substring(0, var4)).lastIndexOf(46);
         String var6 = var3.substring(0, var4);
         var3 = var3.substring(var4 + 1);

         try {
            Control var10 = (Control)Class.forName(var5).newInstance();
            this.addControl(var6, var3, var10);
         } catch (ClassNotFoundException var7) {
         } catch (IllegalAccessException var8) {
         } catch (InstantiationException var9) {
         }
      }

   }

   public final boolean isEmpty() {
      return this.eK.isEmpty();
   }

   public final void addControl(String var1, String var2, Control var3) {
      this.eK.put(new ControlManager.ControlManagerInfo(this, var1, var2), var3);
   }

   public final Control getControl(String var1, String var2) {
      return (Control)this.eK.get(new ControlManager.ControlManagerInfo(this, var1, var2));
   }

   public final Control[] getControls() {
      Enumeration var3 = this.eK.elements();
      Vector var1 = new Vector();

      while(var3.hasMoreElements()) {
         Object var2 = var3.nextElement();
         if (!var1.contains(var2)) {
            var1.addElement(var2);
         }
      }

      Control[] var4 = new Control[0];
      if (var1.size() > 0) {
         var4 = new Control[var1.size()];
         var1.copyInto(var4);
      }

      return var4;
   }

   public final void setActiveState(boolean var1) {
      Enumeration var4 = this.eK.elements();
      Vector var2 = new Vector();

      while(var4.hasMoreElements()) {
         Switchable var3 = (Switchable)var4.nextElement();
         if (!var2.contains(var3)) {
            var2.addElement(var3);
            if (var1) {
               var3.activate();
            } else {
               var3.deactivate();
            }
         }
      }

   }

   class ControlManagerInfo {
      private String gz;
      private String className;

      ControlManagerInfo(ControlManager var1, String var2, String var3) {
         this.gz = var2;
         this.className = var3;
      }

      public final boolean equals(Object var1) {
         try {
            ControlManager.ControlManagerInfo var3 = (ControlManager.ControlManagerInfo)var1;
            return this.gz.equals(var3.gz) && this.className.equals(var3.className);
         } catch (Exception var2) {
            return false;
         }
      }

      public final int hashCode() {
         return this.gz.hashCode() + this.className.hashCode();
      }
   }
}
