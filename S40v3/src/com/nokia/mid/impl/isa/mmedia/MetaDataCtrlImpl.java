package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.control.MetaDataControl;

public class MetaDataCtrlImpl extends Switchable implements MetaDataControl {
   private MediaOut mediaOut;

   public MetaDataCtrlImpl(BasicPlayer var1, MediaOut var2) {
      this.player = var1;
      this.mediaOut = var2;
   }

   public String[] getKeys() {
      return this.mediaOut.getMetaDataKeys();
   }

   public String getKeyValue(String var1) throws IllegalArgumentException {
      synchronized(this.player) {
         return this.player.isActive() ? this.mediaOut.getMetaDataKeyValue(var1) : null;
      }
   }
}
