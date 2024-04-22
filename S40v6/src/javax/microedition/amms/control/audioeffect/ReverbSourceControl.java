package javax.microedition.amms.control.audioeffect;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public interface ReverbSourceControl extends Control {
   int DISCONNECT = Integer.MAX_VALUE;

   void setRoomLevel(int var1) throws MediaException;

   int getRoomLevel();
}
