package javax.microedition.lcdui;

class ChoiceItem {
   public String text;
   public Image image;
   Font jFont = Font.getDefaultFont();
   boolean selected;
   boolean highlighted;
   int id;

   ChoiceItem(String stringPart, Image imagePart) {
      if (stringPart == null) {
         throw new NullPointerException();
      } else {
         this.id = this.hashCode();
         this.set(stringPart, imagePart);
      }
   }

   ChoiceItem() {
      this.id = this.hashCode();
   }

   void set(String stringPart, Image imagePart) throws NullPointerException {
      if (stringPart == null) {
         throw new NullPointerException();
      } else {
         this.text = stringPart;
         this.image = imagePart;
      }
   }
}
