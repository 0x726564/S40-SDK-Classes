package com.nokia.mid.impl.isa.mmedia;

import javax.microedition.media.MediaException;

public abstract class MediaPlayer extends BasicPlayer {
   protected MediaOut mediaOut;
   protected VolumeCtrlImpl volCtrl;
   protected byte[] mediaData;

   protected void doSetLoopCount(int count) {
      this.mediaOut.setLoopCount(count);
   }

   protected void doRealize() throws MediaException {
      try {
         this.dataSource.fetchData();
      } catch (Exception var2) {
         throw new MediaException("Failed to fetch media data");
      }
   }

   protected void doPrefetch() throws MediaException {
      byte vol = 0;

      byte[] sourceId;
      try {
         sourceId = this.dataSource.generateSourceId();
         this.dataSource.start();
      } catch (Exception var4) {
         throw new MediaException("Failed to fetch media data");
      }

      if (this.volCtrl != null) {
         this.volCtrl.readStoredVol();
      }

      if (!this.mediaOut.openDataSession(sourceId, this.locator.contentType, this.loopCount, vol)) {
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

   protected long doSetMediaTime(long time) throws MediaException {
      return this.mediaOut.setMediaTime(time);
   }

   protected long doGetDuration() {
      return this.mediaOut.getDuration();
   }

   protected long doGetMediaTime() {
      return this.mediaOut.getMediaTime();
   }
}
