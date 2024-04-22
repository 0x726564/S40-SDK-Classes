package javax.obex;

public class PasswordAuthentication {
   private byte[] userName;
   private byte[] password;

   public PasswordAuthentication(byte[] var1, byte[] var2) {
      this.userName = var1;
      if (var2 == null) {
         throw new NullPointerException("password cannot be null");
      } else {
         this.password = var2;
      }
   }

   public byte[] getUserName() {
      return this.userName;
   }

   public byte[] getPassword() {
      return this.password;
   }
}
