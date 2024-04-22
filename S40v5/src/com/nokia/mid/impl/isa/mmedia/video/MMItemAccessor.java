package com.nokia.mid.impl.isa.mmedia.video;

public interface MMItemAccessor {
   int ICON_NONE = 0;
   int ICON_PLACE_HOLDER = 1;
   int ICON_BROKEN_VIDEO = 2;

   void init(int var1, int var2);

   void setDisplaySize(int var1, int var2);

   void showIcon(int var1, int var2);

   int getDisplayX();

   int getDisplayY();

   int getMaxWidth();

   int getMaxHeight();
}
