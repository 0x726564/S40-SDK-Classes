package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DeviceControl;
import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.DisplayAccess;
import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.EventProducer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.JavaEventGenerator;
import com.nokia.mid.impl.isa.ui.MIDletAccess;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import com.nokia.mid.ui.lcdui.DisplayStateListener;
import com.nokia.mid.ui.lcdui.VisibilityListener;
import java.util.Timer;
import java.util.Vector;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;

public class Display {
   public static final int LIST_ELEMENT = 1;
   public static final int CHOICE_GROUP_ELEMENT = 2;
   public static final int ALERT = 3;
   public static final int COLOR_BACKGROUND = 0;
   public static final int COLOR_FOREGROUND = 1;
   public static final int COLOR_HIGHLIGHTED_BACKGROUND = 2;
   public static final int COLOR_HIGHLIGHTED_FOREGROUND = 3;
   public static final int COLOR_BORDER = 4;
   public static final int COLOR_HIGHLIGHTED_BORDER = 5;
   static final Object hG = new Object();
   static final Object hH = new Object();
   static final Object hI = new Object();
   private static final int hJ = DeviceInfo.getDisplayWidth(3);
   private static final int hK = DeviceInfo.getDisplayHeight(3);
   private static final int hL = DeviceInfo.getDisplayWidth(2);
   private static final int hM = DeviceInfo.getDisplayHeight(2);
   private static final int hN = DeviceInfo.getDisplayWidth(0);
   private static final int hO = DeviceInfo.getDisplayHeight(0);
   private static final Form hP = new Form("");
   private static final Command hQ = new Command(10, 13);
   private static final Form hR = new Form("");
   private static int av;
   private static Vector hS = new Vector();
   private static String hT;
   private static final EventProducer hU = InitJALM.s_getEventProducer();
   static Display hV;
   private static MIDletAccess hW;
   static boolean hX;
   DisplayStateListener hY;
   private static boolean hZ;
   private static boolean ia = true;
   private boolean ib;
   private boolean ic;
   private final OptionsMenu ie;
   private final TruncatedItemScreen if;
   private final Display.DisplayAccessor ig;
   private int ih;
   private int ii;
   private Displayable ij;
   private Displayable ik;
   private Displayable il;
   private Displayable im;
   private Displayable in;
   private Item io;
   private int ip;
   private int iq;
   private int ir;
   private int is;
   private DirectGraphicsImpl it;
   private MIDlet iu;
   private String iv;
   private boolean iw;
   private boolean ix;
   private boolean iy;

   private Display(MIDlet var1) {
      this.ib = false;
      this.ic = false;
      this.it = null;
      this.iw = false;
      this.ix = false;
      this.iy = false;
      synchronized(hG) {
         this.ig = new Display.DisplayAccessor(this);
         if (hW == null) {
            hW = InitJALM.s_getMIDletAccessor();
         }

         hU.attachEventConsumer(2, this.ig);
         hU.attachEventConsumer(1, this.ig);
         hU.attachEventConsumer(4, this.ig);
         hP.setCommandListener(this.ig);
         this.ie = NativeOptionsMenu.g(this);
         List.N();
         hP.eV = null;
         this.if = TruncatedItemScreen.getTruncatedItemScreen();
      }

      this.resetDisplay(var1);
   }

   public static Display getDisplay(MIDlet var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         synchronized(hG) {
            if (hV == null) {
               InitJALM.s_setDisplayAccessor((hV = new Display(var0)).ig);
            }

            if (hV.iu != var0) {
               throw new RuntimeException("Error: Attempt to getDisplay by application after destroyApp called.");
            }
         }

         return hV;
      }
   }

   public void callSerially(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         synchronized(hG) {
            if (this == hV) {
               hS.addElement(var1);
               JavaEventGenerator.s_generateEvent(0, 2, 3, 0);
            }

         }
      }
   }

   public boolean flashBacklight(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Display.flashBacklight: negative duration not allowed.");
      } else {
         synchronized(hG) {
            if (!hZ || hZ && this.ij != null && this.ij.ak()) {
               return false;
            }
         }

         DeviceControl.flashLights((long)var1);
         return true;
      }
   }

   public int getBestImageHeight(int var1) {
      Zone var2;
      return (var2 = this.getImageZone(var1)).height - var2.getMarginTop() - var2.getMarginBottom();
   }

   public int getBestImageWidth(int var1) {
      Zone var2;
      return (var2 = this.getImageZone(var1)).width - var2.getMarginLeft() - var2.getMarginRight();
   }

   public int getBorderStyle(boolean var1) {
      return 0;
   }

   public int getColor(int var1) {
      switch(var1) {
      case 0:
         return UIStyle.COLOUR_BACKGROUND;
      case 1:
         return UIStyle.COLOUR_TEXT;
      case 2:
         return UIStyle.COLOUR_HIGHLIGHT;
      case 3:
         return UIStyle.COLOUR_HIGHLIGHT_TEXT;
      case 4:
         return UIStyle.COLOUR_BACKGROUND;
      case 5:
         return UIStyle.COLOUR_HIGHLIGHT;
      default:
         throw new IllegalArgumentException();
      }
   }

   public Displayable getCurrent() {
      synchronized(hG) {
         return this.getCurrentImpl();
      }
   }

   public boolean isColor() {
      synchronized(hG) {
         return DeviceInfo.isColor();
      }
   }

   public int numAlphaLevels() {
      synchronized(hG) {
         return DeviceInfo.numAlphaLevels();
      }
   }

   public int numColors() {
      synchronized(hG) {
         return DeviceInfo.numColors();
      }
   }

   public void setCurrent(Displayable var1) {
      if (var1 != null) {
         this.a(var1, (String)null);
      }

   }

   final void a(Displayable var1, String var2) {
      synchronized(hG) {
         if (this == hV) {
            if (var1 != null) {
               if (!(var1 instanceof Alert)) {
                  if (this.iy && this.nativeIsNoteDisplayed()) {
                     this.nativeHideNote();
                  }
               } else {
                  Displayable var4 = this.ij;
                  if (this.ih != 2 && this.ih != 3) {
                     if (this.ij.aj()) {
                        var4 = this.ik;
                     }
                  } else if (this.in.aj()) {
                     if (this.ij.aj()) {
                        var4 = this.ik;
                     }
                  } else {
                     var4 = this.in;
                  }

                  if (var4 == var1) {
                     if (var4 instanceof Alert && var4 == this.ij && this.ih != 2 && this.ih != 3) {
                        ((Alert)var4).O();
                     }

                     return;
                  }

                  if (var4 instanceof Alert) {
                     throw new IllegalArgumentException("Current Displayable is an Alert");
                  }

                  ((Alert)var1).setReturnScreen(var4);
                  if (this.iy && hX && !this.nativeIsNoteDisplayed()) {
                     this.a((Alert)var1);
                  }
               }
            }

            this.a((Displayable)null, var1, (Item)null, var2, true, false);
         }

      }
   }

   public void setCurrent(Alert var1, Displayable var2) {
      this.a((Alert)var1, (Displayable)var2, (String)null);
   }

   final void a(Alert var1, Displayable var2, String var3) {
      if (var1 != null && var2 != null) {
         if (var2 instanceof Alert) {
            throw new IllegalArgumentException();
         } else {
            synchronized(hG) {
               if (this == hV) {
                  var1.setReturnScreen(var2);
                  if (var1 == this.ij && this.ih != 2 && this.ih != 3) {
                     var1.O();
                  }

                  if (this.iy && !this.nativeIsNoteDisplayed() && hX) {
                     this.a(var1);
                  }

                  this.a((Displayable)null, var1, (Item)null, var3, true, false);
               }

            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void setCurrentItem(Item var1) {
      Display var10000 = this;
      Display var5 = null;
      var1 = var1;
      var5 = var10000;
      synchronized(hG) {
         Screen var3;
         if ((var3 = var1.au) != null && !(var3 instanceof Alert)) {
            if (var5 == hV) {
               var5.a((Displayable)null, var3, var3 instanceof Form ? var1 : null, (String)null, true, false);
            }

         } else {
            throw new IllegalStateException();
         }
      }
   }

   public boolean vibrate(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Display.vibrate: negative duration not allowed.");
      } else {
         synchronized(hG) {
            if (!hZ && (this.ij == null || !this.ij.ak())) {
               return false;
            }
         }

         if (var1 == 0) {
            DeviceControl.stopVibra();
            return true;
         } else {
            try {
               DeviceControl.startVibra(100, (long)var1);
            } catch (IllegalStateException var3) {
            }

            return true;
         }
      }
   }

   final void f(Displayable var1) {
      if (hX && this == hV) {
         Displayable var2 = this.ij;
         if (this.ih == 2 || this.ih == 3) {
            var2 = this.in;
         }

         if (var1 == var2) {
            j(var1);
            this.it = new DirectGraphicsImpl(Pixmap.getUpdatedDisplayPixmap().getGraphics());
         }
      }

   }

   static Display getActiveDisplay() {
      return hV;
   }

   Displayable getCurrentImpl() {
      Displayable var1 = null;
      if (this.ij != hP && this.ij != hR && this.ij != null) {
         if (this.ij.aj()) {
            var1 = this.ik;
         } else {
            var1 = this.ij;
         }
      }

      return var1;
   }

   OptionsMenu getOptionsMenu() {
      return this.ie;
   }

   String getMIDletName() {
      return this.iv;
   }

   TruncatedItemScreen getTruncatedItemScreen() {
      return this.if;
   }

   Displayable getCurrentTopOfStackDisplayable() {
      return this.ij;
   }

   final boolean aE() {
      return this.ix;
   }

   final void a(Displayable var1, int var2, int var3, int var4, int var5) {
      synchronized(hG) {
         if (this == hV && var1.isShown() && this.ii != 2) {
            this.ip = Math.min(var2, this.ip);
            this.iq = Math.min(var3, this.iq);
            this.ir = Math.max(var2 + var4, this.ir);
            this.is = Math.max(var3 + var5, this.is);
            if (this.ii == 0) {
               JavaEventGenerator.s_generateEvent(0, 2, 2, av);
               this.ii = 1;
            }
         }

      }
   }

   final void g(Displayable var1) {
      synchronized(hG) {
         if (this == hV && var1.isShown()) {
            if (this.ii == 0) {
               JavaEventGenerator.s_generateEvent(0, 2, 2, av);
            }

            this.ii = 2;
         }

      }
   }

   final void h(Displayable var1) {
      synchronized(hG) {
         if (this == hV) {
            hS.addElement(var1);
            JavaEventGenerator.s_generateEvent(0, 2, 6, 0);
         }

      }
   }

   final void a(Displayable var1, Item var2) {
      synchronized(hG) {
         if (this == hV) {
            hS.addElement(var1);
            hS.addElement(var2);
            JavaEventGenerator.s_generateEvent(0, 2, 7, 0);
         }

      }
   }

   final void a(Command var1, Object var2, Object var3) {
      if (var1 != null && var2 != null && var3 != null) {
         synchronized(hG) {
            if (this == hV) {
               hS.addElement(var1);
               hS.addElement(var2);
               hS.addElement(var3);
               JavaEventGenerator.s_generateEvent(0, 2, 8, 0);
            }

         }
      }
   }

   final void i(Displayable var1) {
      if (this.ih != 1 && this == hV && hX && var1 == this.ij && this.it != null) {
         this.it.reset();
         var1.c((Graphics)this.it);
      }

   }

   final void c(Displayable var1, Displayable var2) {
      if (var2 != null && (var2.aj() || var1 == null)) {
         if (this == hV) {
            if (var2 instanceof Alert) {
               if (this.iy && !this.nativeIsNoteDisplayed() && hX) {
                  this.a((Alert)var2);
               }
            } else if (this.iy && this.nativeIsNoteDisplayed()) {
               this.nativeHideNote();
            }

            this.a(var1, var2, (Item)null, (String)null, false, false);
         }

      }
   }

   final void a(Timer var1) {
      hW.registerTimer(this.iu, var1);
   }

   final void b(Timer var1) {
      hW.deregisterTimer(this.iu, var1);
   }

   final void aF() {
      synchronized(hH) {
         boolean var2 = false;
         int var3 = 0;
         int var4 = 0;
         int var5 = 0;
         int var6 = 0;
         Displayable var7 = null;
         synchronized(hG) {
            if (this == hV) {
               if (this.ii == 0) {
                  return;
               }

               if (hZ && this.ij.isShown() && this.it != null && !this.ij.ak()) {
                  var2 = true;
                  var7 = this.ij;
                  this.it.reset();
                  if (this.ii == 2) {
                     var3 = 0;
                     var4 = 0;
                     if (var7 instanceof Canvas) {
                        if (var7.eS) {
                           var5 = hJ;
                           var6 = hK;
                        } else {
                           var5 = hL;
                           var6 = hM;
                        }
                     } else {
                        var5 = hN;
                        var6 = hO;
                     }
                  } else {
                     var3 = this.ip;
                     var5 = this.ir - this.ip;
                     var4 = this.iq;
                     var6 = this.is - this.iq;
                  }

                  if (var7 instanceof Canvas) {
                     this.it.setTextTransparency(true);
                  } else {
                     this.it.setTextTransparency(false);
                  }

                  this.it.setClip(var3, var4, var5, var6);
                  if (this.k(var7)) {
                     var7.c((Graphics)this.it);
                  }
               }
            }

            this.ii = 0;
            this.ip = 9999;
            this.iq = 9999;
            this.ir = 0;
            this.is = 0;
         }

         if (var2) {
            var7.b((Graphics)this.it);
            this.it.refresh(var3, var4, var5, var6, 0, 0);
         }

      }
   }

   final void aG() {
      this.e(false);
   }

   private void e(boolean var1) {
      if (this == hV && hX) {
         if (hZ || var1) {
            String var2;
            if ((var2 = this.ij.getTitle()) == null || var2.length() == 0) {
               var2 = this.getMIDletName();
            }

            if (var2.length() > 512) {
               var2 = var2.substring(0, 511);
            }

            this.setStatusZoneStr(var2);
            this.ic = false;
            return;
         }

         this.ic = true;
      }

   }

   final void aH() {
      if (this.ij != hP && this.ij != null) {
         this.ij.f(this);
      }

   }

   final void u(int var1, int var2) {
      boolean var3 = false;
      Displayable var4;
      if ((var4 = this.ij) != null && var4.isShown()) {
         var3 = true;
      }

      if (var3) {
         short var6 = (short)var2;
         short var5 = (short)(var2 >> 16);
         switch(var1) {
         case 1:
            var4.c(var6, var5);
            return;
         case 2:
            var4.i(var6, var5);
            break;
         case 3:
            var4.h(var6, var5);
            return;
         }
      }

   }

   final void v(int var1, int var2) {
      label327: {
         Displayable var34;
         boolean var3;
         boolean var4;
         boolean var26;
         DisplayStateListener var27;
         MIDlet var29;
         Object var30;
         switch(var1) {
         case 1:
            if (this.ih == 1) {
               this.aJ();
               return;
            }
            break;
         case 2:
            if (var2 == av) {
               this.aF();
               return;
            }
            break label327;
         case 3:
            var27 = null;
            Runnable var31;
            synchronized(hG) {
               if (hS.size() <= 0) {
                  throw new RuntimeException("Display ERROR: No corresponding Runnable for callSerially.");
               }

               var31 = (Runnable)hS.firstElement();
               hS.removeElementAt(0);
            }

            if (var31 != null) {
               synchronized(hH) {
                  var31.run();
                  return;
               }
            }
            break;
         case 4:
            var34 = null;
            var4 = false;
            synchronized(hG) {
               if (hZ) {
                  hZ = false;
                  if (!this.ij.ak()) {
                     var34 = this.ij;
                     var4 = true;
                  }
               }
            }

            if (var4) {
               var34.e(this);
               var34.b(this);
            }

            return;
         case 5:
            var34 = null;
            var4 = false;
            var26 = false;
            synchronized(hG) {
               if (!hZ) {
                  hZ = true;
                  var34 = this.ij;
                  if (!this.ij.isShown()) {
                     var26 = true;
                  }

                  this.ii = 2;
                  if (this.ib) {
                     this.ib = false;
                     j(this.ij);
                     this.it = new DirectGraphicsImpl(Pixmap.getUpdatedDisplayPixmap().getGraphics());
                     SoftLabel.K();
                     this.e(false);
                  } else if (this.ic) {
                     this.e(false);
                  }

                  if (!this.k(var34)) {
                     var34.c((Graphics)this.it);
                  }

                  var4 = true;
               }
            }

            if (var4) {
               if (var26) {
                  var34.a(this);
               }

               this.aF();
            }

            return;
         case 6:
            var29 = null;
            synchronized(hG) {
               var34 = (Displayable)hS.firstElement();
               hS.removeElementAt(0);
            }

            if (var34 != null) {
               var34.D();
               return;
            }
            break;
         case 7:
            var29 = null;
            var30 = null;
            Item var33;
            synchronized(hG) {
               var34 = (Displayable)hS.firstElement();
               hS.removeElementAt(0);
               var33 = (Item)hS.firstElement();
               hS.removeElementAt(0);
               var33.aK = false;
            }

            if (var34 != null) {
               var34.a(var33);
               return;
            }
            break;
         case 8:
            var29 = null;
            var30 = null;
            var27 = null;
            Command var32;
            Object var28;
            synchronized(hG) {
               var32 = (Command)hS.firstElement();
               hS.removeElementAt(0);
               var30 = hS.firstElement();
               hS.removeElementAt(0);
               var28 = hS.firstElement();
               hS.removeElementAt(0);
            }

            if (var30 instanceof Displayable) {
               synchronized(hH) {
                  ((CommandListener)var28).commandAction(var32, (Displayable)var30);
                  return;
               }
            }

            if (var30 instanceof Item) {
               synchronized(hH) {
                  ((ItemCommandListener)var28).commandAction(var32, (Item)var30);
                  return;
               }
            }
            break;
         case 9:
            var29 = null;
            synchronized(hG) {
               var29 = (MIDlet)hS.firstElement();
               hS.removeElementAt(0);
               if (var29 != this.iu) {
                  var29 = null;
               }
            }

            if (var29 != null) {
               var29.notifyDestroyed();
               return;
            }
            break;
         case 10:
            synchronized(hG) {
               this.iy = true;
               return;
            }
         case 11:
            synchronized(hG) {
               this.iy = false;
               if (this.nativeIsNoteDisplayed()) {
                  this.nativeHideNote();
               }

               return;
            }
         case 12:
            break label327;
         case 13:
            var3 = false;
            var4 = false;
            var27 = this.hY;
            synchronized(hG) {
               if (!hX) {
                  var3 = true;
                  hX = true;
                  this.ib = true;
                  UIStyle.reinitialiseAllForForeground();
                  if (this.ih != 0) {
                     var4 = true;
                  }
               }
            }

            if (var3) {
               if (var27 != null) {
                  synchronized(hH) {
                     var27.displayActive(this);
                  }
               }

               if (var4) {
                  this.aJ();
               }
            }

            return;
         case 14:
            var3 = false;
            var4 = false;
            var26 = false;
            DisplayStateListener var5 = this.hY;
            synchronized(hG) {
               if (hX) {
                  if (this.iy && this.nativeIsNoteDisplayed()) {
                     this.nativeHideNote();
                  }

                  if (this.ij != null && this.ij.ak()) {
                     var4 = true;
                  }
               }
            }

            if (var4) {
               this.ij.e(this);
               this.ij.b(this);
            }

            synchronized(hG) {
               if (hX) {
                  var3 = true;
                  hX = false;
                  this.ib = false;
                  if (this.ih == 1) {
                     if (this.il.aj()) {
                        if (!this.ij.c(this.il)) {
                           this.il.d(this);
                        }

                        this.il = null;
                        this.ih = 0;
                     } else {
                        var26 = true;
                     }
                  }

                  if (this.ij.aj()) {
                     this.il = this.ij.getBottomOfStackDisplayable();
                     this.ih = 1;
                     var26 = true;
                  }
               }
            }

            if (var3) {
               if (var5 != null) {
                  synchronized(hH) {
                     var5.displayInactive(this);
                  }
               }

               this.nativeMIDletAcknowledgedDisplayInactive();
               if (var26) {
                  this.aJ();
               }
            }

            return;
         default:
            throw new RuntimeException("Display ERROR: Unknown event type " + var1);
         }

         return;
      }

      synchronized(hG) {
         this.ii = 2;
      }

      this.aF();
   }

   final void w(int var1, int var2) {
      if (var1 == 2) {
         switch(var2) {
         case 1:
            if (this.il != null && this.il != hP) {
               this.il.d(this);
               return;
            }
         case 2:
         case 4:
         case 5:
         default:
            break;
         case 3:
         case 6:
         case 9:
            hS.removeElementAt(0);
            return;
         case 7:
            hS.removeElementAt(0);
            hS.removeElementAt(0);
            return;
         case 8:
            hS.removeElementAt(0);
            hS.removeElementAt(0);
            hS.removeElementAt(0);
         }
      }

   }

   final void flushImageToScreen(Displayable var1, Image var2, int var3, int var4, int var5, int var6) {
      if (this == hV && hZ && var1.isShown() && this.it != null && !this.ix && var2.getPixmap().isGameCanvasPixmap()) {
         int var7 = 0;
         Zone var8;
         if ((var8 = var1.getTickerZone()) != null) {
            var7 = var8.y + var8.height + var8.getMarginTop() + var8.getMarginBottom();
         }

         this.it.reset();
         this.it.translate(0, var7);
         this.it.setClip(0, 0, var1.getWidth(), var1.getHeight());
         this.it.clipRect(var3, var4, var5, var6);
         var2.getGraphics().refresh(this.it.getClipX(), this.it.getClipY(), this.it.getClipWidth(), this.it.getClipHeight(), 0, var7);
      }
   }

   final void aI() {
      Displayable var1 = null;
      synchronized(hG) {
         if (this.ij != null && this.ij.isShown() && this.ij != hP) {
            var1 = this.ij;
         }
      }

      if (var1 != null) {
         var1.e(this);
         var1.b(this);
      }

   }

   final void resetDisplay(MIDlet var1) {
      this.aI();
      synchronized(hG) {
         this.ih = 0;
         this.iw = false;
         this.aH();
         this.iu = var1;
         this.iv = hW.getMIDletName(this.iu);
         this.ij = hP;
         hP.deleteAll();
         hP.append((Item)(new StringItem((String)null, TextDatabase.getText(0, this.iv))));
         if (hP.isShown()) {
            hP.eV = this;
            this.it = new DirectGraphicsImpl(Pixmap.getUpdatedDisplayPixmap().getGraphics());
         }

         this.ik = null;
         this.il = null;
         this.im = null;
         this.io = null;
         this.in = null;
         this.ii = 0;
         this.ip = 9999;
         this.iq = 9999;
         this.ir = 0;
         this.is = 0;
      }
   }

   private static void j(Displayable var0) {
      if (var0 instanceof Canvas) {
         if (var0.eS) {
            SoftLabel.K();
            av = 3;
         } else {
            av = 2;
         }
      } else if (var0 instanceof Alert) {
         av = 1;
      } else {
         av = 0;
      }

      notifyLayoutChange(av, var0.ak(), var0.al());

      while(!checkLayoutChangeComplete(0)) {
         try {
            Thread.sleep(5L);
         } catch (InterruptedException var1) {
         }
      }

   }

   private final void aJ() {
      boolean var1 = false;
      Displayable var2 = null;
      synchronized(hG) {
         label149: {
            this.iw = false;
            Item var4 = this.io;
            this.io = null;
            if (this.ih == 1) {
               if (this.il == null) {
                  this.ih = 0;
                  this.im = null;
                  return;
               }

               if (this.ij.a(this.im, this.il) || this.ij == hP && this.il == hP && !hP.isShown()) {
                  this.ih = 2;
                  this.ix = true;
                  this.in = this.il;
                  this.il = null;
                  this.im = null;
                  if (var4 != null && var4.au == this.in && this.in instanceof Form) {
                     ((Form)this.in).setCurrentItem(var4);
                  }

                  var1 = this.ij.isShown();
                  var2 = this.ij;
                  break label149;
               }

               if (var4 != null && var4.au == this.ij && this.ij instanceof Form) {
                  ((Form)this.ij).setCurrentItem(var4);
               }

               this.ih = 0;
               if (!this.ij.c(this.il)) {
                  this.il.d(this);
               }

               this.il = null;
               this.im = null;
               return;
            }

            this.ih = 0;
            if (this.il != null) {
               if (!this.ij.c(this.il)) {
                  this.il.d(this);
               }

               this.il = null;
            }

            this.im = null;
            return;
         }
      }

      if (var1) {
         var2.e(this);
      }

      synchronized(hG) {
         this.ij = this.in;
         this.in = null;
         if (this.ij.aj()) {
            if (!var2.aj()) {
               this.ik = var2;
            }
         } else {
            this.ik = null;
         }

         if (this.ih == 3) {
            this.ih = 1;
         } else {
            this.ih = 0;
         }

         if (hZ) {
            this.ii = 2;
         }

         if (hX) {
            j(this.ij);
            this.it = new DirectGraphicsImpl(Pixmap.getUpdatedDisplayPixmap().getGraphics());
         }
      }

      if (hZ) {
         this.ij.a(this);
      }

      if (hZ && !this.ij.ak()) {
         this.aF();
      }

      if (var1) {
         var2.b(this);
      }

      synchronized(hG) {
         if (hX) {
            this.e(ia);
            ia = false;
         }

         if (this.ij.d(var2)) {
            var2.f(this);
         }

         this.ix = false;
      }
   }

   private void a(Alert var1) {
      this.nativeDisplayNote();
      var1.c(this);
   }

   private boolean k(Displayable var1) {
      boolean var2 = false;
      if (!(var1 instanceof GameCanvas) && !(var1 instanceof Canvas)) {
         var2 = true;
      } else if (this.ix && this.ii == 2 || !(var1 instanceof GameCanvas) && UIStyle.isCanvasHasBgImage() && !var1.eS) {
         var2 = true;
      }

      return var2;
   }

   private void a(Displayable var1, Displayable var2, Item var3, String var4, boolean var5, boolean var6) {
      boolean var7 = false;
      boolean var9 = false;
      Displayable var8;
      if (this.ih != 2 && this.ih != 3) {
         var8 = this.ij;
      } else {
         var8 = this.in;
      }

      label80: {
         if (var2 == null && !var6) {
            if (var2 == null) {
               if (var5) {
                  var7 = this.requestDisplayStateChange(0, false, (String)null);
                  if (hX || var8 == hR) {
                     var9 = true;
                  }

                  var2 = hR;
               }
               break label80;
            }

            if (!hX) {
               if (var5) {
                  var7 = this.requestDisplayStateChange(0, true, var4);
                  var9 = true;
               }
               break label80;
            }
         }

         var7 = true;
      }

      if (var7) {
         switch(this.ih) {
         case 0:
            if (var2 != this.ij || var3 != null || this.ij == hP && !hP.isShown()) {
               this.ih = 1;
               this.a(var1, (Displayable)var2, var3, var5, var9);
               return;
            }

            return;
         case 1:
         case 3:
            if (var5 || !this.iw) {
               this.iw = var5;
               if (this.il != var2) {
                  if (this.il != null && !var8.c(this.il)) {
                     this.il.d(this);
                  }

                  this.il = (Displayable)var2;
               }

               this.im = var1;
               this.io = var3;
            }
            break;
         case 2:
            if (var2 == var8 && var3 == null) {
               return;
            }

            this.ih = 3;
            this.a(var1, (Displayable)var2, var3, var5, var9);
            return;
         }
      }

   }

   private void a(Displayable var1, Displayable var2, Item var3, boolean var4, boolean var5) {
      this.il = var2;
      this.im = var1;
      this.io = var3;
      this.iw = var4;
      if (!var5) {
         JavaEventGenerator.s_generateEvent(0, 2, 1, 0);
      }

   }

   private void setStatusZoneStr(String var1) {
      if (!var1.equals(hT)) {
         hT = var1;
         this.notifyStatusZoneUpdate(var1);
      }

   }

   private Zone getImageZone(int var1) {
      Zone var2;
      switch(var1) {
      case 1:
         var2 = Displayable.eI.getZone(16);
         break;
      case 2:
         var2 = Displayable.eI.getZone(11);
         break;
      case 3:
         var2 = Displayable.eI.getZone(32);
         break;
      default:
         throw new IllegalArgumentException();
      }

      return var2;
   }

   private native void nativeDisplayNote();

   private native void nativeHideNote();

   private native boolean nativeIsNoteDisplayed();

   private static native boolean checkLayoutChangeComplete(int var0);

   private static native void notifyLayoutChange(int var0, boolean var1, boolean var2);

   private native void notifyStatusZoneUpdate(String var1);

   private static native void nativeStaticInitializer();

   private native boolean requestDisplayStateChange(int var1, boolean var2, String var3);

   private native void nativeMIDletAcknowledgedDisplayInactive();

   static MIDlet i(Display var0) {
      return var0.iu;
   }

   static Vector aK() {
      return hS;
   }

   static Displayable j(Display var0) {
      return var0.ij;
   }

   Display(MIDlet var1, Object var2) {
      this(var1);
   }

   static Display.DisplayAccessor k(Display var0) {
      return var0.ig;
   }

   static int l(Display var0) {
      return var0.ih;
   }

   static Form aL() {
      return hP;
   }

   static void a(Display var0, Displayable var1, Displayable var2, Item var3, String var4, boolean var5, boolean var6) {
      var0.a((Displayable)null, var2, (Item)null, (String)null, false, true);
   }

   static {
      nativeStaticInitializer();
      hP.addCommand(hQ);
      hR.append((Item)(new StringItem((String)null, "")));
   }

   private class DisplayAccessor implements DisplayAccess, EventConsumer, CommandListener {
      private final Display gt;

      public void commandAction(Command var1, Displayable var2) {
         synchronized(Display.hG) {
            if (this.gt == Display.hV) {
               Display.aK().addElement(Display.i(this.gt));
               JavaEventGenerator.s_generateEvent(0, 2, 9, 0);
            }

         }
      }

      public void consumeEvent(int var1, int var2, int var3) {
         synchronized(Display.hI) {
            boolean var5 = true;
            if (this.gt != Display.hV) {
               var5 = false;
               synchronized(Display.hG) {
                  this.gt.w(var1, var2);
               }
            }

            if (var5) {
               switch(var1) {
               case 1:
                  this.gt.u(var2, var3);
                  break;
               case 2:
                  this.gt.v(var2, var3);
               case 3:
               default:
                  break;
               case 4:
                  Displayable var6;
                  if ((var6 = Display.j(this.gt)) != null) {
                     var6.r(var2, var3);
                  }
               }
            }

         }
      }

      public void flushImageToScreen(Displayable var1, Image var2, int var3, int var4, int var5, int var6) {
         synchronized(Display.hG) {
            this.gt.flushImageToScreen(var1, var2, var3, var4, var5, var6);
         }
      }

      public DisplayAccess replaceDisplay(MIDlet var1) {
         synchronized(Display.hI) {
            Display var3 = Display.hV;
            if (this.gt != var3 && var3 != null) {
               return this;
            } else {
               if (var3 != null) {
                  var3.aI();
               }

               Display.DisplayAccessor var10000;
               synchronized(Display.hG) {
                  if (var3 != null) {
                     var3.aH();
                  }

                  var10000 = Display.k(Display.hV = new Display(var1));
               }

               return var10000;
            }
         }
      }

      public void resetDisplay(MIDlet var1) {
         synchronized(Display.hI) {
            if (this.gt == Display.hV) {
               this.gt.resetDisplay(var1);
            }

         }
      }

      public void setForeground(MIDlet var1) {
         synchronized(Display.hI) {
            if (this.gt == Display.hV) {
               if (Display.i(this.gt) != var1) {
                  this.gt.resetDisplay(var1);
               }

               synchronized(Display.hG) {
                  if (Display.l(this.gt) == 0 && Display.j(this.gt) == Display.aL()) {
                     Display.a(this.gt, (Displayable)null, Display.j(this.gt), (Item)null, (String)null, false, true);
                  }
               }
            }

         }
      }

      public Image createImage(Pixmap var1) {
         return new Image(var1);
      }

      public Pixmap getImagePixmap(Image var1) {
         return var1.getPixmap();
      }

      public Item createMMItem() {
         Item var2 = null;

         try {
            var2 = (Item)Class.forName("javax.microedition.lcdui.MMItemImpl").newInstance();
         } catch (Exception var1) {
         }

         return var2;
      }

      public void showCanvasVideo(Canvas var1, int var2, boolean var3, int var4, int var5, int var6, int var7) {
         var1.a(var3, var2, var4, var5, var6, var7);
      }

      public boolean isDisplayActive(Display var1) {
         if (var1 != Display.hV) {
            throw new IllegalStateException();
         } else {
            return Display.hX;
         }
      }

      public void setCurrent(Display var1, Displayable var2, String var3) {
         if (var1 != Display.hV) {
            throw new IllegalStateException();
         } else {
            this.gt.a(var2, var3);
         }
      }

      public void setCurrent(Display var1, Alert var2, Displayable var3, String var4) {
         if (var1 != Display.hV) {
            throw new IllegalStateException();
         } else {
            this.gt.a(var2, var3, var4);
         }
      }

      public void setDisplayStateListener(Display var1, DisplayStateListener var2) {
         synchronized(Display.hG) {
            if (var1 != Display.hV) {
               throw new IllegalStateException();
            } else {
               this.gt.hY = var2;
            }
         }
      }

      public void setVisibilityListener(Displayable var1, VisibilityListener var2) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            synchronized(Display.hG) {
               var1.setVisibilityListener(var2);
            }
         }
      }

      DisplayAccessor(Display var1, Object var2) {
         this.gt = var1;
      }
   }
}
