package javax.microedition.lcdui;

public abstract class Screen extends Displayable {
   com.nokia.mid.impl.isa.ui.gdi.Font screenGDIFont;
   boolean resetToTop = true;

   Screen() {
      super((String)null);
   }

   Screen(String title) {
      super(title);
   }

   public Ticker getTicker() {
      return super.getTicker();
   }

   public void setTicker(Ticker ticker) {
      super.setTicker(ticker);
   }

   public String getTitle() {
      return super.getTitle();
   }

   public void setTitle(String s) {
      super.setTitle(s);
   }

   void repaintItem(Item item, int x, int y, int w, int h) {
      this.repaintArea(item.bounds[0] + this.viewport[0] + x, item.bounds[1] + this.viewport[1] + y, w, h);
   }

   void repaintRequest() {
      this.repaintFull();
   }
}
