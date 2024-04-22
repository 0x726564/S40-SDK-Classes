package javax.microedition.amms.control.audio3d;

import javax.microedition.media.Control;

public interface CommitControl extends Control {
   void commit();

   boolean isDeferred();

   void setDeferred(boolean var1);
}
