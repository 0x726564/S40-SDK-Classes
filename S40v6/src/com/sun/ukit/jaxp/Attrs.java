package com.sun.ukit.jaxp;

import org.xml.sax.Attributes;

class Attrs implements Attributes {
   String[] mItems = new String[64];
   private char mLength;

   void setLength(char length) {
      if (length > (char)(this.mItems.length >> 3)) {
         this.mItems = new String[length << 3];
      }

      this.mLength = length;
   }

   boolean checkDuplicates() {
      for(int i = 0; i < this.getLength(); ++i) {
         String qName1 = this.getQName(i);

         for(int j = i + 1; j < this.getLength(); ++j) {
            String qName2 = this.getQName(j);
            if (qName1 != null && qName2 != null && qName1.equals(qName2)) {
               return true;
            }
         }
      }

      return false;
   }

   public int getLength() {
      return this.mLength;
   }

   public String getURI(int index) {
      return index >= 0 && index < this.mLength ? this.mItems[index << 3] : null;
   }

   public String getLocalName(int index) {
      return index >= 0 && index < this.mLength ? this.mItems[(index << 3) + 2] : null;
   }

   public String getQName(int index) {
      return index >= 0 && index < this.mLength ? this.mItems[(index << 3) + 1] : null;
   }

   public String getType(int index) {
      if (index < 0) {
         return null;
      } else {
         return index < this.mItems.length >> 3 ? this.mItems[(index << 3) + 4] : null;
      }
   }

   public String getValue(int index) {
      return index >= 0 && index < this.mLength ? this.mItems[(index << 3) + 3] : null;
   }

   public int getIndex(String uri, String localName) {
      char len = this.mLength;

      for(char idx = 0; idx < len; ++idx) {
         if (this.mItems[idx << 3].equals(uri) && this.mItems[(idx << 3) + 2].equals(localName)) {
            return idx;
         }
      }

      return -1;
   }

   public int getIndex(String qName) {
      char len = this.mLength;

      for(char idx = 0; idx < len; ++idx) {
         if (this.getQName(idx).equals(qName)) {
            return idx;
         }
      }

      return -1;
   }

   public String getType(String uri, String localName) {
      int idx = this.getIndex(uri, localName);
      return idx >= 0 ? this.mItems[(idx << 3) + 4] : null;
   }

   public String getType(String qName) {
      int idx = this.getIndex(qName);
      return idx >= 0 ? this.mItems[(idx << 3) + 4] : null;
   }

   public String getValue(String uri, String localName) {
      int idx = this.getIndex(uri, localName);
      return idx >= 0 ? this.mItems[(idx << 3) + 3] : null;
   }

   public String getValue(String qName) {
      int idx = this.getIndex(qName);
      return idx >= 0 ? this.mItems[(idx << 3) + 3] : null;
   }
}
