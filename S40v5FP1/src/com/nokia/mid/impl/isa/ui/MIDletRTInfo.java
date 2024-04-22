package com.nokia.mid.impl.isa.ui;

import javax.microedition.midlet.MIDlet;

final class MIDletRTInfo {
   private final boolean isMIDletExplorer;
   private String mName;
   private String mIcon;
   private Class mCls;
   private MIDletState mInstance;

   static int s_skipSpaces(String str, int idx, int length) {
      while(idx < length && str.charAt(idx) == ' ') {
         ++idx;
      }

      return idx;
   }

   static int s_skipSpacesBack(String str, int idx) {
      while(idx >= 0 && str.charAt(idx) == ' ') {
         --idx;
      }

      return idx;
   }

   static int s_skipToken(String str, int idx, int length) {
      while(idx < length && str.charAt(idx) != ',') {
         ++idx;
      }

      return idx;
   }

   MIDletRTInfo(String propertyStr) throws ClassNotFoundException {
      if (propertyStr == null) {
         throw new NullPointerException("MIDlet property string null");
      } else {
         String[] _args = new String[3];
         int _chIdx = 0;
         int stringLength = propertyStr.length();

         for(int _tokenIdx = 0; _tokenIdx < 3; ++_tokenIdx) {
            int _tokenStart = s_skipSpaces(propertyStr, _chIdx, stringLength);
            _chIdx = s_skipToken(propertyStr, _tokenStart, stringLength);
            int _tokenEnd = s_skipSpacesBack(propertyStr, _chIdx - 1) + 1;
            if (_tokenEnd - _tokenStart > 0) {
               _args[_tokenIdx] = propertyStr.substring(_tokenStart, _tokenEnd);
            }

            if (_chIdx >= stringLength) {
               break;
            }

            ++_chIdx;
         }

         this.mName = _args[0];
         this.mIcon = _args[1];
         this.mCls = Class.forName(_args[2]);
         this.isMIDletExplorer = false;
      }
   }

   MIDletRTInfo(Class meCls) {
      this.mName = "MIDletExplorer";
      this.mIcon = null;
      this.mCls = meCls;
      this.isMIDletExplorer = true;
   }

   final String getName() {
      return this.mName;
   }

   final String getIconName() {
      return this.mIcon;
   }

   final Class getCls() {
      return this.mCls;
   }

   final boolean isMIDletExplorer() {
      return this.isMIDletExplorer;
   }

   final MIDlet constructMIDlet() throws IllegalAccessException, InstantiationException {
      if (this.mCls != null && this.mInstance == null) {
         return (MIDlet)this.mCls.newInstance();
      } else {
         throw new InstantiationException("Failed to construct instance of " + this.mCls);
      }
   }

   final void setInstance(MIDletState ms) {
      this.mInstance = ms;
   }

   final void removeInstance() {
      this.mInstance = null;
   }

   final MIDletState getInstance() {
      return this.mInstance;
   }
}
