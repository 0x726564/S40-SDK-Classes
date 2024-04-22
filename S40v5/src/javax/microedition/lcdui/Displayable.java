package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.KeyMap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import com.nokia.mid.ui.lcdui.VisibilityListener;
import javax.microedition.lcdui.game.GameCanvas;

public abstract class Displayable {
   static final UIStyle eI = UIStyle.getUIStyle();
   private static SoftkeyManager eJ = SoftkeyManager.getSoftkeyManager();
   static final KeyMap eK = KeyMap.getKeyMap();
   static final Zone eL;
   static final Zone eM;
   private static Zone eN;
   static final Zone eO;
   static final Zone eP;
   Ticker eQ;
   String title;
   int[] eR;
   boolean eS;
   boolean eT;
   boolean eU;
   Display eV;
   CommandVector eW;
   private CommandVector eX;
   CommandVector eY;
   private SoftLabel eZ;
   CommandListener fa;
   VisibilityListener fb;
   private boolean fc;
   private boolean fd;
   private boolean fe;
   private boolean ff;
   private Displayable fg;
   boolean fh;

   Displayable() {
      this((String)null);
   }

   Displayable(String var1) {
      this.eQ = null;
      this.title = null;
      this.eR = new int[4];
      this.eS = false;
      this.eT = true;
      this.eU = false;
      this.eW = new CommandVector();
      this.eX = new CommandVector(3);
      this.eY = new CommandVector();
      this.eZ = new SoftLabel(this);
      this.fa = null;
      this.fc = false;
      this.fd = false;
      this.fe = false;
      this.ff = false;
      this.fg = null;
      this.fh = false;
      synchronized(Display.hG) {
         this.ai();
         this.setTitleImpl(var1);
      }
   }

   public void addCommand(Command var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         synchronized(Display.hG) {
            this.b(var1);
         }
      }
   }

   public void removeCommand(Command var1) {
      if (var1 != null) {
         synchronized(Display.hG) {
            this.d(var1);
         }
      }
   }

   public void setCommandListener(CommandListener var1) {
      synchronized(Display.hG) {
         this.fa = var1;
      }
   }

   public Ticker getTicker() {
      return this.eQ;
   }

   public void setTicker(Ticker var1) {
      synchronized(Display.hG) {
         this.setTickerImpl(var1);
      }
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String var1) {
      synchronized(Display.hG) {
         this.setTitleImpl(var1);
      }
   }

   public int getWidth() {
      return this.eR[2];
   }

   public int getHeight() {
      return this.eR[3];
   }

   public boolean isShown() {
      return this.C();
   }

   protected void sizeChanged(int var1, int var2) {
   }

   boolean C() {
      return this.eU;
   }

   void setVisibilityListener(VisibilityListener var1) {
      this.fb = var1;
   }

   boolean b(Command var1) {
      boolean var2;
      if (var2 = this.eW.g(var1)) {
         this.c(true);
      }

      return var2;
   }

   boolean d(Command var1) {
      boolean var2;
      if (var2 = this.eW.h(var1)) {
         this.c(true);
      }

      return var2;
   }

   void setTickerImpl(Ticker var1) {
      Ticker var2;
      if ((var2 = this.eQ) != var1) {
         this.eQ = var1;
         if (!this.eS) {
            if (this.eU) {
               if (var1 != null) {
                  var1.a(this);
               }

               if (var2 != null) {
                  var2.b(this);
               }
            }

            if (var2 == null || var1 == null) {
               this.eT = true;
               if (this.eV != null && var1 != null) {
                  this.eV.i(this);
               }

               this.ai();
               this.invalidate();
            }
         }
      }

   }

   void setTitleImpl(String var1) {
      this.title = var1;
      if (this.eU) {
         this.eV.aG();
      }

      OptionsMenu var2;
      if (this.eV != null && ((Displayable)(var2 = this.eV.getOptionsMenu())).fg == this) {
         ((Displayable)var2).setTitleImpl(this.title);
      }

   }

   void layout() {
      this.ai();
      this.fh = true;
   }

   boolean af() {
      return false;
   }

   boolean B() {
      return false;
   }

   Command[] getExtraCommands() {
      return null;
   }

   boolean a(Command var1) {
      return false;
   }

   Item getCurrentItem() {
      return null;
   }

   void c(int var1, int var2) {
      if (!this.af()) {
         Command var12 = null;
         boolean var3 = false;
         boolean var4 = false;
         ItemCommandListener var5 = null;
         Item var6 = null;
         synchronized(Display.hG) {
            if (DeviceInfo.isSoftkey(var1)) {
               if (this.eS) {
                  var12 = OptionsMenu.iZ;
               } else {
                  switch(var1) {
                  case -7:
                     var12 = this.eX.getCommand(2);
                     break;
                  case -6:
                     var12 = this.eX.getCommand(0);
                     break;
                  case -5:
                     var12 = this.eX.getCommand(1);
                  }
               }
            }

            if (var12 == null) {
               return;
            }

            if (var12 == OptionsMenu.iZ) {
               OptionsMenu var11 = this.eV.getOptionsMenu();
               this.eV.c(this, (Displayable)var11);
            } else if (this.eW.j(var12)) {
               var3 = true;
            } else if ((var6 = this.getCurrentItem()) != null && var6.aG.j(var12)) {
               var5 = var6.getItemCommandListener();
               var4 = true;
            } else {
               this.a(var12);
            }
         }

         if (var3) {
            CommandListener var7;
            if ((var7 = this.fa) == null) {
               return;
            }

            synchronized(Display.hH) {
               var7.commandAction(var12, this);
            }
         } else if (var4 && var5 != null) {
            synchronized(Display.hH) {
               var5.commandAction(var12, var6);
               return;
            }
         }

      }
   }

   void i(int var1, int var2) {
   }

   void h(int var1, int var2) {
   }

   void a(Display var1) {
      VisibilityListener var2 = this.fb;
      synchronized(Display.hG) {
         if (!this.eU) {
            this.eV = var1;
            this.eU = true;
            if (this.eQ != null && !this.eS) {
               this.eQ.a(this);
            }
         }

         this.ff = true;
      }

      this.D();
      if (var2 != null) {
         var2.showNotify(var1, this);
      }

   }

   void e(Display var1) {
   }

   void b(Display var1) {
      VisibilityListener var2 = this.fb;
      synchronized(Display.hG) {
         if (this.eU) {
            if (this.eQ != null && !this.eS) {
               this.eQ.b(this);
            }

            this.eU = false;
         }
      }

      if (!this.B() && var2 != null) {
         synchronized(Display.hH) {
            var2.hideNotify(var1, this);
         }
      }
   }

   void d(Display var1) {
   }

   void b(Graphics var1) {
      if (!this.eS) {
         synchronized(Display.hG) {
            if (this.eQ != null) {
               this.eQ.paint(var1);
            }

            if (this.ff) {
               eI.hideIndex();
               this.eZ.L();
               this.ff = false;
            }

         }
      }
   }

   void D() {
      boolean var1 = false;
      synchronized(Display.hG) {
         this.ai();
         if (this.eT) {
            this.eT = false;
            var1 = true;
         }
      }

      if (var1) {
         synchronized(Display.hH) {
            this.sizeChanged(this.eR[2], this.eR[3]);
         }
      }
   }

   void a(Item var1) {
   }

   void r(int var1, int var2) {
   }

   final void d(int var1, int var2, int var3, int var4) {
      if (this.eU) {
         this.eV.a(this, var1, var2, var3, var4);
      }

   }

   final void ag() {
      if (this.eU) {
         this.eV.g(this);
      }

   }

   final void invalidate() {
      if (this.fh) {
         this.fh = false;
         if (this.eV != null) {
            this.eV.h(this);
         }
      }

   }

   final void b(Item var1) {
      if (!var1.aK) {
         var1.aK = true;
         Display var2;
         if ((var2 = this.eV == null ? Display.getActiveDisplay() : this.eV) != null) {
            var2.a(this, var1);
         }
      }

   }

   final CommandListener getCommandListener() {
      return this.fa;
   }

   final void ah() {
      OptionsMenu var1 = null;
      if (this.eV != null && ((Displayable)this.eV.getOptionsMenu()).fg == this) {
         var1 = this.eV.getOptionsMenu();
      }

      if (var1 != null) {
         var1.setKeepRootOptionsMenu(false);
         var1.nativeDismissOptionsList();
      }

      this.eV.c((Displayable)null, this);
   }

   final void c(boolean var1) {
      synchronized(Display.hG) {
         if (!this.af()) {
            Command var3 = null;
            CommandVector var4 = null;
            Item var5;
            if ((var5 = this.getCurrentItem()) != null) {
               var4 = var5.aG;
               var3 = var5.aw;
            }

            OptionsMenu var10 = null;
            Command var6 = null;
            int var7 = -1;
            if (var1 && this.eV != null && ((Displayable)this.eV.getOptionsMenu()).fg == this && (var7 = (var10 = this.eV.getOptionsMenu()).getHighlightedOptionIndex()) >= 0) {
               var6 = this.eY.getCommand(var7);
            }

            this.eY.a(this.eW, this.getExtraCommands(), var4, var3, this.m());
            if (!this.eS) {
               eJ.a(this, this.eY, this.eX);
            }

            this.eZ.a(this.eX);
            if (this.eU) {
               this.eZ.L();
            }

            int var9;
            if (var6 != null && (var9 = this.eY.i(var6)) != -1) {
               var7 = var9;
            }

            if (var10 != null) {
               var10.update(var7);
            }

         }
      }
   }

   final void f(Display var1) {
      this.d(var1);
      if (this.fg != null) {
         this.fg.f(var1);
         this.fg = null;
      }

   }

   final Displayable getParentDisplayable() {
      return this.fg;
   }

   final boolean c(Displayable var1) {
      if (var1 == this) {
         return true;
      } else {
         return this.fg == null ? false : this.fg.c(var1);
      }
   }

   final Displayable getBottomOfStackDisplayable() {
      return this.fg == null ? this : this.fg.getBottomOfStackDisplayable();
   }

   final boolean d(Displayable var1) {
      return var1 != this && this.fg != var1 && var1 != null;
   }

   final boolean a(Displayable var1, Displayable var2) {
      if (var2 == this) {
         return false;
      } else if (var1 == null) {
         return this.e(var2);
      } else if (var2.fc) {
         if (var2.fg == null) {
            if (var1 == this) {
               var2.fg = this;
               return true;
            } else {
               return this.b(var1, var2);
            }
         } else {
            return var2.fg == var1 ? this.e(var2) : false;
         }
      } else {
         return false;
      }
   }

   private final boolean e(Displayable var1) {
      if (this.fg == null) {
         return !var1.fc;
      } else if (this.fg == var1) {
         this.fg = null;
         return true;
      } else {
         return this.fg.e(var1);
      }
   }

   private final boolean b(Displayable var1, Displayable var2) {
      if (this.fg == null) {
         return false;
      } else if (this.fg == var1) {
         var2.fg = var1;
         this.fg = null;
         return true;
      } else {
         return this.fg.b(var1, var2);
      }
   }

   Zone getScrollbarZone() {
      return null;
   }

   Zone getMainZone() {
      return this.eQ != null ? eN : eL;
   }

   Zone getTickerZone() {
      return this.eQ != null ? eP : null;
   }

   int getTickerHeight() {
      Zone var1 = null;
      return (var1 = this.getTickerZone()) != null ? var1.height : 0;
   }

   final void ai() {
      Zone var1 = this.getMainZone();
      this.eR[0] = var1.x;
      this.eR[1] = var1.y;
      this.eR[2] = var1.width;
      this.eR[3] = var1.height;
   }

   final boolean aj() {
      return this.fc;
   }

   final void setSystemScreen(boolean var1) {
      this.fc = var1;
   }

   final boolean ak() {
      return this.fd;
   }

   final void setNativeDelegate(boolean var1) {
      this.fd = var1;
   }

   final boolean al() {
      boolean var1 = false;
      return this.fe;
   }

   final void setPopup(boolean var1) {
      this.fe = var1;
   }

   boolean m() {
      return true;
   }

   final void c(Graphics var1) {
      if (!this.fd) {
         if (this.al()) {
            var1.a(false, true, false, (Zone)null);
         } else if (!(this instanceof GameCanvas) && (!(this instanceof Canvas) || !this.eS && UIStyle.isCanvasHasBgImage())) {
            var1.a(true, false, false, (Zone)null);
         } else if (!this.eS && this.eQ != null) {
            var1.a(false, false, true, Canvas.aN);
         } else {
            var1.a(false, false, false, (Zone)null);
         }
      }
   }

   static {
      eL = eI.getZone(2);
      eM = eI.getZone(3);
      eN = eI.getZone(4);
      eO = eI.getZone(5);
      eP = eI.getZone(6);
   }
}
