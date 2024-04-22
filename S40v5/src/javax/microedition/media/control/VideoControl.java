package javax.microedition.media.control;

import javax.microedition.media.MediaException;

public interface VideoControl extends GUIControl {
   int USE_DIRECT_VIDEO = 1;

   Object initDisplayMode(int var1, Object var2);

   void setDisplayLocation(int var1, int var2);

   int getDisplayX();

   int getDisplayY();

   void setVisible(boolean var1);

   void setDisplaySize(int var1, int var2) throws MediaException;

   void setDisplayFullScreen(boolean var1) throws MediaException;

   int getSourceWidth();

   int getSourceHeight();

   int getDisplayWidth();

   int getDisplayHeight();

   byte[] getSnapshot(String var1) throws MediaException;
}
