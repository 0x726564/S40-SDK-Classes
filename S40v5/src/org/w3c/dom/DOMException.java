package org.w3c.dom;

public class DOMException extends RuntimeException {
   public static final short WRONG_DOCUMENT_ERR = 4;
   public static final short INDEX_SIZE_ERR = 1;
   public static final short HIERARCHY_REQUEST_ERR = 3;
   public static final short NO_MODIFICATION_ALLOWED_ERR = 7;
   public static final short NOT_FOUND_ERR = 8;
   public static final short NOT_SUPPORTED_ERR = 9;
   public static final short INVALID_STATE_ERR = 11;
   public static final short INVALID_MODIFICATION_ERR = 13;
   public static final short INVALID_ACCESS_ERR = 15;
   public static final short TYPE_MISMATCH_ERR = 17;
   public short code;

   public DOMException(short var1, String var2) {
      super(var2);
      this.code = var1;
   }
}
