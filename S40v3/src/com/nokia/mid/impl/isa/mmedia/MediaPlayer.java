package com.nokia.mid.impl.isa.mmedia;

import javax.microedition.media.MediaException;

public abstract class MediaPlayer extends BasicPlayer {
   protected MediaOut mediaOut;
   protected VolumeCtrlImpl volCtrl;
   protected byte[] mediaData;

   protected void doSetLoopCount(int var1) {
      this.mediaOut.setLoopCount(var1);
   }

   protected void doRealize() throws MediaException {
      try {
         this.dataSource.fetchData();
      } catch (Exception var2) {
         throw new MediaException("Failed to fetch media data");
      }
   }

   protected void doPrefetch() throws MediaException {
      byte[] var1;
      try {
         var1 = this.dataSource.generateSourceId();
         this.dataSource.start();
      } catch (Exception var3) {
         throw new MediaException("Failed to fetch media data");
      }

      if (!this.mediaOut.openDataSession(var1, this.locator.contentType, this.loopCount, this.volCtrl.readStoredVol())) {
         throw new MediaException("device error");
      } else {
         this.setActiveState(true);
      }
   }

   protected void doStart() throws MediaException {
      if (!this.mediaOut.resume()) {
         throw new MediaException("device error");
      }
   }

   protected void doStop() throws MediaException {
      if (!this.mediaOut.pause()) {
         throw new MediaException("device error");
      }
   }

   protected void doDeallocate() {
      this.setActiveState(false);
      this.mediaOut.close();
   }

   protected void doClose() {
   }

   protected long doSetMediaTime(long var1) throws MediaException {
      return this.mediaOut.setMediaTime(var1);
   }

   protected long doGetDuration() {
      return this.mediaOut.getDuration();
   }

   protected long doGetMediaTime() {
      return this.mediaOut.getMediaTime();
   }
}
