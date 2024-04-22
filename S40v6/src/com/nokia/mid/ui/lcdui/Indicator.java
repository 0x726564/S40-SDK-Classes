package com.nokia.mid.ui.lcdui;

import com.nokia.mid.impl.isa.ui.DisplayAccess;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.util.SharedObjects;
import javax.microedition.lcdui.Image;

public final class Indicator {
   public static final int ALERT = 0;
   public static final int INFO = 1;
   private final int indType;
   private boolean indActive;
   private Pixmap indIcon;
   private int indIndex;
   private Indicator indClone;

   public Indicator(int type, Image icon) {
      if (type != 0 && type != 1) {
         throw new IllegalArgumentException();
      } else {
         Image image = Image.createImage(icon);
         DisplayAccess displayAccessor = InitJALM.s_getMIDletAccessor().getDisplayAccessor();
         this.indIcon = displayAccessor.getImagePixmap(image);
         this.indType = type;
      }
   }

   Indicator(Indicator ind) {
      this.indType = ind.indType;
      this.indIcon = ind.indIcon;
      this.indActive = ind.indActive;
      this.indIndex = ind.indIndex;
      this.indClone = ind.indClone;
   }

   public boolean getActive() {
      return this.indActive;
   }

   public int getType() {
      return this.indType;
   }

   public void setActive(boolean active) {
      synchronized(IndicatorManager.indicatorManagerLock) {
         IndicatorManager indMgr = IndicatorManager.getIndicatorManager();
         if (active && !indMgr.indicatorPriorityList.contains(this)) {
            throw new IllegalStateException();
         } else {
            if (active != this.indActive) {
               this.indActive = active;
               IndicatorSharedObject indSharedObject = (IndicatorSharedObject)SharedObjects.getLock("IndicatorSharedObjectAPILock");
               synchronized(indSharedObject) {
                  indSharedObject.prioritiseIndicators();
               }
            }

         }
      }
   }

   public void setIcon(Image icon) {
      synchronized(IndicatorManager.indicatorManagerLock) {
         Image image = Image.createImage(icon);
         DisplayAccess displayAccessor = InitJALM.s_getMIDletAccessor().getDisplayAccessor();
         this.indIcon = displayAccessor.getImagePixmap(image);
         if (this.indActive) {
            IndicatorManager indMgr = IndicatorManager.getIndicatorManager();
            if (indMgr.indicatorPriorityList.contains(this)) {
               IndicatorSharedObject indSharedObject = (IndicatorSharedObject)SharedObjects.getLock("IndicatorSharedObjectAPILock");
               synchronized(indSharedObject) {
                  indSharedObject.updateActiveIndicatorListWithNewIcon(this, this.indIcon);
               }
            }
         }

      }
   }

   void setIndIndex(int index) {
      this.indIndex = index;
   }

   void setIndPixmap(Pixmap icon) {
      this.indIcon = icon;
   }

   void setIndicatorClone(Indicator ind) {
      this.indClone = ind;
   }

   Pixmap getIndPixmap() {
      return this.indIcon;
   }

   int getIndIndex() {
      return this.indIndex;
   }

   Indicator getIndicatorClone() {
      return this.indClone;
   }
}
