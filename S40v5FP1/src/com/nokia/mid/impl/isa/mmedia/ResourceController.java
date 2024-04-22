package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.audio.AudioCapturePlayer;
import com.nokia.mid.impl.isa.mmedia.audio.SampledPlayer;
import com.nokia.mid.impl.isa.mmedia.audio.SynthPlayer;
import com.nokia.mid.impl.isa.mmedia.audio.TonePlayer;
import com.nokia.mid.impl.isa.mmedia.video.CameraPlayer;
import com.nokia.mid.impl.isa.util.SharedObjects;
import java.util.Vector;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public class ResourceController {
   private int audioPlayersStarted;
   private int midiPlayersActive;
   private Vector activePlayersList = new Vector();
   private static final String RESOURCE_CONTROLLER = "com.nokia.mid.impl.isa.mmedia.ResourceController";
   private static final int MIX_STREAMS = nGetMixStreams();
   private static ResourceController rc = (ResourceController)SharedObjects.get("com.nokia.mid.impl.isa.mmedia.ResourceController", "com.nokia.mid.impl.isa.mmedia.ResourceController");
   private static final int MAX_MIDI_PLAYERS = 2;
   private static final byte CAMERA_REC = 1;
   private static final byte AUDIO_REC = 2;
   private static final byte AUDIO_SAMPLED = 3;
   private static final byte AUDIO_SYNTH = 4;
   private static final byte OTHER = 5;

   static void verifyPrefetchAttempt(Player p) throws MediaException {
      ResourceController.PlayerInfo newPlayer = new ResourceController.PlayerInfo((BasicPlayer)p);
      boolean midiPlayer = newPlayer.isSynthAudioType();
      synchronized(rc) {
         rc.updateResourceStatus();
         if (rc.activePlayersList.size() == 1) {
            ResourceController.PlayerInfo ep = (ResourceController.PlayerInfo)rc.activePlayersList.elementAt(0);
            if (ep.isRecordingType()) {
               throw new MediaException("Cannot prefetch while audio capture or camera is active");
            }
         }

         if (newPlayer.isAudioPlayerType()) {
            if (rc.audioPlayersStarted > 0 || midiPlayer && rc.midiPlayersActive >= 2) {
               rc.resolveAudioDispute(newPlayer, true);
            }

            if (rc.audioPlayersStarted > 0 && MIX_STREAMS == 1) {
               throw new MediaException("Cannot prefetch while another audio player is playing");
            }

            if (midiPlayer && rc.midiPlayersActive >= 2) {
               throw new MediaException("Too many MIDI players already prefetched");
            }

            nToneServerStatusChange(true);
            nFcsStatusChange(true);
         } else if (newPlayer.isRecordingType() && rc.activePlayersList.size() > 0) {
            throw new MediaException("Cannot record while other Players are prefetched");
         }

         addPlayerToActiveList(newPlayer);
      }
   }

   static void verifyStartAttempt(Player p) throws MediaException {
      synchronized(rc) {
         ResourceController.PlayerInfo newPlayer = locateActivePlayer((BasicPlayer)p);
         rc.updateResourceStatus();
         if (newPlayer.isAudioPlayerType()) {
            if (rc.audioPlayersStarted >= MIX_STREAMS) {
               rc.resolveAudioDispute(newPlayer, false);
            }

            if (rc.audioPlayersStarted >= MIX_STREAMS) {
               throw new MediaException("Audio player already started");
            }

            newPlayer.setStarted(true);
            ++rc.audioPlayersStarted;
         }

      }
   }

   static void notifyPlayerInactive(Player p) {
      synchronized(rc) {
         ResourceController.PlayerInfo deactivatingPlayer = locateActivePlayer((BasicPlayer)p);
         if (deactivatingPlayer != null) {
            removePlayerFromActiveList(deactivatingPlayer);
            if (deactivatingPlayer.isAudioPlayerType()) {
               nToneServerStatusChange(false);
               nFcsStatusChange(false);
            }
         }

      }
   }

   static void notifyPlayerStopped(Player p) {
      synchronized(rc) {
         ResourceController.PlayerInfo stoppingPlayer = locateActivePlayer((BasicPlayer)p);
         if (stoppingPlayer != null && stoppingPlayer.isAudioPlayerType() && stoppingPlayer.isStarted()) {
            --rc.audioPlayersStarted;
            stoppingPlayer.setStarted(false);
         }

      }
   }

   private final void updateResourceStatus() {
      rc.audioPlayersStarted = 0;
      rc.midiPlayersActive = 0;
      int size = this.activePlayersList.size();

      for(int i = 0; i < size; ++i) {
         ResourceController.PlayerInfo p = (ResourceController.PlayerInfo)this.activePlayersList.firstElement();
         this.activePlayersList.removeElementAt(0);
         if (nIsPlayerActive(p.id)) {
            addPlayerToActiveList(p);
            if (p.isAudioPlayerType() && p.isStarted()) {
               ++rc.audioPlayersStarted;
            }
         }
      }

   }

   private final void resolveAudioDispute(ResourceController.PlayerInfo newPlayer, boolean isPrefetching) {
      if (newPlayer.isHighPriority()) {
         if (rc.audioPlayersStarted >= MIX_STREAMS) {
            this.deactivateAudioPlayer(true);
         }

         if (isPrefetching && newPlayer.isSynthAudioType() && rc.midiPlayersActive >= 2 && rc.audioPlayersStarted >= MIX_STREAMS) {
            this.deactivateAudioPlayer(false);
         }

      }
   }

   private final boolean deactivateAudioPlayer(boolean firstPass) {
      int size = this.activePlayersList.size();

      for(int i = 0; i < size; ++i) {
         ResourceController.PlayerInfo p = (ResourceController.PlayerInfo)this.activePlayersList.elementAt(i);
         if (p.isAudioPlayerType() && !p.isHighPriority() && (firstPass && p.isStarted() || !firstPass && p.isSynthAudioType())) {
            nInterruptPlayer(p.id);
            removePlayerFromActiveList(p);
            return true;
         }
      }

      return false;
   }

   private static ResourceController.PlayerInfo locateActivePlayer(BasicPlayer p) {
      int size = rc.activePlayersList.size();

      for(int i = 0; i < size; ++i) {
         ResourceController.PlayerInfo entry = (ResourceController.PlayerInfo)rc.activePlayersList.elementAt(i);
         if (entry.id == p.playerId) {
            return entry;
         }
      }

      return null;
   }

   private static void addPlayerToActiveList(ResourceController.PlayerInfo p) {
      rc.activePlayersList.addElement(p);
      if (p.isSynthAudioType()) {
         ++rc.midiPlayersActive;
      }

   }

   private static void removePlayerFromActiveList(ResourceController.PlayerInfo p) {
      rc.activePlayersList.removeElement(p);
      if (p.isAudioPlayerType() && p.isStarted()) {
         --rc.audioPlayersStarted;
      }

      if (p.isSynthAudioType()) {
         --rc.midiPlayersActive;
      }

   }

   private static final native boolean nIsPlayerActive(int var0);

   private static final native void nInterruptPlayer(int var0);

   private static final native void nToneServerStatusChange(boolean var0);

   private static final native void nFcsStatusChange(boolean var0);

   private static final native int nGetMixStreams();

   private static class PlayerInfo {
      int id;
      byte type;
      byte category;
      boolean started;

      PlayerInfo(BasicPlayer p) {
         this.category = p.locator.getCategory();
         this.type = getPlayerType(p);
         this.id = p.playerId;
      }

      boolean isHighPriority() {
         return this.category == 1;
      }

      boolean isSynthAudioType() {
         return this.type == 4;
      }

      boolean isRecordingType() {
         return this.type == 1 || this.type == 2;
      }

      boolean isAudioPlayerType() {
         return this.type == 3 || this.type == 4;
      }

      boolean isStarted() {
         return this.started;
      }

      void setStarted(boolean status) {
         this.started = status;
      }

      private static byte getPlayerType(BasicPlayer p) {
         if (!(p instanceof SynthPlayer) && !(p instanceof TonePlayer)) {
            if (p instanceof SampledPlayer) {
               return 3;
            } else if (p instanceof CameraPlayer) {
               return 1;
            } else {
               return (byte)(p instanceof AudioCapturePlayer ? 2 : 5);
            }
         } else {
            return 4;
         }
      }
   }
}
