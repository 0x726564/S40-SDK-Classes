package javax.xml.namespace;

public class QName {
   private final String b;
   private final String c;
   private final String d;

   public QName(String var1, String var2) {
      this(var1, var2, "");
   }

   public QName(String var1, String var2, String var3) {
      if (var1 == null) {
         this.b = "";
      } else {
         this.b = var1;
      }

      if (var2 == null) {
         throw new IllegalArgumentException("local part cannot be \"null\" when creating a QName");
      } else {
         this.c = var2;
         if (var3 == null) {
            throw new IllegalArgumentException("prefix cannot be \"null\" when creating a QName");
         } else {
            this.d = var3;
         }
      }
   }

   public QName(String var1) {
      this("", var1, "");
   }

   public String getNamespaceURI() {
      return this.b;
   }

   public String getLocalPart() {
      return this.c;
   }

   public String getPrefix() {
      return this.d;
   }

   public final boolean equals(Object var1) {
      if (var1 != null && var1 instanceof QName) {
         QName var2 = (QName)var1;
         return this.b.equals(var2.b) && this.c.equals(var2.c);
      } else {
         return false;
      }
   }

   public final int hashCode() {
      return this.b.hashCode() ^ this.c.hashCode();
   }

   public final String toString() {
      return this.b.equals("") ? this.c : "{" + this.b + "}" + this.c;
   }

   public static QName valueOf(String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("cannot create QName from \"null\"");
      } else if (var0.length() == 0) {
         return new QName("");
      } else if (var0.charAt(0) != '{') {
         return new QName("", var0, "");
      } else {
         int var1;
         if ((var1 = var0.indexOf(125)) == -1) {
            throw new IllegalArgumentException("cannot create QName from \"" + var0 + "\", missing closing \"}\"");
         } else if (var1 == var0.length() - 1) {
            throw new IllegalArgumentException("cannot create QName from \"" + var0 + "\", missing local part");
         } else {
            return new QName(var0.substring(1, var1), var0.substring(var1 + 1), "");
         }
      }
   }
}
