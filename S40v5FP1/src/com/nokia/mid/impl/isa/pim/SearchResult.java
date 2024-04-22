package com.nokia.mid.impl.isa.pim;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.microedition.pim.Event;
import javax.microedition.pim.EventList;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.ToDo;
import javax.microedition.pim.ToDoList;

class SearchResult implements Enumeration {
   public static final int SEARCHRESULT_BY_ITEM = 1;
   public static final int SEARCHRESULT_BY_STRING = 2;
   public static final int SEARCHRESULT_BY_CATEGORY_STRING = 3;
   public static final int SEARCHRESULT_BY_TODO_DATES = 4;
   public static final int SEARCHRESULT_BY_EVENT_DATES = 5;
   private SearchManager nativeSearchManager = null;
   private PIMItem foundItem = null;
   private PIMList pimList = null;
   private String searchString = null;
   private PIMItem searchItem = null;
   private int iSearchResultType = 0;
   private int searchType = 0;
   private long startDate = 0L;
   private long endDate = 0L;
   private boolean initialEventOnly = false;
   private int field = 0;

   private SearchResult() {
   }

   public SearchResult(SearchManager nativeSearchManager, PIMItem searchItem, PIMList pimList) {
      this.searchItem = searchItem;
      this.nativeSearchManager = nativeSearchManager;
      this.iSearchResultType = 1;
      this.pimList = pimList;
      if (searchItem != null && pimList.getName().compareTo(searchItem.getPIMList().getName()) != 0) {
         throw new IllegalArgumentException("PIMItems originate from different lists.");
      } else {
         this.findNextElement(true);
      }
   }

   public SearchResult(SearchManager nativeSearchManager, String searchString, boolean bSearchByCategory, PIMList pimList) {
      this.searchString = searchString;
      this.nativeSearchManager = nativeSearchManager;
      this.pimList = pimList;
      if (bSearchByCategory) {
         this.iSearchResultType = 3;
      } else {
         this.iSearchResultType = 2;
      }

      if (this.iSearchResultType != 3 || ((PBSearchManager)nativeSearchManager).isValidCategory(searchString) || searchString == PIMList.UNCATEGORIZED) {
         this.findNextElement(true);
      }

   }

   public SearchResult(SearchManager nativeSearchManager, int field, long startDate, long endDate, PIMList pimList) {
      this.nativeSearchManager = nativeSearchManager;
      this.field = field;
      this.startDate = startDate;
      this.endDate = endDate;
      this.pimList = pimList;
      this.iSearchResultType = 4;
      this.findNextElement(true);
   }

   public SearchResult(SearchManager nativeSearchManager, int searchType, long startDate, long endDate, boolean initialEventOnly, PIMList pimList) {
      this.nativeSearchManager = nativeSearchManager;
      this.searchType = searchType;
      this.startDate = startDate;
      this.endDate = endDate;
      this.initialEventOnly = initialEventOnly;
      this.pimList = pimList;
      this.iSearchResultType = 5;
      this.findNextElement(true);
   }

   public boolean hasMoreElements() {
      return this.foundItem != null;
   }

   public Object nextElement() {
      return this.findNextElement(false);
   }

   private Object findNextElement(boolean forceToFirstFind) throws NoSuchElementException {
      if (!forceToFirstFind && this.foundItem == null) {
         throw new NoSuchElementException("No such matching search items could be found.");
      } else {
         PIMItem preFetchedItem = this.foundItem;
         switch(this.iSearchResultType) {
         case 1:
         default:
            this.foundItem = this.nativeSearchManager.nextElement(this.foundItem, this.searchItem, this.pimList);
            break;
         case 2:
            this.foundItem = this.nativeSearchManager.nextElement(this.foundItem, this.searchString, this.pimList, false);
            break;
         case 3:
            this.foundItem = this.nativeSearchManager.nextElement(this.foundItem, this.searchString, this.pimList, true);
            break;
         case 4:
            if (!(this.nativeSearchManager instanceof CalendarSearchManager)) {
               throw new RuntimeException("Searching by TODO dates is invalid in this context!");
            }

            this.foundItem = ((CalendarSearchManager)this.nativeSearchManager).SearchByToDo((ToDo)this.foundItem, this.field, this.startDate, this.endDate, (ToDoList)this.pimList);
            break;
         case 5:
            if (!(this.nativeSearchManager instanceof CalendarSearchManager)) {
               throw new RuntimeException(" Searching by EVENT dates is invalid in this context!");
            }

            this.foundItem = ((CalendarSearchManager)this.nativeSearchManager).SearchByEvent((Event)this.foundItem, this.searchType, this.startDate, this.endDate, this.initialEventOnly, (EventList)this.pimList);
         }

         return preFetchedItem;
      }
   }
}
