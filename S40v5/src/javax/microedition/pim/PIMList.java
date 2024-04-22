package javax.microedition.pim;

import java.util.Enumeration;

public interface PIMList {
   String UNCATEGORIZED = null;

   String getName();

   void close() throws PIMException;

   Enumeration items() throws PIMException;

   Enumeration items(PIMItem var1) throws PIMException;

   Enumeration items(String var1) throws PIMException;

   Enumeration itemsByCategory(String var1) throws PIMException;

   String[] getCategories() throws PIMException;

   boolean isCategory(String var1) throws PIMException;

   void addCategory(String var1) throws PIMException;

   void deleteCategory(String var1, boolean var2) throws PIMException;

   void renameCategory(String var1, String var2) throws PIMException;

   int maxCategories();

   boolean isSupportedField(int var1);

   int[] getSupportedFields();

   boolean isSupportedAttribute(int var1, int var2);

   int[] getSupportedAttributes(int var1);

   boolean isSupportedArrayElement(int var1, int var2);

   int[] getSupportedArrayElements(int var1);

   int getFieldDataType(int var1);

   String getFieldLabel(int var1);

   String getAttributeLabel(int var1);

   String getArrayElementLabel(int var1, int var2);

   int maxValues(int var1);

   int stringArraySize(int var1);
}
