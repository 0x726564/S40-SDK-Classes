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
   private int kV;
   private int kW;
   private Vector kX = new Vector();
   private static final int kY = nGetMixStreams();
   private static ResourceController kZ = (ResourceController)SharedObjects.get("com.nokia.mid.impl.isa.mmedia.ResourceController", "com.nokia.mid.impl.isa.mmedia.ResourceController");

   static void d(Player var0) throws MediaException {
      ResourceController.PlayerInfo var1;
      boolean var5 = (var1 = new ResourceController.PlayerInfo((BasicPlayer)var0)).u();
      synchronized(kZ) {
         kZ.ac();
         if (kZ.kX.size() == 1 && ((ResourceController.PlayerInfo)kZ.kX.elementAt(0)).v()) {
            throw new MediaException("Cannot prefetch while audio capture or camera is active");
         } else {
            if (var1.w()) {
               if (kZ.kV > 0 || var5 && kZ.kW >= 2) {
                  kZ.a(var1, true);
               }

               if (kZ.kV > 0 && kY == 1) {
                  throw new MediaException("Cannot prefetch while another audio player is playing");
               }

               if (var5 && kZ.kW >= 2) {
                  throw new MediaException("Too many MIDI players already prefetched");
               }

               nRegisterToneServer();
            } else if (var1.v() && kZ.kX.size() > 0) {
               throw new MediaException("Cannot record while other Players are prefetched");
            }

            a(var1);
         }
      }
   }

   static void e(Player var0) throws MediaException {
      synchronized(kZ) {
         ResourceController.PlayerInfo var3 = a((BasicPlayer)var0);
         kZ.ac();
         if (var3.w()) {
            if (kZ.kV >= kY) {
               kZ.a(var3, false);
            }

            if (kZ.kV >= kY) {
               throw new MediaException("Audio player already started");
            }

            var3.setStarted(true);
            ++kZ.kV;
         }

      }
   }

   static void f(Player var0) {
      synchronized(kZ) {
         ResourceController.PlayerInfo var3;
         if ((var3 = a((BasicPlayer)var0)) != null) {
            b(var3);
            if (var3.w()) {
               nUnregisterToneServer();
            }
         }

      }
   }

   static void g(Player var0) {
      synchronized(kZ) {
         ResourceController.PlayerInfo var3;
         if ((var3 = a((BasicPlayer)var0)) != null && var3.w()) {
            --kZ.kV;
            var3.setStarted(false);
         }

      }
   }

   private final void ac() {
      kZ.kV = 0;
      this.kW = 0;
      int var1 = this.kX.size();

      for(int var2 = 0; var2 < var1; ++var2) {
         ResourceController.PlayerInfo var3 = (ResourceController.PlayerInfo)this.kX.firstElement();
         this.kX.removeElementAt(0);
         if (nIsPlayerActive(var3.id)) {
            a(var3);
            if (var3.started) {
               ++kZ.kV;
            }
         }
      }

   }

   private final void a(ResourceController.PlayerInfo var1, boolean var2) {
      if (var1.t()) {
         if (kZ.kV >= kY) {
            this.l(true);
         }

         if (var2 && var1.u() && kZ.kW >= 2 && kZ.kV >= kY) {
            this.l(false);
         }

      }
   }

   private final boolean l(boolean var1) {
      int var2 = this.kX.size();

      for(int var3 = 0; var3 < var2; ++var3) {
         ResourceController.PlayerInfo var4;
         if ((var4 = (ResourceController.PlayerInfo)this.kX.elementAt(var3)).w() && !var4.t() && (var1 && var4.started || !var1 && var4.u())) {
            nInterruptPlayer(var4.id);
            b(var4);
            return true;
         }
      }

      return false;
   }

   private static ResourceController.PlayerInfo a(BasicPlayer var0) {
      int var1 = kZ.kX.size();

      for(int var2 = 0; var2 < var1; ++var2) {
         ResourceController.PlayerInfo var3;
         if ((var3 = (ResourceController.PlayerInfo)kZ.kX.elementAt(var2)).id == var0.playerId) {
            return var3;
         }
      }

      return null;
   }

   private static void a(ResourceController.PlayerInfo var0) {
      kZ.kX.addElement(var0);
      if (var0.u()) {
         ++kZ.kW;
      }

   }

   private static void b(ResourceController.PlayerInfo var0) {
      kZ.kX.removeElement(var0);
      if (var0.started) {
         --kZ.kV;
      }

      if (var0.u()) {
         --kZ.kW;
      }

   }

   private static final native boolean nIsPlayerActive(int var0);

   private static final native void nInterruptPlayer(int var0);

   private static final native void nRegisterToneServer();

   private static final native void nUnregisterToneServer();

   private static final native int nGetMixStreams();

   private static class PlayerInfo {
      int id;
      private byte type;
      private byte bR;
      boolean started;

      PlayerInfo(BasicPlayer var1) {
         this.bR = var1.locator.getCategory();
         this.type = (byte)(!(var1 instanceof SynthPlayer) && !(var1 instanceof TonePlayer) ? (var1 instanceof SampledPlayer ? 3 : (var1 instanceof CameraPlayer ? 1 : (var1 instanceof AudioCapturePlayer ? 2 : 5))) : 4);
         this.id = var1.playerId;
      }

      final boolean t() {
         return this.bR == 1;
      }

      final boolean u() {
         return this.type == 4;
      }

      final boolean v() {
         return this.type == 1 || this.type == 2;
      }

      final boolean w() {
         return this.type == 3 || this.type == 4;
      }

      void setStarted(boolean var1) {
         this.started = var1;
      }
   }
}
