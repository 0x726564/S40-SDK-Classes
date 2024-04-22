package javax.microedition.content;

import com.nokia.mid.impl.isa.content.InvocationAccessor;
import com.nokia.mid.impl.isa.content.InvocationImpl;

class InvocationAccessorImpl implements InvocationAccessor {
   public InvocationImpl getInvocationImpl(Invocation inv) {
      return inv.getImpl();
   }
}
