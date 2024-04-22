package com.nokia.mid.impl.isa.mmedia.control;

import javax.microedition.media.MediaException;

public interface VolumeOut {
   int getLevel() throws MediaException;

   boolean isMuted() throws MediaException;

   int setLevel(int var1) throws MediaException;

   void setMute(boolean var1) throws MediaException;
}
