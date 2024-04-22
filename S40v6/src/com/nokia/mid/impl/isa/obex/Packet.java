package com.nokia.mid.impl.isa.obex;

public class Packet {
   public static final int CONNECT = 128;
   public static final int DISCONNECT = 129;
   public static final int PUT = 2;
   public static final int GET = 3;
   public static final int SETPATH = 133;
   public static final int ABORT = 255;
   protected byte[] serializedHeaders;
   public boolean authenticationChallenge;
   public String realm;
   public boolean userIdRequired;
   public boolean isFullAccess;
   public boolean authenticationResponse;
   public byte[] userName;
   public byte[] password;
   public byte[] myNonce;
   public byte[] itsAuthChallengeNonce;
   public byte[] itsAuthResponseNonce;
   public byte[] digest;
   public int packetSort;
   public boolean isResp;
   public int respCode;
   public boolean isFinal = false;
   public boolean isSetPathBackup;
   public boolean isSetPathCreate;
   public int maxPacketLength;

   public static Packet decode(byte[] bytes, int length) {
      Packet p = decodePacket0(bytes, length);
      if (p == null) {
         return null;
      } else {
         if (p.serializedHeaders != null && p.serializedHeaders.length > 1) {
            p.serializedHeaders[0] = bytes[0];
         }

         p.respCode = bytes[0] & 255;
         if ((bytes[0] & 128) == 128) {
            p.isFinal = true;
         }

         p.packetSort = bytes[0] & 255;
         if (p.packetSort == 130) {
            p.packetSort = 2;
         } else if (p.packetSort == 131) {
            p.packetSort = 3;
         }

         return p;
      }
   }

   public static byte[] encode(Packet p) {
      return encodePacket0(p);
   }

   private static native Packet decodePacket0(byte[] var0, int var1);

   private static native byte[] encodePacket0(Packet var0);

   public static native boolean IsItsDigestValid(byte[] var0, byte[] var1, byte[] var2, byte[] var3);
}
