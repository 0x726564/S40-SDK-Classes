package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import com.nokia.mid.impl.isa.source_handling.JavaConsumerSource;
import com.nokia.mid.impl.isa.util.SharedObjects;
import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.RecordControl;

public class RecordCtrlImpl extends Switchable implements RecordControl {
   private static final byte ASK_TO_OVERWRITE = 1;
   private static final byte ASK_TO_SEND_DATA = 2;
   private static final byte TONE_REC_START = 1;
   private static final byte TONE_REC_FINISH = 2;
   private static final byte TONE_SNAPSHOT = 3;
   public static final byte CAPTURE_TYPE_CAMERA = 1;
   public static final byte CAPTURE_TYPE_AUDIO = 2;
   private static final String FILE_LOC = "file://";
   private static final Object nativeLock = SharedObjects.getLock("com.nokia.mid.impl.isa.mmedia.video.RecordCtrlImpl");
   private String locator;
   private OutputStream stream;
   private int sizeLimit;
   private String fileName;
   private boolean useActiveStreaming = false;
   private boolean standBy = true;
   private boolean recordRequested;
   private boolean recording;
   private boolean setRecordSizeLimitSupported = true;
   private String contentType;
   private int captureType;

   public RecordCtrlImpl(BasicPlayer player, boolean setRecordSizeLimitSupported, String contentType, int captureType) {
      this.init(player, setRecordSizeLimitSupported, contentType, captureType);
   }

   private void init(BasicPlayer player, boolean setRecordSizeLimitSupported, String contentType, int captureType) {
      this.player = player;
      this.setRecordSizeLimitSupported = setRecordSizeLimitSupported;
      this.contentType = contentType;
      this.captureType = captureType;
   }

   public final String getContentType() {
      return this.contentType;
   }

   public final void setRecordStream(OutputStream stream) {
      synchronized(this.player) {
         if (this.recordRequested) {
            throw new IllegalStateException("Cannot be called after startRecord is called");
         } else if (this.locator != null) {
            throw new IllegalStateException("setRecordLocation called before commit");
         } else if (stream == null) {
            throw new IllegalArgumentException("null stream specified");
         } else {
            MediaPrefs.checkPermission(1, this.captureType);
            this.stream = stream;
            if (this.captureType != 1) {
               this.useActiveStreaming = true;
            }

         }
      }
   }

   public final void setRecordLocation(String locator) throws IOException, MediaException {
      synchronized(this.player) {
         if (this.recordRequested) {
            throw new IllegalStateException("Cannot be called after startRecord is called");
         } else if (this.stream != null) {
            throw new IllegalStateException("setRecordStream called before commit");
         } else if (locator == null) {
            throw new IllegalArgumentException("null locator specified");
         } else {
            MediaPrefs.checkPermission(4, this.captureType);
            MediaPrefs.checkPermission(1, this.captureType);
            if (locator.startsWith("file://") && locator.length() > "file://".length()) {
               boolean validFile = false;
               boolean deleteFile = false;
               FileConnection fconn = null;

               try {
                  fconn = (FileConnection)Connector.open(locator);
                  if (!fconn.exists()) {
                     fconn.create();
                     deleteFile = true;
                  }

                  validFile = !fconn.isDirectory() && fconn.canWrite();
                  if (deleteFile) {
                     fconn.delete();
                  }
               } catch (Exception var15) {
                  if (var15 instanceof SecurityException) {
                     throw (SecurityException)var15;
                  }

                  validFile = false;
               } finally {
                  if (fconn != null) {
                     fconn.close();
                  }

               }

               if (!validFile) {
                  throw new IOException("Unable to create: " + locator);
               } else {
                  String newFileName = locator.substring("file://".length());
                  if (!deleteFile) {
                     synchronized(nativeLock) {
                        this.nAskUserPremission(1, newFileName);
                     }
                  }

                  this.fileName = newFileName;
                  this.locator = locator;
               }
            } else {
               throw new MediaException("invalid locator: " + locator);
            }
         }
      }
   }

   private final void startResumeRecord() {
      long sourceID = -1L;
      if (!this.recording) {
         this.nPlayTune(1);
      }

      if (this.useActiveStreaming && !this.recording && this.stream != null) {
         sourceID = this.nStartSourceRecord(this.player.getPlayerId());
         if (sourceID != -1L) {
            JavaConsumerSource consumer = new JavaConsumerSource(sourceID, this.stream, 1024);
            consumer.start();
         }
      } else if (this.useActiveStreaming) {
         sourceID = this.nStartSourceRecord(this.player.getPlayerId());
      } else {
         this.nStartRecord(this.player.getPlayerId(), this.fileName);
      }

      this.recording = true;
      this.fileName = null;
   }

   public final void startRecord() {
      synchronized(this.player) {
         if (this.locator == null && this.stream == null) {
            throw new IllegalStateException("startRecord called before setRecordLocation or setRecordStream");
         } else {
            if (!this.recordRequested) {
               if (this.player.isActive() && !this.standBy) {
                  synchronized(nativeLock) {
                     this.startResumeRecord();
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

   public final int setRecordSizeLimit(int size) throws MediaException {
      if (!this.setRecordSizeLimitSupported) {
         throw new MediaException("setRecordSizeLimit not supported");
      } else if (size <= 0) {
         throw new IllegalArgumentException("record size limit must be > 0");
      } else {
         synchronized(this.player) {
            this.sizeLimit = size == Integer.MAX_VALUE ? 0 : size;
            if (!this.standBy && this.player.isActive()) {
               this.sizeLimit = this.nSetRecordSizeLimit(this.player.getPlayerId(), this.sizeLimit);
            }

            return size == Integer.MAX_VALUE ? Integer.MAX_VALUE : this.sizeLimit;
         }
      }
   }

   static void checkSnapshotSecurity() throws SecurityException {
      MediaPrefs.checkPermission(2, 1);
   }

   byte[] getSnapshot(int width, int height, int quality, String encoding) {
      synchronized(this.player) {
         if (this.recording) {
            throw new IllegalStateException("Device Busy");
         } else {
            byte[] res = null;
            if (this.player.isActive() && this.player.getState() == 400) {
               synchronized(nativeLock) {
                  this.nPlayTune(3);
                  res = this.nGetSnapshot(this.player.getPlayerId(), width, height, quality, encoding);
                  MediaPrefs.checkPermission(3, this.captureType);
               }
            }

            return res;
         }
      }
   }

   public boolean getSnapshotFile(int width, int height, int quality, String encoding, String filePath, boolean isBackground) throws MediaException {
      synchronized(this.player) {
         if (this.recording) {
            throw new IllegalStateException("Device Busy");
         } else {
            boolean res = false;
            if (this.player.isActive() && this.player.getState() == 400) {
               synchronized(nativeLock) {
                  this.nPlayTune(3);
                  res = this.nGetSnapshotFile(this.player.getPlayerId(), width, height, quality, encoding, filePath, isBackground);
               }

               return res;
            } else {
               throw new MediaException("Wrong Player state!");
            }
         }
      }
   }

   public void activate() {
      synchronized(nativeLock) {
         try {
            MediaPrefs.checkPermission(7, this.captureType);
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

   public void setStandbyMode(boolean standBy) {
      this.standBy = standBy;
      synchronized(nativeLock) {
         if (!standBy) {
            this.sizeLimit = this.nSetRecordSizeLimit(this.player.getPlayerId(), this.sizeLimit);
         }

         if (this.recordRequested) {
            if (standBy) {
               this.nPauseRecord(this.player.getPlayerId());
            } else {
               this.startResumeRecord();
            }
         }

      }
   }

   private void commit(boolean save) throws IOException {
      synchronized(this.player) {
         try {
            this.stopRecord();
            if (this.player.isActive() && this.recording) {
               synchronized(nativeLock) {
                  this.fileName = this.nStopRecord(this.player.getPlayerId(), save);
                  this.nPlayTune(2);
               }

               if (save && this.stream != null && !this.useActiveStreaming) {
                  synchronized(nativeLock) {
                     this.nAskUserPremission(2, (String)null);
                  }

                  JavaConsumerSource consumer = new JavaConsumerSource(this.fileName, this.stream, 1024);
                  consumer.consumeData();
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

   private native void nAskUserPremission(int var1, String var2) throws SecurityException;

   private native void nStartRecord(int var1, String var2);

   private native long nStartSourceRecord(int var1);

   private native void nPauseRecord(int var1);

   private native String nStopRecord(int var1, boolean var2);

   private native int nSetRecordSizeLimit(int var1, int var2);

   private native byte[] nGetSnapshot(int var1, int var2, int var3, int var4, String var5);

   private native boolean nGetSnapshotFile(int var1, int var2, int var3, int var4, String var5, String var6, boolean var7);

   private native void nPlayTune(int var1);

   static {
      nInit();
   }
}
