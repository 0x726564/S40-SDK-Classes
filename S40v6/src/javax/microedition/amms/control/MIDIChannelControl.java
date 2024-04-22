package javax.microedition.amms.control;

import javax.microedition.media.Control;

public interface MIDIChannelControl extends Control {
   Control getChannelControl(String var1, int var2);

   Control[] getChannelControls(int var1);
}
