package com.nokia.mid.impl.isa.amms.audio;

import com.nokia.mid.impl.isa.mmedia.ControlManager;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.amms.SoundSource3D;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public class SoundSource3DImpl implements SoundSource3D {
   protected final Vector listOfIdlePlayers = new Vector();
   protected final Vector listOfActivePlayers = new Vector();
   protected final ControlManager controlManager = new ControlManager();
   private static final Hashtable ee = new Hashtable();

   public SoundSource3DImpl() {
      this.controlManager.addControlsFromEncodedStrings(this.nGetSupportedControls());
      Control[] var1 = this.controlManager.getControls();

      for(int var2 = 0; var2 < var1.length; ++var2) {
         ((BaseSoundSource3DControlImpl)var1[var2]).setSoundSource(this);
      }

   }

   public void addMIDIChannel(Player var1, int var2) throws MediaException {
      throw new MediaException("Unsupported operation.");
   }

   public void addPlayer(Player var1) throws MediaException {
      this.c(var1);
      synchronized(var1) {
         synchronized(this) {
            if (!(var1 instanceof AttachableToSoundSource3D)) {
               throw new MediaException("Player type not supported.");
            }

            a(var1, this);
            this.listOfIdlePlayers.addElement(var1);
         }

      }
   }

   public void removeMIDIChannel(Player var1, int var2) {
      this.c(var1);
   }

   public void removePlayer(Player var1) {
      this.c(var1);
      synchronized(var1) {
         synchronized(this) {
            if (!(var1 instanceof AttachableToSoundSource3D)) {
               throw new IllegalArgumentException("Player type not supported.");
            }

            if (!this.listOfIdlePlayers.contains(var1)) {
               throw new IllegalArgumentException("Not in source.");
            }

            this.listOfIdlePlayers.removeElement(var1);
            b(var1, this);
         }

      }
   }

   public Control[] getControls() {
      return this.controlManager.getControls();
   }

   public Control getControl(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null");
      } else if (var1.startsWith("javax.microedition.amms.control.audio3d")) {
         var1 = var1.substring("javax.microedition.amms.control.audio3d".length() + 1);
         return this.controlManager.getControl("javax.microedition.amms.control.audio3d", var1);
      } else {
         return null;
      }
   }

   public Enumeration getActivePlayers() {
      return this.listOfActivePlayers.elements();
   }

   static synchronized void a(Player var0) {
      SoundSource3DImpl var1;
      if ((var1 = (SoundSource3DImpl)ee.get(var0)) != null) {
         var1.setActive(var0);
      }

   }

   static synchronized void b(Player var0) {
      SoundSource3DImpl var1;
      if ((var1 = (SoundSource3DImpl)ee.get(var0)) != null) {
         var1.setInactive(var0);
      }

   }

   private synchronized void c(Player var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null");
      } else if (this.listOfActivePlayers.size() > 0) {
         throw new IllegalStateException("At least one player is in PREFETCHED/STARTED state");
      } else if (var1.getState() > 200) {
         throw new IllegalStateException("Player must not be active.");
      }
   }

   private synchronized void setActive(Player var1) {
      this.listOfIdlePlayers.removeElement(var1);
      this.listOfActivePlayers.addElement(var1);
      var1 = var1;
      Control[] var3 = this.getControls();

      for(int var2 = 0; var2 < var3.length; ++var2) {
         ((BaseSoundSource3DControlImpl)var3[var2]).activate(var1);
      }

   }

   private synchronized void setInactive(Player var1) {
      Player var3 = var1;
      Control[] var2 = this.getControls();

      for(int var4 = 0; var4 < var2.length; ++var4) {
         ((BaseSoundSource3DControlImpl)var2[var4]).deactivate(var3);
      }

      this.listOfActivePlayers.removeElement(var1);
      this.listOfIdlePlayers.addElement(var1);
   }

   private static synchronized void a(Player var0, SoundSource3D var1) throws MediaException {
      if (ee.get(var0) != null) {
         throw new MediaException("Already in another source.");
      } else {
         ee.put(var0, var1);
      }
   }

   private static synchronized void b(Player var0, SoundSource3D var1) {
      SoundSource3DImpl var2 = (SoundSource3DImpl)ee.get(var0);
      if (var1 == var2) {
         ee.remove(var0);
      }

   }

   private native String[] nGetSupportedControls();
}
