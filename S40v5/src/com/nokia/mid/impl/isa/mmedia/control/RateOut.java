package com.nokia.mid.impl.isa.mmedia.control;

import javax.microedition.media.MediaException;

public interface RateOut {
   int getDefaultMaxRate();

   int getDefaultMinRate();

   int getMaxRate();

   int getMinRate();

   int getRate() throws MediaException;

   int setRate(int var1) throws MediaException;
}
