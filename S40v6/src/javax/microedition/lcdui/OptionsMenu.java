package javax.microedition.lcdui;

interface OptionsMenu {
   Command optionsCommand = new Command(9, 2);

   void update(int var1);

   void setKeepRootOptionsMenu(boolean var1);

   int getHighlightedIndex();

   void dismiss();

   int getItemCount();

   boolean isShowing();
}
