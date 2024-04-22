package javax.microedition.media.control;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public interface MIDIControl extends Control {
   int NOTE_ON = 144;
   int CONTROL_CHANGE = 176;

   boolean isBankQuerySupported();

   int[] getProgram(int var1) throws MediaException;

   int getChannelVolume(int var1);

   void setProgram(int var1, int var2, int var3);

   void setChannelVolume(int var1, int var2);

   int[] getBankList(boolean var1) throws MediaException;

   int[] getProgramList(int var1) throws MediaException;

   String getProgramName(int var1, int var2) throws MediaException;

   String getKeyName(int var1, int var2, int var3) throws MediaException;

   void shortMidiEvent(int var1, int var2, int var3);

   int longMidiEvent(byte[] var1, int var2, int var3);
}
