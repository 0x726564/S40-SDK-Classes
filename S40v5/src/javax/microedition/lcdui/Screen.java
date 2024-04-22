package javax.microedition.lcdui;

public abstract class Screen extends Displayable {
   boolean gY = true;

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
}
