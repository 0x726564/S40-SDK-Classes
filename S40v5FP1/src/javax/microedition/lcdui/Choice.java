package javax.microedition.lcdui;

public interface Choice {
   int EXCLUSIVE = 1;
   int MULTIPLE = 2;
   int IMPLICIT = 3;
   int POPUP = 4;
   int TEXT_WRAP_DEFAULT = 0;
   int TEXT_WRAP_ON = 1;
   int TEXT_WRAP_OFF = 2;

   int size();

   String getString(int var1);

   Image getImage(int var1);

   int append(String var1, Image var2);

   void insert(int var1, String var2, Image var3);

   void delete(int var1);

   void deleteAll();

   void set(int var1, String var2, Image var3);

   boolean isSelected(int var1);

   int getSelectedIndex();

   int getSelectedFlags(boolean[] var1);

   void setSelectedIndex(int var1, boolean var2);

   void setSelectedFlags(boolean[] var1);

   void setFitPolicy(int var1);

   int getFitPolicy();

   void setFont(int var1, Font var2);

   Font getFont(int var1);
}
