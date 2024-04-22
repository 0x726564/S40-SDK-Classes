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
   private static final Hashtable globalPlayerToSource = new Hashtable();
   private static final String AUDIO3D_PACKAGE = "javax.microedition.amms.control.audio3d";

   public SoundSource3DImpl() {
      this.controlManager.addControlsFromEncodedStrings(this.nGetSupportedControls());
      Control[] allControls = this.controlManager.getControls();

      for(int i = 0; i < allControls.length; ++i) {
         ((BaseSoundSource3DControlImpl)allControls[i]).setSoundSource(this);
      }

   }

   public void addMIDIChannel(Player player, int channel) throws MediaException {
      throw new MediaException("Unsupported operation.");
   }

   public void addPlayer(Player player) throws MediaException {
      this.commonChecks(player);
      synchronized(player) {
         synchronized(this) {
            if (!(player instanceof AttachableToSoundSource3D)) {
               throw new MediaException("Player type not supported.");
            }

            attachPlayer(player, this);
            this.addPlayerToList(player);
         }

      }
   }

   public void removeMIDIChannel(Player player, int channel) {
      this.commonChecks(player);
   }

   public void removePlayer(Player player) {
      this.commonChecks(player);
      synchronized(player) {
         synchronized(this) {
            if (!(player instanceof AttachableToSoundSource3D)) {
               throw new IllegalArgumentException("Player type not supported.");
            }

            this.removePlayerFromList(player);
            detachPlayer(player, this);
         }

      }
   }

   public Control[] getControls() {
      return this.controlManager.getControls();
   }

   public Control getControl(String controlType) {
      if (controlType == null) {
         throw new IllegalArgumentException("null");
      } else if (controlType.startsWith("javax.microedition.amms.control.audio3d")) {
         controlType = controlType.substring("javax.microedition.amms.control.audio3d".length() + 1);
         return this.controlManager.getControl("javax.microedition.amms.control.audio3d", controlType);
      } else {
         return null;
      }
   }

   public Enumeration getActivePlayers() {
      return this.listOfActivePlayers.elements();
   }

   static synchronized void notifyActive(Player p) {
      SoundSource3DImpl src = (SoundSource3DImpl)globalPlayerToSource.get(p);
      if (src != null) {
         src.setActive(p);
      }

   }

   static synchronized void notifyInactive(Player p) {
      SoundSource3DImpl src = (SoundSource3DImpl)globalPlayerToSource.get(p);
      if (src != null) {
         src.setInactive(p);
      }

   }

   private void addPlayerToList(Player p) {
      this.listOfIdlePlayers.addElement(p);
   }

   private void removePlayerFromList(Player p) {
      if (!this.listOfIdlePlayers.contains(p)) {
         throw new IllegalArgumentException("Not in source.");
      } else {
         this.listOfIdlePlayers.removeElement(p);
      }
   }

   private synchronized void commonChecks(Player p) {
      if (p == null) {
         throw new IllegalArgumentException("null");
      } else if (this.listOfActivePlayers.size() > 0) {
         throw new IllegalStateException("At least one player is in PREFETCHED/STARTED state");
      } else if (p.getState() > 200) {
         throw new IllegalStateException("Player must not be active.");
      }
   }

   private void activateControls(Player p) {
      Control[] ctrls = this.getControls();

      for(int i = 0; i < ctrls.length; ++i) {
         ((BaseSoundSource3DControlImpl)ctrls[i]).activate(p);
      }

   }

   private void deactivateControls(Player p) {
      Control[] ctrls = this.getControls();

      for(int i = 0; i < ctrls.length; ++i) {
         ((BaseSoundSource3DControlImpl)ctrls[i]).deactivate(p);
      }

   }

   private synchronized void setActive(Player player) {
      this.listOfIdlePlayers.removeElement(player);
      this.listOfActivePlayers.addElement(player);
      this.activateControls(player);
   }

   private synchronized void setInactive(Player player) {
      this.deactivateControls(player);
      this.listOfActivePlayers.removeElement(player);
      this.listOfIdlePlayers.addElement(player);
   }

   private static synchronized void attachPlayer(Player p, SoundSource3D src) throws MediaException {
      if (globalPlayerToSource.get(p) != null) {
         throw new MediaException("Already in another source.");
      } else {
         globalPlayerToSource.put(p, src);
      }
   }

   private static synchronized void detachPlayer(Player p, SoundSource3D src) {
      SoundSource3DImpl s = (SoundSource3DImpl)globalPlayerToSource.get(p);
      if (src == s) {
         globalPlayerToSource.remove(p);
      }

   }

   private native String[] nGetSupportedControls();
}
