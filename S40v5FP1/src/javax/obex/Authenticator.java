package javax.obex;

public interface Authenticator {
   PasswordAuthentication onAuthenticationChallenge(String var1, boolean var2, boolean var3);

   byte[] onAuthenticationResponse(byte[] var1);
}
