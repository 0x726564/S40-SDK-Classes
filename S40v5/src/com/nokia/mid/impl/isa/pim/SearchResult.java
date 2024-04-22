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
   private SearchManager iU = null;
   private PIMItem iV = null;
   private PIMList iW = null;
   private String iX = null;
   private PIMItem iY = null;
   private int iZ = 0;
   private int ja = 0;
   private long jb = 0L;
   private long jc = 0L;
   private boolean jd = false;
   private int field = 0;

   private SearchResult() {
   }

   public SearchResult(SearchManager var1, PIMItem var2, PIMList var3) {
      this.iY = var2;
      this.iU = var1;
      this.iZ = 1;
      this.iW = var3;
      if (var2 != null && var3.getName().compareTo(var2.getPIMList().getName()) != 0) {
         throw new IllegalArgumentException("PIMItems originate from different lists.");
      } else {
         this.j(true);
      }
   }

   public SearchResult(SearchManager var1, String var2, boolean var3, PIMList var4) {
      this.iX = var2;
      this.iU = var1;
      this.iW = var4;
      if (var3) {
         this.iZ = 3;
      } else {
         this.iZ = 2;
      }

      if (this.iZ != 3 || ((PBSearchManager)var1).isValidCategory(var2) || var2 == PIMList.UNCATEGORIZED) {
         this.j(true);
      }

   }

   public SearchResult(SearchManager var1, int var2, long var3, long var5, PIMList var7) {
      this.iU = var1;
      this.field = var2;
      this.jb = var3;
      this.jc = var5;
      this.iW = var7;
      this.iZ = 4;
      this.j(true);
   }

   public SearchResult(SearchManager var1, int var2, long var3, long var5, boolean var7, PIMList var8) {
      this.iU = var1;
      this.ja = var2;
      this.jb = var3;
      this.jc = var5;
      this.jd = var7;
      this.iW = var8;
      this.iZ = 5;
      this.j(true);
   }

   public boolean hasMoreElements() {
      return this.iV != null;
   }

   public Object nextElement() {
      return this.j(false);
   }

   private Object j(boolean var1) throws NoSuchElementException {
      if (!var1 && this.iV == null) {
         throw new NoSuchElementException("No such matching search items could be found.");
      } else {
         PIMItem var2 = this.iV;
         switch(this.iZ) {
         case 1:
         default:
            this.iV = this.iU.nextElement(this.iV, this.iY, this.iW);
            break;
         case 2:
            this.iV = this.iU.nextElement(this.iV, this.iX, this.iW, false);
            break;
         case 3:
            this.iV = this.iU.nextElement(this.iV, this.iX, this.iW, true);
            break;
         case 4:
            if (!(this.iU instanceof CalendarSearchManager)) {
               throw new RuntimeException("Searching by TODO dates is invalid in this context!");
            }

            this.iV = ((CalendarSearchManager)this.iU).SearchByToDo((ToDo)this.iV, this.field, this.jb, this.jc, (ToDoList)this.iW);
            break;
         case 5:
            if (!(this.iU instanceof CalendarSearchManager)) {
               throw new RuntimeException(" Searching by EVENT dates is invalid in this context!");
            }

            this.iV = ((CalendarSearchManager)this.iU).SearchByEvent((Event)this.iV, this.ja, this.jb, this.jc, this.jd, (EventList)this.iW);
         }

         return var2;
      }
   }
}
