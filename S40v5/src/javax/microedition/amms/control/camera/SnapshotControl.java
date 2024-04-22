package javax.microedition.amms.control.camera;

import javax.microedition.media.Control;

public interface SnapshotControl extends Control {
   String SHOOTING_STOPPED = "SHOOTING_STOPPED";
   String STORAGE_ERROR = "STORAGE_ERROR";
   String WAITING_UNFREEZE = "WAITING_UNFREEZE";
   int FREEZE = -2;
   int FREEZE_AND_CONFIRM = -1;

   void setDirectory(String var1);

   String getDirectory();

   void setFilePrefix(String var1);

   String getFilePrefix();

   void setFileSuffix(String var1);

   String getFileSuffix();

   void start(int var1) throws SecurityException;

   void stop();

   void unfreeze(boolean var1);
}
