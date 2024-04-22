package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import com.nokia.mid.impl.isa.source_handling.JavaProducerSource;
import com.nokia.mid.impl.policy.PolicyAccess;
import com.nokia.mid.pri.PriAccess;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.TimeBase;

public abstract class BasicPlayer implements Player, TimeBase {
   public static final byte EVENT_CLOSED = 1;
   public static final byte EVENT_DEVICE_AVAILABLE = 2;
   public static final byte EVENT_DEVICE_UNAVAILABLE = 3;
   public static final byte EVENT_END_OF_MEDIA = 4;
   public static final byte EVENT_ERROR = 5;
   public static final byte EVENT_SIZE_CHANGED = 6;
   public static final byte EVENT_STARTED = 7;
   public static final byte EVENT_STOPPED = 8;
   public static final byte EVENT_STOPPED_AT_TIME = 9;
   public static final byte EVENT_VOLUME_CHANGED = 10;
   public static final byte EVENT_RECORD_STARTED = 11;
   public static final byte EVENT_RECORD_STOPPED = 12;
   public static final byte EVENT_RECORD_COMMITED = 13;
   public static final byte EVENT_RECORD_ERROR = 14;
   public static final byte AUDIO_TYPE = 0;
   public static final byte RADIO_TYPE = 1;
   public static final byte VIDEO_TYPE = 2;
   public static final byte NUM_MEDIA_TYPES = 3;
   public static final byte INVALID_TYPE = -1;
   protected static final String racName = "RateControl";
   protected static final String micName = "MIDIControl";
   protected static final String picName = "PitchControl";
   protected static final String stcName = "StopTimeControl";
   protected static final String tecName = "TempoControl";
   protected static final String tocName = "ToneControl";
   protected static final String vocName = "VolumeControl";
   protected static final String guiName = "GUIControl";
   protected static final String vidName = "VideoControl";
   protected static final String recName = "RecordControl";
   protected static final String mecName = "MetaDataControl";
   private static final String[] allCtrls = new String[]{"RateControl", "MIDIControl", "PitchControl", "StopTimeControl", "TempoControl", "ToneControl", "VolumeControl", "GUIControl", "VideoControl", "RecordControl", "MetaDataControl"};
   protected static final String pkgName = "javax.microedition.media.control.";
   private static final String POLICY_PLAYER_API = "javax.microedition.media.Player";
   protected int state = 100;
   protected int loopCount = 1;
   protected int shadowLoopCount = 1;
   protected Vector listeners = new Vector(2);
   EvtQ evtQ = null;
   Object evtLock = new Object();
   private Hashtable ctrlTable = new Hashtable(2);
   private TimeBase timeBase = Manager.getSystemTimeBase();
   public ParsedLocator locator;
   protected JavaProducerSource dataSource = null;
   protected int playerId;
   protected static MediaEventConsumer eventConsumer = MediaEventConsumer.getInstance();
   private boolean active;

   public void setParsedLocator(ParsedLocator var1) throws MediaException {
      this.locator = var1;
   }

   public void setDataSource(JavaProducerSource var1) {
      this.dataSource = var1;
   }

   public synchronized void setLoopCount(int var1) {
      this.chkIllegalState(false);
      if (this.state == 400) {
         throw new IllegalStateException();
      } else if (var1 != 0 && var1 >= -1) {
         this.doSetLoopCount(var1);
         this.loopCount = this.shadowLoopCount = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected abstract void doSetLoopCount(int var1);

   public void setTimeBase(TimeBase var1) throws MediaException {
      this.chkIllegalState(true);
      if (this.state == 400) {
         throw new IllegalStateException();
      } else if (var1 != null && var1 != Manager.getSystemTimeBase()) {
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

   public synchronized long setMediaTime(long var1) throws MediaException {
      this.chkIllegalState(true);
      if (var1 < 0L) {
         var1 = 0L;
      }

      long var3 = this.doSetMediaTime(var1);
      return var3;
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

   public void addPlayerListener(PlayerListener var1) {
      this.chkIllegalState(false);
      if (var1 != null) {
         this.listeners.addElement(var1);
      }

   }

   public void removePlayerListener(PlayerListener var1) {
      this.chkIllegalState(false);
      this.listeners.removeElement(var1);
   }

   public void serializeEvent(int var1, long var2) {
      eventConsumer.serializeEvent(this.playerId, var1, var2);
   }

   public synchronized void dispatchEvent(String var1, Object var2) {
      if (var1 == "deviceUnavailable") {
         this.state = 200;
         ResourceController.notifyPlayerInactive(this);
         this.setActiveState(false);
         if (this.dataSource != null) {
            this.dataSource.disconnect();
         }
      } else if (var1 == "started") {
         this.state = 400;
      } else if (var1 != "stopped" && var1 != "stoppedAtTime") {
         if (var1 == "endOfMedia") {
            if (this.loopCount != -1 && --this.loopCount == 0) {
               this.loopCount = this.shadowLoopCount;
               ResourceController.notifyPlayerStopped(this);
            }

            this.state = 300;
         }
      } else {
         if (var1 == "stoppedAtTime") {
            ResourceController.notifyPlayerStopped(this);
         }

         this.state = 300;
      }

      if (this.listeners.size() != 0) {
         synchronized(this.evtLock) {
            if (this.evtQ == null) {
               this.evtQ = new EvtQ(this);
            }

            this.evtQ.sendEvent(var1, var2);
         }
      }

   }

   public final Control[] getControls() {
      this.chkIllegalState(true);
      Vector var1 = new Vector(3);

      for(int var3 = 0; var3 < allCtrls.length; ++var3) {
         Control var2 = this.getControl(allCtrls[var3]);
         if (var2 != null && !var1.contains(var2)) {
            var1.addElement(var2);
         }
      }

      Control[] var4 = new Control[var1.size()];
      var1.copyInto(var4);
      return var4;
   }

   public Control getControl(String var1) {
      this.chkIllegalState(true);
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         if (var1.startsWith("javax.microedition.media.control.")) {
            var1 = var1.substring("javax.microedition.media.control.".length());
         }

         return (Control)this.ctrlTable.get(var1);
      }
   }

   protected void addControl(String var1, Control var2) {
      this.ctrlTable.put(var1, var2);
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

   protected void setActiveState(boolean var1) {
      Enumeration var2 = this.ctrlTable.elements();
      Vector var3 = new Vector();
      this.active = var1;

      while(var2.hasMoreElements()) {
         Switchable var4 = (Switchable)var2.nextElement();
         if (!var3.contains(var4)) {
            var3.addElement(var4);
            if (var1) {
               var4.activate();
            } else {
               var4.deactivate();
            }
         }
      }

   }

   public synchronized long getTime() {
      return this.timeBase.getTime();
   }

   protected void chkIllegalState(boolean var1) {
      if (this.state == 0 || var1 && this.state == 100) {
         throw new IllegalStateException("Can't invoke method in " + (this.state == 0 ? "closed" : "unrealized") + " state");
      }
   }
}
