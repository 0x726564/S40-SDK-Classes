package com.nokia.mid.impl.isa.mmedia.control;

import javax.microedition.media.MediaException;

public interface StopTimeOut {
   long getStopTime() throws MediaException;

   long setStopTime(long var1) throws MediaException;
}
