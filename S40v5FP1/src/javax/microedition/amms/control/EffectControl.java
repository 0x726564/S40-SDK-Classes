package javax.microedition.amms.control;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public interface EffectControl extends Control {
   int SCOPE_LIVE_ONLY = 1;
   int SCOPE_RECORD_ONLY = 2;
   int SCOPE_LIVE_AND_RECORD = 3;

   void setEnabled(boolean var1);

   boolean isEnabled();

   void setScope(int var1) throws MediaException;

   int getScope();

   void setEnforced(boolean var1);

   boolean isEnforced();

   void setPreset(String var1);

   String getPreset();

   String[] getPresetNames();
}
