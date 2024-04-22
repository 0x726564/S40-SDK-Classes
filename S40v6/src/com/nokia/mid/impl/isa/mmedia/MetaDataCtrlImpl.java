package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.control.MetaDataControl;

public class MetaDataCtrlImpl extends Switchable implements MetaDataControl {
   private MediaOut mediaOut;

   public MetaDataCtrlImpl(BasicPlayer player, MediaOut mediaOut) {
      this.player = player;
      this.mediaOut = mediaOut;
   }

   public String[] getKeys() {
      return this.mediaOut.getMetaDataKeys();
   }

   public String getKeyValue(String key) throws IllegalArgumentException {
      synchronized(this.player) {
         return this.player.isActive() ? this.mediaOut.getMetaDataKeyValue(key) : null;
      }
   }
}
