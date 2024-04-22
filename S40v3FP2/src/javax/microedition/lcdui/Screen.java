package javax.microedition.lcdui;

public abstract class Screen extends Displayable {
   com.nokia.mid.impl.isa.ui.gdi.Font screenGDIFont;
   boolean resetToTop = true;

   Screen() {
      super((String)null);
   }

   Screen(String var1) {
      super(var1);
   }

   public Ticker getTicker() {
      return super.getTicker();
   }

   public void setTicker(Ticker var1) {
      super.setTicker(var1);
   }

   public String getTitle() {
      return super.getTitle();
   }

   public void setTitle(String var1) {
      super.setTitle(var1);
   }

   void repaintItem(Item var1, int var2, int var3, int var4, int var5) {
      this.repaintArea(var1.bounds[0] + this.viewport[0] + var2, var1.bounds[1] + this.viewport[1] + var3, var4, var5);
   }

   void repaintRequest() {
      this.repaintFull();
   }
}
