package com.nokia.mid.impl.isa.util;

import java.lang.ref.WeakReference;

class SharedWeakReference extends WeakReference {
   SharedWeakReference(Object ref) {
      super(ref);
   }
}
