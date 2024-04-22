package com.nokia.mid.impl.isa.crypto;

import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;

public class Algorithm {
   public static final int UNKNOWN = 0;
   public static final int SHA1 = 1;
   public static final int MD5 = 2;
   public static final int MD2 = 3;
   public static final int RSA = 4;
   public static final int SHA1_WITH_RSA = 5;
   public static final int MD5_WITH_RSA = 6;
   public static final int DES = 7;
   public static final int DESEDE = 8;
   public static final int AES = 9;
   public static final int CBC = 17;
   public static final int ECB = 18;
   public static final int BLOCK_NONE = 19;
   public static final int BLOCK_DEFAULT = 20;
   public static final int NO_PADDING = 32;
   public static final int PKCS5_PADDING = 33;
   public static final int PKCS1_PADDING = 34;
   public static final int PADDING_DEFAULT = 35;
   private int gb = 0;
   private int gc = 20;
   private int gd = 35;
   private String ge;

   public Algorithm(String var1) throws NoSuchAlgorithmException {
      try {
         this.init(var1);
      } catch (NoSuchPaddingException var2) {
         throw new NoSuchAlgorithmException(var1);
      }
   }

   public Algorithm(String var1, boolean var2) throws NoSuchAlgorithmException, NoSuchPaddingException {
      this.init(var1);
   }

   private void init(String var1) throws NoSuchAlgorithmException, NoSuchPaddingException {
      String var2 = ("" + var1).toUpperCase().trim();
      String var3 = "";
      int var4;
      if ((var4 = var2.length()) < 3) {
         throw new NoSuchAlgorithmException(var1);
      } else {
         int var5;
         if ((var5 = var2.indexOf(47)) != -1 && var4 > var5 + 2) {
            int var6;
            if ((var6 = var2.indexOf(47, var5 + 1)) != -1) {
               var3 = var2.substring(0, var5);
               String var7 = var2.substring(var5 + 1, var6);
               this.setModeId(var7);
               var1 = var2.substring(var6 + 1);
               this.setPaddingId(var1);
            }
         } else {
            var3 = var2;
         }

         this.setAlgorithmId(var3);
         this.ge = var3;
      }
   }

   public boolean isAlgorithm(int var1) {
      return this.gb == var1;
   }

   public boolean isMode(int var1) {
      return this.gc == var1;
   }

   public boolean isPadding(int var1) {
      return this.gd == var1;
   }

   private void setAlgorithmId(String var1) throws NoSuchAlgorithmException {
      if (var1.equals("SHA-1")) {
         this.gb = 1;
      } else if (var1.equals("MD5")) {
         this.gb = 2;
      } else if (var1.equals("MD2")) {
         this.gb = 3;
      } else if (var1.equals("RSA")) {
         this.gb = 4;
      } else if (var1.equals("SHA1WITHRSA")) {
         this.gb = 5;
      } else if (var1.equals("MD5WITHRSA")) {
         this.gb = 6;
      } else if (var1.equals("DES")) {
         this.gb = 7;
      } else if (var1.equals("DESEDE")) {
         this.gb = 8;
      } else if (var1.equals("AES")) {
         this.gb = 9;
      } else {
         throw new NoSuchAlgorithmException(var1);
      }
   }

   private void setModeId(String var1) throws NoSuchAlgorithmException {
      if (var1.equals("NONE")) {
         this.gc = 19;
      } else if (var1.equals("")) {
         this.gc = 20;
      } else if (var1.equals("ECB")) {
         this.gc = 18;
      } else if (var1.equals("CBC")) {
         this.gc = 17;
      } else {
         throw new NoSuchAlgorithmException(var1);
      }
   }

   private void setPaddingId(String var1) throws NoSuchPaddingException {
      if (var1.equals("")) {
         this.gd = 35;
      } else if (var1.equals("PKCS1PADDING")) {
         this.gd = 34;
      } else if (var1.equals("PKCS5PADDING")) {
         this.gd = 33;
      } else if (var1.equals("NOPADDING")) {
         this.gd = 32;
      } else {
         throw new NoSuchPaddingException(var1);
      }
   }

   public int algorithm() {
      return this.gb;
   }

   public int blockMode() {
      return this.gc;
   }

   public int padding() {
      return this.gd;
   }

   public String getString() {
      return this.ge;
   }
}
