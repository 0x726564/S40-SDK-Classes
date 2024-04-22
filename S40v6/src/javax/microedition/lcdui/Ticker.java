package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.ReinitialiseListener;
import com.nokia.mid.impl.isa.ui.style.UIStyle;

public class Ticker {
   private static final int INACTIVE = 0;
   private static final int ACTIVE = 1;
   private String tickerString;
   private int tickerState = 0;

   public Ticker(String str) {
      this.setString(str);
   }

   public String getString() {
      return this.tickerString;
   }

   public void setString(String str) {
      if (str == null) {
         throw new NullPointerException();
      } else {
         synchronized(Display.LCDUILock) {
            this.tickerString = str;
            if (this.tickerState == 1) {
               setText(this.tickerString.replace('\u0000', ' '));
            }

         }
      }
   }

   void showNotify() {
      setText(this.tickerString.replace('\u0000', ' '));
      this.tickerState = 1;
   }

   void hideNotify() {
      removeText();
      this.tickerState = 0;
   }

   void holdActive() {
      this.tickerState = 0;
   }

   private static native void setText(String var0);

   private static native void removeText();

   private static native void setDefaultProperties();

   static {
      setDefaultProperties();
      UIStyle.registerReinitialiseListener(new ReinitialiseListener() {
         public void reinitialiseForForeground() {
            Ticker.setDefaultProperties();
         }
      });
   }
}
