package javax.microedition.amms.control.tuner;

import java.util.Date;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public interface RDSControl extends Control {
   String RDS_NEW_DATA = "RDS_NEW_DATA";
   String RDS_NEW_ALARM = "RDS_ALARM";
   String RADIO_CHANGED = "radio_changed";

   boolean isRDSSignal();

   String getPS();

   String getRT();

   short getPTY();

   String getPTYString(boolean var1);

   short getPI();

   int[] getFreqsByPTY(short var1);

   int[][] getFreqsByTA(boolean var1);

   String[] getPSByPTY(short var1);

   String[] getPSByTA(boolean var1);

   Date getCT();

   boolean getTA();

   boolean getTP();

   void setAutomaticSwitching(boolean var1) throws MediaException;

   boolean getAutomaticSwitching();

   void setAutomaticTA(boolean var1) throws MediaException;

   boolean getAutomaticTA();
}
