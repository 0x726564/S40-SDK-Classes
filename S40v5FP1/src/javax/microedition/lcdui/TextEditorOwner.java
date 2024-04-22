package javax.microedition.lcdui;

interface TextEditorOwner {
   Displayable getDisplayable();

   boolean hasFocus();

   void changedItemState();

   int getCursorWrap();

   void processTextEditorEvent(int var1, int var2);
}
