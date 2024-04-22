package com.nokia.mid.ui.lcdui;

import com.arm.cldc.mas.GlobalLock;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.Vector;

public final class IndicatorSharedObject extends GlobalLock {
   private Vector indicatorManagerList = new Vector();
   private Indicator[] currentlyActiveIndicatorList = new Indicator[UIStyle.getNumberOfDynamicStatusIndicators() + UIStyle.getNumberOfDynamicPowSaverStatusIndicators()];

   void appendIndicatorManagertoList(IndicatorManager indmgr) {
      this.trimIndicatorManagerfromList();
      int size = this.indicatorManagerList.size();
      if (size == 0) {
         this.indicatorManagerList.addElement(new WeakReference(indmgr));
      } else {
         this.addIndicatorManager(indmgr, size - 1);
      }

   }

   void removeIndicatorManagerfromList(IndicatorManager indmgr) {
      Enumeration e = this.indicatorManagerList.elements();

      while(e.hasMoreElements()) {
         WeakReference ref = (WeakReference)e.nextElement();
         IndicatorManager indmgrinList = (IndicatorManager)ref.get();
         if (indmgrinList == indmgr) {
            this.indicatorManagerList.removeElement(ref);
            break;
         }
      }

      this.prioritiseIndicators();
   }

   IndicatorManager getIndicatorManagerForMidletAppId(int midletAppId) {
      IndicatorManager retIndmgr = null;
      Enumeration e = this.indicatorManagerList.elements();

      while(e.hasMoreElements()) {
         WeakReference ref = (WeakReference)e.nextElement();
         IndicatorManager indmgr = (IndicatorManager)ref.get();
         if (indmgr != null && indmgr.midletAppId == midletAppId) {
            retIndmgr = indmgr;
            break;
         }
      }

      return retIndmgr;
   }

   void prioritiseIndicators() {
      int arrayLength = this.currentlyActiveIndicatorList.length;
      Indicator[] newlyActiveIndicatorList = new Indicator[arrayLength];
      int index = 0;
      byte[] countsArray = new byte[]{1, 0};
      int index = this.getNewlyActiveIndicators(newlyActiveIndicatorList, index, 0, countsArray);
      if (index < arrayLength) {
         this.getNewlyActiveIndicators(newlyActiveIndicatorList, index, 1, countsArray);
      }

      for(int i = 0; i < arrayLength; ++i) {
         Pixmap newPixmap = null;
         Pixmap currentPixmap = null;
         if (newlyActiveIndicatorList[i] != null) {
            newPixmap = newlyActiveIndicatorList[i].getIndPixmap();
         }

         if (this.currentlyActiveIndicatorList[i] != null) {
            currentPixmap = this.currentlyActiveIndicatorList[i].getIndPixmap();
         }

         if (currentPixmap != newPixmap) {
            this.updateActiveIndicatorList(newlyActiveIndicatorList);
            break;
         }
      }

   }

   void updateActiveIndicatorListWithNewIcon(Indicator ind, Pixmap icon) {
      int updateIndex = -1;

      for(int i = 0; i < this.currentlyActiveIndicatorList.length; ++i) {
         if (this.currentlyActiveIndicatorList[i] != null && ind.getIndIndex() == this.currentlyActiveIndicatorList[i].getIndIndex()) {
            updateIndex = i;
            break;
         }
      }

      if (updateIndex != -1) {
         Indicator[] addIndArray = new Indicator[]{null, null};
         Indicator[] removeIndArray = new Indicator[]{null, null};
         this.currentlyActiveIndicatorList[updateIndex].setIndPixmap(icon);
         addIndArray[0] = this.currentlyActiveIndicatorList[updateIndex];
         removeIndArray[0] = addIndArray[0];
         if (ind.getType() == 0 && this.currentlyActiveIndicatorList[updateIndex].getIndicatorClone() != null && ind.getIndIndex() <= UIStyle.getNumberOfDynamicPowSaverStatusIndicators()) {
            this.currentlyActiveIndicatorList[updateIndex + 1].setIndPixmap(icon);
            addIndArray[1] = this.currentlyActiveIndicatorList[updateIndex + 1];
            removeIndArray[1] = addIndArray[1];
         }

         this.nativeUpdateIndicators(addIndArray, removeIndArray);
      }

   }

   private int getNewlyActiveIndicators(Indicator[] activeIndicatorArray, int index, int indType, byte[] countsArray) {
      int maxPowerSaverCount = UIStyle.getNumberOfDynamicPowSaverStatusIndicators();
      int maxIndCount = UIStyle.getNumberOfDynamicStatusIndicators();
      int infoIndCount = 0;
      Enumeration e = this.indicatorManagerList.elements();

      while(true) {
         IndicatorManager indmgr;
         do {
            if (!e.hasMoreElements()) {
               return index;
            }

            WeakReference ref = (WeakReference)e.nextElement();
            indmgr = (IndicatorManager)ref.get();
         } while(indmgr == null);

         Enumeration ei = indmgr.indicatorPriorityList.elements();

         while(ei.hasMoreElements()) {
            Indicator ind = (Indicator)ei.nextElement();
            if (ind.getActive() && ind.getType() == indType) {
               if (indType == 1) {
                  if (infoIndCount >= maxIndCount - countsArray[1]) {
                     return index;
                  }

                  ++infoIndCount;
               }

               Indicator tmpInd = new Indicator(ind);
               tmpInd.setIndIndex(countsArray[0]);
               ind.setIndIndex(countsArray[0]);
               activeIndicatorArray[index] = tmpInd;
               ++index;
               ++countsArray[0];
               if (indType == 0 && index < activeIndicatorArray.length && countsArray[1] < maxPowerSaverCount) {
                  ++countsArray[1];
                  Indicator powerSaverInd = new Indicator(ind);
                  tmpInd.setIndicatorClone(powerSaverInd);
                  powerSaverInd.setIndIndex(maxIndCount + countsArray[1]);
                  activeIndicatorArray[index] = powerSaverInd;
                  ++index;
               }
            }

            if (index >= activeIndicatorArray.length) {
               return index;
            }
         }
      }
   }

   private void updateActiveIndicatorList(Indicator[] newlyActiveIndicatorList) {
      Vector addIndVector = new Vector();
      Vector removeIndVector = new Vector();

      for(int i = 0; i < newlyActiveIndicatorList.length; ++i) {
         if (this.currentlyActiveIndicatorList[i] == null) {
            addIndVector.addElement(newlyActiveIndicatorList[i]);
         } else if (newlyActiveIndicatorList[i] == null) {
            removeIndVector.addElement(this.currentlyActiveIndicatorList[i]);
         } else if (newlyActiveIndicatorList[i].getIndPixmap() != this.currentlyActiveIndicatorList[i].getIndPixmap()) {
            removeIndVector.addElement(this.currentlyActiveIndicatorList[i]);
            addIndVector.addElement(newlyActiveIndicatorList[i]);
         }
      }

      Indicator[] addIndicatorArray = new Indicator[addIndVector.size()];
      addIndVector.copyInto(addIndicatorArray);
      Indicator[] removeIndicatorArray = new Indicator[removeIndVector.size()];
      removeIndVector.copyInto(removeIndicatorArray);
      this.nativeUpdateIndicators(addIndicatorArray, removeIndicatorArray);
      System.arraycopy(newlyActiveIndicatorList, 0, this.currentlyActiveIndicatorList, 0, newlyActiveIndicatorList.length);
   }

   private void addIndicatorManager(IndicatorManager indmgr, int index) {
      WeakReference ref = (WeakReference)this.indicatorManagerList.elementAt(index);
      IndicatorManager indmgrInList = (IndicatorManager)ref.get();
      if (indmgr.midletDomainType < indmgrInList.midletDomainType) {
         if (index == 0) {
            this.indicatorManagerList.insertElementAt(new WeakReference(indmgr), 0);
         } else {
            this.addIndicatorManager(indmgr, index--);
         }
      } else {
         this.indicatorManagerList.insertElementAt(new WeakReference(indmgr), index + 1);
      }

   }

   void trimIndicatorManagerfromList() {
      Enumeration e = this.indicatorManagerList.elements();

      while(e.hasMoreElements()) {
         WeakReference ref = (WeakReference)e.nextElement();
         IndicatorManager indmgrinList = (IndicatorManager)ref.get();
         if (indmgrinList == null) {
            this.indicatorManagerList.removeElement(ref);
         }
      }

      this.prioritiseIndicators();
   }

   private native void nativeUpdateIndicators(Indicator[] var1, Indicator[] var2);
}
