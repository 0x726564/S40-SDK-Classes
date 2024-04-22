package com.nokia.mid.ui.lcdui;

import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.EventProducer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.util.SharedObjects;
import java.util.Vector;

public final class IndicatorManager {
   static final IndicatorSharedObject mainSharedObject = (IndicatorSharedObject)SharedObjects.get("IndicatorSharedObjectAPILock", "com.nokia.mid.ui.lcdui.IndicatorSharedObject");
   static final Object indicatorManagerLock = new Object();
   static final EventProducer indicatorEventDispatcher = InitJALM.s_getEventProducer();
   private static IndicatorManager indicatorManager;
   final EventConsumer indMgrEventConsumer;
   int midletAppId;
   int midletDomainType;
   Vector indicatorPriorityList = new Vector();
   private int infoIndicatorStartOffset = -1;

   private IndicatorManager() {
      this.nativeInitialiseIndicatorManager();
      mainSharedObject.appendIndicatorManagertoList(this);
      this.indMgrEventConsumer = new IndicatorManager.IndicatorManagerListener();
      indicatorEventDispatcher.attachEventConsumer(12, this.indMgrEventConsumer);
   }

   public static IndicatorManager getIndicatorManager() {
      synchronized(mainSharedObject) {
         if (indicatorManager == null) {
            indicatorManager = new IndicatorManager();
         }
      }

      return indicatorManager;
   }

   public int appendIndicator(Indicator indicator, boolean active) {
      int index = -1;
      if (indicator == null) {
         throw new NullPointerException();
      } else {
         synchronized(indicatorManagerLock) {
            if (this == indicatorManager && !this.indicatorPriorityList.contains(indicator)) {
               switch(indicator.getType()) {
               case 0:
                  if (this.infoIndicatorStartOffset != -1) {
                     index = this.infoIndicatorStartOffset;
                     this.indicatorPriorityList.insertElementAt(indicator, index);
                     ++this.infoIndicatorStartOffset;
                  } else {
                     this.indicatorPriorityList.addElement(indicator);
                     index = this.indicatorPriorityList.size() - 1;
                  }
                  break;
               case 1:
                  this.indicatorPriorityList.addElement(indicator);
                  if (this.infoIndicatorStartOffset == -1) {
                     this.infoIndicatorStartOffset = this.indicatorPriorityList.size() - 1;
                  }

                  index = this.indicatorPriorityList.size() - 1 - this.infoIndicatorStartOffset;
               }

               indicator.setActive(active);
               return index;
            } else {
               throw new IllegalStateException();
            }
         }
      }
   }

   public int deleteIndicator(Indicator indicator) {
      int index = true;
      if (indicator == null) {
         throw new NullPointerException();
      } else {
         synchronized(indicatorManagerLock) {
            if (this != indicatorManager) {
               throw new IllegalStateException();
            } else {
               int index = this.indicatorPriorityList.indexOf(indicator);
               if (index != -1) {
                  this.removeIndicator(indicator, indicator.getType(), index);
               }

               return index;
            }
         }
      }
   }

   public void deleteIndicator(int indicatorType, int index) {
      synchronized(indicatorManagerLock) {
         Indicator indicatorAtindicatorNum = this.doBoundsCheckandGetIndicator(indicatorType, index);
         this.removeIndicator(indicatorAtindicatorNum, indicatorType, this.indicatorPriorityList.indexOf(indicatorAtindicatorNum));
      }
   }

   public Indicator getIndicator(int indicatorType, int index) {
      synchronized(indicatorManagerLock) {
         return this.doBoundsCheckandGetIndicator(indicatorType, index);
      }
   }

   public void insertIndicator(int index, Indicator indicator, boolean active) {
      int insertIndex = 0;
      if (indicator == null) {
         throw new NullPointerException();
      } else {
         synchronized(indicatorManagerLock) {
            if (this == indicatorManager && !this.indicatorPriorityList.contains(indicator)) {
               int size = this.indicatorPriorityList.size();
               switch(indicator.getType()) {
               case 0:
                  if (this.infoIndicatorStartOffset != -1) {
                     if (index > this.infoIndicatorStartOffset || index < 0) {
                        throw new IndexOutOfBoundsException();
                     }

                     ++this.infoIndicatorStartOffset;
                  } else if (index > size || index < 0) {
                     throw new IndexOutOfBoundsException();
                  }

                  insertIndex = index;
                  break;
               case 1:
                  if (this.infoIndicatorStartOffset != -1) {
                     int relativeOffset = this.infoIndicatorStartOffset + index;
                     if (relativeOffset > size || relativeOffset < this.infoIndicatorStartOffset) {
                        throw new IndexOutOfBoundsException();
                     }

                     insertIndex = relativeOffset;
                  } else {
                     if (index != 0) {
                        throw new IndexOutOfBoundsException();
                     }

                     this.infoIndicatorStartOffset = this.indicatorPriorityList.size();
                     insertIndex = this.infoIndicatorStartOffset;
                  }
               }

               this.indicatorPriorityList.insertElementAt(indicator, insertIndex);
               indicator.setActive(active);
            } else {
               throw new IllegalStateException();
            }
         }
      }
   }

   public void setIndicator(int index, Indicator indicator, boolean active) {
      if (indicator == null) {
         throw new NullPointerException();
      } else {
         synchronized(indicatorManagerLock) {
            if (this == indicatorManager && !this.indicatorPriorityList.contains(indicator)) {
               Indicator indicatorAtindicatorNum = this.doBoundsCheckandGetIndicator(indicator.getType(), index);
               this.indicatorPriorityList.setElementAt(indicator, this.indicatorPriorityList.indexOf(indicatorAtindicatorNum));
               indicator.setActive(active);
            } else {
               throw new IllegalStateException();
            }
         }
      }
   }

   public int size(int indicatorType) {
      int size = 0;
      if (indicatorType != 0 && indicatorType != 1) {
         throw new IllegalArgumentException();
      } else {
         synchronized(indicatorManagerLock) {
            if (this != indicatorManager) {
               throw new IllegalStateException();
            } else {
               switch(indicatorType) {
               case 0:
                  if (this.infoIndicatorStartOffset != -1) {
                     size = this.infoIndicatorStartOffset;
                  } else {
                     size = this.indicatorPriorityList.size();
                  }
                  break;
               case 1:
                  if (this.infoIndicatorStartOffset != -1) {
                     size = this.indicatorPriorityList.size() - this.infoIndicatorStartOffset;
                  }
               }

               return size;
            }
         }
      }
   }

   public void shutdownIndicatorManager() {
      synchronized(mainSharedObject) {
         if (indicatorManager == this) {
            mainSharedObject.removeIndicatorManagerfromList(this);
            indicatorEventDispatcher.detachEventConsumer(12, this.indMgrEventConsumer);
            indicatorManager = null;
            this.indicatorPriorityList = null;
            this.nativeCleanupIndicatorManager(this.midletAppId);
         }

      }
   }

   private Indicator doBoundsCheckandGetIndicator(int indicatorType, int index) {
      if (indicatorType != 0 && indicatorType != 1) {
         throw new IllegalArgumentException();
      } else if (this != indicatorManager) {
         throw new IllegalStateException();
      } else {
         int size = this.indicatorPriorityList.size();
         Indicator indicator = null;
         switch(indicatorType) {
         case 0:
            if (this.infoIndicatorStartOffset != -1) {
               if (index >= this.infoIndicatorStartOffset || index < 0) {
                  throw new IndexOutOfBoundsException();
               }
            } else if (index >= size || index < 0) {
               throw new IndexOutOfBoundsException();
            }

            indicator = (Indicator)this.indicatorPriorityList.elementAt(index);
            break;
         case 1:
            if (this.infoIndicatorStartOffset == -1) {
               throw new IndexOutOfBoundsException();
            }

            int relativeOffset = this.infoIndicatorStartOffset + index;
            if (relativeOffset >= size || relativeOffset < this.infoIndicatorStartOffset) {
               throw new IndexOutOfBoundsException();
            }

            indicator = (Indicator)this.indicatorPriorityList.elementAt(relativeOffset);
         }

         return indicator;
      }
   }

   private void removeIndicator(Indicator indicator, int indicatorType, int index) {
      this.indicatorPriorityList.removeElementAt(index);
      if (this.indicatorPriorityList.isEmpty()) {
         this.infoIndicatorStartOffset = -1;
      } else if (indicatorType == 0) {
         if (this.infoIndicatorStartOffset != -1) {
            --this.infoIndicatorStartOffset;
         }
      } else if (indicatorType == 1 && this.infoIndicatorStartOffset == this.indicatorPriorityList.size()) {
         this.infoIndicatorStartOffset = -1;
      }

      indicator.setActive(false);
   }

   private native void nativeInitialiseIndicatorManager();

   private native void nativeCleanupIndicatorManager(int var1);

   private class IndicatorManagerListener implements EventConsumer {
      private IndicatorManagerListener() {
      }

      public void consumeEvent(int category, int type, int param) {
         synchronized(IndicatorManager.mainSharedObject) {
            if (category == 12) {
               IndicatorManager indManager = IndicatorManager.mainSharedObject.getIndicatorManagerForMidletAppId(param);
               if (indManager != null) {
                  IndicatorManager.mainSharedObject.removeIndicatorManagerfromList(indManager);
                  indManager.indicatorPriorityList = null;
               } else {
                  IndicatorManager.mainSharedObject.prioritiseIndicators();
               }
            }

         }
      }

      // $FF: synthetic method
      IndicatorManagerListener(Object x1) {
         this();
      }
   }
}
