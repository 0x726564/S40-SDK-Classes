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
   private int algorithmId = 0;
   private int blockModeId = 20;
   private int paddingId = 35;
   private String myAlgorithmStr;

   public Algorithm(String transformation) throws NoSuchAlgorithmException {
      try {
         this.init(transformation);
      } catch (NoSuchPaddingException var3) {
         throw new NoSuchAlgorithmException(transformation);
      }
   }

   public Algorithm(String transformation, boolean padding) throws NoSuchAlgorithmException, NoSuchPaddingException {
      this.init(transformation);
   }

   private void init(String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException {
      String algorithmStr = ("" + transformation).toUpperCase().trim();
      String algorithmSubStr = "";
      int len = algorithmStr.length();
      if (len < 3) {
         throw new NoSuchAlgorithmException(transformation);
      } else {
         int firstIx = algorithmStr.indexOf(47);
         if (firstIx != -1 && len > firstIx + 2) {
            int secondIx = algorithmStr.indexOf(47, firstIx + 1);
            if (secondIx != -1) {
               algorithmSubStr = algorithmStr.substring(0, firstIx);
               String modeStr = algorithmStr.substring(firstIx + 1, secondIx);
               this.setModeId(modeStr);
               String paddingStr = algorithmStr.substring(secondIx + 1);
               this.setPaddingId(paddingStr);
            }
         } else {
            algorithmSubStr = algorithmStr;
         }

         this.setAlgorithmId(algorithmSubStr);
         this.myAlgorithmStr = algorithmSubStr;
      }
   }

   public boolean isAlgorithm(int compare) {
      return this.algorithmId == compare;
   }

   public boolean isMode(int compare) {
      return this.blockModeId == compare;
   }

   public boolean isPadding(int compare) {
      return this.paddingId == compare;
   }

   private void setAlgorithmId(String algorithmStr) throws NoSuchAlgorithmException {
      if (algorithmStr.equals("SHA-1")) {
         this.algorithmId = 1;
      } else if (algorithmStr.equals("MD5")) {
         this.algorithmId = 2;
      } else if (algorithmStr.equals("MD2")) {
         this.algorithmId = 3;
      } else if (algorithmStr.equals("RSA")) {
         this.algorithmId = 4;
      } else if (algorithmStr.equals("SHA1WITHRSA")) {
         this.algorithmId = 5;
      } else if (algorithmStr.equals("MD5WITHRSA")) {
         this.algorithmId = 6;
      } else if (algorithmStr.equals("DES")) {
         this.algorithmId = 7;
      } else if (algorithmStr.equals("DESEDE")) {
         this.algorithmId = 8;
      } else if (algorithmStr.equals("AES")) {
         this.algorithmId = 9;
      } else {
         throw new NoSuchAlgorithmException(algorithmStr);
      }
   }

   private void setModeId(String modeStr) throws NoSuchAlgorithmException {
      if (modeStr.equals("NONE")) {
         this.blockModeId = 19;
      } else if (modeStr.equals("")) {
         this.blockModeId = 20;
      } else if (modeStr.equals("ECB")) {
         this.blockModeId = 18;
      } else if (modeStr.equals("CBC")) {
         this.blockModeId = 17;
      } else {
         throw new NoSuchAlgorithmException(modeStr);
      }
   }

   private void setPaddingId(String paddingStr) throws NoSuchPaddingException {
      if (paddingStr.equals("")) {
         this.paddingId = 35;
      } else if (paddingStr.equals("PKCS1PADDING")) {
         this.paddingId = 34;
      } else if (paddingStr.equals("PKCS5PADDING")) {
         this.paddingId = 33;
      } else if (paddingStr.equals("NOPADDING")) {
         this.paddingId = 32;
      } else {
         throw new NoSuchPaddingException(paddingStr);
      }
   }

   public int algorithm() {
      return this.algorithmId;
   }

   public int blockMode() {
      return this.blockModeId;
   }

   public int padding() {
      return this.paddingId;
   }

   public String getString() {
      return this.myAlgorithmStr;
   }
}
