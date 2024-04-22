package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.source_handling.JavaProducerSource;
import com.nokia.mid.impl.policy.PolicyAccess;
import com.nokia.mid.pri.PriAccess;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.TimeBase;

public abstract class BasicPlayer implements Player, TimeBase {
   public static final byte AUDIO_TYPE = 0;
   public static final byte RADIO_TYPE = 1;
   public static final byte VIDEO_TYPE = 2;
   public static final byte NUM_MEDIA_TYPES = 3;
   public static final byte INVALID_TYPE = -1;
   public static final String mmapiPkgName = "javax.microedition.media.control.";
   public static final String ammsPkgName = "javax.microedition.amms.control.";
   public static final String ammsAudioEffectPkgName = "javax.microedition.amms.control.audioeffect.";
   public static final String ammsCameraPkgName = "javax.microedition.amms.control.camera.";
   public static final String ammsTunerPkgName = "javax.microedition.amms.control.tuner.";
   public static final String racName = "RateControl";
   public static final String micName = "MIDIControl";
   public static final String picName = "PitchControl";
   public static final String stcName = "StopTimeControl";
   public static final String tecName = "TempoControl";
   public static final String tocName = "ToneControl";
   public static final String vocName = "VolumeControl";
   public static final String guiName = "GUIControl";
   public static final String vidName = "VideoControl";
   public static final String recName = "RecordControl";
   public static final String mecName = "MetaDataControl";
   public static final String fpcName = "FramePositioningControl";
   public static final String camName = "CameraControl";
   public static final String ifcName = "ImageFormatControl";
   public static final String snpName = "SnapshotControl";
   private static final String POLICY_PLAYER_API = "javax.microedition.media.Player";
   protected ControlManager controlManager = new ControlManager();
   protected int state = 100;
   protected int loopCount = 1;
   protected int shadowLoopCount = 1;
   protected Vector listeners = new Vector(2);
   EvtQ evtQ = null;
   Object evtLock = new Object();
   private TimeBase timeBase = Manager.getSystemTimeBase();
   public ParsedLocator locator;
   protected JavaProducerSource dataSource = null;
   protected int playerId;
   protected static MediaEventConsumer eventConsumer = MediaEventConsumer.getInstance();
   private boolean active;

   public void setParsedLocator(ParsedLocator loc) throws MediaException {
      this.locator = loc;
   }

   public void setDataSource(JavaProducerSource source) throws IOException {
      this.dataSource = source;
   }

   public synchronized void setLoopCount(int count) {
      this.chkIllegalState(false);
      if (this.state == 400) {
         throw new IllegalStateException();
      } else if (count != 0 && count >= -1) {
         this.doSetLoopCount(count);
         this.loopCount = this.shadowLoopCount = count;
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected abstract void doSetLoopCount(int var1);

   public void setTimeBase(TimeBase master) throws MediaException {
      this.chkIllegalState(true);
      if (this.state == 400) {
         throw new IllegalStateException();
      } else if (master != null && master != Manager.getSystemTimeBase()) {
         throw new MediaException("Only system time base supported");
      }
   }

   public synchronized void realize() throws MediaException {
      if (PriAccess.getInt(5) == 1 && !PolicyAccess.checkPermission("javax.microedition.media.Player", "Allow Media Access?")) {
         throw new SecurityException();
      } else {
         this.chkIllegalState(false);
         if (this.state < 200) {
            this.doRealize();
            this.state = 200;
         }
      }
   }

   protected abstract void doRealize() throws MediaException;

   public synchronized void prefetch() throws MediaException {
      if (PriAccess.getInt(5) == 1 && !PolicyAccess.checkPermission("javax.microedition.media.Player", "Allow Media Access?")) {
         throw new SecurityException();
      } else {
         this.chkIllegalState(false);
         if (this.state < 300) {
            if (this.state < 200) {
               this.realize();
            }

            ResourceController.verifyPrefetchAttempt(this);

            try {
               this.doPrefetch();
            } catch (MediaException var2) {
               ResourceController.notifyPlayerInactive(this);
               throw var2;
            }

            this.state = 300;
         }
      }
   }

   protected abstract void doPrefetch() throws MediaException;

   public synchronized void start() throws MediaException {
      if (PriAccess.getInt(5) == 1 && !PolicyAccess.checkPermission("javax.microedition.media.Player", "Allow Media Access?")) {
         throw new SecurityException();
      } else {
         this.chkIllegalState(false);
         if (this.state < 400) {
            if (this.state < 300) {
               this.prefetch();
            }

            ResourceController.verifyStartAttempt(this);

            try {
               this.doStart();
            } catch (MediaException var2) {
               ResourceController.notifyPlayerStopped(this);
               throw var2;
            }

            this.state = 400;
         }
      }
   }

   protected abstract void doStart() throws MediaException;

   public synchronized void stop() throws MediaException {
      this.chkIllegalState(false);
      if (this.state >= 400) {
         this.doStop();
         ResourceController.notifyPlayerStopped(this);
         this.state = 300;
      }
   }

   protected abstract void doStop() throws MediaException;

   public synchronized void deallocate() {
      this.chkIllegalState(false);
      if (this.state >= 300) {
         if (this.state == 400) {
            try {
               this.stop();
            } catch (MediaException var2) {
            }
         }

         this.doDeallocate();
         ResourceController.notifyPlayerInactive(this);
         this.state = 200;
      }
   }

   protected abstract void doDeallocate();

   public synchronized void close() {
      if (this.state != 0) {
         this.deallocate();
         this.doClose();
         if (this.dataSource != null) {
            this.dataSource.disconnect();
            this.dataSource = null;
         }

         this.state = 0;
         eventConsumer.serializeEvent(this.playerId, 1, -1L);
      }
   }

   protected abstract void doClose();

   public synchronized long setMediaTime(long newTime) throws MediaException {
      this.chkIllegalState(true);
      if (newTime < 0L) {
         newTime = 0L;
      }

      long timeSet = this.doSetMediaTime(newTime);
      return timeSet;
   }

   protected abstract long doSetMediaTime(long var1) throws MediaException;

   public synchronized long getMediaTime() {
      this.chkIllegalState(false);
      return this.doGetMediaTime();
   }

   protected abstract long doGetMediaTime();

   public TimeBase getTimeBase() {
      this.chkIllegalState(true);
      return this.timeBase;
   }

   public int getState() {
      return this.state;
   }

   public synchronized String getContentType() {
      this.chkIllegalState(true);
      return this.locator.contentType;
   }

   public synchronized long getDuration() {
      this.chkIllegalState(false);
      return this.doGetDuration();
   }

   protected abstract long doGetDuration();

   public void addPlayerListener(PlayerListener playerListener) {
      this.chkIllegalState(false);
      if (playerListener != null) {
         this.listeners.addElement(playerListener);
      }

   }

   public void removePlayerListener(PlayerListener playerListener) {
      this.chkIllegalState(false);
      this.listeners.removeElement(playerListener);
   }

   public void serializeEvent(int eventId, long time) {
      eventConsumer.serializeEvent(this.playerId, eventId, time);
   }

   public synchronized void dispatchEvent(String evt, Object evtData) {
      if (evt == "deviceUnavailable") {
         this.state = 200;
         ResourceController.notifyPlayerInactive(this);
         this.setActiveState(false);
         if (this.dataSource != null) {
            this.dataSource.disconnect();
         }
      } else if (evt == "stoppedAtTime") {
         ResourceController.notifyPlayerStopped(this);
         this.state = 300;
      } else if (evt == "endOfMedia" && this.loopCount != -1 && --this.loopCount == 0) {
         this.loopCount = this.shadowLoopCount;
         ResourceController.notifyPlayerStopped(this);
         this.state = 300;
      }

      if (this.listeners.size() != 0) {
         synchronized(this.evtLock) {
            if (this.evtQ == null) {
               this.evtQ = new EvtQ(this);
            }

            this.evtQ.sendEvent(evt, evtData);
         }
      }

   }

   public final Control[] getControls() {
      this.chkIllegalState(true);
      return this.controlManager.getControls();
   }

   public Control getControl(String type) {
      String pkgName = null;
      this.chkIllegalState(true);
      if (type == null) {
         throw new IllegalArgumentException();
      } else {
         if (type.startsWith("javax.microedition.media.control.")) {
            pkgName = "javax.microedition.media.control.";
         } else if (type.startsWith("javax.microedition.amms.control.")) {
            if (type.startsWith("javax.microedition.amms.control.audioeffect.")) {
               pkgName = "javax.microedition.amms.control.audioeffect.";
            } else if (type.startsWith("javax.microedition.amms.control.camera.")) {
               pkgName = "javax.microedition.amms.control.camera.";
            } else if (type.startsWith("javax.microedition.amms.control.tuner.")) {
               pkgName = "javax.microedition.amms.control.tuner.";
            } else {
               pkgName = "javax.microedition.amms.control.";
            }
         }

         if (pkgName == null) {
            pkgName = "javax.microedition.media.control.";
         } else {
            type = type.substring(pkgName.length());
         }

         return this.controlManager.getControl(pkgName, type);
      }
   }

   protected void addControl(String type, Control ctrl) {
      this.controlManager.addControl("javax.microedition.media.control.", type, ctrl);
   }

   protected void addControl(String packageName, String type, Control ctrl) {
      this.controlManager.addControl(packageName, type, ctrl);
   }

   public boolean isActive() {
      return this.active;
   }

   public int getPlayerId() {
      return this.playerId;
   }

   public String getDeviceName() {
      return "AUDIO";
   }

   protected void setActiveState(boolean isActive) {
      this.active = isActive;
      this.controlManager.setActiveState(isActive);
   }

   public synchronized long getTime() {
      return this.timeBase.getTime();
   }

   protected void chkIllegalState(boolean unrealized) {
      if (this.state == 0 || unrealized && this.state == 100) {
         throw new IllegalStateException("Can't invoke method in " + (this.state == 0 ? "closed" : "unrealized") + " state");
      }
   }
}
