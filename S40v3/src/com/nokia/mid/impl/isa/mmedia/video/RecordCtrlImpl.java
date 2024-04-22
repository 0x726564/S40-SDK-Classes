package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import com.nokia.mid.impl.isa.source_handling.JavaConsumerSource;
import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.RecordControl;

public class RecordCtrlImpl extends Switchable implements RecordControl {
   private static final byte RECORD_CTRL_PERMISSION = 1;
   private static final byte SNAPSHOT_PRE_PERMISSION = 2;
   private static final byte SNAPSHOT_PERMISSION = 3;
   private static final byte RECORD_JSR75_PERMISSION = 4;
   private static final byte ASK_FIRST_TIME_PERMISSION = 5;
   private static final byte ASK_TO_OVERWRITE = 1;
   private static final byte ASK_TO_SEND_DATA = 2;
   private static final byte TONE_REC_START = 1;
   private static final byte TONE_REC_FINISH = 2;
   private static final byte TONE_SNAPSHOT = 3;
   private static final byte CAPTURE_TYPE_NONE = 0;
   public static final byte CAPTURE_TYPE_CAMERA = 1;
   public static final byte CAPTURE_TYPE_AUDIO = 2;
   private static final String FILE_LOC = "file://";
   private static final Object nativeLock = new Object();
   private String locator;
   private OutputStream stream;
   private int sizeLimit;
   private String fileName;
   private boolean standBy = true;
   private boolean recordRequested;
   private boolean recording;
   private boolean setRecordSizeLimitSupported = true;
   private String contentType;
   private int captureType;

   public RecordCtrlImpl(BasicPlayer var1, boolean var2, String var3, int var4) {
      this.player = var1;
      this.setRecordSizeLimitSupported = var2;
      this.contentType = var3;
      this.captureType = var4;
   }

   public final String getContentType() {
      return this.contentType;
   }

   public final void setRecordStream(OutputStream var1) {
      synchronized(this.player) {
         if (this.recordRequested) {
            throw new IllegalStateException("Cannot be called after startRecord is called");
         } else if (this.locator != null) {
            throw new IllegalStateException("setRecordLocation called before commit");
         } else if (var1 == null) {
            throw new IllegalArgumentException("null stream specified");
         } else {
            synchronized(nativeLock) {
               nCheckSecurity(this.captureType, 1);
            }

            this.stream = var1;
         }
      }
   }

   public final void setRecordLocation(String var1) throws IOException, MediaException {
      synchronized(this.player) {
         if (this.recordRequested) {
            throw new IllegalStateException("Cannot be called after startRecord is called");
         } else if (this.stream != null) {
            throw new IllegalStateException("setRecordStream called before commit");
         } else if (var1 == null) {
            throw new IllegalArgumentException("null locator specified");
         } else {
            synchronized(nativeLock) {
               nCheckSecurity(this.captureType, 4);
               nCheckSecurity(this.captureType, 1);
            }

            if (var1.startsWith("file://") && var1.length() > "file://".length()) {
               boolean var3 = false;
               boolean var4 = false;
               FileConnection var5 = null;

               try {
                  var5 = (FileConnection)Connector.open(var1);
                  if (!var5.exists()) {
                     var5.create();
                     var4 = true;
                  }

                  var3 = !var5.isDirectory() && var5.canWrite();
                  if (var4) {
                     var5.delete();
                  }
               } catch (Exception var17) {
                  if (var17 instanceof SecurityException) {
                     throw (SecurityException)var17;
                  }

                  var3 = false;
               } finally {
                  if (var5 != null) {
                     var5.close();
                  }

               }

               if (!var3) {
                  throw new IOException("Unable to create: " + var1);
               } else {
                  String var6 = var1.substring("file://".length());
                  if (!var4) {
                     synchronized(nativeLock) {
                        this.nAskUserPremission(1, var6);
                     }
                  }

                  this.fileName = var6;
                  this.locator = var1;
               }
            } else {
               throw new MediaException("invalid locator: " + var1);
            }
         }
      }
   }

   public final void startRecord() {
      synchronized(this.player) {
         if (this.locator == null && this.stream == null) {
            throw new IllegalStateException("startRecord called before setRecordLocation or setRecordStream");
         } else {
            if (!this.recordRequested) {
               if (this.player.isActive() && !this.standBy) {
                  synchronized(nativeLock) {
                     if (!this.recording) {
                        this.recording = true;
                        this.nPlayTune(1);
                     }

                     this.nStartRecord(this.player.getPlayerId(), this.fileName);

                     try {
                        Thread.sleep(100L);
                     } catch (Exception var6) {
                     }

                     this.fileName = null;
                  }
               }

               this.recordRequested = true;
               this.player.serializeEvent(11, this.player.getMediaTime());
            }

         }
      }
   }

   public final void stopRecord() {
      synchronized(this.player) {
         if (this.recordRequested) {
            this.recordRequested = false;
            if (this.player.isActive() && !this.standBy) {
               synchronized(nativeLock) {
                  this.nPauseRecord(this.player.getPlayerId());
               }
            }

            this.player.serializeEvent(12, this.player.getMediaTime());
         }

      }
   }

   public final void commit() throws IOException {
      this.commit(true);
   }

   public final void reset() throws IOException {
      this.commit(false);
   }

   public final int setRecordSizeLimit(int var1) throws MediaException {
      if (!this.setRecordSizeLimitSupported) {
         throw new MediaException("setRecordSizeLimit not supported");
      } else if (var1 <= 0) {
         throw new IllegalArgumentException("record size limit must be > 0");
      } else {
         synchronized(this.player) {
            this.sizeLimit = var1 == Integer.MAX_VALUE ? 0 : var1;
            if (!this.standBy && this.player.isActive()) {
               this.sizeLimit = this.nSetRecordSizeLimit(this.player.getPlayerId(), this.sizeLimit);
            }

            return var1 == Integer.MAX_VALUE ? Integer.MAX_VALUE : this.sizeLimit;
         }
      }
   }

   static void checkSnapshotSecurity() throws SecurityException {
      synchronized(nativeLock) {
         nCheckSecurity(1, 2);
      }
   }

   byte[] getSnapshot(int var1, int var2, int var3) {
      synchronized(this.player) {
         if (this.recording) {
            throw new IllegalStateException("Device Busy");
         } else {
            byte[] var5 = null;
            if (this.player.isActive()) {
               int var10000 = this.player.getState();
               BasicPlayer var10001 = this.player;
               if (var10000 == 400) {
                  synchronized(nativeLock) {
                     this.nPlayTune(3);
                     var5 = this.nGetSnapshot(this.player.getPlayerId(), var1, var2, var3);
                     nCheckSecurity(this.captureType, 3);
                  }
               }
            }

            return var5;
         }
      }
   }

   public void activate() {
      synchronized(nativeLock) {
         try {
            nCheckSecurity(this.captureType, 5);
         } catch (Exception var4) {
         }

      }
   }

   public void deactivate() {
      try {
         this.commit(false);
      } catch (Exception var2) {
      }

   }

   public void setStandbyMode(boolean var1) {
      this.standBy = var1;
      synchronized(nativeLock) {
         if (!var1) {
            this.sizeLimit = this.nSetRecordSizeLimit(this.player.getPlayerId(), this.sizeLimit);
         }

         if (this.recordRequested) {
            if (var1) {
               this.nPauseRecord(this.player.getPlayerId());
            } else {
               if (!this.recording) {
                  this.recording = true;
                  this.nPlayTune(1);
               }

               this.nStartRecord(this.player.getPlayerId(), this.fileName);

               try {
                  Thread.sleep(100L);
               } catch (Exception var5) {
               }

               this.fileName = null;
            }
         }

      }
   }

   private void commit(boolean var1) throws IOException {
      synchronized(this.player) {
         try {
            this.stopRecord();
            if (this.player.isActive() && this.recording) {
               synchronized(nativeLock) {
                  this.fileName = this.nStopRecord(this.player.getPlayerId(), var1);
                  this.nPlayTune(2);
               }

               if (var1 && this.stream != null) {
                  synchronized(nativeLock) {
                     this.nAskUserPremission(2, (String)null);
                  }

                  JavaConsumerSource var3 = new JavaConsumerSource(this.fileName, this.stream, 1024);
                  var3.consumeData();
               }
            }
         } catch (Exception var16) {
            throw new IOException("Unable to upload data");
         } finally {
            this.recordRequested = false;
            this.recording = false;
            this.fileName = null;
            this.locator = null;
            this.stream = null;
         }

      }
   }

   private static native void nInit();

   private static native void nCheckSecurity(int var0, int var1) throws SecurityException;

   private native void nAskUserPremission(int var1, String var2) throws SecurityException;

   private native void nStartRecord(int var1, String var2);

   private native void nPauseRecord(int var1);

   private native String nStopRecord(int var1, boolean var2);

   private native int nSetRecordSizeLimit(int var1, int var2);

   private native byte[] nGetSnapshot(int var1, int var2, int var3, int var4);

   private native void nPlayTune(int var1);

   static {
      nInit();
   }
}
