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

   public Element(QName var1, Type var2, int var3, int var4, boolean var5) throws IllegalArgumentException {
      super(9);
      if (var1 != null && var2 != null && !(var2 instanceof Element)) {
         this.name = var1;
         this.contentType = var2;
         if (var3 < 0 || var4 <= 0 && var4 != -1) {
            throw new IllegalArgumentException("[min|max]Occurs must >= 0");
         } else if (var4 < var3 && var4 != -1) {
            throw new IllegalArgumentException("maxOccurs must > minOccurs");
         } else {
            this.minOccurs = var3;
            this.isOptional = var3 == 0;
            this.maxOccurs = var4;
            this.isArray = var4 > 1 || var4 == -1;
            this.isNillable = var5;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Element(QName var1, Type var2) throws IllegalArgumentException {
      super(9);
      if (var1 != null && var2 != null && !(var2 instanceof Element)) {
         this.name = var1;
         this.contentType = var2;
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
