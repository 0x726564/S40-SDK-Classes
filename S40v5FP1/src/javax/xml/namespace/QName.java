package javax.xml.namespace;

public class QName {
   private final String namespaceURI;
   private final String localPart;
   private final String prefix;

   public QName(String namespaceURI, String localPart) {
      this(namespaceURI, localPart, "");
   }

   public QName(String namespaceURI, String localPart, String prefix) {
      if (namespaceURI == null) {
         this.namespaceURI = "";
      } else {
         this.namespaceURI = namespaceURI;
      }

      if (localPart == null) {
         throw new IllegalArgumentException("local part cannot be \"null\" when creating a QName");
      } else {
         this.localPart = localPart;
         if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be \"null\" when creating a QName");
         } else {
            this.prefix = prefix;
         }
      }
   }

   public QName(String localPart) {
      this("", localPart, "");
   }

   public String getNamespaceURI() {
      return this.namespaceURI;
   }

   public String getLocalPart() {
      return this.localPart;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public boolean equals(Object objectToTest) {
      if (objectToTest != null && objectToTest instanceof QName) {
         QName qName = (QName)objectToTest;
         return this.namespaceURI.equals(qName.namespaceURI) && this.localPart.equals(qName.localPart);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.namespaceURI.hashCode() ^ this.localPart.hashCode();
   }

   public String toString() {
      return this.namespaceURI.equals("") ? this.localPart : "{" + this.namespaceURI + "}" + this.localPart;
   }

   public static QName valueOf(String qNameAsString) {
      if (qNameAsString == null) {
         throw new IllegalArgumentException("cannot create QName from \"null\"");
      } else if (qNameAsString.length() == 0) {
         return new QName("");
      } else if (qNameAsString.charAt(0) != '{') {
         return new QName("", qNameAsString, "");
      } else {
         int endOfNamespaceURI = qNameAsString.indexOf(125);
         if (endOfNamespaceURI == -1) {
            throw new IllegalArgumentException("cannot create QName from \"" + qNameAsString + "\", missing closing \"}\"");
         } else if (endOfNamespaceURI == qNameAsString.length() - 1) {
            throw new IllegalArgumentException("cannot create QName from \"" + qNameAsString + "\", missing local part");
         } else {
            return new QName(qNameAsString.substring(1, endOfNamespaceURI), qNameAsString.substring(endOfNamespaceURI + 1), "");
         }
      }
   }
}
