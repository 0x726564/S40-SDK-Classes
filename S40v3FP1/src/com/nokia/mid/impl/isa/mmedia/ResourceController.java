package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.audio.AudioCapturePlayer;
import com.nokia.mid.impl.isa.mmedia.audio.SampledPlayer;
import com.nokia.mid.impl.isa.mmedia.audio.SynthPlayer;
import com.nokia.mid.impl.isa.mmedia.audio.TonePlayer;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public class ResourceController {
   private static final int MAX_MIDI_PLAYERS = 2;
   private static boolean audioPlayerStarted = false;
   private static int midiPlayersInPrefetch;
   private static boolean audioCaptureStarted = false;

   static synchronized void verifyPrefetchAttempt(Player var0) throws MediaException {
      boolean var1 = var0 instanceof SynthPlayer || var0 instanceof TonePlayer;
      boolean var2 = var1 || var0 instanceof SampledPlayer;
      if (var2) {
         if (audioPlayerStarted) {
            throw new MediaException("Cannot prefetch while another audio player is playing.");
         }

         if (var1) {
            if (midiPlayersInPrefetch >= 2) {
               throw new MediaException("Too many MIDI players already prefetched.");
            }

            ++midiPlayersInPrefetch;
         }
      }

   }

   static synchronized void verifyStartAttempt(Player var0) throws MediaException {
      if (var0 instanceof SampledPlayer || var0 instanceof SynthPlayer || var0 instanceof TonePlayer) {
         if (audioPlayerStarted) {
            throw new MediaException("Audio player already started.");
         }

         if (audioCaptureStarted) {
            throw new MediaException("Can't play audio while recording.");
         }

         audioPlayerStarted = true;
      }

      if (var0 instanceof AudioCapturePlayer) {
         if (audioPlayerStarted) {
            throw new MediaException("Can't record while playing audio");
         }

         if (audioCaptureStarted) {
            throw new MediaException("Can't have more than one capture://audio player");
         }

         audioCaptureStarted = true;
      }

   }

   static synchronized void notifyPlayerInactive(Player var0) {
      boolean var1 = var0 instanceof SynthPlayer || var0 instanceof TonePlayer;
      boolean var2 = var1 || var0 instanceof SampledPlayer;
      boolean var3 = var0 instanceof AudioCapturePlayer;
      if (var1) {
         --midiPlayersInPrefetch;
      }

      if (var2) {
         audioPlayerStarted = false;
      }

      if (var3) {
         audioCaptureStarted = false;
      }

   }

   static synchronized void notifyPlayerStopped(Player var0) {
      if (var0 instanceof SampledPlayer || var0 instanceof SynthPlayer || var0 instanceof TonePlayer) {
         audioPlayerStarted = false;
      }

      if (var0 instanceof AudioCapturePlayer) {
         audioCaptureStarted = false;
      }

   }
}
