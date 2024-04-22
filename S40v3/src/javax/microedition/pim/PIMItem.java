package javax.microedition.pim;

public interface PIMItem {
   int BINARY = 0;
   int BOOLEAN = 1;
   int DATE = 2;
   int INT = 3;
   int STRING = 4;
   int STRING_ARRAY = 5;
   int ATTR_NONE = 0;
   int EXTENDED_FIELD_MIN_VALUE = 16777216;
   int EXTENDED_ATTRIBUTE_MIN_VALUE = 16777216;

   PIMList getPIMList();

   void commit() throws PIMException;

   boolean isModified();

   int[] getFields();

   byte[] getBinary(int var1, int var2);

   void addBinary(int var1, int var2, byte[] var3, int var4, int var5);

   void setBinary(int var1, int var2, int var3, byte[] var4, int var5, int var6);

   long getDate(int var1, int var2);

   void addDate(int var1, int var2, long var3);

   void setDate(int var1, int var2, int var3, long var4);

   int getInt(int var1, int var2);

   void addInt(int var1, int var2, int var3);

   void setInt(int var1, int var2, int var3, int var4);

   String getString(int var1, int var2);

   void addString(int var1, int var2, String var3);

   void setString(int var1, int var2, int var3, String var4);

   boolean getBoolean(int var1, int var2);

   void addBoolean(int var1, int var2, boolean var3);

   void setBoolean(int var1, int var2, int var3, boolean var4);

   String[] getStringArray(int var1, int var2);

   void addStringArray(int var1, int var2, String[] var3);

   void setStringArray(int var1, int var2, int var3, String[] var4);

   int countValues(int var1);

   void removeValue(int var1, int var2);

   int getAttributes(int var1, int var2);

   void addToCategory(String var1) throws PIMException;

   void removeFromCategory(String var1);

   String[] getCategories();

   int maxCategories();
}
