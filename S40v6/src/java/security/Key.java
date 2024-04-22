package java.security;

public interface Key {
   String getAlgorithm();

   String getFormat();

   byte[] getEncoded();
}
