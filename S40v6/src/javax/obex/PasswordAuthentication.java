package javax.obex;

public class PasswordAuthentication {
   private byte[] userName;
   private byte[] password;

   public PasswordAuthentication(byte[] userName, byte[] password) {
      this.userName = userName;
      if (password == null) {
         throw new NullPointerException("password cannot be null");
      } else {
         this.password = password;
      }
   }

   public byte[] getUserName() {
      return this.userName;
   }

   public byte[] getPassword() {
      return this.password;
   }
}
