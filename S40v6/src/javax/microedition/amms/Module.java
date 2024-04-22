package javax.microedition.amms;

import javax.microedition.media.Controllable;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public interface Module extends Controllable {
   void addMIDIChannel(Player var1, int var2) throws MediaException;

   void addPlayer(Player var1) throws MediaException;

   void removeMIDIChannel(Player var1, int var2);

   void removePlayer(Player var1);
}
