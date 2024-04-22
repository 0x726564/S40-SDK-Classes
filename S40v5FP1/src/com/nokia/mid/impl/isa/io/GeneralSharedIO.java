package com.nokia.mid.impl.isa.io;

import com.nokia.mid.impl.isa.util.SharedObjects;

public class GeneralSharedIO {
   public static final Object networkPermissionLock = SharedObjects.getLock("networkPermissionLock");

   private GeneralSharedIO() {
   }
}
