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
   public static final byte CAPTURE_TYPE_CAMERA = 1;
   public static final byte CAPTURE_TYPE_AUDIO = 2;
   private static final Object aG = SharedObjects.getLock("com.nokia.mid.impl.isa.mmedia.video.RecordCtrlImpl");
   private String mb;
   private OutputStream mc;
   private int md;
   private String fileName;
   private boolean me = true;
   private boolean mf;
   private boolean mg;
   private boolean mh = true;
   private String contentType;
   private int mi;

   public RecordCtrlImpl(BasicPlayer var1, boolean var2, String var3, int var4) {
      this.player = var1;
      this.mh = var2;
      this.contentType = var3;
      this.mi = var4;
   }

   public final String getContentType() {
      return this.contentType;
   }

   public final void setRecordStream(OutputStream var1) {
      synchronized(this.player) {
         if (this.mf) {
            throw new IllegalStateException("Cannot be called after startRecord is called");
         } else if (this.mb != null) {
            throw new IllegalStateException("setRecordLocation called before commit");
         } else if (var1 == null) {
            throw new IllegalArgumentException("null stream specified");
         } else {
            MediaPrefs.checkPermission(1, this.mi);
            this.mc = var1;
         }
      }
   }

   public final void setRecordLocation(String var1) throws IOException, MediaException {
      synchronized(this.player) {
         if (this.mf) {
            throw new IllegalStateException("Cannot be called after startRecord is called");
         } else if (this.mc != null) {
            throw new IllegalStateException("setRecordStream called before commit");
         } else if (var1 == null) {
            throw new IllegalArgumentException("null locator specified");
         } else {
            MediaPrefs.checkPermission(4, this.mi);
            MediaPrefs.checkPermission(1, this.mi);
            if (var1.startsWith("file://") && var1.length() > "file://".length()) {
               boolean var3 = false;
               boolean var4 = false;
               FileConnection var5 = null;

               try {
                  if (!(var5 = (FileConnection)Connector.open(var1)).exists()) {
                     var5.create();
                     var4 = true;
                  }

                  var3 = !var5.isDirectory() && var5.canWrite();
                  if (var4) {
                     var5.delete();
                  }
               } catch (Exception var13) {
                  if (var13 instanceof SecurityException) {
                     throw (SecurityException)var13;
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
                     synchronized(aG) {
                        this.nAskUserPremission(1, var6);
                     }
                  }

                  this.fileName = var6;
                  this.mb = var1;
               }
            } else {
               throw new MediaException("invalid locator: " + var1);
            }
         }
      }
   }

   public final void startRecord() {
      synchronized(this.player) {
         if (this.mb == null && this.mc == null) {
            throw new IllegalStateException("startRecord called before setRecordLocation or setRecordStream");
         } else {
            if (!this.mf) {
               if (this.player.isActive() && !this.me) {
                  synchronized(aG) {
                     if (!this.mg) {
                        this.mg = true;
                        this.nPlayTune(1);
                     }

                     this.nStartRecord(this.player.getPlayerId(), this.fileName);
                     this.fileName = null;
                  }
               }

               this.mf = true;
               this.player.serializeEvent(11, this.player.getMediaTime());
            }

         }
      }
   }

   public final void stopRecord() {
      synchronized(this.player) {
         if (this.mf) {
            this.mf = false;
            if (this.player.isActive() && !this.me) {
               synchronized(aG) {
                  this.nPauseRecord(this.player.getPlayerId());
               }
            }

            this.player.serializeEvent(12, this.player.getMediaTime());
         }

      }
   }

   public final void commit() throws IOException {
      this.m(true);
   }

   public final void reset() throws IOException {
      this.m(false);
   }

   public final int setRecordSizeLimit(int var1) throws MediaException {
      if (!this.mh) {
         throw new MediaException("setRecordSizeLimit not supported");
      } else if (var1 <= 0) {
         throw new IllegalArgumentException("record size limit must be > 0");
      } else {
         synchronized(this.player) {
            this.md = var1 == Integer.MAX_VALUE ? 0 : var1;
            if (!this.me && this.player.isActive()) {
               this.md = this.nSetRecordSizeLimit(this.player.getPlayerId(), this.md);
            }

            return var1 == Integer.MAX_VALUE ? Integer.MAX_VALUE : this.md;
         }
      }
   }

   final byte[] a(int var1, int var2, int var3, String var4) {
      synchronized(this.player) {
         if (this.mg) {
            throw new IllegalStateException("Device Busy");
         } else {
            byte[] var6 = null;
            if (this.player.isActive() && this.player.getState() == 400) {
               synchronized(aG) {
                  this.nPlayTune(3);
                  var6 = this.nGetSnapshot(this.player.getPlayerId(), var1, var2, var3, var4);
                  MediaPrefs.checkPermission(3, this.mi);
               }
            }

            return var6;
         }
      }
   }

   public void activate() {
      synchronized(aG) {
         try {
            MediaPrefs.checkPermission(6, this.mi);
         } catch (Exception var2) {
         }

      }
   }

   public void deactivate() {
      try {
         this.m(false);
      } catch (Exception var1) {
      }
   }

   public void setStandbyMode(boolean var1) {
      this.me = var1;
      synchronized(aG) {
         if (!var1) {
            this.md = this.nSetRecordSizeLimit(this.player.getPlayerId(), this.md);
         }

         if (this.mf) {
            if (var1) {
               this.nPauseRecord(this.player.getPlayerId());
            } else {
               if (!this.mg) {
                  this.mg = true;
                  this.nPlayTune(1);
               }

               this.nStartRecord(this.player.getPlayerId(), this.fileName);
               this.fileName = null;
            }
         }

      }
   }

   private void m(boolean var1) throws IOException {
      synchronized(this.player) {
         try {
            this.stopRecord();
            if (this.player.isActive() && this.mg) {
               synchronized(aG) {
                  this.fileName = this.nStopRecord(this.player.getPlayerId(), var1);
                  this.nPlayTune(2);
               }

               if (var1 && this.mc != null) {
                  synchronized(aG) {
                     this.nAskUserPremission(2, (String)null);
                  }

                  (new JavaConsumerSource(this.fileName, this.mc, 1024)).consumeData();
               }
            }
         } catch (Exception var12) {
            throw new IOException("Unable to upload data");
         } finally {
            this.mf = false;
            this.mg = false;
            this.fileName = null;
            this.mb = null;
            this.mc = null;
         }

      }
   }

   private static native void nInit();

   private native void nAskUserPremission(int var1, String var2) throws SecurityException;

   private native void nStartRecord(int var1, String var2);

   private native void nPauseRecord(int var1);

   private native String nStopRecord(int var1, boolean var2);

   private native int nSetRecordSizeLimit(int var1, int var2);

   private native byte[] nGetSnapshot(int var1, int var2, int var3, int var4, String var5);

   private native void nPlayTune(int var1);

   static {
      nInit();
   }
}
