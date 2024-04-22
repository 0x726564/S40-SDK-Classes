package org.xml.sax;

public interface Attributes {
   int getLength();

   String getURI(int var1);

   String getLocalName(int var1);

   String getQName(int var1);

   String getType(int var1);

   String getValue(int var1);

   int getIndex(String var1, String var2);

   int getIndex(String var1);

   String getType(String var1, String var2);

   String getType(String var1);

   String getValue(String var1, String var2);

   String getValue(String var1);
}
