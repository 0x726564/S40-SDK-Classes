package javax.microedition.xml.rpc;

import javax.xml.namespace.QName;

public class Element extends Type {
   public final QName name;
   public final Type contentType;
   public final boolean isNillable;
   public final boolean isArray;
   public final boolean isOptional;
   public final int minOccurs;
   public final int maxOccurs;
   public static final int UNBOUNDED = -1;

   public Element(QName name, Type type, int minOccurs, int maxOccurs, boolean nillable) throws IllegalArgumentException {
      super(9);
      if (name != null && type != null && !(type instanceof Element)) {
         this.name = name;
         this.contentType = type;
         if (minOccurs < 0 || maxOccurs <= 0 && maxOccurs != -1) {
            throw new IllegalArgumentException("[min|max]Occurs must >= 0");
         } else if (maxOccurs < minOccurs && maxOccurs != -1) {
            throw new IllegalArgumentException("maxOccurs must > minOccurs");
         } else {
            this.minOccurs = minOccurs;
            this.isOptional = minOccurs == 0;
            this.maxOccurs = maxOccurs;
            this.isArray = maxOccurs > 1 || maxOccurs == -1;
            this.isNillable = nillable;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Element(QName name, Type type) throws IllegalArgumentException {
      super(9);
      if (name != null && type != null && !(type instanceof Element)) {
         this.name = name;
         this.contentType = type;
         this.minOccurs = 1;
         this.maxOccurs = 1;
         this.isArray = false;
         this.isOptional = false;
         this.isNillable = false;
      } else {
         throw new IllegalArgumentException();
      }
   }
}
