package javax.microedition.pki;

public interface Certificate {
   String getSubject();

   String getIssuer();

   String getType();

   String getVersion();

   String getSigAlgName();

   long getNotBefore();

   long getNotAfter();

   String getSerialNumber();
}
