package com.nokia.mid.impl.isa.amms.video;

import com.nokia.mid.impl.isa.amms.control.ImageFormatCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import com.nokia.mid.impl.isa.mmedia.video.RecordCtrlImpl;
import java.io.IOException;
import java.util.Hashtable;
import javax.microedition.amms.control.camera.SnapshotControl;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.MediaException;

public class SnapshotCtrlImpl extends Switchable implements SnapshotControl {
   private static final int NO_FREEZE = 0;
   private static final int FREEZE_INTERVAL = 2000;
   private SnapshotCtrlImpl.SnapshotState status = new SnapshotCtrlImpl.SnapshotState();
   private static final int STARTED = 1;
   private static final int STOPPING = 2;
   private static final int INTERRUPTING = 4;
   private static final int ERROR = 8;
   private static final int LASTFILE = 16;
   private static final int READY_TO_UNFREEZE = 32;
   private static final int UNFREEZE = 64;
   private BasicPlayer player;
   private RecordCtrlImpl recCtrlImpl;
   private ImageFormatCtrlImpl imgCtrlImpl;
   private CameraCtrlImpl camCtrlImpl;
   private String prefix = null;
   private String suffix = null;
   private int nbrGeneration = 0;
   private Object stopSignal = new Object();
   private Object unfreezeSignal = new Object();
   private String directory = System.getProperty("fileconn.dir.photos").substring("file:///".length());
   private SnapshotCtrlImpl.FileQueue fileQueue = new SnapshotCtrlImpl.FileQueue();
   private int salt;
   private String actualPrefix;
   private String actualSuffix;
   private int nbrPics;
   private String lastFile;
   private int freezeReq;

   public SnapshotCtrlImpl(BasicPlayer p, RecordCtrlImpl rci, ImageFormatCtrlImpl ifci, CameraCtrlImpl cci) {
      this.player = p;
      this.recCtrlImpl = rci;
      this.imgCtrlImpl = ifci;
      this.camCtrlImpl = cci;
      this.salt = (int)System.currentTimeMillis() % 10000;
   }

   public String getDirectory() {
      return this.directory;
   }

   public String getFilePrefix() {
      return this.prefix;
   }

   public String getFileSuffix() {
      return this.suffix;
   }

   public void setDirectory(String s) {
      if (s == null) {
         throw new IllegalArgumentException("null");
      } else {
         try {
            FileConnection fc = (FileConnection)Connector.open("file:///" + s);
            if (!fc.exists()) {
               throw new IllegalArgumentException("dir does not exist");
            }

            if (!fc.isDirectory()) {
               throw new IllegalArgumentException("not a directory");
            }
         } catch (IOException var3) {
            throw new IllegalArgumentException("bad path");
         }

         this.directory = s;
      }
   }

   public void setFilePrefix(String s) {
      if (s == null) {
         throw new IllegalArgumentException("null");
      } else {
         this.prefix = s;
      }
   }

   public void setFileSuffix(String s) {
      if (s == null) {
         throw new IllegalArgumentException("null");
      } else {
         this.suffix = s;
      }
   }

   public synchronized void start(int maxShots) throws SecurityException {
      if (this.suffix != null && this.prefix != null) {
         if (this.player.getState() != 400) {
            throw new IllegalStateException("player");
         } else if (maxShots <= 0 && maxShots != -1 && maxShots != -2) {
            throw new IllegalArgumentException("maxShots < 0");
         } else {
            MediaPrefs.checkPermission(3, 1);
            SnapshotCtrlImpl.CaptureThread st = new SnapshotCtrlImpl.CaptureThread();
            synchronized(this.stopSignal) {
               if (this.status.hasStatus(1)) {
                  this.status.setStatus(4);

                  try {
                     this.stopSignal.wait();
                  } catch (InterruptedException var8) {
                  }
               }

               if (this.freezeReq == -1) {
                  this.freezeReq = 0;
                  synchronized(this.player) {
                     nUnfreezeViewfinder(this.player.getPlayerId(), this.player.getState() == 400);
                  }
               }

               this.status.clearStatus(2);
               this.status.clearStatus(4);
               this.status.clearStatus(8);
               this.status.clearStatus(32);
               this.status.setStatus(1);
               this.nbrPics = maxShots < 0 ? 1 : maxShots;
               this.actualPrefix = this.directory + this.prefix;
               this.actualSuffix = this.suffix;
               this.freezeReq = maxShots < 0 ? maxShots : 0;
               st.start();
            }
         }
      } else {
         throw new IllegalStateException("prefix/suffix");
      }
   }

   public void stop() {
      this.status.setStatus(2);
   }

   public void deactivate() {
      this.status.setStatus(4);
   }

   public synchronized void unfreeze(boolean flag) {
      if (this.freezeReq != 0) {
         synchronized(this.unfreezeSignal) {
            while(!this.status.hasStatus(32) && !this.status.hasStatus(8)) {
               try {
                  this.unfreezeSignal.wait();
               } catch (InterruptedException var6) {
               }
            }

            this.status.setStatus(64);

            try {
               if (!flag) {
                  FileConnection fc = (FileConnection)Connector.open("file:///" + this.lastFile);
                  if (fc.exists()) {
                     fc.delete();
                  }
               }
            } catch (Exception var5) {
            }

            this.unfreezeSignal.notifyAll();
         }
      }
   }

   public synchronized void fileSavedEvent(long fileId, boolean isError) {
      if (isError) {
         this.fileQueue.clear();
         this.player.dispatchEvent("STORAGE_ERROR", this.lastFile);
      }

      this.fileQueue.renameAndRemove(fileId / 1000L);
   }

   public synchronized String getLastFile() {
      return this.status.hasStatus(16) ? this.lastFile : null;
   }

   private String formatNumber(int n, int padLen) {
      String num = Integer.toString(n);
      StringBuffer sb = new StringBuffer();
      if (num.length() < padLen) {
         int padding = padLen - num.length();

         do {
            sb.append('0');
            --padding;
         } while(padding > 0);
      }

      sb.append(num);
      return sb.toString();
   }

   private static native void nFreezeViewfinder(int var0);

   private static native void nUnfreezeViewfinder(int var0, boolean var1);

   class FileQueue {
      private Hashtable table = new Hashtable();

      public synchronized String addFile(String filepath) {
         Long id = new Long((long)(SnapshotCtrlImpl.this.nbrGeneration * 10000 + SnapshotCtrlImpl.this.salt));
         String playerIdPart = SnapshotCtrlImpl.this.formatNumber(SnapshotCtrlImpl.this.player.getPlayerId(), 8);
         String idPart = SnapshotCtrlImpl.this.formatNumber((int)id, 8);
         String newFileName = "!tmp!" + playerIdPart + idPart + ".jpg";
         int lastIdx = filepath.lastIndexOf(47);
         String path = filepath.substring(0, lastIdx + 1);
         filepath.substring(lastIdx + 1);
         this.table.put(id, filepath);
         return path + newFileName;
      }

      public synchronized void renameAndRemove(long id) {
         Long key = new Long(id);
         String realFilePath = (String)this.table.get(key);
         if (realFilePath != null) {
            this.table.remove(key);
            SnapshotCtrlImpl.this.lastFile = realFilePath;
            int lastIdx = realFilePath.lastIndexOf(47);
            String path = realFilePath.substring(0, lastIdx + 1);
            String filename = realFilePath.substring(lastIdx + 1);
            String encodedFilename = path + "!tmp!" + SnapshotCtrlImpl.this.formatNumber(SnapshotCtrlImpl.this.player.getPlayerId(), 8) + SnapshotCtrlImpl.this.formatNumber((int)id, 8) + ".jpg";

            try {
               FileConnection fc = (FileConnection)Connector.open("file:///" + encodedFilename);
               if (fc.exists()) {
                  fc.rename(filename);
                  fc.close();
               }
            } catch (IOException var10) {
               SnapshotCtrlImpl.this.status.setStatus(8);
               SnapshotCtrlImpl.this.player.serializeEvent(107, -1L);
            }
         }

      }

      public synchronized void renameAndRemove(String encodedFilepath) {
         int lastIdx = encodedFilepath.lastIndexOf(47);
         encodedFilepath.substring(0, lastIdx + 1);
         String encodedFilename = encodedFilepath.substring(lastIdx + 1);
         if (!encodedFilename.startsWith("!tmp!") && encodedFilename.length() != 25) {
            throw new IllegalArgumentException("Bad temporary filename!");
         } else {
            String idStr = encodedFilename.substring(13, encodedFilename.lastIndexOf(46));
            Long key = new Long(Long.parseLong(idStr));
            String realFilePath = (String)this.table.get(key);
            if (realFilePath != null) {
               this.table.remove(key);
               String realFilename = realFilePath.substring(realFilePath.lastIndexOf(47) + 1);

               try {
                  FileConnection fc = (FileConnection)Connector.open("file:///" + encodedFilepath);
                  if (fc.exists()) {
                     fc.rename(realFilename);
                     fc.close();
                  }
               } catch (IOException var10) {
                  SnapshotCtrlImpl.this.status.setStatus(8);
                  SnapshotCtrlImpl.this.player.serializeEvent(107, -1L);
               }

            } else {
               throw new IllegalArgumentException("Bad key " + key);
            }
         }
      }

      public synchronized void clear() {
         this.table.clear();
      }

      public synchronized boolean isEmpty() {
         return this.table.isEmpty();
      }
   }

   class SnapshotState {
      private int theState;

      public SnapshotState() {
      }

      public void setStatus(int status) {
         this.theState |= status;
      }

      public void clearStatus(int status) {
         this.theState &= ~status;
      }

      public boolean hasStatus(int status) {
         return (this.theState & status) != 0;
      }

      public void clear() {
         this.theState = 0;
      }
   }

   class CaptureThread extends Thread {
      public void run() {
         int[] res = SnapshotCtrlImpl.this.camCtrlImpl.getSupportedStillResolutions();
         int width = res[SnapshotCtrlImpl.this.camCtrlImpl.getStillResolution() * 2];
         int height = res[SnapshotCtrlImpl.this.camCtrlImpl.getStillResolution() * 2 + 1];
         int quality = SnapshotCtrlImpl.this.imgCtrlImpl.getIntParameterValue("quality");

         for(int j = 0; j < SnapshotCtrlImpl.this.nbrPics && !SnapshotCtrlImpl.this.status.hasStatus(2) && !SnapshotCtrlImpl.this.status.hasStatus(4); ++j) {
            try {
               StringBuffer sb = new StringBuffer();
               sb.append(SnapshotCtrlImpl.this.actualPrefix);
               sb.append(SnapshotCtrlImpl.this.formatNumber(SnapshotCtrlImpl.this.nbrGeneration, 4));
               sb.append(SnapshotCtrlImpl.this.actualSuffix);
               SnapshotCtrlImpl.this.lastFile = sb.toString();
               String encodedFilename = SnapshotCtrlImpl.this.fileQueue.addFile(SnapshotCtrlImpl.this.lastFile);
               if (!SnapshotCtrlImpl.this.recCtrlImpl.getSnapshotFile(width, height, quality, "image/jpeg", encodedFilename, SnapshotCtrlImpl.this.freezeReq == 0)) {
                  SnapshotCtrlImpl.this.fileQueue.renameAndRemove(encodedFilename);
               }
            } catch (MediaException var21) {
               SnapshotCtrlImpl.this.fileQueue.clear();
               SnapshotCtrlImpl.this.status.setStatus(8);
               SnapshotCtrlImpl.this.status.setStatus(16);
               SnapshotCtrlImpl.this.player.serializeEvent(107, -1L);
               break;
            }

            SnapshotCtrlImpl.this.nbrGeneration++;
         }

         synchronized(SnapshotCtrlImpl.this.stopSignal) {
            if (!SnapshotCtrlImpl.this.status.hasStatus(8) && !SnapshotCtrlImpl.this.status.hasStatus(4) && SnapshotCtrlImpl.this.freezeReq == 0) {
               synchronized(SnapshotCtrlImpl.this.fileQueue) {
                  while(!SnapshotCtrlImpl.this.fileQueue.isEmpty()) {
                     try {
                        SnapshotCtrlImpl.this.fileQueue.wait(100L);
                        if (SnapshotCtrlImpl.this.status.hasStatus(4)) {
                           SnapshotCtrlImpl.this.fileQueue.clear();
                        }
                     } catch (InterruptedException var17) {
                     }
                  }
               }

               SnapshotCtrlImpl.this.status.setStatus(16);
               SnapshotCtrlImpl.this.player.serializeEvent(108, -1L);
            } else if (SnapshotCtrlImpl.this.freezeReq == -1) {
               synchronized(SnapshotCtrlImpl.this.unfreezeSignal) {
                  SnapshotCtrlImpl.this.status.setStatus(32);
                  SnapshotCtrlImpl.this.unfreezeSignal.notifyAll();
                  if (!SnapshotCtrlImpl.this.status.hasStatus(8)) {
                     synchronized(SnapshotCtrlImpl.this.player) {
                        SnapshotCtrlImpl.nFreezeViewfinder(SnapshotCtrlImpl.this.player.getPlayerId());
                     }

                     SnapshotCtrlImpl.this.status.setStatus(16);
                     SnapshotCtrlImpl.this.player.serializeEvent(109, -1L);
                     SnapshotCtrlImpl.this.player.serializeEvent(108, -1L);

                     while(!SnapshotCtrlImpl.this.status.hasStatus(4) && !SnapshotCtrlImpl.this.status.hasStatus(2)) {
                        try {
                           SnapshotCtrlImpl.this.unfreezeSignal.wait(1000L);
                        } catch (InterruptedException var15) {
                        }

                        if (SnapshotCtrlImpl.this.status.hasStatus(64)) {
                           this.unfreezeViewfinder();
                           SnapshotCtrlImpl.this.status.clearStatus(64);
                           break;
                        }
                     }
                  }
               }
            } else if (!SnapshotCtrlImpl.this.status.hasStatus(8) && !SnapshotCtrlImpl.this.status.hasStatus(4) && SnapshotCtrlImpl.this.freezeReq == -2) {
               synchronized(SnapshotCtrlImpl.this.player) {
                  this.freezeViewfinder();

                  try {
                     Thread.currentThread();
                     Thread.sleep(2000L);
                  } catch (InterruptedException var13) {
                  }

                  this.unfreezeViewfinder();
                  if (!SnapshotCtrlImpl.this.status.hasStatus(4)) {
                     SnapshotCtrlImpl.this.player.serializeEvent(108, -1L);
                     SnapshotCtrlImpl.this.status.setStatus(16);
                  }
               }
            }

            if (SnapshotCtrlImpl.this.status.hasStatus(8)) {
               SnapshotCtrlImpl.this.player.serializeEvent(108, -1L);
            }

            SnapshotCtrlImpl.this.status.clearStatus(1);
            SnapshotCtrlImpl.this.stopSignal.notifyAll();
         }
      }

      private void unfreezeViewfinder() {
         synchronized(SnapshotCtrlImpl.this.player) {
            SnapshotCtrlImpl.nUnfreezeViewfinder(SnapshotCtrlImpl.this.player.getPlayerId(), SnapshotCtrlImpl.this.player.getState() == 400);
         }
      }

      private void freezeViewfinder() {
         synchronized(SnapshotCtrlImpl.this.player) {
            SnapshotCtrlImpl.nFreezeViewfinder(SnapshotCtrlImpl.this.player.getPlayerId());
         }
      }
   }
}
