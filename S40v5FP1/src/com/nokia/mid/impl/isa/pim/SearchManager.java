package com.nokia.mid.impl.isa.pim;

import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;

interface SearchManager {
   PIMItem nextElement(PIMItem var1, PIMItem var2, PIMList var3);

   PIMItem nextElement(PIMItem var1, String var2, PIMList var3, boolean var4);

   void commitItem(PIMItem var1) throws PIMException;

   void removeItem(PIMItem var1) throws PIMException;

   String[] categories() throws PIMException;

   void renameCategory(String var1, String var2) throws PIMException;
}
